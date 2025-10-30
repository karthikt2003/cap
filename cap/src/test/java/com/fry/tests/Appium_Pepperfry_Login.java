package com.fry.tests;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.fry.utilities.ExtentManager;

import io.appium.java_client.android.AndroidDriver;

public class Appium_Pepperfry_Login {

    AndroidDriver driver;
    ExtentReports extent;
    ExtentTest test;
    WebDriverWait wait;

    @BeforeTest
    public void setup() throws MalformedURLException {
        extent = ExtentManager.getinstance();

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:deviceName", "vivo Y75 5G");
        caps.setCapability("appium:udid", "192.168.2.92:5555");
        caps.setCapability("appium:platformVersion", "15");
        caps.setCapability("appium:appPackage", "com.app.pepperfry");
        caps.setCapability("appium:appActivity", "com.app.pepperfry.main.MainActivity");

        URL url = new URL("http://127.0.0.1:4723/wd/hub");
        driver = new AndroidDriver(url, caps);
        wait = new WebDriverWait(driver, 20);
        System.out.println("✅ Pepperfry app launched successfully!");
    }

    // -------------------- Test 1: Skip Login --------------------
    @Test(priority = 1)
    public void skipLoginTest() throws InterruptedException {
        test = extent.createTest("Step 1 - Skip Login");

        Thread.sleep(8000);
        driver.findElement(By.id("com.app.pepperfry:id/tvSkip")).click();
        test.log(Status.INFO, "Clicked on 'SKIP FOR NOW'.");

        Thread.sleep(4000);
        boolean homeVisible = driver.findElements(
                By.xpath("//android.widget.TextView[contains(@text,'Home') or contains(@text,'Shop')]"))
                .size() > 0;
        Assert.assertTrue(homeVisible, "❌ Home screen not visible after skipping login!");
        test.log(Status.PASS, "✅ Reached Home screen successfully!");
    }

    // -------------------- Test 2: Search for Bed --------------------
    @Test(priority = 2, dependsOnMethods = "skipLoginTest")
    public void searchBedTest() throws InterruptedException {
        test = extent.createTest("Step 2 - Search for 'Bed'");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.app.pepperfry:id/etSearch"))).click();
        test.log(Status.INFO, "Clicked on search bar.");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.app.pepperfry:id/etSearch"))).sendKeys("bed");
        test.log(Status.INFO, "Entered 'bed' in search bar.");

        driver.executeScript("mobile: performEditorAction", java.util.Map.of("action", "search"));
        test.log(Status.INFO, "Performed search action.");

        Thread.sleep(6000);
        boolean resultsLoaded = driver.findElements(
                By.xpath("//android.widget.TextView[@resource-id='com.app.pepperfry:id/tvProductName']")).size() > 0;
        Assert.assertTrue(resultsLoaded, "❌ Product list not loaded!");
        test.log(Status.PASS, "✅ Search results loaded successfully!");
    }

    // -------------------- Test 3: Click First Product --------------------
    @Test(priority = 3, dependsOnMethods = "searchBedTest")
    public void clickFirstProductTest() throws InterruptedException {
        test = extent.createTest("Step 3 - Click First Product");

        By firstProduct = By.xpath("//android.widget.TextView[@resource-id='com.app.pepperfry:id/tvProductName']");
        wait.until(ExpectedConditions.elementToBeClickable(firstProduct)).click();
        test.log(Status.INFO, "Clicked on the first product in results.");

        Thread.sleep(8000);
        boolean productPage = driver.findElements(
                By.xpath("//android.widget.TextView[contains(@text,'ADD TO CART')]")).size() > 0;
        Assert.assertTrue(productPage, "❌ Product page not opened!");
        test.log(Status.PASS, "✅ Product page opened successfully!");
    }

    // -------------------- Test 4: Add to Cart --------------------
    @Test(priority = 4, dependsOnMethods = "clickFirstProductTest")
    public void addToCartTest() throws InterruptedException {
        test = extent.createTest("Step 4 - Add Product to Cart");

        By addToCart = By.id("com.app.pepperfry:id/btnAddToCart");
        wait.until(ExpectedConditions.elementToBeClickable(addToCart)).click();
        test.log(Status.INFO, "Clicked on 'ADD TO CART'.");

        Thread.sleep(5000);
        boolean cartVisible = driver.findElements(
                By.xpath("//android.widget.TextView[contains(@text,'Cart') or contains(@text,'My Cart')]"))
                .size() > 0;
        Assert.assertTrue(cartVisible, "❌ Cart page not displayed!");
        test.log(Status.PASS, "✅ Product successfully added to cart!");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("✅ App closed successfully!");
        }
        extent.flush();
    }
}
