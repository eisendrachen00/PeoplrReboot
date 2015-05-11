package com.example.ahaag.peoplr;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditInfo extends Activity {
    EditText de;
    EditText co;
    UserProfile cr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        startUp s=((startUp)getApplicationContext());
        //cr=s.getCurrUser();

         de=(EditText) findViewById(R.id.dEdit);
         co=(EditText) findViewById(R.id.cEdit);
      Button  b = (Button) findViewById(R.id.Button);

        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String des=de.getText().toString();
                String con=co.getText().toString();
                if  (!des.equals("")) {
                    //cr.setDescription(des);
                }
                if (!con.equals("")) {
                    //cr.setContactInfo(con);
                }
                Intent nextScreen = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(nextScreen);

            }
        });
    }



}
