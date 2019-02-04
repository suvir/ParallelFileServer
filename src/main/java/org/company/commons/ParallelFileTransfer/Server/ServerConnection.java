/*
 * Copyright 2019 Company, Inc. or its affiliates. All Rights Reserved.
 */
package org.company.commons.ParallelFileTransfer.Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A task that can send a file to a client.
 * Files are sent in slices.
 */
public final class ServerConnection implements Runnable {

    /**
     * File to be sent to client.
     */
    private File file;

    /**
     * Socket for sending file to client.
     */
    private Socket socket;

    /**
     * Current slice of file to be sent to client.
     */
    private int currentSlice;

    /**
     * Total slices of file as specified by parent runner.
     */
    private int totalParts;

    /**
     * Reader for buffering data over socket.
     */
    private BufferedReader in;

    /**
     * Array of bytes for holding slice of file being manipulated.
     */
    private byte[] bytes;

    public ServerConnection(Socket socket, String filename, int currentSlice, int totalParts) {
        this.socket = socket;
        this.file = new File(filename);
        this.currentSlice = currentSlice;
        this.totalParts = totalParts;

        bytes = new byte[(int) file.length()];
    }

    /**
     * Start thread to send slice of file to client.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendFile();
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends slice of file to client.
     */
    private void sendFile() {
        try {
            System.out.println("Attempt to send file of length: "
                + file.length() + ",slice:" + currentSlice + 1 + "/" + totalParts);

            readBytesFromFile();
            writeToSocket();

            System.out.println("File:" + file.getName() + " and slice:" + currentSlice + " sent to client.");
        } catch (FileNotFoundException fnfe) {
            System.err.println("File does not exist!" + fnfe);
        } catch (IOException ioe) {
            System.err.println("Error writing to socket!" + ioe);
        }
    }

    /**
     * Reads a slice of file as bytes into byte array.
     *
     * @throws IOException If file cannot be found.
     */
    private void readBytesFromFile() throws IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);
        dis.readFully(bytes, 0, bytes.length);
    }

    /**
     * Writes bytes back to client over socket.
     * Also, sends filename and filesize to client.
     *
     * @throws IOException If there is error communicating with client over socket.
     */
    private void writeToSocket() throws IOException {
        OutputStream os = socket.getOutputStream();

        //Send file name and file size to the server
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeUTF(file.getName());
        dos.writeLong(bytes.length);
        dos.write(bytes, getOffset(), getLength());
        dos.flush();
    }

    /**
     * Calculate the offset for writing to byte array.
     *
     * @return Offset in bytes for writing to byte array.
     */
    private int getOffset() {
        int offset = (int) (file.length() * (currentSlice / (float) totalParts));
        System.out.println("Start byte:" + offset);
        return offset;
    }

    /**
     * Calculate the length of total bytes to be written to byte array.
     *
     * @return Length in bytes to be written to byte array.
     */
    private int getLength() {
        int lastByte = (int) (file.length() * ((currentSlice + 1) / (float) totalParts));
        System.out.println("End byte:" + lastByte);
        return lastByte - getOffset();
    }
}