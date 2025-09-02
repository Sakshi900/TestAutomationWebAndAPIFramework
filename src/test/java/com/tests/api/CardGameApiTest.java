package com.tests.api;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Epic("Games")
@Feature("Card Game API")
public class CardGameApiTest {

    private static final String BASE = "https://deckofcardsapi.com/api/deck";

    @Test(description = "Get new deck, shuffle, deal three to two players, check Blackjack")
    @Severity(SeverityLevel.CRITICAL)
    public void blackjackFlow() {
        RestAssured.useRelaxedHTTPSValidation();

        String newDeckUrl = BASE + "/new/";
        String shuffleNewUrl = BASE + "/new/shuffle/?deck_count=1";

        // Site up check (200)
        given().when().get(newDeckUrl).then().statusCode(200);

        // Get a new deck + shuffle
        Response shuffleResp =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get(shuffleNewUrl)
            .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract().response();

        String deckId = shuffleResp.jsonPath().getString("deck_id");
        Allure.step("Deck id: " + deckId);

        // Draw 3 cards for player1
        JsonPath p1 = draw(deckId, 3).jsonPath();
        // Draw 3 cards for player2
        JsonPath p2 = draw(deckId, 3).jsonPath();

        List<String> v1 = p1.getList("cards.value");
        List<String> v2 = p2.getList("cards.value");

        int t1 = com.utils.BlackjackUtil.total(v1);
        int t2 = com.utils.BlackjackUtil.total(v2);

        Allure.step("P1: " + v1 + " => " + t1);
        Allure.step("P2: " + v2 + " => " + t2);

        boolean p1BJ = (t1 == 21);
        boolean p2BJ = (t2 == 21);

        if (p1BJ || p2BJ) {
            Allure.step("Blackjack! " + (p1BJ ? "Player 1" : "Player 2"));
        } else {
            Allure.step("No Blackjack this round.");
        }

        // Soft assertions example
        Assert.assertTrue(deckId != null && !deckId.isEmpty(), "deck_id should be present");
    }

    @Step("Draw {count} cards for deck {deckId}")
    private Response draw(String deckId, int count) {
        return given()
                .when()
                .get(BASE + "/" + deckId + "/draw/?count=" + count)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
