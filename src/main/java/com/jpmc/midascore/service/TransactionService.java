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

    @Transactional
    public void process(Transaction tx) {
        long senderId    = tx.getSenderId();
        long recipientId = tx.getRecipientId();
        float amount     = tx.getAmount();

        UserRecord sender    = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        if (sender == null || recipient == null || sender.getBalance() < amount) {
            return;
        }

        Incentive inc = restTemplate.postForObject(
            "http://localhost:8080/incentive",
            tx,
            Incentive.class
        );
        float incentiveAmount = (inc != null) ? inc.getAmount() : 0f;

        TransactionRecord record = new TransactionRecord(
            sender,
            recipient,
            amount,
            Instant.now()
        );
        record.setIncentive(incentiveAmount);
        transactionRepository.save(record);

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);
        userRepository.saveAll(List.of(sender, recipient));
    }


    public float getCurrentBalance(Long userId) {
    return userRepository.findById(userId)         // now returns Optional<UserRecord>
                         .map(UserRecord::getBalance)
                         .orElse(0f);
}
}
