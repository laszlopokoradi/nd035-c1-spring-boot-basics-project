package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.config.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.mvc.support.*;

import java.io.*;

import static com.udacity.jwdnd.course1.cloudstorage.controller.SecurityController.*;


@Controller
@RequestMapping("/files")
public class FileController {
    public static final String HOME_TAB_FILES = "/home?tab=files";
    public static final String FILE_ERROR = "files_error";
    public static final String FILE_SUCCESS = "files_success";

    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("fileToUpload") MultipartFile fileToUpload, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        String fileName = fileToUpload.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "Filename is empty!");
            return "redirect:" + HOME_TAB_FILES;
        }

        if (fileService.isFileNameInUse(user.getUserId(), fileName)) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "File \"%s\" already in uploaded.".formatted(fileName));
            return "redirect:" + HOME_TAB_FILES;
        }

        if (fileToUpload.isEmpty()) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "File is empty");
            return "redirect:" + HOME_TAB_FILES;
        }

        try {
            File fileModel = new File(fileToUpload.getOriginalFilename(), fileToUpload.getContentType(), String.valueOf(fileToUpload.getSize()), user.getUserId(), fileToUpload.getBytes());

            if (fileService.uploadFile(fileModel) < 1) {
                redirectAttrs.addFlashAttribute(FILE_ERROR, "Error uploading file!");
                return "redirect:" + HOME_TAB_FILES;
            }

            redirectAttrs.addFlashAttribute(FILE_SUCCESS, "File \"%s\" uploaded successfully.".formatted(fileModel.getFilename()));
        } catch (IOException e) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "Error uploading file: " + e.getMessage());
        }

        return "redirect:" + HOME_TAB_FILES;
    }

    @PostMapping("/{fileId}/delete")
    public String deleteFile(@PathVariable Integer fileId, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        File file = fileService.getFileById(fileId);
        if (file == null) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "File (id: %d= not found!".formatted(fileId));
            return "redirect:" + HOME_TAB_FILES;
        }

        if (!file.getUserId().equals(user.getUserId())) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "You do not have permission to delete this file!");
            return "redirect:" + HOME_TAB_FILES;
        }

        if (fileService.deleteFile(fileId) < 1) {
            redirectAttrs.addFlashAttribute(FILE_ERROR, "Error deleting file!");
            return "redirect:" + HOME_TAB_FILES;
        }

        redirectAttrs.addFlashAttribute(FILE_SUCCESS, "File \"%s\" deleted successfully!".formatted(file.getFilename()));
        return "redirect:" + HOME_TAB_FILES;
    }

    @GetMapping(value = "/{fileId}/view")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Integer fileId, Authentication authentication) {
        if (authentication == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        File file = fileService.getFileById(fileId);

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!file.getUserId()
                 .equals(user.getUserId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(file.getFileData());

        return ResponseEntity.ok()
                             .contentType(MediaType.valueOf(file.getContentType()))
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                             .body(new InputStreamResource(in));
    }
}
