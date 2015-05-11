package com.example.ahaag.peoplr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.MalformedJsonException;

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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TinderProfile extends Activity implements AdapterView.OnItemClickListener {
    final String drawerTitle = "Navigation";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    String[] fragmentNames;
    ListView drawerList;
    CardContainer mCardContainer;
    int tagID;
    int[] u;
    //user u2;

    public static List<UserMin> users;

    List<NameValuePair> tagUpdate;
    List<NameValuePair> params;
    List<List<NameValuePair>> swipes; //TODO ENSURE THAT SWIPES DO NOT OVERWRITE EACH OTHER
    int latestSwipe = 0;
    int lastSent = 0;

    startUp s;

    TinderProfile activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_tinder_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_tinder_profile);

        Toast.makeText(getApplicationContext(),
                "Swipe right to like and left to dislike", Toast.LENGTH_LONG)
                .show();

        TextView tagtext = (TextView) findViewById(R.id.tagtext);
        Intent i = getIntent();
        String tag = i.getStringExtra("tag");
        tagtext.setText(tag);
        s = ((startUp) getApplicationContext());
        //THis is the tag id!!1
        tagID = i.getIntExtra("id", 0);
        activity = this;

        // TODO GET USERS FOR SWIPING REQUEST - RETURN ARRAY OF IDS? WHY THO

        tagUpdate = new ArrayList<NameValuePair>();
        tagUpdate.add(new BasicNameValuePair("tag_id", Integer.toString(tagID)));
        tagUpdate.add(new BasicNameValuePair("user_id", Integer.toString(s.getUserId()))); //TODO MAKE THIS THE REAL USER

        new TagUpdateTask(activity).execute();

        swipes = new ArrayList<List<NameValuePair>>();
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag_id", Integer.toString(tagID)));
        params.add(new BasicNameValuePair("user_id", Integer.toString(s.getUserId()))); //TODO MAKE THIS THE REAL USER

        new UserListDownloadTask(this).execute();

        // Set the drawer toggle as the DrawerListener
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        //Setting up the values of the Sidebar menu (Home, My Profile, Matches, Settings)
        fragmentNames = getResources().getStringArray(R.array.fragment_names);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        //Sets the adapter of the list view for the side Drawer
        drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, fragmentNames));

        //Listener for the drawer objects
        drawerList.setOnItemClickListener(this);


    }

    protected void onUserListResponse(String response) throws MalformedJsonException, JsonSyntaxException {

        Gson gson = new Gson();

        String jsonOutput = response.trim();
        Type listType = new TypeToken<List<UserMin>>(){}.getType();
        users = (List<UserMin>) gson.fromJson(jsonOutput, listType);

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> imageUrls = new ArrayList<String>();
        for (UserMin u : users) {
            list.add(u.getName());
            imageUrls.add(u.getPhoto_url());
        }

        try {
            new ImageDownloadTask(imageUrls).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), (String) list.toString(), Toast.LENGTH_LONG).show();
        //TODO
    }

    protected void onLoadImages(List<Bitmap> images){
        //TODO STUFF

        //delete when image url works
        Resources r=getResources();

        mCardContainer = (CardContainer) findViewById(R.id.layoutview);
        mCardContainer.setOrientation(Orientations.Orientation.Disordered);
        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

        for (int i = 0; i < users.size(); i++) {

            final CardModel card = new CardModel(users.get(i).getName(), users.get(i).getBlurb(), images.get(i));//Must add actual picture
            card.setId(users.get(i).getId());

            card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
                @Override
                public void onLike() {
                    Toast.makeText(getApplicationContext(),
                            "Liked", Toast.LENGTH_LONG)
                            .show();

                    //TODO SEND SWIPE RESULT = ACCEPTED

                    List<NameValuePair> swipe = new ArrayList<NameValuePair>();
                    swipe.add(new BasicNameValuePair("user_id", Integer.toString(s.getUserId())));
                    swipe.add(new BasicNameValuePair("tag_id", Integer.toString(tagID)));
                    swipe.add(new BasicNameValuePair("matcher_id", Integer.toString(s.getUserId())));
                    swipe.add(new BasicNameValuePair("matchee_id", Integer.toString(card.getId())));
                    swipe.add(new BasicNameValuePair("accepted", "true"));

                    swipes.add(swipe);
                    latestSwipe++;

                    new SwipeUpdateTask(activity).execute();
                }

                @Override
                public void onDislike() {
                    // Log.i("Swipeable Cards","I dislike the card");
                    Toast.makeText(getApplicationContext(),
                            "Disliked", Toast.LENGTH_LONG)
                            .show();

                    //TODO SEND SWIPE RESULT = REJECTED

                    List<NameValuePair> swipe = new ArrayList<NameValuePair>();
                    swipe.add(new BasicNameValuePair("user_id", Integer.toString(s.getUserId())));
                    swipe.add(new BasicNameValuePair("tag_id", Integer.toString(tagID)));
                    swipe.add(new BasicNameValuePair("matcher_id", Integer.toString(s.getUserId())));
                    swipe.add(new BasicNameValuePair("matchee_id", Integer.toString(card.getId())));
                    swipe.add(new BasicNameValuePair("accepted", "false"));

                    swipes.add(swipe);
                    latestSwipe++;

                    new SwipeUpdateTask(activity).execute();
                }
            });
            adapter.add(card);
        }

        mCardContainer.setAdapter(adapter);

        //TODO ADD SUPPORT TO END OF LIST....
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    //onItemClick to handle placement of title on drawer
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        getActionBar().setTitle(fragmentNames[position]);
        drawerLayout.closeDrawer(drawerList);
        if (position == 0) {
            Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
//            s.setCurrUser(cr);
//            s.setTags(tagID,tags);//changed from pos
            startActivity(nextScreen);
        }
        if (position == 1) {
            Intent nextScreen = new Intent(getApplicationContext(), MyProfile.class);
//            s.setCurrUser(cr);
//            s.setTags(tagID,tags);
            startActivity(nextScreen);
        }
        if (position == 2) {
            Intent nextScreen = new Intent(getApplicationContext(), Matches.class);
//            s.setCurrUser(cr);
//            s.setTags(tagID,tags);
            // Bundle b = new Bundle();
//            b.putParcelable("currUser", cr);
//            b.putParcelableArrayList("tag1", tag1);
//            b.putParcelableArrayList("tag2", tag2);
//            b.putParcelableArrayList("tag3", tag3);
//            nextScreen.putExtras(b);
            startActivity(nextScreen);
        }
        if (position == 3) {
            Intent nextScreen = new Intent(getApplicationContext(), fblogin.class);
//            s.setCurrUser(cr);
//            s.setTags(tagID,tags);
            startActivity(nextScreen);
        }
    }

    class ImageDownloadTask extends AsyncTask<Void, Void, Void> {

        List<Bitmap> images;
        ArrayList<String> imageUrls;

        public ImageDownloadTask(ArrayList<String> imageUrls) {
            this.imageUrls = imageUrls;
            images = new ArrayList<Bitmap>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (String url : imageUrls){
                    URL urlConnection = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    images.add(myBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            onLoadImages(images);
        }
    }

    //TODO GET SOME USERRRRRS

    class UserListDownloadTask extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;
        Context context;
        TinderProfile activity;

        int streamLength = 0;

        // http://stackoverflow.com/questions/23267345/how-to-use-spinning-or-wait-icon-when-asynctask-is-being-performed-in-android
        // http://stackoverflow.com/questions/1270760/passing-a-string-by-reference-in-java?rq=1

        public UserListDownloadTask(TinderProfile activity){

            this.activity = activity;
            this.context = activity;
            dialog = new ProgressDialog(context);
            //dialog.setTitle("Loading");
            //dialog.setMessage("message");
        }

        protected void onPreExecute() {
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                return loadFromNetwork("http://peoplr-eisendrachen00-4.c9.io/get_users_for_swiping_min");
            } catch (IOException e) {
                return ("Connection error!");
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(activity.getApplicationContext(), (String) result, Toast.LENGTH_LONG).show();

            try {
                onUserListResponse(result);
            } catch (MalformedJsonException e) {
                Log.w("JSON - Malformed Err:  ", result);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException f) {
                    f.printStackTrace();
                }

                new UserListDownloadTask(activity).execute();

            } catch (JsonSyntaxException e) {
                Log.w("JSON - Syntax Err:  ", result);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException f) {
                    f.printStackTrace();
                }

                new UserListDownloadTask(activity).execute();
            }


            dialog.dismiss();
        }

        /** Initiates the fetch operation. */
        private String loadFromNetwork(String url) throws IOException {
            InputStream stream = null;
            String str ="";
            try{
                stream = postRequest(url, params);
                str = readIt(stream, 2 * streamLength); //TODO ENSURE THAT THIS WORKS FOR ALL LENGTHS YA DUMB
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return str;
        }

        // ADD NEW GET AND POST STUFF  ---------------------------------------------------------------->

        private InputStream postRequest(String urlString, List<NameValuePair> params) throws IOException {
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
            streamLength = conn.getContentLength();

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

    //  int latestUserSwipe = 0;
    //  int lastSentSwipe = 0;

    class SwipeUpdateTask extends AsyncTask<Void, Void, String>{

        Context context;
        TinderProfile activity;
        int swipeNum;

        int streamLength = 0;

        public SwipeUpdateTask(TinderProfile activity){
            this.activity = activity;
            this.context = activity;
            swipeNum = lastSent;
            lastSent++;
            if(lastSent > latestSwipe){
                //do something...
            }
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                return loadFromNetwork("http://peoplr-eisendrachen00-4.c9.io/match", swipes.get(swipeNum));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

        /** Initiates the fetch operation. */
        private String loadFromNetwork(String url, List<NameValuePair> swipe) throws IOException {
            InputStream stream = null;
            String str ="";
            try{
                stream = postRequest(url, swipe);
                str = readIt(stream, streamLength); //TODO ENSURE THAT THIS WORKS FOR ALL LENGTHS YA DUMB
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return str;
        }

        private InputStream postRequest(String urlString, List<NameValuePair> swipe) throws IOException {
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
            writer.write(getQuery(swipe));
            writer.flush();
            writer.close();

            // Start the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            streamLength = conn.getContentLength();

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

    class TagUpdateTask extends AsyncTask<Void, Void, String>{

        Context context;
        TinderProfile activity;
        int streamLength = 0;

        public TagUpdateTask(TinderProfile activity){
            this.activity = activity;
            this.context = activity;

        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                return loadFromNetwork("http://peoplr-eisendrachen00-4.c9.io/add_user_to_tag");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

        /** Initiates the fetch operation. */
        private String loadFromNetwork(String url) throws IOException {
            InputStream stream = null;
            String str ="";
            try{
                stream = postRequest(url);
                str = readIt(stream, streamLength); //TODO ENSURE THAT THIS WORKS FOR ALL LENGTHS YA DUMB
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
            writer.write(getQuery(tagUpdate));
            writer.flush();
            writer.close();

            // Start the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            streamLength = conn.getContentLength();

            return stream;

            // END_INCLUDE(get_inputstream)
        }

        private String getQuery(List<NameValuePair> tagUpdate) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : tagUpdate)
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
