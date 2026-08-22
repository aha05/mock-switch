package com.iso8583.mock_switch.transaction.repository;

import com.iso8583.mock_switch.transaction.entity.TransactionJournal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJournalRepository extends JpaRepository<TransactionJournal, Long> {
}
