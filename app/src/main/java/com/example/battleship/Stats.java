package com.example.battleship;

        import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Stats {
    public String username;
    public int numberOfWins;
    public int numberOfLosses;

    public Stats() {
        username = "none";
        numberOfLosses = 0;
        numberOfWins = 0;
    }

}

