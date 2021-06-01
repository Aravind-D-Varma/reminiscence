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

    @Test
    public void testGetExtension() {
        String videoFileName = mediaPaths[0];
        String imageFileName = mediaPaths[1];

        String videoExpectedExtension = MyGalleryAdapter.getExtension(videoFileName);
        String imageExpectedExtension = MyGalleryAdapter.getExtension(imageFileName);

        String videoActualExtension = "mp4";
        String imageActualExtension = "jpg";

        assertEquals("Getting video extension failed",videoExpectedExtension,videoActualExtension);
        assertEquals("Getting image extension failed",imageExpectedExtension,imageActualExtension);
    }

    @Test
    public void testGetMimeType() {
        String videoFileName = mediaPaths[0];
        String imageFileName = mediaPaths[1];

        String videoExpectedMimeType = MyGalleryAdapter.getMimeType(videoFileName);
        String imageExpectedMimeType = MyGalleryAdapter.getMimeType(imageFileName);

        String videoActualMimeType = "video/mp4";
        String imageActualMimeType = "image/jpeg";

        assertEquals("Getting video mime type failed",videoExpectedMimeType,videoActualMimeType);
        assertEquals("Getting image mime type failed",imageExpectedMimeType,imageActualMimeType);
    }

    @Test
    public void testIsImageFile() {

        String imageFileName = mediaPaths[1];

        String imageExpectedisImageFile = String.valueOf(MyGalleryAdapter.isImageFile(imageFileName));

        String imageActualisImageFile = String.valueOf(true);

        assertEquals("Checking filepath has image failed ",imageExpectedisImageFile,imageActualisImageFile);
    }
    @Test
    public void testIsVideoFile(){
        String videoFileName = mediaPaths[0];

        String videoExpectedisVideoFile = String.valueOf(MyGalleryAdapter.isVideoFile(videoFileName));

        String videoActualisVideoFile = String.valueOf(true);

        assertEquals("Checking filepath has video failed",videoExpectedisVideoFile,videoActualisVideoFile);
    }

}