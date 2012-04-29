/*
 * 
 */
package com.damnhandy.uri.template;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
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
      date = formatDate("2012-04-20T16:20:00.000-0400");
   }

   /**
    * Tests how the default date format works.
    * 
    * @throws Exception
    */
   @Test
   public void testWithDefaultDateFormat() throws Exception
   {
      String uri = UriTemplate.fromExpression(TEMPLATE).set("date", date).expand();
      assertEquals("/2012/2012-04-20T16%3A20%3A00.000-0400", uri);
   }

   /**
    * Tests date format with a user supplied {@link DateFormat}
    * 
    * @throws Exception
    */
   @Test
   public void testWithCustomDateFormat() throws Exception
   {
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      String uri = UriTemplate.fromExpression(TEMPLATE).set("date", date, format).expand();
      assertEquals("/2012/2012-04-20", uri);
   }
   
   @Test
   public void testWithCustomDefaultDateFormat() throws Exception
   {
      UriTemplate template = UriTemplate.fromExpression(TEMPLATE)
                                        .withDefaultDateFormat("yyyy-MM-dd")
                                        .set("date",date);
      assertEquals("/2012/2012-04-20", template.expand());
   }
   
   @Test
   public void testDateRangeQueryString() throws Exception
   {
      Date start = formatDate("2012-04-01T16:20:00.000-0400");
      Date end = formatDate("2012-04-30T16:20:00.000-0400");
      UriTemplate template = UriTemplate.fromExpression("/find{?start,end}")
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
