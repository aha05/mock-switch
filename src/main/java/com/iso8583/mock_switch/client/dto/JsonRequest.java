package com.iso8583.mock_switch.client.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class JsonRequest {
    private String merchantType;

    private String posEntryMode;

    private String posConditionCode;

    private String track2Data;

    private String retrievalReferenceNumber;

    private String authorizationCode;

    private String responseCode;

    private String terminalId;

    private String merchantId;

    private String currencyCode;

    private String pinData;

    private String additionalData;

    private String originalDataElements;

    private String additionalResponseData;

    private String transactionLifeCycleId;
}
