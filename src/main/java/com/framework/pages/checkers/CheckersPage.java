package com.framework.pages.checkers;

import com.framework.helpers.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckersPage {
    private final SeleniumHelper helper = new SeleniumHelper();

    private final By restartLink = By.linkText("Restart...");
    private final By statusMakeAMove = By.xpath("//*[contains(text(),'Make a move')]");
    // On this site, the board is rendered as a grid of clickable images.
    // We can fetch all board <img> elements and interact by index (row-major order).
    private final By boardSquares = By.cssSelector("img");

    public boolean isLoaded() {
        return helper.isPresent(restartLink);
    }

    public boolean isReadyToMove() {
        return helper.isPresent(statusMakeAMove);
    }

    public void restart() {
        helper.click(restartLink);
    }

    public int boardSize() {
        return helper.findAll(boardSquares).size();
    }

    /***
     * Attempt a simple opening move by clicking two valid squares.
     * Note: The site DOM has no stable IDs; adjust indices if needed.
     */
    public void makeOpeningMove() {
        List<WebElement> cells = helper.findAll(boardSquares);
        if (cells.size() < 64) return;
        cells.get(44).click(); // select an orange piece near the bottom-left quadrant
        cells.get(37).click(); // move diagonally forward
    }
}
