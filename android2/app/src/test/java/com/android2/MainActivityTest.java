package com.android2;

import com.android2.ui.people.Person;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class MainActivityTest {
    private  MainActivity mainActivity;
    private Person person;

    @Before
    public void setUp() {
         mainActivity = new MainActivity ();
        person = new Person(
                "mockEmail",
                "test",
                1,
                1,
                true);
    }

    @Test
    public void personGetterSetterTest_ValidPerson_ExpectPass() {
        //ARRANGE
        //ACT
        mainActivity.setPerson(person);
        //ASSERT
        Assert.assertEquals(person, mainActivity.getPerson());
    }

    @Test
    public void onStop_PersonIsLoggedIn_ExpectPass() {
        //ARRANGE
        boolean isLoggedIn;
        MainActivity mockMA = Mockito.mock(MainActivity.class);

        when(mockMA.getPerson()).thenReturn(person);

        doAnswer(invocation -> {
            person.setLoggedIn(false);
            return null;
        }).when(mockMA).onStop();
        //ACT
        mockMA.setPerson(person);
        mockMA.onStop();
        isLoggedIn = person.isLoggedIn();
        //ASSERT
        Assert.assertFalse(isLoggedIn);
    }


}