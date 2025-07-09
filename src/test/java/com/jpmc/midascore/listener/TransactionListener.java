package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {
    private final List<Transaction> received = new ArrayList<>();

    @KafkaListener(topics = "${general.kafka-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(Transaction tx) {

        received.add(tx);
    }

    public List<Transaction> getReceived() {
        return received;
    }
}
