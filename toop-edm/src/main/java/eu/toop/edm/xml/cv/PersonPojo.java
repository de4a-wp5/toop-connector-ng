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
package eu.toop.edm.xml.cv;

import java.time.LocalDate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.string.StringHelper;
import com.helger.datetime.util.PDTXMLConverter;

import eu.toop.edm.jaxb.w3.cv.ac.CorePersonType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonBirthDateType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonBirthNameType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonFamilyNameType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonGenderCodeType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonGivenNameType;
import eu.toop.edm.jaxb.w3.cv.bc.PersonIDType;

public class PersonPojo
{
  private final String m_sID;
  private final String m_sIDSchemeID;
  private final String m_sFamilyName;
  private final String m_sGivenName;
  private final String m_sGenderCode;
  private final String m_sBirthName;
  private final LocalDate m_aBirthDate;
  private final AddressPojo m_aAddress;

  public PersonPojo (@Nullable final String sID,
                     @Nullable final String sIDSchemeID,
                     @Nullable final String sFamilyName,
                     @Nullable final String sGivenName,
                     @Nullable final String sGenderCode,
                     @Nullable final String sBirthName,
                     @Nullable final LocalDate aBirthDate,
                     @Nullable final AddressPojo aAddress)
  {
    m_sID = sID;
    m_sIDSchemeID = sIDSchemeID;
    m_sFamilyName = sFamilyName;
    m_sGivenName = sGivenName;
    m_sGenderCode = sGenderCode;
    m_sBirthName = sBirthName;
    m_aBirthDate = aBirthDate;
    m_aAddress = aAddress;
  }

  @Nonnull
  public CorePersonType getAsPerson ()
  {
    final CorePersonType ret = new CorePersonType ();

    if (StringHelper.hasText (m_sID))
    {
      final PersonIDType aID = new PersonIDType ();
      aID.setValue (m_sID);
      aID.setSchemeID (m_sIDSchemeID);
      ret.addPersonID (aID);
    }
    if (StringHelper.hasText (m_sFamilyName))
    {
      final PersonFamilyNameType aFamilyName = new PersonFamilyNameType ();
      aFamilyName.setValue (m_sFamilyName);
      ret.addPersonFamilyName (aFamilyName);
    }
    if (StringHelper.hasText (m_sGivenName))
    {
      final PersonGivenNameType aGivenName = new PersonGivenNameType ();
      aGivenName.setValue (m_sGivenName);
      ret.addPersonGivenName (aGivenName);
    }
    if (StringHelper.hasText (m_sGenderCode))
    {
      final PersonGenderCodeType aGC = new PersonGenderCodeType ();
      aGC.setValue (m_sGenderCode);
      ret.addPersonGenderCode (aGC);
    }
    if (StringHelper.hasText (m_sBirthName))
    {
      final PersonBirthNameType aBirthName = new PersonBirthNameType ();
      aBirthName.setValue (m_sBirthName);
      ret.addPersonBirthName (aBirthName);
    }
    if (m_aBirthDate != null)
    {
      final PersonBirthDateType aBirthDate = new PersonBirthDateType ();
      aBirthDate.setValue (PDTXMLConverter.getXMLCalendarDate (m_aBirthDate));
      ret.addPersonBirthDate (aBirthDate);
    }
    if (m_aAddress != null)
      ret.addPersonCoreAddress (m_aAddress.getAsAddress ());

    return ret;
  }

  @Nonnull
  public static PersonPojo createMinimum ()
  {
    return new PersonPojo (null, null, null, null, null, null, null, null);
  }
}
