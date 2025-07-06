package tasklist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * An Event object holds event type of task which contains LocalDateTime variable
 * Now supports priorities and categories for better task organization.
 */
public class Event extends Task{

    public Event(String description, LocalDateTime dateTime){
        // Extract clean description and call super first
        super(extractCleanDescription(description));
        this.type = "E";
        this.isDone = false;
        this.dateTime = dateTime;
    }
    
    private static String extractCleanDescription(String description) {
        String cleanDescription = description;
        
        // Remove "event " prefix
        if (cleanDescription.startsWith("event ")) {
            cleanDescription = cleanDescription.substring(6);
        }
        
        // Remove "/at" date part
        int atIndex = cleanDescription.indexOf(" /at ");
        if (atIndex != -1) {
            cleanDescription = cleanDescription.substring(0, atIndex);
        }
        
        return cleanDescription;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getDateTimeStr() {
        return "(at: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)) + ")";
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public String[] getKeyword (){
        return description.split(" ");
    }
}
