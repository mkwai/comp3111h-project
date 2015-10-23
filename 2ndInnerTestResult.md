# 2nd Inner Test Result (in virtual device) #

## To-do List ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |
|4              |yes, message box popped out| passed        |
|5              |  yes       | passed        |
|6              | cannot be tested in virtual device| NA            |
|7              |  yes       | passed        |
|8              | yes        |passed         |
|9              | yes        |passed         |
|10             |yes         |passed         |


## Hindering the Access of Specific Websites ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |it works but then it redirect to m.yahoo.com in the device automatically immdiately| passed since no system problem|
|2              |yes         | passed        |
|3              | yes        | passed        |


## Linkage between Photos Taken and Events ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |
|4              |yes         | passed        |


## Add/Edit Event ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |


## Export timetable ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |


## Synchronization with Google Calendar ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              | yes        | passed        |
|2              | yes        | passed        |
|2              | yes        | passed        |


## Timetable Sharing Social Interaction System ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              | yes        |passed         |


## Filtering Function of Finding Common Time ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |






---


# 2nd Inner Test Result (in real device) #

## To-do List ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |
|4              |yes, message box popped out| passed        |
|5              | no, the deadline date and time set to the default setting no matter what user set| failed, fixed |
|6              | yes, there are alarm reminder, but it cannot be stopped| failed, fixed |
|7              | no, deadline time cannot be changed | failed, fixed |
|8              | yes        |passed         |
|9              | yes        |passed         |
|10             |yes         |passed         |


## Hindering the Access of Specific Websites ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |it works but then it redirect to m.yahoo.com in the device automatically immdiately| passed since no system problem|
|2              |yes         | passed        |
|3              | yes        | passed        |


## Linkage between Photos Taken and Events ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |
|4              |yes         | passed        |


## Add/Edit Event ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |


## Export timetable ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |


## Synchronization with Google Calendar ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              | no,cannot login| failed, fixing|
|2              | cannot be tested due to failure to login| unknown       |
|2              | cannot be tested due to failure to login| unknown       |


## Timetable Sharing Social Interaction System ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              | yes        |passed         |


## Filtering Function of Finding Common Time ##
| **test case** | **result** | **condition** |
|:--------------|:-----------|:--------------|
|1              |yes         | passed        |
|2              |yes         | passed        |
|3              | yes        | passed        |


---

# Conclusion #
All test cases in virtual device are passed. The majority test cases in real device are passed except the synchronization section and to-do list section. They passed in virtual device but failed in real device. The problem of to-do list has already been fixed. The problem of failing to login in real device is still fixing.