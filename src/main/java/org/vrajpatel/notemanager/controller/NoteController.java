package org.vrajpatel.notemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vrajpatel.notemanager.exception.NoteNotFoundException;
import org.vrajpatel.notemanager.exception.UserException;
import org.vrajpatel.notemanager.model.Notes;
import org.vrajpatel.notemanager.request.ApiResponse;
import org.vrajpatel.notemanager.request.ShareRequestBody;
import org.vrajpatel.notemanager.request.UpdateNoteRequestBody;
import org.vrajpatel.notemanager.response.NoteResponse;
import org.vrajpatel.notemanager.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {


    @Autowired
    private NoteService noteService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNotes(@RequestHeader("Authorization") String jwt) throws UserException {
        return noteService.getAllNotes(jwt);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNote(@PathVariable Integer id) throws NoteNotFoundException {
        return noteService.getNoteById(id);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<String>> createNote(@RequestBody Notes note,@RequestHeader("Authorization") String jwt) throws UserException {
        try {
            ResponseEntity<ApiResponse<String>> createdNote=noteService.addNote(note,jwt);
            return createdNote;
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), null),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateNote(@PathVariable Integer id,@RequestHeader("Authorization") String jwt, @RequestBody UpdateNoteRequestBody note) throws UserException, NoteNotFoundException {
        try {
            return noteService.updateNote(id,jwt,note);
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), null),HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNote(@PathVariable Integer id,@RequestHeader("Authorization") String jwt) throws UserException, NoteNotFoundException {
        return noteService.deleteNote(id,jwt);
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<ApiResponse<String>> shareNote(@PathVariable Integer id, @RequestBody ShareRequestBody userEmail, @RequestHeader("Authorization") String jwt) throws UserException, NoteNotFoundException {
        return noteService.shareNote(id,userEmail,jwt);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> searchNotes(@RequestParam("q") String query) {
        return noteService.searchNotesByQuery(query);
    }
}
