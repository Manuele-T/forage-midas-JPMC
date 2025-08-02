package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a processed transaction stored in H2.
 */
@Entity
@Table(name = "transaction_record")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private Instant timestamp;

    protected TransactionRecord() {
        // JPA
    }

    /**
     * Create a TransactionRecord with the given details.
     * @param sender the user sending funds
     * @param recipient the user receiving funds
     * @param amount the transaction amount
     * @param timestamp the time the transaction was processed
     */
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, Instant timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public UserRecord getSender() {
        return sender;
    }

    public UserRecord getRecipient() {
        return recipient;
    }

    public float getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("TransactionRecord[id=%d, sender=%d, recipient=%d, amount=%.2f, timestamp=%s]",
                id,
                sender.getId(),
                recipient.getId(),
                amount,
                timestamp.toString());
    }
}
