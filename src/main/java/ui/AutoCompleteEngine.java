package ui;

import tasklist.*;
import parser.SmartDateParser;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto-complete engine for smart command and content suggestions
 */
public class AutoCompleteEngine {
    
    private final TaskList taskList;
    private final List<String> commandHistory;
    private static final int MAX_HISTORY = 50;
    
    // Predefined command templates
    private static final String[] COMMANDS = {
        "list", "todo", "deadline", "event", "done", "delete", 
        "edit", "search", "view", "help", "reset", "bye"
    };
    
    // Common date suggestions
    private static final String[] DATE_SUGGESTIONS = {
        "today", "tomorrow", "next week", "next monday", "next tuesday", 
        "next wednesday", "next thursday", "next friday", "this friday",
        "next month", "end of month", "monday 9am", "friday 5pm"
    };
    
    // Common priority suggestions
    private static final String[] PRIORITY_SUGGESTIONS = {
        "!low", "!normal", "!high", "!urgent", "!critical"
    };
    
    public AutoCompleteEngine(TaskList taskList) {
        this.taskList = taskList;
        this.commandHistory = new ArrayList<>();
    }
    
    /**
     * Get auto-complete suggestions for partial input
     * @param partialInput the partial command typed by user
     * @return list of suggestions sorted by relevance
     */
    public List<String> getSuggestions(String partialInput) {
        if (partialInput == null || partialInput.trim().isEmpty()) {
            return getCommonCommands();
        }
        
        String input = partialInput.trim().toLowerCase();
        List<String> suggestions = new ArrayList<>();
        
        // 1. Command suggestions
        if (!input.contains(" ")) {
            suggestions.addAll(getCommandSuggestions(input));
        } else {
            // 2. Context-aware suggestions based on command
            suggestions.addAll(getContextSuggestions(input));
        }
        
        // 3. Remove duplicates and sort by relevance
        return suggestions.stream()
            .distinct()
            .sorted((a, b) -> compareRelevance(a, b, input))
            .limit(10)
            .collect(Collectors.toList());
    }
    
    /**
     * Get command completion suggestions
     */
    private List<String> getCommandSuggestions(String partial) {
        return Arrays.stream(COMMANDS)
            .filter(cmd -> cmd.startsWith(partial))
            .map(cmd -> cmd + " ")
            .collect(Collectors.toList());
    }
    
    /**
     * Get context-aware suggestions based on the command being typed
     */
    private List<String> getContextSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        String[] parts = input.split(" ");
        String command = parts[0];
        
        switch (command) {
            case "todo":
                suggestions.addAll(getTodoSuggestions(input));
                break;
            case "deadline":
                suggestions.addAll(getDeadlineSuggestions(input));
                break;
            case "event":
                suggestions.addAll(getEventSuggestions(input));
                break;
            case "edit":
                suggestions.addAll(getEditSuggestions(input));
                break;
            case "search":
                suggestions.addAll(getSearchSuggestions(input));
                break;
            case "done":
            case "delete":
                suggestions.addAll(getTaskNumberSuggestions(input));
                break;
            case "view":
                suggestions.addAll(getDateSuggestions(input));
                break;
        }
        
