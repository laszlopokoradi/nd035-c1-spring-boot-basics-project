package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.config.*;
import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.security.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;

import java.util.*;

import static com.udacity.jwdnd.course1.cloudstorage.controller.SecurityController.*;


@Controller
@RequestMapping("/credentials")
public class CredentialController {
    public static final String HOME_TAB_CREDENTIALS = "/home?tab=credentials";
    public static final String CREDENTIALS_ERROR = "credentials_error";
    public static final String CREDENTIALS_SUCCESS = "credentials_success";

    private final UserService userService;
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public CredentialController(UserService userService, CredentialService credentialService, EncryptionService encryptionService) {
        this.userService = userService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/set")
    public String setCredential(@ModelAttribute("credentialModel") Credential credentialForm, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        if (!isCredentialValid(credentialForm)) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, Messages.CREDENTIALS_MSG_EMPTY_INPUT);
            return "redirect:" + HOME_TAB_CREDENTIALS;
        }

        String encryptedPassword = encryptionService.encryptValue(credentialForm.getPlainPassword(), user.getSalt());
        credentialForm.setPassword(encryptedPassword);
        credentialForm.setPlainPassword("");

        if (credentialForm.getCredentialId() == null) {
            createCredential(credentialForm, redirectAttrs, user);
        } else {
            updateCredential(credentialForm, redirectAttrs, user);
        }

        return "redirect:" + HOME_TAB_CREDENTIALS;
    }

    @PostMapping("/{credentialId}/delete")
    public String deleteCredential(@PathVariable Integer credentialId, Authentication authentication, RedirectAttributes redirectAttrs) {
        if (authentication == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        User user = userService.getUser(authentication.getName());
        if (user == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, Messages.MSG_USER_IS_NOT_AUTHENTICATED);
            return "redirect:" + LOGOUT_PATH;
        }

        Credential credential = credentialService.getCredentialById(credentialId);
        if (credential == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Credential (id: %d) not found!".formatted(credentialId));
            return "redirect:" + HOME_TAB_CREDENTIALS;
        }

        if (!credential.getUserId()
                 .equals(user.getUserId())) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "You do not have permission to delete this credential.");
            return "redirect:" + HOME_TAB_CREDENTIALS;
        }

        if (credentialService.deleteCredential(credentialId) != 1) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Error deleting credential (URL: %s, username: %s)."
                    .formatted(credential.getUrl(), credential.getUsername()));
            return "redirect:" + HOME_TAB_CREDENTIALS;
        }

        redirectAttrs.addFlashAttribute(CREDENTIALS_SUCCESS, "Credential deleted successfully.");
        return "redirect:" + HOME_TAB_CREDENTIALS;
    }

    private void createCredential(Credential credentialForm, RedirectAttributes redirectAttrs, User user) {
        credentialForm.setUserId(user.getUserId());

        if (isCredentialNotUnique(credentialForm, user)) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Credential with this URL and username (URL: %s, username: %s) already exists!"
                    .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
            return;
        }

        if (credentialService.createCredential(credentialForm) != 1) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Error creating credential (URL: %s, username: %s)!"
                    .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
            return;
        }

        redirectAttrs.addFlashAttribute(CREDENTIALS_SUCCESS, "Credential created (URL: %s, username: %s) successfully!"
                .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
    }

    private void updateCredential(Credential credentialForm, RedirectAttributes redirectAttrs, User user) {
        Credential existingCredential = credentialService.getCredentialById(credentialForm.getCredentialId());
        if (existingCredential == null) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Credential (id: %d) not found!"
                    .formatted(credentialForm.getCredentialId()));
            return;
        }

        if (!existingCredential.getUserId().equals(user.getUserId())) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "You do not have permission to update this credential!");
            return;
        }

        if (!(existingCredential.getUrl().equals(credentialForm.getUrl()) && existingCredential.getUsername().equals(credentialForm.getUsername())) &&
            isCredentialNotUnique(credentialForm, user)) {
                redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Credential with this URL and username (URL: %s, username: %s) already exists!"
                    .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
                return;
            }

        if (credentialService.updateCredential(credentialForm) != 1) {
            redirectAttrs.addFlashAttribute(CREDENTIALS_ERROR, "Error updating credential (URL: %s, username: %s)!"
                    .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
            return;
        }

        redirectAttrs.addFlashAttribute(CREDENTIALS_SUCCESS, "Credential updated (URL: %s, username: %s) successfully!"
                .formatted(credentialForm.getUrl(), credentialForm.getUsername()));
    }

    private boolean isCredentialValid(Credential credentialForm) {
        return credentialForm.getUrl() != null && !credentialForm.getUrl().isEmpty() &&
               credentialForm.getUsername() != null && !credentialForm.getUsername().isEmpty() &&
               credentialForm.getPlainPassword() != null && !credentialForm.getPlainPassword().isEmpty();
    }

    private boolean isCredentialNotUnique(Credential credentialForm, User user) {
        Credential[] existingCredentials = credentialService.getCredentialsByUserId(user.getUserId());
        return Arrays.stream(existingCredentials)
                     .anyMatch(c -> c.getUrl()
                                     .equals(credentialForm.getUrl()) && c.getUsername()
                                                                          .equals(credentialForm.getUsername()));
    }
}
