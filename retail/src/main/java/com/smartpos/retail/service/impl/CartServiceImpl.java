package com.smartpos.retail.service.impl;

import com.smartpos.retail.model.CartItem;
import com.smartpos.retail.model.ProductRow;
import com.smartpos.retail.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CartService
 */
@Service
public class CartServiceImpl implements CartService {
    
    @Override
    public CartItem addToCart(ProductRow product, int quantity, List<CartItem> cartItems) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        // Check if product already exists in cart
        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getProductName().equals(product.getName()))
                .findFirst();
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            // Validate stock availability
            if (newQuantity > product.getStock()) {
                throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + product.getStock() + 
                    ", Requested: " + newQuantity
                );
            }
            
            item.setQuantity(newQuantity);
            return item;
        } else {
            // Validate stock availability for new item
            if (quantity > product.getStock()) {
                throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + product.getStock() + 
                    ", Requested: " + quantity
                );
            }
            
            CartItem newItem = new CartItem(product.getName(), quantity, product.getPrice());
            cartItems.add(newItem);
            return newItem;
        }
    }
    
    @Override
    public void removeFromCart(List<CartItem> cartItems, String productName) {
        if (productName == null || cartItems == null) {
            return;
        }
        
        cartItems.removeIf(item -> item.getProductName().equals(productName));
    }
    
    @Override
    public void clearCart(List<CartItem> cartItems) {
        if (cartItems != null) {
            cartItems.clear();
        }
    }
    
    @Override
    public double calculateSubtotal(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return 0.0;
        }
        
        return cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();
    }
    
    @Override
    public double calculateTax(double subtotal, double taxRate) {
        return subtotal * taxRate;
    }
    
    @Override
    public double calculateTotal(List<CartItem> cartItems, double taxRate) {
        double subtotal = calculateSubtotal(cartItems);
        double tax = calculateTax(subtotal, taxRate);
        return subtotal + tax;
    }
    
    @Override
    public boolean canAddToCart(ProductRow product, int quantity, List<CartItem> cartItems) {
        if (product == null || quantity <= 0) {
            return false;
        }
        
        // Check current quantity in cart
        int currentCartQuantity = cartItems.stream()
                .filter(item -> item.getProductName().equals(product.getName()))
                .mapToInt(CartItem::getQuantity)
                .sum();
        
        int totalRequested = currentCartQuantity + quantity;
        return totalRequested <= product.getStock();
    }
}
