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
package eu.toop.connector.webapi.validation;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.bdve.executorset.VESID;
import com.helger.bdve.json.BDVEJsonHelper;
import com.helger.bdve.result.ValidationResultList;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.api.IAPIExecutor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import eu.toop.connector.app.validation.EValidationEdmType;
import eu.toop.connector.app.validation.EdmValidator;

public class ApiPostValidateEdm implements IAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ApiPostValidateEdm.class);

  private final EValidationEdmType m_eType;

  public ApiPostValidateEdm (@Nonnull final EValidationEdmType eType)
  {
    m_eType = eType;
  }

  public void invokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                         @Nonnull @Nonempty final String sPath,
                         @Nonnull final Map <String, String> aPathVariables,
                         @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                         @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final Locale aDisplayLocale = Locale.UK;

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    final byte [] aPayload = StreamHelper.getAllBytes (aRequestScope.getRequest ().getInputStream ());
    final VESID aVESID = m_eType.getVESID ();

    LOGGER.info ("API validating " + aPayload.length + " bytes using '" + aVESID.getAsSingleID () + "'");

    final ValidationResultList aValidationResultList = EdmValidator.validate (aVESID, aPayload, aDisplayLocale);

    aSW.stop ();

    LOGGER.info ("API validation finished after " + aSW.getMillis () + " millis");

    // Build response
    final IJsonObject aJson = new JsonObject ();
    BDVEJsonHelper.applyValidationResultList (aJson,
                                              EdmValidator.getVES (aVESID),
                                              aValidationResultList,
                                              aDisplayLocale,
                                              aSW.getMillis (),
                                              null,
                                              null);

    aJson.add ("validationDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));

    ((PhotonUnifiedResponse) aUnifiedResponse).json (aJson);
  }
}