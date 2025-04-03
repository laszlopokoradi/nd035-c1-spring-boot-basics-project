package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.config.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import java.util.*;

import static com.udacity.jwdnd.course1.cloudstorage.controller.SecurityController.*;


@Controller
@RequestMapping("/notes")
public class NoteController {
    public static final String HOME_TAB_NOTES = "/home?tab=notes";
    public static final String NOTES_ERROR = "notes_error";
    public static final String NOTES_SUCCESS = "notes_success";

    private final UserService userService;
    private final NoteService noteService;

    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping("/set")
    public String setNote(@ModelAttribute("noteModel") Note noteForm, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        if (!isNoteValid(noteForm)) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note title and description cannot be empty!");
            return "redirect:" + HOME_TAB_NOTES;
        }

        if (noteForm.getNoteId() == null) {
            createNote(noteForm, redirectAttrs, user);
        } else {
            updateNote(noteForm, redirectAttrs, user);
        }

        return "redirect:" + HOME_TAB_NOTES;
    }

    @PostMapping("/{noteId}/delete")
    public String deleteNote(@PathVariable Integer noteId, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        Note note = noteService.getNoteById(noteId);
        if (note == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note not found!");
            return "redirect:" + HOME_TAB_NOTES;
        }

        if (!note.getUserId()
                 .equals(user.getUserId())) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "You do not have permission to delete this note!");
            return "redirect:" + HOME_TAB_NOTES;
        }

        if (noteService.deleteNote(noteId) != 1) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note deletion failed!");
            return "redirect:" + HOME_TAB_NOTES;
        }

        redirectAttrs.addFlashAttribute(NOTES_SUCCESS, "Note deleted successfully!");
        return "redirect:" + HOME_TAB_NOTES;
    }

    private void createNote(Note noteForm, RedirectAttributes redirectAttrs, User user) {
        noteForm.setUserId(user.getUserId());

        if (isNoteNotUnique(noteForm, user)) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note with this title already exists!");
            return;
        }

        if (noteService.createNote(noteForm) != 1) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note creation failed!");
            return;
        }

        redirectAttrs.addFlashAttribute(NOTES_SUCCESS, "Note created successfully!");
    }

    private void updateNote(Note noteForm, RedirectAttributes redirectAttrs, User user) {
        Note existingNote = noteService.getNoteById(noteForm.getNoteId());
        if (existingNote == null) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note not found");
            return;
        }

        if (!existingNote.getUserId()
                         .equals(user.getUserId())) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "You do not have permission to update this note");
            return;
        }

        if (!existingNote.getNoteTitle().equals(noteForm.getNoteTitle())
            && isNoteNotUnique(noteForm, user)) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note with this title already exists!");
            return;
        }

        if (noteService.updateNote(noteForm) != 1) {
            redirectAttrs.addFlashAttribute(NOTES_ERROR, "Note update failed!");
            return;
        }

        redirectAttrs.addFlashAttribute(NOTES_SUCCESS, "Note updated successfully!");
    }

    private boolean isNoteValid(Note note) {
        return note.getNoteTitle() != null
               && !note.getNoteTitle().isEmpty()
               && note.getNoteDescription() != null
               && !note.getNoteDescription().isEmpty();
    }

    private boolean isNoteNotUnique(Note noteForm, User user) {
        Note[] existingNotes = noteService.getNotesByUserId(user.getUserId());
        return Arrays.stream(existingNotes)
                     .anyMatch(
                             n -> n.getNoteTitle()
                                   .equals(noteForm.getNoteTitle())
                     );
    }
}
