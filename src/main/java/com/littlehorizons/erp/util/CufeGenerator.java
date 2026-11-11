package com.littlehorizons.erp.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

/**
 * Simulated CUFE: SHA-384 hash, in hexadecimal, of invoice number + NIT + date + total.
 */
public final class CufeGenerator {

    private static final String ALGORITHM = "SHA-384";

    private CufeGenerator() {
    }

    public static String generate(String invoiceNumber, String nit, LocalDate issueDate, BigDecimal total) {
        String source = invoiceNumber + nit + issueDate + total.setScale(2).toPlainString();
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder();
            for (byte value : hash) {
                hexadecimal.append(String.format("%02x", value));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            AppLogger.getInstance().error("CufeGenerator.generate", "SHA-384 is not available", e);
            throw new IllegalStateException("No fue posible calcular el CUFE.", e);
        }
    }
}
