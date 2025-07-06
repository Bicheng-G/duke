package tasklist;

import java.time.LocalDateTime;

/**
 * A <code>Task</code> class is the parent class of all types of tasks
 * Now supports priorities and categories for better task organization
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String type;
    protected LocalDateTime dateTime;
    protected Priority priority;
    protected Category category;

    /**
     * Default task constructor
     * @param description parses from user input
     */
    public Task(String description) {
        this.isDone = false;
        
        // Parse priority and category from description
        this.priority = Priority.fromText(description);
        this.category = Category.fromText(description);
        
        // Clean description by removing priority and category markers
        String cleanDescription = description;
        cleanDescription = Priority.removePriorityFromText(cleanDescription);
        cleanDescription = Category.removeCategoriesFromText(cleanDescription);
        
        this.description = cleanDescription;
    }

    public String getDescription() {return description;}

    //isDone.Setter
    public void setDone(boolean isDone) {this.isDone = isDone;}

    public boolean isDone() { return isDone; }

    //isDone.Getter
    public String getStatusIcon() {
        return (isDone ? "√" : " "); // mark done task with X
    }

    public String getType(){return type;}

    public String getDateTimeStr(){
        return "";
    }

    public LocalDateTime getDateTime(){return dateTime;}

    /**
     * Get task priority
     * @return priority enum
     */
    public Priority getPriority() {
        return priority != null ? priority : Priority.NORMAL;
    }

    /**
     * Set task priority
     * @param priority priority to set
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Get task category
     * @return category object
     */
    public Category getCategory() {
        return category != null ? category : new Category();
    }

    /**
     * Set task category
     * @param category category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Get enhanced task display with priority and category
     * @return formatted task string
     */
    public String printTask(){
        StringBuilder task = new StringBuilder();
        
        // Add priority icon
        task.append(getPriority().getIcon()).append(" ");
        
        // Add basic task format
        task.append("[").append(getType()).append("]");
        task.append("[").append(getStatusIcon()).append("] ");
        task.append(description);
        
        // Add date/time if exists
        String dateTimeStr = getDateTimeStr();
        if (!dateTimeStr.isEmpty()) {
            task.append(" ").append(dateTimeStr);
        }
        
        // Add categories if exists
        if (getCategory().hasCategories()) {
            task.append(" ").append(getCategory().getDisplayString());
        }
        
        return task.toString();
    }

    public String[] getKeyword(){
        return description.split(" ");
    }

}