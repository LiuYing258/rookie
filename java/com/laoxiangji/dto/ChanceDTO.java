package com.laoxiangji.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChanceDTO {
    private Long id;
    private String chanceCode;
    private String chanceName;
    private String zone;
    private String province;
    private String city;
    private String area;
    private String address;
    private Integer size;
    private Integer rent;
    private Integer flow;
    private String competition;
    private Boolean favorite;
    private LocalDateTime openTime;
    private String note;
}
