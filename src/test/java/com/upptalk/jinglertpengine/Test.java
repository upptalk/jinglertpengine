package com.upptalk.jinglertpengine;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author bhlangonijr
 *         Date: 4/24/14
 *         Time: 12:21 AM
 */
public class Test {

    static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<methodCall>\n" +
            "   <methodName>di</methodName>\n" +
            "   <params>\n" +
            "   <param><value><string>sbc</string></value></param>\n" +
            "   <param><value><string>postControlCmd</string></value></param>\n" +
            "   <param><value><string>bob.jy5ju42_24130402@127.0.0.1</string></value></param>\n" +
            "   <param><value><string>teardown</string></value></param>\n" +
            "   </params>\n" +
            "</methodCall>\n";

    static final ThreadLocal<DocumentBuilderFactory> documentBuilderFactor = new ThreadLocal<DocumentBuilderFactory>(){
        public DocumentBuilderFactory  initialValue(){
            return  DocumentBuilderFactory.newInstance();
        }
    };


    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = documentBuilderFactor.get();
        DocumentBuilder parser = factory.newDocumentBuilder();

        //DOMParser parser = new DOMParser();
        //parser.parse(new InputSource(new StringReader(xml) ));
        Document doc = parser.parse(new InputSource(new StringReader(xml) ));
        //Document doc = parser.getDocument();

        // Get the document's root XML node
        NodeList root = doc.getElementsByTagName("methodCall").item(0).getChildNodes();

        for (int i=0; i< root.getLength(); i++) {
            System.out.println(root.item(i).getNodeName());
        }

    }
}