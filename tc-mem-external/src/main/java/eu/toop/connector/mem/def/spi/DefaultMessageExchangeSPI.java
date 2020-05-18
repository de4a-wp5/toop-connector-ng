/**
 * Copyright (C) 2018-2020 toop.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.toop.connector.mem.def.spi;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.error.level.EErrorLevel;

import eu.toop.connector.api.as4.IMEIncomingHandler;
import eu.toop.connector.api.as4.IMERoutingInformation;
import eu.toop.connector.api.as4.IMessageExchangeSPI;
import eu.toop.connector.api.as4.MEException;
import eu.toop.connector.api.as4.MEMessage;
import eu.toop.connector.api.as4.MEPayload;
import eu.toop.connector.mem.def.EActingSide;
import eu.toop.connector.mem.def.GatewayRoutingMetadata;
import eu.toop.connector.mem.def.MEMDelegate;
import eu.toop.edm.EDMErrorResponse;
import eu.toop.edm.EDMRequest;
import eu.toop.edm.EDMResponse;
import eu.toop.edm.IEDMTopLevelObject;
import eu.toop.kafkaclient.ToopKafkaClient;

/**
 * Implementation of {@link IMessageExchangeSPI} using the "TOOP AS4 Gateway
 * back-end interface".
 *
 * @author Philip Helger
 */
@IsSPIImplementation
public final class DefaultMessageExchangeSPI implements IMessageExchangeSPI
{
  private IMEIncomingHandler m_aIncomingHandler;

  public DefaultMessageExchangeSPI ()
  {}

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return "external";
  }

  public void registerIncomingHandler (@Nonnull final ServletContext aServletContext,
                                       @Nonnull final IMEIncomingHandler aIncomingHandler) throws MEException
  {
    ValueEnforcer.notNull (aServletContext, "ServletContext");
    ValueEnforcer.notNull (aIncomingHandler, "IncomingHandler");
    if (m_aIncomingHandler != null)
      throw new IllegalStateException ("Another incoming handler was already registered!");
    m_aIncomingHandler = aIncomingHandler;

    final MEMDelegate aDelegate = MEMDelegate.getInstance ();

    aDelegate.registerNotificationHandler (aRelayResult -> {
      // more to come
      ToopKafkaClient.send (EErrorLevel.INFO,
                            () -> "Notification[" +
                                  aRelayResult.getErrorCode () +
                                  "]: " +
                                  aRelayResult.getDescription ());
    });

    aDelegate.registerSubmissionResultHandler (aRelayResult -> {
      // more to come
      ToopKafkaClient.send (EErrorLevel.INFO,
                            () -> "SubmissionResult[" +
                                  aRelayResult.getErrorCode () +
                                  "]: " +
                                  aRelayResult.getDescription ());
    });

    // Register the AS4 handler needed
    aDelegate.registerMessageHandler (aMEMessage -> {
      final IEDMTopLevelObject aTopLevel = IMEIncomingHandler.parseAndFind (aMEMessage.head ()
                                                                                      .getData ()
                                                                                      .getInputStream ());
      if (aTopLevel instanceof EDMRequest)
      {
        m_aIncomingHandler.handleIncomingRequest ((EDMRequest) aTopLevel);
      }
      else
        if (aTopLevel instanceof EDMResponse)
        {
          final ICommonsOrderedMap <String, MEPayload> aAttachments = new CommonsLinkedHashMap <> ();
          aAttachments.putAllMapped (aMEMessage.payloadsWithoutHead (), MEPayload::getPayloadId, Function.identity ());
          m_aIncomingHandler.handleIncomingResponse ((EDMResponse) aTopLevel, aAttachments);
        }
        else
          if (aTopLevel instanceof EDMErrorResponse)
          {
            m_aIncomingHandler.handleIncomingErrorResponse ((EDMErrorResponse) aTopLevel);
          }
          else
            ToopKafkaClient.send (EErrorLevel.ERROR, () -> "Unsuspported Message: " + aTopLevel);
    });
  }

  public void sendDCOutgoing (@Nonnull final IMERoutingInformation aRoutingInfo, @Nonnull final MEMessage aMessage)
  {
    final GatewayRoutingMetadata aGRM = new GatewayRoutingMetadata (aRoutingInfo.getSenderID ().getURIEncoded (),
                                                                    aRoutingInfo.getDocumentTypeID ().getURIEncoded (),
                                                                    aRoutingInfo.getProcessID ().getURIEncoded (),
                                                                    aRoutingInfo.getEndpointURL (),
                                                                    aRoutingInfo.getCertificate (),
                                                                    EActingSide.DC);
    MEMDelegate.getInstance ().sendMessage (aGRM, aMessage);
  }

  public void sendDPOutgoing (@Nonnull final IMERoutingInformation aRoutingInfo,
                              @Nonnull final MEMessage aMessage) throws MEException
  {
    final GatewayRoutingMetadata aGRM = new GatewayRoutingMetadata (aRoutingInfo.getSenderID ().getURIEncoded (),
                                                                    aRoutingInfo.getDocumentTypeID ().getURIEncoded (),
                                                                    aRoutingInfo.getProcessID ().getURIEncoded (),
                                                                    aRoutingInfo.getEndpointURL (),
                                                                    aRoutingInfo.getCertificate (),
                                                                    EActingSide.DP);
    MEMDelegate.getInstance ().sendMessage (aGRM, aMessage);
  }

  public void shutdown (@Nonnull final ServletContext aServletContext)
  {}
}
