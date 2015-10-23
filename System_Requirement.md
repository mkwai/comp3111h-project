# COMP 3111H: Introduction to Software Engineering #
## Problem Statement ##
### DailyAssistant ###

---


**INTRODUCTION**

Since people nowadays are busy to handle job deadlines and leisure time, as well as social gathering, a helpful and portable smart phone app would be a great solution to everyday time management problem people facing. However, there is a lack in time-related apps with comprehensive features in market at the moment. In view of this, the project of DailyAssistant is conducted to assist Android mobile users’ daily lives, in particular on effective and efficient time, task and gathering management.


---


**SYSTEM REQUIREMENTS**

DailyAssistant is only available under Android version of v2.3.3 or above , time zone of GMT +8 and language setting of English.

DailyAssistant needs to provide the following functionality.


---


**1. Calendar and Scheduler**

Calendar and scheduler are the basic features of the personal assistant.


**1.1 Monthly Calendar**

1.1.1 First of all, there is a monthly calendar. The default page shows the calendar of the current month.

1.1.2 For the monthly calendar view, user can press “<” and “>” to view the previous and next month calendar respectively.

1.1.3 User can select a date to see the monthly calendar of the month directly, using “go to” button. The date can be set by either using the “+” and “-” button or typing the month, day and year in the respective boxes. Wrong input is not allowed.

1.1.4 The starting date of the calendar in DailyAssistant is 1/1/1900 and the last is 31/12/2100. 24 hours is available in the schedule in which each row represent 30 minutes. If the date entered/selected by user is out of the range, it should cast back the date within the range when set pressed.




**1.2 Daily Schedule**

1.2.1 By selecting a day in the monthly calendar and click “View Daily Schedule”, a daily timetable of that selected day can be viewed in the vertical interface, showing the detailed schedule of a day.

1.2.2 Three-day calendar, including today, tomorrow and the day after tomorrow, can be shown in the horizontal interface.

1.2.3 Users can scroll up and down the screen to view the schedule if it exceeds the height of the screen. The timetable starts from 12:00am to 12:00am.

1.2.4 For the vertical daily timetable, the schedule of previous day will be shown if the user presses the "previous" button; and, the schedule of next day can be viewed if the user presses the "next" button.

1.2.5 In horizontal mode, pressing “previous” and “next” will shift one day schedule to the past and the future respectively.



---


**2. Managing Events**

There is an add event button in both calendar view page and daily schedule page, which allows users to add an event into the schedule.

**2.1 Adding an event**

2.1.1 It requires the following information entered by customer: title, starting date and time, ending date and time, event location(detail in requirement 9), reminder setting, in addition to the following optional fields: contact person, private event).

2.1.2 For the title field, it cannot be empty. Moreover, the maximum character number is 30. For the first character, space is not allowed. Except the first character, any character, including numbers, English character, spaces and special characters can be included in the title.

2.1.3 In entering the starting and ending date field, three date boxes of month, day and year are used. The default starting and ending date be that of the day user originally viewing, allow user to select the day, month, year by scrolling up and down the scroll bars respectively.

2.1.4 Similar for entering the time fields, hour and minutes boxes are there for user to select the time. The default setting of the starting time is the closest time in the 5-minutes intervals, with one hour later is the default ending time. The minute box has options of only 0,5,10,15,20,25,30,35,40,45,50,and 55 available for the user  to choose.

The details of those optional fields are discussed in the following:

2.1.5 For the reminder checkbox, if  user check the reminder checkbox with no entry of location in location field, then there will be alarm reminder occur at the starting time of the event added.

2.1.6 For the location field, please refer to requirement 3.

2.1.7 In contact person, user can choose the contact person of that event either from the contact list of the phone, or enter a name (maximum 20 characters).

2.1.8 If private event check box is checked, then the event will not be shared with friends (detail in requirement 7). Otherwise, it will be shared if the private event check box is not checked.

2.1.9 Clicking “Confirm”, the event will be added to the schedule. Clicking “Cancel”, it will go back to main page.

**2.2 Editing and Deleting Event**

After an event is added, user can view via the daily schedule. User can edit the event information, which have the same input fields as the add event page.

2.2.1 Click any event and then click “Edit”, the edit event page is displayed.

2.2.2 The default settings of those fields in edit event page are the current information of that event.

