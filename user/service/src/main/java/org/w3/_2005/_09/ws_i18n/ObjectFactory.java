
package org.w3._2005._09.ws_i18n;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.w3._2005._09.ws_i18n package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Tz_QNAME = new QName("http://www.w3.org/2005/09/ws-i18n", "tz");
    private final static QName _Locale_QNAME = new QName("http://www.w3.org/2005/09/ws-i18n", "locale");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.w3._2005._09.ws_i18n
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link International }
     * 
     */
    public International createInternational() {
        return new International();
    }

    /**
     * Create an instance of {@link Preferences }
     * 
     */
    public Preferences createPreferences() {
        return new Preferences();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/09/ws-i18n", name = "tz")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createTz(String value) {
        return new JAXBElement<String>(_Tz_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.w3.org/2005/09/ws-i18n", name = "locale")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLocale(String value) {
        return new JAXBElement<String>(_Locale_QNAME, String.class, null, value);
    }

}
