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

    @FXML
    private TableColumn<TodoItem, Void> actionsColumn;
    
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
        tasksTableView.setEditable(true);

        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setDescription(event.getNewValue());
            System.out.println("Task description updated: " + item.getDescription());
        });

        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Priority.values()));
        priorityColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setPriority(event.getNewValue());
            System.out.println("Task priority updated: " + item.getDescription() + " to " + item.getPriority());
        });

        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        dueDateColumn.setOnEditCommit(event -> {
            TodoItem item = event.getRowValue();
            item.setDueDate(event.getNewValue());
            System.out.println("Task due date updated: " + item.getDescription() + " to " + item.getDueDate());
        });

        creationDateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        
        // Configure statusColumn with a CheckBox Cell Factory
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().doneProperty());
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
                        if (filteredTasks != null && filteredTasks.getPredicate() != null) {
                            filteredTasks.setPredicate(filteredTasks.getPredicate());
                            tasksTableView.refresh();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    pseudoClassStateChanged(PseudoClass.getPseudoClass("completed"), false);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                    setText(null);
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });

        // Configure actionsColumn with a Delete Button Cell Factory
        actionsColumn.setCellFactory(column -> new TableCell<TodoItem, Void>() {
            private final Button deleteButton = new Button("Delete");
            {
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
                                System.out.println("Task deleted: " + item.getDescription());
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

        // Link TableView to TodoListManager's ObservableList (wrapped in FilteredList and SortedList)
        filteredTasks = new FilteredList<>(todoListManager.getTasks(), p -> true); // Show all initially
        SortedList<TodoItem> sortedTasks = new SortedList<>(filteredTasks);
        sortedTasks.comparatorProperty().bind(tasksTableView.comparatorProperty());
        tasksTableView.setItems(sortedTasks);
        
        // Optional: Enable table sorting by due date by default
        // tasksTableView.getSortOrder().add(dueDateColumn);

        // Add rowFactory to apply styles based on task completion and priority
        PseudoClass completedClass = PseudoClass.getPseudoClass("completed");
        PseudoClass highPriorityClass = PseudoClass.getPseudoClass("priority-high");
        PseudoClass mediumPriorityClass = PseudoClass.getPseudoClass("priority-medium");
        PseudoClass lowPriorityClass = PseudoClass.getPseudoClass("priority-low");
        PseudoClass overdueClass = PseudoClass.getPseudoClass("overdue");
        PseudoClass dueSoonClass = PseudoClass.getPseudoClass("due-soon");

        tasksTableView.setRowFactory(tableView -> new TableRow<TodoItem>() {
            @Override
            protected void updateItem(TodoItem item, boolean empty) {
                super.updateItem(item, empty);
                // Reset all pseudo-classes first
                pseudoClassStateChanged(completedClass, false);
                pseudoClassStateChanged(highPriorityClass, false);
                pseudoClassStateChanged(mediumPriorityClass, false);
                pseudoClassStateChanged(lowPriorityClass, false);
                pseudoClassStateChanged(overdueClass, false);
                pseudoClassStateChanged(dueSoonClass, false);

                if (item == null || empty) {
                    // No item, do nothing else
                } else {
                    // Apply completed class if necessary
                    boolean isCompleted = item.isDone();
                    pseudoClassStateChanged(completedClass, isCompleted);

                    // Apply priority-based class (can be set even if completed, CSS handles text)
                    if (item.getPriority() != null) {
                        switch (item.getPriority()) {
                            case HIGH:
                                pseudoClassStateChanged(highPriorityClass, true);
                                break;
                            case MEDIUM:
                                pseudoClassStateChanged(mediumPriorityClass, true);
                                break;
                            case LOW:
                                pseudoClassStateChanged(lowPriorityClass, true);
                                break;
                        }
                    }

                    // Apply due-date based class ONLY if not completed
                    if (!isCompleted) {
                        LocalDate dueDate = item.getDueDate();
                        if (dueDate != null) {
                            LocalDate today = LocalDate.now();
                            if (dueDate.isBefore(today)) {
                                pseudoClassStateChanged(overdueClass, true);
                            } else if (!dueDate.isAfter(today.plusDays(3))) { // Due today or in 1, 2, or 3 days.
                                pseudoClassStateChanged(dueSoonClass, true);
                            }
                        }
                    }
                }
            }
        });

        // 3.3.D. (Optional) Configure filterComboBox
        filterComboBox.getItems().addAll("All", "Active", "Completed");
        filterComboBox.setValue("All"); // Default filter
        filterComboBox.setOnAction(event -> handleFilterTasks());


        // 3.3.E. Load Initial Data (Persistence)
        todoListManager.loadTasks(); // Load tasks from file
        updateSummaryLabel(); // Update summary after loading
        
        System.out.println("MainViewController initialized with FXML components.");
    }    

    @FXML
    private void handleAddTask(ActionEvent event) {
        String description = descriptionTextField.getText();
        Priority priority = priorityComboBox.getValue();
        LocalDate dueDate = dueDatePicker.getValue();

        if (description == null || description.trim().isEmpty()) {
            // System.err.println("Task description cannot be empty.");
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Task description cannot be empty.");
            alert.showAndWait();
            return;
        }
        if (priority == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a priority.");
            alert.showAndWait();
            return;
        }
        if (dueDate == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a due date.");
            alert.showAndWait();
            return;
        }

        todoListManager.addTask(description, priority, dueDate);

        descriptionTextField.clear();
        priorityComboBox.setValue(null);
        dueDatePicker.setValue(null);
        // tasksTableView will update via the FilteredList -> SortedList
        updateSummaryLabel(); // Update summary after adding
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

    // Called from TodoListApplication when the app is closing
    public void handleAppExit() {
        if (todoListManager != null) {
            todoListManager.saveTasks();
            System.out.println("Application exit: Tasks saved.");
        } else {
            System.out.println("Application exit: TodoListManager was null, tasks not saved.");
        }
    }
} 