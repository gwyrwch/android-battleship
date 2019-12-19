package com.example.battleship;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
class Game {
    enum CellState {
        EMPTY,
        HIT,
        MISS,
        SHIP
    }

    enum GameState {
        FIRST_MOVE,
        SECOND_MOVE
    }

    GameState gameState;

    List<List<CellState>> mField1, mField2;
    int playersConnected;
    boolean finished;



    Game() {
        gameState = GameState.FIRST_MOVE;
        finished = false;

        mField1 = new ArrayList<>();
        mField2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ArrayList<CellState> a = new ArrayList<>();
            ArrayList<CellState> b = new ArrayList<>();

            for (int j = 0; j < 10; j++) {
                a.add(CellState.EMPTY);
                b.add(CellState.EMPTY);
            }

            mField1.add(a);
            mField2.add(b);
        }
    }


    boolean makeMove(GameState role, int x, int y) {
        if (role == GameState.FIRST_MOVE) {
            if (mField2.get(x).get(y) == CellState.HIT || mField2.get(x).get(y) == CellState.MISS) {
                return false;
            }
            if (mField2.get(x).get(y) == CellState.EMPTY) {
                gameState = GameState.SECOND_MOVE;
                mField2.get(x).set(y, CellState.MISS);
            } else {
                mField2.get(x).set(y, CellState.HIT);
            }

            return true;
        } else {
            if (mField1.get(x).get(y) == CellState.HIT || mField1.get(x).get(y) == CellState.MISS) {
                return false;
            }
            if (mField1.get(x).get(y) == CellState.EMPTY) {
                gameState = GameState.FIRST_MOVE;
                mField1.get(x).set(y, CellState.MISS);
            } else {
                mField1.get(x).set(y, CellState.HIT);
            }
            return true;
        }
    }
}
