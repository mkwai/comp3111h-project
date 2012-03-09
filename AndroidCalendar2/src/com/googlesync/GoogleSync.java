
package com.googlesync;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class GoogleSync {
	public class Event {
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

	private String userName;
	private String userPassword;

	CalendarService myService;
	DateTime start;
	DateTime end;

	public GoogleSync() {

	}

	public GoogleSync(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;
		try {
			myService = new CalendarService("exampleCo-exampleApp-1");
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
			myService.setUserCredentials(userName, userPassword);

		} catch (Exception e) {
		}
	}

	// METHOD 1 - PRINT ALL
	public void getAllEvents() {
		try {
			CalendarEventFeed resultFeed;
			resultFeed = myService.getFeed(eventFeedUrl,
					CalendarEventFeed.class);
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				getEventEntry(entry); // get each event entry
			}

		} catch (Exception e) {
		}
	}

	// METHOD 2 - PRINT WITHIN A RANGE OF TIME
	public void getRangeEvents(DateTime startTime, DateTime endTime) {
		try {
			CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);
			
			myQuery.setMinimumStartTime(startTime);
			myQuery.setMaximumStartTime(endTime);

			CalendarEventFeed resultFeed = myService.query(myQuery,
					CalendarEventFeed.class);

			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				getEventEntry(entry); // get each event entry
			}

		} catch (Exception e) {
		}
	}

	// Get info, to be stored in the database
	public void getEventEntry(CalendarEventEntry entry) {
		assert entry != null;
		DateFormat dfGoogle = new SimpleDateFormat("yyyy-MM-dd'T00:00:00'");

		Event store = new Event();

		store.title = entry.getTitle().getPlainText(); // get title
		store.description = entry.getPlainTextContent(); // get description
		store.location = entry.getLocations().get(0).getValueString(); // get
																		// location

		if (entry.getTimes().size() > 0) {
			When eventTimes = entry.getTimes().get(0);
			if (eventTimes.getStartTime().isDateOnly()) { // Check if it is a
															// whole day event
				store.iswholeday = true;
			} else {
				store.starttime = eventTimes.getStartTime();
				store.endtime = eventTimes.getEndTime();
			}
		}

		// Display in console - to be removed later
		System.out.println(store.title);
		System.out.println(store.description);
		System.out.println(store.location);
		System.out.println(store.starttime);
		System.out.println(store.endtime);
		System.out.println();
	}

	// METHOD 3- Create event with title, start time, end time
	public void createEvent(String eventTitle, DateTime startTime,
			DateTime endTime) {
		CalendarEventEntry myEntry = new CalendarEventEntry();

		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);

		try {
			myService.insert(eventFeedUrl, myEntry);
		} catch (Exception e) {
		}
	}

	// METHOD 4- DELETE ALL EVENTS
	public void deleteAllEvents() {
		try {
			CalendarEventFeed resultFeed;

			resultFeed = myService.getFeed(eventFeedUrl,
					CalendarEventFeed.class);
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				entry.delete();
			}
		} catch (Exception e) {
		}
	}

	// testing
	public void insertBatchEvents(List<CalendarEventEntry> eventsToInsert) {
		try {
			CalendarEventFeed batchRequest = new CalendarEventFeed();
			for (int i = 0; i < eventsToInsert.size(); i++) {
				CalendarEventEntry toInsert = eventsToInsert.get(i);
				BatchUtils.setBatchId(toInsert, String.valueOf(i));
				BatchUtils.setBatchOperationType(toInsert,
						BatchOperationType.INSERT);
				batchRequest.getEntries().add(toInsert);
			}

			CalendarEventFeed feed;

			feed = myService.getFeed(eventFeedUrl, CalendarEventFeed.class);

			Link batchLink = feed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
			URL batchUrl = new URL(batchLink.getHref());

		} catch (Exception e) {
		}
	}
	
	// testing
	public CalendarEventEntry getNewEvent(String title, DateTime start,
			DateTime end) {
		CalendarEventEntry newEvent = new CalendarEventEntry();

		newEvent.setTitle(new PlainTextConstruct(title));

		When eventTimes = new When();
		eventTimes.setStartTime(start);
		eventTimes.setEndTime(end);
		newEvent.addTime(eventTimes);

		return newEvent;
	}
	
	/*
	 * To Demonstrate how to use this class
	 * 
	 * 
	   GoogleSync temp = new GoogleSync(userName, userPassword);

		//temp.deleteAllEvents();

		temp.createEvent("eventTitle0",
				DateTime.parseDateTime("2012-03-01T22:40:00"),
				DateTime.parseDateTime("2012-03-01T22:45:00"));
				
		temp.getAllEvents();
		//temp.getgetRangeEvents(start,end);
		

	 */
}
