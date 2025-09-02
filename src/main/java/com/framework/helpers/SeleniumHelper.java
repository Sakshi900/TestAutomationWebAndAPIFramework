package com.framework.helpers;

import com.framework.driver.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class SeleniumHelper {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SeleniumHelper() {
        this.driver = DriverFactory.getDriver();   // ✅ always from factory
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }


    public WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void click(By locator) {
        waitClickable(locator).click();
    }

    public void sendKeys(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    public boolean isPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }
    public String getText(By locator) {
        WebElement el = waitVisible(locator);
        return el.getText();

    }
    public String getAttribute(By locator, String attribute) {
        return wait.until(driver -> {
            String value = driver.findElement(locator).getAttribute(attribute);
            return (value != null && !value.isEmpty()) ? value : null;
        });
    }


    public void waitForStatusChange(By locator, String oldText) {
        wait.until(driver -> {
            String newText = driver.findElement(locator).getText();
            return !newText.equals(oldText);
        });
    }
    public String waitForAttribute(By locator, String attribute, String expectedSubstring) {
        return wait.until(driver -> {
            String value = driver.findElement(locator).getAttribute(attribute);
            return (value != null && value.contains(expectedSubstring)) ? value : null;
        });
    }

    public void waitForBrowserActivity() {
        new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(20))
                .until(driver -> {
                    try {
                        return ((JavascriptExecutor) driver)
                                .executeScript("return document.readyState")
                                .equals("complete");
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public void waitForComputerTurn() {
        // Snapshot of board before computer turn
        List<String> before = DriverFactory.getDriver()
                .findElements(By.cssSelector("img"))
                .stream()
                .map(el -> el.getAttribute("src"))
                .collect(Collectors.toList());

        // Wait until any index differs
        new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(10))
                .until(driver -> {
                    List<String> after = driver.findElements(By.cssSelector("img"))
                            .stream()
                            .map(el -> el.getAttribute("src"))
                            .collect(Collectors.toList());

                    if (after.size() != before.size()) return false; // safety check
                    for (int i = 0; i < after.size(); i++) {
                        if (!after.get(i).equals(before.get(i))) {
                            return true; // at least one cell changed
                        }
                    }
                    return false;
                });
    }
    public void waitForTurn(By locator, String finalText) {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        // Optional transient state: "Please wait."
        try {
            wait.until(ExpectedConditions.textToBe(locator, "Please wait."));
        } catch (Exception e) {
            // ignore if it never appeared
        }

        // Then wait until it becomes the expected final text
        wait.until(ExpectedConditions.textToBe(locator, finalText));
    }

}
