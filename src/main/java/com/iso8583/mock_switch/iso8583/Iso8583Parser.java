package com.iso8583.mock_switch.iso8583;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class Iso8583Parser {

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(
                    hex.substring(i, i + 2),
                    16
            );
        }

        return bytes;
    }

    public Iso8583Message parse(byte[] data) {
        if (data == null || data.length < 12) {
            throw new IllegalArgumentException(
                    "Invalid ISO 8583 message"
            );
        }

        int position = 0;

        String mti =
                readAscii(data, position, 4);

        System.out.println("Request MTI: "+mti);

        position += 4;


        byte[] primaryBitmapHex =
                Arrays.copyOfRange(
                        data,
                        position,
                        position + 16
                );


       var primaryHexBytesToString = new String(primaryBitmapHex, StandardCharsets.US_ASCII);
       byte[] primaryBitmapBytes = hexToBytes(primaryHexBytesToString);

        System.out.println(
                "primaryHexBytes: " +
                        primaryHexBytesToString
        );

                System.out.println(
                "RawBytes as bitmap: " +
                        Arrays.toString(primaryBitmapHex)
        );

        position += 16;

        boolean hasSecondaryBitmap =
                (primaryBitmapBytes[0] & 0x80) != 0;

        System.out.println("has secondary: " + hasSecondaryBitmap);

        byte[] bitmapBytes;

        if (hasSecondaryBitmap) {
            byte[] secondaryBitmapHex =
                    Arrays.copyOfRange(
                            data,
                            position,
                            position+16
                    );

            var secondaryHexBytesToString = new String(secondaryBitmapHex, StandardCharsets.US_ASCII);
            byte[] secondaryBitmapBytes = hexToBytes(secondaryHexBytesToString);

            System.out.println(
                    "secondaryHexBytes: " +
                            secondaryHexBytesToString
            );
            position += 16;

            bitmapBytes = new byte[16];

            System.arraycopy(
                    primaryBitmapBytes,
                    0,
                    bitmapBytes,
                    0,
                    8
            );

            System.arraycopy(
                    secondaryBitmapBytes,
                    0,
                    bitmapBytes,
                    8,
                    8
            );

        } else {
            bitmapBytes = primaryBitmapBytes;
        }

        Bitmap bitmap =
                Bitmap.fromBytes(bitmapBytes);

        System.out.println("Request bitmap: " + bitmap);

        Iso8583Message message =
                new Iso8583Message(mti);

        for (int fieldNumber : bitmap.getSetFields()) {
            IsoField field;
            try {
                field = IsoField.fromNumber(fieldNumber);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Unsupported ISO field DE" + fieldNumber, e
                );
            }

            FieldReadResult result =
                    readField(
                            data,
                            position,
                            field
                    );

            System.out.println("DE"+fieldNumber+": " + result.value());

            message.setField(
                    fieldNumber,
                    result.value()
            );

            position = result.nextPosition();
        }

        if (position != data.length) {
            throw new IllegalArgumentException(
                    "Unexpected data after ISO message. "
                            + "Remaining bytes: "
                            + (data.length - position)
            );
        }

        return message;
    }

    private FieldReadResult readField(byte[] data, int position, IsoField field) {
        switch (field.getLengthType()) {

            case FIXED -> {

                int length =
                        field.getMaxLength();

                ensureAvailable(
                        data,
                        position,
                        length
                );

                String value =
                        readAscii(
                                data,
                                position,
                                length
                        );

                return new FieldReadResult(
                        value,
                        position + length
                );
            }

            case LLVAR -> {

                ensureAvailable(
                        data,
                        position,
                        2
                );

                String lengthText =
                        readAscii(
                                data,
                                position,
                                2
                        );

                int length =
                        parseLength(lengthText, field);

                position += 2;

                ensureAvailable(
                        data,
                        position,
                        length
                );

                String value =
                        readAscii(
                                data,
                                position,
                                length
                        );

                return new FieldReadResult(
                        value,
                        position + length
                );
            }

            case LLLVAR -> {

                ensureAvailable(
                        data,
                        position,
                        3
                );

                String lengthText =
                        readAscii(
                                data,
                                position,
                                3
                        );

                int length =
                        parseLength(lengthText, field);

                position += 3;

                ensureAvailable(
                        data,
                        position,
                        length
                );

                String value =
                        readAscii(
                                data,
                                position,
                                length
                        );

                return new FieldReadResult(
                        value,
                        position + length
                );
            }

            default -> throw new IllegalStateException(
                    "Unsupported field length type"
            );
        }
    }

    private int parseLength(String lengthText, IsoField field) {
        try {

            int length =
                    Integer.parseInt(lengthText);

            if (length > field.getMaxLength()) {

                throw new IllegalArgumentException(
                        "DE" + field.getNumber()
                                + " length "
                                + length
                                + " exceeds maximum "
                                + field.getMaxLength()
                );
            }
            return length;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid length for DE"
                            + field.getNumber()
                            + ": "
                            + lengthText,
                    e
            );
        }
    }

    private void ensureAvailable(byte[] data, int position, int required) {
        if (position + required > data.length) {
            throw new IllegalArgumentException(
                    "Unexpected end of ISO message. "
                            + "Required "
                            + required
                            + " bytes at position "
                            + position
            );
        }
    }

    private String readAscii(byte[] data, int position, int length) {

        return new String(
                data,
                position,
                length,
                StandardCharsets.US_ASCII
        );
    }

    private record FieldReadResult(String value, int nextPosition) { }
}
