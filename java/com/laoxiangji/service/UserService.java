package com.laoxiangji.service;

import com.laoxiangji.dto.UserDTO;
import com.laoxiangji.dto.UserDetailDTO;
import com.laoxiangji.dto.TaskDTO;
import com.laoxiangji.entity.User;
import com.laoxiangji.repository.UserRepository;
import com.laoxiangji.security.PermissionUtils;
import com.laoxiangji.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionUtils permissionUtils;

    @Autowired
    private TaskService taskService;

    public UserDetailDTO getCurrentUserWithTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDetailDTO dto = convertToDetailDTO(user);

        // 获取用户任务
        List<TaskDTO> tasks = taskService.getUserTasks(user.getId());
        dto.setTasks(tasks);

        return dto;
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(currentUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        // 检查权限：只能修改自己的信息或者有管理权限
        if (!currentUser.getId().equals(id)) {
            if (!permissionUtils.canManageUser(currentUser, targetUser)) {
                throw new RuntimeException("无权限修改该用户信息");
            }
        }

        // 更新用户信息
        if (userDTO.getName() != null) targetUser.setName(userDTO.getName());
        if (userDTO.getEmail() != null) targetUser.setEmail(userDTO.getEmail());
        if (userDTO.getPhone() != null) targetUser.setPhone(userDTO.getPhone());
        if (userDTO.getZone() != null) targetUser.setZone(userDTO.getZone());
        if (userDTO.getProvince() != null) targetUser.setProvince(userDTO.getProvince());
        if (userDTO.getCity() != null) targetUser.setCity(userDTO.getCity());

        User updatedUser = userRepository.save(targetUser);
        return convertToDTO(updatedUser);
    }

    /**
     * 获取当前用户可管理的用户列表（分页）
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getManagedUsers(Pageable pageable) {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(currentUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        System.out.println("当前用户: " + currentUser.getEmployeeId() + ", 级别: " + currentUser.getLevel());

        // 检查是否有访问人员管理的权限（市级用户不能访问）
        if (!permissionUtils.canAccessPersonnelManagement(currentUser)) {
            System.out.println("用户无权访问人员管理");
            return Page.empty(pageable);
        }

        String currentLevel = currentUser.getLevel();
        Page<User> userPage;

        // 根据不同级别返回不同的用户列表
        switch (currentLevel) {
            case "全国":
            case "管理员":
                // 全国级可以看到所有非全国级用户
                System.out.println("全国级/管理员用户，查询所有非全国级用户");
                userPage = userRepository.findByLevelNot("全国", pageable);
                break;

            case "战区":
                // 战区级可以看到本战区的省级和市级用户
                System.out.println("战区级用户，查询战区: " + currentUser.getZone());
                userPage = userRepository.findByZoneAndLevelIn(
                        currentUser.getZone(),
                        Arrays.asList("省", "市"),
                        pageable);
                break;

            case "省":
                // 省级可以看到本省的市级用户
                System.out.println("省级用户，查询省份: " + currentUser.getProvince());
                userPage = userRepository.findByProvinceAndLevel(
                        currentUser.getProvince(),
                        "市",
                        pageable);
                break;

            default:
                System.out.println("未知级别: " + currentLevel);
                return Page.empty(pageable);
        }

        System.out.println("查询到用户数量: " + userPage.getTotalElements());

        // 转换为DTO
        List<UserDTO> dtoList = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, userPage.getTotalElements());
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Page<UserDTO> getAllUsers(Pageable pageable){
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * 根据ID获取用户详情（包含权限检查）
     * @param id 用户ID
     * @return 用户详情
     */
    public UserDetailDTO getUserDetailById(Long id) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl currentUserDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(currentUserDetails.getId())
                .orElseThrow(() -> new RuntimeException("当前用户不存在"));

        // 查找目标用户
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 权限检查：可以查看自己的信息或有管理权限的用户信息
        if (!currentUser.getId().equals(id) && !permissionUtils.canManageUser(currentUser, targetUser)) {
            throw new RuntimeException("无权限查看该用户信息");
        }

        // 转换为DTO
        UserDetailDTO dto = convertToDetailDTO(targetUser);

        // 获取用户任务（如果有权限查看）
        List<TaskDTO> tasks = taskService.getUserTasks(targetUser.getId());
        dto.setTasks(tasks);

        return dto;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setZone(user.getZone());
        dto.setProvince(user.getProvince());
        dto.setCity(user.getCity());
        dto.setLevel(user.getLevel());
        dto.setBirthdate(user.getBirthdate());
        dto.setCreatedTime(user.getCreatedTime());
        return dto;
    }

    private UserDetailDTO convertToDetailDTO(User user) {
        UserDetailDTO dto = new UserDetailDTO();
        dto.setId(user.getId());
        dto.setEmployeeId(user.getEmployeeId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setZone(user.getZone());
        dto.setProvince(user.getProvince());
        dto.setCity(user.getCity());
        dto.setLevel(user.getLevel());
        dto.setBirthdate(user.getBirthdate());
        dto.setCreatedTime(user.getCreatedTime());
        return dto;
    }
}