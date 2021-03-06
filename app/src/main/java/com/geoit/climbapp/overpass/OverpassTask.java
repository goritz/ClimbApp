package com.geoit.climbapp.overpass;

import android.util.Log;

import com.geoit.climbapp.LatLng;
import com.geoit.climbapp.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;

public class OverpassTask implements Callable<ArrayList<TaggedElement>> {


    private URL requestURL = null;

    public OverpassTask(String api, int radius, LatLng position) {

        radius *= 1000;

        if (position == null || (position.getLongitude() == 0.00f && position.getLatitude() == 0.00f)) {
            return;
        }


        try {
            requestURL = new URL(api + "?data=(" +
                    "node[sport=climbing](around:" + radius + ',' + position.getLatitude() + ',' + position.getLongitude() + ");" +
                    "way[sport=climbing](around:" + radius + ',' + position.getLatitude() + ',' + position.getLongitude() + ");" +
                    "(._;>;);" +
                    ");out%20meta;");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws OverpassException if unable to compute a result
     */
    @Override
    public ArrayList<TaggedElement> call() throws OverpassException, SocketTimeoutException {
        ArrayList<TaggedElement> elements = new ArrayList<>();

        HttpURLConnection connection=null;
        InputStream in=null;


        try {            //OSM Query as URL

            connection = (HttpURLConnection) requestURL.openConnection();
//
            System.out.println("Starte Verbindung ...");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(12500);
            connection.connect();

            //Getting Inputstream as Response

            in = connection.getInputStream();


//            System.out.println("TESTING XML: "+input);
            Document doc = XMLUtils.loadDocument(in);

            NodeList list = doc.getDocumentElement().getChildNodes();
            System.out.println("XML Response has " + list.getLength() + "child nodes!");
//            Overpasser.parseResponse(doc);


            elements = ElementFactory.parseResponse(doc);

        }catch (SocketTimeoutException | UnknownHostException te){
            te.printStackTrace();
            throw new SocketTimeoutException("timeout");


        } catch (IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            throw new OverpassException("could not parse xml result!", e.getCause());

        } finally {
        //always close stream and connection
        if (in != null) {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
        if (connection != null) {
            connection.disconnect();
        }

    }
        return elements;
    }
}
