package com.example.webclient.webclientdemo.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebClientResponse {

    private int statusCode;
    private String body;
}
