package com.laoxiangji.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StoreDTO {
    private Long id;
    private Long storeCode;
    private String storeName;
    private String zone;
    private String province;
    private String city;
    private String area;
    private String address;
    private LocalDate opentime;
    private Integer size;
    private Integer seat;
    private Boolean favorite;
    private String lngLat;
}

