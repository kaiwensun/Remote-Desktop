# Remote-Desktop

A remote computer GUI controller based on Java Robot.

## Progress Log

##### 2016-08-02:
* Project starts.
* Show live view of remote computer.
* Remote mouse actions (motion, click, wheel, etc.).
* Build skeletons for mouse actions and keyboard actions.

##### 2016-08-03:
* Fix memory leak at server side.
* Fix memory leak at client side. The memory leak was caused not having flush ()and reset() on outStream of postman, so image accumulated at client's postman recv().
* Support keyboard actions (press and release).
* Config json file for server
* Security (AES encrypted user authentication and message encryption)
 
## Todos
* Javadoc

