package com.example.nextturn_project.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class QRCodeGenerator {

    public static Bitmap generateQRCode(String text) {

        try {

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    700,
                    700
            );

            Bitmap bitmap = Bitmap.createBitmap(
                    700,
                    700,
                    Bitmap.Config.RGB_565
            );

            for (int x = 0; x < 700; x++) {

                for (int y = 0; y < 700; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );

                }

            }

            return bitmap;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}