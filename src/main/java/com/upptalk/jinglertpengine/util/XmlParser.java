package com.upptalk.jinglertpengine.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author bhlangonijr
 *         Date: 5/26/14
 *         Time: 6:47 PM
 */
public class XmlParser {

    private final XStream stream;

    public XmlParser() {
        stream = new XStream(new DomDriver()) {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MapperWrapper(next) {
                    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                        return definedIn != Object.class && super.shouldSerializeMember(definedIn, fieldName);
                    }
                };
            }
        };
        stream.autodetectAnnotations(true);
    }

    public String toXML(Object object) {
        return stream.toXML(object);
    }

    public Object fromXML(String xml) {
        return stream.fromXML(xml);
    }

}
