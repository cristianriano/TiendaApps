package com.example.cristianr.tiendaapps.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cristianr on 14/01/2017.
 */

public class Developer extends Entity{

    public Developer(){
        super();
    }

    public static Developer parseFromJSON(JSONObject json) throws JSONException{
        Developer developer = new Developer();
        developer.setName(json.getString(NAME_KEY));
        JSONObject attributes = json.getJSONObject(ATTRIBUTES_KEY);
        developer.setUrl(attributes.getString(URL_KEY));
        return developer;
    }

}
