package org.vrajpatel.notemanager.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.vrajpatel.notemanager.model.Notes;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotesRepository extends JpaRepository<Notes, Long> {

    @Query(value = "SELECT * FROM notes n WHERE n.search_vector @@ to_tsquery('english', :query)", nativeQuery = true)
    List<Notes> searchNotesByQuery(@Param("query") String query);

    @Query("Select n from Notes n where n.owner.email=:email OR :email  in (Select u.email from n.sharedUsers u)")
    List<Notes> findAllByEmail(String email);
}
