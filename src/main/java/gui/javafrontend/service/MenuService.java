package gui.javafrontend.service;

import gui.javafrontend.dto.UtilisateurDTO;
import gui.javafrontend.dto.ProjetDTO;
import gui.javafrontend.dto.TacheDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MenuService {
    private static MenuService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final AuthService authService;

    public static class UserStatsDTO {
        private int totalProjects;
        private int totalTasks;
        private int completedTasks;
        private int activeTasks;
        private int acceptedProjects;
        private int pendingProjects;
        private int rejectedProjects;

        public UserStatsDTO() {}

        public UserStatsDTO(int totalProjects, int totalTasks, int completedTasks,
                            int activeTasks, int acceptedProjects, int pendingProjects,
                            int rejectedProjects) {
            this.totalProjects = totalProjects;
            this.totalTasks = totalTasks;
            this.completedTasks = completedTasks;
            this.activeTasks = activeTasks;
            this.acceptedProjects = acceptedProjects;
            this.pendingProjects = pendingProjects;
            this.rejectedProjects = rejectedProjects;
        }

        public int getTotalProjects() { return totalProjects; }
        public void setTotalProjects(int totalProjects) { this.totalProjects = totalProjects; }

        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

        public int getActiveTasks() { return activeTasks; }
        public void setActiveTasks(int activeTasks) { this.activeTasks = activeTasks; }

        public int getAcceptedProjects() { return acceptedProjects; }
        public void setAcceptedProjects(int acceptedProjects) { this.acceptedProjects = acceptedProjects; }

        public int getPendingProjects() { return pendingProjects; }
        public void setPendingProjects(int pendingProjects) { this.pendingProjects = pendingProjects; }

        public int getRejectedProjects() { return rejectedProjects; }
        public void setRejectedProjects(int rejectedProjects) { this.rejectedProjects = rejectedProjects; }
    }

    private MenuService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "http://localhost:8080/api";
        this.authService = AuthService.getInstance();
    }

    public static synchronized MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }

    public CompletableFuture<List<ProjetDTO>> getCurrentUserProjects() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UtilisateurDTO currentUser = authService.getCurrentUserSync();
                if (currentUser == null) {
                    throw new RuntimeException("Aucun utilisateur connecté");
                }

                String url = baseUrl + "/utilisateurs/" + currentUser.getId() + "/projets";
                System.out.println("Récupération des projets depuis: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new TypeReference<List<ProjetDTO>>() {});
                } else {
                    throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération des projets: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<TacheDTO>> getCurrentUserTasks() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                UtilisateurDTO currentUser = authService.getCurrentUserSync();
                if (currentUser == null) {
                    throw new RuntimeException("Aucun utilisateur connecté");
                }

                String url = baseUrl + "/taches/utilisateur/" + currentUser.getId();
                System.out.println("Récupération des tâches depuis: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new TypeReference<List<TacheDTO>>() {});
                } else {
                    throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération des tâches: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<ProjetDTO>> getAllProjects() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = baseUrl + "/projets/liste";
                System.out.println("Récupération de tous les projets depuis: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + authService.getToken())
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(),
                            new TypeReference<List<ProjetDTO>>() {});
                } else {
                    throw new RuntimeException("HTTP Error " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors de la récupération de tous les projets: " + e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<UserStatsDTO> getCurrentUserStats() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CompletableFuture<List<ProjetDTO>> projetsFeature = getCurrentUserProjects();
                CompletableFuture<List<TacheDTO>> tachesFeature = getCurrentUserTasks();

                List<ProjetDTO> projets = projetsFeature.join();
                List<TacheDTO> taches = tachesFeature.join();

                UserStatsDTO stats = new UserStatsDTO();

                // Statistiques des projets
                stats.setTotalProjects(projets.size());
                stats.setAcceptedProjects((int) projets.stream()
                        .filter(p -> "ACCEPTE".equalsIgnoreCase(p.getStatut()) ||
                                "ACCEPTED".equalsIgnoreCase(p.getStatut()) ||
                                "EN_COURS".equalsIgnoreCase(p.getStatut()))
                        .count());
                stats.setPendingProjects((int) projets.stream()
                        .filter(p -> "EN_ATTENTE".equalsIgnoreCase(p.getStatut()) ||
                                "PENDING".equalsIgnoreCase(p.getStatut()) ||
                                "NOUVEAU".equalsIgnoreCase(p.getStatut()))
                        .count());
                stats.setRejectedProjects((int) projets.stream()
                        .filter(p -> "REJETE".equalsIgnoreCase(p.getStatut()) ||
                                "REJECTED".equalsIgnoreCase(p.getStatut()) ||
                                "ANNULE".equalsIgnoreCase(p.getStatut()))
                        .count());

                // Statistiques des tâches
                stats.setTotalTasks(taches.size());
                stats.setCompletedTasks((int) taches.stream()
                        .filter(t -> "TERMINEE".equalsIgnoreCase(t.getEtat()) ||
                                "COMPLETED".equalsIgnoreCase(t.getEtat()) ||
                                "FINIE".equalsIgnoreCase(t.getEtat()))
                        .count());
                stats.setActiveTasks((int) taches.stream()
                        .filter(t -> "EN_COURS".equalsIgnoreCase(t.getEtat()) ||
                                "ACTIVE".equalsIgnoreCase(t.getEtat()) ||
                                "EN_ATTENTE".equalsIgnoreCase(t.getEtat()))
                        .count());

                return stats;
            } catch (Exception e) {
                throw new RuntimeException("Erreur lors du calcul des statistiques", e);
            }
        });
    }

    public CompletableFuture<UserStatsDTO> getCurrentUserStatsWithDetails() {
        return getCurrentUserStats().thenCompose(stats -> {
            return getCurrentUserProjects().thenCompose(projets -> {
                return getCurrentUserTasks().thenApply(taches -> {
                    // Ici vous pouvez ajouter des détails supplémentaires aux stats si nécessaire
                    return stats;
                });
            });
        });
    }

    public CompletableFuture<Void> refreshCache() {
        return CompletableFuture.runAsync(() -> {
            System.out.println("Rafraîchissement du cache MenuService...");
        });
    }
}
