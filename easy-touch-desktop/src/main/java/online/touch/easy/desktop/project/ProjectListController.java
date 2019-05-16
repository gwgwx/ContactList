/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.project;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import online.touch.easy.common.model.Project;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.ProjectRepository;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.ListScene;

/**
 * FXML Controller class
 *
 * @author George
 */
public class ProjectListController extends ListScene<Project> {

    @FXML
    private TableView<Project> projectTable;
    @FXML
    private TableColumn<Project, String> nameColumn;
    @FXML
    private TableColumn<Project, String> projectStatusColumn;
    @FXML
    private TableColumn<Project, String> projectTypeColumn;

    private final ProjectRepository repository;

    public ProjectListController() {
        super(Project.class);
        repository = ProjectRepository.getInstance();
    }
    private static final ObservableList<Project> PROJECT_DATA = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectStatusColumn.setCellValueFactory(new PropertyValueFactory<>("projectStatus"));
        projectTypeColumn.setCellValueFactory(new PropertyValueFactory<>("projectType"));
        
            projectTable.setRowFactory((TableView<Project> tv) -> {
            TableRow<Project> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Project rowData = row.getItem();
                    FormUtils.loadAdvancedForm(Project.class.getSimpleName(), rowData.getId(), projectTable, getMainScene());
                }
            });
            return row;
        });

        // Add observable list data to the table
        projectTable.setItems(PROJECT_DATA);
        PROJECT_DATA.clear();
        Project[] persons = ProjectRepository.getInstance().findAll(Project[].class);
        PROJECT_DATA.addAll(Arrays.asList(persons));
    }

    @Override
    public TableView<Project> getTable() {
        return projectTable;
    }

    @Override
    public AbstractRepository getRepository() {
       return repository;
    }

}
