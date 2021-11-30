package com.geoit.climbapp;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import timber.log.Timber;

public class Overpasser {


    public static void parseResponse(Document document) {

        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("node");

        ArrayList<ClimbingElement> elements=new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element n = (Element) nodeList.item(i);

            try {
                ClimbingElement climbingElement=new ClimbingElement(n);
                elements.add(climbingElement);
            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }


        System.out.println(elements.size()+" elements!");

        for(ClimbingElement el:elements){
            System.out.println(el.toString());
        }

    }

}
