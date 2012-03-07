/**
 * @author (Your Name Here)
 *
 */
package com.googlesync;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * This is a test template
 */

public class GoogleSync {
	public class Event{
		public String title;
		public String description;
		public String location;
		public DateTime starttime;
		public DateTime endtime;
		public boolean iswholeday;
	}
	
	private static final String METAFEED_URL_BASE = "https://www.google.com/calendar/feeds/";
	private static final String EVENT_FEED_URL_SUFFIX = "/private/full";
	private static URL metafeedUrl = null;
	private static URL eventFeedUrl = null;
	
	private static String userName;
	private static String userPassword;
	
	CalendarService myService;
	DateTime start;
	DateTime end;
	public GoogleSync(){
		
	}
	public GoogleSync(String userName, String userPassword){
		this.userName= userName;
		this.userPassword= userPassword;
		try{
			myService= new CalendarService(	"exampleCo-exampleApp-1");
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName+ EVENT_FEED_URL_SUFFIX);
			myService.setUserCredentials(userName, userPassword);

			/* METHOD 1 - PRINT ALL */
			getAllEvents(myService);
			
			/* METHOD 2 - PRINT WITHIN A RANGE OF TIME
			start= DateTime.parseDateTime("2012-01-01T00:00:00");
			end= DateTime.parseDateTime("2012-03-01T00:00:00");
			getRangeEvent(myService,start,end);*/
		}
		catch(Exception e){
			
		}
	}
	
	
	private void getEventEntry(CalendarEventEntry entry) {
	    assert entry != null;
	    
	    Event store = new Event();
	    
	    store.title= entry.getTitle().getPlainText();
	    store.description=  entry.getPlainTextContent();
	    store.location= entry.getLocations().get(0).getValueString();
	    
	    if (entry.getTimes().size() > 0) {
	        When eventTimes = entry.getTimes().get(0);
	        if (eventTimes.getStartTime().isDateOnly()) {
	        	store.iswholeday= true;
	        } 
	        else {
	        	store.starttime = eventTimes.getStartTime();
	        	store.endtime = eventTimes.getEndTime();
			}
	    }
	    System.out.println(store.title);
	    System.out.println(store.description);
	    System.out.println(store.location);
	    System.out.println(store.starttime);
	    System.out.println(store.endtime);
	    System.out.println();
	} 
	private void getAllEvents(CalendarService service)
			throws ServiceException, IOException {
		// Send the request and receive the response:
		CalendarEventFeed resultFeed = service.getFeed(eventFeedUrl,
				CalendarEventFeed.class);

		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = resultFeed.getEntries().get(i);
			getEventEntry(entry);
		}
	}

	private void getRangeEvents(CalendarService service,
			DateTime startTime, DateTime endTime) throws ServiceException,
			IOException {
		CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);
		myQuery.setMinimumStartTime(startTime);
		myQuery.setMaximumStartTime(endTime);

		// Send the request and receive the response:
		CalendarEventFeed resultFeed = service.query(myQuery,
				CalendarEventFeed.class);

		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = resultFeed.getEntries().get(i);
			getEventEntry(entry);
		}
	}

	
}
