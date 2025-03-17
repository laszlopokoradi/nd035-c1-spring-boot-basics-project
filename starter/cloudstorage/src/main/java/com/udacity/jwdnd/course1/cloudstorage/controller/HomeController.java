package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeController {

    private final UserService userService;
    private final FileService fileService;

    public HomeController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }


    @GetMapping("/home")
    public String getHomePage(Authentication authentication, Model model) {
        if (authentication == null) {
            model.addAttribute("error", "User is not authenticated");
            return "redirect:/logout";
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            model.addAttribute("error", "User not found");
            return "redirect:/logout";
        }

        // Fetch files for the authenticated user
        File[] usersFiles = fileService.getFilesForUser(user);
        model.addAttribute("files", usersFiles);

        return "home";
    }
}
