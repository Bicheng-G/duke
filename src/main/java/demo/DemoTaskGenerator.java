package demo;

import tasklist.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates demo tasks to showcase Duke's features when users first run the application
 */
public class DemoTaskGenerator {
    
    /**
     * Generate a comprehensive set of demo tasks showcasing all features
     * @return List of demo tasks with priorities, categories, and varied types
     */
    public static List<Task> generateDemoTasks() {
        List<Task> demoTasks = new ArrayList<>();
        
        // Personal tasks demonstrating different priorities
        demoTasks.add(new Todo("todo Buy groceries for the week !high #personal @home"));
        demoTasks.add(new Todo("todo Call dentist for appointment !normal #health @phone"));
        demoTasks.add(new Todo("todo Plan weekend activities !low #personal @planning"));
        demoTasks.add(new Todo("todo Exercise for 30 minutes !normal #health @gym"));
        
        // Work tasks with deadlines
        LocalDateTime tomorrow6pm = LocalDateTime.now().plusDays(1).withHour(18).withMinute(0);
        demoTasks.add(new Deadline("deadline Submit quarterly report /by tomorrow 6pm !urgent #work @office", tomorrow6pm));
        
        LocalDateTime friday5pm = LocalDateTime.now().plusDays(getNextFriday()).withHour(17).withMinute(0);
        demoTasks.add(new Deadline("deadline Complete code review /by friday 5pm !high #development @computer", friday5pm));
        
        LocalDateTime nextWeek = LocalDateTime.now().plusDays(7).withHour(9).withMinute(0);
        demoTasks.add(new Deadline("deadline Update project documentation /by next week !normal #work @computer", nextWeek));
        
        // Events demonstrating scheduling
        LocalDateTime mondayMeeting = LocalDateTime.now().plusDays(getNextMonday()).withHour(14).withMinute(0);
        demoTasks.add(new Event("event Weekly team standup /at monday 2pm #work @conference-room", mondayMeeting));
        
        LocalDateTime clientMeeting = LocalDateTime.now().plusDays(3).withHour(10).withMinute(30);
        demoTasks.add(new Event("event Client presentation /at upcoming 10:30am !critical #work @client-abc", clientMeeting));
        
        LocalDateTime lunch = LocalDateTime.now().plusDays(2).withHour(12).withMinute(30);
        demoTasks.add(new Event("event Lunch with Sarah /at day-after-tomorrow 12:30pm #personal @restaurant", lunch));
        
        // Project management tasks
        demoTasks.add(new Todo("todo Setup CI/CD pipeline !high #project-x @development"));
        demoTasks.add(new Todo("todo Write unit tests !normal #project-x @computer"));
        
        LocalDateTime sprintReview = LocalDateTime.now().plusDays(5).withHour(15).withMinute(0);
        demoTasks.add(new Event("event Sprint review meeting /at upcoming 3pm #project-x @conference-room", sprintReview));
        
        // Mark some tasks as completed to show mixed status
        demoTasks.get(1).setDone(true); // Call dentist
        demoTasks.get(7).setDone(true); // Team standup (if it was past)
        demoTasks.get(12).setDone(true); // Write unit tests
        
        return demoTasks;
    }
    
    /**
     * Get days until next Friday
     */
    private static int getNextFriday() {
        int today = LocalDateTime.now().getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        int friday = 5; // Friday
        return friday >= today ? friday - today : 7 - today + friday;
    }
    
    /**
     * Get days until next Monday
     */
    private static int getNextMonday() {
        int today = LocalDateTime.now().getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        int monday = 1; // Monday
        return monday >= today ? monday - today : 7 - today + monday;
    }
    
    /**
     * Get welcome message for demo mode
     */
    public static String getWelcomeMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎯 Welcome to Duke Task Manager!\n");
        sb.append("\n");
        sb.append("🎉 New features loaded with demo tasks:\n");
        sb.append("\n");
        sb.append("✨ Priorities: !low, !normal, !high, !urgent, !critical\n");
        sb.append("📂 Categories: #work, #personal, #health\n");
        sb.append("📍 Contexts: @home, @office, @phone\n");
        sb.append("\n");
        sb.append("🚀 Try these commands:\n");
        sb.append("• list - View all tasks with priorities & categories\n");
        sb.append("• search #work - Find work-related tasks\n");
        sb.append("• search @home - Find home context tasks\n");
        sb.append("• edit 1 description New task !urgent #work\n");
        sb.append("• help - See all available commands\n");
        sb.append("\n");
        sb.append("💡 Create new tasks:\n");
        sb.append("• todo Buy coffee !high #personal @cafe\n");
        sb.append("• deadline Report /by tomorrow 5pm !urgent #work\n");
        sb.append("• event Meeting /at monday 2pm #work @office\n");
        sb.append("\n");
        sb.append("📊 Your demo includes tasks at different priority levels\n");
        sb.append("and categories to showcase Duke's powerful organization!");
        return sb.toString();
    }
    
    /**
     * Get quick start guide
     */
    public static String getQuickStartGuide() {
        StringBuilder sb = new StringBuilder();
        sb.append("📚 Quick Start Guide:\n");
        sb.append("\n");
        sb.append("🎯 Priority Levels:\n");
        sb.append("  🟢 !low     🔵 !normal   🟡 !high\n");
        sb.append("  🔴 !urgent  🚨 !critical\n");
        sb.append("\n");
        sb.append("📂 Organization:\n");
        sb.append("  #tag - Categories (work, personal, health)\n");
        sb.append("  @context - Locations (home, office, phone)\n");
        sb.append("\n");
        sb.append("✏️ Commands to try:\n");
        sb.append("  list, search, edit, done, delete\n");
        sb.append("\n");
        sb.append("💡 Natural language dates work too!\n");
        sb.append("  \"tomorrow 3pm\", \"next friday\", \"monday morning\"\n");
        return sb.toString();
    }
} 