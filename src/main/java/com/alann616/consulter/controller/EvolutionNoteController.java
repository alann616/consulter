package com.alann616.consulter.controller;

import com.alann616.consulter.enums.DocumentType;
import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.User;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.service.DocumentService;
import com.alann616.consulter.service.EvolutionNoteService;
import com.alann616.consulter.service.PatientService;
import com.alann616.consulter.service.UserService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class EvolutionNoteController {
    @Getter
    private Patient patient;

    @Getter
    private User doctor;

    private EvolutionNote evolutionNote;

    @Autowired private PatientService patientService;
    @Autowired private UserService userService;
    @Autowired private DocumentService documentService;

    @FXML
    private Label evoDoctorData, lblDateTime, lblName, lblAge;

    @FXML
    private TextArea currentCondition, genInspection, treatmentPlan, treatment,
            diagnosis, prognosis, instructions, laboratoryResults;

    @FXML
    private Spinner<Integer> spnDiastolic, spnSystolic, spnSPo2,
            spnHeartRate, spnRespirationRate;

    @FXML private Button btnSaveNote;

    @FXML
    private Spinner<Double> spnWeight, spnHeight, spnTemperature;

    @Autowired private EvolutionNoteService evolutionNoteService;

    @FXML
    public void initialize() {
        // Presión arterial
        spnSystolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                (70, 200, 120));
        spnDiastolic.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                (40, 130, 80));

        // Saturación de oxígeno (SpO2)
        spnSPo2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                (50, 100, 98));

        // Frecuencia cardíaca (Pulso)
        spnHeartRate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                (30, 200, 75));

        // Frecuencia respiratoria
        spnRespirationRate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory
                (10, 80, 16));

        // Peso (kg)
        spnWeight.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory
                (2.0, 300.0, 60.0, 0.1));

        // Altura (cm convertidos a metros)
        spnHeight.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory
                (0.50, 2.50, 1.60, 0.01));

        // Temperatura (°C)
        spnTemperature.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory
                (30.0, 45.0, 36.5, 0.1));

        bulletTextArea(treatmentPlan);
        bulletTextArea(instructions);

        // Configurar el área de texto para que crezca automáticamente
        setupAutoGrowingTextArea(currentCondition);
        setupAutoGrowingTextArea(genInspection);
        setupAutoGrowingTextArea(treatmentPlan);
        setupAutoGrowingTextArea(treatment);
        setupAutoGrowingTextArea(diagnosis);
        setupAutoGrowingTextArea(prognosis);
        setupAutoGrowingTextArea(instructions);
        setupAutoGrowingTextArea(laboratoryResults);

        prognosis.setText("RESERVADO A EVOLUCIÓN DEL PACIENTE");

        treatmentPlan.setText("""
                · SE EXPLICAN DATOS DE ALARMA
                · MEDIDAS GENERALES PARA PREVENCIÓN DE SARS COV-2
                · CONSUMIR ABUNDANTES LÍQUIDOS""");

        instructions.setText("· CITA ABIERTA EN CASO DE CUALQUIER EVENTUALIDAD" + "\n" + "· REPOSO RELATIVO");
    }

    @FXML
    public void saveEvolutionNote(ActionEvent e) throws IOException {
        if (patient == null || doctor == null) {
            System.err.println("Error: Paciente o doctor no definidos.");
            return;
        }

        // Si evolutionNote es null, significa que estamos guardando una nota nueva.
        // Si no es null, estamos editando una existente (establecida por setEvolutionNote).
        if (evolutionNote == null) {
            evolutionNote = new EvolutionNote(); // Crear una nueva instancia SOLO si es nota nueva
        }

        evolutionNote.setTimestamp(LocalDateTime.now());
        evolutionNote.setPatient(patient);
        evolutionNote.setDoctor(doctor);
        evolutionNote.setSystolicBP(spnSystolic.getValue());
        evolutionNote.setDiastolicBP(spnDiastolic.getValue());
        evolutionNote.setWeight(spnWeight.getValue());
        evolutionNote.setHeight(spnHeight.getValue());
        evolutionNote.setBodyTemp(spnTemperature.getValue());
        evolutionNote.setOxygenSaturation(spnSPo2.getValue());
        evolutionNote.setHeartRate(spnHeartRate.getValue());
        evolutionNote.setRespiratoryRate(spnRespirationRate.getValue());
        evolutionNote.setCurrentCondition(currentCondition.getText());
        evolutionNote.setGeneralInspection(genInspection.getText());
        evolutionNote.setTreatmentPlan(treatmentPlan.getText());
        evolutionNote.setDiagnosticImpression(diagnosis.getText());
        evolutionNote.setTreatment(treatment.getText());
        evolutionNote.setPrognosis(prognosis.getText());
        evolutionNote.setInstructions(instructions.getText());
        evolutionNote.setLaboratoryResults(laboratoryResults.getText());
        evolutionNote.setDocumentType(DocumentType.EVOLUTION_NOTE);

        // Primero guardamos la nota para que obtenga un ID si es nueva
        // Si es una edición, save actualizará la entidad existente
        evolutionNote = evolutionNoteService.saveEvolutionNote(evolutionNote);

        // Ahora generamos el documentName con el ID real (que ahora ya está asignado)
        String documentName = String.format("NotaEvolucion_%d_%s_%s_%s",
                evolutionNote.getDocumentId(), // Usamos el ID asignado
                evolutionNote.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")).replace(":", "-").replace("/", "-").replace(" ", "_"), // Formato amigable para nombre de archivo
                patient.getName().replaceAll("\\s+", ""),
                patient.getLastName().replaceAll("\\s+", "")
        );

        // Actualizamos la nota con el documentName y guardamos de nuevo
        evolutionNote.setDocumentName(documentName);
        evolutionNote = evolutionNoteService.saveEvolutionNote(evolutionNote); // Segunda actualización para el nombre

        // Generamos el documento con el nombre correcto
        generateDocx(evolutionNote);

        System.out.println("Nota de evolución guardada correctamente: " + evolutionNote);

        // 🚨 IMPORTANTE: Resetear la variable de instancia después de guardar
        // para asegurar que la próxima vez que se abra el formulario para una NOTA NUEVA,
        // se cree una nueva instancia.
        this.evolutionNote = null; // Añade esta línea


        ((Stage) btnSaveNote.getScene().getWindow()).close();
    }


    /**
     * Genera un documento Word con los datos de la nota de evolución, organizando por año y mes
     *
     * @param note Nota de evolución a exportar
     */
    public void generateDocx(EvolutionNote note) {
        try {
            String filePath = createDirectoryStructure(note.getDocumentName());
            XWPFDocument wordDocument = createDocument(note);
            saveDocument(wordDocument, filePath);
            System.out.println("Documento creado exitosamente en: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al generar el documento: " + e.getMessage());
        }
    }

    private String createDirectoryStructure(String documentName) {
        // Obtener la carpeta de documentos del usuario actual de forma portable
        String userDocuments = System.getProperty("user.home") + File.separator + "Documents";

        // subcarpeta específica
        String appDirectory = userDocuments + File.separator  + "documentos-consultorio" + File.separator + "notas-evolucion";

        // Obtener la fecha actual para la estructura de directorios
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        // Crear la estructura de directorios año/mes
        String yearDir = appDirectory + File.separator + year;
        String monthDir = yearDir + File.separator + String.format("%02d", month);

        // Crear los directorios si no existen (incluyendo el directorio base de la aplicación)
        File directory = new File(monthDir);
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                throw new RuntimeException("No se pudieron crear los directorios necesarios");
            }
        }

        // Formatear el nombre del documento sin caracteres problemáticos
        String sanitizedName = documentName.replaceAll("[/\\\\:*?\"<>|]", "_");

        // Retornar la ruta completa del archivo
        return monthDir + File.separator + sanitizedName + ".docx";
    }

    private XWPFDocument createDocument(EvolutionNote note) {
        // Crear el documento Word
        XWPFDocument wordDocument = new XWPFDocument();

        // Crear un párrafo para el título
        XWPFParagraph titleParagraph = wordDocument.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("NOTA DE EVOLUCIÓN");
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.addBreak();

        // Datos del paciente y fecha
        addPatientData(wordDocument, note);

        // signos vitales
        addVitalSigns(wordDocument, note);

        // Textos largos
        addLargeTexts(wordDocument, note);

        // Datos del doctor
        addDoctorData(wordDocument, note);

        return wordDocument;
    }

    public void addPatientData(XWPFDocument wordDocument, EvolutionNote note) {
        XWPFParagraph paragraph = wordDocument.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun patientData = paragraph.createRun();
        patientData.setBold(true);

        // Línea 1: Timestamp
        patientData.setText("FECHA Y HORA: " + note.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        patientData.addBreak();

        // Línea 2: Nombre completo
        patientData.setText(note.getPatient().getName() + " " +
                note.getPatient().getLastName() + " " +
                note.getPatient().getSecondLastName());
        patientData.addBreak();

        // Línea 3: Edad
        int edad = note.getPatient().getBirthDate().until(LocalDate.now()).getYears();
        patientData.setText("Edad: " + edad + " años");
        patientData.addBreak();
    }

    private void addDoctorData(XWPFDocument wordDocument, EvolutionNote note) {
        XWPFParagraph paragraph = wordDocument.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun doctorData = paragraph.createRun();
        doctorData.setBold(true);
        doctorData.setText("Dr. " + note.getDoctor().getName());
        doctorData.addBreak();

        doctorData.setText("C.P.: " + note.getDoctor().getDoctorLicense());
    }

    private void addVitalSigns (XWPFDocument wordDocument, EvolutionNote note) {
        XWPFParagraph paragraph = wordDocument.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun vitalSigns = paragraph.createRun();
        vitalSigns.setBold(true);
        vitalSigns.setText(String.format(("TA: %s/%s  " + "PESO: %skg  " + "TALLA: %sm  "+ "TEMP: %sºC  " + "SPo2: %s  " + "FC: %s LPM  " + "FR: %s"),
                note.getSystolicBP(), note.getDiastolicBP(),
                note.getWeight(),
                note.getHeight(),
                note.getBodyTemp(),
                note.getOxygenSaturation(),
                note.getHeartRate(),
                note.getRespiratoryRate()));
    }

    private void addLargeTexts (XWPFDocument wordDocument, EvolutionNote note) {
        XWPFParagraph paragraph = wordDocument.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun currentCondition = paragraph.createRun();
        currentCondition.setText("PADECIMIENTO ACTUAL:" + "\n" + note.getCurrentCondition());
        currentCondition.addBreak();
        currentCondition.addBreak();

        XWPFRun genInspection = paragraph.createRun();
        genInspection.setText("INSPECCIÓN GENERAL: " + "\n" + note.getGeneralInspection());
        genInspection.addBreak();
        genInspection.addBreak();

        XWPFRun treatment = paragraph.createRun();
        treatment.setText("TRATAMIENTO: " + "\n" + note.getTreatment());
        treatment.addBreak();
        treatment.addBreak();

        XWPFRun treatmentPlan = paragraph.createRun();
        treatmentPlan.setText("PLAN: " + "\n" + note.getTreatmentPlan());
        treatmentPlan.addBreak();
        treatmentPlan.addBreak();

        XWPFRun diagnosis = paragraph.createRun();
        diagnosis.setText("IMPRESIÓN DIAGNÓSTICA:" + "\n" + note.getDiagnosticImpression());
        diagnosis.addBreak();
        diagnosis.addBreak();

        XWPFRun prognosis = paragraph.createRun();
        prognosis.setText("PRONÓSTICO: " + "\n" + note.getPrognosis());
        prognosis.addBreak();
        prognosis.addBreak();

        XWPFRun instructions = paragraph.createRun();
        instructions.setText("RECOMENDACIONES: " + "\n" + note.getInstructions());
        instructions.addBreak();
        instructions.addBreak();

        XWPFRun laboratoryResults = paragraph.createRun();
        laboratoryResults.setText("RESULTADOS DE LABORATORIO: " + "\n" + note.getLaboratoryResults());
        laboratoryResults.addBreak();
        laboratoryResults.addBreak();
    }

    private void bulletTextArea(TextArea textArea) {
        textArea.setText("· ");

        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();

                int caretPosition = textArea.getCaretPosition();
                textArea.insertText(caretPosition, "· ");
            }
        });
    }

    private void saveDocument(XWPFDocument document, String filePath) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        document.write(out);
        out.close();
        document.close();
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
        if (doctor != null) {
            evoDoctorData.setText("Dr. " + doctor.getName() + "\n" +
                    "C.P.: " + doctor.getDoctorLicense());
        }
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        int age = patient.getBirthDate().until(LocalDate.now()).getYears();
        if (patient != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Día/Mes/Año Hora:Minuto
            lblDateTime.setText(LocalDateTime.now().format(formatter));
            lblName.setText(patient.getName() + " " + patient.getLastName() + " " + patient.getSecondLastName());
            lblAge.setText(age + " años");
        }
    }

    private void setupAutoGrowingTextArea(TextArea textArea) {
        textArea.setWrapText(true);
        textArea.setMinHeight(45);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                Text text = new Text(newValue);
                text.setFont(textArea.getFont());
                double width = textArea.getWidth() - 40;
                text.setWrappingWidth(Math.max(width, 0));

                // Aumentamos el factor de multiplicación para dar más espacio
                double textHeight = text.getLayoutBounds().getHeight();
                // Versión con valores más agresivos
                double newHeight = textHeight * 2.0 + 50; // Factor 2.0 y padding de 50

                textArea.setPrefHeight(Math.max(newHeight, 0));
            });
        });

        textArea.widthProperty().addListener((observable, oldValue, newValue) -> {
            String currentText = textArea.getText();
            textArea.setText(currentText);
        });
    }

    public void setEvolutionNote(EvolutionNote note) {
        this.evolutionNote = note;

        //Datos del doctor
        evoDoctorData.setText("Dr. " + note.getDoctor().getName() + "\n" +
                "C.P.: " + note.getDoctor().getDoctorLicense());

        // Asignar valores a los campos visuales
        spnSystolic.getValueFactory().setValue(note.getSystolicBP());
        spnDiastolic.getValueFactory().setValue(note.getDiastolicBP());
        spnWeight.getValueFactory().setValue(note.getWeight());
        spnHeight.getValueFactory().setValue(note.getHeight());
        spnTemperature.getValueFactory().setValue(note.getBodyTemp());
        spnSPo2.getValueFactory().setValue(note.getOxygenSaturation());
        spnHeartRate.getValueFactory().setValue(note.getHeartRate());
        spnRespirationRate.getValueFactory().setValue(note.getRespiratoryRate());

        currentCondition.setText(note.getCurrentCondition());
        genInspection.setText(note.getGeneralInspection());
        treatmentPlan.setText(note.getTreatmentPlan());
        treatment.setText(note.getTreatment());
        diagnosis.setText(note.getDiagnosticImpression());
        prognosis.setText(note.getPrognosis());
        instructions.setText(note.getInstructions());
        laboratoryResults.setText(note.getLaboratoryResults());
    }

}
