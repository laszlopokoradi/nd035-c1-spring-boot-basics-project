package com.udacity.jwdnd.course1.cloudstorage;


import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import io.github.bonigarcia.wdm.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    void testNoteCreation() {
        // Visit the signup page.
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TNC_F", "TNC_L", "TNC_U", "TNC_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() ->"Signup failed.").isTrue();
        loginPage.login("TNC_U", "TNC_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        homePage.openNotesTab();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();

        List<String> originalNoteTitles = homePage.notesTab.getCurrentNoteTitles();

        homePage.notesTab.createNote("Test Note Title", "Test Note Description");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();
        assertThat(homePage.notesTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.").isTrue();
        assertThat(homePage.notesTab.getSuccessMessage()).withFailMessage(() -> "Success message was not displayed.").isEqualTo("Note created successfully!");

        List<String> updatedNoteTitles = homePage.notesTab.getCurrentNoteTitles();
        assertThat(updatedNoteTitles).withFailMessage(() -> "Note was not created.")
                                     .hasSize(originalNoteTitles.size() + 1)
                                     .containsOnlyOnce("Test Note Title");
    }

    @Test
    void testNoteViewing() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TNV_F", "TNV_L", "TNV_U", "TNV_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() ->"Signup failed.").isTrue();
        loginPage.login("TNV_U", "TNV_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        homePage.openNotesTab();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();

        homePage.notesTab.createNote("Test Note Title", "Test Note Description");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        Note noteInTable = homePage.notesTab.getNoteByTitle("Test Note Title");
        assertThat(noteInTable).withFailMessage(() -> "Note was not created.")
                .isNotNull()
                .extracting(Note::getNoteTitle, Note::getNoteDescription)
                .containsExactly("Test Note Title", "Test Note Description");

        homePage.notesTab.openNoteModalToEdit("Test Note Title");
        Note noteInModal = homePage.notesTab.getNodeInModal();
        assertThat(noteInModal).withFailMessage(() -> "Note was not created.")
                               .isNotNull()
                               .extracting(Note::getNoteTitle, Note::getNoteDescription)
                               .containsExactly("Test Note Title", "Test Note Description");
    }

    @Test
    void testNoteEditing() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TNE_F", "TNE_L", "TNE_U", "TNE_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() ->"Signup failed.").isTrue();
        loginPage.login("TNE_U", "TNE_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        homePage.openNotesTab();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();

        homePage.notesTab.createNote("Test Note Title", "Test Note Description");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        homePage.notesTab.editNote("Test Note Title", "Updated Note Title", "Updated Note Description");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();
        assertThat(homePage.notesTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.").isTrue();
        assertThat(homePage.notesTab.getSuccessMessage()).withFailMessage(() -> "Success message was not displayed.").isEqualTo("Note updated successfully!");

        Note noteInTable = homePage.notesTab.getNoteByTitle("Updated Note Title");
        assertThat(noteInTable).withFailMessage(() -> "Note was not created.")
                .isNotNull()
                .extracting(Note::getNoteTitle, Note::getNoteDescription)
                .containsExactly("Updated Note Title", "Updated Note Description");

        homePage.notesTab.openNoteModalToEdit("Updated Note Title");
        Note noteInModal = homePage.notesTab.getNodeInModal();
        assertThat(noteInModal).withFailMessage(() -> "Note was not created.")
                               .isNotNull()
                               .extracting(Note::getNoteTitle, Note::getNoteDescription)
                               .containsExactly("Updated Note Title", "Updated Note Description");
    }

    @Test
    void testNoteDeletion() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TND_F", "TND_L", "TND_U", "TND_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() ->"Signup failed.").isTrue();
        loginPage.login("TND_U", "TND_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        homePage.openNotesTab();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();

        homePage.notesTab.createNote("Test Note Title", "Test Note Description");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();

        List<String> originalNoteTitles = homePage.notesTab.getCurrentNoteTitles();

        homePage.notesTab.deleteNote("Test Note Title");
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page").isTrue();
        assertThat(homePage.notesTab.isOnNotesTab()).withFailMessage(() -> "Not on the notes tab").isTrue();
        assertThat(homePage.notesTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.").isTrue();
        assertThat(homePage.notesTab.getSuccessMessage()).withFailMessage(() -> "Success message was not displayed.").isEqualTo("Note deleted successfully!");

        List<String> updatedNoteTitles = homePage.notesTab.getCurrentNoteTitles();
        assertThat(updatedNoteTitles).withFailMessage(() -> "Note was not deleted.")
                                     .hasSize(originalNoteTitles.size() - 1)
                                     .doesNotContain("Test Note Title");
    }
}
