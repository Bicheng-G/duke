package ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import tasklist.*;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for AutoCompleteEngine
 */
public class AutoCompleteEngineTest {
    
    private AutoCompleteEngine autoComplete;
    private TaskList taskList;
    
    @BeforeEach
    void setUp() {
        // Create test task list
        taskList = new TaskList();
        taskList.addTask(new Todo("todo Buy groceries !high #personal @home"));
        taskList.addTask(new Deadline("deadline Submit report /by friday 5pm !urgent #work @office", null));
        taskList.addTask(new Event("event Team meeting /at monday 2pm #work @conference-room", null));
        taskList.addTask(new Todo("todo Review code !normal #work @computer"));
        taskList.addTask(new Todo("todo Exercise !low #health @gym"));
        
        autoComplete = new AutoCompleteEngine(taskList);
    }
    
    @Test
    @DisplayName("Test command completion")
    void testCommandCompletion() {
        // Test partial command completion
        List<String> suggestions = autoComplete.getSuggestions("t");
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
        
        // Test 'd' should suggest both 'deadline' and 'done'
        suggestions = autoComplete.getSuggestions("d");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("deadline")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("done")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("delete")));
        
        // Test 'e' should suggest 'event' and 'edit'
        suggestions = autoComplete.getSuggestions("e");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("event")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("edit")));
        
        // Test 's' should suggest 'search'
        suggestions = autoComplete.getSuggestions("s");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("search")));
        
        // Test 'l' should suggest 'list'
        suggestions = autoComplete.getSuggestions("l");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("list")));
    }
    
    @Test
    @DisplayName("Test exact command matching")
    void testExactCommandMatching() {
        // Test exact matches
        List<String> suggestions = autoComplete.getSuggestions("list");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("list")));
        
        suggestions = autoComplete.getSuggestions("help");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("help")));
        
        suggestions = autoComplete.getSuggestions("bye");
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("bye")));
    }
    
    @Test
    @DisplayName("Test todo command suggestions")
    void testTodoCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("todo");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest todo templates
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
        
        // Test partial todo command
        suggestions = autoComplete.getSuggestions("todo buy");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest priorities if not present
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!")));
    }
    
    @Test
    @DisplayName("Test deadline command suggestions")
    void testDeadlineCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("deadline");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest deadline templates
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("deadline")));
        
        // Test partial deadline command
        suggestions = autoComplete.getSuggestions("deadline report");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest adding /by if not present
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("/by")));
    }
    
    @Test
    @DisplayName("Test event command suggestions")
    void testEventCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("event");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest event templates
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("event")));
        
        // Test partial event command
        suggestions = autoComplete.getSuggestions("event meeting");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest adding /at if not present
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("/at")));
    }
    
    @Test
    @DisplayName("Test edit command suggestions")
    void testEditCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("edit");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest task numbers
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("edit 1")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("edit 2")));
        
        // Test edit with task number
        suggestions = autoComplete.getSuggestions("edit 1");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest edit operations
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("description")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("date")));
    }
    
    @Test
    @DisplayName("Test search command suggestions")
    void testSearchCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("search");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest existing categories
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("#work")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("#personal")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("#health")));
        
        // Should suggest existing contexts
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("@home")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("@office")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("@computer")));
    }
    
    @Test
    @DisplayName("Test done/delete command suggestions")
    void testDoneDeleteCommandSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("done");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest task numbers with descriptions
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("done 1")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("done 2")));
        
        suggestions = autoComplete.getSuggestions("delete");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest task numbers with descriptions
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("delete 1")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("delete 2")));
    }
    
    @Test
    @DisplayName("Test empty input suggestions")
    void testEmptyInputSuggestions() {
        List<String> suggestions = autoComplete.getSuggestions("");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest common commands
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("list")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("deadline")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("event")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("help")));
    }
    
    @Test
    @DisplayName("Test null input handling")
    void testNullInputHandling() {
        List<String> suggestions = autoComplete.getSuggestions(null);
        assertFalse(suggestions.isEmpty());
        
        // Should return common commands for null input
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("list")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
    }
    
    @Test
    @DisplayName("Test suggestion relevance ordering")
    void testSuggestionRelevanceOrdering() {
        List<String> suggestions = autoComplete.getSuggestions("to");
        assertFalse(suggestions.isEmpty());
        
        // Should prioritize exact prefix matches
        String firstSuggestion = suggestions.get(0);
        assertTrue(firstSuggestion.toLowerCase().startsWith("to"));
    }
    
    @Test
    @DisplayName("Test suggestion limit")
    void testSuggestionLimit() {
        List<String> suggestions = autoComplete.getSuggestions("e");
        
        // Should limit suggestions to reasonable number
        assertTrue(suggestions.size() <= 10);
        assertFalse(suggestions.isEmpty());
    }
    
    @Test
    @DisplayName("Test command history")
    void testCommandHistory() {
        // Add some commands to history
        autoComplete.addToHistory("todo Buy milk");
        autoComplete.addToHistory("deadline Report /by friday");
        autoComplete.addToHistory("search #work");
        
        // History should be recorded (internal state)
        // This tests that no exceptions are thrown
        assertDoesNotThrow(() -> autoComplete.addToHistory("list"));
        assertDoesNotThrow(() -> autoComplete.addToHistory("help"));
    }
    
    @Test
    @DisplayName("Test invalid command handling")
    void testInvalidCommandHandling() {
        List<String> suggestions = autoComplete.getSuggestions("invalid");
        
        // Should handle invalid commands gracefully
        assertNotNull(suggestions);
        // May be empty or contain general suggestions
    }
    
    @Test
    @DisplayName("Test case insensitive matching")
    void testCaseInsensitiveMatching() {
        List<String> suggestions = autoComplete.getSuggestions("TODO");
        assertFalse(suggestions.isEmpty());
        
        // Should handle uppercase input
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("todo")));
        
        suggestions = autoComplete.getSuggestions("List");
        assertFalse(suggestions.isEmpty());
        
        // Should handle mixed case
        assertTrue(suggestions.stream().anyMatch(s -> s.toLowerCase().contains("list")));
    }
    
    @Test
    @DisplayName("Test date suggestion integration")
    void testDateSuggestionIntegration() {
        List<String> suggestions = autoComplete.getSuggestions("deadline task /by");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest dates after /by
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("tomorrow")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("friday")));
        
        suggestions = autoComplete.getSuggestions("event meeting /at");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest dates after /at
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("monday")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("today")));
    }
    
    @Test
    @DisplayName("Test priority suggestion integration")
    void testPrioritySuggestionIntegration() {
        List<String> suggestions = autoComplete.getSuggestions("todo task");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest priorities if not present
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!low")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!normal")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!high")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!urgent")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("!critical")));
    }
    
    @Test
    @DisplayName("Test category suggestion integration")
    void testCategorySuggestionIntegration() {
        List<String> suggestions = autoComplete.getSuggestions("todo task");
        assertFalse(suggestions.isEmpty());
        
        // Should suggest categories based on existing tasks
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("#work") || s.contains("#personal") || s.contains("#health")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("@home") || s.contains("@office") || s.contains("@computer")));
    }
    
    @Test
    @DisplayName("Test smart date suggestions")
    void testSmartDateSuggestions() {
        List<String> suggestions = autoComplete.getSmartDateSuggestions();
        assertFalse(suggestions.isEmpty());
        
        // Should contain common date suggestions
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("today")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("tomorrow")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("monday")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("friday")));
    }
    
    @Test
    @DisplayName("Test task templates")
    void testTaskTemplates() {
        Map<String, String> templates = autoComplete.getTaskTemplates();
        assertFalse(templates.isEmpty());
        
        // Should contain various template types
        assertTrue(templates.containsKey("Work Meeting"));
        assertTrue(templates.containsKey("Project Deadline"));
        assertTrue(templates.containsKey("Personal Todo"));
        assertTrue(templates.containsKey("Health Appointment"));
        
        // Templates should be properly formatted
        assertTrue(templates.get("Work Meeting").contains("event"));
        assertTrue(templates.get("Project Deadline").contains("deadline"));
        assertTrue(templates.get("Personal Todo").contains("todo"));
    }
    
    @Test
    @DisplayName("Test whitespace handling")
    void testWhitespaceHandling() {
        List<String> suggestions = autoComplete.getSuggestions("  todo  ");
        assertFalse(suggestions.isEmpty());
        
        // Should handle extra whitespace
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
        
        suggestions = autoComplete.getSuggestions("\ttodo\t");
        assertFalse(suggestions.isEmpty());
        
        // Should handle tabs
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("todo")));
    }
    
    @Test
    @DisplayName("Test suggestion uniqueness")
    void testSuggestionUniqueness() {
        List<String> suggestions = autoComplete.getSuggestions("todo");
        
        // Should not contain duplicate suggestions
        for (int i = 0; i < suggestions.size(); i++) {
            for (int j = i + 1; j < suggestions.size(); j++) {
                assertNotEquals(suggestions.get(i), suggestions.get(j));
            }
        }
    }
    
    @Test
    @DisplayName("Test performance with large task list")
    void testPerformanceWithLargeTaskList() {
        // Create large task list
        TaskList largeTaskList = new TaskList();
        for (int i = 0; i < 1000; i++) {
            largeTaskList.addTask(new Todo("todo Task " + i + " !normal #work @office"));
        }
        
        AutoCompleteEngine largeAutoComplete = new AutoCompleteEngine(largeTaskList);
        
        // Should handle large task lists efficiently
        long startTime = System.currentTimeMillis();
        List<String> suggestions = largeAutoComplete.getSuggestions("todo");
        long endTime = System.currentTimeMillis();
        
        assertFalse(suggestions.isEmpty());
        
        // Should complete in reasonable time (less than 100ms)
        assertTrue(endTime - startTime < 100);
    }
} 