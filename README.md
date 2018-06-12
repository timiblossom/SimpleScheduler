# Server
   Server is a GRPC server that would accept a GRPC client's request to schedule a corresponding job for that client.
   Server would run client jobs in a round-robin scheduling and each client job would have a job slice of 4s each turn.
   Each client needs to peridically send a ping (in our case, the same original request) to the server to maintain its 
   liveness or otherwise, the server would decommission a client job after a time-out (30s by default) if there is no 
   client's heart-beat.

# Client
   Client is a GRPC client that only have an API (sendRequest()).  The sendRequest() would make a GRPC call to the server 
   and send along the client id.  Client can also maintain a keep-alive tcp connection to server through GRPC connection 
   setting.  However, the keep-alive connection alone won't help the server from decommissioning the corresponding client 
   job.  Instead, client needs to send its heartbeats by calling sendRequest() (we should change this API name to a bettter
   one).

#  Build
   This project is using Gradle.  You need to install Gradle according to its instruction for your environment (OSX, Linux or Windows).
   To build and test, run: 

      $ gradle build

   Also, you can open this project in an IDE such as IntelliJ or Eclipse and run unit test from there.

