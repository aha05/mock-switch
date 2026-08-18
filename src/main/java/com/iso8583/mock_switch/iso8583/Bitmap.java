package com.iso8583.mock_switch.iso8583;


import java.util.Collection;

public class Bitmap {

    private static final int PRIMARY_BITMAP_BITS = 64;
    private static final int SECONDARY_BITMAP_BITS = 64;

    private final boolean[] bits = new boolean[129];

    public Bitmap() {
    }

    public static Bitmap fromFields(Collection<Integer> fieldNumbers) {

        Bitmap bitmap = new Bitmap();

        for (Integer fieldNumber : fieldNumbers) {

            if (fieldNumber == null) {
                continue;
            }

            bitmap.set(fieldNumber);
        }

        return bitmap;
    }

    public void set(int fieldNumber) {

        validateFieldNumber(fieldNumber);

        bits[fieldNumber] = true;

        // Field 1 indicates that secondary bitmap exists.
        if (fieldNumber > 64) {
            bits[1] = true;
        }
    }

    public void clear(int fieldNumber) {

        validateFieldNumber(fieldNumber);

        bits[fieldNumber] = false;

        if (fieldNumber > 64 && !hasSecondaryFields()) {
            bits[1] = false;
        }
    }

    public boolean isSet(int fieldNumber) {

        validateFieldNumber(fieldNumber);

        return bits[fieldNumber];
    }

    public boolean hasSecondaryFields() {

        for (int i = 65; i <= 128; i++) {

            if (bits[i]) {
                return true;
            }
        }

        return false;
    }

    public byte[] toBytes() {

        boolean secondary = hasSecondaryFields();

        int bitmapByteCount = secondary ? 16 : 8;

        byte[] result = new byte[bitmapByteCount];

        for (int field = 1; field <= (secondary ? 128 : 64); field++) {

            if (!bits[field]) {
                continue;
            }

            int zeroBasedField = field - 1;

            int byteIndex = zeroBasedField / 8;
            int bitIndex = zeroBasedField % 8; // zeroBasedField/8 = result, zeroBasedField - result * 8 = ?

            result[byteIndex] |= // when positon the bit on specific bytes it shouldn't replace, use bitwise or operator on existing bytes and new byte
                    (byte) (1 << (7 - bitIndex));
        }

        return result;
    }

    public static Bitmap fromBytes(byte[] bytes) {

        if (bytes == null || (bytes.length != 8 && bytes.length != 16)) {
            throw new IllegalArgumentException(
                    "Bitmap must contain either 8 or 16 bytes"
            );
        }

        Bitmap bitmap = new Bitmap();

        int maxField = bytes.length == 8 ? 64 : 128;

        for (int field = 1; field <= maxField; field++) {

            int zeroBasedField = field - 1;

            int byteIndex = zeroBasedField / 8;
            int bitIndex = zeroBasedField % 8;

            boolean set =
                    (bytes[byteIndex] & (1 << (7 - bitIndex))) != 0;

            if (set) {
                bitmap.bits[field] = true;
            }
        }

        return bitmap;
    }

    public int[] getSetFields() {

        int count = 0;

        for (int i = 2; i <= 128; i++) {

            if (bits[i]) {
                count++;
            }
        }

        int[] result = new int[count];

        int index = 0;

        for (int i = 2; i <= 128; i++) {

            if (bits[i]) {
                result[index++] = i;
            }
        }

        return result;
    }

    private void validateFieldNumber(int fieldNumber) {

        if (fieldNumber < 1 || fieldNumber > 128) {
            throw new IllegalArgumentException(
                    "ISO field number must be between 1 and 128"
            );
        }
    }

    @Override
    public String toString() {

        byte[] bytes = toBytes();

        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(
                    String.format("%8s",
                                    Integer.toBinaryString(b & 0xFF)) // b & 0xFF (11111111) is to convert from signed bytes => unsigned bytes
                            .replace(' ', '0')
            );
        }

        return builder.toString();
    }
}
