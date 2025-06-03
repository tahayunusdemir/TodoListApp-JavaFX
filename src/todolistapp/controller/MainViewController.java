package todolistapp.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.BorderPane;
import todolistapp.model.TodoItem;
import todolistapp.model.TodoListManager;
import todolistapp.model.TodoItem.Priority;
import javafx.scene.control.TableCell;
import javafx.scene.control.CheckBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.converter.LocalDateStringConverter;
import javafx.scene.control.TableRow;
import javafx.css.PseudoClass;

/**
 * Controller for the main view of the Todo List Application (MainView.fxml).
 * Handles user interactions, manages the display of tasks, and coordinates
 * with the {@link TodoListManager} for data operations.
 */
public class MainViewController implements Initializable {

    //<editor-fold desc="@FXML Variables">
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField descriptionTextField;

    @FXML
    private ComboBox<Priority> priorityComboBox;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Button addTaskButton;

    @FXML
    private TableView<TodoItem> tasksTableView;

    @FXML
    private TableColumn<TodoItem, Boolean> statusColumn;

    @FXML
    private TableColumn<TodoItem, String> descriptionColumn;

    @FXML
    private TableColumn<TodoItem, Priority> priorityColumn;

    @FXML
    private TableColumn<TodoItem, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<TodoItem, LocalDate> creationDateColumn;

    @FXML
    private TableColumn<TodoItem, Void> actionsColumn;
    
    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button deleteCompletedButton;

    @FXML
    private Label summaryLabel;
    //</editor-fold>

    private TodoListManager todoListManager;
    private FilteredList<TodoItem> filteredTasks;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     * It sets up the model, configures UI components (ComboBoxes, TableView columns,
     * cell factories, row factories for styling), loads initial data, and applies default filters.
     *
     * @param url The location used to resolve relative paths for the root object, or null if not known.
     * @param rb The resources used to localize the root object, or null if not known.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.todoListManager = new TodoListManager();

        priorityComboBox.getItems().setAll(Priority.values());

        tasksTableView.setEditable(true);

