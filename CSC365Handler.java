package com.company;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import javax.swing.plaf.synth.SynthDesktopIconUI;

public class CSC365Handler extends DefaultHandler {

    Hashtable[] articles = new Hashtable[100];

    @Override
    public void startDocument() {
        System.out.println("Parsing started!");
    }

    @Override
    public void endDocument() {
        System.out.println("Parsing completed!");
    }

    @Override
    public void startElement(String ignore1, String ignore2, String tagName, Attributes attr) {
        System.out.println("--*--");

        if (tagName.equals("wiki")) {

            System.out.println("It's a wikipedia article!");
            System.out.println(attr.getValue("name"));
            System.out.println(attr.getValue("url"));
            //create article
            Hashtable aaa = new Hashtable(attr.getValue("name"),attr.getValue("url"));
            //add to array
            for (int i = 0; i < articles.length; i++) {
                if (articles[i] == null) {
                    articles[i] = aaa;
                    i = articles.length;
                }
            }
        }
    }
}

