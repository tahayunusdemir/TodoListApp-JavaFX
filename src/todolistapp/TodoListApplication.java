package todolistapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import todolistapp.controller.MainViewController;

public class TodoListApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainView.fxml"));
            Parent root = loader.load();
            MainViewController controller = loader.getController();

            Scene scene = new Scene(root);
            
            URL cssUrl = getClass().getResource("view/styles.css");
            if (cssUrl != null) {
               scene.getStylesheets().add(cssUrl.toExternalForm());
               System.out.println("CSS loaded successfully: " + cssUrl.toExternalForm());
            } else {
               System.err.println("Cannot find CSS file: view/styles.css. Check the path.");
            }

            primaryStage.setTitle("Todo List Application");
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest(event -> {
                if (controller != null) {
                    controller.handleAppExit();
                }
                System.out.println("Application is closing.");
            });

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