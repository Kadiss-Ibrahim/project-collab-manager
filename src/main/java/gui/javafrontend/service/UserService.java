package gui.javafrontend.service;



import gui.javafrontend.dto.UtilisateurDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private static UserService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final AuthService authService;

    private UserService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "http://localhost:8080/api";
        this.authService = AuthService.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public CompletableFuture<UtilisateurDTO> getUserById(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/utilisateurs/" + userId;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), UtilisateurDTO.class);
                } else {
                    throw new RuntimeException("Erreur " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération de l'utilisateur", e);
            }
        });
    }

    public CompletableFuture<UtilisateurDTO> updateUser(Long userId, UtilisateurDTO userDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/utilisateurs/" + userId;
                String requestBody = objectMapper.writeValueAsString(userDTO);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), UtilisateurDTO.class);
                } else {
                    throw new RuntimeException("Erreur " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e);
            }
        });
    }
}
