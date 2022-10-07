package com.example.webclient.webclientdemo;

import com.example.webclient.webclientdemo.config.WebClientConfig;
import com.example.webclient.webclientdemo.model.WebClientResponse;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;
import io.netty.util.internal.EmptyArrays;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 인증서 만료일 조회(최종 버전 소스임)
 */
public class WebClientSSLTest {

    static final int TOTAL_CALL_COUNT = 10;
//    static final String URL = "https://doksa.angryant.kr/";
//    static final String URL = "https://admin.sycoldstorage.com/";
//    static final String URL = "https://parkhanna.com/";
//    static final String URL = "https://github.com";
    static final String URL = "https://google.com";
//    static final String URL = "http://localhost:8088/api/users";

    /**
     * 인증서 만료일 조회(최종 버전 소스임)
     * @throws Exception
     */
    @Test
    void test() throws Exception {
        SslContext context = SslContextBuilder.forClient()
                .trustManager(new MyTrustManagerFactory())
                .build();

        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(context));

        WebClient wc = WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();


        wc.get().uri(URL)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e ->
                        Mono.just("error : " + e.getMessage())
                )
                .subscribe(rs -> System.out.println("rs = " + rs) );


        Thread.sleep(1000);
    }


    static class MyTrustManagerFactory extends SimpleTrustManagerFactory {

        private static final TrustManager tm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String s) {
                System.out.println("checkClientTrusted chain = " + chain);
                System.out.println(chain[0].getSubjectX500Principal());
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String s) {
                System.out.println("checkServerTrusted chain = " + chain);
                System.out.println("getSubjectDN: " +  chain[0].getSubjectDN().getName());

                Date expiresOn = chain[0].getNotAfter();
                Date now = new Date();

                System.out.println("expiresOn: " +  expiresOn);

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return EmptyArrays.EMPTY_X509_CERTIFICATES;
            }
        };

        private MyTrustManagerFactory() { }

        @Override
        protected void engineInit(KeyStore keyStore) throws Exception { }

        @Override
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception { }

        @Override
        protected TrustManager[] engineGetTrustManagers() {
            return new TrustManager[] { tm };
        }

    }

}




