package command;

import exception.DukeException;
import parser.SmartDateParser;
import storage.Storage;
import tasklist.Deadline;
import tasklist.Event;
import tasklist.Task;
import tasklist.TaskList;
import tasklist.Todo;
import ui.Ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * EditCommand allows users to modify existing tasks
 * Supports editing description, date/time, and task type conversion
 */
public class EditCommand extends Command {
    private final String[] command;
    
    public EditCommand(String[] command) {
        this.command = command;
    }
    
    /**
     * Execute the edit command to modify an existing task
     * @param taskList the task list
     * @param ui to print messages
     * @param storage to save changes
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws DukeException, IOException {
        validateEditCommand(command, taskList);
        
        int taskIndex = Integer.parseInt(command[1]) - 1;
        Task originalTask = taskList.get(taskIndex);
        
        // Parse the edit instruction
        String editInstruction = command[2].toLowerCase().trim();
        String newValue = command.length > 3 ? command[3] : "";
        
        Task editedTask = performEdit(originalTask, editInstruction, newValue);
        
        // Replace the task in the list
        taskList.removeTask(taskIndex);
        taskList.addTask(editedTask);
        
        // Save changes and notify user
        storage.writeToFile(taskList.getTasks());
        ui.printEditSuccess(originalTask, editedTask);
        ui.printTaskCount(taskList);
    }
    
    /**
     * Perform the actual edit operation based on the instruction
     */
    private Task performEdit(Task originalTask, String editInstruction, String newValue) throws DukeException {
        switch (editInstruction) {
            case "description":
            case "desc":
                return editDescription(originalTask, newValue);
                
            case "date":
            case "time":
            case "datetime":
                return editDateTime(originalTask, newValue);
                
            case "type":
                return convertTaskType(originalTask, newValue);
                
            case "todo":
                return convertToTodo(originalTask);
                
            case "deadline":
                return convertToDeadline(originalTask, newValue);
                
            case "event":
                return convertToEvent(originalTask, newValue);
                
            default:
                throw new DukeException("Invalid edit instruction. Use: description, date, type, todo, deadline, or event");
        }
    }
    
    /**
     * Edit the description of a task
     */
    private Task editDescription(Task originalTask, String newDescription) throws DukeException {
        if (newDescription.trim().isEmpty()) {
            throw new DukeException("New description cannot be empty");
        }
        
        String taskType = originalTask.getType();
        boolean isDone = originalTask.isDone();
        
        Task newTask;
        if (taskType.equals("T")) {
            newTask = new Todo("todo " + newDescription);
        } else if (taskType.equals("D")) {
            String dateTimeStr = originalTask.getDateTimeStr();
            newTask = new Deadline("deadline " + newDescription + " /by " + dateTimeStr, originalTask.getDateTime());
        } else { // Event
            String dateTimeStr = originalTask.getDateTimeStr();
            newTask = new Event("event " + newDescription + " /at " + dateTimeStr, originalTask.getDateTime());
        }
        
        newTask.setDone(isDone);
        return newTask;
    }
    
    /**
     * Edit the date/time of a deadline or event
     */
    private Task editDateTime(Task originalTask, String newDateTimeStr) throws DukeException {
        if (originalTask.getType().equals("T")) {
            throw new DukeException("Cannot set date/time for a todo task. Convert to deadline or event first.");
        }
        
        try {
            LocalDateTime newDateTime = SmartDateParser.parseDateTime(newDateTimeStr);
            String description = originalTask.getDescription();
            boolean isDone = originalTask.isDone();
            
            Task newTask;
            if (originalTask.getType().equals("D")) {
                newTask = new Deadline("deadline " + description + " /by " + newDateTimeStr, newDateTime);
            } else {
                newTask = new Event("event " + description + " /at " + newDateTimeStr, newDateTime);
            }
            
            newTask.setDone(isDone);
            return newTask;
        } catch (DateTimeParseException e) {
            throw new DukeException("Invalid date/time format. " + SmartDateParser.getSupportedFormats());
        }
    }
    
