import ui.WhatsAppStyleChatUI;
import java.lang.reflect.Method;

/**
 * Quick test for specific issues found by automated tester
 */
public class QuickTest {
    
    public static void main(String[] args) {
        try {
            WhatsAppStyleChatUI chatUI = new WhatsAppStyleChatUI();
            Method preprocessMethod = WhatsAppStyleChatUI.class.getDeclaredMethod("preprocessInput", String.class);
            preprocessMethod.setAccessible(true);
            
            System.out.println("🔧 Testing Specific Fixes");
            System.out.println("========================");
            
            String[] problemCases = {
                // Cases that were failing
                "due project due friday",
                "due task due tomorrow", 
                "due insurance claim due friday",
                "due paper due monday",
                "",
                "   ",
                "appointment",
                "meeting",
                "deadline",
                "appointment appointment appointment",
                "call mom by morning",
                "meeting tomorrow at 2pm",
                "tell baby a joke",
                "buy milk"
            };
            
            for (String testCase : problemCases) {
                try {
                    String result = (String) preprocessMethod.invoke(chatUI, testCase);
                    System.out.printf("Input:  \"%s\"\n", testCase);
                    System.out.printf("Output: \"%s\"\n", result);
                    System.out.printf("Valid:  %s\n", isValidOutput(result));
                    System.out.println();
                } catch (Exception e) {
                    System.out.printf("Input:  \"%s\"\n", testCase);
                    System.out.printf("ERROR:  %s\n", e.getMessage());
                    System.out.println();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean isValidOutput(String output) {
        if (output == null || output.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = output.trim().split(" ", 2);
        String cmdType = parts[0].toLowerCase();
        
        switch (cmdType) {
            case "event":
                return parts.length > 1 && parts[1].contains("/at") && 
                       !parts[1].startsWith("/at") && !parts[1].endsWith("/at");
            case "deadline":
                return parts.length > 1 && parts[1].contains("/by") && 
                       !parts[1].startsWith("/by") && !parts[1].endsWith("/by");
            case "todo":
                return parts.length > 1 && !parts[1].trim().isEmpty();
            case "help":
            case "list":
            case "search":
                return true;
            default:
                return false;
        }
    }
} 