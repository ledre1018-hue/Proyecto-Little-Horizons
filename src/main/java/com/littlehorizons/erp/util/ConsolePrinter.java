package com.littlehorizons.erp.util;

import java.util.List;

/**
 * Prints titles, aligned tables and status messages on the console.
 */
public class ConsolePrinter {

    private static final int LINE_WIDTH = 78;
    private static final String COLUMN_SEPARATOR = " | ";

    public void printTitle(String title) {
        System.out.println();
        System.out.println("=".repeat(LINE_WIDTH));
        System.out.printf("  %s%n", title.toUpperCase());
        System.out.println("=".repeat(LINE_WIDTH));
    }

    public void printSubtitle(String subtitle) {
        System.out.println();
        System.out.printf("--- %s ---%n", subtitle);
    }

    public void printOption(int number, String text) {
        System.out.printf("  %d. %s%n", number, text);
    }

    public void printLine(String text) {
        System.out.println(text);
    }

    public void printDetail(String label, String value) {
        System.out.printf("  %-24s: %s%n", label, value);
    }

    public void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void printWarning(String message) {
        System.out.println("[AVISO] " + message);
    }

    public void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public <T> void printTable(String[] headers, int[] widths, List<T> items, TableRowFormatter<T> formatter) {
        if (items.isEmpty()) {
            printInfo("No hay registros para mostrar.");
            return;
        }
        String separatorLine = buildSeparator(widths);
        System.out.println(separatorLine);
        System.out.println(buildRow(headers, widths));
        System.out.println(separatorLine);
        for (T item : items) {
            System.out.println(buildRow(formatter.toRow(item), widths));
        }
        System.out.println(separatorLine);
        System.out.printf("  Total de registros: %d%n", items.size());
    }

    private String buildRow(String[] cells, int[] widths) {
        StringBuilder row = new StringBuilder();
        for (int index = 0; index < widths.length; index++) {
            String cell = index < cells.length ? cells[index] : "";
            row.append(String.format("%-" + widths[index] + "s", fit(cell, widths[index])));
            if (index < widths.length - 1) {
                row.append(COLUMN_SEPARATOR);
            }
        }
        return row.toString();
    }

    private String buildSeparator(int[] widths) {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < widths.length; index++) {
            line.append("-".repeat(widths[index]));
            if (index < widths.length - 1) {
                line.append("-+-");
            }
        }
        return line.toString();
    }

    private String fit(String text, int width) {
        String value = text == null ? "" : text;
        if (value.length() <= width) {
            return value;
        }
        return value.substring(0, width - 1) + "~";
    }
}
