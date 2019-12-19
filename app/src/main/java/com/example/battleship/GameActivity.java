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
    private DatabaseReference mGameReference;
    int role;

    boolean finished = false;

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
        role = intent.getIntExtra("role", -1);

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
            mGameReference.child("mField1").setValue(updateFieldWithShips(emptyGame.mField1, ships));
            mGameReference.child("playersConnected").setValue(1);
        } else {
            mGameReference.child("mField2").setValue(updateFieldWithShips(emptyGame.mField2, ships));
            mGameReference.child("playersConnected").setValue(2);
        }

        ValueEventListener gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (finished) {
                    return;
                }
                Game updated_game = dataSnapshot.getValue(Game.class);

                if (updated_game == null) {
                    return;
//                    throw new AssertionError("Game is null"); //fixme:
                }

                if (updated_game.finished && !finished) {
                    finished = true;

                    int winner;
                    if (updated_game.gameState == gridView.role) {
                        winner = 1;
                        mGameReference.removeValue(); //removes game not id

                    } else {
                        winner = 2;
                    }
                    System.out.println("I " + (winner == 1 ? "WIN" : "LOSE") + mFirebaseUser.getUid());
                    Intent intent = new Intent(GameActivity.this, StatsActivity.class);
                    intent.putExtra("win", winner);
                    intent.putExtra("username", mFirebaseUser.getEmail());
                    intent.putExtra("userId", mFirebaseUser.getUid());
                    startActivity(intent);
                    finish();
                    return;
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


                if (shipsKilled == 10) {
                    updated_game.finished = true;
                    anything_changed = true;
                }

                if (anything_changed) {
                    mGameReference.setValue(updated_game);
                    return;
                }

                TextView moveTextView = findViewById(R.id.text_view_game_state);

                String move = (
                        (
                            role == 1 &&
                            updated_game.gameState == Game.GameState.FIRST_MOVE
                        ) ||
                        (
                            role == 2 &&
                            updated_game.gameState == Game.GameState.SECOND_MOVE
                        )
                ) ? "Your move" : "Second player move";
                System.out.println(role + " " + (updated_game.gameState == Game.GameState.FIRST_MOVE ? "true" : false));
                moveTextView.setText(move);

                TextView connectedTextView = findViewById(R.id.text_view_players_connected);
                String playersConnected = updated_game.playersConnected + " players connected.";
                connectedTextView.setText(playersConnected);

                gridView.setGame(updated_game);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };

        mGameReference.addValueEventListener(gameListener);
    }


    @Override
    public void makeMove(Game game) {
        mGameReference.setValue(game);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!finished)
            mGameReference.child("playersConnected").setValue(1);

        if (role == 1)
            mGameReference.removeValue();

    }
}
