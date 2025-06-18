package com.laoxiangji.config;

import com.laoxiangji.entity.Role;
import com.laoxiangji.entity.User;
import com.laoxiangji.repository.RoleRepository;
import com.laoxiangji.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 初始化角色
        initRoles();

        // 初始化测试用户
        initTestUsers();
    }

    private void initRoles() {
        // 创建用户角色
        if (!roleRepository.existsByRoleCode("ROLE_USER")) {
            Role userRole = new Role();
            userRole.setRoleName("普通用户");
            userRole.setRoleCode("ROLE_USER");
            userRole.setDescription("系统普通用户，拥有基本权限");
            roleRepository.save(userRole);
            System.out.println("创建角色: ROLE_USER");
        }

        // 创建管理员角色
        if (!roleRepository.existsByRoleCode("ROLE_ADMIN")) {
            Role adminRole = new Role();
            adminRole.setRoleName("管理员");
            adminRole.setRoleCode("ROLE_ADMIN");
            adminRole.setDescription("系统管理员，拥有所有权限");
            roleRepository.save(adminRole);
            System.out.println("创建角色: ROLE_ADMIN");
        }
    }

    private void initTestUsers() {
        // 创建测试管理员
        if (!userRepository.existsByEmployeeId("ADMIN001")) {
            User admin = new User();
            admin.setEmployeeId("ADMIN001");
            admin.setName("系统管理员");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEmail("admin@laoxiangji.com");
            admin.setLevel("管理员");
            admin.setZone("总部");
            admin.setProvince("安徽");
            admin.setCity("合肥");

            // 分配角色
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(roleRepository.findByRoleCode("ROLE_USER").orElseThrow());
            adminRoles.add(roleRepository.findByRoleCode("ROLE_ADMIN").orElseThrow());
            admin.setRoles(adminRoles);

            userRepository.save(admin);
            System.out.println("创建测试管理员: ADMIN001 / 123456");
        }

        // 创建测试普通用户
        if (!userRepository.existsByEmployeeId("USER001")) {
            User user = new User();
            user.setEmployeeId("USER001");
            user.setName("测试用户");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setEmail("user@laoxiangji.com");
            user.setLevel("普通用户");
            user.setZone("华中");
            user.setProvince("湖北");
            user.setCity("武汉");

            // 分配角色
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(roleRepository.findByRoleCode("ROLE_USER").orElseThrow());
            user.setRoles(userRoles);

            userRepository.save(user);
            System.out.println("创建测试用户: USER001 / 123456");
        }
    }
}