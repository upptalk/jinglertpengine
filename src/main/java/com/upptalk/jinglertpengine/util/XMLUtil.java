package com.upptalk.jinglertpengine.util;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Generic methods for handling XML
 * @author bhlangonijr
 *
 */
public class XMLUtil {
	private static final Logger log = Logger.getLogger(XMLUtil.class);

	public static String xmlToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			log.error("Exception ocurred: ",e);
		} catch (TransformerException e) {
			log.error("Exception ocurred: ",e);
		}
		return null;
	}
	
	/**
	 * Converts XML source to string
	 * @param xml
	 * @return
	 */
	public static String xmlToString(Source xml) {
		String s=null;
		try {
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(xml, result);
			s = stringWriter.getBuffer().toString();
		} catch (Exception e) {
			log.error("Error transforming message: ",e);
		}
		return s;
	}
	
	/**
	 * Fetch node using its name
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	public static Node fetchByName(Node node, String name) {
		final NodeList list = node.getChildNodes(); 
		for(int i=0;i<list.getLength();i++) {
			final Node child = list.item(i);
            if (child.getNodeName().equals(name)) {
				return child;
			}
		}	
		return null;		
	}
	
	
}
