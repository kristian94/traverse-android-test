package com.cborum.traverse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.cborum.traverse.backend.*;
import com.cborum.traverse.backend.User;
import com.cborum.traverse.utils.CredentialsManager;
import com.cborum.traverse.utils.LocationListenerWrapper;

public class StartActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    TraverseApiWrapper taw = TraverseApiWrapper.Companion.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        // todo check if idToken has expired -> get new with refreshToken
        if (CredentialsManager.getCredentials(this).getIdToken() == null) {
            Intent lockIntent = new Intent(this, LoginActivity.class);
            startActivity(lockIntent);
            finish();
        } else {
            Log.d(TAG, CredentialsManager.getCredentials(this).getIdToken());
//            startService(new Intent(getApplicationContext(), LocationListenerWrapper.class));
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    taw.traverseUser(CredentialsManager.getCredentials(getApplicationContext()).getIdToken(), mGetUserCallback);
                }
            }).start();
        }
    }

    private void logout() {
        CredentialsManager.deleteCredentials(getApplicationContext());
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private final ITraverseCallback mGetUserCallback = new TraverseCallback() {
        @Override
        public void onSuccess(int success) {
            Log.d(TAG, "TraverseCallback onSuccess: " + success);
            // todo country
            if (success == 2) {
                Log.d(TAG, "country not set");
                startActivityForResult(new Intent(getApplicationContext(), CountrySelectActivity.class), 1);
            } else {
                Log.d(TAG, "country was set");
                startService(new Intent(getApplicationContext(), LocationListenerWrapper.class));
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }

        @Override
        public void onError(int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed to get Traverse profile", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d(TAG, "TraverseCallback onError: " + error);
            logout();
        }
    };
    // todo duplicate methods (onActivityResult, mUpdateUserCallback) - also in LoginActivity.java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final User user = taw.getUser();
            user.setCountry(data.getStringExtra("selectedCountry"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    taw.updateUser(CredentialsManager.getCredentials(getApplicationContext()).getIdToken(), user, mUpdateUserCallback);
                }
            }).start();
        } else {
            logout();
        }
    }

    private final ITraverseCallback mUpdateUserCallback = new TraverseCallback() {
        @Override
        public void onSuccess(int success) {
            startService(new Intent(getApplicationContext(), LocationListenerWrapper.class));
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        @Override
        public void onError(int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed to get Traverse profile", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d(TAG, "TraverseCallback onError: " + error);
            logout();
        }
    };
}