package org.vrajpatel.notemanager.response;

import org.vrajpatel.notemanager.model.Notes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NoteResponse {


    private String title;
    private String content;


    public NoteResponse(Notes note) {

        this.title = note.getTitle();
        this.content = note.getContent();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
