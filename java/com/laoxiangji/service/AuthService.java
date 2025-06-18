package com.laoxiangji.service;

import com.laoxiangji.dto.*;
import com.laoxiangji.entity.User;
import com.laoxiangji.entity.Role;
import com.laoxiangji.repository.UserRepository;
import com.laoxiangji.repository.RoleRepository;
import com.laoxiangji.security.JwtUtils;
import com.laoxiangji.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import com.laoxiangji.dto.ResetPasswordRequest;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                userDetails.getEmail(), roles);
    }

    @Transactional
    public MessageResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByEmployeeId(signUpRequest.getEmployeeId())) {
            return new MessageResponse("错误: 员工ID已存在!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("错误: 邮箱已被使用!");
        }

        // 创建新用户
        User user = new User();
        user.setEmployeeId(signUpRequest.getEmployeeId());
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setZone(signUpRequest.getZone());
        user.setProvince(signUpRequest.getProvince());
        user.setCity(signUpRequest.getCity());
        user.setLevel(signUpRequest.getLevel() != null ? signUpRequest.getLevel() : "普通用户");

        // 分配角色
        Set<Role> roles = new HashSet<>();

        // 所有用户都有基本用户角色
        Role userRole = roleRepository.findByRoleCode("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("错误: 角色未找到."));
        roles.add(userRole);

        // 如果level是管理员，额外添加管理员角色
        if ("管理员".equals(signUpRequest.getLevel())) {
            Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("错误: 管理员角色未找到."));
            roles.add(adminRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new MessageResponse("用户注册成功!");
    }

    // 添加新方法
    @Transactional
    public void resetPassword(ResetPasswordRequest resetRequest) {
        // 查找用户
        User user = userRepository.findByEmployeeId(resetRequest.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证邮箱
        if (!user.getEmail().equalsIgnoreCase(resetRequest.getEmail())) {
            throw new RuntimeException("邮箱不匹配");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        userRepository.save(user);
    }
}