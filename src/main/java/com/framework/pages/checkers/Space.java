package com.framework.pages.checkers;

public class Space {
    public int row;
    public int col;
    public String name;

    public Space(int row, int col) {
        this.row = row;
        this.col = col;
        this.name = "space" + row + col;
    }
}
