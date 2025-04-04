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
class CredentialTests {
    @LocalServerPort
    private int port;

    private WebDriver driver;

    private final Comparator<Credential> credentialComparator =
            (c1, c2) -> (c1.getUrl()
                           .compareTo(c2.getUrl())
                         + c1.getUsername()
                             .compareTo(c2.getUsername()));

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver()
                        .setup();
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
    void testCredentialCreation() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TCC_F", "TCC_L", "TCC_U", "TCC_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() -> "Signup failed.")
                                                  .isTrue();
        loginPage.login("TCC_U", "TCC_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        homePage.openCredentialsTab();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the credentials tab")
                                                                .isTrue();

        List<Credential> currentCredentials = homePage.credentialsTab.getCurrentCredentials();

        Credential newCredential = new Credential()
                .setUrl("Test URL")
                .setUsername("Test Username")
                .setPlainPassword("Test Password");
        homePage.credentialsTab.createCredential(newCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the notes tab")
                                                                .isTrue();

        assertThat(homePage.credentialsTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.")
                                                                       .isTrue();
        assertThat(homePage.credentialsTab.getSuccessMessage())
                .withFailMessage(() -> "Success message was not displayed.")
                .isEqualTo("Credential created (URL: %s, username: %s) successfully!".formatted(newCredential.getUrl(), newCredential.getUsername()));

        List<Credential> updatedCredentials = homePage.credentialsTab.getCurrentCredentials();
        assertThat(updatedCredentials)
                .usingElementComparator(
                        credentialComparator)
                .withFailMessage(() -> "Credential was not in the table")
                .hasSize(currentCredentials.size() + 1)
                .containsOnlyOnce(newCredential);
    }

    @Test
    void testCredentialView() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TCV_F", "TCV_L", "TCV_U", "TCV_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() -> "Signup failed.")
                                                  .isTrue();
        loginPage.login("TCV_U", "TCV_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        homePage.openCredentialsTab();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the credentials tab")
                                                                .isTrue();

        Credential newCredential = new Credential()
                .setUrl("Test URL")
                .setUsername("Test Username")
                .setPlainPassword("Test Password");
        homePage.credentialsTab.createCredential(newCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        Credential credentialInTable = homePage.credentialsTab.getCredentialFromTable(newCredential);
        assertThat(credentialInTable).withFailMessage(() -> "Credential was not correctly created.")
                                     .isNotNull()
                                     .extracting(Credential::getUrl, Credential::getUsername)
                                     .containsExactly(newCredential.getUrl(), newCredential.getUsername());
        assertThat(credentialInTable).withFailMessage(() -> "Credential was not correctly created.")
                                     .extracting(Credential::getPassword)
                                     .isNotEqualTo(newCredential.getPlainPassword());

        homePage.credentialsTab.openCredentialModalToEdit(newCredential);
        Credential credentialInModal = homePage.credentialsTab.getCredentialFromModal();
        assertThat(credentialInModal).withFailMessage(() -> "Note was not created.")
                                     .isNotNull()
                                     .extracting(Credential::getUrl, Credential::getUsername, Credential::getPlainPassword)
                                     .containsExactly(newCredential.getUrl(), newCredential.getUsername(), newCredential.getPlainPassword());
    }

    @Test
    void testCredentialEditing() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TCE_F", "TCE_L", "TCE_U", "TCE_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() -> "Signup failed.")
                                                  .isTrue();
        loginPage.login("TCE_U", "TCE_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        homePage.openCredentialsTab();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the credentials tab")
                                                                .isTrue();

        Credential newCredential = new Credential()
                .setUrl("Test URL")
                .setUsername("Test Username")
                .setPlainPassword("Test Password");
        homePage.credentialsTab.createCredential(newCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        Credential updatedCredential = new Credential()
                .setUrl("Updated Test URL")
                .setUsername("Updated Test Username")
                .setPlainPassword("Updated Test Password");
        homePage.credentialsTab.editCredential(newCredential, updatedCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the notes tab")
                                                                .isTrue();
        assertThat(homePage.credentialsTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.")
                                                                       .isTrue();
        assertThat(homePage.credentialsTab.getSuccessMessage()).withFailMessage(() -> "Success message was not displayed.")
                                                               .isEqualTo("Credential updated (URL: %s, username: %s) successfully!".formatted(updatedCredential.getUrl(), updatedCredential.getUsername()));

        Credential credentialFromTable = homePage.credentialsTab.getCredentialFromTable(updatedCredential);
        assertThat(credentialFromTable).withFailMessage(() -> "Credential was not correctly created.")
                                       .isNotNull()
                                       .extracting(Credential::getUrl, Credential::getUsername)
                                       .containsExactly(updatedCredential.getUrl(), updatedCredential.getUsername());
        assertThat(credentialFromTable).withFailMessage(() -> "Credential was not correctly created.")
                                       .extracting(Credential::getPassword)
                                       .isNotEqualTo(updatedCredential.getPlainPassword());

        homePage.credentialsTab.openCredentialModalToEdit(updatedCredential);
        Credential credentialFromModal = homePage.credentialsTab.getCredentialFromModal();
        assertThat(credentialFromModal).withFailMessage(() -> "Credential was not correctly created.")
                                       .isNotNull()
                                       .extracting(Credential::getUrl, Credential::getUsername, Credential::getPlainPassword)
                                       .containsExactly(updatedCredential.getUrl(), updatedCredential.getUsername(), updatedCredential.getPlainPassword());
    }

    @Test
    void testCredentialDeletion() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("TCD_F", "TCD_L", "TCD_U", "TCD_P");

        LoginPage loginPage = new LoginPage(driver);
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() -> "Signup failed.")
                                                  .isTrue();
        loginPage.login("TCD_U", "TCD_P");

        // Visit the home page.
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();

        homePage.openCredentialsTab();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the credentials tab")
                                                                .isTrue();

        Credential newCredential = new Credential()
                .setUrl("Test URL")
                .setUsername("Test Username")
                .setPlainPassword("Test Password");

        homePage.credentialsTab.createCredential(newCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();
        List<Credential> currentCredentials = homePage.credentialsTab.getCurrentCredentials();

        homePage.credentialsTab.deleteCredential(newCredential);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Not on the home page")
                                           .isTrue();
        assertThat(homePage.credentialsTab.isOnCredentialsTab()).withFailMessage(() -> "Not on the credentials tab")
                                                                .isTrue();
        assertThat(homePage.credentialsTab.isSuccessMessageDisplayed()).withFailMessage(() -> "Success message was not displayed.")
                                                                       .isTrue();
        assertThat(homePage.credentialsTab.getSuccessMessage()).withFailMessage(() -> "Success message was not correctly displayed.")
                                                               .isEqualTo("Credential deleted successfully.");

        List<Credential> updatedNoteTitles = homePage.credentialsTab.getCurrentCredentials();
        assertThat(updatedNoteTitles).withFailMessage(() -> "Credential was not deleted.")
                                     .hasSize(currentCredentials.size() - 1)
                                     .usingElementComparator(credentialComparator)
                                     .doesNotContain(newCredential);
    }
}
