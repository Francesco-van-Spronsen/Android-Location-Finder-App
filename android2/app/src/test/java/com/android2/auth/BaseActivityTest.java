package com.android2.auth;

import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android2.MainActivity;
import com.android2.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BaseActivityTest {


    private BaseActivity mockedBaseActivity = mock(BaseActivity.class);
    private EditText mockedEditTest = mock (EditText.class);
    private MainActivity mockedMainActivity = mock(MainActivity.class);
    private  ProgressBar MockedProgressBar =mock (ProgressBar.class);

    private BaseActivity baseActivity;



    @Before
    public void setUp() throws Exception {
     baseActivity = new BaseActivity ();
     mockedMainActivity = new MainActivity ();
     //testPb = new ProgressBar ();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void ShowProgressBarTest() {
     //arange
     int exptected =1;
     //act
     int actual =baseActivity.showProgressBar ();
     //assert
     assertEquals (exptected,actual);     ;

    }

    @Test
    public void hideProgressBarTest() {
     //arrange
     int exptected =0;
     //act
     int actual =baseActivity.hideProgressBar ();
     //assert
     assertEquals (exptected,actual);     ;
    }

}