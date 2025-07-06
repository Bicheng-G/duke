package tasklist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Priority enum
 */
public class PriorityTest {
    
    @Test
    @DisplayName("Test priority enum values")
    void testPriorityValues() {
        assertEquals(5, Priority.values().length);
        
        // Test all priority levels exist
        assertNotNull(Priority.LOW);
        assertNotNull(Priority.NORMAL);
        assertNotNull(Priority.HIGH);
        assertNotNull(Priority.URGENT);
        assertNotNull(Priority.CRITICAL);
    }
    
    @Test
    @DisplayName("Test priority parsing from string")
    void testPriorityParsing() {
        // Test exact matches
        assertEquals(Priority.LOW, Priority.fromString("!low"));
        assertEquals(Priority.NORMAL, Priority.fromString("!normal"));
        assertEquals(Priority.HIGH, Priority.fromString("!high"));
        assertEquals(Priority.URGENT, Priority.fromString("!urgent"));
        assertEquals(Priority.CRITICAL, Priority.fromString("!critical"));
        
        // Test case insensitive
        assertEquals(Priority.LOW, Priority.fromString("!LOW"));
        assertEquals(Priority.NORMAL, Priority.fromString("!Normal"));
        assertEquals(Priority.HIGH, Priority.fromString("!HIGH"));
        assertEquals(Priority.URGENT, Priority.fromString("!URGENT"));
        assertEquals(Priority.CRITICAL, Priority.fromString("!Critical"));
        
        // Test mixed case
        assertEquals(Priority.LOW, Priority.fromString("!LoW"));
        assertEquals(Priority.NORMAL, Priority.fromString("!nOrMaL"));
        assertEquals(Priority.HIGH, Priority.fromString("!hIgH"));
    }
    
    @Test
    @DisplayName("Test priority parsing without exclamation mark")
    void testPriorityParsingWithoutExclamation() {
        // Test without exclamation mark
        assertEquals(Priority.LOW, Priority.fromString("low"));
        assertEquals(Priority.NORMAL, Priority.fromString("normal"));
        assertEquals(Priority.HIGH, Priority.fromString("high"));
        assertEquals(Priority.URGENT, Priority.fromString("urgent"));
        assertEquals(Priority.CRITICAL, Priority.fromString("critical"));
    }
    
    @Test
    @DisplayName("Test priority shortcuts")
    void testPriorityShortcuts() {
        // Test single letter shortcuts
        assertEquals(Priority.LOW, Priority.fromString("!l"));
        assertEquals(Priority.NORMAL, Priority.fromString("!n"));
        assertEquals(Priority.HIGH, Priority.fromString("!h"));
        assertEquals(Priority.URGENT, Priority.fromString("!u"));
        assertEquals(Priority.CRITICAL, Priority.fromString("!c"));
        
        // Test shortcuts without exclamation
        assertEquals(Priority.LOW, Priority.fromString("l"));
        assertEquals(Priority.NORMAL, Priority.fromString("n"));
        assertEquals(Priority.HIGH, Priority.fromString("h"));
        assertEquals(Priority.URGENT, Priority.fromString("u"));
        assertEquals(Priority.CRITICAL, Priority.fromString("c"));
    }
    
    @Test
    @DisplayName("Test invalid priority strings")
    void testInvalidPriorityStrings() {
        // Test invalid strings return NORMAL
        assertEquals(Priority.NORMAL, Priority.fromString("invalid"));
        assertEquals(Priority.NORMAL, Priority.fromString(""));
        assertEquals(Priority.NORMAL, Priority.fromString(null));
        assertEquals(Priority.NORMAL, Priority.fromString("!invalid"));
        assertEquals(Priority.NORMAL, Priority.fromString("!"));
        assertEquals(Priority.NORMAL, Priority.fromString("!!high"));
        assertEquals(Priority.NORMAL, Priority.fromString("medium"));
    }
    
    @Test
    @DisplayName("Test priority level ordering")
    void testPriorityOrdering() {
        // Test that priorities are ordered correctly
        assertEquals(1, Priority.LOW.getLevel());
        assertEquals(2, Priority.NORMAL.getLevel());
        assertEquals(3, Priority.HIGH.getLevel());
        assertEquals(4, Priority.URGENT.getLevel());
        assertEquals(5, Priority.CRITICAL.getLevel());
    }
    
    @Test
    @DisplayName("Test priority comparison")
    void testPriorityComparison() {
        // Test priority comparison
        assertTrue(Priority.LOW.compareTo(Priority.NORMAL) < 0);
        assertTrue(Priority.NORMAL.compareTo(Priority.HIGH) < 0);
        assertTrue(Priority.HIGH.compareTo(Priority.URGENT) < 0);
        assertTrue(Priority.URGENT.compareTo(Priority.CRITICAL) < 0);
        
        // Test reverse comparison
        assertTrue(Priority.CRITICAL.compareTo(Priority.URGENT) > 0);
        assertTrue(Priority.URGENT.compareTo(Priority.HIGH) > 0);
        assertTrue(Priority.HIGH.compareTo(Priority.NORMAL) > 0);
        assertTrue(Priority.NORMAL.compareTo(Priority.LOW) > 0);
        
        // Test equality
        assertEquals(0, Priority.LOW.compareTo(Priority.LOW));
        assertEquals(0, Priority.NORMAL.compareTo(Priority.NORMAL));
        assertEquals(0, Priority.HIGH.compareTo(Priority.HIGH));
        assertEquals(0, Priority.URGENT.compareTo(Priority.URGENT));
        assertEquals(0, Priority.CRITICAL.compareTo(Priority.CRITICAL));
    }
    
