package uk.ac.imperial.epi_collect2.util;

import uk.ac.imperial.epi_collect2.R;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ECSettings extends Activity {

	private EditText urltext;
	private DBAccess dbAccess;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        super.setTitle("EpiCollect+ "+this.getResources().getString(R.string.settings)); //"+this.getResources().getString(R.string.about)); 
	
        setContentView(R.layout.ecsettings);
        
        TextView urllabel = (TextView) findViewById(R.id.urllabel);
        urllabel.setText(this.getResources().getString(R.string.project)+"  URL");
        urltext = (EditText) findViewById(R.id.urltext);
                
        Button storebutton = (Button) findViewById(R.id.storebut);
        storebutton.setText(R.string.store);
        Button cancelbutton = (Button) findViewById(R.id.cancelbut);
        cancelbutton.setText(R.string.cancel);
        
        dbAccess = new DBAccess(this);
	    dbAccess.open();
	    
	    urltext.setText(dbAccess.getSettings("url"));
	               
        cancelbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	finish();
            }
           
        });
        
        storebutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	storeData();
            }
           
        });
        
        
	}
	
	private void storeData(){
		
		//HashMap<String, String> rowhash = new HashMap<String, String>();
		
		//rowhash.put("url", urltext.getText().toString());
		
		dbAccess.updateSettingsRow("url", urltext.getText().toString()); //rowhash);
		
		finish();
		
	}
}
