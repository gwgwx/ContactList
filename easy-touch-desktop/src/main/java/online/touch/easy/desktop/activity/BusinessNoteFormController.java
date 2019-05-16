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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import online.touch.easy.common.model.Account;
import online.touch.easy.common.model.ContactMain;
import online.touch.easy.common.model.BusinessNote;
import online.touch.easy.desktop.converters.AccountConverter;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.AccountRepository;
import online.touch.easy.desktop.data.ActivityRepository;
import online.touch.easy.desktop.data.BusinessNoteRepository;
import online.touch.easy.desktop.util.AdvancedEditableController;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationMessage;

/**
 * FXML Controller class
 *
 * @author George
 */
public class BusinessNoteFormController extends AdvancedEditableController<BusinessNote> {

    private static final Logger LOG = Logger.getLogger(BusinessNoteFormController.class.getName());
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea noteArea;
    @FXML
    private ComboBox<ContactMain> linkedContact;
   
    private BusinessNote businessNote;

    private final BusinessNoteRepository repository;
    
    private final AccountRepository accountRepository;
    
    public BusinessNoteFormController() {
        super(BusinessNote.class);
        repository = BusinessNoteRepository.getInstance();
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
    public BusinessNote getSelected() {
        return businessNote;
    }

    public ComboBox<ContactMain> getLinkedContact() {
        return linkedContact;
    }

    @Override
    public BusinessNote getEdited() {
        BusinessNote edited = new BusinessNote();
      
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
        subjectField.setText( businessNote.getSubject());
        noteArea.setText( businessNote.getNote());
       
    }

    @Override
    public Parent getRootPane() {
        return subjectField;
    }

    @Override
    protected SaveState computeSaveState(BusinessNote edited, BusinessNote selected) {
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
            this. businessNote = repository.find(id);
        } else {
            this. businessNote = new BusinessNote();
        }

        updateDetails();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if ( businessNote.getSubject() != null) {
            title.bind(Bindings.format("Easy Touch - %s", subjectField.textProperty()));
        } else {
            title.set("Easy Touch");

        }
        return title;
    }

    /**
     * Called when the SaveIssue button is fired.
     *
     * @param event the action event.
     * @return
     */
    @FXML
    protected boolean save(ActionEvent event) {
        final BusinessNote ref = getSelected();
        final BusinessNote edited = getEdited();
        SaveState saveState = computeSaveState(edited, ref);
        switch (saveState) {
            case INVALID:
                LOG.info("Invalid Data: cannot save form");

                String temp = "";
                for (ValidationMessage message : validationSupport.getValidationResult().getMessages()) {
                    LOG.log(Level.FINER, "validation Message: {0}", message);
                    temp += "\n" + message.getText();
                }

                displayMessage("Invalid Data: cannot save form" + temp);
                return false;
            case UNSAVED:
                if (!canSave()) {
                    return false;
                }
                ActivityRepository ar = ActivityRepository.getInstance();
                ar.createForContact(edited, linkedContact.getValue().getId());
                LOG.info("Issue saved");
                if (getMainScene() != null) {
                    getMainScene().refresh();
                }
                return true;
            default:
                LOG.info("No change needs saving");
                return true;
        }
    }

    

}
