package ui;

import tasklist.*;
import command.Command;
import parser.Parser;
import storage.Storage;
import exception.DukeException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern GUI Controller for Duke Task Manager
 * Features enhanced UI/UX with better visual design and interactions
 */
public class ModernGuiController extends JFrame {
    
    private TaskList taskList;
    private Storage storage;
    private Ui ui;
    private SmartSuggestionSystem suggestionSystem;
    
    // GUI Components
    private JPanel mainPanel;
    private JPanel taskListPanel;
    private JScrollPane taskScrollPane;
    private JTextField commandInput;
    private JTextArea outputArea;
    private JButton executeButton;
    private JPanel suggestionPanel;
    private JLabel statusLabel;
    private JPanel quickActionsPanel;
    private JTabbedPane tabbedPane;
    
    // Color scheme for modern look
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);    // Blue
    private static final Color SECONDARY_COLOR = new Color(46, 204, 113);  // Green
    private static final Color ACCENT_COLOR = new Color(241, 196, 15);     // Yellow
    private static final Color DANGER_COLOR = new Color(231, 76, 60);      // Red
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Light gray
    private static final Color TEXT_COLOR = new Color(52, 73, 94);         // Dark gray
    private static final Color BORDER_COLOR = new Color(220, 221, 222);    // Light border
    
    public ModernGuiController() {
        initializeData();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTaskList();
        
        setTitle("Duke Task Manager - Modern Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Initialize data components
     */
    private void initializeData() {
        storage = new Storage("src/data/tasks.txt");
        ui = new Ui();
        
        try {
            taskList = new TaskList(storage.readFromFile());
        } catch (Exception e) {
            taskList = new TaskList();
            // Load demo tasks if no existing tasks
            if (taskList.size() == 0) {
                loadDemoTasks();
            }
        }
        
        suggestionSystem = new SmartSuggestionSystem(taskList);
    }
    
    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create tabbed pane for different views
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        
        // Task list panel
        createTaskListPanel();
        
        // Command input panel
        createCommandPanel();
        
        // Quick actions panel
        createQuickActionsPanel();
        
        // Status bar
        createStatusBar();
        
        // Suggestion panel
        createSuggestionPanel();
    }
    
    /**
     * Create task list panel with modern design
     */
    private void createTaskListPanel() {
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        
        taskScrollPane = new JScrollPane(taskListPanel);
        taskScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        taskScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        taskScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            "📋 Tasks", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
    }
    
    /**
     * Create command input panel
     */
    private void createCommandPanel() {
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.setBackground(BACKGROUND_COLOR);
        commandPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            "💬 Command Input", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            TEXT_COLOR
        ));
        
        // Command input field
        commandInput = new JTextField();
        commandInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Execute button
        executeButton = new JButton("Execute");
        executeButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        executeButton.setBackground(PRIMARY_COLOR);
        executeButton.setForeground(Color.WHITE);
        executeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        executeButton.setFocusPainted(false);
        
        // Output area
        outputArea = new JTextArea(4, 50);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        outputArea.setBackground(new Color(250, 250, 250));
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        outputArea.setEditable(false);
        
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
        
        // Layout
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(commandInput, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.EAST);
        
        commandPanel.add(inputPanel, BorderLayout.NORTH);
        commandPanel.add(outputScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("📝 Commands", commandPanel);
    }
    
    /**
     * Create quick actions panel
     */
    private void createQuickActionsPanel() {
        quickActionsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        quickActionsPanel.setBackground(BACKGROUND_COLOR);
        quickActionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Quick action buttons
        addQuickActionButton("📋 List All Tasks", "list", PRIMARY_COLOR);
        addQuickActionButton("➕ Add Todo", "todo ", SECONDARY_COLOR);
        addQuickActionButton("⏰ Add Deadline", "deadline ", ACCENT_COLOR);
        addQuickActionButton("📅 Add Event", "event ", new Color(155, 89, 182));
        addQuickActionButton("🔍 Search Tasks", "search ", new Color(52, 152, 219));
        addQuickActionButton("❓ Help", "help", new Color(149, 165, 166));
        
        tabbedPane.addTab("⚡ Quick Actions", quickActionsPanel);
    }
    
    /**
     * Create status bar
     */
    private void createStatusBar() {
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setBackground(BACKGROUND_COLOR);
        statusLabel.setOpaque(true);
    }
    
    /**
     * Create suggestion panel
     */
    private void createSuggestionPanel() {
        suggestionPanel = new JPanel();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        suggestionPanel.setBackground(new Color(255, 255, 255));
        suggestionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            "💡 Smart Suggestions", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            TEXT_COLOR
        ));
        
        JScrollPane suggestionScrollPane = new JScrollPane(suggestionPanel);
        suggestionScrollPane.setPreferredSize(new Dimension(300, 200));
        
        tabbedPane.addTab("🧠 Suggestions", suggestionScrollPane);
    }
    
    /**
     * Add quick action button
     */
    private void addQuickActionButton(String text, String command, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        button.setFocusPainted(false);
        
        button.addActionListener(e -> {
            commandInput.setText(command);
            commandInput.requestFocus();
            if (command.trim().equals("list")) {
                executeCommand();
            }
        });
        
        quickActionsPanel.add(button);
    }
    
    /**
     * Setup layout
     */
    private void setupLayout() {
        // Main layout
        mainPanel.add(taskScrollPane, BorderLayout.CENTER);
        mainPanel.add(tabbedPane, BorderLayout.EAST);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Set preferred sizes
        taskScrollPane.setPreferredSize(new Dimension(600, 600));
        tabbedPane.setPreferredSize(new Dimension(400, 600));
        
        add(mainPanel);
    }
    
    /**
     * Setup event listeners
     */
    private void setupEventListeners() {
        // Execute button
        executeButton.addActionListener(e -> executeCommand());
        
        // Enter key in command input
        commandInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeCommand();
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                    showSuggestions();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_ENTER && e.getKeyCode() != KeyEvent.VK_TAB) {
                    updateSuggestions();
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }
    
    /**
     * Execute command
     */
    private void executeCommand() {
        String input = commandInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        
        try {
            Command command = Parser.parse(input, taskList);
            
            // Capture output
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream originalOut = System.out;
            System.setOut(ps);
            
            command.execute(taskList, ui, storage);
            
            System.setOut(originalOut);
            String output = baos.toString();
            
            // Update output area
            outputArea.setText(output);
            
            // Update task list display
            loadTaskList();
            
            // Clear input
            commandInput.setText("");
            
            // Update status
            statusLabel.setText("Command executed successfully");
            
            // Record command for learning
            suggestionSystem.recordCommand(input);
            
        } catch (DukeException | IOException e) {
            outputArea.setText("Error: " + e.getMessage());
            statusLabel.setText("Error executing command");
        }
    }
    
    /**
     * Load and display task list
     */
    private void loadTaskList() {
        taskListPanel.removeAll();
        
        if (taskList.size() == 0) {
            JLabel emptyLabel = new JLabel("No tasks yet. Add some tasks to get started!");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(149, 165, 166));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(new EmptyBorder(50, 20, 50, 20));
            taskListPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < taskList.size(); i++) {
                Task task = taskList.get(i);
                JPanel taskPanel = createTaskPanel(task, i + 1);
                taskListPanel.add(taskPanel);
            }
        }
        
        taskListPanel.revalidate();
        taskListPanel.repaint();
        
        // Update status
        statusLabel.setText("Tasks loaded: " + taskList.size() + " total");
    }
    
    /**
     * Create task panel with modern design
     */
    private JPanel createTaskPanel(Task task, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Task info
        JLabel taskLabel = new JLabel(index + ". " + task.printTask());
        taskLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taskLabel.setForeground(TEXT_COLOR);
        
        // Priority indicator
        JLabel priorityLabel = new JLabel(getPriorityIcon(task.getPriority()));
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        // Done button
        JButton doneButton = new JButton(task.isDone() ? "✓" : "○");
        doneButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        doneButton.setBackground(task.isDone() ? SECONDARY_COLOR : BORDER_COLOR);
        doneButton.setForeground(Color.WHITE);
        doneButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        doneButton.setFocusPainted(false);
        
        doneButton.addActionListener(e -> {
            commandInput.setText("done " + index);
            executeCommand();
        });
        
        // Delete button
        JButton deleteButton = new JButton("🗑");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteButton.setBackground(DANGER_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        deleteButton.setFocusPainted(false);
        
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Are you sure you want to delete this task?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                commandInput.setText("delete " + index);
                executeCommand();
            }
        });
        
        buttonPanel.add(doneButton);
        buttonPanel.add(deleteButton);
        
        panel.add(priorityLabel, BorderLayout.WEST);
        panel.add(taskLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Get priority icon
     */
    private String getPriorityIcon(Priority priority) {
        switch (priority) {
            case LOW: return "🟢";
            case NORMAL: return "🔵";
            case HIGH: return "🟡";
            case URGENT: return "🔴";
            case CRITICAL: return "🚨";
            default: return "🔵";
        }
    }
    
    /**
     * Update suggestions based on input
     */
    private void updateSuggestions() {
        String input = commandInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }
        
        String suggestions = suggestionSystem.processInput(input);
        updateSuggestionPanel(suggestions);
    }
    
    /**
     * Show suggestions
     */
    private void showSuggestions() {
        String input = commandInput.getText();
        String suggestions = suggestionSystem.processInput(input);
        updateSuggestionPanel(suggestions);
        
        // Switch to suggestions tab
        tabbedPane.setSelectedIndex(2);
    }
    
    /**
     * Update suggestion panel
     */
    private void updateSuggestionPanel(String suggestions) {
        suggestionPanel.removeAll();
        
        if (suggestions.isEmpty()) {
            JLabel noSuggestionsLabel = new JLabel("No suggestions available");
            noSuggestionsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noSuggestionsLabel.setForeground(new Color(149, 165, 166));
            suggestionPanel.add(noSuggestionsLabel);
        } else {
            // Parse suggestions and create clickable buttons
            String[] lines = suggestions.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                JLabel suggestionLabel = new JLabel(line);
                suggestionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                suggestionLabel.setForeground(TEXT_COLOR);
                suggestionLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
                suggestionPanel.add(suggestionLabel);
            }
        }
        
        suggestionPanel.revalidate();
        suggestionPanel.repaint();
    }
    
    /**
     * Load demo tasks
     */
    private void loadDemoTasks() {
        try {
            List<Task> demoTasks = demo.DemoTaskGenerator.generateDemoTasks();
            for (Task task : demoTasks) {
                taskList.addTask(task);
            }
            storage.writeToFile(taskList.getTasks());
        } catch (IOException e) {
            System.err.println("Warning: Could not save demo tasks: " + e.getMessage());
        }
    }
    
    /**
     * Main method for GUI testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new ModernGuiController();
        });
    }
} 