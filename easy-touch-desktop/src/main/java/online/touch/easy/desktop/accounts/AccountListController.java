/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.accounts;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import online.touch.easy.common.model.Account;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.AccountRepository;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.ListScene;

/**
 * FXML Controller class
 *
 * @author George
 */
public class AccountListController extends ListScene<Account> {

    @FXML
    private TableView<Account> accountTable;
    @FXML
    private TableColumn<Account, String> accountNameColumn;
    @FXML
    private TableColumn<Account, String> emailColumn;
    @FXML
    private TableColumn<Account, String> businessNumberColumn;
    @FXML
    private TableColumn<Account, String> businessAddressColumn;
    @FXML
    private TableColumn<Account, String> accountRattingColumn;
    @FXML
    private TableColumn<Account, String> paymentStatusColumn;
    @FXML
    private TableColumn<Account, String> communicationColumn;

    private final AccountRepository repository;

    public AccountListController() {
        super(Account.class);
        repository = AccountRepository.getInstance();
    }
    private static final ObservableList<Account> ACCOUNT_DATA = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        businessNumberColumn.setCellValueFactory(new PropertyValueFactory<>("businessNumber"));
        businessAddressColumn.setCellValueFactory(new PropertyValueFactory<>("businessAddress"));
        accountRattingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        communicationColumn.setCellValueFactory(new PropertyValueFactory<>("communucation"));
        accountTable.setRowFactory((TableView<Account> tv) -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Account rowData = row.getItem();
                    FormUtils.loadAdvancedForm(Account.class.getSimpleName(), rowData.getId(), accountTable, getMainScene());
                }
            });
            return row;
        });

        // Add observable list data to the table
        accountTable.setItems(ACCOUNT_DATA);
        ACCOUNT_DATA.clear();
        Account[] persons = AccountRepository.getInstance().findAll(Account[].class);
        ACCOUNT_DATA.addAll(Arrays.asList(persons));
    }

    @Override
    public TableView<Account> getTable() {
        return accountTable;
    }

    @Override
    public AbstractRepository getRepository() {
        return repository;
    }

}
