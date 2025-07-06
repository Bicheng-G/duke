package ui;

import storage.Storage;
import tasklist.TaskList;
import ui.Ui;
import parser.Parser;
import command.Command;
import exception.DukeException;
import tasklist.Todo;
import tasklist.Deadline;
import tasklist.Event;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Premium WhatsApp-style chat UI with sophisticated design and typography
 */
public class WhatsAppStyleChatUI extends JFrame {
    
    // Enhanced color palette with premium gradients
    private static final Color WHATSAPP_GREEN = new Color(37, 211, 102);
    private static final Color WHATSAPP_DARK_GREEN = new Color(0, 150, 136);
    private static final Color WHATSAPP_LIGHT_GREEN = new Color(220, 248, 198);
    private static final Color WHATSAPP_BLUE = new Color(52, 152, 219);
    private static final Color WHATSAPP_GRAY = new Color(242, 242, 242);
    private static final Color WHATSAPP_DARK_GRAY = new Color(128, 128, 128);
    private static final Color WHATSAPP_BACKGROUND = new Color(230, 221, 212);
    private static final Color MESSAGE_BACKGROUND = new Color(255, 255, 255);
    
    // Premium text colors for better readability
    private static final Color TEXT_PRIMARY = new Color(26, 26, 26);
    private static final Color TEXT_SECONDARY = new Color(115, 115, 115);
    private static final Color TEXT_TERTIARY = new Color(158, 158, 158);
    
    // Core components
    private TaskList taskList;
    private Storage storage;
    private Ui ui;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageInput;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel typingIndicator;
    private Timer typingTimer;
    
    // Premium fonts
    private Font robotoRegular;
    private Font robotoMedium;
    private Font robotoBold;
    
    private boolean isTyping = false;

    // Indicates whether demo tasks were loaded for this session
    private boolean demoTasksLoaded = false;

