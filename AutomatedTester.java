import ui.WhatsAppStyleChatUI;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Automated testing system for natural language processing
 * Generates comprehensive test cases to identify and fix logic flaws
 */
public class AutomatedTester {
    
    private static final Random random = new Random();
    private static WhatsAppStyleChatUI chatUI;
    private static Method preprocessMethod;
    
    // Test statistics
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static List<String> failureReasons = new ArrayList<>();
    private static Set<String> uniqueFailures = new HashSet<>();
    
    public static void main(String[] args) {
        System.out.println("🤖 AUTOMATED NATURAL LANGUAGE PROCESSING TESTER");
        System.out.println("==============================================");
        
        try {
            // Initialize testing environment
            chatUI = new WhatsAppStyleChatUI();
            preprocessMethod = WhatsAppStyleChatUI.class.getDeclaredMethod("preprocessInput", String.class);
            preprocessMethod.setAccessible(true);
            
            // Run 3 iterations of 500 tests each
            for (int iteration = 1; iteration <= 3; iteration++) {
                System.out.printf("\n🔄 ITERATION %d - Testing 500 commands\n", iteration);
                System.out.println("=====================================");
                
                resetStats();
                List<String> testCommands = generateTestCommands(500);
                runTests(testCommands);
                analyzeResults(iteration);
                
                // After each iteration, we should fix identified issues
                if (iteration < 3) {
                    System.out.println("\n⚠️  ISSUES IDENTIFIED - REVIEW AND FIX LOGIC");
                    System.out.println("Continuing to next iteration...");
                    System.out.println("========================================");
                }
            }
            
            System.out.println("\n🎯 FINAL SUMMARY");
            System.out.println("===============");
            System.out.printf("Total tests across all iterations: %d\n", totalTests);
            System.out.printf("Overall success rate: %.2f%%\n", (double)passedTests / totalTests * 100);
            
        } catch (Exception e) {
            System.err.println("Testing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void resetStats() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        failureReasons.clear();
        uniqueFailures.clear();
    }
    
    private static List<String> generateTestCommands(int count) {
        List<String> commands = new ArrayList<>();
        
        // Generate diverse test categories
        commands.addAll(generateTodoCommands(count * 35 / 100));    // 35%
        commands.addAll(generateEventCommands(count * 25 / 100));   // 25%
        commands.addAll(generateDeadlineCommands(count * 25 / 100)); // 25%
        commands.addAll(generateDirectCommands(count * 10 / 100));   // 10%
        commands.addAll(generateEdgeCases(count * 5 / 100));        // 5%
        
        // Shuffle for realistic testing
        Collections.shuffle(commands);
        
        // Ensure exactly the requested count
        while (commands.size() < count) {
            commands.add(generateRandomCommand());
        }
        
        return commands.subList(0, count);
    }
    
    private static List<String> generateTodoCommands(int count) {
        List<String> commands = new ArrayList<>();
        
        String[] todoVerbs = {"add", "create", "new", "todo", "make", "do", "remember to", "need to", "should", "must"};
        String[] todoTasks = {
            "buy milk", "call mom", "walk the dog", "pay bills", "clean house", "write report",
            "fix bike", "water plants", "buy groceries", "send email", "book appointment",
            "learn java", "practice piano", "read book", "exercise", "meditate", "cook dinner",
            "wash car", "organize closet", "backup files", "update resume", "plan vacation",
            "tell baby a joke", "sing a song", "dance", "paint picture", "write story",
            "build something", "repair something", "buy something nice", "visit grandma",
            "help neighbor", "volunteer", "donate clothes", "recycle", "plant flowers"
        };
        
        String[] priorities = {"", " !low", " !normal", " !high", " !urgent", " !critical"};
        String[] categories = {"", " #work", " #personal", " #health", " #shopping", " #family"};
        String[] contexts = {"", " @home", " @office", " @phone", " @computer", " @store"};
        
        for (int i = 0; i < count; i++) {
            String verb = todoVerbs[random.nextInt(todoVerbs.length)];
            String task = todoTasks[random.nextInt(todoTasks.length)];
            String priority = priorities[random.nextInt(priorities.length)];
            String category = categories[random.nextInt(categories.length)];
            String context = contexts[random.nextInt(contexts.length)];
            
            commands.add(verb + " " + task + priority + category + context);
        }
        
        return commands;
    }
    
    private static List<String> generateEventCommands(int count) {
        List<String> commands = new ArrayList<>();
        
        String[] eventVerbs = {"", "add", "create", "new", "schedule", "plan", "event", "meeting"};
        String[] eventTypes = {
            "meeting", "appointment", "call", "conference", "interview", "lunch", "dinner",
            "session", "class", "lecture", "presentation", "workshop", "seminar", "training",
            "celebration", "party", "birthday", "anniversary", "wedding", "funeral",
            "doctor visit", "dentist appointment", "haircut", "massage", "therapy",
            "gym session", "yoga class", "dance lesson", "music lesson", "art class"
        };
        
        String[] times = {
            "tomorrow at 2pm", "today at 9am", "monday at 3pm", "friday at 5pm",
            "next week at 10am", "next monday at 2pm", "this afternoon at 3pm",
            "tomorrow morning at 9am", "next friday at 6pm", "saturday at 11am",
            "sunday at 1pm", "next tuesday at 4pm", "wednesday at 8am",
            "at 2:30pm", "at 9:15am", "at 11:45am", "at 3:00pm", "at 7:30pm",
            "on monday 2pm", "on friday 3pm", "on saturday 10am", "on sunday 11am"
        };
        
        String[] contexts = {
            "", " with client", " with team", " with doctor", " with family", " with friends",
            " at office", " at home", " at restaurant", " at clinic", " at gym", " at school"
        };
        
        for (int i = 0; i < count; i++) {
            String verb = eventVerbs[random.nextInt(eventVerbs.length)];
            String event = eventTypes[random.nextInt(eventTypes.length)];
            String time = times[random.nextInt(times.length)];
            String context = contexts[random.nextInt(contexts.length)];
            
            String command = (verb.isEmpty() ? "" : verb + " ") + event + " " + time + context;
            commands.add(command.trim());
        }
        
        return commands;
    }
    
    private static List<String> generateDeadlineCommands(int count) {
        List<String> commands = new ArrayList<>();
        
        String[] deadlineVerbs = {"", "add", "create", "new", "deadline", "due", "submit", "finish", "complete"};
        String[] deadlineTasks = {
            "project", "report", "assignment", "homework", "presentation", "proposal",
            "application", "form", "essay", "paper", "thesis", "research", "analysis",
            "budget", "plan", "design", "prototype", "code", "website", "app",
            "tax return", "insurance claim", "job application", "college application",
            "loan application", "visa application", "permit application", "registration"
        };
        
        String[] deadlineTimes = {
            "by tomorrow", "by friday", "by next week", "by monday", "by end of month",
            "by tomorrow 5pm", "by friday 3pm", "by next monday", "by next friday",
            "due tomorrow", "due friday", "due next week", "due monday", "due end of month",
            "before friday", "before next week", "before monday", "before end of month",
            "until friday", "until next week", "until monday", "until end of month"
        };
        
        String[] priorities = {"", " !low", " !normal", " !high", " !urgent", " !critical"};
        String[] categories = {"", " #work", " #school", " #personal", " #finance", " #legal"};
        
        for (int i = 0; i < count; i++) {
            String verb = deadlineVerbs[random.nextInt(deadlineVerbs.length)];
            String task = deadlineTasks[random.nextInt(deadlineTasks.length)];
            String time = deadlineTimes[random.nextInt(deadlineTimes.length)];
            String priority = priorities[random.nextInt(priorities.length)];
            String category = categories[random.nextInt(categories.length)];
            
            String command = (verb.isEmpty() ? "" : verb + " ") + task + " " + time + priority + category;
            commands.add(command.trim());
        }
        
        return commands;
    }
    
    private static List<String> generateDirectCommands(int count) {
        List<String> commands = new ArrayList<>();
        
        String[] directCommands = {
            "list", "help", "bye", "reset",
            "done 1", "done 2", "done 3", "done 4", "done 5",
            "delete 1", "delete 2", "delete 3", "delete 4", "delete 5",
            "search work", "search personal", "search #work", "search @home",
            "search meeting", "search project", "search urgent", "search today",
            "view today", "view tomorrow", "view monday", "view friday",
            "edit 1 description new task", "edit 2 date tomorrow", "edit 3 priority !high"
        };
        
        for (int i = 0; i < count; i++) {
            commands.add(directCommands[random.nextInt(directCommands.length)]);
        }
        
        return commands;
    }
    
    private static List<String> generateEdgeCases(int count) {
        List<String> commands = new ArrayList<>();
        
        String[] edgeCases = {
            "", "   ", "a", "by", "at", "the", "and", "or", "but", "if", "when", "where",
            "add", "create", "new", "todo", "event", "deadline", "meeting", "appointment",
            "add add add", "todo todo todo", "event event event", "deadline deadline deadline",
            "meeting meeting meeting", "appointment appointment appointment",
            "buy milk by baby", "call mom by morning", "tell baby a joke by noon",
            "event meeting by tomorrow", "deadline project at 2pm", "todo task /at /by",
            "very long task description that goes on and on and on and never seems to end",
            "task with many words and lots of details and specifications and requirements",
            "123", "!@#", "???", "...", "---", "___", "***", "///", "\\\\\\",
            "hello", "hi", "hey", "good morning", "good afternoon", "good evening",
            "thanks", "thank you", "please", "sorry", "excuse me", "pardon me",
            "what", "when", "where", "who", "why", "how", "which", "what if",
            "maybe", "perhaps", "possibly", "probably", "definitely", "certainly",
            "urgent meeting tomorrow", "important deadline friday", "critical task monday",
            "high priority project", "low priority task", "normal priority work"
        };
        
        for (int i = 0; i < count; i++) {
            commands.add(edgeCases[random.nextInt(edgeCases.length)]);
        }
        
        return commands;
    }
    
    private static String generateRandomCommand() {
        String[] randomCommands = {
            "buy something", "do something", "meeting somewhere", "deadline something",
            "task", "work", "project", "appointment", "call", "email", "write", "read"
        };
        return randomCommands[random.nextInt(randomCommands.length)];
    }
    
    private static void runTests(List<String> commands) {
        System.out.printf("Running %d tests...\n", commands.size());
        
        int testCount = 0;
        for (String command : commands) {
            testCount++;
            if (testCount % 100 == 0) {
                System.out.printf("Progress: %d/%d tests completed\n", testCount, commands.size());
            }
            
            try {
                String result = (String) preprocessMethod.invoke(chatUI, command);
                TestResult testResult = analyzeTestResult(command, result);
                
                totalTests++;
                if (testResult.passed) {
                    passedTests++;
                } else {
                    failedTests++;
                    failureReasons.add(testResult.reason);
                    uniqueFailures.add(testResult.category);
                }
                
            } catch (Exception e) {
                totalTests++;
                failedTests++;
                String error = "EXCEPTION: " + e.getMessage();
                failureReasons.add(error);
                uniqueFailures.add("EXCEPTION");
            }
        }
    }
    
    private static TestResult analyzeTestResult(String input, String output) {
        TestResult result = new TestResult();
        result.input = input;
        result.output = output;
        
        // Check for null or empty output
        if (output == null || output.trim().isEmpty()) {
            result.passed = false;
            result.reason = "Empty output for input: " + input;
            result.category = "EMPTY_OUTPUT";
            return result;
        }
        
        // Check for valid command format
        if (!isValidCommandFormat(output)) {
            result.passed = false;
            result.reason = "Invalid command format: " + output + " (from: " + input + ")";
            result.category = "INVALID_FORMAT";
            return result;
        }
        
        // Check for logical consistency
        if (!isLogicallyConsistent(input, output)) {
            result.passed = false;
            result.reason = "Logical inconsistency: " + input + " -> " + output;
            result.category = "LOGICAL_ERROR";
            return result;
        }
        
        result.passed = true;
        return result;
    }
    
    private static boolean isValidCommandFormat(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = command.trim().split(" ", 2);
        String cmdType = parts[0].toLowerCase();
        
        switch (cmdType) {
            case "event":
                return parts.length > 1 && parts[1].contains("/at") && 
                       !parts[1].startsWith("/at") && !parts[1].endsWith("/at");
            case "deadline":
                return parts.length > 1 && parts[1].contains("/by") && 
                       !parts[1].startsWith("/by") && !parts[1].endsWith("/by");
            case "todo":
                return parts.length > 1 && !parts[1].trim().isEmpty();
            case "list":
            case "help":
            case "reset":
            case "bye":
                return true;
            case "done":
            case "delete":
            case "view":
                return parts.length > 1 && !parts[1].trim().isEmpty();
            case "search":
            case "edit":
                return parts.length > 1 && !parts[1].trim().isEmpty();
            default:
                return false;
        }
    }
    
    private static boolean isLogicallyConsistent(String input, String output) {
        String lowerInput = input.toLowerCase().trim();
        String lowerOutput = output.toLowerCase().trim();
        
        // Check if event-like input produces event output
        if (containsEventKeywords(lowerInput) && !lowerOutput.startsWith("event")) {
            return false;
        }
        
        // Check if deadline-like input produces deadline output  
        if (containsDeadlineKeywords(lowerInput) && !lowerOutput.startsWith("deadline")) {
            return false;
        }
        
        // Check if direct commands are preserved
        if (isDirectCommand(lowerInput) && !lowerOutput.equals(lowerInput)) {
            return false;
        }
        
        return true;
    }
    
    private static boolean containsEventKeywords(String input) {
        String[] eventKeywords = {
            "meeting", "appointment", "event", "call", "conference", "interview", 
            "lunch", "dinner", "visit", "session", "class", "lecture"
        };
        for (String keyword : eventKeywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsDeadlineKeywords(String input) {
        return input.matches(".*\\bdeadline\\b.*") || 
               input.matches(".*\\bdue\\b.*") || 
               input.matches(".*\\bby\\b.*") || 
               input.matches(".*\\bsubmit\\b.*") || 
               input.matches(".*\\bfinish\\b.*");
    }
    
    private static boolean isDirectCommand(String input) {
        return input.startsWith("list") || input.startsWith("help") || input.startsWith("search") || 
               input.startsWith("done") || input.startsWith("delete") || input.startsWith("edit") ||
               input.startsWith("view") || input.startsWith("reset") || input.startsWith("bye");
    }
    
    private static void analyzeResults(int iteration) {
        System.out.printf("\n📊 ITERATION %d RESULTS:\n", iteration);
        System.out.printf("Total tests: %d\n", totalTests);
        System.out.printf("Passed: %d (%.2f%%)\n", passedTests, (double)passedTests / totalTests * 100);
        System.out.printf("Failed: %d (%.2f%%)\n", failedTests, (double)failedTests / totalTests * 100);
        
        if (failedTests > 0) {
            System.out.println("\n❌ FAILURE CATEGORIES:");
            Map<String, Integer> failureCounts = new HashMap<>();
            for (String failure : uniqueFailures) {
                failureCounts.put(failure, failureCounts.getOrDefault(failure, 0) + 1);
            }
            
            failureCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("  %s: %d occurrences\n", entry.getKey(), entry.getValue()));
            
            System.out.println("\n🔍 SAMPLE FAILURES:");
            failureReasons.stream()
                .distinct()
                .limit(20)
                .forEach(reason -> System.out.println("  - " + reason));
        }
    }
    
    private static class TestResult {
        String input;
        String output;
        boolean passed;
        String reason;
        String category;
    }
} 