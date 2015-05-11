package com.example.ahaag.peoplr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class Matches extends Activity implements AdapterView.OnItemClickListener {
    final String drawerTitle = "Navigation";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    String[] fragmentNames;
    ListView drawerList;
    ListView listview;
    ArrayList<NameValuePair> params;
    ArrayList<String> list;
    Matches activity;
    List<Matchee> matchees;


    int id;
    int[] u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        listview = (ListView) findViewById(R.id.fragmentContainer);
        activity = this;
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("user_id", Integer.toString(startUp.getUserId())));
        new MatchesDownloadTask(3, activity, params).execute();


        //list=new ArrayList<>();



//        id=s.getUserId();
//        //GET MATCHeS FOR ID
//        String st="[9,10,11,12,13,14,15,16]";
//
//        Gson gson = new Gson();
//        u = gson.fromJson(st, int[].class);
//        for (int i=0;i<u.length;i++){
//            //get user with id u[i]
//            String st2="{\"id\":10,\"name\":\"Dipper Pines\",\"blurb\":null,\"fb_access_token\":\"222\",\"created_at\":\"2015-05-04T19:14:06.421Z\",\"updated_at\":\"2015-05-05T21:59:45.375Z\",\"latitude\":40.0,\"longitude\":30.1,\"photo_url\":\"http://vignette2.wikia.nocookie.net/gravityfalls/images/c/cb/S1e16_dipper_will_take_room.png/revision/latest/scale-to-width/250?cb=20130406215813\"}";
//            Gson gson2 = new Gson();
//            u2 = gson2.fromJson(st2, user.class);
//            list.add(u2.name);
//        }
//
//        listview = (ListView) findViewById(R.id.fragmentContainer);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
//        listview.setAdapter(adapter);
////
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
////
//                int userID=u[position];
//                Intent nextScreen = new Intent(getApplicationContext(), MatchesProfile.class);
//                nextScreen.putExtra("user", userID);
//
//                startActivity(nextScreen);
//           }
//       });


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
            startActivity(nextScreen);
        }
        if (position == 1) {
            Intent nextScreen = new Intent(getApplicationContext(), MyProfile.class);
            startActivity(nextScreen);
        }
        if (position == 2) {
            Intent nextScreen = new Intent(getApplicationContext(), Matches.class);
            startActivity(nextScreen);
        }
        if (position == 3) {
            Intent nextScreen = new Intent(getApplicationContext(), fblogin.class);
            startActivity(nextScreen);
        }
    }

    public class Matchee {

        @SerializedName("updated_at")
        private String updated_at;

        @SerializedName("created_at")
        private String created_at;


        @SerializedName("matched_user_id")
        private String matched_user_id;

        @SerializedName("matched_user_name")
        private String matched_user_name;

        @SerializedName("tag_name")
        private String tag_name;

        @SerializedName("matched_user_blurb")
        private String matched_user_blurb;

        @SerializedName("matched_user_photo_url")
        private String matched_user_profile_url;

        public final String getname() {
            return matched_user_name;
        }
        public final String gettag(){
            return tag_name;
        }
        public final String getBlurb(){
            return matched_user_blurb;
        }
        public final String getPhoto(){
            return matched_user_profile_url;
        }


    }

    protected void onMatchResponse(String response) {

        Gson gson = new Gson();

        String jsonOutput = response.trim();
        Type listType = new TypeToken<List<Matchee>>() {
        }.getType();
        matchees = (List<Matchee>) gson.fromJson(jsonOutput, listType);

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < matchees.size(); i++) {
            list.add(matchees.get(i).getname()+" ("+matchees.get(i).gettag()+" )");

        }
        //new UserDownloadTask(3, activity).execute();


        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
