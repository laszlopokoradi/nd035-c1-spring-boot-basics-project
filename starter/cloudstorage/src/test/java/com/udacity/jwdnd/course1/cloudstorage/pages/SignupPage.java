package com.udacity.jwdnd.course1.cloudstorage.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


public class SignupPage extends AbstractPage {
    public static final String PAGE_TITLE = "Sign Up";

    public static final By SIGNUP_FIRST_NAME = By.id("inputFirstName");
    public static final By SIGNUP_LAST_NAME = By.id("inputLastName");
    public static final By SIGNUP_USERNAME = By.id("inputUsername");
    public static final By SIGNUP_PASSWORD = By.id("inputPassword");
    public static final By SIGNUP_SUBMIT = By.id("buttonSignUp");
    public static final By SIGNUP_LOGIN_LINK = By.id("login-link");

    public SignupPage(WebDriver driver) {
        super(driver);
    }

    public boolean isOnSignupPage() {
        try {
            return wait.until(ExpectedConditions.titleIs(PAGE_TITLE));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public LoginPage navigateToLoginPage() {
        wait.until(ExpectedConditions.elementToBeClickable(SIGNUP_LOGIN_LINK)).click();
        return new LoginPage(driver);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signup-error"))).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signup-error"))).getText();
        } catch (TimeoutException e) {
            return null;
        }
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signup-success"))).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("signup-success"))).getText();
        } catch (TimeoutException e) {
            return null;
        }
    }

    public void signup(String firstName, String lastName, String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_FIRST_NAME)).sendKeys(firstName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_LAST_NAME)).sendKeys(lastName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_USERNAME)).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_PASSWORD)).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(SIGNUP_SUBMIT)).click();
    }
}
