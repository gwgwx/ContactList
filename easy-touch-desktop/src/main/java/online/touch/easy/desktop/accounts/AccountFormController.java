/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.accounts;

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
import online.touch.easy.common.model.Account;
import online.touch.easy.common.model.Activity;
import online.touch.easy.common.model.BusinessNote;
import online.touch.easy.common.model.HistoryCommonFieldsInterface;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.desktop.activity.BusinessNoteFormController;
import online.touch.easy.desktop.activity.PhoneLogFormController;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.AccountRepository;
import online.touch.easy.desktop.data.ActivityRepository;
import online.touch.easy.desktop.data.OpportunityRepository;
import online.touch.easy.desktop.opportunity.OpportunityFormController;
import online.touch.easy.desktop.util.AdvancedEditableController;
import org.controlsfx.validation.Validator;
import online.touch.easy.desktop.util.EmailPredicate;
import online.touch.easy.desktop.util.FormUtils;
import org.controlsfx.control.Rating;

/**
 * FXML Controller class
 *
 * @author George
 */
public class AccountFormController extends AdvancedEditableController<Account> {

    private static final Logger LOG = Logger.getLogger(AccountFormController.class.getName());
    @FXML
    private TextField accountNameField;
    @FXML
    private TextField officeField;
    @FXML
    private TextField assignedField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField displayAsField;
    @FXML
    private TextField webAddressField;
    @FXML
    private TextField businessNumberField;
    @FXML
    private TextField businessAddressField;
    @FXML
    private TextField businessContactsField;
    @FXML
    private TextField sourceField;
    @FXML
    private TextField accountNumberField;
    @FXML
    private TextField businessTypeField;
    @FXML
    private TextField businessFaxField;
    @FXML
    private TextField revenueField;
    @FXML
    private TextField tickerSymbolField;
    @FXML
    private TextField employeesField;
    @FXML
    private TextField territoriesField;
    @FXML
    private TextField intialtedField;
    @FXML
    private TextField interestAreaField;
    @FXML
    private TextArea notesArea;
    @FXML
    private ComboBox<String> rating;
    @FXML
    private CheckBox call;
    @FXML
    private CheckBox fax;
    @FXML
    private CheckBox dontEmail;
    @FXML
    private CheckBox mail;
    @FXML
    private ComboBox<String> paymentStatus;
    @FXML
    private TableView<HistoryCommonFieldsInterface> historyTable;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colItemType;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colSubject;

    @FXML
    private TableColumn<HistoryCommonFieldsInterface, String> colLinkedTo;

    private Account account;

    private final AccountRepository repository;

    ObservableList<String> list = FXCollections.observableArrayList("Canceled Reversal", "Completed", "Created", "Denied", "Expired", "Failed", "Pending", "Refunded", "Reversed", "Processed", "voided");
    ObservableList<String> ratingList = FXCollections.observableArrayList("Excellent", "Average", "Fair", "Poor");

    public AccountFormController() {
        super(Account.class);
        repository = AccountRepository.getInstance();
    }

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        configureFields();
        paymentStatus.setItems(list);
        rating.setItems(ratingList);
    }

    private void configureFields() {
        LOG.finer("call configureComboBoxes");

        // set up validations
        validationSupport.registerValidator(accountNameField, Validator.createEmptyValidator("Name is required"));
        validationSupport.registerValidator(emailField, Validator.createPredicateValidator(new EmailPredicate(), "Email format is invalid"));

    }

    @Override
    public Account getSelected() {
        return account;
    }

    @Override
    public Account getEdited() {
        Account edited = new Account();

        edited.setName(stringValue(accountNameField));
        edited.setEmail(stringValue(emailField));
        edited.setInterestArea(stringValue(interestAreaField));
        edited.setAccountNumber(stringValue(accountNumberField));
        edited.setAssigned(stringValue(assignedField));
        edited.setBusinessAddress(stringValue(businessAddressField));
        edited.setBusinessContacts(stringValue(businessContactsField));
        edited.setBusinessFax(stringValue(businessFaxField));
        edited.setBusinessNumber(stringValue(businessNumberField));
        edited.setBusinessType(stringValue(businessTypeField));
        edited.setDisplayAs(stringValue(displayAsField));
        edited.setEmployees(stringValue(employeesField));
        edited.setOffice(stringValue(officeField));
        edited.setWebAdress(stringValue(webAddressField));
        edited.setSource(stringValue(sourceField));
        edited.setRevenue(stringValue(revenueField));
        edited.setTickerSymbol(stringValue(tickerSymbolField));
        edited.setTerritories(stringValue(territoriesField));
        edited.setIntialtedBy(stringValue(intialtedField));
        edited.setNotes(stringValue(notesArea));
        edited.setPaymentStatus(paymentStatus.getValue());
        edited.setRating(rating.getValue());
        

        //checkbox
        if (call.isSelected()) {
            edited.setCall(true);

        } else {
            edited.setCall(false);
        }
        if (fax.isSelected()) {
            edited.setFax(true);

        } else {
            edited.setFax(false);
        }
        if (mail.isSelected()) {
            edited.setMail(true);

        } else {
            edited.setMail(false);
        }
        if (dontEmail.isSelected()) {
            edited.setDontEmail(true);

        } else {
            edited.setDontEmail(false);
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
        accountNameField.setText(account.getName());
        emailField.setText(account.getEmail());
        officeField.setText(account.getOffice());
        assignedField.setText(account.getAssigned());
        emailField.setText(account.getEmail());
        displayAsField.setText(account.getDisplayAs());
        webAddressField.setText(account.getWebAdress());
        businessNumberField.setText(account.getBusinessNumber());
        businessAddressField.setText(account.getBusinessAddress());
        businessContactsField.setText(account.getBusinessContacts());
        sourceField.setText(account.getSource());
        accountNumberField.setText(account.getAccountNumber());
        businessTypeField.setText(account.getBusinessType());
        businessFaxField.setText(account.getBusinessFax());
        revenueField.setText(account.getRevenue());
        tickerSymbolField.setText(account.getTickerSymbol());
        employeesField.setText(account.getEmployees());
        territoriesField.setText(account.getTerritories());
        intialtedField.setText(account.getIntialtedBy());
        interestAreaField.setText(account.getInterestArea());
        notesArea.setText(account.getNotes());
        paymentStatus.setValue(account.getPaymentStatus());
        call.setSelected(account.isCall());
        fax.setSelected(account.isFax());
        dontEmail.setSelected(account.isDontEmail());
        mail.setSelected(account.isMail());
        rating.setValue(account.getRating());

    }

    @Override
    public Parent getRootPane() {
        return accountNameField;
    }

    @Override
    protected SaveState computeSaveState(Account edited, Account selected) {
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
            if (!equal(edited.getBusinessNumber(), selected.getBusinessNumber())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getRating(), selected.getRating())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getPaymentStatus(), selected.getPaymentStatus())) {
                return SaveState.UNSAVED;
            }
            if (!equal(edited.getBusinessNumber(), selected.getBusinessNumber())) {
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
            this.account = repository.find(id);
        } else {
            this.account = new Account();
        }

        updateDetails();
        configureHistoryTable();
    }

    @Override
    public StringProperty titleProperty() {
        StringProperty title = new SimpleStringProperty();
        if (account.getName() != null) {
            title.bind(Bindings.format("Easy Touch - %s", accountNameField.textProperty()));
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
