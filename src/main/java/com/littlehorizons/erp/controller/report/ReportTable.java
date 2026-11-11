package com.littlehorizons.erp.controller.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a report: a title, the column headers and widths, and the rows as text.
 */
public class ReportTable {

    private final String title;
    private final String[] headers;
    private final int[] widths;
    private final List<String[]> rows;

    public ReportTable(String title, String[] headers, int[] widths) {
        if (headers.length != widths.length) {
            throw new IllegalArgumentException("Headers and widths must have the same size.");
        }
        this.title = title;
        this.headers = headers.clone();
        this.widths = widths.clone();
        this.rows = new ArrayList<>();
    }

    public void addRow(String... cells) {
        rows.add(cells);
    }

    public String getTitle() {
        return title;
    }

    public String[] getHeaders() {
        return headers.clone();
    }

    public int[] getWidths() {
        return widths.clone();
    }

    public List<String[]> getRows() {
        return new ArrayList<>(rows);
    }
}
