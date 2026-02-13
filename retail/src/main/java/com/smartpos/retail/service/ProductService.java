package com.smartpos.retail.service;

import com.smartpos.retail.model.ProductRow;
import java.util.List;

/**
 * Service interface for product-related operations
 */
public interface ProductService {
    
    /**
     * Get all available products
     * @return List of all products
     */
    List<ProductRow> getAllProducts();
    
    /**
     * Search products by name
     * @param searchText The search text
     * @return List of matching products
     */
    List<ProductRow> searchProducts(String searchText);
    
    /**
     * Get product by name
     * @param productName The product name
     * @return Product if found, null otherwise
     */
    ProductRow getProductByName(String productName);
    
    /**
     * Check if product has sufficient stock
     * @param productName The product name
     * @param requestedQuantity The requested quantity
     * @return true if stock is sufficient, false otherwise
     */
    boolean hasSufficientStock(String productName, int requestedQuantity);
    
    /**
     * Get available stock for a product
     * @param productName The product name
     * @return Available stock quantity
     */
    int getAvailableStock(String productName);
    
    /**
     * Get product by barcode
     * @param barcode The product barcode
     * @return Product if found, null otherwise
     */
    ProductRow getProductByBarcode(String barcode);
    
    /**
     * Create a new product
     * @param product The product to create
     * @return Created product
     */
    ProductRow createProduct(ProductRow product);
    
    /**
     * Update an existing product
     * @param productName The name of product to update
     * @param product Updated product data
     * @return Updated product, null if not found
     */
    ProductRow updateProduct(String productName, ProductRow product);
    
    /**
     * Delete a product
     * @param productName The name of product to delete
     * @return true if deleted, false if not found
     */
    boolean deleteProduct(String productName);
    
    /**
     * Get total number of products
     * @return Total product count
     */
    int getTotalProductCount();
    
    /**
     * Get total stock value
     * @return Total value of all stock
     */
    double getTotalStockValue();
    
    /**
     * Get low stock products (stock < threshold)
     * @param threshold The stock threshold
     * @return List of products with low stock
     */
    List<ProductRow> getLowStockProducts(int threshold);
    
    /**
     * Reduce product stock by specified quantity
     * @param productName The product name
     * @param quantity The quantity to reduce
     * @return true if stock was reduced successfully, false otherwise
     */
    boolean reduceStock(String productName, int quantity);
}
