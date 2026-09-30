package com.contactmanagement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.WebDriverWait;

public class ContactManagementSeleniumTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    static void setUp() {

        driver = new ChromeDriver();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        driver.manage().window().maximize();

    }

    @Test
    void testAddContact() {

        // Open Contact Management System
        driver.get("http://localhost:8080");

        // Enter Name
        WebElement name = wait.until(
                d -> d.findElement(By.id("name"))
        );

        name.clear();
        name.sendKeys("Anshika");

        // Enter Email
        WebElement email = driver.findElement(
                By.id("email")
        );

        email.clear();
        email.sendKeys("anshika@gmail.com");

        // Enter Phone
        WebElement phone = driver.findElement(
                By.id("phone")
        );

        phone.clear();
        phone.sendKeys("9876543210");

        // Click Add Contact
        WebElement addButton = driver.findElement(
                By.id("addContact")
        );

        addButton.click();

        // Check success message
        WebElement message = wait.until(
                d -> d.findElement(By.id("message"))
        );

        String result = message.getText();

        System.out.println("Selenium Result: " + result);

        // Verify result
        assertTrue(
                result.contains("Contact Added Successfully"),
                "Contact was not added successfully"
        );

        assertTrue(
                result.contains("Anshika"),
                "Name not found in success message"
        );

        assertTrue(
                result.contains("anshika@gmail.com"),
                "Email not found in success message"
        );

        assertTrue(
                result.contains("9876543210"),
                "Phone not found in success message"
        );
    }

    @AfterAll
    static void tearDown() {

        if (driver != null) {
            driver.quit();
        }

    }
}