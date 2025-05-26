package com.alann616.consulter.controller;

import com.alann616.consulter.Main;
import com.alann616.consulter.components.PatientCell;
import com.alann616.consulter.enums.DocumentType;
import com.alann616.consulter.model.Patient;
import com.alann616.consulter.model.User;
import com.alann616.consulter.model.doctordocs.ClinicalHistory;
import com.alann616.consulter.model.doctordocs.EvolutionNote;
import com.alann616.consulter.service.ClinicalHistoryService;
import com.alann616.consulter.service.EvolutionNoteService;
import com.alann616.consulter.service.PatientService;
import com.alann616.consulter.service.UserService;
import com.alann616.consulter.util.DocumentSummary;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.controls.MFXToggleButton;
import io.github.palexdev.materialfx.controls.cell.MFXListCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

import static com.alann616.consulter.Main.context;

@Component
public class MainViewController {

    @FXML private VBox selectPatientContent;
    @Autowired @Getter @Setter
    private PatientService patientService;

    @Autowired @Getter @Setter
    private UserService userService;

    private Stage patientFormStage = null;

    @FXML private AnchorPane bgMain;

    @FXML private Label lblUser;

    @FXML private ListView<Patient> lstPatients;
    @FXML private TextField txtSearch;

    @FXML private MFXListView<ClinicalHistory> lstHistories;


    @FXML private StackPane stackProfile;
    @FXML private Button btnProfile;

    @FXML private TextField profileName, profileLicense, profileProfession, profilePhone;

    @FXML MenuButton changeUser;
    @FXML MenuItem changeCarlos, changeVicente;


    @FXML private TableView<DocumentSummary> tblDocuments;
    @FXML private TableColumn<DocumentSummary, LocalDateTime> colTimestamp;
    @FXML private TableColumn<DocumentSummary, Long> colDocumentId;
    @FXML private TableColumn<DocumentSummary, DocumentType> colDocType;
    @FXML private TableColumn<DocumentSummary, String> colDocName;
    @FXML private TableColumn<DocumentSummary, String> colDoctorName;


    private FilteredList<Patient> filteredPatients;
    private SortedList<Patient> sortedPatients;

    @FXML private AnchorPane anchorPatient;

    @Getter private User loggedUser;


    @FXML
    private ToggleGroup toggleOrder;

    @FXML
    private Label lblId, lblName, lblLastName, lblSecondLastName, lblGender,
            lblBirthDate, lblAddress, lblPhone, lblEmail, lblTimestamp;

    @Autowired
    private EvolutionNoteService evolutionNoteService;
    @Autowired
    private ClinicalHistoryService clinicalHistoryService;

    // Declara esta variable a nivel de clase
    private ObservableList<Patient> allPatients;

    @FXML
    private Scene mainScene; // Necesitarás obtener la escena principal

    // --- Nuevas variables FXML ---
    @FXML private StackPane stackSettings; // El nuevo StackPane
    @FXML private Button btnSettings;     // El botón que lo abre (asegúrate que exista en el FXML)
    @FXML private MFXToggleButton toggleDarkMode; // El switch para el modo oscuro

    private boolean isNightMode = false;
    private String defaultThemePath; // Para tu DefaultTheme.css o styles.css (el que sea el tema claro principal)
    private String nightThemePath;

    private final String lightThemeCss = getClass().getResource("/css/styles.css").toExternalForm();
    private final String nightThemeCss = getClass().getResource("/css/mainview-night.css").toExternalForm();

