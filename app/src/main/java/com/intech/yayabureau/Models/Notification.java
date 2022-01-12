package com.intech.yayabureau.Models;

import java.util.Date;

public class Notification {
    private String title,desc,from,to;
    private Date timestamp;

    public Notification() {
    //empty----
    }

    public Notification(String title, String desc, String from, String to, Date timestamp) {
        this.title = title;
        this.desc = desc;
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
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
