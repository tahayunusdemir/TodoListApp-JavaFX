package todolistapp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
// import java.util.ArrayList; // No longer explicitly used
// import java.util.List; // No longer explicitly used

/**
 * Manages a list of {@link TodoItem} objects.
 * This class handles adding, removing, updating, saving, and loading tasks.
 * Tasks are stored in an {@link ObservableList} for easy binding with JavaFX UI components.
 * Persistence is handled via a CSV file.
 */
public class TodoListManager {

    private final ObservableList<TodoItem> tasks = FXCollections.observableArrayList();
    private static final String DATA_FILE_PATH = "tasks.csv";

    /**
     * Gets the observable list of tasks.
     * UI components can bind to this list to automatically update when tasks are changed.
     * @return The {@link ObservableList} of {@link TodoItem}s.
     */
    public ObservableList<TodoItem> getTasks() {
        return tasks;
    }

    /**
     * Adds a new task to the list.
     * @param description The description of the task.
     * @param priority The priority of the task.
     * @param dueDate The due date of the task.
     */
    public void addTask(String description, TodoItem.Priority priority, LocalDate dueDate) {
        TodoItem newItem = new TodoItem(description, priority, dueDate);
        tasks.add(newItem);
        // Consider calling saveTasks() here if save-on-change is desired
    }

    /**
     * Adds an existing {@link TodoItem} to the list.
     * @param item The {@link TodoItem} to add.
     */
    public void addTask(TodoItem item) {
        tasks.add(item);
        // Consider calling saveTasks() here if save-on-change is desired
    }

    /**
     * Removes a task from the list.
     * @param item The {@link TodoItem} to remove.
     */
    public void removeTask(TodoItem item) {
        tasks.remove(item);
        // Consider calling saveTasks() here if save-on-change is desired
    }

    /**
     * Updates an existing task with new details.
     * @param item The {@link TodoItem} to update.
     * @param newDescription The new description for the task.
     * @param newPriority The new priority for the task.
     * @param newDueDate The new due date for the task.
     * @param isDone The new completion status for the task.
     */
    public void updateTask(TodoItem item, String newDescription, TodoItem.Priority newPriority, LocalDate newDueDate, boolean isDone) {
        item.setDescription(newDescription);
        item.setPriority(newPriority);
        item.setDueDate(newDueDate);
        item.setDone(isDone);
        // saveTasks(); // Task 4.4: Implemented save-on-modification, so this can be uncommented or called from controller.
                       // For now, persistence is handled by saveOnClose and explicit save calls triggered by user actions in controller.
    }

    /**
     * Saves the current list of tasks to a CSV file specified by {@code DATA_FILE_PATH}.
     * Each task is written as a line in the CSV file.
     * The format is: "description",isDone,priority,dueDate,creationDate.
     * Descriptions containing commas or quotes are handled by enclosing in double quotes and escaping internal quotes.
     */
    public void saveTasks() {
        System.out.println("Attempting to save tasks to: " + Paths.get(DATA_FILE_PATH).toAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE_PATH))) {
            for (TodoItem item : tasks) {
                // Format: "description",isDone,priority,dueDate,creationDate
                String descriptionCsv = "\"" + item.getDescription().replace("\"", "\"\"") + "\"";
                String dueDateStr = item.getDueDate() != null ? item.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
                String creationDateStr = item.getCreationDate() != null ? item.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
                
                String line = String.join(",",
                        descriptionCsv,
                        String.valueOf(item.isDone()),
                        item.getPriority().name(),
                        dueDateStr,
                        creationDateStr
                );
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Tasks saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving tasks to " + DATA_FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
            // In a real application, inform the user via an Alert dialog.
        }
    }

    /**
     * Loads tasks from the CSV file specified by {@code DATA_FILE_PATH}.
     * The existing list of tasks is cleared before loading.
     * If the file does not exist, the application starts with an empty task list.
     * Each line in the CSV file is parsed to create a {@link TodoItem}.
     * Errors during parsing of a line are logged, and the application attempts to continue loading other tasks.
     */
    public void loadTasks() {
        if (!Files.exists(Paths.get(DATA_FILE_PATH))) {
            System.out.println("Data file not found, starting with an empty task list: " + Paths.get(DATA_FILE_PATH).toAbsolutePath());
            return;
        }
        tasks.clear();
        System.out.println("Attempting to load tasks from: " + Paths.get(DATA_FILE_PATH).toAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Expected format: "description",isDone,priority,dueDate,creationDate
                String[] parts = line.split(",", 5); 
                if (parts.length == 5) {
                    try {
                        String description = parts[0];
                        // Remove surrounding quotes and unescape double quotes within the description
                        if (description.length() >= 2 && description.startsWith("\"") && description.endsWith("\"")) {
                            description = description.substring(1, description.length() - 1).replace("\"\"", "\"");
                        }
                        
                        boolean isDone = Boolean.parseBoolean(parts[1]);
                        TodoItem.Priority priority = TodoItem.Priority.valueOf(parts[2]);
                        
                        LocalDate dueDate = null;
                        if (parts[3] != null && !parts[3].trim().isEmpty()) {
                            dueDate = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                        
                        // The TodoItem constructor sets creationDate to LocalDate.now().
                        // If preserving the creationDate from the CSV is crucial, the TodoItem class
                        // would need a way to set it after construction, or the constructor
                        // would need to accept it as a parameter.
                        // For this project, the original creationDate from the file is not restored to keep it simple.
                        // LocalDate creationDateFromFile = (parts[4] != null && !parts[4].trim().isEmpty()) ? LocalDate.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE) : null;

                        TodoItem item = new TodoItem(description, priority, dueDate);
                        item.setDone(isDone);

                        tasks.add(item);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date for task line: [" + line + "]. Details: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing priority or boolean for task line: [" + line + "]. Details: " + e.getMessage());
                    } catch (Exception e) { 
                        System.err.println("Unexpected error parsing task line: [" + line + "]. Details: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Skipping malformed task line (incorrect number of parts, expected 5): [" + line + "]");
                }
            }
            System.out.println(tasks.size() + " tasks loaded successfully from " + DATA_FILE_PATH + ".");
        } catch (IOException e) {
            System.err.println("Error loading tasks from " + DATA_FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
            // In a real application, inform the user via an Alert dialog.
        }
    }

    // Note: The comment below referring to "Section VI" is from the TODO.md plan and is now implicitly covered by the methods above.
    // Persistence methods loadTasks() and saveTasks() will be detailed in Section VI 
} 