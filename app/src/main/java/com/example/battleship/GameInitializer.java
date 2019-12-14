package com.example.battleship;

import android.graphics.Rect;

import java.util.ArrayList;

public class GameInitializer {
    ArrayList<Rect> ships;

    public ArrayList<Rect> getShips() {
        return ships;
    }

    int[] leftCount;

    public int getLeftCount(int i) {
        return leftCount[i - 1];
    }

    GameInitializer() {
        leftCount = new int[4];
        ships = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            leftCount[i] = 4 - i;
        }
    }
}
