package online.touch.easy.desktop;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private AnchorPane root;
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("/fxml/Scene.fxml"));
        root = (AnchorPane) loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Easy Touch");
        stage.setScene(scene);
        stage.show();

        FXMLController controller = loader.getController();
        controller.setMainApp(this);
    }

    public Stage getStage() {
        return stage;
    }

    public MainApp() {
    }

    public static void main(String[] args) {
        launch(args);
    }

}
