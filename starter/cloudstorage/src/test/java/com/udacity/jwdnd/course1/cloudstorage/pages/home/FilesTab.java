package com.udacity.jwdnd.course1.cloudstorage.pages.home;


import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;


public class FilesTab
        extends AbstractPage {
    public static final By TAB_ID = By.id("nav-files-tab");

    public FilesTab(WebDriver driver) {
        super(driver);
    }

    public boolean isOnFilesTab() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TAB_ID));
            wait.until(ExpectedConditions.attributeContains(TAB_ID, "class", "active"));
            return true;
        } catch (NullPointerException | WebDriverException e) {
            return false;
        }
    }

}
