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
package eu.toop.edm.cccev;

import com.helger.jaxb.GenericJAXBMarshaller;

import eu.toop.edm.cpsv.CCPSV;
import eu.toop.edm.jaxb.cccev.CCCEVRequirementType;
import eu.toop.edm.jaxb.cccev.ObjectFactory;

public class RequirementMarshaller extends GenericJAXBMarshaller <CCCEVRequirementType>
{
  public RequirementMarshaller ()
  {
    // TODO XSDs
    super (CCCEVRequirementType.class, CCPSV.XSDS, x -> new ObjectFactory ().createRequirement (x));
    setNamespaceContext (CCCEVNamespaceContext.getInstance ());
  }
}