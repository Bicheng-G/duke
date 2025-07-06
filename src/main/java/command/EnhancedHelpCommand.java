package command;

import exception.DukeException;
import storage.Storage;
import tasklist.TaskList;
import ui.SmartSuggestionSystem;
import ui.Ui;

import java.io.IOException;

/**
 * Enhanced help command that provides contextual help and suggestions
 */
public class EnhancedHelpCommand extends Command {
    
    private final String helpTopic;
    private final SmartSuggestionSystem suggestionSystem;
    
    public EnhancedHelpCommand(String helpTopic, TaskList taskList) {
        this.helpTopic = helpTopic;
        this.suggestionSystem = new SmartSuggestionSystem(taskList);
    }
    
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws DukeException, IOException {
        if (helpTopic == null || helpTopic.trim().isEmpty()) {
            // Show general help
            System.out.println(suggestionSystem.getContextHelp("general"));
            ui.Separator();
            System.out.println(suggestionSystem.getSmartTemplates());
        } else {
            // Show specific help for a topic
            String topic = helpTopic.trim().toLowerCase();
            
            switch (topic) {
                case "templates":
                    System.out.println(suggestionSystem.getSmartTemplates());
                    break;
                case "examples":
                    showExamples();
                    break;
                case "shortcuts":
                    showShortcuts();
                    break;
                case "tips":
                    showTips();
                    break;
                default:
                    System.out.println(suggestionSystem.getContextHelp(topic));
            }
        }
    }
    
    /**
     * Show practical examples
     */
    private void showExamples() {
        System.out.println("\n=== PRACTICAL EXAMPLES ===");
        System.out.println("📚 Practical Examples:");
        System.out.println();
        System.out.println("🎯 Personal Task Management:");
        System.out.println("  todo Buy groceries !high #personal @home");
        System.out.println("  deadline Pay rent /by end of month !urgent #personal");
        System.out.println("  event Dentist appointment /at next week 2pm #health @clinic");
        System.out.println();
        System.out.println("💼 Work Project Management:");
        System.out.println("  todo Setup development environment !high #project-x @computer");
        System.out.println("  deadline Submit quarterly report /by friday 5pm !urgent #work");
        System.out.println("  event Sprint planning meeting /at monday 9am #work @conference-room");
        System.out.println();
        System.out.println("🎓 Study & Learning:");
        System.out.println("  todo Read chapter 5 !normal #study @library");
        System.out.println("  deadline Assignment due /by tomorrow 11:59pm !critical #study");
        System.out.println("  event Study group /at thursday 7pm #study @campus");
        System.out.println();
        System.out.println("🏠 Home & Family:");
        System.out.println("  todo Call mom !normal #family @phone");
        System.out.println("  deadline Book vacation /by next month !high #family");
        System.out.println("  event Family dinner /at sunday 6pm #family @home");
        System.out.println();
        System.out.println("💡 Pro tip: Mix and match priorities, categories, and contexts!");
    }
    
    /**
     * Show shortcuts and power user features
     */
    private void showShortcuts() {
        System.out.println("\n=== SHORTCUTS & POWER FEATURES ===");
        System.out.println("⚡ Shortcuts & Power Features:");
        System.out.println();
        System.out.println("🔤 Command Shortcuts:");
        System.out.println("  • Start typing any command for auto-complete");
        System.out.println("  • Use partial commands: 'l' → 'list', 't' → 'todo'");
        System.out.println("  • Tab completion for dates and categories");
        System.out.println();
        System.out.println("🎯 Priority Shortcuts:");
        System.out.println("  • !l = !low, !n = !normal, !h = !high");
        System.out.println("  • !u = !urgent, !c = !critical");
        System.out.println("  • Auto-priority based on keywords (urgent, asap, etc.)");
        System.out.println();
        System.out.println("📅 Date Shortcuts:");
        System.out.println("  • tom = tomorrow, fri = next friday");
        System.out.println("  • eom = end of month, eow = end of week");
        System.out.println("  • 9am, 2pm, 5:30pm for quick times");
        System.out.println();
        System.out.println("🔍 Search Shortcuts:");
        System.out.println("  • s #work = search #work");
        System.out.println("  • s @home = search @home");
        System.out.println("  • s urgent = search urgent");
        System.out.println();
        System.out.println("✏️ Edit Shortcuts:");
        System.out.println("  • e 1 d = edit 1 description");
        System.out.println("  • e 2 date = edit 2 date");
        System.out.println("  • e 3 !urgent = edit 3 priority urgent");
        System.out.println();
        System.out.println("💡 Most commands support partial matching!");
    }
    
    /**
     * Show productivity tips
     */
    private void showTips() {
        System.out.println("\n=== PRODUCTIVITY TIPS ===");
        System.out.println("💡 Productivity Tips:");
        System.out.println();
        System.out.println("🎯 Priority Management:");
        System.out.println("  • Use !critical sparingly - only for true emergencies");
        System.out.println("  • Review !urgent tasks daily");
        System.out.println("  • Batch !low priority tasks weekly");
        System.out.println();
        System.out.println("📂 Organization Best Practices:");
        System.out.println("  • Create project-specific tags: #project-alpha");
        System.out.println("  • Use contexts for location: @home, @office, @phone");
        System.out.println("  • Combine for powerful filtering: search #work @office");
        System.out.println();
        System.out.println("⏰ Time Management:");
        System.out.println("  • Set realistic deadlines with buffer time");
        System.out.println("  • Use events for scheduled activities");
        System.out.println("  • Review overdue tasks with 'search overdue'");
        System.out.println();
        System.out.println("🔍 Search Like a Pro:");
        System.out.println("  • Search by priority: 'search urgent'");
        System.out.println("  • Find incomplete tasks: 'search incomplete'");
        System.out.println("  • Review completed: 'search complete'");
        System.out.println();
        System.out.println("📋 Task Lifecycle:");
        System.out.println("  1. Create with proper priority and category");
        System.out.println("  2. Edit as requirements change");
        System.out.println("  3. Mark done when complete");
        System.out.println("  4. Archive or delete when no longer needed");
        System.out.println();
        System.out.println("🚀 Workflow Ideas:");
        System.out.println("  • Weekly review: 'list' to see all tasks");
        System.out.println("  • Daily standup: 'search @office' for work tasks");
        System.out.println("  • End of day: 'search #personal' for home tasks");
    }
    
    @Override
    public boolean isExit() {
        return false;
    }
} 