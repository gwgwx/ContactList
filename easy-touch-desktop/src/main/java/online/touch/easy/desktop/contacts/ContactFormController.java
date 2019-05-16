/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.contacts;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import online.touch.easy.common.model.Activity;
import online.touch.easy.common.model.BusinessNote;
import online.touch.easy.common.model.Contact;
import online.touch.easy.common.model.ContactMain;
import online.touch.easy.common.model.HistoryCommonFieldsInterface;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.desktop.activity.BusinessNoteFormController;
import online.touch.easy.desktop.activity.PhoneLogFormController;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.ActivityRepository;
import online.touch.easy.desktop.data.ContactRepository;
import online.touch.easy.desktop.data.OpportunityRepository;
import online.touch.easy.desktop.opportunity.OpportunityFormController;
import online.touch.easy.desktop.util.AdvancedEditableController;
import online.touch.easy.desktop.util.DateUtil;
import online.touch.easy.desktop.util.EmailPredicate;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.IntegerTextFormatter;
import org.controlsfx.validation.Validator;

/**
 * FXML Controller class
 *
 * @author George
 */
public class ContactFormController extends AdvancedEditableController<Contact> {

    private static final Logger LOG = Logger.getLogger(ContactFormController.class.getName());

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField mobileNumberField;
    @FXML
    private TextField otherNumberField;
    @FXML
    private TextField homeAddressField;
    @FXML
    private TextField otherAddressField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private DatePicker anniversaryPicker;
    @FXML
    private TextArea notesArea;
    @FXML
    private ComboBox<String> titleBox;
    @FXML
    private CheckBox activityCheck;
    @FXML
    private ComboBox<String> paymentBox;
    @FXML
    private ComboBox<String> interestAreaBox;
    @FXML
    private ComboBox<String> ratingBox;
    @FXML
    private ComboBox<ContactMain> linkedContact;
    @FXML
    private TableView<HistoryCommonFieldsInterface> historyTable;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colItemType;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colSubject;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colLinkedTo;

    private Contact person;

    private final ContactRepository repository;

    ObservableList<String> list = FXCollections.observableArrayList("Miss", "Mr", "Mrs", "Ms");
    ObservableList<String> paymentList = FXCollections.observableArrayList("Current", "Overdue");
    ObservableList<String> interestAreaList = FXCollections.observableArrayList("Products", "Services", "Consulting");
    ObservableList<String> ratingList = FXCollections.observableArrayList("Excellent", "Average", "Fair", "Poor");

    public ContactFormController() {
        super(Contact.class);
        repository = ContactRepository.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        configureFields();
        titleBox.setItems(list);
        paymentBox.setItems(paymentList);
        interestAreaBox.setItems(interestAreaList);
        ratingBox.setItems(ratingList);
    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        //set up formats
        postalCodeField.setTextFormatter(new IntegerTextFormatter());
        // set up validations
        validationSupport.registerValidator(lastNameField, Validator.createEmptyValidator("Last Name is required"));
        validationSupport.registerValidator(emailField, Validator.createPredicateValidator(new EmailPredicate(), "Email format is invalid"));

    }

    @Override
    public Contact getSelected() {
        return person;
    }

    public ComboBox<ContactMain> getLinkedContact() {
        return linkedContact;
    }

    @Override
    public Contact getEdited() {

        Contact edited = new Contact();

        edited.setFirstName(stringValue(firstNameField));
        edited.setLastName(stringValue(lastNameField));
        edited.setEmail(stringValue(emailField));
        edited.setHomeAddress(stringValue(homeAddressField));
        edited.setOtherAddress(stringValue(otherAddressField));
        edited.setMobileNumber(stringValue(mobileNumberField));
        edited.setOtherNumber(stringValue(otherNumberField));
        edited.setBirthday(DateUtil.toDate(birthdayPicker.getValue()));
        edited.setBirthday(DateUtil.toDate(anniversaryPicker.getValue()));
        edited.setNotes(stringValue(notesArea));
        edited.setTitle(titleBox.getValue());
        edited.setPostalCode(integerValue(postalCodeField));
        edited.setPaymentStatus(paymentBox.getValue());
        edited.setInterestArea(interestAreaBox.getValue());
        edited.setContactRating(ratingBox.getValue());

        //checkbox
        if (activityCheck.isSelected()) {
            edited.setActive(true);

        } else {
            edited.setActive(false);
        }

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

        firstNameField.setText(person.getFirstName());
        lastNameField.setText(person.getLastName());
        emailField.setText(person.getEmail());
        homeAddressField.setText(person.getHomeAddress());
        otherNumberField.setText(person.getOtherNumber());
        otherAddressField.setText(person.getOtherAddress());
        mobileNumberField.setText(person.getMobileNumber());
        birthdayPicker.setValue(DateUtil.toLocalDate(person.getBirthday()));
        anniversaryPicker.setValue(DateUtil.toLocalDate(person.getAnniversary()));
        notesArea.setText(person.getNotes());
        postalCodeField.setText(Integer.toString(person.getPostalCode()));
        titleBox.setValue(person.getTitle());
        paymentBox.setValue(person.getPaymentStatus());
        interestAreaBox.setValue(person.getInterestArea());
        ratingBox.setValue(person.getContactRating());
        activityCheck.setSelected(person.isActive());

    }

    @Override
    public Parent getRootPane() {
        return firstNameField;
    }

    @Override
    protected SaveState computeSaveState(Contact edited, Contact selected) {
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
            if (!equal(edited.getFirstName(), selected.getFirstName())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getLastName(), selected.getLastName())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getMobileNumber(), selected.getMobileNumber())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getOtherNumber(), selected.getOtherNumber())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getHomeAddress(), selected.getHomeAddress())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getOtherAddress(), selected.getOtherAddress())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getEmail(), selected.getEmail())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getBirthday(), selected.getBirthday())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getNotes(), selected.getNotes())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getPostalCode(), selected.getPostalCode())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getTitle(), selected.getTitle())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getPaymentStatus(), selected.getPaymentStatus())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getInterestArea(), selected.getInterestArea())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getContactRating(), selected.getContactRating())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getAnniversary(), selected.getAnniversary())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.isActive(), selected.isActive())) {
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
        return 880;
    }

    @Override
    protected double getStageHeight() {
        return 580;
    }

    @Override
    public void load(Long id) {
        if (id != null) {
            this.person = repository.find(id);
        } else {
            this.person = new Contact();
        }

        updateDetails();
        configureHistoryTable();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if (person.getFirstName() != null) {
            title.bind(Bindings.format("Easy Touch - %s %s", firstNameField.textProperty(), lastNameField.textProperty()));
        } else if (person.getLastName() != null) {

            title.bind(Bindings.format("Easy Touch - %s ", lastNameField.textProperty()));
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

//        addActivity.setOnAction(event -> {
//            Long contactId = getEdited().getId();
//            BusinessNote note = new BusinessNote();
//            note.setSubject("Subject of: " + new Date());
//            ar.createForContact(note, contactId);
//        });
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
