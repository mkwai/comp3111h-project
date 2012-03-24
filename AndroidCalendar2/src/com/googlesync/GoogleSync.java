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
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
	private CalendarService myService;
	private static URL metafeedUrl = null;
	private static URL eventFeedUrl = null;
	
	private String userName;
	private String userPassword;

	public GoogleSync() {
	}

	public GoogleSync(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;
	}
	
	public void setUserName(String name){
		this.userName= name;
	}
	public void setUserPassword(String password){
		this.userPassword= password;
	}
	public void setUserInfo(String name, String password){
		this.userName= name;
		this.userPassword= password;
	}
	
	
	/*
	 * 
	 * METHOD 0 - Start Connection with userName, userPassword, return false if
	 * the connection fails.
	 */
	public boolean GoogleLogin() {
		myService = new CalendarService("applicationName");
		try {
			if (userName== null || userPassword== null)
				return false;
			
			myService.setUserCredentials(userName, userPassword);
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			//System.out.println("User password is not matched");
			return false;
			// e.printStackTrace();
		} catch (MalformedURLException e) {
			// Bad URL
			//System.err.println("Uh oh - you've got an invalid URL.");
			return false;
			// e.printStackTrace();
		}
		return true; // no error
	}

	/*
	 * METHOD 1 - Get all event from Google Calendar, get the details of each
	 * event using the function: getEventEntry(entry).
	 */
	public void getAllEvents() {
		CalendarEventFeed resultFeed;
		try {
			resultFeed = myService.getFeed(eventFeedUrl,
					CalendarEventFeed.class);

			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				getEventEntry(entry); // get each event entry
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOEX");
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * METHOD 2 - PRINT WITHIN A RANGE OF TIME
	 */
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

	/*
	 * helper function for 2b
	 */
	public String addDays(String dt, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.add(Calendar.DATE, days); // number of days to add
		dt = sdf.format(c.getTime()); // dt is now the new date
		return dt;
	}

	/*
	 * METHOD 2b - Overloaded. PRINT WITHIN A RANGE OF TIME
	 */
	public void getRangeEvents(String startDate, int passDay, int futureDay) {

		CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);
		String t1 = addDays(startDate, -passDay);
		String t2 = addDays(startDate, futureDay);

		myQuery.setMinimumStartTime(DateTime.parseDate(t1));
		myQuery.setMaximumStartTime(DateTime.parseDate(t2));

		CalendarEventFeed resultFeed;
		try {
			resultFeed = myService.query(myQuery, CalendarEventFeed.class);

			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				getEventEntry(entry); // get each event entry
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Called from method 1, 2 : Get info, to be stored in the database
	 */
	public void getEventEntry(CalendarEventEntry entry) {
		assert entry != null;
		
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

		/*
		 * Display in console - to be removed later
		 */
		   
		 	System.out.println(store.title);
			System.out.println(store.description);
			System.out.println(store.location);
			System.out.println(store.starttime);
			System.out.println(store.endtime);
			System.out.println();
		
		
	}

	/*
	 * METHOD 3- Create event with title, start time, end time
	 */
	public void createEvent(String eventTitle, DateTime startTime,
			DateTime endTime) throws IOException, ServiceException {
		CalendarEventEntry myEntry = new CalendarEventEntry();
		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);
		myService.insert(eventFeedUrl, myEntry);
	}

	/*
	 * METHOD 3b- Overloaded. Create event with title, start time, end time
	 */
	public void createEvent(String eventTitle, String startTime, String endTime)
			throws IOException, ServiceException {
		CalendarEventEntry myEntry = new CalendarEventEntry();
		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(DateTime.parseDateTime(startTime));
		eventTimes.setEndTime(DateTime.parseDateTime(endTime));
		myEntry.addTime(eventTimes);
		myService.insert(eventFeedUrl, myEntry);
	}

	/*
	 * METHOD 4- DELETE ALL EVENTS
	 */
	public void deleteAllEvents() throws IOException, ServiceException {
		CalendarEventFeed resultFeed;
		resultFeed = myService.getFeed(eventFeedUrl, CalendarEventFeed.class);
		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = resultFeed.getEntries().get(i);
			entry.delete();
		}
	}
}

/*
 * // testing public void insertBatchEvents(List<CalendarEventEntry>
 * eventsToInsert) { try { CalendarEventFeed batchRequest = new
 * CalendarEventFeed(); for (int i = 0; i < eventsToInsert.size(); i++) {
 * CalendarEventEntry toInsert = eventsToInsert.get(i);
 * BatchUtils.setBatchId(toInsert, String.valueOf(i));
 * BatchUtils.setBatchOperationType(toInsert, BatchOperationType.INSERT);
 * batchRequest.getEntries().add(toInsert); }
 * 
 * CalendarEventFeed feed;
 * 
 * feed = myService.getFeed(eventFeedUrl, CalendarEventFeed.class);
 * 
 * Link batchLink = feed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM); URL
 * batchUrl = new URL(batchLink.getHref());
 * 
 * } catch (Exception e) { } }
 * 
 * // testing public CalendarEventEntry getNewEvent(String title, DateTime
 * start, DateTime end) { CalendarEventEntry newEvent = new
 * CalendarEventEntry();
 * 
 * newEvent.setTitle(new PlainTextConstruct(title));
 * 
 * When eventTimes = new When(); eventTimes.setStartTime(start);
 * eventTimes.setEndTime(end); newEvent.addTime(eventTimes);
 * 
 * return newEvent; }
 */

