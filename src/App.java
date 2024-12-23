import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class App extends Application {
    private ArrayList<Task> tasks = new ArrayList<>();
    private ListView<Task> taskListView;
    private TextField titleField = new TextField();
    private TextField descriptionField = new TextField();
    private TextField searchField = new TextField();
    private DatePicker dueDatePicker = new DatePicker();
    private ComboBox<String> priorityComboBox = new ComboBox<>();
    private ComboBox<String> categoryComboBox = new ComboBox<>();
    private ComboBox<String> recurrenceComboBox = new ComboBox<>();
    private ComboBox<Integer> dependenciesComboBox = new ComboBox<>();
    private int taskNumber = 0;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Todo List App");

        taskListView = new ListView<>();
        taskListView.setMaxWidth(450);
        
        // Mouse Click On taskListView Event
        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
                if (e.getButton() == MouseButton.SECONDARY) {
                    // Allow user to toggle complete when right clicked
                    selectedTask.toggleComplete(); 
                    taskListView.refresh();
                    // Check recurrence interval and automatically add new task when a recurring task is completed
                    if (selectedTask.getCompletionStatus() == true) {
                        if (selectedTask.getDueDate() == null && ! selectedTask.getReccurence().equals("None")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), null, selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence());
                        }
                        else if (selectedTask.getReccurence().equals("Daily")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusDays(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence());
                        }
                        else if (selectedTask.getReccurence().equals("Weekly")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusWeeks(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence());
                        }
                        else if (selectedTask.getReccurence().equals("Monthly")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusMonths(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence());
                        }
                        
                        taskListView.refresh();
                    }
                } 
                else if (e.getClickCount() == 1) {
                    // Fetch data to input fields when task is selected
                    fetchData();
                }
        });
      
        // Mark As Complete Label
        Label markAsComplete = new Label("* Right click on task to mark as complete");
        markAsComplete.setStyle("-fx-font-size: 10;");

        // Add / Edit / Delete Task Label
        Label addDelete = new Label("=== Add / Edit / Delete Tasks ===");
        addDelete.setStyle("-fx-font-weight: bold");

        // HBox for Title
        Label titleLabel = new Label("Title: ");
        titleLabel.setStyle("-fx-padding: 3 0 0 0;");
        titleField.setPromptText("Enter Title");
        titleField.setMaxWidth(200);
        HBox titleLayout = new HBox(5, titleLabel, titleField);

        // HBox for Description
        Label descriptionLabel = new Label("Description: ");
        descriptionLabel.setStyle("-fx-padding: 3 0 0 0;");
        descriptionField.setPromptText("Describe task");
        descriptionField.setMaxWidth(120); 
        descriptionField.setPrefHeight(20); 
        HBox descriptionLayout = new HBox(5, descriptionLabel, descriptionField);
        
        // HBox for Due Date
        Label dueDateLabel = new Label("Due Date:");
        dueDatePicker.setPromptText("Select Due Date");
        dueDatePicker.setMaxWidth(130);
        dueDateLabel.setStyle("-fx-padding: 5 0 0 0;");
        HBox dueDateLayout = new HBox(5, dueDateLabel, dueDatePicker);

        // HBox for Recurring Task
        Label recurrenceLabel = new Label("Recurrence\n  Interval  :");
        recurrenceLabel.setStyle("-fx-padding: 3 0 0 0;");
        recurrenceComboBox.getItems().addAll("Daily", "Weekly", "Monthly", "None");
        recurrenceComboBox.setPrefWidth(100);
        HBox recurringLayout = new HBox(10, recurrenceLabel, recurrenceComboBox);

        // HBox for Category
        Label categoryLabel = new Label("Category:");
        categoryLabel.setStyle("-fx-padding: 3 0 0 0;");
        categoryComboBox.getItems().addAll("Homework", "Personal", "Work");
        categoryComboBox.setPrefWidth(110);
        HBox categoryLayout = new HBox(10, categoryLabel, categoryComboBox);

        // HBox for Priority
        Label priorityLabel = new Label("Priority Level:");
        priorityLabel.setStyle("-fx-padding: 3 0 0 0;");
        priorityComboBox.getItems().addAll("Low", "Medium", "High");
        priorityComboBox.setPrefWidth(100);
        HBox priorityLayout = new HBox(10, priorityLabel, priorityComboBox);

        // HBox for Task Dependencies
        Label dependenciesLabel = new Label("Task Dependencies:");
        dependenciesLabel.setStyle("-fx-padding: 3 0 0 0;");
        dependenciesComboBox.setPrefWidth(80);
        
        // dependenciesComboBox.getItems().addAll(tasks.get(taskNumber));
        // dependenciesComboBox.setStyle("-fx-padding: 5 0 5 0");
        HBox dependenciesLayout = new HBox(5, dependenciesLabel, dependenciesComboBox);

        // Add Task Button
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> addTask(
                titleField.getText(),
                descriptionField.getText(),
                dueDatePicker.getValue(),
                categoryComboBox.getValue(),
                priorityComboBox.getValue(),
                recurrenceComboBox.getValue()
        )
        );

        // Edit Task Button
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> editTask());

        // Delete Task Button
        Button deleteButton = new Button("Delete");
        // buttonBorder(deleteButton);
        deleteButton.setOnAction(e -> deleteTask());

        //Sort Tasks Buttons
        Label sortBy = new Label("======== Sort Tasks ========");
        sortBy.setStyle("-fx-font-weight: bold");
        Button dueDateAscending = new Button(" Due Date\nAscending");
        Button dueDateDescending = new Button(" Due Date\nDescending");
        Button priorityAscending = new Button("   Priority\nAscending");
        Button priorityDescending = new Button("   Priority\nDescending");

        //Call sorting methods when sort buttons are clicked
        dueDateAscending.setOnAction(e -> {
            sortDuedateAscending();
        });
        dueDateDescending.setOnAction(e -> {
            sortDuedateDescending();
        });
        priorityAscending.setOnAction(e -> {
            sortPriorityAscending();
        });
        priorityDescending.setOnAction(e -> {
            sortPriorityDescending();
        });
        
        // Buttons Layout (Add, Edit, Delete)
        HBox buttonLayout1 = new HBox(15, addButton, editButton, deleteButton);

        // Buttons Layout (Sort by Due Date)
        HBox buttonLayout2 = new HBox(15, dueDateAscending, dueDateDescending);

        // Buttons Layout (Sort by Priority)
        HBox buttonLayout3 = new HBox(15, priorityAscending, priorityDescending);
        
        //Searching
        //Search Title
        Label searchTitle = new Label("== Search by Title or Description ==");

        //Search Field Layout
        Label searchLabel = new Label("Search: ");
        searchLabel.setStyle("-fx-padding: 5 0 0 0");
        searchTitle.setStyle("-fx-font-weight: bold");
        searchField.setPromptText("Enter a keyword");

        //Search Button
        Button searchButton = new Button("Search");

        //Reset Button
        Button resetButton = new Button("Reset");

        //Search and Reset Button Hbox
        HBox searchHBox = new HBox(10, searchButton, resetButton);
        searchHBox.setAlignment(Pos.CENTER_RIGHT);
        
        //Search Button Event Handler
        searchButton.setOnAction(e -> searchTasks());
        //Reset Button Event Handler
        resetButton.setOnAction(e -> resetTaskListView());

        //Analytics Dashboard
        Label analyticsLabel = new Label("===== Analytics Dashboard =====");
        analyticsLabel.setStyle("-fx-font-weight: bold");
        Label totalTasksLabel = new Label("- Total tasks: ");
        Label completedTasksLabel = new Label("- Completed: ");
        Label pendingTasksLabel = new Label("- Pending: ");
        Label completionRateLabel = new Label("- Completion Rate: ");
        Label taskCategoriesLabel = new Label("- Task Categories: ");
        VBox analyticsDashboardVBox = new VBox(2, analyticsLabel, totalTasksLabel,
        completedTasksLabel, pendingTasksLabel, completionRateLabel, taskCategoriesLabel);

        // VBox for all elements
        VBox inputLayout = new VBox(10, markAsComplete, addDelete,  titleLayout, descriptionLayout, dueDateLayout,  categoryLayout, 
        recurringLayout, priorityLayout, dependenciesLayout, buttonLayout1, sortBy, buttonLayout2, buttonLayout3, searchTitle, searchField, searchHBox, analyticsDashboardVBox);
        inputLayout.setPrefWidth(200);

        // Border Pane
        Label toDo = new Label("To-Do List");
        toDo.setStyle("-fx-font-size: 22; -fx-padding: 5 10 5 15; -fx-font-weight: bold");
        BorderPane layout = new BorderPane();
        layout.setCenter(taskListView);
        layout.setRight(inputLayout);
        layout.setTop(toDo); // Add the title label to the top

        // Adding a border around the layout
        layout.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 10 20 10 10;"); // Added 20 pixels of padding to the right

        // Deselect task when other area / buttons are clicked
        layout.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
            dependenciesComboBox.getItems().clear();
        });
        addButton.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        dueDateAscending.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        dueDateDescending.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        priorityAscending.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        priorityDescending.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });

        // Setting the scene
        Scene scene = new Scene(layout, 720, 760);
        primaryStage.setScene(scene);
        primaryStage.show();

        //check database for existing tasks
        loadTasks();
    }

    //Tasks Functions
    // Add Tasks to To-Do List
    private void addTask(String title, String description, LocalDate dueDate, String category, String priority, String recurrence) {
        if (title.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a title.", ButtonType.OK);
            alert.showAndWait();
        } else {
            if (taskListView.getItems().isEmpty()) {
                taskNumber = 0;
            }
            taskNumber++;
            Task newTask = new Task(taskNumber, title, description, dueDate, category, priority, recurrence);
            tasks.add(newTask);
            taskListView.getItems().add(newTask);
            clearInputFields();
            taskListView.refresh();
            ConnectionManager.addTask(newTask);

            // Debugging statement to verify data
            System.out.println("Added Task: " + newTask.getTitle() + ", " + newTask.getDescription() + ", " + newTask.getDueDate() + ", " + newTask.getCategory() + ", " + newTask.getPriority() + ", " + newTask.getReccurence());
        }
    }

    //Startup tasks
    public void loadTasks(){
        ConnectionManager.loadTasks(tasks,taskListView);
        taskNumber=tasks.size();
        taskListView.refresh();
    }

    //Delete Selected Task From To-Do List
    private void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            taskListView.getItems().remove(selectedTask);
            taskListView.getSelectionModel().clearSelection();
            clearInputFields();
            //Reassign Task Number After Deleting A Task
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setTaskNumber(i+1);
            }
            taskNumber--; // to assign correct task number when add new task
            taskListView.refresh();
            //delete from database and update task number
            ConnectionManager.deleteTask(selectedTask);
            ConnectionManager.updateTaskNumber(tasks);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to delete.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Fetch Data
    public void fetchData() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        titleField.setText(selectedTask.getTitle());
        descriptionField.setText((selectedTask.getDescription()));
        dueDatePicker.setValue(selectedTask.getDueDate());
        categoryComboBox.setValue(selectedTask.getCategory());
        priorityComboBox.setValue(selectedTask.getPriority());
        recurrenceComboBox.setValue(selectedTask.getReccurence());

        // Fill in Dependencies Combo Box
        dependenciesComboBox.getItems().clear();
        for (Task task : tasks) {
            if (task.getTaskNumber() != selectedTask.getTaskNumber()) {
                dependenciesComboBox.getItems().add(task.getTaskNumber());
            }
        }
    }

    //Edit Tasks
    public void editTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskListView.refresh();
            selectedTask.setTitle(titleField.getText());
            selectedTask.setDescription(descriptionField.getText());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setCategory(categoryComboBox.getValue());
            selectedTask.setPriority(priorityComboBox.getValue());
            selectedTask.setRecurrence(recurrenceComboBox.getValue());
            taskListView.getSelectionModel().clearSelection();
            clearInputFields();
            //update values in database
            ConnectionManager.editTask(selectedTask);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to edit.", ButtonType.OK);
            alert.showAndWait();
        }
    }


     // Clear input fields after adding new task
     public void clearInputFields() {
        titleField.clear();
        descriptionField.clear();
        dueDatePicker.setValue(null);
        categoryComboBox.setValue(null);
        priorityComboBox.setValue(null);
        recurrenceComboBox.setValue(null);
        dependenciesComboBox.setValue(null);
    }

    //Sorting
    //Due Date Ascending
    public void sortDuedateAscending() {
        
        for (int i = 0; i < tasks.size() -1; i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
                Task task1 = tasks.get(i);
                Task task2 = tasks.get(j);

                if (task1.getDueDate() == null && task2.getDueDate() == null) {
                    continue; // Both are null, do nothing
                } else if (task1.getDueDate() == null) {
                    // If dueDate1 is null, it should come after dueDate2
                    // Swap task1 and task2
                    tasks.set(i, task2);
                    tasks.set(j, task1);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                    continue;
                } else if (task2.getDueDate() == null) {
                    // If dueDate2 is null, it should come after dueDate1
                    continue;
                }
    
                if (task2.getDueDate().isBefore(task1.getDueDate())) {
                    //Swap Tasks
                    tasks.set(i, task2);
                    tasks.set(j, task1);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                }
            }
        }
        //update task number
        ConnectionManager.updateTaskNumber(tasks);
        updateTaskListView();
    }

    //Due Date Descending
    public void sortDuedateDescending() {
        for (int i = 0; i < tasks.size() -1; i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
                Task task1 = tasks.get(i);
                Task task2 = tasks.get(j);
                
                if (task1.getDueDate() == null && task2.getDueDate() == null) {
                    continue; // Both are null, do nothing
                } else if (task1.getDueDate() == null) {
                    // If dueDate1 is null, it should come after dueDate2
                    // Swap task1 and task2
                    tasks.set(i, task2);
                    tasks.set(j, task1);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                    continue;
                } else if (task2.getDueDate() == null) {
                    // If dueDate2 is null, it should come after dueDate1
                    continue;
                }

                if (task2.getDueDate().isAfter(task1.getDueDate())) {
                    //Swap Tasks
                    tasks.set(i, task2);
                    tasks.set(j, task1);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                }
            }
        }
        //update task number
        ConnectionManager.updateTaskNumber(tasks);
        updateTaskListView();
    }

    //Priority Ascending
    public int priorityLevelAscending(String priority) {   // Assign number based on priority level
        switch (priority) {
            case "Low":
                return 1;
            case "Medium":
                return 2;
            case "High":
                return 3;
            case null:
                return 4;
            default:
                return 5;
        }
    }

    public void sortPriorityAscending() {
        for (int i = 0; i < tasks.size() - 1; i++) {
             for (int j = i + 1; j < tasks.size(); j++) {
                Task task1 = tasks.get(i);
                Task task2 = tasks.get(j);
                String priority1 = task1.getPriority(); 
                String priority2 = task2.getPriority();
               
                if ((priorityLevelAscending(priority2) < priorityLevelAscending(priority1))) {
                    //Swap Tasks
                    tasks.set(i, task2);
                    tasks.set(j, task1);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                }
      }
    }
    //update task number
    ConnectionManager.updateTaskNumber(tasks);
    updateTaskListView();
}
    

    //Priority Descending
    public int priorityLevelDescending(String priority) {
        switch (priority) {
            case "Low":
                return 1;
            case "Medium":
                return 2;
            case "High":
                return 3;
            case null:
                return 0;
            default:
                return 0;
        }
    }
    public void sortPriorityDescending() {
        for (int i = 0; i < tasks.size() - 1; i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
               Task task1 = tasks.get(i);
               Task task2 = tasks.get(j);
               String priority1 = task1.getPriority();
               String priority2 = task2.getPriority();
               
               if ((priorityLevelDescending(priority2) > priorityLevelDescending(priority1))) {
                   //Swap Tasks
                   tasks.set(i, task2);
                   tasks.set(j, task1);

                   //Swap Task Number
                   int tempNum = task1.getTaskNumber();
                   task1.setTaskNumber(task2.getTaskNumber());
                   task2.setTaskNumber(tempNum);
                }
            }
        }
        //update task number
        ConnectionManager.updateTaskNumber(tasks);
        updateTaskListView();
    }

    // Update Tasks List After Sorting
    private void updateTaskListView() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(tasks);
    }

    //Search Tasks
    private void searchTasks() {
        String searchInput = searchField.getText().toLowerCase();
        // Clear the current list view  
         taskListView.getItems().clear();

        if (searchInput.isEmpty()) {
        // If search input is empty, show all tasks
            taskListView.getItems().addAll(tasks);
        } 
        else {
        // Add tasks that match the search input
        for (Task task : tasks) {
            if ((task.getTitle() != null && task.getTitle().toLowerCase().contains(searchInput)) ||
                (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchInput))) {
                    taskListView.getItems().add(task);
            }
        }
        // Tell user no task match with search input
        if (taskListView.getItems().isEmpty()) {
            taskListView.getItems().addAll(tasks);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No Task Found!", ButtonType.OK);
            alert.showAndWait();
        }
    }
        searchField.clear();
    }

    //Reset Tasks List View to Original After Searching
    private void resetTaskListView() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(tasks);
        searchField.clear();
    }

    // Add Task Dependencies
    // private void addTaskDependencies() {

    // }

    // Connect with database
    // static Connection conn;
    // ResultSet rs;
    // int row, col;


    public static void main(String[] args) {
        launch(args);
    }
}