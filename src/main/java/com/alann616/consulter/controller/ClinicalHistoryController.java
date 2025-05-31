package com.alann616.consulter.controller;

import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.User;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.service.ClinicalHistoryService;
import com.alann616.consulter.model.doctordocs.historytables.*;
import com.alann616.consulter.util.PdfExporter;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class ClinicalHistoryController {
    @FXML
    private VBox vboxPage1, vboxPage2, vboxPage3;
    @Getter
    private Patient patient;
    @Getter
    private User doctor;
    private ClinicalHistory clinicalHistory;

    @Autowired
    private ClinicalHistoryService clinicalHistoryService;


    //================================================================================
    // Fields of hereditary antecedents table
    // @table: hereditary
    //================================================================================
    @FXML
    private MFXTextField txtDiabetes, txtHypertension, txtTB, txtNeoplasia, txtHeartDisease,
            txtBirthDisease, txtEndocrineDisorders, txtOtherHereditary;

    //================================================================================
    // Fields of non-pathological antecedents table
    // @table: non_pathological
    //================================================================================
    @FXML
    private ToggleGroup toggleMaritalStatus;
    @FXML
    private MFXRadioButton rbtnSingle, rbtnMarried, rbtnWidowded, rbtnHacinamiento;

    @FXML
    private MFXTextField txtReligion, txtOcupation;

    @FXML
    private MFXCheckbox cboxDirt, cboxCement,
            cboxBlock, cboxAdobe, cboxWood, cboxOtherWalls,
            cboxWater, cboxDrainage, cboxGas, cboxLenia;

    @FXML
    private MFXToggleButton toggleVaccunation;

    @FXML
    private MFXCheckbox cboxDrugs, cboxAlcoholism, cboxTabaquism;

    //================================================================================
    // Fields of pathological antecedents table
    // @table: pathological
    //================================================================================
    @FXML
    private MFXTextField txtSurgery, txtTraumatic, txtAllergic, txtTransfusion,
            txtCombe, txtHas, txtDM2, txtOtherPathological;

    //================================================================================
    // Fields of gynecological antecedents table
    // @table: gynaecological
    //================================================================================
    @FXML
    private Spinner<Integer> spnMenarche, spnIVSA, spnGestas, spnPartos,
            spnAbortos, spnCesareas, spnMacrosomicos, spnBajoPeso;
    @FXML
    private MFXDatePicker dpickerFUM, dpickerLastDelivery, dpickerPAP;
    @FXML
    private MFXTextField txtLUI, txtPlanificacion, txtTime;
    @FXML
    private MFXComboBox<String> cboxRythm;

    //================================================================================
    // Fields of clinical interview table
    // @table: patient_interview
    //================================================================================
    @FXML
    private TextArea txtCurrentCondition, txtOrgans, txtGeneralSimptoms;

    @FXML
    private Spinner<Double> spnWeight, spnHeight, spnIMC, spnCefalico, spnAbdominal,
            spnTemperature, spnGlucemia;

    @FXML
    private Spinner<Integer> spnHeartRate, spnRespiratoryRate, spnSystolic, spnDiastolic, spnSpO2;

    @FXML
    private MFXTextField txtGeneralInspection, txtHead, txtNeck, txtThorax, txtAbdomen,
            txtVertebral, txtGenitalia, txtRectalTouch, txtVaginalTouch, txtLimbs;

    @FXML
    private TextArea txtDiagnosticImpression, txtTreatment, txtPrognosis;

    @FXML
    private Button btnSaveHistory;

    @FXML
    private Label lblDate, lblHour, lblPatientName, lblPatientAge,
            lblPatientGender, lblAddress, lblPhone, lblEmail;

    @FXML
    private Label lblDoctorName, lblLicense;

    @FXML
    private MFXToggleButton toggleType;

    @FXML
    public void initialize() {
        rbtnHacinamiento.setUserData("Hacinamiento");
        rbtnSingle.setUserData("Soltero");
        rbtnMarried.setUserData("Casado");
        rbtnWidowded.setUserData("Union libre");

        // Add items to the ComboBox
        cboxRythm.getItems().addAll("REGULAR", "IRREGULAR");


        // Set default values for the spinners
        spnMenarche.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnIVSA.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnGestas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnPartos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnAbortos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnCesareas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnMacrosomicos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        spnBajoPeso.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        // Set default values for the other spinners
        spnWeight.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0, 300.0, 0.0, 0.1));
        spnHeight.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0, 300.0, 1.65, 0.01));
        spnIMC.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0, 100.0, 0.0, 0.1));
        spnCefalico.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0, 100.0, 0.0, 0.1));
        spnAbdominal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0.0, 300.0, 0.0, 0.1));
        spnHeartRate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 300, 75, 1));
        spnRespiratoryRate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 100, 75, 1));
        spnTemperature.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0, 100.0, 36.5, 0.1));
        spnGlucemia.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                0, 1000.0, 95, 1));
        spnSpO2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 100, 95, 1));
        spnSystolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 300, 120, 1));
        spnDiastolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, 300, 80, 1));
    }

