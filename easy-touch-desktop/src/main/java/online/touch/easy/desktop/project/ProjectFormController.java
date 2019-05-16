/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.project;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import online.touch.easy.common.model.Activity;
import online.touch.easy.common.model.BusinessNote;
import online.touch.easy.common.model.ContactMain;
import online.touch.easy.common.model.HistoryCommonFieldsInterface;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.common.model.Project;
import online.touch.easy.desktop.activity.BusinessNoteFormController;
import online.touch.easy.desktop.activity.PhoneLogFormController;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.ActivityRepository;
import online.touch.easy.desktop.data.OpportunityRepository;
import online.touch.easy.desktop.data.ProjectRepository;
import online.touch.easy.desktop.opportunity.OpportunityFormController;
import online.touch.easy.desktop.util.AdvancedEditableController;
import org.controlsfx.validation.Validator;
import online.touch.easy.desktop.util.EmailPredicate;
import online.touch.easy.desktop.util.FormUtils;

/**
 * FXML Controller class
 *
 * @author George
 */
public class ProjectFormController extends AdvancedEditableController<Project> {

    private static final Logger LOG = Logger.getLogger(ProjectFormController.class.getName());
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<ContactMain> linkedContact;
    @FXML
    private ComboBox<String> projectType;
    @FXML
    private ComboBox<String> projectStatus;
     @FXML
    private TableView<HistoryCommonFieldsInterface> historyTable;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colItemType;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colSubject;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colLinkedTo;

    private Project project;

    private final ProjectRepository repository;

    public ProjectFormController() {
        super(Project.class);
        repository = ProjectRepository.getInstance();
    }
    ObservableList<String> type = FXCollections.observableArrayList("Fixed Fee", "Time and Material");
    ObservableList<String> status = FXCollections.observableArrayList("Started", "In Progress", "Completed", "Cancelled");

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        configureFields();
        projectType.setItems(type);
        projectStatus.setItems(status);
    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        // set up validations
        validationSupport.registerValidator(nameField, Validator.createEmptyValidator("Name is required"));
        
    }

    @Override
    public Project getSelected() {
        return project;
    }

    public ComboBox<ContactMain> getLinkedContact() {
        return linkedContact;
    }

    @Override
    public Project getEdited() {
        Project edited = new Project();

        edited.setName(stringValue(nameField));
        edited.setEmail(stringValue(emailField));
        edited.setProjectStatus(projectStatus.getValue());
        edited.setProjectType(projectType.getValue());

        edited.setCreatedBy(getSelected().getCreatedBy());
        edited.setCreatedOn(getSelected().getCreatedOn());
        edited.setId(getSelected().getId());
        edited.setModifiedBy(getSelected().getModifiedBy());
        edited.setModifiedOn(getSelected().getModifiedOn());
        edited.setUid(getSelected().getUid());
        edited.setVersion(getSelected().getVersion());
        return edited;
    }

    @Override
    protected void updateDetails() {
        nameField.setText(project.getName());
        projectStatus.setValue(project.getProjectStatus());
        projectType.setValue(project.getProjectType());
    }

    @Override
    public Parent getRootPane() {
        return nameField;
    }

    @Override
    protected SaveState computeSaveState(Project edited, Project selected) {
        try {

            if (validationSupport.isInvalid()) {
                return SaveState.INVALID;
            }

            // These fields are not editable - so if they differ they are invalid
            // and we cannot save.
            if (!equal(edited.getId(), selected.getId())) {
                return SaveState.INVALID;
            }
            if (!equal(edited.getUid(), selected.getUid())) {
                return SaveState.INVALID;
            }

            // If these fields differ, the issue needs saving.
            if (!equal(edited.getName(), selected.getName())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getEmail(), selected.getEmail())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getProjectStatus(), selected.getProjectStatus())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getProjectType(), selected.getProjectType())) {
                return SaveState.UNSAVED;
            }
        } catch (Exception x) {
            // If there's an exception, some fields are invalid.
            LOG.log(Level.SEVERE, "error calling computeSaveState", x);
            return SaveState.INVALID;
        }
        // No field is invalid, no field needs saving.
        return SaveState.UNCHANGED;
    }

    @Override
    protected AbstractRepository getRepository() {
        return repository;
    }

    @Override
    protected double getStageWidth() {
        return 500;
    }

    @Override
    protected double getStageHeight() {
        return 500;
    }

    @Override
    public void load(Long id) {
        if (id != null) {
            this.project = repository.find(id);
        } else {
            this.project = new Project();
        }

        updateDetails();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if (project.getName() != null) {
            title.bind(Bindings.format("Easy Touch - %s", nameField.textProperty()));
        } else {
            title.set("Easy Touch");

        }
        return title;
    }
        @FXML
    private Button addActivity;

    private void configureHistoryTable() {

        colItemType.setCellValueFactory((TableColumn.CellDataFeatures<HistoryCommonFieldsInterface, String> param) -> {
            HistoryCommonFieldsInterface activity = param.getValue();
            if (activity instanceof BusinessNote) {
                return new SimpleStringProperty("Business Note");
            } else if (activity instanceof PhoneLog) {
                return new SimpleStringProperty("Phone Log");
            }
            return new SimpleStringProperty(null);
        });

        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));

        colLinkedTo.setCellValueFactory((TableColumn.CellDataFeatures<HistoryCommonFieldsInterface, String> param) -> {
            if (param.getValue() == null) {
                return new SimpleStringProperty(null);
            }
            return new SimpleStringProperty(getEdited().getName());
        });

        ActivityRepository ar = ActivityRepository.getInstance();
           historyTable.setRowFactory((TableView<HistoryCommonFieldsInterface> tv) -> {
            TableRow<HistoryCommonFieldsInterface> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    HistoryCommonFieldsInterface rowData = row.getItem();
                    FormUtils.loadAdvancedForm(rowData.getClass().getSimpleName(), rowData.getId(), historyTable, getMainScene());
                }
            });
            return row;
        });

        Activity[] activities = ar.findAll(Activity[].class,
                getEdited().getId());

        historyTable.getItems().addAll(activities);

        OpportunityRepository or = OpportunityRepository.getInstance();

        Opportunity[] opportunity = or.findAll(Opportunity[].class,
                getEdited().getId());
        System.err.println("called ops respo");

        historyTable.getItems().addAll(opportunity);
    }

    @FXML
    private void newBusinessNote(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource("/fxml/activity/BusinessNoteForm.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            AdvancedEditableController controller = loader.<AdvancedEditableController>getController();
            controller.load(null);
            controller.setTableView(historyTable);
            controller.setMainScene(getMainScene());
            //cast to OpportunityForm and set the linked to
            BusinessNoteFormController ofc = (BusinessNoteFormController) controller;
            ofc.getLinkedContact().setValue(this.getEdited());

            Scene scene = new Scene(root, this.getStageWidth(), this.getStageHeight());
            scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {

                LOG.finer("call FormUtils#load add event hander");
                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
//                    Stage sb = (Stage) scene.getWindow();//use any one object
//                    sb.close();
                    t.consume();
                    if (controller.close()) {
                        controller.getRootPane().getScene().getWindow().hide();
                    }

                }
            });

            Stage stage = new Stage();
            stage.titleProperty().bind(controller.titleProperty());
