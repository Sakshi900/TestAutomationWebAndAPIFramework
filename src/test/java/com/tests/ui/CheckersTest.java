package com.tests.ui;

import com.framework.helpers.SeleniumHelper;
import com.framework.pages.checkers.CheckersPage;
import com.framework.pages.checkers.Move;
import com.utils.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

@Epic("Games")
@Feature("Checkers UI")
public class CheckersTest extends BaseTest {

    private CheckersPage page;
    SeleniumHelper helper = new SeleniumHelper();

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        page = new CheckersPage();
    }

    @Test(description = "E2E Checkers flow with status change assertions", groups = {"ui"})
    @Severity(SeverityLevel.BLOCKER)
    public void checkersE2EFlow() throws InterruptedException {
        Allure.step("Confirm the site is up");
        Assert.assertTrue(page.isLoaded(), " Checkers page did not load");

        Allure.step("Verify initial status is 'Select an orange piece to move.'");
        String initial = page.getStatusText();
        Assert.assertEquals(initial, "Select an orange piece to move.", " Initial status text mismatch");

        // --- First move ---
        Allure.step("Move 1: space62 To space73");
        page.moveByName("space62", "space73");
        page.waitForTurn();
        String destSrc = page.waitForPieceSrc("space73", "you1.gif");
        Assert.assertTrue(destSrc.contains("you1.gif"),
                " Move 1 failed: Destination square (space73) does not contain orange piece");
        page.waitForStatusChange(initial);
        Assert.assertEquals(page.getStatusText(), "Make a move.", " Status after move 1 mismatch");

        // --- Second move ---
        Allure.step("Move 2: space42 To space53");
        String beforeMove2 = page.getStatusText();
        page.moveByName("space42", "space53");
        page.waitForTurn();
        String destSrc2 = page.waitForPieceSrc("space53", "you1.gif");
        Assert.assertTrue(destSrc2.contains("you1.gif"),
                " Move 2 failed: Destination square (space53) does not contain orange piece");
        String afterMove2 = page.getStatusText();
        Assert.assertEquals(afterMove2, "Make a move.", " Status after move 2 mismatch");

        // --- Third move ---
        Allure.step("Move 3: space73 To space64");
        page.moveByName("space73", "space64");
        page.waitForTurn();
        String destSrc3 = page.waitForPieceSrc("space64", "you1.gif");
        Assert.assertTrue(destSrc3.contains("you1.gif"),
                " Move 3 failed: Destination square (space64) does not contain orange piece");
        Assert.assertEquals(page.getStatusText(), "Make a move.", " Status after move 3 mismatch");

        // --- Fourth move (capture) ---
        Allure.step("Move 4 (capture): space51 To space73");
        page.moveByName("space51", "space73");
        page.waitForTurn();
        String destSrc4 = page.waitForPieceSrc("space73", "you1.gif");
        Assert.assertTrue(destSrc4.contains("you1.gif"),
                " Move 4 failed: Destination square (space73) does not contain orange piece");
        Assert.assertEquals(page.getStatusText(), "Make a move.", " Status after move 4 mismatch");

        // --- Fifth move ---
        Allure.step("Move 5: space40 To space51");
        String beforeMove5 = page.getStatusText();
        page.moveByName("space40", "space51");
        page.waitForTurn();
        String destSrc5 = page.waitForPieceSrc("space51", "you1.gif");
        Assert.assertTrue(destSrc5.contains("you1.gif"),
                " Move 5 failed: Destination square (space51) does not contain orange piece");
        Assert.assertEquals(page.getStatusText(), "Make a move.", " Status after move 5 mismatch");

        // --- Restart ---
        Allure.step("Restart the game");
        page.restart();
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.",
                " Status after restart mismatch");
        Allure.step("Verify board square reset");
        String afterResetAttr = page.waitForPieceSrc("space40", "you1.gif");
        Assert.assertTrue(afterResetAttr.contains("you1.gif"),
                " Reset failed: space40 does not contain orange piece after restart");
    }
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