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
    private ArrayList<Task> dependencies; 
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

    // Get Task Number
    public int getTaskNumber() {
        return this.taskNumber;
    }

    //Set Task Number
    public void setTaskNumber(int newTaskNumber) {
        this.taskNumber = newTaskNumber;
    }
    
    // Get Title
    public String getTitle() {
        return this.title;
    }

    // Set Title
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    // Get Description
    public String getDescription() {
        return this.description;
    }

    // Set Description
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    // Get Due Date
    public LocalDate getDueDate() {
        return this.dueDate;
    }

    // Set Due Date
    public void setDueDate(LocalDate newDueDate) {
        this.dueDate = newDueDate;
    }

    // Get Category
    public String getCategory() {
        return this.category;
    }

    // Set Category
    public void setCategory(String newCategory) {
        this.category = newCategory;
    }

    // Get Priority
    public String getPriority() {
        return this.priority;
    }

    // Set Priority
    public void setPriority(String newPriority) {
        this.priority = newPriority;
    }

    // Toggle task as complete / pending
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
        String displayTitle = isComplete ? "~~~~ " + title + " ~~~~  (COMPLETED)" : title;
        return String.format("%d.  %s\n     Description:  %s\n     Due:  %s\n     Category:  %s\n     Priority Level:  %s",
                taskNumber ,displayTitle, description != "" ? description : "None", dueDate != null ? dueDate.toString() : "None",
                category != null ? category : "None",
                priority != null ? priority : "None",
                recurring != null ? recurring : "None");
    }
}

