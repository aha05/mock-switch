package com.iso8583.mock_switch.transaction;

import com.iso8583.mock_switch.iso8583.Iso8583Message;
import org.springframework.stereotype.Component;

@Component
public class ReversalHandler {

    public Iso8583Message handle(Iso8583Message request) {

        validateRequest(request);

        Iso8583Message response = new Iso8583Message();

        /*
         * 0400 → 0410
         */
        response.setMti("0410");

        /*
         * Copy relevant fields
         */
        copyField(request, response, 3);   // Processing Code
        copyField(request, response, 4);   // Amount
        copyField(request, response, 7);   // Transmission Date/Time
        copyField(request, response, 11);  // STAN
        copyField(request, response, 12);  // Local Time
        copyField(request, response, 13);  // Local Date
        copyField(request, response, 37);  // RRN
        copyField(request, response, 41);  // Terminal ID
        copyField(request, response, 42);  // Merchant ID

        /*
         * Mock reversal approved.
         */
        response.setField(39, "00");

        return response;
    }

    private void validateRequest(Iso8583Message request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Reversal request cannot be null"
            );
        }

        if (!"0400".equals(request.getMti())) {
            throw new IllegalArgumentException(
                    "Invalid MTI for reversal: " +
                            request.getMti()
            );
        }

        requireField(request, 11);
        requireField(request, 41);
    }

    private void requireField(
            Iso8583Message message,
            int field
    ) {

        String value = message.getField(field);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Required field " + field + " is missing"
            );
        }
    }

    private void copyField(
            Iso8583Message request,
            Iso8583Message response,
            int field
    ) {

        String value = request.getField(field);

        if (value != null) {
            response.setField(field, value);
        }
    }
}