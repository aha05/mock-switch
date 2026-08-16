package com.iso8583.mock_switch.iso8583;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Iso8583Test {

    @Test
    void shouldBuildAndParsePurchaseMessage() {

        Iso8583Message request =
                new Iso8583Message("0200");

        request.setField(
                IsoField.PAN,
                "4000001234567890"
        );

        request.setField(
                IsoField.PROCESSING_CODE,
                "000000"
        );

        request.setField(
                IsoField.AMOUNT_TRANSACTION,
                "000000010000"
        );

        request.setField(
                IsoField.TRANSMISSION_DATE_TIME,
                "0814233745"
        );

        request.setField(
                IsoField.SYSTEM_TRACE_AUDIT_NUMBER,
                "123456"
        );

        request.setField(
                IsoField.LOCAL_TRANSACTION_TIME,
                "233745"
        );

        request.setField(
                IsoField.LOCAL_TRANSACTION_DATE,
                "0814"
        );

        request.setField(
                IsoField.MERCHANT_TYPE,
                "5411"
        );

        request.setField(
                IsoField.POS_ENTRY_MODE,
                "012"
        );

        request.setField(
                IsoField.POS_CONDITION_CODE,
                "00"
        );

        request.setField(
                IsoField.RETRIEVAL_REFERENCE_NUMBER,
                "123456789012"
        );

        request.setField(
                IsoField.TERMINAL_ID,
                "TERM0001"
        );

        request.setField(
                IsoField.MERCHANT_ID,
                "MERCHANT0000001"
        );

        request.setField(
                IsoField.CURRENCY_CODE,
                "230"
        );

        // Build
        Iso8583Builder builder =
                new Iso8583Builder();

        byte[] bytes =
                builder.build(request);

        System.out.println(
                "Message length: " + bytes.length
        );

        // Parse
        Iso8583Parser parser =
                new Iso8583Parser();

        Iso8583Message parsed =
                parser.parse(bytes);

        // Verify
        assertEquals(
                "0200",
                parsed.getMti()
        );

        assertEquals(
                "4000001234567890",
                parsed.getField(2)
        );

        assertEquals(
                "000000",
                parsed.getField(3)
        );

        assertEquals(
                "000000010000",
                parsed.getField(4)
        );

        assertEquals(
                "123456",
                parsed.getField(11)
        );

        assertEquals(
                "TERM0001",
                parsed.getField(41)
        );

        assertEquals(
                "MERCHANT0000001",
                parsed.getField(42)
        );

        assertEquals(
                "230",
                parsed.getField(49)
        );
    }
}