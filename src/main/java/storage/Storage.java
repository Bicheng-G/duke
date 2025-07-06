package storage;

import parser.Parser;
import tasklist.Deadline;
import tasklist.Event;
import tasklist.Task;
import tasklist.Todo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import static parser.Parser.commandToArray;


/**
 * The storage class handles the read and write function
 *  read and write tasks list to and from the task.txt file
 */
public class Storage {

    protected String filePath;

    public Storage (String filePath){
        this.filePath = filePath;
    }

    public String getFilePath(){return filePath;}

    /**
     * The method writes any changes to the task list to the tasks.txt file
     * @param taskList is the task list
     * @throws IOException when file or directory is not found
     */
    public void writeToFile(ArrayList <Task> taskList) throws IOException {
        File file = new File(filePath);
        File dir = file.getParentFile();
        if(!file.exists()){
            if(!dir.exists()){
                dir.mkdirs();
            }
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        for(Task task : taskList){
            String type = task.getType();
            String toText = null;
            String isDone = task.isDone() ? "√" : " ";
            String description = task.getDescription();

            switch (type){
                case "T":
                    toText = "todo [" + isDone + "] " + description + System.getProperty("line.separator");
                    break;
                case "D":
                    toText = "deadline [" + isDone + "] " + description + " /by " + task.getDateTimeStr() + System.getProperty("line.separator");
                    break;
                case "E":
                    toText = "event [" + isDone + "] " + description + " /at " + task.getDateTimeStr() + System.getProperty("line.separator");
                    break;
            }
            assert toText != null;
            fw.write(toText);
        }
        fw.close();
    }


    /**
     * The method reads the task list from the tasks.txt file
     * @throws FileNotFoundException when file or directory is not found
     */
    public ArrayList<Task> readFromFile() throws FileNotFoundException {
        ArrayList<Task> taskList = new ArrayList<>();
        File file = new File(filePath);
        Scanner sc = new Scanner(file);
        while(sc.hasNext()){
            String text = sc.nextLine();
            String parseText = text.replaceAll(" \\[.*?\\] ", " ");
            boolean isDone = text.contains("√");
            String[] command = commandToArray(parseText);
            String keyword = command[0];
            LocalDateTime dateTime;
            Task task;

            if(keyword.equals("todo")){
                task = new Todo(parseText);
                task.setDone(isDone);
            }
            else if(keyword.equals("deadline")){
                dateTime = Parser.parseDateTimeFromFile(command);
                task = new Deadline(parseText, dateTime);
                task.setDone(isDone);
            }
            else{
                dateTime = Parser.parseDateTimeFromFile(command);
                task = new Event(parseText, dateTime);
                task.setDone(isDone);
            }
            taskList.add(task);
        }
        return taskList;
    }
}
