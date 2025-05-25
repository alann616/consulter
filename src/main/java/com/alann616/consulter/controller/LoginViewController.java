package com.alann616.consulter.controller;

import com.alann616.consulter.Main;
import com.alann616.consulter.model.User;
import com.alann616.consulter.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginViewController {
    @Autowired
    private UserService userService;

    @FXML
    private HBox customBar;
    @FXML
    private RadioButton btnCarlos, btnVicente;
    @FXML
    private ToggleGroup userGroup;
    @FXML
    private Button btnLogin, btnMinimize, btnClose;

    private double xOffset = 0, yOffset = 0;

    @FXML
    public void initialize() {

    }

    @FXML
    private Long choice(ActionEvent event){
        if (btnCarlos.isSelected()){
            System.out.println("Carlos");
            return 11810523L;
        } else if (btnVicente.isSelected()) {
            System.out.println("Vicente");
            return 729376L;
        }
        return null;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        Long license = choice(event);

        if (license == null) {
            showAlert("Error", "Por favor, seleccione un usuario");
            return;
        }

        User user = userService.getUserById(license);

        Stage oldStage = (Stage) btnLogin.getScene().getWindow();
        oldStage.close(); // Cerrar la ventana de login

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/MainView.fxml"));
            fxmlLoader.setControllerFactory(Main.context::getBean);

            Parent root = fxmlLoader.load();
            MainViewController mainViewController = fxmlLoader.getController();
            mainViewController.setLoggedUser(user);

            Scene scene = new Scene(root);

            // Crear una nueva ventana con la barra nativa
            Stage newStage = new Stage();
            newStage.setTitle("Consultorio Martínez");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Métodos para minimizar y cerrar la ventana
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) customBar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) customBar.getScene().getWindow();
        stage.setIconified(true);
    }

    //Métodos para arrastrar la ventana
    @FXML
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        Stage stage = (Stage) customBar.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }


    //Manejo de errores
    public void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
