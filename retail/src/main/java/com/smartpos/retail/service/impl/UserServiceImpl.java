package com.smartpos.retail.service.impl;

import com.smartpos.retail.model.User;
import com.smartpos.retail.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserService
 * TODO: Replace with database-backed implementation
 */
@Service
public class UserServiceImpl implements UserService {
    
    private final List<User> users;
    private User currentUser;
    
    public UserServiceImpl() {
        // Initialize with sample users - will be replaced with database later
        this.users = new ArrayList<>();
        
        // Default admin user
        users.add(new User("admin", "admin123", User.UserRole.ADMIN, "مدير النظام"));
        
        // Default cashier user
        users.add(new User("cashier", "cashier123", User.UserRole.CASHIER, "كاشير"));
        
        // Additional sample users
        users.add(new User("manager", "manager123", User.UserRole.ADMIN, "مدير المتجر"));
        users.add(new User("ahmed", "ahmed123", User.UserRole.CASHIER, "أحمد محمد"));
    }
    
    @Override
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && 
                               user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public User getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    @Override
    public void logout() {
        this.currentUser = null;
    }
    
    @Override
    public boolean canAccessInventory() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    @Override
    public boolean canMakeSales() {
        return currentUser != null; // Any logged in user can make sales
    }
}
