package uk.ac.imperial.epi_collect2;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Help extends Activity {

	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        super.setTitle("EpiCollect+ "+this.getResources().getString(R.string.about)); //About"); 
	
        setContentView(R.layout.help);
        
        TextView descview = (TextView) findViewById(R.id.desctext);
        Button backbutton = (Button) findViewById(R.id.backbut);
        TextView aboutview = (TextView) findViewById(R.id.abouttext);
        backbutton.setText(R.string.back);
        
        DBAccess dbAccess = new DBAccess(this);
	    dbAccess.open();
	    
        String description = "";
        
        if(getIntent().getExtras().getString("PROJECT").length() > 0)
        	description = dbAccess.getValue("project_description");
        
        if(description.length() == 0){
        	descview.setText(this.getResources().getString(R.string.this_version)+" "+getIntent().getExtras().getString("VERSION")+" of EpiCollect+");
        	aboutview.setText(Html.fromHtml(this.getResources().getString(R.string.comments)+":<br><br>http://www.epicollect.net"));
        }
        else{
        	descview.setText(description);
        	aboutview.setText(Html.fromHtml(this.getResources().getString(R.string.this_version)+" "+getIntent().getExtras().getString("VERSION")+" of EpiCollect+.<br><br>"+this.getResources().getString(R.string.comments)+":<br><br>http://www.epicollect.net"));
        }
           
        backbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	finish();
            }
           
        });
        
        
	}
}
