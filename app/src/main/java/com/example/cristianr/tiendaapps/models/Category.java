package com.example.cristianr.tiendaapps.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cristianr on 13/01/2017.
 */

public class Category extends Entity implements Serializable{

    private String name, id;
    private URL url;

    public static final String URL_KEY = "scheme";

    public Category(){
        super();
    }

    public static Category parseFromJSON(JSONObject json) throws JSONException {
        Category category = new Category();
        JSONObject attributes = json.getJSONObject(ATTRIBUTES_KEY);
        category.setId(attributes.getString(ID_KEY));
        category.setName(attributes.getString(NAME_KEY));
        category.setUrl(attributes.getString(URL_KEY));
        return category;
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
