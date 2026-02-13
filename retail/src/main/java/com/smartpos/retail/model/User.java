package com.smartpos.retail.model;

import jakarta.persistence.*;

/**
 * Model class representing a user in the system (Entity)
 */
@Entity
@Table(name = "users") // Good practice to name the table explicitly
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Saves "ADMIN" or "CASHIER" in the database
    private UserRole role;

    private String fullName;

    // --- 1. MANDATORY EMPTY CONSTRUCTOR ---
    // Hibernate uses this to create the object before filling it with data
    public User() {
    }

    // --- 2. CONSTRUCTOR FOR CREATING NEW USERS ---
    public User(String username, String password, UserRole role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    // --- 3. GETTERS AND SETTERS ---
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // --- 4. LOGIC METHODS ---
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isCashier() {
        return role == UserRole.CASHIER;
    }

    // --- 5. ENUM DEFINITION ---
    public enum UserRole {
        ADMIN("مدير"),
        CASHIER("كاشير");

        private final String arabicName;

        UserRole(String arabicName) {
            this.arabicName = arabicName;
        }

        public String getArabicName() {
            return arabicName;
        }
    }
}