package uk.ac.imperial.epi_collect2.media.camera;

import uk.ac.imperial.epi_collect2.R;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


public class ImageViewer extends Activity {

	     private ImageView imageView; 
	     private String imagedir;
	     private ImageButton endButton; // playButton, 
	     
	     /** Called when the activity is first created. */ 
	     @Override 
	     public void onCreate(Bundle savedInstanceState) { 
	          super.onCreate(savedInstanceState); 
	          requestWindowFeature(Window.FEATURE_NO_TITLE); 
	          //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
	          setContentView(uk.ac.imperial.epi_collect2.R.layout.image_view); 
	          	         		         
	          imageView = (ImageView)findViewById(uk.ac.imperial.epi_collect2.R.id.imageView); 
	         
	          endButton = (ImageButton) findViewById(uk.ac.imperial.epi_collect2.R.id.endbut);
	          
	         DBAccess dbAccess = new DBAccess(this);
	  	     dbAccess.open();
	  	     
	         imagedir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + dbAccess.getProject(); //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	          
	         Bundle extras = getIntent().getExtras();
	         if (extras != null && extras.getString("IMAGE_ID") != null){
	        	// Log.i("IMAGE FILE", imagedir+"/"+extras.getString("IMAGE_ID"));
	        	 imageView.setImageURI(Uri.parse(imagedir+"/"+extras.getString("IMAGE_ID")));
	         }
	         else{
	        	 showAlert(this.getResources().getString(R.string.image_problem)); //"Problem with image!");
	         }
	         
	         endButton.setBackgroundColor(Color.DKGRAY);
	         endButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.undo);
	                  
	         endButton.setOnClickListener(new View.OnClickListener() {

	              public void onClick(View arg0) {
	            	  //startRecording();
	            	  endImage();
	              }
	             
	          });
	         
	     } 
	     
	     private void endImage(){

       	  	Bundle extras = getIntent().getExtras();
       	  	this.getIntent().putExtras(extras);
       	  	setResult(RESULT_OK, this.getIntent());
       	  	finish();  
	     }
	     
	     public void showAlert(String result){
     		new AlertDialog.Builder(this)
     	    .setTitle(R.string.error) //"Error")
     	    .setMessage(result)
     	    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

     	         public void onClick(DialogInterface dialog, int whichButton) {
     	        	endImage();
     	         }
     	    }).show();	
     	}
	     
	     
	     
	     
}