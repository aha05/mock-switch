package com.iso8583.mock_switch.iso8583;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class Iso8583Parser {

    public Iso8583Message parse(byte[] data) {

        if (data == null || data.length < 12) {
            throw new IllegalArgumentException(
                    "Invalid ISO 8583 message"
            );
        }

        int position = 0;

        // -----------------------------------------
        // MTI
        // -----------------------------------------

        String mti =
                readAscii(data, position, 4);

        position += 4;

        // -----------------------------------------
        // Primary Bitmap
        // -----------------------------------------

        byte[] primaryBitmapBytes =
                Arrays.copyOfRange(
                        data,
                        position,
                        position + 8
                );

        position += 8;

        boolean hasSecondaryBitmap =
                (primaryBitmapBytes[0] & 0x80) != 0;

        byte[] bitmapBytes;

        if (hasSecondaryBitmap) {
            byte[] secondaryBitmapBytes =
                    Arrays.copyOfRange(
                            data,
                            position,
                            position + 8
                    );

            position += 8;

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

        // -----------------------------------------
        // Create message
        // -----------------------------------------

        Iso8583Message message =
                new Iso8583Message(mti);

        // -----------------------------------------
        // Read fields
        // -----------------------------------------

        for (int fieldNumber : bitmap.getSetFields()) {
            IsoField field;
            try {
                field =
                        IsoField.fromNumber(fieldNumber);

            } catch (IllegalArgumentException e) {

                throw new IllegalArgumentException(
                        "Unsupported ISO field DE"
                                + fieldNumber,
                        e
                );
            }

            FieldReadResult result =
                    readField(
                            data,
                            position,
                            field
                    );

            message.setField(
                    fieldNumber,
                    result.value()
            );

            position = result.nextPosition();
        }

        // -----------------------------------------
        // Make sure nothing unexpected remains
        // -----------------------------------------

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
