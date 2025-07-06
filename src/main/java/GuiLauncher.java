import ui.ModernGuiController;

import javax.swing.*;

/**
 * GUI Launcher for Duke Task Manager
 */
public class GuiLauncher {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Launch GUI on EDT
        SwingUtilities.invokeLater(() -> {
            System.out.println("🚀 Starting Duke Task Manager - Modern GUI Edition");
            new ModernGuiController();
        });
    }
} 