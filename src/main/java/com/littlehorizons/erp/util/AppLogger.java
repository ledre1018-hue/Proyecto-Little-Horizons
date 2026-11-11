package com.littlehorizons.erp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton that appends log lines to error_log.txt.
 * Format: [date time] [LEVEL] [source] message (+ stack trace for errors).
 */
public final class AppLogger {

    public static final String LOG_FILE_NAME = "error_log.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static AppLogger instance;

    private final File logFile;

    private AppLogger() {
        this.logFile = new File(LOG_FILE_NAME);
    }

    public static synchronized AppLogger getInstance() {
        if (instance == null) {
            instance = new AppLogger();
        }
        return instance;
    }

    public void info(String source, String message) {
        write(LogLevel.INFO, source, message, null);
    }

    public void warning(String source, String message) {
        write(LogLevel.WARNING, source, message, null);
    }

    public void error(String source, String message, Throwable error) {
        write(LogLevel.ERROR, source, message, error);
    }

    public File getLogFile() {
        return logFile;
    }

    private synchronized void write(LogLevel level, String source, String message, Throwable error) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write("[" + timestamp + "] [" + level + "] [" + source + "] " + message);
            writer.newLine();
            if (error != null) {
                writer.write(stackTraceOf(error));
            }
        } catch (IOException e) {
            // The program must keep running even if the log cannot be written
            System.err.println("Aviso: no fue posible escribir en " + LOG_FILE_NAME + ".");
        }
    }

    private String stackTraceOf(Throwable error) {
        StringWriter text = new StringWriter();
        try (PrintWriter printer = new PrintWriter(text)) {
            error.printStackTrace(printer);
        }
        return text.toString();
    }
}
