package com.example.cristianr.tiendaapps.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cristianr on 13/01/2017.
 */

public class Application extends Entity {

    public static final String NAME_OBJECT_KEY = "im:name";
    public static final String IMAGE_OBJECT_KEY = "im:image";
    public static final String SUMMARY_OBJECT_KEY = "summary";
    public static final String PRICE_OBJECT_KEY = "im:price";
    public static final String RELASE_DATE_OBJECT_KEY = "im:releaseDate";
    public static final String CURRENCY_KEY = "currency";
    public static final String AMOUNT_KEY = "amount";
    public static final String LINK_OBJECT_KEY = "link";
    public static final String ID_OBJECT_KEY = "id";

    private String summary, currency;
    private double price;
    private String imageUrl;
    private Date relaseDate;
    private final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private Category category;
    private Developer developer;

    public Application(){
        super();
    }

    public static Application parseFromJSON(JSONObject json) throws JSONException {
        Application app = new Application();
        app.setName(json.getJSONObject(NAME_OBJECT_KEY).getString(NAME_KEY));
        app.setImageUrl(json.getJSONArray(IMAGE_OBJECT_KEY).getJSONObject(1).getString(NAME_KEY));
        app.setSummary(json.getJSONObject(SUMMARY_OBJECT_KEY).getString(NAME_KEY));
        JSONObject jsonPrice= json.getJSONObject(PRICE_OBJECT_KEY).getJSONObject(ATTRIBUTES_KEY);
        app.setCurrency(jsonPrice.getString(CURRENCY_KEY));
        app.setPrice(jsonPrice.getDouble(AMOUNT_KEY));
        app.setRelaseDate(json.getJSONObject(RELASE_DATE_OBJECT_KEY).getString(NAME_KEY));
        app.setUrl(json.getJSONObject(LINK_OBJECT_KEY).getJSONObject(ATTRIBUTES_KEY).getString(URL_KEY));
        app.setId(json.getJSONObject(ID_OBJECT_KEY).getJSONObject(ATTRIBUTES_KEY).getString(ID_KEY));
        return app;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setRelaseDate(Date relaseDate) {
        this.relaseDate = relaseDate;
    }

    public void setRelaseDate(String s){
        try{
            this.relaseDate = dateFormater.parse(s);
        }
        catch (java.text.ParseException ex){
            ex.printStackTrace();
            this.relaseDate = null;
        }
    }

    public Date getRelaseDate(){
        return relaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
