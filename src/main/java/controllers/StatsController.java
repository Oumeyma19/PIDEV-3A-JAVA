package controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import services.ClientService;
import services.GuideService;

public class StatsController {

    @FXML
    private VBox statsContainer;

    @FXML
    private Label clientsCountLabel;

    @FXML
    private Label guidesCountLabel;

    @FXML
    private PieChart clientsPieChart;

    @FXML
    private PieChart guidesPieChart;

    private ClientService clientService = ClientService.getInstance();
    private GuideService guideService = GuideService.getInstance();

    @FXML
    public void initialize() {
        // Appliquer les styles aux labels
        clientsCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-family: 'System Bold';");
        guidesCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-family: 'System Bold';");

        loadStats();
    }

    private void applyPieChartColors(PieChart pieChart) {
        // Appliquer des couleurs personnalisées aux sections du PieChart
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            if (data.getName().startsWith("Actifs")) {
                node.setStyle("-fx-pie-color: #219EA2;"); // Vert
            } else if (data.getName().startsWith("Inactifs")) {
                node.setStyle("-fx-pie-color: #FAD2C3;"); // Rouge
            }
        }
    }

    private void loadStats() {
        // Charger les statistiques des clients
        int totalClients = clientService.getClientsCount();
        int activeClients = clientService.getActiveClientsCount();
        int inactiveClients = clientService.getInactiveClientsCount();

        clientsCountLabel.setText("Nombre de clients: " + totalClients);

        // Calculer les pourcentages pour les clients
        double activeClientsPercentage = (double) activeClients / totalClients * 100;
        double inactiveClientsPercentage = (double) inactiveClients / totalClients * 100;

        // Ajouter les données au PieChart avec les pourcentages
        clientsPieChart.getData().clear();
        clientsPieChart.getData().add(new PieChart.Data(
            String.format("Actifs (%.1f%%)", activeClientsPercentage), activeClients));
        clientsPieChart.getData().add(new PieChart.Data(
            String.format("Inactifs (%.1f%%)", inactiveClientsPercentage), inactiveClients));

        // Appliquer les couleurs personnalisées
        applyPieChartColors(clientsPieChart);

        // Charger les statistiques des guides
        int totalGuides = guideService.getGuidesCount();
        int activeGuides = guideService.getActiveGuidesCount();
        int inactiveGuides = guideService.getInactiveGuidesCount();

        guidesCountLabel.setText("Nombre de guides: " + totalGuides);

        // Calculer les pourcentages pour les guides
        double activeGuidesPercentage = (double) activeGuides / totalGuides * 100;
        double inactiveGuidesPercentage = (double) inactiveGuides / totalGuides * 100;

        // Ajouter les données au PieChart avec les pourcentages
        guidesPieChart.getData().clear();
        guidesPieChart.getData().add(new PieChart.Data(
            String.format("Actifs (%.1f%%)", activeGuidesPercentage), activeGuides));
        guidesPieChart.getData().add(new PieChart.Data(
            String.format("Inactifs (%.1f%%)", inactiveGuidesPercentage), inactiveGuides));

        // Appliquer les couleurs personnalisées
        applyPieChartColors(guidesPieChart);
    }
}
