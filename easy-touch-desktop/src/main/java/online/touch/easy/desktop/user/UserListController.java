/*
 * To change this license header, choose License Headers in User Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.user;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import online.touch.easy.common.model.User;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.UserRepository;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.ListScene;

/**
 * FXML Controller class
 *
 * @author George
 */
public class UserListController extends ListScene<User> {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;

    private final UserRepository repository;

    public UserListController() {
        super(User.class);
        repository = UserRepository.getInstance();
    }
    private static final ObservableList<User>USER_DATA = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
       usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
            userTable.setRowFactory((TableView<User> tv) -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    User rowData = row.getItem();
                    FormUtils.loadAdvancedForm(User.class.getSimpleName(), rowData.getId(), userTable, getMainScene());
                }
            });
            return row;
        });

        // Add observable list data to the table
        userTable.setItems(USER_DATA);
       USER_DATA.clear();
        User[] persons = UserRepository.getInstance().findAll(User[].class);
       USER_DATA.addAll(Arrays.asList(persons));
    }

    @Override
    public TableView<User> getTable() {
        return userTable;
    }

    @Override
    public AbstractRepository getRepository() {
       return repository;
    }

}
