package xyz.prinkov.selex;

import java.util.HashMap;

public class Param {
    String query;
    HashMap<String, String> view;
    String name;

    public Param(String query, HashMap<String, String> view, String name) {
        this.query = query;
        this.view = view;
        this.name = name;
    }

}
