package ui;

import tasklist.TaskList;
import java.util.*;

/**
 * Smart suggestion system that provides contextual help and auto-complete
 */
public class SmartSuggestionSystem {
    
    private final AutoCompleteEngine autoComplete;
    private boolean suggestionsEnabled = true;
    private int maxSuggestions = 5;
    
    public SmartSuggestionSystem(TaskList taskList) {
        this.autoComplete = new AutoCompleteEngine(taskList);
    }
    
    /**
     * Process user input and provide suggestions
     * @param input partial user input
     * @return formatted suggestions for display
     */
    public String processInput(String input) {
        if (!suggestionsEnabled || input == null) {
            return "";
        }
        
        // Get suggestions from auto-complete engine
        List<String> suggestions = autoComplete.getSuggestions(input);
        
        if (suggestions.isEmpty()) {
            return getHelpHint(input);
        }
        
        return formatSuggestions(suggestions, input);
    }
    
    /**
     * Format suggestions for display
     */
    private String formatSuggestions(List<String> suggestions, String input) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("💡 Suggestions:\n");
        
        int count = Math.min(suggestions.size(), maxSuggestions);
        for (int i = 0; i < count; i++) {
            String suggestion = suggestions.get(i);
            sb.append("  ").append(i + 1).append(". ").append(suggestion).append("\n");
        }
        
        if (suggestions.size() > maxSuggestions) {
            sb.append("  ... and ").append(suggestions.size() - maxSuggestions).append(" more\n");
        }
        
        sb.append("\n💡 Tip: Use Tab to complete or continue typing");
        
        return sb.toString();
    }
    
    /**
     * Get contextual help hints for invalid or incomplete input
     */
    private String getHelpHint(String input) {
        if (input.trim().isEmpty()) {
            return getGettingStartedHint();
        }
        
        String lowerInput = input.trim().toLowerCase();
        
        // Check for common typos or partial commands
        if (lowerInput.startsWith("l")) {
            return "💡 Did you mean 'list'? Try: list";
        } else if (lowerInput.startsWith("t")) {
            return "💡 Did you mean 'todo'? Try: todo Buy groceries !high #personal @home";
        } else if (lowerInput.startsWith("d")) {
            return "💡 Did you mean 'deadline' or 'done'?\n" +
                   "  Try: deadline Submit report /by tomorrow 5pm !urgent\n" +
                   "  Or: done 1";
        } else if (lowerInput.startsWith("e")) {
            return "💡 Did you mean 'event' or 'edit'?\n" +
                   "  Try: event Meeting /at monday 2pm #work\n" +
                   "  Or: edit 1 description New task";
        } else if (lowerInput.startsWith("s")) {
            return "💡 Did you mean 'search'? Try: search #work";
        } else if (lowerInput.startsWith("h")) {
            return "💡 Did you mean 'help'? Try: help";
        }
        
        return getCommandNotFoundHint();
    }
    
        /**
     * Get getting started hint for new users
     */
    private String getGettingStartedHint() {
        return "Getting Started: Try 'todo Buy groceries', 'list', or 'help' for assistance.";
    }

    /**
     * Get hint for unrecognized commands
     */
    private String getCommandNotFoundHint() {
        return "Command not recognized. Available commands: todo, deadline, event, list, done, delete, edit, search, help, bye";
    }
    
    /**
     * Get smart templates based on current context
     */
    public String getSmartTemplates() {
        Map<String, String> templates = autoComplete.getTaskTemplates();
        StringBuilder sb = new StringBuilder();
        
        sb.append("📋 Quick Templates:\n\n");
        
        int i = 1;
        for (Map.Entry<String, String> template : templates.entrySet()) {
            sb.append("  ").append(i++).append(". ").append(template.getKey()).append("\n");
            sb.append("     ").append(template.getValue()).append("\n\n");
        }
        
        sb.append("💡 Copy and modify any template above!");
        
        return sb.toString();
    }
    
    /**
     * Get context-sensitive help based on what user is trying to do
     */
    public String getContextHelp(String command) {
        switch (command.toLowerCase()) {
            case "todo":
                return "Todo format: todo [description] [!priority] [#category] [@context]";
            case "deadline":
                return "Deadline format: deadline [description] /by [date/time] [!priority] [#category] [@context]";
            case "event":
                return "Event format: event [description] /at [date/time] [!priority] [#category] [@context]";
            case "search":
                return "Search format: search [keyword] or search [#category] or search [@context]";
            case "edit":
                return "Edit format: edit [task_number] [field] [new_value]";
            default:
                return "Type 'help' for general help or 'help [command]' for specific help.";
        }
    }
    
    // Simplified helper methods for Java 11 compatibility
    private String getTodoHelp() {
        return "Todo format: todo [description] [!priority] [#category] [@context]";
    }

    private String getDeadlineHelp() {
        return "Deadline format: deadline [description] /by [date/time] [!priority] [#category] [@context]";
    }

    private String getEventHelp() {
        return "Event format: event [description] /at [date/time] [!priority] [#category] [@context]";
    }

    private String getEditHelp() {
        return "Edit format: edit [task_number] [field] [new_value]";
    }

    private String getSearchHelp() {
        return "Search format: search [keyword] or search [#category] or search [@context]";
    }

    private String getPriorityHelp() {
        return "Priorities: !low, !normal, !high, !urgent, !critical";
    }

    private String getCategoryHelp() {
        return "Categories: #work, #personal, #health. Contexts: @home, @office, @phone";
    }
    
    public String getGettingStartedMessage() {
        return "Welcome to Duke! Type 'help' for assistance.";
    }

    public String getGeneralHelp() {
        return "Available commands: todo, deadline, event, list, done, delete, search, help, bye";
    }
    
    /**
     * Record user command for learning
     */
    public void recordCommand(String command) {
        autoComplete.addToHistory(command);
    }
    
    /**
     * Toggle suggestions on/off
     */
    public void toggleSuggestions() {
        suggestionsEnabled = !suggestionsEnabled;
    }
    
    /**
     * Check if suggestions are enabled
     */
    public boolean isSuggestionsEnabled() {
        return suggestionsEnabled;
    }
    
    /**
     * Set maximum number of suggestions to show
     */
    public void setMaxSuggestions(int max) {
        this.maxSuggestions = Math.max(1, Math.min(max, 10));
    }
} 