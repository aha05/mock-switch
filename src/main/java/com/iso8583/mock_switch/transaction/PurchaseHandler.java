package com.iso8583.mock_switch.transaction;

import com.iso8583.mock_switch.client.HttpClient;
import com.iso8583.mock_switch.iso8583.Iso8583Message;
import com.iso8583.mock_switch.transaction.mapper.Iso8583MessageMapper;
import com.iso8583.mock_switch.transaction.repository.TransactionJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PurchaseHandler {
    private final HttpClient httpClient;
    private final Iso8583MessageMapper iso8583MessageMapper;
    private final TransactionJournalRepository transactionJournalRepository;

    public Iso8583Message handle(Iso8583Message request) {

        validateRequest(request);

        Iso8583Message response = new Iso8583Message();

        // 0200 → 0210

        response.setMti("0210");

        //Copy fields from request
        copyField(request, response, 3);   // Processing Code
        copyField(request, response, 4);   // Amount
        copyField(request, response, 7);   // Transmission Date/Time
        copyField(request, response, 11);  // STAN
        copyField(request, response, 12);  // Local Time
        copyField(request, response, 13);  // Local Date
        copyField(request, response, 18);  // Merchant Type
        copyField(request, response, 22);  // POS Entry Mode
        copyField(request, response, 25);  // POS Condition Code
        copyField(request, response, 37);  // RRN
        copyField(request, response, 41);  // Terminal ID
        copyField(request, response, 42);  // Merchant ID

        response.setField(39, "00");

        response.setField(38, "123456");


        // record in db
        var transactionJournal = iso8583MessageMapper.toEntity(response);
        transactionJournalRepository.save(transactionJournal);

        // update third party
        var jsonRequest = iso8583MessageMapper.toJson(response);
        httpClient.postPayment(jsonRequest);

        return response;
    }

    private void validateRequest(Iso8583Message request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Purchase request cannot be null"
            );
        }

        if (!"0200".equals(request.getMti())) {
            throw new IllegalArgumentException(
                    "Invalid MTI for purchase: " +
                            request.getMti()
            );
        }

        /*
         * Required fields for our mock purchase.
         */
        requireField(request, 3);
        requireField(request, 4);
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