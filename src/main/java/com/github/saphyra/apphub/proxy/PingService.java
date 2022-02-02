package com.github.saphyra.apphub.proxy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ping.enabled", havingValue = "true")
public class PingService {
    private final RestTemplate restTemplate;

    @Value("${host}")
    private String host;

    @Value("${ping.url}")
    private String url;

    @Value("${ping.exitOnFailure}")
    private Boolean exitOnPingFailure;

    @Scheduled(fixedRateString = "${ping.interval}")
    public void pingServer() {
        try {
            String url = "http://" + host + "/platform/health";
            log.info("Pinging server on url {}", url);
            restTemplate.getForObject(url, Void.class);
            log.info("Ping successful");
        } catch (Exception e) {
            log.error("Connection lost.", e);
            if(exitOnPingFailure){
                System.exit(0);
            }
        }
    }
}
