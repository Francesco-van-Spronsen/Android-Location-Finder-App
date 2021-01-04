package com.android2;

import android.net.Uri;

import androidx.core.util.PatternsCompat;

import com.android2.ui.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProfileFragmentTest {
    private ProfileFragment mockedProfileFragment = mock(ProfileFragment.class);

    private FirebaseAuth mockFireBaseAuth = Mockito.mock(FirebaseAuth.class);
    private FirebaseUser mockFirebaseUser = Mockito.mock(FirebaseUser.class);

    //1
    @Test
    public void changeEmail_WithValidEmailAddresses_ExpectPass() {
        //Arrange
        boolean emailsAreValid = true;
        List<String> validEmailAddresses = getValidEmailAddresses();
        //Act
        for (String email : validEmailAddresses) {
            if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                emailsAreValid = false;
                break;
            }
        }
        //Assert
        Assert.assertTrue(emailsAreValid);
    }

    private List<String> getValidEmailAddresses() {
        List<String> validEmails = new ArrayList<>();
        validEmails.add("email@example.com");
        validEmails.add("firstname.lastname@example.com");
        validEmails.add("email@subdomain.example.com");
        validEmails.add("firstname+lastname@example.com");
        validEmails.add("email@123.123.123.123");
        validEmails.add("email@example.com");
        validEmails.add("1234567890@example.com");
        validEmails.add("email@example-one.com");
        validEmails.add("_______@example.com");
        validEmails.add("email@example.name");
        validEmails.add("email@example.museum");
        validEmails.add("email@example.co.jp");
        validEmails.add("firstname-lastname@example.com");
        return validEmails;
    }

    //2
    @Test
    public void changeEmail_WithInValidEmailAddresses_ExpectFail() {
        //Arrange
        boolean emailsAreValid = false;
        List<String> InvalidEmailAddresses = getInValidEmailAddresses();
        //Act
        for (String email : InvalidEmailAddresses) {
            if (PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                emailsAreValid = true;
                break;
            }
        }
        //Assert
        Assert.assertFalse(emailsAreValid);
    }

    private List<String> getInValidEmailAddresses() {
        List<String> inValidEmails = new ArrayList<>();
        inValidEmails.add("email");
        inValidEmails.add("#@%^%#$@#$@#.com");
        inValidEmails.add("@example.com");
        inValidEmails.add("Joe Smith <email@example.com>");
        inValidEmails.add("email.example.com");
        inValidEmails.add("email@example@example.com");
        inValidEmails.add("あいうえお@example.com");
        inValidEmails.add("email@example.com (Joe Smith)");
        inValidEmails.add("email@example");
        inValidEmails.add("email@-example.com");
        inValidEmails.add("email@example..com");
        return inValidEmails;
    }

    //3
    @Test
    public void passwordCheck_OldPasswordFieldIsEmpty_ExpectFail() {
        //ARRANGE
        boolean oldPasswordsIsEmpty;
        //ACT
        oldPasswordsIsEmpty = mockedProfileFragment.passwordCheck("", "newPassword");
        //ASSERT
        Assert.assertFalse(oldPasswordsIsEmpty);
    }

    //4
    @Test
    public void passwordCheck_NewPasswordFieldIsTooShort_ExpectFail() {
        //ARRANGE
        boolean newPasswordIsTooShort; //min password length is 6
        //ACT
        newPasswordIsTooShort = mockedProfileFragment.passwordCheck("oldPassword", "12345");
        //ASSERT
        Assert.assertFalse(newPasswordIsTooShort);
    }

    //5
    @Test
    public void updateEmail_WithValidEmailAddress_ExpectPass() {
        //ARRANGE
        String oldEmail = "mockOldEmail";
        String newEmail = "mockNewEmail";
        when(mockFireBaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(Objects.requireNonNull(mockFirebaseUser).getEmail()).thenReturn(newEmail);
        //ACT
        mockFirebaseUser.updateEmail(newEmail);
        newEmail = mockFirebaseUser.getEmail();
        //ASSERT
        Assert.assertNotEquals(oldEmail, newEmail);
    }

    //6
    @Test
    public void getCurrentUserEmail_UserIsNotNull_ExpectPass() {
        //ARRANGE
        when(mockFireBaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(Objects.requireNonNull(mockFirebaseUser).getEmail()).thenReturn("testmail@test.com");
        //ACT
        String mockEmail = Objects.requireNonNull(mockFireBaseAuth.getCurrentUser()).getEmail();
        //ASSERT
        Assert.assertEquals("testmail@test.com", mockEmail);
    }

    //7
    @Test(expected = NullPointerException.class)
    public void getCurrentUserEmail_UserIsNull_ExpectNullPointerExceptionFail() {
        //ARRANGE
        when(mockFireBaseAuth.getCurrentUser()).thenReturn(null);
        //ACT
        String mockEmail = Objects.requireNonNull(mockFireBaseAuth.getCurrentUser()).getEmail();
        //ASSERT
        Assert.assertNull(mockEmail);
    }

    //8
    @Test
    public void getPhotoUrl_ValidPhotoUrl_ExpectNotNull() {
        //ARRANGE
        Uri mockUri = Mockito.mock(Uri.class);
        when(mockFirebaseUser.getPhotoUrl()).thenReturn(mockUri);
        //ACT
        Uri mockPhotoUrl = mockFirebaseUser.getPhotoUrl();
        //ASSERT
        Assert.assertNotNull(mockPhotoUrl);
    }

}
