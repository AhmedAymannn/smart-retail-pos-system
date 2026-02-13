package com.smartpos.retail.controllers;

import com.smartpos.retail.model.CartItem;
import com.smartpos.retail.model.ProductRow;
import com.smartpos.retail.service.CartService;
import com.smartpos.retail.service.ProductService;
import com.smartpos.retail.service.SalesService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Controller
public class SalesController implements Initializable {

    private final ApplicationContext springContext;
    private final ProductService productService;
    private final CartService cartService;
    private final SalesService salesService;
    
    @Autowired
    public SalesController(ApplicationContext springContext, 
                        ProductService productService,
                        CartService cartService,
                        SalesService salesService) {
        this.springContext = springContext;
        this.productService = productService;
        this.cartService = cartService;
        this.salesService = salesService;
    }

    @FXML
    private TextField barcodeField;
    
    @FXML
    private Label dateLabel;
    
    @FXML
    private Label lastScannedLabel;
    
    @FXML
    private VBox recentScansArea;
    
    @FXML
    private TableView<CartItem> cartTable;
    
    @FXML
    private TableColumn<CartItem, String> cartProductColumn;
    
    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;
    
    @FXML
    private TableColumn<CartItem, Double> cartPriceColumn;
    
    @FXML
    private TableColumn<CartItem, Double> cartTotalColumn;
    
    @FXML
    private TableColumn<CartItem, String> cartRemoveColumn;
    
    @FXML
    private Label subtotalLabel;
    
    @FXML
    private Label taxLabel;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private Button clearCartButton;
    
    @FXML
    private Button checkoutButton;
    
    @FXML
    private Label statusLabel;
    
    private ObservableList<CartItem> cartItems;
    private static final double TAX_RATE = 0.0; // Can be configured later
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDateLabel();
        initializeCart();
        setupBarcodeScanner();
        
