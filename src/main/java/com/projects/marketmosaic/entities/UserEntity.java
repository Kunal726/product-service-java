package com.projects.marketmosaic.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "marketmosaic_users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incrementing ID
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "roles")
    private String roles;  // Can store roles as a comma-separated string or a more complex structure (e.g., List<String>)

    @Column(name = "business_name")
    private String businessName;  // For suppliers

    @Column(name = "contact_phone")
    private String contactPhone;  // For suppliers

    @Column(name = "address")
    private String address;  // For suppliers

    @Column(name = "date_added")
    private LocalDateTime dateAdded;

    @Column(name = "is_active")
    private Boolean isActive;
}
