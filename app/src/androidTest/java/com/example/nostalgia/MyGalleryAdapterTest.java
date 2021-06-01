package com.example.nostalgia;
import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MyGalleryAdapterTest {

    String[] mediaPaths;

    @Before
    public void setUp(){
        mediaPaths = ("/storage/emulated/0/Download/sometimes-good-sometimes-shit.mp4," +
                "/storage/emulated/0/Download/IMG_20210401_124550-min (1).jpg").split(",");
    }

}