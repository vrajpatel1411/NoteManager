package org.vrajpatel.notemanager.response;

public class SharedUserDTO {
    private String email;

    public SharedUserDTO(String email) {
        this.email = email;
    }

    // Getter and Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

