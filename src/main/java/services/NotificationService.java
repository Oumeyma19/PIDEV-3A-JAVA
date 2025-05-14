package services;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationService {

    private static NotificationService _instance;

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (_instance == null) {
            _instance = new NotificationService();
        }
        return _instance;
    }

    public void showNotification(String title, String body) {
        Notifications not = Notifications.create()
                .graphic(null)
                .hideAfter(Duration.seconds(7))
                .position(Pos.TOP_RIGHT)
                .onAction(event -> System.out.println("clicked on notification"));

        not.darkStyle();

        not.title(title);
        not.text(body);
        not.showInformation();
    }
}
