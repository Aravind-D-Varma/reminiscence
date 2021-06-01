package com.example.nostalgia;

import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class IntroPagerAdapterTest{

    private IntroPagerAdapter mIntroPagerAdapter;

    @Before
    public void before(){
        mIntroPagerAdapter = new IntroPagerAdapter(InstrumentationRegistry.getInstrumentation().getContext());
    }
    @Test
    public void listOfStringToStringTest(){
        List<String> allEvents = new LinkedList<String>();
        allEvents.add("Student Life");allEvents.add("Work");allEvents.add("Festivals");
        String actual = mIntroPagerAdapter.stringListToString(allEvents);
        String expected = "Student Life,Work,Festivals,";
        assertEquals("Joining Events gone wrong",expected,actual);
    }

}