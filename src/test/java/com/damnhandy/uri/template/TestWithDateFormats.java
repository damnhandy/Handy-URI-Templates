/*
 * Copyright 2012, Ryan J. McDonough
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.damnhandy.uri.template;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Testing some use cases with different date formats;
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision: 1.1 $
 */
public class TestWithDateFormats
{



   private static final String TEMPLATE = "/{date:4}/{date}";


   private Date date;

   @Before
   public void setUp()
   {
      Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT-04:00"));
      cal.set(Calendar.YEAR, 2012);
      cal.set(Calendar.MONTH, Calendar.APRIL);
      cal.set(Calendar.DAY_OF_MONTH, 20);
      cal.set(Calendar.HOUR_OF_DAY, 16);
      cal.set(Calendar.MINUTE, 20);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      date = cal.getTime();
   }

   /**
    * Tests how the default date format works.
    *
    * @throws Exception
    */
   @Test
   @Ignore
   public void testWithDefaultDateFormat() throws Exception
   {
      String uri = UriTemplate.fromTemplate(TEMPLATE).set("date", date).expand();
      assertEquals("/2012/2012-04-20T16%3A20%3A00.000-0400", uri);
   }


   @Test
   public void testWithCustomDefaultDateFormat() throws Exception
   {
      UriTemplate template = UriTemplate.fromTemplate(TEMPLATE)
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .set("date",date);
      assertEquals("/2012/2012-04-20", template.expand());
   }

   @Test
   public void testWithSimpleDateFormat() throws Exception
   {
      UriTemplate template = UriTemplate.fromTemplate(TEMPLATE)
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .set("date",date);
      assertEquals("/2012/2012-04-20", template.expand());
   }

   @Test
   public void testDateRangeQueryString() throws Exception
   {
      Date start = formatDate("2012-04-01T16:20:00.000-0400");
      Date end = formatDate("2012-04-30T16:20:00.000-0400");
      UriTemplate template = UriTemplate.fromTemplate("/find{?start,end}")
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .set("start",start)
                                        .set("end", end);
      assertEquals("/find?start=2012-04-01&end=2012-04-30",template.expand());
   }

   private Date formatDate(String dateString)
   {
      DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      try
      {
         return formatter.parse(dateString);
      }
      catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
   }
}
