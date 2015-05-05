/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kostenko.forecastchooser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 *
 * @author Pavel
 */
public class ForecastChooser {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {

        XMLParser parser = new XMLParser();
        
        String link1 = "http://api.openweathermap.org/data/2.5/forecast/daily?q=Tbilisi&mode=xml&units=metric&cnt=7";
        String link2 = "http://export.yandex.ru/weather-ng/forecasts/37549.xml";
        String link3 = "http://xml.weather.co.ua/1.2/forecast/53137?dayf=5&userid=YourSite_com&lang=uk";
        
        Document d1 = parser.getDocumentFromXML(link1);
        parser.parseDocumentOPENWEATHER(d1);
        
        System.out.println("");
        
        Document d2 = parser.getDocumentFromXML(link2);
        parser.parseDocumentYANDEX(d2);
        
        System.out.println("");
        
        Document d3 = parser.getDocumentFromXML(link3);
        parser.parseDocumentWEATHERCOMUA(d3);
        
        
//        try {
//            System.out.println("");
//            XMLParser.printDocument(d1, System.out);
//        } catch (IOException ex) {
//            Logger.getLogger(ForecastChooser.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (TransformerException ex) {
//            Logger.getLogger(ForecastChooser.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    
}
