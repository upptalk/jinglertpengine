package com.upptalk.jinglertpengine.web.servlets;

import com.upptalk.jinglertpengine.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bhlangonijr
 *         Date: 4/27/14
 *         Time: 8:17 PM
 */
public class XmlRpcRequest {
    static final ThreadLocal<DocumentBuilderFactory> documentBuilderFactor = new ThreadLocal<DocumentBuilderFactory>(){
        public DocumentBuilderFactory  initialValue(){
            return  DocumentBuilderFactory.newInstance();
        }
    };

    private final String methodName;
    private final List<String> params;

    public XmlRpcRequest(String methodName, List<String> params) {
        this.methodName = methodName;
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParams() {
        return params;
    }

    public static XmlRpcRequest parseFromStream(InputStream is) throws ParserConfigurationException,
            IOException, SAXException {
        DocumentBuilderFactory factory = documentBuilderFactor.get();
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.parse(new InputSource(is));
        final List<String> paramList = new ArrayList<String>();
        final String methodName = XMLUtil.fetchByName(doc.getElementsByTagName("methodCall").item(0),
                "methodName").getTextContent();
        Node params = XMLUtil.fetchByName(doc.getElementsByTagName("methodCall").item(0),
                "params");
        for (int i=0; i<  params.getChildNodes().getLength(); i++) {
            Node node = params.getChildNodes().item(i);
            if (node.getNodeName().equals("param")) {
                paramList.add(params.getChildNodes().item(i).getTextContent());
            }
        }
        return new XmlRpcRequest(methodName, paramList);
    }

    @Override
    public String toString() {
        return "XmlRpcRequest{" +
                "methodName='" + methodName + '\'' +
                ", params=" + params +
                '}';
    }
}





