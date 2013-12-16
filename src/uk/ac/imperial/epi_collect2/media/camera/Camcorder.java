package uk.ac.imperial.epi_collect2.media.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import uk.ac.imperial.epi_collect2.Epi_collect;
import uk.ac.imperial.epi_collect2.R;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
//import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.graphics.Color;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class Camcorder extends Activity {

	     private uk.ac.imperial.epi_collect2.media.camera.CamcorderView camcorderView; 
	     private boolean recording = false; 
	     private static final String KEY_VIDEO = "VIDEO_ID";
	     private String videoid, existing_videoid = "-1", video_table = "", video_refid = "", videodir;
	     private ImageButton startButton; //, stopButton;
	     private TextView rectv;
	     private boolean start = false;
	     private DBAccess dbAccess;
	     
	     /** Called when the activity is first created. */ 
	     @Override 
	     public void onCreate(Bundle savedInstanceState) { 
	          super.onCreate(savedInstanceState); 
	          requestWindowFeature(Window.FEATURE_NO_TITLE); 
	          setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
	          setContentView(uk.ac.imperial.epi_collect2.R.layout.camcorder_preview); 
	          
	          Bundle extras = getIntent().getExtras();
		         if (extras != null && extras.getString("VIDEO_ID") != null){
		        	 existing_videoid = extras.getString("VIDEO_ID");
		        	 video_table = extras.getString("VIDEO_TABLE");
		        	 video_refid = extras.getString("VIDEOVIEW_ID");
		         }
		         		         
	          //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	          camcorderView = (uk.ac.imperial.epi_collect2.media.camera.CamcorderView) findViewById(uk.ac.imperial.epi_collect2.R.id.camcorder_preview); 
 	     
	         // DisplayMetrics displaymetrics = new DisplayMetrics();
	  		 // getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	  		 // int height = displaymetrics.heightPixels;
	  		 // int width = displaymetrics.widthPixels;
	  		 // camcorderView.setSize(width, height);
	          /*if(existing_videoid.equalsIgnoreCase("0")){
		    		 Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		    		   	
		    		   	String date = ""+cal.getTimeInMillis();
		    			TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		    			videoid = video_table+"_"+video_refid+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".mp4";
	          }
	          else
	        	  videoid = existing_videoid;*/
		    	     	 
	          startButton = (ImageButton) findViewById(uk.ac.imperial.epi_collect2.R.id.startbut);
	          //startButton.setText("Start");
	          startButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.record24);
	          //stopButton = (Button) findViewById(uk.ac.imperial.epi_collect2.R.id.stopbut);
	          //stopButton.setEnabled(false);
	          
	          rectv = (TextView)findViewById(uk.ac.imperial.epi_collect2.R.id.rectext);
	          
	         dbAccess = new DBAccess(this);
	  	     dbAccess.open();
	  	     
	         videodir = Epi_collect.appFiles+"/"+dbAccess.getProject()+"/videos"; //Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + dbAccess.getProject(); //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	          
	         try{
	            File f = new File(videodir);
	         	if(!f.exists()){
	         		f.mkdir();
	         	}
	         }
	         catch(Exception e){
	         	//havesdcard = false;
	         	showAlert(this.getResources().getString(R.string.video_card_error)); //"SD card not present. Required for video capture");
	         }
	         
	         
	         
	          startButton.setOnClickListener(new View.OnClickListener() {

	              public void onClick(View arg0) {
	            	  //startRecording();
	            	  setRecording();
	              }
	             
	          });
	          
	        /*  stopButton.setOnClickListener(new View.OnClickListener() {

	              public void onClick(View arg0) {
	            	  rectv.setText("");
	            	  camcorderView.stopRecording();
	            	  endRecording();
	              }
	             
	          }); */
	     } 
	     
	     public boolean onKeyDown(int keyCode, KeyEvent event)
	     {
	     	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
	     		Bundle extras = getIntent().getExtras();
           	  	extras.putString(KEY_VIDEO, videoid);
           	  	this.getIntent().putExtras(extras);
           	  	setResult(RESULT_OK, this.getIntent());
           	  	dbAccess.close();
           	  	finish(); 
	     	}
	     	if(keyCode == KeyEvent.KEYCODE_MENU)
	     		openOptionsMenu();

	         return true;
	     } 
	     
	    /* private void startRecording(){
	    		    	 
	    	 recording = true; 
             camcorderView.startRecording(); 
             startButton.setEnabled(false);
             stopButton.setEnabled(true);
             rectv.setText("RECORDING");
             rectv.setTextColor(Color.RED);
	     } */
	     
	     private void setRecording(){
	    	 
	    	 if(start == false){
	    		 recording = true; 
	    		 camcorderView.startRecording(); 
	    		 //startButton.setEnabled(false);
	    		 //stopButton.setEnabled(true);
	    		 rectv.setText(R.string.recording); //"RECORDING");
	    		 rectv.setTextColor(Color.RED);
	    		 start = true;
	    		 //startButton.setText("Stop");
	    		 startButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.stop24);
	    	 }
	    	 else{
	    		rectv.setText("");
           	  	camcorderView.stopRecording();
           	  	//endRecording();
           	  	if(existing_videoid.equalsIgnoreCase("-1")){
           	  		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    		   	
	    		   	//String date = ""+cal.getTimeInMillis();
	    			//TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    			videoid = video_table+"_"+video_refid+"_"+UUID.randomUUID().toString()+".mp4";
           	  	}
           	  	else
           	  		videoid = existing_videoid;
           	  	
           	  	copyFile();
	    	 
           	  	Bundle extras = getIntent().getExtras();
           	  	extras.putString(KEY_VIDEO, videoid);
           	  	this.getIntent().putExtras(extras);
           	  	setResult(RESULT_OK, this.getIntent());
           	  	dbAccess.close();
           	  	finish();  
	    	 	}
	     }
	     
	    /* private void endRecording(){
	    	 
	    	copyFile();
	    	 
	    	Bundle extras = getIntent().getExtras();
	    	extras.putString(KEY_VIDEO, videoid);
	    	this.getIntent().putExtras(extras);
	    	setResult(RESULT_OK, this.getIntent());
	    	finish();  
	     } */
	     
	     
       /*  @Override 
         public boolean onKeyDown(int keyCode, KeyEvent event) 
         { 
             if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) 
             { 
           	  if (recording) { 
           		  	camcorderView.stopRecording();
                    finish(); 
                } else { 
                    recording = true; 
                    camcorderView.startRecording(); 
                } 
                 return true; 
             } 
             return super.onKeyDown(keyCode, event); 
         }	  */   
         
         public void showAlert(String result){
        		new AlertDialog.Builder(this)
        	    .setTitle(R.string.error) //"Error")
        	    .setMessage(result)
        	    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

        	         public void onClick(DialogInterface dialog, int whichButton) {
        	        	endCamcorder();
        	         }
        	    }).show();	
        	}
         
         private void endCamcorder(){
        	setResult(RESULT_OK, this.getIntent());
	 	    finish();  
         }
         
         private void copyFile(){ //String srFile, String dtFile){
        	 File tempfile = new File(Epi_collect.appFiles+"/temp.mp4"); //Environment.getExternalStorageDirectory()+"/EpiCollect/temp.mp4");   
        	 try{

        	      InputStream in = new FileInputStream(tempfile);

        	      //For Overwrite the file.
        	      OutputStream out = new FileOutputStream(new File(videodir+"/"+videoid));

        	      byte[] buf = new byte[1024];
        	      int len;
        	      while ((len = in.read(buf)) > 0){
        	        out.write(buf, 0, len);
        	      }
        	      in.close();
        	      out.close();
        	    }
        	    catch(FileNotFoundException ex){
        	    	Log.i("Camcorder", ex.toString());
        	    }
        	    catch(IOException e){
        	    	Log.i("Camcorder", e.toString());
        	    }
        	    
        	    try{
        	   		tempfile.delete();
        	   	}
        	   	catch (Exception e){
        	   		
        	   	}
        	  }
}