//
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Matchee m=matchees.get(position);

                Intent nextScreen = new Intent(getApplicationContext(), MatchesProfile.class);
                nextScreen.putExtra("name", m.getname());
                nextScreen.putExtra("blurb", m.getBlurb());
                nextScreen.putExtra("photo_url", m.getPhoto());
                startActivity(nextScreen);
            }
        });
        //return list;
    }

    class MatchesDownloadTask extends AsyncTask<Void, Void, String> {

        int type;
        List<NameValuePair> params2;

        ProgressDialog dialog;
        Context context;
        Matches activity;
        int streamLength = 0;

        // http://stackoverflow.com/questions/23267345/how-to-use-spinning-or-wait-icon-when-asynctask-is-being-performed-in-android
        // http://stackoverflow.com/questions/1270760/passing-a-string-by-reference-in-java?rq=1

        public MatchesDownloadTask(int type, Matches activity, List<NameValuePair> params) {

            this.type = type;
            this.params2 = params;
            this.activity = activity;
            this.context = activity;
            dialog = new ProgressDialog(context);
            dialog.setTitle("Loading matches");
            dialog.setMessage("One moment please");

        }

        protected void onPreExecute() {
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Void... args) {
            try {
                return loadFromNetwork("https://peoplr-eisendrachen00-4.c9.io/get_matches", false, params2);
            } catch (IOException e) {
                return ("Connection error!");
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(activity.getApplicationContext(), (String) result, Toast.LENGTH_LONG).show();
            onMatchResponse(result);
            dialog.dismiss();
        }

        /**
         * Initiates the fetch operation.
         */
        private String loadFromNetwork(String url, Boolean isPOST, List<NameValuePair> params2) throws IOException {
            InputStream stream = null;
            String str = "";
            try {
                stream = getRequest(url, params2);
                str = readIt(stream, streamLength); //TODO ENSURE THAT THIS WORKS FOR ALL LENGTHS YA DUMB
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return str;
        }

        // ADD NEW GET AND POST STUFF  ---------------------------------------------------------------->

        private InputStream getRequest(String urlString, List<NameValuePair> params2) throws IOException {
            // BEGIN_INCLUDE(get_inputstream)
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(50000 /* milliseconds */);
            conn.setConnectTimeout(50000 /* milliseconds */);
            conn.setRequestMethod("POST");///I CHANGEd from GET
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params2));
            writer.flush();
            writer.close();
            // Start the query
            //getQuery(params);
            conn.connect();
            InputStream stream = conn.getInputStream();
            streamLength = conn.getContentLength();
            return stream;
            // END_INCLUDE(get_inputstream)
        }

        private String getQuery(List<NameValuePair> params2) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params2) {
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

        /**
         * Reads an InputStream and converts it to a String.
         *
         * @param stream InputStream containing HTML from targeted site.
         * @param len    Length of string that this method returns.
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

//        class UserDownloadTask extends AsyncTask<Void, Void, String> {
//
//            int type;
//            List<NameValuePair> params;
//
//            ProgressDialog dialog;
//            Context context;
//            MainActivity activity;

        // http://stackoverflow.com/questions/23267345/how-to-use-spinning-or-wait-icon-when-asynctask-is-being-performed-in-android
        // http://stackoverflow.com/questions/1270760/passing-a-string-by-reference-in-java?rq=1

//            public UserDownloadTask(int type, MainActivity activity) {
//
//                this.type = type;
//
//                this.activity = activity;
//                this.context = activity;
//                dialog = new ProgressDialog(context);
//                dialog.setTitle("title");
//                dialog.setMessage("message");
//            }
//
//            protected void onPreExecute() {
//                this.dialog.show();
//            }
//
//            @Override
//            protected String doInBackground(Void... args) {
//                try {
//                    return loadFromNetwork("https://peoplr-eisendrachen00-4.c9.io/get_profile", false, params);
//                } catch (IOException e) {
//                    return ("Connection error!");
//                }
//            }

        //       @Override
//        protected void onPostExecute(String result) {
//
//            Toast.makeText(activity.getApplicationContext(), (String) result, Toast.LENGTH_LONG).show();
//            onTagResponse(result);
//            dialog.dismiss();
//        }

        /** Initiates the fetch operation. */


//        }
    }
}