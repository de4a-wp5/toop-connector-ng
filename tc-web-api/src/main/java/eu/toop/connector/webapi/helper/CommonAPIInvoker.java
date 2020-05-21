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
package eu.toop.connector.webapi.helper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;

import com.helger.bdve.json.BDVEJsonHelper;
import com.helger.commons.callback.IThrowingRunnable;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJsonObject;

public class CommonAPIInvoker
{
  private CommonAPIInvoker ()
  {}

  public static void invoke (@Nonnull final IJsonObject aJson, @Nonnull final IThrowingRunnable <Exception> r)
  {

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();
    try
    {
      r.run ();
    }
    catch (final Exception ex)
    {
      aJson.add ("success", false);
      aJson.addJson ("exception", BDVEJsonHelper.getJsonStackTrace (ex));
    }
    aSW.stop ();

    aJson.add ("invocationDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));
    aJson.add ("invocationDurationMillis", aSW.getMillis ());
  }
}
