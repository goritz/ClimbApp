package com.geoit.climbapp.overpass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

@Deprecated
public class Overpasser {


    @Deprecated
    public static void parseResponse(Document document) {
        ArrayList<TaggedElement> nodeElements=new ArrayList<>();
        ArrayList<TaggedElement> wayElements=new ArrayList<>();



        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("node");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element n = (Element) nodeList.item(i);
            try {
                TaggedElement taggedElement =new TaggedElement(0, ElementType.UNKNOWN,n);
                nodeElements.add(taggedElement);
            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }
        NodeList wayList = root.getElementsByTagName("way");

        for (int i = 0; i < wayList.getLength(); i++) {

            Element w = (Element) wayList.item(i);
            try {
                TaggedElement taggedElement =new TaggedElement(0, ElementType.UNKNOWN,w);
                wayElements.add(taggedElement);
            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }


        System.out.println(nodeElements.size()+" node elements!");
        System.out.println(wayElements.size()+" way elements!");

        for(TaggedElement el:nodeElements){
            System.out.println(el.toString());
        }
        for(TaggedElement el:wayElements){
            System.out.println(el.toString());
        }

    }

}