2.2.3 User can delete the event in the edit event page by clicking “Delete This Event”.



---


**3. Location-based Alarm**

People sometimes forget a meeting or failed to estimate travelling time. To prevent missing an appointment or being late, DailyAssistant provides location-based alarm function. If an event is set to require an alarm reminder, this function will be applied.


3.1 User can input keyword of location in the location field and then click “search”. Then, the list of searching result will be shown and user can select the one he/she want to enter.

3.2 If there is location searched and entered, user can check the reminder checkbox in order to enable the location-based alarm reminder function.

3.3 Using the GPS function of smart phones, DailyAssistant can get the current location of the user. The travelling time between current location and the next event location can be calculated and updated from time to time. A departure message will pop up to alert the user when the current time plus the travelling time equals to the starting time of the next event.

3.4 If the destination is unreachable, such as too far away or requiring to go there by specific transportation, the alarm will not be able to alert the user.

3.5 All the location searching result and travelling time calculation are based on Google map system.

3.6 In order to switch the alarm off before or during the alarm, user can edit the event and uncheck the reminder checkbox.


---


**4. Linkage between Photos Taken and Events**


4.1 Users are able to view a page with all photos taken by the mobile during a particular event in timetable. Click on the event and click “View photos”, the photo list will be shown. Photo can be viewed by clicking its photo name on the list.


---


**5. Synchronization with Google Calendar**


Some users are using other online calendars or timetable, including Google Calendar. DailyAssistant provides the function of synchronizing the online Google Calendar with the one stored in the phone. To achieve this, a Google account is required and the Google calendar has to be already started.


5.1 User’s Google account ID and password required in the “User Name” and “Password” fields.

5.2 It cannot be synchronized and an error message box pop out if user click “synchronize” with wrong account information entered.

5.2 Users can select the number of days before and after the current day that they want to synchronize. The choices include: 7 days, 30 days, 60 days, 90 days, 180 days and 365 days, for both past day and future day.

5.3 Click “Synchronize” to synchronize the Google calendar and the calendar in the smart phone under Internet connection After the successful connection, when the user edited his/her schedule online in Google calendar, the change will be updated in the calendar of DailyAssistant. Similarly, when the schedule in our phone app is edited, the change can be seen in the user’s google calendar also.

5.4 User can click “refresh” in dailyview to update the calendar in DailyAssistant.

5.5 User can click “Disconnect” to disconnect the linkage. User needs to reconnect by providing login information and clicking the synchronize button again.



---



**6. Exporting daily timetable**


6.1 When the user pressed “ Export jpg” in daily schedule page, the screen with part of the schedule shown will be captured and exported to a jpeg. file in an album folder called “dailyassistant\_export” stored in the phone.


---


**7. Timetable Sharing Social Interaction System**

The basic function of the timetable management is using local machine (android phone) to manipulate timetable data, which does not require Internet connection. Therefore, this kind of timetable management is using local private data only; no one can assess this data if the user does not permit.


Apart from those basic time management function, an advanced function of the app is provided, which is featured as a social interaction system, requiring Internet connection. It allows users to share their time table to their friends via Internet. As the system requires account to access the server, users can use Facebook account to log in when internet connection is available.


**7.1 Login**

7.1.2 Click “Login” and the Facebook login page is displayed.

7.1.3 User can login by entering Facebook account email address and password and click “login”.

7.1.4 If the user does not have an existing account, there is a button to sign up, which direct to the page of Facebook registration.

7.1.5 If the account information entered is invalid, user cannot login.

**7.2 Refresh Friend List**

7.2.1 By clicking “Refresh friend list”, the updated Facebook friend list is displayed in the page.


**7.3 Share Timetable**

7.3.1 When a user wants to share his timetable to others, he can press the share button after checking some checkboxes in the list of the user to choose who can browse his/her timetable. All people selected will be shown in the share page after pressing the share time table button.

7.3.2 In the sharing page, user can select a time period.

7.3.3 After pressing the share button, the selected group of users can view the user’s shared schedule. They can only browse the latest version of timetable, but not the deleted previous one.

7.3.4 Private events cannot be seen in the shared timetable by friends.


**7.4 Download Timetable**

7.4.1 When a group of friends’ checkboxes are checked and download button is pressed, latest version of timetable information is got and saved into user system. User can click on the friend's name to load and view saved timetables.

