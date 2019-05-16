/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.opportunity;

import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import online.touch.easy.common.model.Opportunity;
import online.touch.easy.desktop.data.AbstractRepository;
import online.touch.easy.desktop.data.OpportunityRepository;
import online.touch.easy.desktop.util.FormUtils;
import online.touch.easy.desktop.util.ListScene;

/**
 * FXML Controller class
 *
 * @author George
 */
public class OpportunityListController extends ListScene<Opportunity> {

    @FXML
    private TableView<Opportunity> opportunityTable;
    @FXML
    private TableColumn<Opportunity, String> nameColumn;
    @FXML
    private TableColumn<Opportunity, String> emailColumn;
    @FXML
    private TableColumn<Opportunity, String> salesStageColumn;
    @FXML
    private TableColumn<Opportunity, String> typeColumn;
    @FXML
    private TableColumn<Opportunity, String> followUpColumn;

    private final OpportunityRepository repository;

    public OpportunityListController() {
        super(Opportunity.class);
        repository = OpportunityRepository.getInstance();
    }
    private static final ObservableList<Opportunity> OPPORTUNITY_DATA = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        salesStageColumn.setCellValueFactory(new PropertyValueFactory<>("salesStage"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        followUpColumn.setCellValueFactory(new PropertyValueFactory<>("followUp"));

        opportunityTable.setRowFactory((TableView<Opportunity> tv) -> {
            TableRow<Opportunity> row = new TableRow<>();
            row.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Opportunity rowData = row.getItem();
                    FormUtils.loadAdvancedForm(Opportunity.class.getSimpleName(), rowData.getId(), opportunityTable, getMainScene());
                }
            });
            return row;
        });

        // Add observable list data to the table
        opportunityTable.setItems(OPPORTUNITY_DATA);
        OPPORTUNITY_DATA.clear();
        Opportunity[] persons = OpportunityRepository.getInstance().findAll(Opportunity[].class);
        OPPORTUNITY_DATA.addAll(Arrays.asList(persons));
    }

    @Override
    public TableView<Opportunity> getTable() {
        return opportunityTable;
    }

    @Override
    public AbstractRepository getRepository() {
        return repository;
    }

}
