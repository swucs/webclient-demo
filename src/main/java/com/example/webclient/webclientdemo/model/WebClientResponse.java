package com.example.webclient.webclientdemo.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class WebClientResponse {

    private HttpStatus.Series statusSeries;
    private String body;
}
