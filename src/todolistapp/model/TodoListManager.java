package todolistapp.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class TodoListManager {

    private final ObservableList<TodoItem> tasks = FXCollections.observableArrayList();

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
    }

    // Persistence methods (loadTasks() and saveTasks()) will be added later.
} 