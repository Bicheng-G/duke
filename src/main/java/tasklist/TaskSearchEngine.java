package tasklist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced search engine for tasks with filtering, sorting, and intelligent matching
 */
public class TaskSearchEngine {
    
    public enum SortBy {
        DATE, DESCRIPTION, TYPE, COMPLETION, PRIORITY
    }
    
    public enum FilterBy {
        ALL, COMPLETED, PENDING, TODO, DEADLINE, EVENT, 
        LOW_PRIORITY, NORMAL_PRIORITY, HIGH_PRIORITY, URGENT_PRIORITY, CRITICAL_PRIORITY
    }
    
    /**
     * Search criteria for advanced task searching
     */
    public static class SearchCriteria {
        private String keyword;
        private FilterBy filter = FilterBy.ALL;
        private SortBy sortBy = SortBy.DATE;
        private boolean ascending = true;
        private LocalDate dateFrom;
        private LocalDate dateTo;
        
        public SearchCriteria(String keyword) {
            this.keyword = keyword;
        }
        
        public SearchCriteria filter(FilterBy filter) {
            this.filter = filter;
            return this;
        }
        
        public SearchCriteria sortBy(SortBy sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        
        public SearchCriteria ascending(boolean ascending) {
            this.ascending = ascending;
            return this;
        }
        
        public SearchCriteria dateRange(LocalDate from, LocalDate to) {
            this.dateFrom = from;
            this.dateTo = to;
            return this;
        }
        
        // Getters
        public String getKeyword() { return keyword; }
        public FilterBy getFilter() { return filter; }
        public SortBy getSortBy() { return sortBy; }
        public boolean isAscending() { return ascending; }
        public LocalDate getDateFrom() { return dateFrom; }
        public LocalDate getDateTo() { return dateTo; }
    }
    
    /**
     * Search result container with metadata
     */
    public static class SearchResult {
        private final List<Task> tasks;
        private final int totalFound;
        private final String searchSummary;
        
        public SearchResult(List<Task> tasks, int totalFound, String searchSummary) {
            this.tasks = tasks;
            this.totalFound = totalFound;
            this.searchSummary = searchSummary;
        }
        
        public List<Task> getTasks() { return tasks; }
        public int getTotalFound() { return totalFound; }
        public String getSearchSummary() { return searchSummary; }
    }
    
    /**
     * Perform advanced search with the given criteria
     */
    public static SearchResult search(TaskList taskList, SearchCriteria criteria) {
        List<Task> allTasks = taskList.getTasks();
        List<Task> filteredTasks = new ArrayList<>();
        
        // Step 1: Filter by type and completion status
        for (Task task : allTasks) {
            if (matchesFilter(task, criteria.getFilter())) {
                filteredTasks.add(task);
            }
        }
        
        // Step 2: Filter by date range (if specified)
        if (criteria.getDateFrom() != null || criteria.getDateTo() != null) {
            filteredTasks = filteredTasks.stream()
                .filter(task -> matchesDateRange(task, criteria.getDateFrom(), criteria.getDateTo()))
                .collect(Collectors.toList());
        }
        
        // Step 3: Filter by keyword (if specified)
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            filteredTasks = filteredTasks.stream()
                .filter(task -> matchesKeyword(task, criteria.getKeyword()))
                .collect(Collectors.toList());
        }
        
        // Step 4: Sort results
        sortTasks(filteredTasks, criteria.getSortBy(), criteria.isAscending());
        
        // Step 5: Create search summary
        String summary = createSearchSummary(criteria, filteredTasks.size(), allTasks.size());
        
        return new SearchResult(filteredTasks, filteredTasks.size(), summary);
    }
    
    /**
     * Quick search for keyword only (backward compatibility)
     */
    public static TaskList quickSearch(TaskList taskList, String keyword) {
        SearchCriteria criteria = new SearchCriteria(keyword);
        SearchResult result = search(taskList, criteria);
        
        TaskList resultList = new TaskList();
        for (Task task : result.getTasks()) {
            resultList.addTask(task);
        }
        return resultList;
    }
    
    /**
     * Check if task matches the filter criteria
     */
    private static boolean matchesFilter(Task task, FilterBy filter) {
        switch (filter) {
            case ALL:
                return true;
            case COMPLETED:
                return task.isDone();
            case PENDING:
                return !task.isDone();
            case TODO:
                return task.getType().equals("T");
            case DEADLINE:
                return task.getType().equals("D");
            case EVENT:
                return task.getType().equals("E");
            case LOW_PRIORITY:
                return task.getPriority() == Priority.LOW;
            case NORMAL_PRIORITY:
                return task.getPriority() == Priority.NORMAL;
            case HIGH_PRIORITY:
                return task.getPriority() == Priority.HIGH;
            case URGENT_PRIORITY:
                return task.getPriority() == Priority.URGENT;
            case CRITICAL_PRIORITY:
                return task.getPriority() == Priority.CRITICAL;
            default:
                return true;
        }
    }
    
