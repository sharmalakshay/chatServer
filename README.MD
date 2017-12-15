**Main project is inside udp**

Unable to test UDP chat server on the testing link provided, so created tcp version.

PLEASE CHECK the udp server!

if the script file doesn't work:

javac udp/chat/*.java
javac udp/chat/server/*.java
java udp/chat/server/StartServer 8190			//8190 is the port number

Client can be started on different terminal or machines as:
java udp/chat/Join Lakshay localhost 8190		//localhost being the ip address if creating client on same machines


I HAVE TESTED IT RUNNING on scss nebula.
(I created a debian machine, ran server on it and then ran the client on a scss virtual machine)


NO CLIENT PROGRAM IS DEVELOPED FOR TCP version because I just wanted to check the connection by that.