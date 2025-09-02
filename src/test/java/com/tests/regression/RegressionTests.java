package com.tests.regression;

import com.framework.helpers.SeleniumHelper;
import com.framework.pages.checkers.CheckersPage;
import com.framework.pages.checkers.Move;
import com.utils.BaseTest;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RegressionTests extends BaseTest {

    private CheckersPage page;
    private SeleniumHelper helper;

    @Test(description = "E2E Checkers flow with dynamic move discovery", groups = {"Regression"})
    public void checkersE2EFlow_Optimized() throws InterruptedException {
        // Initialize page + helper AFTER driver is ready
        helper = new SeleniumHelper();
        page = new CheckersPage();

        Allure.step("Confirm the site is up");
        Assert.assertTrue(page.isLoaded(), "Checkers page did not load");
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Initial Status");

        for (int i = 1; i <= 5; i++) {
            List<Move> moves = page.getValidOrangeMoves();
            Assert.assertFalse(moves.isEmpty(), "No valid orange moves found");

            boolean moveDone = false;

            for (Move move : moves) {
                String fromSrc = page.waitForPieceSrc(move.from, "you1.gif");
                String toSrc = page.waitForPieceSrc(move.to, "gray.gif");

                if (fromSrc != null && fromSrc.contains("you1.gif")
                        && toSrc != null && toSrc.contains("gray.gif")) {

                    page.moveByName(move.from, move.to);
                    page.waitForTurn();

                    String newToSrc = page.waitForPieceSrc(move.to, "you1.gif");
                    if (newToSrc != null && newToSrc.contains("you1.gif")) {
                        moveDone = true;
                        break;
                    }
                }
            }

            Assert.assertTrue(moveDone, "No valid orange move succeeded in turn " + i);
            Assert.assertEquals(page.getStatusText(), "Make a move.", "Status mismatch after move " + i);
        }

        Allure.step("Restart the game");
        page.restart();
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Status after restart mismatch");
    }
}
