package com.framework.pages.checkers;

public class Move {
    public String from;
    public String to;
    public boolean isCapture;

    public Move(String from, String to) {
        this(from, to, false);
    }

    public Move(String from, String to, boolean isCapture) {
        this.from = from;
        this.to = to;
        this.isCapture = isCapture;
    }

    @Override
    public String toString() {
        return from + " -> " + to + (isCapture ? " (capture)" : "");
    }
}
