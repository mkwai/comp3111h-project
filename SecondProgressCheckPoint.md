INNER TEST 2 TEST CASES



---


**To-do List (ref. spec 6)**

1.     Click “To-do list” and then the “+”button. The page with different fields will be displayed. Is the default setting of 0% being the progress bar?

2.     Move the progress scroll bar. Can user select any integer percentage from 0% to 100%?


3.     Enter “123abc#$%” in the title fieldset the progress to “24%” and leave the location fields empty and uncheck the deadline checkbox,  . Click “Confirm” .  Can the task be added and displayed in the to-do list successfully?

4.     Click “+” button and enter “ 	”  in the title field(5 spaces). Is the task can not be added and a message box popped out?


5.     Edit the task “123abc#$%”.  Enter “Taskssss” in the title field. Set the progress to 99%. Enter “HKUST =]” in the location field and then select the deadline time to more than 10 minutes after the current time. Tick the reminder setting and set it to alarm “5 minutes” before the deadline time. Click “Confirm”. Can the task be edited and displayed in the to-do list successfully?


6.     Wait for a while. Is there an alarm reminder occur 5 minutes before the deadline time (5 minutes later)?

7.     After stopping the alarm, change the deadline time to more than 2 hours and 5 minutes later and the progress of task “123abc#$%” to 0%. Keep the alarm and set it to “2 hours before the deadline time”. Click “Confirm”. Wait for 5 minutes, is there no alarm reminder occur?

8.     Add a new task “abc” and set the progress to 100%. Then uncheck the “show completed task”. Is the tasks disappear is the list view ?

9.     Add 3 tasks with title “Aa”, “aa”, “bb”  with the progress 0%, 20% and 15% respectively. Select “sort by  progress”. Is the sorted list shown correctly?

10.   Select “sort by title”. Is the list shown in alphabetical order? (0,1..A..Z,a..z)



---


**Hindering the Access of Specific Websites (ref. spec 7)**

1.     Click “Web Hindering”. Enter “hk.yahoo.com” and “www.facebook.com” into the field of websites that wanted to be restricted. Close the app and access hk.yahoo.com. Keep going to different pages in www.yahoo.com.hk  Is there a message box reminder pop out everytime the page refresh?

2.     Go to www.ust.hk and browsing different pages of www.ust.hk. Can it be browsed successfully without any obstacles?

3.     Access www.facebook.com. Is the function of hindering the access work again?


---


**Linkage between Photos Taken and Events (ref. spec 8)**

1.    Add an event with title "test1", set the starting time be the current time and the ending time  be 1 hour later. Then take several photos using the phone camera. Back to the Daily Scheulde view, click the event, click the button “view photo” in the dialog box, are the photos appeared in the list?

2.     Add another event, "test2" which start 1 hour later, are those photos appeared in the list of this events?

3.    Delete one of the photo. Is the list of photo renew?

4.     Edit "test1", minus one hour in the start time and end time. Then view photo again. Can those photo be view via "test1" ?


---


**Add/Edit Event (ref. spec 2)**

1.   	Add an event. Enter “private event” in the title field and set the starting time be 5:30pm and the ending time be 7:45pm of the day. In the contact person field, Click “….”. Can user select a contact person from the contact list in the phone or SIM card into the field?

2.   	Delete the contact person just added. Enter  “Peter Chan 12345678!@#”(21 characters) in the contact field this time. Can the contact person be successfully set?

3.   	Re-enter “Peter Chan=]” into the contact person field. Tick the private event option. Click “Add”. Can the event added displayed in the schedule?


---


**Export timetable (ref. spec 3)**

1.       In the page of the daily schedule with the event just added. Click “Export jpg”. Is there a jpeg image of the daily schedule exported to a specified folder in the phone?

2.       Back to the daily schedule page. Rotate the phone to view three days schedule horizontally. Click “Export”. Is there a jpeg image of the three-days daily schedule exported to a photo folder in the phone?

3.       Is event “Comp3111H Meeting” displayed in the exported timetable image?


---


**Synchronization with Google Calendar (ref. spec 3)**

1. Click “Google Sync.” and login with a Google account and password

2. Add an event, then go to Google Calendar with that account. Is the new event in the device synchronized to Google calendar?

3 .Delete the event which is just synchronized, is the event also deleted in Google calendar?


---




**Timetable Sharing Social Interaction System (ref. spec 4)**

1. Click “Sharing” button and click “login”. Login with an existing Facebook account and then click “Refresh”, is the account’s friend list, which include all friends, showed in the page?


---


**Filtering Function of Finding Common Time (ref. spec 5)**

1. Add a event in today with the duration is 10:00-14:00. Then uses another account that had shared that schedule of today to it, and find out the common free time by clicking “Find common time”. Is the common free time shown correctly, which is the time excluded 10:00-14:00?

2. Set the filter to 06:00-12:00, are the previous common free time slot found on that time period being filtered out?

3. Use another period of time to calculate the common time. The set the filter to "Sunday". Can the common free time slot shown correctly?


---


**TESTING RESULT**

TO BE UPDATE.