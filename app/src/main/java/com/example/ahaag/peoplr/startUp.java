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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


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
        new UserSetTask(context).execute();
        new ProfilePhotoDownloadTask().execute();
    }

    public static void updateUser(Context context){
        if(blurbDirtyBit || latitudeDirtyBit || longitudeDirtyBit || urlDirtyBit || contactInfoDirtyBit){
            // call the thiiiing
            new UserSetTask(context).execute();
             if(urlDirtyBit){
                 new ProfilePhotoDownloadTask().execute(); // todo um
             }
        }
    }

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

    static class UserSetTask extends AsyncTask<Void, Void, String> {

        Context context = null;
        List<NameValuePair> params;
        String destUrl = "";

        public UserSetTask(Context context){
            this.context = context;
            Log.w("UserCreateTask", "In Constructor");
        }

        @Override
        protected void onPreExecute() {
            //todo something?
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                params = new ArrayList<NameValuePair>();

                if(userCreation) {
                    params.add(new BasicNameValuePair("fb_access_token", fb_access_token));
                    params.add(new BasicNameValuePair("name", name)); //TODO MAKE THIS THE REAL USER
                    params.add(new BasicNameValuePair("photo_url", url));
                    params.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
                    params.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
                    destUrl = "http://peoplr-eisendrachen00-4.c9.io/create_user";

                    //todo start new img load task for profile?
                }
                else{
                    if(blurbDirtyBit){
                        params.add(new BasicNameValuePair("blurb", blurb));
                        blurbDirtyBit = false;
                    }
                    if(latitudeDirtyBit){
                        params.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
                        latitudeDirtyBit = false;
                    }
                    if(longitudeDirtyBit){
                        params.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
                        longitudeDirtyBit = false;
                    }
                    if(urlDirtyBit){
                        params.add(new BasicNameValuePair("photo_url", url));
                        urlDirtyBit = false;
                    }
//      TODO          if(contactInfoDirtyBit){
//                        params.add(new BasicNameValuePair("contact_info", contactInfo));
//                        contactInfoDirtyBit = false;
//                    }
                    if(params.size() > 0){
                        params.add(new BasicNameValuePair("user_id", Integer.toString(id)));
                        destUrl = "http://peoplr-eisendrachen00-4.c9.io/update_user";
                    }
                }

                return loadFromNetwork(destUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if(userCreation) { // MUST SAVE ID, ADVANCE INTENT!
                onUserCreate(result);
            } else { // DON'T REALLY NEED TO DO ANYTHING TBH
                onUserUpdate(result);
            }
        }

        /** Initiates the fetch operation. */
        private String loadFromNetwork(String url) throws IOException {
            InputStream stream = null;
            String str ="";
            try{
                stream = postRequest(url);
                str = readIt(stream, 3000); //TODO ENSURE THAT THIS WORKS FOR ALL LENGTHS YA DUMB
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return str;
        }

        private InputStream postRequest(String urlString) throws IOException {
            // BEGIN_INCLUDE(get_inputstream)
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(50000 /* milliseconds */);
            conn.setConnectTimeout(50000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();

            // Start the query
            conn.connect();
            InputStream stream = conn.getInputStream();

            return stream;

            // END_INCLUDE(get_inputstream)
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        // END NEW GET AND POST STUFF  ---------------------------------------------------------------->

        /** Reads an InputStream and converts it to a String.
         * @param stream InputStream containing HTML from targeted site.
         * @param len Length of string that this method returns.
         * @return String concatenated according to len parameter.
         * @throws java.io.IOException
         * @throws java.io.UnsupportedEncodingException
         */
        private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

}
