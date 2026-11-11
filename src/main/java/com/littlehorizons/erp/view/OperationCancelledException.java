package com.littlehorizons.erp.view;

/**
 * Thrown when the user types 0 to cancel an operation and go back to the menu.
 */
public class OperationCancelledException extends RuntimeException {

    public OperationCancelledException() {
        super("Operación cancelada por el usuario.");
    }
}
