package com.udacity.jwdnd.course1.cloudstorage.services;


import com.udacity.jwdnd.course1.cloudstorage.mapper.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import org.springframework.stereotype.*;


@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public Integer createNote(Note note) {
        return noteMapper.insertNote(note);
    }

    public Integer updateNote(Note note) {
        return noteMapper.updateNote(note);
    }

    public Note getNoteById(Integer noteId) {
        return noteMapper.getNoteById(noteId);
    }

    public Note[] getNotesByUserId(Integer userId) {
        return noteMapper.getNotesByUserId(userId);
    }

    public Note[] getNotesByUsername(String userName) {
        return noteMapper.getNotesByUsername(userName);
    }

    public Integer deleteNote(Integer noteId) {
        return noteMapper.deleteNoteById(noteId);
    }
}
