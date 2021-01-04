package com.android2.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.android2.R;
import com.android2.auth.BaseActivity;
import com.android2.auth.SignInActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import com.android2.auth.AccountManager;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {}

    private static final String TAG = "ProfileFragment";

    private ProfileViewModel profileViewModel;
    private View root;

    private AccountManager accManger;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private InputMethodManager imm;

    private TextView textEmail;
    private TextView textOldPassword;
    private TextView textNewPassword;
    public Button btnChangeProfPic, btnChangeEmail, btnChangePassword, btnCancelEmail, btnCancelPassword, btnLogout;
    private ImageButton btnShowHidePasswordPassword;
    private ImageView ProfilePicImageView;

    private Boolean PwIsVisible;
    private int TAKE_IMAGE_CODE = 10001;
    private String userEmail;
    private final int minPasswordLength = 6;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        accManger = AccountManager.getInstance(getContext());
        mAuth = accManger.getAuth();
        try {
        currentUser = mAuth.getCurrentUser();
        } catch (NullPointerException e) {
            //Log.d(TAG, "currentUser.getEmail() Error: " + e);
        }

        ProfilePicImageView = root.findViewById(R.id.profile_imageView);
        //Request for camera permission
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA
            }, TAKE_IMAGE_CODE);
        }

        //Show profile picture
        if (currentUser.getPhotoUrl() != null) {
            Glide.with(getActivity())
                    .load(currentUser.getPhotoUrl())
                    .into(ProfilePicImageView);
        }

        //Show current email address
        textEmail = root.findViewById(R.id.textEmail);
        userEmail = currentUser.getEmail();
        textEmail.setText(userEmail);

        textOldPassword = root.findViewById(R.id.textOldPassword);
        textNewPassword = root.findViewById(R.id.textNewPassword);

        //CHANGE PROFILE PICTURE
        btnChangeProfPic = root.findViewById(R.id.btnChangeProfPic);
        btnChangeProfPic.setOnClickListener(v -> { changeImage(); });

        //CHANGE EMAIL
        btnChangeEmail = root.findViewById(R.id.btnChangeEmail);
        btnChangeEmail.setOnClickListener(v -> { changeEmail(); });

        //CANCEL EMAIL
        btnCancelEmail = root.findViewById(R.id.btnCancelEmail);
        btnCancelEmail.setOnClickListener(v -> { cancelEmail(); });

        //CHANGE PASSWORD
        btnChangePassword = root.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> { changePassword(); });

        //SHOW/HIDE PASSWORD
        textNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        PwIsVisible = false;
        btnShowHidePasswordPassword = root.findViewById(R.id.btnShowHidePassword);
        btnShowHidePasswordPassword.setOnClickListener(v -> { ShowHidePassword(); });

        //CANCEL PASSWORD
        btnCancelPassword = root.findViewById(R.id.btnCancelPassword);
        btnCancelPassword.setOnClickListener(v -> { hidePasswordFields(); });

        //LOGOUT
        btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> { LogOut(); });
        return root;
    }

    private void changeImage() {
        //Open camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Gets result of camera picture taking
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == TAKE_IMAGE_CODE ) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap profilePicture = (Bitmap) data.getExtras().get("data");
                    ProfilePicImageView.setImageBitmap(profilePicture);
                    try {
                        imageUpload(profilePicture);
                    } catch (Exception e) {
                        //Log.e(TAG, "onActivityResult: Image upload failed", e.getCause());
                    }
            }
        }
    }

    private void imageUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        //Sets right path to the db
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("Images")
                .child("ProfilePics")
                .child(userEmail + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(taskSnapshot -> {
                    getDownloadUrl(reference);
                })
                .addOnFailureListener(e -> {
                    //Log.d(TAG, "imageUpload: ", e.getCause());
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        //Gets reference to profile picture
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    //Log.d(TAG, "getDownloadUrl: " + uri);
                    setUserProfileUrl(uri);
                });
    }

    private void setUserProfileUrl(Uri uri) {
        //Uploads profile picture
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        currentUser.updateProfile(request)
                .addOnSuccessListener(aVoid -> {
                    Toast toast = Toast.makeText(getActivity(), "Profile picture updated", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                })
                .addOnFailureListener(e -> {
                    Toast toast = Toast.makeText(getActivity(), "Profile picture update failed", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                });
    }

    public void changeEmail() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //btn text changes from "Change Email" to "Update" and back on click event
        if (btnChangeEmail.getText().equals("Change Email")) {
            showEmailFields(); //Allows user to write in the edit text
            imm.showSoftInput(textEmail, InputMethodManager.SHOW_IMPLICIT); //Shows keyboard
        } else if (btnChangeEmail.getText().equals("Update")) {
            String newEmail = textEmail.getText().toString();

            BaseActivity baseActivity = new BaseActivity();
            //Checks if the email address provided is valid
            if (!baseActivity.isValidEmail(newEmail, (EditText) textEmail)) {
                imm.showSoftInput(textEmail, InputMethodManager.SHOW_IMPLICIT);
                return;
            }
            currentUser.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Log.d(TAG, "changeEmail: User email address updated.");
                            Toast toast = Toast.makeText(getActivity(), "Email successfully updated", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                        } else {
                            //Log.d(TAG, "changeEmail: User email address not updated.");
                            Toast toast = Toast.makeText(getActivity(), "Email could not be updated", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                        }
                    });
            hideEmailFields();
        } //End else if
    }

    private void cancelEmail() {
        textEmail.setText("");
        textEmail.setFocusable(false);
        textEmail.setEnabled(false);
        textEmail.setClickable(false);
        textEmail.setFocusableInTouchMode(false);
        btnChangeEmail.setText("Change Email");
        btnCancelEmail.setVisibility(View.INVISIBLE);
    }

    private void showEmailFields() {
        textEmail.setText("");
        textEmail.setFocusable(true);
        textEmail.setEnabled(true);
        textEmail.setClickable(true);
        textEmail.setFocusableInTouchMode(true);
        textEmail.requestFocus();
        btnChangeEmail.setText("Update");
        btnCancelEmail.setVisibility(View.VISIBLE);
    }

    private void hideEmailFields() {
        textEmail.setFocusable(false);
        textEmail.setEnabled(false);
        textEmail.setClickable(false);
        textEmail.setFocusableInTouchMode(false);
        textEmail.setTextColor(Color.parseColor("#000000")); //Black
        btnChangeEmail.setText("Change Email");
        btnCancelEmail.setVisibility(View.INVISIBLE);
    }

    public void changePassword() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //btn text changes from "Change Password" to "Update" and back on click event
        if (btnChangePassword.getText().equals("Change Password")) {
            showPasswordFields(); //Allows user to write in the edit text
            imm.showSoftInput(textOldPassword, InputMethodManager.SHOW_IMPLICIT); //Shows keyboard

        } else if (btnChangePassword.getText().equals("Update")) {
            currentUser = accManger.getCurrentUser();

            final String oldPassword = textOldPassword.getText().toString();
            final String newPassword = textNewPassword.getText().toString();

            //Checks if both passwords have valid lengths
            if (!passwordCheck(oldPassword, newPassword)) { return; }

            //Re-authenticates user which is needed to change the password
            AuthCredential credential = EmailAuthProvider.getCredential(userEmail,oldPassword);

            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    currentUser.updatePassword(newPassword);
                    //Log.d(TAG, "changePassword: User password updated.");
                    Toast toast = Toast.makeText(getActivity(),"Password successfully updated", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                }else {
                    //Log.d(TAG, "changePassword: Authentication failed.");
                    Toast toast = Toast.makeText(getActivity(),"Error! Old password incorrect", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
                }
            });
            hidePasswordFields();
        } //End else if
    }

    public Boolean passwordCheck(String oldPassword, String newPassword) {
        Boolean oldPw, newPw;
        if (oldPassword.isEmpty()) {
            //Log.d(TAG, "changePassword: Old password is empty.");
            textOldPassword.setError("This field cannot be blank");
            oldPw = false;
        } else {
            textOldPassword.setError(null);
            oldPw = true;
        }
        if (newPassword.length() < minPasswordLength) {
            //Log.d(TAG, "changePassword: New password too short.");
            textNewPassword.setError("Password must be at least 6 characters long");
            newPw = false;
        } else {
            textNewPassword.setError(null);
            newPw = true;
        }
        return oldPw && newPw;
    }

    private void ShowHidePassword() {
        if (PwIsVisible) {
            textNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            PwIsVisible = false;
        } else {
            textNewPassword.setTransformationMethod(null);
            PwIsVisible = true;
        }
    }

    private void showPasswordFields() {
        textOldPassword.setText("");
        textOldPassword.setFocusable(true);
        textOldPassword.setEnabled(true);
        textOldPassword.setClickable(true);
        textOldPassword.setFocusableInTouchMode(true);
        textOldPassword.setHint("Enter old password");
        textOldPassword.setVisibility(View.VISIBLE);
        textOldPassword.requestFocus();

        textNewPassword.setText("");
        textNewPassword.setFocusable(true);
        textNewPassword.setEnabled(true);
        textNewPassword.setClickable(true);
        textNewPassword.setFocusableInTouchMode(true);
        textNewPassword.setHint("Enter new password");
        textNewPassword.setVisibility(View.VISIBLE);

        btnChangePassword.setText("Update");
        btnCancelPassword.setVisibility(View.VISIBLE);
        btnShowHidePasswordPassword.setVisibility(View.VISIBLE);
    }

    private void hidePasswordFields() {
        textOldPassword.setText("");
        textOldPassword.setFocusable(false);
        textOldPassword.setEnabled(false);
        textOldPassword.setClickable(false);
        textOldPassword.setFocusableInTouchMode(false);
        textOldPassword.setVisibility(View.INVISIBLE);

        textNewPassword.setText("");
        textNewPassword.setFocusable(false);
        textNewPassword.setEnabled(false);
        textNewPassword.setClickable(false);
        textNewPassword.setFocusableInTouchMode(false);
        textNewPassword.setVisibility(View.INVISIBLE);

        btnChangePassword.setText("Change Password");
        btnCancelPassword.setVisibility(View.INVISIBLE);
        btnShowHidePasswordPassword.setVisibility(View.INVISIBLE);
    }

    private void LogOut() {
        mAuth.getInstance().signOut();
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }

}









