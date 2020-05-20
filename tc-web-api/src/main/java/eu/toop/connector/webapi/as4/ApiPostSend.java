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
package eu.toop.connector.webapi.as4;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.mime.MimeTypeParser;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.api.IAPIExecutor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import eu.toop.connector.api.me.IMessageExchangeSPI;
import eu.toop.connector.api.me.MessageExchangeManager;
import eu.toop.connector.api.me.model.MEMessage;
import eu.toop.connector.api.me.model.MEPayload;
import eu.toop.connector.api.me.outgoing.IMERoutingInformation;
import eu.toop.connector.api.me.outgoing.MERoutingInformation;
import eu.toop.connector.api.rest.TCOutgoingMessage;
import eu.toop.connector.api.rest.TCOutgoingPayload;
import eu.toop.connector.api.rest.TCRestJAXB;
import eu.toop.connector.webapi.APIParamException;

public class ApiPostSend implements IAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ApiPostSend.class);

  public void invokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                         @Nonnull @Nonempty final String sPath,
                         @Nonnull final Map <String, String> aPathVariables,
                         @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                         @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    final TCOutgoingMessage aOutgoingMsg = TCRestJAXB.outgoingMessage ().read (aRequestScope.getRequest ().getInputStream ());
    if (aOutgoingMsg == null)
      throw new APIParamException ("Failed to interpret the message body as an 'OutgoingMessage'");

    final IMessageExchangeSPI aMEM = MessageExchangeManager.getConfiguredImplementation ();

    // Convert metadata
    final IMERoutingInformation aRoutingInfo = MERoutingInformation.createFrom (aOutgoingMsg.getMetadata ());

    // Add payloads
    final MEMessage.Builder aMessage = MEMessage.builder ();
    for (final TCOutgoingPayload aPayload : aOutgoingMsg.getPayload ())
    {
      aMessage.addPayload (MEPayload.builder ()
                                    .mimeType (MimeTypeParser.parseMimeType (aPayload.getMimeType ()))
                                    .contentID (StringHelper.getNotEmpty (aPayload.getContentID (), MEPayload.createRandomContentID ()))
                                    .data (aPayload.getValue ()));
    }
    aMEM.sendOutgoing (aRoutingInfo, aMessage.build ());

    aSW.stop ();

    LOGGER.info ("API validation finished after " + aSW.getMillis () + " millis");

    // Build response
    final IJsonObject aJson = new JsonObject ();
    // TODO
    aJson.add ("sendDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));

    ((PhotonUnifiedResponse) aUnifiedResponse).json (aJson);
  }
}
