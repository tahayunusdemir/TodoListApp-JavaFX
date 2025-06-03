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
            URL fxmlUrl = getClass().getResource("view/MainView.fxml");
            if (fxmlUrl == null) {
                System.err.println("Cannot find FXML file: view/MainView.fxml. Check the path.");
                // Optionally show an Alert to the user here
                return;
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            
            // Link CSS file
            URL cssUrl = getClass().getResource("view/styles.css");
            if (cssUrl != null) {
               scene.getStylesheets().add(cssUrl.toExternalForm());
               System.out.println("CSS loaded successfully: " + cssUrl.toExternalForm());
            } else {
               System.err.println("Cannot find CSS file: view/styles.css. Check the path.");
            }

            primaryStage.setTitle("Todo List Application");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an Alert to the user for the IOException
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 