    /**
     * Convert task to a different type
     */
    private Task convertTaskType(Task originalTask, String newType) throws DukeException {
        switch (newType.toLowerCase()) {
            case "todo":
            case "t":
                return convertToTodo(originalTask);
            case "deadline":
            case "d":
                throw new DukeException("To convert to deadline, use: edit [task_number] deadline [date/time]");
            case "event":
            case "e":
                throw new DukeException("To convert to event, use: edit [task_number] event [date/time]");
            default:
                throw new DukeException("Invalid task type. Use: todo, deadline, or event");
        }
    }
    
    /**
     * Convert any task to a todo (removes date/time)
     */
    private Task convertToTodo(Task originalTask) {
        String description = originalTask.getDescription();
        boolean isDone = originalTask.isDone();
        
        Task newTask = new Todo("todo " + description);
        newTask.setDone(isDone);
        return newTask;
    }
    
    /**
     * Convert any task to a deadline
     */
    private Task convertToDeadline(Task originalTask, String dateTimeStr) throws DukeException {
        if (dateTimeStr.trim().isEmpty()) {
            throw new DukeException("Please specify a deadline date/time");
        }
        
        try {
            LocalDateTime dateTime = SmartDateParser.parseDateTime(dateTimeStr);
            String description = originalTask.getDescription();
            boolean isDone = originalTask.isDone();
            
            Task newTask = new Deadline("deadline " + description + " /by " + dateTimeStr, dateTime);
            newTask.setDone(isDone);
            return newTask;
        } catch (DateTimeParseException e) {
            throw new DukeException("Invalid date/time format. " + SmartDateParser.getSupportedFormats());
        }
    }
    
    /**
     * Convert any task to an event
     */
    private Task convertToEvent(Task originalTask, String dateTimeStr) throws DukeException {
        if (dateTimeStr.trim().isEmpty()) {
            throw new DukeException("Please specify an event date/time");
        }
        
        try {
            LocalDateTime dateTime = SmartDateParser.parseDateTime(dateTimeStr);
            String description = originalTask.getDescription();
            boolean isDone = originalTask.isDone();
            
            Task newTask = new Event("event " + description + " /at " + dateTimeStr, dateTime);
            newTask.setDone(isDone);
            return newTask;
        } catch (DateTimeParseException e) {
            throw new DukeException("Invalid date/time format. " + SmartDateParser.getSupportedFormats());
        }
    }
    
    /**
     * Validate the edit command format and parameters
     */
    private void validateEditCommand(String[] command, TaskList taskList) throws DukeException {
        if (command.length < 3) {
            throw new DukeException(getEditHelpMessage());
        }
        
        // Validate task number
        try {
            int taskNumber = Integer.parseInt(command[1]) - 1;
            if (taskNumber < 0 || taskNumber >= taskList.size()) {
                throw new DukeException("Task number " + command[1] + " is not valid. You have " + taskList.size() + " tasks.");
            }
        } catch (NumberFormatException e) {
            throw new DukeException("Please provide a valid task number");
        }
        
        // Check if certain edit operations require additional parameters
        String editType = command[2].toLowerCase();
        if ((editType.equals("description") || editType.equals("desc")) && command.length < 4) {
            throw new DukeException("Please provide a new description: edit [task_number] description [new description]");
        }
        
        if ((editType.equals("date") || editType.equals("time") || editType.equals("datetime")) && command.length < 4) {
            throw new DukeException("Please provide a new date/time: edit [task_number] date [new date/time]");
        }
        
        if ((editType.equals("deadline") || editType.equals("event")) && command.length < 4) {
            throw new DukeException("Please provide a date/time: edit [task_number] " + editType + " [date/time]");
        }
    }
    
    /**
     * Get help message for edit command
     */
    private String getEditHelpMessage() {
        return "Edit command usage:\n" +
               "📝 Edit description: edit [task_number] description [new description]\n" +
               "📅 Edit date/time:   edit [task_number] date [new date/time]\n" +
               "🔄 Convert to todo:  edit [task_number] todo\n" +
               "⏰ Convert to deadline: edit [task_number] deadline [date/time]\n" +
               "📆 Convert to event: edit [task_number] event [date/time]\n" +
               "\nExamples:\n" +
               "  edit 1 description Buy groceries and cook dinner\n" +
               "  edit 2 date tomorrow 3pm\n" +
               "  edit 3 deadline next friday 6pm\n" +
               "  edit 4 todo";
    }
} 