package com.smartpos.retail.controllers;

import com.smartpos.retail.model.ProductRow;
import com.smartpos.retail.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DashboardController implements Initializable {

    private final ApplicationContext springContext;
    private final ProductService productService;
    
    @Autowired
    public DashboardController(ApplicationContext springContext, ProductService productService) {
        this.springContext = springContext;
        this.productService = productService;
    }
    
    @FXML
    private Label totalProductsLabel;
    
    @FXML
    private Label totalStockValueLabel;
    
    @FXML
    private Label lowStockLabel;
    
    @FXML
    private Label averagePriceLabel;
    
    @FXML
    private TableView<ProductRow> lowStockTable;
    
    @FXML
    private TableColumn<ProductRow, String> lowStockNameColumn;
    
    @FXML
    private TableColumn<ProductRow, String> lowStockBarcodeColumn;
    
    @FXML
    private TableColumn<ProductRow, Double> lowStockPriceColumn;
    
    @FXML
    private TableColumn<ProductRow, Integer> lowStockStockColumn;
    
    private ObservableList<ProductRow> lowStockProducts;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupLowStockTable();
        refreshDashboard();
    }
    
    private void setupLowStockTable() {
        lowStockProducts = FXCollections.observableArrayList();
        
        lowStockNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lowStockBarcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        lowStockPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        lowStockStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        lowStockTable.setItems(lowStockProducts);
    }
    
    @FXML
    private void handleRefresh() {
        refreshDashboard();
    }
    
    private void refreshDashboard() {
        // Total products
        int totalProducts = productService.getTotalProductCount();
        totalProductsLabel.setText(String.valueOf(totalProducts));
        
        // Total stock value
        double totalStockValue = productService.getTotalStockValue();
        totalStockValueLabel.setText(String.format("%.2f ر.س", totalStockValue));
        
        // Low stock products
        List<ProductRow> lowStock = productService.getLowStockProducts(50);
        lowStockLabel.setText(String.valueOf(lowStock.size()));
        lowStockProducts.clear();
        lowStockProducts.addAll(lowStock);
        
        // Average price
        List<ProductRow> allProducts = productService.getAllProducts();
        double averagePrice = allProducts.isEmpty() ? 0.0 :
            allProducts.stream()
                .mapToDouble(ProductRow::getPrice)
                .average()
                .orElse(0.0);
        averagePriceLabel.setText(String.format("%.2f ر.س", averagePrice));
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent homeView = loader.load();
            
            javafx.scene.Node contentArea = totalProductsLabel.getScene().lookup("#contentArea");
            if (contentArea instanceof javafx.scene.layout.StackPane) {
                ((javafx.scene.layout.StackPane) contentArea).getChildren().setAll(homeView);
            }
        } catch (IOException e) {
            System.err.println("Error loading home view: " + e.getMessage());
        }
    }
}
