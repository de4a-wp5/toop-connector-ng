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
package eu.toop.connector.api.simulator;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Singleton;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xsds.bdxr.smp1.CBDXRSMP1;
import com.helger.xsds.xmldsig.CXMLDSig;

/**
 * The namespace context to be used as the namespace prefix mapper.
 *
 * @author Philip Helger
 */
@Singleton
public class TCSimulatorNamespaceContext extends MapBasedNamespaceContext
{
  private static final class SingletonHolder
  {
    static final TCSimulatorNamespaceContext s_aInstance = new TCSimulatorNamespaceContext ();
  }

  protected TCSimulatorNamespaceContext ()
  {
    addMapping (CXMLDSig.DEFAULT_PREFIX, CXMLDSig.NAMESPACE_URI);
    addMapping (CBDXRSMP1.DEFAULT_PREFIX, CBDXRSMP1.NAMESPACE_URI);
    addDefaultNamespaceURI (TCSimulatorJAXB.NS_URI);
  }

  @Nonnull
  public static TCSimulatorNamespaceContext getInstance ()
  {
    return SingletonHolder.s_aInstance;
  }
}