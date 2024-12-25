import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;


public class Task {
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean isComplete = false;
    private String category;
    private String priority;
    private ArrayList<Task> dependencies;
    private int taskNumber;
    private  String recurrence;

    // Constructor
    public Task(int taskNumber, String title, String description, LocalDate dueDate, String category, String priority, String recurrence) {
        this.taskNumber = taskNumber;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.category = category;
        this.priority = priority;
        this.recurrence = recurrence;
        this.dependencies = new ArrayList<>();
    }
    //Constructor with dependency
    public Task(int taskNumber, String title, String description, LocalDate dueDate, String category, String priority, String recurrence,String dependantTaskTitle,ArrayList<Task> tasks) {
        this.taskNumber = taskNumber;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.isComplete = false;
        this.category = category;
        this.priority = priority;
        this.recurrence = recurrence;
        this.dependencies = new ArrayList<>();
        if(dependantTaskTitle!=null) {
            String[] splitDependantTaskTitle = dependantTaskTitle.split(",");
            for (String s : splitDependantTaskTitle) {
                for (Task task : tasks) {
                    if (task.getTitle().equals(s)) {
                        this.dependencies.add(task);
                        //System.out.println(this.title + " task dependency added " + task.getTitle());
                    }
                }
            }
        }

    }

    // Getters and Setters
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

    // Get Completion Status
    public boolean getCompletionStatus() {
        return this.isComplete;
    }

    // Toggle task as complete / pending
    public void toggleComplete() {
        this.isComplete = ! this.isComplete;
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

    // Get Reccurrence 
    public String getReccurence() {
        return this.recurrence;
    }

    // Set Reccurrence
    public void setRecurrence(String newRecurrence) {
        this.recurrence = newRecurrence;
    }

    //Dependencies
    // Get Dependencies
    public ArrayList<Task> getDependencies() {
        return this.dependencies;
    }
    // Add Dependencies
    public void addDependency(String title,ArrayList<Task> tasks) {
        for(Task task: tasks) {
            if(task.getTitle().equals(title)) {
                this.dependencies.add(task);
                //System.out.println(this.title+" task dependency added "+task.getTitle());
            }
        }
    }
    // Set Dependencies
    public void setDependencies(ArrayList<Task> newDependencies) {
        this.dependencies = newDependencies;
    }

    // Check if all dependencies are completed
    public boolean areAllDependenciesCompleted() {
        for (Task dependency : dependencies) {
            if (!dependency.getCompletionStatus()) {
                return false; // Found an incomplete dependency
            }
        }
        return true; // All dependencies are completed
    }
    


    

    @Override
    public String toString() {
        String displayTitle = isComplete ? "~~~~ " + title + " ~~~~  (COMPLETED)" : title;
        return String.format("%d.  %s\n     Description:  %s\n     Due:  %s\n     Category:  %s\n     Recurrence:  %s\n     Priority Level:  %s\n",
                taskNumber , displayTitle, description != "" ? description : "None", dueDate != null ? dueDate.toString() : "None",
                category != null ? category : "None",
                recurrence != null ? recurrence : "None",
                priority != null ? priority : "None"
               );
    }
}
