package com.googlesync;

import android.os.Looper;

import com.calendar.AndroidCalendar2Activity;
import com.google.gdata.client.Query;
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

	private int count;
	private boolean isGoogleConnected = false;

	// Constructors
	public GoogleSync() {} 

	public GoogleSync(String userName, String userPassword) {
		this.userName = userName;
		this.userPassword = userPassword;
	}

	// setter
	public void setUserName(String name) {this.userName = name;}
	public void setUserPassword(String password) {this.userPassword = password;}
	public void setUserInfo(String name, String password) {
		this.userName = name;
		this.userPassword = password;
	}
	
	// getter
	public String getUserName() {return userName;}
	public String getUserPassword() {return userPassword;}
	public boolean isGoogleConnected() {return isGoogleConnected;}
	public void setCount(int count) {this.count = count;}
	public void isGoogleConnected(boolean state) {isGoogleConnected = state;}
	public int getCount() {return count;}

	// Start Connection with userName, userPassword, return false if the connection fails.
	public boolean GoogleLogin() {
		myService = new CalendarService("applicationName");
		try {
			if (userName == null || userPassword == null)
				return false;
			myService.setUserCredentials(userName, userPassword);
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
			resultFeed = myService.getFeed(eventFeedUrl,CalendarEventFeed.class);
			total = resultFeed.getEntries().size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

	//Helper function- add days to a given date in string format, return a new string
	public String addDays(String dt, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(dt));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.add(Calendar.DATE, days); // number of days to add
		dt = sdf.format(c.getTime()); // dt is now the new date
		return dt;
	}

	//Helper function- Get info of each calendar event entry, storing in the database
	public void getEventEntry(CalendarEventEntry entry) {
		assert entry != null;
		if (entry == null) {
			System.out.println("No entry");
			return;
		}
		
		String title = entry.getTitle().getPlainText(); 
		String description = entry.getPlainTextContent();
		String location;
		if (entry.getLocations().get(0).getValueString()== null)
			location = "";
		else
			location= entry.getLocations().get(0).getValueString();
		
		String eventid = entry.getId().substring(entry.getId().lastIndexOf("/") + 1);
		//eventid= entry.getId();
		 
		DateTime startdt, enddt;
		String startDate = null, startTime = null, endDate = null, endTime = null;

		if (entry.getTimes().size() > 0) {
			When eventTimes = entry.getTimes().get(0);
			// Check if it is a whole day event
			if (eventTimes.getStartTime().isDateOnly()) { 	 		
				Boolean iswholeday = true;
			} else { 
				// example format- "2012-03-01T22:40:00"
				startdt = eventTimes.getStartTime();
				enddt = eventTimes.getEndTime(); 
				
				String[] t = startdt.toString().split("[T:+\\.-]");
				startDate = t[0] + t[1] + t[2];
				startTime = t[3] + ":" + t[4];
				
				String[] t2 = enddt.toString().split("[T:+\\.-]");
				endDate = t2[0] + t2[1] + t2[2];
				endTime = t2[3] + ":" + t2[4];
				
				//System.out.println(startDate);
				//System.out.println(startTime);
				//System.out.println(endDate);
				//System.out.println(endTime);
				//System.out.println();
			}
		}
		
		String isPrivate = "0";
		String remind = ""; 
		String milliS= "";
		String contact= "";
		
		
		// check if record is in the database
		JSONArray temp= AndroidCalendar2Activity.getDB().fetchAllNotes(
				"TimeTable", new String[]{"eventid"}, new String[] {eventid} ) ; 
		if (temp.length()==0 ){  // new event
			System.out.println("new event: ");
			String args[] = { eventid, title, startDate, endDate, startTime,
				endTime, isPrivate, location, remind, milliS, contact};
			AndroidCalendar2Activity.getDB().insert("TimeTable", args); 
			//AndroidCalendar2Activity.getDB().insert("RefTable", new String[] { eventid});
		}
		else{	// old google event -> update record
			JSONArray temp2= AndroidCalendar2Activity.getDB().fetchAllNotes(
					"TimeTable",new String[]{"eventid"}, new String[] {eventid} ) ;
			System.out.println("old event -> update record: ");
			
			String args[] = {title, startDate, endDate,
					startTime, endTime, isPrivate, location, remind, milliS+"",contact};
			String fields[] = {"title", "startDate", "endDate", 
					"startTime", "endTime", "private", "location", "reminder", "milliS", "contact"};
			String condition=" eventID = '"+eventid+"' ";
			
			AndroidCalendar2Activity.getDB().updateConditional("TimeTable", condition, fields, args);
		}
		System.out.println(); 
		System.out.println("EVENTID= " +eventid );
		System.out.println("EditLink= "+entry.getEditLink());
		System.out.println("getHref= "+entry.getEditLink().getHref());
		System.out.println("title= "+title);
			
		
			//	} 

			//if (temp2.length()== 0){ 
			/*else{
				System.out.println("old event -detail correct, update data: ");
				String condition= "eventid = '" +eventid + "'" ; 
				String fields[] = { "title","startDate","endDate", "startTime","endTime"};
				String args[] = { title,startDate,endDate,startTime,endTime};
				AndroidCalendar2Activity.getDB().updateConditional("TimeTable", condition, fields, args);
			}*/
		
		// check if record is in the database
		/*JSONArray temp2= AndroidCalendar2Activity.getDB().fetchAllNotes(
				"TimeTable",
				new String[]{"title","startDate","endDate", "startTime","endTime"}, 
				new String[] {title,startDate,endDate,startTime,endTime} 
				) ;
		if (temp2.length()== 0){		
			//eventid = AndroidCalendar2Activity.getDB().GiveEventID();
			String args[] = { eventid, title, startDate, endDate, startTime,
					endTime, isPrivate, location, remind, milliS, contact};
			
			AndroidCalendar2Activity.getDB().insert("TimeTable", args);
		} 
		
		System.out.println("****************");
		 System.out.println(eventid);
		 System.out.println(title);
		 System.out.println(startDate);
		 System.out.println(endDate);
		 System.out.println(startTime); 
		 System.out.println(endTime); 
		 System.out.println(isPrivate); 
		 System.out.println(location); 
		 System.out.println(remind); 
		
		
	
		 //String eventid2 = entry.getId().substring(entry.getId().lastIndexOf("/") + 1);
		*/
		//////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
	}
	public CalendarEventEntry getGoogleEvent( String googleEventId){	
		 try { 
			 String eventEntryUrl= "https://www.google.com/calendar/feeds/" + userName+ "/private/full/" + googleEventId ;
			 System.out.println("********eventEntryUrl= "+eventEntryUrl);
			 Query partialQuery = new Query(new URL(eventEntryUrl));
			 CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
			 return event;
			 
		} catch (Exception e) {
			System.out.println("no such event ID");
			//e.printStackTrace();
			return null;
		} 
	}	
	
	public void deleteGoogleEvent( final String googleEventId ){	
		 new Thread(new Runnable() {
		  	 public void run() {
				 String eventEntryUrl= "https://www.google.com/calendar/feeds/" + userName+ "/private/full/" + googleEventId ;
				 //System.out.println("********eventEntryUrl= "+eventEntryUrl);
				 try { 
					 Looper.prepare();
					 Query partialQuery = new Query(new URL(eventEntryUrl));
					 CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
					 event.delete();
					 Looper.loop();
				} catch (Exception e) {
					e.printStackTrace();
					
				}
		  	}
		}).start();
	}
	/*public void insert(String eventTitle, String startTime, String endTime) {
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

*/ 
	public String updateGoogleEvent( final String googleEventId, final String eventTitle, 
									final DateTime startDateTime, final DateTime endDateTime){	
		 
		String eventEntryUrl= "https://www.google.com/calendar/feeds/" + userName+ "/private/full/" + googleEventId ;
		 try { 
			 //delete an event 
			 Query partialQuery = new Query(new URL(eventEntryUrl));
			 System.out.println("Delete :" + googleEventId);
			 CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
			 event.delete();
			 System.out.println("Delete successfully!");
			 
			 //add new event
			 CalendarEventEntry myEntry = new CalendarEventEntry();
			 myEntry.setTitle(new PlainTextConstruct(eventTitle));

			 When eventTimes = new When();
			 eventTimes.setStartTime(startDateTime);
		 	 eventTimes.setEndTime(endDateTime);
		 	 myEntry.addTime(eventTimes);
			
			 // retrieve eventID after insert
			 CalendarEventEntry entry= myService.insert(eventFeedUrl, myEntry);
			 return entry.getId().substring(entry.getId().lastIndexOf("/") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	// Create google event with String eventTitle, DateTime starttime, DateTime endtime
	// DateTime Format = "2012-03-01T22:40:00" 
	public String insert(String eventTitle, DateTime startTime, DateTime endTime) {
		if (isGoogleConnected() == false) { 
			System.out.println("Connection not started");
			return "";
		}
		CalendarEventEntry myEntry = new CalendarEventEntry();
		myEntry.setTitle(new PlainTextConstruct(eventTitle));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);
		try {
			System.out.println("Insert a google calendar event! \"" + eventTitle+ "\"");
			
			// retrieve eventID after insert
			CalendarEventEntry entry= myService.insert(eventFeedUrl, myEntry);
			String eventid = entry.getId().substring(entry.getId().lastIndexOf("/") + 1);
			//System.out.println("TITLE (INSERT)= " + eventTitle);
			//System.out.println("ID (INSERT)= " + eventID);
			
			return eventid;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}
	/*
	public void deleteGoogleEvent( final String googleEventId){	
		 new Thread(new Runnable() {
					public void run() {
					 String eventEntryUrl= "https://www.google.com/calendar/feeds/" + userName+ "/private/full/" + googleEventId ;
					 
					// String eventEntryUrl= " https://www.google.com/calendar/feeds/klhoab%40gmail.com/private/full/6r8ukatsoglfunu9fsqa5q1na8"; 
					System.out.println("********eventEntryUrl= "+eventEntryUrl);
			 
					 try { 
						 Looper.prepare();
						 Query partialQuery = new Query(new URL(eventEntryUrl));
						 CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
						 System.out.println("ooooooEVENTID=  " + event.getId());
						 System.out.println("ooooooTITLE=  " + event.getTitle().getPlainText());
						 //event.setTitle(new PlainTextConstruct("on9") );
						 //event.update();
						 event.delete();
						 Looper.loop();
					} catch (Exception e) {
						//System.out.println("no such event ID");
						e.printStackTrace();
						
					}
				}
		 }).start();
			 
		
	}*/
	
	/*public void temp2( String googleEventId){	
		 try {
			 //String eventEntryUrl = "https://www.google.com/calendar/feeds/klhoab@gmail.com/private/full/aq5pnd10417quu6n66l43pd5k8";
			 
			 String eventEntryUrl= "https://www.google.com/calendar/feeds/" + userName+ "/private/full/" + googleEventId ;
			 Query partialQuery = new Query(new URL(eventEntryUrl));
			 CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
			 
			 //System.out.println("ooooooEVENTID=  " + event.getId());
			// System.out.println("ooooooTITLE=  " + event.getTitle().getPlainText());
			 
			 event.setTitle(new PlainTextConstruct("NEW"+event.getTitle().getPlainText() )) ;
			 event.update();
			 
			 //check
			 event = myService.getEntry(partialQuery.getUrl(),CalendarEventEntry.class);
			 System.out.println("NNNNNoEVENTID=  " + event.getId());
			 System.out.println("NNNNNoTITLE=  " + event.getTitle().getPlainText());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}*/
	/*
	 private static final String CALENDAR_FEEDS_PREFIX = "https://www.google.com/calendar/feeds/";

	 public void updateAttendeeStatus() throws IOException, ServiceException {
		 String eventId= "aq5pnd10417quu6n66l43pd5k8";
		
		 //https://www.google.com/calendar/feeds/klhoab%40gmail.com/private/full/aq5pnd10417quu6n66l43pd5k8

		    // URL of calendar entry to update.
		    String eventEntryUrl = CALENDAR_FEEDS_PREFIX + userName + "/private/full/" + eventId;
		    // Selection criteria to fetch only the attendee status of specified	user.
		    //String selectAttendee =
		    //    "@gd:etag,title,gd:who[@email='" + uname + "']";

		    Query partialQuery = new Query(new URL(eventEntryUrl));
		   // partialQuery.setFields(selectAttendee);

		    
		    CalendarEventEntry event = myService.getEntry(partialQuery.getUrl(),
		        CalendarEventEntry.class);
		    
		    System.out.println("TITLE!!!!!!!!!!= "+ event.getTitle());
		    // The participant list will contain exactly one attendee matching
		    // above partial query selection criteria.
		   // event.getParticipants().get(0).setAttendeeStatus(selection);

		    // Field selection to update attendeeStatus only.
		   // String toUpdateFields = "gd:who/gd:attendeeStatus";

		    // Make patch request which returns full representation for the event.
		   // event = service.patch(
		   //     new URL(eventEntryUrl), toUpdateFields, event);

		    // Print the updated attendee status.
		   // OUT.println(event.getTitle().getPlainText() + " updated to: "
		   //     + event.getParticipants().get(0).getAttendeeStatus());
		  } 
*/
	

	/* Create google event with String eventTitle, String starttime, String endtime
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
			String eventID= myService.insert(eventFeedUrl, myEntry).getId();
			System.out.print("TITLE (INSERT)= " + eventTitle);
			System.out.print("ID (INSERT)= " + eventID);
			//myService.insert(eventFeedUrl, myEntry);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}*/

	/*
	//DELETE ALL EVENTS
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/

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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*
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
	*/
	/*
	public void temp(){
		try {
			String eventTitle= "NEW EVENT";
			
			//URL feedUrl = new URL ("http://www.google.com/calendar/feeds/klhoab%40gmail.com/events/aq5pnd10417quu6n66l43pd5k8");
			URL feedUrl= new URL ("https://www.google.com/calendar/feeds/klhoab%40gmail.com/private/full/aq5pnd10417quu6n66l43pd5k8");

			//CalendarEventEntry entry = myFeed.getEntries().get(0);
			//entry.setTitle(new PlainTextConstruct("Important meeting"));
			
			//URL editUrl = new URL(entry.getEditLink().getHref());
			//System.out.println("########EDIT LINK= " +entry.getEditLink().getHref());
			
			//myService.insert(eventFeedUrl, entry);
			//CalendarEventEntry updatedEntry = (CalendarEventEntry)myService.update(feedUrl, entry);
			
			/////////////////
			//System.out.println("########TITLE= " +entry.getTitle());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
			//myService.insert(eventFeedUrl, myEntry);
			
			//CalendarEventEntry updatedEntry = (CalendarEventEntry)myService.update(editUrl, myEntry);
	}

		
	}*/
}

