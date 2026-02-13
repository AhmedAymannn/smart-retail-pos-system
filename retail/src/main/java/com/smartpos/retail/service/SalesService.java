package com.smartpos.retail.service;

import com.smartpos.retail.model.CartItem;
import com.smartpos.retail.model.Receipt;
import java.util.List;

/**
 * Service interface for sales operations
 */
public interface SalesService {
    
    /**
     * Process a sale/checkout
     * @param cartItems The items in the cart
     * @param taxRate The tax rate to apply
     * @return Sale transaction ID (for future database implementation)
     * @throws IllegalStateException if cart is empty
     */
    String processSale(List<CartItem> cartItems, double taxRate);
    
    /**
     * Validate if checkout can be processed
     * @param cartItems The items in the cart
     * @return true if valid, false otherwise
     */
    boolean canProcessCheckout(List<CartItem> cartItems);
    
    /**
     * Process a sale/checkout with product service for stock updates
     * @param cartItems The items in the cart
     * @param taxRate The tax rate to apply
     * @param productService The product service to update stock
     * @return Sale transaction ID
     * @throws IllegalStateException if cart is empty
     */
    String processSale(List<CartItem> cartItems, double taxRate, ProductService productService);
    
    /**
     * Generate receipt for a sale
     * @param cartItems The items in the cart
     * @param taxRate The tax rate applied
     * @param transactionId The transaction ID
     * @return Receipt object
     */
    Receipt generateReceipt(List<CartItem> cartItems, double taxRate, String transactionId);
}
