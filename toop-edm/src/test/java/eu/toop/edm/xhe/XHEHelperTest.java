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
package eu.toop.edm.xhe;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.mime.CMimeType;
import com.helger.peppolid.simple.participant.SimpleParticipantIdentifier;
import com.helger.xhe.XHE10Marshaller;
import com.helger.xhe.v10.XHE10XHEType;

import eu.toop.commons.codelist.EPredefinedDocumentTypeIdentifier;
import eu.toop.commons.codelist.EPredefinedProcessIdentifier;

/**
 * Test class for class {@link XHEHelper}.
 *
 * @author Philip Helger
 */
public final class XHEHelperTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (XHEHelperTest.class);

  private static void _validate (@Nonnull final XHE10XHEType aXHE)
  {
    assertNotNull (aXHE);

    if (false)
    {
      final XHE10Marshaller x = new XHE10Marshaller (false);
      x.setFormattedOutput (true);
      LOGGER.info (x.getAsString (aXHE));
    }

    final XHE10Marshaller aMarshaller = new XHE10Marshaller (true);
    assertNotNull (aMarshaller.getAsDocument (aXHE));
  }

  @Test
  public void testBasic ()
  {
    final ICommonsOrderedMap <String, String> aParams = new CommonsLinkedHashMap <> ();
    for (int i = 1; i <= 3; ++i)
      aParams.put ("param" + i, "value" + i);

    // As small as it gets
    XHE10XHEType aXHE = XHEHelper.createXHE (null,
                                             null,
                                             new CommonsArrayList <> (new SimpleParticipantIdentifier ("scheme",
                                                                                                       "to1")),
                                             null);
    _validate (aXHE);

    // With more
    aXHE = XHEHelper.createXHE (aParams,
                                new SimpleParticipantIdentifier ("scheme", "from"),
                                new CommonsArrayList <> (new SimpleParticipantIdentifier ("scheme", "to1")),
                                null);
    _validate (aXHE);

    // With payload
    aXHE = XHEHelper.createXHE (aParams,
                                new SimpleParticipantIdentifier ("scheme", "from"),
                                new CommonsArrayList <> (new SimpleParticipantIdentifier ("scheme", "to1")),
                                new CommonsArrayList <> (XHEHelper.createPayload ("DataRequest",
                                                                                  EPredefinedDocumentTypeIdentifier.REQUEST_REGISTEREDORGANIZATION,
                                                                                  EPredefinedProcessIdentifier.DATAREQUESTRESPONSE,
                                                                                  CMimeType.APPLICATION_XML,
                                                                                  "DataRequest.xml")));
    _validate (aXHE);
  }
}