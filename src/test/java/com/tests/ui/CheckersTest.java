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

     CheckersPage page = new CheckersPage();
    SeleniumHelper helper = new SeleniumHelper();

    @BeforeMethod(alwaysRun = true)
    public void initPage() {
        page = new CheckersPage();
    }

    @Test(description = "E2E Checkers flow with dynamic move discovery", groups = {"ui"})
    public void checkersE2EFlow_Optimized() throws InterruptedException {
        page = new CheckersPage();

        Allure.step("Confirm the site is up");
        Assert.assertTrue(page.isLoaded(), "Checkers page did not load");
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Initial Status");
        int beforeStartingOrangeMoves, beforeStartingBlueMoves;
        beforeStartingOrangeMoves = page.getPiecesCount("you1.gif");
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
        int afterRestartingOrangeMoves, afterRestartingBlueMoves;
        afterRestartingOrangeMoves = page.getPiecesCount("you1.gif");
        Allure.step("Restart the game");
        page.restart();
        Assert.assertNotEquals(beforeStartingOrangeMoves,afterRestartingOrangeMoves);
        Assert.assertEquals(page.getStatusText(), "Select an orange piece to move.", "Status after restart mismatch");
    }

}