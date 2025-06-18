package com.laoxiangji.repository;

import com.laoxiangji.entity.Chance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChanceRepository extends JpaRepository<Chance, Long> {
    List<Chance> findByFavoriteTrue();
    Page<Chance> findByZone(String zone, Pageable pageable);
    Page<Chance> findByCity(String city, Pageable pageable);
}