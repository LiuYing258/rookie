package com.laoxiangji.repository;

import com.laoxiangji.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    List<UserTask> findByUserId(Long userId);

    @Query("SELECT ut FROM UserTask ut JOIN FETCH ut.task WHERE ut.user.id = :userId")
    List<UserTask> findByUserIdWithTask(@Param("userId") Long userId);
}