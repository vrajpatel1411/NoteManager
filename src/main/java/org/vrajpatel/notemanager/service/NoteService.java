package org.vrajpatel.notemanager.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoteService {

    @Autowired
    private NotesRepository notesRepository;


    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;



    public ResponseEntity<ApiResponse<List<NoteResponse>>> getAllNotes(String jwt) throws UserException {
        Users user = validateAndGetUser(jwt);
        List<Notes> allNotes = notesRepository.findAllByEmail(user.getEmail());
        return createNoteResponse(allNotes);
    }

    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNoteById(Integer id) throws NoteNotFoundException {
        Optional<Notes> note = notesRepository.findById(Long.valueOf(id));
        if (note.isPresent()) {
            List<NoteResponse> noteResponseList = List.of(new NoteResponse(note.get()));

            return new ResponseEntity<>(
                    new ApiResponse<>("Success", noteResponseList),
                    HttpStatus.OK
            );
        }
        throw new NoteNotFoundException("Note not found with ID: " + id);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> addNote(@Valid Notes note, String jwt) throws UserException {
        Users user = validateAndGetUser(jwt);
        note.setUpdatedAt();
        note.setOwner(user);
        Notes savedNotes = notesRepository.save(note);
        user.getNotes().add(savedNotes);
        usersRepository.save(user);
        return new ResponseEntity<>(new ApiResponse<>("Note saved successfully", null), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> updateNote(Integer id, String jwt, @Valid UpdateNoteRequestBody updatedNote) throws UserException, NoteNotFoundException {
        Users user = validateAndGetUser(jwt);
        Notes note = notesRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoteNotFoundException("Note not found with ID: " + id));

        validateOwnership(user, note);

        if (updatedNote.getTitle() != null && !updatedNote.getTitle().equals(note.getTitle())) {
            note.setTitle(updatedNote.getTitle());
        }
        if (updatedNote.getContent() != null && !updatedNote.getContent().equals(note.getContent())) {
            note.setContent(updatedNote.getContent());
        }

        note.setUpdatedAt();
        notesRepository.save(note);
        return new ResponseEntity<>(new ApiResponse<>("Note updated successfully", null), HttpStatus.OK);
    }

    private Users validateAndGetUser(String jwt) throws UserException {
        String email = jwtProvider.getEmailFromToken(jwt);
        if (email == null) {
            throw new UserException("Invalid or missing JWT token.");
        }
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("User not found with email " + email);
        }
        return user;
    }

    private void validateOwnership(Users user, Notes note) throws UserException {
        if (!user.getEmail().equals(note.getOwner().getEmail())) {
            throw new UserException("You are not the owner of this note.");
        }
    }

    public ResponseEntity<ApiResponse<String>> deleteNote(Integer id, String jwt) throws UserException, NoteNotFoundException {
        Users user = validateAndGetUser(jwt);
        Notes note = notesRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoteNotFoundException("Note not found with ID: " + id));

        validateOwnership(user, note);
        notesRepository.deleteById(Long.valueOf(id));
        return new ResponseEntity<>(new ApiResponse<>("Note deleted successfully", null), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> shareNote(Integer id, @Valid ShareRequestBody userEmail, String jwt) throws UserException, NoteNotFoundException {
        Users user = validateAndGetUser(jwt);
        Notes note = notesRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoteNotFoundException("Note not found with ID: " + id));

        validateOwnership(user, note);

        Users targetUser = usersRepository.findByEmail(userEmail.getEmail());
        if (targetUser == null) {
            throw new UserException("User doesn't exist");
        }
        if (!note.getSharedUsers().add(targetUser)) {
            return new ResponseEntity<>(new ApiResponse<>("Already shared", null), HttpStatus.OK);
        }
        notesRepository.save(note);
        return new ResponseEntity<>(new ApiResponse<>("Note shared successfully", null), HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse<List<NoteResponse>>> searchNotesByQuery(String query) {
        List<Notes> notes = notesRepository.searchNotesByQuery(query);
        return createNoteResponse(notes);
    }

    private ResponseEntity<ApiResponse<List<NoteResponse>>> createNoteResponse(List<Notes> notes) {
        if (notes.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>("Empty", null), HttpStatus.NO_CONTENT);
        }
        List<NoteResponse> notesResponse = notes.stream()
                .map(NoteResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ApiResponse<>("Success", notesResponse), HttpStatus.OK);
    }
}