7.4.2 By clicking name of a friend whose time  table has not been downloaded, “no single record” message appear.


**7.5 Reset**

7.5.1 By clicking “Reset”, the friend list and the downloaded timetables will be cleared.


---


**8. Finding Common Free Time**


More than that, there is a feature in DailyAssistant that user can find out the common available time slots among friends in a certain period.


8.1 User can select a group of people, whose timetables may be already downloaded and click “Find commontime”. The finding common free time page is displayed.

8.2 User can select a certain period in the same way with sharing timetable.

8.3 Click “Calculate the common free time” and all common available time slots among all friends and user him/herself is listed out, which are consist of day, month, week, starting time-ending time, such as “29/2 (Sat) 8:00-10:00”.

8.4 The common free time result is in chronological order.

8.5 There is a filter function to set constraints on the resulting list. Different option is provided, including Midnight 00:00 to 06:00, Morning 06:00 to 12:00, AfterNoon 12:00 to 18:00, Night 18:00 to 24:00, Saturday and Sunday. User can check those time period he want to filter out.

8.6 When there is no common free time slot, result would be empty and hence show nothing.

8.7 If the whole period selected is the common free time, then a message “all days free !” appears.



---


**9. To-Do List**

To-Do list page shows the list of tasks. User can set and update their progress in percentage in order to keep track of the progress for completing that task.



**9.1 Adding Task**
9.1.1 Clicking “+”, the add task page displays. User has to enter information in the title and progress fields (required) in order to create a task.

9.1.2 User has to enter the title with maximum limit of 30 characters. For the first character, space is not allowed. Except the first and the last character, any input character, including numbers, English character space and special character can be included in the title.

9.1.3 The default setting of the progress is 0%. User can move the progress bar to set the progress from 0%-100% (integer only) at any time to indicate the percentage of completion.

9.1.4 User can also set optional information including location (maximum 100 characters) and deadline time (date-time format) for a task.

9.1.5 When checkbox of deadline time is checked, the user can set the deadline date and time and enable the alarm reminder. The minute box has options of only 0,5,10,15,20,25,30,35,40,45,50,and 55 available for the user  to choose.

9.1.6 When the alarm reminder checkbox is checked, user can set the time period the alarm appear before the deadline. Options include 5 minutes, 30 minutes, 1 hour, 2 hours, 6 hours and 1 day before the deadline. If the task is finished before the alarm reminder with progress of 100%, there will not be alarm notification. If a task does not have a deadline, the user can uncheck the deadline checkbox, and there should be no alarm setting.

9.1.7 By clicking “Confirm” after fields are filled, the new task will be added into the to-do list page. By clicking “Cancel”, it will return to the to-do list page.



**9.2 Editing and Deleting Tasks**

9.2.1 After a task is created, user can edit the task by clicking on the task. Information of the task can be changed.

9.2.2 The default settings of those fields in edit task page are the current information of that task.

9.2.3 User can set the progress to 100% in order to indicate a particular task that has already been finished.

9.2.4 User can remove a particular task from the to-do list in the edit page by clicking “Delete This Task”.

9.2.5 User can delete all of the completed tasks by clicking “delete finished task”


**9.3 Sorting the list**

9.3.1 In the To-Do list view, user can sort the task according to different criteria, including sort by title, sort by progress and sort by deadline. The title-sorted list is listed in the order from numbers to special character to English character and in ascending/alphabetic order. The progress-sorted list is listed from less progress to more progress. The deadline-sorted list is listed in the order from nearer time to farer time.

9.3.2 User can uncheck the “show completed task” checkbox  in order to only show those tasks in progress.


---


**10. Hindering Access to Specific Website**

During some event, such as having lectures, holding a meeting and studying period before exams, the user may hope to restrict him or herself from accessing some website of entertainment, such as Facebook or Twitter. DailyAssistant provides a function to remind users not to browse these websites.


10.1 User can enter the website and click “add”.

10.2 The added website will be listed out.

10.3 By clicking the website in the list, the user can delete that website from the list.

10.4 After clicking “on”, the access of website added will be hindered by popping out alert message everytime the page refresh. If a web site is added already then the same website cannot be add again.

10.5 User can turn off the function by clicking “off”.