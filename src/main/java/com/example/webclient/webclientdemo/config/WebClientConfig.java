package com.example.webclient.webclientdemo.config;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS)));


        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();


        //Memory 조정: 2M (default 256KB)
//        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
//                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2*1024*1024))
//                .build();

//        return WebClient.builder()
//                .baseUrl("http://localhost:5011")
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .filter(
//                        (req, next) -> next.exchange(
//                                ClientRequest.from(req).header("from", "webclient").build()
//                        )
//                )
//                .filter(
//                        ExchangeFilterFunction.ofRequestProcessor(
//                                clientRequest -> {
//                                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
//                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
//                                    clientRequest.headers().forEach(
//                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
//                                    );
//                                    return Mono.just(clientRequest);
//                                }
//                        )
//                )
//                .filter(
//                        ExchangeFilterFunction.ofResponseProcessor(
//                                clientResponse -> {
//                                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
//                                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
//                                    return Mono.just(clientResponse);
//                                }
//                        )
//                )
////                .exchangeStrategies(exchangeStrategies)
//                .defaultHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.3")
//                .defaultCookie("httpclient-type", "webclient")
//                .build();
    }
}
