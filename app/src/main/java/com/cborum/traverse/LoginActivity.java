package com.cborum.traverse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.result.Credentials;

import com.auth0.android.result.UserProfile;
import com.cborum.traverse.backend.*;
import com.cborum.traverse.backend.User;
import com.cborum.traverse.utils.CredentialsManager;
import com.cborum.traverse.utils.LocationListenerWrapper;


public class LoginActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    private Lock mLock;
    private Auth0 auth0;
    private AuthenticationAPIClient authenticationAPIClient;
    private TraverseApiWrapper taw;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taw = TraverseApiWrapper.Companion.getInstance();
        auth0 = new Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain));
        mLock = Lock.newBuilder(auth0, mCallback)
                //Add parameters to the builder
                .build(this);
        startActivity(mLock.newIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        mLock.onDestroy(this);
        mLock = null;
    }

    private final LockCallback mCallback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            Toast.makeText(getApplicationContext(), "Log In - Success", Toast.LENGTH_SHORT).show();
            System.out.println("user credentials: " + credentials.getIdToken());
            authenticationAPIClient = new AuthenticationAPIClient(auth0);
            authenticationAPIClient.tokenInfo(credentials.getIdToken()).start(mBaseCallback);
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
            CredentialsManager.saveCredentials(getApplicationContext(), credentials);
        }

        @Override
        public void onCanceled() {
            Toast.makeText(getApplicationContext(), "Log In - Cancelled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(LockException error) {
            Toast.makeText(getApplicationContext(), "Log In - Error Occurred", Toast.LENGTH_SHORT).show();
        }
    };

    private final BaseCallback<UserProfile, AuthenticationException> mBaseCallback = new BaseCallback<UserProfile, AuthenticationException>() {
        @Override
        public void onSuccess(UserProfile payload) {
            taw.traverseUser(payload, CredentialsManager.getCredentials(getApplicationContext()).getIdToken(), mTraverseCallback);
            System.out.println("user profile: " + payload.getEmail() + payload.getName());
            // Valid ID > Navigate to the app's MainActivity
        }

        @Override
        public void onFailure(AuthenticationException error) {
            Log.d(TAG, "BaseCallback onError: " + error);
            // Invalid ID Scenario
        }
    };

    private final ITraverseCallback mTraverseCallback = new TraverseCallback() {
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
            finish();
        }
    };

    private void logout() {
        CredentialsManager.deleteCredentials(getApplicationContext());
        startActivity(new Intent(this, LoginActivity.class));
    }

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
