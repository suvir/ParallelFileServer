# Parallel File Server

* This repository contains a Java implementation of a server-client system for transferring files.
* Files are sent from server to client.
* To maximize throughput, file is transferred concurrently over multiple connections.

### How to run

* ./run-server.sh
* ./run-client.sh
* Check output directory after client completes. It should have a copy of the file identical to that in input directory.
**Sample shell output from server**
```$xslt
± % ./run-server.sh                                                                                              !10522
Server started.
Accepted connection : Socket[addr=/127.0.0.1,port=54400,localport=4444]
Accepted connection : Socket[addr=/127.0.0.1,port=54401,localport=4444]
Accepted connection : Socket[addr=/127.0.0.1,port=54402,localport=4444]
Attempt to send file of length: 35,slice:11/3
Attempt to send file of length: 35,slice:01/3
Attempt to send file of length: 35,slice:21/3
Start byte:11
Start byte:0
End byte:11
Start byte:23
End byte:35
Start byte:23
Start byte:0
File:file and slice:2 sent to client.
End byte:23
Start byte:11
File:file and slice:1 sent to client.
File:file and slice:0 sent to client.
```

**Sample shell output from client**
````$xslt
± % ./run-client.sh                                                                                              !10520
Attempting new connection.
Attempting new connection.
Attempting new connection.
Waiting 5 seconds before starting merge.
File file received from client.
File file received from client.
File file received from client.
Starting file merge
Trying to merge:/Users/suvirj/Downloads/fileserver/output/file_0
Trying to merge:/Users/suvirj/Downloads/fileserver/output/file_1
Trying to merge:/Users/suvirj/Downloads/fileserver/output/file_2
````

### Implementation details

The system works as follows:

* To achieve parallel connections, both server and client are multi-threaded.
* Once server comes up, it waits for a client to connect.
* The client kicks off parallel threads (configurable) to connect with server.
* The server receives each client request and responds with a slice of the file.
* After receiving all the slices of a file, client merges the files into a single file. 

### Directory structure

* **./input**  Directory containing file to be sent by server
* **./output** Directory containing file to be received by server
* **./src/main**    All the source code
* **fileclient.jar and fileserver.jar** JAR files for server and client 
* **run-server.sh and run-client.sh** Shell scrips to run server and client
 
### To-Dos and Improvements

With some more time, I would like to improve some aspects of the project:
* Number of concurrent tasks is hardcoded across server and client. Decouples these values and make the system 
robust to client/server supporting different values. Some sort of handshake protocol may be worth exploring. 
* The file transmission has only been tested with text files. Add handling for more file types.
* Test with client and server running on different physical machines. Handle any errors noticed.
* Unit tests for all classes. 