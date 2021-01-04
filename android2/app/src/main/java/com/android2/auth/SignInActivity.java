package com.android2.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android2.MainActivity;
import com.android2.R;
import com.android2.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private ActivitySignInBinding mSignInBinding;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());

        setContentView(mSignInBinding.getRoot());
        setProgressBar(mSignInBinding.progressBar);

        mSignInBinding.buttonGoogleSignIn.setOnClickListener(this);
        mSignInBinding.buttonSignIn.setOnClickListener(this);
        mSignInBinding.buttonSignUp.setOnClickListener(this);

        mAccountManager = AccountManager.getInstance(this);
        mGoogleSignInClient = mAccountManager.getGoogleSignInClient();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();

        if (intent.hasExtra("accountCreated") &&
                intent.getBooleanExtra("accountCreated", false)) {
            mSignInBinding.textViewMessage.setTextColor(ContextCompat.getColor(this, R.color.colorSuccess));
            mSignInBinding.textViewMessage.setText("Your account was successfully created");
            mSignInBinding.textViewMessage.setVisibility(View.VISIBLE);
        }

        if (mAccountManager.isAuthenticated()) {
            redirectToMain();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                googleSignIn(account.getIdToken());
            } catch (ApiException e) { }
        }
    }

    @Override
    public void finish() {
        super.finish();
        hideProgressBar();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_sign_up) {
            redirectToSignUp();
            return;
        }

        if (viewId == R.id.button_sign_in && isValidForm()) {
            signIn(mSignInBinding.editTextEmail.getText().toString(),
                    mSignInBinding.editTextPassword.getText().toString());
        } else if (viewId == R.id.button_google_sign_in) {
            Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(googleSignInIntent, RC_SIGN_IN);
        }

        mSignInBinding.textViewMessage.setVisibility(View.VISIBLE);
        showProgressBar();
    }

    private void googleSignIn(String idToken) {
        showProgressBar();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAccountManager
                .getAuth()
                .signInWithCredential(credential)
                .addOnSuccessListener(listener -> {
                    redirectToMain();
                }).addOnFailureListener(listener -> {
                    mSignInBinding.textViewMessage.setText("Invalid email or password.");
                    mSignInBinding.editTextPassword.setText("");

                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                });
    }

    private void signIn(String email, String password) {
        mAccountManager
                .getAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener -> {
                    redirectToMain();
                })
                .addOnFailureListener(listener -> {
                    mSignInBinding.textViewMessage.setText("Invalid email or password.");
                    mSignInBinding.editTextPassword.setText("");

                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                });
    }

    private boolean isValidForm() {
        boolean valid = true;

        EditText editTextEmail = mSignInBinding.editTextEmail;
        if (!isValidEmail(editTextEmail.getText().toString(), editTextEmail)) {
            valid = false;
        }

        String password = mSignInBinding.editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mSignInBinding.editTextPassword.setError("Required");
            valid = false;
        } else {
            mSignInBinding.editTextPassword.setError(null);
        }

        return valid;
    }

    private void redirectToMain() {
        redirect(MainActivity.class);
    }

    private void redirectToSignUp() {
       redirect(SignUpActivity.class);
    }

    private void redirect(Class classType) {
        Intent intent = new Intent(this, classType);
        startActivity(intent);
        finish();
    }
}
