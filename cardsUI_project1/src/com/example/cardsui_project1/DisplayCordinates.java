package com.example.cardsui_project1;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class DisplayCordinates extends Activity {
	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_form);
 
        Bundle b = getIntent().getExtras();
        TextView cordinates = (TextView) findViewById(R.id.cordinates);
		double lat = b.getDouble("lat");
		double lng = b.getDouble("lng");
	
		cordinates.setText("Latitude:"+Double.toString(lat) + "\nLongitude:"+Double.toString(lng));
        
       // name.setText(b.getCharSequence("name"));
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
            	startActivityForResult(myIntent, 0);
                
            }
        });
        
    Button button1 = (Button)findViewById(R.id.button1);

    button1.setOnClickListener(new OnClickListener() {

        @Override
        public void onClick(View v) {
        	finish();
        	Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);

        }
    });
        
    }
}