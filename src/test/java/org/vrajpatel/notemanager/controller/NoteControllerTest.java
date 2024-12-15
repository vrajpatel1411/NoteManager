
package org.vrajpatel.notemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.vrajpatel.notemanager.model.Notes;
import org.vrajpatel.notemanager.model.Users;
import org.vrajpatel.notemanager.repository.NotesRepository;
import org.vrajpatel.notemanager.repository.UsersRepository;
import org.vrajpatel.notemanager.request.ApiResponse;
import org.vrajpatel.notemanager.request.LoginRequest;
import org.vrajpatel.notemanager.request.ShareRequestBody;
import org.vrajpatel.notemanager.request.UpdateNoteRequestBody;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class NoteControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private UsersRepository usersRepository;

    private String jwtToken;

    private Long noteId;

    private Users testUser;

    private Users tempUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testUser = new Users();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword(passwordEncoder.encode("securepassword"));
        testUser=usersRepository.save(testUser);
        Notes testNote = new Notes();
        testNote.setTitle("Test Note");
        testNote.setContent("This is a test note");
        testNote.setOwner(testUser);
        testNote.setUpdatedAt();
        Notes savedNote = notesRepository.save(testNote);
        noteId = savedNote.getId();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("securepassword");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
        jwtToken = objectMapper.readTree(result.getResponse().getContentAsString()).get("jwt").asText();
    }

    @AfterEach
    public void teardown() {
        notesRepository.deleteById(noteId);
        Users user = usersRepository.findByEmail("testuser@example.com");
        if (user != null) {
            usersRepository.delete(user);
        }

    }

    @Test
    void testGetNoteById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notes/{id}", noteId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty())
                .andReturn();
    }

    @Test
    void testCreateNote() throws Exception {
        Notes note = new Notes();
        note.setTitle("Test Note");
        note.setContent("This is a test note");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/")
                        .header("Authorization", "Bearer " + jwtToken)  // Use actual JWT token
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Note saved successfully"))
                .andReturn();
    }

    @Test
    void testUpdateNote() throws Exception {
        UpdateNoteRequestBody updateNoteRequest = new UpdateNoteRequestBody();
        updateNoteRequest.setTitle("Updated Test Note");
        updateNoteRequest.setContent("This content has been updated");
        mockMvc.perform(MockMvcRequestBuilders.put("/api/notes/{id}", noteId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateNoteRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Note updated successfully"))
                .andReturn();
    }

    @Test
    void testDeleteNote() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/notes/{id}", noteId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Note deleted successfully"))
                .andReturn();
    }




    @Test
    void testShareNote_NotExistingUser() throws Exception {
        ShareRequestBody shareRequestBody = new ShareRequestBody();
        shareRequestBody.setEmail("user1@example.com");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/{id}/share", noteId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(shareRequestBody)))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void testSearchNotes() throws Exception {
        String query = "Test";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/notes/search")
                        .param("q", query)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Success"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andReturn();
    }
}