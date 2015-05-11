package com.example.ahaag.peoplr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shannoncox on 5/10/15.
 */
class User {

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("blurb")
    private String blurb;

    @SerializedName("fb_access_token")
    private String fb_access_token;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

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
    public final String getFb_access_token() {
        return this.fb_access_token;
    }
    public final double getLatitude() {
        return Double.parseDouble(this.latitude);
    }
    public final double getLongitude() {
        return Double.parseDouble(this.longitude);
    }
    public final String getPhoto_url() {
        return this.photo_url;
    }

}