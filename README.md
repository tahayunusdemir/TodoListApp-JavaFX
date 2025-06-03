# Todo List Application (JavaFX & MVC)

## 1. Project Title and Authors

*   **Project Title**: Todo List Application
*   **Author**: Taha Yunus Demir, Naz Çelik

## 2. Introduction

This is a simple Todo List application developed using JavaFX. It allows users to manage their tasks by adding, viewing, editing, and deleting them. Tasks can be assigned a description, priority (High, Medium, Low), and a due date. The application follows the Model-View-Controller (MVC) architectural pattern for a clear separation of concerns and better maintainability. Task data is persisted locally in a CSV file.

## 3. Features Implemented

*   **Task Management (CRUD)**:
    *   **Add Task**: Add new tasks with description, priority, and due date.
    *   **View Tasks**: Display tasks in a table with status, description, priority, due date, and creation date.
    *   **Edit Task**: Edit existing tasks directly in the table (description, priority, due date).
    *   **Delete Task**: Delete individual tasks using a delete button in each row.
    *   **Mark Complete/Incomplete**: Toggle task completion status using a checkbox.
*   **Task Properties**:
    *   Description: Textual detail of the task.
    *   Priority: High, Medium, or Low.
    *   Due Date: Date by which the task should be completed.
    *   Creation Date: Automatically recorded when a task is created.
    *   Status: Indicates whether a task is completed or active.
*   **User Interface**:
    *   Clear input area for adding new tasks.
    *   `TableView` for displaying tasks with sortable columns.
    *   Visual styling for tasks (strikethrough for completed, color-coding for priority, and visual cues for overdue/due soon tasks).
*   **Filtering**:
    *   Filter tasks by "All", "Active", or "Completed" status.
*   **Bulk Operations**:
    *   "Delete Completed Tasks" button to remove all completed tasks at once.
*   **Persistence**:
    *   Tasks are saved to a local CSV file (`tasks.csv`) on application close.
    *   Tasks are loaded from the CSV file on application startup.
*   **MVC Architecture**:
    *   **Model**: `TodoItem.java` (represents a single task), `TodoListManager.java` (manages the list of tasks and persistence).
    *   **View**: `MainView.fxml` (defines the UI layout), `styles.css` (defines UI appearance).
    *   **Controller**: `MainViewController.java` (handles user interactions and mediates between View and Model).

## 4. Design Choices

*   **Technology Stack**:
    *   JavaFX: For the graphical user interface.
    *   Java: Core programming language.
*   **Architecture**:
    *   MVC (Model-View-Controller): Chosen for its separation of concerns, making the application easier to develop, test, and maintain.
        *   The Model (`todolistapp.model`) is independent of JavaFX UI components.
        *   The View (`todolistapp.view`) is defined in FXML and styled with CSS.
        *   The Controller (`todolistapp.controller`) links the View and Model, handling UI events and data flow.
*   **Persistence**:
    *   CSV (Comma Separated Values) format: Chosen for its simplicity for this project's scope. The file `tasks.csv` stores task data.
    *   Save on Close: Data is automatically saved when the application is closed.
    *   Load on Startup: Data is automatically loaded when the application starts.
*   **User Experience**:
    *   Inline editing in the `TableView` for quick modifications.
    *   Visual cues (colors, strikethrough) for task status and priority.
    *   Confirmation dialogs for delete operations.
    *   Input validation for adding new tasks.
*   **JavaFX Properties and `ObservableList`**:
    *   Used extensively in the Model (`TodoItem`) and `TodoListManager` to enable automatic updates in the `TableView` when data changes.

## 5. How to Run the Application

1.  **Prerequisites**:
    *   **JDK (Java Development Kit)**: Version 21 or later (Project configured for JDK 24).
    *   **JavaFX SDK**: Version 21 or later (Project configured with JavaFX 24 libraries).
    *   **Apache NetBeans IDE**: Recommended, as the project is set up as a NetBeans project. (Tested with NetBeans)

2.  **Setup in NetBeans**:
    *   Clone the repository or download the source code.
    *   Open the project in NetBeans (`File > Open Project...` and select the `Proje/TodoListApp` folder).
    *   **Configure JavaFX Library (if not automatically detected)**:
        *   Ensure you have a JavaFX SDK downloaded (e.g., from [GluonHQ](https://gluonhq.com/products/javafx/)).
        *   In NetBeans, go to `Tools > Libraries`.
        *   Click "New Library...", name it (e.g., `JavaFX24`).
        *   Add all `*.jar` files from the `lib` folder of your JavaFX SDK to this library.
        *   Right-click the `TodoListApp` project, select "Properties".
        *   Go to "Libraries".
        *   Under the "Compile" tab, click "Add Library..." and add your `JavaFX24` library.
        *   Go to the "Run" tab, and ensure the `JavaFX24` library is also listed or add it.
    *   **VM Options**:
        *   In "Project Properties" -> "Run", ensure "VM Options" are set. The project is configured with:
            `--add-modules javafx.controls,javafx.fxml,javafx.web`
            (If you encounter issues, you might need to specify the module path: `--module-path "PATH_TO_YOUR_JAVAFX_SDK/lib" --add-modules javafx.controls,javafx.fxml,javafx.web`. Replace `PATH_TO_YOUR_JAVAFX_SDK` with the actual path.)

3.  **Set Main Class**:
    *   In "Project Properties" -> "Run", ensure "Main Class" is set to:
        `todolistapp.TodoListApplication`

4.  **Clean and Build**:
    *   Right-click the project and select "Clean and Build".

5.  **Run**:
    *   Right-click the project and select "Run" (or press F6).

6.  **Data File**:
    *   The application saves task data in a file named `tasks.csv` located in the root directory where the application is run from (usually the `Proje/TodoListApp/` directory when run from NetBeans).
    *   This `tasks.csv` file is included in `.gitignore` and should not be committed if it contains user-specific data.

## 6. Known Issues or Limitations

*   **CSV Parsing**: The current CSV parsing in `TodoListManager` is basic. It handles quoted descriptions with escaped quotes but might be fragile with extremely complex CSV edge cases. A dedicated CSV library could improve robustness.
*   **Error Handling**: While some error handling is present (e.g., for file I/O, data parsing), it primarily logs to the console. In a production application, user-facing error dialogs would be more appropriate for critical errors.
*   **Creation Date Preservation**: The `creationDate` from the CSV file is not strictly preserved upon loading; the `TodoItem` constructor re-initializes it to `LocalDate.now()` when a task is loaded. This was a simplification; if historical creation dates are critical, the loading logic and `TodoItem` would need adjustment.
*   **No "Edit" Dialog**: Editing is done inline in the table. A separate dialog for editing could offer a more structured editing experience for complex tasks, but this was not implemented as per the plan's focus on inline editing.
*   **Time Zones**: Dates are handled as `java.time.LocalDate` without explicit time zone management. This is generally fine for a local desktop application but could be a consideration for applications with data shared across different time zones.

## 7. External Libraries/Resources Used

*   **Java Development Kit (JDK)**: Standard Java platform.
*   **JavaFX SDK**: For the user interface components and framework. Sourced from GluonHQ.
*   No other external third-party libraries are used beyond the standard JDK and JavaFX.