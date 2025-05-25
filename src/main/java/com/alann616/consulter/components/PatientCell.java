package com.alann616.consulter.components;

import com.alann616.consulter.controller.MainViewController;
import com.alann616.consulter.controller.PatientFormController;
import com.alann616.consulter.model.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Optional;

public class PatientCell extends ListCell<Patient> {
    @FXML private HBox boxPatientItem;
    @FXML private Circle circleInitials;
    @FXML private Label lblPatientInitials;
    @FXML private Label lblPatientName;
    @FXML private Label lblPatientPhone;
    @FXML private MenuItem btnDeletePatient;
    @FXML private MenuItem btnUpdatePatient;

    private FXMLLoader loader;
    private MainViewController mainViewController;
    private PatientFormController patientFormController;


    public PatientCell(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        this.loader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/PatientItem.fxml"));
        loader.setController(this);

        try {
            Parent root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Patient patient, boolean empty) {
        super.updateItem(patient, empty);

        if (empty || patient == null) {
            setGraphic(null);
            return;
        }

        // Generate patient initials
        StringBuilder initials = new StringBuilder();
        Optional.ofNullable(patient.getName()).filter(s -> !s.isEmpty()).ifPresent(s -> initials.append(s.charAt(0)));
        Optional.ofNullable(patient.getLastName()).filter(s -> !s.isEmpty()).ifPresent(s -> initials.append(s.charAt(0)));


        // Configure UI values
        lblPatientInitials.setText(initials.toString().toUpperCase());
        lblPatientName.setText(
                String.format("%s %s %s",
                        Optional.ofNullable(patient.getName()).orElse(""),
                        Optional.ofNullable(patient.getLastName()).orElse(""),
                        Optional.ofNullable(patient.getSecondLastName()).orElse("")
                ).trim()
        );
        lblPatientPhone.setText(Optional.ofNullable(patient.getPhone()).orElse("N/A"));

        setGraphic(boxPatientItem);
    }

    // Method to delete a patient and show the confirmation alert
// En PatientCell.java
    @FXML
    void deletePatient(ActionEvent event) {
        Patient selectedPatient = getItem();
        if (selectedPatient == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // ... (configuración del alert) ...

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Llama al servicio para eliminar
                mainViewController.getPatientService().deletePatient(selectedPatient.getPatientId());

                // Refresca la lista de la UI SOLO si la eliminación fue exitosa (no lanzó excepción)
                mainViewController.refreshPatientList(); // Esta llamada ahora pide datos frescos post-commit

            } catch (Exception e) {
                // Mostrar error al usuario si la eliminación falló
                System.err.println("Error al intentar eliminar paciente desde UI: " + e.getMessage());
                e.printStackTrace(); // Log completo en consola
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de Eliminación");
                errorAlert.setHeaderText("No se pudo eliminar el paciente.");
                // Mostrar un mensaje más amigable, quizás sin el detalle técnico
                errorAlert.setContentText("Ocurrió un error al intentar eliminar al paciente y/o sus documentos asociados. Verifique que no tenga registros pendientes o contacte al administrador.");
                // errorAlert.setContentText("Detalle: " + e.getMessage()); // O mostrar detalle si prefieres
                errorAlert.showAndWait();
            }
        }
    }

    // Edit the patient details
    @FXML
    public void updatePatient(ActionEvent event) {
        Patient selectedPatient = getItem();
        if (selectedPatient == null) return;

        mainViewController.updatePatient(selectedPatient);
    }
}