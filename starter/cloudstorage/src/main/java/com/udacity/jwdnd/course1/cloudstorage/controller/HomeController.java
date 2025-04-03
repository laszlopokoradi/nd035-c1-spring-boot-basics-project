package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.config.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import static com.udacity.jwdnd.course1.cloudstorage.controller.SecurityController.*;


@Controller
public class HomeController {
    private final UserService userService;
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialService credentialService;

    public HomeController(UserService userService, FileService fileService, NoteService noteService, CredentialService credentialService) {
        this.userService = userService;
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
    }

    @GetMapping("/home")
    public String getHomePage(@RequestParam(value = "tab", defaultValue = "files") String tab, Authentication authentication, Model model,
                              RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute("error", Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute("error", Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        File[] usersFiles = fileService.getFilesByUserId(user.getUserId());
        model.addAttribute("files", usersFiles);

        Note[] usersNotes = noteService.getNotesByUsername(user.getUsername());
        model.addAttribute("notes", usersNotes);
        model.addAttribute("noteModel", new Note());

        Credential[] usersCredentials = credentialService.getDecryptedCredentialsByUser(user);
        model.addAttribute("credentials", usersCredentials);
        model.addAttribute("credentialModel", new Credential());

        model.addAttribute("activeTab", tab);

        return "home";
    }
}
