package controllers;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class FlightReminderNotification {

    public void checkUpcomingFlightReminder(List<ReservationFlightViewController.ReservationViewModel> reservationsList) {
        // Find the closest upcoming flight
        Optional<ReservationFlightViewController.ReservationViewModel> upcomingFlight = reservationsList.stream()
                .filter(this::isUpcomingFlight)
                .min(Comparator.comparing(this::getFlightDateTime));

        // If an upcoming flight is found, send a notification
        upcomingFlight.ifPresent(this::sendFlightReminder);
    }

    private boolean isUpcomingFlight(ReservationFlightViewController.ReservationViewModel reservation) {
        try {
            // Parse the flight date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date flightDate = dateFormat.parse(reservation.getFlightDate());


            // Convert to LocalDateTime
            LocalDateTime flightDateTime = new Timestamp(flightDate.getTime()).toLocalDateTime();

            LocalDateTime now = LocalDateTime.now();

            // Check if flight is within the next 7 days and in the future

            return flightDateTime.isAfter(now) &&
                    ChronoUnit.DAYS.between(now, flightDateTime) <= 7;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private LocalDateTime getFlightDateTime(ReservationFlightViewController.ReservationViewModel reservation) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date flightDate = dateFormat.parse(reservation.getFlightDate());
            return new Timestamp(flightDate.getTime()).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            return LocalDateTime.MAX;
        }
    }

    private void sendFlightReminder(ReservationFlightViewController.ReservationViewModel flight) {
        Platform.runLater(() -> {

            try {
                String audioPath = "src/main/resources/audio_reminder_vol_homme.mp3";

                // Play the audio file
                Media media = new Media(new File(audioPath).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();





                // PowerShell command for Windows Runtime Notification
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
                                "            <image placement='appLogoOverride' hint-crop='circle'>ms-appx:/resources/alarm-icon.png  </image>\n" +
                                "            <text id='1'>Rappel prochain vol</text>\n" +
                                "            <text id='2'>Votre Vol " + escapeXml(flight.getFlightNumber()) + " à " +
                                escapeXml(flight.getDestination()) + " est à venir dans la date  " +
                                escapeXml(flight.getFlightDate()) + "</text>\n" +
                                "        </binding>\n" +
                                "    </visual>\n" +
                                "     <audio src='ms-winsoundevent:Notification.Looping.Call'/>\n" +
                                "</toast>\n" +
                                "'@\n" +
                                "$xml = New-Object Windows.Data.Xml.Dom.XmlDocument\n" +
                                "$xml.LoadXml($template)\n" +
                                "$toast = New-Object Windows.UI.Notifications.ToastNotification $xml\n" +
                                "[Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('Rappel des vols').Show($toast)"
                };

                // Execute the PowerShell command
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
                PauseTransition delay = new PauseTransition(Duration.seconds(10));
                delay.setOnFinished(event -> mediaPlayer.stop());
                delay.play();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // XML escaping method
    private static String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // Method to schedule periodic flight checks
    public void scheduleFlightReminderChecks(List<ReservationFlightViewController.ReservationViewModel> reservationsList) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkUpcomingFlightReminder(reservationsList);
            }
        }, 0, 24 * 60 * 60 * 1000); // Check daily
    }
}