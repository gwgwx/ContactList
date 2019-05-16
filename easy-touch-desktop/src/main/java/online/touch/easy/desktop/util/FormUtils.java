/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;
import javafx.stage.Modality;
import online.touch.easy.common.model.Account;
import online.touch.easy.common.model.BusinessNote;
import online.touch.easy.common.model.Contact;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.common.model.PhoneLog;
import online.touch.easy.common.model.Project;
import online.touch.easy.common.model.User;

/**
 *
 * @author George
 */
public class FormUtils {

    private static final Logger LOG = Logger.getLogger(FormUtils.class.getName());

    private static final Map<String, String> FXMLS = new HashMap<>();

    static {
        FXMLS.put(Contact.class.getSimpleName(), "/fxml/contact/ContactForm.fxml");
        FXMLS.put(Account.class.getSimpleName(), "/fxml/account/AccountForm.fxml");
        FXMLS.put(Project.class.getSimpleName(), "/fxml/project/ProjectForm.fxml");
        FXMLS.put(Opportunity.class.getSimpleName(), "/fxml/opportunity/OpportunityForm.fxml");
        FXMLS.put(BusinessNote.class.getSimpleName(), "/fxml/activity/BusinessNoteForm.fxml");
        FXMLS.put(PhoneLog.class.getSimpleName(), "/fxml/activity/PhoneLogForm.fxml");
        FXMLS.put(User.class.getSimpleName(), "/fxml/user/UserForm.fxml");
        
    }

    public static void loadAdvancedForm(String entityName, Long id, TableView table, MainScene mainScene) {
        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource(FXMLS.get(entityName)));
        Parent root;
        try {
            root = (Parent) loader.load();
            AdvancedEditableController controller = loader.<AdvancedEditableController>getController();
            controller.load(id);
            controller.setTableView(table);
            controller.setMainScene(mainScene);

            Scene scene = new Scene(root, controller.getStageWidth(), controller.getStageHeight());
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

    public static void loadAdvancedForm(String entityName, Long id, TableView table, boolean modal) {
        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource(FXMLS.get(entityName)));
        Parent root;
        try {
            root = (Parent) loader.load();
            AdvancedEditableController controller = loader.<AdvancedEditableController>getController();
            controller.load(id);
            controller.setTableView(table);

            Scene scene = new Scene(root, controller.getStageWidth(), controller.getStageHeight());
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
            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            stage.setScene(scene);
            stage.show();

            scene.getWindow().setOnCloseRequest(ev -> {
                LOG.finer("Stage is closing");
                if (!controller.close()) {
                    ev.consume();
                }
            });

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static void loadSimpleForm(String entityName, Long id) {
        FXMLLoader loader = new FXMLLoader(FormUtils.class.getResource(FXMLS.get(entityName)));
        Parent root;
        try {
            root = (Parent) loader.load();
            SimpleEditableController controller = loader.<SimpleEditableController>getController();
            controller.load(id);

            Scene scene = new Scene(root);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {

                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
                    Stage sb = (Stage) scene.getWindow();//use any one object
                    sb.close();

                }
            });

            Stage stage = new Stage();
            stage.titleProperty().bind(controller.titleProperty());
//            stage.getIcons().add(new Image("images/liberty_logo_icon.png"));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    // modal window intended to display an entity
    public static void showModal(String name, boolean resizable, Long id, Scene owner) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FormUtils.class.getResource(name));
            Parent root = (Parent) fxmlLoader.load();
            SimpleEditableController controller = (SimpleEditableController) fxmlLoader.getController();
            Scene scene = new Scene(root);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
                    Stage sb = (Stage) scene.getWindow();//use any one object
                    sb.close();
                }
            });
            final Stage popUp = new Stage();

//            popUp.setResizable(resizable);
            popUp.initStyle(StageStyle.DECORATED);

            popUp.setScene(scene);
//            popUp.getIcons().add(new Image("images/liberty_logo_icon.png"));
            popUp.titleProperty().bind(controller.titleProperty());
            popUp.initOwner(owner.getWindow());
//            popUp.initModality(Modality.WINDOW_MODAL);
            popUp.show();
            if (id != null) {
                controller.load(id);
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    // modal window not tied
    public static void showModal(String name, String title, Scene owner) {
        showModal(name, title, owner, 0, 0);
    }

    public static void showModal(String name, String title, Scene owner, double width, double height) {
        LOG.finer("call MainSceneController#showModal");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FormUtils.class.getResource(name));
            Parent root = (Parent) fxmlLoader.load();
//                    EditShipController shipController = (EditShipController) fxmlLoader.getController();
//                    shipController.loadShip(Long.valueOf(shipTableView.getSelectionModel().getSelectedItem().getId()));
            Scene scene = new Scene(root);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
                LOG.finer("call MainSceneController#showModal add event hander");
                if (t.getCode() == KeyCode.ESCAPE) {
                    LOG.finer("click on escape");
                    Stage sb = (Stage) scene.getWindow();//use any one object
                    sb.close();
                }
            });
            final Stage popUp = new Stage();
            popUp.setScene(scene);
            popUp.setResizable(false);
            popUp.initStyle(StageStyle.DECORATED);
//            popUp.getIcons().add(new Image("images/liberty_logo_icon.png"));
            popUp.setTitle(title);
            popUp.initOwner(owner.getWindow());
            popUp.initModality(Modality.APPLICATION_MODAL);
            if (width > 0 && height > 0) {
                popUp.setWidth(width);
                popUp.setHeight(height);
            }
            popUp.show();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

}
