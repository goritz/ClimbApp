package com.geoit.climbapp.overpass;

import android.util.Log;

import com.geoit.climbapp.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class ElementFactory {

    public static ArrayList<TaggedElement> parseResponse(Document document) throws OverpassException {

        ArrayList<TaggedElement> nodeElements = new ArrayList<>();
        ArrayList<TaggedElement> wayElements = new ArrayList<>();

        HashMap<Long, LatLng> referenceMap = new HashMap<>();


        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("node");

        for (int i = 0; i < nodeList.getLength(); i++) {

            Element n = (Element) nodeList.item(i);
            try {

                BaseElement baseElement = ElementFactory.parseElement(n);
                if (baseElement instanceof ReferenceElement) {
                    ReferenceElement ref = (ReferenceElement) baseElement;
                    referenceMap.put(ref.getId(), ref.getLatLng());

                } else if (baseElement instanceof TaggedElement) {
                    TaggedElement el = (TaggedElement) baseElement;
                    nodeElements.add(el);
                    Log.d("[NODE]",el.toString());
                }

            } catch (OverpassException e) {
                e.printStackTrace();
            }

        }

        NodeList wayList = root.getElementsByTagName("way");

        for (int i = 0; i < wayList.getLength(); i++) {

            Element w = (Element) wayList.item(i);
            try {
                BaseElement baseElement = ElementFactory.parseElement(w);

                if (baseElement instanceof TaggedElement) {
                    TaggedElement el = (TaggedElement) baseElement;

                    ArrayList<LatLng> refLatLngs = new ArrayList<>();
                    for (Long l : el.getReferenceIDs()) {
                        LatLng latLng = referenceMap.get(l);
                        if (latLng != null) {
                            refLatLngs.add(latLng);
                        }
                    }

                    LatLng centroid = LatLng.calcCentroid(refLatLngs);
                    el.setLatLng(centroid);

                    wayElements.add(el);
                    Log.d("[WAY]",el.toString());
                }

            } catch (OverpassException e) {
                e.printStackTrace();
            }
        }

        Log.d("[Parse Result]","nodes found: "+nodeElements.size());
        Log.d("[Parse Result]","ways found: "+wayElements.size());
        Log.d("[Parse Result]","total references: "+referenceMap.size());


        ArrayList<TaggedElement> resultList = new ArrayList<>(nodeElements);
        resultList.addAll(wayElements);

        return resultList;
    }

    /**
     * Liest die
     * @param osmElement
     * @return
     * @throws OverpassException
     */
    private static BaseElement parseElement(Element osmElement) throws OverpassException {

        long id;

        try {
            id = Long.parseLong(osmElement.getAttribute("id"));
        } catch (NumberFormatException | NullPointerException | DateTimeParseException nfe) {
            nfe.printStackTrace();
            throw new OverpassException("could not resolve attributes of " + osmElement.toString(), nfe.getCause());
        }

        boolean isReferenceElement = false;
        ElementType type = ElementType.UNKNOWN;

        if (osmElement.getTagName().equals("node")) {
            type = ElementType.NODE;
            if (osmElement.getFirstChild() == null) {
                isReferenceElement = true;
            }
        } else if (osmElement.getTagName().equals("way")) {
            type = ElementType.WAY;
        }

        LatLng latLng = null;
        if (type == ElementType.NODE) {
            try {

                double lat = Double.parseDouble(osmElement.getAttribute("lat"));
                double lng = Double.parseDouble(osmElement.getAttribute("lon"));
                latLng = new LatLng(lat, lng);
                if (isReferenceElement) {
                    return new ReferenceElement(id, latLng);
                }

            } catch (NumberFormatException | NullPointerException | DateTimeParseException nfe) {
                nfe.printStackTrace();
                throw new OverpassException("could not resolve attributes of " + osmElement.toString(), nfe.getCause());
            }
        }

        TaggedElement element = new TaggedElement(id, type, osmElement);
        if (type == ElementType.NODE) {
            element.setLatLng(latLng);
        }

        return element;
    }
}
