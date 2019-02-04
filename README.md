# Parallel File Server

* This repository contains a Java implementation of a server-client system for transferring files.
* Files are sent from server to client.
* To maximize throughput, file is transferred concurrently over multiple connections.

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

### To-Dos and Improvements

With some more time, I would like to improve some aspects of the project:
* Number of concurrent tasks is hardcoded across server and client. Decouples these values and make the system 
robust to client/server supporting different values. Some sort of handshake protocol may be worth exploring. 
* The file transmission has only been tested with text files. Add handling for more file types.
* Test with client and server running on different physical machines. Handle any errors noticed.
* Unit tests for all classes. 