package com.example.cristianr.tiendaapps.models;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cristianr on 14/01/2017.
 */

public class Entity {

    public final static String NAME_KEY = "label";
    public final static String ATTRIBUTES_KEY = "attributes";
    public final static String URL_KEY = "href";
    public static final String ID_KEY = "im:id";

    protected String name, id;
    protected URL url;

    public Entity(){
        url = null;
    }

    public void setUrl(String s){
        try{
            this.url = new URL(s);
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
            this.url = null;
        }
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
