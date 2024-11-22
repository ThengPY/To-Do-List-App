import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class ConnectionManager {
    private String url = "jdbc:sqlite:C:/Users/keste/IdeaProjects/To-Do-List-App/src/tasks.db";

    //conection test
    public static void main(String[] args) {
        ConnectionManager cm = new ConnectionManager();
        cm.connect();
    }


    public void connect() {
        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        /*temporarily drop
        String dropTable = "DROP TABLE IF EXISTS tasklist";
        temporarily drop
        statement.executeUpdate(dropTable);*/

    public void addTask(Task task) {
        //queries

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


        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                Statement statement = con.createStatement();
                statement.executeUpdate(createTable);

                PreparedStatement preparedStatement = con.prepareStatement(insertValue);
                preparedStatement.setInt(1, task.getTaskNumber());
                preparedStatement.setString(2, task.getTitle());
                preparedStatement.setString(3, task.getDescription());
                //date changed to unix time
                long dueDateAsInt = task.getDueDate().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                preparedStatement.setLong(4, dueDateAsInt);
                preparedStatement.setString(5, task.getCategory());
                preparedStatement.setString(6, task.getPriority());
                preparedStatement.setString(7, task.getReccurence());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void editTask(Task task) {
        //queries
        String updateValue = "UPDATE tasklist SET title=?," +
                "description=?," +
                "dueDate=?," +
                "category=?," +
                "priority=?," +
                "recurrence=? " +
                "WHERE taskNumber=?";
        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(updateValue);
                preparedStatement.setInt(7, task.getTaskNumber());
                preparedStatement.setString(1, task.getTitle());
                preparedStatement.setString(2, task.getDescription());
                //date changed to unix time
                long dueDateAsInt = task.getDueDate().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                preparedStatement.setLong(3, dueDateAsInt);
                preparedStatement.setString(4, task.getCategory());
                preparedStatement.setString(5, task.getPriority());
                preparedStatement.setString(6, task.getReccurence());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(Task task) {
        ConnectionManager cm = new ConnectionManager();
        //query
        String deleteRow = "DELETE FROM tasklist WHERE taskNumber=?";
        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(deleteRow);
                preparedStatement.setInt(1, task.getTaskNumber());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskNumber(ArrayList<Task> tasks) {
        ConnectionManager cm = new ConnectionManager();
        //query
        String updateTaskNumber = "UPDATE tasklist SET taskNumber = ? WHERE title=?";
        //String orderByTaskNumber = "SELECT * FROM tasklist ORDER BY taskNumber ASC";
        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                PreparedStatement preparedStatement = con.prepareStatement(updateTaskNumber);
                for (int i = 0; i < tasks.size(); i++) {
                    preparedStatement.setInt(1, tasks.get(i).getTaskNumber());
                    preparedStatement.setString(2, tasks.get(i).getTitle());
                    preparedStatement.executeUpdate();
                }
                //Statement statement = con.createStatement();
                //statement.executeUpdate(orderByTaskNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //not done yet
    public void loadTasks(Task task) {
        //queries
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


        try (Connection con = DriverManager.getConnection(this.url)) {
            if (con != null) {
                Statement statement = con.createStatement();
                statement.executeUpdate(createTable);

                PreparedStatement preparedStatement = con.prepareStatement(insertValue);
                preparedStatement.setInt(1, task.getTaskNumber());
                preparedStatement.setString(2, task.getTitle());
                preparedStatement.setString(3, task.getDescription());
                //date changed to unix time
                long dueDateAsInt = task.getDueDate().atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                preparedStatement.setLong(4, dueDateAsInt);
                preparedStatement.setString(5, task.getCategory());
                preparedStatement.setString(6, task.getPriority());
                preparedStatement.setString(7, task.getReccurence());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
//load data