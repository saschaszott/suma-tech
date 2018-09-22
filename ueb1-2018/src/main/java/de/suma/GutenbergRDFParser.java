package de.suma;

import org.apache.commons.lang3.StringUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

public class GutenbergRDFParser {

    public GutenbergDoc parse(File file) throws FileNotFoundException {

        GutenbergDoc document = null;
        InputStream in = null;
        XMLEventReader eventReader = null;
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            in = new FileInputStream(file);
            eventReader = inputFactory.createXMLEventReader(in);
            boolean authorStart = false;
            boolean subjectStart = false;
            boolean languageStart = false;
            boolean hasFormatStart = false;
            boolean typeStart = false;
            String fileURL = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    QName name = event.asStartElement().getName();
                    String localPart = name.getLocalPart();
                    String prefix = name.getPrefix();
                    switch (prefix + ":" + localPart) {
                        case "rdf:RDF":
                            document = new GutenbergDoc();
                            String docId = StringUtils.substringBetween(file.getName(), "pg", ".rdf");
                            document.setDocId(docId);
                            break;
                        case "dcterms:title":
                            document.setTitle(getCharacterData(eventReader));
                            break;
                        case "dcterms:creator":
                            authorStart = true;
                            break;
                        case "pgterms:name":
                            if (authorStart) {
                                document.setAuthor(getCharacterData(eventReader));
                            }
                            break;
                        case "pgterms:webpage":
                            if (authorStart) {
                                QName qName = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#","resource", "rdf");
                                Attribute attribute = event.asStartElement().getAttributeByName(qName);
                                String wikipediaUrl = attribute.getValue();
                                if (StringUtils.startsWith(wikipediaUrl, "http://en.wikipedia.org")) {
                                    document.setAuthorWikipediaURL(wikipediaUrl);
                                }
                            }
                            break;
                        case "dcterms:subject":
                            subjectStart = true;
                            break;
                        case "rdf:value":
                            if (subjectStart) {
                                document.addSubjectHeading(getCharacterData(eventReader));
                                subjectStart = false;
                            }
                            else if (languageStart) {
                                document.addLanguage(getCharacterData(eventReader));
                                languageStart = false;
                            }
                            else if (typeStart) {
                                document.setDocType(getCharacterData(eventReader));
                                typeStart = false;
                            }
                            else if (fileURL != null) {
                                String value = getCharacterData(eventReader);
                                if ("text/plain; charset=utf-8".equals(value)) {
                                    document.setFulltextUrl(fileURL);
                                }
                                fileURL = null;
                                hasFormatStart = false;
                            }
                            break;
                        case "dcterms:language":
                            languageStart = true;
                            break;
                        case "pgterms:downloads":
                            document.setNumOfDownloadsLast30Days(getCharacterData(eventReader));
                            break;
                        case "dcterms:type":
                            typeStart = true;
                            break;
                        case "dcterms:hasFormat":
                            hasFormatStart = true;
                            break;
                        case "pgterms:file":
                            if (hasFormatStart) {
                                QName qName = new QName("http://www.w3.org/1999/02/22-rdf-syntax-ns#","about", "rdf");
                                Attribute attribute = event.asStartElement().getAttributeByName(qName);
                                String plainTextURL = attribute.getValue();
                                if (StringUtils.endsWithIgnoreCase(plainTextURL, "utf-8")) {
                                    document.setFulltextUrl(plainTextURL);
                                    hasFormatStart = false;
                                }
                                else if (StringUtils.endsWithIgnoreCase(plainTextURL, ".txt")){
                                    fileURL = plainTextURL;
                                }
                                else {
                                    hasFormatStart = false;
                                }
                            }
                            break;
                    }
                } else if (event.isEndElement()) {
                    QName name = event.asEndElement().getName();
                    String localPart = name.getLocalPart();
                    String prefix = name.getPrefix();
                    String tagName = prefix + ":" + localPart;
                    if (authorStart && "dcterms:creator".equals(tagName)) {
                        authorStart = false;
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.err.println("Fehler bei der Verarbeitung von Datei " + file.getName());
            throw new RuntimeException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("Fehler beim Schließen des InputStream");
                }
                if (eventReader != null) {
                    try {
                        eventReader.close();
                    } catch (XMLStreamException e) {
                        System.err.println("Fehler beim Schließen des XMLEventReader");
                    }
                }
            }
        }
        return document;
    }

    private String getCharacterData(XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        XMLEvent event = eventReader.nextEvent();
        while (event instanceof Characters) {
            result += event.asCharacters().getData().trim();
            result += " ";
            event = eventReader.nextEvent();
        }
        result = StringUtils.remove(result.trim(), "\r");
        return StringUtils.replace(result, "\n", " ");
    }

}
