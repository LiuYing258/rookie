package com.laoxiangji.dto;

import lombok.Data;

// 通用响应
@Data
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
