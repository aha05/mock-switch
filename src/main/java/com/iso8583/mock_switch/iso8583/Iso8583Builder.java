package com.iso8583.mock_switch.iso8583;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class Iso8583Builder {

    public byte[] build(Iso8583Message message) {

        validateMessage(message);

        ByteArrayOutputStream output = new ByteArrayOutputStream(); // this is used to collect bytes in memory as you write them. it has flexible size

        // MTI
        writeAscii(output, message.getMti());

        // Bitmap
        Bitmap bitmap = Bitmap.fromFields(message.getFields().keySet());

        writeBytes(output, bitmap.toBytes());

        // Data elements
        for (Map.Entry<Integer, String> entry :
                message.getFields().entrySet()) {

            int fieldNumber = entry.getKey();

            String value = entry.getValue();

            IsoField field = IsoField.fromNumber(fieldNumber);

            writeField(output, field, value);
        }

        return output.toByteArray();
    }

    private void writeField(ByteArrayOutputStream output, IsoField field, String value) {

        validateFieldLength(field, value);

        switch (field.getLengthType()) {

            case FIXED -> writeAscii(output, value);

            case LLVAR -> {

                String length =
                        String.format("%02d", value.length());

                writeAscii(output, length);
                writeAscii(output, value);
            }

            case LLLVAR -> {
                String length =
                        String.format("%03d", value.length());

                writeAscii(output, length);
                writeAscii(output, value);
            }
        }
    }

    private void validateFieldLength(IsoField field, String value) {

        int length = value.length();

        switch (field.getLengthType()) {

            case FIXED -> {

                if (length != field.getMaxLength()) {

                    throw new IllegalArgumentException(
                            "DE" + field.getNumber()
                                    + " must be exactly "
                                    + field.getMaxLength()
                                    + " characters, but was "
                                    + length
                    );
                }
            }

            case LLVAR, LLLVAR -> {

                if (length > field.getMaxLength()) {

                    throw new IllegalArgumentException(
                            "DE" + field.getNumber()
                                    + " exceeds maximum length "
                                    + field.getMaxLength()
                    );
                }
            }
        }
    }

    private void validateMessage(Iso8583Message message) {

        if (message == null) {
            throw new IllegalArgumentException(
                    "ISO message cannot be null"
            );
        }

        if (message.getMti() == null) {
            throw new IllegalArgumentException(
                    "MTI is required"
            );
        }
    }

    private void writeAscii(ByteArrayOutputStream output, String value) {
        byte[] bytes =
                value.getBytes(StandardCharsets.US_ASCII);

        output.writeBytes(bytes);
    }

    private void writeBytes(ByteArrayOutputStream output, byte[] bytes) {
        output.writeBytes(bytes);
    }
}