// En: src/main/java/com/alann616/consulter/controller/ClinicalHistoryController.java

    @FXML
    public void saveHistory() {
        if (patient == null || doctor == null) {
            System.err.println("Error: Paciente o doctor no definidos.");
            // Mostrar alerta al usuario
            return;
        }

        // Configurar el tamaño de los VBox para la captura de pantalla del PDF
        vboxPage1.setMaxSize(816, 1056);
        vboxPage1.setMinSize(816, 1056);
        vboxPage2.setMaxSize(816, 1056);
        vboxPage2.setMinSize(816, 1056);
        vboxPage3.setMaxSize(816, 1056);
        vboxPage3.setMinSize(816, 1056);

        // Si 'clinicalHistory' es nulo, creamos una instancia nueva. Si no, la editamos.
        if (clinicalHistory == null) {
            clinicalHistory = new ClinicalHistory();
            clinicalHistory.setHereditary(new Hereditary());
            clinicalHistory.setNonPathological(new NonPathological());
            clinicalHistory.setPathological(new Pathological());
            clinicalHistory.setGynecological(new Gynecological());
            clinicalHistory.setPatientInterview(new PatientInterview());
        }

        // 1. Poblar el objeto ClinicalHistory y todas sus tablas hijas con los datos del formulario.
        // ... (Tu código existente para poblar los objetos h, np, p, g, pi y clinicalHistory va aquí)
        // (Este código es largo y ya lo tienes, así que se omite por brevedad)
        // Ejemplo:
        clinicalHistory.setTimestamp(LocalDateTime.now());
        clinicalHistory.setPatient(patient);
        clinicalHistory.setDoctor(doctor);
        // ... resto de los setters ...

        try {
            // Desactivar el botón para evitar doble clic
            btnSaveHistory.setDisable(true);

            // 2. Guardar la historia clínica UNA VEZ para obtener el ID.
            ClinicalHistory savedHistory = clinicalHistoryService.saveClinicalHistory(clinicalHistory);

            // 3. Generar el nombre del documento con el ID real.
            String documentName = String.format("HistoriaClinica_%d_%s_%s_%s",
                    savedHistory.getDocumentId(),
                    savedHistory.getTimestamp().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    patient.getName().replaceAll("\\s+", ""),
                    patient.getLastName().replaceAll("\\s+", "")
            );

            // 4. Asignar el nombre y actualizar la entidad.
            savedHistory.setDocumentName(documentName);
            clinicalHistoryService.saveClinicalHistory(savedHistory);

            System.out.println("Historia clínica guardada en BD: " + savedHistory);

            // 5. Generar el PDF en un hilo separado para no congelar la UI.
            final ClinicalHistory historyForPdf = savedHistory;
            Task<Void> pdfTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    List<VBox> vboxes = Arrays.asList(vboxPage1, vboxPage2, vboxPage3);
                    generatePdf(historyForPdf, vboxes); // Usar el objeto final para el hilo
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        // Mostrar confirmación y cerrar la ventana SÓLO cuando el PDF se ha generado
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "La historia clínica se ha guardado y exportado a PDF correctamente.");
                        alert.setTitle("Éxito");
                        alert.setHeaderText("Operación completada");
                        alert.showAndWait();

                        // Cerrar el formulario
                        Stage form = (Stage) btnSaveHistory.getScene().getWindow();
                        form.close();
                    });
                }

                @Override
                protected void failed() {
                    // En caso de error en la generación del PDF
                    getException().printStackTrace();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "La historia se guardó en la base de datos, pero ocurrió un error al generar el archivo PDF.");
                        alert.setTitle("Error de Exportación");
                        alert.setHeaderText("No se pudo generar el PDF");
                        alert.showAndWait();
                        btnSaveHistory.setDisable(false); // Reactivar el botón
                    });
                }
            };
            new Thread(pdfTask).start();

        } catch (Exception e) {
            System.err.println("Error al guardar la historia clínica en la base de datos: " + e.getMessage());
            e.printStackTrace();
            btnSaveHistory.setDisable(false); // Reactivar el botón en caso de error
            // Mostrar alerta de error de base de datos
        }
    }


    public void setClinicalHistory(ClinicalHistory history) {
        this.clinicalHistory = history;

        // --- Hereditary antecedents table ---
        if (history.getHereditary() != null) {
            txtDiabetes.setText(history.getHereditary().getDiabetesMellitus());
            txtHypertension.setText(history.getHereditary().getHypertension());
            txtTB.setText(history.getHereditary().getTuberculosis());
            txtNeoplasia.setText(history.getHereditary().getNeoplasms());
            txtHeartDisease.setText(history.getHereditary().getHeartConditions());
            txtBirthDisease.setText(history.getHereditary().getCongenitalAnomalies());
            txtEndocrineDisorders.setText(history.getHereditary().getEndocrineDisorders());
            txtOtherHereditary.setText(history.getHereditary().getOtherHereditaryConditions());
        }

        // --- Non-pathological antecedents table ---
        if (history.getNonPathological() != null) {
            NonPathological np = history.getNonPathological();

            String maritalStatus = np.getMaritalStatus();
            // CAMBIO: Comprobar que maritalStatus no sea nulo antes de usarlo.
            if (maritalStatus != null) {
                for (Toggle toggle : toggleMaritalStatus.getToggles()) {
                    if (maritalStatus.equals(toggle.getUserData())) {
                        toggleMaritalStatus.selectToggle(toggle);
                        break;
                    }
                }
            }

            txtReligion.setText(np.getReligion());
            txtOcupation.setText(np.getOccupation());

            String wallMaterial = np.getWallMaterial();
            // CAMBIO: Comprobar que wallMaterial no sea nulo antes de hacer split.
            if (wallMaterial != null && !wallMaterial.isEmpty()) {
                List<String> selectedMaterials = Arrays.asList(wallMaterial.split(","));
                cboxBlock.setSelected(selectedMaterials.contains("Block"));
                cboxAdobe.setSelected(selectedMaterials.contains("Adobe"));
                cboxWood.setSelected(selectedMaterials.contains("Madera"));
                cboxOtherWalls.setSelected(selectedMaterials.contains("Otros"));
            } else {
                // Asegurarse de que los checkboxes estén deseleccionados si no hay datos.
                cboxBlock.setSelected(false);
                cboxAdobe.setSelected(false);
                cboxWood.setSelected(false);
                cboxOtherWalls.setSelected(false);
            }

            // CAMBIO: Añadir la misma comprobación para el material del piso (floorMaterial).
            String floorMaterial = np.getFloorMaterial();
            if (floorMaterial != null && !floorMaterial.isEmpty()) {
                List<String> selectedFloors = Arrays.asList(floorMaterial.split(","));
                cboxDirt.setSelected(selectedFloors.contains("Tierra"));
                cboxCement.setSelected(selectedFloors.contains("Cemento"));
            } else {
                cboxDirt.setSelected(false);
                cboxCement.setSelected(false);
            }

            String services = np.getServices();
            // CAMBIO: Comprobar que services no sea nulo.
            if (services != null && !services.isEmpty()) {
                List<String> selectedServices = Arrays.asList(services.split(","));
                cboxWater.setSelected(selectedServices.contains("Agua"));
                cboxDrainage.setSelected(selectedServices.contains("Drenaje"));
                cboxGas.setSelected(selectedServices.contains("Gas"));
                cboxLenia.setSelected(selectedServices.contains("Leña"));
            } else {
                cboxWater.setSelected(false);
                cboxDrainage.setSelected(false);
                cboxGas.setSelected(false);
                cboxLenia.setSelected(false);
            }


            toggleVaccunation.setSelected(np.isFullyVaccinated());
            setCheckboxFromString(cboxDrugs, np.getSubstanceUse());
            setCheckboxFromString(cboxAlcoholism, np.getIsDrinker());
            setCheckboxFromString(cboxTabaquism, np.getIsSmoker());
        }

        // --- Pathological antecedents table ---
        if (history.getPathological() != null) {
            txtSurgery.setText(history.getPathological().getSurgicalHistory());
            txtTraumatic.setText(history.getPathological().getTraumaticHistory());
            txtAllergic.setText(history.getPathological().getAllergicHistory());
            txtTransfusion.setText(history.getPathological().getTransfusionHistory());
            txtCombe.setText(history.getPathological().getCoombsTest());
            txtHas.setText(history.getPathological().getHypertension());
            txtDM2.setText(history.getPathological().getDiabetes());
            txtOtherPathological.setText(history.getPathological().getOtherPathologicalConditions());
        }

        // --- Gynecological antecedents table ---
        // CAMBIO: Comprobar que la sección ginecológica no sea nula.
        if (history.getGynecological() != null) {
            Gynecological g = history.getGynecological();
            spnMenarche.getValueFactory().setValue(g.getMenarcheAge());
            // CAMBIO: Comprobar que el valor del ComboBox no sea nulo.
            if (g.getMenstrualCycleRegularity() != null) {
                cboxRythm.setValue(g.getMenstrualCycleRegularity());
            }
            spnIVSA.getValueFactory().setValue(g.getSexualActivityStartAge());
            spnGestas.getValueFactory().setValue(g.getNumberOfPregnancies());
            spnPartos.getValueFactory().setValue(g.getNumberOfBirths());
            spnAbortos.getValueFactory().setValue(g.getNumberOfAbortions());
            spnCesareas.getValueFactory().setValue(g.getNumberOfCesareanSections());
            spnMacrosomicos.getValueFactory().setValue(g.getMacrosomicChildren());
            spnBajoPeso.getValueFactory().setValue(g.getLowBirthWeightChildren());

            dpickerFUM.setValue(g.getLastMenstrualPeriod());
            dpickerLastDelivery.setValue(g.getLastDeliveryDate());
            dpickerPAP.setValue(g.getLastPapSmearDate());

            txtLUI.setText(g.getUterineCurettage());
            txtPlanificacion.setText(g.getFamilyPlanningMethod());
            txtTime.setText(g.getContraceptiveUsageDuration());
        }

        // --- Clinical interview table ---
        // CAMBIO: Comprobar que la sección de entrevista no sea nula.
        if (history.getPatientInterview() != null) {
            PatientInterview pi = history.getPatientInterview();
            txtOrgans.setText(pi.getReviewOfSystems());
            txtGeneralSimptoms.setText(pi.getGeneralSimptoms());
            txtHead.setText(pi.getHead());
            txtNeck.setText(pi.getNeck());
            txtThorax.setText(pi.getThorax());
            txtAbdomen.setText(pi.getAbdomen());
            txtVertebral.setText(pi.getBackbone());
            txtGenitalia.setText(pi.getExternalGenitalia());
            txtRectalTouch.setText(pi.getRectalTouch());
            txtVaginalTouch.setText(pi.getVaginalTouch());
            txtLimbs.setText(pi.getLimbs());
        }

        // --- Datos generales y vitales (en la entidad ClinicalHistory) ---
        txtCurrentCondition.setText(history.getCurrentCondition());
        spnWeight.getValueFactory().setValue(history.getWeight());
        spnHeight.getValueFactory().setValue(history.getHeight());
        spnIMC.getValueFactory().setValue(history.getBodyMassIndex());
        spnCefalico.getValueFactory().setValue(history.getCephalicPerimeter());
        spnAbdominal.getValueFactory().setValue(history.getAbdominalPerimeter());
        spnHeartRate.getValueFactory().setValue(history.getHeartRate());
        spnRespiratoryRate.getValueFactory().setValue(history.getRespiratoryRate());
        spnTemperature.getValueFactory().setValue(history.getBodyTemp());
        spnGlucemia.getValueFactory().setValue(history.getCapillaryGlycemia());
        spnSpO2.getValueFactory().setValue(history.getOxygenSaturation());
        spnSystolic.getValueFactory().setValue(history.getSystolicBP());
        spnDiastolic.getValueFactory().setValue(history.getDiastolicBP());
        txtGeneralInspection.setText(history.getGeneralInspection());
        txtDiagnosticImpression.setText(history.getDiagnosticImpression());
        txtTreatment.setText(history.getTreatment());
        txtPrognosis.setText(history.getPrognosis());
        toggleType.setSelected(history.getType() != null && history.getType());

        // --- Rellenar etiquetas con datos del paciente y doctor (estos no deberían ser nulos) ---
        lblDate.setText(history.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHour.setText(history.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        lblPatientName.setText(String.format("%s %s %s", history.getPatient().getName(), history.getPatient().getLastName(), history.getPatient().getSecondLastName()));
        int ageInYears = history.getPatient().getBirthDate().until(LocalDate.now()).getYears();
        lblPatientAge.setText(String.valueOf(ageInYears));
        lblPatientGender.setText(history.getPatient().getGender().getDescription());
        lblAddress.setText(history.getPatient().getAddress());
        lblDoctorName.setText(history.getDoctor().getName());
        lblLicense.setText(String.valueOf(history.getDoctor().getDoctorLicense()));
    }

    private String getCheckboxValue(MFXCheckbox checkbox) {
        if (checkbox.isIndeterminate()) return "Indeterminado";
        return checkbox.isSelected() ? "Sí" : "No";
    }

    private void setCheckboxFromString(MFXCheckbox checkbox, String value) {
        if ("Indeterminado".equals(value)) {
            checkbox.setIndeterminate(true);
        } else {
            checkbox.setIndeterminate(false);
            checkbox.setSelected("Sí".equals(value));
        }
    }

    private String getSelectedFloor() {
        StringBuilder selectedMaterials = new StringBuilder();
        if (cboxDirt.isSelected()) selectedMaterials.append("Tierra,");
        if (cboxCement.isSelected()) selectedMaterials.append("Cemento,");

        // Remove the last comma
        if (selectedMaterials.length() > 0) {
            selectedMaterials.setLength(selectedMaterials.length() - 1);
        }
        return selectedMaterials.toString();
    }

    private String getSelectedWalls() {
        StringBuilder selectedMaterials = new StringBuilder();
        if (cboxBlock.isSelected()) selectedMaterials.append("Block,");
        if (cboxAdobe.isSelected()) selectedMaterials.append("Adobe,");
        if (cboxWood.isSelected()) selectedMaterials.append("Madera,");
        if (cboxOtherWalls.isSelected()) selectedMaterials.append("Otros,");

        // Remove the last comma
        if (selectedMaterials.length() > 0) {
            selectedMaterials.setLength(selectedMaterials.length() - 1);
        }
        return selectedMaterials.toString();
    }

    private String getSelectedServices() {
        StringBuilder selectedMaterials = new StringBuilder();
        if (cboxWater.isSelected()) selectedMaterials.append("Agua,");
        if (cboxDrainage.isSelected()) selectedMaterials.append("Drenaje,");
        if (cboxGas.isSelected()) selectedMaterials.append("Gas,");
        if (cboxLenia.isSelected()) selectedMaterials.append("Leña,");

        // Remove the last comma
        if (selectedMaterials.length() > 0) {
            selectedMaterials.setLength(selectedMaterials.length() - 1);
        }
        return selectedMaterials.toString();
    }

    public void setDoctor(User user) {
        this.doctor = user;

        lblDoctorName.setText("Dr. " + doctor.getName());
        lblLicense.setText("C.P.: " + doctor.getDoctorLicense());
    }

    public void setPatient(Patient patient) {
        this.patient = patient;

        lblDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHour.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        lblPatientName.setText(patient.getName() + " " + patient.getLastName() + " " + patient.getSecondLastName());
        int ageInYears = patient.getBirthDate().until(java.time.LocalDate.now()).getYears();
        lblPatientAge.setText(ageInYears + " años");
        lblPatientGender.setText(String.valueOf(patient.getGender()));
        lblAddress.setText(patient.getAddress());
        lblPhone.setText(patient.getPhone());
        lblEmail.setText(patient.getEmail());
    }

    public String createDirectoryStructure(String historyName) {
        // Get the user's home directory
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";
        // Set the base directory for the application
        String appDirectory = userDocuments + File.separator  + "documentos-consultorio" + File.separator + "historias-clinicas";

        // Get the current date
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // Create the directory structure
        String yearDir = appDirectory + File.separator + year;
        String monthDir = yearDir + File.separator + String.format("%02d", month);

        // Create the year directory if it doesn't exist
        File directory = new File(monthDir);
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                throw new RuntimeException("No se pudieron crear los directorios necesarios");
            }
        }

        // Format the history name to remove invalid characters
        String sanitizedName = historyName.replaceAll("[/\\\\:*?\"<>|]", "_");

        // Return the full path to the PDF file
        return monthDir + File.separator + sanitizedName + ".pdf";
    }

    public void generatePdf(ClinicalHistory history, List<VBox> vboxes) {
        try {
            String filePath = createDirectoryStructure(history.getDocumentName());

            try (PDDocument pdfDocument = new PDDocument()) {
                for (VBox vbox : vboxes) {
                    BufferedImage bufferedImage = takeSnapshot(vbox);

                    if (bufferedImage != null) {
                        PDPage page = new PDPage(PDRectangle.LETTER);
                        pdfDocument.addPage(page);

                        PDImageXObject pdImage = LosslessFactory.createFromImage(pdfDocument, bufferedImage);
                        PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);

                        float scale = Math.min(
                                PDRectangle.LETTER.getWidth() / pdImage.getWidth(),
                                PDRectangle.LETTER.getHeight() / pdImage.getHeight()
                        );

                        float width = pdImage.getWidth() * scale;
                        float height = pdImage.getHeight() * scale;
                        float x = (PDRectangle.LETTER.getWidth() - width) / 2;
                        float y = (PDRectangle.LETTER.getHeight() - height) / 2;

                        contentStream.drawImage(pdImage, x, y, width, height);
                        contentStream.close();
                    }
                }

                pdfDocument.save(filePath);
                System.out.println("✅ PDF creado exitosamente: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al generar el PDF: " + e.getMessage());
        }
    }

    public BufferedImage takeSnapshot(VBox vbox) throws InterruptedException {
        final BufferedImage[] result = new BufferedImage[1];
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                vbox.setMinSize(816, 1056);
                vbox.setMaxSize(816, 1056);
                vbox.setPrefSize(816, 1056);

                WritableImage fxImage = vbox.snapshot(new SnapshotParameters(), null);
                result[0] = SwingFXUtils.fromFXImage(fxImage, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        return result[0];
    }

    /**
     * Prepara el controlador y el formulario para crear una nueva historia clínica.
     * Este método reinicia el estado del controlador para evitar la sobreescritura
     * de registros existentes.
     * @param patient El paciente al que se le creará la historia.
     * @param doctor El doctor que la registra.
     */
    public void prepareForNewHistory(Patient patient, User doctor) {
        // 1. Reinicia la variable de estado principal a null.
        this.clinicalHistory = null;

        // 2. Carga los datos del paciente y doctor en las etiquetas correspondientes.
        setPatient(patient);
        setDoctor(doctor);

        // 3. Reinicia TODOS los campos del formulario a su estado por defecto.

        // --- General ---
        toggleType.setSelected(false);

        // --- Hereditarios ---
        txtDiabetes.clear();
        txtHypertension.clear();
        txtTB.clear();
        txtNeoplasia.clear();
        txtHeartDisease.clear();
        txtBirthDisease.clear();
        txtEndocrineDisorders.clear();
        txtOtherHereditary.clear();

        // --- No Patológicos ---
        toggleMaritalStatus.selectToggle(null); // Deselecciona el grupo de botones
        txtReligion.clear();
        txtOcupation.clear();
        cboxDirt.setSelected(false);
        cboxCement.setSelected(false);
        cboxBlock.setSelected(false);
        cboxAdobe.setSelected(false);
        cboxWood.setSelected(false);
        cboxOtherWalls.setSelected(false);
        cboxWater.setSelected(false);
        cboxDrainage.setSelected(false);
        cboxGas.setSelected(false);
        cboxLenia.setSelected(false);
        toggleVaccunation.setSelected(false);
        cboxDrugs.setSelected(false);
        cboxAlcoholism.setSelected(false);
        cboxTabaquism.setSelected(false);

        // --- Patológicos ---
        txtSurgery.clear();
        txtTraumatic.clear();
        txtAllergic.clear();
        txtTransfusion.clear();
        txtCombe.clear();
        txtHas.clear();
        txtDM2.clear();
        txtOtherPathological.clear();

        // --- Ginecológicos ---
        spnMenarche.getValueFactory().setValue(0);
        spnIVSA.getValueFactory().setValue(0);
        spnGestas.getValueFactory().setValue(0);
        spnPartos.getValueFactory().setValue(0);
        spnAbortos.getValueFactory().setValue(0);
        spnCesareas.getValueFactory().setValue(0);
        spnMacrosomicos.getValueFactory().setValue(0);
        spnBajoPeso.getValueFactory().setValue(0);
        dpickerFUM.clear();
        dpickerLastDelivery.clear();
        dpickerPAP.clear();
        txtLUI.clear();
        txtPlanificacion.clear();
        txtTime.clear();
        cboxRythm.clearSelection();

        // --- Entrevista y Signos Vitales ---
        txtCurrentCondition.clear();
        txtOrgans.clear();
        txtGeneralSimptoms.clear();
        spnWeight.getValueFactory().setValue(0.0);
        spnHeight.getValueFactory().setValue(1.65);
        spnIMC.getValueFactory().setValue(0.0);
        spnCefalico.getValueFactory().setValue(0.0);
        spnAbdominal.getValueFactory().setValue(0.0);
        spnHeartRate.getValueFactory().setValue(75);
        // CORRECCIÓN: El valor por defecto para Frec. Respiratoria era muy alto. 18 es más realista.
        spnRespiratoryRate.getValueFactory().setValue(18);
        spnTemperature.getValueFactory().setValue(36.5);
        spnGlucemia.getValueFactory().setValue(95.0);
        spnSpO2.getValueFactory().setValue(95);
        spnSystolic.getValueFactory().setValue(120);
        spnDiastolic.getValueFactory().setValue(80);

        // --- Inspección Física ---
        txtGeneralInspection.clear();
        txtHead.clear();
        txtNeck.clear();
        txtThorax.clear();
        txtAbdomen.clear();
        txtVertebral.clear();
        txtGenitalia.clear();
        txtRectalTouch.clear();
        txtVaginalTouch.clear();
        txtLimbs.clear();

        // --- Diagnóstico y Plan ---
        txtDiagnosticImpression.clear();
        txtTreatment.clear();
        txtPrognosis.clear();
    }
}
