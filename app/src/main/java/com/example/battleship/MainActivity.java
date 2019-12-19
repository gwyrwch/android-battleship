package com.example.battleship;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences mSharedPreferences;


    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";

    private Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();


        findViewById(R.id.button_sign_out).setOnClickListener(this);
        findViewById(R.id.button_stats).setOnClickListener(this);
        findViewById(R.id.button_new_game).setOnClickListener(this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        System.out.println(v.getId());
        System.out.println(R.id.button_new_game);

        switch (v.getId()) {
            case R.id.button_sign_out:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
            case R.id.button_new_game:
                startActivity(new Intent(this, GamePrepareActivity.class));
                break;
            case R.id.button_stats:
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                intent.putExtra("win", 0);
                intent.putExtra("username", mFirebaseUser.getEmail());
                intent.putExtra("userId", mFirebaseUser.getUid());
                startActivity(intent);
                break;
        }
    }
}
