package com.jpmc.midascore.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Process an incoming Transaction from Kafka
     */
    @Transactional
    public void process(Transaction tx) {
        long senderId = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount    = tx.getAmount();

        UserRecord sender    = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        // 1. both users must exist
        if (sender == null || recipient == null) {
            return;
        }

        // 2. sender must have sufficient funds
        if (sender.getBalance() < amount) {
            return;
        }

        // 3a. record the transaction
        TransactionRecord record = new TransactionRecord(
            sender,
            recipient,
            amount,
            Instant.now()
        );
        transactionRepository.save(record);

        // 3b. update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);
        userRepository.saveAll(List.of(sender, recipient));
    }
}