        return suggestions;
    }
    
    /**
     * Get suggestions for todo commands
     */
    private List<String> getTodoSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        // If just "todo", suggest common task descriptions
        if (input.equals("todo")) {
            suggestions.addAll(Arrays.asList(
                "todo Buy groceries !high #personal @home",
                "todo Call dentist !normal #health @phone",
                "todo Exercise !normal #health @gym",
                "todo Review code !high #work @computer"
            ));
        } else {
            // Suggest priorities and categories
            if (!containsPriority(input)) {
                suggestions.addAll(Arrays.asList(PRIORITY_SUGGESTIONS));
            }
            suggestions.addAll(getCategorySuggestions(input));
        }
        
        return suggestions;
    }
    
    /**
     * Get suggestions for deadline commands
     */
    private List<String> getDeadlineSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        if (input.equals("deadline")) {
            suggestions.addAll(Arrays.asList(
                "deadline Submit report /by tomorrow 5pm !urgent #work",
                "deadline Pay bills /by end of month !normal #personal",
                "deadline Complete project /by next friday !high #work"
            ));
        } else if (input.contains("/by")) {
            // Suggest dates after /by
            for (String dateSugg : DATE_SUGGESTIONS) {
                suggestions.add(input + " " + dateSugg);
            }
        } else if (!input.contains("/by")) {
            // Suggest adding /by
            suggestions.add(input + " /by ");
            suggestions.add(input + " /by tomorrow 5pm");
            suggestions.add(input + " /by next friday");
        }
        
        return suggestions;
    }
    
    /**
     * Get suggestions for event commands
     */
    private List<String> getEventSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        if (input.equals("event")) {
            suggestions.addAll(Arrays.asList(
                "event Team meeting /at monday 2pm #work @office",
                "event Lunch with client /at tomorrow 12pm #work @restaurant",
                "event Doctor appointment /at next week 10am #health @clinic"
            ));
        } else if (input.contains("/at")) {
            // Suggest times after /at
            for (String dateSugg : DATE_SUGGESTIONS) {
                suggestions.add(input + " " + dateSugg);
            }
        } else if (!input.contains("/at")) {
            // Suggest adding /at
            suggestions.add(input + " /at ");
            suggestions.add(input + " /at tomorrow 2pm");
            suggestions.add(input + " /at monday 9am");
        }
        
        return suggestions;
    }
    
    /**
     * Get suggestions for edit commands
     */
    private List<String> getEditSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        String[] parts = input.split(" ");
        
        if (parts.length == 1) {
            // Suggest task numbers
            for (int i = 1; i <= Math.min(taskList.size(), 10); i++) {
                suggestions.add("edit " + i + " ");
            }
        } else if (parts.length == 2) {
            // Suggest edit operations
            suggestions.addAll(Arrays.asList(
                input + " description ",
                input + " date ",
                input + " deadline ",
                input + " event ",
                input + " todo"
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Get suggestions for search commands
     */
    private List<String> getSearchSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        if (input.equals("search")) {
            // Suggest common search terms
            Set<String> categories = getExistingCategories();
            Set<String> contexts = getExistingContexts();
            
            for (String category : categories) {
                suggestions.add("search #" + category);
            }
            
            for (String context : contexts) {
                suggestions.add("search @" + context);
            }
            
            // Common search terms
            suggestions.addAll(Arrays.asList(
                "search meeting",
                "search report", 
                "search urgent",
                "search project"
            ));
        }
        
        return suggestions;
    }
    
    /**
     * Get task number suggestions for done/delete commands
     */
    private List<String> getTaskNumberSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        for (int i = 1; i <= Math.min(taskList.size(), 5); i++) {
            Task task = taskList.get(i - 1);
            String taskDesc = task.getDescription();
            if (taskDesc.length() > 30) {
                taskDesc = taskDesc.substring(0, 30) + "...";
            }
            suggestions.add(input + " " + i + " # " + taskDesc);
        }
        
        return suggestions;
    }
    
    /**
     * Get date suggestions
     */
    private List<String> getDateSuggestions(String input) {
        return Arrays.stream(DATE_SUGGESTIONS)
            .map(date -> input + " " + date)
            .collect(Collectors.toList());
    }
    
    /**
     * Get category suggestions based on existing tasks
     */
    private List<String> getCategorySuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        Set<String> categories = getExistingCategories();
        Set<String> contexts = getExistingContexts();
        
        for (String category : categories) {
            if (!input.contains("#" + category)) {
                suggestions.add(input + " #" + category);
            }
        }
        
        for (String context : contexts) {
            if (!input.contains("@" + context)) {
                suggestions.add(input + " @" + context);
            }
        }
        
        return suggestions;
    }
    
    /**
     * Get existing categories from tasks
     */
    private Set<String> getExistingCategories() {
        Set<String> categories = new HashSet<>();
        for (Task task : taskList.getTasks()) {
            categories.addAll(task.getCategory().getTags());
        }
        
        // Add common categories if none exist
        if (categories.isEmpty()) {
            categories.addAll(Arrays.asList("work", "personal", "health", "shopping"));
        }
        
        return categories;
    }
    
    /**
     * Get existing contexts from tasks
     */
    private Set<String> getExistingContexts() {
        Set<String> contexts = new HashSet<>();
        for (Task task : taskList.getTasks()) {
            contexts.addAll(task.getCategory().getContexts());
        }
        
        // Add common contexts if none exist
        if (contexts.isEmpty()) {
            contexts.addAll(Arrays.asList("home", "office", "phone", "computer"));
        }
        
        return contexts;
    }
    
    /**
     * Check if input contains priority
     */
    private boolean containsPriority(String input) {
        for (String priority : PRIORITY_SUGGESTIONS) {
            if (input.contains(priority)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get common commands for empty input
     */
    private List<String> getCommonCommands() {
        return Arrays.asList(
            "list",
            "todo Buy groceries !high #personal @home",
            "deadline Submit report /by tomorrow 5pm !urgent #work",
            "event Team meeting /at monday 2pm #work @office",
            "search #work",
            "help"
        );
    }
    
    /**
     * Compare relevance of suggestions
     */
    private int compareRelevance(String a, String b, String input) {
        // Exact prefix match has highest priority
        boolean aStartsWith = a.toLowerCase().startsWith(input);
        boolean bStartsWith = b.toLowerCase().startsWith(input);
        
        if (aStartsWith && !bStartsWith) return -1;
        if (!aStartsWith && bStartsWith) return 1;
        
        // Shorter suggestions are more relevant
        return Integer.compare(a.length(), b.length());
    }
    
    /**
     * Add command to history for learning
     */
    public void addToHistory(String command) {
        if (command != null && !command.trim().isEmpty()) {
            commandHistory.add(0, command.trim());
            
            // Keep history size manageable
            if (commandHistory.size() > MAX_HISTORY) {
                commandHistory.remove(commandHistory.size() - 1);
            }
        }
    }
    
    /**
     * Get smart date suggestions based on current context
     */
    public List<String> getSmartDateSuggestions() {
        List<String> suggestions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Add formatted current date/time options
        suggestions.add("today " + now.format(DateTimeFormatter.ofPattern("H:mm")));
        suggestions.add("tomorrow " + now.format(DateTimeFormatter.ofPattern("H:mm")));
        suggestions.add("next monday 9am");
        suggestions.add("friday 5pm");
        suggestions.add("end of week");
        
        return suggestions;
    }
    
    /**
     * Get template suggestions for quick task creation
     */
    public Map<String, String> getTaskTemplates() {
        Map<String, String> templates = new HashMap<>();
        
        templates.put("Work Meeting", "event Team meeting /at monday 2pm #work @office");
        templates.put("Project Deadline", "deadline Complete project /by next friday !high #work");
        templates.put("Personal Todo", "todo Buy groceries !normal #personal @home");
        templates.put("Health Appointment", "event Doctor visit /at next week 10am #health @clinic");
        templates.put("Urgent Task", "todo Important task !urgent #work");
        templates.put("Shopping List", "todo Buy items !low #shopping @store");
        
        return templates;
    }
} 