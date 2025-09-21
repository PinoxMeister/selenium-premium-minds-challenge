package challenge.premiumminds.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class VehiclesPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By vehicleForm = By.cssSelector("form.add-car");
    private By vehicleNameInput = By.name("comment");
    private By plateInput = By.name("plate");
    private By typeDropdown = By.name("type");
    private By saveButton = By.cssSelector("a.btn.btn-small.btn-success.vehiclebtnbig.thin");
    private By cancelButton = By.cssSelector("a.btn.btn-small.btn-danger.vehiclebtnbig.thin");
    private By vehicleBlocks = By.cssSelector("span.vehicles");

    public VehiclesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public WebElement addVehicle(String name, String plate, String typeValue) {
        List<WebElement> vehiclesBefore = driver.findElements(vehicleBlocks);

        // Show the form
        WebElement newVehicleButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("aNewVehicle")));
        newVehicleButton.click();

        // Wait for form
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(vehicleForm));

        // Fill fields
        WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(vehicleNameInput));
        nameField.clear();
        nameField.sendKeys(name);

        WebElement plateField = wait.until(ExpectedConditions.elementToBeClickable(plateInput));
        plateField.clear();
        plateField.sendKeys(plate);

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(typeDropdown));
        new Select(dropdown).selectByValue(typeValue);

        // Save
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        saveBtn.click();

        // Wait for form to disappear
        wait.until(ExpectedConditions.invisibilityOf(form));

        // Wait until a new vehicle block appears by its displayed name
        By newVehicleLocator = By.xpath(
                "//div[contains(@class,'span2') and contains(@class,'vehicles')]" +
                "[.//div[contains(@class,'topVehicle')]/span[text()='" + name + "']]"
        );
        WebElement addedVehicle = wait.until(ExpectedConditions.presenceOfElementLocated(newVehicleLocator));

        return addedVehicle;
    }

    public void cancelAddVehicle() {
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(vehicleForm));
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        cancelBtn.click();
        wait.until(ExpectedConditions.invisibilityOf(form));
    }

    public void removeVehicle(String name) {
    // Locate the vehicle block by the displayed vehicle name
    By vehicleLocator = By.xpath(
        "//div[contains(@class,'vehicles')]//div[contains(@class,'topVehicle')]/span[text()='" 
        + name + "']/ancestor::div[contains(@class,'vehicles')]"
    );
    WebElement vehicleBlock = wait.until(ExpectedConditions.presenceOfElementLocated(vehicleLocator));

    // Click Edit if present (optional)
    try {
        WebElement editButton = vehicleBlock.findElement(By.cssSelector("a.btn.btn-small.btn-info.vehiclebtnbig"));
        wait.until(ExpectedConditions.elementToBeClickable(editButton)).click();
    } catch (Exception ignored) {}

    // Click Delete (close icon) - not inside vehicleBlock, but in the global container
    WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
        By.cssSelector("#deletevehicle a.close")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", deleteButton);
    deleteButton.click();

    // Wait for confirmation modal to be visible
    WebElement deleteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteConfirmModal")));

    // Now wait until confirm button is clickable
    WebElement confirmDelete = deleteModal.findElement(By.cssSelector("a.btn.btn-success"));
    wait.until(ExpectedConditions.elementToBeClickable(confirmDelete));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmDelete);

    // Wait for modal to disappear
    wait.until(ExpectedConditions.invisibilityOf(deleteModal));

    // Wait until the vehicle block is gone
    wait.until(ExpectedConditions.stalenessOf(vehicleBlock));
}


}
