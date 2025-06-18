package com.laoxiangji.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDetailDTO {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private String zone;
    private String province;
    private String city;
    private String level;
    private LocalDate birthdate;
    private LocalDateTime createdTime;
    private List<TaskDTO> tasks;
}