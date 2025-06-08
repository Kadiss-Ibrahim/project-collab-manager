package gui.javafrontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gui.javafrontend.dto.AuthRequestDTO;
import gui.javafrontend.dto.AuthResponseDTO;
import gui.javafrontend.dto.RefreshTokenRequestDTO;
import gui.javafrontend.dto.UtilisateurDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static AuthService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private String currentToken;
    private String currentRefreshToken;
    private UtilisateurDTO currentUser;

    private AuthService() {
        this.baseUrl = "http://localhost:8080/api/auth";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public CompletableFuture<AuthResponseDTO> login(AuthRequestDTO authRequest) {
        try {
            String jsonBody = objectMapper.writeValueAsString(authRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                AuthResponseDTO authResponse = objectMapper.readValue(response.body(), AuthResponseDTO.class);
                                this.currentToken = authResponse.getAccessToken();
                                this.currentRefreshToken = authResponse.getRefreshToken();
                                this.currentUser = authResponse.getUtilisateur();
                                return authResponse;
                            } catch (Exception e) {
                                throw new RuntimeException("Erreur parsing réponse: " + e.getMessage());
                            }
                        } else {
                            throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
                        }
                    });

        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Erreur login: " + e.getMessage()));
        }
    }

    public CompletableFuture<String> register(UtilisateurDTO utilisateur, String password) {
        try {
            // Vérification des données avant envoi
            if (utilisateur == null) {
                throw new RuntimeException("Utilisateur ne peut pas être null");
            }
            if (utilisateur.getNom() == null || utilisateur.getNom().trim().isEmpty()) {
                throw new RuntimeException("Le nom est obligatoire");
            }
            if (utilisateur.getPrenom() == null || utilisateur.getPrenom().trim().isEmpty()) {
                throw new RuntimeException("Le prénom est obligatoire");
            }
            if (utilisateur.getEmail() == null || utilisateur.getEmail().trim().isEmpty()) {
                throw new RuntimeException("L'email est obligatoire");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("Le mot de passe est obligatoire");
            }

            // Créer l'objet de requête selon le format attendu par le backend
            var registerRequest = new RegisterRequestDTO(
                    generateIdentifiant(utilisateur.getEmail()), // Générer un identifiant
                    utilisateur.getNom().trim(),
                    utilisateur.getPrenom().trim(),
                    utilisateur.getEmail().trim(),
                    password,
                    null, // photoProfile
                    true, // actif
                    "USER" // role par défaut
            );

            String jsonBody = objectMapper.writeValueAsString(registerRequest);

            // Log pour debug
            System.out.println("=== REQUÊTE ENVOYÉE ===");
            System.out.println("URL: " + baseUrl + "/inscription");
            System.out.println("JSON Body: " + jsonBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/inscription"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        System.out.println("=== RÉPONSE REÇUE ===");
                        System.out.println("Status Code: " + response.statusCode());
                        System.out.println("Response Body: " + response.body());

                        if (response.statusCode() == 200 || response.statusCode() == 201) {
                            // Le backend retourne un simple message texte, pas un JSON
                            // Donc on retourne directement le message
                            return response.body();
                        } else {
                            String errorBody = response.body();
                            System.err.println("Erreur HTTP " + response.statusCode() + ": " + errorBody);
                            throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + errorBody);
                        }
                    })
                    .exceptionally(throwable -> {
                        System.err.println("Exception lors de l'inscription: " + throwable.getMessage());
                        throwable.printStackTrace();
                        throw new RuntimeException("Erreur inscription: " + throwable.getMessage());
                    });

        } catch (Exception e) {
            System.err.println("Exception avant envoi: " + e.getMessage());
            return CompletableFuture.failedFuture(new RuntimeException("Erreur inscription: " + e.getMessage()));
        }
    }

    // Classe interne pour la requête d'inscription - Format attendu par le backend
    public static class RegisterRequestDTO {
        private String identifiant;
        private String nom;
        private String prenom;
        private String email;
        private String motDePasse;
        private String photoProfile;
        private boolean actif;
        private String role;

        public RegisterRequestDTO(String identifiant, String nom, String prenom, String email,
                                  String motDePasse, String photoProfile, boolean actif, String role) {
            this.identifiant = identifiant;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.motDePasse = motDePasse;
            this.photoProfile = photoProfile;
            this.actif = actif;
            this.role = role;
        }

        // Getters pour Jackson
        public String getIdentifiant() { return identifiant; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getEmail() { return email; }
        public String getMotDePasse() { return motDePasse; }
        public String getPhotoProfile() { return photoProfile; }
        public boolean isActif() { return actif; }
        public String getRole() { return role; }
    }

    // Méthode pour générer un identifiant unique
    private String generateIdentifiant(String email) {
        // Générer un identifiant basé sur l'email et un timestamp
        String baseIdentifiant = email.split("@")[0];
        long timestamp = System.currentTimeMillis();
        return baseIdentifiant + timestamp;
    }

    public CompletableFuture<AuthResponseDTO> refreshToken() {
        if (currentRefreshToken == null || currentUser == null) {
            return CompletableFuture.failedFuture(new RuntimeException("Aucun refresh token"));
        }

        try {
            RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(currentUser.getId(), currentRefreshToken);
            String jsonBody = objectMapper.writeValueAsString(refreshRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/refresh"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                AuthResponseDTO authResponse = objectMapper.readValue(response.body(), AuthResponseDTO.class);
                                this.currentToken = authResponse.getAccessToken();
                                this.currentRefreshToken = authResponse.getRefreshToken();
                                return authResponse;
                            } catch (Exception e) {
                                throw new RuntimeException("Erreur parsing réponse: " + e.getMessage());
                            }
                        } else {
                            throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
                        }
                    });

        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Erreur refresh: " + e.getMessage()));
        }
    }

    public CompletableFuture<Void> logout() {
        if (currentToken == null) {
            clearAuthData();
            return CompletableFuture.completedFuture(null);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/logout"))
                .header("Authorization", "Bearer " + currentToken)
                .POST(HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(30))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .handle((response, ex) -> {
                    clearAuthData();
                    return null;
                });
    }

    public CompletableFuture<UtilisateurDTO> getCurrentUser() {
        if (currentToken == null) {
            return CompletableFuture.failedFuture(new RuntimeException("Utilisateur non connecté"));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/me"))
                .header("Authorization", "Bearer " + currentToken)
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            UtilisateurDTO user = objectMapper.readValue(response.body(), UtilisateurDTO.class);
                            this.currentUser = user;
                            return user;
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur parsing user: " + e.getMessage());
                        }
                    } else {
                        throw new RuntimeException("HTTP Error " + response.statusCode());
                    }
                });
    }

    public boolean isLoggedIn() {
        return currentToken != null && currentUser != null;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public UtilisateurDTO getCurrentUserSync() {
        return currentUser;
    }

    public void setCurrentUser(UtilisateurDTO user) {
        this.currentUser = user;
    }

    private void clearAuthData() {
        this.currentToken = null;
        this.currentRefreshToken = null;
        this.currentUser = null;
    }

    public HttpRequest.Builder createAuthenticatedRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(30));

        if (currentToken != null) {
            builder.header("Authorization", "Bearer " + currentToken);
        }

        return builder;
    }
}