package challenge.premiumminds.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import challenge.premiumminds.pages.VehiclesPage;
import challenge.premiumminds.pages.LoginPage;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VehicleTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox"); //No sandbox mode because in WSL the sandbox can't be initialized properly
        options.addArguments("--disable-dev-shm-usage"); //It avoids memory issues in WSL
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        driver.get("https://app.telpark.com/pt/login");

        // Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername("pedro.matias.carvalho@gmail.com");
        loginPage.enterPassword("welcome01");
        loginPage.clickLogin();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("parkingMainContent")));

        // Accept cookies
        try {
            WebElement acceptCookies = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            acceptCookies.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.onetrust-pc-dark-filter")));
        } catch (Exception ignored) {}
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    
    @Test
    void testAddAndRemoveVehicle() {
        driver.get("https://app.telpark.com/vehicles");
        wait.until(ExpectedConditions.urlContains("/vehicles"));
        assertEquals("Telpark - Vehicles", driver.getTitle());

        VehiclesPage vehiclesPage = new VehiclesPage(driver);

        // Add vehicle
        WebElement newVehicle = vehiclesPage.addVehicle("Volvo V40", "AE-74-MC", "0");

        System.out.println(newVehicle.isDisplayed());
        // Verify vehicle is present
        assertTrue(newVehicle.isDisplayed(), "Vehicle should be visible after adding.");

        System.out.println("vou remover o veiculo");
        // Remove vehicle
        vehiclesPage.removeVehicle("Volvo V40");
        System.out.println("Removi o veiculo");

        // Verify vehicle is gone
        List<WebElement> vehicles = driver.findElements(By.cssSelector("span.vehicles"));
        boolean stillExists = vehicles.stream().anyMatch(v -> {
            WebElement input = v.findElement(By.name("comment"));
            return input.getAttribute("value").equals("Volvo V40");
        });
        assertTrue(!stillExists, "Vehicle should be deleted.");
    }
    
}
