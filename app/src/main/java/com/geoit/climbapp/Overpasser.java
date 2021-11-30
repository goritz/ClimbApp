package com.geoit.climbapp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Overpasser {


    public static void parseResponse(Document document) {
        ArrayList<ClimbingElement> nodeElements=new ArrayList<>();
        ArrayList<ClimbingElement> wayElements=new ArrayList<>();



        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("node");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element n = (Element) nodeList.item(i);
            try {
                ClimbingElement climbingElement=new ClimbingElement(n);
                nodeElements.add(climbingElement);
            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }
        NodeList wayList = root.getElementsByTagName("way");

        for (int i = 0; i < wayList.getLength(); i++) {

            Element w = (Element) wayList.item(i);
            try {
                ClimbingElement climbingElement=new ClimbingElement(w);
                wayElements.add(climbingElement);
            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }


        System.out.println(nodeElements.size()+" node elements!");
        System.out.println(wayElements.size()+" way elements!");

        for(ClimbingElement el:nodeElements){
            System.out.println(el.toString());
        }
        for(ClimbingElement el:wayElements){
            System.out.println(el.toString());
        }

    }

}
