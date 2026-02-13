package com.smartpos.retail.controllers;

import com.smartpos.retail.model.User;
import com.smartpos.retail.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {

    private final ApplicationContext springContext;
    private final UserService userService;
    
    @Autowired
    public LoginController(ApplicationContext springContext, UserService userService) {
        this.springContext = springContext;
        this.userService = userService;
    }
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Allow login on Enter key
        passwordField.setOnKeyPressed(this::handleKeyPress);
        usernameField.setOnKeyPressed(this::handleKeyPress);
        
        // Focus on username field
        usernameField.requestFocus();
    }
    
    private void handleKeyPress(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
    @FXML
private void handleLogin() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText();
    
    if (username.isEmpty() || password.isEmpty()) {
        showError("يرجى إدخال اسم المستخدم وكلمة المرور");
        return;
    }
    
    User user = userService.authenticate(username, password);
    
    if (user == null) {
        showError("اسم المستخدم أو كلمة المرور غير صحيحة");
        passwordField.clear();
        return;
    }
    
    userService.setCurrentUser(user);
    
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-shell.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent mainView = loader.load();
        
        // 1. Get the current Stage (Window)
        javafx.stage.Stage stage = (javafx.stage.Stage) usernameField.getScene().getWindow();
        
        // 2. Set the new root
        usernameField.getScene().setRoot(mainView);
        
        // 3. THE FIX: Force the window to maximize and refresh
        stage.setMaximized(true); 
        
        // Optional: If you want to allow it to be resizable
        stage.setResizable(true);

    } catch (IOException e) {
        System.err.println("Error loading main view: " + e.getMessage());
        showError("خطأ في تحميل التطبيق");
    }
}
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
