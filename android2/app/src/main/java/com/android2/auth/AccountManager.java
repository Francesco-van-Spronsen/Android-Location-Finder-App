package com.android2.auth;

import android.content.Context;
import com.android2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountManager {
        private AccountManager(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public static AccountManager getInstance(Context context) {
        return new AccountManager(context);
    }

    public FirebaseAuth mAuth;

    public GoogleSignInClient googleSignInClient;

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public boolean isAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseAuth getAuth() { return mAuth; }

    public GoogleSignInClient getGoogleSignInClient() { return googleSignInClient; }

    public void signOut() {
        googleSignInClient.signOut();
        mAuth.signOut();
    }
}
