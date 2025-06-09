package gui.javafrontend.enums;

/**
 * Enumeration for project status
 * @author ${USERS}
 */
public enum StatutProjet {
    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"), // Changed from ACCEPTER to ACCEPTE
    REFUSE("Refusé"),   // Changed from REFUSER to REFUSE
    ACTIVE("Actif"),    // Added for active projects
    TERMINE("Terminé"), // Added for completed projects
    SUSPENDU("Suspendu"); // Added for suspended projects

    private final String displayName;

    StatutProjet(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}