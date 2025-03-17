package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.*;


@Controller
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("fileToUpload") MultipartFile file, Authentication authentication, Model model) {
        if (authentication == null) {
            model.addAttribute("error", "User is not authenticated");
            return "redirect:/logout";
        }

        User user = userService.getUser(authentication.getName());

        if (file.isEmpty()) {
            model.addAttribute("error", "File is empty");
            return "redirect:/home";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            model.addAttribute("error", "File name is empty");
            return "redirect:/home";
        }

        try {
            fileService.uploadFile( new File(file.getOriginalFilename(), file.getContentType(), String.valueOf(file.getSize()), user.getUserId(), file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/home";
    }
}
