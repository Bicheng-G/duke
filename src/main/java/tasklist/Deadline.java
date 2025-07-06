package tasklist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * An Deadline object holds deadline type of task which contains LocalDateTime variable
 * Now supports priorities and categories for better task organization.
 */
public class Deadline extends Task {

    public Deadline(String description, LocalDateTime dateTime){
        // Extract clean description and call super first
        super(extractCleanDescription(description));
        this.type = "D";
        this.isDone = false;
        this.dateTime = dateTime;
    }
    
    private static String extractCleanDescription(String description) {
        String cleanDescription = description;
        
        // Remove "deadline " prefix
        if (cleanDescription.startsWith("deadline ")) {
            cleanDescription = cleanDescription.substring(9);
        }
        
        // Remove "/by" date part
        int byIndex = cleanDescription.indexOf(" /by ");
        if (byIndex != -1) {
            cleanDescription = cleanDescription.substring(0, byIndex);
        }
        
        return cleanDescription;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getDateTimeStr() {
        return "(by: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)) + ")";
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public String[] getKeyword (){
        return description.split(" ");
    }

}
