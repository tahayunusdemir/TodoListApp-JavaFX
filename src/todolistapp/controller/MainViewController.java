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
import javafx.scene.layout.BorderPane;
import todolistapp.model.TodoItem;
import todolistapp.model.TodoListManager;
import todolistapp.model.TodoItem.Priority;
import javafx.scene.control.TableCell;
import javafx.scene.control.CheckBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;


public class MainViewController implements Initializable {

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

    // Optional: @FXML private TableColumn<TodoItem, Void> actionsColumn;
    
    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button deleteCompletedButton;

    @FXML
    private Label summaryLabel;

    private TodoListManager todoListManager;
    private FilteredList<TodoItem> filteredTasks;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 3.3.A. Instantiate Model
        this.todoListManager = new TodoListManager();

        // 3.3.B. Configure priorityComboBox
        priorityComboBox.getItems().setAll(Priority.values());

        // 3.3.C. Configure tasksTableView
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        
        // Configure statusColumn with a CheckBox Cell Factory
        statusColumn.setCellFactory(column -> new TableCell<TodoItem, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(event -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        TodoItem item = getTableView().getItems().get(getIndex());
                        item.setDone(checkBox.isSelected());
                        // todoListManager.updateTask(item, item.getDescription(), item.getPriority(), item.getDueDate(), item.isDone()); // Or rely on property binding if active persistence
                        System.out.println("Task status changed: " + item.getDescription() + " to " + item.isDone());
                        updateSummaryLabel();
                        // Optionally re-apply filter if it depends on 'done' status
                        if (filteredTasks != null) {
                            filteredTasks.setPredicate(filteredTasks.getPredicate());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        // Link TableView to TodoListManager's ObservableList (wrapped in FilteredList and SortedList)
        filteredTasks = new FilteredList<>(todoListManager.getTasks(), p -> true); // Show all initially
        SortedList<TodoItem> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(tasksTableView.comparatorProperty());
        tasksTableView.setItems(sortedTasks);
        
        // Optional: Enable table sorting by due date by default
        // tasksTableView.getSortOrder().add(dueDateColumn);

        // 3.3.D. (Optional) Configure filterComboBox
        filterComboBox.getItems().addAll("All", "Active", "Completed");
        filterComboBox.setValue("All"); // Default filter
        filterComboBox.setOnAction(event -> handleFilterTasks());


        // 3.3.E. Load Initial Data (Persistence) - Will be called later
        // todoListManager.loadTasks(); // Placeholder
        updateSummaryLabel();
        
        System.out.println("MainViewController initialized with FXML components.");
    }    

    @FXML
    private void handleAddTask(ActionEvent event) {
        String description = descriptionTextField.getText();
        Priority priority = priorityComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (description == null || description.trim().isEmpty()) {
            // TODO: Show an alert to the user - description cannot be empty
            System.err.println("Task description cannot be empty.");
            return;
        }
        if (priority == null) {
            // Default to MEDIUM if not selected, or show an alert
            priority = Priority.MEDIUM; 
            System.out.println("Priority not selected, defaulting to MEDIUM.");
        }
        // Due date can be null

        todoListManager.addTask(description, priority, dueDate);
        
        descriptionTextField.clear();
        priorityComboBox.setValue(null); // Or set to default
        dueDatePicker.setValue(null);

        updateSummaryLabel();
        System.out.println("Task added: " + description);
    }

    @FXML
    private void handleDeleteCompleted(ActionEvent event) {
        // Iterate backwards or create a list of items to remove to avoid ConcurrentModificationException
        java.util.List<TodoItem> toRemove = new java.util.ArrayList<>();
        for (TodoItem item : todoListManager.getTasks()) {
            if (item.isDone()) {
                toRemove.add(item);
            }
        }
        if (!toRemove.isEmpty()) {
            // Optional: Confirm deletion
            // Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete all completed tasks?", ButtonType.YES, ButtonType.NO);
            // alert.showAndWait().ifPresent(response -> {
            //     if (response == ButtonType.YES) {
            //         toRemove.forEach(todoListManager::removeTask);
            //         updateSummaryLabel();
            //         System.out.println(toRemove.size() + " completed tasks deleted.");
            //     }
            // });
            toRemove.forEach(todoListManager::removeTask); // Direct removal for now
            updateSummaryLabel();
            System.out.println(toRemove.size() + " completed tasks deleted.");
        } else {
            System.out.println("No completed tasks to delete.");
        }
    }
    
    private void handleFilterTasks() {
        String filterType = filterComboBox.getValue();
        if (filterType == null) filterType = "All";

        final String selectedFilter = filterType; // effectively final for lambda
        filteredTasks.setPredicate(item -> {
            if (selectedFilter.equals("Completed")) {
                return item.isDone();
            } else if (selectedFilter.equals("Active")) {
                return !item.isDone();
            }
            return true; // "All" or default
        });
        updateSummaryLabel();
    }

    private void updateSummaryLabel() {
        long pendingTasks = filteredTasks.stream().filter(item -> !item.isDone()).count();
        long totalTasksInView = filteredTasks.size();
        // Or use todoListManager.getTasks().size() for total regardless of filter
        // long totalTasks = todoListManager.getTasks().size();
        summaryLabel.setText(pendingTasks + " pending / " + totalTasksInView + " tasks in view");
    }

    // Placeholder for save on exit, to be called from TodoListApplication
    // public void handleAppExit() {
    //     if (todoListManager != null) {
    //         // todoListManager.saveTasks(); // Persistence will be implemented later
    //         System.out.println("Application exit: Tasks would be saved here.");
    //     }
    // }
} 