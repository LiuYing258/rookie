package com.laoxiangji.service;

import com.laoxiangji.dto.StoreDTO;
import com.laoxiangji.dto.ChanceDTO;
import com.laoxiangji.entity.Store;
import com.laoxiangji.entity.Chance;
import com.laoxiangji.entity.User;
import com.laoxiangji.repository.StoreRepository;
import com.laoxiangji.repository.ChanceRepository;
import com.laoxiangji.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ChanceRepository chanceRepository;

    @Autowired
    private UserRepository userRepository;

    public List<StoreDTO> getFavoriteStores() {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = authentication.getName();
        User currentUser = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取所有收藏的门店（favorite=true）
        List<Store> favoriteStores = storeRepository.findByFavoriteTrue();

        // 根据用户级别进行筛选
        return favoriteStores.stream()
                .filter(store -> filterByUserLevel(store, currentUser))
                .map(this::convertStoreToDTO)
                .collect(Collectors.toList());
    }

    public List<ChanceDTO> getFavoriteChances() {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = authentication.getName();
        User currentUser = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 获取所有收藏的机会点（favorite=true）
        List<Chance> favoriteChances = chanceRepository.findByFavoriteTrue();

        // 根据用户级别进行筛选
        return favoriteChances.stream()
                .filter(chance -> filterByUserLevel(chance, currentUser))
                .map(this::convertChanceToDTO)
                .collect(Collectors.toList());
    }

    // 根据用户级别筛选门店
    private boolean filterByUserLevel(Store store, User user) {
        String userLevel = user.getLevel();

        if (userLevel == null) {
            return false;
        }

        return switch (userLevel) {
            case "全国" ->
                // 全国级别可以看到所有门店
                    true;
            case "战区" ->
                // 战区级别只能看到同一战区的门店
                    store.getZone() != null && store.getZone().equals(user.getZone());
            case "省" ->
                // 省级别只能看到同一省份的门店
                    store.getProvince() != null && (store.getProvince()+'省').equals(user.getProvince());
            case "市" ->
                // 市级别只能看到同一城市的门店
                    store.getCity() != null && (store.getCity()+'市').equals(user.getCity());
            default -> false;
        };
    }

    // 根据用户级别筛选机会点
    private boolean filterByUserLevel(Chance chance, User user) {
        String userLevel = user.getLevel();

        if (userLevel == null) {
            return false;
        }

        return switch (userLevel) {
            case "全国" ->
                // 全国级别可以看到所有机会点
                    true;
            case "战区" ->
                // 战区级别只能看到同一战区的机会点
                    chance.getZone() != null && chance.getZone().equals(user.getZone());
            case "省" ->
                // 省级别只能看到同一省份的机会点
                    chance.getProvince() != null && chance.getProvince().equals(user.getProvince());
            case "市" ->
                // 市级别只能看到同一城市的机会点
                    chance.getCity() != null && chance.getCity().equals(user.getCity());
            default -> false;
        };
    }

    @Transactional
    public StoreDTO toggleStoreFavorite(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));

        store.setFavorite(!store.getFavorite());
        Store updated = storeRepository.save(store);
        return convertStoreToDTO(updated);
    }

    @Transactional
    public ChanceDTO toggleChanceFavorite(Long chanceId) {
        Chance chance = chanceRepository.findById(chanceId)
                .orElseThrow(() -> new RuntimeException("机会点不存在"));

        chance.setFavorite(!chance.getFavorite());
        Chance updated = chanceRepository.save(chance);
        return convertChanceToDTO(updated);
    }

    private StoreDTO convertStoreToDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setStoreCode(store.getStoreCode());
        dto.setStoreName(store.getStoreName());
        dto.setZone(store.getZone());
        dto.setProvince(store.getProvince());
        dto.setCity(store.getCity());
        dto.setArea(store.getArea());
        dto.setAddress(store.getAddress());
        dto.setOpentime(store.getOpentime());
        dto.setSize(store.getSize());
        dto.setSeat(store.getSeat());
        dto.setFavorite(store.getFavorite());
        return dto;
    }

    private ChanceDTO convertChanceToDTO(Chance chance) {
        ChanceDTO dto = new ChanceDTO();
        dto.setId(chance.getId());
        dto.setChanceCode(chance.getChanceCode());
        dto.setChanceName(chance.getChanceName());
        dto.setZone(chance.getZone());
        dto.setProvince(chance.getProvince());
        dto.setCity(chance.getCity());
        dto.setArea(chance.getArea());
        dto.setAddress(chance.getAddress());
        dto.setSize(chance.getSize());
        dto.setRent(chance.getRent());
        dto.setFlow(chance.getFlow());
        dto.setCompetition(chance.getCompetition());
        dto.setFavorite(chance.getFavorite());
        return dto;
    }

    @Transactional
    public ChanceDTO createChance(ChanceDTO chanceDTO) {
        Chance chance = convertToEntity(chanceDTO);
        Chance savedChance = chanceRepository.save(chance);
        return convertToDTO(savedChance);
    }

    @Transactional
    public ChanceDTO updateChance(Long id, ChanceDTO chanceDTO) {
        Chance existingChance = chanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("机会点不存在"));

        // 更新字段
        updateChanceFields(existingChance, chanceDTO);
        Chance updatedChance = chanceRepository.save(existingChance);
        return convertToDTO(updatedChance);
    }

    private Chance convertToEntity(ChanceDTO dto) {
        Chance chance = new Chance();
        updateChanceFields(chance, dto);
        return chance;
    }

    private void updateChanceFields(Chance chance, ChanceDTO dto) {
        if (dto.getChanceCode() != null) chance.setChanceCode(dto.getChanceCode());
        if (dto.getChanceName() != null) chance.setChanceName(dto.getChanceName());
        if (dto.getZone() != null) chance.setZone(dto.getZone());
        if (dto.getProvince() != null) chance.setProvince(dto.getProvince());
        if (dto.getCity() != null) chance.setCity(dto.getCity());
        if (dto.getArea() != null) chance.setArea(dto.getArea());
        if (dto.getAddress() != null) chance.setAddress(dto.getAddress());
        if (dto.getSize() != null) chance.setSize(dto.getSize());
        if (dto.getRent() != null) chance.setRent(dto.getRent());
        if (dto.getFlow() != null) chance.setFlow(dto.getFlow());
        if (dto.getCompetition() != null) chance.setCompetition(dto.getCompetition());
        if (dto.getFavorite() != null) chance.setFavorite(dto.getFavorite());
    }

    private ChanceDTO convertToDTO(Chance chance) {
        ChanceDTO dto = new ChanceDTO();
        dto.setId(chance.getId());
        dto.setChanceCode(chance.getChanceCode());
        dto.setChanceName(chance.getChanceName());
        dto.setZone(chance.getZone());
        dto.setProvince(chance.getProvince());
        dto.setCity(chance.getCity());
        dto.setArea(chance.getArea());
        dto.setAddress(chance.getAddress());
        dto.setSize(chance.getSize());
        dto.setRent(chance.getRent());
        dto.setFlow(chance.getFlow());
        dto.setCompetition(chance.getCompetition());
        dto.setFavorite(chance.getFavorite());
        return dto;
    }

    @Transactional
    public StoreDTO createStore(StoreDTO storeDTO) {
        Store store = convertToEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return convertStoreToDTO(savedStore);
    }

    @Transactional
    public StoreDTO updateStore(Long id, StoreDTO storeDTO) {
        Store existingStore = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("门店不存在"));

        // 更新字段
        updateStoreFields(existingStore, storeDTO);
        Store updatedStore = storeRepository.save(existingStore);
        return convertToDTO(updatedStore);
    }

    private Store convertToEntity(StoreDTO dto) {
        Store store = new Store();
        updateStoreFields(store, dto);
        return store;
    }

    private void updateStoreFields(Store store, StoreDTO dto) {
        if (dto.getStoreCode() != null) store.setStoreCode(dto.getStoreCode());
        if (dto.getStoreName() != null) store.setStoreName(dto.getStoreName());
        if (dto.getZone() != null) store.setZone(dto.getZone());
        if (dto.getProvince() != null) store.setProvince(dto.getProvince());
        if (dto.getCity() != null) store.setCity(dto.getCity());
        if (dto.getArea() != null) store.setArea(dto.getArea());
        if (dto.getAddress() != null) store.setAddress(dto.getAddress());
        if (dto.getSize() != null) store.setSize(dto.getSize());
        if (dto.getSeat() != null) store.setSeat(dto.getSeat());
        if (dto.getFavorite() != null) store.setFavorite(dto.getFavorite());
    }

    private StoreDTO convertToDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setStoreCode(store.getStoreCode());
        dto.setStoreName(store.getStoreName());
        dto.setZone(store.getZone());
        dto.setProvince(store.getProvince());
        dto.setCity(store.getCity());
        dto.setArea(store.getArea());
        dto.setAddress(store.getAddress());
        dto.setSize(store.getSize());
        dto.setSeat(store.getSeat());
        dto.setFavorite(store.getFavorite());
        return dto;
    }
}