//            stage.getIcons().add(new Image("images/liberty_logo_icon.png"));
            stage.setScene(scene);
            stage.show();

            scene.getWindow().setOnCloseRequest(ev -> {
                if (!controller.close()) {
                    ev.consume();
                }
            });

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void newPhoneLog(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource("/fxml/activity/PhoneLogForm.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            AdvancedEditableController controller = loader.<AdvancedEditableController>getController();
            controller.load(null);
            controller.setTableView(historyTable);
            controller.setMainScene(getMainScene());
            //cast to OpportunityForm and set the linked to
            PhoneLogFormController ofc = (PhoneLogFormController) controller;
            ofc.getLinkedContact().setValue(this.getEdited());

            Scene scene = new Scene(root, this.getStageWidth(), this.getStageHeight());
            scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {

                LOG.finer("call FormUtils#load add event hander");
                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
//                    Stage sb = (Stage) scene.getWindow();//use any one object
//                    sb.close();
                    t.consume();
                    if (controller.close()) {
                        controller.getRootPane().getScene().getWindow().hide();
                    }

                }
            });

            Stage stage = new Stage();
            stage.titleProperty().bind(controller.titleProperty());
//            stage.getIcons().add(new Image("images/liberty_logo_icon.png"));
            stage.setScene(scene);
            stage.show();

            scene.getWindow().setOnCloseRequest(ev -> {
                if (!controller.close()) {
                    ev.consume();
                }
            });

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void newOpportunity(ActionEvent event) {
        //TODO need to develop a reusable appraoch to pass additional params to the form

        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource("/fxml/opportunity/OpportunityForm.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            AdvancedEditableController controller = loader.<AdvancedEditableController>getController();
            controller.load(null);
            controller.setTableView(historyTable);
            controller.setMainScene(getMainScene());
            //cast to OpportunityForm and set the linked to
            OpportunityFormController ofc = (OpportunityFormController) controller;
            ofc.getLinkedContact().setValue(this.getEdited());

            Scene scene = new Scene(root, this.getStageWidth(), this.getStageHeight());
            scene.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {

                LOG.finer("call FormUtils#load add event hander");
                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
//                    Stage sb = (Stage) scene.getWindow();//use any one object
//                    sb.close();
                    t.consume();
                    if (controller.close()) {
                        controller.getRootPane().getScene().getWindow().hide();
                    }

                }
            });

            Stage stage = new Stage();
            stage.titleProperty().bind(controller.titleProperty());
//            stage.getIcons().add(new Image("images/liberty_logo_icon.png"));
            stage.setScene(scene);
            stage.show();

            scene.getWindow().setOnCloseRequest(ev -> {
                if (!controller.close()) {
                    ev.consume();
                }
            });

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
//        FormUtils.loadAdvancedForm(Opportunity.class.getSimpleName(), null, historyTable, getMainScene());
    }
    

}
