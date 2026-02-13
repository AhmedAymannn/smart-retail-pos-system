package com.smartpos.retail.model;

import jakarta.persistence.*;
import javafx.beans.property.*;

@Entity
@Table(name = "products")
public class ProductRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use standard types for Hibernate
    private String name;
    private String barcode;
    private double price;
    private int stock;

    // 1. MANDATORY: Hibernate needs this empty constructor
    public ProductRow() {}

    // 2. Your constructor for adding new products
    public ProductRow(String name, String barcode, double price, int stock) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.stock = stock;
    }

    // 3. For JavaFX TableView: Create properties "on the fly"
    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public DoubleProperty priceProperty() {
        return new SimpleDoubleProperty(price);
    }

    public IntegerProperty stockProperty() {
        return new SimpleIntegerProperty(stock);
    }

    public StringProperty barcodeProperty() {
        return new SimpleStringProperty(barcode);
    }

    // Standard Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}