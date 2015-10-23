# COMP 3111H: Introduction to Software Engineering #
## Problem Statement ##
### DailyAssistant ###


---



**INTRODUCTION**

Since people nowadays are busy to handle job deadlines and leisure time, as well as social gathering, a helpful and portable smart phone app would be a great solution to everyday time management problem people facing. However, there is a lack in time-related apps with comprehensive features in market at the moment. In view of this, the project of DailyAssistant is conducted to assist Android mobile users’ daily lives, in particular on effective and efficient time, task and gathering management.


---


**SYSTEM REQUIREMENTS**

The DailyAssistant needs to provide the following functionality.


---


**1. Calendar and Scheduler**

Calendar and scheduler are the basic features of the personal assistant. First of all, there is a monthly calendar. The default page shows the calendar of current month. By selecting a day in the monthly calendar, a daily timetable of that selected day can be viewed in the vertical interface, showing the detailed schedule of a day. Three-day calendar, including today, tomorrow and the day after tomorrow, can be shown in the horizontal interface.

For the monthly calendar view, user can scroll up to view the previous month calendar and scroll down to view the next month calendar.

For the daily schedule view, users can scroll up and down the screen to view the schedule if it exceeds the height of the screen. Pressing "previous" and "next" button, users can view the information of another day. Concerning the vertical daily timetable, the schedule of previous day will be shown if the user presses the "previous" button; and, the schedule of next day can be viewed if the user presses the "next" button. In horizontal mode, three days are shown in a screen.

Moreover, a user can select a date to see the monthly calendar of the month directly. More functions and features requirement of the app will be discussed in the following.

The starting date of the calendar in DailyAssistant is 1/1/1950 and the last is 31/12/2050. 24 hours is available in the schedule in which each row represent 30 minutes.


---


**2. Adding an event**

There is an “+” button, which allows users to add an event into the schedule. Adding an new event requires the following information entered by customer: title (maximum 30 characters), starting date and time, ending date and time, event location(detail in requirement 9), reminder setting, in addition to the following optional fields: contact person, private event, restrict facebook/twitter during event(detail in requirement 7).

In entering the starting and ending date field, date format is used. A box will appear when inputting the date, which has the default starting and ending date be that of the day user originally viewing, allow user to select the day, month, year by scrolling up and down the scroll bars respectively. Similar for entering the time fields, hour and minutes scroll bars is there for user to select the time. The default setting of the starting time is the closest time in the 5-minutes intervals, with one hour later is the default ending time.

The details of those optional fields are discussed in the following. In contact person, user can choose the contact person of that event either from the contact list of the phone, or enter a name( maximum 20 characters). Besides, if private event is selected as true, then the exported time table will not show the event. Otherwise, it will be shown if private event is not selected (selected as false).


---


**3. Synchronization with Google Calendar and Exporting**

Some users are using other online calendars or timetable,including google calendar.  DailyAssistant provides the function of synchronizing the online google calendar with the one stored in the phone. To achieve this, a google account is required  and the google calendar has to be already started. At the first time this function is used, the user has to enter his/her google account id and password to create a linkage.

The user has to click to synchronize the calendar under Internet connection. Hence, the information stored in the two calendars will be  synchronized. When the user edit his/her schedule online in google calendar, the change will be updated in the calendar of DailyAssistant. Similarly, when the schedule in our phone app is edited, the change can be seen in the user’s google calendar also.

Users can select the number of days before and after the current day that they want to synchronize. The choices include: 7 days, 30 days, 60 days, 90 days, 180 days and 365 days, for both past day and future day selection.

For exporting timetable, user can export his/her timetable in an image format(.jpg). When the user press “ Export”, the time table of that week will be export to the photo folder in “.jpg” format.


---


**4. Timetable Sharing Social Interaction System**

The basic function of the timetable management is using local machine (android phone) to manipulate timetable data, which does not require Internet connection. Therefore, this kind of timetable management is using local private data only; no one can assess this data if the user does not permit.

Apart from those basic time management function, an advanced function of the app is provided, which is featured as a social interaction system, requiring Internet connection. It allows users to share their time table to their friends via Internet.  As the system requires account to access the server, users can use Facebook account to log in. If the user does not have an existing account, there is a button to create an account, which will refer to the page of Facebook registration.

