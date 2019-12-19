package com.example.battleship;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mStatsReference;
    private TextView usernameTextView, winsTextView, lossesTextView, resultTextView;
    boolean loaded;
    int wins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        wins = intent.getIntExtra("win", 0);

        System.out.println("Opened stats with "  +  userId + "; wins = " + wins);


        usernameTextView = findViewById(R.id.usernameTextView);
        winsTextView = findViewById(R.id.winsTextView);
        lossesTextView = findViewById(R.id.lossesTextView);
        resultTextView = findViewById(R.id.resultTextView);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (userId == null) throw new AssertionError("Role is -1");
        mStatsReference = mDatabase.child("stats").child(userId);
        loaded = false;

        mStatsReference.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Stats stats = dataSnapshot.getValue(Stats.class);

                if (stats == null)  {
                    stats = new Stats();
                    stats.username = mFirebaseUser.getEmail();
                    stats.numberOfWins = 0;
                    stats.numberOfLosses = 0;
                }

                if (!loaded) {
                    loaded = true;
                    if (wins == 1) {
                        stats.numberOfWins += 1;
                        resultTextView.setText(R.string.winner);
                    } else if (wins == 2) {
                        resultTextView.setText(R.string.loser);
                        stats.numberOfLosses += 1;
                    }
                    mStatsReference.setValue(stats);

                    System.out.println(stats.numberOfLosses);
                    System.out.println(stats);


                    usernameTextView.setText(stats.username);
                    winsTextView.setText(Integer.toString(stats.numberOfWins));
                    lossesTextView.setText(Integer.toString(stats.numberOfLosses));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