    @Test
    @DisplayName("Test priority emoji display")
    void testPriorityEmojiDisplay() {
        // Test emoji strings
        assertEquals("🟢", Priority.LOW.getEmoji());
        assertEquals("🔵", Priority.NORMAL.getEmoji());
        assertEquals("🟡", Priority.HIGH.getEmoji());
        assertEquals("🔴", Priority.URGENT.getEmoji());
        assertEquals("🚨", Priority.CRITICAL.getEmoji());
    }
    
    @Test
    @DisplayName("Test priority string representation")
    void testPriorityStringRepresentation() {
        assertEquals("LOW", Priority.LOW.toString());
        assertEquals("NORMAL", Priority.NORMAL.toString());
        assertEquals("HIGH", Priority.HIGH.toString());
        assertEquals("URGENT", Priority.URGENT.toString());
        assertEquals("CRITICAL", Priority.CRITICAL.toString());
    }
    
    @Test
    @DisplayName("Test priority color codes")
    void testPriorityColorCodes() {
        // Test that each priority has a unique color
        assertNotNull(Priority.LOW.getEmoji());
        assertNotNull(Priority.NORMAL.getEmoji());
        assertNotNull(Priority.HIGH.getEmoji());
        assertNotNull(Priority.URGENT.getEmoji());
        assertNotNull(Priority.CRITICAL.getEmoji());
        
        // Test that colors are different
        assertNotEquals(Priority.LOW.getEmoji(), Priority.NORMAL.getEmoji());
        assertNotEquals(Priority.NORMAL.getEmoji(), Priority.HIGH.getEmoji());
        assertNotEquals(Priority.HIGH.getEmoji(), Priority.URGENT.getEmoji());
        assertNotEquals(Priority.URGENT.getEmoji(), Priority.CRITICAL.getEmoji());
    }
    
    @Test
    @DisplayName("Test priority parsing with whitespace")
    void testPriorityParsingWithWhitespace() {
        // Test with leading/trailing whitespace
        assertEquals(Priority.LOW, Priority.fromString(" !low "));
        assertEquals(Priority.NORMAL, Priority.fromString("  !normal  "));
        assertEquals(Priority.HIGH, Priority.fromString("\t!high\t"));
        assertEquals(Priority.URGENT, Priority.fromString("\n!urgent\n"));
        assertEquals(Priority.CRITICAL, Priority.fromString(" !critical "));
    }
    
    @Test
    @DisplayName("Test priority parsing edge cases")
    void testPriorityParsingEdgeCases() {
        // Test with special characters
        assertEquals(Priority.NORMAL, Priority.fromString("!high!"));
        assertEquals(Priority.NORMAL, Priority.fromString("!!high"));
        assertEquals(Priority.NORMAL, Priority.fromString("!h!"));
        assertEquals(Priority.NORMAL, Priority.fromString("!@#$%"));
        
        // Test with numbers
        assertEquals(Priority.NORMAL, Priority.fromString("!1"));
        assertEquals(Priority.NORMAL, Priority.fromString("!high1"));
        assertEquals(Priority.NORMAL, Priority.fromString("!2high"));
    }
    
    @Test
    @DisplayName("Test priority extraction from text")
    void testPriorityExtractionFromText() {
        // Test extraction from longer text
        assertEquals(Priority.HIGH, Priority.fromString("This is !high priority"));
        assertEquals(Priority.URGENT, Priority.fromString("!urgent task description"));
        assertEquals(Priority.CRITICAL, Priority.fromString("Complete !critical project"));
        
        // Test multiple priorities (should return first found)
        assertEquals(Priority.LOW, Priority.fromString("!low and !high priority"));
        assertEquals(Priority.HIGH, Priority.fromString("!high !urgent task"));
    }
    
    @Test
    @DisplayName("Test priority levels for sorting")
    void testPriorityLevelsForSorting() {
        // Create array of priorities in random order
        Priority[] priorities = {
            Priority.NORMAL, Priority.CRITICAL, Priority.LOW, 
            Priority.URGENT, Priority.HIGH
        };
        
        // Sort by priority level
        java.util.Arrays.sort(priorities);
        
        // Verify sorted order (LOW to CRITICAL)
        assertEquals(Priority.LOW, priorities[0]);
        assertEquals(Priority.NORMAL, priorities[1]);
        assertEquals(Priority.HIGH, priorities[2]);
        assertEquals(Priority.URGENT, priorities[3]);
        assertEquals(Priority.CRITICAL, priorities[4]);
    }
    
    @Test
    @DisplayName("Test priority for task filtering")
    void testPriorityForTaskFiltering() {
        // Test high priority check
        assertTrue(Priority.HIGH.getLevel() >= 3);
        assertTrue(Priority.URGENT.getLevel() >= 3);
        assertTrue(Priority.CRITICAL.getLevel() >= 3);
        
        assertFalse(Priority.LOW.getLevel() >= 3);
        assertFalse(Priority.NORMAL.getLevel() >= 3);
        
        // Test urgent priority check
        assertTrue(Priority.URGENT.getLevel() >= 4);
        assertTrue(Priority.CRITICAL.getLevel() >= 4);
        
        assertFalse(Priority.LOW.getLevel() >= 4);
        assertFalse(Priority.NORMAL.getLevel() >= 4);
        assertFalse(Priority.HIGH.getLevel() >= 4);
    }
} 