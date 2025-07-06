import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * A GUI for Duke using Swing.
 */
public class Main extends JFrame {

    private Duke duke = new Duke();
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    public Main() {
        setTitle("Duke, the Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.WHITE);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add welcome message
        chatArea.append("Hello! I'm Duke\n");
        chatArea.append("What can I do for you?\n\n");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Chat area with scroll pane
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(inputPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUserInput();
            }
        };
        
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
    }
    
    private void handleUserInput() {
        String input = inputField.getText().trim();
        if (!input.isEmpty()) {
            // Add user input to chat
            chatArea.append("You: " + input + "\n");
            
            // Get Duke's response
            String response = duke.getResponse(input);
            chatArea.append("Duke: " + response + "\n\n");
            
            // Clear input field
            inputField.setText("");
            
            // Scroll to bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
            
            // Check if user wants to exit
            if (input.equalsIgnoreCase("bye")) {
                System.exit(0);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}