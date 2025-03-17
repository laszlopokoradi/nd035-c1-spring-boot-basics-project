package com.udacity.jwdnd.course1.cloudstorage.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.*;


public class LoginPage extends AbstractPage {

    protected static final String PAGE_TITLE = "Login";
    protected static final By LOGIN_USERNAME = By.id("inputUsername");
    protected static final By LOGIN_PASSWORD = By.id("inputPassword");
    protected static final By LOGIN_BUTTON = By.id("login-button");
    protected static final By ERROR_MESSAGE = By.className("alert");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public HomePage login(String username, String password) {
        wait.until(ExpectedConditions.titleContains(PAGE_TITLE));

        driver.findElement(LOGIN_USERNAME).sendKeys(username);
        driver.findElement(LOGIN_PASSWORD).sendKeys(password);
        driver.findElement(LOGIN_BUTTON).click();

        return new HomePage(driver);
    }

    public String getErrorMessage() {
        WebElement errorElement = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        return errorElement.getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.withTimeout(Duration.ofSeconds(5));
            return driver.findElement(ERROR_MESSAGE).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
