package com.geoit.climbapp;

import org.w3c.dom.Element;

import java.time.format.DateTimeParseException;

public abstract class BaseElement{

    private final long id;
//    private long version=0;
//    private Instant timeStamp;


    public BaseElement(Element osmNode) throws OverpassException {

//<way id="998135237" version="1" timestamp="2021-11-01T11:48:03Z" changeset="113232327" uid="114161" user="Polarbear">

        try {
            id = Long.parseLong(osmNode.getAttribute("id"));
//            version=Integer.parseInt(osmElement.getAttribute("version"));
//            timeStamp=Instant.parse(osmElement.getAttribute("timeStamp"));

        } catch (NumberFormatException | NullPointerException | DateTimeParseException nfe) {
            nfe.printStackTrace();
            throw new OverpassException("could not resolve attributes of " + osmNode.toString(), nfe.getCause());
        }

    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BaseElement{" +
                "id=" + id +
                '}';
    }
}
