package com.smartpos.retail.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model class representing an item in the shopping cart
 */
public class CartItem {
    private final SimpleStringProperty productName;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;
    private final SimpleDoubleProperty total;
    
    public CartItem(String productName, int quantity, double price) {
        this.productName = new SimpleStringProperty(productName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.total = new SimpleDoubleProperty(quantity * price);
    }
    
    public String getProductName() { 
        return productName.get(); 
    }
    
    public int getQuantity() { 
        return quantity.get(); 
    }
    
    public double getPrice() { 
        return price.get(); 
    }
    
    public double getTotal() { 
        return total.get(); 
    }
    
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        this.total.set(quantity * price.get());
    }
    
    public SimpleStringProperty productNameProperty() {
        return productName;
    }
    
    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }
    
    public SimpleDoubleProperty priceProperty() {
        return price;
    }
    
    public SimpleDoubleProperty totalProperty() {
        return total;
    }
}
