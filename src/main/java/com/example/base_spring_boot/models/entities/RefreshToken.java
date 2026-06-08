package com.example.base_spring_boot.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chuỗi token ngẫu nhiên (UUID)
    @Column(nullable = false, unique = true)
    private String token;

    // Thời gian hết hạn của Refresh Token
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    // Trạng thái đã thu hồi/vô hiệu hóa hay chưa
    @Column(nullable = false)
    private boolean revoked;

    // Khóa ngoại liên kết tới bảng User hiện có
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
