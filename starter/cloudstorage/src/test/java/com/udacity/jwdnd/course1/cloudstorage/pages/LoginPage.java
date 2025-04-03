package com.udacity.jwdnd.course1.cloudstorage.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


public class LoginPage
        extends AbstractPage {

    public static final String PAGE_TITLE = "Login";
    public static final By LOGIN_USERNAME = By.id("inputUsername");
    public static final By LOGIN_PASSWORD = By.id("inputPassword");
    public static final By LOGIN_BUTTON = By.id("loginButton");
    public static final By ERROR_MESSAGE = By.className("alert");
    public static final By SIGNUP_MESSAGE = By.id("successSignupMsg");
    public static final By SIGNUP_LINK = By.id("linkToSignup");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public  boolean isOnLoginPage() {
        try {
            return wait.until(ExpectedConditions.titleContains(PAGE_TITLE));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void login(String username, String password) {
        if (!isOnLoginPage()) {
            throw new IllegalStateException("Not on the login page");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_USERNAME))
            .sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_PASSWORD))
            .sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON))
            .click();
    }

    public String getErrorMessage() {
        if (!isErrorMessageDisplayed()) {
            return null;
        }

        return driver.findElement(ERROR_MESSAGE)
                     .getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void goToSignupPage() {
        if (!isOnLoginPage()) {
            throw new IllegalStateException("Not on the login page");
        }

        wait.until(ExpectedConditions.elementToBeClickable(SIGNUP_LINK)).click();
    }

    public boolean isSignupSuccessful() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SIGNUP_MESSAGE));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
