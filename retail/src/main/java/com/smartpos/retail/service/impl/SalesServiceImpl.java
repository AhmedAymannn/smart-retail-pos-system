package com.smartpos.retail.service.impl;

import com.smartpos.retail.model.CartItem;
import com.smartpos.retail.model.Receipt;
import com.smartpos.retail.service.CartService;
import com.smartpos.retail.service.ProductService;
import com.smartpos.retail.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of SalesService
 * TODO: Integrate with database to persist sales
 */
@Service
public class SalesServiceImpl implements SalesService {
    
    private final CartService cartService;
    
    @Autowired
    public SalesServiceImpl(CartService cartService) {
        this.cartService = cartService;
    }
    
    @Override
    public String processSale(List<CartItem> cartItems, double taxRate) {
        // This method is kept for backward compatibility
        // But it should not be used - use the one with ProductService instead
        throw new UnsupportedOperationException("Use processSale with ProductService parameter");
    }
    
    @Override
    public String processSale(List<CartItem> cartItems, double taxRate, ProductService productService) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot process sale: cart is empty");
        }
        
        // Calculate totals
        double subtotal = cartService.calculateSubtotal(cartItems);
        double tax = cartService.calculateTax(subtotal, taxRate);
        double total = cartService.calculateTotal(cartItems, taxRate);
        
        // Update product stock for each item in cart
        for (CartItem item : cartItems) {
            try {
                boolean stockReduced = productService.reduceStock(
                    item.getProductName(), 
                    item.getQuantity()
                );
                if (!stockReduced) {
                    throw new IllegalStateException(
                        "فشل تحديث المخزون للمنتج: " + item.getProductName()
                    );
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(
                    "خطأ في تحديث المخزون: " + e.getMessage()
                );
            }
        }
        
        // TODO: Save sale to database
        // TODO: Generate receipt
        
        // Generate transaction ID
        String transactionId = UUID.randomUUID().toString();
        
        // Log the sale (in production, this would be saved to database)
        System.out.println("Sale processed - Transaction ID: " + transactionId);
        System.out.println("Subtotal: " + subtotal);
        System.out.println("Tax: " + tax);
        System.out.println("Total: " + total);
        System.out.println("Stock updated for " + cartItems.size() + " items");
        
        return transactionId;
    }
    
    @Override
    public boolean canProcessCheckout(List<CartItem> cartItems) {
        return cartItems != null && !cartItems.isEmpty();
    }
    
    @Override
    public Receipt generateReceipt(List<CartItem> cartItems, double taxRate, String transactionId) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("لا يمكن إنشاء إيصال: السلة فارغة");
        }
        
        // Calculate totals
        double subtotal = cartService.calculateSubtotal(cartItems);
        double tax = cartService.calculateTax(subtotal, taxRate);
        double total = cartService.calculateTotal(cartItems, taxRate);
        
        // Convert cart items to receipt items
        List<Receipt.ReceiptItem> receiptItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Receipt.ReceiptItem receiptItem = new Receipt.ReceiptItem(
                cartItem.getProductName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                cartItem.getTotal()
            );
            receiptItems.add(receiptItem);
        }
        
        // Create receipt
        return new Receipt(
            transactionId,
            LocalDateTime.now(),
            receiptItems,
            subtotal,
            tax,
            total
        );
    }
}
