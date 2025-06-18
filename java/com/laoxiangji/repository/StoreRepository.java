package com.laoxiangji.repository;

import com.laoxiangji.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByFavoriteTrue();
    Page<Store> findByZone(String zone, Pageable pageable);
    Page<Store> findByCity(String city, Pageable pageable);
}