package com.example.base_spring_boot.models.repositories;

import com.example.base_spring_boot.models.entities.RefreshToken;
import com.example.base_spring_boot.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // Tìm kiếm RefreshToken dựa trên chuỗi token ngẫu nhiên (Yêu cầu Câu 2)
    Optional<RefreshToken> findByToken(String token);
    
    // Xóa/thu hồi toàn bộ RefreshToken của một User cụ thể (Yêu cầu Câu 3)
    @Modifying
    void deleteByUser(User user);
}
