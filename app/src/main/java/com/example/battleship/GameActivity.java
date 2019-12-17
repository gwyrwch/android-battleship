package com.example.battleship;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements MakeMoveHandler {
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    FieldGridView gridView;
    private ValueEventListener mGameListener;
    private DatabaseReference mGameReference;

    ArrayList<Rect> ships;

    List<List<Game.CellState>> updateFieldWithShips(List<List<Game.CellState>> field, List<Rect> ships)  {
        for (Rect s : ships) {
            for (int x = s.left; x <= s.right; x++)
                for (int y = s.top; y <= s.bottom; y++) {
                    field.get(x).set(y, Game.CellState.SHIP);
                }
        }
        return field;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        final String gameId = intent.getStringExtra("gameId");
        int role = intent.getIntExtra("role", -1);

        if (gameId == null) throw new AssertionError("Game id is null");
        if (role == -1) throw new AssertionError("Role is -1");

        ships = intent.getParcelableArrayListExtra("myField");

        System.out.println("role = " + role);
        System.out.println(ships);

        gridView = findViewById(R.id.game_field);
        Game emptyGame = new Game();
        gridView.setGame(emptyGame);
        gridView.setRoleAndShips(role, ships);
        gridView.setMakeMoveHandler(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mGameReference = mDatabase.child("game").child(gameId);

        if (role == 1) {
            mGameReference.setValue(emptyGame);
            // set my field to database
            mGameReference.child("mField1").setValue(updateFieldWithShips(emptyGame.mField1, ships));
            mGameReference.child("playersConnected").setValue(1);
        } else {
            // set my field to database
            mGameReference.child("mField2").setValue(updateFieldWithShips(emptyGame.mField2, ships));
            mGameReference.child("playersConnected").setValue(2);
        }

        ValueEventListener gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game updated_game = dataSnapshot.getValue(Game.class);

                if (updated_game == null) {
                    throw new AssertionError("Game is null");
                }

                if (updated_game.finished) {
                    Intent intent = new Intent(GameActivity.this, StatsActivity.class);
                    intent.putExtra("win", 1);
                    intent.putExtra("username", mFirebaseUser.getEmail());
                    intent.putExtra("userId", mFirebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }

                boolean anything_changed = false;
                int shipsKilled = 0;
                for (Rect s : ships) {
                    boolean killed = true;
                    for (int x = s.left; x <= s.right; x++)
                        for (int y = s.top; y <= s.bottom; y++) {
                            if (gridView.role == Game.GameState.FIRST_MOVE) {
                                if (updated_game.mField1.get(x).get(y) != Game.CellState.HIT) {
                                    killed = false;
                                }
                            } else {
                                if (updated_game.mField2.get(x).get(y) != Game.CellState.HIT) {
                                    killed = false;
                                }
                            }
                        }
                    if (killed) {
                        shipsKilled += 1;
                        for (int x = s.left - 1; x <= s.right + 1; x++)
                            for (int y = s.top - 1; y <= s.bottom + 1; y++) {
                                if (x >= 0 && x < 10 && y >= 0 && y < 10) {
                                    if (gridView.role == Game.GameState.FIRST_MOVE) {
                                        if (updated_game.mField1.get(x).get(y) == Game.CellState.EMPTY) {
                                            updated_game.mField1.get(x).set(y, Game.CellState.MISS);
                                            anything_changed = true;
                                        }
                                    } else {
                                        if (updated_game.mField2.get(x).get(y) == Game.CellState.EMPTY) {
                                            updated_game.mField2.get(x).set(y, Game.CellState.MISS);
                                            anything_changed = true;
                                        }
                                    }
                                }
                            }
                    }
                }

                if (anything_changed) {
                    if (shipsKilled == 10) {
                        updated_game.finished = true;
                    }
                    mGameReference.setValue(updated_game);

                    if(updated_game.finished) {
                        Intent intent = new Intent(GameActivity.this, StatsActivity.class);
                        intent.putExtra("win", 2);
                        intent.putExtra("username", mFirebaseUser.getEmail());
                        intent.putExtra("userId", mFirebaseUser.getUid());
                        startActivity(intent);
                        finish();
                    }
                }

                TextView v = findViewById(R.id.text_view_game_state);
                v.setText(updated_game.gameState == Game.GameState.FIRST_MOVE ? "First player move" : "Second player move");

                v = findViewById(R.id.text_view_players_connected);
                v.setText(updated_game.playersConnected + " players connected.");

                gridView.setGame(updated_game);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mGameReference.addValueEventListener(gameListener);
    }


    @Override
    public void makeMove(Game game, int i, int j) {
        mGameReference.setValue(game);
    }
}
