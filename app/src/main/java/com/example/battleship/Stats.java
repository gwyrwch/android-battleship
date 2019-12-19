package com.example.battleship;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
class Stats {
    String username;
    int numberOfWins;
    int numberOfLosses;

    Stats() {
        username = "none";
        numberOfLosses = 0;
        numberOfWins = 0;
    }

}

