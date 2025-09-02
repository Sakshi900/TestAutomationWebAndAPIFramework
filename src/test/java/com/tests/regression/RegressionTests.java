package com.tests.regression;

import com.framework.helpers.SeleniumHelper;
import com.framework.pages.checkers.CheckersPage;
import com.framework.pages.checkers.Move;
import io.qameta.allure.Allure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RegressionTests {
    private CheckersPage page;
    SeleniumHelper helper = new SeleniumHelper();
    @Test(description = "E2E Checkers flow with dynamic move discovery", groups = {"ui"})
    public void checkersE2EFlow_Optimized() throws InterruptedException {
        Allure.step("Confirm the site is up");
        Assert.assertTrue(page.isLoaded(), "Checkers page did not load");
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Initial Status");

        // Perform 5 moves dynamically
        for (int i = 1; i <= 5; i++) {
            System.out.println("Move " + i);

            List<Move> moves = page.getValidOrangeMoves();
            System.out.println("Found " + moves.size() + " valid orange moves.");
            Assert.assertFalse(moves.isEmpty(), "No valid orange moves found");

            boolean moveDone = false;

            for (Move move : moves) {
                System.out.println("Trying move: " + move.from + " -> " + move.to);

                // Check if 'from' still has orange piece
                String fromSrc = page.waitForPieceSrc(move.from, "you1.gif");
                if (fromSrc == null || !fromSrc.contains("you1.gif")) {
                    System.out.println("Skipping move: no orange piece at " + move.from);
                    continue;
                }

                // Check if 'to' is still empty (gray)
                String toSrc = page.waitForPieceSrc(move.to, "gray.gif");
                if (toSrc == null || !toSrc.contains("gray.gif")) {
                    System.out.println("Skipping move: destination " + move.to + " is occupied");
                    continue;
                }

                // Make the move
                System.out.println("Making move: " + move.from + " -> " + move.to);
                page.moveByName(move.from, move.to);

                page.waitForTurn();

                // Verify move success
                String newToSrc = page.waitForPieceSrc(move.to, "you1.gif");
                if (newToSrc != null && newToSrc.contains("you1.gif")) {
                    System.out.println("Move " + move.from + " to " + move.to + " succeeded.");
                    moveDone = true;
                    break; // Move done, exit move loop
                } else {
                    System.out.println("Move " + move.from + " to " + move.to + " failed.");
                }
            }

            Assert.assertTrue(moveDone, "No valid orange move succeeded in turn " + i);
            String status = page.getStatusText();
            System.out.println("Status after move " + i + ": " + status);
            Assert.assertEquals(status, "Make a move.", "Status mismatch after move " + i);
        }

        // Restart the game at the end
        Allure.step("Restart the game");
        page.restart();
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Status after restart mismatch");
    }
}
