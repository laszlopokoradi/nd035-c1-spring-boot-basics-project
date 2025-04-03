package com.udacity.jwdnd.course1.cloudstorage.pages;


import com.udacity.jwdnd.course1.cloudstorage.pages.home.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


public class HomePage extends AbstractPage {
    public static final String PAGE_TITLE = "Home";

    private static final By LOGOUT_BUTTON = By.id("logoutButton");
    private static final By NOTE_TAB = By.id("nav-notes-tab");

    public final NotesTab notesTab;
    public final CredentialsTab credentialsTab;
    public final FilesTab filesTab;

    public HomePage(WebDriver driver) {
        super(driver);
        this.notesTab = new NotesTab(driver);
        this.credentialsTab = new CredentialsTab(driver);
        this.filesTab = new FilesTab(driver);
    }

    public boolean isOnHomePage() {
        try {
            return wait.until(ExpectedConditions.titleIs(PAGE_TITLE));
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void logout() {
        if (!isOnHomePage()) {
            throw new IllegalStateException("Not on the home page");
        }

        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON));
        logoutButton.click();
    }

    public void openFilesTab() {
        if (!isOnHomePage()) {
            throw new IllegalStateException("Not on the home page");
        }

        wait.until(ExpectedConditions.elementToBeClickable(FilesTab.TAB_ID)).click();
    }

    public void openNotesTab() {
        if (!isOnHomePage()) {
            throw new IllegalStateException("Not on the home page");
        }

        wait.until(ExpectedConditions.elementToBeClickable(NotesTab.TAB_ID)).click();
    }

    public void openCredentialsTab() {
        if (!isOnHomePage()) {
            throw new IllegalStateException("Not on the home page");
        }

        wait.until(ExpectedConditions.elementToBeClickable(CredentialsTab.TAB_ID)).click();
    }
}
