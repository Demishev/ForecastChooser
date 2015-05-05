/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.forecastchooser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Pavel
 */
public class XMLParser {

    Document doc = null;

    public Document getDocumentFromXML(String l) {
        try {
            URL forecastURL = new URL(l);
            HttpURLConnection conn = (HttpURLConnection) forecastURL.openConnection();
            InputSource source = new InputSource(conn.getInputStream());
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (MalformedURLException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return doc;
    }

    public void parseDocumentOPENWEATHER(Node n) {
        int minTempValue = 1000;
        int maxTempValue = -1000;
        int humid = 1000;
        
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if (subNodeLevel1.getNodeName() == "forecast") {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if (subNodeLevel2.getNodeName() == "time") {
                        DateFormat formatOPENWEATHER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String s1 = getAttribute(subNodeLevel2,"day");
                        String s2 = formatOPENWEATHER.format(getTomorrow());
                        if (s1.equals(s2)){
                            NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                            for (int k = 0; k < subListLevel3.getLength(); k++) {
                                Node subNodeLevel3 = subListLevel3.item(k);
                                if (subNodeLevel3.getNodeType() == Node.ELEMENT_NODE) {
                                    if (subNodeLevel3.getNodeName() == "temperature"){
                                        float min = Float.parseFloat(getAttribute(subNodeLevel3, "min"));
                                        float max = Float.parseFloat(getAttribute(subNodeLevel3, "max"));
                                        minTempValue = Math.round(min);
                                        maxTempValue = Math.round(max);
                                    } else if (subNodeLevel3.getNodeName().equals("humidity")) {
                                        humid = Integer.parseInt(getAttribute(subNodeLevel3, "value"));
//                                    } else if (subNodeLevel3.getNodeName().equals("clouds")) {
//                                        System.out.println("clouds = " + getAttribute(subNodeLevel3, "value"));
//                                    }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("WWW.OPENWEATHER.COM:");
        System.out.println("tempreture [" + minTempValue + " - " + maxTempValue + "]");
        System.out.println("humidity = " + humid + "%");
    }

    public void parseDocumentYANDEX(Node n) {
        int dayTempValue = -1000;
        int nightTempValue = -1000;
        int dayHumid = -1000;
        int nightHumid = -1000;
        String dayClouds = null;
        String nightClouds = null;
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if (subNodeLevel1.getNodeName() == "day") {
                DateFormat formatYANDEX = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                String s1 = getAttribute(subNodeLevel1,"date");
                String s2 = formatYANDEX.format(getTomorrow());
                
                if (s1.equals(s2)){
                    NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                    for (int k = 0; k < subListLevel2.getLength(); k++) {
                        Node subNodeLevel2 = subListLevel2.item(k);
                        if (subNodeLevel2.getNodeType() == Node.ELEMENT_NODE) {
                            if (subNodeLevel2.getNodeName() == "day_part"){
                                if (getAttribute(subNodeLevel2,"type").equals("day_short")){
                                    NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                                    for (int j = 0; j<subListLevel3.getLength(); j++){
                                        Node subNodeLevel3 = subListLevel3.item(j);
                                        if (subNodeLevel3.getNodeName()=="temperature"){
                                            dayTempValue = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        } else if (subNodeLevel3.getNodeName()=="humidity"){
                                            dayHumid = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        }
                                    }
                                } else if (getAttribute(subNodeLevel2,"type").equals("night_short")){
                                    NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                                    for (int j = 0; j<subListLevel3.getLength(); j++){
                                        Node subNodeLevel3 = subListLevel3.item(j);
                                        if (subNodeLevel3.getNodeName()=="temperature"){
                                            nightTempValue = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        } else if (subNodeLevel3.getNodeName()=="humidity"){
                                            nightHumid = Integer.parseInt(subNodeLevel3.getFirstChild().getNodeValue());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("WWW.YANDEX.RU:");
        System.out.println("tempreture [" + nightTempValue + " - " + dayTempValue + "]");
        int humid = (dayHumid + nightHumid)/2;
        System.out.println("humidity = " + humid + "%");
    }

    public void parseDocumentWEATHERCOMUA(Node n) {
        int minTempValue = 1000;
        int maxTempValue = -1000;
        int minHumid = 1000;
        int maxHumid = -1000;
        NodeList rootNodeList = n.getChildNodes();
        Node rootNode = rootNodeList.item(0);
        NodeList subListLevel1 = rootNode.getChildNodes();
        for (int i = 0; i < subListLevel1.getLength(); i++) {
            Node subNodeLevel1 = subListLevel1.item(i);
            if (subNodeLevel1.getNodeName() == "forecast") {
                NodeList subListLevel2 = subNodeLevel1.getChildNodes();
                for (int j = 0; j < subListLevel2.getLength(); j++) {
                    Node subNodeLevel2 = subListLevel2.item(j);
                    if (subNodeLevel2.getNodeName() == "day") {
                        DateFormat formatOPENWEATHER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        String s1 = getAttribute(subNodeLevel2,"date");
                        String s2 = formatOPENWEATHER.format(getTomorrow());
                        if (s1.equals(s2)){
                            NodeList subListLevel3 = subNodeLevel2.getChildNodes();
                            for (int k = 0; k < subListLevel3.getLength(); k++) {
                                Node subNodeLevel3 = subListLevel3.item(k);
                                if (subNodeLevel3.getNodeType() == Node.ELEMENT_NODE) {
                                    if (subNodeLevel3.getNodeName() == "t"){
                                        NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                        for (int l = 0; l<subListLevel4.getLength(); l++){
                                            Node subNodeLevel4 = subListLevel4.item(l);
                                            if (subNodeLevel4.getNodeName()=="min"){
                                                int localMin = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (minTempValue>localMin){
                                                    minTempValue=localMin;
                                                }
                                            } else if (subNodeLevel4.getNodeName()=="max"){
                                                int localMax = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (maxTempValue<localMax){
                                                    maxTempValue=localMax;
                                                }
                                            }
                                        }
                                    } else if (subNodeLevel3.getNodeName() == "hmid"){
                                        NodeList subListLevel4 = subNodeLevel3.getChildNodes();
                                        for (int l = 0; l<subListLevel4.getLength(); l++){
                                            Node subNodeLevel4 = subListLevel4.item(l);
                                            if (subNodeLevel4.getNodeName()=="min"){
                                                int localMinHumid = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (minHumid>localMinHumid){
                                                    minHumid=localMinHumid;
                                                }
                                            } else if (subNodeLevel4.getNodeName()=="max"){
                                                int localMaxHumid = Integer.parseInt(subNodeLevel4.getFirstChild().getNodeValue());
                                                if (maxHumid<localMaxHumid){
                                                    maxHumid=localMaxHumid;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("WWW.WEATHER.CO.UA:");
        System.out.println("tempreture [" + minTempValue + " - " + maxTempValue + "]");
        int humid = (minHumid + maxHumid)/2;
        System.out.println("humidity = " + humid + "%");
    }

    private String getAttribute(Node x, String attribute) {
        return x.getAttributes().getNamedItem(attribute).getNodeValue();
    }

    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    private Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        
        return tomorrow;
    }
    
    static private Date stringToDate(String s){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(s);
        } catch (ParseException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }
}
