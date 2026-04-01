/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author ep
 */
public class BondStaxParser {

    private static BondStaxParser instance_ = null;
    private ItemBond item = null;
    private final List<ItemBond> items = new ArrayList<>();

    public static BondStaxParser getInstance() {
        if (instance_ == null) {
            instance_ = new BondStaxParser();
        }

        return instance_;
    }

    public List<ItemBond> readFile(String fileName) throws FileNotFoundException, XMLStreamException {

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader
                = factory.createXMLStreamReader(new FileInputStream(/*System.getProperty("user.dir") + "//import//bonds_ita.xml"*/fileName));

        String elementName = "";
        while (reader.hasNext()) {
            int event = reader.next();
            switch (event) {
                case XMLStreamConstants.START_ELEMENT -> {
                    elementName = reader.getLocalName();
                    switch (elementName) {
                        case "bond":
                            item = new ItemBond();
                            break;
                        case "isincode":
                            item.isincode = reader.getElementText();
                            break;
                        case "description":
                            item.description = reader.getElementText();
                            break;
                        case "currency":
                            item.currency = reader.getElementText();
                            break;                                                       
                        case "minimumlot":
                            item.minimumlot = Double.parseDouble(reader.getElementText());
                            break;
                        case "status":
                            item.status = reader.getElementText();
                            break;
                        case "amount":
                            item.amount = Double.parseDouble(reader.getElementText());
                            break;
                        case "issuedate":
                            item.issuedate = java.sql.Date.valueOf(reader.getElementText());
                            break;
                        case "issueprice":
                            item.issueprice = Double.parseDouble(reader.getElementText());
                            break;
                        case "redemptiondate":
                            item.redemptiondate = java.sql.Date.valueOf(reader.getElementText());
                            break;
                        case "redemptionprice":
                            item.redemptionprice = Double.parseDouble(reader.getElementText());
                            break;
                        case "coupon": {
                            Coupon c = new Coupon();
                            c.couponDate = java.sql.Date.valueOf(reader.getAttributeValue(null, "date"));
                            c.couponValue = Double.parseDouble(reader.getAttributeValue(null, "value"));
                            item.coupons.add(c);
                        }
                        break;
                        case "couponrate":
                            item.couponrate = Double.parseDouble(reader.getElementText());
                            break;
                        case "couponperiodicity":
                            item.couponperiodicity = Integer.parseInt(reader.getElementText());
                            break;
                        case "ccissuedelta":
                            item.ccissuedelta = Integer.parseInt(reader.getElementText());
                            break;
                        case "cccoupon":
                            item.cccoupon = Integer.parseInt(reader.getElementText());
                            break;
                        case "taxrate":
                            item.taxrate = Double.parseDouble(reader.getElementText());
                            break;
                        default:
                            break;
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    elementName = reader.getLocalName();
                    switch (elementName) {
                        case "bond" -> items.add(item);
                        default -> {
                    }
                    }
                }
            }
        }
        reader.close();

        return items;
    }
}
