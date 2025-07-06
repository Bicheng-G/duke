package parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unit tests for SmartDateParser
 */
public class SmartDateParserTest {
    
    private SmartDateParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new SmartDateParser();
    }
    
    @Test
    @DisplayName("Test natural language date parsing")
    void testNaturalLanguageParsing() {
        // Test "today" variations
        assertNotNull(parser.parseDateTime("today"));
        assertNotNull(parser.parseDateTime("today 3pm"));
        assertNotNull(parser.parseDateTime("today 15:30"));
        
        // Test "tomorrow" variations
        assertNotNull(parser.parseDateTime("tomorrow"));
        assertNotNull(parser.parseDateTime("tomorrow 9am"));
        assertNotNull(parser.parseDateTime("tomorrow morning"));
        
        // Test weekday parsing
        assertNotNull(parser.parseDateTime("monday"));
        assertNotNull(parser.parseDateTime("next friday"));
        assertNotNull(parser.parseDateTime("friday 5pm"));
        
        // Test relative dates
        assertNotNull(parser.parseDateTime("next week"));
        assertNotNull(parser.parseDateTime("end of month"));
        assertNotNull(parser.parseDateTime("next month"));
    }
    
    @Test
    @DisplayName("Test time-only parsing")
    void testTimeOnlyParsing() {
        // Test AM/PM format
        assertNotNull(parser.parseDateTime("9am"));
        assertNotNull(parser.parseDateTime("3pm"));
        assertNotNull(parser.parseDateTime("12:30pm"));
        
        // Test 24-hour format
        assertNotNull(parser.parseDateTime("15:30"));
        assertNotNull(parser.parseDateTime("09:00"));
        assertNotNull(parser.parseDateTime("23:59"));
    }
    
    @Test
    @DisplayName("Test ISO date format parsing")
    void testIsoDateParsing() {
        assertNotNull(parser.parseDateTime("2024-12-25"));
        assertNotNull(parser.parseDateTime("2024-12-25T14:30"));
        assertNotNull(parser.parseDateTime("2024-12-25 14:30"));
    }
    
    @Test
    @DisplayName("Test US date format parsing")
    void testUsDateParsing() {
        assertNotNull(parser.parseDateTime("12/25/2024"));
        assertNotNull(parser.parseDateTime("12/25/2024 2:30pm"));
        assertNotNull(parser.parseDateTime("Dec 25 2024"));
        assertNotNull(parser.parseDateTime("December 25, 2024"));
    }
    
    @Test
    @DisplayName("Test UK date format parsing")
    void testUkDateParsing() {
        assertNotNull(parser.parseDateTime("25/12/2024"));
        assertNotNull(parser.parseDateTime("25/12/2024 14:30"));
        assertNotNull(parser.parseDateTime("25 Dec 2024"));
        assertNotNull(parser.parseDateTime("25th December 2024"));
    }
    
    @Test
    @DisplayName("Test readable date format parsing")
    void testReadableDateParsing() {
        assertNotNull(parser.parseDateTime("Jan 15 2024"));
        assertNotNull(parser.parseDateTime("January 15, 2024"));
        assertNotNull(parser.parseDateTime("15 Jan 2024"));
        assertNotNull(parser.parseDateTime("15th January 2024"));
    }
    
    @Test
    @DisplayName("Test legacy format parsing")
    void testLegacyFormatParsing() {
        assertNotNull(parser.parseDateTime("25/12/2024 1800"));
        assertNotNull(parser.parseDateTime("31/1/2024 0900"));
    }
    
    @Test
    @DisplayName("Test invalid date handling")
    void testInvalidDateHandling() {
        // Test completely invalid dates
        assertNull(parser.parseDateTime("invalid"));
        assertNull(parser.parseDateTime(""));
        assertNull(parser.parseDateTime("32/13/2024"));
        assertNull(parser.parseDateTime("tomorrow yesterday"));
        
        // Test edge cases
        assertNull(parser.parseDateTime("25am"));
        assertNull(parser.parseDateTime("60:00"));
        assertNull(parser.parseDateTime("Feb 30 2024"));
    }
    
    @Test
    @DisplayName("Test date validation")
    void testDateValidation() {
        // Test valid dates
        assertTrue(parser.parseDateTime("2024-02-29") != null); // Valid leap year
        assertTrue(parser.parseDateTime("2024-12-31") != null); // Valid end of year
        
        // Test invalid dates
        assertNull(parser.parseDateTime("2023-02-29")); // Invalid leap year
        assertNull(parser.parseDateTime("2024-13-01")); // Invalid month
        assertNull(parser.parseDateTime("2024-12-32")); // Invalid day
    }
    
    @Test
    @DisplayName("Test time validation")
    void testTimeValidation() {
        // Test valid times
        assertNotNull(parser.parseDateTime("today 00:00"));
        assertNotNull(parser.parseDateTime("today 23:59"));
        assertNotNull(parser.parseDateTime("today 12:30pm"));
        
        // Test invalid times
        assertNull(parser.parseDateTime("today 24:00"));
        assertNull(parser.parseDateTime("today 12:60"));
        assertNull(parser.parseDateTime("today 13pm"));
    }
    
    @Test
    @DisplayName("Test case insensitivity")
    void testCaseInsensitivity() {
        assertNotNull(parser.parseDateTime("TODAY"));
        assertNotNull(parser.parseDateTime("Tomorrow"));
        assertNotNull(parser.parseDateTime("MONDAY"));
        assertNotNull(parser.parseDateTime("Next Friday"));
        assertNotNull(parser.parseDateTime("3PM"));
    }
    
    @Test
    @DisplayName("Test whitespace handling")
    void testWhitespaceHandling() {
        assertNotNull(parser.parseDateTime(" today "));
        assertNotNull(parser.parseDateTime("  tomorrow  3pm  "));
        assertNotNull(parser.parseDateTime("next    friday"));
        assertNotNull(parser.parseDateTime("2024-12-25    14:30"));
    }
    
    @Test
    @DisplayName("Test complex date expressions")
    void testComplexExpressions() {
        assertNotNull(parser.parseDateTime("next monday 9am"));
        assertNotNull(parser.parseDateTime("friday at 5pm"));
        assertNotNull(parser.parseDateTime("tomorrow morning 10:30"));
        assertNotNull(parser.parseDateTime("end of week 6pm"));
    }
    
    @Test
    @DisplayName("Test date parsing accuracy")
    void testDateParsingAccuracy() {
        // Test that "tomorrow" actually gives tomorrow's date
        LocalDateTime tomorrow = parser.parseDateTime("tomorrow");
        LocalDateTime expectedTomorrow = LocalDateTime.now().plusDays(1);
        
        assertEquals(expectedTomorrow.toLocalDate(), tomorrow.toLocalDate());
        
        // Test specific date parsing
        LocalDateTime christmas = parser.parseDateTime("2024-12-25");
        assertNotNull(christmas);
        assertEquals(25, christmas.getDayOfMonth());
        assertEquals(12, christmas.getMonthValue());
        assertEquals(2024, christmas.getYear());
    }
    
    @Test
    @DisplayName("Test supported formats method")
    void testSupportedFormats() {
        String formats = SmartDateParser.getSupportedFormats();
        assertNotNull(formats);
        assertFalse(formats.isEmpty());
        
        // Should contain key format examples
        assertTrue(formats.contains("Natural language"));
        assertTrue(formats.contains("ISO format"));
        assertTrue(formats.contains("US format"));
        assertTrue(formats.contains("UK format"));
    }
    
    @Test
    @DisplayName("Test parser robustness")
    void testParserRobustness() {
        // Test with null input
        assertNull(parser.parseDateTime(null));
        
        // Test with empty string
        assertNull(parser.parseDateTime(""));
        
        // Test with very long invalid string
        assertNull(parser.parseDateTime("this is a very long string that should not parse as a date"));
        
        // Test with special characters
        assertNull(parser.parseDateTime("@#$%^&*()"));
        
        // Test with numbers only
        assertNull(parser.parseDateTime("123456789"));
    }
    
    @Test
    @DisplayName("Test error message generation")
    void testErrorMessageGeneration() {
        // Test that error messages are helpful
        String errorMessage = SmartDateParser.getSupportedFormats();
        assertTrue(errorMessage.contains("examples"));
        assertTrue(errorMessage.contains("format"));
        
        // Error message should be user-friendly
        assertFalse(errorMessage.contains("Exception"));
        assertFalse(errorMessage.contains("Error"));
    }
} 