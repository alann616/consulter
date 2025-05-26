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

        // Hereditary antecedents table
        txtDiabetes.setText(history.getHereditary().getDiabetesMellitus());
        txtHypertension.setText(history.getHereditary().getHypertension());
        txtTB.setText(history.getHereditary().getTuberculosis());
        txtNeoplasia.setText(history.getHereditary().getNeoplasms());
        txtHeartDisease.setText(history.getHereditary().getHeartConditions());
        txtBirthDisease.setText(history.getHereditary().getCongenitalAnomalies());
        txtEndocrineDisorders.setText(history.getHereditary().getEndocrineDisorders());
        txtOtherHereditary.setText(history.getHereditary().getOtherHereditaryConditions());

        // Non-pathological antecedents table
        String maritalStatus = history.getNonPathological().getMaritalStatus();
        if (maritalStatus != null) {
            for (Toggle toggle : toggleMaritalStatus.getToggles()) {
                if (maritalStatus.equals(toggle.getUserData())) {
                    toggleMaritalStatus.selectToggle(toggle);
                    break;
                }
            }
        }

        txtReligion.setText(history.getNonPathological().getReligion());
        txtOcupation.setText(history.getNonPathological().getOccupation());

        String wallMaterial = history.getNonPathological().getWallMaterial();
        List<String> selectedMaterials = Arrays.asList(wallMaterial.split(","));
        cboxDirt.setSelected(selectedMaterials.contains("Tierra"));
        cboxCement.setSelected(selectedMaterials.contains("Cemento"));
        cboxBlock.setSelected(selectedMaterials.contains("Block"));
        cboxAdobe.setSelected(selectedMaterials.contains("Adobe"));
        cboxWood.setSelected(selectedMaterials.contains("Madera"));
        cboxOtherWalls.setSelected(selectedMaterials.contains("Otros"));

        String services = history.getNonPathological().getServices();
        List<String> selectedServices = Arrays.asList(services.split(","));
        cboxWater.setSelected(selectedServices.contains("Agua"));
        cboxDrainage.setSelected(selectedServices.contains("Drenaje"));
        cboxGas.setSelected(selectedServices.contains("Gas"));
        cboxLenia.setSelected(selectedServices.contains("Leña"));

        toggleVaccunation.setSelected(history.getNonPathological().isFullyVaccinated());

        setCheckboxFromString(cboxDrugs, history.getNonPathological().getSubstanceUse());
        setCheckboxFromString(cboxAlcoholism, history.getNonPathological().getIsDrinker());
        setCheckboxFromString(cboxTabaquism, history.getNonPathological().getIsSmoker());

        // Pathological antecedents table
        txtSurgery.setText(history.getPathological().getSurgicalHistory());
        txtTraumatic.setText(history.getPathological().getTraumaticHistory());
        txtAllergic.setText(history.getPathological().getAllergicHistory());
        txtTransfusion.setText(history.getPathological().getTransfusionHistory());
        txtCombe.setText(history.getPathological().getCoombsTest());
        txtHas.setText(history.getPathological().getHypertension());
        txtDM2.setText(history.getPathological().getDiabetes());
        txtOtherPathological.setText(history.getPathological().getOtherPathologicalConditions());

        // Gynecological antecedents table
        spnMenarche.getValueFactory().setValue(history.getGynecological().getMenarcheAge());
        cboxRythm.setValue(history.getGynecological().getMenstrualCycleRegularity());
        spnIVSA.getValueFactory().setValue(history.getGynecological().getSexualActivityStartAge());
        spnGestas.getValueFactory().setValue(history.getGynecological().getNumberOfPregnancies());
        spnPartos.getValueFactory().setValue(history.getGynecological().getNumberOfBirths());
        spnAbortos.getValueFactory().setValue(history.getGynecological().getNumberOfAbortions());
        spnCesareas.getValueFactory().setValue(history.getGynecological().getNumberOfCesareanSections());
        spnMacrosomicos.getValueFactory().setValue(history.getGynecological().getMacrosomicChildren());
        spnBajoPeso.getValueFactory().setValue(history.getGynecological().getLowBirthWeightChildren());

        dpickerFUM.setValue(history.getGynecological().getLastMenstrualPeriod());
        dpickerLastDelivery.setValue(history.getGynecological().getLastDeliveryDate());
        dpickerPAP.setValue(history.getGynecological().getLastPapSmearDate());

        txtLUI.setText(history.getGynecological().getUterineCurettage());
        txtPlanificacion.setText(history.getGynecological().getFamilyPlanningMethod());
        txtTime.setText(String.valueOf(history.getGynecological().getContraceptiveUsageDuration()));

        // Clinical interview table
        txtCurrentCondition.setText(history.getCurrentCondition());
        txtOrgans.setText(history.getPatientInterview().getReviewOfSystems());
        txtGeneralSimptoms.setText(history.getPatientInterview().getGeneralSimptoms());

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
        txtHead.setText(history.getPatientInterview().getHead());
        txtNeck.setText(history.getPatientInterview().getNeck());
        txtThorax.setText(history.getPatientInterview().getThorax());
        txtAbdomen.setText(history.getPatientInterview().getAbdomen());
        txtVertebral.setText(history.getPatientInterview().getBackbone());
        txtGenitalia.setText(history.getPatientInterview().getExternalGenitalia());
        txtRectalTouch.setText(history.getPatientInterview().getRectalTouch());
        txtVaginalTouch.setText(history.getPatientInterview().getVaginalTouch());
        txtLimbs.setText(history.getPatientInterview().getLimbs());

        txtDiagnosticImpression.setText(history.getDiagnosticImpression());
        txtTreatment.setText(history.getTreatment());
        txtPrognosis.setText(history.getPrognosis());

        toggleType.setSelected(history.getType());

        // Set the date and time labels
        lblDate.setText(history.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblHour.setText(history.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Set the patient information labels
        lblPatientName.setText(history.getPatient().getName() +
                " " + history.getPatient().getLastName() +
                " " + history.getPatient().getSecondLastName());

        int ageInYears = history.getPatient().getBirthDate().until(LocalDate.now()).getYears();
        lblPatientAge.setText(String.valueOf(ageInYears));
        lblPatientGender.setText(String.valueOf(history.getPatient().getGender()));
        lblAddress.setText(history.getPatient().getAddress());

        // Set the doctor information labels
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

}
