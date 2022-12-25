package net.etaservice.crawnew.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news")
@Getter
@Setter
@ToString
public class New {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  length = 255)
    private String title ;

    private String source;

    @Column(name = "url_full")
    private String urlFull;

    @Column(name = "url_thumb_image")
    private String urlThumbImage;

    private Date created;

    private String description;

    private int type;

    public New(String title, String source, String urlFull, String urlThumbImage, Date created, String description, int type) {
        this.title = title;
        this.source = source;
        this.urlFull = urlFull;
        this.urlThumbImage = urlThumbImage;
        this.created = created;
        this.description = description;
        this.type = type;
    }

    public New() {
    }

    public boolean isEmpty(){
        if (this.getTitle() == null || this.getTitle().trim().equals("") || this.getUrlFull() == null || this.getUrlFull().trim().equals("") || this.getUrlThumbImage() == null || this.getUrlThumbImage().trim().equals("")) {
            return false;
        }
        return  true;
    }


}
