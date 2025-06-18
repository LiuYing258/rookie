package com.laoxiangji.repository;

import com.laoxiangji.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    Page<User> findByLevelNot(String level, Pageable pageable);
    Page<User> findByZoneAndLevelIn(String zone, List<String> levels, Pageable pageable);
    Page<User> findByProvinceAndLevel(String province, String level, Pageable pageable);




// ===== 异常处理类 =====

    /**
     * 用户相关异常
     */
    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * 权限不足异常
     */
    public class AccessDeniedException extends RuntimeException {
        public AccessDeniedException(String message) {
            super(message);
        }
    }
}