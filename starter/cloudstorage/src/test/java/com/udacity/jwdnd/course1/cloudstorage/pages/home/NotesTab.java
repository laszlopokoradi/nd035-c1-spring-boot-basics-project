package com.udacity.jwdnd.course1.cloudstorage.pages.home;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;

import java.util.*;


public class NotesTab
        extends AbstractPage {

    public static final By TAB_ID = By.id("nav-notes-tab");

    private static final By ADD_NOTE_BUTTON = By.id("addNoteButton");
    private static final By NOTE_TABLE = By.id("notesTable");
    private static final By NOTE_TITLE_INPUT = By.id("noteTitleInput");
    private static final By NOTE_DESCRIPTION_INPUT = By.id("noteDescriptionInput");
    private static final By NOTE_MODAL = By.id("noteModal");
    private static final By NOTES_ERROR = By.id("notesError");
    private static final By NOTES_SUCCESS = By.id("notesSuccess");
    private static final By MODAL_SAVE_BUTTON = By.id("saveNoteButton");
    private static final By EDIT_NOTE_BUTTON = By.className("btn-success");
    private static final By DELETE_NOTE_BUTTON = By.className("btn-danger");
    public static final By DESCRIPTION_COLUMN = By.cssSelector("td:nth-child(3)");

    public NotesTab(WebDriver driver) {
        super(driver);
    }

    public boolean isOnNotesTab() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TAB_ID));
            wait.until(ExpectedConditions.attributeContains(TAB_ID, "class", "active"));
            return true;
        } catch (NullPointerException | WebDriverException e) {
            return false;
        }
    }

    public List<String> getCurrentNoteTitles() {
        if (!isOnNotesTab()) {
            throw new IllegalStateException("Not on the notes tab");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TABLE));

        if (isNotesTableEmpty(notesTable)) {
            return Collections.emptyList();
        }

        List<WebElement> rows = notesTable.findElements(By.cssSelector("tbody tr"));
        List<String> noteTitles = new ArrayList<>();
        for (WebElement row : rows) {
            String title = row.findElement(By.cssSelector("th"))
                              .getText();
            noteTitles.add(title);
        }

        return noteTitles;
    }

    public void createNote(String noteTitle, String noteDescription) {
        if (!isOnNotesTab()) {
            throw new IllegalStateException("Not on the notes tab");
        }

        wait.until(ExpectedConditions.elementToBeClickable(ADD_NOTE_BUTTON)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_MODAL));
        WebElement noteTitleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TITLE_INPUT));
        noteTitleInput.sendKeys(noteTitle);

        WebElement noteDescriptionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_DESCRIPTION_INPUT));
        noteDescriptionInput.sendKeys(noteDescription);

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(MODAL_SAVE_BUTTON));
        saveButton.click();
    }

    public Note getNoteByTitle(String noteTitle) {
        if (!isOnNotesTab()) {
            throw new IllegalStateException("Not on the notes tab");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TABLE));

        if (isNotesTableEmpty(notesTable)) {
            return null;
        }

        WebElement row = getNoteRowByTitle(noteTitle);

        if (row == null) {
            return null;
        }

        String description = row.findElement(DESCRIPTION_COLUMN)
                                  .getText();

        Note n = new Note();
        n.setNoteTitle(noteTitle)
         .setNoteDescription(description);
        return n;
    }

    public void openNoteModalToEdit(String noteTitle) {
        if (!isOnNotesTab()) {
            throw new IllegalStateException("Not on the notes tab");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TABLE));

        if (isNotesTableEmpty(notesTable)) {
            throw new IllegalStateException("No note to edit.");
        }

        WebElement row = getNoteRowByTitle(noteTitle);
        if (row == null) {
            throw new NoSuchElementException("Note with title '" + noteTitle + "' not found");
        }

        row.findElement(EDIT_NOTE_BUTTON).click();
    }

    public Note getNodeInModal() {
        if (!isNoteModalOpen()) {
            throw new IllegalStateException("Note modal is not open");
        }

        WebElement noteTitleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TITLE_INPUT));
        String noteTitle = noteTitleInput.getAttribute("value");

        WebElement noteDescriptionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_DESCRIPTION_INPUT));
        String noteDescription = noteDescriptionInput.getAttribute("value");

        Note n = new Note();
        n.setNoteTitle(noteTitle)
         .setNoteDescription(noteDescription);
        return n;
    }

    public void editNote(String currentNoteTitle, String newNoteTitle, String newNoteDescription) {
        openNoteModalToEdit(currentNoteTitle);

        if (!isNoteModalOpen()) {
            throw new IllegalStateException("Note modal is not open");
        }

        WebElement noteTitleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TITLE_INPUT));
        noteTitleInput.clear();
        noteTitleInput.sendKeys(newNoteTitle);

        WebElement noteDescriptionInput = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_DESCRIPTION_INPUT));
        noteDescriptionInput.clear();
        noteDescriptionInput.sendKeys(newNoteDescription);

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(MODAL_SAVE_BUTTON));
        saveButton.click();
    }

    public void deleteNote(String noteTitle) {
        if (!isOnNotesTab()) {
            throw new IllegalStateException("Not on the notes tab");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TABLE));

        if (isNotesTableEmpty(notesTable)) {
            return;
        }

        WebElement row = getNoteRowByTitle(noteTitle);
        if (row == null) {
            throw new IllegalStateException("Note with title '" + noteTitle + "' not found");
        }
        row.findElement(DELETE_NOTE_BUTTON).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NOTES_SUCCESS))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NOTES_ERROR))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NOTES_SUCCESS))
                       .getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NOTES_ERROR))
                       .getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    private WebElement getNoteRowByTitle(String noteTitle) {
        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_TABLE));
        List<WebElement> rows = notesTable.findElements(By.cssSelector("tbody tr"));
        for (WebElement row : rows) {
            String title = row.findElement(By.cssSelector("th"))
                              .getText();
            if (title.equals(noteTitle)) {
                return row;
            }
        }
        return null;
    }

    private boolean isNoteModalOpen() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NOTE_MODAL))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private static boolean isNotesTableEmpty(WebElement notesTable) {
        return notesTable.findElement(By.cssSelector("tbody tr td"))
                         .getText()
                         .equals("No notes added yet.");
    }
}
