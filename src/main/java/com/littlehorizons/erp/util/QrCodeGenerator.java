package com.littlehorizons.erp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Creates a QR code as a PNG image using ZXing.
 */
public final class QrCodeGenerator {

    private static final String IMAGE_FORMAT = "PNG";

    private QrCodeGenerator() {
    }

    public static byte[] generatePng(String content, int size) throws WriterException, IOException {
        BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, output);
            return output.toByteArray();
        }
    }
}
