package com.alann616.consulter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Lanza Spring Boot primero
        context = SpringApplication.run(Main.class, args);
        // Luego lanza JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargamos el archivo FXML con el contexto de Spring
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/LoginView.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);

        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
