package com.android2.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android2.R;
import com.android2.databinding.ActivitySignUpBinding;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySignUpBinding mSignUpBinding;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());

        setContentView(mSignUpBinding.getRoot());
        setProgressBar(mSignUpBinding.progressBar);

        mSignUpBinding.buttonSignUp.setOnClickListener(this);
        mSignUpBinding.buttonSignIn.setOnClickListener(this);
        mAccountManager = AccountManager.getInstance(this);
    }

    @Override
    public void finish() {
        super.finish();
        hideProgressBar();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_sign_in) {
            redirectToSignIn(false);
            return;
        }

        if (!isValidForm()) {
            return;
        }

        showProgressBar();
        mSignUpBinding.textViewMessage.setVisibility(View.VISIBLE);
        signUp(mSignUpBinding.editTextEmail.getText().toString(),
                mSignUpBinding.editTextPassword.getText().toString());
    }

    public void signUp(String email, String password) {
        mAccountManager
                .getAuth()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(listener -> {
                    mAccountManager.signOut();
                    redirectToSignIn(true);
                })
                .addOnFailureListener(listener -> {
                    mSignUpBinding.textViewMessage.setText(listener.getMessage());
                    mSignUpBinding.editTextPassword.setText("");
                    mSignUpBinding.editTextConfirmPassword.setText("");

                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                });
    }

    private boolean isValidForm() {
        boolean valid = true;

        EditText editTextEmail = mSignUpBinding.editTextEmail;
        if (!isValidEmail(editTextEmail.getText().toString(), editTextEmail)) {
            valid = false;
        }

        EditText password = mSignUpBinding.editTextPassword;
        EditText confirmPassword = mSignUpBinding.editTextConfirmPassword;

        if (!isValidPassword(password.getText().toString(), password)) {
            valid = false;
        }

        if (!isValidPassword(confirmPassword.getText().toString(), confirmPassword)) {
            valid = false;
        }

        if (!valid) {
            return false;
        }

        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            mSignUpBinding.textViewMessage.setText("Passwords do not match.");
            mSignUpBinding.textViewMessage.setVisibility(View.VISIBLE);
            return false;
        }

        mSignUpBinding.textViewMessage.setText("");
        return true;
    }

    private boolean isValidPassword(String password, EditText editTextPassword) {
        boolean valid = true;

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Required");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        return valid;
    }

    private void redirectToSignIn(Boolean accountCreated) {
        Intent signInActivity = new Intent(this, SignInActivity.class);

        if (accountCreated) {
            signInActivity.putExtra("accountCreated", true);
        }

        startActivity(signInActivity);
        finish();
    }
}
