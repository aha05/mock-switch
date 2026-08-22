package com.iso8583.mock_switch.transaction.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_Journal")
public class TransactionJournal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantType;

    private String posEntryMode;

    private String posConditionCode;

    private String track2Data;

    @Column(unique = true)
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
