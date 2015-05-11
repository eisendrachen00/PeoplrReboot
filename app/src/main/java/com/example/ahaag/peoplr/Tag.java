package com.example.ahaag.peoplr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shannoncox on 5/10/15.
 */
public class Tag {

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public final Integer getId() {
        return Integer.parseInt(this.id);
    }

    public final String getName() {
        return this.name;
    }
}
