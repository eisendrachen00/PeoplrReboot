package com.example.ahaag.peoplr;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class startUp extends Application {

    private static Context context = null;

    private static String fb_access_token = "";
    private static int id = -1;
    private static String name = "";
    private static String blurb = "";
    private static double latitude = 0;
    private static double longitude = 0;
    private static String url = "";
    private static String contactInfo = "";
    private static Bitmap photo = null;

    private static boolean userCreation = false;
    private static boolean blurbDirtyBit = false;
    private static boolean latitudeDirtyBit = false;
    private static boolean longitudeDirtyBit = false;
    private static boolean urlDirtyBit = false;
    private static boolean contactInfoDirtyBit = false;

    public static String getFb_access_token(){
        return fb_access_token;
    }
    public static void setFb_access_token(String Fb_access_token){
        fb_access_token = Fb_access_token;
    }

    public static int getUserId(){
        return id;
    }
    public static void setUserId(int newUserId){
        id = newUserId;
    }

    public static String getName(){
        return name;
    }
    public static void setName(String newName){
        name = newName;
    }

    public static String getBlurb(){
        return blurb;
    }
    public static void setBlurb(String newBlurb){
        blurbDirtyBit = true;
        blurb = newBlurb;
    }

    public static double getLatitude(){
        return latitude;
    }
    public static void setLatitude(double newLatitude){
        latitude = newLatitude;
    }

    public static double getLongitude(){
        return longitude;
    }
    public static void setLongitude(double newLongitude){
        longitude = newLongitude;
    }

    public static String getUrl(){
        return url;
    }
    public static void setUrl(String newUrl){
        url = newUrl;
    }

    public static String getContactInfo(){
        return contactInfo;
    }
    public static void setContactInfo(String newContactInfo){
        contactInfo = newContactInfo;
    }

    public static Bitmap getPhoto(){
        return photo;
    }
    public static void setPhoto(Bitmap newPhoto){
        photo = newPhoto;
    }

    public static Context getContext() { return context; }
    public static void setContext(Context newContext) { context = newContext; }

    public static void createUser(String new_fb_access_token, String new_name, double new_latitude,
                           double new_longitude, String new_url, Context new_context){

        // goal = update backend and set user_id
        userCreation = true;
        fb_access_token = new_fb_access_token;
        name = new_name;
        latitude = new_latitude;
        longitude = new_longitude;
        url = new_url; //new_url;
        context = new_context;
        //new UserSetTask(context).execute();
        new ProfilePhotoDownloadTask().execute();
    }

//    public static void updateUser(Context context){
//        if(blurbDirtyBit || latitudeDirtyBit || longitudeDirtyBit || urlDirtyBit || contactInfoDirtyBit){
//            // call the thiiiing
//            new UserSetTask(context).execute();
//             if(urlDirtyBit){
//                 new ProfilePhotoDownloadTask().execute(); // todo um
//             }
//        }
//    }

    public static void loadProfilePhoto(ImageView imageView){
        if(photo == null) new ProfilePhotoDownloadTask().execute();
        else {
            if (photo.getHeight() > photo.getWidth()) imageView.setImageBitmap(Bitmap.createBitmap(photo, 0, (photo.getHeight() - photo.getWidth())/2, photo.getWidth(), photo.getWidth()));
            else imageView.setImageBitmap(Bitmap.createBitmap(photo, (photo.getWidth() - photo.getHeight()) / 2, 0, photo.getHeight(), photo.getHeight()));
        }
    }

    public static void checkBlurb(String testBlurb){
        if(!blurb.equals(testBlurb)){
            blurb = testBlurb;
            blurbDirtyBit = true;
        }
    }

    public static void checkLocation(double testLatitude, double testLongitude){
        if(latitude != testLatitude){
            latitude = testLatitude;
            latitudeDirtyBit = true;
        }
        if(longitude != testLongitude){
            longitude = testLongitude;
            longitudeDirtyBit = true;
        }
    }

    public static void checkUrl(String testUrl){
        if(!url.equals(testUrl)){
            url = testUrl;
            urlDirtyBit = true;
        }
    }

    public static void checkContactInfo(String testContactInfo){
        if(!contactInfo.equals(testContactInfo)){
            contactInfo = testContactInfo;
            contactInfoDirtyBit = true;
        }
    }

    protected static void onUserCreate(String result){

//        Log.w("Result:  ", result);

        Gson gson = new Gson();
        String jsonOutput = result.trim();
        Type userType = new TypeToken<User>(){}.getType();
        User user = (User) gson.fromJson(jsonOutput, userType);
        setUserId(user.getId());

        Log.w("Confirm User ID Set", "YES! User ID = " + getUserId());

        context.startActivity(new Intent(context, MainActivity.class));
    }

    protected static void onUserUpdate(String result){
        //todo something?
    }

    static class ProfilePhotoDownloadTask extends AsyncTask<Void, Void, Void> {

        public ProfilePhotoDownloadTask() {}

        @Override
        protected Void doInBackground(Void... params) {
            URL Aurl = null;
            try {
//                URL urlConnection = new URL(url);
//                HttpURLConnection connection = (HttpURLConnection) urlConnection
//                        .openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                photo = BitmapFactory.decodeStream(input);

                Log.w("URL TEXT: ", url);
                Aurl = new URL(url);
                if(Aurl == null) Log.w("GODDAMMIT ", "IT'S FUCKING NULL");
                HttpURLConnection connection = (HttpURLConnection) Aurl.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                photo = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.w("THE URL: ", Aurl.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
