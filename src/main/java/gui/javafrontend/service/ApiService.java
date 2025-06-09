package gui.javafrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gui.javafrontend.dto.ProjetDTO;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ApiService {
    private static final Logger LOGGER = Logger.getLogger(ApiService.class.getName());
    private static final String BASE_URL = "http://localhost:8080/api";

    // ID de groupe par défaut
    private static final long DEFAULT_GROUP_ID = 1L;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Configuration d'authentification
    private String authToken;
    private String basicAuthCredentials;
    private String apiKey;

    // Référence au service d'authentification
    private final AuthService authService;

    public ApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        // Utiliser l'instance singleton d'AuthService
        this.authService = AuthService.getInstance();
    }

    /**
     * Configure l'authentification par token Bearer
     * @param token Token d'authentification
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        LOGGER.info("Token d'authentification configuré");
    }

    /**
     * Configure l'authentification basique
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     */
    public void setBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        this.basicAuthCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        LOGGER.info("Authentification basique configurée pour l'utilisateur: " + username);
    }

    /**
     * Configure l'API Key
     * @param apiKey Clé API
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        LOGGER.info("API Key configurée");
    }

    /**
     * Crée un builder de requête HTTP avec les en-têtes d'authentification
     * @param url URL de la requête
     * @return HttpRequest.Builder configuré
     */
    private HttpRequest.Builder createAuthenticatedRequest(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("User-Agent", "JavaFX-Desktop-App/1.0");

        // MODIFICATION PRINCIPALE : Utiliser le token du AuthService en priorité
        String currentToken = authService.getCurrentToken();
        if (currentToken != null && !currentToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + currentToken);
            LOGGER.info("Utilisation du token d'AuthService");
        } else if (authToken != null && !authToken.isEmpty()) {
            builder.header("Authorization", "Bearer " + authToken);
            LOGGER.info("Utilisation du token local");
        } else if (basicAuthCredentials != null && !basicAuthCredentials.isEmpty()) {
            builder.header("Authorization", "Basic " + basicAuthCredentials);
            LOGGER.info("Utilisation de l'authentification basique");
        } else if (apiKey != null && !apiKey.isEmpty()) {
            builder.header("X-API-Key", apiKey);
            LOGGER.info("Utilisation de l'API Key");
        } else {
            LOGGER.warning("Aucune authentification configurée - Risque d'erreur 403");
        }

        return builder;
    }

    /**
     * Vérifie si l'utilisateur est authentifié
     * @return true si authentifié, false sinon
     */
    private boolean isAuthenticated() {
        return authService.isLoggedIn() ||
                authToken != null ||
                basicAuthCredentials != null ||
                apiKey != null;
    }

    /**
     * Récupère les projets par groupe de manière asynchrone (groupe par défaut: 1)
     * @return CompletableFuture contenant la liste des projets
     */
    public CompletableFuture<List<ProjetDTO>> getProjetsParGroupe() {
        return getProjetsParGroupe(DEFAULT_GROUP_ID);
    }

    /**
     * Récupère les projets par groupe de manière asynchrone
     * @param groupeId ID du groupe
     * @return CompletableFuture contenant la liste des projets
     */
    public CompletableFuture<List<ProjetDTO>> getProjetsParGroupe(long groupeId) {
        // Vérifier l'authentification avant la requête
        if (!isAuthenticated()) {
            LOGGER.warning("Tentative d'accès sans authentification");
            return CompletableFuture.failedFuture(
                    new RuntimeException("Authentification requise. Veuillez vous connecter.")
            );
        }

        // Validation de l'ID du groupe
        if (groupeId <= 0) {
            LOGGER.warning("ID de groupe invalide: " + groupeId);
            return CompletableFuture.failedFuture(
                    new RuntimeException("ID de groupe invalide: " + groupeId)
            );
        }

        String url = BASE_URL + "/projets/par-groupe/" + groupeId;
        LOGGER.info("Récupération des projets pour le groupe: " + groupeId);

        HttpRequest request = createAuthenticatedRequest(url)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(this::parseProjets)
                .exceptionally(this::handleError);
    }

    /**
     * Rejoint un groupe (groupe par défaut: 1)
     * @return CompletableFuture contenant le message de réponse
     */
    public CompletableFuture<String> rejoindreGroupe() {
        return rejoindreGroupe(DEFAULT_GROUP_ID);
    }

    /**
     * Rejoint un groupe
     * @param groupeId ID du groupe à rejoindre
     * @return CompletableFuture contenant le message de réponse
     */
    public CompletableFuture<String> rejoindreGroupe(long groupeId) {
        // Vérifier l'authentification avant la requête
        if (!isAuthenticated()) {
            LOGGER.warning("Tentative de rejoindre un groupe sans authentification");
            return CompletableFuture.failedFuture(
                    new RuntimeException("Authentification requise. Veuillez vous connecter.")
            );
        }

        // Validation de l'ID du groupe
        if (groupeId <= 0) {
            LOGGER.warning("ID de groupe invalide pour rejoindre: " + groupeId);
            return CompletableFuture.failedFuture(
                    new RuntimeException("ID de groupe invalide: " + groupeId)
            );
        }

        String url = BASE_URL + "/groupes/" + groupeId + "/rejoindre";
        LOGGER.info("Tentative de rejoindre le groupe: " + groupeId);

        HttpRequest request = createAuthenticatedRequest(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .exceptionally(throwable -> {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la tentative de rejoindre le groupe", throwable);
                    return "Erreur: " + throwable.getMessage();
                });
    }

    /**
     * Récupère tous les projets disponibles
     * @return CompletableFuture contenant la liste de tous les projets
     */
    public CompletableFuture<List<ProjetDTO>> getTousProjets() {
        // Vérifier l'authentification avant la requête
        if (!isAuthenticated()) {
            LOGGER.warning("Tentative d'accès à tous les projets sans authentification");
            return CompletableFuture.failedFuture(
                    new RuntimeException("Authentification requise. Veuillez vous connecter.")
            );
        }

        String url = BASE_URL + "/projets";
        LOGGER.info("Récupération de tous les projets");

        HttpRequest request = createAuthenticatedRequest(url)
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::handleResponse)
                .thenApply(this::parseProjets)
                .exceptionally(this::handleError);
    }

    /**
     * Teste la connectivité avec l'API
     * @return CompletableFuture<Boolean> indiquant si l'API est accessible
     */
    public CompletableFuture<Boolean> testerConnectivite() {
        String url = BASE_URL + "/health";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> response.statusCode() >= 200 && response.statusCode() < 300)
                .exceptionally(throwable -> {
                    LOGGER.warning("API non accessible: " + throwable.getMessage());
                    return false;
                });
    }

    /**
     * Gère la réponse HTTP et vérifie les erreurs
     * @param response Réponse HTTP
     * @return Corps de la réponse si succès
     * @throws RuntimeException si erreur HTTP
     */
    private String handleResponse(HttpResponse<String> response) {
        LOGGER.info("Réponse reçue - Code: " + response.statusCode());
        LOGGER.info("Corps de la réponse: " + (response.body() != null ? response.body().substring(0, Math.min(200, response.body().length())) : "null"));

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            String body = response.body();
            if (body == null || body.trim().isEmpty()) {
                LOGGER.warning("Réponse vide reçue du serveur");
                return "[]"; // Retourner un tableau JSON vide par défaut
            }
            return body;
        } else {
            String errorMessage;
            String responseBody = response.body() != null ? response.body() : "Pas de détails";

            switch (response.statusCode()) {
                case 400:
                    errorMessage = "Erreur de requête (400) - Paramètres invalides: " + responseBody;
                    break;
                case 401:
                    errorMessage = "Erreur d'authentification (401) - Token expiré ou invalide. Veuillez vous reconnecter.";
                    // Optionnel : déclencher une reconnection automatique
                    Platform.runLater(() -> {
                        System.err.println("Token expiré - Reconnection nécessaire");
                    });
                    break;
                case 403:
                    errorMessage = "Accès refusé (403) - Permissions insuffisantes ou authentification manquante. Vérifiez que vous êtes connecté.";
                    break;
                case 404:
                    errorMessage = "Ressource non trouvée (404) - Vérifiez l'URL de l'API ou l'ID du groupe";
                    break;
                case 500:
                    errorMessage = "Erreur serveur (500) - Problème côté serveur: " + responseBody;
                    break;
                default:
                    errorMessage = String.format("Erreur API: %d - %s", response.statusCode(), responseBody);
            }

            LOGGER.severe(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Parse la réponse JSON en liste de projets avec validation renforcée
     * @param jsonResponse Réponse JSON
     * @return Liste des projets
     * @throws RuntimeException si erreur de parsing
     */
    private List<ProjetDTO> parseProjets(String jsonResponse) {
        try {
            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                LOGGER.warning("Réponse JSON vide reçue");
                return Collections.emptyList();
            }

            // Vérifier si la réponse est un JSON valide
            if (!jsonResponse.trim().startsWith("[") && !jsonResponse.trim().startsWith("{")) {
                LOGGER.warning("Format de réponse inattendu: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())));
                return Collections.emptyList();
            }

            // Log the raw JSON for debugging
            LOGGER.info("Raw JSON response: " + jsonResponse);

            // Configure ObjectMapper to handle null values gracefully
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            // Configure to skip null values and unknown properties
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<ProjetDTO> projets;

            try {
                projets = mapper.readValue(jsonResponse, new TypeReference<List<ProjetDTO>>() {});
            } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
                LOGGER.severe("Erreur de mapping JSON - données malformées: " + e.getMessage());
                // Try to parse as a single object if array parsing fails
                try {
                    ProjetDTO singleProjet = mapper.readValue(jsonResponse, ProjetDTO.class);
                    projets = singleProjet != null ? List.of(singleProjet) : Collections.emptyList();
                } catch (Exception singleParseException) {
                    LOGGER.severe("Impossible de parser comme objet unique également: " + singleParseException.getMessage());
                    return Collections.emptyList();
                }
            }

            // Vérifier que les projets ne sont pas null
            if (projets == null) {
                LOGGER.warning("Liste de projets null après parsing");
                return Collections.emptyList();
            }

            // Filtrer les projets null éventuels et valider les données essentielles
            List<ProjetDTO> validProjets = projets.stream()
                    .filter(Objects::nonNull)
                    .filter(projet -> {
                        // Vérifier que les propriétés essentielles ne sont pas null
                        if (projet.getId() == null) {
                            LOGGER.warning("Projet avec ID null ignoré");
                            return false;
                        }
                        if (projet.getNom() == null || projet.getNom().trim().isEmpty()) {
                            LOGGER.warning("Projet avec nom null/vide ignoré (ID: " + projet.getId() + ")");
                            return false;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            LOGGER.info("Projets valides parsés: " + validProjets.size() + " sur " + projets.size() + " éléments");

            // Log des détails des projets pour debugging
            validProjets.forEach(projet -> {
                LOGGER.fine("Projet valide: ID=" + projet.getId() + ", Nom=" + projet.getNom());
            });

            return validProjets;

        } catch (IOException e) {
            String errorMessage = "Erreur lors du parsing JSON: " + e.getMessage() +
                    "\nContenu reçu: " + (jsonResponse != null ? jsonResponse.substring(0, Math.min(200, jsonResponse.length())) : "null");
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            String errorMessage = "Erreur inattendue lors du parsing: " + e.getMessage();
            LOGGER.log(Level.SEVERE, errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Gère les erreurs de manière centralisée avec plus de détails
     * @param throwable Exception capturée
     * @return Liste vide en cas d'erreur
     */
    private List<ProjetDTO> handleError(Throwable throwable) {
        String errorMessage = "Erreur lors de la récupération des projets: " + throwable.getMessage();
        LOGGER.log(Level.SEVERE, errorMessage, throwable);

        // Afficher l'erreur sur le thread JavaFX si nécessaire
        Platform.runLater(() -> {
            System.err.println("Erreur API: " + throwable.getMessage());

            // Si c'est une erreur d'authentification, suggérer une reconnection
            if (throwable.getMessage() != null &&
                    (throwable.getMessage().contains("401") || throwable.getMessage().contains("403"))) {
                System.err.println("Suggestion: Vérifiez votre authentification et reconnectez-vous si nécessaire.");
            }
        });

        return Collections.emptyList();
    }

    /**
     * Méthode utilitaire pour obtenir l'ID du groupe par défaut
     * @return ID du groupe par défaut
     */
    public static long getDefaultGroupId() {
        return DEFAULT_GROUP_ID;
    }

    /**
     * Ferme proprement le service
     */
    public void shutdown() {
        LOGGER.info("Fermeture du service API");
    }
}