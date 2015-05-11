package com.example.ahaag.peoplr;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;

/**
 * Created by Josephine on 4/21/2015.
 */
public class UserProfile implements Parcelable {
    private String name;
    private String contactInfo;
    private String description;
    //private location??
    ArrayList <UserProfile> matches;
    private Drawable image;



    public UserProfile(String n, String c, String d, ArrayList m){//Need to add image
         name=n;
         contactInfo=c;
         description=d;
          matches=m;
       // image=i;
    }
    String getName (){
        return name;
    }
    String getContactInfo() {
        return contactInfo;
    }
    String getDescription(){
       return description;
    }
   ArrayList getMatches() {
        return matches;
    }
    //Drawable getImage(){return image;}
    void setMatches(ArrayList m){
        matches=m;
    }
    void setDescription(String d){description=d;}
    void setContactInfo(String c){contactInfo=c;}



    public int describeContents() {

        return 0;

    }

    public void writeToParcel(Parcel dest, int flag) {
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString("name", name);
        bundle.putString("contactInfo", contactInfo);
        bundle.putString("description", description);
        bundle.putParcelableArrayList("matches", matches);


        //bundle.setClassLoader(LocationType.class.getClassLoader());
        //NEED TO ADD IMage
        //dest.writeString(name);
       // dest.writeString(contactInfo);
       // dest.writeString(description);
       dest.writeBundle(bundle);

    }
    public static final Parcelable.Creator<UserProfile> CREATOR = new Creator<UserProfile>() {

        @Override
        public UserProfile createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new UserProfile(bundle.getString("name"),
                    bundle.getString("contactInfo"),bundle.getString("description"),
                    bundle.getParcelableArrayList("matches"));
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }

    };

}
