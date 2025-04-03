package com.udacity.jwdnd.course1.cloudstorage.controller;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.*;


@Controller
public class SecurityController {
    public static final String LOGIN_PATH = "/login";
    public static final String SIGNUP_PATH = "/signup";
    public static final String LOGOUT_PATH = "/logout";

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupView() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signupUser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttrs) {
        String signupError = null;

        if (!userService.isUsernameAvailable(user.getUsername())) {
            signupError = "The username already exists.";
        }

        if (signupError == null && userService.createUser(user) < 0) {
            signupError = "There was an error signing you up. Please try again.";
        }

        if (signupError != null) {
            model.addAttribute("signupError", signupError);
            return "signup";
        }

        redirectAttrs.addFlashAttribute("signupSuccess", true);
        return "redirect:" + LOGIN_PATH;
    }

    @PostMapping("/logout")
    public String logoutUser() {
        return "redirect:" + LOGIN_PATH;
    }
}
