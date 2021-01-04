package com.android2;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.android2.auth.BaseActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class BaseActivityTest {

    private BaseActivity mockedBaseActivity = mock(BaseActivity.class);
    private ProgressBar mockedProgressBar = mock(ProgressBar.class);
    private EditText mockedEditText = mock(EditText.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        doCallRealMethod().when(mockedBaseActivity).setProgressBar(isA(ProgressBar.class));
        mockedBaseActivity.setProgressBar(mockedProgressBar);
    }

    @Test
    public void setProgressBar_ExpectPass() {
        Assert.assertNotNull(mockedBaseActivity.mProgressBar);
    }

    @Test
    public void showProgressBar_ExpectPass() {
        doNothing().when(mockedBaseActivity).showProgressBar();
        mockedBaseActivity.showProgressBar();

        Assert.assertTrue(mockedBaseActivity.mProgressBar.getVisibility() == View.VISIBLE);
    }

    @Test
    public void hideProgressBar_ExpectPass() {
        doNothing().doThrow().when(mockedBaseActivity).hideProgressBar();
        mockedBaseActivity.hideProgressBar();

        Assert.assertTrue(mockedProgressBar.getVisibility() == View.INVISIBLE);
    }

    @Test
    public void isValidEmail_ExpectPass() {
        doReturn(true).when(mockedBaseActivity).isValidEmail(isA(String.class), isA(EditText.class));
        boolean result = mockedBaseActivity.isValidEmail("admin@gmail.com", mockedEditText);

        Assert.assertTrue(result);
    }

    @Test
    public void isValidEmail_ExpectFail() {
        doReturn(false).when(mockedBaseActivity).isValidEmail(isA(String.class), isA(EditText.class));
        boolean result = mockedBaseActivity.isValidEmail("invalid_email.com", mockedEditText);

        Assert.assertFalse(result);
    }
}
