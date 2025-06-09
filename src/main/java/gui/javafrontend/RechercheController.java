package gui.javafrontend;

import gui.javafrontend.dto.ProjetDTO;
import gui.javafrontend.service.AuthService;
import gui.javafrontend.service.ProjetService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class RechercheController implements Initializable {

    @FXML
    private Button backToLoginButton;

    @FXML
    private TableColumn<ProjetDTO, String> colChercheur;

    @FXML
    private TableColumn<ProjetDTO, String> colStatut;

    @FXML
    private TableColumn<ProjetDTO, String> colTitre;

    @FXML
    private ImageView image2;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<ProjetDTO> tableProjets;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    private ProjetService projetService;
    private AuthService authService;
    private ObservableList<ProjetDTO> projetsData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialiser les services avec gestion d'erreur
            projetService = ProjetService.getInstance();
            authService = AuthService.getInstance();

            // Initialiser la liste observable
            projetsData = FXCollections.observableArrayList();

            // Configurer les colonnes du tableau
            setupTableColumns();

            // Lier les données au tableau
            tableProjets.setItems(projetsData);

            // Vérifier l'authentification
            checkAuthentication();

            // Configurer les éléments UI
            setupUI();

            // Charger tous les projets au démarrage seulement si connecté
            if (authService != null && authService.isLoggedIn()) {
                chargerTousProjets();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du RechercheController: " + e.getMessage());
            updateStatus("Erreur d'initialisation");
        }
    }

    // Getters pour les tests
    public ObservableList<ProjetDTO> getProjetsData() {
        return projetsData;
    }

    public TableView<ProjetDTO> getTableProjets() {
        return tableProjets;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public ProgressIndicator getLoadingIndicator() {
        return loadingIndicator;
    }

    private void setupTableColumns() {
        try {
            // Configuration des colonnes avec vérification de nullité
            if (colTitre != null) {
                colTitre.setCellValueFactory(new PropertyValueFactory<>("nomLong"));
                colTitre.setText("Nom du Projet");
                colTitre.prefWidthProperty().bind(tableProjets.widthProperty().multiply(0.45));
            }

            if (colChercheur != null) {
                colChercheur.setCellValueFactory(new PropertyValueFactory<>("theme"));
                colChercheur.setText("Thème");
                colChercheur.prefWidthProperty().bind(tableProjets.widthProperty().multiply(0.35));
            }

            if (colStatut != null) {
                colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
                colStatut.setText("Statut");
                colStatut.prefWidthProperty().bind(tableProjets.widthProperty().multiply(0.20));

                // Personnaliser l'affichage du statut avec des couleurs
                colStatut.setCellFactory(column -> new TableCell<ProjetDTO, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            // Colorer selon le statut
                            switch (item.toUpperCase()) {
                                case "ACCEPTE":
                                    setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                                    break;
                                case "EN_ATTENTE":
                                    setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                                    break;
                                case "REJETE":
                                    setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                                    break;
                                case "CLOS":
                                    setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
                                    break;
                                default:
                                    setStyle("-fx-text-fill: #212529;");
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du setup des colonnes: " + e.getMessage());
        }
    }

    private void setupUI() {
        try {
            // Configurer le statut initial
            if (statusLabel != null) {
                statusLabel.setText("Prêt pour la recherche");
            }

            // Masquer l'indicateur de chargement
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(false);
            }

            // Configurer l'action sur Enter dans le champ de recherche
            if (searchField != null) {
                searchField.setOnAction(this::recherche);
                searchField.setPromptText("Entrez un mot-clé pour rechercher...");
            }

            // Configurer le double-clic sur une ligne du tableau pour afficher les détails
            if (tableProjets != null) {
                tableProjets.setRowFactory(tv -> {
                    TableRow<ProjetDTO> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && !row.isEmpty()) {
                            afficherDetailsProjet(new ActionEvent());
                        }
                    });
                    return row;
                });

                // Configurer le placeholder pour le tableau vide
                tableProjets.setPlaceholder(new Label("Aucun projet à afficher"));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du setup UI: " + e.getMessage());
        }
    }

    private void checkAuthentication() {
        try {
            if (authService == null || !authService.isLoggedIn()) {
                showError("Vous devez être connecté pour utiliser cette fonctionnalité");
                // Rediriger vers la page de connexion si nécessaire
                Platform.runLater(() -> {
                    try {
                        backToLogin();
                    } catch (IOException e) {
                        System.err.println("Erreur redirection login: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification d'authentification: " + e.getMessage());
        }
    }

    @FXML
    void backToAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("accueil.fxml"));
            HelloApplication.getScene().setRoot(loader.load());
        } catch (IOException e) {
            showError("Erreur lors du retour à l'accueil: " + e.getMessage());
            System.err.println("Erreur navigation accueil: " + e.getMessage());
        }
    }

    private void backToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        HelloApplication.getScene().setRoot(loader.load());
    }

    @FXML
    void recherche(ActionEvent event) {
        String motCle = searchField != null ? searchField.getText() : "";

        if (motCle == null || motCle.trim().isEmpty()) {
            showWarning("Veuillez entrer un mot-clé pour la recherche");
            if (searchField != null) {
                searchField.requestFocus();
            }
            return;
        }

        // Vérifier l'authentification avant la recherche
        if (authService == null || !authService.isLoggedIn()) {
            showError("Session expirée. Veuillez vous reconnecter.");
            try {
                backToLogin();
            } catch (IOException e) {
                System.err.println("Erreur redirection: " + e.getMessage());
            }
            return;
        }

        // Vérifier que le service projet est disponible
        if (projetService == null) {
            showError("Service de projet non disponible");
            return;
        }

        // Afficher l'indicateur de chargement
        showLoading(true);
        updateStatus("Recherche en cours...");

        // Désactiver le bouton de recherche pendant la requête
        if (searchButton != null) {
            searchButton.setDisable(true);
        }

        try {
            // Effectuer la recherche de manière asynchrone avec une URL corrigée
            CompletableFuture<List<ProjetDTO>> searchFuture = projetService.rechercherProjetsAvecRefresh(motCle.trim());

            searchFuture
                    .thenAccept(projets -> {
                        // Mise à jour de l'UI dans le thread JavaFX
                        Platform.runLater(() -> {
                            try {
                                if (projetsData != null) {
                                    projetsData.clear();
                                    if (projets != null && !projets.isEmpty()) {
                                        projetsData.addAll(projets);
                                    }
                                }

                                updateStatus("Recherche terminée: " + (projets != null ? projets.size() : 0) + " projet(s) trouvé(s)");
                                showLoading(false);
                                if (searchButton != null) {
                                    searchButton.setDisable(false);
                                }

                                if (projets == null || projets.isEmpty()) {
                                    showInfo("Aucun projet trouvé pour le mot-clé: \"" + motCle + "\"");
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur lors de la mise à jour UI: " + e.getMessage());
                                updateStatus("Erreur de mise à jour");
                                showLoading(false);
                                if (searchButton != null) {
                                    searchButton.setDisable(false);
                                }
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        // Gestion des erreurs dans le thread JavaFX
                        Platform.runLater(() -> {
                            String errorMessage = throwable.getMessage();
                            if (errorMessage != null && errorMessage.contains("400")) {
                                showError("Erreur de requête: Vérifiez les paramètres de recherche");
                            } else {
                                showError("Erreur lors de la recherche: " + errorMessage);
                            }

                            updateStatus("Erreur de recherche");
                            showLoading(false);
                            if (searchButton != null) {
                                searchButton.setDisable(false);
                            }

                            // Si erreur d'authentification, rediriger vers login
                            if (errorMessage != null && (errorMessage.contains("401") ||
                                    errorMessage.contains("authentifié") ||
                                    errorMessage.contains("Session expirée"))) {
                                try {
                                    backToLogin();
                                } catch (IOException e) {
                                    System.err.println("Erreur redirection: " + e.getMessage());
                                }
                            }
                        });
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initiation de la recherche: " + e.getMessage());
            showError("Erreur lors de l'initiation de la recherche");
            showLoading(false);
            if (searchButton != null) {
                searchButton.setDisable(false);
            }
        }
    }

    /**
     * Charger tous les projets au démarrage
     */
    private void chargerTousProjets() {
        if (authService == null || !authService.isLoggedIn()) {
            updateStatus("Non connecté");
            return;
        }

        if (projetService == null) {
            updateStatus("Service non disponible");
            return;
        }

        showLoading(true);
        updateStatus("Chargement des projets...");

        try {
            projetService.obtenirTousProjets()
                    .thenAccept(projets -> {
                        Platform.runLater(() -> {
                            try {
                                if (projetsData != null) {
                                    projetsData.clear();
                                    if (projets != null && !projets.isEmpty()) {
                                        projetsData.addAll(projets);
                                    }
                                }
                                updateStatus("Prêt - " + (projets != null ? projets.size() : 0) + " projet(s) disponible(s)");
                                showLoading(false);
                            } catch (Exception e) {
                                System.err.println("Erreur mise à jour projets: " + e.getMessage());
                                updateStatus("Erreur de chargement");
                                showLoading(false);
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        Platform.runLater(() -> {
                            updateStatus("Erreur chargement initial");
                            showLoading(false);
                            showError("Impossible de charger les projets: " + throwable.getMessage());
                            System.err.println("Erreur chargement projets: " + throwable.getMessage());
                        });
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des projets: " + e.getMessage());
            updateStatus("Erreur chargement initial");
            showLoading(false);
        }
    }

    /**
     * Rafraîchir la liste des projets
     */
    @FXML
    void rafraichir(ActionEvent event) {
        updateStatus("Actualisation...");
        chargerTousProjets();
    }

    /**
     * Vider le champ de recherche et recharger tous les projets
     */
    @FXML
    void effacerRecherche(ActionEvent event) {
        if (searchField != null) {
            searchField.clear();
        }
        updateStatus("Réinitialisation...");
        chargerTousProjets();
    }

    /**
     * Rechercher avec un terme spécifique (méthode utilitaire)
     */
    @FXML
    void rechercherAvecTerme(String terme) {
        if (searchField != null) {
            searchField.setText(terme);
        }
        recherche(new ActionEvent());
    }

    /**
     * Filtrer les projets par statut
     */
    @FXML
    void filtrerParStatut(ActionEvent event) {
        // Créer une boîte de dialogue pour choisir le statut
        ChoiceDialog<String> dialog = new ChoiceDialog<>("TOUS",
                "TOUS", "ACCEPTE", "EN_ATTENTE", "REJETE", "CLOS");
        dialog.setTitle("Filtrer par statut");
        dialog.setHeaderText("Sélectionnez un statut pour filtrer les projets");
        dialog.setContentText("Statut:");

        dialog.showAndWait().ifPresent(statut -> {
            if ("TOUS".equals(statut)) {
                chargerTousProjets();
            } else {
                filtrerProjetsParStatut(statut);
            }
        });
    }

    private void filtrerProjetsParStatut(String statut) {
        if (projetService == null) {
            showError("Service non disponible");
            return;
        }

        showLoading(true);
        updateStatus("Filtrage par statut: " + statut);

        projetService.obtenirTousProjets()
                .thenAccept(projets -> {
                    Platform.runLater(() -> {
                        try {
                            if (projetsData != null) {
                                projetsData.clear();
                                if (projets != null) {
                                    List<ProjetDTO> projetsFiltres = projets.stream()
                                            .filter(p -> statut.equalsIgnoreCase(p.getStatut()))
                                            .toList();
                                    projetsData.addAll(projetsFiltres);
                                }
                            }
                            updateStatus("Filtrage terminé: " + projetsData.size() + " projet(s) avec statut " + statut);
                            showLoading(false);
                        } catch (Exception e) {
                            System.err.println("Erreur filtrage: " + e.getMessage());
                            updateStatus("Erreur de filtrage");
                            showLoading(false);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        updateStatus("Erreur de filtrage");
                        showLoading(false);
                        showError("Impossible de filtrer les projets: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    // Méthodes utilitaires pour l'UI
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void showError(String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur affichage alerte: " + e.getMessage());
        }
    }

    private void showWarning(String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur affichage warning: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur affichage info: " + e.getMessage());
        }
    }

    /**
     * Afficher les détails d'un projet sélectionné
     */
    @FXML
    void afficherDetailsProjet(ActionEvent event) {
        if (tableProjets == null) {
            showWarning("Tableau non initialisé");
            return;
        }

        ProjetDTO projetSelectionne = tableProjets.getSelectionModel().getSelectedItem();
        if (projetSelectionne == null) {
            showWarning("Veuillez sélectionner un projet pour voir ses détails");
            return;
        }

        try {
            // Créer une fenêtre de détails avec un contenu scrollable
            Alert detailsAlert = new Alert(Alert.AlertType.INFORMATION);
            detailsAlert.setTitle("Détails du Projet");
            detailsAlert.setHeaderText(projetSelectionne.getNomLong() != null ?
                    projetSelectionne.getNomLong() : "Projet sans nom");

            StringBuilder details = new StringBuilder();
            details.append("ID: ").append(projetSelectionne.getId() != null ?
                    projetSelectionne.getId() : "Non spécifié").append("\n\n");

            details.append("Nom court: ").append(projetSelectionne.getNomCourt() != null ?
                    projetSelectionne.getNomCourt() : "Non spécifié").append("\n\n");

            details.append("Description: ").append(projetSelectionne.getDescription() != null ?
                    projetSelectionne.getDescription() : "Non spécifiée").append("\n\n");

            details.append("Thème: ").append(projetSelectionne.getTheme() != null ?
                    projetSelectionne.getTheme() : "Non spécifié").append("\n\n");

            details.append("Type: ").append(projetSelectionne.getType() != null ?
                    projetSelectionne.getType() : "Non spécifié").append("\n\n");

            details.append("Public: ").append(projetSelectionne.isEstPublic() ? "Oui" : "Non").append("\n\n");

            details.append("Licence: ").append(projetSelectionne.getLicense() != null ?
                    projetSelectionne.getLicense() : "Non spécifiée").append("\n\n");

            details.append("Statut: ").append(projetSelectionne.getStatut() != null ?
                    projetSelectionne.getStatut() : "Non spécifié").append("\n\n");

            details.append("Groupe ID: ").append(projetSelectionne.getGroupeId() != null ?
                    projetSelectionne.getGroupeId() : "Non spécifié").append("\n\n");

            details.append("Date de création: ").append(projetSelectionne.getDateCreation() != null ?
                    projetSelectionne.getDateCreation().toString() : "Non spécifiée").append("\n");

            // Configurer la fenêtre pour qu'elle soit redimensionnable
            detailsAlert.setContentText(details.toString());
            detailsAlert.getDialogPane().setPrefWidth(500);
            detailsAlert.setResizable(true);
            detailsAlert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur affichage détails: " + e.getMessage());
            showError("Erreur lors de l'affichage des détails");
        }
    }

    /**
     * Exporter les résultats de recherche (fonctionnalité bonus)
     */
    @FXML
    void exporterResultats(ActionEvent event) {
        if (projetsData == null || projetsData.isEmpty()) {
            showWarning("Aucun projet à exporter");
            return;
        }

        try {
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Nom Court,Nom Long,Description,Thème,Type,Statut,Public,Licence,Groupe ID,Date Création\n");

            for (ProjetDTO projet : projetsData) {
                csv.append(escapeCSV(String.valueOf(projet.getId()))).append(",");
                csv.append(escapeCSV(projet.getNomCourt())).append(",");
                csv.append(escapeCSV(projet.getNomLong())).append(",");
                csv.append(escapeCSV(projet.getDescription())).append(",");
                csv.append(escapeCSV(projet.getTheme())).append(",");
                csv.append(escapeCSV(projet.getType())).append(",");
                csv.append(escapeCSV(projet.getStatut())).append(",");
                csv.append(projet.isEstPublic() ? "Oui" : "Non").append(",");
                csv.append(escapeCSV(projet.getLicense())).append(",");
                csv.append(escapeCSV(String.valueOf(projet.getGroupeId()))).append(",");
                csv.append(escapeCSV(projet.getDateCreation() != null ?
                        projet.getDateCreation().toString() : "")).append("\n");
            }

            // Afficher le CSV dans une fenêtre de dialogue
            Alert exportAlert = new Alert(Alert.AlertType.INFORMATION);
            exportAlert.setTitle("Export CSV");
            exportAlert.setHeaderText("Données exportées (" + projetsData.size() + " projets)");

            TextArea textArea = new TextArea(csv.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setPrefRowCount(15);
            textArea.setPrefColumnCount(80);

            exportAlert.getDialogPane().setContent(textArea);
            exportAlert.setResizable(true);
            exportAlert.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'export: " + e.getMessage());
            showError("Erreur lors de l'export des données");
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Obtenir le projet actuellement sélectionné
     * @return Le projet sélectionné ou null si aucune sélection
     */
    public ProjetDTO getSelectedProjet() {
        return tableProjets != null ? tableProjets.getSelectionModel().getSelectedItem() : null;
    }

    /**
     * Sélectionner un projet par index
     * @param index L'index du projet à sélectionner
     */
    public void selectProjet(int index) {
        if (tableProjets != null && projetsData != null && index >= 0 && index < projetsData.size()) {
            tableProjets.getSelectionModel().select(index);
            tableProjets.scrollTo(index);
        }
    }

    /**
     * Obtenir le nombre de projets affichés
     * @return Le nombre de projets dans la liste
     */
    public int getNombreProjets() {
        return projetsData != null ? projetsData.size() : 0;
    }

    /**
     * Vérifier si des projets sont chargés
     * @return true si au moins un projet est affiché
     */
    public boolean hasProjets() {
        return projetsData != null && !projetsData.isEmpty();
    }

    /**
     * Nettoyer les ressources (utile pour les tests)
     */
    public void cleanup() {
        if (projetsData != null) {
            projetsData.clear();
        }
        if (searchField != null) {
            searchField.clear();
        }
        updateStatus("Prêt pour la recherche");
        showLoading(false);
        if (searchButton != null) {
            searchButton.setDisable(false);
        }
    }

    /**
     * Actualiser les données de manière forcée
     */
    public void forceRefresh() {
        cleanup();
        chargerTousProjets();
    }
}