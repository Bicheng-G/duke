import ui.WhatsAppStyleChatUI;

import javax.swing.*;

/**
 * Launcher for WhatsApp-style Chat Interface
 */
public class ChatLauncher {
    
    public static void main(String[] args) {
        // Set system look and feel for better integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch WhatsApp-style Chat UI
        SwingUtilities.invokeLater(() -> {
            System.out.println("🚀 Starting Duke - WhatsApp Style Chat Interface");
            new WhatsAppStyleChatUI();
        });
    }
} 