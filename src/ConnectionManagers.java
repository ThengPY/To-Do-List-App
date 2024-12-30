import java.sql.*;
import java.time.LocalDate;
// import java.time.Instant;
// import java.time.ZoneOffset;
import java.util.ArrayList;
import javafx.scene.control.ListView;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ConnectionManagers {
    //change depending on path
    private static String url = "jdbc:sqlite:C:/Users/60115/IdeaProjects/To-Do-List-App2/src/tasks.db";

    public static void main(String[] args) {
        ConnectionManagers cm = new ConnectionManagers();
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
                "title TEXT," +
                "description TEXT," +
                "dueDate INTEGER," +
                "category TEXT," +
                "priority TEXT," +
                "recurrence TEXT," +
                "dependency TEXT)";

        String insertValue = "INSERT into tasklist(taskNumber,title,description,dueDate,category,priority,recurrence,dependency)" +
                "VALUES (?,?,?,?,?,?,?,?)";

        StringBuilder stringDependentTask= new StringBuilder();
        //string dependant task title name to (a,b,c,)
        for(Task taskDependentTasks:task.getDependencies()){
            stringDependentTask.append(taskDependentTasks.getTitle());
            stringDependentTask.append(",");
        }

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
                preparedStatement.setString(8, stringDependentTask.toString());

                // Debugging statement to verify data
                System.out.println("Inserting Task: " + task.getTitle() + ", " + task.getDescription() + ", " + task.getDueDate() + ", " + task.getCategory() + ", " + task.getPriority() + ", " + task.getReccurence() + ", " + stringDependentTask.toString());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void editTask(Task task) {
        StringBuilder stringDependentTask = new StringBuilder();
        // String dependent task title names (e.g., "a,b,c")
        for (Task taskDependentTasks : task.getDependencies()) {
            stringDependentTask.append(taskDependentTasks.getTitle());
            stringDependentTask.append(",");
        }
        String updateValue = "UPDATE tasklist SET title=?," +
                            "description=?," +
                            "dueDate=?," +
                            "category=?," +
                            "priority=?," +
                            "recurrence=?," +
                            "dependency=?" +
                            "WHERE taskNumber=?";
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(updateValue);
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription() != null ? task.getDescription() : "None");
                preparedStatement.setString(3, task.getDueDate() != null ? task.getDueDate().toString() : "None");
                preparedStatement.setString(4, task.getCategory() != null ? task.getCategory() : "None");
                preparedStatement.setString(5, task.getPriority() != null ? task.getPriority() : "None");
                preparedStatement.setString(6, task.getReccurence() != null ? task.getReccurence() : "None");
                preparedStatement.setString(7, stringDependentTask.toString());
                preparedStatement.setInt(8, task.getTaskNumber());
    
                // Debugging statement to verify data
                System.out.println("Updating Task: " + task.getTitle() + ", " + task.getDescription() + ", " + task.getDueDate() + ", " + task.getCategory() + ", " + task.getPriority() + ", " + task.getReccurence() + ", [" + stringDependentTask.toString() + "]");
    
                int rowsUpdated = preparedStatement.executeUpdate();
                System.out.println("Rows updated: " + rowsUpdated); // Debug statement
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
        String createTable = "CREATE TABLE IF NOT EXISTS tasklist(" +
                "taskNumber INTEGER NOT NULL," +
                "title TEXT," +
                "description TEXT," +
                "dueDate INTEGER," +
                "category TEXT," +
                "priority TEXT," +
                "recurrence TEXT," +
                "dependency TEXT)";

        String readValue = "SELECT * FROM tasklist ORDER BY taskNumber ASC";
    
        try (Connection con = DriverManager.getConnection(url)) {
            if (con != null) {
                Statement statement = con.createStatement();
                statement.executeUpdate(createTable);
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
                            resultSet.getString("recurrence")
                    );
                    tasks.add(newTask);
                    taskListView.getItems().add(newTask);
    
                    // Debugging statement to verify data
                    System.out.println("Loaded Task: " + newTask.getTitle() + ", " + newTask.getDescription() + ", " + newTask.getDueDate() + ", " + newTask.getCategory() + ", " + newTask.getPriority() + ", " + newTask.getReccurence());
                }

                //Read dependency column
                resultSet = statement.executeQuery(readValue);
                int i=0;
                while (resultSet.next()) {
                    String title =resultSet.getString("title");
                    String dependantTaskTitle =resultSet.getString("dependency");
                    if(dependantTaskTitle!=null){
                        //for debug
                        //System.out.printf("Connection manager\t%s add dependency %s\n",title,dependantTaskTitle);
                        String[] splitDependantTaskTitle = dependantTaskTitle.split(",");
                        for (String s : splitDependantTaskTitle) {
                            for (Task task : tasks) {
                                if (task.getTitle().equals(s)) {
                                    tasks.get(i).addDependency(task.getTitle(),tasks);
                                }
                            }
                        }
                    }
                    i++;

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Task> getTasksDueIn24Hours() {
        ArrayList<Task> tasksDueSoon = new ArrayList<>();
        String query = "SELECT * FROM tasklist WHERE dueDate IS NOT NULL";

        try (Connection con = DriverManager.getConnection(url)) {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String dueDateStr = resultSet.getString("dueDate");
                LocalDate dueDate = LocalDate.parse(dueDateStr);

                if (dueDate.minusDays(1).equals(LocalDate.now())) {
                    Task task = new Task(
                            resultSet.getInt("taskNumber"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            dueDate,
                            resultSet.getString("category"),
                            resultSet.getString("priority"),
                            resultSet.getString("recurrence")
                    );
                    tasksDueSoon.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasksDueSoon;
    }
}




