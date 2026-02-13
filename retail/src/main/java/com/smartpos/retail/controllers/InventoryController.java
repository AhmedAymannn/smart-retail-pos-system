package com.smartpos.retail.controllers;

import com.smartpos.retail.model.ProductRow;
import com.smartpos.retail.service.ProductService;
import com.smartpos.retail.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class InventoryController implements Initializable {

    private final ApplicationContext springContext;
    private final ProductService productService;
    private final UserService userService;
    
    @Autowired
    public InventoryController(ApplicationContext springContext, ProductService productService, UserService userService) {
        this.springContext = springContext;
        this.productService = productService;
        this.userService = userService;
    }
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button addProductButton;
    
    @FXML
    private TableView<ProductRow> productsTable;
    
    @FXML
    private TableColumn<ProductRow, String> nameColumn;
    
    @FXML
    private TableColumn<ProductRow, String> barcodeColumn;
    
    @FXML
    private TableColumn<ProductRow, Double> priceColumn;
    
    @FXML
    private TableColumn<ProductRow, Integer> stockColumn;
    
    @FXML
    private TableColumn<ProductRow, String> actionsColumn;
    
    @FXML
    private Label statusLabel;
    
    private ObservableList<ProductRow> products;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if user has permission
        if (!userService.canAccessInventory()) {
            showAlert("غير مصرح", "فقط المدير يمكنه الوصول للمخزون");
            handleBack();
            return;
        }
        
        setupTable();
        loadProducts();
    }
    
    private void setupTable() {
        products = FXCollections.observableArrayList();
        
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        // Actions column with Edit and Delete buttons
        actionsColumn.setCellFactory(column -> new TableCell<ProductRow, String>() {
            private final Button editButton = new Button("تعديل");
            private final Button deleteButton = new Button("حذف");
            private final HBox buttonBox = new HBox(5);
            
            {
                editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                buttonBox.getChildren().addAll(editButton, deleteButton);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProductRow product = getTableView().getItems().get(getIndex());
                    editButton.setOnAction(e -> handleEditProduct(product));
                    deleteButton.setOnAction(e -> handleDeleteProduct(product));
                    setGraphic(buttonBox);
                }
            }
        });
        
        productsTable.setItems(products);
    }
    
    private void loadProducts() {
        List<ProductRow> productList = productService.getAllProducts();
        products.clear();
        products.addAll(productList);
        statusLabel.setText("تم تحميل " + products.size() + " منتج");
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        List<ProductRow> filtered = productService.searchProducts(searchText);
        
        products.clear();
        products.addAll(filtered);
        
        if (filtered.isEmpty() && !searchText.isEmpty()) {
            statusLabel.setText("لم يتم العثور على منتجات");
        } else {
            statusLabel.setText("تم العثور على " + filtered.size() + " منتج");
        }
    }
    
    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }
    
    private void handleEditProduct(ProductRow product) {
        showProductDialog(product);
    }
    
    private void handleDeleteProduct(ProductRow product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("تأكيد الحذف");
        alert.setHeaderText("حذف المنتج");
        alert.setContentText("هل أنت متأكد من حذف المنتج: " + product.getName() + "؟");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = productService.deleteProduct(product.getName());
                    if (deleted) {
                        loadProducts();
                        statusLabel.setText("تم حذف المنتج بنجاح");
                    } else {
                        showAlert("خطأ", "فشل حذف المنتج");
                    }
                } catch (Exception e) {
                    showAlert("خطأ", "حدث خطأ أثناء الحذف: " + e.getMessage());
                }
            }
        });
    }
    
    private void showProductDialog(ProductRow product) {
        Dialog<ProductRow> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "إضافة منتج جديد" : "تعديل منتج");
        
        ButtonType saveButtonType = new ButtonType("حفظ", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create form
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("اسم المنتج");
        TextField barcodeField = new TextField();
        barcodeField.setPromptText("الباركود");
        TextField priceField = new TextField();
        priceField.setPromptText("السعر");
        TextField stockField = new TextField();
        stockField.setPromptText("المخزون");
        
        if (product != null) {
            nameField.setText(product.getName());
            barcodeField.setText(product.getBarcode());
            priceField.setText(String.valueOf(product.getPrice()));
            stockField.setText(String.valueOf(product.getStock()));
        }
        
        grid.add(new Label("اسم المنتج:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("الباركود:"), 0, 1);
        grid.add(barcodeField, 1, 1);
        grid.add(new Label("السعر:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("المخزون:"), 0, 3);
        grid.add(stockField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/Disable save button
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDefaultButton(true);
        
        // Convert result to ProductRow
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String barcode = barcodeField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());
                    
                    if (name.isEmpty()) {
                        showAlert("خطأ", "اسم المنتج مطلوب");
                        return null;
                    }
                    
                    ProductRow newProduct = new ProductRow(name, barcode.isEmpty() ? null : barcode, price, stock);
                    
                    if (product == null) {
                        // Create new product
                        productService.createProduct(newProduct);
                        statusLabel.setText("تم إضافة المنتج بنجاح");
                    } else {
                        // Update existing product
                        productService.updateProduct(product.getName(), newProduct);
                        statusLabel.setText("تم تحديث المنتج بنجاح");
                    }
                    
                    loadProducts();
                    return newProduct;
                } catch (NumberFormatException e) {
                    showAlert("خطأ", "يرجى إدخال أرقام صحيحة للسعر والمخزون");
                    return null;
                } catch (IllegalArgumentException e) {
                    showAlert("خطأ", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent homeView = loader.load();
            
            javafx.scene.Node contentArea = statusLabel.getScene().lookup("#contentArea");
            if (contentArea instanceof javafx.scene.layout.StackPane) {
                ((javafx.scene.layout.StackPane) contentArea).getChildren().setAll(homeView);
            }
        } catch (IOException e) {
            System.err.println("Error loading home view: " + e.getMessage());
            statusLabel.setText("خطأ في تحميل الصفحة الرئيسية");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
