package com.contactmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContactManagementSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testAddContact() {

        // Contact Management application running on port 8082
        driver.get("http://localhost:8082");

        WebDriverWait wait =
                new WebDriverWait(driver, Duration.ofSeconds(15));

        // Wait until Name field is available
        wait.until(driver ->
                driver.findElement(By.id("name")).isDisplayed()
        );

        // Enter Name
        driver.findElement(By.id("name"))
                .sendKeys("Test User");

        // Enter Email
        driver.findElement(By.id("email"))
                .sendKeys("test@gmail.com");

        // Enter Phone
        driver.findElement(By.id("phone"))
                .sendKeys("9876543210");

        // Click Submit button
        driver.findElement(By.cssSelector("button[type='submit']"))
                .click();

        // Wait for success message
        wait.until(driver ->
                driver.getPageSource()
                        .contains("Contact Added Successfully")
        );

        // Verify result
        assertTrue(
                driver.getPageSource()
                        .contains("Contact Added Successfully")
        );
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}