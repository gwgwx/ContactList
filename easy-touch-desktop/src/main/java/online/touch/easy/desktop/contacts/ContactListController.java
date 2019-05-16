/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.contacts;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import online.touch.easy.common.model.Contact;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.ContactRepository;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.ListScene;

/**
 * FXML Controller class
 *
 * @author George
 */
public class ContactListController extends ListScene<Contact> {

    @FXML
    private TableView<Contact> personTable;
    @FXML
    private TableColumn<Contact, String> firstNameColumn;
    @FXML
    private TableColumn<Contact, String> lastNameColumn;
    @FXML
    private TableColumn<Contact, String> homeAddressColumn;
    @FXML
    private TableColumn<Contact, String> otherAddressColumn;
    @FXML
    private TableColumn<Contact, String> emailColumn;
    @FXML
    private TableColumn<Contact, String> notesColumn;
    @FXML
    private TableColumn<Contact, String> mobileNumberColumn;
    @FXML
    private TableColumn<Contact, String> otherNumberColumn;
    @FXML
    private TableColumn<Contact, Date> birthdayColumn;
    @FXML
    private TableColumn<Contact, String> genderColumn;
    @FXML
    private TableColumn<Contact, String> postalCodeColumn;
    @FXML
    private TableColumn<Contact, String> titleColumn;
    @FXML
    private TableColumn<Contact, String> imageColumn;

    private final ContactRepository repository;

    public ContactListController() {
        super(Contact.class);
        repository = ContactRepository.getInstance();
    }

    private static final ObservableList<Contact> PERSON_DATA = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        // Initialize the person table
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        homeAddressColumn.setCellValueFactory(new PropertyValueFactory<>("homeAddress"));
        otherAddressColumn.setCellValueFactory(new PropertyValueFactory<>("otherAddress"));
        mobileNumberColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        otherNumberColumn.setCellValueFactory(new PropertyValueFactory<>("otherNumber"));
        postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postal code"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        imageColumn.setCellFactory(col -> new TableCell<Contact, String>() {
            ImageView imageview = new ImageView();

            @Override
            public void updateItem(String valid, boolean empty) {
                super.updateItem(valid, empty);
                if (valid != null) {
                    imageview.setImage(new Image(valid));
                    imageview.setFitWidth(90);
                    imageview.setFitHeight(90);
                    imageview.setPreserveRatio(true);
                    imageview.setSmooth(true);
                    setGraphic(imageview);
                } else {

                    setGraphic(null);
                }
            }
        });

//        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
// Custom rendering of the table cell.
        birthdayColumn.setCellFactory(column -> {
            return new TableCell<Contact, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // Format date.
//                        setText(myDateFormatter.format(item));
                        setText(new SimpleDateFormat("dd.MM.yyyy").format(item));

                        // Style all dates in March with a different color.
                    }
                }
            };
        });

        personTable.setRowFactory((TableView<Contact> tv) -> {
            TableRow<Contact> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Contact rowData = row.getItem();
                    FormUtils.loadAdvancedForm(Contact.class.getSimpleName(), rowData.getId(), personTable, getMainScene());
                }
            });
            return row;
        });

        // Add observable list data to the table
        personTable.setItems(PERSON_DATA);
        PERSON_DATA.clear();
        Contact[] persons = ContactRepository.getInstance().findAll(Contact[].class);
        PERSON_DATA.addAll(Arrays.asList(persons));
    }    

    @Override
    public TableView<Contact> getTable() {
        return personTable;
    }

    @Override
    public AbstractRepository getRepository() {
        return repository;
    }
    
}
