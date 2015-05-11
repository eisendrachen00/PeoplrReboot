package com.example.ahaag.peoplr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shannoncox on 5/10/15.
 */
public class UserMin {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("blurb")
    private String blurb;

    @SerializedName("photo_url")
    private String photo_url;

    public final Integer getId() {
        return Integer.parseInt(this.id);
    }
    public final String getName() {
        return this.name;
    }
    public final String getBlurb() {
        return this.blurb;
    }
    public final String getPhoto_url() {
        return this.photo_url;
    }
}