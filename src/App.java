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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private ComboBox<String> dependenciesComboBox = new ComboBox<>();
    private int taskNumber = 0;
    private HashMap<String, List<String>> graph;

    // New variables for email functionality
    private TextField emailField = new TextField();  // Email input field
    private Button setEmailButton = new Button("Set Email for Reminders"); // Button to set email
    private String userEmail = "";  // Store the user's email for reminder notifications

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Todo List App");

        taskListView = new ListView<>();
        taskListView.setMaxWidth(450);

        // Mouse Click On taskListView Event
        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (e.getButton() == MouseButton.SECONDARY) {
                //Completable check
                if (selectedTask.areAllDependenciesCompleted()) {
                    // Allow user to toggle complete when right-clicked
                    selectedTask.toggleComplete();
                    taskListView.refresh();
                    // Check recurrence interval and automatically add new task when a recurring task is completed
                    if (selectedTask.getCompletionStatus() == true) {
                        StringBuilder stringDependentTask = new StringBuilder();
                        //string dependant task title name to (a,b,c,)
                        for (Task taskDependentTasks : selectedTask.getDependencies()) {
                            stringDependentTask.append(taskDependentTasks.getTitle());
                            stringDependentTask.append(",");
                        }
                        if (selectedTask.getDueDate() == null && !selectedTask.getReccurence().equals("None")) {
                            //nothing happens if null
                        } else if (selectedTask.getReccurence().equals("Daily")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusDays(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence(), stringDependentTask.toString());
                        } else if (selectedTask.getReccurence().equals("Weekly")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusWeeks(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence(), stringDependentTask.toString());
                        } else if (selectedTask.getReccurence().equals("Monthly")) {
                            addTask(selectedTask.getTitle(), selectedTask.getDescription(), selectedTask.getDueDate().plusMonths(1), selectedTask.getCategory(), selectedTask.getPriority(), selectedTask.getReccurence(), stringDependentTask.toString());
                        }

                        taskListView.refresh();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Task dependencies uncompleted", ButtonType.OK);
                    alert.showAndWait();
                }
            } else if (e.getClickCount() == 1) {
                // Fetch data to input fields when task is selected
                fetchData();
            }
        });

        // Email input field
        Label emailLabel = new Label("Email Address for Reminders: ");
        emailField.setPromptText("Your Email Address");

        // Button to set email address
        setEmailButton.setOnAction(e -> {
            userEmail = emailField.getText().trim();
            if (!userEmail.isEmpty()) {
                System.out.println("Email set for reminders: " + userEmail);
                startEmailReminderTask();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid email address.", ButtonType.OK);
                alert.showAndWait();
            }
        });


        // Add the event listener for dependenciesComboBox
        dependenciesComboBox.setOnShowing(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                populateDependenciesComboBox(selectedTask);
            }
        });

        // Layout for the email input and button
        HBox emailLayout = new HBox(5, emailLabel, emailField, setEmailButton);
        emailLayout.setAlignment(Pos.CENTER);

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
                        recurrenceComboBox.getValue(),
                        dependenciesComboBox.getValue()
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
        VBox inputLayout = new VBox(10,emailLayout,markAsComplete, addDelete, titleLayout, descriptionLayout, dueDateLayout, categoryLayout,
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
        Scene scene = new Scene(layout, 900, 760);
        primaryStage.setScene(scene);
        primaryStage.show();

        //check database for existing tasks
        loadTasks();

        //System.out.println(tasks.get(0).getDependencies().toString());
        //System.out.printf("True or False: %s\n",hasCycle(tasks,tasks.get(0)));
    }

   //Email Reminder Task Function
    private void startEmailReminderTask() {
        // Start a scheduled task that runs every 24 hours to check for tasks due in 24 hours
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> sendTaskReminderEmails(), 0, 24, TimeUnit.HOURS);
    }

    private void sendTaskReminderEmails() {
        if (userEmail.isEmpty()) {
            System.out.println("No email address set for notifications.");
            return;
        }
        // Instantiate the ConnectionManager and get tasks due in the next 24 hours
        ConnectionManagers connectionManager = new ConnectionManagers();
        ArrayList<Task> tasksDueSoon = connectionManager.getTasksDueIn24Hours();

        if (tasksDueSoon.isEmpty()) {
            System.out.println("No tasks due in the next 24 hours.");
            return;
        }
        for (Task task : tasksDueSoon) {
            String subject = "Task Reminder: " + task.getTitle();
            String content = "You have a task due in 24 hours:\n\n" +
                    "Title: " + task.getTitle() + "\n" +
                    "Description: " + task.getDescription() + "\n" +
                    "Due Date: " + task.getDueDate() + "\n" +
                    "Category: " + task.getCategory() + "\n" +
                    "Priority: " + task.getPriority() + "\n";

            // Call a method to send the email
            EmailNotification.sendEmail(userEmail, subject, content);
        }
    }

    //Tasks Functions
    // Add Tasks to To-Do List
    //Startup tasks
    public void loadTasks(){
        ConnectionManagers.loadTasks(tasks,taskListView);
        taskNumber=tasks.size();
        taskListView.refresh();
    }

    private void addTask(String title, String description, LocalDate dueDate, String category, String priority, String recurrence,String dependencies) {
        if (title.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a title.", ButtonType.OK);
            alert.showAndWait();
        } else {
            if (taskListView.getItems().isEmpty()) {
                taskNumber = 0;
            }

            taskNumber++;
            Task newTask = new Task(taskNumber, title, description, dueDate, category, priority, recurrence,dependencies,tasks);
            tasks.add(newTask);
            taskListView.getItems().add(newTask);
            clearInputFields();
            taskListView.refresh();
            ConnectionManagers.addTask(newTask);

            // Debugging statement to verify data
            System.out.println("Added Task: " + newTask.getTitle() + ", " + newTask.getDescription() + ", " + newTask.getDueDate() + ", " + newTask.getCategory() + ", " + newTask.getPriority() + ", " + newTask.getReccurence()+ ", " + newTask.getDependencies().toString());
        }
    }

    //Edit Tasks
    public void editTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            //Store the original dependencies in a temporary list
            ArrayList<Task> originalDependencies = new ArrayList<>(selectedTask.getDependencies());
            taskListView.refresh();
            selectedTask.setTitle(titleField.getText());
            selectedTask.setDescription(descriptionField.getText());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setCategory(categoryComboBox.getValue());
            selectedTask.setPriority(priorityComboBox.getValue());
            selectedTask.setRecurrence(recurrenceComboBox.getValue());
            selectedTask.addDependency(dependenciesComboBox.getValue(),tasks);

            if(hasCycle(tasks,selectedTask)){

                Alert alert = new Alert(Alert.AlertType.ERROR, "Cycle Detected", ButtonType.OK);
                alert.showAndWait();
                //reset selectedTask
                selectedTask.setDependencies(originalDependencies);
                return;
            }

            taskListView.getSelectionModel().clearSelection();
            clearInputFields();
            //update values in database
            ConnectionManagers.editTask(selectedTask);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to edit.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    //Delete Selected Task From To-Do List
    private void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            removeTask(selectedTask);
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
            ConnectionManagers.deleteTask(selectedTask);
            ConnectionManagers.updateTaskNumber(tasks);
            for(Task task : tasks){
                ConnectionManagers.editTask(task);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to delete.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void removeTask(Task taskToRemove) {
        // Remove the task from the main list
        tasks.remove(taskToRemove);

        // Clean up dependencies in other tasks
        for (Task task : tasks) {
            task.getDependencies().remove(taskToRemove); // Remove the reference to the removed task
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

       populateDependenciesComboBox(selectedTask);
    }

    //Fill in Dependencies Combo Box for addTask
    private void populateDependenciesComboBox() {
        dependenciesComboBox.getItems().clear(); // Clear existing items
        for (Task task : tasks) {
            // Check if the task is not already a dependency
                dependenciesComboBox.getItems().add(task.getTitle()); // Add task titles to the combo box
        }
    }

    //Fill in Dependencies Combo Box for editTask
    private void populateDependenciesComboBox(Task selectedTask) {
        dependenciesComboBox.getItems().clear(); // Clear existing items
        for (Task task : tasks) {
            // Check if the task is not already a dependency
            if (!task.equals(selectedTask)&&!selectedTask.getDependencies().contains(task)) {
                dependenciesComboBox.getItems().add(task.getTitle()); // Add task titles to the combo box
            }
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
        ConnectionManagers.updateTaskNumber(tasks);
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
        ConnectionManagers.updateTaskNumber(tasks);
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
    ConnectionManagers.updateTaskNumber(tasks);
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
        ConnectionManagers.updateTaskNumber(tasks);
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

    public static boolean hasCycle(ArrayList<Task> tasks, Task newTask) {
        HashSet<Task> visited = new HashSet<>();
        HashSet<Task> recursionStack = new HashSet<>();

        // Start the cycle detection from the new task
        return hasCycleUtil(newTask, visited, recursionStack, tasks);
    }

    private static boolean hasCycleUtil(Task task, HashSet<Task> visited, HashSet<Task> recursionStack, ArrayList<Task> tasks) {
        if (recursionStack.contains(task)) {
            return true; // Cycle detected
        }
        if (visited.contains(task)) {
            return false; // Already visited this node
        }

        visited.add(task);
        recursionStack.add(task);

        // Check dependencies of the current task
        for (Task dependency : task.getDependencies()) {
            if (hasCycleUtil(dependency, visited, recursionStack, tasks)) {
                return true; // Cycle detected in the dependency
            }
        }

        recursionStack.remove(task); // Remove from recursion stack
        return false; // No cycle detected
    }
    public static void main(String[] args) {
        launch(args);
    }
}