package gui.javafrontend;

import gui.javafrontend.dto.ProjetDTO;
import gui.javafrontend.dto.UtilisateurDTO;
import gui.javafrontend.service.ApiService;
import gui.javafrontend.service.AuthService;
import gui.javafrontend.service.ProjetService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InformatiqueProjetsController implements Initializable {

    @FXML
    private TableColumn<ProjetDTO, String> colChercheur;

    @FXML
    private TableColumn<ProjetDTO, String> colStatut;

    @FXML
    private TableColumn<ProjetDTO, String> colTitre;

    @FXML
    private ImageView image2;

    @FXML
    private TableView<ProjetDTO> tableProjets;

    @FXML
    private Button btnRejoindreGroupe;

    @FXML
    private Button btnActualiser;

    @FXML
    private Label lblStatut;

    @FXML
    private ProgressIndicator progressIndicator;

    private ApiService apiService;
    private AuthService authService;
    private ProjetService projetService;
    private ObservableList<ProjetDTO> projetsData;
    private static final long GROUPE_ID = 1L; // ID du groupe informatique

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les services
        apiService = new ApiService();
        authService = AuthService.getInstance();
        projetService = ProjetService.getInstance();
        projetsData = FXCollections.observableArrayList();

        // Configurer les colonnes du TableView
        setupTableColumns();

        // Configurer le TableView
        setupTableView();

        // Vérifier l'authentification avant de charger les données
        if (authService.isLoggedIn()) {
            chargerProjets();
        } else {
            // Afficher un message demandant à l'utilisateur de se connecter
            if (lblStatut != null) {
                lblStatut.setText("Veuillez vous connecter pour voir les projets");
            }
            showWarningAlert("Authentification requise",
                    "Vous devez vous connecter pour accéder aux projets.");
        }

        // Configurer les styles CSS
        setupStyles();
    }

    /**
     * Configure les colonnes du TableView
     */
    private void setupTableColumns() {
        // Modification: utiliser "nomCourt" au lieu de "NOM"
        colTitre.setCellValueFactory(new PropertyValueFactory<>("nomCourt"));
        // Modification: utiliser "theme" pour la colonne chercheur
        colChercheur.setCellValueFactory(new PropertyValueFactory<>("theme"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Personnaliser l'affichage des cellules
        colStatut.setCellFactory(column -> new TableCell<ProjetDTO, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    // Appliquer des couleurs selon le statut
                    switch (statut.toLowerCase()) {
                        case "en cours":
                            setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2;");
                            break;
                        case "terminé":
                            setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32;");
                            break;
                        case "en attente":
                            setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #f57c00;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });

        // Ajout de cellules personnalisées pour gérer les valeurs null
        colTitre.setCellFactory(column -> new TableCell<ProjetDTO, String>() {
            @Override
            protected void updateItem(String nomCourt, boolean empty) {
                super.updateItem(nomCourt, empty);
                if (empty || nomCourt == null) {
                    setText("");
                } else {
                    setText(nomCourt);
                }
            }
        });

        colChercheur.setCellFactory(column -> new TableCell<ProjetDTO, String>() {
            @Override
            protected void updateItem(String theme, boolean empty) {
                super.updateItem(theme, empty);
                if (empty || theme == null) {
                    setText("");
                } else {
                    setText(theme);
                }
            }
        });
    }

    /**
     * Configure le TableView
     */
    private void setupTableView() {
        tableProjets.setItems(projetsData);
        tableProjets.setRowFactory(tv -> {
            TableRow<ProjetDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ProjetDTO projet = row.getItem();
                    afficherDetailsProjet(projet);
                }
            });
            return row;
        });

        // Message quand la table est vide
        tableProjets.setPlaceholder(new Label("Aucun projet trouvé"));
    }

    /**
     * Configure les styles CSS
     */
    private void setupStyles() {
        tableProjets.getStyleClass().add("elegant-table");
    }

    /**
     * Vérifie l'authentification et affiche un message approprié
     */
    private boolean verifierAuthentification() {
        if (!authService.isLoggedIn()) {
            if (lblStatut != null) {
                lblStatut.setText("Authentification requise");
            }
            showWarningAlert("Authentification requise",
                    "Vous devez vous connecter pour effectuer cette action.");
            return false;
        }
        return true;
    }

    /**
     * Charge les projets depuis l'API en utilisant getAllProjets()
     * et filtre pour ne garder que ceux du groupe informatique (ID = 1)
     */
    @FXML
    private void chargerProjets() {
        // Vérifier l'authentification avant de faire la requête
        if (!verifierAuthentification()) {
            return;
        }

        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }

        if (lblStatut != null) {
            lblStatut.setText("Chargement des projets...");
        }

        Task<List<ProjetDTO>> task = new Task<List<ProjetDTO>>() {
            @Override
            protected List<ProjetDTO> call() throws Exception {
                // Utiliser ProjetService.getAllProjets() pour récupérer tous les projets
                List<ProjetDTO> tousProjets = projetService.getAllProjets().get();

                // Filtrer les projets pour ne garder que ceux du groupe informatique (ID = 1)
                return tousProjets.stream()
                        .filter(projet -> projet.getGroupeId() != null && projet.getGroupeId().equals(GROUPE_ID))
                        .collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<ProjetDTO> projets = getValue();
                    projetsData.clear();
                    projetsData.addAll(projets);

                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }

                    if (lblStatut != null) {
                        lblStatut.setText(projets.size() + " projet(s) du groupe informatique chargé(s)");
                    }

                    System.out.println("✓ Projets filtrés chargés: " + projets.size() + " projets du groupe " + GROUPE_ID);

                    // Debug: afficher les données chargées
                    for (ProjetDTO projet : projets) {
                        System.out.println("Projet: " + projet.getNomCourt() + " | Thème: " + projet.getTheme() + " | Statut: " + projet.getStatut());
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }

                    Throwable exception = getException();
                    String errorMessage = exception.getMessage();

                    if (lblStatut != null) {
                        lblStatut.setText("Erreur lors du chargement");
                    }

                    System.err.println("✗ Erreur lors du chargement des projets: " + errorMessage);

                    // Gestion spécifique des erreurs d'authentification
                    if (errorMessage.contains("403") || errorMessage.contains("Authentification requise")) {
                        showAuthenticationError();
                    } else if (errorMessage.contains("401")) {
                        showTokenExpiredError();
                    } else {
                        showErrorAlert("Erreur de chargement",
                                "Impossible de charger les projets: " + errorMessage);
                    }
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Rejoint le groupe
     */
    @FXML
    private void rejoindreGroupe(ActionEvent event) {
        // Vérifier l'authentification avant de faire la requête
        if (!verifierAuthentification()) {
            return;
        }

        if (lblStatut != null) {
            lblStatut.setText("Rejoindre le groupe...");
        }

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return apiService.rejoindreGroupe(GROUPE_ID).get();
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (lblStatut != null) {
                        lblStatut.setText("Groupe rejoint avec succès!");
                    }
                    showInfoAlert("Succès", "Vous avez rejoint le groupe avec succès!");
                    chargerProjets(); // Recharger les projets
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    String errorMessage = exception.getMessage();

                    if (lblStatut != null) {
                        lblStatut.setText("Erreur lors de la jointure");
                    }

                    // Gestion spécifique des erreurs d'authentification
                    if (errorMessage.contains("403") || errorMessage.contains("Authentification requise")) {
                        showAuthenticationError();
                    } else if (errorMessage.contains("401")) {
                        showTokenExpiredError();
                    } else {
                        showErrorAlert("Erreur",
                                "Impossible de rejoindre le groupe: " + errorMessage);
                    }
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Actualise la liste des projets
     */
    @FXML
    private void actualiserProjets(ActionEvent event) {
        chargerProjets();
    }

    /**
     * Version alternative de chargement utilisant ApiService
     * (gardée au cas où vous voudriez l'utiliser)
     */
    private void chargerProjetsAvecApiService() {
        // Vérifier l'authentification avant de faire la requête
        if (!verifierAuthentification()) {
            return;
        }

        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }

        if (lblStatut != null) {
            lblStatut.setText("Chargement des projets...");
        }

        Task<List<ProjetDTO>> task = new Task<List<ProjetDTO>>() {
            @Override
            protected List<ProjetDTO> call() throws Exception {
                // Utiliser ApiService.getProjetsParGroupe() si cette méthode existe
                List<ProjetDTO> tousProjets = apiService.getProjetsParGroupe(GROUPE_ID).get();

                // Si ApiService n'a pas de méthode pour filtrer par groupe,
                // vous pouvez utiliser une méthode générale et filtrer ici
                return tousProjets.stream()
                        .filter(projet -> projet.getGroupeId() != null && projet.getGroupeId().equals(GROUPE_ID))
                        .collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<ProjetDTO> projets = getValue();
                    projetsData.clear();
                    projetsData.addAll(projets);

                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }

                    if (lblStatut != null) {
                        lblStatut.setText(projets.size() + " projet(s) du groupe informatique chargé(s)");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }

                    Throwable exception = getException();
                    String errorMessage = exception.getMessage();

                    if (lblStatut != null) {
                        lblStatut.setText("Erreur lors du chargement");
                    }

                    // Gestion spécifique des erreurs d'authentification
                    if (errorMessage.contains("403") || errorMessage.contains("Authentification requise")) {
                        showAuthenticationError();
                    } else if (errorMessage.contains("401")) {
                        showTokenExpiredError();
                    } else {
                        showErrorAlert("Erreur de chargement",
                                "Impossible de charger les projets: " + errorMessage);
                    }
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Affiche les détails d'un projet
     */
    private void afficherDetailsProjet(ProjetDTO projet) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Projet");
        alert.setHeaderText(projet.getNomCourt());

        // Récupérer l'utilisateur actuel pour afficher ses informations
        UtilisateurDTO utilisateur = authService.getCurrentUserSync();

        String details = String.format(
                "Nom complet: %s%n" +
                        "Thème: %s%n" +
                        "Statut: %s%n" +
                        "Date de creation: %d%n" +
                        "Groupe : Informatique%n" +
                        "Utilisateur : %s%n",
                projet.getNomCourt() != null ? projet.getNomCourt() : "Non défini",
                projet.getTheme() != null ? projet.getTheme() : "Non défini",
                projet.getStatut() != null ? projet.getStatut() : "Non défini",
                projet.getDateCreation(),
                utilisateur != null ? utilisateur.getNom() : "Non connecté"
        );

        if (projet.getDescription() != null && !projet.getDescription().isEmpty()) {
            details += "Description: " + projet.getDescription();
        }

        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * Affiche une erreur d'authentification
     */
    private void showAuthenticationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur d'authentification");
        alert.setHeaderText("Authentification requise");
        alert.setContentText("Vous devez vous connecter pour accéder à cette fonctionnalité. " +
                "Veuillez vous reconnecter.");

        ButtonType loginButton = new ButtonType("Se connecter");
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(loginButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == loginButton) {
                // Ici, vous pouvez déclencher l'ouverture de la fenêtre de login
                // Par exemple : ouvrirFenetreLogin();
                System.out.println("Redirection vers la page de login nécessaire");
            }
        });
    }

    /**
     * Affiche une erreur de token expiré
     */
    private void showTokenExpiredError() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Session expirée");
        alert.setHeaderText("Votre session a expiré");
        alert.setContentText("Votre session a expiré. Veuillez vous reconnecter.");

        ButtonType loginButton = new ButtonType("Se reconnecter");
        ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(loginButton, cancelButton);

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == loginButton) {
                // Nettoyer les données d'authentification
                authService.clearToken();
                // Rediriger vers la page de login
                System.out.println("Redirection vers la page de login nécessaire");
            }
        });
    }

    /**
     * Affiche une alerte d'erreur
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'information
     */
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'avertissement
     */
    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Méthode pour rafraîchir l'interface après une connexion réussie
     * À appeler depuis l'extérieur après une connexion
     */
    public void onUserLoggedIn() {
        Platform.runLater(() -> {
            if (lblStatut != null) {
                lblStatut.setText("Utilisateur connecté - Chargement des projets...");
            }
            chargerProjets();
        });
    }

    /**
     * Méthode utilitaire pour obtenir le service de projets
     * @return ProjetService instance
     */
    public ProjetService getProjetService() {
        return projetService;
    }

    /**
     * Méthode utilitaire pour obtenir les données des projets
     * @return Liste observable des projets
     */
    public ObservableList<ProjetDTO> getProjetsData() {
        return projetsData;
    }

    /**
     * Méthode pour forcer le rechargement des projets depuis l'extérieur
     */
    public void forceReloadProjets() {
        Platform.runLater(this::chargerProjets);
    }
}