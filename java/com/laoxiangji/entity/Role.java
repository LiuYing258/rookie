package com.laoxiangji.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    @Column(name = "description")
    private String description;

    @Column(name = "created_time", insertable = false, updatable = false)
    private LocalDateTime createdTime;

    // 与用户的多对多关系
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}