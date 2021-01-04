package com.android2.auth;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressBar mProgressBar;

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public int  showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        return  1;
    }

    public int hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        return  0;
    }

    public boolean isValidEmail(String email, EditText editTextEmail) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Required");
            valid = false;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Invalid email");
                valid = false;
            } else {
                editTextEmail.setError(null);
            }
        }

        return valid;
    }
}