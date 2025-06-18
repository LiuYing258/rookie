package com.laoxiangji.dto;

import lombok.Data;

// UserController.java - 第56行
@Data
public class TaskProgressRequest {
    private Integer progress;
    private String notes;
}