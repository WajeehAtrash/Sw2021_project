package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
@Entity
@Table(name="movie")
public class HomeMovie extends Movie implements Serializable {
    private String Link;
    int price;
    HomeMovie(){}
    public HomeMovie(String engName, String hebName, String actors, String genere, String description, String producer, String imgURL, String Link, int price){
        super(engName,hebName,actors,genere,description,producer,imgURL);
        this.Link=Link;
        this.price=price;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
