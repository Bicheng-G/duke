package tasklist;

/**
 * An Todo object holds todo type of task which does not contains date and time.
 * Now supports priorities and categories for better task organization.
 */
public class Todo extends Task{
    public Todo(String description){
        // Remove "todo " prefix before parsing and call super first
        super(description.startsWith("todo ") ? description.substring(5) : description);
        this.type = "T";
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public String[] getKeyword (){
        return description.split(" ");
    }
}
