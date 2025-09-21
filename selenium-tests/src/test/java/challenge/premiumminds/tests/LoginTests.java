package challenge.premiumminds.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import challenge.premiumminds.pages.LoginPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Setup ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Configure Chrome for headless mode in WSL
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox"); //No sandbox mode because in WSL the sandbox can't be initialized properly
        options.addArguments("--disable-dev-shm-usage"); //It avoids memory issues in WSL

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testLoginSuccessful() {
        // Go to login page
        driver.get("https://app.telpark.com/pt/login");

        // Assert page title
        assertEquals("Log in to Telpark", driver.getTitle());

        // Login with correct credentials
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("pedro.matias.carvalho@gmail.com");
        loginPage.enterPassword("welcome01");
        loginPage.clickLogin();

        System.out.println("Page URL after login: " + driver.getCurrentUrl());

        // Wait for the post-login element
        WebElement parkingDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("parkingMainContent")));
        assertTrue(parkingDiv.isDisplayed());

        // Check header text
        WebElement header = parkingDiv.findElement(By.tagName("h2"));
        assertEquals("Book Parking", header.getText());
    }

    @Test
    void testLoginWrongEmail() {
        // Go to login page
        driver.get("https://app.telpark.com/pt/login");

        // Assert page title
        assertEquals("Log in to Telpark", driver.getTitle());

        // Login with wrong email
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("pedro.matias.carvalho@wrongemail.com");
        loginPage.enterPassword("welcome01");
        loginPage.clickLogin();

        // Wait for error alert
        WebElement alertDiv = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert.alert-error"))
        );

        WebElement feedbackText = alertDiv.findElement(By.cssSelector("span.kc-feedback-text"));
        assertEquals("Invalid username or password.", feedbackText.getText().trim());

        System.out.println("Invalid login correctly shows error alert.");
    }

    @Test
    void testLoginWrongPassword() {
        // Go to login page
        driver.get("https://app.telpark.com/pt/login");

        // Assert page title
        assertEquals("Log in to Telpark", driver.getTitle());

        // Login with wrong email
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("pedro.matias.carvalho@gmail.com");
        loginPage.enterPassword("welcome02");
        loginPage.clickLogin();

        // Wait for error alert
        WebElement alertDiv = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.alert.alert-error"))
        );

        WebElement feedbackText = alertDiv.findElement(By.cssSelector("span.kc-feedback-text"));
        assertEquals("Invalid username or password.", feedbackText.getText().trim());

        System.out.println("Invalid login correctly shows error alert.");
    }
}
