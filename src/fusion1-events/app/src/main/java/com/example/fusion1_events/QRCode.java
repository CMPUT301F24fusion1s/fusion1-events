package com.example.fusion1_events;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.UUID;

public class QRCode {
    // Inner class to encapsulate the QR code result
    public static class QRCodeResult implements Parcelable {
        private final Bitmap bitmap;
        private final String hash;

        public QRCodeResult(Bitmap bitmap, String hash) {
            this.bitmap = bitmap;
            this.hash = hash;
        }

        protected QRCodeResult(Parcel in) {
            bitmap = in.readParcelable(Bitmap.class.getClassLoader());
            hash = in.readString();
        }

        public static final Creator<QRCodeResult> CREATOR = new Creator<QRCodeResult>() {
            @Override
            public QRCodeResult createFromParcel(Parcel in) {
                return new QRCodeResult(in);
            }

            @Override
            public QRCodeResult[] newArray(int size) {
                return new QRCodeResult[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(bitmap, flags);
            dest.writeString(hash);
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public String getHash() {
            return hash;
        }
    }

    /**
     * Generates a QR code for the given event and returns both the bitmap and hash.
     *
     * @param eventId The ID of the event.
     * @return The generated QR code as a QRCodeResult containing a Bitmap and a hash.
     * @throws WriterException If an error occurs while generating the QR code.
     */
    public static QRCodeResult generateQRCode(UUID eventId) throws WriterException {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

        // Generate the QR code based on the event ID
        BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                eventId.toString(),
                com.google.zxing.BarcodeFormat.QR_CODE,
                200,
                200
        );

        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
        String hash = generateQRCodeHash(eventId.toString());

        return new QRCodeResult(bitmap, hash);
    }

    /**
     * Generates a hash for the QR code based on the event ID.
     *
     * @param eventId The ID of the event.
     * @return A hash string of the event ID.
     */
    static String generateQRCodeHash(String eventId) {
        // Generate the QR code hash based on the event ID
        // This is a simple example, replace with actual hashing logic
        return eventId;
    }
}
