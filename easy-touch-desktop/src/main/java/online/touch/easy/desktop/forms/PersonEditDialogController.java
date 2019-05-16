/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.forms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import online.touch.easy.desktop.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import online.touch.easy.common.model.Contact;

import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.ContactRepository;
import online.touch.easy.desktop.util.AdvancedEditableController;
import online.touch.easy.desktop.util.EmailPredicate;
import online.touch.easy.desktop.util.IntegerTextFormatter;
import org.controlsfx.validation.Validator;
import org.jboss.resteasy.util.Base64;

public class PersonEditDialogController extends AdvancedEditableController<Contact> {

    private static final Logger LOG = Logger.getLogger(PersonEditDialogController.class.getName());

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
    private TextArea notesArea;
    @FXML
    private ComboBox<String> titleBox;
    @FXML
    private CheckBox activityCheck;
    @FXML
    private RadioButton maleButton;
    @FXML
    private RadioButton femaleButton;
    @FXML
    private RadioButton otherButton;

    private Contact person;

    private final ContactRepository repository;

    public PersonEditDialogController() {
        super(Contact.class);
        repository = ContactRepository.getInstance();
    }
    ObservableList<String> list = FXCollections.observableArrayList("Miss", "Mr", "Mrs", "Ms");

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        configureFields();
        titleBox.setItems(list);
    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        //set up formats
        postalCodeField.setTextFormatter(new IntegerTextFormatter());
        // set up validations
        validationSupport.registerValidator(lastNameField, Validator.createEmptyValidator("Last Name is required"));
        validationSupport.registerValidator(emailField, Validator.createPredicateValidator(new EmailPredicate(), "Email format is invalid"));

    }

    /**
     * Sets the person to be edited in the dialog.
     *
     * @param id
     */
    @Override
    public void load(Long id) {
        if (id != null) {
            this.person = repository.find(id);
        } else {
            this.person = new Contact();
        }

        updateDetails();

    }

    /**
     * Validates the user input in the text fields.
     *
     * @return true if the input is valid
     */
    @Override
    public Contact getSelected() {
        return person;
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
        edited.setNotes(stringValue(notesArea));
        edited.setTitle(titleBox.getValue());
        edited.setPostalCode(integerValue(postalCodeField));
        edited.setImage(imagepath);

       
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
        notesArea.setText(person.getNotes());
        postalCodeField.setText(Integer.toString(person.getPostalCode()));
        titleBox.setValue(person.getTitle());
        activityCheck.setSelected(person.isActive());
        
        if (person.getImage() != null) {
            Image image = new Image(person.getImage(), 80, 80, false, false);
            imageView.setImage(image);
        }
        
    }

    @Override
    public Parent getRootPane() {
        return firstNameField;
    }

    @Override
    protected SaveState computeSaveState(Contact edited, Contact selected
    ) {
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
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        title.bind(Bindings.format("Easy Touch - %s %s", firstNameField.textProperty(), lastNameField.textProperty()));
        return title;
    }

    @Override
    protected double getStageWidth() {
        return 600;
    }

    @Override
    protected double getStageHeight() {
        return 350;
    }

    private boolean booleanValue(CheckBox activity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
