package parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * SmartDateParser provides flexible date parsing capabilities,
 * supporting multiple formats including natural language input.
 */
public class SmartDateParser {
    
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),     // Original format
        DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),     // With colon
        DateTimeFormatter.ofPattern("d/M/yyyy h:mm a"),   // 12-hour format
        DateTimeFormatter.ofPattern("d-M-yyyy HHmm"),     // Dashes
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),   // ISO-like
        DateTimeFormatter.ofPattern("MMM d, yyyy HHmm"),  // Month name
        DateTimeFormatter.ofPattern("d MMM yyyy HHmm")    // Day month year
    );
    
    private static final List<DateTimeFormatter> DATE_ONLY_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("MMM d, yyyy"),
        DateTimeFormatter.ofPattern("d MMM yyyy")
    );
    
    private static final Map<String, Integer> RELATIVE_DAYS = new HashMap<>();
    static {
        RELATIVE_DAYS.put("today", 0);
        RELATIVE_DAYS.put("tomorrow", 1);
        RELATIVE_DAYS.put("tmr", 1);
        RELATIVE_DAYS.put("yesterday", -1);
    }
    
    private static final Map<String, Integer> DAY_OFFSETS = new HashMap<>();
    static {
        DAY_OFFSETS.put("monday", 1);
        DAY_OFFSETS.put("tuesday", 2);
        DAY_OFFSETS.put("wednesday", 3);
        DAY_OFFSETS.put("thursday", 4);
        DAY_OFFSETS.put("friday", 5);
        DAY_OFFSETS.put("saturday", 6);
        DAY_OFFSETS.put("sunday", 7);
        DAY_OFFSETS.put("mon", 1);
        DAY_OFFSETS.put("tue", 2);
        DAY_OFFSETS.put("wed", 3);
        DAY_OFFSETS.put("thu", 4);
        DAY_OFFSETS.put("fri", 5);
        DAY_OFFSETS.put("sat", 6);
        DAY_OFFSETS.put("sun", 7);
    }
    
    /**
     * Parse a date/time string using various formats and natural language
     * @param input the input string to parse
     * @return LocalDateTime object
     * @throws DateTimeParseException if no format matches
     */
    public static LocalDateTime parseDateTime(String input) throws DateTimeParseException {
        if (input == null || input.trim().isEmpty()) {
            throw new DateTimeParseException("Date/time input cannot be empty", input, 0);
        }
        
        String normalizedInput = input.trim().toLowerCase();
        
        // Try natural language parsing first
        LocalDateTime naturalResult = tryNaturalLanguage(normalizedInput);
        if (naturalResult != null) {
            return naturalResult;
        }
        
        // Try exact format matching
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }
        
        // Try date-only formats (default to 9 AM)
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(input, formatter);
                return date.atTime(9, 0); // Default to 9 AM
            } catch (DateTimeParseException e) {
                // Continue to next formatter
            }
        }
        
        // If all parsing attempts fail, throw detailed exception
        throw new DateTimeParseException(generateHelpfulErrorMessage(), input, 0);
    }
    
    /**
     * Try to parse natural language date/time expressions
     */
    private static LocalDateTime tryNaturalLanguage(String input) {
        String[] parts = input.split("\\s+");
        LocalDate date = null;
        LocalTime time = null;
        
        // Process each part
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Check for relative days
            if (RELATIVE_DAYS.containsKey(part)) {
                date = LocalDate.now().plusDays(RELATIVE_DAYS.get(part));
                continue;
            }
            
            // Check for day names (next week)
            if (DAY_OFFSETS.containsKey(part)) {
                int targetDay = DAY_OFFSETS.get(part);
                int currentDay = LocalDate.now().getDayOfWeek().getValue();
                int daysToAdd = targetDay - currentDay;
                if (daysToAdd <= 0) {
                    daysToAdd += 7; // Next week
                }
                date = LocalDate.now().plusDays(daysToAdd);
                continue;
            }
            
            // Check for "next [day]" patterns
            if (part.equals("next") && i + 1 < parts.length) {
                String nextPart = parts[i + 1];
                if (DAY_OFFSETS.containsKey(nextPart)) {
                    int targetDay = DAY_OFFSETS.get(nextPart);
                    int currentDay = LocalDate.now().getDayOfWeek().getValue();
                    int daysToAdd = targetDay - currentDay + 7; // Always next week
                    date = LocalDate.now().plusDays(daysToAdd);
                    i++; // Skip the next part as we've processed it
                    continue;
                }
                
                if (nextPart.equals("week")) {
                    date = LocalDate.now().plusWeeks(1);
                    i++; // Skip the next part
                    continue;
                }
                
                if (nextPart.equals("month")) {
                    date = LocalDate.now().plusMonths(1);
                    i++; // Skip the next part
                    continue;
                }
            }
            
            // Try to parse as time
            time = tryParseTime(part);
        }
        
        // If we have a date but no time, default to 9 AM
        if (date != null && time == null) {
            time = LocalTime.of(9, 0);
        }
        
        // If we have time but no date, use today
        if (time != null && date == null) {
            date = LocalDate.now();
        }
        
        // Return combined result
        if (date != null && time != null) {
            return LocalDateTime.of(date, time);
        }
        
        return null; // Could not parse as natural language
    }
    
    /**
     * Try to parse a time string in various formats
     */
    private static LocalTime tryParseTime(String input) {
        try {
            // Common time formats
            String[] timeFormats = {
                "H:mm",      // 14:30
                "HHmm",      // 1430
                "h:mm a",    // 2:30 PM
                "ha",        // 2PM
                "H'pm'",     // 2pm
                "H'am'",     // 9am
                "HH'pm'",    // 14pm (treat as 2pm)
                "HH'am'"     // 09am (treat as 9am)
            };
            
            for (String format : timeFormats) {
                try {
                    return LocalTime.parse(input, DateTimeFormatter.ofPattern(format));
                } catch (DateTimeParseException e) {
                    // Continue to next format
                }
            }
            
            // Handle special cases like "2pm", "9am"
            if (input.matches("\\d{1,2}(pm|am)")) {
                int hour = Integer.parseInt(input.replaceAll("[^\\d]", ""));
                boolean isPM = input.contains("pm");
                
                if (isPM && hour != 12) {
                    hour += 12;
                } else if (!isPM && hour == 12) {
                    hour = 0;
                }
                
                return LocalTime.of(hour, 0);
            }
            
            // Handle "6:30pm" style
            if (input.matches("\\d{1,2}:\\d{2}(pm|am)")) {
                String[] timeParts = input.replaceAll("[^\\d:]", "").split(":");
                int hour = Integer.parseInt(timeParts[0]);
                int minute = Integer.parseInt(timeParts[1]);
                boolean isPM = input.contains("pm");
                
                if (isPM && hour != 12) {
                    hour += 12;
                } else if (!isPM && hour == 12) {
                    hour = 0;
                }
                
                return LocalTime.of(hour, minute);
            }
            
        } catch (Exception e) {
            // Could not parse as time
        }
        
        return null;
    }
    
    /**
     * Generate a helpful error message with examples
     */
    private static String generateHelpfulErrorMessage() {
        return "Invalid date/time format. Try these examples:\n" +
               "📅 Exact formats:\n" +
               "  • 31/12/2024 1800\n" +
               "  • 31/12/2024 6:00 PM\n" +
               "  • Dec 31, 2024 1800\n" +
               "\n" +
               "🗣️ Natural language:\n" +
               "  • tomorrow 6pm\n" +
               "  • next friday 2:30 PM\n" +
               "  • today 9am\n" +
               "  • monday 3pm\n" +
               "\n" +
               "⏰ Time only (uses today's date):\n" +
               "  • 6pm\n" +
               "  • 14:30\n" +
               "  • 9am\n" +
               "\n" +
               "📆 Date only (uses 9 AM):\n" +
               "  • 31/12/2024\n" +
               "  • tomorrow\n" +
               "  • next week";
    }
    
    /**
     * Get examples of supported date formats
     */
    public static String getSupportedFormats() {
        return generateHelpfulErrorMessage();
    }
    
    /**
     * Check if a string can be parsed as a date/time
     */
    public static boolean canParse(String input) {
        try {
            parseDateTime(input);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
} 