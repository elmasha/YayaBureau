package com.intech.yayabureau.Models;

import java.util.Date;

public class Notification {
    private String title,description,from,to;
    private Date timestamp;

    public Notification() {
    }

    public Notification(String title, String description, String from, String to, Date timestamp) {
        this.title = title;
        this.description = description;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
