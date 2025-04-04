package com.udacity.jwdnd.course1.cloudstorage.pages;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.*;


public abstract class AbstractPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public AbstractPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }
}
