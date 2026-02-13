package com.smartpos.retail.service;

import com.smartpos.retail.model.CartItem;
import com.smartpos.retail.model.ProductRow;
import java.util.List;

/**
 * Service interface for shopping cart operations
 */
public interface CartService {
    
    /**
     * Add product to cart or update quantity if already exists
     * @param product The product to add
     * @param quantity The quantity to add
     * @param cartItems Current cart items
     * @return The updated or newly created cart item
     * @throws IllegalArgumentException if quantity is invalid or stock is insufficient
     */
    CartItem addToCart(ProductRow product, int quantity, List<CartItem> cartItems);
    
    /**
     * Remove item from cart
     * @param cartItems Current cart items
     * @param productName The product name to remove
     */
    void removeFromCart(List<CartItem> cartItems, String productName);
    
    /**
     * Clear all items from cart
     * @param cartItems Current cart items
     */
    void clearCart(List<CartItem> cartItems);
    
    /**
     * Calculate subtotal of cart items
     * @param cartItems Current cart items
     * @return Subtotal amount
     */
    double calculateSubtotal(List<CartItem> cartItems);
    
    /**
     * Calculate tax amount
     * @param subtotal The subtotal amount
     * @param taxRate The tax rate (e.g., 0.10 for 10%)
     * @return Tax amount
     */
    double calculateTax(double subtotal, double taxRate);
    
    /**
     * Calculate total amount (subtotal + tax)
     * @param cartItems Current cart items
     * @param taxRate The tax rate
     * @return Total amount
     */
    double calculateTotal(List<CartItem> cartItems, double taxRate);
    
    /**
     * Validate if product can be added to cart
     * @param product The product to validate
     * @param quantity The requested quantity
     * @param cartItems Current cart items
     * @return true if valid, false otherwise
     */
    boolean canAddToCart(ProductRow product, int quantity, List<CartItem> cartItems);
}
