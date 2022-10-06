package com.example.webclient.webclientdemo;

import com.example.webclient.webclientdemo.config.WebClientConfig;
import com.example.webclient.webclientdemo.model.WebClientResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;

public class WebClientTest {

    static final int TOTAL_CALL_COUNT = 10;
    static final String URL = "https://doksa.angryant.kr/";
//    static final String URL = "http://localhost:8088/api/users";

    WebClient webClient() {
        return new WebClientConfig().webClient();
    }

    @Test
    void callWebClient() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(TOTAL_CALL_COUNT);
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        for (int i = 0; i < TOTAL_CALL_COUNT; i++) {

            final int index = i;

            webClient().get()
                    .uri(URL)
                    .exchangeToMono(response ->
                            response.bodyToMono(String.class)
                            .map(body -> WebClientResponse.builder()
                                    .statusSeries(response.statusCode().series())
                                    .body(body)
                            )
                    )
                    .onErrorResume(e ->
                        Mono.just(WebClientResponse.builder()
                                .statusSeries(HttpStatus.Series.SERVER_ERROR)
                                .body(null))
                    )
                    .doOnTerminate(() -> countDownLatch.countDown())
                    .subscribe(result -> {
                        System.out.println("result " + index + " + = " + result);
                    })



//                    .retrieve()
//                    .onStatus(HttpStatus::is2xxSuccessful, response -> {
//                        System.out.println("onStatus " + index + "= " + response.statusCode());
//                        return Mono.empty();
//                    })
//                    .onStatus(HttpStatus::is4xxClientError, response -> {
//                        System.out.println("onStatus " + index + "= " + response.statusCode());
//                        return Mono.empty();
//                    })
//                    .onStatus(HttpStatus::is5xxServerError, response -> {
//                        System.out.println("onStatus " + index + "= " + response.statusCode());
//                        return Mono.empty();
//                    })
//                    .bodyToMono(String.class)
//                    .doOnTerminate(() -> countDownLatch.countDown())
//                    .subscribe()
            ;
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        System.out.println("shortSummary : " + stopWatch.shortSummary());
        System.out.println("소요시간(초) : " + stopWatch.getTotalTimeMillis() / 1000.0d);
        System.out.println("prettyPrint : " + stopWatch.prettyPrint());

    }

    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Test
    void callRest() {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        for (int i = 0; i < TOTAL_CALL_COUNT; i++) {
            try {
                ResponseEntity<String> response = restTemplate().exchange(URL, HttpMethod.GET, null, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    String body = response.getBody();
                    System.out.println("onStatus " + i + "= " + response.getStatusCode());
                    System.out.println("body = " + body);
                }
            } catch (Exception e) {
                System.out.println("onStatus " + i + "= 500");
            }
        }

        stopWatch.stop();
        System.out.println("shortSummary : " + stopWatch.shortSummary());
        System.out.println("소요시간(초) : " + stopWatch.getTotalTimeMillis() / 1000.0d);
        System.out.println("prettyPrint : " + stopWatch.prettyPrint());
    }

}