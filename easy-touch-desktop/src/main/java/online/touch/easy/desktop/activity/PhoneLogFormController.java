/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.activity;

;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import online.touch.easy.common.model.Account;
import online.touch.easy.common.model.ContactMain;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.desktop.converters.AccountConverter;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.AccountRepository;
import online.touch.easy.desktop.data.PhoneLogRepository;
import online.touch.easy.desktop.util.AdvancedEditableController;
import org.controlsfx.validation.Validator;


/**
 * FXML Controller class
 *
 * @author George
 */
public class PhoneLogFormController extends AdvancedEditableController<PhoneLog> {

    private static final Logger LOG = Logger.getLogger(PhoneLogFormController.class.getName());
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea noteArea;

    private PhoneLog phoneLog;
    
    @FXML
    private ComboBox<ContactMain> linkedContact;

    private final PhoneLogRepository repository;
    
    private final AccountRepository accountRepository;

    public PhoneLogFormController() {
        super(PhoneLog.class);
        repository = PhoneLogRepository.getInstance();
        accountRepository = AccountRepository.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        configureFields();
        
    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        // set up validations
        validationSupport.registerValidator(subjectField, Validator.createEmptyValidator("Name is required"));
        validationSupport.registerValidator(linkedContact, Validator.createEmptyValidator("Linked Contact is required"));
       
        
        linkedContact.setConverter(new AccountConverter());
        linkedContact.getItems().addAll(accountRepository.findAll(Account[].class));

    }

    @Override
    public PhoneLog getSelected() {
        return phoneLog;
    }

    public ComboBox<ContactMain> getLinkedContact() {
        return linkedContact;
    }

    @Override
    public PhoneLog getEdited() {
        PhoneLog edited = new PhoneLog();
      
        edited.setSubject(stringValue(subjectField));
        edited.setNote(stringValue(noteArea));
        
        

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
        subjectField.setText( phoneLog.getSubject());
        noteArea.setText( phoneLog.getNote());
       
    }

    @Override
    public Parent getRootPane() {
        return subjectField;
    }

    @Override
    protected SaveState computeSaveState(PhoneLog edited, PhoneLog selected) {
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
            if (!equal(edited.getSubject(), selected.getSubject())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getNote(), selected.getNote())) {
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
            this. phoneLog = repository.find(id);
        } else {
            this. phoneLog = new PhoneLog();
        }

        updateDetails();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if ( phoneLog.getSubject() != null) {
            title.bind(Bindings.format("Easy Touch - %s", subjectField.textProperty()));
        } else {
            title.set("Easy Touch");

        }
        return title;
    }

    

}
