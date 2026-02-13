package com.smartpos.retail.controllers;

import com.smartpos.retail.model.User;
import com.smartpos.retail.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import java.io.IOException;

@Controller
public class MainController {

    private final ApplicationContext springContext;
    private final UserService userService;

    @FXML
    private StackPane contentArea;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private Button inventoryButton;
    
    @FXML
    private Label userInfoLabel;

    @Autowired
    public MainController(ApplicationContext springContext, UserService userService) {
        this.springContext = springContext;
        this.userService = userService;
    }
    
    @FXML
    public void initialize() {
        // Update UI based on user role
        updateUIForUserRole();
        
        // Show home view on startup
        if (contentArea != null) {
            showHomeView();
        }
    }
    
    private void updateUIForUserRole() {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser != null) {
            // Display user info
            if (userInfoLabel != null) {
                userInfoLabel.setText("المستخدم: " + currentUser.getFullName() + 
                                    " (" + currentUser.getRole().getArabicName() + ")");
            }
            
            // Show/hide inventory button based on role
            if (inventoryButton != null) {
                inventoryButton.setVisible(userService.canAccessInventory());
                inventoryButton.setManaged(userService.canAccessInventory());
            }
        }
    }

    @FXML
    public void showDashboardView() {
        loadView("/fxml/dashboard-view.fxml");
    }
    
    @FXML
    public void showSalesView() {
        loadView("/fxml/sales-view.fxml");
    }
    
    @FXML
    public void showInventoryView() {
        // Check permission
        if (!userService.canAccessInventory()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.WARNING
            );
            alert.setTitle("غير مصرح");
            alert.setHeaderText("ليس لديك صلاحية");
            alert.setContentText("فقط المدير يمكنه الوصول للمخزون");
            alert.showAndWait();
            return;
        }
        
        loadView("/fxml/inventory-view.fxml");
    }
    
    public void showHomeView() {
        loadView("/fxml/home-view.fxml");
    }

    @FXML
    public void handleLogout() {
        // Logout user
        userService.logout();
        
        // Return to login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent loginView = loader.load();
            
            javafx.scene.Node contentArea = this.contentArea.getScene().getRoot();
            if (contentArea.getScene() != null) {
                contentArea.getScene().setRoot(loginView);
            }
        } catch (IOException e) {
            System.err.println("Error loading login view: " + e.getMessage());
            System.exit(0);
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }
}