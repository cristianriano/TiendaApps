package Models;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cristianr on 13/01/2017.
 */

public class Application {
    private int id;
    private String name, urlImage, summary, currency;
    private float price;
    private Date relaseDate;
    private final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private URL url;

    public Application(){
    }

    public void setRelaseDate(Date relaseDate) {
        this.relaseDate = relaseDate;
    }

    public void setUrl(URL url) {
        this.url = url;
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

    public URL getUrl(){
        return url;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
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

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
