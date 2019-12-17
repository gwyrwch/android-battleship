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

public class StatsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ValueEventListener mGameListener;
    private DatabaseReference mStatsReference;
    private TextView usernameTextView, winsTextView, lossesTextView;
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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

                if (loaded) {
                    return;
                } else {
                    loaded = true;
                    if (wins == 1) {
                        stats.numberOfWins += 1;
                    } else if (wins == 2) {
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
