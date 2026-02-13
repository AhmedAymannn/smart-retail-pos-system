package com.smartpos.retail;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RetailApplication extends Application {

	private ConfigurableApplicationContext springContext;

	@Override
	public void init() {
		// Start Spring Boot
		springContext = new SpringApplicationBuilder(RetailApplication.class).run();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Start with login screen
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));

		// Link JavaFX to Spring so @Autowired works in Controllers
		loader.setControllerFactory(springContext::getBean);

		primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("نظام نقاط البيع الذكية - تسجيل الدخول");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	@Override
	public void stop() {
		springContext.close();
	}

	public static void main(String[] args) {
		// This is called by MainLauncher
		Application.launch(RetailApplication.class, args);
	}
}