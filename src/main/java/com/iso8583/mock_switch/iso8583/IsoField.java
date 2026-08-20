package com.iso8583.mock_switch.iso8583;

public enum IsoField {

    PAN(2, "PAN", 19, LengthType.LLVAR),

    PROCESSING_CODE(3, "Processing Code", 6, LengthType.FIXED),

    AMOUNT_TRANSACTION(4, "Transaction Amount", 12, LengthType.FIXED),

    TRANSMISSION_DATE_TIME(7, "Transmission Date/Time", 10, LengthType.FIXED),

    SYSTEM_TRACE_AUDIT_NUMBER(11, "STAN", 6, LengthType.FIXED),

    LOCAL_TRANSACTION_TIME(12, "Local Transaction Time", 6, LengthType.FIXED),

    LOCAL_TRANSACTION_DATE(13, "Local Transaction Date", 4, LengthType.FIXED),

    MERCHANT_TYPE(18, "Merchant Type", 4, LengthType.FIXED),

    POS_ENTRY_MODE(22, "POS Entry Mode", 3, LengthType.FIXED),

    POS_CONDITION_CODE(25, "POS Condition Code", 2, LengthType.FIXED),

    TRACK_2_DATA(35, "Track 2 Data", 37, LengthType.LLVAR),

    RETRIEVAL_REFERENCE_NUMBER(37, "RRN", 12, LengthType.FIXED),

    AUTHORIZATION_CODE(38, "Authorization Code", 6, LengthType.FIXED),

    RESPONSE_CODE(39, "Response Code", 2, LengthType.FIXED),

    TERMINAL_ID(41, "Terminal ID", 8, LengthType.FIXED),

    MERCHANT_ID(42, "Merchant ID", 15, LengthType.FIXED),

    CURRENCY_CODE(49, "Currency Code", 3, LengthType.FIXED),

    PIN_DATA(52, "PIN Data", 16, LengthType.FIXED),

    ADDITIONAL_DATA(48, "Additional Data", 999, LengthType.LLLVAR),

    ORIGINAL_DATA_ELEMENTS(90, "Original Data Elements", 42, LengthType.FIXED),

    ADDITIONAL_RESPONSE_DATA(44, "Additional Response Data", 25, LengthType.LLVAR),

    TRANSACTION_LIFE_CYCLE_ID(112, "Transaction Life Cycle ID", 999, LengthType.LLLVAR);

    private final int number;
    private final String description;
    private final int maxLength;
    private final LengthType lengthType;

    IsoField(
            int number,
            String description,
            int maxLength,
            LengthType lengthType
    ) {
        this.number = number;
        this.description = description;
        this.maxLength = maxLength;
        this.lengthType = lengthType;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public LengthType getLengthType() {
        return lengthType;
    }

    public static IsoField fromNumber(int number) {

        for (IsoField field : values()) {
            if (field.number == number) {
                return field;
            }
        }

        throw new IllegalArgumentException(
                "Unsupported ISO 8583 field: " + number
        );
    }

    public enum LengthType {
        FIXED,
        LLVAR,
        LLLVAR
    }
}
