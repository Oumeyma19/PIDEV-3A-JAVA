package controllers;

import controllers.HuggingFaceAPI;
import models.Recompense;
import services.ProgrammeFideliteService;
import services.RecompenseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AjouterRecompenseController {

    @FXML
    private TextField nomField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField pointsField;

    @FXML
    private Button uploadButton;

    @FXML
    private Button generateImageButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Label noImageLabel;

    private String photoPath;
    private File selectedImageFile;

    private final RecompenseService recompenseService = new RecompenseService();
    private final ProgrammeFideliteService programmeFideliteService = new ProgrammeFideliteService();
    private final HuggingFaceAPI huggingFaceAPI = new HuggingFaceAPI();
    private RewardsManagementController parentController;

    private static final String IMAGES_DIR = "images"; // Relative path to the images directory

    @FXML
    public void initialize() {
        // Initialiser les éléments UI
        if (noImageLabel != null) {
            noImageLabel.setVisible(true);
        }

        // S'assurer que l'ImageView est correctement initialisé
        if (imageView != null) {
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
        }
    }

    // Méthode pour définir le controller parent
    public void setParentController(RewardsManagementController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleAddRecompense() {
        String nom = nomField.getText().trim();
        String description = descriptionField.getText().trim();
        String pointsText = pointsField.getText().trim();

        // Vérification des entrées
        if (nom.isEmpty() || pointsText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification du nom (seules les lettres et espaces sont autorisés)
        if (!nom.matches("^[A-Za-zÀ-ÖØ-öø-ÿ ]+$")) {
            showAlert("Erreur", "Le nom ne doit contenir que des lettres et des espaces !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification des points requis (nombre entier positif)
        int pointsRequis;
        try {
            pointsRequis = Integer.parseInt(pointsText);
            if (pointsRequis <= 0) {
                showAlert("Erreur", "Les points requis doivent être un nombre positif !", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les points requis doivent être un nombre valide !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de l'image
        if (photoPath == null || photoPath.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner une image ou générer une image pour la récompense !", Alert.AlertType.ERROR);
            return;
        }

        try {
            String destinationPath;

            // Si l'image a été générée par l'IA, utiliser directement photoPath
            if (photoPath.startsWith("http://localhost/images/") || selectedImageFile == null) {
                destinationPath = photoPath;
            } else {
                // Sinon, copier l'image téléchargée dans un dossier d'images de l'application
                destinationPath = saveImageToApplicationFolder(selectedImageFile);
            }

            // Obtenir un ID de programme valide
            int programmeId = programmeFideliteService.getFirstProgrammeId();

            if (programmeId <= 0) {
                showAlert("Erreur", "Aucun programme de fidélité n'a pu être trouvé ou créé.", Alert.AlertType.ERROR);
                return;
            }

            // Création et ajout de la récompense avec le nouveau chemin d'image et la description
            Recompense recompense = new Recompense(programmeId, nom, description, pointsRequis, destinationPath);
            recompenseService.addRecompense(recompense);

            // Afficher une confirmation stylisée
            showAlert("Succès", "La récompense \"" + nom + "\" a été ajoutée avec succès !", Alert.AlertType.INFORMATION);

            clearFields();

            // Rafraîchir la liste des récompenses dans le controller parent si disponible
            if (parentController != null) {
                parentController.refreshRecompensesList();
            }

            // Fermer la fenêtre
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ajouter la récompense: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    // Méthode pour générer une image à partir de la description
    @FXML
    private void handleGenerateImage() {
        String description = descriptionField.getText().trim();
        String nom = nomField.getText().trim();

        if (description.isEmpty() || nom.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom et une description avant de générer une image !", Alert.AlertType.ERROR);
            return;
        }

        try {
            generateImageButton.setDisable(true);
            generateImageButton.setText("Génération en cours...");

            new Thread(() -> {
                boolean success = generateDescriptionImage(description, nom);
                javafx.application.Platform.runLater(() -> {
                    generateImageButton.setDisable(false);
                    generateImageButton.setText("Générer une image");

                    if (!success) {
                        showAlert("Erreur", "La génération de l'image a échoué.", Alert.AlertType.ERROR);
                    }
                });
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la génération de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean generateDescriptionImage(String description, String nom) {
        try {
            String enhancedPrompt = String.format("A detailed image of a reward: %s. Name: %s.", description, nom);
            Path imagesDir = Paths.get(IMAGES_DIR); // Use the relative path directly
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
            }

            String uniqueFileName = "reward_" + nom.replaceAll("\\s+", "_").toLowerCase() + "_" + System.currentTimeMillis() + ".png";
            Path outputFile = imagesDir.resolve(uniqueFileName);

            boolean success = huggingFaceAPI.generateImage(enhancedPrompt, outputFile.toString());

            if (success) {
                javafx.application.Platform.runLater(() -> {
                    try {
                        Image image = new Image("file:" + outputFile.toAbsolutePath().toString());
                        imageView.setImage(image);
                        photoPath = outputFile.toAbsolutePath().toString();
                        selectedImageFile = outputFile.toFile(); // Set selectedImageFile to the generated image

                        if (noImageLabel != null) {
                            noImageLabel.setVisible(false);
                        }
                        uploadButton.setText("Image générée ✓");
                        uploadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                        showAlert("Succès", "Image générée et enregistrée avec succès.", Alert.AlertType.INFORMATION);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Erreur lors du chargement de l'image générée: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                showAlert("Erreur", "Erreur pendant la génération de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
            });
            return false;
        }
    }

    // Méthode pour sauvegarder l'image dans un dossier de l'application
    private String saveImageToApplicationFolder(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IOException("Aucun fichier image sélectionné");
        }

        // Créer le dossier images s'il n'existe pas
        Path imagesDir = Paths.get(IMAGES_DIR); // Use the relative path directly
        if (!Files.exists(imagesDir)) {
            Files.createDirectories(imagesDir);
        }

        // Générer un nom de fichier unique basé sur le timestamp
        String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
        Path destinationPath = imagesDir.resolve(fileName);

        // Copier le fichier
        Files.copy(imageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return destinationPath.toAbsolutePath().toString(); // Return absolute path
    }

    @FXML
    private void goBackToRecompenseList() {
        // Close the window (same as your handleCancel method)
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = new Stage();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            photoPath = selectedImageFile.getAbsolutePath();

            // Mettre à jour le texte du bouton
            uploadButton.setText("Image sélectionnée ✓");
            uploadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            // Charger et afficher l'image sélectionnée dans l'ImageView
            try {
                Image image = new Image("file:" + selectedImageFile.toURI().getPath(), 300, 200, true, true);
                if (image.isError()) {
                    throw new IOException("Erreur lors du chargement de l'image");
                }
                if (imageView != null) {
                    imageView.setImage(image);
                }
                // Masquer le label "Aucune image sélectionnée"
                if (noImageLabel != null) {
                    noImageLabel.setVisible(false);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Impossible de charger l'image sélectionnée: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        // Fermer la fenêtre
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
        pointsField.clear();
        photoPath = null;
        selectedImageFile = null;
        uploadButton.setText("Sélectionner une image");
        uploadButton.setStyle("-fx-background-color: #3A86FF; -fx-text-fill: white;");

        if (imageView != null) {
            imageView.setImage(null);
        }

        if (noImageLabel != null) {
            noImageLabel.setVisible(true);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}