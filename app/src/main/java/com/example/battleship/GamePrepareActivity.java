package com.example.battleship;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private DatabaseReference mGamePrepareReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private String myGameId;
    private String gameId;

    private EditText gameIdEditText;
    private Button btnConnect;

    DataSnapshot lastSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_prepare);
        gameInitializer = new GameInitializer();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        gameIdEditText = findViewById(R.id.edit_text_game_id);
        btnConnect = findViewById(R.id.button_connect);
        myGameId = mFirebaseUser.getUid().substring(0, 5);

        gameIdEditText.setText(myGameId);
        mGamePrepareReference = mDatabase.child("game");


        mGamePrepareReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
             public void onDataChange(DataSnapshot snapshot) {
                lastSnapshot = snapshot;
                gameIdEditText.setText(gameIdEditText.getText());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gameIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("Text changed");
                if ((lastSnapshot != null && lastSnapshot.hasChild(s.toString())) && !s.toString().equals(myGameId)) {
                    btnConnect.setEnabled(true);
                    btnConnect.setText(R.string.connect_to_game);
                }
                else if(s.toString().equals(myGameId)) {
                    btnConnect.setEnabled(true);
                    btnConnect.setText(R.string.create_new_game);
                } else {
                    btnConnect.setEnabled(false);
                    btnConnect.setText(R.string.connect_to_game);
                }
            }
        });

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

                gameId = gameIdEditText.getText().toString();
                int role;

                if (gameId.equals(myGameId)) {
                    Toast.makeText(getApplicationContext(), "Creating new game", Toast.LENGTH_LONG).show();
                    role = 1;
                } else {
                    Toast.makeText(getApplicationContext(), "Connecting to the game " + gameId, Toast.LENGTH_LONG).show();
                    role = 2;
                }

                Intent intent = new Intent(GamePrepareActivity.this, GameActivity.class);
                intent.putExtra("gameId", gameId);
                intent.putExtra("role", role);
                intent.putExtra("myField", gameInitializer.getShips());
                startActivity(intent);
                break;
        }
    }
}
