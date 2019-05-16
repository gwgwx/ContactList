/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import online.touch.easy.common.model.Identifiable;
import online.touch.easy.desktop.data.AbstractRepository;

/**
 *
 * @author George
 * @param <T>
 */
public abstract class ListScene<T extends Identifiable> extends ViewScene {

    @FXML
    private MenuItem deleteMenuItem;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    public abstract TableView<T> getTable();

    private final Class<T> entityClass;

    public ListScene(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void createNew() {
        FormUtils.loadAdvancedForm(entityClass.getSimpleName(), null, null, getMainScene());
    }

    public void editSelected() {

        TableView<T> tv = getTable();
        if (tv.getSelectionModel().getSelectedItems() != null && tv.getSelectionModel().getSelectedItems().size() > 10) {
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "No more than ten records can be opened simultaneously?");
            alert.setTitle("Liberty Vessel Client");
            alert.setHeaderText(null);
            alert.showAndWait();
        } else if (!tv.getSelectionModel().isEmpty()) {
            tv.getSelectionModel().getSelectedItems().stream().forEach(o -> {
                FormUtils.loadAdvancedForm(o.getClass().getSimpleName(), ((T) o).getId(), tv, getMainScene());
            });
        }
    }

    public void deleteSelected() {
        if (!getTable().getSelectionModel().isEmpty()) {
            getRepository().remove(getTable().getSelectionModel().getSelectedItem());
//            getTable().getItems().remove(getTable().getSelectionModel().getSelectedIndex());
        }
    }

    protected void updateDeleteIssueButtonState() {
        boolean disable = true;
        if (deleteMenuItem != null && getTable() != null) {
            final boolean nothingSelected = getTable().getSelectionModel().getSelectedItems().isEmpty();
            disable = nothingSelected;
        }
        if (deleteMenuItem != null) {
            deleteMenuItem.setDisable(disable);
        }
        if (editButton != null) {
            editButton.setDisable(disable);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(disable);
        }
    }

    protected void configureButtons() {
        if (deleteMenuItem != null) {
            deleteMenuItem.setDisable(true);
        }
        if (editButton != null) {
            editButton.setDisable(true);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(true);
        }
    }

    protected String formatDate(long date) {
        final SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        return format.format(new Date(date));
    }

    protected String formatDateTime(long date) {
        final SimpleDateFormat format = new SimpleDateFormat();
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.applyPattern("dd/MM/yyyy HH:mm");
        return format.format(new Date(date));
    }
    
    public abstract AbstractRepository getRepository();
}
