package com.aqulasoft.disyam.models;

import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Getter
public class DownloadInfo {

    private String host;
    private String path;
    private String ts;
    private String region;
    private String s;

    private DownloadInfo(NodeList data) {
        for (int i = 0; i < data.getLength(); i++) {
            Node item = data.item(i);
            System.out.println(item.getTextContent());
            switch (item.getNodeName()) {
                case "host":
                    this.host = item.getTextContent();
                    break;
                case "path":
                    this.path = item.getTextContent();
                    break;
                case "ts":
                    this.ts = item.getTextContent();
                    break;
                case "region":
                    this.region = item.getTextContent();
                    break;
                case "s":
                    this.s = item.getTextContent();
                    break;
            }
        }

    }

    public static DownloadInfo create(byte[] xml) {
        NodeList data = parseFromXml(xml);
        if (data != null) return new DownloadInfo(data);
        return null;
    }


    private static NodeList parseFromXml(byte[] xml) {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xml);
            Document doc = builder.parse(input);
            return doc.getDocumentElement().getChildNodes();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}
