/*
 * Copyright 2019 Company, Inc. or its affiliates. All Rights Reserved.
 */
package org.company.commons.ParallelFileTransfer.Client;

import java.io.File;
import java.net.Socket;

/**
 * This class runs a client to the parallel file server.
 * Every new connection is delegated to a new thread running {@link ClientConnection} task.
 */
public final class FileClient {

    private static final int NUM_TASKS = 3;
    private static final int PORT = 4444;
    private static final String OUTPUT_DIR = "output/";
    private static final String OUTPUT_FILE_NAME = "transferred";
    private static final String HOST = "localhost";
    private static final int SLEEP_DELAY_IN_MILLIS = 5000;
    private static Socket sock;

    public static void main(String[] args) throws Exception {
        FileJoiner joiner = new FileJoiner();

        emptyOutputDirIfNeeded(new File(OUTPUT_DIR));

        for (int i = 0; i < NUM_TASKS; i++) {
            try {
                sock = new Socket(HOST, PORT);
                System.out.println("Attempting new connection.");
                Thread t = new Thread(new ClientConnection(sock, i, OUTPUT_DIR));
                t.start();
            } catch (Exception e) {
                System.err.println("Something went wrong" + e);
                System.exit(1);
            }
        }

        System.out.println("Waiting 5 seconds before starting merge.");
        // TODO : Figure out a better way to check if threads have completed receiving file.
        // Allow client to receive all the files.
        Thread.sleep(SLEEP_DELAY_IN_MILLIS);

        System.out.println("Starting file merge");

        // Merge files into a single file.
        joiner.merge(OUTPUT_DIR, OUTPUT_FILE_NAME);
    }

    /**
     * Deletes all files from a given directory.
     *
     * @param dir Directory to delete all files from.
     */
    private static void emptyOutputDirIfNeeded(File dir) {
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }
}