package com.framework.pages.checkers;

import com.framework.driver.DriverFactory;
import com.framework.helpers.SeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CheckersPage {
    private static final Logger log = LoggerFactory.getLogger(CheckersPage.class);
    private final SeleniumHelper helper = new SeleniumHelper();

    private final By restartLink = By.linkText("Restart...");
    private final By statusMessage = By.cssSelector("#message");
    private By boardPieces = By.xpath("//div[@class='line']//img");

    private final int boardSize = 8;

    public boolean isLoaded() {
        return helper.isPresent(restartLink);
    }

    public String getStatusText() {
        return helper.getText(statusMessage).trim();
    }

    public void restart() {
        helper.click(restartLink);
        helper.waitForPageLoad();

    }

    public void moveByName(String from, String to) throws InterruptedException {
        helper.click(By.name(from));
        helper.click(By.name(to));
        Thread.sleep(500); // let the board update
    }

    public String getAttributeForRestart(String locator) {
        return helper.getAttribute(By.name(locator), "src");
    }

    public void waitForStatusChange(String oldText) {
        helper.waitForStatusChange(statusMessage, oldText);
    }

    public String waitForPieceSrc(String spaceName, String expected) {
        return helper.waitForAttribute(By.name(spaceName), "src", expected);
    }

    public void waitForTurn() {
        helper.waitForTurn(statusMessage, "Make a move.");
    }

    // Find all orange pieces on the board
    public List<Space> getOrangePieces() {
        List<Space> orangePieces = new ArrayList<>();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                String spaceName = getSpaceName(row, col);
                try {
                    String src = DriverFactory.getDriver()
                            .findElement(By.name(spaceName))
                            .getAttribute("src");
                    if (src != null && src.contains("you1.gif")) { // orange piece found
                        orangePieces.add(new Space(row, col));
                        log.info("Found orange piece at " + spaceName + " (row=" + row + ", col=" + col + ")");
                    }
                } catch (Exception e) {
                    // ignore elements not found
                }
            }
        }
        return orangePieces;
    }

    // Get valid moves for orange pieces moving diagonally forward
    public List<Move> getValidOrangeMoves() {
        List<Move> validMoves = new ArrayList<>();
        List<Space> orangePieces = getOrangePieces();

        for (Space orangePieceSpace : orangePieces) {
            int fromRow = orangePieceSpace.row;
            int fromCol = orangePieceSpace.col;

            // Normal moves: diagonal forward one step
            int[][] normalTargets = {{fromRow + 1, fromCol - 1}, {fromRow + 1, fromCol + 1}};
            for (int[] target : normalTargets) {
                int toRow = target[0];
                int toCol = target[1];

                if (!isValidPosition(toRow, toCol)) continue;

                String toSpaceName = getSpaceName(toRow, toCol);

                if (isGray(toSpaceName)) {
                    validMoves.add(new Move(orangePieceSpace.name, toSpaceName, false));
                    log.info("  Added normal move: " + orangePieceSpace.name + " -> " + toSpaceName);
                }
            }

            // Capture moves: jump over blue piece diagonally (two steps)
            int[][] captureTargets = {
                    {fromRow + 2, fromCol - 2},
                    {fromRow + 2, fromCol + 2}
            };

            for (int[] target : captureTargets) {
                int toRow = target[0];
                int toCol = target[1];

                if (!isValidPosition(toRow, toCol)) continue;

                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                String midSpaceName = getSpaceName(midRow, midCol);
                String toSpaceName = getSpaceName(toRow, toCol);

                // Capture move conditions:
                // - Middle space has opponent piece (blue)
                // - Destination is empty (gray)
                if (isBlue(midSpaceName) && isGray(toSpaceName)) {
                    validMoves.add(new Move(orangePieceSpace.name, toSpaceName, true));
                   log.info("  Added capture move: " + orangePieceSpace.name + " -> " + toSpaceName + " capturing " + midSpaceName);
                }
            }
        }
        return validMoves;
    }

    private boolean isBlue(String spaceName) {
        try {
            String src = DriverFactory.getDriver().findElement(By.name(spaceName)).getAttribute("src");
            return src != null && src.contains("blue.gif");
        } catch (Exception e) {
            return false;
        }
    }


    // Validate board coordinates
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    // Build space name like "space62"
    private String getSpaceName(int row, int col) {
        return "space" + row + col;
    }

    // Check if the space is empty (gray.gif)
    private boolean isGray(String spaceName) {
        try {
            String src = DriverFactory.getDriver()
                    .findElement(By.name(spaceName))
                    .getAttribute("src");
            return src != null && src.contains("gray.gif");
        } catch (Exception e) {
            return false;
        }
    }
    public int getPiecesCount(String srcValue) {
        By locator = By.xpath("//img[contains(@src,'" + srcValue + "') and @onclick]");
        List<WebElement> elements = helper.findAll(locator);
        return elements.size();
    }

}
