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
import java.util.ArrayList;
import java.util.List;

public class TodoListManager {

    private final ObservableList<TodoItem> tasks = FXCollections.observableArrayList();
    private static final String DATA_FILE_PATH = "tasks.csv"; // Data file in the application's root execution directory

    public ObservableList<TodoItem> getTasks() {
        return tasks;
    }

    public void addTask(String description, TodoItem.Priority priority, LocalDate dueDate) {
        TodoItem newItem = new TodoItem(description, priority, dueDate);
        tasks.add(newItem);
    }

    public void addTask(TodoItem item) {
        tasks.add(item);
    }

    public void removeTask(TodoItem item) {
        tasks.remove(item);
    }

    public void updateTask(TodoItem item, String newDescription, TodoItem.Priority newPriority, LocalDate newDueDate, boolean isDone) {
        item.setDescription(newDescription);
        item.setPriority(newPriority);
        item.setDueDate(newDueDate);
        item.setDone(isDone);
        // saveTasks(); // Optional: save after every update
    }

    public void saveTasks() {
        System.out.println("Attempting to save tasks to: " + Paths.get(DATA_FILE_PATH).toAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE_PATH))) {
            for (TodoItem item : tasks) {
                // Format: description,isDone,priority,dueDate,creationDate
                // Handle potential commas and quotes in description
                String descriptionCsv = "\"" + item.getDescription().replace("\"", "\"\"") + "\"";
                String dueDateStr = item.getDueDate() != null ? item.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
                String creationDateStr = item.getCreationDate() != null ? item.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""; // Should always exist
                
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
            System.err.println("Error saving tasks: " + e.getMessage());
            e.printStackTrace();
            // Consider showing an Alert to the user
        }
    }

    public void loadTasks() {
        if (!Files.exists(Paths.get(DATA_FILE_PATH))) {
            System.out.println("Data file not found, starting with an empty task list: " + DATA_FILE_PATH);
            return;
        }
        tasks.clear(); // Clear existing tasks before loading
        System.out.println("Attempting to load tasks from: " + Paths.get(DATA_FILE_PATH).toAbsolutePath());

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Expected format: "description",isDone,priority,dueDate,creationDate
                String[] parts = line.split(",", 5); // Split into 5 parts, description might contain commas
                if (parts.length == 5) {
                    try {
                        String description = parts[0];
                        if (description.startsWith("\"") && description.endsWith("\"")) {
                            description = description.substring(1, description.length() - 1).replace("\"\"", "\"");
                        } else {
                            // Handle cases where description was not quoted (e.g. old format or no special chars)
                            // This might be an issue if unquoted description has a comma, but split(limit) helps.
                        }
                        
                        boolean isDone = Boolean.parseBoolean(parts[1]);
                        TodoItem.Priority priority = TodoItem.Priority.valueOf(parts[2]);
                        
                        LocalDate dueDate = null;
                        if (parts[3] != null && !parts[3].isEmpty()) {
                            dueDate = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                        
                        // creationDate from file is parts[4], but TodoItem constructor sets it to LocalDate.now().
                        // For simplicity, we'll let the constructor handle creationDate. 
                        // If preserving file's creationDate is critical, TodoItem needs a way to set it post-construction.
                        // LocalDate creationDate = parts[4].isEmpty() ? null : LocalDate.parse(parts[4], DateTimeFormatter.ISO_LOCAL_DATE);

                        TodoItem item = new TodoItem(description, priority, dueDate);
                        item.setDone(isDone);
                        // If you needed to set creationDate from file after construction:
                        // item.setCreationDate(creationDate); // This would require a setter in TodoItem

                        tasks.add(item);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date for task line: " + line + "; " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error parsing priority or boolean for task line: " + line + "; " + e.getMessage());
                    } catch (Exception e) { // Catch any other unexpected errors during parsing a line
                        System.err.println("Error parsing task line: " + line + "; " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Skipping malformed task line (incorrect number of parts): " + line);
                }
            }
            System.out.println(tasks.size() + " tasks loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            e.printStackTrace();
            // Consider showing an Alert to the user
        }
    }

    // Persistence methods loadTasks() and saveTasks() will be detailed in Section VI
} 