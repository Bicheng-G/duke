package tasklist;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

/**
 * Unit tests for Category class
 */
public class CategoryTest {
    
    private Category category;
    
    @BeforeEach
    void setUp() {
        category = new Category();
    }
    
    @Test
    @DisplayName("Test empty category creation")
    void testEmptyCategory() {
        assertTrue(category.getTags().isEmpty());
        assertTrue(category.getContexts().isEmpty());
        assertEquals("", category.toString());
    }
    
    @Test
    @DisplayName("Test single hashtag parsing")
    void testSingleHashtagParsing() {
        String text = "Buy groceries #personal";
        Category cat = Category.parseCategories(text);
        
        assertEquals(1, cat.getTags().size());
        assertTrue(cat.getTags().contains("personal"));
        assertTrue(cat.getContexts().isEmpty());
    }
    
    @Test
    @DisplayName("Test multiple hashtag parsing")
    void testMultipleHashtagParsing() {
        String text = "Team meeting #work #project-alpha #urgent";
        Category cat = Category.parseCategories(text);
        
        assertEquals(3, cat.getTags().size());
        assertTrue(cat.getTags().contains("work"));
        assertTrue(cat.getTags().contains("project-alpha"));
        assertTrue(cat.getTags().contains("urgent"));
        assertTrue(cat.getContexts().isEmpty());
    }
    
    @Test
    @DisplayName("Test single context parsing")
    void testSingleContextParsing() {
        String text = "Call client @phone";
        Category cat = Category.parseCategories(text);
        
        assertEquals(1, cat.getContexts().size());
        assertTrue(cat.getContexts().contains("phone"));
        assertTrue(cat.getTags().isEmpty());
    }
    
    @Test
    @DisplayName("Test multiple context parsing")
    void testMultipleContextParsing() {
        String text = "Meeting @office @conference-room @client-abc";
        Category cat = Category.parseCategories(text);
        
        assertEquals(3, cat.getContexts().size());
        assertTrue(cat.getContexts().contains("office"));
        assertTrue(cat.getContexts().contains("conference-room"));
        assertTrue(cat.getContexts().contains("client-abc"));
        assertTrue(cat.getTags().isEmpty());
    }
    
    @Test
    @DisplayName("Test mixed hashtags and contexts")
    void testMixedHashtagsAndContexts() {
        String text = "Buy groceries #personal #shopping @home @store";
        Category cat = Category.parseCategories(text);
        
        assertEquals(2, cat.getTags().size());
        assertTrue(cat.getTags().contains("personal"));
        assertTrue(cat.getTags().contains("shopping"));
        
        assertEquals(2, cat.getContexts().size());
        assertTrue(cat.getContexts().contains("home"));
        assertTrue(cat.getContexts().contains("store"));
    }
    
    @Test
    @DisplayName("Test hashtag validation")
    void testHashtagValidation() {
        // Valid hashtags
        String validText = "Task #work #project-1 #test_case";
        Category validCat = Category.parseCategories(validText);
        assertEquals(3, validCat.getTags().size());
        
        // Invalid hashtags (should be ignored)
        String invalidText = "Task #123 #-invalid #_start # #work ";
        Category invalidCat = Category.parseCategories(invalidText);
        assertEquals(1, invalidCat.getTags().size());
        assertTrue(invalidCat.getTags().contains("work"));
    }
    
    @Test
    @DisplayName("Test context validation")
    void testContextValidation() {
        // Valid contexts
        String validText = "Task @office @home-office @client_abc";
        Category validCat = Category.parseCategories(validText);
        assertEquals(3, validCat.getContexts().size());
        
        // Invalid contexts (should be ignored)
        String invalidText = "Task @123 @-invalid @_start @ @office ";
        Category invalidCat = Category.parseCategories(invalidText);
        assertEquals(1, invalidCat.getContexts().size());
        assertTrue(invalidCat.getContexts().contains("office"));
    }
    
    @Test
    @DisplayName("Test case sensitivity")
    void testCaseSensitivity() {
        String text = "Task #Work #PERSONAL @HOME @office";
        Category cat = Category.parseCategories(text);
        
        // Should preserve original case
        assertTrue(cat.getTags().contains("Work"));
        assertTrue(cat.getTags().contains("PERSONAL"));
        assertTrue(cat.getContexts().contains("HOME"));
        assertTrue(cat.getContexts().contains("office"));
    }
    
    @Test
    @DisplayName("Test duplicate removal")
    void testDuplicateRemoval() {
        String text = "Task #work #work #personal @home @home @office";
        Category cat = Category.parseCategories(text);
        
        // Should remove duplicates
        assertEquals(2, cat.getTags().size());
        assertEquals(2, cat.getContexts().size());
        
        assertTrue(cat.getTags().contains("work"));
        assertTrue(cat.getTags().contains("personal"));
        assertTrue(cat.getContexts().contains("home"));
        assertTrue(cat.getContexts().contains("office"));
    }
    
    @Test
    @DisplayName("Test string representation")
    void testStringRepresentation() {
        Category cat = new Category();
        cat.addTag("work");
        cat.addTag("personal");
        cat.addContext("home");
        cat.addContext("office");
        
        String result = cat.toString();
        
        // Should contain all tags and contexts
        assertTrue(result.contains("#work"));
        assertTrue(result.contains("#personal"));
        assertTrue(result.contains("@home"));
        assertTrue(result.contains("@office"));
    }
    
    @Test
    @DisplayName("Test empty string representation")
    void testEmptyStringRepresentation() {
        Category emptyCat = new Category();
        assertEquals("", emptyCat.toString());
    }
    
