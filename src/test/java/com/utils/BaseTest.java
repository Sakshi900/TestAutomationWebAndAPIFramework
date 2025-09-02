package com.utils;

import com.framework.config.TestConfig;
import com.framework.driver.DriverFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class BaseTest {
    @BeforeClass(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        DriverFactory.getDriver().get(TestConfig.baseUrl());
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }
}