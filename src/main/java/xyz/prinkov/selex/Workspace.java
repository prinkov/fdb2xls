package xyz.prinkov.selex;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class Workspace {
    public static ArrayList<Param> params = new ArrayList<Param>();
    public static String username = "SYSDBA";
    public static String password = "masterkey";
    public static String host = "localhost";
    public static String paramFile = ClassLoader.getSystemResource("input.xml").toString();
//    .getClass().getResource("input.xml").toString();


    public static void init() {
        params = new ArrayList<Param>();
        try {
//            Parameters parsing from xml
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(paramFile);
            Node root = document.getDocumentElement();
            NodeList parameters = root.getChildNodes();

            for (int i = 0; i < parameters.getLength(); i++) {
                Node param = parameters.item(i);
                if (param.getNodeType() != Node.TEXT_NODE) {
                    NodeList items = param.getChildNodes();
                    int counter = 0;
                    String name = "";
                    String query = "";
                    String[] keys = null;
                    String[] vals = null;
                    HashMap<String, String> view;
                    for(int j = 0; j < items.getLength(); j++) {
                        Node item = items.item(j);
                        if (item.getNodeType() != Node.TEXT_NODE) {
                            if(counter == 0)
                                name = item.getChildNodes().item(0).getTextContent();
                            else if(counter == 1) {
                                vals = (item.getChildNodes().item(0).getTextContent()).split(",");
                            } else if(counter == 2) {
                                keys = (item.getChildNodes().item(0).getTextContent()).split(",");
                            } else
                                query = item.getChildNodes().item(0).getTextContent();
                            counter++;
                        }
                    }
                    view = new LinkedHashMap<String, String>();
                    int unique;

//                  pre-compile escaping
//                  todo удалить пробелы из OldColNames
                    String withAlias = query.substring(query.indexOf("SELECT") + 6, query.indexOf("FROM"));String withoutAlias = withAlias.substring(0, withAlias.length());

                    for (int p = 0; p < vals.length; p++) {
                        if(vals[p].indexOf("{{") != -1) {
                            unique = Math.abs(new Random().nextInt());
                            vals[p] = "escaping"+unique;
                            withoutAlias = withoutAlias.replaceFirst("\\{\\{","");
                            withoutAlias = withoutAlias.replaceFirst("\\}\\}", " as escaping"+unique);
                        }
                    }
                    for(int j = 0; j < keys.length; j++) {
                        if (vals[j].indexOf("escaping") == -1) {
                            withoutAlias = withoutAlias.replace(vals[j], vals[j] + " as " + vals[j].replaceAll("\\.", "unique"));

                            vals[j] = vals[j].replaceAll("\\.", "unique");
                            view.put(keys[j].replaceAll(" ", ""), vals[j].replaceAll(" ", ""));
                        } else {
                            view.put(keys[j].replaceAll(" ", ""), vals[j].replaceAll(" ", ""));
                        }
                    }
                    query = query.replace(withAlias, withoutAlias);
                    params.add(new Param(query, view, name));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,  "Файл запросов неверного формата", "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

    }

    public static void reset() {
        username = "SYSDBA";
        password = "masterkey";
        host = "localhost";
        paramFile = ClassLoader.getSystemResource("input.xml").toString();
    }
}
