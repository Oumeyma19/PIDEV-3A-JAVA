package test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainAdminDashboard extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Send Windows Runtime Notification
        sendWindowsRuntimeNotification(
                "Notification Title",
                "Detailed Notification Message"
        );

        primaryStage.setTitle("Notification Demo");
        primaryStage.show();
    }

    public static void sendWindowsRuntimeNotification(String title, String message) {
        Platform.runLater(() -> {
            try {
                // Prepare the PowerShell command exactly matching the provided script
                String[] command = {
                        "powershell.exe",
                        "-Command",
                        "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null\n" +
                                "[Windows.UI.Notifications.ToastNotification, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null\n" +
                                "[Windows.Data.Xml.Dom.XmlDocument, Windows.Data.Xml.Dom.XmlDocument, ContentType = WindowsRuntime] | Out-Null\n" +
                                "$template = @'\n" +
                                "<toast>\n" +
                                "    <visual>\n" +
                                "        <binding template='ToastText02'>\n" +
                                "            <text id='1'>" +  "</text>\n" +
                                "            <text id='2'>"  + "</text>\n" +
                                "        </binding>\n" +
                                "    </visual>\n" +
                                "</toast>\n" +
                                "'@\n" +
                                "$xml = New-Object Windows.Data.Xml.Dom.XmlDocument\n" +
                                "$xml.LoadXml($template)\n" +
                                "$toast = New-Object Windows.UI.Notifications.ToastNotification $xml\n" +
                                "[Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('TEST').Show($toast)"
                };

                // Execute the PowerShell command
                ProcessBuilder pb = new ProcessBuilder(command);
                Process process = pb.start();

                // Capture and print error output for debugging
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream())
                );

                StringBuilder errorOutput = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine).append("\n");
                }

                // Wait for the process to complete
                int exitCode = process.waitFor();

                // Print debugging information
                System.out.println("Exit Code: " + exitCode);
                if (errorOutput.length() > 0) {
                    System.err.println("Error Output:\n" + errorOutput);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Escape XML special characters to prevent XML injection
     */
    private static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // Method to send multiple notifications
    public static void sendMultipleNotifications() {
        for (int i = 1; i <= 3; i++) {
            sendWindowsRuntimeNotification(
                    "Notification " + i,
                    "This is notification number " + i
            );
        }
    }
}