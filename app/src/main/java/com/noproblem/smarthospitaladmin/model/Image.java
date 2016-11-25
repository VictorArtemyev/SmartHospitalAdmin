package com.noproblem.smarthospitaladmin.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Victor Artemyev on 24/11/2016.
 */

@IgnoreExtraProperties
public class Image {

    public String id;
    public String url;

    public Image() {
    }

    public Image(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
