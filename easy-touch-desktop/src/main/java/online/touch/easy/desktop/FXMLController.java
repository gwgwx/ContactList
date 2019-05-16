package online.touch.easy.desktop;

import com.sun.javafx.stage.StageHelper;
import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import online.touch.easy.desktop.util.ListScene;
import online.touch.easy.desktop.util.MainScene;
import online.touch.easy.desktop.util.ViewScene;

public class FXMLController implements MainScene {

    private static final Logger LOG = Logger.getLogger(FXMLController.class.getName());

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private MenuItem selectAllMenuItem;

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    private MenuItem cmdOpenSelected;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    private ViewScene loadedScene;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public FXMLController() {

    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.NONE);
        alert.initOwner(mainApp.getStage());
        alert.setTitle("Easy Touch");
        alert.setHeaderText(null);
        alert.setContentText("Author: Gwgw");
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480, 320);
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void initialize() {

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void refresh() {
        LOG.fine("call refresh");
        String s = (String) mainBorderPane.getUserData();
        if (s != null && !s.isEmpty()) {
            loadScene(s);
        }
    }

    @FXML
    private void selectAllAction(ActionEvent ev) {
        if (loadedScene != null && (loadedScene instanceof ListScene)) {
            ListScene s = (ListScene) loadedScene;
            TableView tableView = s.getTable();
            tableView.getSelectionModel().selectAll();
            tableView.requestFocus();
        }
    }

    @FXML
    private void newButtonAction(ActionEvent event) {
        LOG.info("call MainSceneController#newButtonAction!");
        if (loadedScene instanceof ListScene) {
            ((ListScene) loadedScene).createNew();
        } else {

        }
    }

    @FXML
    private void editButtonAction(ActionEvent event) {
        LOG.fine("call MainSceneController#newButtonAction");
        if (loadedScene instanceof ListScene) {
            ((ListScene) loadedScene).editSelected();
        }
    }

    @FXML
    void deleteButtonAction(ActionEvent event) {
        Alert alert = new Alert(
                AlertType.CONFIRMATION,
                "Are you sure that you want to permanently delete the selected item(s)?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Easy Touch");
        alert.setHeaderText(null);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            LOG.finer("user clicked YES");
            if (loadedScene instanceof ListScene) {
                ListScene scene = (ListScene) loadedScene;
                scene.deleteSelected();
            }

        } else if (result.get() == ButtonType.NO) {
            LOG.fine("user clicked not");
            // do nothing
        }

    }

    private void setTextFill(Color RED) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadScene(String name) {
        try {
            FXMLLoader ldr = new FXMLLoader(getClass().getResource(name));
            mainBorderPane.setCenter(ldr.load());
            mainBorderPane.setUserData(name);

            loadedScene = (ViewScene) ldr.getController();
            loadedScene.setMainScene(this);

            deleteMenuItem.disableProperty().unbind();
            deleteButton.disableProperty().unbind();
            editButton.disableProperty().unbind();
            cmdOpenSelected.disableProperty().unbind();

            if (loadedScene != null) {
                if (loadedScene instanceof ListScene) {
                    deleteMenuItem.disableProperty().bind(((ListScene) loadedScene).getTable().getSelectionModel().selectedItemProperty().isNull());
                    deleteButton.disableProperty().bind(deleteMenuItem.disableProperty());
                    editButton.disableProperty().bind(deleteMenuItem.disableProperty());
                    cmdOpenSelected.disableProperty().bind(deleteMenuItem.disableProperty());
                }
                selectAllMenuItem.setDisable(false);
            } else {
                selectAllMenuItem.setDisable(true);
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void loadContactListAction(ActionEvent event) {
        String name = "/fxml/contact/ContactList.fxml";
        loadScene(name);

    }

    @FXML
    private void loadAccountListAction(ActionEvent event) {
        String name = "/fxml/account/AccountList.fxml";
        loadScene(name);

    }

    @FXML
    private void loadProjectAction(ActionEvent event) {
        String name = "/fxml/project/ProjectList.fxml";
        loadScene(name);

    }

    @FXML
    private void loadOpportunityAction(ActionEvent event) {
        String name = "/fxml/opportunity/OpportunityList.fxml";
        loadScene(name);

    }

    @FXML
    private void loadUserAction(ActionEvent event) {
        String name = "/fxml/user/UserList.fxml";
        loadScene(name);

    }
}
