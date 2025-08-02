package com.jpmc.midascore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MidasCoreApplication {
    static {
        if (System.getProperty("broker.id") == null) {
            System.setProperty("broker.id", "1");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MidasCoreApplication.class, args);
    }

}
