package tasklist;

/**
 * Priority levels for tasks with visual indicators and smart ordering
 */
public enum Priority {
    LOW("!low", "🟢", "Low", 1),
    NORMAL("!normal", "🔵", "Normal", 2),
    HIGH("!high", "🟡", "High", 3),
    URGENT("!urgent", "🔴", "Urgent", 4),
    CRITICAL("!critical", "🚨", "Critical", 5);
    
    private final String keyword;
    private final String icon;
    private final String displayName;
    private final int level;
    
    Priority(String keyword, String icon, String displayName, int level) {
        this.keyword = keyword;
        this.icon = icon;
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public String getIcon() {
        return icon;
    }
    
    /**
     * Get emoji for priority (alias for getIcon for test compatibility)
     * @return emoji string
     */
    public String getEmoji() {
        return icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getLevel() {
        return level;
    }
    
    /**
     * Parse priority from string input (for test compatibility)
     * @param input string containing priority keyword
     * @return Priority enum or NORMAL if not found
     */
    public static Priority fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return NORMAL;
        }
        
        String normalized = input.trim().toLowerCase();
        
        // Handle full keywords with or without !
        for (Priority priority : Priority.values()) {
            String keyword = priority.keyword.toLowerCase();
            String keywordNoExclamation = keyword.substring(1); // Remove !
            
            if (normalized.equals(keyword) || 
                normalized.equals(keywordNoExclamation) ||
                normalized.equals(keyword.substring(1, 2))) { // Single letter shortcuts
                return priority;
            }
        }
        
        return NORMAL;
    }
    
    /**
     * Parse priority from text input
     * @param text input text containing priority keyword
     * @return Priority enum or NORMAL if not found
     */
    public static Priority fromText(String text) {
        if (text == null) return NORMAL;
        
        String lowerText = text.toLowerCase();
        for (Priority priority : Priority.values()) {
            if (lowerText.contains(priority.keyword)) {
                return priority;
            }
        }
        return NORMAL;
    }
    
    /**
     * Remove priority keyword from text
     * @param text input text
     * @return text with priority keyword removed
     */
    public static String removePriorityFromText(String text) {
        if (text == null) return text;
        
        String result = text;
        for (Priority priority : Priority.values()) {
            result = result.replaceAll("\\s*" + priority.keyword + "\\s*", " ");
        }
        return result.trim();
    }
    
    /**
     * Get all priority keywords for help text
     * @return string containing all priority options
     */
    public static String getPriorityHelp() {
        StringBuilder help = new StringBuilder("Priority levels:\n");
        for (Priority priority : Priority.values()) {
            help.append("  ").append(priority.icon).append(" ")
                .append(priority.keyword).append(" - ").append(priority.displayName).append("\n");
        }
        return help.toString();
    }
    
    /**
     * Check if text contains any priority keyword
     * @param text input text
     * @return true if priority keyword found
     */
    public static boolean containsPriority(String text) {
        if (text == null) return false;
        
        String lowerText = text.toLowerCase();
        for (Priority priority : Priority.values()) {
            if (lowerText.contains(priority.keyword)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return icon + " " + displayName;
    }
} 