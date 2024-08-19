package io.github.liruohrh.apiweb.model;

import lombok.Data;

@Data
public class VVHanWeatherReq {
    private String city;
    private String ip;
    private String type;
}