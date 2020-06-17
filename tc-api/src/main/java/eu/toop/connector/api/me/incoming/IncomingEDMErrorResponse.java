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
package eu.toop.connector.api.me.incoming;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

import eu.toop.edm.EDMErrorResponse;

/**
 * Incoming EDM error response. Uses {@link EDMErrorResponse} and
 * {@link IMEIncomingTransportMetadata} for the metadata.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class IncomingEDMErrorResponse implements IIncomingEDMResponse
{
  private final EDMErrorResponse m_aErrorResponse;
  private final IMEIncomingTransportMetadata m_aMetadata;

  public IncomingEDMErrorResponse (@Nonnull final EDMErrorResponse aErrorResponse, @Nonnull final IMEIncomingTransportMetadata aMetadata)
  {
    ValueEnforcer.notNull (aErrorResponse, "ErrorResponse");
    ValueEnforcer.notNull (aMetadata, "Metadata");
    m_aErrorResponse = aErrorResponse;
    m_aMetadata = aMetadata;
  }

  @Nonnull
  public EDMErrorResponse getErrorResponse ()
  {
    return m_aErrorResponse;
  }

  @Nonnull
  public IMEIncomingTransportMetadata getMetadata ()
  {
    return m_aMetadata;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("ErrorResponse", m_aErrorResponse).append ("Metadata", m_aMetadata).getToString ();
  }
}
