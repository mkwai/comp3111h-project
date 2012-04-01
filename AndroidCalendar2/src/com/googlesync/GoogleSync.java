package com.googlesync;

import com.calendar.AndroidCalendar2Activity;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;

public class GoogleSync {

	private static final String METAFEED_URL_BASE = "https://www.google.com/calendar/feeds/";
	private static final String EVENT_FEED_URL_SUFFIX = "/private/full";
	private CalendarService myService;
	private static URL metafeedUrl = null;
	private static URL eventFeedUrl = null;

	private String userName;
	private String userPassword;

	private String eventid;
	private String title;
	private String description;
	private String location;

	private String startDate;
	private String startTime;
	private DateTime startdt;
	private DateTime enddt;
	private String endDate;
	private String endTime;
	private String isPrivate;
	private String remind;

	private int count;
	private boolean isGoogleConnected = false;

	// Constructors
	public GoogleSync() {
	}

	public GoogleSync(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;
	}

	// setter
	public void setUserName(String name) {
		this.userName = name;
	}

	public void setUserPassword(String password) {
		this.userPassword = password;
	}

	public void setUserInfo(String name, String password) {
		this.userName = name;
		this.userPassword = password;
	}

	public void isGoogleConnected(boolean state) {
		isGoogleConnected = state;
	}

	public int getCount() {
		return count;
	}

