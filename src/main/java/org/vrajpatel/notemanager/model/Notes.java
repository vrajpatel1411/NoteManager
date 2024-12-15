package org.vrajpatel.notemanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="notes")
public class Notes {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "notes_seq_generator")
    @SequenceGenerator(name="note_seq_generator",sequenceName = "notes_sequence",allocationSize=1)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title cannot be blank")
    @NotNull
    private String title;

    @Column(nullable = false,columnDefinition = "TEXT")
    @NotBlank(message = "Content cannot be blank")
    @NotNull
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id",nullable = false)
    @JsonIgnore
    private Users owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name="shared_notes",
            joinColumns = @JoinColumn(name="note_id"),
            inverseJoinColumns = @JoinColumn(name="shared_with_id")
    )
    private Set<Users> sharedUsers =new HashSet<>();


    @Type(PostgreSQLTSVectorType.class)
    @Column(columnDefinition = "tsvector")
    private String searchVector;



    public String getSearchVector() {
        return searchVector;
    }

    public void setSearchVector(String searchVector) {
        this.searchVector = searchVector;
    }

    public Notes() {
    }

    public Notes(String content, LocalDateTime createdAt, Long id, Users owner, Set<Users> sharedUsers, String title, LocalDateTime updatedAt) {
        this.content = content;
        this.createdAt = createdAt;
        this.id = id;
        this.owner = owner;
        this.sharedUsers = sharedUsers;
        this.title = title;
        this.updatedAt = updatedAt;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getOwner() {
        return owner;
    }

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public Set<Users> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(Set<Users> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