    /**
     * Check if task falls within the specified date range
     */
    private static boolean matchesDateRange(Task task, LocalDate from, LocalDate to) {
        LocalDateTime taskDateTime = task.getDateTime();
        if (taskDateTime == null) {
            return false; // Todo tasks have no date
        }
        
        LocalDate taskDate = taskDateTime.toLocalDate();
        
        if (from != null && taskDate.isBefore(from)) {
            return false;
        }
        
        if (to != null && taskDate.isAfter(to)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Enhanced keyword matching with multiple strategies
     */
    private static boolean matchesKeyword(Task task, String keyword) {
        String lowerKeyword = keyword.toLowerCase().trim();
        String description = task.getDescription().toLowerCase();
        
        // Strategy 1: Exact phrase match (highest priority)
        if (description.contains(lowerKeyword)) {
            return true;
        }
        
        // Strategy 2: Word boundary match
        String[] keywordWords = lowerKeyword.split("\\s+");
        String[] descriptionWords = description.split("\\s+");
        
        for (String keywordWord : keywordWords) {
            boolean found = false;
            for (String descWord : descriptionWords) {
                if (descWord.contains(keywordWord)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false; // All keyword words must be found
            }
        }
        
        return true;
    }
    
    /**
     * Sort tasks based on the specified criteria
     */
    private static void sortTasks(List<Task> tasks, SortBy sortBy, boolean ascending) {
        Comparator<Task> comparator;
        
        switch (sortBy) {
            case DATE:
                comparator = (t1, t2) -> {
                    LocalDateTime dt1 = t1.getDateTime();
                    LocalDateTime dt2 = t2.getDateTime();
                    
                    // Tasks without dates (todos) go to the end
                    if (dt1 == null && dt2 == null) return 0;
                    if (dt1 == null) return 1;
                    if (dt2 == null) return -1;
                    
                    return dt1.compareTo(dt2);
                };
                break;
                
            case DESCRIPTION:
                comparator = (t1, t2) -> t1.getDescription().compareToIgnoreCase(t2.getDescription());
                break;
                
            case TYPE:
                comparator = (t1, t2) -> {
                    // Order: Todo (T), Deadline (D), Event (E)
                    String type1 = t1.getType();
                    String type2 = t2.getType();
                    return type1.compareTo(type2);
                };
                break;
                
            case COMPLETION:
                comparator = (t1, t2) -> Boolean.compare(t1.isDone(), t2.isDone());
                break;
                
            case PRIORITY:
                comparator = (t1, t2) -> Integer.compare(t2.getPriority().getLevel(), t1.getPriority().getLevel());
                break;
                
            default:
                return; // No sorting
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        tasks.sort(comparator);
    }
    
    /**
     * Create a human-readable search summary
     */
    private static String createSearchSummary(SearchCriteria criteria, int found, int total) {
        StringBuilder summary = new StringBuilder();
        
        summary.append("Found ").append(found).append(" task(s)");
        if (found < total) {
            summary.append(" out of ").append(total);
        }
        
        // Add search details
        List<String> filters = new ArrayList<>();
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            filters.add("keyword: \"" + criteria.getKeyword() + "\"");
        }
        
        if (criteria.getFilter() != FilterBy.ALL) {
            filters.add("filter: " + criteria.getFilter().toString().toLowerCase());
        }
        
        if (criteria.getDateFrom() != null || criteria.getDateTo() != null) {
            if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
                filters.add("date range: " + criteria.getDateFrom() + " to " + criteria.getDateTo());
            } else if (criteria.getDateFrom() != null) {
                filters.add("from: " + criteria.getDateFrom());
            } else {
                filters.add("until: " + criteria.getDateTo());
            }
        }
        
        if (!filters.isEmpty()) {
            summary.append(" (").append(String.join(", ", filters)).append(")");
        }
        
        // Add sorting info
        if (criteria.getSortBy() != SortBy.DATE || !criteria.isAscending()) {
            summary.append(", sorted by ").append(criteria.getSortBy().toString().toLowerCase());
            if (!criteria.isAscending()) {
                summary.append(" (descending)");
            }
        }
        
        return summary.toString();
    }
    
    /**
     * Get search suggestions based on existing tasks
     */
    public static List<String> getSearchSuggestions(TaskList taskList, String partialKeyword) {
        Set<String> suggestions = new HashSet<>();
        String lowerPartial = partialKeyword.toLowerCase();
        
        for (Task task : taskList.getTasks()) {
            String[] words = task.getDescription().toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.startsWith(lowerPartial) && word.length() > partialKeyword.length()) {
                    suggestions.add(word);
                }
            }
        }
        
        List<String> sortedSuggestions = new ArrayList<>(suggestions);
        sortedSuggestions.sort(String::compareTo);
        return sortedSuggestions.subList(0, Math.min(5, sortedSuggestions.size())); // Top 5 suggestions
    }
} 