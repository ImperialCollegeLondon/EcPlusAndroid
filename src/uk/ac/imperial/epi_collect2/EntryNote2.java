package uk.ac.imperial.epi_collect2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.Vector;

import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import uk.ac.imperial.epi_collect2.media.audio.AudioRecorder;
import uk.ac.imperial.epi_collect2.media.camera.Camcorder;
import uk.ac.imperial.epi_collect2.media.camera.ImageViewer;
import uk.ac.imperial.epi_collect2.media.camera.VideoPlayer;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;

//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EntryNote2 extends Activity implements LocationListener {
 
	private static final int ACTIVITY_VIDEO=2;
	private static final int ACTIVITY_BRANCH=3;
	private static final int ACTIVITY_BRANCH_LIST=4;
	private static final int ACTIVITY_CAP_PHOTO=6;
	private static final int ACTIVITY_SELECT_PICTURE=7;
	private Hashtable<String, EditText> textviewhash;
	private Hashtable<String, ImageView> imageviewhash;
	private Hashtable<String, String> imageviewvalhash;	
	private Hashtable<String, ImageView> videoviewhash;
	private Hashtable<String, String> videoviewvalhash; 
	private Hashtable<String, String> audioviewvalhash;
	private Hashtable<Integer, String> textviewposhash, radioposhash, imageviewposhash, videoviewposhash, audioviewposhash, gpsposhash;
	private Hashtable<String, Integer> allitemposhash;
	private Hashtable<Integer, String> allitemposhashrev;
	private Hashtable<String, LinkedHashMap<String, RadioButton>> thisradiohash; 
	private Hashtable<Integer, Vector<RadioButton>> jumpradiohash;
	private Hashtable<String, String> radioselectedhash = null;

	private static Hashtable <String, String>jumps; 
	private Hashtable <Integer, String>branchhash = new Hashtable <Integer, String>();
	private Hashtable <Integer, TextView>branchtvhash = new Hashtable <Integer, TextView>();
	private Vector<String> jumpreversevec = new Vector<String>();
	private Vector<String> nodisplay = new Vector<String>();
	private Vector<String> radioimages = new Vector<String>();
	private Vector<String> noteditable = new Vector<String>();
	private Vector<String> primary_keys = new Vector<String>();
	private static String[] textviews = new String[0];
    private static String[] radios = new String[0];
    private static Vector<String> gpstags = new Vector<String>();
    private static String[] photos = new String[0];
    private static String[] videos = new String[0];
    private static String[] audio = new String[0];
    private static Vector<String> requiredfields=new Vector<String>(), requiredradios=new Vector<String>(), storedrequiredfields=new Vector<String>(), storedrequiredspinners=new Vector<String>(), storedrequiredradios=new Vector<String>();
	private static final int RESET_ID = 1;
	private static final int CHANGE_GPS = 2;
	private static final int HOME = 3;
	private static final int SAVE_RECORD = 4;
	private static final int HIERARCHY = 5;
	private ViewFlipper flipper;
	private EditText et; //, et2;
	private DBAccess dbAccess;
	private Button photoButton, audioButton;
	private String coretable; 
	private static Vector<String> listfields=new Vector<String>(), listradios=new Vector<String>();
	private Vector<String>gpstagstoskip = new Vector<String>();
	private String[] allviews = new String[0];
	private int lastpage = 0, thispage = 1;
	private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private GestureDetector gestureDetector;
    private String primary_key = "", title = ""; 
    private boolean newentry = true, usesgps = false; 
    private LocationManager locationManager; 
    private String select_table, list_select_table, foreign_key = "", detailstarget = "", thumbdir, picdir, existing_photoid = "-1", imagefile, videodir, audiodir;
    private int isnew = 1;
    private Hashtable<String, Boolean> gpssethash = new Hashtable<String, Boolean>();
    private Hashtable<String, Button> gpsbuttonhash = new Hashtable<String, Button>();
    private Hashtable<String, String> gpssettingshash = new Hashtable<String, String>(); 
    private boolean secondcheck = false, secondkeycheck = true; 
    private String latitude = "0", longitude = "0", altitude = "N/A", bearing =  "N/A", accuracy = "N/A";
    private double project_version = 1.0;
    private String provider = "";
    private long oldtime = 0, newtime = 0;
    private Handler mHandler; 
    private Hashtable<String, Button> photobuttonhash = new Hashtable<String, Button>(), videobuttonhash = new Hashtable<String, Button>();
    private Hashtable<String, ImageButton>audioplaybuttonhash, audiorecordbuttonhash, audiostopbuttonhash;
    private Hashtable<String, TextView>audiotextviewhash;
    private Vector<String> hiddentextviewkeys = new Vector<String>();
    private AudioRecorder recorder;
    public boolean audioactive = false;
    private String genkey = "";
    private Vector<TextView> gpstvwarnvec;
    private boolean phonekeyset = false;
    private int phonekey;
    private String project, currentkey = "";
	private int isbranch = 0;
	private boolean resetting_radiobuttons = false, canedit = true, flipping = false;
	private Vector <Vector<RadioButton>>radiogroup_vec = new Vector<Vector<RadioButton>>();
	private ProgressDialog myProgressDialog = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        
		dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    project = dbAccess.getProject();
	    //super.setTitle("EpiCollect+ "+project);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    Bundle extras = getIntent().getExtras();
	    
	    mHandler = new Handler();
	    
	    thumbdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + project; // + this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	    picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + project; //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	    videodir = Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + project;
	    audiodir = Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + project;
	        
	    if(icicle != null){
	    	coretable = icicle.getString("table");
	    	isnew = icicle.getInt("new");  
	    	list_select_table = icicle.getString("select_table_key_column");
	    	select_table = icicle.getString("select_table");
	    	foreign_key = icicle.getString("foreign_key");
	    	detailstarget = icicle.getString("target");
	    	thispage = icicle.getInt("thispage");
	    	primary_key = icicle.getString("primary_key");
	    	imagefile = icicle.getString("imagefile");
	    	isbranch = icicle.getInt("branch");
	    	    	
	    }
	    else{
	    	coretable = extras.getString("table");
	    	isnew = extras.getInt("new");
	    	list_select_table = extras.getString("select_table_key_column");
	    	foreign_key = extras.getString("foreign_key");
	    	select_table = extras.getString("select_table");
	    	isbranch = extras.getInt("branch");
	    	
		    if(extras.getString("target") != null && extras.getString("target").length() > 0){
		    	detailstarget = extras.getString("target");
		    }
		    if(isnew == 0){
		    	primary_key = extras.getString("primary_key");
		    	
		    }
	    }

		    
		  // This may not be needed
		  if(isnew == 0){
		    	newentry = false;
		  }
	    	
	    getValues(coretable);
	    
	    try{
			project_version = Double.parseDouble(dbAccess.getValue("project_version"));
		}
		catch(NumberFormatException npe){
			project_version = 1.0;
		}
		catch(NullPointerException npe){
			project_version = 1.0;
		}
		
	    	    
	    setContentView(setLayout(coretable, extras)); 

	}

	
	@Override

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(0, RESET_ID, 0, R.string.menu_reset);
		if(usesgps && gpsposhash.get(thispage) != null){
	    	menu.add(0, CHANGE_GPS, 0, R.string.menu_change_gps);
	    }
		
		menu.add(0, SAVE_RECORD, 0, R.string.menu_save);
	    menu.add(0, HOME, 0, R.string.menu_home);
	    if(isbranch == 0 && dbAccess.getTableNum(coretable) > 1) //(!foreign_key.equalsIgnoreCase("Null") && isbranch == 0)
	    	menu.add(0, HIERARCHY, 0, R.string.menu_hierarchy);
	    
		return super.onPrepareOptionsMenu(menu);

	}


	@Override
    protected void onPause() {
        super.onPause();
        Log.i("LIFECYCLE", "onPause");
        dbAccess.close();
        dbAccess = null;
        if(usesgps){
        	//locationManager.removeUpdates(this);
        	removeGPSUpdates();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("LIFECYCLE", "onResume");
        if (dbAccess == null) {
        	dbAccess = new DBAccess(this);
        	dbAccess.open();
        }
        if(usesgps){
        	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //mlocListener = new MyLocationListener();
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this); //mlocListener);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LIFECYCLE", "onStart");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("LIFECYCLE", "onDestroy");
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
    		if(dbAccess != null)
    			dbAccess.close();
    		Intent i = new Intent(this, Epi_collect.class);
 	   		startActivity(i);
    			//confirmBack(); //event);
    	}
    	// Menu button doesn't work by default on this view for some reason
    	if(keyCode == KeyEvent.KEYCODE_MENU){
    		//openOptionsMenu();
    	}

        return true;
    } 
    
 // Prevents warnings about casting from an object to a hashtable
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	super.onActivityResult(requestCode, resultCode, data);
	        
	 // Cascades the exit program call
	   	if (requestCode == 123) {
		 	   this.finish();
		 	   }
	   	
	   	Bundle extras = null;
	   	if(data != null)
	   		extras = data.getExtras();
	    switch(requestCode) {
	   /* case ACTIVITY_PHOTO:
	    	//Log.i("IN PHOTO", "1");
	    	//if(extras != null){
	    		//Log.i("IN PHOTO", "2");
	    		updateData(extras, 1);   		
	    	//}
	        break; */
	    case ACTIVITY_CAP_PHOTO:
	    	createThumbnail();
	    	File f = new File(picdir+"/"+imagefile);
	    	// If the back button is pressed while taking a picture the file will not exist
	    	if(f.exists()){
	    		updateData(extras, 1);
	    	}

	        break;
	    case ACTIVITY_SELECT_PICTURE:   
	        
	        try{
	   			Uri selectedImageUri = data.getData();
	   			//OI FILE Manager
	   			String filemanagerstring = selectedImageUri.getPath();

	   			//MEDIA GALLERY
	   			String selectedImagePath = getPath(selectedImageUri);

	   			String fpath = "";
	   			//NOW WE HAVE OUR WANTED STRING
	   			if(selectedImagePath!=null){
	   				System.out.println("selectedImagePath is the right one for you!");
	   				fpath = selectedImagePath;
	   			}
	   			else{
	   				System.out.println("filemanagerstring is the right one for you!");
	   				fpath = filemanagerstring;
	   			}
	        
	   			if(fpath.length() > 0){
	   				existing_photoid = existing_photoid.replaceAll("\\s+", "");
	   				if(existing_photoid.equalsIgnoreCase("-1") || existing_photoid.length() == 0){
	   					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	   				   	String date = ""+cal.getTimeInMillis();
	   					TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	   					imagefile = coretable+"_"+imageviewposhash.get(thispage)+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".jpg";
	   				}
	   				else
	   					imagefile = existing_photoid;
	   				copyFile(new File(fpath), new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
	   			}
	   			createThumbnail();
	   			updateData(extras, 1);   	
	   		}
	   		catch(NullPointerException npe){
	   		}
	   			
	        break;
	    case ACTIVITY_VIDEO:
	    	//if(extras != null){
	    	
	    	updateData(extras, 2);   		
	    	//}
	        break;
	    case ACTIVITY_BRANCH:
	    	if (dbAccess == null) {
	        	dbAccess = new DBAccess(this);
	        	dbAccess.open();
	        }
	    	// Need to reset this as it seems to be the branch values otherwise
	    	// Need to find out why
	    	getValues(coretable);
	    	String branch_key = "";

			branch_key = branch_key.replaceFirst(",", "");
	    	int entries = dbAccess.getBranchCount(extras.getString("BRANCH_FORM"), branch_key); //primary_keys.elementAt(0), textviewhash.get(primary_keys.elementAt(0)).getText().toString());
	    	branchtvhash.get(thispage).setText(Html.fromHtml("<center>"+entries+" "+this.getResources().getString(R.string.entries_for_record)+"</center>"));
	        break;
	    case ACTIVITY_BRANCH_LIST:
	    	if (dbAccess == null) {
	        	dbAccess = new DBAccess(this);
	        	dbAccess.open();
	        }
	    	break;
	   
	    }
	 }
	 
	 public String getPath(Uri uri) {
		    String[] projection = { MediaStore.Images.Media.DATA };
		    Cursor cursor = managedQuery(uri, projection, null, null, null);
		    if(cursor!=null)
		    {
		        //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
		        //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
		        int column_index = cursor
		        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		        cursor.moveToFirst();
		        return cursor.getString(column_index);
		    }
		    else return null;
		}
    
    
    private void removeGPSUpdates(){
    	if(locationManager != null){
			locationManager.removeUpdates(this);
			//mlocListener.removeUpdates();
    	}	
    }
    
    
   
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	super.onSaveInstanceState(outState);  

        for(String key : radios){
        	outState.putString(key, radioselectedhash.get(key));
        }
        
        outState.putString("table", coretable);
	    
        outState.putInt("branch", isbranch);

        outState.putString("select_table_key_column", list_select_table);
        outState.putString("select_table", select_table);
        outState.putString("foreign_key", foreign_key);
        outState.putString("primary_key", primary_key);
        //outState.putInt("isnew", 1);
        outState.putString("imagefile", imagefile);

        outState.putString("target", detailstarget);
        outState.putInt("thispage", thispage);
	    	
    }  
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      
      Log.i("LIFECYCLE", "onRestoreInstanceState");
      coretable = savedInstanceState.getString("table");
	  isnew = savedInstanceState.getInt("new");  
	  list_select_table = savedInstanceState.getString("select_table_key_column");
	  select_table = savedInstanceState.getString("select_table");
	  foreign_key = savedInstanceState.getString("foreign_key");
	  detailstarget = savedInstanceState.getString("target");
	  thispage = savedInstanceState.getInt("thispage");
	  primary_key = savedInstanceState.getString("primary_key");
	  imagefile = savedInstanceState.getString("imagefile");
	  isbranch = savedInstanceState.getInt("branch");
	  //coretablekey = dbAccess.getKeyValue(coretable);
	  
	  // This may not be needed
	  if(isnew == 0){
	    	newentry = false;
	  }
	  
    
	    //Log.i("LIFECYCLE", "onRestoreInstanceState PKEY: "+primary_key+" THISPAGE "+thispage+" ISNEW "+isnew);
	    //Log.i("CREATING", "IN ONCREATE");
	    
	    flipper.setDisplayedChild(0); 
	    int targetpage = thispage;
	    thispage = 1;
	    try{
	    	if(targetpage > 1){
	    		flipper.setInAnimation(null);
	            flipper.setOutAnimation(null);
	    		//Log.i("FLIPPING", "FLIPPING PAGE");
	    		while(thispage < targetpage){
	    			flipper.showNext();
	    			thispage++;
	    		}
	    	}
	    }
	    catch(NullPointerException npe){}
    }

     
    private void resetData(){
    	if(imageviewvalhash != null)
    		imageviewvalhash.clear();
    	if(videoviewvalhash != null)
    		videoviewvalhash.clear();
    	if(audioviewvalhash != null)
    		audioviewvalhash.clear();
   		
   		primary_key = "";
   		phonekeyset = false;
   		
    	//for(String key : textviews){
    	for(String key : textviewhash.keySet()){
    		if(!nodisplay.contains(key)){ // && !setextras.containsKey(key)){
    			textviewhash.get(key).setText("");
    			if(genkey.equalsIgnoreCase(key)){
	        		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	sIMEI += "_"+cal.getTimeInMillis();
	    	    	textviewhash.get(key).setText(sIMEI);
	    			et.setFocusable(false);
	        	}

    			if(gpsbuttonhash.get(key) != null){
    				gpsbuttonhash.get(key).setEnabled(true);
    				gpsbuttonhash.get(key).setTextColor(Color.BLACK);
    				gpsbuttonhash.get(key).setText(R.string.set_location); //"Tap to Set Location");
    				gpssettingshash.put(key+"_lat", "0"); //gpslocation.getLatitude()));
    				gpssettingshash.put(key+"_lon", "0"); //gpslocation.getLongitude()));
    				gpssettingshash.put(key+"_alt", "N/A"); //gpslocation.getAltitude()));
    				gpssettingshash.put(key+"_acc", "N/A"); //gpslocation.getAccuracy()));
    				gpssettingshash.put(key+"_bearing", "N/A");
    				gpssettingshash.put(key+"_provider", "");
    				
    				gpssethash.put(key, false);
    			}
	
    		}
        } 
                      
        resetting_radiobuttons = true;
        for(Vector<RadioButton> rg : radiogroup_vec){
        	// For some reason this doesn't clear the last radiogroup 
        	// so using the for loop to force it
        	//rg.clearCheck();
        	//for(int i = 0; i < rg.getChildCount(); i++){
        	//	((RadioButton) rg.getChildAt(i)).setChecked(false);
        	//}
        	for(RadioButton rb : rg){
            	rb.setChecked(false);
            	//Log.i("RADIO LABEL", rb.getTag().toString());
            	}
        }
        resetting_radiobuttons = false;
        
        if(radioselectedhash != null){
        	for(String key : radioselectedhash.keySet())
        		radioselectedhash.put(key, "");
        }
        
        ImageView iv;
        for(String key : imageviewhash.keySet()){
        	iv = imageviewhash.get(key);
        	iv.setImageBitmap(null);
        }
        
        for(String photoButton : photobuttonhash.keySet())
        	photobuttonhash.get(photoButton).setText(R.string.add_photo); //"Tap to Add Photo");
        
        for(String key : videoviewhash.keySet()){
        	iv = videoviewhash.get(key);
        	iv.setImageBitmap(null); //setImageURI(null);
        }
        
        for(String videoButton : videobuttonhash.keySet())
        	videobuttonhash.get(videoButton).setText(R.string.add_video); //"Tap to Add Video");
        
        if(audiorecordbuttonhash != null){
        	for(String audiorecordButton : audiorecordbuttonhash.keySet())
        		audiorecordbuttonhash.get(audiorecordButton).setEnabled(true);
        }
        
        if(audioplaybuttonhash != null){
        	for(String audioplayButton : audioplaybuttonhash.keySet())
        		audioplaybuttonhash.get(audioplayButton).setEnabled(false);
        }
        if(audiostopbuttonhash != null){
        	for(String audiostopButton : audiostopbuttonhash.keySet())
        		audiostopbuttonhash.get(audiostopButton).setEnabled(false);
        }
        if(audiotextviewhash != null){
        	for(String audioTextView : audiotextviewhash.keySet())
        		audiotextviewhash.get(audioTextView).setText(R.string.no_audio); //"No Audio Recorded");
        }
        // If a primary key field is hidden need to reset it
        
        for(String key : hiddentextviewkeys){
        	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        	String sIMEI = mTelephonyMgr.getDeviceId();
    	
        	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
   	   	
        	sIMEI += "_"+cal.getTimeInMillis();
        	textviewhash.get(key).setText(sIMEI);
        	
        }
        
        
    }
    
    private void getValues(String table){
    	
    	// Reset values as otherwise odd values seem to get left behind:
    	// Project - data - back -back - new project - data -> causes crash
    	// If first project has spinner and second doesn't then second "spinners"
    	// still contains
    	textviews = new String[0];
        radios = new String[0];
        gpstags = new Vector<String>();
        photos = new String[0];
        videos = new String[0];
        audio = new String[0];
        primary_keys.clear();
        jumps = new Hashtable <String, String>();
        
    	if(dbAccess.getValue(table, "textviews") != null && dbAccess.getValue(table, "textviews").length() > 0){
			textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
    	}

    	if(dbAccess.getValue(table, "radios") != null && dbAccess.getValue(table, "radios").length() > 0){
    		radios = (dbAccess.getValue(table, "radios")).split(",,");
    	}
    	String[] tempstring = null;
    	if(dbAccess.getValue(table, "gps") != null && dbAccess.getValue(table, "gps").length() > 0){
    		tempstring = (dbAccess.getValue(table, "gps")).split(",,");
    		for(String key : tempstring){     
    			gpstags.addElement(key);
    		}
    		
    	}
    	if(dbAccess.getValue(table, "photos") != null && dbAccess.getValue(table, "photos").length() > 0)
    		photos = (dbAccess.getValue(table, "photos")).split(",,");
    	
    	if(dbAccess.getValue(table, "videos") != null && dbAccess.getValue(table, "videos").length() > 0)
    		videos = (dbAccess.getValue(table, "videos")).split(",,");
    	
    	if(dbAccess.getValue(table, "audio") != null && dbAccess.getValue(table, "audio").length() > 0)
    		audio = (dbAccess.getValue(table, "audio")).split(",,");
    	    
    	tempstring = null;

    	// Could probably get this from getkeyvalue 
    	if(dbAccess.getKeyValue(table) != null && dbAccess.getValue(table, "ecpkey").length() > 0){
    		for(String key : (dbAccess.getValue(table, "ecpkey")).split(";")){
    			primary_keys.addElement(key);
        	}
    	}
        
        if(dbAccess.getValue(table, "nodisplay") != null && dbAccess.getValue(table, "nodisplay").length() > 0){
        	for(String key : (dbAccess.getValue(table, "nodisplay")).split(",,")){
        		nodisplay.addElement(key);
        	}
        }
        
        if(dbAccess.getValue(table, "editable") != null && dbAccess.getValue(table, "editable").length() > 0){
        	for(String key : (dbAccess.getValue(table, "editable")).split(",,")){
        		noteditable.addElement(key);
        	}
        }

        if(dbAccess.getValue(table, "genkey") != null && dbAccess.getValue(table, "genkey").length() > 0){
        	genkey = dbAccess.getValue(table, "genkey");
        }
        
        String[] temp;
        String ref;
        if(dbAccess.getValue(table, "jumps") != null && dbAccess.getValue(table, "jumps").length() > 0){
    		for(String key : (dbAccess.getValue(table, "jumps")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			jumps.put(ref, key);
    			
    		}
    	}
        
        if(dbAccess.getValue(table, "radioimages") != null && dbAccess.getValue(table, "radioimages").length() > 0){
        	for(String key : (dbAccess.getValue(table, "radioimages")).split(",,")){
        		radioimages.addElement(key);
        	}
        }
           
    }
    
    
    //@SuppressLint("NewApi")
	private RelativeLayout setLayout(String table, Bundle extras){
        
    	Button confirmEditButton, barcodeButton, branchButton, listbranchButton, groupButton;
    	LinkedHashMap<String, RadioButton> radiohash;
    	// CHANGE TEXTVIEWHASH TO EDITTEXTHASH
    	textviewhash = new Hashtable<String, EditText>();
    	imageviewhash = new Hashtable<String, ImageView>();
    	videoviewhash = new Hashtable<String, ImageView>();
    	textviewposhash = new Hashtable<Integer, String>();
    	imageviewposhash = new Hashtable<Integer, String>();
    	videoviewposhash = new Hashtable<Integer, String>();
    	audioviewposhash = new Hashtable<Integer, String>();
    	gpsposhash = new Hashtable<Integer, String>();
    	radioposhash = new Hashtable<Integer, String>();
    	allitemposhash = new Hashtable<String, Integer>();
    	allitemposhashrev = new Hashtable<Integer, String>();
        thisradiohash = new Hashtable<String, LinkedHashMap<String, RadioButton>>(); // RadioButton
        
		jumpradiohash = new Hashtable<Integer, Vector<RadioButton>>(); //RadioGroup>();
		
		 int Measuredwidth = 0;
		  int Measuredheight = 0;
		  Point size = new Point();
		  WindowManager w = getWindowManager();

		  	// Need to check android version to get dimension
		    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
		          w.getDefaultDisplay().getSize(size);

		          Measuredwidth = size.x;
		          Measuredheight = size.y; 
		        }else{
		          Display d = w.getDefaultDisplay(); 
		          Measuredwidth = d.getWidth(); 
		          Measuredheight = d.getHeight(); 
		        }
    	
        String views =  dbAccess.getValue(table, "notes_layout");// parser.getValues();
                
        flipper = new ViewFlipper(this);
	    
	    RelativeLayout ll = new RelativeLayout(this);
	    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    
	 // Calculate last page
	    
	    int count = 0;
	    String[] viewvalues;
	    String[] tempstring;
	    
	    views = views.replaceFirst(",,,", "");
	    Log.i(getClass().getSimpleName(), "VIEWS NOW "+views);
	    allviews = views.split(",,,");

	    lastpage = 0; // 1; Lastpage starts at 1 to allow for the extra page for the store button
    	    
	    Vector<String>types = new Vector<String>();
    	types.add("input");
    	types.add("select1");
    	types.add("radio");
    	types.add("photo");
    	types.add("video");
    	types.add("audio");
    	types.add("gps");
    	types.add("GPS");
    	types.add("location");
    	types.add("barcode");
    	types.add("branch");
    	types.add("group");
    	types.add("select");
    	
    	for(String thisview : allviews){
	    	
	    	viewvalues = thisview.split(",,");
	    	
	    	// Don't want to display this field
	    	if(nodisplay.contains(viewvalues[1])){
	    		if(genkey.equalsIgnoreCase(viewvalues[1])){
	        		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	sIMEI += "_"+cal.getTimeInMillis();
	    	       	et = new EditText(this);
	    			et.setText(sIMEI);
	    			textviewhash.put(viewvalues[1], et);
	    			
	    			hiddentextviewkeys.addElement(viewvalues[1]);
	        	}
	    		//continue;
	    	}
	    	
	    	
	    	else if(types.contains(viewvalues[0]))
	    		lastpage++;
	    	
	    } 
	   
	
	    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
	       
	    ll.addView(flipper, linear1layout2);
	       
	    ScrollView s = new ScrollView(this);
	    
	    flipper.addView(s); 
	    
	    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    TableLayout l=new TableLayout(this);
	    
	    s.addView(l); 
	      
	    RelativeLayout rl2;
	    RelativeLayout.LayoutParams rlp3=null;
	    rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	   // View v;
	        
	    if(lastpage >= 2){
	    	rl2 = new RelativeLayout(this);

	    	rlp3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    	//rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        
	    	l.addView(rl2);

	    	}
	   
		count = 0;
		   
		
		
		int page = 2;
		
	    for(String thisview : allviews){
	    	viewvalues = thisview.split(",,");
	    	
	    	
	    	if(count >= 1){ 
	    		s = new ScrollView(this);
	    	    
	    		flipper.addView(s); //, sp);
	    	    
	    	    l=new TableLayout(this);
	    	    
	    	    rl2 = new RelativeLayout(this);
	    	    
	    	    ImageButton bp = new ImageButton(this);
	    	    bp.setOnClickListener(listenerPrevious);
	    	    bp.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.back);
	    		//bp.setWidth(100);
	    	    //bp.setText(R.string.previous); //"Prev");
	    	    //rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    	    
	    	    rl2.addView(bp, rlp3);
	    	    
	    	    page++;
	    	    
	    	    l.addView(rl2);
	    	    
	    	    s.addView(l, lp);
	
	    	   /* v = new View(this);
	    		v.setMinimumHeight(2);
	    		v.setBackgroundColor(Color.WHITE);
	    		l.addView(v, lp);
	    		
	    		v = new View(this);
	    		v.setMinimumHeight(15);
	    		l.addView(v, lp);*/
	    		
	    		count = 0;
	    		}
	    	
	    
	    	if(viewvalues[0].equalsIgnoreCase("location") || viewvalues[0].equalsIgnoreCase("GPS")){
	    		
	    	    // Only want to initialise this once
    			if(!usesgps){
    				locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		       	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this); //mlocListener);

    			}
    			
    			usesgps = true;
    			
    			gpsposhash.put(page-1, viewvalues[1]);
    			allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
	    			    		
	    		gpssettingshash.put(viewvalues[1]+"_lat", "0");
	    		gpssettingshash.put(viewvalues[1]+"_lon", "0");
	    		gpssettingshash.put(viewvalues[1]+"_alt", "N/A");
	    		gpssettingshash.put(viewvalues[1]+"_acc", "N/A");
	    		gpssettingshash.put(viewvalues[1]+"_bearing", "N/A");
	    		gpssettingshash.put(viewvalues[1]+"_provider", "");
	    		
    		} 
	    	
	    	if(viewvalues[0].equalsIgnoreCase("photo")){
	    		
	    		// Only create the hash if it is actually needed
	    		if(imageviewvalhash == null)
	    			imageviewvalhash = new Hashtable<String, String>();
    			
       	
	        	ImageView iv = new ImageView(this);
	        	l.addView(iv, lp);
	        		    		
	        	imageviewvalhash.put(viewvalues[1], "-1");
	        	imageviewhash.put(viewvalues[1], iv);
	    		// To set the focus when the page is flipped
	    		imageviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
 		
	    		
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("video")){
	    		
	    		// Only create the hash if it is actually needed
	    		if(videoviewvalhash == null)
	    			videoviewvalhash = new Hashtable<String, String>();
	    			        	
	        	ImageView iv = new ImageView(this);
	        	l.addView(iv, lp);
	    		
	        	videoviewhash.put(viewvalues[1], iv);
	    		// To set the focus when the page is flipped
	    		videoviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	videoviewvalhash.put(viewvalues[1], "-1");
	    		count++;

	    		
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("audio")){
	    		
	    		// Only create the hash if it is actually needed
	    		if(audioviewvalhash == null){
	    			audioviewvalhash = new Hashtable<String, String>();
	    			recorder = new AudioRecorder();
	    			audioplaybuttonhash = new Hashtable<String, ImageButton>();
	    			audiotextviewhash = new Hashtable<String, TextView>();
	    			audiorecordbuttonhash = new Hashtable<String, ImageButton>();
	    			audiostopbuttonhash = new Hashtable<String, ImageButton>();
	    			audiotextviewhash = new Hashtable<String, TextView>();
	    		}
	    		
	        	ImageButton recButton = new ImageButton(this);
	        	recButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.record24);
    			l.addView(recButton, lp);
    			audiorecordbuttonhash.put(viewvalues[1], recButton);
    			
    			ImageButton playButton = new ImageButton(this);
    			playButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.play24);
    			l.addView(playButton, lp);
    			audioplaybuttonhash.put(viewvalues[1], playButton);
    			playButton.setEnabled(false);
    			
    			ImageButton stopButton = new ImageButton(this);
    			stopButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.stop24);
    			l.addView(stopButton, lp);
    			audiostopbuttonhash.put(viewvalues[1], stopButton);
    			stopButton.setEnabled(false);
    			
    			recButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		
    		          	recAudioCheck();
    		        }
    		           
    		    }); 
    			
    			playButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    	
    		          	playAudio();
    		        }
    		           
    		    }); 
    			
    			stopButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		
    		          	stopAudio();
    		        }
    		           
    		    });

	    		// To set the focus when the page is flipped
	    		audioviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	audioviewvalhash.put(viewvalues[1], "-1");
	    		count++;

	    		
	    	}
	    
	    	if(viewvalues[0].equalsIgnoreCase("radio")){ 
	    		
	    		if(radioselectedhash == null)
	    			radioselectedhash = new Hashtable<String, String>();

	        	RadioButton rb;
	        	
	        	//RadioGroup rg = new RadioGroup(this);
	        	Vector<RadioButton> rg = new Vector<RadioButton>();
	        	
	        	//LinearLayout rgl = new LinearLayout(this);
	        	//rgl.addView(rg);
	        	
	        	radiohash = new LinkedHashMap<String, RadioButton>();
	        	
	    		//l.addView(rgl);
	    	    		
	    		tempstring = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");
    		
	    		TableRow tr1 = new TableRow(this);
	    		l.addView(tr1);
	    		for (int i = 1; i < tempstring.length; i++) {
	    			if(i > 1 && i % 2 == 1){
	    				tr1 = new TableRow(this);
	    	    		l.addView(tr1);
	    			}
	    			rb = new RadioButton(this);
	    			radiohash.put(tempstring[i], rb);
	    			//rg.addView(rb);
	    			rg.addElement(rb);
	    			tr1.addView(rb);
	    			rb.setTag(tempstring[i]);
	    			rb.setWidth(250);

	    			if(radioimages.contains(viewvalues[1])){
	    				Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/EpiCollect/radioimgdir_epicollect_"+dbAccess.getProject()+"/" + tempstring[i]+".jpg"); 
	    				Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Measuredwidth/2, Measuredwidth/2, true));
	    				rb.setButtonDrawable(d);
	    			}
	    			else{
	    				rb.setText(tempstring[i]);
	    			}
	    			
	    			// Use this instead of rb.setButtonDrawable(d) and button is overlaid over background so selection is shown
	    			//rb.setBackgroundDrawable(d); 
	    			
	    			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    			//@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
						
							if(!resetting_radiobuttons){
								updateRadioButtons();
								//View current  = flipper.getCurrentView();
								//current.setVisibility(View.INVISIBLE);
								flipToNext();
																
								if(gpsposhash.get(thispage) != null){
									showDialog();	
									Thread thread = new Thread()
									{
									    @Override
									    public void run() {
									    	setGPS();
 	        
									    }
									};

									thread.start();
					        		
					        	}
							}
							
						}
			    	});
	 	    	}
	    		radiogroup_vec.add(rg);
	    		  
    			jumpradiohash.put(page-1, rg);
	    		thisradiohash.put(viewvalues[1], radiohash);
	    		
	    		radioselectedhash.put(viewvalues[1], "");
	 		 	    	
	 	    	radioposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    		
	    		count++;
	    		
	    	}
	    	
	    	
	    }
	    
	   
	    return ll;
	    
	 }
    
    private void showDialog(){
    	myProgressDialog = ProgressDialog.show(this, "", "", true);
    }
  
    private void addBranch(){
    	
    	
    	Intent i = new Intent(this, EntryNote.class);
		
    	// Add the branch table
		i.putExtra("table", branchhash.get(thispage));

		// The 2 signifies it is a branch
		i.putExtra("new", 2);
		i.putExtra("branch", 1);
		
		// To add the linked from primary key value
		//for(String key: primary_keys){
		//	 i.putExtra(key, textviewhash.get(key).getText().toString());
		//}
		//i.putExtra(primary_keys.elementAt(0), textviewhash.get(primary_keys.elementAt(0)).getText().toString());

		i.putExtra("fromlist", 0);
		
		
		// This is the not needed
		i.putExtra("select_table_key_column", "Null");
		i.putExtra("foreign_key", "Null");
		i.putExtra("select_table", "Null");
		
		dbAccess.close();
	    startActivityForResult(i, ACTIVITY_BRANCH);
    }
    
  
	
    private void updateRadioButtons(){
		
    	if(!resetting_radiobuttons){
    		for(String i : thisradiohash.get(radioposhash.get(thispage)).keySet()){
    			//Log.i("UPDATING RADIO I CHECK", ""+i);
    			if(thisradiohash.get(radioposhash.get(thispage)).get(i).isChecked()){
    				radioselectedhash.put(radioposhash.get(thispage), i);
    				//Log.i("UPDATING RADIO", radioposhash.get(thispage)+" "+i);
    				return;
    			}
    		}
    	}
    	
    }
    
	    
	    private void flipToNext(){
	    	
	    	//if(thispage == lastpage){
			//	storeData(3);
			//	newEntry();
			//	return;
				
			//}

	    	int jumpnum = 0;
        	if(flipping || checkEntry()){ // checkTotals() && 

        		// Reset value for checkEntry
        		secondcheck = false;
       		
        		//flipper.focusSearch(flipper.FOCUS_RIGHT).requestFocus();
        		//flipper.findViewById(flipper.getNextFocusDownId()).requestFocus();
        		jumpnum = checkJump();

        		//Log.i("JUMP NUM", ""+jumpnum);
        		if(jumpnum == 0){
        			flipper.showNext();
        			thispage++;
        			// Need to remove entries in case new data has been added
        			if(jumpreversevec.contains(""+thispage))
        				jumpreversevec.remove(""+thispage);
        			// If this is a required field as it is no longer jumped
        			// it must be put back
        			if(storedrequiredfields.contains(allitemposhashrev.get(thispage)))
        				requiredfields.addElement(allitemposhashrev.get(thispage));
        			if(storedrequiredradios.contains(allitemposhashrev.get(thispage)))
	        			requiredradios.addElement(allitemposhashrev.get(thispage));
        			if(gpstagstoskip.contains(allitemposhashrev.get(thispage)))
        				gpstagstoskip.remove(allitemposhashrev.get(thispage));
        			// For John - IGNORE THIS as storeDate(3) called anyway
        			//tempStore();
        			
        		}
        		else{
        			// If the jump is to stay on the same page
        			if(thispage == jumpnum)
        				return;
        			//Log.i("RETURNED JUMPNUM CHECK", ""+jumpnum+ " THIS PAGE "+ thispage);
        			flipper.setInAnimation(null);
                    flipper.setOutAnimation(null);
                    //thispage++;
                    for(int i = thispage; i < jumpnum-1; i++){
                    	flipper.showNext();
                    	thispage++;
                    	jumpreversevec.add(""+thispage);  
                    	
                    	// To avoid errors when data is saved
	        			// If an entry is jumped it can't be required
	        			if(requiredfields.contains(allitemposhashrev.get(thispage)))
	        				requiredfields.remove(allitemposhashrev.get(thispage));
	        			if(requiredradios.contains(allitemposhashrev.get(thispage)))
		        			requiredradios.remove(allitemposhashrev.get(thispage));
	        			if(gpstags.contains(allitemposhashrev.get(thispage)))
	        				gpstagstoskip.addElement(allitemposhashrev.get(thispage));
                    }
                    flipper.setInAnimation(animateInFrom(RIGHT));
                	flipper.setOutAnimation(animateOutTo(LEFT));
                    flipper.showNext();
                            			
                    thispage++;

                 // Need to remove entries in case new data has been added
        			if(jumpreversevec.contains(""+thispage))
        				jumpreversevec.remove(""+thispage);
        		}
        		
        	}
        	
        	
        	
	    }
	    
	    private OnClickListener listenerPrevious = new OnClickListener() {
	        public void onClick(View v) {
	        	//removeFocus();
	        	// Only need total check on next. Must be able to go back to fix error
	        	//if(checkDoubleInput() && checkValidDate() && checkEntry() && checkRe()){ // && checkTotals(thispage)){
	        		
	        		//storeData(3);
	        		// Reset value for checkEntry
	        	
	        	 	if(audioactive)
	        	 		return;
	        	 	
            		secondcheck = false;
	        		flipper.setInAnimation(animateInFrom(LEFT));
                    flipper.setOutAnimation(animateOutTo(RIGHT));
                    //Log.i("FLIPPER REV CHECK", ""+thispage);
                                       	
                    // Clear buttins on this page
                    resetting_radiobuttons = true;
                    for(RadioButton rb : thisradiohash.get(allitemposhashrev.get(thispage)).values()){
                    	rb.setChecked(false);
                    	
                    }
                    	
                    if(!jumpreversevec.contains(""+(thispage-1))){
                    	flipper.showPrevious();
                    	thispage--;
                    	//Log.i("FLIPPER REV CHECK 4", ""+thispage);
                    	for(RadioButton rb : thisradiohash.get(allitemposhashrev.get(thispage)).values()){
                        	rb.setChecked(false);
                        	
                        }
                    }
                    else{
                    	flipper.setInAnimation(null);
	                    flipper.setOutAnimation(null);
	                    while(jumpreversevec.contains(""+(thispage-1))){
	                    	//Log.i("JUMP REVERSE VEC", ""+thispage);
	                    	flipper.showPrevious();
	                    	thispage--;

	                    }
	                    flipper.setInAnimation(animateInFrom(RIGHT));
	                    flipper.setOutAnimation(animateOutTo(LEFT));
	                    flipper.showPrevious();
	                    thispage--;
	                    //Log.i("FLIPPER REV CHECK 2", ""+thispage);
                    	for(RadioButton rb : thisradiohash.get(allitemposhashrev.get(thispage)).values()){
                        	rb.setChecked(false);
                        	
                        }
                    }
                    resetting_radiobuttons = false;
                    //Log.i("FLIPPER REV CHECK 3", ""+thispage);
	        }

	    };
	    

	    private Animation animateInFrom(int fromDirection) {

	        Animation inFrom = null;

	        switch (fromDirection) {
	        case LEFT:
	                inFrom = new TranslateAnimation(
	                                Animation.RELATIVE_TO_PARENT, -1.0f, 
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f);
	                break;
	        case RIGHT:
	                inFrom = new TranslateAnimation(
	                                Animation.RELATIVE_TO_PARENT, +1.0f, 
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f);
	                break;
	        }

	        inFrom.setDuration(250);
	        inFrom.setInterpolator(new AccelerateInterpolator());
	        return inFrom;
	    }

	    private Animation animateOutTo(int toDirection) {

	        Animation outTo = null;

	        switch (toDirection) {
	        case LEFT:
	                outTo = new TranslateAnimation(
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, -1.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f);
	                break;
	        case RIGHT:
	                outTo = new TranslateAnimation(
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, +1.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f,
	                                Animation.RELATIVE_TO_PARENT, 0.0f);
	                break;
	        }

	        outTo.setDuration(250);
	        outTo.setInterpolator(new AccelerateInterpolator());
	        return outTo;
	    }

	    
	    
	    private int checkJump(){ //int thispage){
	    
	    	String val, query, selected;
	    	int pos; //, selected;

	    	
	    	String[] temp;
	    	if(radioposhash.get(thispage) != null && jumps.containsKey(radioposhash.get(thispage))){ //((version <= 1.0 && jumps.containsKey(radioposhash.get(thispage))) || (version > 1.0 && jumps1.containsKey(radioposhash.get(thispage))))){
	    		
	    		query = radioposhash.get(thispage);
	    		temp = jumps.get(query).split(",");

	    		for(int i = 1; i < temp.length; i+=2){
	    			val = temp[i+1];
	    			if(val.equalsIgnoreCase("All")){
	    				if(temp[i].equalsIgnoreCase("End"))
	    					return lastpage;
	    				else{
	    					try{
	    						return allitemposhash.get(temp[i]);
	    					}
	    					catch(NullPointerException npe){
	    						return 0;
	    					}
	    				}
	    			}
	    		
	    			boolean not = false;
	    			if(val.startsWith("!"))
	    				not = true;
	    			val = val.replaceAll("!", "");
	    			if(project_version <= 1.0){
	    				try{
	    					pos = Integer.parseInt(val);
	    			    			
	    				}
	    				catch(NumberFormatException npe){
	    					return 0;
	    				}

	    				//int rbid = jumpradiohash.get(thispage).getCheckedRadioButtonId();    	
	    				//View rb = jumpradiohash.get(thispage).findViewById(rbid);
	    				//int sel = jumpradiohash.get(thispage).indexOfChild(rb) + 1;
	    				
	    				int sel = 1;
	    				for(RadioButton rb : jumpradiohash.get(thispage)){
	    					if(rb.isChecked())
	    						break;
	    					sel++;
	    				}
	    				
	    				if((not && pos != sel) || (!not && pos == sel)){
		    				if(temp[i].equalsIgnoreCase("End"))
		    					return lastpage;
		    				else{
		    					try{
		    						return allitemposhash.get(temp[i]);
		    					}
		    					catch(NullPointerException npe){
		    						return 0;
		    					}
		    				}
		    			}
	    				
	    			}
	    			else{
	    				selected = radioselectedhash.get(allitemposhashrev.get(thispage));
	    			
	    				if((not && !val.equalsIgnoreCase(selected)) || (!not && val.equalsIgnoreCase(selected))){
	    					if(temp[i].equalsIgnoreCase("End"))
	    						return lastpage;
	    					else{
	    						try{
	    							return allitemposhash.get(temp[i]);
	    						}
	    						catch(NullPointerException npe){
	    							return 0;
	    						}
	    					}
	    				}
	    			}
	    		}
	    		
	    		return 0;
	    	}
	    	
	    	return 0;
	    }
	    
	    
	    private void flipTo(String target){
	    	
	    	flipper.setInAnimation(null);
            flipper.setOutAnimation(null);
            
	    	while(thispage != allitemposhash.get(target)){
	    		//flipper.showNext();
	    		//thispage++;
	    		flipToNext();
	    	}
	    		
	    	// Just in case ...
	    	if(thispage == lastpage)
	    		return;
	    		    	
	    }
	 
	    
	    private void storeData(int type){ //int full){
	    	
	    	String entry, ecjumped = "";
	    	
	    	primary_key = "";
	    	title = "";
	    	
	    // Use a hash to get these values from strings.xml
	    	HashMap<String, String> rowhash = new HashMap<String, String>();
	    	HashMap<String, String> imagerowhash = new HashMap<String, String>();
	    	HashMap<String, String> videorowhash = new HashMap<String, String>();
	    	HashMap<String, String> audiorowhash = new HashMap<String, String>();
	    	Vector<String> branchtodelete = new Vector<String>();

	    	
	    	if(type == 3){
	    		
		    	for(String key : radios){
			    	if(primary_keys.contains(key) && (radioselectedhash.get(key).equalsIgnoreCase(""))){
			    		return;
			    	}
	    		}
	    	}
	    	
	    	// This record has now been saved so if page is recreated isnew needs to be 0
	    	isnew = 0;
	    	
	    	if(!phonekeyset){
	    		phonekey = dbAccess.getMaxPhonekeyValue(coretable);
	    		phonekey++;
	    		phonekeyset = true;
	    	}
	       	
	       	rowhash.put("ecphonekey", ""+phonekey);
	    	if(dbAccess.getValue(coretable, "genkey") != null && dbAccess.getValue(coretable, "genkey").length() > 0){
	    		title += "- "+phonekey + " "; 
	        }
 	    	
	    	// Need to do this separately to catch "branch" if it is jumped
 	    	for(String key : allitemposhash.keySet()){
 	    		if(jumpreversevec.contains(""+allitemposhash.get(key))){
 	    			ecjumped += ","+key;
 	    			
 	    			if(branchhash.get(allitemposhash.get(key)) != null)
 	    				branchtodelete.addElement(branchhash.get(allitemposhash.get(key)));
  	    		}
 	    	}
 	    	
	    	if(textviews != null){
	    		for(String key : textviews){
	    			if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    				rowhash.put("'"+key+"'", "");
	    				//ecjumped += ","+key;
	    				continue;
	    			}
	    			
	    			if(textviewhash.get(key) == null){
	    				rowhash.put("'"+key+"'", "");
	    				
	    			}
	    			else{
	    				entry = textviewhash.get(key).getText().toString();
	    				entry = entry.replaceAll("\\\\n", " ");
	    				rowhash.put("'"+key+"'", entry); //textviewhash.get(key).getText().toString());
	    				if(primary_keys.contains(key)){
	    					primary_key += ","+key+","+entry;
	    		    		}
	    				}
	    			}
	    		} 
	    	
	    	if(gpstags != null){
	    		for(String key : gpstags){
	    			if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    				rowhash.put("'"+key+"_lon'", "");
	    				rowhash.put("'"+key+"_alt'", "");
	    				rowhash.put("'"+key+"_acc'", "");
	    				rowhash.put("'"+key+"_bearing'", "");
	    				rowhash.put("'"+key+"_provider'", "");
	    				continue;
	    			}
    				rowhash.put("'"+key+"_lat'", gpssettingshash.get(key+"_lat"));
	    				rowhash.put("'"+key+"_lon'", gpssettingshash.get(key+"_lon"));
	    				rowhash.put("'"+key+"_alt'", gpssettingshash.get(key+"_alt"));
	    				rowhash.put("'"+key+"_acc'", gpssettingshash.get(key+"_acc"));
	    				rowhash.put("'"+key+"_bearing'", gpssettingshash.get(key+"_bearing"));
	    				rowhash.put("'"+key+"_provider'", gpssettingshash.get(key+"_provider"));
	    				if(listfields != null && listfields.contains(key) && gpssettingshash.get(key+"_lat").length() > 2){
	    					title += "- "+key+" LAT: " + gpssettingshash.get(key+"_lat")  + " ";
	    					title += " "+key+" LON: " + gpssettingshash.get(key+"_lon")  + " ";
	    					title += " "+key+" ALT: " + gpssettingshash.get(key+"_alt")  + " ";
	    					title += " "+key+" ACC: " + gpssettingshash.get(key+"_acc")  + " ";
	    					title += " "+key+" BEAR: " + gpssettingshash.get(key+"_bearing")  + " ";
	    				}
	    			}

	    		}
	    	 	    	
	    	for(String key : radios){
	    		if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    			rowhash.put("'"+key+"'", "Null");
	    			//ecjumped += ","+key;
	    			continue;
	    		}
	    		
	    		rowhash.put("'"+key+"'", ""+radioselectedhash.get(key));
	    		
	    		String value;
	    		value = radioselectedhash.get(key);
	    			    		
	    		if(listradios != null && listradios.contains(key) && !value.equalsIgnoreCase("")){ //) && match){
					title += "- "+ value + " "; //tempstring[i] + " "; //radioselectedhash.get(key) + " "; // (radiosvalueshash.get(key)[radioselectedhash.get(key)]) + " ";
	    		}
	    		
	    		if(primary_keys.contains(key)){
					primary_key += ","+key+","+value;
		    		}

	         }
	         
	        if(imageviewvalhash != null){
	        	for(String key : imageviewvalhash.keySet()){
	        		if(jumpreversevec.contains(""+allitemposhash.get(key))){
	        			//ecjumped += ","+key;
	        			continue;
	        		}
	        		if(!imageviewvalhash.get(key).equalsIgnoreCase("-1")){
	        			rowhash.put("'"+key+"'", imageviewvalhash.get(key));
	        			imagerowhash.put("id", imageviewvalhash.get(key));
	        		}
		        }
	         }
	         
	         if(videoviewvalhash != null){
		        	for(String key : videoviewvalhash.keySet()){
		        		if(jumpreversevec.contains(""+allitemposhash.get(key))){
		        			//ecjumped += ","+key;
		    				continue;
		        		}
		        		if(!videoviewvalhash.get(key).equalsIgnoreCase("-1")){
		        			rowhash.put("'"+key+"'", videoviewvalhash.get(key));
		        			videorowhash.put("id", videoviewvalhash.get(key));
		        		}
			        }
		         }
	         
	         if(audioviewvalhash != null){
		        	for(String key : audioviewvalhash.keySet()){
		        		if(jumpreversevec.contains(""+allitemposhash.get(key))){
		        			//ecjumped += ","+key;	
		        			continue;
		        		}
		        		if(!audioviewvalhash.get(key).equalsIgnoreCase("-1")){
		        			rowhash.put("'"+key+"'", audioviewvalhash.get(key));
		        			audiorowhash.put("id", audioviewvalhash.get(key));
		        		}
			        }
		         }
	         	    
	    	rowhash.put("ecremote", ""+0);
	    	rowhash.put("ecstored", "N");
	    	
	    	ecjumped = ecjumped.replaceFirst(",", "");
	    	rowhash.put("ecjumped", ecjumped);
	    	
	    	title = title.replaceFirst("- ", "");
	    	
	    	if(title.equalsIgnoreCase(""))
	    		title = this.getResources().getString(R.string.no_title_set); //"No Title Set";

	    	rowhash.put("ectitle", title);
	    	    	
	    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	       	   	
	       	String ecdate = ""+cal.getTimeInMillis();
	       	      	
	       	rowhash.put("ecdate", ecdate);
	       		       	
	       	primary_key = primary_key.replaceFirst(",", "");
	       		 	       	
	       	rowhash.put("ecpkey", primary_key);
	       	rowhash.put("ecfkey", foreign_key);
	       		    		       	
	    	dbAccess.createRow(coretable, rowhash);
	    	
	    	if(imagerowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Image", imagerowhash);
	    	if(videorowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Video", videorowhash);
	    	if(audiorowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Audio", audiorowhash);
	    			    	
	    	if(imageviewvalhash != null){
	        	for(String key : imageviewvalhash.keySet()){
	        		rowhash.put("'"+key+"'", ""+imageviewvalhash.get(key));
	        		imagerowhash.put("id", "'"+imageviewvalhash.get(key)+"'");
		        }
	         }
	         
	         if(videoviewvalhash != null){
		        	for(String key : videoviewvalhash.keySet()){
		        		rowhash.put("'"+key+"'", ""+videoviewvalhash.get(key));
		        		videorowhash.put("id", "'"+videoviewvalhash.get(key)+"'");
			        }
		         }
	         
	         if(audioviewvalhash != null){
		        	for(String key : audioviewvalhash.keySet()){
		        		rowhash.put("'"+key+"'", ""+audioviewvalhash.get(key));
		        		audiorowhash.put("id", "'"+audioviewvalhash.get(key)+"'");
			        }
		         }
	    	
	    	
	    	
	    }
	    
	   
	    private void newEntry(){
	    	resetData();
	    	jumpreversevec.clear();
	    	
	    	flipper.setInAnimation(null);
            flipper.setOutAnimation(null);
	    	while(thispage != 1){
	    		flipper.showPrevious();
	    		thispage--;
	    	}
	    	
	    }
	 	 
		 
	    private void setGPS(){
			String tag = gpsposhash.get(thispage);
			int count = 0;
			double oldaccuracy = 1000;
				
	         
			do{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count++;

	         	if(!accuracy.equalsIgnoreCase("N/A") && Double.parseDouble(accuracy) < oldaccuracy){
	         		gpssettingshash.put(tag+"_lat", latitude); //Double.toString(latitude)); //gpslocation.getLatitude()));
	         		gpssettingshash.put(tag+"_lon", longitude); //Double.toString(longitude)); //gpslocation.getLongitude()));
	         		gpssettingshash.put(tag+"_alt", altitude); //Double.toString(altitude)); //gpslocation.getAltitude()));
	         		gpssettingshash.put(tag+"_acc", accuracy); //Double.toString(accuracy)); //gpslocation.getAccuracy()));
	         		gpssettingshash.put(tag+"_bearing", bearing); //Double.toString(bearing));
	         		gpssettingshash.put(tag+"_provider", provider);
	         		oldaccuracy = Double.parseDouble(accuracy);
	         		
	         		if(oldaccuracy <= 40)
	         			count = 40;
	         	}
			}	
			while (count < 40); 
											  
			mHandler.post(new Runnable() {
             	public void run() { 
             		myProgressDialog.dismiss();
             		if(thispage == lastpage){
        				storeData(3);
        				newEntry();
        			}
        			else{
        				flipToNext();
        			}
             	}
         	});
         
			
		 }
		 
	
	
	 public void addPhoto(String gallery){
		 
		// May be more than one photo for each record so need ID
		String id = imageviewposhash.get(thispage);
		
		String photoid = "-1";
		//String photoview_id = "", photo_refid = "", photo_table = "", sIMEI = "", email = "";
		String date;
		
		imagefile = "0";
		
		try{
			if(imageviewvalhash.get(id) != null){
				photoid = imageviewvalhash.get(id);
				existing_photoid = photoid;
			}
		}
		catch(NullPointerException npe){
			photoid = "-1";
		}
		
	    if(gallery.equalsIgnoreCase("1")){
	    	photoid = photoid.replaceAll("\\s+", "");
	    	if(photoid.equalsIgnoreCase("-1") || photoid.length() == 0){
	    		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    	   	date = ""+cal.getTimeInMillis();
	    		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    		imagefile = coretable+"_"+id+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".jpg";
	    	}
	    	else
	    		imagefile = photoid;
		

			File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
		
			Intent imageCaptureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
			imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			startActivityForResult(imageCaptureIntent, ACTIVITY_CAP_PHOTO);
	    	}
	    
	    else{
	    	Intent intent = new Intent();
	    	intent.setType("image/*");
	    	intent.setAction(Intent.ACTION_GET_CONTENT);
	    	startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_SELECT_PICTURE);
	    }

	 }
	 
	 public void addVideo(){
		 
			// May be more than one video for each record so need ID
			String id = videoviewposhash.get(thispage);
			
			//showAlert(id, "TEST");
			String videoid = "-1";
			try{
			if(videoviewvalhash.get(id) != null)
				videoid = videoviewvalhash.get(id);
			}
			catch(NullPointerException npe){}
			
			Intent i = new Intent(this, Camcorder.class);
		    
			i.putExtras(this.getIntent().getExtras());
			i.putExtra("VIDEOVIEW_ID", id);
			i.putExtra("VIDEO_ID", videoid);
			i.putExtra("VIDEO_TABLE", coretable);
			
		    startActivityForResult(i, ACTIVITY_VIDEO);

		 }

	 public void recAudioCheck(){
		String id = audioviewposhash.get(thispage);
		
		try{
			if(audioviewvalhash.get(id) != null && !audioviewvalhash.get(id).equalsIgnoreCase("-1")){
				showAudioAlert();
				return;
			}

		}
		catch(NullPointerException npe){
			
		}
		recAudio();
		
	 }
		
	 public void recAudio(){
		 
		 audioactive = true;
		 
		 audiodir = Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + dbAccess.getProject();
		 
		if(!checkAudioDirectory(audiodir))
			 return;
		 
		// May be more than one photo for each record so need ID
		String id = audioviewposhash.get(thispage);
			
		String audioid = "-1";
		try{
			if(audioviewvalhash.get(id) != null)
				audioid = audioviewvalhash.get(id);
			}
		catch(NullPointerException npe){}
			
		if(audioid.equalsIgnoreCase("-1")){
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    	   	
	    	String date = ""+cal.getTimeInMillis();
	    	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	audioid = coretable+"_"+id+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".mp4";
	    	
	    	
		}
		try{
			recorder.record(audiodir+"/"+audioid, audiotextviewhash.get(audioviewposhash.get(thispage)), audiorecordbuttonhash.get(audioviewposhash.get(thispage)), audioplaybuttonhash.get(audioviewposhash.get(thispage)), audiostopbuttonhash.get(audioviewposhash.get(thispage)));
			
		}
		catch(Exception e){
			audioactive = false;
			return;
			
		}
		
		audioviewvalhash.put(id, audioid);

	}
	 
	 public void playAudio(){
		 audioactive = true;
		 
		 audiodir = Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + dbAccess.getProject();
		 String audioid = audioviewvalhash.get(audioviewposhash.get(thispage));
		 
		try{
			recorder.play(this, audiodir+"/"+audioid, audiotextviewhash.get(audioviewposhash.get(thispage)), audioplaybuttonhash.get(audioviewposhash.get(thispage)), audiorecordbuttonhash.get(audioviewposhash.get(thispage)), audiostopbuttonhash.get(audioviewposhash.get(thispage)));
			}
		catch(Exception e){
			audioactive = false;
		}		
		
		//audioactive = false;
	 }
	 
	 public void stopAudio(){
		 audioactive = false;
		 try{
			 recorder.stop(audiotextviewhash.get(audioviewposhash.get(thispage)));
		 }
		 catch(Exception e){}
	 }
	 
	 private boolean checkAudioDirectory(String audiodir){
		  try{
			  File f = new File(audiodir);
			  if(!f.exists()){
				  f.mkdir();
			  }
		  }
		  catch(Exception e){
			  return false;
		  }
		  
		  return true;
	 }
	 
	public void updateData(Bundle extras, int type){
		
		// NEED TO ADD VIDEO HERE
		
		if(type == 1){
			String photoviewid = imageviewposhash.get(thispage), photoid = imagefile;
		
			if(photoid != null && !photoid.endsWith("-1")){
				
				try{
					
					// Need to use bitmap to get the image to update
					Bitmap bmp = BitmapFactory.decodeFile(thumbdir+"/"+photoid);
					imageviewhash.get(photoviewid).setImageBitmap(bmp);
					imageviewvalhash.put(photoviewid, photoid);
					photobuttonhash.get(photoviewid).setText("Tap to Update Photo");
					
					imageviewhash.get(photoviewid).setOnClickListener(new View.OnClickListener() {
        		    	public void onClick(View arg0) {
        		    		
        		          	showImage();
        		        }
        		           
        		    });
				}
				catch(Exception e){
				}
			}
		}
		
		else if(type == 2){
			String videoviewid = "", videoid = "";
			if(extras != null){
				videoviewid = extras.getString("VIDEOVIEW_ID");
				videoid = extras.getString("VIDEO_ID");
			}
			else{
				videoviewid = videoviewposhash.get(thispage);
				videoid = videoviewposhash.get(thispage);
			}
			
			if(videoid != null && !videoid.endsWith("-1")){
				try{
					// This will work in 2.2
					//Bitmap bm = ThumbnailUtils.createVideoThumbnail(videodir+"/"+videoid, ThumbnailUtils.MINI_KIND);
					Bitmap bm = getVideoFrame(videodir+"/"+videoid);
					videoviewhash.get(videoviewid).setImageBitmap(bm);
					
					videoviewvalhash.put(videoviewid, videoid);
					videobuttonhash.get(videoviewid).setText("Tap to Update Video");
					
					videoviewhash.get(videoviewid).setOnClickListener(new View.OnClickListener() {
        		    	public void onClick(View arg0) {
        		    		
        		          	playVideo();
        		        }
        		           
        		    });
					
				}
				catch(Exception e){
				}
			}
		}

	}
	
	private boolean checkEntry(){ //int page){
		
		// If this is a second check on the primary key need to check if the key has changed
		if(secondkeycheck){
			String tempkey = getCurrentKey(); //"";
    				
    		if(dbAccess.checkValue(coretable, "ecpkey", tempkey)){
    			// If the key has changed since the last check it needs to be checked again so reset secondcheck
        		if(!currentkey.equalsIgnoreCase("") && !tempkey.equalsIgnoreCase(currentkey)){
        			secondcheck = false;
        		}
    			
    		}
		}		
				
		if(secondcheck) // || fromdetails)
			return true;
		
		secondkeycheck = false;
		
    	String key;
    	
    	boolean noerrors = true; //, keyexists = false;
    	if((textviewposhash.get(thispage) != null && primary_keys.contains(textviewposhash.get(thispage))) || 
    			(radioposhash.get(thispage) != null && primary_keys.contains(radioposhash.get(thispage)))){
    		
    		String tempkey = getCurrentKey(); 
	
    		if(dbAccess.checkValue(coretable, "ecpkey", tempkey)){
    			if(dbAccess.getValue("change_synch").equalsIgnoreCase("false") && dbAccess.getValue(coretable, "ecstored", tempkey).equalsIgnoreCase("R")){
    				return false;
    			}
    			else{
    				showCheckAlert(this.getResources().getString(R.string.entry_exists_1)+".<br>"+this.getResources().getString(R.string.entry_exists_2)); // "Entry exists for this primary key "+primary_key+".<br>Any changes to record cannot be undone"
    				secondcheck = true;
    				noerrors = false; 	
    				secondkeycheck = true;
    			}
    		}
    		
    		currentkey = tempkey;
    	
		}
    	
    	if(imageviewposhash.get(thispage) != null){
    		key = imageviewposhash.get(thispage);
			if(requiredfields.contains(key) && ((imageviewvalhash.get(key) == null) || (imageviewvalhash.get(key).equalsIgnoreCase("-1")))){
				showCheckAlert(this.getResources().getString(R.string.image_required)); //"WARNING: Picture is required");
				secondcheck = true;
				noerrors = false;
			}
		}
		
    	if(videoviewposhash.get(thispage) != null){
    		key = videoviewposhash.get(thispage);
			if(requiredfields.contains(key) && ((videoviewvalhash.get(key) == null) || (videoviewvalhash.get(key).equalsIgnoreCase("-1")))){
				showCheckAlert(this.getResources().getString(R.string.video_required)); //"WARNING: Video is required");
				secondcheck = true;
				noerrors = false;
			}
		}
    	
    	if(audioviewposhash.get(thispage) != null){
    		key = audioviewposhash.get(thispage);
			if(requiredfields.contains(key) && ((audioviewvalhash.get(key) == null) || (audioviewvalhash.get(key).equalsIgnoreCase("-1")))){
				showCheckAlert(this.getResources().getString(R.string.audio_required)); //"WARNING: Audio is required");
				secondcheck = true;
				noerrors = false;
			}
		}
    	
    	if(radioposhash.get(thispage) != null){
    		key = radioposhash.get(thispage);
			if(radioselectedhash.get(key).equals("")){
				if(primary_keys.contains(key)){
       				showCheckAlert(this.getResources().getString(R.string.key_required_1)+" "+ key+".<br>"+this.getResources().getString(R.string.key_required_2)); // "Value required for primary key field "+ key+".<br>Entry cannot be stored"
        			secondcheck = true;
        			noerrors = false;
        		}
				else if(requiredradios.contains(key)){
					showCheckAlert(this.getResources().getString(R.string.value_required)); //"WARNING: Value is required");
					secondcheck = true;
					noerrors = false;
				}
			}
		}
		
    	return noerrors;
  
	}
	
	private String getCurrentKey(){
		String tempkey = "";
		
    	for(String pkey : radios){	    		
	    	if(primary_keys.contains(pkey)){
				tempkey += ","+pkey+","+radioselectedhash.get(pkey);
		    	}
    	}
    	tempkey = tempkey.replaceFirst(",", "");
    	return tempkey;
	}
	
	public void showCheckAlert(String result){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.invalid_entry); //"Invalid Entry")
        alert.setMessage(Html.fromHtml(result+".<br><br>"+this.getResources().getString(R.string.next_to_continue))); //Tap Next again to continue"))
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // OK

             public void onClick(DialogInterface dialog, int whichButton) {
            	
             }
        });
       
        alert.show();	
    }
	
	public void showAudioAlert(){
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.warning); //"Warning")
        alert.setMessage(Html.fromHtml(this.getResources().getString(R.string.audio_stored)+"<br>"+this.getResources().getString(R.string.continue_question))); // "Audio recording stored. This will be overwritten.<br>Continue?"
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // OK

             public void onClick(DialogInterface dialog, int whichButton) {
            	recAudio();
             }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // No

             public void onClick(DialogInterface dialog, int whichButton) {
             }
        });
        alert.show();	
    }

	 
	//@Override
	public void onLocationChanged(Location location) {

		oldtime = newtime;
   		newtime = location.getTime(); //gps_time;

        latitude = Double.toString(location.getLatitude());
        longitude = Double.toString(location.getLongitude());
        if(location.hasAltitude())
        	altitude = Double.toString(location.getAltitude());
        else
        	altitude = "N/A";

        if(location.hasAccuracy() && location.getAccuracy()  != 0.0)
        	accuracy = Double.toString(location.getAccuracy());
        else
        	accuracy = "N/A";
        if(location.hasBearing())
        	bearing = Double.toString(location.getBearing());
        else
        	bearing = "N/A";
        provider = location.getProvider();

	}

	//@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private void playVideo(){
		
		Intent i = new Intent(this, VideoPlayer.class);
		
		String id = videoviewposhash.get(thispage);
		
		i.putExtra("VIDEO_ID", videoviewvalhash.get(id));
		i.putExtras(this.getIntent().getExtras());
		
	    startActivity(i);
	
	}
	
	private void showImage(){
		
		try{
			Intent i = new Intent(this, ImageViewer.class);
		
			String id = imageviewposhash.get(thispage);
			i.putExtra("IMAGE_ID", imageviewvalhash.get(id));
		
			startActivity(i);
		}
		catch(Exception e){}
	
	}
	
	// This came from http://stackoverflow.com/questions/1334694/android-is-it-possible-to-display-video-thumbnails
	private Bitmap getVideoFrame(String uri) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
 
        try {
            retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);
            retriever.setDataSource(uri);
            return retriever.captureFrame();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }  catch (NoSuchMethodError nsme){
       	 return ((BitmapDrawable)(this.getResources().getDrawable(R.drawable.video1))).getBitmap();
        }finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
            }
        }
        return null;
    }

	
	//@TargetApi(Build.VERSION_CODES.ECLAIR)
	//@SuppressLint("NewApi")
	private void createThumbnail(){
	   	File file = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
	   	
	    // If cancel on camera pressed
	   	if(!file.exists()){
	   		return;
	   	}
	   	
	   	try{
	       	File f = new File(thumbdir);
	    	if(!f.exists()){
	    		f.mkdir();
	    	}
	    	f = new File(picdir);
	    	if(!f.exists())
	    		f.mkdir();
	    	}
	    catch(Exception e){
	    }

	   	int torotate = 0;
		try {
			switch (new ExifInterface(file.getPath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				torotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				torotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				torotate = 270;
				break;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	   	
	   	copyFile(file, new File(picdir+"/"+imagefile));
	      	
	    try{
	    	file.delete();
	    }
	    catch (Exception e){
	    	Log.i("Create Thumbnail","Image Delete Fail") ;
	    }

	    try {
	      	// load the original BitMap (500 x 500 px)
	       	// This sometimes causes out of memory errors
	       	//Bitmap bmp = BitmapFactory.decodeFile(picdir+"/"+imagefile);
	       	
	       	// Used this instead
	       	
	       	BitmapFactory.Options options = new BitmapFactory.Options();
	       	options.inSampleSize = 8;
	       	Bitmap bmp = BitmapFactory.decodeFile(picdir+"/"+imagefile, options);
	       				
	       	int width = bmp.getWidth();
	       	int height = bmp.getHeight();
	       	int newWidth, newHeight;
	       		        
	       	if(width > height){
	       	  	newWidth = 512;
	       	   	newHeight = 384;
	       	}
	       	else{
	       	  	newWidth = 384;
	       	   	newHeight = 512;
	       	}
	       		       
	       	// calculate the scale - in this case = 0.4f
	       	float scaleWidth = ((float) newWidth) / width;
	       	float scaleHeight = ((float) newHeight) / height;
	       		       
	       	// create a matrix for the manipulation
	       	Matrix matrix = new Matrix();
	       	// resize the bit map
	       	matrix.postScale(scaleWidth, scaleHeight);
	       	// rotate the Bitmap
	       	//matrix.postRotate(45);

	       	// recreate the new Bitmap
	       	Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true); 
	       	
	      //rotate
	       	matrix = new Matrix();
			width = torotate == 0 || torotate == 180 ? resizedBitmap.getWidth() : resizedBitmap.getHeight();
			float scale = 0.8f * getWindowManager().getDefaultDisplay().getWidth() / width;
			matrix.setScale(scale, scale);
			matrix.postRotate(torotate);
			resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
			
	       	FileOutputStream out = new FileOutputStream(thumbdir+"/"+imagefile);//this.openFileOutput("ping_media.jpg",MODE_PRIVATE);
	       	resizedBitmap.compress(CompressFormat.JPEG, 50, out) ;
	       	out.close() ;
	       	
	       	// Free up the memory used by the bitmaps
	       	// Without this can get out of memory exception when 
	       	// subsequent photo taken
	       	bmp.recycle();
	       	resizedBitmap.recycle();
	       	
	       	//media_path = "/sdcard/dcim/.thumbnails/" ;
	    } catch (FileNotFoundException e) {
	    	Log.i("ImageSwitcher","FileNotFoundException generated when using camera") ;
	    } catch (IOException e) {
	    	Log.i("ImageSwitcher","IOException generated when using camera") ;
	    }

	}
	
	private void copyFile(File f1, File f2){ //String srFile, String dtFile){
	    try{

	      InputStream in = new FileInputStream(f1);

	      OutputStream out = new FileOutputStream(f2);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
	    }
	    catch(FileNotFoundException ex){
	    	Log.i("ImageSwitcher", ex.toString());
	    }
	    catch(IOException e){
	    	Log.i("ImageSwitcher", e.toString());
	    }
	  }
	   
}

