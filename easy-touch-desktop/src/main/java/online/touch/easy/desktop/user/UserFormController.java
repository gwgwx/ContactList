/*
 * To change this license header, choose License Headers in User Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.user;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import online.touch.easy.common.model.User;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.UserRepository;
import online.touch.easy.desktop.util.AdvancedEditableController;
import org.controlsfx.validation.Validator;
import online.touch.easy.desktop.util.EmailPredicate;

/**
 * FXML Controller class
 *
 * @author George
 */
public class UserFormController extends AdvancedEditableController<User> {

    private static final Logger LOG = Logger.getLogger(UserFormController.class.getName());
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField password;

    private User User;

    private final UserRepository repository;

    public UserFormController() {
        super(User.class);
        repository = UserRepository.getInstance();
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
        validationSupport.registerValidator(nameField, Validator.createEmptyValidator("Name is required"));
      

    }

    @Override
    public User getSelected() {
        return User;
    }

    @Override
    public User getEdited() {
        User edited = new User();

        edited.setName(stringValue(nameField));
        edited.setUserName(stringValue(usernameField));
        edited.setPassword(stringValue(password));

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
        nameField.setText(User.getName());
        usernameField.setText(User.getUserName());
        password.setText(User.getPassword());
    }

    @Override
    public Parent getRootPane() {
        return nameField;
    }

    @Override
    protected SaveState computeSaveState(User edited, User selected) {
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
            if (!equal(edited.getUserName(), selected.getUserName())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getPassword(), selected.getPassword())) {
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
        return 300;
    }

    @Override
    public void load(Long id) {
        if (id != null) {
            this.User = repository.find(id);
        } else {
            this.User = new User();
        }

        updateDetails();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if (User.getName() != null) {
            title.bind(Bindings.format("Easy Touch - %s", nameField.textProperty()));
        } else {
            title.set("Easy Touch");

        }
        return title;
    }

}
