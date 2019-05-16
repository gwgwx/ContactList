/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.opportunity;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import online.touch.easy.common.model.Account;
import online.touch.easy.common.model.ContactMain;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.desktop.converters.AccountConverter;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.AccountRepository;
import online.touch.easy.desktop.data.OpportunityRepository;
import online.touch.easy.desktop.util.AdvancedEditableController;
import online.touch.easy.desktop.util.DateUtil;
import org.controlsfx.validation.Validator;
import online.touch.easy.desktop.util.EmailPredicate;

/**
 * FXML Controller class
 *
 * @author George
 */
public class OpportunityFormController extends AdvancedEditableController<Opportunity> {

    private static final Logger LOG = Logger.getLogger(OpportunityFormController.class.getName());
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> type;
    @FXML
    private ComboBox<String> salesStage;
    @FXML
    private CheckBox initial;
    @FXML
    private CheckBox follow;
    @FXML
    private CheckBox interest;
    @FXML
    private DatePicker initialSales;
    @FXML
    private DatePicker followUp;
    @FXML
    private DatePicker closeDate;
    @FXML
    private DatePicker expirationDate;
    @FXML
    private DatePicker deliveryDate;
    @FXML
    private DatePicker reminder;
    @FXML
    private DatePicker interestKnown;
    @FXML
    private ComboBox<ContactMain> linkedContact;

    private Opportunity opportunity;

    private final OpportunityRepository repository;

    private final AccountRepository accountRepository;

    
    ObservableList<String> sales = FXCollections.observableArrayList("Prospecting", "Qualification", "Needs Analysis", "Proposal", "Negotiation", "Closed Won", "Closed Lost");
    ObservableList<String> types = FXCollections.observableArrayList("Standard", "Bulk", "Special Order");
    

    public OpportunityFormController() {
        super(Opportunity.class);
        repository = OpportunityRepository.getInstance();
        accountRepository = AccountRepository.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        configureFields();
        type.setItems(types);
        salesStage.setItems(sales);

    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        // set up validations
        validationSupport.registerValidator(nameField, Validator.createEmptyValidator("Name is required"));
        validationSupport.registerValidator(linkedContact, Validator.createEmptyValidator("Linked Contact is required"));
        validationSupport.registerValidator(emailField, Validator.createPredicateValidator(new EmailPredicate(), "Email format is invalid"));

        linkedContact.setConverter(new AccountConverter());
        linkedContact.getItems().addAll(accountRepository.findAll(Account[].class));

    }

    @Override
    public Opportunity getSelected() {
        return opportunity;
    }

    public ComboBox<ContactMain> getLinkedContact() {
        return linkedContact;
    }

    @Override
    public Opportunity getEdited() {
        Opportunity edited = new Opportunity();

        edited.setName(stringValue(nameField));
        edited.setEmail(stringValue(emailField));
        edited.setParent(linkedContact.getValue());
        edited.setType(type.getValue());
        edited.setSalesStage(salesStage.getValue());
        edited.setInitialSales(DateUtil.toDate(initialSales.getValue()));
        edited.setExpirationDate(DateUtil.toDate(expirationDate.getValue()));
        edited.setDeliveryDate(DateUtil.toDate(deliveryDate.getValue()));
        edited.setReminder(DateUtil.toDate(reminder.getValue()));
        edited.setCloseDate(DateUtil.toDate(closeDate.getValue()));

        if (initial.isSelected()) {
            edited.setInitialSales(DateUtil.toDate(initialSales.getValue()));
        } else {
            edited.setInitialSales(null);
        }
        if (follow.isSelected()) {
            edited.setFollowUp(DateUtil.toDate(followUp.getValue()));

        } else {
            edited.setFollowUp(null);
        }
        if (interest.isSelected()) {
            edited.setInterestKnown(DateUtil.toDate(interestKnown.getValue()));
        } else {
            edited.setInterestKnown(null);
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
        nameField.setText(opportunity.getName());
        emailField.setText(opportunity.getEmail());
        linkedContact.setValue(opportunity.getParent());
        type.setValue(opportunity.getType());
        salesStage.setValue(opportunity.getSalesStage());
        initialSales.setValue(DateUtil.toLocalDate(opportunity.getInitialSales()));
        followUp.setValue(DateUtil.toLocalDate(opportunity.getFollowUp()));
        closeDate.setValue(DateUtil.toLocalDate(opportunity.getCloseDate()));
        expirationDate.setValue(DateUtil.toLocalDate(opportunity.getExpirationDate()));
        deliveryDate.setValue(DateUtil.toLocalDate(opportunity.getDeliveryDate()));
        reminder.setValue(DateUtil.toLocalDate(opportunity.getReminder()));
        

        if (followUp != null) {

            follow.setSelected(true);
        }
        if (initialSales != null) {

            initial.setSelected(true);
        }
        if (interestKnown != null) {

            interest.setSelected(true);
        }
    }

    @Override
    public Parent getRootPane() {
        return nameField;
    }

    @Override
    protected SaveState computeSaveState(Opportunity edited, Opportunity selected) {
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
            if (!equal(edited.getParent(), selected.getParent())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getSalesStage(), selected.getSalesStage())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getInitialSales(), selected.getInitialSales())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getFollowUp(), selected.getFollowUp())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getInterestKnown(), selected.getInterestKnown())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getType(), selected.getType())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getCloseDate(), selected.getCloseDate())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getExpirationDate(), selected.getExpirationDate())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getDeliveryDate(), selected.getDeliveryDate())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getReminder(), selected.getReminder())) {
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
            this.opportunity = repository.find(id);
        } else {
            this.opportunity = new Opportunity();
        }

        updateDetails();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if (opportunity.getName() != null) {
            title.bind(Bindings.format("Easy Touch - %s", nameField.textProperty()));
        } else {
            title.set("Easy Touch");

        }
        return title;
    }

}
