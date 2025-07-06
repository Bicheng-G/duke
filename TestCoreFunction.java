import ui.WhatsAppStyleChatUI;
import java.lang.reflect.Method;

/**
 * Test class to verify core functionality of natural language processing
 */
public class TestCoreFunction {
    
    public static void main(String[] args) {
        try {
            // Create an instance to test preprocessing
            WhatsAppStyleChatUI chatUI = new WhatsAppStyleChatUI();
            
            // Use reflection to access the private preprocessInput method
            Method preprocessMethod = WhatsAppStyleChatUI.class.getDeclaredMethod("preprocessInput", String.class);
            preprocessMethod.setAccessible(true);
            
            System.out.println("🧪 Testing Core Natural Language Processing");
            System.out.println("===========================================\n");
            
            // Test cases
            String[] testInputs = {
                // Event tests
                "meeting tomorrow at 2pm",
                "event meeting tomorrow at 2pm", 
                "add meeting tomorrow at 2pm",
                "appointment with doctor on friday 3pm",
                "lunch with client next monday at 12pm",
                
                // Deadline tests  
                "deadline project by friday",
                "submit report by tomorrow 5pm",
                "finish homework by next week",
                "project due next friday",
                
                // Todo tests
                "add buy milk",
                "todo tell baby a joke", 
                "create shopping list",
                "buy groceries",
                "call mom",
                
                // Direct commands (should pass through)
                "list",
                "help", 
                "done 1",
                "delete 2"
            };
            
            for (String input : testInputs) {
                try {
                    String result = (String) preprocessMethod.invoke(chatUI, input);
                    System.out.printf("Input:  \"%s\"\n", input);
                    System.out.printf("Output: \"%s\"\n", result);
                    System.out.println("Status: " + (isValidCommand(result) ? "✅ VALID" : "❌ INVALID"));
                    System.out.println();
                } catch (Exception e) {
                    System.out.printf("Input:  \"%s\"\n", input);
                    System.out.printf("Error:  %s\n", e.getMessage());
                    System.out.println("Status: ❌ ERROR");
                    System.out.println();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Failed to test: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean isValidCommand(String command) {
        String cmd = command.toLowerCase().trim();
        
        // Check for valid todo format
        if (cmd.startsWith("todo ") && cmd.length() > 5) {
            return true;
        }
        
        // Check for valid event format
        if (cmd.startsWith("event ") && cmd.contains(" /at ")) {
            return true;
        }
        
        // Check for valid deadline format
        if (cmd.startsWith("deadline ") && cmd.contains(" /by ")) {
            return true;
        }
        
        // Check for direct commands
        if (cmd.equals("list") || cmd.equals("help") || cmd.startsWith("done ") || 
            cmd.startsWith("delete ") || cmd.startsWith("search ")) {
            return true;
        }
        
        return false;
    }
} 