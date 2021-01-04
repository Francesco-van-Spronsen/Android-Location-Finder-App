package com.android2;

import com.android2.auth.AccountManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountManagerTest {
    private AccountManager mockedAccountManager = mock(AccountManager.class);
    private FirebaseAuth mockFireBaseAuth = mock(FirebaseAuth.class);
    private FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);

    @Before
    public void setUp() {
        mockedAccountManager.mAuth = mockFireBaseAuth;
    }

    @Test
    public void getCurrentUser_UserIsNotNull_ExpectPass() {
        when(mockedAccountManager.getCurrentUser())
                .thenReturn(mockFirebaseUser);

        FirebaseUser tempFirebaseUser = mockedAccountManager.getCurrentUser();
        Assert.assertEquals(mockFirebaseUser, tempFirebaseUser);
        Assert.assertNotNull(tempFirebaseUser);
    }

    @Test
    public void isAuthenticated_ExpectPass() {
        when(mockedAccountManager.isAuthenticated())
                .thenReturn(mockFirebaseUser != null);

        Assert.assertTrue(mockedAccountManager.isAuthenticated());
    }

    @Test
    public void signOut_ExpectPass() {
        doNothing().when(mockedAccountManager).signOut();
        verify(mockFirebaseUser, null);
    }
}
