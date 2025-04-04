package com.udacity.jwdnd.course1.cloudstorage.pages.home;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;

import java.util.*;


public class CredentialsTab
        extends AbstractPage {
    public static final By TAB_ID = By.id("nav-credentials-tab");

    private static final By ADD_CREDENTIAL_BUTTON = By.id("addCredentialButton");
    private static final By CREDENTIALS_TABLE = By.id("credentialsTable");

    private static final By CREDENTIAL_URL_INPUT = By.id("credential-url");
    private static final By CREDENTIAL_USERNAME_INPUT = By.id("credential-username");
    private static final By CREDENTIAL_PASSWORD_INPUT = By.id("credential-password");

    public CredentialsTab(WebDriver driver) {
        super(driver);
    }

    public boolean isOnCredentialsTab() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TAB_ID));
            wait.until(ExpectedConditions.attributeContains(TAB_ID, "class", "active"));
            return true;
        } catch (NullPointerException | WebDriverException e) {
            return false;
        }
    }

    public List<Credential> getCurrentCredentials() {
        if (!isOnCredentialsTab()) {
            throw new IllegalStateException("Not on the credentials tab.");
        }

        WebElement credentialsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIALS_TABLE));

        if (isCredentialsTableEmpty(credentialsTable)) {
            return Collections.emptyList();
        }

        List<WebElement> rows = credentialsTable.findElements(By.cssSelector("tbody tr"));

        List<Credential> credentials = new ArrayList<>();
        for (WebElement row : rows) {
            credentials.add(getCredentialFromRow(row));
        }

        return credentials;
    }

    public void createCredential(Credential newCredential) {
        if (!isOnCredentialsTab()) {
            throw new IllegalStateException("Not on the credentials tab.");
        }

        wait.until(ExpectedConditions.elementToBeClickable(ADD_CREDENTIAL_BUTTON)).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialModal")));

        WebElement credentialUrlInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_URL_INPUT));
        credentialUrlInput.sendKeys(newCredential.getUrl());

        WebElement credentialUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_USERNAME_INPUT));
        credentialUsernameInput.sendKeys(newCredential.getUsername());

        WebElement credentialPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_PASSWORD_INPUT));
        credentialPasswordInput.sendKeys(newCredential.getPlainPassword());

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("saveCredentialButton")));
        saveButton.click();
    }

    public Credential getCredentialFromTable(Credential credential) {
        if (!isOnCredentialsTab()) {
            throw new IllegalStateException("Not on the credentials tab.");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIALS_TABLE));

        if (notesTable.findElement(By.cssSelector("tbody tr td"))
                      .getText()
                      .equals("No notes added yet.")) {
            return null;
        }

        WebElement row = getCredentialRowBy(credential);
        if (row == null) {
            return null;
        }

        return getCredentialFromRow(row);
    }

    public void openCredentialModalToEdit(Credential credential) {
        if (!isOnCredentialsTab()) {
            throw new IllegalStateException("Not on the credentials tab.");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIALS_TABLE));

        if (notesTable.findElement(By.cssSelector("tbody tr td"))
                      .getText()
                      .equals("No notes added yet.")) {
            throw new IllegalStateException("No credential to edit.");
        }

        WebElement row = getCredentialRowBy(credential);
        if (row == null) {
            throw new NoSuchElementException("Credential with url: %s and username: %s  not found.".formatted(credential.getUrl(), credential.getUsername()));
        }

        row.findElement(By.className("btn-success")).click();
    }

    public Credential getCredentialFromModal() {
        if (!isCredentialModalOpen()) {
            throw new IllegalStateException("Credential modal is not open");
        }

        WebElement credentialUrlInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_URL_INPUT));
        String credentialUrl = credentialUrlInput.getAttribute("value");

        WebElement credentialUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_USERNAME_INPUT));
        String credentialUsername = credentialUsernameInput.getAttribute("value");

        WebElement credentialPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_PASSWORD_INPUT));
        String credentialPlainPassword = credentialPasswordInput.getAttribute("value");

        return new Credential()
                .setUrl(credentialUrl)
                .setUsername(credentialUsername)
                .setPlainPassword(credentialPlainPassword);
    }

    public void editCredential(Credential currentCredential, Credential newCredential) {
        openCredentialModalToEdit(currentCredential);

        if (!isCredentialModalOpen()) {
            throw new IllegalStateException("Credential modal is not open");
        }

        WebElement credentialUrlInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_URL_INPUT));
        credentialUrlInput.clear();
        credentialUrlInput.sendKeys(newCredential.getUrl());

        WebElement credentialUsernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_USERNAME_INPUT));
        credentialUsernameInput.clear();
        credentialUsernameInput.sendKeys(newCredential.getUsername());

        WebElement credentialPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIAL_PASSWORD_INPUT));
        credentialPasswordInput.clear();
        credentialPasswordInput.sendKeys(newCredential.getPlainPassword());

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("saveCredentialButton")));
        saveButton.click();
    }

    public void deleteCredential(Credential credential) {
        if (!isOnCredentialsTab()) {
            throw new IllegalStateException("Not on the credentials tab.");
        }

        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIALS_TABLE));

        if (notesTable.findElement(By.cssSelector("tbody tr td"))
                      .getText()
                      .equals("No notes added yet.")) {
            return;
        }

        WebElement row = getCredentialRowBy(credential);
        if (row == null) {
            throw new IllegalStateException("Credential with url: %s and username: %s  not found.".formatted(credential.getUrl(), credential.getUsername()));
        }
        row.findElement(By.className("btn-danger")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialsSuccess")))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialsError")))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialsSuccess")))
                       .getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialsError")))
                       .getText();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Credential getCredentialFromRow(WebElement row) {
        String url = row.findElement(By.cssSelector("th"))
                        .getText();
        String username = row.findElement(By.cssSelector("td:nth-child(3)"))
                             .getText();
        String encryptedPassword = row.findElement(By.cssSelector("td:nth-child(4)"))
                                      .getText();
        Credential c = new Credential();
        c.setUrl(url)
         .setUsername(username)
         .setPassword(encryptedPassword);
        return c;
    }

    private WebElement getCredentialRowBy(Credential credential) {
        WebElement notesTable = wait.until(ExpectedConditions.visibilityOfElementLocated(CREDENTIALS_TABLE));
        List<WebElement> rows = notesTable.findElements(By.cssSelector("tbody tr"));
        for (WebElement row : rows) {
            String rowUrl = row.findElement(By.cssSelector("th"))
                              .getText();

            String rowUsername = row.findElement(By.cssSelector("td:nth-child(3)"))
                               .getText();

            if (rowUrl.equals(credential.getUrl()) && rowUsername.equals(credential.getUsername())) {
                return row;
            }
        }
        return null;
    }

    private boolean isCredentialModalOpen() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialModal")))
                       .isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private static boolean isCredentialsTableEmpty(WebElement notesTable) {
        return notesTable.findElement(By.cssSelector("tbody tr td"))
                         .getText()
                         .equals("No credential added yet.");
    }
}
