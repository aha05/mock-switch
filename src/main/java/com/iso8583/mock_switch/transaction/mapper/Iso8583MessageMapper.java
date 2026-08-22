package com.iso8583.mock_switch.transaction.mapper;

import com.iso8583.mock_switch.client.dto.JsonRequest;
import com.iso8583.mock_switch.client.dto.JsonResponse;
import com.iso8583.mock_switch.iso8583.Iso8583Message;
import com.iso8583.mock_switch.transaction.entity.TransactionJournal;

public interface Iso8583MessageMapper {
    JsonRequest toJson(Iso8583Message iso8583Message);
    Iso8583Message fromJson(JsonResponse jsonResponse);
    TransactionJournal toEntity(Iso8583Message iso8583Message);
}
