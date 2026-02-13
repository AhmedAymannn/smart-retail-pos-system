package com.smartpos.retail.service;

import com.smartpos.retail.model.User;

/**
 * Service interface for user authentication and management
 */
public interface UserService {
    
    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The password
     * @return User if authenticated, null otherwise
     */
    User authenticate(String username, String password);
    
    /**
     * Get current logged in user
     * @return Current user, null if not logged in
     */
    User getCurrentUser();
    
    /**
     * Set current logged in user
     * @param user The user to set as current
     */
    void setCurrentUser(User user);
    
    /**
     * Logout current user
     */
    void logout();
    
    /**
     * Check if user has permission to access inventory
     * @return true if user is admin, false otherwise
     */
    boolean canAccessInventory();
    
    /**
     * Check if user has permission to make sales
     * @return true if user is logged in, false otherwise
     */
    boolean canMakeSales();
}
