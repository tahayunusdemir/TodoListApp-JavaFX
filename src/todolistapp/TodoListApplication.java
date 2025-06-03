package todolistapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class TodoListApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Path to FXML, assuming it's in todolistapp/view/MainView.fxml
            URL fxmlUrl = getClass().getResource("view/MainView.fxml");
            if (fxmlUrl == null) {
                System.err.println("Cannot find FXML file. Check the path: view/MainView.fxml");
                // Consider showing an Alert to the user
                return;
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            
            // Optional: Link CSS later if you have one in the view package
            // URL cssUrl = getClass().getResource("view/styles.css");
            // if (cssUrl != null) {
            //    scene.getStylesheets().add(cssUrl.toExternalForm());
            // } else {
            //    System.err.println("Cannot find CSS file. Check the path.");
            // }

            primaryStage.setTitle("Todo List Application");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception, e.g., show an error dialog to the user
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 