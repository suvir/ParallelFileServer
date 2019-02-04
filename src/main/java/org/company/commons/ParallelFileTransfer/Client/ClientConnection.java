/*
 * Copyright 2019 Company, Inc. or its affiliates. All Rights Reserved.
 */
package org.company.commons.ParallelFileTransfer.Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A task that can received a file from a server.
 * Each instance of this task receives a single slice of the file.
 */
public final class ClientConnection implements Runnable {

    /**
     * Socket for receiving file from server.
     */
    private Socket socket;

    /**
     * Current slice of the file being read.
     */
    private int currentSlice;

    /**
     * Directory to save the file to.
     */
    private String outputDir;

    /**
     * Reader for buffering data over socket.
     */
    private BufferedReader in;

    public ClientConnection(Socket client, int curSlice, String outputDir) {
        this.socket = client;
        this.currentSlice = curSlice;
        this.outputDir = outputDir;
    }

    /**
     * Start thread to receive slice of file from server.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            receiveFile();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Receives slice of file from server.
     */
    private void receiveFile() {
        try {
            DataInputStream clientData = new DataInputStream(socket.getInputStream());
            String fileName = clientData.readUTF();
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];

            int bytesRead;
            OutputStream output = new FileOutputStream((outputDir + fileName + "_" + currentSlice));
            while (size > 0 &&
                  (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            clientData.close();
            System.out.println("File " + fileName + " received from client.");

        } catch (IOException ex) {
            System.err.println("Client error. Connection closed.");
        }
    }
}