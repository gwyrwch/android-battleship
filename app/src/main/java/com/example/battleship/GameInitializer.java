package com.example.battleship;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Random;

class GameInitializer {
    private ArrayList<Rect> ships;
    private UpdateListener updateListener;

    ArrayList<Rect> getShips() {
        return ships;
    }

    private int[] leftCount;

    int getLeftCount(int i) {
        return leftCount[i - 1];
    }

    GameInitializer() {
        updateListener = null;
        leftCount = new int[4];
        init();
    }

    private void init() {
        ships = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            leftCount[i] = 4 - i;
        }
    }

    private boolean touch(Rect s1, Rect s2) {
        return s2.intersects(s1.left - 2, s1.top - 2, s1.right + 2, s1.bottom + 2);
    }

    void randomShuffle() {
        if (canStart()) {
             init();
        }

        Random r = new Random();
        for (int i = 0; i < 4; i++) {
            while (leftCount[i] > 0) {
                while (true) {
                    int x = r.nextInt(10);
                    int y = r.nextInt(10);
                    int dir = r.nextInt(2);

                    Rect ship = new Rect(
                        x, y,
                        x + (dir == 0 ? i : 0),
                        y + (dir == 1 ? i : 0)
                    );

                    if (ship.right >= 10 || ship.bottom >= 10) {
                        continue;
                    }

                    boolean ok = true;
                    for (int j = 0; j < ships.size(); j++) {
                        if (touch(ships.get(j), ship)) {
                            ok = false;
                            break;
                        }
                    }

                    if (ok) {
                        ships.add(ship);
                        break;
                    }
                }
                leftCount[i] -= 1;
            }
        }

        if (updateListener != null) {
            updateListener.onUpdate();
        }
    }

    boolean canStart() {
        return leftCount[0] + leftCount[1] + leftCount[2] + leftCount[3] == 0;
    }

    void setUpdateListener(UpdateListener l) {
        this.updateListener = l;
    }
}
