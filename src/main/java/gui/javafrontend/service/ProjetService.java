package gui.javafrontend.service;

import gui.javafrontend.dto.ProjetDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.io.IOException;

/**
 * Enhanced ProjetService with comprehensive error handling, validation, and logging
 * Combines functionality from multiple implementations for a robust solution
 */
public class ProjetService {
    private static ProjetService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final AuthService authService;

    // Configuration constants
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRIES = 3;
    private static final String USER_AGENT = "JavaFX-Client/1.0";

    private ProjetService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "http://localhost:8080";
        this.authService = AuthService.getInstance();
    }

    public static synchronized ProjetService getInstance() {
        if (instance == null) {
            instance = new ProjetService();
        }
        return instance;
    }

    /**
     * Creates a new project with comprehensive error handling and validation
     *
     * @param projetDTO The project data to create
     * @return CompletableFuture containing the created project
     */
    public CompletableFuture<ProjetDTO> creerProjet(ProjetDTO projetDTO) {
        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            try {
                // Pre-request validation
                validateProjetDTO(projetDTO);
                validateAuthentication();

                // Build request
                HttpRequest request = buildCreateProjectRequest(projetDTO);

                // Log request details
                logRequestDetails(request, projetDTO);

                // Send request and handle response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Log response details
                logResponseDetails(response, startTime);

                // Process response
                return processCreateProjectResponse(response);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                String errorMsg = "Requête interrompue lors de la création du projet";
                System.err.println("✗ " + errorMsg);
                throw new RuntimeException(errorMsg, e);

            } catch (IOException e) {
                String errorMsg = "Erreur de communication avec le serveur: " + e.getMessage();
                System.err.println("✗ " + errorMsg);
                throw new RuntimeException(errorMsg, e);

            } catch (ValidationException e) {
                String errorMsg = "Données du projet invalides: " + e.getMessage();
                System.err.println("✗ " + errorMsg);
                throw new RuntimeException(errorMsg, e);

            } catch (AuthenticationException e) {
                String errorMsg = "Problème d'authentification: " + e.getMessage();
                System.err.println("✗ " + errorMsg);
                throw new RuntimeException(errorMsg, e);

            } catch (Exception e) {
                String errorMsg = "Erreur inattendue lors de la création du projet: " +
                        (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
                System.err.println("✗ " + errorMsg);
                e.printStackTrace();
                throw new RuntimeException(errorMsg, e);
            }
        });
    }

    /**
     * Creates a project with retry mechanism for better reliability
     */
    public CompletableFuture<ProjetDTO> creerProjetAvecRetry(ProjetDTO projetDTO, int maxRetries) {
        return CompletableFuture.supplyAsync(() -> {
            Exception lastException = null;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    System.out.println("Tentative " + attempt + "/" + maxRetries + " de création du projet");

                    return creerProjet(projetDTO).get(); // Blocking call to get result

                } catch (Exception e) {
                    lastException = e;
                    System.err.println("✗ Échec tentative " + attempt + ": " + e.getMessage());

                    if (attempt < maxRetries) {
                        try {
                            // Exponential backoff: wait 2^attempt seconds
                            long waitTime = (long) Math.pow(2, attempt) * 1000;
                            System.out.println("Attente de " + waitTime + "ms avant nouvelle tentative...");
                            Thread.sleep(waitTime);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrompu", ie);
                        }
                    }
                }
            }

            // All retries failed
            String errorMsg = "Échec de création après " + maxRetries + " tentatives";
            System.err.println("✗ " + errorMsg);
            throw new RuntimeException(errorMsg, lastException);
        });
    }

    /**
     * Récupère un projet par son ID
     */
    public CompletableFuture<ProjetDTO> getProjetById(Long projetId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets/" + projetId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode())) {
                    return objectMapper.readValue(response.body(), ProjetDTO.class);
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    throw new RuntimeException(errorMessage);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération du projet ID " + projetId + ": " + e.getMessage());
                throw new RuntimeException("Erreur lors de la récupération du projet", e);
            }
        });
    }


    /**
     * Récupère tous les projets
     */
    public CompletableFuture<List<ProjetDTO>> getAllProjets() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets/liste";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode())) {
                    TypeReference<List<ProjetDTO>> typeRef = new TypeReference<List<ProjetDTO>>() {};
                    return objectMapper.readValue(response.body(), typeRef);
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    throw new RuntimeException(errorMessage);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des projets: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la récupération des projets", e);
            }
        });
    }

    /**
     * Met à jour un projet existant
     */
    public CompletableFuture<ProjetDTO> updateProjet(Long projetId, ProjetDTO projetDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateProjetDTO(projetDTO);
                validateAuthentication();

                String url = baseUrl + "/api/projets/" + projetId;
                String requestBody = objectMapper.writeValueAsString(projetDTO);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode())) {
                    ProjetDTO projetMisAJour = objectMapper.readValue(response.body(), ProjetDTO.class);
                    System.out.println("✓ Projet mis à jour avec succès: " + projetMisAJour.getNomLong());
                    return projetMisAJour;
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    throw new RuntimeException(errorMessage);
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour du projet ID " + projetId + ": " + e.getMessage());
                throw new RuntimeException("Erreur lors de la mise à jour du projet", e);
            }
        });
    }

    /**
     * Supprime un projet
     */
    public CompletableFuture<Boolean> deleteProjet(Long projetId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets/" + projetId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .DELETE()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode()) || response.statusCode() == 204) {
                    System.out.println("✓ Projet ID " + projetId + " supprimé avec succès");
                    return true;
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    throw new RuntimeException(errorMessage);
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la suppression du projet ID " + projetId + ": " + e.getMessage());
                throw new RuntimeException("Erreur lors de la suppression du projet", e);
            }
        });
    }

    /**
     * Recherche des projets avec un terme de recherche
     */
    public CompletableFuture<List<ProjetDTO>> rechercherProjetsAvecRefresh(String termeRecherche) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets/recherche?q=" +
                        java.net.URLEncoder.encode(termeRecherche, "UTF-8");

                System.out.println("=== RECHERCHE PROJETS ===");
                System.out.println("Terme: " + termeRecherche);
                System.out.println("URL: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode())) {
                    TypeReference<List<ProjetDTO>> typeRef = new TypeReference<List<ProjetDTO>>() {};
                    List<ProjetDTO> resultats = objectMapper.readValue(response.body(), typeRef);
                    System.out.println("✓ Recherche réussie: " + resultats.size() + " projets trouvés");
                    return resultats;
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    System.err.println("✗ Erreur recherche: " + errorMessage);
                    throw new RuntimeException(errorMessage);
                }

            } catch (Exception e) {
                System.err.println("✗ Exception lors de la recherche: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la recherche de projets: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Méthode compatible avec le contrôleur existant
     */
    public CompletableFuture<ProjetDTO> ajouterProjetAvecRefresh(ProjetDTO projetDTO) {
        return creerProjet(projetDTO)
                .thenCompose(projetCree -> {
                    // Optionally refresh data or perform additional operations
                    System.out.println("✓ Projet ajouté, rafraîchissement des données...");

                    // You can add refresh logic here if needed
                    // For now, just return the created project
                    return CompletableFuture.completedFuture(projetCree);
                })
                .exceptionally(throwable -> {
                    System.err.println("✗ Erreur lors de l'ajout avec rafraîchissement: " + throwable.getMessage());
                    throw new RuntimeException(throwable);
                });
    }

    /**
     * Obtient tous les projets - Alias pour getAllProjets()
     */
    public CompletableFuture<List<ProjetDTO>> obtenirTousProjets() {
        return getAllProjets();
    }

    /**
     * Récupère les projets d'un groupe spécifique
     */
    public CompletableFuture<List<ProjetDTO>> getProjetsByGroupe(Long groupeId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets/groupe/" + groupeId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (isSuccessStatus(response.statusCode())) {
                    TypeReference<List<ProjetDTO>> typeRef = new TypeReference<List<ProjetDTO>>() {};
                    return objectMapper.readValue(response.body(), typeRef);
                } else {
                    String errorMessage = parseErrorResponse(response.body(), response.statusCode());
                    throw new RuntimeException(errorMessage);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des projets du groupe " + groupeId + ": " + e.getMessage());
                throw new RuntimeException("Erreur lors de la récupération des projets du groupe", e);
            }
        });
    }

    // =============== PRIVATE HELPER METHODS ===============

    /**
     * Validates the project DTO before sending to server
     */
    private void validateProjetDTO(ProjetDTO projetDTO) throws ValidationException {
        if (projetDTO == null) {
            throw new ValidationException("Les données du projet sont manquantes");
        }

        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (isNullOrEmpty(projetDTO.getNomCourt())) {
            errors.append("- Le nom court est obligatoire\n");
        } else if (projetDTO.getNomCourt().length() > 50) {
            errors.append("- Le nom court ne peut pas dépasser 50 caractères\n");
        }

        if (isNullOrEmpty(projetDTO.getNomLong())) {
            errors.append("- Le nom long est obligatoire\n");
        } else if (projetDTO.getNomLong().length() > 200) {
            errors.append("- Le nom long ne peut pas dépasser 200 caractères\n");
        }

        if (isNullOrEmpty(projetDTO.getDescription())) {
            errors.append("- La description est obligatoire\n");
        }

        if (isNullOrEmpty(projetDTO.getTheme())) {
            errors.append("- Le thème est obligatoire\n");
        }

        if (isNullOrEmpty(projetDTO.getType())) {
            errors.append("- Le type est obligatoire\n");
        }

        if (projetDTO.getGroupeId() == null || projetDTO.getGroupeId() <= 0) {
            errors.append("- L'ID du groupe est obligatoire et doit être valide\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException("Erreurs de validation:\n" + errors.toString());
        }

        System.out.println("✓ Validation ProjetDTO réussie");
    }

    /**
     * Validates user authentication
     */
    private void validateAuthentication() throws AuthenticationException {
        if (!authService.isLoggedIn()) {
            throw new AuthenticationException("Utilisateur non connecté");
        }

        String token = authService.getToken();
        if (token == null || token.trim().isEmpty()) {
            throw new AuthenticationException("Token d'authentification manquant");
        }

    }

    /**
     * Builds the HTTP request for creating a project
     */
    private HttpRequest buildCreateProjectRequest(ProjetDTO projetDTO) throws IOException {
        String url = baseUrl + "/api/projets/creer";
        if (projetDTO.getGroupeId() != null) {
            url += "?groupeId=" + projetDTO.getGroupeId();
        }

        String requestBody = objectMapper.writeValueAsString(projetDTO);

        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authService.getToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();
    }

    /**
     * Logs request details for debugging
     */
    private void logRequestDetails(HttpRequest request, ProjetDTO projetDTO) {
        System.out.println("=== REQUÊTE CRÉATION PROJET ===");
        System.out.println("URL: " + request.uri());
        System.out.println("Method: " + request.method());
        System.out.println("Headers: " + request.headers().map());

        try {
            String bodyPreview = objectMapper.writeValueAsString(projetDTO);
            System.out.println("Body: " + (bodyPreview.length() > 500 ?
                    bodyPreview.substring(0, 500) + "..." : bodyPreview));
        } catch (Exception e) {
            System.out.println("Body: [Error serializing body]");
        }

        System.out.println("Token: " + (authService.getToken() != null ? "Présent" : "Absent"));
        System.out.println("===============================");
    }

    /**
     * Logs response details for debugging
     */
    private void logResponseDetails(HttpResponse<String> response, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("=== RÉPONSE SERVEUR ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Headers: " + response.headers().map());
        System.out.println("Duration: " + duration + "ms");

        String body = response.body();
        if (body != null) {
            System.out.println("Body: " + (body.length() > 1000 ?
                    body.substring(0, 1000) + "..." : body));
        }
        System.out.println("======================");
    }

    /**
     * Processes the create project response
     */
    private ProjetDTO processCreateProjectResponse(HttpResponse<String> response) throws IOException {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (isSuccessStatus(statusCode)) {
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new RuntimeException("Réponse vide du serveur malgré le statut de succès");
            }

            try {
                ProjetDTO projetCree = objectMapper.readValue(responseBody, ProjetDTO.class);

                if (projetCree == null) {
                    throw new RuntimeException("Projet créé mais données de réponse nulles");
                }

                System.out.println("✓ Projet créé avec succès: " + projetCree.getNomLong() +
                        " (ID: " + projetCree.getId() + ")");
                return projetCree;

            } catch (JsonProcessingException e) {
                System.err.println("✗ Erreur lors du parsing de la réponse JSON: " + e.getMessage());
                System.err.println("Réponse brute: " + responseBody);
                throw new RuntimeException("Erreur lors du traitement de la réponse du serveur", e);
            }

        } else {
            String errorMessage = parseErrorResponse(responseBody, statusCode);
            System.err.println("✗ Erreur création projet (Status " + statusCode + "): " + errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Checks if the HTTP status code indicates success
     */
    private boolean isSuccessStatus(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Parses error response from server
     */
    private String parseErrorResponse(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return getDefaultErrorMessage(statusCode);
        }

        try {
            // Try to parse structured error response
            JsonNode errorNode = objectMapper.readTree(responseBody);

            if (errorNode.has("message")) {
                return errorNode.get("message").asText();
            }

            if (errorNode.has("error")) {
                return errorNode.get("error").asText();
            }

            if (errorNode.has("details")) {
                return errorNode.get("details").asText();
            }

            // If no structured error, return the raw response
            return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." : responseBody;

        } catch (Exception e) {
            // If JSON parsing fails, return raw response or default message
            System.err.println("Impossible de parser la réponse d'erreur JSON: " + e.getMessage());
            return responseBody.length() > 200 ? responseBody.substring(0, 200) + "..." :
                    (!responseBody.trim().isEmpty() ? responseBody : getDefaultErrorMessage(statusCode));
        }
    }

    /**
     * Gets default error message based on HTTP status code
     */
    private String getDefaultErrorMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Données de la requête invalides";
            case 401:
                return "Authentification requise ou token invalide";
            case 403:
                return "Accès refusé - permissions insuffisantes";
            case 404:
                return "Ressource non trouvée";
            case 409:
                return "Conflit - le projet existe peut-être déjà";
            case 422:
                return "Données non traitables - vérifiez les champs";
            case 500:
                return "Erreur interne du serveur";
            case 502:
                return "Erreur de passerelle - serveur indisponible";
            case 503:
                return "Service temporairement indisponible";
            case 504:
                return "Timeout du serveur";
            default:
                return "Erreur HTTP " + statusCode + " - " + getStatusText(statusCode);
        }
    }

    /**
     * Gets HTTP status text for unknown status codes
     */
    private String getStatusText(int statusCode) {
        if (statusCode >= 400 && statusCode < 500) {
            return "Erreur client";
        } else if (statusCode >= 500) {
            return "Erreur serveur";
        } else {
            return "Statut inconnu";
        }
    }

    /**
     * Utility method to check if string is null or empty
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // =============== PUBLIC UTILITY METHODS ===============

    /**
     * Vérifie si le service est connecté
     */
    public boolean isConnected() {
        return authService.isLoggedIn() && authService.getToken() != null;
    }

    /**
     * Méthode utilitaire pour tester la connexion
     */
    public CompletableFuture<Boolean> testConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                validateAuthentication();

                String url = baseUrl + "/api/projets";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .header("User-Agent", USER_AGENT)
                        .timeout(Duration.ofSeconds(5))
                        .method("HEAD", HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return response.statusCode() < 400;

            } catch (Exception e) {
                System.err.println("Test de connexion échoué: " + e.getMessage());
                return false;
            }
        });
    }

    // =============== CUSTOM EXCEPTIONS ===============

    /**
     * Custom exception for validation errors
     */
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Custom exception for authentication errors
     */
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}