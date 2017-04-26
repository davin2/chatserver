# Basic Chat Server

This project implements a basic chat server with the following features:

* Send message from one user to another user
* Add a user as a friend
* Remove a user as a friend
* Get list of friends and their current status

## Design and Implementation details

Please note that the current implementation uses In-Memory versions of DB and Queue. It uses Java Client and Server Sockets to establish connection
between client and server. Client and servers are command line java programs which interact with each other sending pipe separated string commands.

Conceptual design flow:

1. When a Server is launched, it connects to a persistent Distributed Queue System such as Kafka and creates a new Queue which it monitors
for any incoming messages.
2. Clients connect/login to Servers over HTTPS/WebSockets using JSON or XML format.
3. Server authenticates the client and establishes 2-way connection
4. Server stores the user session information in a persistent DB such as Cassandra. This contains information about where
a particular user is connected in the form of Queue Name or ID on the Distributed Queue System.
5. When a user (source) sends a message to another user (destination), the destination may or may not be logged on the same machine.
If not co-located, Server (where source is connected) looks up this session info for destination in the DB and pushes the message into destination Queue.
6. When the Queue delivers the message to destination server, it sends the message to destination user if it is connected.
7. If a server dies, clients should re-connect to another server and the undelivered messages on the old server's queue can be re-played to queues of new servers where clients are re-connected.

This design enables servers to scale independently and not depend upon the physical location or IP of other servers.
Also the Distributed Queue System can scale independently based on load.



## TO DO

1. Add Log4J instead of system outs and print stack trace
2. Handle exceptions better
3. Use system properties or config file to externalize all configurations
4. Add unit tests
5. Add clean shutdowns
6. Use service discovery for db/queue and use circuit breakers to avoid cascading failures
7. Add a build system

## Limitations and Assumptions

1. Relogin does not check if already logged in
2. Add friend does not check if user exists
3. Does not check before sending a message if user is in friends list
4. Does not check while removing if friend exists or is in friends list
5. Client does not exit when server dies - does not reconnect
6. Supports a max of 20 clients at a time. This is due to the fact that we are using blocking sockets and thread pool is bound to 20 threads. Ideally we should use NIO based servers such as Netty/Play.
7. Current version assumes that client and server are running on localhost. However it can be changed to run clients on different machine with a simple change of config.
8. Authentication assumes all users are valid.
9. There is no user registration process.
10. Does not handle same user logging in from multiple places. This can be easily supported by keeping multiple sessions for the user and sending the message to all the sessions.
11. Does not send message if the destination user is offline. This can be achieved by fetching undelivered messages from DB when the user logs in.
12. Only online and offline status values are handled but this can be extended to other states such as idle/away, typing, or some custom status.
13. Some caching may be used to improve performance instead of looking up user session from DB every time.



## How to run

1. Download the project from github
2. cd to the chatserver dir
3. compile by running javac $(find . -name "*.java")
4. cd to src
5. run server - java com.chatserver.server.ChatServer 5678
6. open another terminal and run client - java com.chatserver.client.ChatClient 5678
7. Use the below commands to login, logout, send message etc.

 * Login - Login|<userId> example: Login|user1
 * Logout - Logout example: Logout
 * Send - Send|<userId>|<message> example: Send|user2|hello there
 * AddFriend - AddFriend|<userId> example: AddFriend|user2
 * RemoveFriend - RemoveFriend|<userId> example: RemoveFriend|user2
 * GetFriends - GetFriends example: GetFriends (returns comma separated list of userId:status)

