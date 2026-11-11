package com.littlehorizons.erp.util;

/**
 * Converts one item into the text cells of a console table row.
 */
@FunctionalInterface
public interface TableRowFormatter<T> {

    String[] toRow(T item);
}
