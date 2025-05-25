//package com.alann616.consulter.controller;
//
//import com.alann616.consulter.Main;
//import com.alann616.consulter.components.PatientCell;
//import com.alann616.consulter.enums.DocumentType;
//import com.alann616.consulter.model.Patient;
//import com.alann616.consulter.model.User;
//import com.alann616.consulter.model.doctordocs.ClinicalHistory;
//import com.alann616.consulter.model.doctordocs.EvolutionNote;
//import com.alann616.consulter.service.ClinicalHistoryService;
//import com.alann616.consulter.service.EvolutionNoteService;
//import com.alann616.consulter.service.PatientService;
//import com.alann616.consulter.service.UserService;
//import com.alann616.consulter.util.DocumentSummary;
//import io.github.palexdev.materialfx.controls.MFXListView;
//import io.github.palexdev.materialfx.controls.MFXToggleButton;
//import io.github.palexdev.materialfx.controls.cell.MFXListCell;
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.collections.transformation.FilteredList;
//import javafx.collections.transformation.SortedList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.stage.Modality;
//import javafx.stage.Screen;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.util.StringConverter;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.URL; // Importar URL
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Comparator;
//import java.util.Objects;
//
//import static com.alann616.consulter.Main.context;
//
//@Component
//public class MainTest {
//
//    // --- Inyecciones y Servicios ---
//    @Autowired @Getter @Setter
//    private PatientService patientService;
//    @Autowired @Getter @Setter
//    private UserService userService;
//    @Autowired
//    private EvolutionNoteService evolutionNoteService;
//    @Autowired
//    private ClinicalHistoryService clinicalHistoryService;
//
//    // --- Componentes FXML ---
//    @FXML private AnchorPane bgMain; // Panel Raíz
//    @FXML private SplitPane splitMain; // SplitPane principal
//    @FXML private AnchorPane leftSplit; // Panel izquierdo
//    @FXML private AnchorPane rightSplit; // Panel derecho
//    @FXML private VBox selectPatientContent; // Contenido inicial derecho
//    @FXML private AnchorPane anchorPatient; // Contenido detalles derecho
//    @FXML private Label lblUser;
//    @FXML private ListView<Patient> lstPatients;
//    @FXML private TextField txtSearch;
//    @FXML private MFXListView<ClinicalHistory> lstHistories;
//    @FXML private TableView<DocumentSummary> tblDocuments;
//    @FXML private TableColumn<DocumentSummary, LocalDateTime> colTimestamp;
//    @FXML private TableColumn<DocumentSummary, Long> colDocumentId;
//    @FXML private TableColumn<DocumentSummary, DocumentType> colDocType;
//    @FXML private TableColumn<DocumentSummary, String> colDocName;
//    @FXML private TableColumn<DocumentSummary, String> colDoctorName;
//    @FXML private Label lblId, lblName, lblLastName, lblSecondLastName, lblGender,
//            lblBirthDate, lblAddress, lblPhone, lblEmail, lblTimestamp;
//    @FXML private Button btnPatients, btnProfile, btnSettings, btnAddPatient;
//    @FXML private MenuButton changeUser;
//    @FXML private MenuItem changeCarlos, changeVicente;
//    @FXML private RadioMenuItem btnYoungToOld, btnOldToYoung, btnNewest, btnOldest;
//    @FXML private ToggleGroup toggleOrder;
//    @FXML private StackPane stackProfile, stackSettings;
//    @FXML private TextField profileName, profileLicense, profileProfession, profilePhone;
//    @FXML private MFXToggleButton toggleDarkMode; // Toggle para modo oscuro
//
//    // --- Variables de Estado y Datos ---
//    private Stage patientFormStage = null;
//    private FilteredList<Patient> filteredPatients;
//    private SortedList<Patient> sortedPatients;
//    @Getter private User loggedUser;
//    private ObservableList<Patient> allPatients;
//
//    // --- Variables de Tema ---
//    private boolean isNightMode = false; // Estado actual del tema
//    private String lightThemePath;       // Ruta al CSS del tema claro (styles.css)
//    private String nightThemePath;       // Ruta al CSS del tema oscuro (mainview-night.css)
//
//
//    @FXML
//    public void initialize() {
//        // --- Inicialización de Rutas CSS ---
//        try {
//            URL lightThemeUrl = getClass().getResource("/css/styles.css"); // CSS principal del tema claro
//            URL nightThemeUrl = getClass().getResource("/css/mainview-night.css");
//
//            Objects.requireNonNull(lightThemeUrl, "styles.css no encontrado en /css/");
//            Objects.requireNonNull(nightThemeUrl, "mainview-night.css no encontrado en /css/");
//
//            lightThemePath = lightThemeUrl.toExternalForm();
//            nightThemePath = nightThemeUrl.toExternalForm();
//
//        } catch (NullPointerException e) {
//            System.err.println("Error crítico: No se pudo cargar un archivo CSS esencial. Verifica las rutas.");
//            e.printStackTrace();
//            if (toggleDarkMode != null) toggleDarkMode.setDisable(true);
//            return; // Salir si los CSS no cargan
//        } catch (Exception e) {
//            System.err.println("Error inesperado inicializando rutas CSS: " + e.getMessage());
//            e.printStackTrace();
//            if (toggleDarkMode != null) toggleDarkMode.setDisable(true);
//            return;
//        }
//
//        // --- Configuración Inicial del Tema y Toggle ---
//        // loadThemePreference(); // Opcional: Cargar preferencia guardada
//        if (toggleDarkMode != null) {
//            toggleDarkMode.setSelected(isNightMode);
//            // ASIGNAR LA ACCIÓN CORRECTA AL TOGGLE
//            toggleDarkMode.setOnAction(event -> handleThemeToggle()); // Llama al método unificado
//        } else {
//            System.err.println("Advertencia: toggleDarkMode no encontrado en FXML.");
//        }
//
//        // Aplicar tema inicial cuando la escena esté lista
//        bgMain.sceneProperty().addListener((obs, oldScene, newScene) -> {
//            if (newScene != null) {
//                System.out.println("Escena disponible. Aplicando tema inicial.");
//                updateStylesheets(newScene); // Aplicar tema basado en isNightMode inicial
//            }
//        });
//
//
//        // --- Inicialización Paneles Ocultos (Perfil y Ajustes) ---
//        setupPaneVisibilityToggle(btnProfile, stackProfile, stackSettings);
//        setupPaneVisibilityToggle(btnSettings, stackSettings, stackProfile);
//        setupAutoHideOnClickOutside(bgMain, stackProfile, btnProfile);
//        setupAutoHideOnClickOutside(bgMain, stackSettings, btnSettings);
//
//
//        // --- Configuración Lista de Pacientes ---
//        allPatients = FXCollections.observableArrayList();
//        filteredPatients = new FilteredList<>(allPatients, p -> true);
//        sortedPatients = new SortedList<>(filteredPatients, Comparator.comparing(Patient::getName)); // Orden inicial por nombre
//        lstPatients.setItems(sortedPatients);
//        lstPatients.setCellFactory(param -> new PatientCell(this)); // Asumiendo que PatientCell existe y funciona
//        lstPatients.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//            if (newSelection != null) {
//                showPatientDetails(newSelection);
//            } else {
//                // Opcional: Limpiar detalles si no hay selección
//                anchorPatient.setVisible(false);
//                selectPatientContent.setVisible(true);
//                selectPatientContent.setManaged(true);
//            }
//        });
//
//        // --- Configuración Búsqueda ---
//        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> filterPatients(newValue));
//
//        // --- Configuración Lista de Historias Clínicas (MFXListView) ---
//        configureHistoryList();
//
//        // --- Configuración Tabla de Documentos ---
//        configureDocumentsTable();
//
//        // --- Configuración Perfil ---
//        configureProfileSection();
//
//        // --- Configuración Cierre de Ventana ---
//        setupCloseHandler();
//
//        // --- Carga Inicial ---
//        refreshPatientList(); // Cargar pacientes al inicio
//        // setLoggedUser(...); // Asegúrate de llamar a esto desde donde obtienes el usuario logueado
//    }
//
//    // --- Lógica de Cambio de Tema (Unificada y Corregida) ---
//
//    @FXML
//    private void handleThemeToggle() {
//        Scene scene = bgMain.getScene();
//        if (scene == null) {
//            System.err.println("Error: La escena no está disponible para cambiar tema.");
//            return;
//        }
//        isNightMode = !isNightMode; // Invierte el estado ANTES de actualizar
//        System.out.println("Cambiando a modo: " + (isNightMode ? "Noche" : "Día"));
//        updateStylesheets(scene); // Aplica el nuevo tema
//        // saveThemePreference(); // Opcional: Guardar preferencia
//        // El estado del botón MFXToggleButton debería actualizarse solo, pero si no:
//        // if (toggleDarkMode != null) toggleDarkMode.setSelected(isNightMode);
//    }
//
//    /**
//     * Método centralizado para actualizar las hojas de estilo de la escena.
//     * Elimina AMBAS hojas de tema (claro y oscuro) y luego añade la correcta.
//     * @param scene La escena a la que aplicar los estilos.
//     */
//    private void updateStylesheets(Scene scene) {
//        if (lightThemePath == null || nightThemePath == null) {
//            System.err.println("Error: Rutas de temas no inicializadas.");
//            return;
//        }
//
//        ObservableList<String> stylesheets = scene.getStylesheets();
//        System.out.println("Hojas ANTES de actualizar: " + stylesheets);
//
//        // --- Lógica CRÍTICA: Eliminar AMBAS hojas primero ---
//        boolean removedLight = stylesheets.remove(lightThemePath);
//        boolean removedNight = stylesheets.remove(nightThemePath);
//        // Opcional: Imprimir si se eliminaron
//        // System.out.println("¿Se eliminó tema claro? " + removedLight);
//        // System.out.println("¿Se eliminó tema noche? " + removedNight);
//
//
//        // --- Añadir la hoja correcta ---
//        if (isNightMode) {
//            if (!stylesheets.contains(nightThemePath)) { // Evitar añadir duplicados (aunque add() lo maneja)
//                stylesheets.add(nightThemePath);
//                System.out.println("Añadido tema Noche: " + nightThemePath);
//            }
//        } else {
//            if (!stylesheets.contains(lightThemePath)) {
//                stylesheets.add(lightThemePath); // Añadir tema claro (styles.css)
//                System.out.println("Añadido tema Claro: " + lightThemePath);
//            }
//        }
//
//        System.out.println("Hojas DESPUÉS de actualizar (" + (isNightMode ? "Noche" : "Día") + "): " + stylesheets);
//    }
//
//    // --- Métodos de Configuración (Separados de initialize) ---
//
//    private void configureHistoryList() {
//        lstHistories.setConverter(new StringConverter<ClinicalHistory>() {
//            @Override
//            public String toString(ClinicalHistory history) {
//                if (history == null) return "";
//                return "Historia clínica: " + history.getPatient().getName() + " "
//                        + history.getPatient().getLastName() + " - " + history.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
//                        + " - " + "Dr. " + history.getDoctor().getName();
//            }
//            @Override
//            public ClinicalHistory fromString(String string) { return null; }
//        });
//
//        lstHistories.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            if (event.getClickCount() == 2) {
//                ClinicalHistory selectedHistory = lstHistories.getSelectionModel().getSelectedValue();
//                if (selectedHistory != null) {
//                    openClinicalHistory(selectedHistory.getDocumentId());
//                }
//            }
//        });
//
//        // Configurar menú contextual para lstHistories (si es necesario)
//        // ... (código del menú contextual similar al de tblDocuments si lo necesitas) ...
//        lstHistories.setCellFactory(lv -> {
//            MFXListCell<ClinicalHistory> cell = new MFXListCell<>(lstHistories, null); // El segundo parámetro es el dato, se setea después
//            ContextMenu contextMenu = new ContextMenu();
//            // ... Definir MenuItems (Abrir, Editar, Eliminar) y sus acciones ...
//            MenuItem openItem = new MenuItem("Abrir");
//            openItem.setOnAction(event -> {
//                ClinicalHistory history = cell.getData(); // Obtener el dato de la celda
//                if (history != null) openClinicalHistory(history.getDocumentId());
//            });
//            // ... añadir items al contextMenu ...
//            contextMenu.getItems().addAll(openItem /*, otros items */);
//            cell.setContextMenu(contextMenu);
//            return cell;
//        });
//    }
//
//    private void configureDocumentsTable() {
//        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
//        colDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
//        colDocType.setCellValueFactory(new PropertyValueFactory<>("documentType"));
//        colDocName.setCellValueFactory(new PropertyValueFactory<>("documentName"));
//        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
//
//        // Formatear la columna de fecha/hora
//        colTimestamp.setCellFactory(column -> new TableCell<DocumentSummary, LocalDateTime>() {
//            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
//            @Override
//            protected void updateItem(LocalDateTime item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty || item == null) {
//                    setText(null);
//                } else {
//                    setText(formatter.format(item));
//                }
//            }
//        });
//
//
//        tblDocuments.setRowFactory(tv -> {
//            TableRow<DocumentSummary> row = new TableRow<>();
//            ContextMenu rowMenu = new ContextMenu();
//
//            MenuItem openItem = new MenuItem("Abrir");
//            openItem.setOnAction(event -> {
//                DocumentSummary doc = row.getItem();
//                if (doc != null) {
//                    if (doc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
//                        openEvolutionNote(doc.getDocumentId());
//                    } else if (doc.getDocumentType() == DocumentType.CLINICAL_HISTORY) {
//                        // Asumiendo que el ID en DocumentSummary para historia es el correcto
//                        openClinicalHistory(doc.getDocumentId());
//                    }
//                }
//            });
//
//            MenuItem editItem = new MenuItem("Editar");
//            editItem.setOnAction(event -> {
//                DocumentSummary doc = row.getItem();
//                if (doc != null) {
//                    if (doc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
//                        openEvolutionNote(doc.getDocumentId()); // Abrir para editar
//                    } else if (doc.getDocumentType() == DocumentType.CLINICAL_HISTORY) {
//                        openClinicalHistory(doc.getDocumentId()); // Abrir para editar
//                    }
//                }
//            });
//
//
//            MenuItem deleteItem = new MenuItem("Eliminar");
//            deleteItem.setOnAction(event -> {
//                DocumentSummary doc = row.getItem();
//                if (doc != null) {
//                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de que desea eliminar este documento?", ButtonType.YES, ButtonType.NO);
//                    alert.setTitle("Confirmación");
//                    alert.setHeaderText("Eliminar " + doc.getDocumentType().getDescription());
//                    alert.showAndWait().ifPresent(response -> {
//                        if (response == ButtonType.YES) {
//                            try {
//                                if (doc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
//                                    evolutionNoteService.deleteEvolutionNote(doc.getDocumentId());
//                                } else if (doc.getDocumentType() == DocumentType.CLINICAL_HISTORY) {
//                                    clinicalHistoryService.deleteClinicalHistory(doc.getDocumentId());
//                                }
//                                refreshDocumentsTable(); // Refrescar la tabla
//                            } catch (Exception e) {
//                                System.err.println("Error al eliminar documento: " + e.getMessage());
//                                // Mostrar alerta de error
//                            }
//                        }
//                    });
//                }
//            });
//
//            rowMenu.getItems().addAll(openItem, editItem, new SeparatorMenuItem(), deleteItem);
//
//            row.contextMenuProperty().bind(
//                    javafx.beans.binding.Bindings.when(row.emptyProperty())
//                            .then((ContextMenu)null)
//                            .otherwise(rowMenu)
//            );
//
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && !row.isEmpty()) {
//                    DocumentSummary selectedDoc = row.getItem();
//                    if (selectedDoc.getDocumentType() == DocumentType.EVOLUTION_NOTE) {
//                        openEvolutionNote(selectedDoc.getDocumentId());
//                    } else if (selectedDoc.getDocumentType() == DocumentType.CLINICAL_HISTORY) {
//                        openClinicalHistory(selectedDoc.getDocumentId());
//                    }
//                }
//            });
//
//            return row;
//        });
//    }
//
//    private void configureProfileSection() {
//        stackProfile.setVisible(false);
//        stackProfile.setManaged(false);
//
//        // Listener para actualizar al presionar Enter
//        addEnterKeyListener(profileName);
//        addEnterKeyListener(profileProfession);
//        addEnterKeyListener(profilePhone);
//
//        // Acciones para cambiar usuario
//        changeCarlos.setOnAction(event -> switchUser(11810523L));
//        changeVicente.setOnAction(event -> switchUser(729376L));
//    }
//
//    // --- Métodos Helper para UI ---
//
//    private void setupPaneVisibilityToggle(Button triggerButton, StackPane paneToShow, StackPane otherPane) {
//        paneToShow.setVisible(false);
//        paneToShow.setManaged(false);
//        if (triggerButton != null) {
//            triggerButton.setOnAction(event -> {
//                // Ocultar el otro panel si está visible
//                if (otherPane != null && otherPane.isVisible()) {
//                    otherPane.setVisible(false);
//                    otherPane.setManaged(false);
//                }
//                // Mostrar/ocultar el panel actual
//                boolean isVisible = paneToShow.isVisible();
//                paneToShow.setVisible(!isVisible);
//                paneToShow.setManaged(!isVisible);
//                if (!isVisible) {
//                    paneToShow.toFront(); // Asegurar que esté al frente si se muestra
//                }
//            });
//        } else {
//            System.err.println("Advertencia: Botón trigger no encontrado para " + paneToShow.getId());
//        }
//    }
//
//    private void setupAutoHideOnClickOutside(Node rootNode, StackPane paneToHide, Button triggerButton) {
//        rootNode.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
//            if (paneToHide.isVisible() && !isClickInsideNode(event, paneToHide)) {
//                // Solo ocultar si el clic NO fue en el botón que lo abre
//                if (triggerButton == null || !isClickInsideNode(event, triggerButton)) {
//                    paneToHide.setVisible(false);
//                    paneToHide.setManaged(false);
//                }
//            }
//        });
//    }
//
//    private boolean isClickInsideNode(MouseEvent event, Node node) {
//        if (node == null) return false;
//        Node target = (Node) event.getTarget();
//        while (target != null) {
//            if (target.equals(node)) {
//                return true;
//            }
//            target = target.getParent();
//        }
//        return false;
//    }
//
//    private void addEnterKeyListener(TextField textField) {
//        textField.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                handleProfileFieldUpdate(textField);
//                textField.getParent().requestFocus();
//                event.consume();
//            }
//        });
//    }
//
//    // --- Métodos de Lógica de Negocio / Datos ---
//
//    public void setLoggedUser(User loggedUser) {
//        this.loggedUser = loggedUser;
//        if (loggedUser != null) {
//            System.out.println("Bienvenido, Dr. " + loggedUser.getName());
//            lblUser.setText("Dr. " + loggedUser.getName() + "\nC.P.: " + loggedUser.getDoctorLicense());
//            profileName.setText(loggedUser.getName());
//            profileLicense.setText(String.valueOf(loggedUser.getDoctorLicense()));
//            profileProfession.setText(loggedUser.getSpeciality());
//            profilePhone.setText(loggedUser.getPhone());
//            changeUser.setText("Dr. " + loggedUser.getName()); // Actualizar texto del MenuButton
//        } else {
//            // Limpiar UI si no hay usuario
//            lblUser.setText("Dr. Desconocido");
//            profileName.clear();
//            profileLicense.clear();
//            profileProfession.clear();
//            profilePhone.clear();
//            changeUser.setText("Cambiar Usuario");
//        }
//        // Seleccionar el RadioMenuItem correspondiente al usuario actual
//        if (toggleChangeUser != null && loggedUser != null) {
//            if (loggedUser.getDoctorLicense() == 11810523L) {
//                toggleChangeUser.selectToggle(changeCarlos.getGraphic().getParent().getProperties().get("javafx.scene.control.RadioMenuItem") != null ? (Toggle) changeCarlos.getGraphic().getParent().getProperties().get("javafx.scene.control.RadioMenuItem") : null); // Esto es complejo, revisar API de RadioMenuItem
//                // Mejor usar fx:id directamente si es posible: toggleChangeUser.selectToggle(changeCarlos);
//            } else if (loggedUser.getDoctorLicense() == 729376L) {
//                // toggleChangeUser.selectToggle(changeVicente);
//            }
//        }
//    }
//
//    private void switchUser(Long license) {
//        try {
//            User newUser = userService.getUserById(license);
//            if (newUser != null) {
//                setLoggedUser(newUser);
//                refreshPatientList();
//                anchorPatient.setVisible(false); // Ocultar detalles del paciente anterior
//                selectPatientContent.setVisible(true); // Mostrar mensaje inicial
//                selectPatientContent.setManaged(true);
//                System.out.println("Usuario cambiado a Dr. " + newUser.getName());
//            } else {
//                System.err.println("No se encontró el usuario con licencia: " + license);
//            }
//        } catch (Exception e) {
//            System.err.println("Error al cambiar de usuario: " + e.getMessage());
//        }
//    }
//
//    public void refreshPatientList() {
//        System.out.println("Refrescando lista de pacientes...");
//        try {
//            allPatients.setAll(patientService.getAllPatients()); // Más eficiente que clear/addAll
//            System.out.println("Lista actualizada con " + allPatients.size() + " pacientes.");
//        } catch (Exception e) {
//            System.err.println("Error refrescando lista de pacientes: " + e.getMessage());
//            e.printStackTrace();
//            // Mostrar alerta
//        }
//    }
//
//    private void filterPatients(String query) {
//        String lowerCaseQuery = query.toLowerCase().trim();
//        filteredPatients.setPredicate(patient -> {
//            if (lowerCaseQuery.isEmpty()) return true;
//            String fullName = String.format("%s %s %s", patient.getName(), patient.getLastName(), patient.getSecondLastName()).toLowerCase();
//            String phone = patient.getPhone() != null ? patient.getPhone().toLowerCase() : "";
//            String email = patient.getEmail() != null ? patient.getEmail().toLowerCase() : "";
//            return fullName.contains(lowerCaseQuery) || phone.contains(lowerCaseQuery) || email.contains(lowerCaseQuery);
//        });
//    }
//
//    private void sortPatients(Comparator<Patient> comparator) {
//        // Asegurarse de que sortedPatients esté vinculado a filteredPatients
//        if (sortedPatients.getComparator() == null || !sortedPatients.getComparator().equals(comparator)) {
//            sortedPatients.setComparator(comparator);
//        }
//    }
//
//    @FXML private void orderPatientsYoungToOld() { sortPatients(Comparator.comparing(Patient::getBirthDate).reversed()); }
//    @FXML private void orderPatientsOldToYoung() { sortPatients(Comparator.comparing(Patient::getBirthDate)); }
//    @FXML private void orderPatientsNewest() { sortPatients(Comparator.comparing(Patient::getTimestamp).reversed()); }
//    @FXML private void orderPatientsOldest() { sortPatients(Comparator.comparing(Patient::getTimestamp)); }
//
//    private void showPatientDetails(Patient patient) {
//        if (patient == null) {
//            anchorPatient.setVisible(false);
//            selectPatientContent.setVisible(true);
//            selectPatientContent.setManaged(true);
//            return;
//        }
//        anchorPatient.setVisible(true);
//        selectPatientContent.setVisible(false);
//        selectPatientContent.setManaged(false);
//
//        lblId.setText(String.valueOf(patient.getPatientId()));
//        lblName.setText(patient.getName());
//        lblLastName.setText(patient.getLastName());
//        lblSecondLastName.setText(patient.getSecondLastName());
//        lblGender.setText(patient.getGender().getDescription());
//        lblBirthDate.setText(patient.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//        int ageInYears = patient.getBirthDate().until(LocalDate.now()).getYears();
//        lblAddress.setText(ageInYears + " años"); // Corregido "años"
//        lblPhone.setText(patient.getPhone());
//        lblEmail.setText(patient.getEmail());
//        lblTimestamp.setText(patient.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//
//        refreshDocumentsTable(patient); // Cargar documentos del paciente seleccionado
//        refreshHistoryList(patient); // Cargar historias del paciente seleccionado
//    }
//
//    private void refreshDocumentsTable(Patient patient) {
//        if (patient != null) {
//            try {
//                tblDocuments.setItems(evolutionNoteService.getEvolutionNotesSummary(patient.getPatientId()));
//            } catch (Exception e) {
//                System.err.println("Error cargando documentos: " + e.getMessage());
//                tblDocuments.setItems(FXCollections.emptyObservableList()); // Limpiar tabla en caso de error
//            }
//        } else {
//            tblDocuments.setItems(FXCollections.emptyObservableList()); // Limpiar si no hay paciente
//        }
//    }
//    private void refreshHistoryList(Patient patient) {
//        if (patient != null) {
//            try {
//                lstHistories.setItems(clinicalHistoryService.getHistoriesByPatient(patient.getPatientId()));
//            } catch (Exception e) {
//                System.err.println("Error cargando historias: " + e.getMessage());
//                lstHistories.setItems(FXCollections.emptyObservableList());
//            }
//        } else {
//            lstHistories.setItems(FXCollections.emptyObservableList());
//        }
//    }
//
//
//    private void handleProfileFieldUpdate(TextField sourceField) {
//        if (loggedUser == null) return;
//        String newValue = sourceField.getText().trim();
//        boolean changed = false;
//        // ... (lógica de comparación y seteo) ...
//        if (sourceField == profileName && !newValue.equals(loggedUser.getName())) { loggedUser.setName(newValue); changed = true; }
//        else if (sourceField == profileProfession && !newValue.equals(loggedUser.getSpeciality())) { loggedUser.setSpeciality(newValue); changed = true; }
//        else if (sourceField == profilePhone && !newValue.equals(loggedUser.getPhone())) { loggedUser.setPhone(newValue); changed = true; }
//
//        if (changed) {
//            try {
//                userService.saveUser(loggedUser);
//                System.out.println("Perfil actualizado para Dr. " + loggedUser.getName());
//                // ... (feedback visual de éxito) ...
//                sourceField.setStyle("-fx-border-color: green; -fx-border-width: 1;");
//                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
//                pause.setOnFinished(e -> sourceField.setStyle(null)); // Usar null para resetear
//                pause.play();
//            } catch (Exception e) {
//                System.err.println("Error al actualizar el perfil: " + e.getMessage());
//                // ... (feedback visual de error) ...
//                sourceField.setStyle("-fx-border-color: red; -fx-border-width: 1;");
//                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
//                pause.setOnFinished(ev -> sourceField.setStyle(null)); // Usar null para resetear
//                pause.play();
//            }
//        }
//    }
//
//    // --- Métodos para Abrir Formularios/Ventanas ---
//
//    @FXML
//    private void openForm(ActionEvent event) { openPatientForm(null); }
//    public void updatePatient(Patient selectedPatient) { openPatientForm(selectedPatient); }
//
//    private void openPatientForm(Patient patient) {
//        if (patientFormStage != null && patientFormStage.isShowing()) {
//            patientFormStage.toFront();
//            return;
//        }
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/PatientForm.fxml"));
//            fxmlLoader.setControllerFactory(context::getBean);
//            Parent root = fxmlLoader.load();
//            PatientFormController formController = fxmlLoader.getController();
//            formController.setOnPatientSaved(this::refreshPatientList); // Pasar el método de refresco
//            if (patient != null) formController.setPatientData(patient);
//
//            patientFormStage = new Stage();
//            // ... (configuración del stage: modality, style, title, scene) ...
//            patientFormStage.initModality(Modality.WINDOW_MODAL); // Bloquea la ventana principal
//            patientFormStage.initOwner(bgMain.getScene().getWindow()); // Asocia con la ventana principal
//            patientFormStage.initStyle(StageStyle.TRANSPARENT); // Sin decoración
//            patientFormStage.setResizable(false);
//            patientFormStage.setTitle(patient == null ? "Añadir Paciente" : "Editar Paciente");
//            Scene scene = new Scene(root);
//            scene.setFill(Color.TRANSPARENT); // Fondo transparente
//            patientFormStage.setScene(scene);
//            patientFormStage.setOnHidden(e -> patientFormStage = null); // Limpiar referencia al cerrar
//            patientFormStage.showAndWait(); // Esperar a que se cierre
//        } catch (IOException e) {
//            e.printStackTrace(); // Manejar error
//        }
//    }
//
//    @FXML
//    public void addEvolutionNote(ActionEvent event) {
//        Patient selectedPatient = lstPatients.getSelectionModel().getSelectedItem();
//        if (selectedPatient == null) { /* Mostrar alerta */ return; }
//        openEvolutionNoteWindow(null, selectedPatient); // Pasar null para nueva nota
//    }
//    private void openEvolutionNote(Long documentId) {
//        EvolutionNote note = evolutionNoteService.getEvolutionNoteById(documentId);
//        if (note != null) {
//            openEvolutionNoteWindow(note, note.getPatient());
//        } else { /* Manejar error: nota no encontrada */ }
//    }
//    private void openEvolutionNoteWindow(EvolutionNote noteToLoad, Patient patient) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/EvolutionNote.fxml"));
//            fxmlLoader.setControllerFactory(context::getBean);
//            Parent root = fxmlLoader.load();
//            EvolutionNoteController controller = fxmlLoader.getController();
//            controller.setPatient(patient); // Siempre pasar paciente
//            controller.setDoctor(loggedUser);
//            if (noteToLoad != null) {
//                controller.setEvolutionNote(noteToLoad); // Cargar datos existentes
//            }
//            Stage noteStage = new Stage();
//            // ... (configuración del stage: title, height, scene) ...
//            noteStage.setTitle(noteToLoad == null ? "Nueva Nota de Evolución" : "Ver/Editar Nota de Evolución");
//            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
//            noteStage.setHeight(screenHeight * 0.9); // Usar 90% de la altura
//            noteStage.setScene(new Scene(root));
//            noteStage.initOwner(bgMain.getScene().getWindow()); // Asociar con ventana principal
//            noteStage.initModality(Modality.WINDOW_MODAL); // Modal para esperar
//            noteStage.setOnHidden(event -> refreshDocumentsTable(patient)); // Refrescar al cerrar
//            noteStage.showAndWait();
//        } catch (IOException e) { e.printStackTrace(); }
//    }
//
//    @FXML
//    public void addClinicalHistory(ActionEvent event) {
//        Patient selectedPatient = lstPatients.getSelectionModel().getSelectedItem();
//        if (selectedPatient == null) { /* Mostrar alerta */ return; }
//        openClinicalHistoryWindow(null, selectedPatient); // Pasar null para nueva historia
//    }
//    private void openClinicalHistory(Long documentId) {
//        ClinicalHistory history = clinicalHistoryService.getClinicalHistoryById(documentId);
//        if (history != null) {
//            openClinicalHistoryWindow(history, history.getPatient());
//        } else { /* Manejar error: historia no encontrada */ }
//    }
//    private void openClinicalHistoryWindow(ClinicalHistory historyToLoad, Patient patient) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/ClinicalHistory.fxml"));
//            fxmlLoader.setControllerFactory(context::getBean);
//            Parent root = fxmlLoader.load();
//            ClinicalHistoryController controller = fxmlLoader.getController();
//            controller.setPatient(patient);
//            controller.setDoctor(loggedUser);
//            if (historyToLoad != null) {
//                controller.setClinicalHistory(historyToLoad); // Cargar datos existentes
//            }
//            Stage historyStage = new Stage();
//            // ... (configuración del stage: title, height, scene) ...
//            historyStage.setTitle(historyToLoad == null ? "Nueva Historia Clínica" : "Ver/Editar Historia Clínica");
//            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
//            historyStage.setHeight(screenHeight * 0.95); // Usar 95% de la altura
//            historyStage.setScene(new Scene(root));
//            historyStage.initOwner(bgMain.getScene().getWindow());
//            historyStage.initModality(Modality.WINDOW_MODAL);
//            historyStage.setOnHidden(event -> refreshHistoryList(patient)); // Refrescar al cerrar
//            historyStage.showAndWait();
//        } catch (IOException e) { e.printStackTrace(); }
//    }
//
//    // --- Método de Cierre ---
//    private void setupCloseHandler() {
//        Platform.runLater(() -> {
//            Stage stage = (Stage) bgMain.getScene().getWindow();
//            if (stage != null) {
//                stage.setOnCloseRequest(event -> {
//                    event.consume();
//                    closeAndOpenLogin(stage);
//                });
//            } else {
//                System.err.println("Error: No se pudo obtener el Stage para configurar cierre.");
//            }
//        });
//    }
//    private void closeAndOpenLogin(Stage currentStage) {
//        currentStage.close();
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alann616/consulter/LoginView.fxml"));
//            fxmlLoader.setControllerFactory(Main.context::getBean);
//            Parent loginRoot = fxmlLoader.load();
//            Scene loginScene = new Scene(loginRoot);
//            loginScene.setFill(Color.TRANSPARENT);
//            Stage loginStage = new Stage();
//            loginStage.setScene(loginScene);
//            loginStage.initStyle(StageStyle.TRANSPARENT); // Aplicar estilo transparente
//            loginStage.centerOnScreen();
//            loginStage.show();
//        } catch (IOException e) {
//            e.printStackTrace(); // Manejar error crítico
//            // Mostrar Alerta
//        }
//    }
//
//    // --- Métodos de Eliminación ---
//    private void deleteEvolutionNote(Long documentId) {
//        try {
//            evolutionNoteService.deleteEvolutionNote(documentId);
//            refreshDocumentsTable(lstPatients.getSelectionModel().getSelectedItem()); // Refrescar tabla
//        } catch (Exception e) {
//            System.err.println("Error eliminando nota de evolución: " + e.getMessage());
//            // Mostrar alerta de error
//        }
//    }
//    // Necesitarás un método similar para eliminar historias clínicas si lo permites
//    private void deleteClinicalHistory(Long documentId) {
//        try {
//            clinicalHistoryService.deleteClinicalHistory(documentId);
//            refreshHistoryList(lstPatients.getSelectionModel().getSelectedItem()); // Refrescar lista de historias
//        } catch (Exception e) {
//            System.err.println("Error eliminando historia clínica: " + e.getMessage());
//            // Mostrar alerta de error
//        }
//    }
//
//}
//