    @FXML
    public void initialize() {
        // --- Inicialización de rutas CSS ---
        // Es más robusto obtener las rutas así para evitar problemas con espacios o caracteres especiales
        try {
            // Carga las rutas a los CSS. Ajusta los nombres si es necesario.
            // Asumimos que DefaultTheme.css es tu tema claro principal que se intercambia.
            // Si styles.css es el que define el tema claro, usa esa ruta.
            defaultThemePath = Objects.requireNonNull(getClass().getResource("/css/DefaultTheme.css"), "DefaultTheme.css no encontrado").toExternalForm();
            nightThemePath = Objects.requireNonNull(getClass().getResource("/css/mainview-night.css"), "mainview-night.css no encontrado").toExternalForm();

            // Configuración del botón de cambio de tema (si existe)
            if (toggleDarkMode != null) {
                // Cargar preferencia de tema (opcional)
                // loadThemePreference();
                toggleDarkMode.setSelected(isNightMode);
                toggleDarkMode.setOnAction(event -> toggleTheme());
            }

            // Aplicar el tema inicial cuando la escena esté disponible.
            // Es más seguro que hacerlo directamente en initialize(), ya que la escena podría no estar lista.
            bgMain.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    applyInitialTheme(newScene);
                }
            });

        } catch (NullPointerException e) {
            System.err.println("Error crítico: No se pudo cargar un archivo CSS esencial. Verifica las rutas en MainViewController.");
            e.printStackTrace();
            if (toggleDarkMode != null) {
                toggleDarkMode.setDisable(true); // Deshabilitar el botón si los CSS no cargan
            }
        }

        // --- Inicialización StackPane Ajustes ---
        stackSettings.setVisible(false);
        stackSettings.setManaged(false);
        // Asegúrate de tener un botón btnSettings en tu FXML con este ID
        if (btnSettings != null) {
            btnSettings.setOnAction(event -> togglePaneVisibility(stackSettings, stackProfile)); // Usamos método helper
        } else {
            System.err.println("Advertencia: btnSettings no encontrado en el FXML.");
        }

        // --- Configurar Toggle Dark Mode ---
        toggleDarkMode.setOnAction(event -> toggleNightMode());

        // Ocultar paneles al hacer clic fuera
        bgMain.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            // Ocultar Perfil si está visible y se hizo clic fuera
            if (stackProfile.isVisible() && !isClickInsideNode(event, stackProfile) && !isClickInsideNode(event, btnProfile)) {
                stackProfile.setVisible(false);
                stackProfile.setManaged(false);
            }
            // Ocultar Ajustes si está visible y se hizo clic fuera
            if (stackSettings.isVisible() && !isClickInsideNode(event, stackSettings) && (btnSettings == null || !isClickInsideNode(event, btnSettings))) {
                stackSettings.setVisible(false);
                stackSettings.setManaged(false);
            }
        });

        // Inicializa la lista base
        allPatients = FXCollections.observableArrayList();
        allPatients.addAll(patientService.getAllPatients());

        // Crea la cadena de transformación una sola vez
        filteredPatients = new FilteredList<>(allPatients, p -> true);
        sortedPatients = new SortedList<>(filteredPatients);

        // Asigna la lista final al ListView
        lstPatients.setItems(sortedPatients);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);
        });

        lstPatients.setCellFactory(param -> new PatientCell(this));
        lstPatients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showPatientDetails(newSelection);
            }
        });

        lstHistories.setConverter(new StringConverter<ClinicalHistory>() {
            @Override
            public String toString(ClinicalHistory history) {
                if (history == null) return "";
                return "Historia clínica: " + history.getPatient().getName() + " "
                        + history.getPatient().getLastName() + " - " + history.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        + " - " + "Dr. " + history.getDoctor().getName();
            }

            @Override
            public ClinicalHistory fromString(String string) {
                return null; // No es necesario implementar esto para visualización
            }
        });

        lstHistories.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) { // Doble clic
                Long selectedHistory = lstHistories.getSelectionModel().getSelectedValue().getDocumentId();
                if (selectedHistory != null) {
                    openClinicalHistory(selectedHistory);
                }
            }
        });

        // Configurar el menú contextual para la lista de historias clínicas
        lstHistories.setCellFactory(clinicalHistory -> {
            TableRow<DocumentSummary> row = new TableRow<>();

            MFXListCell<ClinicalHistory> cell = new MFXListCell<>(lstHistories, clinicalHistory);
            ContextMenu contextMenu = new ContextMenu();

            MenuItem openItem = new MenuItem("Abrir");
            openItem.setOnAction(event -> {
                Long selectedHistory = clinicalHistory.getDocumentId();
                if (selectedHistory != null) {
                    openClinicalHistory(selectedHistory);
                }
            });

            MenuItem editItem = new MenuItem("Editar");
            editItem.setOnAction(event -> {
                Long selectedHistory = clinicalHistory.getDocumentId();
                if (selectedHistory != null) {
                    openClinicalHistory(selectedHistory);
                }
            });

            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(event -> {
                DocumentSummary doc = row.getItem();
                if (doc != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    // ... (alert setup code) ...
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            deleteEvolutionNote(doc.getDocumentId());
                            refreshDocumentsTable(); // <-- ADD THIS LINE
                        }
                    });
                }
            });

            contextMenu.getItems().addAll(openItem, editItem, deleteItem);
            cell.setOnContextMenuRequested(event -> {
                if (lstHistories.getSelectionModel() != null) {
                    contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                }
            });

            return cell;
        });

        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        colDocType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        colDocName.setCellValueFactory(new PropertyValueFactory<>("documentName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        tblDocuments.setRowFactory(tv -> {
            TableRow<DocumentSummary> row = new TableRow<>();

            // Crear el menú contextual para documentos
            ContextMenu rowMenu = new ContextMenu();

            MenuItem openItem = new MenuItem("Abrir");
            openItem.setOnAction(event -> {
                DocumentSummary doc = row.getItem();
                if (doc != null && doc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
                    openEvolutionNote(doc.getDocumentId());
                }
            });

            MenuItem editItem = new MenuItem("Editar");
            editItem.setOnAction(event -> {
                DocumentSummary doc = row.getItem();
                if (doc != null && doc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
                    openEvolutionNote(doc.getDocumentId());
                }
            });

            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(event -> {
                DocumentSummary doc = row.getItem();
                if (doc != null) {
                    // Confirmar eliminación
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmación de eliminación");
                    alert.setHeaderText("¿Está seguro de que desea eliminar este documento?");
                    alert.setContentText("Esta acción no se puede deshacer.");
                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            // Eliminar el documento
                            deleteEvolutionNote(doc.getDocumentId());
                        }
                    });
                }
            });

            rowMenu.getItems().addAll(openItem, editItem, deleteItem);

            // Mostrar menú contextual solo en filas no vacías
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(rowMenu)
            );

            // Mantener la funcionalidad de doble clic para abrir
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    DocumentSummary selectedDoc = row.getItem();
                    if (selectedDoc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
                        openEvolutionNote(selectedDoc.getDocumentId());
                    }
                }
            });

            return row;
        });

        stackProfile.setVisible(false);
        stackProfile.setManaged(false);

        btnProfile.setOnAction(event -> {
            boolean isVisible = stackProfile.isVisible();
            stackProfile.setVisible(!isVisible);
            stackProfile.setManaged(!isVisible);
            if (!isVisible) {
                stackProfile.toFront();
            }
        });

        // Hide stackProfile when clicking outside of it
        bgMain.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (stackProfile.isVisible() && !isClickInsideNode(event, stackProfile)) {
                // Check if the click was on btnProfile itself to avoid immediate hiding
                if (!isClickInsideNode(event, btnProfile)){
                    stackProfile.setVisible(false);
                    stackProfile.setManaged(false);
                }
            }
        });

        addEnterKeyListener(profileName);
        addEnterKeyListener(profileProfession);
        addEnterKeyListener(profilePhone);

        changeCarlos.setOnAction(event -> switchUser(11810523L)); // Use Carlos's license
        changeVicente.setOnAction(event -> switchUser(729376L));

        setupCloseHandler();
    }

    // --- Nuevo método helper para mostrar/ocultar paneles ---
    private void togglePaneVisibility(StackPane paneToShow, StackPane paneToHide) {
        // Ocultar el otro panel si está visible
        if (paneToHide != null && paneToHide.isVisible()) {
            paneToHide.setVisible(false);
            paneToHide.setManaged(false);
        }

        // Mostrar/ocultar el panel actual
        boolean isVisible = paneToShow.isVisible();
        paneToShow.setVisible(!isVisible);
        paneToShow.setManaged(!isVisible);
        if (!isVisible) {
            paneToShow.toFront();
        }
    }


    @FXML
    private void toggleNightMode() {
        if (mainScene == null) {
            mainScene = bgMain.getScene(); // Obtener la escena la primera vez
        }

        // Limpia los estilos anteriores antes de aplicar el nuevo
        mainScene.getStylesheets().remove(lightThemeCss);
        mainScene.getStylesheets().remove(nightThemeCss);

        if (isNightMode) {
            // Cambiar a modo claro
            mainScene.getStylesheets().add(lightThemeCss); // O el CSS por defecto
            System.out.println("Cambiando a modo claro");
        } else {
            // Cambiar a modo noche
            mainScene.getStylesheets().add(nightThemeCss);
            System.out.println("Cambiando a modo noche");
        }
        isNightMode = !isNightMode;
    }

    private void applyInitialTheme(Scene scene) {
        ObservableList<String> stylesheets = scene.getStylesheets();

        // Opcional: Cargar preferencia de tema aquí también si no se hizo antes
        // loadThemePreference();

        // Asegúrate de que styles.css (si es un base común) esté cargado.
        // Si styles.css es tu tema claro, entonces defaultThemePath debería apuntar a él.
        // String baseCss = Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm();
        // if (!stylesheets.contains(baseCss)) {
        //     stylesheets.add(baseCss);
        // }

        // Aplicar el tema correspondiente (claro u oscuro)
        if (isNightMode) {
            if (!stylesheets.contains(nightThemePath)) {
                stylesheets.add(nightThemePath);
            }
            stylesheets.remove(defaultThemePath); // Quitar el claro si está
        } else {
            if (!stylesheets.contains(defaultThemePath)) {
                stylesheets.add(defaultThemePath);
            }
            stylesheets.remove(nightThemePath); // Quitar el oscuro si está
        }
    }

    // Este método puede ser llamado por un botón, MenuItem, o el ToggleButton
    @FXML
    private void handleToggleThemeAction() { // Si tienes un botón genérico y no el ToggleButton con su propio listener
        toggleTheme();
    }

    private void toggleTheme() {
        Scene scene = bgMain.getScene();
        if (scene == null) {
            System.err.println("La escena no está disponible. No se puede cambiar el tema.");
            return;
        }
        if (defaultThemePath == null || nightThemePath == null) {
            System.err.println("Rutas de temas no inicializadas. No se puede cambiar el tema.");
            return;
        }

        ObservableList<String> stylesheets = scene.getStylesheets();
        isNightMode = !isNightMode; // Cambia el estado

        if (isNightMode) {
            System.out.println("Cambiando a Modo Noche");
            stylesheets.remove(defaultThemePath); // Quita el tema claro
            if (!stylesheets.contains(nightThemePath)) { // Añade el tema noche si no está ya
                stylesheets.add(nightThemePath);
            }
        } else {
            System.out.println("Cambiando a Modo Día");
            stylesheets.remove(nightThemePath); // Quita el tema noche
            if (!stylesheets.contains(defaultThemePath)) { // Añade el tema claro si no está ya
                stylesheets.add(defaultThemePath);
            }
        }

        if (toggleDarkMode != null) {
            toggleDarkMode.setSelected(isNightMode); // Sincroniza el estado del botón
        }

        // Opcional: Guardar la preferencia del tema
        // saveThemePreference();
    }

    private void switchUser(Long license) {
        try {
            User newUser = userService.getUserById(license);
            if (newUser != null) {
                setLoggedUser(newUser); // Update UI and internal user
                refreshPatientList();   // Refresh the main patient list
                anchorPatient.setVisible(false);
                System.out.println("Usuario cambiado a Dr. " + newUser.getName());
            } else {
                System.err.println("No se encontró el usuario con licencia: " + license);
                // Optionally show an error alert
            }
        } catch (Exception e) {
            System.err.println("Error al cambiar de usuario: " + e.getMessage());
            // Optionally show an error alert
        }
    }

    public void setupCloseHandler() {
        Platform.runLater(() -> { // Asegura que la escena/ventana esté lista
            Stage stage = (Stage) lblUser.getScene().getWindow();
            if (stage != null) {
                stage.setOnCloseRequest(event -> {
                    event.consume(); // Previene el cierre por defecto
                    closeAndOpenLogin(stage); // Llama al nuevo método
                });
            } else {
                System.err.println("Error: No se pudo obtener el Stage para configurar el manejador de cierre.");
                // Considera mostrar un Alert aquí si esto falla consistentemente
            }
        });
    }

    // Nuevo método para cerrar la ventana actual y abrir una nueva de Login
    private void closeAndOpenLogin(Stage currentStage) {
        // 1. Cierra la ventana actual (MainView)
        currentStage.close();

        // 2. Crea y muestra una NUEVA ventana de Login
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/LoginView.fxml"));
            // Asegúrate de que Main.context esté disponible o usa el método getBean apropiado
            fxmlLoader.setControllerFactory(Main.context::getBean);
            Parent loginRoot = fxmlLoader.load();
            Scene loginScene = new Scene(loginRoot);
            loginScene.setFill(Color.TRANSPARENT); // Aplica transparencia a la escena

            Stage loginStage = new Stage(); // Crea una NUEVA instancia de Stage
            loginStage.setScene(loginScene);

            // Establece el estilo ANTES de mostrar la ventana
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.initStyle(StageStyle.TRANSPARENT); // Si tenías ambos, aplica ambos

            loginStage.centerOnScreen();
            loginStage.show(); // Muestra la nueva ventana de login

            // Limpia datos sensibles si es necesario (opcional aquí, ya que la instancia del MainViewController se destruirá)
            this.loggedUser = null;
            // ... otras limpiezas si fuera necesario ...

        } catch (IOException e) {
            e.printStackTrace();
            // Maneja el error crítico (no se puede mostrar el login)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Crítico");
            alert.setHeaderText("No se pudo reiniciar la aplicación.");
            alert.setContentText("Error al cargar la vista de inicio de sesión: " + e.getMessage());
            alert.showAndWait();
            // Podrías considerar System.exit(1); aquí si el login es esencial
        }
    }

    // Helper method to check if click is inside a node or its children
    private boolean isClickInsideNode(MouseEvent event, Node node) {
        Node target = (Node) event.getTarget();
        while (target != null) {
            if (target.equals(node)) {
                return true;
            }
            target = target.getParent();
        }
        return false;
    }

    public void updatePatient(Patient selectedPatient) {
        openPatientForm(selectedPatient); // Llamar con un paciente para editarlo
    }

    @FXML
    private void openForm(ActionEvent event) {
        openPatientForm(null); // Llamamos al método principal pasando null para crear un nuevo paciente
    }

    public void openPatientForm(Patient patient) {
        if (patientFormStage != null && patientFormStage.isShowing()) {
            patientFormStage.toFront();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/PatientForm.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            PatientFormController formController = fxmlLoader.getController();
            formController.setOnPatientSaved(this::refreshPatientList);

            if (patient != null) { // Si es edición, pasamos el paciente al formulario
                formController.setPatientData(patient);
            }

            patientFormStage = new Stage();
            patientFormStage.initModality(Modality.WINDOW_MODAL);
            patientFormStage.initStyle(StageStyle.UNDECORATED);
            patientFormStage.initStyle(StageStyle.TRANSPARENT);
            patientFormStage.setResizable(false);
            patientFormStage.setTitle(patient == null ? "Añadir Paciente" : "Editar Paciente");

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            patientFormStage.setScene(scene);

            patientFormStage.setOnHidden(e -> patientFormStage = null);

            patientFormStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addEvolutionNote(ActionEvent event) {
        Patient selectedPatient = lstPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            // Mostrar un mensaje de error si no hay un paciente seleccionado
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un paciente");
            alert.setContentText("Por favor, seleccione un paciente para añadir una nota de evolución.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/EvolutionNote.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            EvolutionNoteController noteController = fxmlLoader.getController();

            // ---- CAMBIO CLAVE ----
            // En lugar de configurar el paciente y el doctor por separado,
            // llamamos al nuevo método que reinicia completamente el estado del controlador.
            noteController.prepareForNewNote(selectedPatient, loggedUser);
            // ----------------------

            Stage noteStage = new Stage();
            noteStage.setTitle("Nueva nota de evolución");

            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            noteStage.setHeight(screenHeight);

            Scene scene = new Scene(root);
            noteStage.setScene(scene);

            // Refrescar después de cerrar
            noteStage.setOnHidden(event2 -> showPatientDetails(selectedPatient));

            noteStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEvolutionNote(Long documentId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/EvolutionNote.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            EvolutionNoteController noteController = loader.getController();
            EvolutionNote note = evolutionNoteService.getEvolutionNoteById(documentId);

            if (note == null) {
                System.err.println("No se encontró la nota con ID: " + documentId);
                return;
            }

            noteController.setDoctor(loggedUser); // ya lo tenés guardado en tu noteController
            noteController.setPatient(note.getPatient());
            noteController.setEvolutionNote(note); // Necesitamos crear este método (ver siguiente paso)

            Stage stage = new Stage();
            stage.setTitle("Nota de evolución");

            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            stage.setHeight(screenHeight);

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addClinicalHistory(ActionEvent event) {
        Patient selectedPatient = lstPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            // Mostrar un mensaje de error si no hay un paciente seleccionado
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un paciente");
            alert.setContentText("Por favor, seleccione un paciente para añadir una historia clínica.");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/ClinicalHistory.fxml"));
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            ClinicalHistoryController historyController = fxmlLoader.getController();
            historyController.setPatient(selectedPatient);
            historyController.setDoctor(loggedUser);

            Stage historyStage = new Stage();
            historyStage.setTitle("Añadir Historia Clínica");

            Scene scene = new Scene(root);
            historyStage.setScene(scene);

            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            historyStage.setHeight(screenHeight);

            // ⬇️ Añadir esto para refrescar después de cerrar
            historyStage.setOnHidden(event2 -> {
                // Refrescar la lista de documentos del paciente seleccionado
                Patient currentSelectedPatient = lstPatients.getSelectionModel().getSelectedItem();
                if (currentSelectedPatient != null) {
                    showPatientDetails(currentSelectedPatient);
                }
            });

            historyStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openClinicalHistory(Long selectedHistory) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/ClinicalHistory.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            ClinicalHistoryController controller = loader.getController();
            ClinicalHistory history = clinicalHistoryService.getClinicalHistoryById(selectedHistory);

            if (history == null) {
                System.err.println("No se encontró la historia clínica con ID: " + selectedHistory);
                return;
            }

            controller.setDoctor(loggedUser);
            controller.setPatient(history.getPatient());
            controller.setClinicalHistory(history); // Necesitamos crear este método (ver siguiente paso)

            Stage stage = new Stage();
            stage.setTitle("Historia Clínica");

            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            stage.setHeight(screenHeight);

            stage.setScene(new Scene(root));
            // ⬇️ Añadir esto para refrescar después de cerrar si se hicieron cambios (aunque el open es más para ver)
            stage.setOnHidden(event2 -> {
                Patient currentSelectedPatient = lstPatients.getSelectionModel().getSelectedItem();
                if (currentSelectedPatient != null) {
                    showPatientDetails(currentSelectedPatient);
                }
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshPatientList() {
        System.out.println("Refrescando la lista de pacientes en la UI...");
        try {
            // El servicio se encarga de la caché. Esta llamada traerá datos frescos si la caché fue invalidada.
            ObservableList<Patient> updatedPatients = patientService.getAllPatients();
            allPatients.setAll(updatedPatients); // setAll es eficiente para actualizar la UI

            // Limpiar la selección y los detalles del paciente
            lstPatients.getSelectionModel().clearSelection();
            anchorPatient.setVisible(false);
            selectPatientContent.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error refreshing patient list UI: " + e.getMessage());
            e.printStackTrace();
            // Mostrar alerta de error al usuario
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error de Actualización");
            errorAlert.setHeaderText("No se pudo actualizar la lista de pacientes.");
            errorAlert.setContentText("Detalle: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    public void refreshDocumentsTable() {
        Patient selectedPatient = lstPatients.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            tblDocuments.setItems(evolutionNoteService.getEvolutionNotesSummary(selectedPatient.getPatientId()));
        }
    }

    private void showPatientDetails(Patient selectedPatient) {
        // Hacer visible el AnchorPane
        anchorPatient.setVisible(true);
        selectPatientContent.setVisible(false);
        selectPatientContent.setManaged(false);

        // Mostrar la información en los componentes
        lblId.setText(String.valueOf(selectedPatient.getPatientId()));
        lblName.setText(selectedPatient.getName());
        lblLastName.setText(selectedPatient.getLastName());
        lblSecondLastName.setText(selectedPatient.getSecondLastName());
        lblGender.setText(selectedPatient.getGender().getDescription());
        lblBirthDate.setText(selectedPatient.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        int ageInYears = selectedPatient.getBirthDate().until(LocalDate.now()).getYears();
        lblAddress.setText(ageInYears + "años");
        lblPhone.setText(selectedPatient.getPhone());
        lblEmail.setText(selectedPatient.getEmail());
        lblTimestamp.setText(selectedPatient.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        tblDocuments.setItems(evolutionNoteService.getEvolutionNotesSummary(selectedPatient.getPatientId()));

        lstHistories.setItems(clinicalHistoryService.getHistoriesByPatient(selectedPatient.getPatientId()));
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
        System.out.println("Bienvenido, Dr. " + loggedUser.getName());
        lblUser.setText("Dr. " + loggedUser.getName() +
                "\nC.P.: " + loggedUser.getDoctorLicense());
        // --- Start: Populate Profile Fields ---
        if (loggedUser != null) {
            profileName.setText(loggedUser.getName());
            profileLicense.setText(String.valueOf(loggedUser.getDoctorLicense()));
            profileProfession.setText(loggedUser.getSpeciality());
            profilePhone.setText(loggedUser.getPhone());
            // Also update the changeUser button text here (see next section)
            changeUser.setText("Dr. " + loggedUser.getName());
        } else {
            // Clear fields if user is null (e.g., on logout)
            profileName.clear();
            profileLicense.clear();
            profileProfession.clear();
            profilePhone.clear();
            changeUser.setText("Cambiar Usuario"); // Reset button text
        }
    }

    private void addEnterKeyListener(TextField textField) {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleProfileFieldUpdate(textField);
                // Optional: Move focus away or consume event
                textField.getParent().requestFocus(); // Move focus to parent container
                event.consume();
            }
        });
    }

    // Method to handle the update logic
    private void handleProfileFieldUpdate(TextField sourceField) {
        if (loggedUser == null) return; // Should not happen if fields are populated

        String newValue = sourceField.getText().trim();

        boolean changed = false;
        if (sourceField == profileName && !newValue.equals(loggedUser.getName())) {
            loggedUser.setName(newValue);
            changed = true;
        } else if (sourceField == profileProfession && !newValue.equals(loggedUser.getSpeciality())) {
            loggedUser.setSpeciality(newValue);
            changed = true;
        } else if (sourceField == profilePhone && !newValue.equals(loggedUser.getPhone())) {
            loggedUser.setPhone(newValue);
            changed = true;
        }

        if (changed) {
            try {
                userService.saveUser(loggedUser);
                System.out.println("Perfil actualizado para Dr. " + loggedUser.getName());
                // Optional: Show a success indicator briefly
                sourceField.setStyle("-fx-border-color: green; -fx-border-width: 1;");
                // Use a PauseTransition to remove the style after a short delay
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(e -> sourceField.setStyle("")); // Reset style
                pause.play();

            } catch (Exception e) {
                System.err.println("Error al actualizar el perfil: " + e.getMessage());
                // Optional: Show an error indicator
                sourceField.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
                pause.setOnFinished(ev -> sourceField.setStyle("")); // Reset style
                pause.play();
                // You might want to show an Alert dialog too
            }
        }
    }

    /**
     * Aplica filtro a la lista de pacientes en tiempo real según el texto de búsqueda.
     *
     * @param query Texto ingresado por el usuario.
     */
    private void filterPatients(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();

        filteredPatients.setPredicate(patient -> {
            if (lowerCaseQuery.isEmpty()) {
                return true; // Mostrar todos si el campo de búsqueda está vacío
            }

            // Construir el texto con los datos a buscar
            String fullName = String.format("%s %s %s",
                    patient.getName(),
                    patient.getLastName(),
                    patient.getSecondLastName()).toLowerCase();

            String phone = patient.getPhone() != null ? patient.getPhone().toLowerCase() : "";
            String email = patient.getEmail() != null ? patient.getEmail().toLowerCase() : "";

            // Comparar con el texto ingresado
            return fullName.contains(lowerCaseQuery) || phone.contains(lowerCaseQuery) || email.contains(lowerCaseQuery);
        });
    }

    private void sortPatients(Comparator<Patient> comparator) {
        sortedPatients.setComparator(comparator);
    }

    @FXML
    public void orderPatientsYoungToOld() {
        sortPatients(Comparator.comparing(Patient::getBirthDate).reversed()); // más joven primero
    }

    @FXML
    public void orderPatientsOldToYoung() {
        sortPatients(Comparator.comparing(Patient::getBirthDate)); // más viejo primero
    }

    @FXML
    public void orderPatientsNewest() {
        sortPatients(Comparator.comparing(Patient::getTimestamp).reversed()); // más reciente primero
    }

    @FXML
    public void orderPatientsOldest() {
        sortPatients(Comparator.comparing(Patient::getTimestamp)); // más antiguo primero
    }

    private void deleteEvolutionNote(Long documentId) {
        evolutionNoteService.deleteEvolutionNote(documentId);
        refreshDocumentsTable();
    }
}
