package todolistapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import todolistapp.controller.MainViewController;

/**
 * Main class for the Todo List Application.
 * Extends {@link Application} to launch the JavaFX application.
 * This class is responsible for loading the main view (MainView.fxml),
 * setting up the primary stage, and handling application startup and shutdown events.
 */
public class TodoListApplication extends Application {

    /**
     * The main entry point for all JavaFX applications.
     * This method is called after the FX runtime is initialized and the
     * Application class is constructed. It sets up the primary stage,
     * loads the FXML for the main view, retrieves the controller, applies CSS,
     * and sets a handler for the application close request to save tasks.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     */
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
               // System.out.println("CSS loaded successfully: " + cssUrl.toExternalForm()); // Keep for now or use a proper logger
            } else {
               System.err.println("Warning: Cannot find CSS file: view/styles.css. Styles will not be applied. Check the path.");
            }

            primaryStage.setTitle("Todo List Application");
            primaryStage.setScene(scene);

            // Set up a handler to save tasks when the application window is closed.
            primaryStage.setOnCloseRequest(event -> {
                if (controller != null) {
                    controller.handleAppExit(); // Call controller to handle saving tasks
                } else {
                    System.err.println("TodoListApplication: Controller was null during close request. Tasks might not be saved.");
                }
                // System.out.println("Application is closing."); // Keep for now or use a proper logger
            });

            primaryStage.show();
        } catch (IOException e) {
            // In a real application, show an Alert to the user for critical errors like FXML loading failure.
            System.err.println("Critical Error: Could not load FXML or initialize the application.");
            e.printStackTrace();
            // Example of showing an alert:
            // Alert alert = new Alert(Alert.AlertType.ERROR);
            // alert.setTitle("Application Error");
            // alert.setHeaderText("Failed to start the application.");
            // alert.setContentText("An unexpected error occurred: " + e.getMessage());
            // alert.showAndWait();
        }
    }

    /**
     * The main method, which is the entry point of the Java application.
     * It launches the JavaFX application by calling {@link Application#launch(String...)}.
     *
     * @param args Command line arguments passed to the application. Not used in this application.
     */
    public static void main(String[] args) {
        launch(args);
    }
} 