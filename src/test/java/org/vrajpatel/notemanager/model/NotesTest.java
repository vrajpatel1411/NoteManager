package org.vrajpatel.notemanager.model;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import org.vrajpatel.notemanager.repository.NotesRepository;
import org.vrajpatel.notemanager.repository.UsersRepository;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotesTest {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Users testUser;
    private Notes testNote;
    @Autowired
    private Validator validator;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        testUser = new Users();
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser = usersRepository.save(testUser);
        testNote = new Notes();
        testNote.setTitle("Test Note");
        testNote.setContent("This is a test note content.");
        testNote.setOwner(testUser);
    }

    @AfterEach
    void tearDown() {
        notesRepository.deleteById(testNote.getId());
        usersRepository.deleteById(testUser.getId());
    }

    @Test
    @Transactional
    void saveNote_Success() {
        Notes savedNote = notesRepository.save(testNote);

        assertNotNull(savedNote.getId());
        assertEquals("Test Note", savedNote.getTitle());
        assertEquals("This is a test note content.", savedNote.getContent());
        assertEquals(testUser.getEmail(), savedNote.getOwner().getEmail());
    }


    @Test
    @Transactional
    void updateNoteContent_Success() {
        Notes savedNote = notesRepository.save(testNote);
        savedNote.setContent("Updated content.");
        notesRepository.save(savedNote);
        Notes updatedNote = notesRepository.findById(savedNote.getId()).orElse(null);
        assertNotNull(updatedNote);
        assertEquals("Updated content.", updatedNote.getContent());
    }

    @Test
    @Transactional
    void associateSharedUsers_Success() {
        Users sharedUser = new Users();
        sharedUser.setEmail("shareduser@example.com");
        sharedUser.setPassword("password");
        usersRepository.save(sharedUser);
        testNote.getSharedUsers().add(sharedUser);
        Notes savedNote = notesRepository.save(testNote);
        Notes retrievedNote = notesRepository.findById(savedNote.getId()).orElse(null);
        assertNotNull(retrievedNote);
        assertTrue(retrievedNote.getSharedUsers().contains(sharedUser));
    }

    @Test
    @Transactional
    void deleteNote_Success() {
        Notes savedNote = notesRepository.save(testNote);
        notesRepository.deleteById(savedNote.getId());
        Optional<Notes> deletedNote = notesRepository.findById(savedNote.getId());
        assertFalse(deletedNote.isPresent());
    }

    @Test
    @Transactional
    void verifySearchVector_Success() {
        testNote.setSearchVector("test search vector");
        Notes savedNote = notesRepository.save(testNote);
        Notes retrievedNote = notesRepository.findById(savedNote.getId()).orElse(null);
        assertNotNull(retrievedNote);
        assertEquals("test search vector", retrievedNote.getSearchVector());
    }
}
