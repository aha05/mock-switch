package com.iso8583.mock_switch.iso8583;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Iso8583Message {

    private String mti; // Message Type Indicator: It is the first 4 digits of an ISO 8583 message and tells you what type of message it is.

    private final Map<Integer, String> fields = new TreeMap<>(); // TreeMap store data in key value pairs. However, it automatically sorts the keys.

    public Iso8583Message() {
    }

    public Iso8583Message(String mti) {
        setMti(mti);
    }

    public String getMti() {
        return mti;
    }

    public void setMti(String mti) {

        if (mti == null || !mti.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "MTI must be exactly 4 numeric characters"
            );
        }

        this.mti = mti;
    }

    public void setField(int fieldNumber, String value) {

        if (fieldNumber < 2 || fieldNumber > 128) {
            throw new IllegalArgumentException(
                    "Field number must be between 2 and 128"
            );
        }

        if (value == null) {
            throw new IllegalArgumentException(
                    "Field " + fieldNumber + " cannot have null value"
            );
        }

        fields.put(fieldNumber, value);
    }

    public void setField(IsoField field, String value) {
        setField(field.getNumber(), value);
    }

    public String getField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    public String getField(IsoField field) {
        return getField(field.getNumber());
    }

    public boolean hasField(int fieldNumber) {
        return fields.containsKey(fieldNumber);
    }

    public boolean hasField(IsoField field) {
        return hasField(field.getNumber());
    }

    public void removeField(int fieldNumber) {
        fields.remove(fieldNumber);
    }

    public Map<Integer, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public void clearFields() {
        fields.clear();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("MTI: ")
                .append(mti)
                .append(System.lineSeparator());

        for (Map.Entry<Integer, String> entry : fields.entrySet()) {

            builder.append("DE")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }
}
