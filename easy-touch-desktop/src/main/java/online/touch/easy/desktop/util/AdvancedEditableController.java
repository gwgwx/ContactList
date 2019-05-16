/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

//import com.falaisia.annotations.PrintableField;
//import com.falaisia.common.domain.model.DistributedIdentifiable;
//import com.falaisia.liberty.operator.print.GenericReportDatasource;
//import com.falaisia.liberty.operator.print.JRViewerFxController;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperPrintManager;
import javafx.animation.FadeTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import online.touch.easy.common.model.Identifiable;
import online.touch.easy.desktop.data.AbstractRepository;
import org.apache.commons.lang.StringUtils;
import org.controlsfx.validation.ValidationMessage;

/**
 *
 * @author George
 * @param <T>
 */
public abstract class AdvancedEditableController<T extends Identifiable> extends SimpleEditableController<T> {

    public String imagepath;
    
    
    private static final Logger LOG = Logger.getLogger(AdvancedEditableController.class.getName());

    public AdvancedEditableController(Class<T> entityClass) {
        super(entityClass);
    }

    @FXML
    private Button btnNext;

    @FXML
    private Button btnPrevious;

    @FXML
    private MenuItem miNext;

    @FXML
    private MenuItem miPrevious;

    @FXML
    public ImageView imageView;

    private TableView<T> tableView;

    public abstract T getSelected();

    public abstract T getEdited();

    protected abstract void updateDetails();

    public abstract Parent getRootPane();

    public TableView<T> getTableView() {
        return tableView;
    }

    // check to see if saved state is unsaved and if so confirm if the user would still like to cle the screen.
    // YES, Save and return true
    // NO, return true
    // CANCEL, return false
    protected abstract SaveState computeSaveState(T edited, T selected);

