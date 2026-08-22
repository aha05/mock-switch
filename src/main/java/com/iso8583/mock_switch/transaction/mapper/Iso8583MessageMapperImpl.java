package com.iso8583.mock_switch.transaction.mapper;

import com.iso8583.mock_switch.client.dto.JsonRequest;
import com.iso8583.mock_switch.client.dto.JsonResponse;
import com.iso8583.mock_switch.iso8583.Iso8583Message;
import com.iso8583.mock_switch.iso8583.IsoField;
import com.iso8583.mock_switch.transaction.entity.TransactionJournal;
import org.springframework.stereotype.Component;

@Component
public class Iso8583MessageMapperImpl implements Iso8583MessageMapper{
    @Override
    public JsonRequest toJson(Iso8583Message iso8583Message) {
        return JsonRequest.builder()
                .merchantType(iso8583Message.getField(IsoField.MERCHANT_TYPE))
                .posEntryMode(iso8583Message.getField(IsoField.POS_ENTRY_MODE))
                .posConditionCode(iso8583Message.getField(IsoField.POS_CONDITION_CODE))
                .track2Data(iso8583Message.getField(IsoField.TRACK_2_DATA))
                .retrievalReferenceNumber(iso8583Message.getField(IsoField.RETRIEVAL_REFERENCE_NUMBER))
                .authorizationCode(iso8583Message.getField(IsoField.AUTHORIZATION_CODE))
                .responseCode(iso8583Message.getField(IsoField.RESPONSE_CODE))
                .terminalId(iso8583Message.getField(IsoField.TERMINAL_ID))
                .merchantId(iso8583Message.getField(IsoField.MERCHANT_ID))
                .currencyCode(iso8583Message.getField(IsoField.CURRENCY_CODE))
                .pinData(iso8583Message.getField(IsoField.PIN_DATA))
                .additionalData(iso8583Message.getField(IsoField.ADDITIONAL_DATA))
                .originalDataElements(iso8583Message.getField(IsoField.ORIGINAL_DATA_ELEMENTS))
                .additionalResponseData(iso8583Message.getField(IsoField.ADDITIONAL_RESPONSE_DATA))
                .transactionLifeCycleId(iso8583Message.getField(IsoField.TRANSACTION_LIFE_CYCLE_ID))
                .build();
    }

    @Override
    public Iso8583Message fromJson(JsonResponse jsonResponse) {
        return null;
    }

    @Override
    public TransactionJournal toEntity(Iso8583Message iso8583Message) {
        return TransactionJournal.builder()
                .merchantType(iso8583Message.getField(IsoField.MERCHANT_TYPE))
                .posEntryMode(iso8583Message.getField(IsoField.POS_ENTRY_MODE))
                .posConditionCode(iso8583Message.getField(IsoField.POS_CONDITION_CODE))
                .track2Data(iso8583Message.getField(IsoField.TRACK_2_DATA))
                .retrievalReferenceNumber(iso8583Message.getField(IsoField.RETRIEVAL_REFERENCE_NUMBER))
                .authorizationCode(iso8583Message.getField(IsoField.AUTHORIZATION_CODE))
                .responseCode(iso8583Message.getField(IsoField.RESPONSE_CODE))
                .terminalId(iso8583Message.getField(IsoField.TERMINAL_ID))
                .merchantId(iso8583Message.getField(IsoField.MERCHANT_ID))
                .currencyCode(iso8583Message.getField(IsoField.CURRENCY_CODE))
                .pinData(iso8583Message.getField(IsoField.PIN_DATA))
                .additionalData(iso8583Message.getField(IsoField.ADDITIONAL_DATA))
                .originalDataElements(iso8583Message.getField(IsoField.ORIGINAL_DATA_ELEMENTS))
                .additionalResponseData(iso8583Message.getField(IsoField.ADDITIONAL_RESPONSE_DATA))
                .transactionLifeCycleId(iso8583Message.getField(IsoField.TRANSACTION_LIFE_CYCLE_ID))
                .build();
    }
}
