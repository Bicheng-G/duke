package tasklist;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Category system for task organization with hashtags and @mentions
 */
public class Category {
    private final Set<String> tags;
    private final Set<String> contexts;
    
    // Patterns for parsing categories
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#([a-zA-Z0-9_-]+)");
    private static final Pattern CONTEXT_PATTERN = Pattern.compile("@([a-zA-Z0-9_-]+)");
    
    public Category() {
        this.tags = new HashSet<>();
        this.contexts = new HashSet<>();
    }
    
    public Category(Set<String> tags, Set<String> contexts) {
        this.tags = new HashSet<>(tags);
        this.contexts = new HashSet<>(contexts);
    }
    
    /**
     * Copy constructor
     * @param other Category to copy from
     */
    public Category(Category other) {
        this.tags = new HashSet<>(other.tags);
        this.contexts = new HashSet<>(other.contexts);
    }
    
    /**
     * Parse categories from text input
     * @param text input text containing #tags and @contexts
     * @return Category object with parsed tags and contexts
     */
    public static Category fromText(String text) {
        if (text == null) return new Category();
        
        Set<String> tags = new HashSet<>();
        Set<String> contexts = new HashSet<>();
        
        // Parse hashtags (#tag)
        Matcher hashtagMatcher = HASHTAG_PATTERN.matcher(text);
        while (hashtagMatcher.find()) {
            tags.add(hashtagMatcher.group(1).toLowerCase());
        }
        
        // Parse contexts (@context)
        Matcher contextMatcher = CONTEXT_PATTERN.matcher(text);
        while (contextMatcher.find()) {
            contexts.add(contextMatcher.group(1).toLowerCase());
        }
        
        return new Category(tags, contexts);
    }
    
    /**
     * Parse categories from text input (alias for fromText for test compatibility)
     * @param text input text containing #tags and @contexts
     * @return Category object with parsed tags and contexts
     */
    public static Category parseCategories(String text) {
        return fromText(text);
    }
    
    /**
     * Remove category markers from text
     * @param text input text
     * @return text with #tags and @contexts removed
     */
    public static String removeCategoriesFromText(String text) {
        if (text == null) return text;
        
        String result = text;
        result = HASHTAG_PATTERN.matcher(result).replaceAll(" ");
        result = CONTEXT_PATTERN.matcher(result).replaceAll(" ");
        
        // Clean up multiple spaces
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }
    
    /**
     * Check if text contains any category markers
     * @param text input text
     * @return true if #tags or @contexts found
     */
    public static boolean containsCategories(String text) {
        if (text == null) return false;
        
        return HASHTAG_PATTERN.matcher(text).find() || 
               CONTEXT_PATTERN.matcher(text).find();
    }
    
    /**
     * Remove categories from text
     * @param text input text
     * @return text with #tags and @contexts removed
     */
    public static String removeCategories(String text) {
        if (text == null) return text;
        
        String result = text;
        result = HASHTAG_PATTERN.matcher(result).replaceAll(" ");
        result = CONTEXT_PATTERN.matcher(result).replaceAll(" ");
        
        // Clean up multiple spaces
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }
    
    /**
     * Get all tags
     * @return set of tags
     */
    public Set<String> getTags() {
        return new HashSet<>(tags);
    }
    
    /**
     * Get all contexts
     * @return set of contexts
     */
    public Set<String> getContexts() {
        return new HashSet<>(contexts);
    }
    
    /**
     * Add a tag
     * @param tag tag to add
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            tags.add(tag.toLowerCase().trim());
        }
    }
    
    /**
     * Add a context
     * @param context context to add
     */
    public void addContext(String context) {
        if (context != null && !context.trim().isEmpty()) {
            contexts.add(context.toLowerCase().trim());
        }
    }
    
    /**
     * Remove a tag
     * @param tag tag to remove
     */
    public void removeTag(String tag) {
        tags.remove(tag.toLowerCase());
    }
    
    /**
     * Remove a context
     * @param context context to remove
     */
    public void removeContext(String context) {
        contexts.remove(context.toLowerCase());
    }
    
    /**
     * Check if has any categories
     * @return true if has tags or contexts
     */
    public boolean hasCategories() {
        return !tags.isEmpty() || !contexts.isEmpty();
    }
    
    /**
     * Check if has specific tag
     * @param tag tag to check
     * @return true if has tag
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag.toLowerCase());
    }
    
    /**
     * Check if has specific context
     * @param context context to check
     * @return true if has context
     */
    public boolean hasContext(String context) {
        return contexts.contains(context.toLowerCase());
    }
    
    /**
     * Get display string for categories
     * @return formatted string with tags and contexts
     */
    public String getDisplayString() {
        if (!hasCategories()) {
            return "";
        }
        
        StringBuilder display = new StringBuilder();
        
        // Add tags
        if (!tags.isEmpty()) {
            List<String> sortedTags = new ArrayList<>(tags);
            Collections.sort(sortedTags);
            for (String tag : sortedTags) {
                display.append("#").append(tag).append(" ");
            }
        }
        
        // Add contexts
        if (!contexts.isEmpty()) {
            List<String> sortedContexts = new ArrayList<>(contexts);
            Collections.sort(sortedContexts);
            for (String context : sortedContexts) {
                display.append("@").append(context).append(" ");
            }
        }
        
        return display.toString().trim();
    }
    
    /**
     * Get category help text
     * @return help text explaining category usage
     */
    public static String getCategoryHelp() {
        return "Categories:\n" +
               "  📋 #tag - Add tags (e.g., #work, #personal, #urgent)\n" +
               "  📍 @context - Add contexts (e.g., @home, @office, @client-name)\n" +
               "\nExamples:\n" +
               "  todo Buy groceries #personal @home\n" +
               "  deadline Submit report #work @office !high\n" +
               "  event Meeting #work @client-abc !urgent";
    }
    
    /**
     * Merge with another category
     * @param other category to merge with
     * @return new category with combined tags and contexts
     */
    public Category merge(Category other) {
        if (other == null) return this;
        
        Set<String> mergedTags = new HashSet<>(this.tags);
        mergedTags.addAll(other.tags);
        
        Set<String> mergedContexts = new HashSet<>(this.contexts);
        mergedContexts.addAll(other.contexts);
        
        return new Category(mergedTags, mergedContexts);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Category category = (Category) obj;
        return Objects.equals(tags, category.tags) && 
               Objects.equals(contexts, category.contexts);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tags, contexts);
    }
    
    @Override
    public String toString() {
        return getDisplayString();
    }
} 