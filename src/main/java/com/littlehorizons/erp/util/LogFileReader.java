package com.littlehorizons.erp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the last lines of the log file, line by line.
 */
public class LogFileReader {

    private final File logFile;

    public LogFileReader(File logFile) {
        this.logFile = logFile;
    }

    public boolean logExists() {
        return logFile.exists();
    }

    public String getLogPath() {
        return logFile.getAbsolutePath();
    }

    public List<String> readLastLines(int lineCount) throws IOException {
        List<String> lastLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line = reader.readLine();
            while (line != null) {
                lastLines.add(line);
                if (lastLines.size() > lineCount) {
                    lastLines.remove(0);
                }
                line = reader.readLine();
            }
        }
        return lastLines;
    }
}
