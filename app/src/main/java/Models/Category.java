package Models;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cristianr on 13/01/2017.
 */

public class Category {

    private int id;
    private String name;
    private URL url;

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
}
