package com.laoxiangji.controller;

import com.laoxiangji.dto.TaskProgressRequest;
import com.laoxiangji.dto.*;
import com.laoxiangji.service.UserService;
import com.laoxiangji.service.TaskService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailDTO> getCurrentUserWithTasks() {
        return ResponseEntity.ok(userService.getCurrentUserWithTasks());
    }

    /**
     * 根据用户ID获取用户详情
     * @param id 用户ID
     * @return 用户详情信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable Long id) {
        UserDetailDTO userDetail = userService.getUserDetailById(id);
        return ResponseEntity.ok(userDetail);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    /**
     * 获取指定用户详情
     */
    @GetMapping("person/{id}")
    public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable Long id) {
        UserDetailDTO userDetail = userService.getUserDetailById(id);
        return ResponseEntity.ok(userDetail);
    }

    /**
     * 获取所有用户列表（分页）
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managed")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getManagedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("=== getManagedUsers 开始执行 ===");
            System.out.println("请求参数: page=" + page + ", size=" + size);

            Pageable pageable = PageRequest.of(page, size);
            Page<UserDTO> result = userService.getManagedUsers(pageable);

            System.out.println("返回结果数量: " + result.getTotalElements());
            System.out.println("=== getManagedUsers 执行完成 ===");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("=== getManagedUsers 发生异常 ===");
            e.printStackTrace();

            // 返回更详细的错误信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("获取用户列表失败: " + e.getMessage()));
        }
    }

    @PutMapping("/tasks/{taskId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateTaskProgress(
            @PathVariable Long taskId,
            @RequestBody TaskProgressRequest request) {
        // 获取当前用户ID
        UserDetailDTO currentUser = userService.getCurrentUserWithTasks();
        taskService.updateTaskProgress(currentUser.getId(), taskId,
                request.getProgress(), request.getNotes());
        return ResponseEntity.ok(new MessageResponse("任务进度更新成功!"));
    }
}