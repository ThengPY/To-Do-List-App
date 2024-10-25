import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private int taskNumber = 0;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Todo List App");
        
        taskListView = new ListView<>();
        taskListView.setMaxWidth(400);
        
        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            
            if (e.getClickCount() == 2) {
                clearInputFields();
                selectedTask.toggleComplete(); // Change the completion status
            }
            if (e.getClickCount() == 1) {
                fetchData();
            }
            taskListView.refresh(); // Refresh the ListView to show the change
        });
      
        // Mark As Complete Label
        Label markAsComplete = new Label("* Double click to mark task as complete");
        markAsComplete.setStyle("-fx-font-size: 10;");

        // Add / Edit / Delete task label
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
        Label recurringLabel = new Label("Recurrence\nInterval: ");
        recurringLabel.setStyle("-fx-padding: 3 0 0 0;");
        recurrenceComboBox.getItems().addAll("Daily", "Weekly", "Monthly", "None");
        recurrenceComboBox.setPrefWidth(100);
        HBox recurringLayout = new HBox(10, recurringLabel, recurrenceComboBox);

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

        // Add Task Button
        Button addButton = new Button("Add");
        // buttonBorder(addButton);
        addButton.setOnAction(e -> addTask(
                titleField.getText(),
                descriptionField.getText(),
                dueDatePicker.getValue(),
                categoryComboBox.getValue(),
                priorityComboBox.getValue()
        )
        );
        taskListView.refresh();

        // Edit Task Button
        Button editButton = new Button("Edit");
        // buttonBorder(editButton);
        editButton.setOnAction(e -> editTask());

        // Delete Task Button
        Button deleteButton = new Button("Delete");
        // buttonBorder(deleteButton);
        deleteButton.setOnAction(e -> deleteTask());

        //Sort Tasks Buttons
        Label sortBy = new Label("======== Sort Tasks ========");
        sortBy.setStyle("-fx-font-weight: bold");
        Button dueDateAscending = new Button(" Due Date\nAscending");
        Button dueDateDescending = new Button(" Due Date\nDecending");
        Button priorityAscending = new Button("   Priority\nAscending");
        Button priorityDescending = new Button("   Priority\nDescending");
        // buttonBorder(dueDateAscending);
        // buttonBorder(dueDateDescending);
        // buttonBorder(priorityAscending);
        // buttonBorder(priorityDescending);

        //Call Sorting methods
        dueDateAscending.setOnAction(e -> {
            sortDuedateAscending();
            updateTaskListView();
        });
        dueDateDescending.setOnAction(e -> {
            sortDuedateDescending();
            updateTaskListView();
        });
        priorityAscending.setOnAction(e -> {
            sortPriorityAscending();
            updateTaskListView();
        });
        priorityDescending.setOnAction(e -> {
            sortPriorityDescending();
            updateTaskListView();
        });
        
        // Buttons Layout (Add, Edit, Delete)
        HBox buttonLayout1 = new HBox(15, addButton, editButton, deleteButton);

        // Buttons Layout (Sort by Due Date)
        HBox buttonLayout2 = new HBox(15, dueDateAscending, dueDateDescending);

        // Buttons Layout (Sort by Priority)
        HBox buttonLayout3 = new HBox(15, priorityAscending, priorityDescending);
        
        //Searching
        Label searchLabel = new Label("== Search By Title or Description ==");
        searchLabel.setStyle("-fx-font-weight: bold");
        searchField.setPromptText("Enter a keyword");
        Button searchButton = new Button("Go");
        HBox searcHBox = new HBox(5, searchField, searchButton);

        // VBox for all inputs
        VBox inputLayout = new VBox(10, markAsComplete, addDelete,  titleLayout, descriptionLayout, dueDateLayout, recurringLayout, categoryLayout, 
        priorityLayout, buttonLayout1, sortBy, buttonLayout2, buttonLayout3, searchLabel, searcHBox);
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

        // Deselect selected task when other area is clicked
        // titleField.setOnMouseClicked(e -> {
        //     taskListView.getSelectionModel().clearSelection();
        // });
        // descriptionField.setOnMouseClicked(e -> {
        //     taskListView.getSelectionModel().clearSelection();
        // });
        // dueDatePicker.setOnMouseClicked(e -> {
        //     taskListView.getSelectionModel().clearSelection();
        // });
        // categoryComboBox.setOnMouseClicked(e -> {
        //     taskListView.getSelectionModel().clearSelection();
        // });
        // priorityComboBox.setOnMouseClicked(e -> {
        //     taskListView.getSelectionModel().clearSelection();
        // });

        layout.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
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
        Scene scene = new Scene(layout, 650, 580);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Tasks Functions
    // Add Task to To Do List
    private void addTask(String title, String description, LocalDate dueDate, String category, String priority) {
        if (title == "") {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a title.", ButtonType.OK);
            alert.showAndWait();
        }
        else {
            if (taskListView.getItems().isEmpty()) {
                taskNumber = 0;
            }
            taskNumber++;
            Task newTask = new Task(taskNumber, title, description, dueDate, category, priority);
            tasks.add(newTask);
            taskListView.getItems().add(newTask);
            clearInputFields();
        }
    }

    //Delete Selected Task From To Do List
    private void deleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            taskListView.getItems().remove(selectedTask);
            taskListView.getSelectionModel().clearSelection();
            clearInputFields();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to delete.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    //Edit Tasks
    public void fetchData() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        titleField.setText(selectedTask.getTitle());
        descriptionField.setText((selectedTask.getDescription()));
        dueDatePicker.setValue(selectedTask.getDueDate());
        categoryComboBox.setValue(selectedTask.getCategory());
        priorityComboBox.setValue(selectedTask.getPriority());
    }

    public void editTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskListView.refresh();
            selectedTask.setTitle(titleField.getText());
            selectedTask.setDescription(descriptionField.getText());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setCategory(categoryComboBox.getValue());
            selectedTask.setPriority(priorityComboBox.getValue());
            taskListView.getSelectionModel().clearSelection();
            clearInputFields();
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
    }

    //Add Button Border
    // public void buttonBorder(Button button) {
    //     button.setStyle("-fx-border-color: black; -fx-border-width: 1;  -fx-border-radius: 2;");
    // }

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
                    // If dueDate2 is null, it should come after dueDate1
                    // Swap task1 and task2
                    tasks.set(i, task2);
                    tasks.set(j, task1);
                    continue;
                } else if (task2.getDueDate() == null) {
                    // If dueDate1 is null, it should come after dueDate2
                    continue;
                }
    
                if (task2.getDueDate().isBefore(task1.getDueDate())) {
                    //Swap Tasks
                    Task temp = tasks.get(i);
                    tasks.set(i, task2);
                    tasks.set(j, temp);

                    //Swap Task Number
                    int tempNum = task1.getTaskNumber();
                    task1.setTaskNumber(task2.getTaskNumber());
                    task2.setTaskNumber(tempNum);
                }
            }
        }
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
                    // If dueDate2 is null, it should come after dueDate1
                    // Swap task1 and task2
                    tasks.set(i, task2);
                    tasks.set(j, task1);
                    continue;
                } else if (task2.getDueDate() == null) {
                    // If dueDate1 is null, it should come after dueDate2
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
    }

    //Priority Ascending
    public int priorityLevelAscending(String priority) {
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
    }

    // Update Tasks List After Sorting
    private void updateTaskListView() {
        taskListView.getItems().clear();
        taskListView.getItems().addAll(tasks);
    }

    public static void main(String[] args) {
        launch(args);
    }
}