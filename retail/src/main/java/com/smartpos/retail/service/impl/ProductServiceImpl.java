package com.smartpos.retail.service.impl;

import com.smartpos.retail.model.ProductRow;
import com.smartpos.retail.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductServiceImpl implements ProductService {
    
    private final List<ProductRow> products;
    
    public ProductServiceImpl() {
        // Initialize with sample data - will be replaced with database later
        this.products = new ArrayList<>();
        products.add(new ProductRow("Product 1", "1234567890123", 10.50, 100));
        products.add(new ProductRow("Product 2", "1234567890124", 25.00, 50));
        products.add(new ProductRow("Product 3", "1234567890125", 5.75, 200));
        products.add(new ProductRow("Product 4", "1234567890126", 15.25, 75));
        products.add(new ProductRow("Product 5", "1234567890127", 8.90, 150));
    }
    
    @Override
    public List<ProductRow> getAllProducts() {
        return new ArrayList<>(products);
    }
    
    @Override
    public List<ProductRow> searchProducts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllProducts();
        }
        
        String lowerSearchText = searchText.toLowerCase().trim();
        return products.stream()
                .filter(product -> product.getName().toLowerCase().contains(lowerSearchText))
                .collect(Collectors.toList());
    }
    
    @Override
    public ProductRow getProductByName(String productName) {
        if (productName == null) {
            return null;
        }
        
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public boolean hasSufficientStock(String productName, int requestedQuantity) {
        ProductRow product = getProductByName(productName);
        if (product == null) {
            return false;
        }
        return product.getStock() >= requestedQuantity;
    }
    
    @Override
    public int getAvailableStock(String productName) {
        ProductRow product = getProductByName(productName);
        return product != null ? product.getStock() : 0;
    }
    
    @Override
    public ProductRow getProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        
        return products.stream()
                .filter(product -> barcode.equals(product.getBarcode()))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public ProductRow createProduct(ProductRow product) {
        if (product == null) {
            throw new IllegalArgumentException("المنتج لا يمكن أن يكون فارغاً");
        }
        
        // Check if product with same name or barcode already exists
        if (getProductByName(product.getName()) != null) {
            throw new IllegalArgumentException("منتج بنفس الاسم موجود بالفعل");
        }
        
        if (product.getBarcode() != null && getProductByBarcode(product.getBarcode()) != null) {
            throw new IllegalArgumentException("منتج بنفس الباركود موجود بالفعل");
        }
        
        products.add(product);
        return product;
    }
    
    @Override
    public ProductRow updateProduct(String productName, ProductRow updatedProduct) {
        if (productName == null || updatedProduct == null) {
            throw new IllegalArgumentException("البيانات غير صحيحة");
        }
        
        ProductRow existingProduct = getProductByName(productName);
        if (existingProduct == null) {
            return null;
        }
        
        // Update product properties
        int index = products.indexOf(existingProduct);
        if (index != -1) {
            // Check if new name conflicts with another product
            if (!productName.equals(updatedProduct.getName())) {
                if (getProductByName(updatedProduct.getName()) != null) {
                    throw new IllegalArgumentException("منتج بنفس الاسم موجود بالفعل");
                }
            }
            
            // Check if new barcode conflicts with another product
            if (updatedProduct.getBarcode() != null) {
                ProductRow existingByBarcode = getProductByBarcode(updatedProduct.getBarcode());
                if (existingByBarcode != null && !existingByBarcode.getName().equals(productName)) {
                    throw new IllegalArgumentException("منتج بنفس الباركود موجود بالفعل");
                }
            }
            
            products.set(index, updatedProduct);
            return updatedProduct;
        }
        
        return null;
    }
    
    @Override
    public boolean deleteProduct(String productName) {
        if (productName == null) {
            return false;
        }
        
        ProductRow product = getProductByName(productName);
        if (product != null) {
            return products.remove(product);
        }
        return false;
    }
    
    @Override
    public int getTotalProductCount() {
        return products.size();
    }
    
    @Override
    public double getTotalStockValue() {
        return products.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
    }
    
    @Override
    public List<ProductRow> getLowStockProducts(int threshold) {
        return products.stream()
                .filter(product -> product.getStock() < threshold)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean reduceStock(String productName, int quantity) {
        if (productName == null || quantity <= 0) {
            return false;
        }
        
        ProductRow product = getProductByName(productName);
        if (product == null) {
            return false;
        }
        
        int currentStock = product.getStock();
        if (currentStock < quantity) {
            throw new IllegalArgumentException(
                "المخزون غير كافي. المتاح: " + currentStock + ", المطلوب: " + quantity
            );
        }
        
        int newStock = currentStock - quantity;
        product.setStock(newStock);
        return true;
    }
}