        // Focus on barcode field for immediate scanning
        Platform.runLater(() -> barcodeField.requestFocus());
    }
    
    private void setupDateLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dateLabel.setText(LocalDateTime.now().format(formatter));
    }
    
    private void setupBarcodeScanner() {
        // Listen for Enter key (barcode scanners send Enter after scanning)
        barcodeField.setOnKeyPressed(this::handleBarcodeInput);
    }
    
    private void handleBarcodeInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String barcode = barcodeField.getText().trim();
            if (!barcode.isEmpty()) {
                processBarcode(barcode);
            }
            event.consume();
        }
    }
    
    private void processBarcode(String barcode) {
        // Find product by barcode
        ProductRow product = productService.getProductByBarcode(barcode);
        
        if (product == null) {
            // Try searching by name as fallback
            product = productService.getProductByName(barcode);
        }
        
        if (product == null) {
            showStatusMessage("المنتج غير موجود", true);
            barcodeField.clear();
            Platform.runLater(() -> barcodeField.requestFocus());
            return;
        }
        
        // Add product to cart (quantity 1 by default)
        try {
            cartService.addToCart(product, 1, cartItems);
            
            // Update UI
            updateTotals();
            updateRecentScans(product);
            showStatusMessage("تم الإضافة: " + product.getName(), false);
            
            // Clear barcode field and refocus for next scan
            barcodeField.clear();
            Platform.runLater(() -> barcodeField.requestFocus());
            
        } catch (IllegalArgumentException e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("Insufficient stock") || errorMsg.contains("المخزون غير كافي")) {
                    errorMsg = "المخزون غير كافي";
                } else if (errorMsg.contains("Quantity must be greater than 0") || errorMsg.contains("الكمية يجب أن تكون أكبر من صفر")) {
                    errorMsg = "الكمية يجب أن تكون أكبر من صفر";
                } else if (errorMsg.contains("Product cannot be null")) {
                    errorMsg = "المنتج غير صالح";
                } else {
                    errorMsg = "خطأ في الإضافة";
                }
            } else {
                errorMsg = "خطأ غير معروف";
            }
            showStatusMessage("خطأ: " + errorMsg, true);
            barcodeField.clear();
            Platform.runLater(() -> barcodeField.requestFocus());
        }
    }
    
    private void updateRecentScans(ProductRow product) {
        lastScannedLabel.setText("آخر: " + product.getName() + " - " + String.format("%.2f", product.getPrice()) + " ر.س");
        lastScannedLabel.setVisible(true);
        
        // Add to recent scans area (keep last 5)
        if (recentScansArea.getChildren().size() >= 5) {
            recentScansArea.getChildren().remove(0);
        }
        
        HBox scanItem = new HBox(10);
        scanItem.setPadding(new Insets(5));
        scanItem.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1;");
        
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");
        Label priceLabel = new Label(String.format("%.2f", product.getPrice()) + " ر.س");
        priceLabel.setStyle("-fx-text-fill: #27ae60;");
        
        scanItem.getChildren().addAll(nameLabel, priceLabel);
        recentScansArea.getChildren().add(scanItem);
    }
    
    private void initializeCart() {
        cartItems = FXCollections.observableArrayList();
        
        cartProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        // Remove column with Remove button
        cartRemoveColumn.setCellFactory(column -> new TableCell<CartItem, String>() {
            private final Button removeButton = new Button("إزالة");
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CartItem cartItem = getTableView().getItems().get(getIndex());
                    removeButton.setOnAction(e -> {
                        cartService.removeFromCart(cartItems, cartItem.getProductName());
                        updateTotals();
                        showStatusMessage("تم إزالة العنصر", false);
                    });
                    removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    setGraphic(removeButton);
                }
            }
        });
        
        cartTable.setItems(cartItems);
        updateTotals();
    }
    
    @FXML
    private void handleClearCart() {
        if (cartItems.isEmpty()) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("مسح السلة");
        alert.setHeaderText("هل تريد مسح سلة التسوق؟");
        alert.setContentText("هل أنت متأكد من إزالة جميع العناصر من السلة؟");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cartService.clearCart(cartItems);
                updateTotals();
                recentScansArea.getChildren().clear();
                lastScannedLabel.setVisible(false);
                showStatusMessage("تم مسح السلة", false);
                Platform.runLater(() -> barcodeField.requestFocus());
            }
        });
    }
    
    @FXML
    private void handleCheckout() {
        if (!salesService.canProcessCheckout(cartItems)) {
            showAlert("السلة فارغة", "يرجى مسح العناصر قبل الدفع.");
            Platform.runLater(() -> barcodeField.requestFocus());
            return;
        }
        
        double total = cartService.calculateTotal(cartItems, TAX_RATE);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("الدفع");
        alert.setHeaderText("إتمام البيع؟");
        alert.setContentText("الإجمالي: " + String.format("%.2f", total) + " ر.س");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Process sale using service (with product service for stock update)
                    String transactionId = salesService.processSale(cartItems, TAX_RATE, productService);
                    
                    // Generate receipt
                    com.smartpos.retail.model.Receipt receipt = salesService.generateReceipt(
                        cartItems, 
                        TAX_RATE, 
                        transactionId
                    );
                    
                    showStatusMessage("تم إتمام البيع", false);
                    
                    // Clear cart after successful checkout
                    cartService.clearCart(cartItems);
                    recentScansArea.getChildren().clear();
                    lastScannedLabel.setVisible(false);
                    updateTotals();
                    
                    // Show receipt
                    showReceipt(receipt);
                    
                    // Refocus on barcode field for next sale
                    Platform.runLater(() -> barcodeField.requestFocus());
                } catch (IllegalStateException e) {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && errorMsg.contains("cart is empty")) {
                        errorMsg = "السلة فارغة";
                    }
                    showAlert("خطأ", errorMsg != null ? errorMsg : "حدث خطأ أثناء المعالجة");
                }
            }
        });
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent homeView = loader.load();
            
            // Find contentArea in the scene and replace content
            javafx.scene.Node contentArea = statusLabel.getScene().lookup("#contentArea");
            if (contentArea instanceof javafx.scene.layout.StackPane) {
                ((javafx.scene.layout.StackPane) contentArea).getChildren().setAll(homeView);
            }
        } catch (IOException e) {
            System.err.println("Error loading home view: " + e.getMessage());
            showStatusMessage("خطأ في تحميل الصفحة الرئيسية", true);
        }
    }
    
    private void updateTotals() {
        double subtotal = cartService.calculateSubtotal(cartItems);
        double tax = cartService.calculateTax(subtotal, TAX_RATE);
        double total = cartService.calculateTotal(cartItems, TAX_RATE);
        
        subtotalLabel.setText(String.format("%.2f", subtotal));
        taxLabel.setText(String.format("%.2f", tax));
        totalLabel.setText(String.format("%.2f", total));
    }
    
    private void showStatusMessage(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showReceipt(com.smartpos.retail.model.Receipt receipt) {
        Dialog<Void> receiptDialog = new Dialog<>();
        receiptDialog.setTitle("إيصال البيع");
        receiptDialog.setHeaderText("إيصال البيع / Sales Receipt");
        
        ButtonType printButton = new ButtonType("طباعة", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButton = new ButtonType("إغلاق", ButtonBar.ButtonData.CANCEL_CLOSE);
        receiptDialog.getDialogPane().getButtonTypes().addAll(printButton, closeButton);
        
        // Create scrollable text area for receipt
        TextArea receiptText = new TextArea();
        receiptText.setText(receipt.generateReceiptText());
        receiptText.setEditable(false);
        receiptText.setFont(javafx.scene.text.Font.font("Courier New", 12));
        receiptText.setPrefRowCount(25);
        receiptText.setPrefColumnCount(50);
        receiptText.setWrapText(false);
        
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.getChildren().add(receiptText);
        
        receiptDialog.getDialogPane().setContent(vbox);
        receiptDialog.getDialogPane().setPrefWidth(500);
        receiptDialog.getDialogPane().setPrefHeight(600);
        
        // Handle print button
        Button printBtn = (Button) receiptDialog.getDialogPane().lookupButton(printButton);
        printBtn.setOnAction(e -> {
            // Print receipt (you can implement actual printing here)
            Alert printAlert = new Alert(Alert.AlertType.INFORMATION);
            printAlert.setTitle("طباعة");
            printAlert.setHeaderText("تم إرسال الإيصال للطباعة");
            printAlert.setContentText("يمكنك نسخ النص وطباعته يدوياً");
            printAlert.showAndWait();
        });
        
        receiptDialog.showAndWait();
    }
}
