/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author ep
 */
public class Params extends StaXParser {

    private static Params instance_ = null;
    private String elementName = "";
    private Item item = null;

    public static Params getInstance() {
        if (instance_ == null) {
            instance_ = new Params();
        }

        return instance_;
    }

    @Override
    protected boolean isInsert() {
        return "value".equals(elementName);
    }

    @Override
    protected void reset() {
        elementName = "";
    }

    /**
     *
     * @param elementName
     * @param eventReader
     * @return
     * @throws javax.xml.stream.XMLStreamException
     */
    @Override
    protected Item getNextElement(String elementName, XMLEventReader eventReader) throws XMLStreamException {
        this.elementName = elementName;
        XMLEvent event;

        switch (elementName) {
            case "params" -> {
            }
            case "param" -> item = createItem();
            case "name" -> {
                event = eventReader.nextEvent();
                item.setId(event.asCharacters().getData());
            }
            case "value" -> {
                event = eventReader.nextEvent();
                item.setValue(event.asCharacters().getData());
            }
            default -> {
            }
        }

        return item;
    }
}
