package com.utils;

import com.framework.config.TestConfig;
import com.framework.driver.DriverFactory;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeClass(alwaysRun = true)
    @Step("Initialize WebDriver and navigate to base URL")
    public void setUp() {
        DriverFactory.initDriver();
        DriverFactory.getDriver().get(TestConfig.baseUrl());
    }

    @AfterClass(alwaysRun = true)
    @Step("Quit WebDriver")
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
