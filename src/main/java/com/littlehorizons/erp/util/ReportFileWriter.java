package com.littlehorizons.erp.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Saves a text report in the reports folder (created next to where the program runs).
 * File name: prefix + date and time, for example rendimiento_materias_20261120_183000.txt.
 */
public class ReportFileWriter {

    public static final String REPORT_FOLDER = "reports";
    private static final String FILE_EXTENSION = ".txt";
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final Path reportFolder;

    public ReportFileWriter() {
        this.reportFolder = Paths.get(REPORT_FOLDER);
    }

    /** Writes the lines in UTF-8 and returns the absolute path of the new file. */
    public Path write(String filePrefix, List<String> lines) throws IOException {
        Files.createDirectories(reportFolder);
        Path file = buildUniquePath(filePrefix);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        return file.toAbsolutePath();
    }

    /** Adds _1, _2... when two reports are generated in the same second. */
    private Path buildUniquePath(String filePrefix) {
        String baseName = filePrefix + "_" + LocalDateTime.now().format(FILE_TIMESTAMP);
        Path file = reportFolder.resolve(baseName + FILE_EXTENSION);
        int copy = 1;
        while (Files.exists(file)) {
            file = reportFolder.resolve(baseName + "_" + copy + FILE_EXTENSION);
            copy++;
        }
        return file;
    }
}
