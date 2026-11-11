package com.littlehorizons.erp.util;

/**
 * Thrown by controllers when a business rule is broken. The message is shown to the user.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String userMessage) {
        super(userMessage);
    }
}
