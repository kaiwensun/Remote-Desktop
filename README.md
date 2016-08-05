# Remote-Desktop

A remote computer GUI controller based on Java Robot.
(!structure.png)[structure.png]

## Supported Environment
The application has been tested under Ubuntu 16.04, Windows 7 and Windows 10. But all desktop operating systems supporting Java (awt & swing) should be able to run this system. Server and clients don't have to be the same kind of operating system.
The jar files in [release](./release) are compiled using javac 1.8.0\_65 . You can run them using this or later version of JRE.

## Setup Server (Controllee)
To run the server, in the directory containing _VideoServer.jar_, run command:

~~~~
$ java -jar VideoServer.jar
~~~~

To configure server, in the same directory, edit the fields in _server.json_ in JSON format:

| Field Name	| Value's Type	| Value's Meaning				|
| ------------- | ------------- | ----------------------------- |
| port			| string		| Application's service port	|
| users			| array			| A list of user information. See next table. |
| authenticate	| bool			| Whether to authenticate users. This field should be identical to client side configuration. If false, any client can view and control the server without login.	|

For each element in the array of user information,

| Field Name	| Value's Type	| Value's Meaning				|
| ------------- | ------------- | ----------------------------- |
| username		| string		| User which is allowed to connect to the server. UTF-8 characters	allowed	|
| password		| string		| Pssword of this user			|
| allow action	| bool			| Whether the user is allowed to control the server using mouse and keyboard	|

It is recommended to set `authenticate` as `true` and allow at most one user to control the server's mouse and keyboard, because multiple users are allowed to conect to the server synchronously.

## Setup Client (Controller)

To run the client, in the directory containing _VideoServer.jar_, run command:

~~~~
$ java -jar VideoClient.jar
~~~~

To configure client, in the same directory, edit the fields in _client.json_ in JSON format:

| Field Name	| Value's Type	| Value's Meaning				|
| ------------- | ------------- | ----------------------------- |
| ip			| string		| Server's IP address			|
| port			| int			| Server's service port			|
| frame height	| int			| The height of screen at client side	|
| frame width	| int			| The width of screen at client side	|
| username		| string		| User's name to login server. UTF-8 characters	allowed	|
| password		| string		| User's password to login server. Only ASCII characters allowed.	|
| authenticate	| bool			| Whether to authenticate users. This field should be identical to client side configuration.	|

## Progress Log

##### 2016-08-02 Screen video backhaul and mouse actions:
* Project starts.
* Show live view of remote computer.
* Remote mouse actions (motion, click, wheel, etc.).
* Build skeletons for mouse actions and keyboard actions.
* Config json file for client.

##### 2016-08-03 Keyboard actions and security:
* Fix memory leak at server side.
* Fix memory leak at client side. The memory leak was caused not having flush ()and reset() on outStream of postman, so image accumulated at client's postman recv().
* Support keyboard actions (press and release).
* Config json file for server.
* Security (AES encrypted user authentication and message encryption).
* Reduce server frame rate to reduce server's CPU use.
 
##### 2016-08-04 Optimization and wind up
* Avoid server from capturing screen too frequently when many clients are connected to it. Stop capturing when no clients are connected.
* Javadoc.
* User guide in _README.md_

