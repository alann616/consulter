package com.alann616.consulter.controller;

import com.alann616.consulter.enums.Gender;
import com.alann616.consulter.model.Patient;
import com.alann616.consulter.service.PatientService;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxcore.utils.converters.DateStringConverter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class PatientFormController {
    @Autowired PatientService patientService;

    @Setter
    private Runnable onPatientSaved;

    @FXML
    private Label lblPatId;

    @FXML
    private MFXTextField txtName, txtLastName, txtSecondLastName;

    @FXML
    private MFXComboBox<Gender> cbxGender;

    @FXML
    private MFXDatePicker dtpBirthDate;

    @FXML
    private MFXTextField txtPhone, txtEmail, txtAddress;

    @FXML
    private MFXButton btnSave, btnCancel;

    private Patient currentPatient;

    @FXML
    public void initialize() {
        fillGenderCombo();
        configureGenderComboBox(cbxGender);

        configureDatePicker(dtpBirthDate); // Configurar el DatePicker
    }

    // Método para configurar el formato del DatePicker
    private void configureDatePicker(MFXDatePicker datePicker) {
        datePicker.setConverterSupplier(() -> new DateStringConverter(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    //Rellenar combo de género
    private void fillGenderCombo() {
        cbxGender.getItems().addAll(Gender.values());
    }

    private void configureGenderComboBox(MFXComboBox<Gender> genderComboBox) {
        // 1. Asegurarse de que sea editable
        genderComboBox.setAllowEdit(true); // ¡Esta línea es correcta y necesaria!

        // Creamos el converter y lo guardamos en una variable para usarlo en setOnCommit
        StringConverter<Gender> genderConverter = new StringConverter<Gender>() {
            @Override
            public String toString(Gender gender) {
                // Cómo se muestra el valor seleccionado en el campo y la lista
                if (gender == null) {
                    return "";
                }
                return String.valueOf(gender);
            }

            @Override
            public Gender fromString(String string) {
                // Cómo convertir el texto que el usuario escribe ("M" o "F") de vuelta al enum Gender
                if (string == null || string.trim().isEmpty()) {
                    return null; // No hay texto, no hay enum
                }

                // Convertir a mayúsculas para que sea insensible a M/m o F/f
                String upperCaseString = string.trim().toUpperCase();

                // Comparar con "M" o "F"
                if ("M".equals(upperCaseString) || "MASCULINO".equals(upperCaseString)) {
                    return Gender.M; // Devuelve el enum Masculino
                } else if ("F".equals(upperCaseString) || "FEMENINO".equals(upperCaseString)) {
                    return Gender.F; // Devuelve el enum Femenino
                } else {
                    // Si no es ni "M" ni "F", loguear un error (opcional)
                    System.err.println("Input must be 'M' or 'F'. Typed: " + string);
                    // Devolver null significa que la conversión falló
                    return null;
                }
            }
        };

        // 2. Asignar el StringConverter
        genderComboBox.setConverter(genderConverter);

        // 3. **SOBRESCRIBIR LA ACCIÓN onCommit** para controlar la conversión manualmente
        genderComboBox.setOnCommit(text -> {
            System.out.println("onCommit triggered for Gender ComboBox with text: '" + text + "'"); // Debugging
            Gender selectedGender = genderConverter.fromString(text); // Usar el converter para intentar parsear

            if (selectedGender != null) {
                // Si el parseo fue exitoso, establecer el valor del ComboBox
                genderComboBox.setValue(selectedGender);
                System.out.println("Successfully parsed '" + text + "' to " + selectedGender + ". Value set."); // Debugging
            } else {
                // Si el parseo falló (texto no fue "M" o "F"), no se establece el valor.
                // Opcionalmente, puedes querer resetear el texto del campo a lo que había antes,
                // o mostrar un mensaje de error visual al usuario.
                System.out.println("Parsing failed for text: '" + text + "'. Value remains " + genderComboBox.getValue()); // Debugging
                // Resetear el texto a la representación del valor actual
                Platform.runLater(() -> { // Asegurarse de que la actualización de UI ocurra en el hilo de JavaFX
                    genderComboBox.setText(genderConverter.toString(genderComboBox.getValue()));
                });
            }
        });

        // Opcional: Configurar onCancel si quieres un comportamiento específico
        // cuando el usuario presiona Esc o Ctrl+Shift+Z (por defecto en MFXTextField)
        // genderComboBox.setOnCancel(text -> {
        //     System.out.println("onCancel triggered. Text: " + text);
        //     // Resetear el texto a la representación del valor actual
        //     Platform.runLater(() -> {
        //          genderComboBox.setText(genderConverter.toString(genderComboBox.getValue()));
        //     });
        // });
    }

    // Método consolidado para guardar (nuevo) o actualizar (existente) un paciente
    @FXML // Asumiendo que btnSave está conectado a este método en tu archivo FXML
    private void savePatient() {
        // 1. Validar campos y recopilar datos en un objeto Patient
        Patient patientToSave = validateFields();

        // 2. Verificar si la validación fue exitosa
        if (patientToSave != null) {
            try {
                // 3. Llamar al servicio. Asumimos que patientService.savePatient
                //    maneja internamente si es INSERT (si patientToSave.getPatientId es null)
                //    o UPDATE (si patientToSave.getPatientId está presente).
                patientService.savePatient(patientToSave);
                System.out.println("Paciente guardado/actualizado con éxito.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Paciente guardado/actualizado con éxito.");
                alert.showAndWait();

                // 4. Ejecutar el callback (ej. para refrescar la tabla en la vista principal)
                if (onPatientSaved != null) {
                    Platform.runLater(onPatientSaved); // Ejecutar en el hilo de JavaFX
                }

                // 5. Cerrar el formulario
                Stage form = (Stage) btnSave.getScene().getWindow();
                form.close();

            } catch (Exception e) {
                // Manejar posibles errores del servicio (ej. problemas de base de datos)
                e.printStackTrace(); // Imprimir stack trace para depuración
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error del Servicio");
                alert.setHeaderText("Ocurrió un error al guardar el paciente");
                alert.setContentText("Detalle: " + e.getMessage());
                alert.showAndWait();
            }

        } else {
            // 6. Mostrar alerta si la validación de campos falla
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al guardar");
            alert.setHeaderText("Por favor, complete todos los campos obligatorios.");
            alert.showAndWait();
        }
    }

    // Método para validar los campos del formulario y crear/poblar el objeto Patient
    private Patient validateFields() {
        // Recopilar y limpiar datos de los campos
        String name = txtName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String secondLastName = txtSecondLastName.getText().trim();
        Gender gender = cbxGender.getValue(); // Obtiene el valor del ComboBox (null si no hay selección/entrada válida)
        LocalDate birthDate = dtpBirthDate.getValue(); // Obtiene el valor del DatePicker (null si no hay selección/entrada válida)
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();


        // Verificar campos obligatorios
        if (name.isEmpty() || lastName.isEmpty() || secondLastName.isEmpty() ||
                gender == null || birthDate == null) {
            System.out.println("Validation failed: Required fields are empty.");
            return null; // Validación fallida
        }

        // Si los campos obligatorios están completos, crear/poblar el objeto Patient
        Patient patient = new Patient();

        // === Lógica para determinar si es UPDATE o NEW ===
        // Verificamos si la etiqueta de ID es visible y contiene un ID válido
        if (lblPatId.isVisible() && !lblPatId.getText().isEmpty() && lblPatId.getText().startsWith("ID: #")) {
            try {
                // Intentamos parsear el ID de la etiqueta
                long patientId = Long.parseLong(lblPatId.getText().replace("ID: #", ""));
                patient.setPatientId(patientId); // Asignar ID al objeto para indicar actualización
                System.out.println("Validation successful. Identified as UPDATE for ID: " + patientId);

                // Opcional: Si tu servicio necesita el timestamp original para la actualización,
                // lo copias desde el 'currentPatient' almacenado (si coincide el ID).
                if (this.currentPatient != null && this.currentPatient.getPatientId() != null && this.currentPatient.getPatientId().equals(patientId)) {
                    patient.setTimestamp(this.currentPatient.getTimestamp());
                }

            } catch (NumberFormatException e) {
                // Error al parsear el ID de la etiqueta - esto es un estado inesperado en modo edición
                System.err.println("Error parsing Patient ID from label during validation: " + lblPatId.getText());
                // Podrías mostrar un error específico al usuario o simplemente fallar la validación
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error interno");
                alert.setHeaderText("No se pudo leer el ID del paciente.");
                alert.setContentText("Por favor, reporte este problema.");
                alert.showAndWait();
                return null; // Tratar como fallo de validación
            }
        } else {
            System.out.println("Validation successful. Identified as NEW patient.");
            // patient.setPatientId seguirá siendo null, lo que indica al servicio que es un nuevo registro
        }
        // ===============================================


        // Asignar los demás campos al objeto Patient
        patient.setName(name);
        patient.setLastName(lastName);
        patient.setSecondLastName(secondLastName); // Corregido si era un typo 'secondName'
        patient.setGender(gender);
        patient.setBirthDate(birthDate);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setAddress(address);

        return patient; // Validación exitosa, devuelve el objeto Patient poblado
    }

    // Método para cargar datos de un paciente existente en el formulario (para edición)
    public void setPatientData(Patient patient) {
        this.currentPatient = patient; // Almacenar la referencia al paciente original

        // Poblar los campos del formulario en el hilo de JavaFX
        Platform.runLater(() -> {
            lblPatId.setVisible(true); // Mostrar la etiqueta de ID
            lblPatId.setText("ID: #" + patient.getPatientId()); // Mostrar el ID
            txtName.setText(patient.getName());
            txtLastName.setText(patient.getLastName());
            txtSecondLastName.setText(patient.getSecondLastName());
            cbxGender.setValue(patient.getGender()); // Establecer el valor del ComboBox
            dtpBirthDate.setValue(patient.getBirthDate()); // Establecer el valor del DatePicker
            txtPhone.setText(patient.getPhone());
            txtEmail.setText(patient.getEmail());
            txtAddress.setText(patient.getAddress());
            System.out.println("Formulario poblado con datos para edición del paciente ID: " + patient.getPatientId());
        });
    }


    @FXML
    private void cancel() {
        // Cerrar formulario
        Stage form = (Stage) btnCancel.getScene().getWindow();
        form.close();
    }

}
