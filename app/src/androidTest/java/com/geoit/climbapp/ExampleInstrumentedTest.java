package com.geoit.climbapp;

import android.content.Context;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.geoit.climbapp", appContext.getPackageName());


//        File file=new File(appContext.getApplicationInfo().dataDir,"testabfrage2.xml");
//        System.out.println("root path: "+file.getAbsolutePath());

        testLocalXML(appContext,"testabfrage2.xml");
        testLocalXML(appContext,"australia.xml");
        testLocalXML(appContext,"pyranaen.xml");
        testLocalXML(appContext,"portugal.xml");

        testLocalXML(appContext,"southamerica.xml");



    }

    private void testLocalXML(Context context,String filename){
        try {
            System.out.println("TESTING XML: "+filename);
            Document doc=XMLUtils.loadDocument(context.getAssets().open(filename));
            Overpasser.parseResponse(doc);

        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

    }
}