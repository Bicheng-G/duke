import command.Command;
import demo.DemoTaskGenerator;
import exception.DukeException;
import parser.Parser;
import storage.Storage;
import tasklist.Task;
import tasklist.TaskList;
import ui.Ui;

import java.io.*;
import java.util.List;

/**
 * The duke class is the main class of the program
 */
public class Duke {

    private TaskList taskList;
    private final Storage storage;
    private final Ui ui;

    public Duke() {
        String filePath = "src/data/tasks.txt";
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            taskList = new TaskList(storage.readFromFile());
            
            // If task list is empty, load demo tasks for first-time users
            if (taskList.size() == 0) {
                loadDemoTasks();
            }
        } catch (DukeException e) {
            ui.exceptionMessage(e);
            taskList = new TaskList();
            loadDemoTasks();
        } catch (FileNotFoundException e) {
            ui.exceptionMessage(e);
            taskList = new TaskList();
            loadDemoTasks();
        }
    }

    public Duke(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            taskList = new TaskList(storage.readFromFile());
            
            // If task list is empty, load demo tasks for first-time users
            if (taskList.size() == 0) {
                loadDemoTasks();
            }
        } catch (DukeException e) {
            ui.exceptionMessage(e);
            taskList = new TaskList();
            loadDemoTasks();
        } catch (FileNotFoundException e) {
            ui.exceptionMessage(e);
            taskList = new TaskList();
            loadDemoTasks();
        }
    }

    private boolean isDemoMode = false;
    
    public void run() {
        Ui.Welcome();
        
        // Initialize the suggestion system
        ui.initializeSuggestionSystem(taskList);
        
        // Show demo welcome message if we loaded demo tasks
        if (isDemoMode) {
            System.out.println(DemoTaskGenerator.getWelcomeMessage());
            ui.Separator();
        }
        
        boolean isExit = false;
        while(!isExit){
            try {
                String userInput = ui.userInputWithSuggestions();
                ui.Separator();
                Command c = Parser.parse(userInput, taskList);
                c.execute(taskList, ui, storage);
                isExit = c.isExit();
            } catch (DukeException | IOException e){
                ui.errorMessage(e);
            } finally {
                ui.Separator();
            }
        }
    }
    
    /**
     * Load demo tasks for first-time users
     */
    private void loadDemoTasks() {
        try {
            List<Task> demoTasks = DemoTaskGenerator.generateDemoTasks();
            for (Task task : demoTasks) {
                taskList.addTask(task);
            }
            
            // Save demo tasks to file
            storage.writeToFile(taskList.getTasks());
            isDemoMode = true;
            
        } catch (IOException e) {
            System.err.println("Warning: Could not save demo tasks to file: " + e.getMessage());
        }
    }

    /**
     * The method is used to capture the ui messages and show on the GUI
     * @param input is the user input from GUI
     * @return string of ui messages
     */
    public String dukeReply(String input) {
        try {
            Command c = Parser.parse(input, taskList);
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
            c.execute(taskList, ui, storage);
            return outContent.toString();
        } catch (DukeException | IOException e) {
            return e.getMessage();
        } catch (NullPointerException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
    }

    /**
     * Get response from Duke for the given input
     * @param input user input
     * @return Duke's response
     */
    public String getResponse(String input) {
        return dukeReply(input);
    }

    public static void main(String[] args){
        new Duke("src/data/tasks.txt").run();
    }
}
