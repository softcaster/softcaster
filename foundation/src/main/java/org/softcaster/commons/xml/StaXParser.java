/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author ep
 */
public class StaXParser {

    public Item createItem() {
        return new Item();
    }

    @SuppressWarnings({"unchecked", "null"})
    public List<Item> readConfig(String configFile) {
        List<Item> items = new ArrayList<>();
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();

            // Setup a new eventReader
            InputStream in = new FileInputStream(configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            // read the XML document
            Item item = null;
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();

                    // If we have an item element, we create a new item
                    String elementName = startElement.getName().getLocalPart();

                    item = getNextElement(elementName, eventReader);
                } else if (event.isEndElement() && isInsert()) {
                    if (item != null) {
                        items.add(item);
                        reset();
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            System.out.println(e.toString());
        }
        return items;
    }

    protected Item getNextElement(String elementName, XMLEventReader eventReader) throws XMLStreamException {
        return null;
    }

    protected boolean isInsert() {
        return false;
    }

    protected void reset() {
        System.out.println("Burp!");
    }
}
