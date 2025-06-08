package gui.javafrontend.dto;

public  class RegisterRequestDTO {
    public String nom;
    public String prenom;
    public String email;
    public String password;

    public RegisterRequestDTO(String nom, String prenom, String email, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
    }
}

