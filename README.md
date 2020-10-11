# Introduction
An application, what provides a TV schedule for the next week for the Great Britain

# Running the application
To run the application, execute the following commands:
* Build an application
   
      cd tvschedule
      mvn package
* Run an application

      java -jar target/tv-schedule-${version}.jar server tv-schedule.yml

e.g. 
    
      java -jar target/tv-schedule-1.0.jar server tv-schedule.yml
        
* To try an application, execute in your browser:

      http://localhost:8080/program
    
# API description
### Entities description
Application operates with `Program` entity, which contains the following fields:
* id - id of a program. Assigned by the application and will be absent in (and only in) program creation request
* name - name of a program (show). Cannot be longer than 100 characters. Mandatory
* channel - name of a channel, where program is aired. Cannot be longer than 100 characters. Mandatory
* date - air date of a program in a 'yyyy-MM-dd' format. Will always be on the next week. Mandatory
* startTime - air time of a program in a 'HH:mm' format, in 'Europe/London' time zone. Mandatory
* endTime - end date and time of a program in a 'yyyy-MM-dd HH:mm' format, in 'Europe/London' time zone. 
Date of the ending could differ from the start date. Mandatory.

Example:

    {
      "id" : 1, 
      "name" : "The most interesting show",
      "channel" : "BBC Once",
      "date" : "2020-10-12",
      "startTime" : "23:30",
      "endTime" : "2020-10-13 00:35"
    }

## Endpoints description
#### Get next week schedule request:
     
      GET /program?date=yyyy-MM-dd&keywords=keywords HTTP/1.1
* where 
  * date - date on the next week, in 'yyyy-MM-dd' format. Optional
  * keywords - words, which should be in program name. Will return only these shows, which names contain all key words, 
  in any order. Optional 
* Responses:
  * `400 Bad Request` if date is in wrong format
  * `422 Unprocessable Entity` if date is not on the next week
  * `200 OK Content-Type: application/json` response with programs list. Example:
  
    
        GET http://localhost:8080/program?date=2020-10-13&keywords=least
        
        HTTP/1.1 200 OK
        Content-Type: application/json   
        [{
          "id" : 112, 
          "name" : "The least interesting show",
          "channel" : "BBC Once",
          "date" : "2020-10-13",
          "startTime" : "13:30",
          "endTime" : "2020-10-13 13:35"
        },...
        ]     
#### Get exact program request:

       GET /program/{program id} HTTP/1.1
* where
  * program id - id of the program
* Responses:
  * `404 Not Found` if there is no program with the given id
  * `200 OK Content-Type: application/json` response with program. Example:
    
    
        GET http://localhost:8080/program/112
        
        HTTP/1.1 200 OK
        Content-Type: application/json
        {
          "id" : 112, 
          "name" : "The least interesting show",
          "channel" : "BBC Once",
          "date" : "2020-10-13",
          "startTime" : "13:30",
          "endTime" : "2020-10-13 13:35"
        } 
      
#### Add new program to the schedule
    POST /program HTTP/1.1
    Content-Type: application/json
    
    {
      "name": program name,
      "channel": channel name,
      "date": air date,
      "startTime": air time,
      "endTime": end time
    }      
**Should NOT contain program id**
* Responses:
  * `400 Bad Request` if json is corrupted and could not be deseriallized
  * `422 Unprocessable Entity` if some of the fields do not meet requirements
  * `201 Created Content-Type: application/json` response with added program. Example:

        POST http://localhost:8080/program HTTP/1.1
        Content-Type: application/json       
        {
          "name": "new show",
          "channel": "New channel",
          "date": "2020-10-17",
          "startTime": "22:00",
          "endTime": "2020-10-17 23:10"
        }
        
        HTTP/1.1 201 Created
        Location: http://localhost:8080/program/118
        Content-Type: application/json        
        {
          "id": 118,
          "name": "new show",
          "channel": "New channel",
          "date": "2020-10-17",
          "startTime": "22:00",
          "endTime": "2020-10-17 23:10"
        }

#### Update existed program
    PUT /program HTTP/1.1
    Content-Type: application/json
    
    {
      "id": 118,
      "name": name,
      "channel": channel name,
      "date": air date,
      "startTime": air time,
      "endTime": end time
    }      
**Should contain program id**
* Responses:
  * `400 Bad Request` if json is corrupted and could not be deseriallized
  * `422 Unprocessable Entity` if some of the fields do not meet requirements
  * `404 Not Found` if there is no program with such id
  * `200 OK Content-Type: application/json` response with updated program. Example:

        PUT http://localhost:8080/program HTTP/1.1
        Content-Type: application/json       
        {
          "id": 118,
          "name": "other show name",
          "channel": "New channel",
          "date": "2020-10-17",
          "startTime": "21:00",
          "endTime": "2020-10-17 22:10"
        }
        
        HTTP/1.1 200 OK
        Content-Type: application/json
        
        {
          "id": 118,
          "name": "other show name",
          "channel": "New channel",
          "date": "2020-10-17",
          "startTime": "21:00",
          "endTime": "2020-10-17 22:10"
        }

#### Remove program

    DELETE /program/{program id} HTTP/1.1
* where
  * program id - id of the program
* Responses:
  * `404 Not Found` if there is no program with such id
  * `204 No Content` if program was successfully deleted. Example:
  
        DELETE http://localhost:8080/program/110
        
        HTTP/1.1 204 No Content
        
# Tech notes:
* Some of the programs in TV maze response do not contain information about channel, where program is aired. 
Currently these shows are being skipped, but this behaviour is a subject for further improvements
* Since this application is intended to be schedule for the Great Britain, time zones are not considered here:
  * TV Maze returns date and time info in default time zone for the given country ('Europe/London' in case of GB)
  * All times in requests are considered to belong to the same time zone as the schedule
