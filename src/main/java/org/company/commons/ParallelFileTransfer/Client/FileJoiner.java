/*
 * Copyright 2019 Company, Inc. or its affiliates. All Rights Reserved.
 */
package org.company.commons.ParallelFileTransfer.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to join list of files into a single output file.
 */
public final class FileJoiner {

    /**
     * Join list of files into a single output file.
     *
     * @param directoryWithFiles Directory containing files to be joined.
     * @param outputFileName Name of file expected to contain the result.
     *
     * @throws Exception In case there are problems with joining the file.
     */
    public void merge(String directoryWithFiles, String outputFileName) throws Exception {
        File dir = new File(directoryWithFiles);
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Expected a directory as first argument");
        }

        File[] fileList = dir.listFiles();
        int totalFiles = fileList.length;
        Map<Integer, File> orderedFileMap = new HashMap<>();

        // Extract into a map. Later use this for ordering files in correct order.
        for (File f : fileList) {
            orderedFileMap.put(Integer.valueOf(f.getName().split("_")[1]), f);
        }

        // Initialize the output file
        PrintWriter writer = new PrintWriter(directoryWithFiles + outputFileName);

        // Traverse files in order of key in map
        BufferedReader reader;
        File curFile;
        String curLine;
        for (int i = 0; i < totalFiles; i++) {
            curFile = orderedFileMap.get(i);
            System.out.println("Trying to merge:" + curFile.getCanonicalPath());
            // Copy file contents into destination file
            reader = new BufferedReader(new FileReader(curFile.getCanonicalFile()));
            curLine = reader.readLine();
            while (curLine != null) {

                //TODO: Figure out why some files have empty newline as first line.
                //The length check is a workaround to get around the above TODO.
                if (curLine.length() != 0) {
                    writer.println(curLine);
                }
                curLine = reader.readLine();
            }

            reader.close();

            // Delete the file
            orderedFileMap.get(i).delete();
        }
        writer.flush();
        writer.close();
    }
}