When a user wants to share his timetable to others, he can press the share button after checking some checkboxes in the list of the user, which is same as that of Facebook, to choose who can browse his/her timetable. All events are supposed to be previously set to either private or able to share.

If friend's checkbox is selected, then user can pick the friend to open permission in viewing his/her time table. And all of the people in user’s contact list will able to view his/her time table if to all friends' checked is checked.

After pressing the share button, the records of authorized users’ permission are saved into system and the shared timetable is updated in a remote server and other users can only browse the latest version of timetable, but not the deleted previous one.

Another button is a download button. It is to get information of other users from the server. It is supposed that the user has already gotten the permission. When download  button is pressed with some checked checkbox in friend list, timetable information is got and saved into user system and would show up after pressing download button. User can click on the friend's name to load and view saved timetables.


---


**5. Finding Common Free Time**

More than that, there is a feature in DailyAssistant that user can find out the common available time slots among friends in a certain period as the schedule is likely to have changes if it is too far way from today. User can select a group of people, whose timetables are already downloaded, to list out all common available time slots, in the format of “day/month starting time-ending time”, such as 29/2 8:00-10:00”. The result is in chronological order.

Additionally, there is a filter function to set constrains on the resulting list. Different option is provided to facilitate the function, including filter out which day of the week, or time period, which shown in a list and user can cross out those he want to filter out.

e.g.
Time period that can be filter out include:

0:00-06:00

06:00-12:00

12:00-18:00

18:00-0:00

For instant, user can get the result not showing time slots on Sunday. The available time slots that have all people available in the selected group can also be shown.  When there is no common time slot, there would be an empty list. Whenever an empty list appears, there is another way to for the user to re-select the group of people such that user can select specific group of friends by clicking their name in the list.


---


**6. To-Do List**

To-Do list page shows the list of tasks. User can set and update their progress in percentage in order to keep track of the progress for completing that task. When adding a task/event into the list, user has to enter the title with limit of 30 characters and the default setting of the progress is 0%, while the title and progress field are required. User can also set optional information including location(maximum 100 characters) and deadline time (date-time format) for a task.

After a task is created, user can change the progress from 0%-100% (integer only) at any time  to indicate the percentage of completion.User can click an indicator, which is a tick, to indicate a particular event/task has already been finished.

If there is a deadline for a task, the user can enable the alarm reminder and set how long before the deadline to have alert when adding a new task. Options include 5 minutes, 30 minutes, 1 hour, 2 hours, 6 hours and 1 day before the deadline. The task is said to be not complete if the progress is not 100% when it reach the deadline.User should set the event to be finished state by setting the progress to 100%. If the task does not have a deadline, there is no alarm setting.

The tasks with deadline in To-Do list can also be imported into the timetable, regardless of times of updates. When the To-Do tasks are imported into timetable, the title shown on the timetable is the same as the title in To-Do list by default. The timetable will show that there is a deadline in that certain time. The deadline of To-Do tasks can be overlapped with normal events. This information is not sharable in order to provide privacy.


---


**7. Hindering Access to Specific Website**

During some event, such as having lectures,  holding a meeting and studying period before exams, the user may hope to restrict him or herself from accessing some website of entertainment, such as Facebook or Twitter. DailyAssistant provides a function to remind users not to browse these websites. Users can set a time period and choose the websites they do not want to browse during that time period. During that period, if the user access those websites, a message box reminder will pop out everytime the page refresh.


---


**8. Linkage between Photos Taken and Events**

When user take photos during an event arranged in timetable, they can choose to link up those photos taken with the event. In DailyAssistant, users are able to view a page with all photos taken during a particular event. These photo pages could be accessed by viewing detail of events, which is just click on that event in the calendar.


---


**9. Location-based Alarm**

People sometimes forget a meeting or failed to estimate travelling time. To prevent missing an appointment or being late, DailyAssistant provides location-based alarm function. If an event is set to require an alarm reminder, this function will be applied.

If the user inputted the location of an event (by selecting existing location) when he/she added the event into schedule, the assistant will calculate the required traveling time from the previous event location to the next event location. In order to enter the location, user can type in the keywords of the venue and the search result of the location will be shown. User should select the right one from the result list.

Using the GPS function of smart phones, DailyAssistant can get the current location of the user. The travelling time between current location and the next event location can be calculated and updated from time to time. A departure message will pop up to alert the user when the current time plus the travelling time equals to the starting time of the event.



---
