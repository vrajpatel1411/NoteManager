package org.vrajpatel.notemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;


import java.util.List;


@Entity
@Table(name="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_seq_generator")
    @SequenceGenerator(
            name="user_seq_generator",
            sequenceName = "user_sequence",
            allocationSize =  1
    )
    private Long id;

    @Email
    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable=false)
    private String password;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Notes> notes;


    public Users() {
    }



    public Users(String email, Long id, List<Notes> notes, String password) {
        this.email = email;
        this.id = id;
        this.notes = notes;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Notes> getNotes() {
        return notes;
    }

    public void setNotes(List<Notes> notes) {
        this.notes = notes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