    public WhatsAppStyleChatUI() {
        initializePremiumFonts();
        initializeData();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupResponsiveLayout();
        showWelcomeAnimation();
        
        // Premium window setup – border-less and full-screen inside the CheerPJ canvas
        setTitle("Duke – Premium Task Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Remove native title bar (CheerpJ shows an extra blue bar otherwise)
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fill the available display area
        // Fallback minimum for environments where maximize isn't honoured
        setMinimumSize(new Dimension(380, 600));
        
        // Modern window appearance (for desktop environments)
        setBackground(Color.WHITE);
        
        pack(); // Layout preferred sizes first
        // Ensure we cover the whole display after packing (particularly important after setPreferredSize removal)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    private void initializePremiumFonts() {
        try {
            // Load Roboto font family from system or fallback to premium alternatives
            robotoRegular = loadPremiumFont("Roboto-Regular.ttf", Font.PLAIN, 15);
            robotoMedium = loadPremiumFont("Roboto-Medium.ttf", Font.PLAIN, 15);
            robotoBold = loadPremiumFont("Roboto-Bold.ttf", Font.BOLD, 15);
            
            // Premium fallback fonts if Roboto not available
            if (robotoRegular == null) {
                robotoRegular = new Font("SF Pro Text", Font.PLAIN, 15);
                if (!robotoRegular.getFamily().equals("SF Pro Text")) {
                    robotoRegular = new Font("Segoe UI", Font.PLAIN, 15);
                }
            }
            
            if (robotoMedium == null) {
                robotoMedium = new Font("SF Pro Text", Font.PLAIN, 15);
                if (!robotoMedium.getFamily().equals("SF Pro Text")) {
                    robotoMedium = new Font("Segoe UI", Font.PLAIN, 15);
                }
            }
            
            if (robotoBold == null) {
                robotoBold = new Font("SF Pro Text", Font.BOLD, 15);
                if (!robotoBold.getFamily().equals("SF Pro Text")) {
                    robotoBold = new Font("Segoe UI", Font.BOLD, 15);
                }
            }
            
        } catch (Exception e) {
            // Fallback to system fonts
            robotoRegular = new Font("Segoe UI", Font.PLAIN, 15);
            robotoMedium = new Font("Segoe UI", Font.PLAIN, 15);
            robotoBold = new Font("Segoe UI", Font.BOLD, 15);
        }
    }
    
    private Font loadPremiumFont(String fontName, int style, int size) {
        try {
            InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/" + fontName);
            if (fontStream != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                return font.deriveFont(style, size);
            }
        } catch (Exception e) {
            // Silently fall back to system fonts
        }
        return null;
    }

    private void initializeData() {
        try {
            storage = new Storage("src/data/tasks.txt");
            taskList = new TaskList(storage.readFromFile());
            ui = new Ui();

            // If no tasks yet, create quick-start demo tasks
            if (taskList.size() == 0) {
                loadDemoTasks();
                try {
                    storage.writeToFile(taskList.getTasks());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (Exception e) {
            taskList = new TaskList();
            ui = new Ui();

            // Storage file does not exist or unreadable – start with demo tasks
            loadDemoTasks();
            try {
                // Attempt to persist demo tasks for next launch
                storage = new Storage("src/data/tasks.txt");
                storage.writeToFile(taskList.getTasks());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * Adds three simple demo tasks so new users have something to play with.
     */
    private void loadDemoTasks() {
        // 1. Simple todo
        taskList.addTask(new Todo("Buy groceries"));

        // 2. Deadline due next Monday 5 pm
        LocalDateTime nextMonday = LocalDateTime.now().plusDays((8 - LocalDateTime.now().getDayOfWeek().getValue()) % 7);
        nextMonday = nextMonday.withHour(17).withMinute(0);
        taskList.addTask(new Deadline("Submit assignment", nextMonday));

        // 3. Event tomorrow at 2 pm
        LocalDateTime tomorrowMeeting = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        taskList.addTask(new Event("Team meeting", tomorrowMeeting));

        demoTasksLoaded = true;
    }

    private void initializeComponents() {
        // Premium chat panel with sophisticated background handling
        chatPanel = new JPanel() {
            private BufferedImage backgroundImage;
            private boolean backgroundLoaded = false;
            
            {
                loadBackgroundImage();
            }
            
            private void loadBackgroundImage() {
                try {
                    // Load and optimize background image
                    ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("images/whatsapp_bg.jpg"));
                    if (icon.getIconWidth() > 0) {
                        // Create high-quality scaled version
                        Image img = icon.getImage();
                        backgroundImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = backgroundImage.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.drawImage(img, 0, 0, 400, 400, null);
                        g2d.dispose();
                        backgroundLoaded = true;
                    }
                } catch (Exception e) {
                    backgroundLoaded = false;
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                if (backgroundLoaded && backgroundImage != null) {
                    // High-quality tiling without blur
                    int imgWidth = backgroundImage.getWidth();
                    int imgHeight = backgroundImage.getHeight();
                    
                    for (int x = 0; x < getWidth(); x += imgWidth) {
                        for (int y = 0; y < getHeight(); y += imgHeight) {
                            g2d.drawImage(backgroundImage, x, y, imgWidth, imgHeight, null);
                        }
                    }
                } else {
                    // Premium fallback pattern
                    g2d.setColor(WHATSAPP_BACKGROUND);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Sophisticated dot pattern
                    g2d.setColor(new Color(255, 255, 255, 15));
                    for (int i = 0; i < getWidth(); i += 40) {
                        for (int j = 0; j < getHeight(); j += 40) {
                            g2d.fillOval(i, j, 2, 2);
                        }
                    }
                }
                
                // Premium overlay for better message visibility
                g2d.setColor(new Color(255, 255, 255, 4));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setOpaque(false);
        chatPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        
        // Premium scroll pane with modern design
        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setBorder(null);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatScrollPane.setOpaque(false);
        chatScrollPane.getViewport().setOpaque(false);
        
        // Ultra-modern scrollbar design
        chatScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200, 120);
                this.trackColor = new Color(255, 255, 255, 0);
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(thumbColor);
                g2d.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, 
                    thumbBounds.width - 4, thumbBounds.height - 4, 12, 12);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Invisible track for modern look
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createInvisibleButton();
            }
            
            private JButton createInvisibleButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        // Premium message input with superior styling
        messageInput = new JTextField() {
            private boolean hasFocus = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Premium background with subtle depth
                Color bgColor = hasFocus ? Color.WHITE : new Color(252, 252, 252);
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 28, 28));
                
                // Premium focus ring
                if (hasFocus) {
                    g2d.setColor(new Color(37, 211, 102, 80));
                    g2d.setStroke(new BasicStroke(2.5f));
                    g2d.draw(new RoundRectangle2D.Double(1.5, 1.5, getWidth() - 3, getHeight() - 3, 26, 26));
                } else {
                    g2d.setColor(new Color(220, 220, 220, 100));
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 2, getHeight() - 2, 27, 27));
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            @Override
            protected void processFocusEvent(FocusEvent e) {
                super.processFocusEvent(e);
                hasFocus = (e.getID() == FocusEvent.FOCUS_GAINED);
                repaint();
            }
        };
        
        messageInput.setFont(robotoRegular);
        messageInput.setBorder(new EmptyBorder(16, 20, 16, 60));
        messageInput.setBackground(Color.WHITE);
        messageInput.setForeground(TEXT_PRIMARY);
        messageInput.setOpaque(false);
        messageInput.setCaretColor(WHATSAPP_GREEN);
        messageInput.setSelectionColor(new Color(37, 211, 102, 80));
        
        // Premium placeholder functionality
        addPremiumPlaceholder();
        
        // Ultra-premium send button
        sendButton = new JButton("📤") {
            private boolean isHovered = false;
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Premium button states
                Color buttonColor;
                if (isPressed) {
                    buttonColor = new Color(25, 180, 85);
                } else if (isHovered) {
                    buttonColor = new Color(45, 220, 110);
                } else {
                    buttonColor = WHATSAPP_GREEN;
                }
                
                // Premium gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, buttonColor,
                    0, getHeight(), new Color(
                        Math.max(0, buttonColor.getRed() - 15),
                        Math.max(0, buttonColor.getGreen() - 15),
                        Math.max(0, buttonColor.getBlue() - 15)
                    )
                );
                g2d.setPaint(gradient);
                g2d.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                
                // Premium highlight
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                
                // Premium emoji rendering
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Apple Color Emoji", Font.PLAIN, 20));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2d.drawString(getText(), 
                    (getWidth() - textWidth) / 2, 
                    (getHeight() + textHeight) / 2 - 2);
                
                g2d.dispose();
            }
            
            @Override
            protected void processMouseEvent(MouseEvent e) {
                super.processMouseEvent(e);
                if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                    isHovered = true;
                } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                    isHovered = false;
                } else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
                    isPressed = true;
                } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                    isPressed = false;
                }
                repaint();
            }
        };
        
        sendButton.setFont(new Font("Apple Color Emoji", Font.PLAIN, 20));
        sendButton.setPreferredSize(new Dimension(48, 48));
        sendButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        
        // Premium status elements
        statusLabel = new JLabel("Online");
        statusLabel.setFont(robotoRegular.deriveFont(12f));
        statusLabel.setForeground(new Color(255, 255, 255, 180));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        typingIndicator = new JLabel("Duke is typing...");
        typingIndicator.setFont(robotoRegular.deriveFont(Font.ITALIC, 12f));
        typingIndicator.setForeground(TEXT_SECONDARY);
        typingIndicator.setVisible(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Premium header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(WHATSAPP_DARK_GREEN);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Duke - Task Assistant");
        titleLabel.setFont(robotoBold.deriveFont(18f));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        // Premium input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new OverlayLayout(inputPanel));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create a panel for the text input
        JPanel textInputPanel = new JPanel(new BorderLayout());
        textInputPanel.setOpaque(false);
        textInputPanel.add(messageInput, BorderLayout.CENTER);
        
        // Create a panel for the send button positioned on the right
        JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        sendButtonPanel.setOpaque(false);
        sendButtonPanel.add(sendButton);
        
        // Add both panels to the overlay
        inputPanel.add(textInputPanel);
        inputPanel.add(sendButtonPanel);
        
        // Premium footer panel with typing indicator
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(WHATSAPP_GRAY);
        footerPanel.setBorder(new EmptyBorder(5, 20, 5, 20));
        footerPanel.add(typingIndicator, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
        
        sendButton.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);
        
        // Real-time typing indicator
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
            
            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }
    
    private void setupResponsiveLayout() {
        // Add component listener for window resize events
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Trigger layout update for all message bubbles
                SwingUtilities.invokeLater(() -> {
                    updateMessageBubbleLayout();
                    chatPanel.revalidate();
                    chatPanel.repaint();
                });
            }
        });
        
        // Add hierarchy listener to handle dynamic content changes
        chatPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                SwingUtilities.invokeLater(() -> {
                    updateMessageBubbleLayout();
                });
            }
        });
    }
    
    private void updateMessageBubbleLayout() {
        // Force layout update for all message components
        Component[] components = chatPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                updatePanelLayout(panel);
            }
        }
    }
    
    private void updatePanelLayout(JPanel panel) {
        // Recursively update layout for nested panels
        panel.doLayout();
        Component[] components = panel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updatePanelLayout((JPanel) comp);
            }
        }
    }
    
    private void sendMessage() {
        String message = messageInput.getText().trim();
        
        // Don't send if empty or if it's the placeholder text
        if (message.isEmpty() || message.equals("Type a message...")) {
            return;
        }
        
        System.out.println("Sending message: " + message); // Debug output
        
        // Premium message cleaning - remove trailing spaces and normalize
        message = cleanMessageText(message);
        
        // Add user message
        addUserMessage(message);
        
        // Reset input field to placeholder
        messageInput.setText("Type a message...");
        messageInput.setForeground(new Color(160, 160, 160));
        
        // Request focus back to input field for continuous typing
        SwingUtilities.invokeLater(() -> messageInput.requestFocusInWindow());
        
        // Show typing indicator
        showTypingIndicator();
        
        // Process message and respond
        processMessage(message);
    }
    
    private String cleanMessageText(String message) {
        if (message == null) {
            return "";
        }

        // Normalise line endings to "\n"
        message = message.replace("\r\n", "\n");

        // Split into individual lines so we can tidy spaces without losing deliberate line breaks
        String[] lines = message.split("\n", -1); // keep empty trailing lines
        StringBuilder cleanedBuilder = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Trim leading/trailing whitespace in the line
            line = line.trim();

            // Collapse consecutive spaces / tabs inside the line (but NOT newlines)
            line = line.replaceAll("[ \t\f]+", " ");

            // Remove any invisible/control chars except allowed newline and tab
            line = line.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "");

            // Ensure proper spacing around common punctuation inside the line
            line = line.replaceAll("\\s*\\.\\s*", ". ");
            line = line.replaceAll("\\s*\\?\\s*", "? ");
            line = line.replaceAll("\\s*!\\s*", "! ");

            cleanedBuilder.append(line.trim());
            if (i < lines.length - 1) {
                cleanedBuilder.append("\n");
            }
        }

        // Remove superfluous multiple blank lines (collapse 3+ to 2) for neatness
        String cleaned = cleanedBuilder.toString().replaceAll("\n{3,}", "\n\n");

        return cleaned.trim();
    }
    
    private void addUserMessage(String message) {
        JPanel messagePanel = createPremiumMessageBubble(message, true);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(8));
        
        // Lightweight refresh – only revalidate to avoid full repaint of every bubble
        chatPanel.revalidate();
        // chatPanel.repaint(); // Let Swing decide when to repaint to reduce needless redraws
        scrollToBottomSmoothly();
    }
    
    private void addDukeMessage(String message) {
        JPanel messagePanel = createPremiumMessageBubble(message, false);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(8));
        
        // Lightweight refresh – only revalidate to avoid full repaint of every bubble
        chatPanel.revalidate();
        // chatPanel.repaint(); // Let Swing decide when to repaint to reduce needless redraws
        scrollToBottomSmoothly();
    }
    
    private JPanel createPremiumMessageBubble(String message, boolean isUser) {
        JPanel bubblePanel = new JPanel(new BorderLayout());
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(new EmptyBorder(8, 0, 8, 0)); // Premium spacing
        
        // Create the premium bubble with sophisticated styling
        JPanel bubble = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                
                int width = getWidth();
                int height = getHeight();
                int arc = 20; // Premium corner radius
                
                // Premium multi-layer shadow system
                g2d.setColor(new Color(0, 0, 0, 6));
                g2d.fill(new RoundRectangle2D.Double(4, 4, width - 4, height - 4, arc, arc));
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fill(new RoundRectangle2D.Double(3, 3, width - 3, height - 3, arc, arc));
                g2d.setColor(new Color(0, 0, 0, 14));
                g2d.fill(new RoundRectangle2D.Double(2, 2, width - 2, height - 2, arc, arc));
                g2d.setColor(new Color(0, 0, 0, 18));
                g2d.fill(new RoundRectangle2D.Double(1, 1, width - 1, height - 1, arc, arc));
                
                // Premium gradient backgrounds
                if (isUser) {
                    // User messages with sophisticated green gradient
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 255, 220), 
                        0, height, new Color(215, 245, 195)
                    );
                    g2d.setPaint(gradient);
                } else {
                    // Duke messages with premium white gradient
                    GradientPaint gradient = new GradientPaint(
                        0, 0, Color.WHITE,
                        0, height, new Color(252, 252, 252)
                    );
                    g2d.setPaint(gradient);
                }
                
                g2d.fill(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, arc, arc));
                
                // Premium border with subtle depth
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new RoundRectangle2D.Double(0.5, 0.5, width - 2, height - 2, arc - 1, arc - 1));
                
                // Premium message tail with enhanced design
                if (isUser) {
                    // Right-pointing tail for user messages
                    g2d.setColor(new Color(215, 245, 195));
                    int[] xPoints = {width - 2, width + 10, width - 2};
                    int[] yPoints = {height - 28, height - 20, height - 12};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    
                    // Tail border
                    g2d.setColor(new Color(0, 0, 0, 8));
                    g2d.setStroke(new BasicStroke(0.8f));
                    g2d.drawPolygon(xPoints, yPoints, 3);
                } else {
                    // Left-pointing tail for Duke messages
                    g2d.setColor(new Color(252, 252, 252));
                    int[] xPoints = {2, -10, 2};
                    int[] yPoints = {height - 28, height - 20, height - 12};
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    
                    // Tail border
                    g2d.setColor(new Color(0, 0, 0, 8));
                    g2d.setStroke(new BasicStroke(0.8f));
                    g2d.drawPolygon(xPoints, yPoints, 3);
                }
                
                g2d.dispose();
            }
        };
        
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(16, 20, 16, 20)); // Premium internal padding
        
        // Dynamic, responsive message text with superior typography
        JTextArea messageText = new JTextArea(cleanMessageText(message)) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // Premium text rendering
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        
        messageText.setFont(robotoRegular.deriveFont(15f));
        messageText.setForeground(TEXT_PRIMARY);
        messageText.setOpaque(false);
        messageText.setEditable(false);
        messageText.setWrapStyleWord(true);
        messageText.setLineWrap(true);
        messageText.setFocusable(false);
        
        // Premium line spacing for optimal readability
        messageText.putClientProperty("lineSpacing", 1.2f);
        
        // Premium timestamp with enhanced styling
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        JLabel timeLabel = new JLabel(timestamp);
        timeLabel.setFont(robotoRegular.deriveFont(11f));
        timeLabel.setForeground(TEXT_TERTIARY);
        timeLabel.setHorizontalAlignment(isUser ? SwingConstants.RIGHT : SwingConstants.LEFT);
        timeLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
        
        // Premium read indicators for user messages
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setOpaque(false);
        timePanel.add(timeLabel, BorderLayout.CENTER);
        
        if (isUser) {
            JLabel readIndicator = new JLabel("✓✓");
            readIndicator.setFont(robotoMedium.deriveFont(11f));
            readIndicator.setForeground(new Color(79, 188, 233)); // Premium blue
            readIndicator.setBorder(new EmptyBorder(0, 6, 0, 0));
            timePanel.add(readIndicator, BorderLayout.EAST);
        }
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(messageText, BorderLayout.CENTER);
        textPanel.add(timePanel, BorderLayout.SOUTH);
        
        bubble.add(textPanel, BorderLayout.CENTER);
        
        // Premium styled avatar
        JPanel avatarPanel = createPremiumAvatar(isUser);
        
        // Dynamic, responsive layout with adaptive spacing
        if (isUser) {
            // User messages on the right with responsive spacing
            JPanel rightPanel = new JPanel(new BorderLayout()) {
                @Override
                public void doLayout() {
                    super.doLayout();
                    // Dynamic width calculation based on container size
                    int containerWidth = getParent() != null ? getParent().getWidth() : 400;
                    int availableWidth = containerWidth - 80; // Account for margins and avatar
                    int maxBubbleWidth = Math.min(Math.max(280, (int)(availableWidth * 0.75)), 450); // 75% max, but at least 280px, max 450px
                    int dynamicMargin = Math.max(20, containerWidth - maxBubbleWidth - 60); // Dynamic left margin
                    
                    // Update text area width dynamically
                    Component[] components = bubble.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            Component[] subComponents = panel.getComponents();
                            for (Component subComp : subComponents) {
                                if (subComp instanceof JTextArea) {
                                    JTextArea textArea = (JTextArea) subComp;
                                    int textWidth = maxBubbleWidth - 40; // Account for bubble padding
                                    textArea.setSize(new Dimension(textWidth, Integer.MAX_VALUE));
                                    int preferredHeight = Math.max(textArea.getPreferredSize().height, 24);
                                    textArea.setPreferredSize(new Dimension(textWidth, preferredHeight));
                                }
                            }
                        }
                    }
                    
                    setBorder(new EmptyBorder(0, dynamicMargin, 0, 12));
                }
            };
            rightPanel.setOpaque(false);
            rightPanel.add(bubble, BorderLayout.CENTER);
            
            bubblePanel.add(rightPanel, BorderLayout.CENTER);
            bubblePanel.add(avatarPanel, BorderLayout.EAST);
        } else {
            // Duke messages on the left with responsive spacing
            JPanel leftPanel = new JPanel(new BorderLayout()) {
                @Override
                public void doLayout() {
                    super.doLayout();
                    // Dynamic width calculation based on container size
                    int containerWidth = getParent() != null ? getParent().getWidth() : 400;
                    int availableWidth = containerWidth - 80; // Account for margins and avatar
                    int maxBubbleWidth = Math.min(Math.max(280, (int)(availableWidth * 0.85)), 500); // 85% max for Duke, larger than user
                    int dynamicMargin = Math.max(20, containerWidth - maxBubbleWidth - 60); // Dynamic right margin
                    
                    // Update text area width dynamically
                    Component[] components = bubble.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            Component[] subComponents = panel.getComponents();
                            for (Component subComp : subComponents) {
                                if (subComp instanceof JTextArea) {
                                    JTextArea textArea = (JTextArea) subComp;
                                    int textWidth = maxBubbleWidth - 40; // Account for bubble padding
                                    textArea.setSize(new Dimension(textWidth, Integer.MAX_VALUE));
                                    int preferredHeight = Math.max(textArea.getPreferredSize().height, 24);
                                    textArea.setPreferredSize(new Dimension(textWidth, preferredHeight));
                                }
                            }
                        }
                    }
                    
                    setBorder(new EmptyBorder(0, 12, 0, dynamicMargin));
                }
            };
            leftPanel.setOpaque(false);
            leftPanel.add(bubble, BorderLayout.CENTER);
            
            bubblePanel.add(avatarPanel, BorderLayout.WEST);
            bubblePanel.add(leftPanel, BorderLayout.CENTER);
        }
        
        return bubblePanel;
    }
    
    private JPanel createPremiumAvatar(boolean isUser) {
        JPanel avatarPanel = new JPanel() {
            private Image avatarImage;
            
            {
                try {
                    // Load premium avatar images
                    String imagePath = isUser ? "images/DaUser.png" : "images/DaDuke.png";
                    avatarImage = new ImageIcon(getClass().getClassLoader().getResource(imagePath)).getImage();
                } catch (Exception e) {
                    avatarImage = null;
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int size = Math.min(getWidth(), getHeight()) - 6; // Premium margin
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                if (avatarImage != null) {
                    // Premium circular clipping
                    g2d.setClip(new java.awt.geom.Ellipse2D.Double(x, y, size, size));
                    
                    // High-quality image rendering
                    g2d.drawImage(avatarImage, x, y, size, size, null);
                    
                    // Reset clip
                    g2d.setClip(null);
                } else {
                    // Premium fallback avatar
                    GradientPaint gradient = new GradientPaint(
                        x, y, isUser ? new Color(100, 150, 255) : new Color(150, 100, 255),
                        x + size, y + size, isUser ? new Color(80, 120, 200) : new Color(120, 80, 200)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(x, y, size, size);
                    
                    // Premium emoji rendering
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Apple Color Emoji", Font.PLAIN, size/2));
                    String emoji = isUser ? "👤" : "🤖";
                    FontMetrics fm = g2d.getFontMetrics();
                    int emojiWidth = fm.stringWidth(emoji);
                    int emojiHeight = fm.getAscent();
                    g2d.drawString(emoji, 
                        x + (size - emojiWidth) / 2, 
                        y + (size + emojiHeight) / 2 - 2);
                }
                
                // Premium avatar border
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawOval(x, y, size, size);
                
                g2d.dispose();
            }
        };
        
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(44, 44)); // Premium avatar size
        
        return avatarPanel;
    }
    
    private void showTypingIndicator() {
        typingIndicator.setVisible(true);
        isTyping = true;
        
        // Hide after 2 seconds using Swing Timer
        if (typingTimer != null) {
            typingTimer.stop();
        }
        typingTimer = new Timer(2000, e -> {
            typingIndicator.setVisible(false);
            isTyping = false;
        });
        typingTimer.setRepeats(false);
        typingTimer.start();
    }
    
    private void processMessage(String message) {
        // Simulate processing delay for realistic chat feel using Swing Timer
        Timer responseTimer = new Timer(1000 + (int)(Math.random() * 1000), e -> {
            String response = getDukeResponse(message);
            addDukeMessage(response);
        });
        responseTimer.setRepeats(false);
        responseTimer.start();
    }
    
    private String getDukeResponse(String input) {
        try {
            // Make input more natural
            String processedInput = preprocessInput(input);
            
            // Validate the processed input before passing to parser
            if (processedInput == null || processedInput.trim().isEmpty()) {
                return makeErrorNatural("Invalid input");
            }
            
            // Additional validation for common parsing issues
            if (!isValidCommandFormat(processedInput)) {
                return makeErrorNatural("Command format issue: " + processedInput);
            }
            
            Command command = Parser.parse(processedInput, taskList);
            
            // Capture Duke's response
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream originalOut = System.out;
            System.setOut(ps);
            
            command.execute(taskList, ui, storage);
            
            System.setOut(originalOut);
            String response = baos.toString().trim();
            
            // Make response more conversational
            return makeResponseNatural(response, input);
            
        } catch (DukeException | IOException e) {
            return makeErrorNatural(e.getMessage());
        } catch (Exception e) {
            // Catch any other unexpected errors
            return makeErrorNatural("Oops! Something went wrong. Please try rephrasing your request.");
        }
    }
    
    private boolean isValidCommandFormat(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = command.trim().split(" ", 2);
        String cmdType = parts[0].toLowerCase();
        
        // Check basic command structure
        switch (cmdType) {
            case "event":
                // Event must have description and /at
                return parts.length > 1 && parts[1].contains("/at") && 
                       !parts[1].startsWith("/at") && !parts[1].endsWith("/at");
            case "deadline":
                // Deadline must have description and /by
                return parts.length > 1 && parts[1].contains("/by") && 
                       !parts[1].startsWith("/by") && !parts[1].endsWith("/by");
            case "todo":
                // Todo just needs description
                return parts.length > 1 && !parts[1].trim().isEmpty();
            case "list":
            case "help":
            case "reset":
            case "bye":
                // These commands don't need additional parameters
                return true;
            case "done":
            case "delete":
            case "view":
                // These need exactly one parameter
                return parts.length > 1 && !parts[1].trim().isEmpty();
            case "search":
            case "edit":
                // These need at least one parameter
                return parts.length > 1 && !parts[1].trim().isEmpty();
            default:
                return false;
        }
    }
    
    private String preprocessInput(String input) {
        // Handle null, empty, or whitespace-only input
        if (input == null || input.trim().isEmpty()) {
            return "help"; // Show help for empty input
        }
        
        String processed = input.toLowerCase().trim();
        
        // Handle single character or very short inputs
        if (processed.length() <= 2) {
            return "help";
        }
        
        // Handle greetings and pleasantries
        if (processed.matches("(hi|hello|hey|good morning|good afternoon|good evening|thanks|thank you).*")) {
            return "help"; // Show help for greetings
        }
        
        // Detect questions - these should NOT become tasks
        if (isQuestion(processed)) {
            return handleQuestion(processed);
        }
        
        // Detect search/list requests 
        if (isSearchRequest(processed)) {
            return handleSearchRequest(input);
        }
        
        // Handle category-only inputs (like "#grocery")
        if (isCategoryOnly(processed)) {
            return "search " + input.trim();
        }
        
        // Direct commands that don't need preprocessing
        if (processed.startsWith("list") || processed.startsWith("help") || processed.startsWith("search") || 
            processed.startsWith("done") || processed.startsWith("delete") || processed.startsWith("edit") ||
            processed.startsWith("view") || processed.startsWith("reset") || processed.startsWith("bye")) {
            return input;
        }
        
        // Handle natural language task creation
        return processNaturalLanguageTask(input);
    }
    
    private boolean isQuestion(String input) {
        // Detect questions by question words and question marks
        return input.contains("?") ||
               input.startsWith("how ") ||
               input.startsWith("what ") ||
               input.startsWith("when ") ||
               input.startsWith("where ") ||
               input.startsWith("why ") ||
               input.startsWith("who ") ||
               input.startsWith("which ") ||
               input.contains("can you ") ||
               input.contains("could you ") ||
               input.contains("would you ") ||
               input.contains("do you ") ||
               input.contains("does this ") ||
               input.contains("is this ") ||
               input.contains("are there ");
    }
    
    private String handleQuestion(String input) {
        // Analyze the question and provide appropriate guidance
        if (input.contains("category") || input.contains("categories")) {
            return "help categories"; // Specific help about categories
        }
        if (input.contains("delete") || input.contains("remove")) {
            return "help delete";
        }
        if (input.contains("command") || input.contains("available")) {
            return "help";
        }
        if (input.contains("search") || input.contains("find")) {
            return "help search";
        }
        
        // Default to general help for other questions
        return "help";
    }
    
    private boolean isSearchRequest(String input) {
        // Detect search/list requests
        return input.startsWith("show ") ||
               input.startsWith("show me ") ||
               input.startsWith("find ") ||
               input.startsWith("find me ") ||
               input.startsWith("search ") ||
               input.startsWith("get ") ||
               input.startsWith("display ") ||
               input.startsWith("list all ") ||
               input.startsWith("see ") ||
               input.startsWith("view ") && !input.startsWith("view \\d") || // not "view 1", "view 2"
               input.contains("show me") ||
               input.contains("find all");
    }
    
    private String handleSearchRequest(String input) {
        String lowerInput = input.toLowerCase();
        
        // Extract the search term from common patterns
        String searchTerm = "";
        
        if (lowerInput.startsWith("show me ")) {
            searchTerm = input.substring(8).trim(); // Remove "show me "
        } else if (lowerInput.startsWith("show ")) {
            searchTerm = input.substring(5).trim(); // Remove "show "
        } else if (lowerInput.startsWith("find ")) {
            searchTerm = input.substring(5).trim(); // Remove "find "
        } else if (lowerInput.startsWith("search ")) {
            searchTerm = input.substring(7).trim(); // Remove "search "
        } else if (lowerInput.startsWith("get ")) {
            searchTerm = input.substring(4).trim(); // Remove "get "
        } else if (lowerInput.startsWith("list all ")) {
            searchTerm = input.substring(9).trim(); // Remove "list all "
        } else {
            // Default extraction - take everything after the first word
            String[] parts = input.split(" ", 2);
            if (parts.length > 1) {
                searchTerm = parts[1].trim();
            }
        }
        
        // Clean up common words
        searchTerm = searchTerm.replaceAll("\\b(tasks?|items?|things?)\\b", "").trim();
        
        if (searchTerm.isEmpty()) {
            return "list"; // Show all tasks if no specific search term
        }
        
        return "search " + searchTerm;
    }
    
    private boolean isCategoryOnly(String input) {
        // Check if input is just a category tag like "#grocery" or "@home"
        return input.matches("^[#@]\\w+$");
    }
    
    private String processNaturalLanguageTask(String input) {
        String lowerInput = input.toLowerCase().trim();
        
        // Handle repeated words or nonsensical input
        if (isRepeatedOrNonsensical(lowerInput)) {
            return "help";
        }
        
        // Check for event indicators (meeting, appointment, event, etc.)
        if (containsEventKeywords(lowerInput)) {
            return processEventCommand(input);
        }
        
        // Check for deadline indicators (deadline, due, by, etc.)
        if (containsDeadlineKeywords(lowerInput)) {
            return processDeadlineCommand(input);
        }
        
        // Check if it looks like a task (contains action words or starts with task verbs)
        if (looksLikeTask(lowerInput)) {
            return processTodoCommand(input);
        }
        
        // If it doesn't clearly look like a task, ask for clarification
        return "help";
    }
    
    private boolean looksLikeTask(String input) {
        // Check for vague statements first - these should NOT be tasks
        if (isVagueStatement(input)) {
            return false;
        }
        
        // Check for task-indicating patterns
        
        // Starts with task verbs
        if (input.startsWith("add ") || input.startsWith("create ") || input.startsWith("new ") ||
            input.startsWith("todo ") || input.startsWith("make ") || input.startsWith("do ") ||
            input.startsWith("need to ") || input.startsWith("remember to ") || 
            input.startsWith("should ") || input.startsWith("must ")) {
            return true;
        }
        
        // Contains priority or category tags (strong indicator of task)
        if (input.contains("!") || input.contains("#") || input.contains("@")) {
            return true;
        }
        
        // Contains action verbs - expanded list
        String[] actionVerbs = {
            "buy", "call", "write", "read", "send", "book", "pay", "clean", "wash", 
            "fix", "repair", "build", "cook", "eat", "visit", "go", "walk", "run",
            "study", "learn", "practice", "exercise", "work", "plan", "organize",
            "finish", "complete", "submit", "deliver", "prepare", "setup", "install",
            "update", "download", "upload", "backup", "sync", "check", "review",
            "schedule", "cancel", "reschedule", "confirm", "order", "purchase",
            "print", "scan", "copy", "move", "transfer", "delete", "remove",
            "start", "begin", "end", "stop", "pause", "resume", "continue",
            "meet", "contact", "email", "text", "message", "reply", "respond"
        };
        
        for (String verb : actionVerbs) {
            // Check if the verb appears as a standalone word
            if (input.matches(".*\\b" + verb + "\\b.*")) {
                return true;
            }
        }
        
        // Contains task-related nouns
        String[] taskNouns = {
            "homework", "assignment", "project", "report", "paper", "essay",
            "presentation", "meeting", "appointment", "call", "email", "message",
            "groceries", "shopping", "laundry", "dishes", "cleaning", "workout"
        };
        
        for (String noun : taskNouns) {
            if (input.contains(noun)) {
                return true;
            }
        }
        
        // Short, imperative-style phrases (but not questions or vague statements)
        String[] words = input.split("\\s+");
        if (words.length <= 5 && !input.contains("?")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isVagueStatement(String input) {
        // Check for vague/uncertain language that shouldn't become tasks
        String[] vagueIndicators = {
            "maybe", "perhaps", "possibly", "probably", "might", "could be",
            "think about", "consider", "random", "arbitrary", "some", "any",
            "this is", "that is", "it is", "just", "only", "simply",
            "looks like", "seems like", "appears to be", "something like",
            "kind of", "sort of", "i guess", "i think", "i suppose",
            "nothing", "anything", "everything", "something"
        };
        
        String lowerInput = input.toLowerCase();
        
        for (String indicator : vagueIndicators) {
            if (lowerInput.contains(indicator)) {
                return true;
            }
        }
        
        // Check for sentence patterns that are typically not tasks
        if (lowerInput.startsWith("i ") || lowerInput.startsWith("this ") || 
            lowerInput.startsWith("that ") || lowerInput.startsWith("it ") ||
            lowerInput.startsWith("there ") || lowerInput.startsWith("here ") ||
            lowerInput.startsWith("some ") || lowerInput.startsWith("any ") ||
            lowerInput.startsWith("maybe ") || lowerInput.startsWith("perhaps ")) {
            return true;
        }
        
        // Check for patterns that sound like descriptions rather than tasks
        if (lowerInput.contains("text that") || lowerInput.contains("sentence") ||
            lowerInput.contains("statement") || lowerInput.contains("phrase") ||
            lowerInput.contains("words") || lowerInput.contains("example")) {
            return true;
        }
        
        // Check for overly generic or nonsensical combinations
        if (lowerInput.contains("random") && lowerInput.contains("text")) {
            return true;
        }
        
        if (lowerInput.contains("arbitrary") && lowerInput.contains("sentence")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isRepeatedOrNonsensical(String input) {
        // Check for repeated single words
        String[] words = input.split("\\s+");
        if (words.length >= 3) {
            boolean allSame = true;
            for (int i = 1; i < words.length; i++) {
                if (!words[i].equals(words[0])) {
                    allSame = false;
                    break;
                }
            }
            if (allSame) {
                return true;
            }
        }
        
        // Check for single word inputs that are command words
        if (words.length == 1 && 
            (words[0].equals("add") || words[0].equals("create") || words[0].equals("new") ||
             words[0].equals("todo") || words[0].equals("event") || words[0].equals("deadline") ||
             words[0].equals("meeting") || words[0].equals("appointment"))) {
            return true;
        }
        
        return false;
    }
    
    private boolean containsEventKeywords(String input) {
        String[] eventKeywords = {
            "meeting", "appointment", "event", "conference", "interview", 
            "lunch", "dinner", "visit", "session", "class", "lecture"
        };
        for (String keyword : eventKeywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        
        // Special case: "call" is only an event if it has time indicators
        if (input.contains("call") && hasTimeIndicators(input)) {
            return true;
        }
        
        return false;
    }
    
    private boolean containsDeadlineKeywords(String input) {
        // More sophisticated deadline detection
        // Only consider it a deadline if it has BOTH deadline words AND time indicators
        
        boolean hasDeadlineWord = false;
        boolean hasTimeIndicator = false;
        
        // Check for deadline words
        if (input.matches(".*\\bdeadline\\b.*") || 
            input.matches(".*\\bdue\\b.*") || 
            input.matches(".*\\bby\\b.*")) {
            hasDeadlineWord = true;
        }
        
        // Check for time indicators
        String[] timeIndicators = {
            "tomorrow", "today", "tonight", "yesterday",
            "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
            "next week", "this week", "next month", "this month",
            "am", "pm", "o'clock", "noon", "midnight",
            "\\b\\d{1,2}:\\d{2}\\b", "\\b\\d{1,2}pm\\b", "\\b\\d{1,2}am\\b",
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        };
        
        for (String indicator : timeIndicators) {
            if (indicator.contains("\\b") || indicator.contains("\\d")) {
                // Handle regex patterns
                if (java.util.regex.Pattern.compile(indicator).matcher(input.toLowerCase()).find()) {
                    hasTimeIndicator = true;
                    break;
                }
            } else {
                // Handle simple string matching
                if (input.toLowerCase().contains(indicator)) {
                    hasTimeIndicator = true;
                    break;
                }
            }
        }
        
        // For explicit deadline commands, we're more lenient
        if (input.toLowerCase().startsWith("deadline ")) {
            return true;
        }
        
        // For "submit", "finish", "complete" - only if they have time indicators
        if (input.matches(".*\\b(submit|finish|complete)\\b.*")) {
            return hasTimeIndicator;
        }
        
        // For other cases, require both deadline word and time indicator
        return hasDeadlineWord && hasTimeIndicator;
    }
    
    private String processEventCommand(String input) {
        String lowerInput = input.toLowerCase().trim();
        
        // Remove command words like "add", "create", "new", "event"
        String cleanInput = removeCommandWords(input);
        
        // Handle edge case: if clean input is empty or just the event keyword
        if (cleanInput.trim().isEmpty() || 
            cleanInput.toLowerCase().trim().equals("meeting") ||
            cleanInput.toLowerCase().trim().equals("appointment") ||
            cleanInput.toLowerCase().trim().equals("event")) {
            return "help"; // Ask for more information
        }
        
        // Special handling for "call" - if no time is specified, make it a todo
        if (cleanInput.toLowerCase().startsWith("call") && !hasTimeIndicators(cleanInput)) {
            return "todo " + cleanInput;
        }
        
        // Look for time indicators
        String[] timeIndicators = {"at", "on", "tomorrow", "today", "monday", "tuesday", "wednesday", 
                                  "thursday", "friday", "saturday", "sunday", "next", "this", 
                                  "am", "pm", ":", "\\d+pm", "\\d+am"};
        
        int timeStartIndex = findTimeStartIndex(cleanInput, timeIndicators);
        
        if (timeStartIndex != -1) {
            String description = cleanInput.substring(0, timeStartIndex).trim();
            String timepart = cleanInput.substring(timeStartIndex).trim();
            
            // Remove "at" or "on" if it's at the beginning of timepart
            timepart = timepart.replaceFirst("^(at|on)\\s+", "");
            
            // Validate that we have both description and time
            if (description.isEmpty() || timepart.isEmpty()) {
                return "todo " + cleanInput; // Fallback to todo
            }
            
            return "event " + description + " /at " + timepart;
        } else {
            // No clear time found, check if it looks like an event or should be a todo
            if (shouldBeEvent(cleanInput)) {
                return "help"; // Ask for time instead of creating incomplete event
            } else {
                return "todo " + cleanInput;
            }
        }
    }
    
    private boolean hasTimeIndicators(String input) {
        String lowerInput = input.toLowerCase();
        String[] timeWords = {"at", "on", "tomorrow", "today", "monday", "tuesday", "wednesday", 
                             "thursday", "friday", "saturday", "sunday", "next", "this", 
                             "am", "pm"};
        
        for (String timeWord : timeWords) {
            if (lowerInput.contains(timeWord)) {
                return true;
            }
        }
        
        // Check for time patterns like "2pm", "3:30", etc.
        return lowerInput.matches(".*\\d+[ap]m.*") || lowerInput.matches(".*\\d+:\\d+.*");
    }
    
    private boolean shouldBeEvent(String input) {
        String lowerInput = input.toLowerCase();
        // Only treat as event if it contains typical event words but no time
        String[] strongEventWords = {"meeting", "appointment", "conference", "interview", "session"};
        
        for (String eventWord : strongEventWords) {
            if (lowerInput.contains(eventWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    private String processDeadlineCommand(String input) {
        String cleanInput = removeCommandWords(input);
        
        // Handle edge case: if clean input is empty or just the deadline keyword
        if (cleanInput.trim().isEmpty() || 
            cleanInput.toLowerCase().trim().equals("deadline")) {
            return "help"; // Ask for more information
        }
        
        // Special handling for "due [task] due [time]" pattern
        if (cleanInput.toLowerCase().contains("due")) {
            String[] parts = cleanInput.toLowerCase().split("\\bdue\\b");
            if (parts.length >= 3) {
                // Multiple "due" keywords - extract the task and time properly
                String description = parts[1].trim();
                String timepart = parts[2].trim();
                
                if (!description.isEmpty() && !timepart.isEmpty()) {
                    return "deadline " + description + " /by " + timepart;
                }
            } else if (parts.length == 2) {
                // Single "due" keyword
                String beforeDue = parts[0].trim();
                String afterDue = parts[1].trim();
                
                if (!beforeDue.isEmpty() && !afterDue.isEmpty()) {
                    return "deadline " + beforeDue + " /by " + afterDue;
                } else if (!afterDue.isEmpty()) {
                    return "deadline " + afterDue + " /by ";
                }
            }
        }
        
        // Look for "by" or deadline time indicators with word boundaries
        String[] deadlineIndicators = {"\\bby\\b", "\\bdeadline\\b", "\\bbefore\\b", "\\buntil\\b", 
                                      "tomorrow", "today", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        
        int timeStartIndex = findTimeStartIndex(cleanInput, deadlineIndicators);
        
        if (timeStartIndex != -1) {
            String description = cleanInput.substring(0, timeStartIndex).trim();
            String timepart = cleanInput.substring(timeStartIndex).trim();
            
            // Remove "by", "due", or "deadline" if it's at the beginning of timepart
            timepart = timepart.replaceFirst("^(by|due|deadline)\\s+", "");
            
            // Validate that we have both description and time
            if (description.isEmpty() || timepart.isEmpty()) {
                return "todo " + cleanInput; // Fallback to todo
            }
            
            return "deadline " + description + " /by " + timepart;
        } else {
            // No clear time found, check if it's really a deadline or should be a todo
            if (shouldBeDeadline(cleanInput)) {
                return "help"; // Ask for time instead of creating incomplete deadline
            } else {
                return "todo " + cleanInput;
            }
        }
    }
    
    private boolean shouldBeDeadline(String input) {
        String lowerInput = input.toLowerCase();
        // Only treat as deadline if it contains typical deadline words
        String[] strongDeadlineWords = {"submit", "finish", "complete", "turn in", "hand in"};
        
        for (String deadlineWord : strongDeadlineWords) {
            if (lowerInput.contains(deadlineWord)) {
                return true;
            }
        }
        
        return false;
    }
    
    private String processTodoCommand(String input) {
        String cleanInput = removeCommandWords(input);
        return "todo " + cleanInput;
    }
    
    private String removeCommandWords(String input) {
        String result = input.trim();
        
        // Remove common command words from the beginning
        String[] commandWords = {"add", "create", "new", "todo", "event", "deadline", "make", "schedule"};
        
        for (String command : commandWords) {
            if (result.toLowerCase().startsWith(command + " ")) {
                result = result.substring(command.length()).trim();
                break;
            }
        }
        
        return result;
    }
    
    private int findTimeStartIndex(String input, String[] indicators) {
        String lowerInput = input.toLowerCase();
        int earliestIndex = Integer.MAX_VALUE;
        
        for (String indicator : indicators) {
            int index;
            if (indicator.startsWith("\\b") && indicator.endsWith("\\b")) {
                // Handle word boundary patterns
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(indicator);
                java.util.regex.Matcher matcher = pattern.matcher(lowerInput);
                if (matcher.find()) {
                    index = matcher.start();
                    if (index < earliestIndex && index > 0) { // Don't start at beginning
                        earliestIndex = index;
                    }
                }
            } else if (indicator.contains("\\d")) {
                // Handle regex patterns like "\\d+pm"
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(indicator);
                java.util.regex.Matcher matcher = pattern.matcher(lowerInput);
                if (matcher.find()) {
                    index = matcher.start();
                    if (index < earliestIndex && index > 0) { // Don't start at beginning
                        earliestIndex = index;
                    }
                }
            } else {
                // Handle simple string matching
                index = lowerInput.indexOf(indicator);
                if (index != -1 && index < earliestIndex && index > 0) { // Don't start at beginning
                    earliestIndex = index;
                }
            }
        }
        
        return earliestIndex == Integer.MAX_VALUE ? -1 : earliestIndex;
    }
    
    private String makeResponseNatural(String response, String originalInput) {
        if (response.isEmpty()) {
            return "🤔 I'm not quite sure what you mean there!\n\n✨ Here are some things you can try:\n• \"Add [task name]\" - to create a new task\n• \"Show my tasks\" - to see your current list\n• \"Help\" - to see all available commands\n\nWhat would you like to do? 😊";
        }
        
        // Handle greetings and help requests
        if (originalInput.toLowerCase().matches("(hi|hello|hey|good morning|good afternoon|good evening|thanks|thank you).*") ||
            originalInput.toLowerCase().contains("help")) {
            return "🤖 Hey there! I'm here to make task management super easy for you!\n\n" + 
                   "📝 **Quick Start Guide:**\n" +
                   "• Say \"Add [task]\" to create tasks\n" +
                   "• Try \"Show my tasks\" to see your list\n" +
                   "• Use \"Done [number]\" to complete tasks\n" +
                   "• Say \"Delete [number]\" to remove tasks\n\n" +
                   "🎯 **Advanced Features:**\n" +
                   "• Add priorities: \"Add report !urgent\"\n" +
                   "• Set deadlines: \"Deadline project by Friday\"\n" +
                   "• Add categories: \"Add workout #health\"\n" +
                   "• Create events: \"Meeting tomorrow at 2pm\"\n\n" +
                   "💬 Just talk to me naturally - I'll understand! What can I help you with first?";
        }
        
        // Handle different types of responses with contextual guidance
        if (response.contains("added") || response.contains("Got it")) {
            String[] successPhrases = {
                "Perfect! ✅ ", "Awesome! ✅ ", "Got it! ✅ ", "Done! ✅ ", "All set! ✅ "
            };
            String prefix = successPhrases[(int)(Math.random() * successPhrases.length)];
            
            // Count current tasks for contextual advice
            int taskCount = taskList.size();
            String advice = "";
            if (taskCount <= 3) {
                advice = "\n\n💡 Want to add more details? Try:\n• \"Add task !high\" (for priority)\n• \"Add task #work\" (for categories)\n• \"Deadline task by Friday\" (for deadlines)";
            } else if (taskCount >= 5) {
                advice = "\n\n📋 Your list is growing! Try \"Show my tasks\" to see everything, or use \"Search work\" to find specific tasks.";
            }
            
            return prefix + response + advice;
        }
        
        if (response.contains("tasks in the list") || response.contains("Here are the tasks")) {
            if (response.contains("no tasks")) {
                return "🎉 Your task list is completely clear! \n\n" + 
                       "Ready to add your first task? Here are some examples:\n" +
                       "• \"Add buy groceries\"\n" +
                       "• \"Deadline submit report by Friday\"\n" +
                       "• \"Meeting with team tomorrow at 2pm\"\n" +
                       "• \"Add workout #health !high\"\n\n" +
                       "What would you like to add first? 😊";
            } else {
                int taskCount = taskList.size();
                String plural = taskCount == 1 ? "task" : "tasks";
                return "📋 Here are your " + taskCount + " " + plural + ":\n\n" + response + 
                       "\n\n💡 **Quick actions:**\n" +
                       "• \"Done [number]\" to mark complete\n" +
                       "• \"Delete [number]\" to remove\n" +
                       "• \"Edit [number] [new text]\" to modify\n" +
                       "• \"Search [keyword]\" to find specific tasks\n\n" +
                       "Which task would you like to work on? 🎯";
            }
        }
        
        if (response.contains("marked as done") || response.contains("Nice!")) {
            String[] celebrationPhrases = {
                "🎉 Fantastic work! ", "🎊 Well done! ", "✨ Great job! ", "🎯 Excellent! ", "🚀 Amazing! "
            };
            String prefix = celebrationPhrases[(int)(Math.random() * celebrationPhrases.length)];
            
            // Motivational follow-up
            String motivation = "\n\nKeep up the momentum! 💪 Ready to tackle another task?";
            return prefix + response + motivation;
        }
        
        if (response.contains("removed") || response.contains("deleted")) {
            return "🗑️ Task removed successfully! " + response + "\n\n📝 Anything else you'd like to add or modify in your list?";
        }
        
        if (response.contains("found") || response.contains("matching")) {
            return "🔍 **Search Results:**\n\n" + response + 
                   "\n\n💡 **Tip:** You can also search by:\n" +
                   "• Categories: \"Search #work\" or \"Find #personal\"\n" +
                   "• Contexts: \"Search @home\" or \"Find @office\"\n" +
                   "• Priorities: \"Search !urgent\" or \"Find !high\"";
        }
        
        if (response.contains("edited") || response.contains("updated")) {
            return "✏️ **Update Complete!** " + response + "\n\n✨ Your task has been successfully modified. Need to make any other changes?";
        }
        
        if (response.contains("Invalid date") || response.contains("format")) {
            return "📅 **Date Format Help:**\n\n" +
                   "I understand lots of natural date formats! Try:\n" +
                   "• \"tomorrow\" or \"tomorrow 3pm\"\n" +
                   "• \"next Friday\" or \"Friday at 2pm\"\n" +
                   "• \"end of week\" or \"this weekend\"\n" +
                   "• \"December 25\" or \"25/12/2024\"\n\n" +
                   "What date were you trying to set? 🤔";
        }
        
        // Default friendly response
        return response + "\n\n😊 What else can I help you with?";
    }
    
    private String makeErrorNatural(String error) {
        if (error.contains("empty")) {
            return "🤔 Oops! Looks like you forgot to tell me what the task is.\n\n" +
                   "Try something like:\n• \"Add buy milk\"\n• \"Todo call mom\"\n• \"Add meeting notes\"";
        }
        
        if (error.contains("format") || error.contains("datetime")) {
            return "📅 I didn't quite catch that date format.\n\n" +
                   "Try these natural formats:\n• \"Deadline report by tomorrow\"\n• \"Meeting next Friday at 2pm\"\n• \"Due this weekend\"";
        }
        
        if (error.contains("not found") || error.contains("invalid")) {
            return "🤷‍♂️ I couldn't find that task.\n\n" +
                   "Try \"Show my tasks\" to see what's available, or use task numbers like \"Done 1\" or \"Delete 2\"";
        }
        
        if (error.contains("number")) {
            return "🔢 I need a task number for that.\n\n" +
                   "Try:\n• \"Done 1\" (mark task 1 complete)\n• \"Delete 2\" (remove task 2)\n• \"Edit 3\" (modify task 3)";
        }
        
        // Generic error with helpful suggestion
        return "😅 Hmm, something went wrong: " + error + 
               "\n\nTry saying \"help\" to see what I can do, or just describe what you want naturally!";
    }
    
    private void scrollToBottomSmoothly() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    private void showWelcomeAnimation() {
        // 1️⃣ Friendly greeting
        Timer t1 = new Timer(800, e ->
            addDukeMessage("👋 Hey! I'm Duke, your personal task assistant!"));
        t1.setRepeats(false);
        t1.start();

        // 2️⃣ Short description
        Timer t2 = new Timer(2500, e ->
            addDukeMessage("I help you manage tasks through natural conversation — just like texting a friend! 😊"));
        t2.setRepeats(false);
        t2.start();

        int offset = 4000;

        // 3️⃣ Highlight demo list (if present)
        if (demoTasksLoaded) {
            Timer tDemo = new Timer(offset, e ->
                addDukeMessage("🎁 I've pre-loaded 3 sample tasks so you can explore right away. Type **list** (or tap the list button) to see them!"));
            tDemo.setRepeats(false);
            tDemo.start();
            offset += 1500; // add delay before next tip
        }

        // 4️⃣ Quick actions suggestion
        Timer t3 = new Timer(offset, e ->
            addDukeMessage("✨ Try typing:\n• \"list\" to view your tasks\n• \"Add buy coffee\" to create one\n• \"Deadline report by Friday\" to set a due date"));
        t3.setRepeats(false);
        t3.start();
        offset += 2000;

        // 5️⃣ Natural language tip
        Timer t4 = new Timer(offset, e ->
            addDukeMessage("💡 Pro tip: I understand natural language, so feel free to talk to me however feels comfortable!"));
        t4.setRepeats(false);
        t4.start();
    }

    private void addPremiumPlaceholder() {
        final String placeholderText = "Type a message...";
        final Color placeholderColor = new Color(160, 160, 160);
        final Color textColor = TEXT_PRIMARY;
        
        // Set initial placeholder
        messageInput.setText(placeholderText);
        messageInput.setForeground(placeholderColor);
        
        messageInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageInput.getText().equals(placeholderText)) {
                    messageInput.setText("");
                    messageInput.setForeground(textColor);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (messageInput.getText().trim().isEmpty()) {
                    messageInput.setText(placeholderText);
                    messageInput.setForeground(placeholderColor);
                }
            }
        });
        
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (messageInput.getText().equals(placeholderText) && 
                    messageInput.getForeground().equals(placeholderColor)) {
                    messageInput.setText("");
                    messageInput.setForeground(textColor);
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                // Ensure text color remains correct
                if (!messageInput.getText().equals(placeholderText)) {
                    messageInput.setForeground(textColor);
                }
            }
            
            @Override
            public void keyTyped(KeyEvent e) {}
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WhatsAppStyleChatUI();
        });
    }
} 