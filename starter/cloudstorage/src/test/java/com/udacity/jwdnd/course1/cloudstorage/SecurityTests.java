package com.udacity.jwdnd.course1.cloudstorage;


import com.udacity.jwdnd.course1.cloudstorage.pages.*;
import io.github.bonigarcia.wdm.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.web.server.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTests {
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
    void testPageRestrictions() {
        // Visit the home page.
        driver.get("http://localhost:" + this.port + "/home");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page (Home)").isEqualTo(LoginPage.PAGE_TITLE);

        driver.get("http://localhost:" + this.port + "/home?tab=notes");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page (Home- Notes)").isEqualTo(LoginPage.PAGE_TITLE);

        driver.get("http://localhost:" + this.port + "/home?tab=files");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page (Home - Files)").isEqualTo(LoginPage.PAGE_TITLE);

        driver.get("http://localhost:" + this.port + "/home?tab=credentials");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page (Home - Credentials)").isEqualTo(LoginPage.PAGE_TITLE);

        driver.get("http://localhost:" + this.port + "/signup");
        assertThat(driver.getTitle()).withFailMessage(() -> "Signup page is not accessible.").isEqualTo(SignupPage.PAGE_TITLE);

        // Visit the login page.
        driver.get("http://localhost:" + this.port + "/login");
        assertThat(driver.getTitle()).withFailMessage(() -> "Login page is not accessible").isEqualTo(LoginPage.PAGE_TITLE);
    }

    @Test
    void testLogin() {
        // Visit the login page.
        driver.get("http://localhost:" + this.port + "/login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.goToSignupPage();
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup("testuser", "testuser", "testuser", "password");
        assertThat(loginPage.isSignupSuccessful()).withFailMessage(() ->"Signup failed").isTrue();

        loginPage.login("testuser", "password");
        HomePage homePage = new HomePage(driver);
        assertThat(homePage.isOnHomePage()).withFailMessage(() -> "Login failed").isTrue();

        homePage.logout();
        assertThat(loginPage.isOnLoginPage()).withFailMessage(() -> "Not redirected to login page after logout").isTrue();

        driver.get("http://localhost:" + this.port + "/home");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page after logout (Home)").isEqualTo(LoginPage.PAGE_TITLE);

        driver.get("http://localhost:" + this.port + "/home?tab=notes");
        assertThat(driver.getTitle()).withFailMessage(() -> "Not redirected to login page after logout (Home - Notes)").isEqualTo(LoginPage.PAGE_TITLE);
    }
}
