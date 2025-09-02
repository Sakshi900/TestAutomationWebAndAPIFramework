package com.utils;

import java.util.List;

public class BlackjackUtil {

    public static int cardValue(String value) {
        // value can be "ACE", "KING", "QUEEN", "JACK" or "2".."10"
        switch (value.toUpperCase()) {
            case "KING":
            case "QUEEN":
            case "JACK":
                return 10;
            case "ACE":
                return 11; // initially 11; adjust later if bust
            default:
                return Integer.parseInt(value);
        }
    }

    public static int totalValue(List<String> values) {
        int sum = 0;
        int aces = 0;
        for (String v : values) {
            int cv = cardValue(v);
            if ("ACE".equalsIgnoreCase(v)) aces++;
            sum += cv;
        }
        while (sum > 21 && aces > 0) {
            sum -= 10; // turn an Ace from 11 -> 1
            aces--;
        }
        return sum;
    }

    public static boolean isBlackjack21(List<String> values) {
        return totalValue(values) == 21;
    }
}
