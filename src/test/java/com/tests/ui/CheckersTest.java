package com.tests.ui;

import com.framework.pages.checkers.CheckersPage;
import com.utils.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Games")
@Feature("Checkers UI")
public class CheckersTest extends BaseTest {

    @Test(description = "Confirm site is up, make moves, and restart", groups = {"ui"})
    @Severity(SeverityLevel.CRITICAL)
    public void checkersFlow() throws InterruptedException {
        CheckersPage page = new CheckersPage();

        Allure.step("Confirm the site is up");
        Assert.assertTrue(page.isLoaded(), "Checkers page did not load");

        Allure.step("Confirm page prompts to Make a move");
        Assert.assertTrue(page.isReadyToMove(), "'Make a move' prompt not found");

        Allure.step("Make five legal moves as orange (best-effort demo)");
        for (int i = 0; i < 5; i++) {
            page.makeOpeningMove();
            Thread.sleep(500); // small delay to let the engine respond visually
        }

        Allure.step("Restart the game");
        page.restart();

        Allure.step("Confirm restart was successful (prompt shown again)");
        Assert.assertTrue(page.isReadyToMove(), "After restart, 'Make a move' prompt not visible");
    }
}
