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
        taskListView.setMaxWidth(320);
        
        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
            selectedTask.toggleComplete(); // Change the completion status
            // selectedTask.markComplete();
            taskListView.refresh(); // Refresh the ListView to show the change
        }
        });
      
        // HBox for Title
        Label titleLabel = new Label("Title: ");
        titleLabel.setStyle("-fx-padding: 3 0 0 0;");
        titleField.setPromptText("Enter Title");
        titleField.setMaxWidth(200);
        HBox titleLayout = new HBox(5, titleLabel, titleField);

        // HBox for Description
        Label descriptionLabel = new Label("Description: ");
        descriptionLabel.setStyle("-fx-padding: 3 0 0 0;");
        descriptionField.setPromptText("Describe your task");
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
        recurrenceComboBox.getItems().addAll("Daily", "Weekly", "Monthly");
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
        addButton.setPrefWidth(70);
        // addButton.setStyle("-fx-border-color: black; -fx-border-width: 1");
        addButton.setOnAction(e -> addTask(
                titleField.getText(),
                descriptionField.getText(),
                dueDatePicker.getValue(),
                categoryComboBox.getValue(),
                priorityComboBox.getValue()
        )
        );
        taskListView.refresh();

        // Delete Task Button
        Button deleteButton = new Button("Delete");
        // deleteButton.setStyle("-fx-border-color: black; -fx-border-width: 1");
        deleteButton.setPrefWidth(70);
        deleteButton.setOnAction(e -> deleteTask());

        
        //Sort Tasks Buttons
        Label sortBy = new Label("---------------------------------\nSort By:");
        Button dueDateAscending = new Button(" Due Date\nAscending");
        Button dueDateDescending = new Button(" Due Date\nDecending");
        Button priorityAscending = new Button("   Priority\nAscending");
        Button priorityDescending = new Button("   Priority\nAscending");

        // Buttons Layout (Add, Delete)
        HBox buttonLayout1 = new HBox(20, addButton, deleteButton);

        // Buttons Layout (Sort by Due Date)
        HBox buttonLayout2 = new HBox(15, dueDateAscending, dueDateDescending);

        // Buttons Layout (Sort by Priority)
        HBox buttonLayout3 = new HBox(15, priorityAscending, priorityDescending);
        
        //Searching
        Label search = new Label("-------------------------------\nSearch by title or description: ");
        searchField.setPromptText("Enter a keyword");
        Button searchButton = new Button("Go");
        HBox searcHBox = new HBox(5, searchField, searchButton);

        // VBox for all inputs
        VBox inputLayout = new VBox(10, titleLayout, descriptionLayout, dueDateLayout, recurringLayout, categoryLayout, priorityLayout, buttonLayout1, sortBy, buttonLayout2, buttonLayout3, search, searcHBox);
        inputLayout.setPrefWidth(200);

        // Border Pane
        Label toDo = new Label("To-Do List");
        toDo.setStyle("-fx-font-size: 26; -fx-padding: 5 10 5 10;");

        BorderPane layout = new BorderPane();
        layout.setCenter(taskListView);
        layout.setRight(inputLayout);
        layout.setTop(toDo); // Add the title label to the top

        // Adding a border around the layout
        layout.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-padding: 10 20 10 10;"); // Added 20 pixels of padding to the right

        // Deselect selected task when other area is clicked
        titleField.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        descriptionField.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        dueDatePicker.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        categoryComboBox.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
        priorityComboBox.setOnMouseClicked(e -> {
            taskListView.getSelectionModel().clearSelection();
        });
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
        Scene scene = new Scene(layout, 570, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task to delete.", ButtonType.OK);
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
  
    //Sorting
    //Due Date Ascending
    public void sortDuedateAscending() {

    }

    //Due Date Descending
    public void sortDuedateDescending() {
        
    }

    //Priority Ascending
    public void sortPriorityAscending() {
        
    }

    //Priority Descending
    public void sortPriorityDescending() {
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
