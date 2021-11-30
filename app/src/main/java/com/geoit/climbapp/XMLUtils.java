package com.geoit.climbapp;

import android.content.Context;
import android.net.Uri;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLUtils {
    public static Document loadDocument(Context context, Uri uri) throws IOException, SAXException, ParserConfigurationException {
        //Xml wird innerhalb der App nicht validiert! (Validierung per AndroidStudio/IntelliJ)

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(context.getContentResolver().openInputStream(uri));
        return document;
    }

    public static Document loadDocument(File file) throws IOException, SAXException, ParserConfigurationException {
        //Xml wird innerhalb der App nicht validiert! (Validierung per AndroidStudio/IntelliJ)

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        return document;
    }

    public static Document loadDocument(InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
        //Xml wird innerhalb der App nicht validiert! (Validierung per AndroidStudio/IntelliJ)

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputStream);
        return document;
    }
}