        // Configure Description Column
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setDescription(event.getNewValue());
            // todoListManager.saveTasks(); // Or a specific update method in manager
        });

        // Configure Priority Column
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Priority.values()));
        priorityColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setPriority(event.getNewValue());
            // todoListManager.saveTasks();
        });

        // Configure Due Date Column
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        dueDateColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setDueDate(event.getNewValue());
            // todoListManager.saveTasks();
        });

        // Configure Creation Date Column (read-only)
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        
        // Configure Status Column with CheckBox
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().doneProperty());
        statusColumn.setCellFactory(column -> new TableCell<TodoItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(event -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        TodoItem item = getTableView().getItems().get(getIndex());
                        item.setDone(checkBox.isSelected());
                        updateSummaryLabel();
                        if (filteredTasks != null && filteredTasks.getPredicate() != null) {
                            // Re-apply predicate to refresh view if filtering is active
                            filteredTasks.setPredicate(filteredTasks.getPredicate()); 
                        }
                        tasksTableView.refresh(); // Refresh row to apply CSS changes
                        // todoListManager.saveTasks();
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                    setText(null);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        // Configure Actions Column with Delete Button
        actionsColumn.setCellFactory(column -> new TableCell<TodoItem, Void>() {
            private final Button deleteButton = new Button("Delete");
            {
                // Apply a style class for specific CSS styling if needed
                // deleteButton.getStyleClass().add("action-button-delete"); 
                deleteButton.setOnAction(event -> {
                    TodoItem item = getTableView().getItems().get(getIndex());
                    if (item != null) {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirm Deletion");
                        alert.setHeaderText("Delete Task: " + item.getDescription());
                        alert.setContentText("Are you sure you want to delete this task?");
                        alert.showAndWait().ifPresent(response -> {
                            if (response == javafx.scene.control.ButtonType.OK) {
                                todoListManager.removeTask(item);
                                updateSummaryLabel();
                                // todoListManager.saveTasks();
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        // Setup FilteredList and SortedList for the TableView
        filteredTasks = new FilteredList<>(todoListManager.getTasks(), p -> true); 
        SortedList<TodoItem> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(tasksTableView.comparatorProperty());
        tasksTableView.setItems(sortedTasks);
        
        // Set up RowFactory for CSS pseudo-classes (completed, priority, due status)
        setupRowStyling();

        // Configure Filter ComboBox
        filterComboBox.getItems().addAll("All", "Active", "Completed");
        filterComboBox.setValue("All"); 
        filterComboBox.setOnAction(event -> handleFilterTasks());

        // Load tasks from persistence layer
        todoListManager.loadTasks(); 
        updateSummaryLabel(); 
        
        // Initial log to confirm initialization
        // System.out.println("MainViewController initialized."); // Removed - redundant
    }

    /**
     * Sets up the row factory for the tasks TableView to apply dynamic CSS styling
     * based on task properties like completion status, priority, and due date.
     */
    private void setupRowStyling() {
        final PseudoClass completedClass = PseudoClass.getPseudoClass("completed");
        final PseudoClass highPriorityClass = PseudoClass.getPseudoClass("priority-high");
        final PseudoClass mediumPriorityClass = PseudoClass.getPseudoClass("priority-medium");
        final PseudoClass lowPriorityClass = PseudoClass.getPseudoClass("priority-low");
        final PseudoClass overdueClass = PseudoClass.getPseudoClass("overdue");

        tasksTableView.setRowFactory(tableView -> new TableRow<TodoItem>() {
            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                // Reset all pseudo-classes first for the row
                pseudoClassStateChanged(completedClass, false);
                pseudoClassStateChanged(highPriorityClass, false);
                pseudoClassStateChanged(mediumPriorityClass, false);
                pseudoClassStateChanged(lowPriorityClass, false);
                pseudoClassStateChanged(overdueClass, false);

                if (item != null && !empty) {
                    boolean isCompleted = item.isDone();
                    pseudoClassStateChanged(completedClass, isCompleted);

                    if (item.getPriority() != null) {
                        switch (item.getPriority()) {
                            case HIGH: pseudoClassStateChanged(highPriorityClass, true); break;
                            case MEDIUM: pseudoClassStateChanged(mediumPriorityClass, true); break;
                            case LOW: pseudoClassStateChanged(lowPriorityClass, true); break;
                        }
                    }

                    if (!isCompleted && item.getDueDate() != null) {
                        LocalDate today = LocalDate.now();
                        if (item.getDueDate().isBefore(today)) {
                            pseudoClassStateChanged(overdueClass, true);
                        }
                    }
                } else {
                    // Ensure no styles are applied to empty rows
                    setStyle("");
                }
            }
        });
    }

    /**
     * Handles the "Add Task" button action. Validates input fields (description, priority, due date),
     * adds a new task to the {@link TodoListManager} if valid, clears input fields,
     * and updates the summary label.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleAddTask(ActionEvent event) {
        String description = descriptionTextField.getText();
        Priority priority = priorityComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        // Input Validation
        if (description == null || description.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validation Error", "Task description cannot be empty.");
            return;
        }
        if (priority == null) {
            showAlert(AlertType.WARNING, "Validation Error", "Please select a priority.");
            return;
        }
        if (dueDate == null) {
            showAlert(AlertType.WARNING, "Validation Error", "Please select a due date.");
            return;
        }

        todoListManager.addTask(description, priority, dueDate);
        // todoListManager.saveTasks(); // Save if save-on-change is active

        descriptionTextField.clear();
        priorityComboBox.setValue(null);
        dueDatePicker.setValue(null);
        updateSummaryLabel();
    }

    /**
     * Handles the "Delete Completed" button action.
     * Removes all tasks marked as done from the {@link TodoListManager}
     * and updates the summary label.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleDeleteCompleted(ActionEvent event) {
        java.util.List<TodoItem> toRemove = new java.util.ArrayList<>();
        for (TodoItem item : todoListManager.getTasks()) { // Iterate over original list for collection
            if (item.isDone()) {
                toRemove.add(item);
            }
        }
        if (!toRemove.isEmpty()) {
            // Optional: Confirmation Dialog before deleting multiple items
            // Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Delete all " + toRemove.size() + " completed tasks?", javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
            // confirmAlert.showAndWait().ifPresent(response -> {
            // if (response == javafx.scene.control.ButtonType.YES) {
            // toRemove.forEach(todoListManager::removeTask); // This modifies the underlying list of filteredTasks
            // updateSummaryLabel();
            // todoListManager.saveTasks(); 
            // }
            // });
            toRemove.forEach(todoListManager::removeTask); 
            updateSummaryLabel();
            // todoListManager.saveTasks(); 
        } else {
            showAlert(AlertType.INFORMATION, "No Tasks", "No completed tasks to delete.");
        }
    }
    
    /**
     * Handles changes in the filter ComboBox selection.
     * Updates the predicate of the {@link FilteredList} to show tasks
     * based on the selected filter ("All", "Active", "Completed")
     * and updates the summary label.
     */
    private void handleFilterTasks() {
        String filterType = filterComboBox.getValue();
        if (filterType == null) filterType = "All"; // Default to "All" if null

        final String selectedFilter = filterType; 
        filteredTasks.setPredicate(item -> {
            if (selectedFilter.equals("Completed")) {
                return item.isDone();
            } else if (selectedFilter.equals("Active")) {
                return !item.isDone();
            }
            return true; // "All"
        });
        updateSummaryLabel();
    }

    /**
     * Updates the summary label to display the count of pending tasks
     * and the total number of tasks currently visible in the TableView (respecting filters).
     */
    private void updateSummaryLabel() {
        long pendingTasks = filteredTasks.stream().filter(item -> !item.isDone()).count();
        long totalTasksInView = filteredTasks.size();
        summaryLabel.setText(pendingTasks + " pending / " + totalTasksInView + " tasks in view");
    }

    /**
     * Helper method to display an Alert dialog.
     * @param alertType The type of the alert (e.g., WARNING, INFORMATION).
     * @param title The title of the alert window.
     * @param content The message content of the alert.
     */
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles application exit operations. This method is intended to be called
     * by the main application class when the application is closing.
     * It triggers the saving of tasks through the {@link TodoListManager}.
     */
    public void handleAppExit() {
        if (todoListManager != null) {
            todoListManager.saveTasks();
            System.out.println("MainViewController: Tasks saved on application exit.");
        } else {
            System.err.println("MainViewController: TodoListManager was null during app exit, tasks not saved.");
        }
    }
} 