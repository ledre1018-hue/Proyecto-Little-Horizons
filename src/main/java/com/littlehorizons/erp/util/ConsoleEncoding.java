package com.littlehorizons.erp.util;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Forces UTF-8 on the console so accents and box characters are shown correctly.
 */
public final class ConsoleEncoding {

    private ConsoleEncoding() {
    }

    public static void configureUtf8() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
    }
}