    public boolean close() {

        LOG.finer("call close!");

        SaveState saveState = computeSaveState(getEdited(), getSelected());
        if (saveState.equals(SaveState.UNSAVED) || saveState.equals(SaveState.INVALID)) {

            Alert dlg = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to save your changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            dlg.initModality(Modality.APPLICATION_MODAL);

            dlg.setTitle("Liberty Vessel Client");
            dlg.setHeaderText(null);

//            configureSampleDialog(dlg, optionalMasthead);
//            dlg.showAndWait().ifPresent(result -> {
//                switch (result.getButtonData()) {
//                    case YES:
//                        save(null);//save and fall throught to no!
//                    case NO:
//                        rootPane.getScene().getWindow().hide();
//                    case CANCEL_CLOSE:
//                    // do nothing!
//                        close(true);
//                }
//            });
            ButtonType bType = dlg.showAndWait().get();

            switch (bType.getButtonData()) {
                case YES:
                    break;
                case NO:
                    return true;
                case CANCEL_CLOSE:
                    // do nothing!
                    return false;
            }

        }
        if (validationSupport.isInvalid()) {
            String temp = "";
            for (ValidationMessage message : validationSupport.getValidationResult().getMessages()) {
                LOG.log(Level.FINER, "validation Message: {0}", message);
                temp += message.getText() + "\n";
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText(null);
            alert.setContentText(temp);

            alert.showAndWait();
            return false;
        }
        if (!canSave()) {
            return false;
        }
        // user wants to save and all validation passed, save and return true;
        save(null);//save and fall throught to no!
        return true;
    }

    protected String isInValid() {
        return null;
    }

    protected boolean canSave() {

        String validationErrors = isInValid();
        LOG.log(Level.FINER, "validationErrors: {0}", validationErrors);

        if (validationErrors != null && !validationErrors.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setHeaderText(null);
            alert.setContentText(validationErrors);

            alert.showAndWait();
            return false;
        }
        return true;

    }

    public void setTableView(TableView<T> tableView) {
        this.tableView = tableView;

        if (this.tableView != null
                && btnPrevious != null
                && miPrevious != null
                && btnNext != null
                && miNext != null
                && this.tableView.getItems().size() > 0) {

            btnPrevious.disableProperty().bind(tableView.getSelectionModel().selectedIndexProperty().isEqualTo(0));
            miPrevious.disableProperty().bind(btnPrevious.disableProperty());

            btnNext.disableProperty().bind(tableView.getSelectionModel().selectedIndexProperty().isEqualTo(tableView.getItems().size() - 1));
            miNext.disableProperty().bind(btnNext.disableProperty());
        }
    }

    private MainScene mainScene;

    public void setMainScene(MainScene mainScene) {
        this.mainScene = mainScene;
    }

    protected MainScene getMainScene() {
        return this.mainScene;
    }

    public void previousRecord(ActionEvent event) {
        if (tableView != null && AdvancedEditableController.this.close()) {
            tableView.getSelectionModel().select(getSelected());
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.getSelectionModel().selectPrevious();
            tableView.getSelectionModel().clearSelection(i);
            T rowData = tableView.getSelectionModel().getSelectedItem();
            FormUtils.loadAdvancedForm(rowData.getClass().getSimpleName(), rowData.getId(), tableView, mainScene);
            btnNext.getScene().getWindow().hide();
        }
    }

    public void nextRecord(ActionEvent event) {
        if (tableView != null && AdvancedEditableController.this.close()) {
            tableView.getSelectionModel().select(getSelected());
            int i = tableView.getSelectionModel().getSelectedIndex();
            tableView.getSelectionModel().selectNext();
            tableView.getSelectionModel().clearSelection(i);
            T rowData = tableView.getSelectionModel().getSelectedItem();
            FormUtils.loadAdvancedForm(rowData.getClass().getSimpleName(), rowData.getId(), tableView, mainScene);
            btnNext.getScene().getWindow().hide();
        }
    }

    /**
     * Called when the SaveIssue button is fired.
     *
     * @param event the action event.
     * @return
     */
    @FXML
    protected boolean save(ActionEvent event) {
        final T ref = getSelected();
        final T edited = getEdited();
        SaveState saveState = computeSaveState(edited, ref);
        switch (saveState) {
            case INVALID:
                LOG.info("Invalid Data: cannot save form");

                String temp = "";
                for (ValidationMessage message : validationSupport.getValidationResult().getMessages()) {
                    LOG.log(Level.FINER, "validation Message: {0}", message);
                    temp += "\n" + message.getText();
                }

                displayMessage("Invalid Data: cannot save form" + temp);
                return false;
            case UNSAVED:
                if (!canSave()) {
                    return false;
                }
                getRepository().saveOrUpdate(edited);
                LOG.info("Issue saved");
                if (mainScene != null) {
                    mainScene.refresh();
                }
                return true;
            default:
                LOG.info("No change needs saving");
                return true;
        }
    }

    @FXML
    void saveAndNew(ActionEvent event) {
        LOG.fine("call AbstractFormController#saveAndNewFired!");
        boolean close = save(event);

        if (close) {
            FormUtils.loadAdvancedForm(getSelected().getClass().getSimpleName(), null, tableView, mainScene);
            getRootPane().getScene().getWindow().hide();
        }
    }

    @FXML
    void saveAndClose(ActionEvent event) {
        boolean close = save(event);
        if (close) {
            getRootPane().getScene().getWindow().hide();
        }
    }

    @FXML
    void delete(ActionEvent event) {
        getRepository().remove(getSelected());
        close(event);
    }

    @FXML
    public void chooseFile(ActionEvent actionEvent) throws java.io.IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            imagepath = file.toURI().toURL().toString();
            System.out.println("file:" + imagepath);
            Image image = new Image(imagepath, 80, 80, false, false);
            System.out.println("height:" + image.getHeight() + "\nWidth:" + image.getWidth());
            imageView.setImage(image);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            /*alert.setContentText("You didn't select a file!");*/
            alert.showAndWait();
        }
    }

    /*

    public void exportFields(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/report/ExportDialog.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            ExportDialogController<T> exportController = (ExportDialogController) fxmlLoader.getController();
            exportController.load(getEdited(), entityClass);
            Scene scene = new Scene(root);
            final Stage popUp = new Stage();
            popUp.setScene(scene);
            popUp.setTitle("Export Report Field Values");
            popUp.getIcons().add(new Image("images/liberty_logo_icon.png"));
            popUp.initOwner(btnNext.getScene().getWindow());
            popUp.initModality(Modality.WINDOW_MODAL);
            popUp.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void showPrintPreview(ActionEvent event) {

        try {
//            JRViewerFx viewer = new JRViewerFx(jasperPrint, JRViewerFxMode.REPORT_PRINT, MainApp.getPrimaryStage());
//            viewer.start(MainApp.getPrimaryStage());

            InputStream fxmlStream = null;
            try {
                fxmlStream = getClass().getResourceAsStream("/fxml/print/FRViewerFx.fxml");
                FXMLLoader loader = new FXMLLoader();
                Parent page = (Parent) loader.load(fxmlStream);
                Scene scene = new Scene(page);
                final Stage popUp = new Stage();
                popUp.setTitle(entityTitle() + " - (Print Preview)");
                popUp.getIcons().add(new Image("images/liberty_logo_icon.png"));
                popUp.initOwner(btnNext.getScene().getWindow());
                popUp.initModality(Modality.APPLICATION_MODAL);
                popUp.setScene(scene);
                popUp.show();
                Object o = loader.getController();
                if (o instanceof JRViewerFxController) {
                    JRViewerFxController jrViewerFxController = (JRViewerFxController) o;
                    jrViewerFxController.setJasperPrint(getJasperPrint());
                    jrViewerFxController.show();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            Logger.getLogger(ArrivalReportFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showPrint(ActionEvent event) {
        try {
            JasperPrintManager.printReport(getJasperPrint(), true);
        } catch (JRException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private JasperPrint getJasperPrint() {

        JasperPrint jasperPrint = null;

//        list.sort((Map o1, Map o2) -> ((String) o1.get("group")).compareTo(((String) o2.get("group"))));
        GenericReportDatasource datasource = new GenericReportDatasource(reportDetails());
        try {
            jasperPrint = JasperFillManager.fillReport(getClass().getResourceAsStream(REPORTS.get(entityClass.getSimpleName())), reportParameters(), datasource);
        } catch (JRException e) {
            LOG.log(Level.SEVERE, "Error trying to fill report", e);
        }

        return jasperPrint;

    }

    protected Map<String, Object> reportParameters() {
        Map<String, Object> params = new HashMap<>();

        params.put("reportName", entityTitle());
        return params;
    }

    protected List<Map> reportDetails() {

        List<Map> list = new ArrayList<>();

        for (Method method : entityClass.getMethods()) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof PrintableField) {
                    PrintableField pAnno = (PrintableField) annotation;
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("group", pAnno.group());
                        map.put("order", pAnno.order());
                        map.put("name", pAnno.label().isEmpty() ? splitCamelCase(method.getName()) : pAnno.label());
                        map.put("value", method.invoke(getSelected()) != null ? formatPrintValue(method.invoke(getSelected())) : "");
                        list.add(map);
//                    selectionList.add(new ExportDialogController.Item(method.getName(), Boolean.FALSE));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        Collections.sort(list, new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                int i = ((String) o1.get("group")).compareTo(((String) o2.get("group")));
                if (i != 0) {
                    return i;
                }
                i = ((Integer) o1.get("order")).compareTo(((Integer) o2.get("order")));
                if (i != 0) {
                    return i;
                } else {
                    return ((String) o1.get("name")).compareTo(((String) o2.get("name")));
                }
            }
        });
        return list;

    }

    private static final Map<String, String> REPORTS = new HashMap<>();

    static {
        REPORTS.put(ArrivalReport.class.getSimpleName(), "/jasper/ArrivalReport.jasper");
        REPORTS.put(DepartureReport.class.getSimpleName(), "/jasper/DepartureReport.jasper");
        REPORTS.put(AtSeaReport.class.getSimpleName(), "/jasper/AtSeaReport.jasper");
        REPORTS.put(PortReport.class.getSimpleName(), "/jasper/PortReport.jasper");
        REPORTS.put(BunkerReceivedNote.class.getSimpleName(), "/jasper/BunkerReceivedNote.jasper");
        REPORTS.put(LubricantReceivedNote.class.getSimpleName(), "/jasper/LubricantReceivedNote.jasper");
        REPORTS.put(PortOperationNote.class.getSimpleName(), "/jasper/PortOperationNote.jasper");
        REPORTS.put(CleaningReport.class.getSimpleName(), "/jasper/CleaningReport.jasper");
        REPORTS.put(Vessel.class.getSimpleName(), "/jasper/vessel/Vessel.jasper");
        REPORTS.put(FuelType.class.getSimpleName(), "/jasper/FuelItem.jasper");
        REPORTS.put(Lubricant.class.getSimpleName(), "/jasper/Lubricant.jasper");
        REPORTS.put(InventoryItemAccount.class.getSimpleName(), "/jasper/InventoryItemAccount.jasper");
        REPORTS.put(InventoryAdjustment.class.getSimpleName(), "/jasper/InventoryAdjustment.jasper");
        REPORTS.put(LetterOfProtest.class.getSimpleName(), "/jasper/LetterOfProtest.jasper");
        REPORTS.put(Voyage.class.getSimpleName(), "/jasper/vessel/Voyage.jasper");
        REPORTS.put(WorkOrder.class.getSimpleName(), "/jasper/WorkOrder.jasper");
        REPORTS.put(PurchaseRequisition.class.getSimpleName(), "/jasper/PurchaseRequisition.jasper");
        REPORTS.put(NonConformanceReport.class.getSimpleName(), "/jasper/NonConformanceReport.jasper");
    }
     */
    @FXML
    private void handleHelpAboutAction(ActionEvent event) {
        LOG.fine("call AdvancedEditableController#handleHelpAboutAction");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/About.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Scene scene = new Scene(root);
            final Stage popUp = new Stage();
            popUp.setScene(scene);
//            popUp.getIcons().add(new Image("images/liberty_logo_icon.png"));
            popUp.initOwner(getRootPane().getScene().getWindow());
            popUp.initModality(Modality.WINDOW_MODAL);
            popUp.show();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error loading help window: ", e);
        }
    }

    @FXML
    private Label messageBar;

    private FadeTransition messageTransition = null;

    protected void displayMessage(String message) {
        if (messageBar != null) {
            if (messageTransition != null) {
                messageTransition.stop();
            } else {
                messageTransition = new FadeTransition(Duration.millis(2000), messageBar);
                messageTransition.setFromValue(1.0);
                messageTransition.setToValue(0.0);
                messageTransition.setDelay(Duration.millis(1000));
                messageTransition.setOnFinished((ActionEvent event) -> {
                    messageBar.setVisible(false);
                });
            }
            messageBar.setText(message);
            messageBar.setVisible(true);
            messageBar.setOpacity(1.0);
            messageTransition.playFromStart();
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @FXML
    void close(ActionEvent event) {
        LOG.finer("call close issue Fired!");
        if (AdvancedEditableController.this.close()) {
            getRootPane().getScene().getWindow().hide();
        }
    }

    /*

    protected String entityTitle() {

        String reportTitle = getSelected().getClass().getSimpleName();

        // if class has annotation override with value of label.
        for (Annotation annotation : entityClass.getDeclaredAnnotations()) {
            if (annotation instanceof PrintableField) {
                PrintableField pAnno = (PrintableField) annotation;
                if (!pAnno.label().isEmpty()) {
                    reportTitle = pAnno.label();
                }
                break;
            }
        }
        return reportTitle;

    }

    protected Currency findBaseCurrency() {
        List<Currency> currencies = CurrencyRepository.getInstance().getItems(null, null);
        if (currencies.isEmpty()) {
            LOG.finer("return null currrencis is empty!");
            return null;
        }
        LOG.finer("return currency");
        return currencies.get(0);
    }

    @FXML
    private void voidDocument(ActionEvent event) {
        LOG.fine("call voidDocument");

        Alert alert = new Alert(
                AlertType.CONFIRMATION,
                "Are you sure that you want to void the selected item(s)?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Liberty Vessel Client");
        alert.setHeaderText(null);
//            alert.setContentText("Are you sure that you want to permanently delete the selected item(s)?");
//
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {

            DistributedIdentifiable e = getSelected();
            if (e instanceof Document) {
                Document d = (Document) e;
                d.setVoided(true);
                d.setDataTransferAction(true);
//                getRootPane().setDisable(true);
                for (int i = 0; i < getRootPane().getChildrenUnmodifiable().size(); i++) {
                    if (getRootPane().getChildrenUnmodifiable().get(i) instanceof TextField) {
                        getRootPane().getChildrenUnmodifiable().get(i).setDisable(true);

                    }

                }
                disableChildren(getRootPane());
            }

        } else if (result.get() == ButtonType.NO) {
            LOG.fine("user clicked not");
            // do nothing
        }
    }
     */
    private void disableChildren(Parent node) {
        for (int i = 0; i < node.getChildrenUnmodifiable().size(); i++) {
            if (node.getChildrenUnmodifiable().get(i) instanceof TextField
                    || node.getChildrenUnmodifiable().get(i) instanceof TextArea
                    || node.getChildrenUnmodifiable().get(i) instanceof ComboBox
                    || node.getChildrenUnmodifiable().get(i) instanceof TableView
                    || node.getChildrenUnmodifiable().get(i) instanceof ListView
                    || node.getChildrenUnmodifiable().get(i) instanceof TreeView
                    || node.getChildrenUnmodifiable().get(i) instanceof CheckBox) {
                Control control = (Control) node.getChildrenUnmodifiable().get(i);
                if (!control.disableProperty().isBound()) {
                    control.setDisable(true);
                }
//                node.getChildrenUnmodifiable().get(i).setDisable(true);
                LOG.finer("textfield!");
            } else if (node.getChildrenUnmodifiable().get(i) instanceof Parent) {
                disableChildren(((Parent) node.getChildrenUnmodifiable().get(i)));
            }
        }
    }

    // edit menu items
    @FXML
    private MenuItem miUndo;

    @FXML
    private MenuItem miCut;

    @FXML
    private MenuItem miCopy;

    @FXML
    private MenuItem miPaste;

    @FXML
    private MenuItem meClear;

    @FXML
    private MenuItem miSelectAll;

    private Clipboard systemClipboard;

    private String getSelectedText() {
        TextInputControl[] tfs = findFormFields();
        for (TextInputControl tf : tfs) {
            if (StringUtils.isNotEmpty(tf.getSelectedText())) {
                return tf.getSelectedText();
            }
        }
        return null;
    }

    public void copy() {
        String text = getSelectedText();

        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        systemClipboard.setContent(content);
    }

    private TextInputControl getFocusedTextField() {
        TextInputControl[] tfs = findFormFields();
        for (TextInputControl tf : tfs) {
            if (tf.isFocused()) {
                return tf;
            }
        }
        return null;
    }

    public void cut() {

        TextInputControl focusedTF = getFocusedTextField();

        String text = focusedTF.getSelectedText();

        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        systemClipboard.setContent(content);

        IndexRange range = focusedTF.getSelection();
        String origText = focusedTF.getText();
        String firstPart = StringUtils.substring(origText, 0, range.getStart());
        String lastPart = StringUtils.substring(origText, range.getEnd(), StringUtils.length(origText));
        focusedTF.setText(firstPart + lastPart);

        focusedTF.positionCaret(range.getStart());

    }

    public void clear() {

        TextInputControl focusedTF = getFocusedTextField();

        focusedTF.setText("");

    }

    public void selectAll() {

        TextInputControl focusedTF = getFocusedTextField();

        focusedTF.selectAll();

    }

    public void paste() {

        if (!systemClipboard.hasContent(DataFormat.PLAIN_TEXT)) {
            adjustForEmptyClipboard();
            return;
        }

        String clipboardText = systemClipboard.getString();

        TextInputControl focusedTF = getFocusedTextField();
        IndexRange range = focusedTF.getSelection();

        String origText = focusedTF.getText();

        int endPos = 0;
        String updatedText = "";
        String firstPart = StringUtils.substring(origText, 0, range.getStart());
        String lastPart = StringUtils.substring(origText, range.getEnd(), StringUtils.length(origText));

        updatedText = firstPart + clipboardText + lastPart;

        if (range.getStart() == range.getEnd()) {
            endPos = range.getEnd() + StringUtils.length(clipboardText);
        } else {
            endPos = range.getStart() + StringUtils.length(clipboardText);
        }

        focusedTF.setText(updatedText);
        focusedTF.positionCaret(endPos);
    }

    public void showingEditMenu() {
        if (systemClipboard == null) {
            systemClipboard = Clipboard.getSystemClipboard();
        }

        if (systemClipboard.hasString()) {
            adjustForClipboardContents();
        } else {
            adjustForEmptyClipboard();
        }

        if (anythingSelected()) {
            adjustForSelection();

        } else {
            adjustForDeselection();
        }

        if (textFieldSelected()) {
            adjustForSelectAll();
        } else {
            adjustForCannotSelectAll();
        }
    }

    private boolean anythingSelected() {
        TextInputControl[] tfs = findFormFields();
        for (TextInputControl tf : tfs) {
            if (StringUtils.isNotEmpty(tf.getSelectedText())) {
                return true;
            }
        }
        return false;

    }

    private boolean textFieldSelected() {
        TextInputControl[] tfs = findFormFields();
        for (TextInputControl tf : tfs) {
            if (tf.isFocused()) {
                return true;
            }
        }
        return false;

    }

    private void adjustForEmptyClipboard() {
        miPaste.setDisable(true);  // nothing to paste
    }

    private void adjustForClipboardContents() {
        miPaste.setDisable(false);  // something to paste 
    }

    private void adjustForSelection() {
        miCut.setDisable(false);
        miCopy.setDisable(false);
    }

    private void adjustForSelectAll() {
        miSelectAll.setDisable(false);
        meClear.setDisable(false);
    }

    private void adjustForCannotSelectAll() {
        miSelectAll.setDisable(true);
        miPaste.setDisable(true);
        meClear.setDisable(true);
    }

    private void adjustForDeselection() {
        miCut.setDisable(true);
        miCopy.setDisable(true);
    }

    protected TextInputControl[] findFormFields() {
        TextInputControl[] textFields = new TextInputControl[allTextFields().size()];
        textFields = allTextFields().toArray(textFields);
        return textFields;
    }

    private List<TextInputControl> allTextFields() {
        List<TextInputControl> fields = new ArrayList<>();
        for (int i = 0; i < getRootPane().getChildrenUnmodifiable().size(); i++) {
            if (getRootPane().getChildrenUnmodifiable().get(i) instanceof TextField
                    || getRootPane().getChildrenUnmodifiable().get(i) instanceof TextArea) {
                fields.add((TextField) getRootPane().getChildrenUnmodifiable().get(i));

            }

        }
        addChildren(getRootPane(), fields);
        return fields;

    }

    private void addChildren(Parent node, List<TextInputControl> fields) {
        for (int i = 0; i < node.getChildrenUnmodifiable().size(); i++) {
            if (node.getChildrenUnmodifiable().get(i) instanceof TextField
                    || node.getChildrenUnmodifiable().get(i) instanceof TextArea) {
                fields.add((TextInputControl) node.getChildrenUnmodifiable().get(i));
            } else if (node.getChildrenUnmodifiable().get(i) instanceof Parent) {
                addChildren(((Parent) node.getChildrenUnmodifiable().get(i)), fields);
            }
        }
    }

    protected abstract AbstractRepository getRepository();

    protected abstract double getStageWidth();

    protected abstract double getStageHeight();

    private void openFile(File selectedFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