    @Test
    @DisplayName("Test category extraction from text")
    void testCategoryExtractionFromText() {
        String originalText = "Buy groceries #personal @home and milk";
        Category cat = Category.parseCategories(originalText);
        
        // Should extract categories
        assertEquals(1, cat.getTags().size());
        assertEquals(1, cat.getContexts().size());
        assertTrue(cat.getTags().contains("personal"));
        assertTrue(cat.getContexts().contains("home"));
    }
    
    @Test
    @DisplayName("Test category removal from text")
    void testCategoryRemovalFromText() {
        String originalText = "Buy groceries #personal @home and milk";
        String cleanedText = Category.removeCategories(originalText);
        
        // Should remove categories but keep other text
        assertEquals("Buy groceries  and milk", cleanedText);
        assertFalse(cleanedText.contains("#personal"));
        assertFalse(cleanedText.contains("@home"));
    }
    
    @Test
    @DisplayName("Test complex category removal")
    void testComplexCategoryRemoval() {
        String originalText = "Team meeting #work #project-alpha @office @conference-room with client";
        String cleanedText = Category.removeCategories(originalText);
        
        // Should remove all categories
        assertEquals("Team meeting    with client", cleanedText);
        assertFalse(cleanedText.contains("#work"));
        assertFalse(cleanedText.contains("#project-alpha"));
        assertFalse(cleanedText.contains("@office"));
        assertFalse(cleanedText.contains("@conference-room"));
    }
    
    @Test
    @DisplayName("Test special characters in categories")
    void testSpecialCharactersInCategories() {
        String text = "Task #work-project @client_abc #test-case-1 @home-office";
        Category cat = Category.parseCategories(text);
        
        // Should handle hyphens and underscores
        assertTrue(cat.getTags().contains("work-project"));
        assertTrue(cat.getTags().contains("test-case-1"));
        assertTrue(cat.getContexts().contains("client_abc"));
        assertTrue(cat.getContexts().contains("home-office"));
    }
    
    @Test
    @DisplayName("Test category matching for search")
    void testCategoryMatchingForSearch() {
        Category cat = new Category();
        cat.addTag("work");
        cat.addTag("personal");
        cat.addContext("home");
        cat.addContext("office");
        
        // Test matching
        assertTrue(cat.hasTag("work"));
        assertTrue(cat.hasTag("personal"));
        assertTrue(cat.hasContext("home"));
        assertTrue(cat.hasContext("office"));
        
        // Test non-matching
        assertFalse(cat.hasTag("shopping"));
        assertFalse(cat.hasContext("gym"));
    }
    
    @Test
    @DisplayName("Test category merging")
    void testCategoryMerging() {
        Category cat1 = new Category();
        cat1.addTag("work");
        cat1.addContext("office");
        
        Category cat2 = new Category();
        cat2.addTag("personal");
        cat2.addContext("home");
        
        cat1.merge(cat2);
        
        // Should have all tags and contexts
        assertEquals(2, cat1.getTags().size());
        assertEquals(2, cat1.getContexts().size());
        
        assertTrue(cat1.hasTag("work"));
        assertTrue(cat1.hasTag("personal"));
        assertTrue(cat1.hasContext("office"));
        assertTrue(cat1.hasContext("home"));
    }
    
    @Test
    @DisplayName("Test category copying")
    void testCategoryCopying() {
        Category original = new Category();
        original.addTag("work");
        original.addContext("office");
        
        Category copy = new Category(original);
        
        // Should have same content
        assertEquals(original.getTags().size(), copy.getTags().size());
        assertEquals(original.getContexts().size(), copy.getContexts().size());
        
        assertTrue(copy.hasTag("work"));
        assertTrue(copy.hasContext("office"));
        
        // Should be independent
        copy.addTag("personal");
        assertFalse(original.hasTag("personal"));
    }
    
    @Test
    @DisplayName("Test edge cases")
    void testEdgeCases() {
        // Test with null text
        Category nullCat = Category.parseCategories(null);
        assertTrue(nullCat.getTags().isEmpty());
        assertTrue(nullCat.getContexts().isEmpty());
        
        // Test with empty text
        Category emptyCat = Category.parseCategories("");
        assertTrue(emptyCat.getTags().isEmpty());
        assertTrue(emptyCat.getContexts().isEmpty());
        
        // Test with whitespace only
        Category whitespaceCat = Category.parseCategories("   \n\t   ");
        assertTrue(whitespaceCat.getTags().isEmpty());
        assertTrue(whitespaceCat.getContexts().isEmpty());
    }
    
    @Test
    @DisplayName("Test category equality")
    void testCategoryEquality() {
        Category cat1 = new Category();
        cat1.addTag("work");
        cat1.addContext("office");
        
        Category cat2 = new Category();
        cat2.addTag("work");
        cat2.addContext("office");
        
        Category cat3 = new Category();
        cat3.addTag("personal");
        cat3.addContext("home");
        
        assertTrue(cat1.equals(cat2));
        assertFalse(cat1.equals(cat3));
        assertEquals(cat1.hashCode(), cat2.hashCode());
    }
    
    @Test
    @DisplayName("Test category size limits")
    void testCategorySizeLimits() {
        // Test with many categories
        StringBuilder text = new StringBuilder("Task ");
        for (int i = 0; i < 20; i++) {
            text.append("#tag").append(i).append(" ");
            text.append("@context").append(i).append(" ");
        }
        
        Category cat = Category.parseCategories(text.toString());
        
        // Should handle many categories
        assertEquals(20, cat.getTags().size());
        assertEquals(20, cat.getContexts().size());
    }
} 