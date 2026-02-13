package com.smartpos.retail.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Model class representing a sales receipt
 */
public class Receipt {
    private String transactionId;
    private LocalDateTime saleDate;
    private List<ReceiptItem> items;
    private double subtotal;
    private double tax;
    private double total;
    
    public Receipt(String transactionId, LocalDateTime saleDate, List<ReceiptItem> items, 
                   double subtotal, double tax, double total) {
        this.transactionId = transactionId;
        this.saleDate = saleDate;
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public LocalDateTime getSaleDate() {
        return saleDate;
    }
    
    public List<ReceiptItem> getItems() {
        return items;
    }
    
    public double getSubtotal() {
        return subtotal;
    }
    
    public double getTax() {
        return tax;
    }
    
    public double getTotal() {
        return total;
    }
    
    /**
     * Generate formatted receipt text
     */
    public String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        receipt.append("═══════════════════════════════════\n");
        receipt.append("         إيصال بيع\n");
        receipt.append("         Sales Receipt\n");
        receipt.append("═══════════════════════════════════\n");
        receipt.append("التاريخ / Date: ").append(saleDate.format(dateFormatter)).append("\n");
        receipt.append("رقم المعاملة / Transaction ID: ").append(transactionId).append("\n");
        receipt.append("───────────────────────────────────\n");
        receipt.append("المنتج              الكمية    السعر    الإجمالي\n");
        receipt.append("Product            Qty    Price    Total\n");
        receipt.append("───────────────────────────────────\n");
        
        for (ReceiptItem item : items) {
            String productName = item.getProductName();
            if (productName.length() > 20) {
                productName = productName.substring(0, 17) + "...";
            }
            receipt.append(String.format("%-20s %4d  %7.2f  %8.2f\n",
                productName,
                item.getQuantity(),
                item.getPrice(),
                item.getTotal()));
        }
        
        receipt.append("───────────────────────────────────\n");
        receipt.append(String.format("المجموع الفرعي / Subtotal: %20.2f ر.س\n", subtotal));
        receipt.append(String.format("الضريبة / Tax: %27.2f ر.س\n", tax));
        receipt.append("───────────────────────────────────\n");
        receipt.append(String.format("الإجمالي / Total: %25.2f ر.س\n", total));
        receipt.append("═══════════════════════════════════\n");
        receipt.append("         شكراً لزيارتك\n");
        receipt.append("        Thank You!\n");
        receipt.append("═══════════════════════════════════\n");
        
        return receipt.toString();
    }
    
    /**
     * Inner class for receipt items
     */
    public static class ReceiptItem {
        private String productName;
        private int quantity;
        private double price;
        private double total;
        
        public ReceiptItem(String productName, int quantity, double price, double total) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public double getPrice() {
            return price;
        }
        
        public double getTotal() {
            return total;
        }
    }
}
