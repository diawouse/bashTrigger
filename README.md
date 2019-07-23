# bashTrigger

## Description

This project is a batch Trigger java project. It reads a Unix command from file passed in parameter, and sends a mail if the command is not successful. This Trigger will allow us to realize several scenarios, to name a few:
* Testing connection
* Testing service running
* Launching a test probe
* Launching a test by another script

In this first implementation, we expect the following functions from the Trigger:
* Trigger reads commands from a file passed in parameter
* Trigger launch command every x secondes configured in the file
* If the command fails, sends mails using EmailNotification service

bashTrigger is packed as a simple JAR bashTrigger-<version>-app.jar file including all dependencies.

## Steps

### clone and build bashTrigger projet

clone project
* into the project directory rum `mvn clean install`
* into the project directory go to folder target and verify that bashTrigger-<version>-app.jar file is present

### Start bashTrigger (here example for version 0.0.1-SNAPSHOT)

Into the project directory/target execute following command : 
```
nohup sudo java -jar bashTrigger-0.0.1-SNAPSHOT-app.jar test.sh http://localhost:8080/api/notification/v1/users/ data.json 4000 &  
```

Where: 
- test.sh : file containing shell commands to excute
  ```
     ping -c 1 192.168.107.4
     ping -c 1 192.168.110.146
     nc -z -v 172.16.200.10 80
     sudo ./script.sh
  ```
  
- http://localhost:8080/api/notification/v1/users/ : Rest API url to send mail

- data.json : Json data file containing full details of recipient users
   ```
		    { 
		   "users":[ 
		      { 
		         "firstName":"Your first name1",
		         "lastName":"Your last name1",
		         "emailAddress":"username@domain.com"
		      },
		      { 
		         "firstName":"Your first name1",
		         "lastName":"Your last name2",
		         "emailAddress":"username@domain.com"
		      }
		   ]
		}
   ```
   
- 4000 : The number of seconds between two executions of the scripts

### Log file

This project is configured with log4j. The log files are located in /var/log/bashTrigger.log