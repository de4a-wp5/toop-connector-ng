/**
 * This work is protected under copyrights held by the members of the
 * TOOP Project Consortium as indicated at
 * http://wiki.ds.unipi.gr/display/TOOP/Contributors
 * (c) 2018-2021. All rights reserved.
 *
 * This work is dual licensed under Apache License, Version 2.0
 * and the EUPL 1.2.
 *
 *  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
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
 *
 *  = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *         https://joinup.ec.europa.eu/software/page/eupl
 */
package eu.toop.connector.app.dsd;

import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.string.ToStringGenerator;

import eu.toop.connector.api.TCConfig;
import eu.toop.connector.api.dsd.DSDDatasetHelper;
import eu.toop.connector.api.dsd.DSDDatasetResponse;
import eu.toop.connector.api.dsd.IDSDDatasetResponseProvider;
import eu.toop.connector.api.error.ITCErrorHandler;
import eu.toop.connector.api.http.TCHttpClientSettings;
import eu.toop.dsd.client.DSDClient;
import eu.toop.edm.error.EToopErrorCode;
import eu.toop.edm.jaxb.dcatap.DCatAPDatasetType;
import eu.toop.kafkaclient.ToopKafkaClient;

/**
 * This class implements the {@link IDSDDatasetResponseProvider} interface using
 * a remote query to DSD.
 *
 * @author Philip Helger
 */
public class DSDDatasetResponseProviderRemote implements IDSDDatasetResponseProvider
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DSDDatasetResponseProviderRemote.class);

  private final String m_sBaseURL;

  /**
   * Constructor using the DSD URL from the configuration file.
   */
  public DSDDatasetResponseProviderRemote ()
  {
    this (TCConfig.DSD.getDSDBaseUrl ());
  }

  /**
   * Constructor with an arbitrary DSD URL.
   *
   * @param sBaseURL
   *        The base URL to be used. May neither be <code>null</code> nor empty.
   */
  public DSDDatasetResponseProviderRemote (@Nonnull final String sBaseURL)
  {
    ValueEnforcer.notEmpty (sBaseURL, "BaseURL");
    m_sBaseURL = sBaseURL;
  }

  /**
   * @return The DSD Base URL as provided in the constructor. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  public final String getBaseURL ()
  {
    return m_sBaseURL;
  }

  @Nonnull
  public ICommonsSet <DSDDatasetResponse> getAllDatasetResponsesByCountry (@Nonnull final String sLogPrefix,
                                                                           @Nonnull @Nonempty final String sDatasetType,
                                                                           @Nonnull @Nonempty final String sCountryCode,
                                                                           @Nonnull final ITCErrorHandler aErrorHandler)
  {
    final DSDClient aDSDClient = new DSDClient (m_sBaseURL);
    aDSDClient.setHttpClientSettings (new TCHttpClientSettings ());

    ICommonsSet <DSDDatasetResponse> ret;
    try
    {
      final List <DCatAPDatasetType> aDatasetTypeList = aDSDClient.queryDatasetByLocation (sDatasetType, sCountryCode);
      ret = DSDDatasetHelper.buildDSDResponseSet (aDatasetTypeList);
    }
    catch (final RuntimeException ex)
    {
      LOGGER.error (ex.getMessage (), GlobalDebug.isDebugMode () ? ex : null);
      aErrorHandler.onError ("Failed to query the DSD", ex, EToopErrorCode.DD_001);
      // return EMPTY result set.
      ret = new CommonsHashSet <> ();
    }

    final int nResultCount = ret.size ();
    ToopKafkaClient.send (EErrorLevel.INFO,
                          () -> sLogPrefix +
                                "DSD querying '" +
                                sDatasetType +
                                "' and country code '" +
                                sCountryCode +
                                "' lead to " +
                                nResultCount +
                                " result entries");

    return ret;
  }

  @Nonnull
  public ICommonsSet <DSDDatasetResponse> getAllDatasetResponsesByDPType (@Nonnull final String sLogPrefix,
                                                                          @Nonnull @Nonempty final String sDatasetType,
                                                                          @Nonnull @Nonempty final String sDPType,
                                                                          @Nonnull final ITCErrorHandler aErrorHandler)
  {
    final DSDClient aDSDClient = new DSDClient (m_sBaseURL);
    aDSDClient.setHttpClientSettings (new TCHttpClientSettings ());

    ICommonsSet <DSDDatasetResponse> ret;
    try
    {
      final List <DCatAPDatasetType> aDatasetTypeList = aDSDClient.queryDatasetByDPType (sDatasetType, sDPType);
      ret = DSDDatasetHelper.buildDSDResponseSet (aDatasetTypeList);
    }
    catch (final RuntimeException ex)
    {
      LOGGER.error (ex.getMessage (), GlobalDebug.isDebugMode () ? ex : null);
      aErrorHandler.onError ("Failed to query the DSD", ex, EToopErrorCode.DD_001);
      // return EMPTY result set.
      ret = new CommonsHashSet <> ();
    }

    final int nResultCount = ret.size ();
    ToopKafkaClient.send (EErrorLevel.INFO,
                          () -> sLogPrefix +
                                "DSD querying '" +
                                sDatasetType +
                                "' and DPType '" +
                                sDPType +
                                "' lead to " +
                                nResultCount +
                                " result entries");

    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("BaseURL", m_sBaseURL).getToString ();
  }
}
