package main.java.com.example.mytasknote.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Getter @Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String username;

    @Column(nullable=false, length=255)
    private String password;

    @Column(name="display_name", length=100)
    private String displayName;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;
}
