package com.example.webclient.webclientdemo;

import com.example.webclient.webclientdemo.config.WebClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

public class WebClientTest {

    WebClient webClient() {
        return new WebClientConfig().webClient();
    }

    @Test
    void callWebClient() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(10);
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        for (int i = 0; i < 10; i++) {

            final int index = i;

            webClient().get()
                    .uri("http://localhost:8088/api/users")
                    .retrieve()
                    .onStatus(HttpStatus::is2xxSuccessful, response -> {
                        System.out.println("onStatus " + index + "= " + response.statusCode());
                        return Mono.empty();
                    })
                    .onStatus(HttpStatus::is4xxClientError, response -> {
                        System.out.println("onStatus " + index + "= " + response.statusCode());
                        return Mono.empty();
                    })
                    .bodyToMono(String.class)
                    .doOnTerminate(() -> countDownLatch.countDown())
                    .subscribe()
            ;
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        System.out.println(stopWatch.shortSummary());
        System.out.println(stopWatch.getTotalTimeMillis());
        System.out.println(stopWatch.prettyPrint());

    }

}