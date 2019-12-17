package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GamePrepareActivity extends AppCompatActivity implements View.OnClickListener {
    GameInitializer gameInitializer;

    FieldGridView fieldGridView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String myGameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_prepare);
        gameInitializer = new GameInitializer();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView v = findViewById(R.id.edit_text_game_id);
        myGameId = mFirebaseUser.getUid().substring(0, 5);

        v.setText(myGameId);

        findViewById(R.id.button_rundomize).setOnClickListener(this);
        findViewById(R.id.button_connect).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fieldGridView = findViewById(R.id.put_ships_field);
        fieldGridView.setGameInitializer(gameInitializer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_rundomize:
                gameInitializer.randomShuffle();
                break;
            case R.id.button_connect:
                if (!gameInitializer.canStart()) {
                    Toast.makeText(getApplicationContext(), "Your field is not set", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText gameIdEditText =  findViewById(R.id.edit_text_game_id);
                String id = gameIdEditText.getText().toString();
                int role;

                if (id.equals(myGameId)) {
                    Toast.makeText(getApplicationContext(), "Creating new game", Toast.LENGTH_LONG).show();
                    role = 1;
                } else {
                    Toast.makeText(getApplicationContext(), "Connecting to the game " + id, Toast.LENGTH_LONG).show();
                    role = 2;
                }

                Intent intent = new Intent(GamePrepareActivity.this, GameActivity.class);
                intent.putExtra("gameId", id);
                intent.putExtra("role", role);
                intent.putExtra("myField", gameInitializer.getShips());
                startActivity(intent);
                break;
        }
    }
}
