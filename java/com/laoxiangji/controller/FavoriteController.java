package com.laoxiangji.controller;

import com.laoxiangji.dto.*;
import com.laoxiangji.service.FavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/favorites")
@PreAuthorize("hasRole('USER')")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/stores")
    public ResponseEntity<List<StoreDTO>> getFavoriteStores() {
        return ResponseEntity.ok(favoriteService.getFavoriteStores());
    }

    @GetMapping("/chances")
    public ResponseEntity<List<ChanceDTO>> getFavoriteChances() {
        return ResponseEntity.ok(favoriteService.getFavoriteChances());
    }

    @PutMapping("/stores/{id}/toggle")
    public ResponseEntity<StoreDTO> toggleStoreFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteService.toggleStoreFavorite(id));
    }

    @PutMapping("/chances/{id}/toggle")
    public ResponseEntity<ChanceDTO> toggleChanceFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(favoriteService.toggleChanceFavorite(id));
    }

    @PostMapping("/stores")
    public ResponseEntity<StoreDTO> createStore(@Valid @RequestBody StoreDTO storeDTO) {

            StoreDTO createdStore = favoriteService.createStore(storeDTO);
            return ResponseEntity.ok(createdStore);

    }

    @PutMapping("/stores/{id}")
    public ResponseEntity<StoreDTO> updateStore(@PathVariable Long id,
                                                @Valid @RequestBody StoreDTO storeDTO) {

            StoreDTO updatedStore = favoriteService.updateStore(id, storeDTO);
            return ResponseEntity.ok(updatedStore);

    }

    @PostMapping("/chances")
    public ResponseEntity<ChanceDTO> createChance(@Valid @RequestBody ChanceDTO chanceDTO) {
        try {
            ChanceDTO createdChance = favoriteService.createChance(chanceDTO);
            return ResponseEntity.ok(createdChance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/chances/{id}")
    public ResponseEntity<ChanceDTO> updateChance(@PathVariable Long id,
                                                  @Valid @RequestBody ChanceDTO chanceDTO) {
        try {
            ChanceDTO updatedChance = favoriteService.updateChance(id, chanceDTO);
            return ResponseEntity.ok(updatedChance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}



