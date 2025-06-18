package com.laoxiangji.service;

import com.laoxiangji.dto.TaskDTO;
import com.laoxiangji.entity.Task;
import com.laoxiangji.entity.UserTask;
import com.laoxiangji.repository.TaskRepository;
import com.laoxiangji.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    public List<TaskDTO> getUserTasks(Long userId) {
        List<UserTask> userTasks = userTaskRepository.findByUserIdWithTask(userId);

        return userTasks.stream().map(ut -> {
            TaskDTO dto = new TaskDTO();
            Task task = ut.getTask();
            dto.setId(task.getId());
            dto.setTaskCode(task.getTaskCode());
            dto.setTaskName(task.getTaskName());
            dto.setTaskType(task.getTaskType());
            dto.setDescription(task.getDescription());
            dto.setStatus(task.getStatus());
            dto.setPriority(task.getPriority());
            dto.setStartDate(task.getStartDate());
            dto.setEndDate(task.getEndDate());
            dto.setProgress(ut.getProgress());
            dto.setNotes(ut.getNotes());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateTaskProgress(Long userId, Long taskId, Integer progress, String notes) {
        UserTask userTask = userTaskRepository.findByUserId(userId).stream()
                .filter(ut -> ut.getTask().getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        userTask.setProgress(progress);
        userTask.setNotes(notes);

        if (progress >= 100) {
            userTask.setCompletedDate(java.time.LocalDateTime.now());
        }

        userTaskRepository.save(userTask);
    }
}