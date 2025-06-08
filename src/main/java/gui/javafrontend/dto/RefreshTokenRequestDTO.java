package gui.javafrontend.dto;

public class RefreshTokenRequestDTO {
    private long id;

    private String refreshToken;

    public RefreshTokenRequestDTO(long id, String refreshToken) {
        this.id = id;
        this.refreshToken = refreshToken;
    }

    public RefreshTokenRequestDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

