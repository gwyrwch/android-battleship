package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

public class GameActivity extends AppCompatActivity {
    GameInitializer gameInitializer;

    FieldGridView fieldGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameInitializer = new GameInitializer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fieldGridView = findViewById(R.id.put_ships_field);
        fieldGridView.setGameInitializer(gameInitializer);
    }


}
