# Demo

##Interview assignment project.

Spring Boot Rest application.

Java version: 1.8
Maven version: 3.3.9
Spring Boot version: 2.0.1.RELEASE

## Installation:

1. Clone or Download project.
2. Build project using Maven: Execute "mvn install" in command line when inside Typeqast directory.
3. Copy created "fat" jar from target directory (Typeqast/target/) named "electra-0.0.1-SNAPSHOT-exec.jar" to desired location where you want to run it.
4. Run application by executing "java -jar electra-0.0.1-SNAPSHOT-exec.jar" in command line.

## Usage:

To test application you need a Rest client (I use RESTClient Firefox extension).
You must add header "Content-Type: application/json" for application to accept your RequestBody because exchange format is JSON.

#### Exposed endpoints are:
-------
###### 1. Insert ratios

http://<IP>:8080/ratios/insert (Method POST)

Example: http://localhost:8080/ratios/insert

Body: [{"month":"JAN","profile":"A","ratio":0.09},{"month":"FEB","profile":"A","ratio":0.09},{"month":"MAR","profile":"A","ratio":0.09},{"month":"APR","profile":"A","ratio":0.09},{"month":"MAY","profile":"A","ratio":0.09},{"month":"JUN","profile":"A","ratio":0.09},{"month":"JUL","profile":"A","ratio":0.09},{"month":"AUG","profile":"A","ratio":0.09},{"month":"SEP","profile":"A","ratio":0.09},{"month":"OCT","profile":"A","ratio":0.09},{"month":"NOV","profile":"A","ratio":0.05},{"month":"DEC","profile":"A","ratio":0.05}]

Note: Example body is for single profile in list, you can add ratios for additional profiles in the same list.
-------
###### 2. Get all ratios

http://<IP>:8080/ratios/getall (Method GET)

Example: http://localhost:8080/ratios/getall
-------
###### 3. Get ratios by profile

http://<IP>:8080/ratios/getbyprofile/{profile} (Method GET) 

Example: http://localhost:8080/ratios/getbyprofile/A
-------
###### 4. Delete ratios by profile

http://<IP>:8080/ratios/deletebyprofile/{profile} (Method DELETE) 

Example: http://localhost:8080/ratios/deletebyprofile/A
-------
###### 5. Delete all ratios

http://<IP>:8080/ratios/deleteall (Method DELETE) 

Example: http://localhost:8080/ratios/deleteall
-------
###### 6. Insert readings for year

http://<IP>:8080/readings/insert/{year} (Method POST) 

Example: http://localhost:8080/readings/insert/2018

Body: [{"connectionId":"0001","profile":"A","month":"JAN","reading":9},{"connectionId":"0001","profile":"A","month":"FEB","reading":18},{"connectionId":"0001","profile":"A","month":"MAR","reading":27},{"connectionId":"0001","profile":"A","month":"APR","reading":36},{"connectionId":"0001","profile":"A","month":"MAY","reading":45},{"connectionId":"0001","profile":"A","month":"JUN","reading":54},{"connectionId":"0001","profile":"A","month":"JUL","reading":63},{"connectionId":"0001","profile":"A","month":"AUG","reading":72},{"connectionId":"0001","profile":"A","month":"SEP","reading":81},{"connectionId":"0001","profile":"A","month":"OCT","reading":90},{"connectionId":"0001","profile":"A","month":"NOV","reading":95},{"connectionId":"0001","profile":"A","month":"DEC","reading":100}]

Note: Example body is for single connection in list, you can add readings for additional connections in the same list.
-------
###### 7. Get all readings

http://<IP>:8080/readings/getall (Method GET) 

Example: http://localhost:8080/readings/getall
-------
###### 8. Get readings by connection

http://<IP>:8080/readings/getbyconn/{connectionId} (Method GET) 

Example: http://localhost:8080/readings/getbyconn/0001
-------
###### 9. Get single reading for connection year and month

http://<IP>:8080/readings/getone/{connectionId}/{year}/{month} (Method GET) 

Example: http://localhost:8080/readings/getbyconn/0001/2018/JAN
-------
###### 10. Delete readings by connection

http://<IP>:8080/readings/deletebyconn/{connectionId} (Method DELETE) 

Example: http://localhost:8080/readings/deletebyconn/0001
-------
###### 11. Delete all readings

http://<IP>:8080/readings/deleteall (Method DELETE) 

Example: http://localhost:8080/readings/deleteall
