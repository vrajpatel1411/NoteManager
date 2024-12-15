package org.vrajpatel.notemanager.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.vrajpatel.notemanager.config.JwtProvider;
import org.vrajpatel.notemanager.exception.NoteNotFoundException;
import org.vrajpatel.notemanager.exception.UserException;
import org.vrajpatel.notemanager.model.Notes;
import org.vrajpatel.notemanager.model.Users;
import org.vrajpatel.notemanager.repository.NotesRepository;
import org.vrajpatel.notemanager.repository.UsersRepository;
import org.vrajpatel.notemanager.request.ApiResponse;
import org.vrajpatel.notemanager.request.ShareRequestBody;
import org.vrajpatel.notemanager.request.UpdateNoteRequestBody;
import org.vrajpatel.notemanager.response.NoteResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceUnitTest {

    @Mock
    private NotesRepository notesRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private NoteService noteService;

    private Users testUser;
    private Notes testNote;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNotes(new ArrayList<>());

        testNote = new Notes();
        testNote.setId(1L);
        testNote.setTitle("Test Note");
        testNote.setContent("This is a test note.");
        testNote.setOwner(testUser);
    }

    @AfterEach
    void tearDown(){
        notesRepository.delete(testNote);
        usersRepository.delete(testUser);
    }

    @Test
    void testGetAllNotes_Success() throws UserException {
        String jwt = "valid.jwt.token";
        when(jwtProvider.getEmailFromToken(jwt)).thenReturn(testUser.getEmail());
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(notesRepository.findAllByEmail(testUser.getEmail())).thenReturn(List.of(testNote));
        ResponseEntity<ApiResponse<List<NoteResponse>>> response = noteService.getAllNotes(jwt);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertFalse(((List<?>) apiResponse.getData()).isEmpty());
        verify(notesRepository, times(1)).findAllByEmail(testUser.getEmail());
    }

    @Test
    void testAddNote_Success() throws UserException {
        String jwt = "valid.jwt.token";
        when(jwtProvider.getEmailFromToken(jwt)).thenReturn(testUser.getEmail());
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(notesRepository.save(testNote)).thenReturn(testNote);
        ResponseEntity<ApiResponse<String>> response = noteService.addNote(testNote, jwt);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Note saved successfully", apiResponse.getMessage());
        verify(notesRepository, times(1)).save(testNote);
        verify(usersRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateNote_Success() throws UserException, NoteNotFoundException {
        String jwt = "valid.jwt.token";
        UpdateNoteRequestBody updatedNote = new UpdateNoteRequestBody();
        updatedNote.setTitle("Updated Title");
        when(jwtProvider.getEmailFromToken(jwt)).thenReturn(testUser.getEmail());
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(notesRepository.findById(testNote.getId())).thenReturn(Optional.of(testNote));
        ResponseEntity<ApiResponse<String>> response = noteService.updateNote(1, jwt, updatedNote);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Note updated successfully", apiResponse.getMessage());
        verify(notesRepository, times(1)).save(testNote);
    }

    @Test
    void testDeleteNote_Success() throws UserException, NoteNotFoundException {
        String jwt = "valid.jwt.token";
        when(jwtProvider.getEmailFromToken(jwt)).thenReturn(testUser.getEmail());
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(notesRepository.findById(testNote.getId())).thenReturn(Optional.of(testNote));
        ResponseEntity<ApiResponse<String>> response = noteService.deleteNote(1, jwt);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Note deleted successfully", apiResponse.getMessage());
        verify(notesRepository, times(1)).deleteById(testNote.getId());
    }

    @Test
    void testShareNote_Success() throws UserException, NoteNotFoundException {
        String jwt = "valid.jwt.token";
        ShareRequestBody shareRequestBody = new ShareRequestBody();
        shareRequestBody.setEmail("shareduser@example.com");
        Users sharedUser = new Users();
        sharedUser.setEmail(shareRequestBody.getEmail());
        when(jwtProvider.getEmailFromToken(jwt)).thenReturn(testUser.getEmail());
        when(usersRepository.findByEmail(testUser.getEmail())).thenReturn(testUser);
        when(notesRepository.findById(testNote.getId())).thenReturn(Optional.of(testNote));
        when(usersRepository.findByEmail(shareRequestBody.getEmail())).thenReturn(sharedUser);
        ResponseEntity<ApiResponse<String>> response = noteService.shareNote(1, shareRequestBody, jwt);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Note shared successfully", apiResponse.getMessage());
        verify(notesRepository, times(1)).save(testNote);
    }

    @Test
    void testSearchNotesByQuery_Success() {
        String query = "Test";
        when(notesRepository.searchNotesByQuery(query)).thenReturn(List.of(testNote));
        ResponseEntity<ApiResponse<List<NoteResponse>>> response = noteService.searchNotesByQuery(query);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ApiResponse<?> apiResponse = response.getBody();
        assertNotNull(apiResponse);
        assertEquals("Success", apiResponse.getMessage());
        assertFalse(((List<?>) apiResponse.getData()).isEmpty());
        verify(notesRepository, times(1)).searchNotesByQuery(query);
    }
}
