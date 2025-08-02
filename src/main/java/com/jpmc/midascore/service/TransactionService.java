package com.jpmc.midascore.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRepository;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    public TransactionService(UserRepository userRepository,
                              TransactionRepository transactionRepository,
                              RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Process an incoming Transaction from Kafka:
     * 1. Verify both sender and recipient exist.
     * 2. Check sender has sufficient balance.
     * 3. If valid:
     *    - Call external Incentive API.
     *    - Persist a TransactionRecord (including incentive).
     *    - Update balances: subtract from sender, add amount+incentive to recipient.
     * 4. Otherwise: no DB changes.
     */
    @Transactional
    public void process(Transaction tx) {
        long senderId    = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount     = tx.getAmount();

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

        // 3. fetch incentive from external API
        Incentive inc = restTemplate.postForObject(
            "http://localhost:8080/incentive",
            tx,
            Incentive.class
        );
        float incentiveAmount = (inc != null) ? inc.getAmount() : 0f;

        // 4a. record the transaction including incentive
        TransactionRecord record = new TransactionRecord(
            sender,
            recipient,
            amount,
            Instant.now()
        );
        record.setIncentive(incentiveAmount);
        transactionRepository.save(record);

        // 4b. update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(
            recipient.getBalance() + amount + incentiveAmount
        );
        userRepository.saveAll(List.of(sender, recipient));
    }
}
