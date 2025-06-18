package com.laoxiangji.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_chance_info")
public class Chance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chance_code", nullable = false)
    private String chanceCode;

    @Column(name = "chance_name", nullable = false)
    private String chanceName;

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

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private Integer rent;

    @Column(nullable = false)
    private Integer flow;

    @Column(nullable = false)
    private String competition;

    private String contact;
    private String phone;

    @Column(name = "open_time")
    private LocalDateTime openTime;

    private String note;
    private Boolean favorite;

    @Column(name = "img_list", columnDefinition = "TEXT")
    private String imgList;

    @Column(name = "file_list", columnDefinition = "TEXT")
    private String fileList;

    @Column(name = "other_file_list", columnDefinition = "TEXT")
    private String otherFileList;
}