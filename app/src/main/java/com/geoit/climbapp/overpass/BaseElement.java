package com.geoit.climbapp.overpass;

import org.w3c.dom.Element;

import java.time.format.DateTimeParseException;

public abstract class BaseElement{

    private final long id;
//    private long version=0;
//    private Instant timeStamp;




    public BaseElement(long id) {
        this.id = id;
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
