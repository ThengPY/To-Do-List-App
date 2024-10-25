import java.time.LocalDate;
import java.util.ArrayList;

public class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean isComplete;
    private String category;
    private String priority;
    private String recurring;
    private ArrayList<Task> dependencies; // List of dependent tasks
    private int taskNumber;

    // Constructor
    public Task(int taskNumber, String title, String description, LocalDate dueDate, String category, String priority) {
        this.taskNumber = taskNumber;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.category = category;
        this.priority = priority;
        this.dependencies = new ArrayList<>();
    }

    // Toggle task as completed
    public void toggleComplete() {
        this.isComplete = !this.isComplete;
    }

    //Dependencies
    public void addDependency(Task task) {
        dependencies.add(task);
    }

    public boolean canComplete() {
        for (Task dependency : dependencies) {
            if (!dependency.isComplete) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String displayTitle = isComplete ? "~~~" + title + "~~~ (COMPLETED)" : title;
        return String.format("%d.  %s\nDescription:  %s\nDue:  %s\nCategory:  %s\nPriority Level:  %s",
                taskNumber ,displayTitle, description != "" ? description.toString() : "None", dueDate != null ? dueDate.toString() : "None",
                category != null ? category : "None",
                priority != null ? priority : "None",
                recurring != null ? recurring : "None");
    }
}