	// getter
	public String getUserName() {
		return userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public boolean isGoogleConnected() {
		return isGoogleConnected;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/*
	 * Start Connection with userName, userPassword, return false if the
	 * connection fails.
	 */
	public boolean GoogleLogin() {
		myService = new CalendarService("applicationName");
		try {
			if (userName == null || userPassword == null)
				return false;

			myService.setUserCredentials(userName, userPassword);
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			// System.out.println("User password is not matched");
			return false;
			// e.printStackTrace();
		} catch (MalformedURLException e) {
			// Bad URL
			// System.err.println("Uh oh - you've got an invalid URL.");
			return false;
			// e.printStackTrace();
		}
		return true; // no error
	}

	// Get total number of event in google calendar
	public int getEventCount() {
		CalendarEventFeed resultFeed;
		int total = 0;

		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return 0;
		}

		try {
			resultFeed = myService.getFeed(eventFeedUrl,
					CalendarEventFeed.class);
			total = resultFeed.getEntries().size();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}

	/*
	 * Get all event from Google Calendar, get the details of each event using
	 * the function: getEventEntry(entry).
	 */
	public void getAllEvents() {
		CalendarEventFeed resultFeed;

		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
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
	 * Helper function- add days to a given date in string format, return a new
	 * string
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
	 * Helper function- Get info of each calendar event entry, storing in the
	 * database
	 */
	public void getEventEntry(CalendarEventEntry entry) {
		assert entry != null;

		if (entry == null) {
			System.out.println("No entry");
			return;
		}

		title = entry.getTitle().getPlainText(); // get title
		description = entry.getPlainTextContent(); // get description
		location = entry.getLocations().get(0).getValueString(); // get location

		String temp = entry.getId();
		System.out.println(temp + '\t' + title);

		if (entry.getTimes().size() > 0) {
			When eventTimes = entry.getTimes().get(0);
			if (eventTimes.getStartTime().isDateOnly()) { // Check if it is a
															// whole day event
				Boolean iswholeday = true;
			} else {
				// example format- "2012-03-01T22:40:00"
				startdt = eventTimes.getStartTime();
				String[] t = startdt.toString().split("[T:+\\.-]");

				startDate = t[0] + t[1] + t[2];
				startTime = t[3] + ":" + t[4];

				enddt = eventTimes.getEndTime();
				t = null;
				t = enddt.toString().split("[T:+\\.-]");

				endDate = t[0] + t[1] + t[2];
				endTime = t[3] + ":" + t[4];
				System.out.println(startDate);
				System.out.println(startTime);
				System.out.println(endDate);
				System.out.println(endTime);
				System.out.println();

				isPrivate = "0";
				remind = null;
			}
		}

		// adding the google event to the database
		eventid = AndroidCalendar2Activity.getDB().GiveEventID();
		String args[] = { eventid, title, startDate, endDate, startTime,
				endTime, isPrivate, location, remind };
		AndroidCalendar2Activity.getDB().insert("TimeTable", args);

		/*
		 * System.out.println(store.title);
		 * System.out.println(store.description);
		 * System.out.println(store.location);
		 * System.out.println(store.starttime);
		 * System.out.println(store.endtime); System.out.println();
		 */

	}

	/*
	 * Create google event with String eventTitle, DateTime starttime, DateTime
	 * endtime
	 */
	public void insert(String eventTitle, DateTime startTime, DateTime endTime) {
		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
		CalendarEventEntry myEntry = new CalendarEventEntry();
		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);
		try {
			myService.insert(eventFeedUrl, myEntry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Create google event with String eventTitle, String starttime, String
	 * endtime
	 */
	public void insert(String eventTitle, String startTime, String endTime) {
		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
		CalendarEventEntry myEntry = new CalendarEventEntry();
		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(DateTime.parseDateTime(startTime));
		eventTimes.setEndTime(DateTime.parseDateTime(endTime));
		myEntry.addTime(eventTimes);
		try {
			myService.insert(eventFeedUrl, myEntry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	/*
	 * DELETE ALL EVENTS
	 */
	public void deleteAllEvents() {
		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
		CalendarEventFeed resultFeed;
		try {
			resultFeed = myService.getFeed(eventFeedUrl,
					CalendarEventFeed.class);
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);
				entry.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}


	 //Get google events from a range of days
	public void getRangeEvents2(String startDate, int passDay, int futureDay) {
		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
		CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);

		String t1 = addDays(startDate, -passDay);
		String t2 = addDays(startDate, futureDay);

		myQuery.setMinimumStartTime(DateTime.parseDate(t1));
		myQuery.setMaximumStartTime(DateTime.parseDate(t2));

		myQuery.setMaxResults(1000);
		int startIndex = 1;
		int entriesReturned;

		CalendarEventFeed resultFeed;

		while (true) {
			myQuery.setStartIndex(startIndex);
			try {
				resultFeed = myService.query(myQuery, CalendarEventFeed.class);

				entriesReturned = resultFeed.getEntries().size();
				if (entriesReturned == 0)
					break;

				for (int i = 0; i < resultFeed.getEntries().size(); i++) {
					CalendarEventEntry entry = resultFeed.getEntries().get(i);
					getEventEntry(entry); // get each event entry
				}
				startIndex = startIndex + entriesReturned;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	//delete google events from a range of days
	public void delRangeEvents2(String startDate, int passDay, int futureDay) {
		if (isGoogleConnected() == false) {
			System.out.println("Connection not started");
			return;
		}
		CalendarQuery myQuery = new CalendarQuery(eventFeedUrl);

		String t1 = addDays(startDate, -passDay);
		String t2 = addDays(startDate, futureDay);

		myQuery.setMinimumStartTime(DateTime.parseDate(t1));
		myQuery.setMaximumStartTime(DateTime.parseDate(t2));

		myQuery.setMaxResults(1000);
		int startIndex = 1;
		int entriesReturned;

		CalendarEventFeed resultFeed;
		while (true) {
			myQuery.setStartIndex(startIndex);

			try {
				resultFeed = myService.query(myQuery, CalendarEventFeed.class);

				entriesReturned = resultFeed.getEntries().size();
				if (entriesReturned == 0)
					// We've hit the end of the list
					break;

				for (int i = 0; i < resultFeed.getEntries().size(); i++) {
					CalendarEventEntry entry = resultFeed.getEntries().get(i);
					entry.delete();
				}

				startIndex = startIndex + entriesReturned;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
