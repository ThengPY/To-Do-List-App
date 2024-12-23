import java.sql.*;
import java.time.LocalDate;
// import java.time.Instant;
// import java.time.ZoneOffset;
import java.util.ArrayList;
import javafx.scene.control.ListView;

public class ConnectionManager {
    //change depending on path
    private static String url = "jdbc:sqlite:/Users/theng/To-Do List App/src/tasks.db";

    public static void main(String[] args) {
        ConnectionManager cm = new ConnectionManager();
        cm.connect();
    }

    public void connect() {
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addTask(Task task) {
        String createTable = "CREATE TABLE IF NOT EXISTS tasklist(" +
                "taskNumber INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "dueDate INTEGER NOT NULL," +
                "category TEXT NOT NULL," +
                "priority TEXT NOT NULL," +
                "recurrence TEXT NOT NULL)";

        String insertValue = "INSERT into tasklist(taskNumber,title,description,dueDate,category,priority,recurrence)" +
                "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                Statement statement = con.createStatement();
                statement.executeUpdate(createTable);

                PreparedStatement preparedStatement = con.prepareStatement(insertValue);
                preparedStatement.setInt(1, task.getTaskNumber());
                preparedStatement.setString(2, task.getTitle());
                preparedStatement.setString(3, task.getDescription() != "" ? task.getDescription() : "None");
                preparedStatement.setString(4, task.getDueDate() != null ? task.getDueDate().toString() : "None");
                preparedStatement.setString(5, task.getCategory() != null ? task.getCategory() : "None");
                preparedStatement.setString(6, task.getPriority() != null ? task.getPriority() : "None");
                preparedStatement.setString(7, task.getReccurence() != null ? task.getReccurence() : "None");

                // Debugging statement to verify data
                System.out.println("Inserting Task: " + task.getTitle() + ", " + task.getDescription() + ", " + task.getDueDate() + ", " + task.getCategory() + ", " + task.getPriority() + ", " + task.getReccurence());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void editTask(Task task) {
        String updateValue = "UPDATE tasklist SET title=?, description=?, dueDate=?, category=?, priority=?, recurrence=? WHERE taskNumber=?";
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(updateValue);
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription());
                
                // Check if dueDate is null and set it accordingly
                if (task.getDueDate() != null) {
                    preparedStatement.setString(3, task.getDueDate().toString());
                } else {
                    preparedStatement.setString(3, "None");
                }
                
                if (task.getCategory() != null) {
                    preparedStatement.setString(4, task.getCategory());
                } else {
                    preparedStatement.setString(4, "None");
                }
                
                if (task.getPriority() != null) {
                    preparedStatement.setString(5, task.getPriority());
                } else {
                    preparedStatement.setString(5, "None");
                }
                
                if (task.getReccurence() != null) {
                    preparedStatement.setString(6, task.getReccurence());
                } else {
                    preparedStatement.setString(6, "None");
                }

                preparedStatement.setInt(7, task.getTaskNumber());
    
                // Debugging statement to verify data
                System.out.println("Updating Task: " + task.getTitle() + ", " + task.getDescription() + ", " + (task.getDueDate() != null ? task.getDueDate().toString() : "null") + ", " + task.getCategory() + ", " + task.getPriority() + ", " + task.getReccurence());
    
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Task updated successfully: " + task.getTitle());
                } else {
                    System.out.println("Failed to update task: " + task.getTitle());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update task in the database: " + e.getMessage());
        }
    }
    public static void deleteTask(Task task) {
        String deleteRow = "DELETE FROM tasklist WHERE taskNumber=?";
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(deleteRow);
                preparedStatement.setInt(1, task.getTaskNumber());

                // Debugging statement to verify data
                System.out.println("Deleting Task: " + task.getTitle() + ", " + task.getDescription());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateTaskNumber(ArrayList<Task> tasks) {
        String updateTaskNumber = "UPDATE tasklist SET taskNumber = ? WHERE title=?";
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(updateTaskNumber);
                for (int i = 0; i < tasks.size(); i++) {
                    preparedStatement.setInt(1, tasks.get(i).getTaskNumber());
                    preparedStatement.setString(2, tasks.get(i).getTitle());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadTasks(ArrayList<Task> tasks, ListView<Task> taskListView) {
        String readValue = "SELECT * FROM tasklist ORDER BY taskNumber ASC";
    
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery(readValue);
    
                while (resultSet.next()) {
                    // Get the dueDate as a string
                    String dueDateStr = resultSet.getString("dueDate");
                    LocalDate dueDate = null;
    
                    // Check if the dueDate is not "None" before parsing
                    if (!"None".equals(dueDateStr)) {
                        dueDate = LocalDate.parse(dueDateStr);
                    }
    
                    Task newTask = new Task(
                            resultSet.getInt("taskNumber"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            dueDate,
                            resultSet.getString("category"),
                            resultSet.getString("priority"),
                            resultSet.getString("recurrence"));
                    tasks.add(newTask);
                    taskListView.getItems().add(newTask);
    
                    // Debugging statement to verify data
                    System.out.println("Loaded Task: " + newTask.getTitle() + ", " + newTask.getDescription() + ", " + newTask.getDueDate() + ", " + newTask.getCategory() + ", " + newTask.getPriority() + ", " + newTask.getReccurence());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}