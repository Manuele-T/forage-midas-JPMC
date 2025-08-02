package com.jpmc.midascore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Exposes a RestTemplate bean so it can be @Autowired elsewhere.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
