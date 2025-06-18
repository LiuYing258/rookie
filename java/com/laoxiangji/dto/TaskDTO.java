package com.laoxiangji.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TaskDTO {
    private Long id;
    private String taskCode;
    private String taskName;
    private String taskType;
    private String description;
    private String status;
    private Integer priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer progress;
    private String notes;
}