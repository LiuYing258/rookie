package com.laoxiangji.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_store_info")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_code", nullable = false)
    private Long storeCode;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String zone;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String address;

    private String mode = "3.5";

    @Column(nullable = false)
    private LocalDate opentime;

    private String floor = "2";
    private Integer seat = 48;
    private Integer size;
    private Integer minigrade = 3;
    private String mix = "风险区域";
    private Float rate = 0.94f;
    private String malltype = "社区";
    private Integer mallgrade = 4;
    private Boolean favorite = false;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @Column(name = "lng_lat")
    private String lngLat;
}