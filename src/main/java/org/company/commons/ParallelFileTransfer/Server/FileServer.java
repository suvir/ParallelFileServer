/*
 * Copyright 2019 Company, Inc. or its affiliates. All Rights Reserved.
 */
package org.company.commons.ParallelFileTransfer.Server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class runs the parallel file server.
 * Every new connection is delegated to a new thread running {@link ServerConnection} task.
 */
public final class FileServer {

    private static final int PORT = 4444;
    private static final String INPUT_DIRECTORY = "input/";
    private static final String INPUT_FILE = "file";
    private static final int TOTAL_SLICES = 3;

    private static ServerSocket serverSocket;
    private static Socket s;
    private static int current_slice = 0;

    public static void main(String[] args) {

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started.");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }

        while (true) {
            try {
                s = serverSocket.accept();
                System.out.println("Accepted connection : " + s);
                Thread t = new Thread(
                    new ServerConnection(s, INPUT_DIRECTORY + INPUT_FILE, current_slice, TOTAL_SLICES));
                t.start();
            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            } finally {
                current_slice += 1;

                //Allows client to request a file again.
                if (current_slice == TOTAL_SLICES) {
                    current_slice = 0;
                }
            }
        }
    }
}