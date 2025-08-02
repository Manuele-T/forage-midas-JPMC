package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Task 4 debug test – runs against an embedded single-node KRaft broker.
 */
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = {
            // ── mandatory KRaft settings ─────────────────────────────
            "process.roles=broker,controller",
            "node.id=1",
            "broker.id=1",                        // ← ensure broker.id matches node.id
            "controller.listener.names=CONTROLLER",
            // two listeners, both on *random* ports chosen by the OS
            "listeners=PLAINTEXT://localhost:0,CONTROLLER://localhost:0",
            // tell Raft who the sole voter is (same host, controller port)
            "controller.quorum.voters=1@localhost:0"
        }
)
class TaskFourTests {

    private static final Logger log = LoggerFactory.getLogger(TaskFourTests.class);

    @Autowired private KafkaProducer kafkaProducer;
    @Autowired private UserPopulator userPopulator;
    @Autowired private FileLoader    fileLoader;

    @Test
    void task_four_verifier() throws InterruptedException {

        userPopulator.populate();
        for (String line : fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk")) {
            kafkaProducer.send(line);
        }
        Thread.sleep(2_000);                      // give the listener time

        log.info("----------------------------------------------------------");
        log.info("Attach the debugger and read Wilbur’s balance.");
        log.info("Stop the test once you have the number.");
        log.info("----------------------------------------------------------");

        while (true) {
            Thread.sleep(20_000);
            log.info("…");
        }
    }
}
