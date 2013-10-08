package uk.ac.imperial.epi_collect2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import uk.ac.imperial.epi_collect2.media.audio.AudioRecorder;
import uk.ac.imperial.epi_collect2.media.camera.Camcorder;
import uk.ac.imperial.epi_collect2.media.camera.ImageSwitcher_epi_collect;
import uk.ac.imperial.epi_collect2.media.camera.ImageViewer;
import uk.ac.imperial.epi_collect2.media.camera.VideoPlayer;
import uk.ac.imperial.epi_collect2.util.barcode.IntentIntegrator;
import uk.ac.imperial.epi_collect2.util.barcode.IntentResult;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
//import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListAdapter;
//import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;
import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.AdapterView.OnItemClickListener;

//@SuppressLint("NewApi")
//@SuppressLint("NewApi")
//@SuppressLint("NewApi")
@SuppressLint("NewApi")
public class EntryNote extends Activity implements LocationListener {
 
	//private static final int ACTIVITY_PHOTO=1;
	private static final int ACTIVITY_VIDEO=2;
	private static final int ACTIVITY_BRANCH=3;
	private static final int ACTIVITY_BRANCH_LIST=4;
	private static final int ACTIVITY_LIST=5;
	private static final int ACTIVITY_CAP_PHOTO=6;
	private static final int ACTIVITY_SELECT_PICTURE=7;
	//private static final int ACTIVITY_AUDIO=3;
	private static final int DATE_DIALOG_ID = 0;
	private static final int TIME_DIALOG_ID = 1;
	private ArrayAdapter<String> aspnLocs; 
	private Hashtable<String, EditText> textviewhash;
	private Hashtable<String, ImageView> imageviewhash;
	private Hashtable<String, String> imageviewvalhash;	
	private Hashtable<String, ImageView> videoviewhash;
	private Hashtable<String, String> videoviewvalhash; // = new Hashtable<String, String>();
	//private Hashtable<String, ImageView> audioviewhash;
	private Hashtable<String, String> audioviewvalhash;
	private Hashtable<Integer, String> textviewposhash, spinnerposhash, radioposhash, checkboxposhash, imageviewposhash, videoviewposhash, audioviewposhash, gpsposhash;
	private Hashtable<String, Integer> allitemposhash;
	private Hashtable<Integer, String> allitemposhashrev;
	private Hashtable<String, Spinner> thisspinnerhash;
	private Hashtable<String, LinkedHashMap<String, RadioButton>> thisradiohash; 
	private Hashtable<Integer, RadioGroup> jumpradiohash;
	private Hashtable<String, String> radioselectedhash = null;
	private Hashtable <String, ArrayList<String>>spinnershash = new Hashtable <String, ArrayList<String>>();
	//private static Hashtable <String, String[]>spinnersvalueshash = new Hashtable <String, String[]>();
	//private Hashtable <String, ArrayList<String>>radioshash = new Hashtable <String, ArrayList<String>>();
	//private static Hashtable <String, String[]>radiosvalueshash = new Hashtable <String, String[]>();
	private static Hashtable <String, String[]>totals = new Hashtable <String, String[]>();
	private static Hashtable <String, String>jumps; // = new Hashtable <String, String>();
	private static Hashtable <String, String>jumps1; // = new Hashtable <String, String>();
	private static Hashtable <String, String>jumps2; // = new Hashtable <String, String>();
	private static Hashtable <String, String>groups; // = new Hashtable <String, String>();
	private static Hashtable <String, String>groupkeyhash;
	private static Hashtable <String, String>dates; // = new Hashtable <String, String>();
	private static Hashtable <String, String>setdates; // = new Hashtable <String, String>();
	private static Hashtable <String, String>settimes; // = new Hashtable <String, String>();
	private static Hashtable <String, String>crumbs; // = new Hashtable <String, String>();
	private static Hashtable <String, String>res; // = new Hashtable <String, String>();
	private static Hashtable <String, String>matches; // = new Hashtable <String, String>();
	private static Hashtable <String, String>def_vals; // = new Hashtable <String, String>();
	private static Hashtable <String, String>mincheck; // = new Hashtable <String, String>();
	private static Hashtable <String, String>maxcheck; // = new Hashtable <String, String>();
	private static Hashtable <String, String>mincheck2; // = new Hashtable <String, String>();
	private static Hashtable <String, String>maxcheck2; // = new Hashtable <String, String>();
	private Hashtable <Integer, String>branchhash = new Hashtable <Integer, String>();
	private Hashtable <Integer, TextView>branchtvhash = new Hashtable <Integer, TextView>();
	private LinkedHashMap<String, CheckBox> checkboxhash;
	private Vector<String> doubles = new Vector<String>();
	private Vector<String> integers = new Vector<String>();
	private Vector<String> uppercase = new Vector<String>();
	private Vector<String> doublecheck = new Vector<String>();
	private Vector<String> nodisplay = new Vector<String>();
	private Vector<String> radioimages = new Vector<String>();
	private Vector<String> noteditable = new Vector<String>();
	private Vector<String> barcodes = new Vector<String>();
	private Vector<String> primary_keys = new Vector<String>();
	//private Hashtable<String, String> foreign_keys = new Hashtable<String, String>();
	private static String[] textviews = new String[0];
    private static String[] spinners = new String[0];
    private static String[] radios = new String[0];
    private static String[] checkboxes = new String[0];
    // Has to be a Vector so gpstagstoskip can be set easily when jumps made
    private static Vector<String> gpstags = new Vector<String>();
    private static String[] photos = new String[0];
    private static String[] videos = new String[0];
    private static String[] audio = new String[0];
    //private static String[] title = new String[0];
    private static Vector<String> requiredfields=new Vector<String>(), requiredspinners=new Vector<String>(), requiredradios=new Vector<String>(), storedrequiredfields=new Vector<String>(), storedrequiredspinners=new Vector<String>(), storedrequiredradios=new Vector<String>();
	private int alldata = 0;
	private static final int RESET_ID = 1;
	private static final int CHANGE_GPS = 2;
	private static final int HOME = 3;
	private static final int SAVE_RECORD = 4;
	private static final int HIERARCHY = 5;
	private ViewFlipper flipper;
	private TextView tv, tv2, pagetv;
	private EditText et, et2;
	private Spinner spin;
	private CheckBox cb;
	private DBAccess dbAccess;
	//private boolean canupdate = true;
	private Button confirmButton, gpsButton, photoButton, videoButton; //, audioButton;
	private String coretable; //, coretableview; //, coretablekey;
	private Hashtable<Integer, EditText> doublecheckhash, doublecheckhash2, dateshash;
	private Hashtable<Integer, String[]> totalshash;
	private Vector<String> jumpreversevec = new Vector<String>();
	private Hashtable<Integer, String> dateshashformat;
	// The lis... vectors was set as static. Not sure why but caused problem with title so removed
	private Vector<String> listfields=new Vector<String>(), listspinners=new Vector<String>(), listradios=new Vector<String>(), listcheckboxes=new Vector<String>();
	private Vector<String>gpstagstoskip = new Vector<String>();
	private String[] allviews = new String[0];
	private int lastpage = 0, thispage = 1;
	private Hashtable<String, String> spinnervalshash = new Hashtable<String, String>();
	
	private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private GestureDetector gestureDetector;
    
    //LinkedHashMap <String, Integer> spinhash = new LinkedHashMap<String, Integer>();
    LinkedHashMap <String, String> spinhash = new LinkedHashMap<String, String>();
    //HashMap<String, String> keytablehash = new HashMap<String, String>();
    private String primary_key = "", title = "", lat="0", lon="0", alt="", gpsacc="";  // keytable = "", keyvalue = "", 
    private boolean newentry = true, usesgps = false; //, gpsset = false;
    private LocationManager locationManager; 
    //private LocationProvider IP;
    //private boolean fromlist = false; //, isstored = false;
    private String select_table, list_select_table, foreign_key = "", detailstarget = "", thumbdir, picdir, existing_photoid = "-1", imagefile, videodir, audiodir;
    private boolean stored = false, fromdetails = false;
    private ImageButton bn, bp;
    private int isnew = 1;
    private Hashtable<String, Boolean> gpssethash = new Hashtable<String, Boolean>();
    private Hashtable<String, Button> gpsbuttonhash = new Hashtable<String, Button>();
    private Hashtable<String, String> gpssettingshash = new Hashtable<String, String>(); 
    private boolean secondcheck = false, secondkeycheck = true; //, validentry = true 
    private Bundle setextras;
    private String latitude = "0", longitude = "0", altitude = "N/A", bearing =  "N/A", accuracy = "N/A";
    private double project_version = 1.0;
    private String provider = "";
    //private long gps_time = 0;
    private long oldtime = 0, newtime = 0;
    //private int gps_count;
    private Handler mHandler; 
    private String gps_message;
    private Hashtable<String, Button> photobuttonhash = new Hashtable<String, Button>(), videobuttonhash = new Hashtable<String, Button>(), audiobuttonhash = new Hashtable<String, Button>();
    private Hashtable<String, ImageButton>audioplaybuttonhash, audiorecordbuttonhash, audiostopbuttonhash;
    private Hashtable<String, TextView>audiotextviewhash;
    private Vector<String> hiddentextviewkeys = new Vector<String>();
    private AudioRecorder recorder;
    public boolean audioactive = false;
    private String genkey = "";
    private boolean multitable = false;
    private Vector<TextView> gpstvwarnvec;
    private boolean fromlist = false, phonekeyset = false;
    private int phonekey;
    //private long timems = 0;
    //private String time = "";
    //private MyLocationListener mlocListener;
    //private Menu optionsmenu;
    //private String pkey = "";
    // For John
    //Spinner countryspin, regionspin, cerclespin, communespin, villagespin;
    //EditText countrytext, regiontext, cercletext, communetext, villagetext;
    EditText selectedtext;
    int selectedpage; 
    //String coretablekey;
    HashMap<String, Spinner> groupspinhash = null;
	HashMap<String, EditText> grouptexthash = null;
	String project, currentkey = "";
	//private LinkedHashMap<String, RadioButton> radiohash;
	private int isbranch = 0;
	private boolean resetting_radiobuttons = false, canedit = true, flipping = false, hasbranch = false;
	private Vector <RadioGroup>radiogroup_vec = new Vector<RadioGroup>();

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        //super.setTitle(this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/app_name", null, null))); 
        
		boolean change_synch = true;
		
		dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    project = dbAccess.getProject();
	    super.setTitle("EpiCollect+ "+project);
	    
	    Bundle extras = getIntent().getExtras();
	    setextras = extras;
	    
	    thumbdir = Epi_collect.appFiles+"/"+project+"/thumbs"; // + this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	    picdir = Epi_collect.appFiles+"/"+project+"/images"; //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + project; //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
	    videodir = Epi_collect.appFiles+"/"+project+"/videos"; //Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + project;
	    audiodir = Epi_collect.appFiles+"/"+project+"/audio"; //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + project;
	        
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
		    	fromdetails = true;
		    }
		    if(isnew == 0){
		    	primary_key = extras.getString("primary_key");
		    	
		    }
	    }

	    if (extras != null){
		   	for(String key : dbAccess.getKeys().keySet()){
		   		spinhash.put(key, extras.getString(key));
		   		//Log.i("IN EXTRAS " +key, extras.getString(key));
		   	}
		}
	    
		  //coretablekey = dbAccess.getKeyValue(coretable);
		  
		  // This may not be needed
		  if(isnew == 0){
		    	newentry = false;
		  }
		  
	    
		    if(detailstarget.length() > 0){
		    	fromdetails = true;
		    	
		    }

	    
	    // For John
	    //coretablekey = dbAccess.getKeyValue(coretable);
	    	
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
		
		if(dbAccess.getValue("change_synch").equalsIgnoreCase("false"))
			change_synch = false;
		//Log.i("VERSION", ""+project_version);
		
		if(fromdetails){
	    	if(!change_synch && extras.getString("status").equalsIgnoreCase("R"))
	    		canedit = false;	
	    }
		
	    setContentView(setLayout(coretable, extras)); 
	    
	    if(icicle != null){
	    		    	
	    	flipper.setDisplayedChild(0); 
		    int targetpage = thispage;
		    thispage = 1;
		    try{
		    	if(targetpage > 1){
		    		flipper.setInAnimation(null);
		            flipper.setOutAnimation(null);
		            // Prevent record saving as it is flipping otherwise "R" set to "N"
		            flipping = true;
		            flipTo(allitemposhashrev.get(targetpage));
		            flipping = false;
		    		//while(thispage < targetpage){
		    		//	flipper.showNext();
		    		//	thispage++;
		    		//}
		    	}
		    }
		    catch(NullPointerException npe){}
	    }
        	
	    if(fromdetails){
	    	// Prevent record saving as it is flipping otherwise "R" set to "N"
	    	flipping = true;
	    	flipTo(extras.getString("target"));
	    	flipping = false;
	    }
	    
	    if(dbAccess.getKeys().keySet().size() > 1)
    		multitable = true;
	        	    
		
	}

	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    //menu.add(R.string.menu_reset);
	    //menu.add(R.string.menu_home);
	    //return super.onCreateOptionsMenu(menu);
	    //optionsmenu = menu;
	    menu.add(0, RESET_ID, 0, R.string.menu_reset);
	    //if(usesgps){
	    //	menu.add(0, CHANGE_GPS, 0, R.string.menu_change_gps);
	    //}
	    menu.add(0, SAVE_RECORD, 0, R.string.menu_save);
	    menu.add(0, HOME, 0, R.string.menu_home);
	    menu.add(0, HIERARCHY, 0, R.string.menu_hierarchy);
	    
	    return true;
	}*/
	
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
    		// If it is from details or list the record has already been saved so no check is needed
    		if(fromdetails || fromlist){
    			dbAccess.close();
        		Intent i = getIntent();
        		i.putExtras(setextras);
    			setResult(RESULT_OK, i);
    	        finish();
    		}
    		else
    			confirmBack(); //event);
    	}
    	// Menu button doesn't work by default on this view for some reason
    	if(keyCode == KeyEvent.KEYCODE_MENU)
    		openOptionsMenu();

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
	   				copyFile(new File(fpath), new File(Epi_collect.appFiles+"/"+project, "temp.jpg"));
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
	    case ACTIVITY_LIST:
	    	if (dbAccess == null) {
	        	dbAccess = new DBAccess(this);
	        	dbAccess.open();
	        }
	    	fromlist = true;
	    	break;
	    case ACTIVITY_BRANCH:
	    	//Log.i("CORETABLE", coretable);
	    	//Log.i("KEY", primary_keys.elementAt(0));
	    	//Log.i("VALUE", textviewhash.get(primary_keys.elementAt(0)).getText().toString());
	    	if (dbAccess == null) {
	        	dbAccess = new DBAccess(this);
	        	dbAccess.open();
	        }
	    	// Need to reset this as it seems to be the branch values otherwise
	    	// Need to find out why
	    	getValues(coretable);
	    	//Log.i("CALLER", extras.getString("BRANCH_FORM"));
	    	//int entries = dbAccess.getBranchCount(coretable, primary_keys.elementAt(0), textviewhash.get(primary_keys.elementAt(0)).getText().toString());
	    	String branch_key = "";
			for(String key: primary_keys){
				branch_key += ","+key +","+ textviewhash.get(key).getText().toString();
				// A branch record has now been added so the primary key can no longer be edited
				textviewhash.get(key).setFocusable(false);
			}
			//Log.i("BRANCH KEY", branch_key);
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
	    case IntentIntegrator.REQUEST_CODE: {
	           if (resultCode != RESULT_CANCELED) {
	               	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
	               if (scanResult != null) {
	                   String upc = scanResult.getContents();
	                   // Do whatever you want with the barcode...
	                   String tag = gpsposhash.get(thispage); 	
	       		 	   EditText gpstext = textviewhash.get(tag);
	       		 	   gpstext.setText(upc);
	       		 	   if(doublecheck.contains(tag)){
	       		 		   gpstext = doublecheckhash2.get(thispage);
	       		 		   gpstext.setText(upc);
	       		 	   }
	               }
	           }
	           break;
	    	}
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
    
    private void confirmBack(){ //KeyEvent event){
    	
    	if(dbAccess.getValue("change_synch").equalsIgnoreCase("false") && dbAccess.getValue(coretable, "ecstored", getCurrentKey()).equalsIgnoreCase("R")){
			endActivity();
			return;
		}
    	
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(R.string.warning); //"Warning");
        
    	boolean keycomplete = true;
    	for(String key : textviews){
    		//if(primary_keys.contains(key))
    		//	Log.i("P KEY CHECK", key+ "  "+textviewhash.get(key).getText().toString());
			if(primary_keys.contains(key) && ((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase("")))){
				keycomplete = false;
			}
    	}
		
    	if(keycomplete){
        	alertDialog.setMessage(this.getResources().getString(R.string.save_current_data)); //Html.fromHtml("Save current data?<br><br>If No selected record will be deleted"));
        	alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){ // Yes
        		public void onClick(DialogInterface dialog, int whichButton) {
        			endActivity();
        			return;
        			//checkPrimaryKeyExists();
        			/*if(usesgps)
            			removeGPSUpdates();
            		// It's from a branch
            		if(isbranch == 1){ //isnew == 2){
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		}
            		else if(!fromdetails){
            			selectTable(false); // false
            		}
            		else{
            			Bundle extras = getIntent().getExtras();
            			getIntent().putExtras(extras);
            			extras.putString("target", extras.getString("target"));
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		} */
        		}
        	});
        	
            alertDialog.setButton2(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener(){ // Cancel
            	public void onClick(DialogInterface dialog, int whichButton) {
            		return;
            	}
            });
        
            alertDialog.setButton3(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){ // No
            	public void onClick(DialogInterface dialog, int whichButton) {
            		deleteEntry();
            		stored = false;
            		endActivity();
            		return;
            		/*if(usesgps)
            			removeGPSUpdates();
            		// It's from a branch
            		if(isbranch == 1){ //isnew == 2){
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		}
            		else if(!fromdetails){
            			selectTable(false); // false
            		}
            		else{
            			Bundle extras = getIntent().getExtras();
            			getIntent().putExtras(extras);
            			extras.putString("target", extras.getString("target"));
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		}*/
            	}
            });
        }
    	else{
    		String keyerror = "";
    		for(String key : textviews){
	    		if(primary_keys.contains(key)){
	    			keyerror += ","+key;
		    	}
			}
    		keyerror = keyerror.replaceFirst(",", "(");
    		keyerror += ")";
    		
    		alertDialog.setMessage(this.getResources().getString(R.string.cannot_save_entry_1)+ " "+keyerror+" "+ this.getResources().getString(R.string.cannot_save_entry_2)); // "Entry cannot be saved, primary key "+keyerror+" incomplete.\n\nContinue without saving?");
        	alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){ // Yes
        		public void onClick(DialogInterface dialog, int whichButton) {
        			endActivity();
        			return;
        			/*if(usesgps)
        				removeGPSUpdates();
        			// It's from a branch
            		if(isbranch == 1){ //isnew == 2){
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		}
            		else if(!fromdetails){
                		selectTable(false); // false
                		}
                		else{
                			Bundle extras = getIntent().getExtras();
                			getIntent().putExtras(extras);
                			extras.putString("target", extras.getString("target"));
                			setResult(RESULT_OK, getIntent());
                			dbAccess.close();
                			finish();
                		}*/
        		}
        	});
        	
            alertDialog.setButton2(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){ // No
            	public void onClick(DialogInterface dialog, int whichButton) {
            		return;
            	}
            });
    	}
       alertDialog.show();	
	}
    
    private void endActivity(){
    	if(usesgps)
			removeGPSUpdates();
		// It's from a branch
		if(isbranch == 1){ //isnew == 2){
			setResult(RESULT_OK, getIntent());
			dbAccess.close();
			finish();
		}
		else if(!fromdetails){
			selectTable(false); // false
		}
		else{
			Bundle extras = getIntent().getExtras();
			getIntent().putExtras(extras);
			extras.putString("target", extras.getString("target"));
			setResult(RESULT_OK, getIntent());
			dbAccess.close();
			finish();
		}
    }
    
    private void deleteEntry(){
    	String primary_key = "";
    	for(String key : textviews){
    			if(primary_keys.contains(key)){
				primary_key = key+","+textviewhash.get(key).getText().toString();
			}
    	}
    	dbAccess.deleteRow(coretable, "ecpkey", primary_key);
    }
    
    /*private void confirmChangeGPS(){ //KeyEvent event){
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle("Warning");
        alertDialog.setMessage("Change GPS Setting?");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener(){
             public void onClick(DialogInterface dialog, int whichButton) {
            	setGPS();
             }
        });
        
       alertDialog.setButton3("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
            	return;
            }
       });
       
       alertDialog.show();	
	}*/
    
    private void removeGPSUpdates(){
    	if(locationManager != null){
			locationManager.removeUpdates(this);
			//mlocListener.removeUpdates();
    	}	
    }
    
    private void confirmChangeGPS2(){ //KeyEvent event){
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(this.getResources().getString(R.string.warning)); //"Warning");
        alertDialog.setMessage(this.getResources().getString(R.string.change_location)); //"Change Location Setting?");
        alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){ // Yes
             public void onClick(DialogInterface dialog, int whichButton) {
            	setGPS();
             }
        });
        
       alertDialog.setButton3(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){ // No
            public void onClick(DialogInterface dialog, int whichButton) {
            	return;
            }
       });
       
       alertDialog.show();	
	}
    
    private void confirmReset(){ //KeyEvent event){
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(this.getResources().getString(R.string.warning)); //"Warning");
        alertDialog.setMessage(this.getResources().getString(R.string.clear_data)); //Html.fromHtml("This will clear all data for this entry.<br><br>Continue?"));
        alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){ // Yes
             public void onClick(DialogInterface dialog, int whichButton) {
            	 resetData();
             }
        });
        
        alertDialog.setButton2(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){ // No
            public void onClick(DialogInterface dialog, int whichButton) {
            	return;
            }
       });
        
       alertDialog.show();	
	}
    
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	super.onSaveInstanceState(outState);  
           
    	Log.i("LIFECYCLE", "onSaveInstanceState");
    	for(String key : textviews){
    		try{
    			outState.putString(key, textviewhash.get(key).getText().toString());
    		}
    		catch(NullPointerException npe){
    			outState.putString(key, "");
    		}
    	   	}
    	
        for(String key : spinners){
        	outState.putInt(key, thisspinnerhash.get(key).getSelectedItemPosition());
        }
        
        for(String key : radios){
        	outState.putString(key, radioselectedhash.get(key));
        }
        
        for(String key : checkboxes){
        	if(checkboxhash.get(key).isChecked()){
        		outState.putBoolean(key, true);
        	}
        	else{
        		outState.putBoolean(key, false);
        	}
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
	  
	  // NEED TO FILL IN DATA FIELDS HERE
	    
	    if(detailstarget.length() > 0){
	    	fromdetails = true;
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
	    
	   
	   /* if(primary_key.length() > 1){
	    	
	    	if(textviews != null){
	    		for(String key : textviews){
	    			textviewhash.get(key).setText(dbAccess.getValue(coretable, key, primary_key));
	    			if(noteditable.contains(key) && dbAccess.getValue(coretable, "ecstored", primary_key).equalsIgnoreCase("R")){ //checkRemote(table, primary_key)){
	    				textviewhash.get(key).setFocusable(false);
    	    		}
	    		}
	    	}
	    	
	    	if(gpstags != null){
	    		for(String key : gpstags){
	    			
	    			lat = dbAccess.getValue(coretable, key+"_lat", primary_key);
    				gpsacc = dbAccess.getValue(coretable, key+"_acc", primary_key);
    				if(lat.equalsIgnoreCase("0")){
    					textviewhash.get(key).setText("Location Not Set");
    					gpsbuttonhash.get(key).setEnabled(true);
    					gpsbuttonhash.get(key).setTextColor(Color.BLACK);
    					gpsbuttonhash.get(key).setText("Tap to Set Location");
    				}
    				else{
    					textviewhash.get(key).setText("Location Set - Accuracy "+gpsacc+"m");
    					gpsbuttonhash.get(key).setEnabled(false);
    					gpsbuttonhash.get(key).setTextColor(Color.WHITE);
    					gpsbuttonhash.get(key).setText("Location stored. Use menu option to reset");
    				}
	    		}
	    	}
	    	
	    	
	    	if(imageviewvalhash != null){
	        	for(String key : imageviewvalhash.keySet()){
	        		try{
	    				String photoid = dbAccess.getValue(coretable, key, primary_key);

	    				if(photoid.length() > 2){
	    					imageviewhash.get(key).setImageURI(Uri.parse(thumbdir+"/"+photoid));
	    					imageviewvalhash.put(key, photoid);
	    					photoButton.setText("Tap to Update Photo");
	    				}
	    				else{
	    					photoButton.setText("Tap to Add Photo");
	    				}	    				
	    				
	    				imageviewhash.get(key).setOnClickListener(new View.OnClickListener() {
	    					public void onClick(View arg0) {
	    		    		
	    						showImage();
	    					}
	    		           
	    				});
	    			}
	    			catch(Exception e){
	    				//showAlert("Image not available");
	    			}
		        }
	         }

	    	for(String key : spinners){
	    		thisspinnerhash.get(key).setSelection(Integer.parseInt(dbAccess.getValue(coretable, key, primary_key)));
	    	}
	    	
	    for(String key : radios){
	    	try{
	    		int sel = Integer.parseInt(dbAccess.getValue(coretable, key, primary_key));
	    		if(sel != 0){
	    			thisradiohash.get(key).get(sel).setChecked(true);
	    			radioselectedhash.put(key, sel);
	    			}
	    		}
	    		catch(Exception e){}
         }
         	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    	
	    }*/
    }

    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    super.onMenuItemSelected(featureId, item);
	    switch(item.getItemId()) {
	    case RESET_ID:
	    	//resetData();
	    	confirmReset();
	        break;
	    case CHANGE_GPS:
 	    	gpsButton = gpsbuttonhash.get(allitemposhashrev.get(thispage));
			gpsButton.setEnabled(true);
			gpsButton.setTextColor(Color.BLACK);
			gpsButton.setText(R.string.change_location_2); //"Location stored - tap again to update");
			showToast(this.getResources().getString(R.string.change_location_3)); //"This enables a stored location to be changed");
			break;
	    case HOME:
			confirmHome(this.getResources().getString(R.string.confirm_home), 0); //"Any entered data will be lost. To return to form press \"Cancel\"",  0);
			break;
	    case SAVE_RECORD:
	    	for(String key : textviews){
    			if(primary_keys.contains(key) && ((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase("")))){
    				showAlert(this.getResources().getString(R.string.entry_required)+": "+ key, "Error"); // "Entry required for primary key field"
    				alldata = 1;
    				return false;
    			}
    		}
	    	//if((textviewhash.get(coretablekey).getText().toString() == null) || (textviewhash.get(coretablekey).getText().toString().equalsIgnoreCase(""))){
	    	//	showAlert("Entry required for primary key field: "+ coretableview, "Error");
			//	alldata = 1;
			//	return false;
	    	//}
	    	storeData(2);
			//confirmHome("Any entered data will be lost. To return to form press \"Cancel\".");
			break;
	    case HIERARCHY:
			showHierarchy();
			break;
	    }
	        
	    return true;
	}
    
    private void resetData(){
    	if(imageviewvalhash != null)
    		imageviewvalhash.clear();
    	if(videoviewvalhash != null)
    		videoviewvalhash.clear();
    	if(audioviewvalhash != null)
    		audioviewvalhash.clear();
    	
    	String dateformat;
		SimpleDateFormat sdf;
   		Date date = new Date();
   		
   		primary_key = "";
   		title = "";
   		phonekeyset = false;
   		//isnew = 1;
   		//newentry = true;
   		//getValues(coretable);
   		   		   		
    	//for(String key : textviews){
    	for(String key : textviewhash.keySet()){
    		if(!nodisplay.contains(key) && !setextras.containsKey(key)){
    			textviewhash.get(key).setText("");
    			if(genkey.equalsIgnoreCase(key)){
	        		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	//String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	//sIMEI += "_"+cal.getTimeInMillis();
	    	       	UUID uuid = UUID.randomUUID();
	    	    	textviewhash.get(key).setText(uuid.toString());
	    	    	textviewhash.get(key).setFocusable(false);
	        	}
    			if(setdates.get(key) != null){
    				dateformat = setdates.get(key);
    				sdf = new SimpleDateFormat(dateformat); 
    	       		textviewhash.get(key).setText(sdf.format(date));
    			}
    			if(settimes.get(key) != null){
    				// IMPLEMENT THIS
    				dateformat = settimes.get(key);
    				sdf = new SimpleDateFormat(dateformat); 
    	       		textviewhash.get(key).setText(sdf.format(date));
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
    			if(def_vals.containsKey(key)){
    				textviewhash.get(key).setText(def_vals.get(key));
		    	} 	
    		}
        }
    	
    	// Reset the doublecheck second edittexts
    	
    	for(Integer i : doublecheckhash2.keySet())
    		doublecheckhash2.get(i).setText("");
        
    	String[] tempstring2; //, tempstring2;
    	int pos = 0;
        for(String key : spinners){
        	thisspinnerhash.get(key).setSelection(0);
        	//tempstring = (dbAccess.getValue(coretable, "spinner_"+key)).split(",,");
    		tempstring2 = (dbAccess.getValue(coretable, "spinner_values_"+key)).split(",,");
        	if(def_vals.containsKey(key)){
        		if(project_version > 1.0){
        			pos = 0;
        			for (int i = 1; i < tempstring2.length; i++) {
        				if(def_vals.get(key).equals(tempstring2[i]))
        					pos = i; // + 1;
        			}
        			thisspinnerhash.get(key).setSelection(pos);
        		}
        		else{
        			try{
        				int spos = Integer.parseInt(def_vals.get(key));
        				thisspinnerhash.get(key).setSelection(spos);
        			}
        			catch(NumberFormatException npe){
        			}
        		}
 	    	}	  
        }
         
        
      /*  LinkedHashMap<String, RadioButton> radiohash;
        resetting_radiobuttons = true;
        for(String key : radios){
        	//if(radioselectedhash.get(key).equalsIgnoreCase("")){
        	//	thisradiohash.get(key).get(radioselectedhash.get(key)).setSelected(false);
        	//}
        	
        	radiohash = thisradiohash.get(key);
        	for(String rb : radiohash.keySet())
        		radiohash.get(rb).setChecked(false);
        			
        	if(def_vals.containsKey(key)){
 	    		int oldthispage = thispage;
 	    		String sel = "";
 	    		try{
 	    			int spos = Integer.parseInt(def_vals.get(key));
 	    			// The radiohash is a LinkedHashMap, which is ordered.
 	    			// Need to find the radio button at the required position
 	    			int hcount = 1;
 	    			
 	    			for(String key2 : radiohash.keySet()){
 	    				if(spos == hcount){
 	    					sel = key2;
 	    					continue;
 	    				}
 	    				hcount++;
 	    			}
	 	    		radiohash.get(sel).setChecked(true);
	 	    		radioselectedhash.put(key, sel);
 	    		}
 	    		catch(Exception npe){
 	    		}
 	    		thispage = oldthispage;
 	    	}	  
        }
        resetting_radiobuttons = false;  */
                
        resetting_radiobuttons = true;
        for(RadioGroup rg : radiogroup_vec)
        	rg.clearCheck();
        resetting_radiobuttons = false;
        
        //radioselectedhash.clear();
        if(radioselectedhash != null){
        	for(String key : radioselectedhash.keySet())
        		radioselectedhash.put(key, "");
        }
        
        String sel;
        for(String key : radios){
        	if(def_vals.containsKey(key)){
        		int oldthispage = thispage;
 	    		
 	    		try{
	 	    		sel = def_vals.get(key);
	 	    		thispage = oldthispage-1;
	 	    		thisradiohash.get(key).get(sel).setChecked(true);
	 	    		radioselectedhash.put(key, sel);
	 	    		}
 	    		catch(Exception npe){
 	    		} 
 	    		
 	    		thispage = oldthispage;
	    		}
	    	}
        
        for(String key : checkboxes){
        	checkboxhash.get(key).setChecked(false);
        	
        }
        
        Vector<String> def_vals_vec = new Vector<String>();
        for(String val : checkboxposhash.values()){
        	//checkboxposhash.put(page-1, viewvalues[1]);
        	def_vals_vec.clear();
        	if(def_vals.containsKey(val)){
        		String[] tempvals = null;
        		tempvals = def_vals.get(val).split(",");
        		for(String key : tempvals){     
        			def_vals_vec.addElement(key);
        			}
        	}	  
    	
        	int countbox = 1;
        	for(String key : checkboxhash.keySet()){
        		if(key.startsWith(val+"_")){
        			if(def_vals_vec.contains(""+countbox))
        				checkboxhash.get(key).setChecked(true);
            		countbox++;
        		}
    		}
        }
        
        ImageView iv;
        for(String key : imageviewhash.keySet()){
        	iv = imageviewhash.get(key);
        	iv.setImageBitmap(null);
        	//iv.setImageURI(null);
        	//photoButton.setText("Tap to Update Photo");
        }
        
        for(String photoButton : photobuttonhash.keySet())
        	photobuttonhash.get(photoButton).setText(R.string.add_photo); //"Tap to Add Photo");
        
        for(String key : videoviewhash.keySet()){
        	iv = videoviewhash.get(key);
        	iv.setImageBitmap(null); //setImageURI(null);
        	//videoButton.setText("Tap to Update Photo");
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
        	//String sIMEI = mTelephonyMgr.getDeviceId();
    	
        	//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
   	   	
        	//sIMEI += "_"+cal.getTimeInMillis();
        	textviewhash.get(key).setText(UUID.randomUUID().toString());
        	
        }
        
        
     // If a branch has been added to a previous record the primary key field would have been
   		// made non-editable. Need to enable focus again
   		if(hasbranch){
   			for(Integer key: branchtvhash.keySet()){
   				branchtvhash.get(key).setText(Html.fromHtml("<center>0 "+this.getResources().getString(R.string.entries_for_record)+"</center>"));
   			}
   			//for(String key: textviewhash.keySet()){
   			//	textviewhash.get(key).setText("HELLO");
   			//	Log.i("TEXTVIEWS", key);
   			//}
   			for(String key: primary_keys){
   				//textviewhash.get(key).setEnabled(true);
   				textviewhash.get(key).setFocusable(true);
   				// Have to add this to otherwise EditText will not enable
   				textviewhash.get(key).setFocusableInTouchMode(true);
   			 				
   			}
   		}
        
        
    }

    public void showAlert(String result, String title){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setNegativeButton("OK", new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }

    
    private void getValues(String table){
    	
    	// Reset values as otherwise odd values seem to get left behind:
    	// Project - data - back -back - new project - data -> causes crash
    	// If first project has spinner and second doesn't then second "spinners"
    	// still contains
    	textviews = new String[0];
        spinners = new String[0];
        radios = new String[0];
        checkboxes = new String[0];
        gpstags = new Vector<String>();
        photos = new String[0];
        videos = new String[0];
        audio = new String[0];
        primary_keys.clear();
        jumps = new Hashtable <String, String>();
        jumps1 = new Hashtable <String, String>();
    	jumps2 = new Hashtable <String, String>();
    	dates = new Hashtable <String, String>();
    	setdates = new Hashtable <String, String>();
    	settimes = new Hashtable <String, String>();
    	crumbs = new Hashtable <String, String>();
    	res = new Hashtable <String, String>();
    	matches = new Hashtable <String, String>();
    	def_vals = new Hashtable <String, String>();
    	mincheck = new Hashtable <String, String>();
    	maxcheck = new Hashtable <String, String>();
    	mincheck2 = new Hashtable <String, String>();
    	maxcheck2 = new Hashtable <String, String>();
        
    	if(dbAccess.getValue(table, "textviews") != null && dbAccess.getValue(table, "textviews").length() > 0){
    		//Log.i("TEXVIEWS", dbAccess.getValue(table, "textviews"));
			textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
    	}
    	if(dbAccess.getValue(table, "spinners") != null && dbAccess.getValue(table, "spinners").length() > 0){
    		//Log.i("GET SPINNERS", dbAccess.getValue(table, "spinners"));
    		spinners = (dbAccess.getValue(table, "spinners")).split(",,");
    	}
    	if(dbAccess.getValue(table, "radios") != null && dbAccess.getValue(table, "radios").length() > 0){
    		//Log.i("GET SPINNERS", dbAccess.getValue(table, "spinners"));
    		radios = (dbAccess.getValue(table, "radios")).split(",,");
    	}
    	if(dbAccess.getValue(table, "checkboxes") != null && dbAccess.getValue(table, "checkboxes").length() > 0)
    		checkboxes = (dbAccess.getValue(table, "checkboxes")).split(",,");
    	
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
    	/*for(String key : spinners){       	
        	tempstring = dbAccess.getValue(table, "spinner_values_"+key).split(",,");
	    	spinnersvalueshash.put(key, tempstring);
        }
    	
    	for(String key : radios){       	
        	tempstring = dbAccess.getValue(table, "radio_values_"+key).split(",,");
	    	radiosvalueshash.put(key, tempstring);
        }*/
    	//for(String t : textviews)
    	//	//Log.i(getClass().getSimpleName(), "TEXT VIEW "+t);
    	
    	//Log.i(getClass().getSimpleName(), "TOTALS CHECK "+dbAccess.getValue(table, "totals"));
    	String[] temp;
    	if(dbAccess.getValue(table, "totals") != null && dbAccess.getValue(table, "totals").length() > 0){
    		//Log.i(getClass().getSimpleName(), "TOTALS CHECK 2 "+table+" "+dbAccess.getValue(table, "totals"));
    		for(String key : (dbAccess.getValue(table, "totals")).split(",,")){
    			temp = key.split("\t");
    			totals.put(temp[0], temp);
    			//Log.i(getClass().getSimpleName(), "TOTALS CHECK 2 "+temp[0] + " IS "+temp.toString());
    		}
    	}
    	
    	if(dbAccess.getValue(table, "regex") != null && dbAccess.getValue(table, "regex").length() > 0){
    		//Log.i(getClass().getSimpleName(), "RE CHECK 3 ");
    		for(String key : (dbAccess.getValue(table, "regex")).split(",,")){
    			temp = key.split("\t");
    			res.put(temp[0], temp[1]);
    			//Log.i(getClass().getSimpleName(), "RE CHECK 2 "+temp[0] + " IS "+temp[1]);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "matches") != null && dbAccess.getValue(table, "matches").length() > 0){
    		//Log.i(getClass().getSimpleName(), "RE CHECK 3 ");
    		for(String key : (dbAccess.getValue(table, "matches")).split(",,")){
    			temp = key.split("\t");
    			matches.put(temp[0], temp[1]);
    			//Log.i(getClass().getSimpleName(), "RE CHECK 2 "+temp[0] + " IS "+temp[1]);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "defaultvals") != null && dbAccess.getValue(table, "defaultvals").length() > 0){
    		//Log.i(getClass().getSimpleName(), "RE CHECK 3 ");
    		for(String key : (dbAccess.getValue(table, "defaultvals")).split(",,")){
    			temp = key.split("\t");
    			def_vals.put(temp[0], temp[1]);
    			//Log.i("DEF VALS", temp[0] + " IS "+temp[1]);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "mincheck") != null && dbAccess.getValue(table, "mincheck").length() > 0){
    		//Log.i(getClass().getSimpleName(), "RE CHECK 3 ");
    		for(String key : (dbAccess.getValue(table, "mincheck")).split(",,")){
    			temp = key.split("\t");
    			mincheck.put(temp[0], temp[1]);
    			//Log.i(getClass().getSimpleName(), "RE CHECK 1 "+temp[0] + " IS "+temp[1]);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "maxcheck") != null && dbAccess.getValue(table, "maxcheck").length() > 0){
    		//Log.i(getClass().getSimpleName(), "RE CHECK 3 ");
    		for(String key : (dbAccess.getValue(table, "maxcheck")).split(",,")){
    			temp = key.split("\t");
    			maxcheck.put(temp[0], temp[1]);
    			//Log.i(getClass().getSimpleName(), "RE CHECK 2 "+temp[0] + " IS "+temp[1]);
    		}
    	}
    	
    	String ref;
    	if(dbAccess.getValue(table, "mincheck2") != null && dbAccess.getValue(table, "mincheck2").length() > 0){
    		for(String key : (dbAccess.getValue(table, "mincheck2")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			Log.i("MINCHECK2", key);
    			//temp = key.split("\t");
    			mincheck2.put(ref, key);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "maxcheck2") != null && dbAccess.getValue(table, "maxcheck2").length() > 0){
    		for(String key : (dbAccess.getValue(table, "maxcheck2")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			//temp = key.split("\t");
    			maxcheck2.put(ref, key);
    		}
    	}
    	
    	//StringBuffer tempbuff = new StringBuffer();
    	//String ref;
    	//Log.i(getClass().getSimpleName(), "JUMPS CHECK 1 "+dbAccess.getValue(table, "jumps"));
    	if(dbAccess.getValue(table, "jumps") != null && dbAccess.getValue(table, "jumps").length() > 0){
    		for(String key : (dbAccess.getValue(table, "jumps")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			//Log.i(getClass().getSimpleName(), "JUMPS CHECK 2 "+temp[0]);
    			//for(int i = 1; i < temp.length-1; i+=2){
    				//Log.i(getClass().getSimpleName(), "JUMPS CHECK 3 "+temp[i] + " IS "+temp[i+1]);
    			//	tempbuff.append(temp[i] +" "+ temp[i+1]);
    			///	
    			//}
    			//Log.i(getClass().getSimpleName(), "JUMPS CHECK INIT "+ref);
    			jumps.put(ref, key);
    			
    		}
    	}
    	
    	//Log.i(getClass().getSimpleName(), "JUMPS CHECK1 1 "+dbAccess.getValue(table, "jumps1"));
    	if(dbAccess.getValue(table, "jumps1") != null && dbAccess.getValue(table, "jumps1").length() > 0){
    		for(String key : (dbAccess.getValue(table, "jumps1")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			jumps1.put(ref, key);
    			
    		}
    	}
    	if(dbAccess.getValue(table, "jumps2") != null && dbAccess.getValue(table, "jumps2").length() > 0){
    		for(String key : (dbAccess.getValue(table, "jumps2")).split(",,")){
    			temp = key.split(",");
    			ref = temp[0];
    			jumps2.put(ref, key);
    			//Log.i(getClass().getSimpleName(), "JUMPS CHECK INIT "+ref+" IS "+key);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "groups") != null && dbAccess.getValue(table, "groups").length() > 0){
    		groups = new Hashtable <String, String>();
    		groupkeyhash = new Hashtable <String, String>();
    		for(String key : (dbAccess.getValue(table, "groups")).split(",,")){
    			temp = key.split("\t");
    			ref = temp[0];
    			groups.put(ref, key);
    			//Log.i(getClass().getSimpleName(), "JUMPS CHECK INIT "+ref+" IS "+key);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "dates") != null && dbAccess.getValue(table, "dates").length() > 0){
    		for(String key : (dbAccess.getValue(table, "dates")).split(",,")){
    			//Log.i(getClass().getSimpleName(), "DATES CHECK "+key);
    			temp = key.split("\t");
    			if(temp.length > 1){
    				dates.put(temp[0], temp[1]);
    				//Log.i(getClass().getSimpleName(), "DATES CHECK 2 "+temp[0] +" "+ temp[1]);
    			}
    		}
    	}
    	
    	if(dbAccess.getValue(table, "setdates") != null && dbAccess.getValue(table, "setdates").length() > 0){
    		for(String key : (dbAccess.getValue(table, "setdates")).split(",,")){
    			//Log.i(getClass().getSimpleName(), "DATES CHECK "+key);
    			temp = key.split("\t");
    			if(temp.length > 1){
    				setdates.put(temp[0], temp[1]);
    				//Log.i(getClass().getSimpleName(), "DATES CHECK 2 "+temp[0] +" "+ temp[1]);
    			}
    		}
    	}
    	
    	if(dbAccess.getValue(table, "settimes") != null && dbAccess.getValue(table, "settimes").length() > 0){
    		for(String key : (dbAccess.getValue(table, "settimes")).split(",,")){
    			temp = key.split("\t");
    			if(temp.length > 1){
    				settimes.put(temp[0], temp[1]);
    			}
    		}
    	}
    	
    	if(dbAccess.getValue(table, "crumbs") != null && dbAccess.getValue(table, "crumbs").length() > 0){
    		for(String key : (dbAccess.getValue(table, "crumbs")).split(",,")){
    			//Log.i(getClass().getSimpleName(), "DATES CHECK "+key);
    			temp = key.split("\t");
    			if(temp.length > 1){
    				crumbs.put(temp[0], temp[1]);
    				//Log.i(getClass().getSimpleName(), "DATES CHECK 2 "+temp[0] +" "+ temp[1]);
    			}
    		}
    	}
    	
    	if(dbAccess.getValue(table, "doubles") != null && dbAccess.getValue(table, "doubles").length() > 0){
    		for(String key : (dbAccess.getValue(table, "doubles")).split(",,")){
    			doubles.addElement(key);
    		}
    	}
        
    	if(dbAccess.getValue(table, "integers") != null && dbAccess.getValue(table, "integers").length() > 0){
    		for(String key : (dbAccess.getValue(table, "integers")).split(",,")){
    			integers.addElement(key);
    		}
    	}
    	
    	if(dbAccess.getValue(table, "uppercase") != null && dbAccess.getValue(table, "uppercase").length() > 0){
    		for(String key : (dbAccess.getValue(table, "uppercase")).split(",,")){
    			uppercase.addElement(key);
    		}
    	}
    	
    	// Could probably get this from getkeyvalue 
    	if(dbAccess.getKeyValue(table) != null && dbAccess.getValue(table, "ecpkey").length() > 0){
    		//Log.i("PRIMARY KEYS", dbAccess.getValue(table, "ecpkey"));
    		for(String key : (dbAccess.getValue(table, "ecpkey")).split(";")){
    			//Log.i("PRIMARY KEYS 2", key);
    			primary_keys.addElement(key);
        	}
    	}
        
        if(dbAccess.getValue(table, "doublecheck") != null && dbAccess.getValue(table, "doublecheck").length() > 0){
        	for(String key : (dbAccess.getValue(table, "doublecheck")).split(",,")){
        		doublecheck.addElement(key);
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
        
        if(dbAccess.getValue(table, "barcodes") != null && dbAccess.getValue(table, "barcodes").length() > 0){
    		for(String key : (dbAccess.getValue(table, "barcodes")).split(",,")){
    			barcodes.addElement(key);
    		}
    	}
          
        List<String> list;
        if(dbAccess.getValue(table, "listfields") != null && dbAccess.getValue(table, "listfields").length() > 0){
        	list = Arrays.asList(dbAccess.getValue(table, "listfields").split(",,")); //("\\s+"));
        	listfields = new Vector<String>(list);
        }
        
        if(dbAccess.getValue(table, "listspinners") != null && dbAccess.getValue(table, "listspinners").length() > 0){
        	list = Arrays.asList(dbAccess.getValue(table, "listspinners").split(",,")); //("\\s+"));
        	listspinners = new Vector<String>(list);
        }
        
        if(dbAccess.getValue(table, "listradios") != null && dbAccess.getValue(table, "listradios").length() > 0){
        	list = Arrays.asList(dbAccess.getValue(table, "listradios").split(",,")); //("\\s+"));
        	listradios = new Vector<String>(list);
        }
         
        if(dbAccess.getValue(table, "listcheckboxes") != null && dbAccess.getValue(table, "listcheckboxes").length() > 0){
        	list = Arrays.asList(dbAccess.getValue(table, "listcheckboxes").split(",,")); //("\\s+"));
        	listcheckboxes = new Vector<String>(list);
        }
        
        if(dbAccess.getValue(table, "requiredtext") != null && dbAccess.getValue(table, "requiredtext").length() > 0){
        	list = Arrays.asList((dbAccess.getValue(table, "requiredtext")).split(",,"));
        	requiredfields = new Vector<String>(list);
        	storedrequiredfields = new Vector<String>(list);
        }
        
        if(dbAccess.getValue(table, "requiredspinners") != null && dbAccess.getValue(table, "requiredspinners").length() > 0){
        	list = Arrays.asList((dbAccess.getValue(table, "requiredspinners")).split(",,"));
        	requiredspinners = new Vector<String>(list);
        	storedrequiredspinners = new Vector<String>(list);
        }
           
        if(dbAccess.getValue(table, "requiredradios") != null && dbAccess.getValue(table, "requiredradios").length() > 0){
        	list = Arrays.asList((dbAccess.getValue(table, "requiredradios")).split(",,"));
        	requiredradios = new Vector<String>(list);
        	storedrequiredradios = new Vector<String>(list);
        }
        
        if(dbAccess.getValue(table, "genkey") != null && dbAccess.getValue(table, "genkey").length() > 0){
        	genkey = dbAccess.getValue(table, "genkey");
        }
           
        if(dbAccess.getValue(table, "radioimages") != null && dbAccess.getValue(table, "radioimages").length() > 0){
        	for(String key : (dbAccess.getValue(table, "radioimages")).split(",,")){
        		radioimages.addElement(key);
        	}
        }
    }
    
    
    
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
    	spinnerposhash = new Hashtable<Integer, String>();
    	radioposhash = new Hashtable<Integer, String>();
    	checkboxposhash = new Hashtable<Integer, String>();
    	allitemposhash = new Hashtable<String, Integer>();
    	allitemposhashrev = new Hashtable<Integer, String>();
        thisspinnerhash = new Hashtable<String, Spinner>();
        thisradiohash = new Hashtable<String, LinkedHashMap<String, RadioButton>>(); // RadioButton
        checkboxhash = new LinkedHashMap<String, CheckBox>();
        doublecheckhash = new Hashtable<Integer, EditText>();
        doublecheckhash2 = new Hashtable<Integer, EditText>();
        totalshash = new Hashtable<Integer, String[]>();
        dateshash = new Hashtable<Integer, EditText>();
        dateshashformat = new Hashtable<Integer, String>();
        
        //if(project.equalsIgnoreCase("SCORE_FULL") || project.equalsIgnoreCase("SCORE_DEMO") || project.equalsIgnoreCase("Tanz"))
			jumpradiohash = new Hashtable<Integer, RadioGroup>();
    	
        String views =  dbAccess.getValue(table, "notes_layout");// parser.getValues();
                
        flipper = new ViewFlipper(this);
	    
	    RelativeLayout ll = new RelativeLayout(this);
	    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    
	 // Calculate last page
	    
	    int count = 0;
	    String[] viewvalues;
	    String[] tempstring, tempstring2;
	    
	    //Log.i(getClass().getSimpleName(), "VIEWS WAS "+views);
	    //views = views.replaceFirst(",,,", "");
	    Log.i(getClass().getSimpleName(), "VIEWS NOW "+views);
	    allviews = views.split(",,,");
	    
	    //lastpage = allviews.length;
	    //.split(",,")
	    
	    lastpage = 1; // 0; Lastpage starts at 1 to allow for the extra page for the store button
	    
	    // If record being edited then final store button not needed
	    if(fromdetails)
	    	lastpage = 0;
    	
	    //Log.i(getClass().getSimpleName(), "VIEWS "+views+" ALLVIEWS "+allviews[0]);
	    
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
	    	
	    	//Log.i(getClass().getSimpleName(), "VIEWS 2 "+thisview);
	    	//if(thisview.startsWith(",,"))
	    	//	thisview.replaceFirst(",,", "");
	    	
	    	viewvalues = thisview.split(",,");
	    	
	    	// Don't want to display this field
	    	if(nodisplay.contains(viewvalues[1])){
	    		if(genkey.equalsIgnoreCase(viewvalues[1])){
	        		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	//String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	//sIMEI += "_"+cal.getTimeInMillis();
	    	       	et = new EditText(this);
	    	       	
	    	       	UUID uuid = UUID.randomUUID();
	    			et.setText(uuid.toString());
	    			et.setFocusable(false);
	    			textviewhash.put(viewvalues[1], et);
	        	}
	    		//continue;
	    	}
	    	
	    	
	    	else if(types.contains(viewvalues[0]))
	    		lastpage++;
	    	
	    	//if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("select1")  || viewvalues[0].equalsIgnoreCase("radio")|| viewvalues[0].equalsIgnoreCase("photo") || viewvalues[0].equalsIgnoreCase("video") || viewvalues[0].equalsIgnoreCase("audio") || viewvalues[0].equalsIgnoreCase("gps") || viewvalues[0].equalsIgnoreCase("barcode") || viewvalues[0].equalsIgnoreCase("branch") || viewvalues[0].equalsIgnoreCase("group")){
	    	//	lastpage++;
	    	//}

	    	//else if(viewvalues[0].equalsIgnoreCase("select")){
	    	//	lastpage++;
	    	//}
	    	
	    } 
	   /* int checkcount = 0;
	    boolean checkincremented = false;
	    for(String thisview : allviews){
	    	viewvalues = thisview.split(",,");
	    	if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("select1")){
	    		count++;
	    		totalcount++;
	    		checkincremented = false;
	    	}

	    	if(viewvalues[0].equalsIgnoreCase("select")){
	    		count++;
	    		for(int i = 3; i < viewvalues.length; i++){
	    			checkcount++;
	        			
	        		if(i == 3 && count == 6){
	        			// The checkbox is at the start of a new page so ensure lastpage is incremented
	        			lastpage++;
	        			count= 1;
	        		}
	        		i++; // The viewvalues now contains the label and the value for each checkbox
	    		}
	    	}
	    	// Check wardresources
	    	// If when checkboxes added the next page takes it
	    	// to exactly 5 then lastpage is one short
	    	if(count > 5 || (count + checkcount > 5)){ //|| (totalcount > 5 && count >= 5)){
	    		if(!checkincremented)
	    			lastpage++;

	    		// If there are multiple checkboxes only want to increment lastpage once
	    		// If there are 10 for example lastpage gets incremented twice without
	    		// the checkincremented flag
	    		
	    		if(count + checkcount > 5)
	    			checkincremented = true;
	    		count= 1;
	    		checkcount = 0;
	    		
	    	}
	    	
	    } */
	
	    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
	       
	    ll.addView(flipper, linear1layout2);
	       
	    ScrollView s = new ScrollView(this);
	    
	    flipper.addView(s); 
	    gestureDetector = new GestureDetector(new MyGestureDetector());
	    
	    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	    TableLayout l=new TableLayout(this);
	    
	    s.addView(l); 
	      
	    RelativeLayout rl2;
	    //Button bp, bn;
	    RelativeLayout.LayoutParams rlp3=null, rlp4=null, rlp5=null;
	    rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    View v;
	    
	   /* int Measuredwidth = 0;
		 // int Measuredheight = 0;
		  Point size = new Point();
		  WindowManager w = getWindowManager();

		  	// Need to check android version to get dimension
		    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
		          w.getDefaultDisplay().getSize(size);

		          Measuredwidth = size.x;
		        //  Measuredheight = size.y; 
		        }else{
		          Display d = w.getDefaultDisplay(); 
		          Measuredwidth = d.getWidth(); 
		        //  Measuredheight = d.getHeight(); 
		        } */

	        
	    if(lastpage >= 2){
	    	rl2 = new RelativeLayout(this);
	    
	    	bp = new ImageButton(this);
	    	bp.setOnClickListener(listenerPrevious);
	    	bp.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.back);
	    	//bp.setWidth(100);
	    	//bp.setText(R.string.previous); //"Prev");
	
	    	rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	          
	    	rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
	    
	    	TextView pagetv = new TextView(this);
	    	// For John - no page numbers
	    	//float pc = (float)1/(float)lastpage*100;
	    	//pagetv.setText(pc+"%"); //("P 1 of "+lastpage);
	    	//if(!dbAccess.getProject().equalsIgnoreCase("BF") && !dbAccess.getProject().equalsIgnoreCase("BF_Supp") && !dbAccess.getProject().equalsIgnoreCase("BF_Full"))
	    		pagetv.setText("P 1 of "+lastpage);
	    	//pagetv.setWidth(100);
	    	//pagetv.setTextSize(18);
	    	rl2.addView(pagetv, rlp4);
	    	pagetv.bringToFront();
	    	
	    	bn = new ImageButton(this);
	    	bn.setOnClickListener(listenerNext);
	    	bn.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.forward);
	    	//bn.setWidth(100);
	    	//bn.setText("Next");
	    
	    	rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    
	    	rl2.addView(bn, rlp5);
	   
	    	l.addView(rl2);
	    	v = new View(this);
    		v.setMinimumHeight(2);
    		v.setBackgroundColor(Color.WHITE);
    		
    		l.addView(v, lp);
    		
    		v = new View(this);
    		v.setMinimumHeight(25);
    		l.addView(v, lp);
    		
	    	}
	   
		count = 0;
		   
		
		
		int page = 2;
		
		boolean addconfirm = false;
	    for(String thisview : allviews){
	    	addconfirm = false;
	    	viewvalues = thisview.split(",,");
	    	//Log.i(getClass().getSimpleName(), "VIEWVALUES "+viewvalues[0]);
	    	try{
	    	//Log.i(getClass().getSimpleName(), "VIEWVALUES "+viewvalues[0] +" "+viewvalues[1]);
	    	}
	    	catch(Exception e){}
	    	
	    	// Don't want to display this field
	    	// Only text fields can be "no display"
	    	if(viewvalues[0].equalsIgnoreCase("input") && nodisplay.contains(viewvalues[1])){ 
	    		// This is a primary key that will not be set by the user. Therefore use phone ID and date
	    		//Log.i("Primary Key Needs Setting", "Here");
	    		
	    		// THIS IS HARD CODED FOR SCORE ONLY
	    		//Log.i("TEST TABLE "+table, viewvalues[1]);
	    		/*if(table.equalsIgnoreCase("Results") && viewvalues[1].startsWith("Specimen_ID")){
	    			//Log.i("TEST TABLE 2 "+table, viewvalues[1]+" "+textviewhash.get("Aliquot_ID").getText().toString());

	    			et = new EditText(this);
	    			et.setText(dbAccess.getValueWithFKey("Aliquot", viewvalues[1], "Aliquot_ID,"+textviewhash.get("Aliquot_ID").getText().toString()));
	    			
	    			//Log.i("RESULT "+textviewhash.get("Aliquot_ID").getText().toString(), dbAccess.getValueWithFKey("Aliquot", viewvalues[1], "Aliquot_ID,"+textviewhash.get("Aliquot_ID").getText().toString()));
	    			
	    			textviewhash.put(viewvalues[1], et);
	    		}
	    		else*/ 
	    		if(primary_keys.contains(viewvalues[1])){
	    			et = new EditText(this);
	    			if(newentry == true){
	    				//TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    				//String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    				//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    				
	    				//sIMEI += "_"+cal.getTimeInMillis();
	    				et.setText(UUID.randomUUID().toString());}
	    			else{
	    				et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    			}
	    			textviewhash.put(viewvalues[1], et);
	    			hiddentextviewkeys.addElement(viewvalues[1]);
	    			//Log.i("Primary Key Set", sIMEI);
	    			
	    		}
	    		else if(extras.containsKey(viewvalues[1])){
	    			// Need to store the value as it is a foreign key
	    			et = new EditText(this);
	    			et.setText(extras.getString(viewvalues[1]));
	    			textviewhash.put(viewvalues[1], et);
	    			//Log.i("SETTING EXTRAS "+viewvalues[1], extras.getString(viewvalues[1]));
	    			//keytablehash.put(viewvalues[1], extras.getString(viewvalues[1]));
	    		}
	    		else if(newentry == false){
	    			// It is the foreign key
	    			et = new EditText(this);
	    			//et.setText(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key));
	    			et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    			textviewhash.put(viewvalues[1], et);
	    			//keytablehash.put(viewvalues[1], extras.getString(viewvalues[1]));
	    			//
	    			//if(!canedit)
	    			//	et.setFocusable(false);
	    		}
	    		
	    		else if(def_vals.containsKey(viewvalues[1])){
	    			et = new EditText(this);
		    		et.setText(def_vals.get(viewvalues[1]));
		    		textviewhash.put(viewvalues[1], et);
		    		//Log.i("HIDDEN", viewvalues[1]+" "+def_vals.get(viewvalues[1]));
		    	} 
	    		// This is for SCORE only and can be removed
	    		// Required where a multi primary key has been set (Results) but the server
	    		// has the old version of the code and requires a single primary key
	    		else if(genkey.equalsIgnoreCase(viewvalues[1]) && newentry == true){
	    			et = new EditText(this);
	        		//TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	//String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	//sIMEI += "_"+cal.getTimeInMillis();
	    			UUID uuid = UUID.randomUUID();
	    			et.setText(uuid.toString());
	    			textviewhash.put(viewvalues[1], et);
	        	}
	    		else{
	    			et = new EditText(this); // Ensure edittext is created to avoid null pointer exception
	    			textviewhash.put(viewvalues[1], et);
	    		}
	    			//et.setText(""); 
	    		// This shouldn't be possible ... but just in case
	    		//if(coretablekey.equalsIgnoreCase(viewvalues[1]))
	    		//	coretableview = viewvalues[2];

	    		
	    		
	    		continue;
	    	}
	    	
	    	if(count >= 1){ // && totalcount >= 7){
	    		s = new ScrollView(this);
	    	    
	    		flipper.addView(s); //, sp);
	    	    
	    	    l=new TableLayout(this);
	    	    
	    	    rl2 = new RelativeLayout(this);
	    	    
	    	    bp = new ImageButton(this);
	    	    bp.setOnClickListener(listenerPrevious);
	    	    bp.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.back);
	    		//bp.setWidth(100);
	    	    //bp.setText(R.string.previous); //"Prev");
	    	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    	    
	    	    rl2.addView(bp, rlp3);
	    	    	    	    
	    	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	    
	    	    TextView pagetv = new TextView(this);
	    	    pagetv.setGravity(Gravity.CENTER_VERTICAL);
	    	 // For John - no page numbers
	    	    //float pc = (float)page/(float)lastpage*100;
	    	    //if(page == lastpage)
	    	    //	pc = 100;
	    	    //pagetv.setText((int)pc+"%"); //("P "+page+" of "+lastpage);
	    	    //if(!dbAccess.getProject().equalsIgnoreCase("BF") && !dbAccess.getProject().equalsIgnoreCase("BF_Supp") && !dbAccess.getProject().equalsIgnoreCase("BF_Full"))
	    	    	pagetv.setText("P "+page+" of "+lastpage);
	    	    //pagetv.setTextSize(18);
	    	    rl2.addView(pagetv, rlp4);
	    	    pagetv.bringToFront();
	    	    
	    	    if(page < lastpage){	    	    
	    	    
	    		bn = new ImageButton(this);
	    		bn.setOnClickListener(listenerNext);
	    		bn.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.forward);
	    		//bn.setWidth(100);
	    	    //bn.setText(R.string.next); //"Next");
	    	    
	    	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	    
	    	    rl2.addView(bn, rlp5);
	    	    }
	    	    
	    	    page++;
	    	    
	    	    l.addView(rl2);
	    	    
	    	    s.addView(l, lp);
	
	    	    //View v = new View(this);
	    	    v = new View(this);
	    		v.setMinimumHeight(2);
	    		v.setBackgroundColor(Color.WHITE);
	    		//v.setPadding(0, 10, 0, 10);
	    		l.addView(v, lp);
	    		
	    		v = new View(this);
	    		v.setMinimumHeight(15);
	    		l.addView(v, lp);
	    		
	    		count = 0;
	    		}
	    	
	    	String label;
	    	if(viewvalues[0].equalsIgnoreCase("input")){
	    		addconfirm = true;
	    		
	    		if(crumbs.get(viewvalues[1]) != null){
	    			
	    			String[] crumbsdet = (crumbs.get(viewvalues[1])).split(",");
	    			this.setTitle(crumbsdet[1]+" - "+textviewhash.get(crumbsdet[0]).getText().toString());
	    			tv = new TextView(this);
	    			tv.setTextSize(18);
	    			tv.setText(crumbsdet[1]+" - "+textviewhash.get(crumbsdet[0]).getText().toString());
	    			
	    			l.addView(tv, lp);
	    			
	    			// To add padding 
		    	    v = new View(this);
		    		v.setMinimumHeight(10);
		    		l.addView(v, lp);
		    		
	    			v = new View(this);
		    		v.setMinimumHeight(2);
		    		v.setBackgroundColor(Color.WHITE);
		    		l.addView(v, lp);
		    		
		    		// To add padding 
		    	    v = new View(this);
		    		v.setMinimumHeight(10);
		    		l.addView(v, lp);
	    		}
	    		
	    		tv = new TextView(this);
	    		
	    		label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		if(dates.get(viewvalues[1]) != null){
	    			// If the label doesn't already contain the required date format then add it
	    			if(!label.toLowerCase().contains(dates.get(viewvalues[1]).toLowerCase()))
	    					label += " ("+dates.get(viewvalues[1]).toLowerCase()+")";
	    		}
	    		/*if(mincheck.containsKey(viewvalues[1]) && maxcheck.containsKey(viewvalues[1]))
	    			label += " ("+mincheck.get(viewvalues[1]) + " - "+maxcheck.get(viewvalues[1]) +")";
	    		else if(mincheck.containsKey(viewvalues[1]))
	    			label += " (Min "+mincheck.get(viewvalues[1]) +")";
	    		else if(maxcheck.containsKey(viewvalues[1]))
	    			label += " (Max "+maxcheck.get(viewvalues[1]) +")";*/
	    		
	    		tv.setText(label); //(Html.fromHtml(label));
	    		//tv.setText(viewvalues[2]);
	        	tv.setTextSize(18);
	        	
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	/*if(viewvalues[1].equalsIgnoreCase("GPS")){
	    			gpsButton = new Button(this);
	    			gpsButton.setText("Tap to Set GPS Location");
	    			l.addView(gpsButton, lp);
	    			
	    			gpsset = false;
	    			gpsButton.setOnClickListener(new View.OnClickListener() {
	    		    	public void onClick(View arg0) {
	    		    		if(!gpsset)
	    		    			setGPS();
	    		    		else
	    		    			confirmChangeGPS();
	    		    	}
	    		           
	    		    });
	    		}*/	
	    		
	        	//if(setdates.get(viewvalues[1]) == null){ 
	        		et = new EditText(this);
	        		// Capitalize the first word of all sentences
	        		et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
	        		
	        		if(uppercase.contains(viewvalues[1])){
	        			et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		    		}
	        		//et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
	        		
	        	//}
	        		
	        	    
	    	    /*	if(table.equalsIgnoreCase("Results") && viewvalues[1].equalsIgnoreCase("Specimen_ID")){
	        			et.setText(dbAccess.getValue("Aliquot", viewvalues[1], textviewhash.get("Aliquot_ID").getText().toString()));
	        		} */
	    	    	
	    	    if(genkey.equalsIgnoreCase(viewvalues[1]) && newentry == true){
	        		//TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    	    	//String sIMEI = mTelephonyMgr.getDeviceId();
	    	    	
	    	    	//Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    	       	//sIMEI += "_"+cal.getTimeInMillis();
	    	    	UUID uuid = UUID.randomUUID();
	    			et.setText(uuid.toString());
	    			et.setFocusable(false);
	        	}
	        	
	    		else if(integers.contains(viewvalues[1])){
	    			et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
	    		}
    			else if(doubles.contains(viewvalues[1])){
    				et.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    		}
	    		/*if(viewvalues[1].equalsIgnoreCase("GPS")){
	    			usesgps = true;
	    			
	    			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
    		       	IP = locationManager.getProvider("gps");
    		       	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	    			et.setFocusable(false);
	    		}*/
	    		
	    		//else 
	    		
	    		else if(dates.get(viewvalues[1]) != null){
	    			//Log.i(getClass().getSimpleName(), "IN TOTALS HASH "+viewvalues[1]);
	    			//totalshash.put(page-1, totals.get(viewvalues[1]));
	    			
	    			et.setInputType(InputType.TYPE_CLASS_DATETIME); //|InputType.TYPE_DATETIME_VARIATION_DATE|InputType.TYPE_DATETIME_VARIATION_TIME);
	    	       	dateshash.put(page-1, et);
	    	       	dateshashformat.put(page-1, dates.get(viewvalues[1]));
	    		}
	    		
	    		else if(setdates.get(viewvalues[1]) != null){ 
	    			/*Calendar cal = Calendar.getInstance();
       	   	       	
	    	       	int m = cal.get(Calendar.MONTH) + 1;
	    	       	String month = ""+m;
	    	       	if(m < 10)
	    	       		month = "0"+month; 
	    	       	
	    	       	int d = cal.get(Calendar.DAY_OF_MONTH);
	    	       	String day = ""+d;
	    	       	if(d < 10)
	    	       		day = "0"+day; 
	    	       		
	    	       	String datefull = month + "/" + day + "/" + cal.get(Calendar.YEAR); // +" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
	    	       	*/
	    				   
	    			Button dateButton = new Button(this);
	    			
	    			l.addView(dateButton, lp);
	    			dateButton.setText(R.string.set_date); //"Set Date");
	    			dateButton.setOnClickListener(new View.OnClickListener() {
	    		    	public void onClick(View arg0) {
	    		    		showDialog(DATE_DIALOG_ID);
	    		    	}
	    		           
	    		    });
	    			//showDialog(DATE_DIALOG_ID);
	    			
	    	       	String dateformat = setdates.get(viewvalues[1]); //, dateformat2;
		    	    SimpleDateFormat sdf = new SimpleDateFormat(dateformat); 
	    	       	
		    	    Date date = new Date();
	    	       	
	    	       	et.setText(sdf.format(date));
	    	       	// This isn't needed as the EditText isn't focusable
	    	       	//et.setInputType(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE|InputType.TYPE_DATETIME_VARIATION_TIME);
	    			
	    	       	dateshash.put(page-1, et);
	    	       	dateshashformat.put(page-1, setdates.get(viewvalues[1]));
	    	       	
	    	       	et.setFocusable(false);
	    	       	if(!newentry && !canedit)
	    	       		dateButton.setEnabled(false);
	    		}
	    	    
	    		else if(settimes.get(viewvalues[1]) != null){ 
 
	    			Button timeButton = new Button(this);
	    			Button pickTimeButton = new Button(this);
	    			
	    			l.addView(timeButton, lp);
	    			timeButton.setText(R.string.set_time); //"Set Time");
	    			timeButton.setOnClickListener(new View.OnClickListener() {
	    		    	public void onClick(View arg0) {
	    		    		setTime();
	    		    	}
	    		           
	    		    });
	    			
	    			l.addView(pickTimeButton, lp);
	    			pickTimeButton.setText(R.string.pick_time); //"Set Time");
	    			pickTimeButton.setOnClickListener(new View.OnClickListener() {
	    		    	public void onClick(View arg0) {
	    		    		showDialog(TIME_DIALOG_ID);
	    		    	}
	    		           
	    		    });
	    			
	    	       	String timeformat = settimes.get(viewvalues[1]); //, dateformat2;
		    	    SimpleDateFormat sdf = new SimpleDateFormat(timeformat); 
	    	       	
		    	    Date date = new Date();
	    	       	
	    	       	et.setText(sdf.format(date));
	    	       	et.setInputType(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_TIME);
	    			
	    	       	dateshash.put(page-1, et);
	    	       	dateshashformat.put(page-1, settimes.get(viewvalues[1]));
	    	       	
	    	       	if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("1") && viewvalues[1].equalsIgnoreCase("ecdatev1")){
	    	       		et2 = new EditText(this);
	    	       		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		    			et2.setText(""+cal.getTimeInMillis());
		    			textviewhash.put("ectime", et2);
	    	       	}
	    	       	
	    	       	et.setFocusable(false);
	    	       	if(!newentry && !canedit){
	    	       		timeButton.setEnabled(false);
	    	       		pickTimeButton.setEnabled(false);
	    	       	}
	    		}
	    		
	    	    if(newentry && def_vals.containsKey(viewvalues[1])){
		    		et.setText(def_vals.get(viewvalues[1]));
		    	} 	    
	    		
	    		l.addView(et, lp);
	    		
	    		
	    		if(doublecheck.contains(viewvalues[1])){ // || doublecheck.contains(viewvalues[1])){
	    			et2 = new EditText(this);
	    			et2.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
	    			if(integers.contains(viewvalues[1])){
		    			et2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		    		}
	    			else if(doubles.contains(viewvalues[1])){
	    				et2.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		}
	    			if(uppercase.contains(viewvalues[1])){
	        			et2.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		    		}
	    			tv2 = new TextView(this);
		        	tv2.setText(R.string.confirm_value); //"Confirm Value");
		        	tv2.setTextSize(18);
		        	l.addView(tv2, lp);
		        	tv2.bringToFront();
	    			l.addView(et2, lp);
	    			doublecheckhash.put(page-1, et);
	    			doublecheckhash2.put(page-1, et2);
	    			
	    			if(newentry == false)
	    				et2.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    		}
	    		
	    		//Log.i(getClass().getSimpleName(), "CHECK TOTALS HASH OUT "+viewvalues[1] +"XX");
	    		
	    		//for(String key2 : totals.keySet())
	    		//	//Log.i(getClass().getSimpleName(), "CHECK TOTALS HASH IN "+key2 +"XX");
	    		
	    		if(totals.get(viewvalues[1]) != null){
	    			//Log.i(getClass().getSimpleName(), "IN TOTALS HASH "+viewvalues[1]);
	    			totalshash.put(page-1, totals.get(viewvalues[1]));
	    		}
	    		
	    		//Log.i(getClass().getSimpleName(), "VIEW VALUES "+viewvalues[1]);
	    		textviewhash.put(viewvalues[1], et);
	    		// To set the focus when the page is flipped
	    		textviewposhash.put(page-1, viewvalues[1]);
	    		allitemposhash.put(viewvalues[1], page-1);
	    		allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
	    		
	    		//Log.i("TABLE KEY CHECK", extras.getString("keytable") + " "+extras.getString("keyvalue"));	
	    		
	    		//if(keytable.equalsIgnoreCase(viewvalues[1])){
	    		if(extras.containsKey(viewvalues[1])){ // && !viewvalues[1].equalsIgnoreCase("Specimen_ID")){
	    			et.setText(extras.getString(viewvalues[1]));
	    			et.setFocusable(false); //Done
	    			Log.i("SETTING EXTRAS 2 "+viewvalues[1], extras.getString(viewvalues[1]));
	    			//keytablehash.put(viewvalues[1], extras.getString(viewvalues[1]));
	    		}
	    			    			
	    		if(newentry == false){
	    			// It is the primary key
	    			//if(viewvalues[1].equalsIgnoreCase(coretablekey))
		    		//	et.setText(primary_key);
	    			//else{
	    				//et.setText(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key));
	    				et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    			//}
	    				
	    				//Log.i("ISNEW 0 NEWENTRY FALSE: ",  primary_key +" "+dbAccess.getValue(table, viewvalues[1], primary_key));
	    				// If this is a stored version it cannot be edited
	    				if(noteditable.contains(viewvalues[1]) && dbAccess.getValue(table, "ecstored", primary_key).equalsIgnoreCase("R")){ //checkRemote(table, primary_key)){
	    	    			et.setFocusable(false); //Done
	    	    		}
	    				
	    				else if(primary_keys.contains(viewvalues[1]) || !canedit)
	    					et.setFocusable(false); //Done
	    				
	    				
	    		}
	    		
	    		//if(coretablekey.equalsIgnoreCase(viewvalues[1]))
	    		//	coretableview = viewvalues[2];
	    		
	    		// For John
	    		/*if(dbAccess.getProject().equalsIgnoreCase("BF") || dbAccess.getProject().equalsIgnoreCase("BF_Full")){
	    			Vector<String> locnames = new Vector<String>();
	    			locnames.addElement("Q3");
	    			locnames.addElement("Q9");
	    			locnames.addElement("Q11");
	    			locnames.addElement("Q13");
	    			locnames.addElement("Q22");
	    			locnames.addElement("Q31");
	    			locnames.addElement("Q41");
	    			locnames.addElement("Q50");
	    			locnames.addElement("Q59");
	    	    
	    			if(locnames.contains(viewvalues[1])){
	    				selectedpage = page-1;
	    				selectedtext = et;
	    				Button locbutton = new Button(this);
	    				locbutton.setText("Select Location");
	    				l.addView(locbutton, lp);
	    			
	    				locbutton.setOnClickListener(new View.OnClickListener() {
	    					public void onClick(View view) {
	    						goToCountry();
	    					}
	    				});
	    			}

	    		} */   			
	    		// For John to Here
	    		 
	    		 
	    	} 
	    	
	    	/*if(viewvalues[0].equalsIgnoreCase("Date")){
    			Calendar cal = Calendar.getInstance();
    	       	   	       	
    	       	int m = cal.get(Calendar.MONTH) + 1;
    	       	String month = ""+m;
    	       	if(m < 10)
    	       		month = "0"+month; 
    	       	
    	       	int d = cal.get(Calendar.DAY_OF_MONTH);
    	       	String day = ""+d;
    	       	if(d < 10)
    	       		day = "0"+day; 
    	       		
    	       	String datefull = month + "/" + day + "/" + cal.get(Calendar.YEAR); // +" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
    	       	et.setText(datefull);
    	       	et.setInputType(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE);
    	       	
    	       	//if(dates.get(viewvalues[1]) != null){
    	       		dateshash.put(page-1, et);
	    	       	dateshashformat.put(page-1, dates.get(viewvalues[1]));
	    		//}
    		}*/
	    	
	    	if(viewvalues[0].equalsIgnoreCase("location") || viewvalues[0].equalsIgnoreCase("GPS")){
	    		
	    		if(gpstvwarnvec == null){
	    			gpstvwarnvec = new Vector<TextView>();
	    		}
	    		addconfirm = true;
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
    			gpsButton = new Button(this);
    			gpsButton.setText(R.string.set_location); //"Tap to Set Location");
    			gpsButton.setTag(viewvalues[1]);
    			l.addView(gpsButton, lp);
    			tv2 = new TextView(this);
    			tv2.setText(R.string.location_search); //"Searching for location ...");
	        	tv2.setTextSize(18);
	        	l.addView(tv2, lp);
	        	gpstvwarnvec.add(tv2);
	        	
    			//Log.i("GPS BUTTON", viewvalues[1] +" IS "+(String)gpsButton.getTag());
    			
    			gpssethash.put(viewvalues[1], false);
    			gpsButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		//Log.i("GPS BUTTON 2", " IS "+(String)gpsButton.getTag());
    		    		//if(!gpssethash.get((String)gpsbuttonhash.get(gpsposhash.get(thispage)).getTag()))
    		    		if(!gpssethash.get(gpsposhash.get(thispage)))
    		    			setGPS();
    		    		else
    		    			confirmChangeGPS2();
    		    	}
    		           
    		    });
    			
    			// Only want to initialise this once
    			if(!usesgps){
    				//locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
    		       	//IP = locationManager.getProvider("gps");
    		       	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    		       	
    		       	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	                //mlocListener = new MyLocationListener();
    		       	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this); //mlocListener);

    			}
    			
    			usesgps = true;
    			
    			et = new EditText(this);
    			
    			et.setFocusable(false);
    			
    			l.addView(et, lp);
    			
    			gpsbuttonhash.put(viewvalues[1], gpsButton);
    			textviewhash.put(viewvalues[1], et);
	    		// To set the focus when the page is flipped
	    		textviewposhash.put(page-1, viewvalues[1]);
    			// To set the focus when the page is flipped
	    		// MIGHT BE ABLE TO DELETE THIS HASH
	    		gpsposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
	    		
	    		if(newentry == false){
		    		       	
    				//lat = dbAccess.getValue(table, viewvalues[1]+"_lat", coretablekey, primary_key);
    				//gpsacc = dbAccess.getValue(table, viewvalues[1]+"_acc", coretablekey, primary_key);
    				lat = dbAccess.getValue(table, viewvalues[1]+"_lat", primary_key);
    				gpsacc = dbAccess.getValue(table, viewvalues[1]+"_acc", primary_key);
    				//Log.i("GPS IS", gpsacc);
    				if(lat.equalsIgnoreCase("0")){
    					et.setText("Location Not Set");
    					gpsButton.setEnabled(true);
    					gpsButton.setTextColor(Color.BLACK);
    					gpsButton.setText(R.string.set_location); //"Tap to Set Location");

    				}
    				else{
    					String lon, alt, bearing;
    					lon = dbAccess.getValue(table, viewvalues[1]+"_lon", primary_key);
    					alt = dbAccess.getValue(table, viewvalues[1]+"_alt", primary_key);
    					bearing = dbAccess.getValue(table, viewvalues[1]+"_bearing", primary_key);
    					et.setText("Location Set - Accuracy "+gpsacc+"m\nLat "+lat+"\nLon "+lon+"\nAlt "+alt+"\nBearing "+bearing);
    					gpsButton.setEnabled(false);
    					gpsButton.setTextColor(Color.WHITE);
    					gpsButton.setText(R.string.location_stored); //"Location stored. Use menu option to reset");

    				}
    				
    				gpssettingshash.put(viewvalues[1]+"_lat", lat);
					gpssettingshash.put(viewvalues[1]+"_lon", dbAccess.getValue(table, viewvalues[1]+"_lon", primary_key));
					gpssettingshash.put(viewvalues[1]+"_alt", dbAccess.getValue(table, viewvalues[1]+"_alt", primary_key));
					gpssettingshash.put(viewvalues[1]+"_acc", gpsacc);
					gpssettingshash.put(viewvalues[1]+"_bearing", dbAccess.getValue(table, viewvalues[1]+"_bearing", primary_key));
					gpssettingshash.put(viewvalues[1]+"_provider", dbAccess.getValue(table, viewvalues[1]+"_provider", primary_key));
					
					if(primary_keys.contains(viewvalues[1]) || !canedit){
						gpsButton.setEnabled(false);
						gpsButton.setTextColor(Color.WHITE);
					}
	    		}
	    		else{
	    			// Initialise values
	    			gpssettingshash.put(viewvalues[1]+"_lat", "0");
	    			gpssettingshash.put(viewvalues[1]+"_lon", "0");
	    			gpssettingshash.put(viewvalues[1]+"_alt", "N/A");
	    			gpssettingshash.put(viewvalues[1]+"_acc", "N/A");
	    			gpssettingshash.put(viewvalues[1]+"_bearing", "N/A");
	    			gpssettingshash.put(viewvalues[1]+"_provider", "");
	    		}
				
				//Log.i("2004 TEST 2", viewvalues[1]);
				
    		}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("barcode")){
	    		addconfirm = true;
	    		
	    		if(crumbs.get(viewvalues[1]) != null){
	    			
	    			String[] crumbsdet = (crumbs.get(viewvalues[1])).split(",");
	    			this.setTitle(crumbsdet[1]+" - "+textviewhash.get(crumbsdet[0]).getText().toString());
	    			tv = new TextView(this);
	    			tv.setTextSize(18);
	    			tv.setText(crumbsdet[1]+" - "+textviewhash.get(crumbsdet[0]).getText().toString());
	    			
	    			l.addView(tv, lp);
	    			
	    			// To add padding 
		    	    v = new View(this);
		    		v.setMinimumHeight(10);
		    		l.addView(v, lp);
		    		
	    			v = new View(this);
		    		v.setMinimumHeight(2);
		    		v.setBackgroundColor(Color.WHITE);
		    		l.addView(v, lp);
		    		
		    		// To add padding 
		    	    v = new View(this);
		    		v.setMinimumHeight(10);
		    		l.addView(v, lp);
	    		} 

	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
    			barcodeButton = new Button(this);
    			barcodeButton.setText(R.string.read_barcode); //"Tap to Read Barcode");
    			barcodeButton.setTag(viewvalues[1]);
    			l.addView(barcodeButton, lp);
    			   			
    			barcodeButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		IntentIntegrator.initiateScan(EntryNote.this);
    		    		//readBarcode();
    		    	}
    		           
    		    });
    			
    			et = new EditText(this);
    			
    			if(uppercase.contains(viewvalues[1])){
        			et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
	    		}
    			
    			//et.setFocusable(false);
    			
    			//if(genkey.equalsIgnoreCase(viewvalues[1]) && newentry == true){
	    	    	
	    	    //	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		       	   	
	    		//	et.setText(""+cal.getTimeInMillis()); //sIMEI);
	        	//}

    			if(integers.contains(viewvalues[1])){
    				et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    			}
    			else if(doubles.contains(viewvalues[1])){
    				et.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
    			}
    			
    			l.addView(et, lp);
    			
    			if(doublecheck.contains(viewvalues[1])){ // || doublecheck.contains(viewvalues[1])){
	    			et2 = new EditText(this);
	    			if(integers.contains(viewvalues[1])){
		    			et2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
		    		}
	    			else if(doubles.contains(viewvalues[1])){
	    				et2.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		}
	    			if(uppercase.contains(viewvalues[1])){
	        			et2.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		    		}
	    			tv2 = new TextView(this);
		        	tv2.setText(R.string.confirm_value); //"Confirm Value");
		        	tv2.setTextSize(18);
		        	l.addView(tv2, lp);
		        	tv2.bringToFront();
	    			l.addView(et2, lp);
	    			doublecheckhash.put(page-1, et);
	    			doublecheckhash2.put(page-1, et2);
	    			
	    			if(newentry == false)
	    				et2.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    		}
    			
    			if(newentry == false){
    				et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
    				if(doublecheck.contains(viewvalues[1]))
    					et2.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
    				
    				if(primary_keys.contains(viewvalues[1]) || !canedit){
    					et.setFocusable(false);
    					barcodeButton.setEnabled(false);
    					barcodeButton.setTextColor(Color.WHITE);
    					if(doublecheck.contains(viewvalues[1]))
    						et2.setFocusable(false);
    				}
	    		}
				
    			//if(coretablekey.equalsIgnoreCase(viewvalues[1]))
	    		//	coretableview = viewvalues[2];
    			
    			textviewhash.put(viewvalues[1], et);
	    		// To set the focus when the page is flipped
	    		textviewposhash.put(page-1, viewvalues[1]);
    			// To set the focus when the page is flipped
	    		// Can use this hash for barcodes too
	    		gpsposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
    		}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("group")){
	    		addconfirm = true;
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
    			groupButton = new Button(this);
    			groupButton.setText(R.string.tap_to_select); //"Tap to Select");
    			groupButton.setTag(viewvalues[1]);
    			l.addView(groupButton, lp);
    			 
    			groupButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		//IntentIntegrator.initiateScan(EntryNote.this);
    		    		//readBarcode();
    		    		goToGroup((String)arg0.getTag());
    		    	}
    		           
    		    });
    			
    			et = new EditText(this);
    				
    			l.addView(et, lp);
    			
    			if(newentry == false){
    				et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
    				
    				if(primary_keys.contains(viewvalues[1]) || !canedit){
						groupButton.setEnabled(false);
						groupButton.setTextColor(Color.WHITE);
						et.setFocusable(false);
    				}
	    		}

    			textviewhash.put(viewvalues[1], et);
	    		// To set the focus when the page is flipped
	    		textviewposhash.put(page-1, viewvalues[1]);
    			// To set the focus when the page is flipped
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
    		}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("branch")){
	    		addconfirm = true;
	    		hasbranch = true;
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	        	//label += " links to "+viewvalues[3].replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
    			branchButton = new Button(this);
    			branchButton.setText(R.string.add_record); //"Tap to Add Record");
    			branchButton.setTag(viewvalues[1]);
    			l.addView(branchButton, lp);
    			   			
    			branchButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		addBranch();
    		    	}
    		           
    		    });
    			
    			// Using primary_keys.elementAt(0) so this only works with single value primary keys
    			// viewvalues[3] is the branch table
    			String branch_key = "";
    			int entries = 0;
    			
    			if(newentry == false){ //isnew == 0){
    				for(String key: primary_keys){
    					branch_key += ","+ key +","+ textviewhash.get(key).getText().toString();
    				}
    				branch_key = branch_key.replaceFirst(",", "");
    				entries = dbAccess.getBranchCount(viewvalues[3], branch_key); //primary_keys.elementAt(0), textviewhash.get(primary_keys.elementAt(0)).getText().toString());
    			
    				if(!canedit){
    					branchButton.setEnabled(false);
    					branchButton.setTextColor(Color.WHITE);
    				}
    			}
    			
    			tv2 = new TextView(this);
	        	tv2.setText(Html.fromHtml("<center>"+entries+" "+this.getResources().getString(R.string.entries_for_record)+"</center>")); // entries for this record
	        	tv2.setTextSize(18);
	        	l.addView(tv2, lp);
	        	tv2.bringToFront();
	        	listbranchButton = new Button(this);
    			listbranchButton.setText(R.string.list_records); //"List Records");
    			listbranchButton.setTag(viewvalues[1]);
    			l.addView(listbranchButton, lp);
    			   			
    			listbranchButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		//IntentIntegrator.initiateScan(EntryNote.this);
    		    		//readBarcode();
    		    		listBranch();
    		    	}
    		           
    		    });
    			
    			//et = new EditText(this);
    			

    			//l.addView(et, lp);
    			
    			//if(newentry == false){
    			//	et.setText(dbAccess.getValue(table, viewvalues[1], primary_key));
	    		//}
    			
    			//textviewhash.put(viewvalues[1], et);
	    		// To set the focus when the page is flipped
	    		//textviewposhash.put(page-1, viewvalues[1]);
    			// To set the focus when the page is flipped
	    		// Can use this hash for barcodes too
	    		//gpsposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	branchhash.put(page-1, viewvalues[3]);
	 	    	branchtvhash.put(page-1, tv2);
	    		count++;
    		}
	    	
	    	
	    	if(viewvalues[0].equalsIgnoreCase("photo")){
	    		addconfirm = true;
	    		
	    		// Only create the hash if it is actually needed
	    		if(imageviewvalhash == null)
	    			imageviewvalhash = new Hashtable<String, String>();
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	photoButton = new Button(this);
	        	photoButton.setText(R.string.add_photo); //"Tap to Add Photo");
    			l.addView(photoButton, lp);
    			
    			// Required to reset the text on all the buttons when a new record is entered
    			photobuttonhash.put(viewvalues[1], photoButton);
    			
    			final String photo_capture = this.getResources().getString(R.string.photo_capture), select_photo = this.getResources().getString(R.string.select_photo); 
    			photoButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		showImageAlert(photo_capture, select_photo); // "Photo Capture", "Select Photo Option"
    		          	//addPhoto();
    		        }
    		           
    		    });
	        	
	        	ImageView iv = new ImageView(this);
	        	l.addView(iv, lp);
	        		    		
	        	imageviewvalhash.put(viewvalues[1], "-1");
	        	imageviewhash.put(viewvalues[1], iv);
	    		// To set the focus when the page is flipped
	    		imageviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
	    		
	    		if(newentry == false){
	    			
	    			try{
	    				//String photoid = dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key);
	    				String photoid = dbAccess.getValue(table, viewvalues[1], primary_key);
	    				// On Motorola Droid the camera restes this activity
	    				// When putting all the data back if the primary key is set then
	    				// newentry set to false. However, the photo may not have been taken by this
	    				// point so heck photoid exists
	    				if(photoid != null && photoid.length() > 4){
	    					iv.setImageURI(Uri.parse(thumbdir+"/"+photoid));
	    					imageviewvalhash.put(viewvalues[1], photoid);
	    					photoButton.setText(R.string.update_photo); //"Tap to Update Photo");
	    				}
	    				else{
	    					photoButton.setText(R.string.add_photo); //"Tap to Add Photo");
	    				}	    				
	    				
	    				iv.setOnClickListener(new View.OnClickListener() {
	    					public void onClick(View arg0) {
	    		    		
	    						showImage();
	    					}
	    		           
	    				});
	    			}
	    			catch(Exception e){
	    				//showAlert("Image not available");
	    			}
	    			
	    			if(!canedit){
	    				photoButton.setEnabled(false);
	    				photoButton.setTextColor(Color.WHITE);
	    			}
	    		}
	    			
	    		
	    		
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("video")){
	    		addconfirm = true;
	    		
	    		// Only create the hash if it is actually needed
	    		if(videoviewvalhash == null)
	    			videoviewvalhash = new Hashtable<String, String>();
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2];//.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	videoButton = new Button(this);
	        	videoButton.setText(R.string.add_video); //"Tap to Add Video");
    			l.addView(videoButton, lp);
    			
    			// Required to reset the text on all the buttons when a new record is entered
    			videobuttonhash.put(viewvalues[1], videoButton);
    			
    			videoButton.setOnClickListener(new View.OnClickListener() {
    		    	public void onClick(View arg0) {
    		    		
    		          	addVideo();
    		        }
    		           
    		    });
	        	
	        	ImageView iv = new ImageView(this);
	        	l.addView(iv, lp);
	    		
	        	videoviewhash.put(viewvalues[1], iv);
	    		// To set the focus when the page is flipped
	    		videoviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	videoviewvalhash.put(viewvalues[1], "-1");
	    		count++;
	    		
	    		if(newentry == false){
	    			try{
	    				
	    				//String photoid = dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key);
	    				String videoid = dbAccess.getValue(table, viewvalues[1], primary_key);
	    				
	    				if(videoid != null && !videoid.equalsIgnoreCase("-1") && videoid.length() > 4){
	    					String videodir = Epi_collect.appFiles+"/"+project+"/videos"; //Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + dbAccess.getProject();
	    					try{
	    						  File f = new File(videodir+"/"+videoid);
	    						  if(f.exists()){
	    							// This will work in 2.2
	    		    					//Bitmap bm = ThumbnailUtils.createVideoThumbnail(videodir+"/"+videoid, ThumbnailUtils.MINI_KIND);
	    		    					Bitmap bm = getVideoFrame(videodir+"/"+videoid);
	    		    				
	    		    					iv.setImageBitmap(bm); // .setImageURI(Uri.parse(thumbdir+"/"+photoid));
	    		    					videoviewvalhash.put(viewvalues[1], videoid);
	    		    					videoButton.setText(R.string.update_video); //"Tap to Update Video");
	    		    			
	    		    					iv.setOnClickListener(new View.OnClickListener() {
	    		    						public void onClick(View arg0) {
	    		        		    		
	    		    							playVideo();
	    		    						}
	    		        		           
	    		    					});
	    						  }
	    						  else{
	    							  TextView videotv = new TextView(this);
	    							  videotv.setGravity(Gravity.CENTER_HORIZONTAL); 
	    							  videotv.setText(R.string.video_file_error); //"Video Recorded But File Unavailable");
	    							  videotv.setTextSize(18);
	    							  l.addView(videotv, lp);
	    							  videoButton.setText(R.string.update_video); //"Tap to Update Video");
	    						  }
	    					  }
	    					  catch(Exception e){
	    					  }
	    					
	    				}
	    				
	    			}
	    			catch(Exception e){
	    				//showAlert("Image not available");
	    			}
	    			
	    			if(!canedit){
						videoButton.setEnabled(false);
						videoButton.setTextColor(Color.WHITE);
	    			}
	    		}
	    		
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("audio")){
	    		addconfirm = true;
	    		
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
	    		
	    		tv = new TextView(this);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	ImageButton recButton = new ImageButton(this);
	        	//recButton.setText("Rec");
	        	recButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.record24);
    			l.addView(recButton, lp);
    			audiorecordbuttonhash.put(viewvalues[1], recButton);
    			
    			ImageButton playButton = new ImageButton(this);
	        	//playButton.setText("Play");
    			playButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.play24);
    			l.addView(playButton, lp);
    			audioplaybuttonhash.put(viewvalues[1], playButton);
    			playButton.setEnabled(false);
    			
    			ImageButton stopButton = new ImageButton(this);
	        	//stopButton.setText("Stop");
    			stopButton.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.stop24);
    			l.addView(stopButton, lp);
    			audiostopbuttonhash.put(viewvalues[1], stopButton);
    			stopButton.setEnabled(false);
    			
    			TextView audiotv = new TextView(this);
    			audiotv.setGravity(Gravity.CENTER_HORIZONTAL); 
    			audiotv.setText(R.string.no_audio); //"No Audio Recorded");
    			audiotv.setTextSize(18);
	        	l.addView(audiotv, lp);
	        	
	        	audiotextviewhash.put(viewvalues[1], audiotv);
    			
    			// Required to reset the text on all the buttons when a new record is entered
    			//videobuttonhash.put(viewvalues[1], videoButton);
    			
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
	        	
	        	//ImageView iv = new ImageView(this);
	        	//l.addView(iv, lp);
	    		
	        	//audioviewhash.put(viewvalues[1], iv);
	    		// To set the focus when the page is flipped
	    		audioviewposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	audioviewvalhash.put(viewvalues[1], "-1");
	    		count++;
	    		
	    		if(newentry == false){
    				
	    				//String photoid = dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key);
	    				String audioid = dbAccess.getValue(table, viewvalues[1], primary_key);
	    			try{
	    				if(audioid != null && !audioid.equalsIgnoreCase("-1") && audioid.length() > 4){
	    					audiodir = Epi_collect.appFiles+"/"+project+"/audio"; //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + dbAccess.getProject();
	    					
	    						  File f = new File(audiodir+"/"+audioid);
	    						  if(f.exists()){
	    							  audioviewvalhash.put(viewvalues[1], audioid);
	    							  playButton.setEnabled(true);
	    		    				
	    							  audiotv.setText(R.string.audio_available); //"Audio Available");
	    						  }
	    						  else{
	    							  audiotv.setText(R.string.audio_file_error); //"Audio Recorded But File Unavailable");
	    						  }
	    					  }				  
	    					
	    				}
	    			 catch(Exception e){
					  }
	    			if(!canedit){
						recButton.setEnabled(false);
						//recButton.setTextColor(Color.WHITE);
	    			}
	    		} 
	    		
	    	}
	    	
	          
	    	if(viewvalues[0].equalsIgnoreCase("select1")){
	    		addconfirm = true;
	    		
	    		tv = new TextView(this);
	        	//tv.setText(viewvalues[2]);
	        	label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	    		spin = new Spinner(this); 
		    	
	    		l.addView(spin, lp);
	    		
	    		Log.i("ADDING TO SPIN HASH", viewvalues[1]);
	    		
	    		thisspinnerhash.put(viewvalues[1], spin);
	    		tempstring = (dbAccess.getValue(table, "spinner_"+viewvalues[1])).split(",,");
	    		tempstring2 = (dbAccess.getValue(table, "spinner_values_"+viewvalues[1])).split(",,");
	    		
	    		if(spinnershash.get(viewvalues[1]) == null){
	 	    		spinnershash.put(viewvalues[1], new ArrayList<String>());
	 	    	}
	    		
	    		// NOT SURE IF ANY OF THIS IS NECESSARY AS IT IS SET BELOW
	    		// FOR ALL THE SPINNERS
	    		String sel = "";
	    		int pos = 0;
	    		if(newentry == false){
	 	    		//spin.setSelection(Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
	 	    		sel = dbAccess.getValue(table, viewvalues[1], primary_key);
	 	    	}
	    		//Log.i("SPINNER VALUE", sel);
	    		//This is for "Select"
	    		spinnershash.get(viewvalues[1]).add("Select");
	 	    	for (int i = 1; i < tempstring.length; i++) {
	 	    		spinnershash.get(viewvalues[1]).add(tempstring[i]);
	 	    		//try{
	 	    			if(sel.equals(tempstring2[i]))
	 	    				pos = i; // + 1;
	 	    		//}
	 	    		//catch(NullPointerException npe){}
	 	    	}
	 	    	
	 	    	if(newentry == false){
	 	    		//spin.setSelection(Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
	 	    		spin.setSelection(pos); //Integer.parseInt(dbAccess.getValue(table, viewvalues[1], primary_key)));
	 	    		if(primary_keys.contains(viewvalues[1]) || !canedit){
	 	    			spin.setEnabled(false);
	 	    			
	 	    		}
	 	    	}
	 	    	
	 	    	else if(def_vals.containsKey(viewvalues[1])){
	 	    		if(project_version > 1.0){
	 	    			pos = 0;
	 	    			for (int i = 1; i < tempstring.length; i++) {
	 	    				if(def_vals.get(viewvalues[1]).equals(tempstring2[i]))
	 	    					pos = i; // + 1;
	 	    			}
	 	    			spin.setSelection(pos);
	 	    		}
	 	    		else{
	 	    			try{
	 	    				int spos = Integer.parseInt(def_vals.get(viewvalues[1]));
	 	    				spin.setSelection(spos);
	 	    			}
	 	    			catch(NumberFormatException npe){
	 	    			}
	 	    		}
	 	    	}	  
	 	    	
	 	    	spinnerposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	    		count++;
	    		
	    		//flipper.setNextFocusDownId(tv.getId());
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("radio")){ 
	    		
	    		if(radioselectedhash == null)
	    			radioselectedhash = new Hashtable<String, String>();
	    		//Hashtable<String, Hashtable<String, RadioButton>> thisradiohash;
	    		
	    		addconfirm = true;
	    		
	    		tv = new TextView(this);
	        	//tv.setText(viewvalues[2]);
	    		label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	RadioButton rb;
	        	RadioGroup rg = new RadioGroup(this);
	        	
	        	LinearLayout rgl = new LinearLayout(this);
	        	rgl.addView(rg);
	        	
	        	radiohash = new LinkedHashMap<String, RadioButton>();
	        	
	    		//spin = new Spinner(this); 
		    	
	    		l.addView(rgl);
	    	    		
	    		tempstring = (dbAccess.getValue(table, "radio_"+viewvalues[1])).split(",,");
	    		tempstring2 = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");

	    		String sel = "";
	    		//RadioButton selectedrb = null;
	    		//if(newentry == false){
	 	    		//spin.setSelection(Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
	 	    	//	sel = dbAccess.getValue(table, viewvalues[1], primary_key);
	 	    	//}
	    		
	    		for (int i = 1; i < tempstring.length; i++) {
	    			rb = new RadioButton(this);
	    			radiohash.put(tempstring2[i], rb);
	    			rg.addView(rb);
	    			if(radioimages.contains(viewvalues[1])){
	    				Bitmap bitmap = BitmapFactory.decodeFile(Epi_collect.appFiles+"/"+project+"/radioimg"+"/" + tempstring2[i]+".jpg"); 
	    				Drawable d = new BitmapDrawable(getResources(), Bitmap.createBitmap(bitmap)); //, Measuredwidth/2, Measuredwidth/2, true));
	    				rb.setWidth(bitmap.getWidth());
	    				rb.setBackground(d);
	    			}
	    			else{
	    				rb.setText(tempstring[i]);
	    			}
	    			//if(sel.equals(tempstring[i]))
	    			//	selectedrb = rb;
	    			//Log.i("RADIO 1", tempstring[i]);
	    			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    			//@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							updateRadioButtons();
							
						}
			    	});
	 	    	}
	    		radiogroup_vec.add(rg);
	    		  
	    		//if(project.equalsIgnoreCase("SCORE_FULL") || project.equalsIgnoreCase("SCORE_DEMO") || project.equalsIgnoreCase("Tanz"))
	    			jumpradiohash.put(page-1, rg);
	    		thisradiohash.put(viewvalues[1], radiohash);
	    		
	    		radioselectedhash.put(viewvalues[1], "");
	 		 	    	
	 	    	radioposhash.put(page-1, viewvalues[1]);
	 	    	allitemposhash.put(viewvalues[1], page-1);
	 	    	allitemposhashrev.put(page-1, viewvalues[1]);
	 	    	
	 	    	/*if(requiredradios.contains(viewvalues[1])){
	 	    		int oldthispage = thispage;
	 	    		thispage = page - 1;
	    			radiohash.get(1).setChecked(true);
	    			radioselectedhash.put(viewvalues[1], 1);
	    			// Reset thipage after button checked
	 	    		thispage = oldthispage;
	    		}*/
	 	
	 	    	//for(int i : thisradiohash.get(radioposhash.get(thispage)).keySet()){
	 	    		
	 	    	// This triggers onCheckedChanged so need to ensure above hashes are created first
	 	    	if(newentry == false){
	 	    		int oldthispage = thispage;
	 	    		try{
	 	    		sel = dbAccess.getValue(table, viewvalues[1], primary_key);
	 	    		//Log.i("RADIO 2", sel);
	 	    		thispage = page-1;
	 	    		radiohash.get(sel).setChecked(true);
	 	    		radioselectedhash.put(viewvalues[1], sel);
	 	    		
	 	    		if(primary_keys.contains(viewvalues[1]) || !canedit)
	 	    			for(String r : radiohash.keySet())
	 	    				radiohash.get(r).setEnabled(false);
	 	    		}
	 	    		catch(Exception e){}
	 	    	// Reset thipage after button checked
 	    			thispage = oldthispage;
	 	    	
	 	    		//spin.setSelection(Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
	 	    		// In case no radio button is selected
	 	    		/*try{
	 	    			String sel = dbAccess.getValue(table, viewvalues[1], primary_key);
	 	    			//Log.i("RADIO BUTTON", ""+sel);
	 	    			int oldthispage = thispage;
	 	    			if(!sel.equalsIgnoreCase("")){
	 	    				thispage = page-1;
	 	    				radiohash.get(sel).setChecked(true);
	 	    				radioselectedhash.put(viewvalues[1], sel);
	 	    				}
	 	    		
	 	    			// Reset thipage after button checked
	 	    			thispage = oldthispage;
	 	    		}
	 	    		catch(Exception e){} */
	 	    	}
	 	    	
	 	    	else if(def_vals.containsKey(viewvalues[1])){
	 	    		int oldthispage = thispage;
	 	    		
	 	    		if(project_version > 1.0){
	 	    			try{
	 	    				sel = def_vals.get(viewvalues[1]);
	 	    				thispage = page-1;
	 	    				radiohash.get(sel).setChecked(true);
	 	    				radioselectedhash.put(viewvalues[1], sel);
		 	    			}
	 	    			catch(Exception npe){
	 	    			} 
	 	    		
	 	    			thispage = oldthispage;
	 	    		}
	 	    		else{
	 	    			try{
	 	    				int spos = Integer.parseInt(def_vals.get(viewvalues[1]));
	 	    				thispage = page-1;
	 	    				// The radiohash is a LinkedHashMap, which is ordered.
	 	    				// Need to find the radio button at the required position
	 	    				int hcount = 1;
	 	    				for(String key : radiohash.keySet()){
	 	    					if(spos == hcount){
	 	    						sel = key;
	 	    						break;
	 	    					}
	 	    					hcount++;
	 	    				}
	 	    				radiohash.get(sel).setChecked(true);
	 	    				radioselectedhash.put(viewvalues[1], sel);
	 	    			}
	 	    			catch(Exception npe){
	 	    			} 
	 	    			thispage = oldthispage; 
	 	    		}
	 	    	}
	 	    	
	 	    	//thisradiohash.get(radioposhash.get(thispage)).keySet()	    	
	    		count++;
	    		
	    		//flipper.setNextFocusDownId(tv.getId());
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("select")){
	    		addconfirm = true;
	    		
	    		tv = new TextView(this);
	    		label = viewvalues[2]; //.replaceAll("\\\\n", "<br>");
	    		tv.setText(label); //Html.fromHtml(label));
	        	//tv.setText(viewvalues[2]);
	        	tv.setTextSize(18);
	        	l.addView(tv, lp);
	        	tv.bringToFront();
	        	Vector<String> def_vals_vec = new Vector<String>();
	        	if(newentry && def_vals.containsKey(viewvalues[1])){
	        		String[] tempvals = null;
	        		tempvals = def_vals.get(viewvalues[1]).split(",");
	            	for(String key : tempvals){     
	            		def_vals_vec.addElement(key);
	            		}
	            }	  
	        	
	        	int countbox = 1;
	        	for(int i = 3; i < viewvalues.length; i++){
	        		cb = new CheckBox(this);
	        		cb.setText(viewvalues[i]);
	        		l.addView(cb, lp);
	        		i++;
	        		checkboxhash.put(viewvalues[1]+"_"+viewvalues[i], cb);
	        		count++;
	        		
	        		if(newentry == false){
		        		//if(Integer.parseInt(dbAccess.getValue(table, viewvalues[1]+"_"+viewvalues[i], coretablekey, primary_key)) == 1){
		        		if(Integer.parseInt(dbAccess.getValue(table, viewvalues[1]+"_"+viewvalues[i], primary_key)) == 1){
		        			cb.setChecked(true);
		               	}
		               	else{
		               		cb.setChecked(false);
		               	}
		                	//if(!canupdate)
		                	//	checkboxhash.get(key).setClickable(false);
		        		if(primary_keys.contains(viewvalues[1]) || !canedit)
		        			cb.setEnabled(false);
		            }
	        		
	        		else if(def_vals_vec.contains(""+countbox)) //viewvalues[i]))
	        			cb.setChecked(true);
	        			
	        		countbox++;
		 	    	//Log.i("SPINNER SETTING CHECK", ""+Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
		 	    }
	        	
	        	/*if(newentry == false){
	        		for(String key : checkboxes){
	        			//Log.i("CHECKBOX NEW ENTRY", key);
	                	if(Integer.parseInt(dbAccess.getValue(table, key, coretablekey, primary_key)) == 1){
	                		checkboxhash.get(key).setChecked(true);
	                	}
	                	else{
	                		checkboxhash.get(key).setChecked(false);
	                	}
	                	//if(!canupdate)
	                	//	checkboxhash.get(key).setClickable(false);
	                }
	 	    		//Log.i("SPINNER SETTING CHECK", ""+Integer.parseInt(dbAccess.getValue(table, viewvalues[1], coretablekey, primary_key)));
	 	    	} */
	        	
	        	checkboxposhash.put(page-1, viewvalues[1]);
	        	allitemposhash.put(viewvalues[1], page-1);
	        	allitemposhashrev.put(page-1, viewvalues[1]);
	        	//flipper.setNextFocusDownId(tv.getId());
	    	}
	    	
	    	if(addconfirm && fromdetails){
	    		v = new View(this);
	    		v.setMinimumHeight(10);
	    		l.addView(v, lp);
	    		
	    	    v = new View(this);
	    		v.setMinimumHeight(2);
	    		v.setBackgroundColor(Color.WHITE);
	    		// Doesn't seem to work!
	    		//v.setPadding(0, 20, 0, 20);
	    		l.addView(v, lp);
	    		
	    		// To add padding 
	    	    v = new View(this);
	    		v.setMinimumHeight(25);
	    		l.addView(v, lp);
	    		
	    		// Only to be displayed when the record is being edited
	    		confirmEditButton = new Button(this);
	    		if(canedit)
	    			confirmEditButton.setText(R.string.store_edit); //"Store Edit");
	    		else
	    			confirmEditButton.setText(R.string.ret);
		   
	    		l.addView(confirmEditButton, lp);
		    
	    		confirmEditButton.setOnClickListener(new View.OnClickListener() {
	    			public void onClick(View view) {
	    				//if(canedit)
						//	confirmData(0);
						//else
							confirmEdit();
	    				//confirmEdit();
	    				
	    			}
		    	});
	    	}
	    	//if(count >= 5 && totalcount >= 7){
	    	    
	    		//View v = new View(this);
	    	/*	v = new View(this);
	    		v.setMinimumHeight(2);
	    		v.setBackgroundColor(Color.WHITE);
	    		l.addView(v, lp);
	    		
	    		rl2 = new RelativeLayout(this);
	    	    
	    		if(page > 2){
	    			bp = new Button(this);
	    			bp.setOnClickListener(listenerPrevious);
	    			bp.setWidth(100);
	    			bp.setText("Previous");
	    	    
	    			rl2.addView(bp, rlp3);
	    		}
	    	    	    	    	    	    
	    	    pagetv = new TextView(this);
	    	    pagetv.setText("Page: "+(page-1)+" of "+lastpage);
	    	    pagetv.setTextSize(18);
	    	    rl2.addView(pagetv, rlp4);
	    		    	    
	    	    if(page-1 < lastpage){	   
	    	    	bn = new Button(this);
	    	    	bn.setOnClickListener(listenerNext);
	    	    	bn.setWidth(100);
	    	    	bn.setText("Next");
	    	    	    	    
	    	    	rl2.addView(bn, rlp5);
	    	    }
	    	    l.addView(rl2); */
	    	    	    	   
	    		//}
	    	
	    }
	    
	    //if(addconfirm && fromdetails){
		//}
		//else{
			s = new ScrollView(this);
    	    
    		flipper.addView(s); //, sp);
    	    
    	    l=new TableLayout(this);
    	    
    	    rl2 = new RelativeLayout(this);
    	    
    	    bp = new ImageButton(this);
    	    bp.setOnClickListener(listenerPrevious);
    	    bp.setImageResource(uk.ac.imperial.epi_collect2.R.drawable.back);
    		//bp.setWidth(100);
    	    //bp.setText(R.string.previous); //"Prev");
    	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	    
    	    rl2.addView(bp, rlp3);
    	    	    	    
    	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
    	    
    	    TextView pagetv = new TextView(this);
    	    pagetv.setGravity(Gravity.CENTER_VERTICAL);
    	 // For John - no page numbers
    	    //float pc = (float)page/(float)lastpage*100;
    	    //if(page == lastpage)
    	    //	pc = 100;
    	    //pagetv.setText((int)pc+"%"); //("P "+page+" of "+lastpage);
    	   // if(!dbAccess.getProject().equalsIgnoreCase("BF") && !dbAccess.getProject().equalsIgnoreCase("BF_Supp") && !dbAccess.getProject().equalsIgnoreCase("BF_Full"))
    	    	pagetv.setText("P "+page+" of "+lastpage);
    	    //pagetv.setTextSize(18);
    	    rl2.addView(pagetv, rlp4);
    	    pagetv.bringToFront();
	    	
    	    l.addView(rl2);
	    	
    	    s.addView(l, lp);
			
			// To add padding 
			//View v = new View(this);
			v = new View(this);
			v.setMinimumHeight(10);
			l.addView(v, lp);
		
			v = new View(this);
			v.setMinimumHeight(2);
			v.setBackgroundColor(Color.WHITE);
			// Doesn't seem to work!
			//v.setPadding(0, 20, 0, 20);
			l.addView(v, lp);
		
			// To add padding 
			v = new View(this);
			v.setMinimumHeight(25);
			l.addView(v, lp);
		
			tv = new TextView(this);
    		
    		tv.setText(R.string.tap_to_store); //"Tap button to store record");
    		//tv.setText(viewvalues[2]);
        	tv.setTextSize(18);
        	l.addView(tv, lp);
        	tv.bringToFront();
        	v = new View(this);
			v.setMinimumHeight(10);
			l.addView(v, lp);
			
			confirmButton = new Button(this);
			//if(canedit)
				confirmButton.setText(R.string.store); //"Store");
			//else
			//	confirmButton.setText(R.string.ret);
	   
			l.addView(confirmButton, lp);
	    
			confirmButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					//if(canedit)
						confirmData(0);
					//else
					//	confirmEdit();
				}
			});
			
		//}
		
			
		// Add group data
			
		Vector<String> grouptables;
		Spinner groupspin;
		EditText grouptext;
		
		int maxgroups = Integer.parseInt(dbAccess.getGroupValue("keys_"+dbAccess.getProject(), "max(fromgroup)"));
		String nextspinner = "";
		//Log.i("GROUP MAX SELECT", "TOTAL = "+groups);
		
		if(maxgroups > 0){
			groupspinhash = new HashMap<String, Spinner>();
			grouptexthash = new HashMap<String, EditText>();
		}
		
		//for(int i = 1; i <= groups; i++){
		//	groupspinhash.put(""+i, new Vector<Spinner>());
		//}
		
		ArrayList<String> temparr;
		Vector<String> tempvec;
		for(int i = 1; i <= maxgroups; i++){
			grouptables = dbAccess.getGroupTables(i);
			
			s = new ScrollView(this);
       		flipper.addView(s); //, sp);
       	    l=new TableLayout(this);
       	    s.addView(l, lp);
    	    	
       	    String alltables = ""+i;
       	    int tcount = -1;
	        for(String grouptable : grouptables){
	        	tcount++;
	        	alltables += "\t" + grouptable;
	        	tv2 = new TextView(this);
		        tv2.setText(Html.fromHtml(grouptable));
		        tv2.setTextSize(18);
		        l.addView(tv2, lp);
		        tv2.bringToFront();
	        	
	        	groupspin = new Spinner(this); 
	        	groupspinhash.put(i+"_"+grouptable, groupspin);
	        	//Log.i("TCOUNT", tcount +" - "+grouptables.size());
	        	nextspinner = "Null";
	        	if(tcount < grouptables.size()-1)
	        		nextspinner = grouptables.elementAt(tcount+1);
	        	groupspin.setTag(i+",,"+grouptable+",,"+nextspinner);
	        	l.addView(groupspin, lp);
	    	
	        	grouptext = new EditText(this);
	        	grouptexthash.put(i+"_"+grouptable, grouptext);
	        	l.addView(grouptext);
	    		
	        	temparr = new ArrayList<String>();
	        	temparr.add("Select");
	        		        	
	        	// Only need to add values for the first spinner
	        	if(tcount == 0){
	        		tempvec = dbAccess.getGroupSpinnerValues(grouptable);// XX NEED THE TITLE HERE
	        		//Log.i("TEMPVEC VALS", "ADDING");
	    		   	for (int j = 0; j < tempvec.size(); j+=2) {
	    		   		//Log.i("TEMPVEC VALS", grouptable+"_"+tempvec.elementAt(j)+" = "+tempvec.elementAt(j+1));
	    		   		temparr.add(tempvec.elementAt(j));
	    		   		groupkeyhash.put(grouptable+"_"+tempvec.elementAt(j), tempvec.elementAt(j+1));
	    		   	}
	        	}

	        	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparr);
	        	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        	groupspinhash.get(i+"_"+grouptable).setAdapter(aspnLocs); 
	    		
	        	groupspinhash.get(i+"_"+grouptable).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        			String value = (String)parent.getItemAtPosition(pos);
	        			Log.i("TAG", (String)parent.getTag());
	        			updateGroupSpinner(value, (String)parent.getTag()); 	    	    	
	        		}
	        		public void onNothingSelected(AdapterView<?> parent) {
	        		}
	        	}); 
	        	 	

	        }
		 
	        Button locbutton = new Button(this);
 	    	locbutton.setText(R.string.ret); //"Return");
 	    	
 	    	l.addView(locbutton);
 	    	
 	    	//alltables.replaceFirst("\t", "");
 	    	locbutton.setTag(alltables);
 	    	
 	    	locbutton.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View view) {
    				
    			updateGroupText((String)view.getTag());
    			/*	String result = "NEED TO ADD THIS CODE"; //"Country: "+countrytext.getText().toString()+" - Region: "+regiontext.getText().toString()+" - Cercle: "+cercletext.getText().toString()+" - Commune: "+communetext.getText().toString()+" - Village: "+villagetext.getText().toString();
    				selectedtext.setText(result);
    				
    				flipper.setInAnimation(null);
    		        flipper.setOutAnimation(null);
    				while(thispage != selectedpage){
    					flipper.showPrevious();
    					thispage--;
    				} */
    			}
	    	}); 
		} 
				
	    for(String key : spinners){
	    	//if(key.length() > 0){
	    	Log.i("SPINNER SETTING CHECK", key);
	    		aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
	    		aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    		thisspinnerhash.get(key).setAdapter(aspnLocs); 
	    		
	    		int pos = 0;
	    		tempstring = (dbAccess.getValue(table, "spinner_"+key)).split(",,");
	    		tempstring2 = (dbAccess.getValue(table, "spinner_values_"+key)).split(",,");
	    		
	    		// Create the spinnervalshash
	    		for(int k = 0; k < tempstring.length; k++){
	    			spinnervalshash.put(key+"_"+k, tempstring2[k]);
	    		
	    		}

	    		if(newentry == false){
	    			pos = 0;
	    			String sel = dbAccess.getValue(table, key, primary_key);
	    			//for (String val : spinnershash.get(key)) {
	    			//	if(sel.equals(val))
	    			//		pos = spinnershash.get(key).indexOf(sel);
		 	    	//}
	    			
	    			//Log.i("SELECTED", sel+" "+key);
	    			//tempstring = (dbAccess.getValue(table, "spinner_"+key)).split(",,");
		    		//tempstring2 = (dbAccess.getValue(table, "spinner_values_"+key)).split(",,");
		    		
		    		//This is for "Select"
		 	    	for (int i = 1; i < tempstring.length; i++) {
		 	    		//try{
		 	    			if(sel.equals(tempstring2[i]))
		 	    				pos = i; // + 1;
		 	    		//}
		 	    		//catch(NullPointerException npe){}
		 	    	} 
	    				    					 	    	
	    			//thisspinnerhash.get(key).setSelection(Integer.parseInt(dbAccess.getValue(table, key, coretablekey, primary_key)));
	    			thisspinnerhash.get(key).setSelection(pos); //Integer.parseInt(dbAccess.getValue(table, key, primary_key)));
	    			//Log.i("SETTING SEL", key+" "+pos);
	    		}
	    		else if(def_vals.containsKey(key)){
	    			if(project_version > 1.0){
	    				pos = 0;
	    				for (int i = 1; i < tempstring.length; i++) {
	    					if(def_vals.get(key).equals(tempstring2[i]))
	    						pos = i; // + 1;
	    				}
	    				thisspinnerhash.get(key).setSelection(pos);
	    			}
	    			else{
	    				try{
	    					int spos = Integer.parseInt(def_vals.get(key));
	    					thisspinnerhash.get(key).setSelection(spos);
	    				}
	    				catch(NumberFormatException npe){
	    				}
	    			}
	 	    	}	  
	    		
	    	//}
	    }
	
	    return ll;
	    
	 }
    
    private void updateGroupText(String gtables){
    	//String result = "NEED TO ADD THIS CODE"; //"Country: "+countrytext.getText().toString()+" - Region: "+regiontext.getText().toString()+" - Cercle: "+cercletext.getText().toString()+" - Commune: "+communetext.getText().toString()+" - Village: "+villagetext.getText().toString();
		
    	String[] grouptables = gtables.split("\t");
    	String result = "";
    	String group = grouptables[0];
    	for(int i = 1; i < grouptables.length; i++){
    		//Log.i("GROUP CHECK", group +" "+grouptables[i]);
    		result += grouptables[i] + " - " + grouptexthash.get(group+"_"+grouptables[i]).getText().toString();
    		if(i < grouptables.length - 1)
    			result += ", ";
    	}
    	
    	selectedtext.setText(result);
		
		flipper.setInAnimation(null);
        flipper.setOutAnimation(null);
		while(thispage != selectedpage){
			flipper.showPrevious();
			thispage--;
		}
    }
    
    private void updateGroupSpinner(String value, String spinnertag){
    	
    	// spinnertag = i,,spinnername,,nextspinnername
    	//groupspinhash.put(i+"_"+grouptable, groupspin);
    	String[] vals = spinnertag.split(",,");
    	Vector<String> tempvec;
	    grouptexthash.get(vals[0]+"_"+vals[1]).setText(value);
	    ArrayList<String> temparr = new ArrayList<String>();
	    temparr.add("Select");
	    Log.i("VALUE", value);
	    String fullvalue;
	    if(!value.equalsIgnoreCase("Select") && groupspinhash.containsKey(vals[0]+"_"+vals[2])){
	    	String[] tempstring = value.split("-");

	    	fullvalue = value;
	    	value = tempstring[0];
	    	value = value.replaceAll("\\s+", "");
	    	
	    	// This is the ecpkey, just want the key value
	    	String[] tempstring2 = groupkeyhash.get(vals[1]+"_"+fullvalue).split(",");

	    	tempvec = (dbAccess.getNextGroupSpinnerValues(vals[2], vals[1], tempstring2[1])); 
	    	//showAlert("VALUE "+value, "TEST2");
	    	for (int j = 0; j < tempvec.size(); j+=2) {
		   		Log.i("TEMPVEC VALS", vals[1]+"_"+tempvec.elementAt(j)+" = "+tempvec.elementAt(j+1));
		   		temparr.add(tempvec.elementAt(j));
		   		groupkeyhash.put(vals[2]+"_"+tempvec.elementAt(j), tempvec.elementAt(j+1));
		   	}
	    	//for (String val: tempvec) {
	    		//showAlert("VALUE "+val, "TEST");
	    	//	temparr.add(val);
	    	//}
 	    
	    }
	    else{
	    	grouptexthash.get(vals[0]+"_"+vals[1]).setText(value);
	    }
	
	    //Log.i("SETTING NEXT", vals[0]+"_"+vals[1]);
	    if(groupspinhash.containsKey(vals[0]+"_"+vals[2]))
	    	setNextSpinner(temparr, groupspinhash.get(vals[0]+"_"+vals[2]));
    }
    
    private void addBranch(){
    	
    	for(String key: primary_keys){
    		if((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase(""))){
				showAlert(this.getResources().getString(R.string.primary_key_required), this.getResources().getString(R.string.error)); //"Entry required for primary key field", "Error");
	    	}
		}
    	
    	Intent i = new Intent(this, EntryNote.class);
		
    	// Add the branch table
		i.putExtra("table", branchhash.get(thispage));

		// The 2 signifies it is a branch
		i.putExtra("new", 2);
		i.putExtra("branch", 1);
		
		// To add the linked from primary key value
		for(String key: primary_keys){
			 i.putExtra(key, textviewhash.get(key).getText().toString());
		}
		//i.putExtra(primary_keys.elementAt(0), textviewhash.get(primary_keys.elementAt(0)).getText().toString());

		i.putExtra("fromlist", 0);
		
		//i.putExtra("branch", isbranch);
		
		// This is the not needed
		i.putExtra("select_table_key_column", "Null");
		i.putExtra("foreign_key", "Null");
		i.putExtra("select_table", "Null");
		
		dbAccess.close();
	    startActivityForResult(i, ACTIVITY_BRANCH);
    }
    
    private void listBranch(){
    	Intent i = new Intent(this, ListRecords.class);
		
    	// Add the branch table
		i.putExtra("table", branchhash.get(thispage));

		i.putExtra("select_table_key_column", textviewhash.get(primary_keys.elementAt(0)).getText().toString()); // coretable
		i.putExtra("foreign_key", primary_keys.elementAt(0)+","+ textviewhash.get(primary_keys.elementAt(0)).getText().toString());
		i.putExtra("select_table", coretable);
		
		// FindRecord sends a query
		i.putExtra("query", "branch");
			
		startActivityForResult(i, ACTIVITY_BRANCH_LIST);
    }
    
    private void setTime(){
      	      
       	String timeformat = settimes.get(allitemposhashrev.get(thispage)); //, dateformat2;
	    SimpleDateFormat sdf = new SimpleDateFormat(timeformat); 
       	
	    Date date = new Date();
	           	
		dateshash.get(thispage).setText(sdf.format(date));
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Calendar c = Calendar.getInstance();
    	int cyear = c.get(Calendar.YEAR);
    	int cmonth = c.get(Calendar.MONTH);
    	int cday = c.get(Calendar.DAY_OF_MONTH);
    	switch (id) {
    	case DATE_DIALOG_ID:
    		return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
    	case TIME_DIALOG_ID:
    		return new TimePickerDialog(this,  mTimeSetListener,  c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
    	}
    	return null;
    	}
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
    	// onDateSet method
    		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {			
    			String dateformat = setdates.get(allitemposhashrev.get(thispage)); //, dateformat2;
	    	    SimpleDateFormat sdf = new SimpleDateFormat(dateformat); 
    	       	
	    	    Date date = new Date();
	    	    date.setDate(dayOfMonth);
	    	    date.setMonth(monthOfYear);
	    	    date.setYear(year-1900);
	    	    //String date = String.valueOf(monthOfYear+1)+" /"+String.valueOf(dayOfMonth)+" /"+String.valueOf(year);
    	       	
    			dateshash.get(thispage).setText(sdf.format(date));
    			//Toast.makeText(ExampleApp.this, "Selected Date is ="+date_selected, Toast.LENGTH_SHORT).show();
    			}
    	}; 
    	
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
       	// onDateSet method
       		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {			
       			String timeformat = settimes.get(allitemposhashrev.get(thispage));
        	    SimpleDateFormat sdf = new SimpleDateFormat(timeformat); 
       	       	
        	    Date date = new Date();
        	    date.setHours(hourOfDay);
        	    date.setMinutes(minute);
        	    date.setSeconds(0);
        	       	
       			dateshash.get(thispage).setText(sdf.format(date));
       			}

       	}; 
        
    
    private void goToGroup(String target){
       flipper.setInAnimation(null);
       flipper.setOutAnimation(null);
           
       // Reset all the spinners
       //countryspin.setSelection(0);
       
       String[] tg = groups.get(target).split("\t");
       int targetgroup = Integer.parseInt(tg[1]);
       selectedtext = textviewhash.get(allitemposhashrev.get(thispage));
       //int p = thispage;
       selectedpage = thispage;
       // //Log.i("PAGE", "Lastpage = "+lastpage+" selected = "+p);
       while(thispage < lastpage+targetgroup){
         	flipper.showNext();
           	thispage++;
       }
       
       // Not sure why but if from details it needs to be flipped an extra page
       if(fromdetails){
    	   flipper.showNext();
    	   thispage++;
       }
    	   
    } 
    	
    // For John
    private void setNextSpinner(ArrayList<String> temparr, Spinner spin){
		 
    		aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparr);
    		aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		spin.setAdapter(aspnLocs);
    		
    		spin.setSelection(0);
		 }
    
    // For John
   /* private void goToCountry(){
    	flipper.setInAnimation(null);
        flipper.setOutAnimation(null);
        
        // Reset all the spinners
        //countryspin.setSelection(0);
        
        selectedtext = textviewhash.get(allitemposhashrev.get(thispage));
        //int p = thispage;
        selectedpage = thispage;
       // //Log.i("PAGE", "Lastpage = "+lastpage+" selected = "+p);
        while(thispage < lastpage+1){
        	flipper.showNext();
        	thispage++;
        }
    } */
    
    // For John to here
	
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
    	//for(int i : radiohash.keySet())
		//	radiohash.get(i).setSelected(false);
		//rb.setSelected(true);
    }
    
	 private OnClickListener listenerNext = new OnClickListener() {
	        public void onClick(View v) {
	        	//removeFocus();
	        	
	        	if(audioactive)
        	 		return;
	        	
	        	flipper.setInAnimation(animateInFrom(RIGHT));
                flipper.setOutAnimation(animateOutTo(LEFT));
	        	flipToNext(1);
	        }

	    };
	    
	    private void flipToNext(int type){
	    	int jumpnum = 0;
        	if(flipping || (checkDoubleInput() && checkValidDate() && checkEntry() && checkRe() && checkMatch() && checkMinMax() && checkMinMax2())){ // checkTotals() && 
        		
        		if(canedit && !flipping)
        			storeData(3);
        		// Reset value for checkEntry
        		secondcheck = false;
        		//removeFocus();
        		if(type == 1){
        			flipper.setInAnimation(animateInFrom(RIGHT));
        			flipper.setOutAnimation(animateOutTo(LEFT));
        		}
        		
        		//flipper.focusSearch(flipper.FOCUS_RIGHT).requestFocus();
        		//flipper.findViewById(flipper.getNextFocusDownId()).requestFocus();
        		
        		jumpnum = checkJump();
        		// Only check for the second jump option if the first doesn't exist
        		if(jumpnum == 0)
        			jumpnum = checkJump2();
        		//Log.i("JUMPNUM CHECK", ""+jumpnum);
        		if(jumpnum == 0){
        			if(type != 2)
        				flipper.showNext();
        			setFocus(thispage, 1);
        			thispage++;
        			// Need to remove entries in case new data has been added
        			if(jumpreversevec.contains(""+thispage))
        				jumpreversevec.remove(""+thispage);
        			// If this is a required field as it is no longer jumped
        			// it must be put back
        			if(storedrequiredfields.contains(allitemposhashrev.get(thispage)))
        				requiredfields.addElement(allitemposhashrev.get(thispage));
        			if(storedrequiredspinners.contains(allitemposhashrev.get(thispage)))
	        			requiredspinners.addElement(allitemposhashrev.get(thispage));
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
                    	if(type != 2)
                    		flipper.showNext();
                    	thispage++;
                    	jumpreversevec.add(""+thispage);  
                    	
                    	// To avoid errors when data is saved
	        			// If an entry is jumped it can't be required
	        			if(requiredfields.contains(allitemposhashrev.get(thispage)))
	        				requiredfields.remove(allitemposhashrev.get(thispage));
	        			if(requiredspinners.contains(allitemposhashrev.get(thispage)))
		        			requiredspinners.remove(allitemposhashrev.get(thispage));
	        			if(requiredradios.contains(allitemposhashrev.get(thispage)))
		        			requiredradios.remove(allitemposhashrev.get(thispage));
	        			if(gpstags.contains(allitemposhashrev.get(thispage)))
	        				gpstagstoskip.addElement(allitemposhashrev.get(thispage));
                    }
                   // jumpreversevec.add(""+thispage);
                    if(type == 1){
                    	flipper.setInAnimation(animateInFrom(RIGHT));
                    	flipper.setOutAnimation(animateOutTo(LEFT));
                    }
                    if(type != 2)
                    	flipper.showNext();
                    setFocus(thispage, 1);
                    thispage++;
                    
                 // Need to remove entries in case new data has been added
        			if(jumpreversevec.contains(""+thispage))
        				jumpreversevec.remove(""+thispage);
                 // For John - IGNORE THIS as storeDate(3) called anyway
                 //   tempStore();
        		}
        		
        	}
        	if(usesgps)
        		setGPSMenu();
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
                    if(!jumpreversevec.contains(""+(thispage-1))){
                    	flipper.showPrevious();
                    	setFocus(thispage, -1);
                    	thispage--;
                    }
                    else{
                    	flipper.setInAnimation(null);
	                    flipper.setOutAnimation(null);
	                    while(jumpreversevec.contains(""+(thispage-1))){
	                    	flipper.showPrevious();
	                    	thispage--;
	                    }
	                    flipper.setInAnimation(animateInFrom(RIGHT));
	                    flipper.setOutAnimation(animateOutTo(LEFT));
	                    flipper.showPrevious();
	                    setFocus(thispage, -1);
	                    thispage--;
                    }
	        		
	        	//}
	        	if(usesgps)
	        		setGPSMenu();
	        }

	    };
	    
	    
	    
	 /*   private void confirmHome(){
	    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    	alertDialog.setTitle("Confirm");
	    	alertDialog.setMessage("Any unsaved data will be lost. Are you sure?");
	        alertDialog.setButton("Yes", new DialogInterface.OnClickListener(){
	             public void onClick(DialogInterface dialog, int whichButton) {
	            	goHome();
	             }
	        });

	        	alertDialog.setButton2("No", new DialogInterface.OnClickListener(){
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			return;
	        		}
	        	});
	       // }
	        alertDialog.show();	
		} */
    
	 /*  private void goHome(){
	    	Intent i;
	    	if(!fromlist){
	    		i = new Intent(this, Epi_collect.class);
	 	   		startActivity(i);
	    	}
	    	else{
	    		i = new Intent(this, ListRecords.class);
	    		
	    		i.putExtra("table", coretable);
	    		i.putExtra("select_table_key_column", list_select_table);
	    		i.putExtra("foreign_key", list_foreign_key);

	    		for(String key : thisspinnerhash.keySet()){
	    			i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
	    			}
	    		
	    	    startActivity(i);
	    	}
	    } */
	    
	    
	   
	   @Override
	    public boolean onTouchEvent(MotionEvent event) {
	        if (gestureDetector.onTouchEvent(event))
	                return true;
	        else
	                return false;
	    }

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

	    class MyGestureDetector extends SimpleOnGestureListener {

	        // from:
	        // http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/

	        private static final int SWIPE_MIN_DISTANCE = 120;
	        private static final int SWIPE_MAX_OFF_PATH = 250;
	        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	                try {
	                		int jumpnum;
	                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	                                return false;
	                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
	                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                                // right to left swipe
	                                                	
	                        	if(thispage != lastpage && !audioactive && checkDoubleInput() && checkValidDate() && checkEntry() && checkRe() && checkMatch() && checkMinMax() && checkMinMax2()){ // && checkTotals(thispage)){
	                        		
	                        		if(canedit)
	                        			storeData(3);
	                        		// Reset value for checkEntry
	                        		secondcheck = false;
	                        		
	                        		//removeFocus();
	                        		flipper.setInAnimation(animateInFrom(RIGHT));
	                        		flipper.setOutAnimation(animateOutTo(LEFT));
	                        		jumpnum = checkJump();
	                        		// Only check for the second jump option if the first doesn't exist
	            	        		if(jumpnum == 0)
	            	        			jumpnum = checkJump2();
	            	        		if(jumpnum == 0){
	            	        			flipper.showNext();
	            	        			setFocus(thispage, 1);
	            	        			thispage++;
	            	        			// Need to remove entries in case new data has been added
	            	        			if(jumpreversevec.contains(""+thispage))
	            	        				jumpreversevec.remove(""+thispage);
	            	        			// If this is a required field as it is no longer jumped
	            	        			// it must be put back
	            	        			if(storedrequiredfields.contains(allitemposhashrev.get(thispage)))
	            	        				requiredfields.addElement(allitemposhashrev.get(thispage));
	            	        			if(storedrequiredspinners.contains(allitemposhashrev.get(thispage)))
	            		        			requiredspinners.addElement(allitemposhashrev.get(thispage));
	            	        			if(storedrequiredradios.contains(allitemposhashrev.get(thispage)))
	            		        			requiredradios.addElement(allitemposhashrev.get(thispage));
	            	        			if(gpstagstoskip.contains(allitemposhashrev.get(thispage)))
            		        				gpstagstoskip.remove(allitemposhashrev.get(thispage));
	            	        			// For John - IGNORE THIS as storeDate(3) called anyway
	            	        			//tempStore();
	            	        		}
	            	        		else{// If the jump is to stay on the same page
	            	        			if(thispage == jumpnum)
	            	        				return false;
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
	            		        			if(requiredspinners.contains(allitemposhashrev.get(thispage)))
	            			        			requiredspinners.remove(allitemposhashrev.get(thispage));
	            		        			if(requiredradios.contains(allitemposhashrev.get(thispage)))
	            			        			requiredradios.remove(allitemposhashrev.get(thispage));
	            		        			if(gpstags.contains(allitemposhashrev.get(thispage)))
	            		        				gpstagstoskip.addElement(allitemposhashrev.get(thispage));
	            	                    }
	            	                    //jumpreversevec.add(""+thispage);
	            	                    flipper.setInAnimation(animateInFrom(RIGHT));
	            	                    flipper.setOutAnimation(animateOutTo(LEFT));
	            	                    flipper.showNext();
	            	                    setFocus(thispage, 1);
	            	                    thispage++;
	            	                    
	            	                 // Need to remove entries in case new data has been added
	            	        			if(jumpreversevec.contains(""+thispage))
	            	        				jumpreversevec.remove(""+thispage);
	            	                 // For John - IGNORE THIS as storeDate(3) called anyway
	            	                  // tempStore();
	            	        		}
	                        	}                             
	                        	
	                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
	                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	                                // left to right swipe
	                        	// Only need total check on next. Must be able to go back to fix error
	                        	if(thispage != 1 && !audioactive){ // && checkDoubleInput() && checkValidDate() && checkEntry() && checkRe()){
	                        		
	                        		//storeData(3);
	                        		// Reset value for checkEntry
	                        		secondcheck = false;
	                        		//removeFocus();
	                        		flipper.setInAnimation(animateInFrom(LEFT));
	                                flipper.setOutAnimation(animateOutTo(RIGHT));
	                                if(!jumpreversevec.contains(""+(thispage-1))){
	                                	flipper.showPrevious();
	                                	setFocus(thispage, -1);
	                                	thispage--;
	                                }
	                                else{
	                                	flipper.setInAnimation(null);
	            	                    flipper.setOutAnimation(null);
	            	                    while(jumpreversevec.contains(""+(thispage-1))){
	            	                    	flipper.showPrevious();
	            	                    	thispage--;
	            	                    }
	            	                    flipper.setInAnimation(animateInFrom(RIGHT));
	            	                    flipper.setOutAnimation(animateOutTo(LEFT));
	            	                    flipper.showPrevious();
	            	                    setFocus(thispage, -1);
	            	                    thispage--;
	                                }
	                        	}
	                            
	                        }
	                } catch (Exception e) {}
	                setGPSMenu();
	                
	               return false;
	        }
	    }
	    
	  // For John - IGNORE THIS as storeDate(3) called anyway
	  /*  private void tempStore(){
	    	if((textviewhash.get(coretablekey).getText().toString() == null) || (textviewhash.get(coretablekey).getText().toString().equalsIgnoreCase(""))){
	    		//showAlert("Entry required for primary key field: "+ coretableview, "Error");
	    		return;
	    	}
	    	
	    	storeData(3);
	    } */
	    
	    // For John to here

	    private void setFocus(int page, int type){
	    	
	    	//Log.i("FOCUS CHECK 1", ""+page+ " TEXT "+ textviewposhash.get(page).getText().toString());
	    	if(textviewposhash.get(page) != null){
	    		textviewhash.get(textviewposhash.get(page)).clearFocus();
	    		if(textviewposhash.get(page+type) == null || !textviewhash.get(textviewposhash.get(page+type)).isFocusable()){
	    			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    			imm.hideSoftInputFromWindow(textviewhash.get(textviewposhash.get(page)).getWindowToken(), 0);
	    		}
	    	}
	    	if(textviewposhash.get(page+type) != null && textviewhash.get(textviewposhash.get(page+type)).isFocusable()){
	    		int p = page + type;
	    		//Log.i("SET FOCUS", "PAGE IS: "+p);
	    		textviewhash.get(textviewposhash.get(page+type)).requestFocus();
	    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    		imm.showSoftInput(textviewhash.get(textviewposhash.get(page+type)), 0);
	    	}

	    	//if(textviewposhash.get(page+type) != null)
		    //	textviewhash.get(textviewposhash.get(page+type)).requestFocus();

	    	//if(textviewhash.get(page+type) != null)
	    	//	textviewhash.get(textviewposhash.get(page)).requestFocus();

	    }
	    
	    private boolean checkDoubleInput(){ //int thispage){
	    	
	    	if(doublecheckhash.get(thispage) != null){
	    		if(!doublecheckhash.get(thispage).getText().toString().equalsIgnoreCase(doublecheckhash2.get(thispage).getText().toString())){
	    			showAlert(this.getResources().getString(R.string.different_values), this.getResources().getString(R.string.error)); //"Values are different", "Error");
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	    
	    private int checkJump(){ //int thispage){
	    
	    	String val, comp, query, selected;
	    	int pos; //, selected;

	    	//String project_version = dbAccess.getValue("project_version");
	    	
	    	//boolean isjump1 = true;
	    	//if(jumps.keySet().size() > 0)
	    	//	isjump1 = false;
	    		    	
	    	String[] temp;
	    	if(textviewposhash.containsKey(thispage) && jumps.containsKey(textviewposhash.get(thispage))){ //((version <= 1.0 && jumps.containsKey(textviewposhash.get(thispage))) || (version > 1.0 && jumps1.containsKey(textviewposhash.get(thispage))))){
	    		//Log.i("JUMP CHECK 2", textviewposhash.get(thispage));
	    		query = textviewposhash.get(thispage);
	    		//if(version > 1.0)
	    			temp = jumps.get(query).split(",");
	    		//else
	    		//	temp = jumps1.get(query).split(",");
	    			
	    		for(int i = 1; i < temp.length; i+=2){
	    			//Log.i("JUMP CHECK 3", jumps.get(query)[i]+" ");
	    			val = temp[i+1];
	    			comp = textviewhash.get(allitemposhashrev.get(thispage)).getText().toString();
	    			if(val.equalsIgnoreCase("All") || ((comp == null || comp.length() == 0) && val.equalsIgnoreCase("NULL")) || comp.equalsIgnoreCase(val)){
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
	    		return 0;
	    	}
	    	else if(spinnerposhash.get(thispage) != null && jumps.containsKey(spinnerposhash.get(thispage))){ //((version <= 1.0 && jumps.containsKey(spinnerposhash.get(thispage))) || (version > 1.0 && jumps1.containsKey(spinnerposhash.get(thispage))))){
	    			    		
	    		query = spinnerposhash.get(thispage);
	    		//if(isjump1)
	    		//	temp = jumps1.get(query).split(",");
	    		//else
	    			temp = jumps.get(query).split(",");
	    			
	    		//val = jumps.get(query)[1];
	    		//Log.i("JUMP CHECK 4", query+" "+temp[0]);
	    		for(int i = 1; i < temp.length; i+=2){
	    			//Log.i("JUMP CHECK 3", temp[i]+" "+temp[i+1]);
	    			val = temp[i+1];
	    			if(val.equalsIgnoreCase("All")){
	    				if(temp[i].equalsIgnoreCase("End"))
	    					return lastpage;
	    				//else
	    				//	return allitemposhash.get(temp[i]); //jumps.get(query)[0]);
	    				else{
	    					try{
	    						return allitemposhash.get(temp[i]);
	    					}
	    					catch(NullPointerException npe){
	    						return 0;
	    					}
	    				}
	    			}
	    		
	    			//Log.i("SPINNER JUMP CHECK 3", val+ " "+query+" "+jumps.get(query)[0]);
	    			boolean not = false;
	    			if(val.startsWith("!"))
	    				not = true;
	    			val = val.replaceAll("!", "");
	    			if(project_version <= 1.0){
	    					try{
	    						pos = Integer.parseInt(val);
	    			  			}
	    					catch(NumberFormatException npe){
	    						showAlert(this.getResources().getString(R.string.jump_error_1) +" " +spinnerposhash.get(thispage)+" "+this.getResources().getString(R.string.jump_error_2), this.getResources().getString(R.string.error));  //There is an error in the XML. Entry \"jump\" for "+spinnerposhash.get(thispage)+" must be an integer", "Error");
	    						return 0;
	    					}
	    					int sel = thisspinnerhash.get(allitemposhashrev.get(thispage)).getSelectedItemPosition();	    	    			
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
	    				
	    				/*String[] tempstring = dbAccess.getValue(coretable, "spinner_"+spinnerposhash.get(thispage)).split(",,");
	    	    		String[] tempstring2 = dbAccess.getValue(coretable, "spinner_values_"+spinnerposhash.get(thispage)).split(",,");
	    	    		
	    	    		
	    	    		String value;
	    	    		value = thisspinnerhash.get(spinnerposhash.get(thispage)).getSelectedItem().toString();
	    	    		int j = 0;
	    	    		//boolean match = false;
	    	    		for(j = 0; j < tempstring.length; j++){
	    	    			if(tempstring[j].equalsIgnoreCase(value)){
	    	    				//match = true;
	    	    				break;
	    	    			}
	    	    		}*/
	    				
	    				selected = spinnervalshash.get(spinnerposhash.get(thispage)+"_"+thisspinnerhash.get(allitemposhashrev.get(thispage)).getSelectedItemPosition()); //tempstring2[j]; //thisspinnerhash.get(allitemposhashrev.get(thispage)).getSelectedItem().toString(); //Position();
	    			
	    				//Log.i("SPINNER JUMP", selected +" "+val);
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
	    	else if(radioposhash.get(thispage) != null && jumps.containsKey(radioposhash.get(thispage))){ //((version <= 1.0 && jumps.containsKey(radioposhash.get(thispage))) || (version > 1.0 && jumps1.containsKey(radioposhash.get(thispage))))){
	    		
	    		query = radioposhash.get(thispage);
	    		//if(isjump1)
	    		//	temp = jumps1.get(query).split(",");
	    		//else
	    			temp = jumps.get(query).split(",");
	    		//val = jumps.get(query)[1];
	    		//Log.i("JUMP CHECK 4", jumps.get(query));
	    		for(int i = 1; i < temp.length; i+=2){
	    			//Log.i("JUMP CHECK 3", temp[i]+" "+temp[i+1]);
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
	    					showAlert(this.getResources().getString(R.string.jump_error_1) +" "+radioposhash.get(thispage)+" "+this.getResources().getString(R.string.jump_error_2), this.getResources().getString(R.string.error)); // "There is an error in the XML. Entry \"jump\" for "+radioposhash.get(thispage)+" must be an integer", "Error");
	    					return 0;
	    				}

	    				int rbid = jumpradiohash.get(thispage).getCheckedRadioButtonId();    	
	    				View rb = jumpradiohash.get(thispage).findViewById(rbid);
	    				int sel = jumpradiohash.get(thispage).indexOfChild(rb) + 1;
	    				
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
	    	else if(checkboxposhash.get(thispage) != null && jumps.containsKey(checkboxposhash.get(thispage))){ //((version <= 1.0 && jumps.containsKey(checkboxposhash.get(thispage))) || (version > 1.0 && jumps1.containsKey(checkboxposhash.get(thispage))))){
	    		//val = checkboxposhash.get(thispage);
	    		
	    		//if(val.equalsIgnoreCase("All"))
	    		//	return allitemposhash.get(jumps.get(query)[1]);
	    		Vector<String> checkvec = new Vector<String>();
	    		for(String key : checkboxes){
	    			//Log.i("JUMP CHECK 1", key);
	    			if(checkboxhash.get(key).isChecked())
	    				checkvec.addElement(key);
	    		}
	    		query = checkboxposhash.get(thispage);
	    		temp = jumps.get(query).split(",");
	    		for(int i = 1; i < temp.length; i+=2){
	    			//Log.i("JUMP CHECK 2", temp[i]+" "+temp[i+1]);
	    			val = temp[i+1];
	    			if(val.equalsIgnoreCase("All")){
	    				if(temp[i].equalsIgnoreCase("End"))
	    					return lastpage;
	    				//else
	    				//	return allitemposhash.get(temp[i]); //jumps.get(query)[0]);
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
	    			val = checkboxposhash.get(thispage)+"_"+val;
	    				    		
	    			//Log.i("JUMP CHECK 3", val);
	    			if((not && !checkvec.contains(val)) || (!not && checkvec.contains(val))){
	    				if(temp[i].equalsIgnoreCase("End"))
	    					return lastpage;
	    				//else
	    				//	return allitemposhash.get(temp[i]); //jumps.get(query)[0]);
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
	    
	    private int checkJump2(){ 
		    
	    	/*
			 *  IN ORDER FOR A CHECKBOX LOOKUP TO WORK IT IS SOMETHING LIKE:
			 *  jump2="Location,Location_ID,CHECKBOXGROUP_REF,0,Adult_First_Name"
			 *  OR
			 *  jump2="Location,Location_ID,CHECKBOXGROUP_REF,1,Adult_First_Name"
			 *  
			 *  FOR SELECT1 AND RADIO THE ACTUAL VALUE IS REQUIRED AND NOT THE
			 *  INDEX AS USED FOR "JUMP". THIS IS DUE TO JUMP2 DOING A DATABASE
			 *  LOOKUP IF IT HAS TO CHECK A DIFFERENT TABLE
			 */
	    	
	    	String query = ""; // val, comp, 
	    	//int pos, selected;
	    	String[] temp;
	    	if((textviewposhash.containsKey(thispage) && jumps2.containsKey(textviewposhash.get(thispage))) ||
	    			(spinnerposhash.get(thispage) != null && jumps2.containsKey(spinnerposhash.get(thispage))) ||
	    			(radioposhash.get(thispage) != null && jumps2.containsKey(radioposhash.get(thispage))) ||
	    			(checkboxposhash.get(thispage) != null && jumps2.containsKey(checkboxposhash.get(thispage)))){
	    		if(textviewposhash.containsKey(thispage))
	    			query = textviewposhash.get(thispage);
	    		else if(spinnerposhash.containsKey(thispage))
	    			query = spinnerposhash.get(thispage);
	    		else if(radioposhash.containsKey(thispage))
	    			query = radioposhash.get(thispage);
	    		else if(checkboxposhash.containsKey(thispage))
	    			query = checkboxposhash.get(thispage);
	    		temp = jumps2.get(query).split(",");
	    		
	    		//Log.i("JUMP2 CHECK", "IN JUMP2");
	    		
	    		String key_val = "";
	    		boolean checkboxselected = false;
	    		for(int i = 1; i < temp.length; i+=4){
	    			if(temp[i].equalsIgnoreCase(coretable)){
	    				if(textviewhash.containsKey(temp[i+1]))
	    					key_val = textviewhash.get(temp[i+1]).getText().toString();
	    				else if(thisspinnerhash.containsKey(temp[i+1])){
	    					/*String[] tempstring = dbAccess.getValue(coretable, "spinner_"+temp[i+1]).split(",,");
		    	    		String[] tempstring2 = dbAccess.getValue(coretable, "spinner_values_"+temp[i+1]).split(",,");

		    	    		String value;
		    	    		value = thisspinnerhash.get(temp[i+1]).getSelectedItem().toString();
		    	    		int j = 0;
		    	    		for(j = 0; j < tempstring.length; j++){
		    	    			if(tempstring[j].equalsIgnoreCase(value)){
		    	    				//match = true;
		    	    				break;
		    	    			}
		    	    		}*/
		    				
	    					key_val = spinnervalshash.get(thisspinnerhash.get(temp[i+1])+"_"+thisspinnerhash.get(temp[i+1]).getSelectedItemPosition());// tempstring2[j]; //""+thisspinnerhash.get(temp[i+1]).getSelectedItem(); //Position();
	    				}
	    				else if(thisradiohash.containsKey(temp[i+1]))
	    					key_val = ""+radioselectedhash.get(temp[i+1]);
	    			
	    				
	    				else if(checkboxhash.containsKey(temp[i+1])){
	    					if(checkboxhash.get(temp[i+1]).isChecked())
	    						key_val = "1";
	    					else
	    						key_val = "0";   					

	    				}
	    				
	    				if(key_val.equalsIgnoreCase(temp[i+2]) || checkboxselected){
	    					if(temp[i+3].equalsIgnoreCase("End"))
		    					return lastpage;
		    				//else
		    				//	return allitemposhash.get(temp[i+4]);
	    					else{
	    						try{
	    							return allitemposhash.get(temp[i+3]);
	    						}
	    						catch(NullPointerException npe){
	    							return 0;
	    						}
	    					}
	    				}
	    			}
	    			else{
	    				key_val = "";
	    				// Get primary key fields for jump table
	    				Vector<String> kvec = new Vector<String>();
	    				for(String key : dbAccess.getKeyValue(temp[i]).split(";"))
	    					kvec.addElement(key);
	    	    		
	    				for(String pkey : textviews){
	    		    		if(kvec.contains(pkey)){
	    		    				key_val += ","+pkey+","+textviewhash.get(pkey).getText().toString();
	    		    			}
	    	    		}
	    	    		
	    		    	for(String pkey : spinners){	
	    	    			if(kvec.contains(pkey)){
	    	    				/*String[] tempstring = dbAccess.getValue(coretable, "spinner_"+pkey).split(",,");
			    	    		String[] tempstring2 = dbAccess.getValue(coretable, "spinner_values_"+pkey).split(",,");

			    	    		String value;
			    	    		value = thisspinnerhash.get(pkey).getSelectedItem().toString();
			    	    		int j = 0;
			    	    		for(j = 0; j < tempstring.length; j++){
			    	    			if(tempstring[j].equalsIgnoreCase(value)){
			    	    				//match = true;
			    	    				break;
			    	    			}
			    	    		}*/
			    				
	    	   					key_val += ","+pkey+","+spinnervalshash.get(pkey+"_"+thisspinnerhash.get(pkey).getSelectedItemPosition()); // tempstring2[j]; //thisspinnerhash.get(pkey).getSelectedItem().toString();
	    			    		}
	    		    	}
	    		    	
	    		    	for(String pkey : radios){	    		
	    			    	if(kvec.contains(pkey)){
	    	    				key_val += ","+pkey+","+radioselectedhash.get(pkey);
	    				    	}
	    		    	}
	    	    		
	    	    		key_val = key_val.replaceFirst(",", "");
	    				//if(textviewhash.containsKey(temp[i+1]))
	    				//	key_val = textviewhash.get(temp[i+1]).getText().toString();
	    				
	    			
	    				
	    				if(dbAccess.checkJump2(temp[i], "ecpkey", key_val, temp[i+1], temp[i+2])){ //Village,Village_ID,Cohort,N,Notes))
	    					if(temp[i+3].equalsIgnoreCase("End"))
		    					return lastpage;
		    				//else
		    				//	return allitemposhash.get(temp[i+4]);
	    					else{
	    						try{
	    							return allitemposhash.get(temp[i+3]);
	    						}
	    						catch(NullPointerException npe){
	    							return 0;
	    						}
	    					}
	    				}
	    			}
	    		}

	    	}
	    	return 0;
	    }

	   /* private int checkJump2(){ 
	    	
	    	String query = ""; // val, comp, 
	    	String[] temp;
	    	if((textviewposhash.containsKey(thispage) && jumps2.containsKey(textviewposhash.get(thispage))) ||
	    			(spinnerposhash.get(thispage) != null && jumps2.containsKey(spinnerposhash.get(thispage))) ||
	    			(radioposhash.get(thispage) != null && jumps2.containsKey(radioposhash.get(thispage))) ||
	    			(checkboxposhash.get(thispage) != null && jumps2.containsKey(checkboxposhash.get(thispage)))){
	    		if(textviewposhash.containsKey(thispage))
	    			query = textviewposhash.get(thispage);
	    		else if(spinnerposhash.containsKey(thispage))
	    			query = spinnerposhash.get(thispage);
	    		else if(radioposhash.containsKey(thispage))
	    			query = radioposhash.get(thispage);
	    		else if(checkboxposhash.containsKey(thispage))
	    			query = checkboxposhash.get(thispage);
	    		temp = jumps2.get(query).split(",");
	    		
	    		String key_val = "";
	    		boolean checkboxselected = false;
	    		for(int i = 1; i < temp.length; i+=5){
	    			if(temp[i].equalsIgnoreCase(coretable)){
	    				if(textviewhash.containsKey(temp[i+2]))
	    					key_val = textviewhash.get(temp[i+2]).getText().toString();
	    				else if(thisspinnerhash.containsKey(temp[i+2]))
	    					key_val = ""+thisspinnerhash.get(temp[i+2]).getSelectedItem(); //Position();
	    				else if(thisradiohash.containsKey(temp[i+2]))
	    					key_val = ""+radioselectedhash.get(temp[i+2]);
	    			
	    				
	    				else if(checkboxhash.containsKey(temp[i+2])){
	    					if(checkboxhash.get(temp[i+2]).isChecked())
	    						key_val = "1";
	    					else
	    						key_val = "0";   					

	    				}
	    				
	    				if(key_val.equalsIgnoreCase(temp[i+3]) || checkboxselected){
	    					if(temp[i+4].equalsIgnoreCase("End"))
		    					return lastpage;

	    					else{
	    						try{
	    							return allitemposhash.get(temp[i+4]);
	    						}
	    						catch(NullPointerException npe){
	    							return 0;
	    						}
	    					}
	    				}
	    			}
	    			else{
	    				if(textviewhash.containsKey(temp[i+1]))
	    					key_val = textviewhash.get(temp[i+1]).getText().toString();
	    				
	    				
	    				if(dbAccess.checkJump2(temp[i], temp[i+1], key_val, temp[i+2], temp[i+3])){ //Village,Village_ID,Cohort,N,Notes))
	    					if(temp[i+4].equalsIgnoreCase("End"))
		    					return lastpage;

	    					else{
	    						try{
	    							return allitemposhash.get(temp[i+4]);
	    						}
	    						catch(NullPointerException npe){
	    							return 0;
	    						}
	    					}
	    				}
	    			}
	    		}

	    	}
	    	return 0;
	    } */

	  /*  private int checkJump2(){ 
		    
	    	String query = ""; // val, comp, 
	    	//int pos, selected;
	    	String[] temp;
	    	if((textviewposhash.containsKey(thispage) && jumps2.containsKey(textviewposhash.get(thispage))) ||
	    			(spinnerposhash.get(thispage) != null && jumps2.containsKey(spinnerposhash.get(thispage))) ||
	    			(radioposhash.get(thispage) != null && jumps2.containsKey(radioposhash.get(thispage))) ||
	    			(checkboxposhash.get(thispage) != null && jumps2.containsKey(checkboxposhash.get(thispage)))){
	    		if(textviewposhash.containsKey(thispage))
	    			query = textviewposhash.get(thispage);
	    		else if(spinnerposhash.containsKey(thispage))
	    			query = spinnerposhash.get(thispage);
	    		else if(radioposhash.containsKey(thispage))
	    			query = radioposhash.get(thispage);
	    		else if(checkboxposhash.containsKey(thispage))
	    			query = checkboxposhash.get(thispage);
	    		//Log.i("JUMP2", jumps2.get(query));
	    		temp = jumps2.get(query).split(",");
	    		
	    		String key_val = "";
	    		
	    		for(int i = 1; i < temp.length; i+=5){
	    			if(temp[i].equalsIgnoreCase(coretable)){
	    				if(textviewhash.containsKey(temp[i+2]))
	    					key_val = textviewhash.get(temp[i+2]).getText().toString();
	    				else if(thisspinnerhash.containsKey(temp[i+2]))
	    					key_val = ""+thisspinnerhash.get(temp[i+2]).getSelectedItemPosition();
	    				else if(thisradiohash.containsKey(temp[i+2])){
	    					int page = allitemposhash.get(temp[i+2]);
	    					int rbid = jumpradiohash.get(page).getCheckedRadioButtonId();    	
		    				View rb = jumpradiohash.get(page).findViewById(rbid);
		    				key_val = ""+(jumpradiohash.get(page).indexOfChild(rb) + 1);
	    					//key_val = ""+""+radioselectedhash.get(temp[i+2]);
	    				}
	    			
	    				if(key_val.equalsIgnoreCase(temp[i+3])){
	    					if(temp[i].equalsIgnoreCase("End"))
		    					return lastpage;
		    				else
		    					return allitemposhash.get(temp[i+4]);
	    				}
	    			}
	    			else{
	    				if(textviewhash.containsKey(temp[i+1]))
	    					key_val = textviewhash.get(temp[i+1]).getText().toString();
	    				else if(thisspinnerhash.containsKey(temp[i+1]))
	    					key_val = ""+thisspinnerhash.get(temp[i+1]).getSelectedItemPosition();
	    				else if(thisradiohash.containsKey(temp[i+1])){
	    					int page = allitemposhash.get(temp[i+2]);
	    					int rbid = jumpradiohash.get(page).getCheckedRadioButtonId();    	
		    				View rb = jumpradiohash.get(page).findViewById(rbid);
		    				key_val = ""+(jumpradiohash.get(page).indexOfChild(rb) + 1);
	    					//key_val = ""+""+radioselectedhash.get(temp[i+1]);
	    				}
	    		
	    				if(dbAccess.checkJump2(temp[i], temp[i+1], key_val, temp[i+2], temp[i+3])){ //Village,Village_ID,Cohort,N,Notes))
	    					if(temp[i].equalsIgnoreCase("End"))
		    					return lastpage;
		    				else
		    					return allitemposhash.get(temp[i+4]);
	    				}
	    			}
	    		}

	    	}
	    	return 0;
	    } */
	    
	  /*  private boolean checkTotals(){ //int thispage){
	    	
	    	String[] totals;
	    	int total = 0, toadd = 0;
	    	// Try - in case total is being checked before all required fields have been displayed.
	    	// Therefore, error in xml
	    	try{
	    		if(totalshash.get(thispage) != null){
	    			
	    			totals = totalshash.get(thispage);
	    			try{
	    				total = Integer.parseInt(textviewhash.get(totals[1]).getText().toString());
	    			}
	    			catch(NumberFormatException npe){
	    				showAlert("The total field entry is invalid. It must contain a number", "Error");
	    				return false;
    		    	}
	    			for(int i = 2; i < totals.length; i++){
	    				try{
	    					toadd += Integer.parseInt(textviewhash.get(totals[i]).getText().toString());
	    				}
	    				catch(NumberFormatException npe){
	    					//showAlert("Incorrect number format in one of the entries");
	    		    	}
	    			}
	    			if(total != toadd){
	    				showAlert("Total is incorrect. Check values", "Error");
	    				return false;
	    			}
	    		}
	    	}
	    	catch(NullPointerException npe){
	    		//Log.i("TOTAL CHECK ERROR", "Is total being checked after all required fields have been displayed?");
	    	}
	    	
	    	return true;
	    } */
	    	
	    public boolean checkValidDate(){ //int thispage){
	    	
	    	//Log.i("IN TOTAL CHECK ERROR", "Is total being checked after all required fields have been displayed?");
	    	String date = "";
	    	if(dateshash.get(thispage) != null){
	    		try{
	    			date = dateshash.get(thispage).getText().toString();
	    		}
	    		catch(NullPointerException npe){
	    			date = "";
	    		}
	    	}
	    	else{
	    		return true;
	    	}
	    	    // set date format, this can be changed to whatever format
	    	    // you want, MM-dd-yyyy, MM.dd.yyyy, dd.MM.yyyy etc.
	    	    // you can read more about it here:
	    	    // http://java.sun.com/j2se/1.4.2/docs/api/index.html
	    	     
	    		String dateformat = dateshashformat.get(thispage); //, dateformat2;
	    	    SimpleDateFormat sdf = new SimpleDateFormat(dateformat); // "MM/dd/yyyy");
	    	     
	    	    // declare and initialize testDate variable, this is what will hold
	    	    // our converted string
	    	     
	    	    Date testDate = null;
	    	    //boolean correctformat = false;
	    	    // we will now try to parse the string into date form
	    	    
	    	    // Try dd/MM/yyyy
	    	    try
	    	    {
	    	      testDate = sdf.parse(date);
	    	      //correctformat = true;
	    	    }
	    	 
	    	    // If the format of the string provided doesn't match the format we
	    	    // declared in SimpleDateFormat() we will get an exception
	    	 
	    	    catch (ParseException e){ 
	    	    	showAlert(this.getResources().getString(R.string.invalid_date) +" - ("+dateshashformat.get(thispage).toLowerCase()+")", this.getResources().getString(R.string.error)); //"The date you provided is in an invalid format", "Error");
	    	    	return false;
	    	    	//correctformat = false; 
	    	    }
	    	    
	    	    // Try d/MM/yyyy
	    	    /*if(!correctformat){
	    	    	try
		    	    {
	    	    	  dateformat2 = dateformat.replaceAll("dd", "d");
	    	    	  sdf = new SimpleDateFormat(dateformat2);
		    	      testDate = sdf.parse(date);
		    	      correctformat = true;
		    	    }
		    	    catch (ParseException e){ correctformat = false; }
	    	    }
	    	    
	    	    // Try dd/M/yyyy
	    	    if(!correctformat){
	    	    	try
		    	    {
	    	    	  dateformat2 = dateformat.replaceAll("MM", "M");
	    	    	  sdf = new SimpleDateFormat(dateformat2);
		    	      testDate = sdf.parse(date);
		    	      correctformat = true;
		    	    }
		    	    catch (ParseException e){ correctformat = false; }
	    	    }
	    	 // Try d/M/yyyy
	    	    Log.i("DATE 1", "DATE = "+date+" TEST DATE = "+testDate);
	    	    if(!correctformat){
	    	    	try
		    	    {
	    	    	  dateformat2 = dateformat.replaceAll("MM", "M");
	    	    	  dateformat2 = dateformat2.replaceAll("dd", "d");
	    	    	  sdf = new SimpleDateFormat(dateformat2);
		    	      testDate = sdf.parse(date);
		    	      correctformat = true;
		    	    }
		    	    catch (ParseException e){ correctformat = false; }
	    	    } */
	    	    
	    	    
	    	    // dateformat.parse will accept any date as long as it's in the format
	    	    // you defined, it simply rolls dates over, for example, december 32
	    	    // becomes jan 1 and december 0 becomes november 30
	    	    // This statement will make sure that once the string
	    	    // has been checked for proper formatting that the date is still the
	    	    // date that was entered, if it's not, we assume that the date is invalid
	    	 
	    	    //Log.i("DATE2", "DATE = "+date+" TEST DATE = "+testDate);
	    	    //if (!correctformat){
	    	    //	Log.i("DATE ERROR", "1");
	    	    //	showAlert("The date you provided is in an invalid format", "Error");
	    	    	
	    	    //	return false;
	    	    //}
	    	    if (!sdf.format(testDate).equals(date)){
	    	    	//Log.i("DATE ERROR", "2");
	    	    	showAlert(this.getResources().getString(R.string.invalid_date), this.getResources().getString(R.string.error)); //"The date you provided is in an invalid format", "Error");
	    	    	return false;
	    	    }
	    	     
	    	    // if we make it to here without getting an error it is assumed that
	    	    // the date was a valid one and that it's in the proper format
	    	 
	    	    return true;
	    }
	    
	    private boolean checkRe(){ //int thispage){
		    
	    	String comp, re;

	    	if(textviewposhash.containsKey(thispage) && res.containsKey(textviewposhash.get(thispage))){
	    		re = res.get(textviewposhash.get(thispage));	
	    		comp = textviewhash.get(allitemposhashrev.get(thispage)).getText().toString();
	    		
	    		//Log.i(" RE CHECK", comp+" "+re);
	    		
	    		if(!comp.matches(re)){
	    			showAlert(this.getResources().getString(R.string.re_error), this.getResources().getString(R.string.error)); //"Entry does not match required format", "Error");
	    			return false;
	    		}
	    	} 	    	
	    return true;
	    }
	    
	    private boolean checkMatch(){ //int thispage){
		    
	    	String first = "", second = "", key_val = "", tomatch = "";
	    	String[] match; 
	    	Pattern r;
	    	Matcher m;

	    	/*
	    	There are 2 possibilities to check the ID - match="Person,Person_ID,Person_ID,(\w\d\d\d\d)$" checks against 
	    	the Person_ID in Person table where the Person Person_ID = the Person_ID in this table. Used where it is not 
	    	matching against the primary key. First Person_ID is the primary key, second is the value to match.
			Second is match="Person_ID,(\w\d\d\d\d)$" checks against the Person_ID in this table. Requires Person_ID to 
			be carried through from Person
			*/
			
	    	if(textviewposhash.containsKey(thispage) && matches.containsKey(textviewposhash.get(thispage))){
	    		
	    		match = matches.get(textviewposhash.get(thispage)).split(",");
	    		
	    		// For match in same table: match="Person_ID,(\w\d\d\d\d)$"
	    		if(match.length == 2){
	    			//Log.i("MATCHER 1", "HERE");
	    			r = Pattern.compile(match[1]);
	    		  	      // Now create matcher object.
	    			m = r.matcher(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
	    			if (m.find()) {
	    				first = m.group(0);
	    				//Log.i("MATCHER 1 FIRST", first);
	    			}
	    	      	    
	    			m = r.matcher(textviewhash.get(match[0]).getText().toString());
	    			if (m.find()) {
	    				second = m.group(0);
	    				//Log.i("MATCHER 1 SECOND", second);
	    			}
	    		}
	    		// For match in different table: match="Person,Person_ID,(\w\d\d\d\d)$"
	    		else if(match.length == 3){
	    			
	    			// Get primary key fields for jump table
    				Vector<String> kvec = new Vector<String>();
    				for(String key : dbAccess.getKeyValue(match[0]).split(";"))
    					kvec.addElement(key);
    	    		
    				for(String pkey : textviews){
    		    		if(kvec.contains(pkey)){
    		    				key_val += ","+pkey+","+textviewhash.get(pkey).getText().toString();
    		    			}
    	    		}
    	    		
    		    	for(String pkey : spinners){	
    	    			if(kvec.contains(pkey)){	
    	   					key_val += ","+pkey+","+spinnervalshash.get(pkey+"_"+thisspinnerhash.get(pkey).getSelectedItemPosition()); // tempstring2[j]; //thisspinnerhash.get(pkey).getSelectedItem().toString();
    			    		}
    		    	}
    		    	
    		    	for(String pkey : radios){	    		
    			    	if(kvec.contains(pkey)){
    	    				key_val += ","+pkey+","+radioselectedhash.get(pkey);
    				    	}
    		    	}
    	    		
    	    		key_val = key_val.replaceFirst(",", "");
    	    		
	    			r = Pattern.compile(match[2]);
	    		  	      // Now create matcher object.
	    			m = r.matcher(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
	    			if (m.find()) {
	    				first = m.group(0);
	    			}
	    	      	    
	    			tomatch = dbAccess.checkMatch(match[0], match[1], key_val);
	    			m = r.matcher(tomatch);
	    			if (m.find()) {
	    				second = m.group(0);
	    			}
	    		}
	    		// This one is for the old version: match="Person,Person_ID,Person_ID,(\d\d\d\d)$"
	    		else{
	    			
	    			r = Pattern.compile(match[3]);
  		  	      // Now create matcher object.
	    			m = r.matcher(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
	    			if (m.find()) {
	    				first = m.group(0);
	    			}
	    			if(textviewhash.containsKey(match[1]))
    					key_val = textviewhash.get(match[1]).getText().toString();
	    			
	    			tomatch = dbAccess.checkMatch2(match[0], match[1], key_val, match[2]);
	    				
	    			m = r.matcher(tomatch);
	    			if (m.find()) {
	    				second = m.group(0);
	    			}
	    			
	    		}
	    		if(!first.equalsIgnoreCase(second)){
	    			showAlert(this.getResources().getString(R.string.re_error), this.getResources().getString(R.string.error)); //"Entry does not match required format", "Error");
	    			return false;
	    		}
	    	}
	    	return true;
	    }
	    
	    private boolean checkMinMax(){ //int thispage){
		    
	    	float val = 0, comp = 0;

	    	if(textviewposhash.containsKey(thispage)){
	    		if(mincheck.containsKey(textviewposhash.get(thispage))){
	    			try{
	    				val = Float.valueOf(mincheck.get(textviewposhash.get(thispage)));
	    			}
	    			catch(NumberFormatException nfe){
	    				showAlert(this.getResources().getString(R.string.xml_error), this.getResources().getString(R.string.error)); //"Error in XML form definition", "Error");
	    			}
	    			try{
	    				comp = Float.valueOf(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
	    			}
	    			catch(NumberFormatException nfe){
	    				showAlert(this.getResources().getString(R.string.number_error), this.getResources().getString(R.string.error)); //"Entry is not a number", "Error");
	    				return false;
	    			}
	    			
	    			if(comp < val){
	    				// Don't use val as if this is an integer field don't want to show float value
	    				showAlert(this.getResources().getString(R.string.number_minimum_error)+" - ("+mincheck.get(textviewposhash.get(thispage))+")", this.getResources().getString(R.string.error)); //"Entry is less than required minimum", "Error");
		    			return false;
	    			}
	    		}
	    		
	    		if(maxcheck.containsKey(textviewposhash.get(thispage))){
	    			try{
	    				val = Float.valueOf(maxcheck.get(textviewposhash.get(thispage)));
	    			}
	    			catch(NumberFormatException nfe){
	    				showAlert(this.getResources().getString(R.string.xml_error), this.getResources().getString(R.string.error)); //"Error in XML form definition", "Error");
	    			}
	    			try{
	    				comp = Float.valueOf(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
	    			}
	    			catch(NumberFormatException nfe){
	    				showAlert(this.getResources().getString(R.string.number_error), this.getResources().getString(R.string.error)); //"Entry is not a number", "Error");
	    				return false;
	    			}
	    			
	    			if(comp > val){
	    				showAlert(this.getResources().getString(R.string.number_maximum_error)+" - ("+mincheck.get(textviewposhash.get(thispage))+")", this.getResources().getString(R.string.error)); //"Entry is greater than required maximum", "Error");
		    			return false;
	    			}
	    		}
	    	} 	    	
	    return true;
	    }
	    
	    private boolean checkMinMax2(){
	    	
	    	String query = ""; 
	    	String[] temp;
	    	boolean res = true;
	    	
	    	if(textviewposhash.containsKey(thispage) && (mincheck2.containsKey(textviewposhash.get(thispage)) || maxcheck2.containsKey(textviewposhash.get(thispage)))){
	    		query = textviewposhash.get(thispage);
	    		
	    		if(mincheck2.containsKey(textviewposhash.get(thispage))){
	    			temp = mincheck2.get(query).split(",");
	    			if(!queryMinMax2(temp, true))
	    				res = false;
	    		}
	    		if(maxcheck2.containsKey(textviewposhash.get(thispage))){
	    			temp = maxcheck2.get(query).split(",");
	    			if(!queryMinMax2(temp, false))
	    				res = false;
	    		}
	    	}
	    	
	    	return res;
	    }
	    
	    private boolean queryMinMax2(String[] query, boolean ismin){ 
	    	
	    	//String query = ""; 
	    	//String[] temp;
	    	//boolean ismin = false;
	    	float val = 0, comp = 0;
    		
	    		String key_val = "";
	    		
	    		try{
					comp = Float.valueOf(textviewhash.get(allitemposhashrev.get(thispage)).getText().toString());
				}
				catch(NumberFormatException nfe){
					showAlert(this.getResources().getString(R.string.number_error), this.getResources().getString(R.string.error)); //"Entry is not a number", "Error");
					return false;
				}
	    		
	    		for(int i = 1; i < query.length; i+=4){
	    					    		   				
	    			if(query[i].equalsIgnoreCase(coretable)){
	    				if(textviewhash.containsKey(query[i+1]))
	    					key_val = textviewhash.get(query[i+1]).getText().toString();
    					}
    				
    					if(key_val.equalsIgnoreCase(query[i+2])){
    						try{
    		    				val = Float.valueOf(query[i+3]);
    		    			}
    		    			catch(NumberFormatException nfe){
    		    				showAlert(this.getResources().getString(R.string.xml_error), this.getResources().getString(R.string.error)); //"Error in XML form definition", "Error");
    		    				return false;
    		    			}
    						
    						if(ismin){
    			    			if(comp < val){
    			    				// Don't use val as if an integer field don't want to show float value
    	    						showAlert(this.getResources().getString(R.string.number_minimum_error)+" - ("+query[i+3]+")", this.getResources().getString(R.string.error)); //"Entry is less than required minimum", "Error");
    	    						return false;
    	    					}
    	    				}
    		    		
    	    				else{
    		    				if(comp > val){
    	    						showAlert(this.getResources().getString(R.string.number_maximum_error)+" - ("+query[i+3]+")", this.getResources().getString(R.string.error)); //"Entry is greater than required maximum", "Error");
    	    						return false;
    	    					}
    	    				}
    					}
    				    			
    				else{
    					// Get primary key fields for jump table
    					key_val = "";
	    				Vector<String> kvec = new Vector<String>();
	    				for(String key : dbAccess.getKeyValue(query[i]).split(";"))
	    					kvec.addElement(key);
	    	    		
	    				for(String pkey : textviews){
	    		    		if(kvec.contains(pkey)){
	    		    				key_val += ","+pkey+","+textviewhash.get(pkey).getText().toString();
	    		    			}
	    	    		}
	    	    		
	    		    	for(String pkey : spinners){	
	    	    			if(kvec.contains(pkey)){
	    	   					key_val += ","+pkey+","+thisspinnerhash.get(pkey).getSelectedItem().toString();
	    			    		}
	    		    	}
	    		    	
	    		    	for(String pkey : radios){	    		
	    			    	if(kvec.contains(pkey)){
	    	    				key_val += ","+pkey+","+radioselectedhash.get(pkey);
	    				    	}
	    		    	}
	    	    		
	    	    		key_val = key_val.replaceFirst(",", "");
    					//if(textviewhash.containsKey(temp[i+1]))
    					//	key_val = textviewhash.get(temp[i+1]).getText().toString();
    				
    					// The key_val is the primary key value and this is always a text field
    				
	    	    		//try{
		    			//	val = Integer.valueOf(query[i+3]);
		    			//}
		    			//catch(NumberFormatException nfe){
		    			//	showAlert(this.getResources().getString(R.string.xml_error), this.getResources().getString(R.string.error)); //"Error in XML form definition", "Error");
		    			//}
	    	    		
    					if(dbAccess.checkJump2(query[i], "ecpkey", key_val, query[i+1], query[i+2])){ //Village,Village_ID,Cohort,N,Notes))
    						try{
    		    				val = Float.valueOf(query[i+3]);
    		    			}
    		    			catch(NumberFormatException nfe){
    		    				showAlert(this.getResources().getString(R.string.xml_error), this.getResources().getString(R.string.error)); //"Error in XML form definition", "Error");
    		    				return false;
    		    			}
    						
    						//Log.i("VAL: ", val+ " COMP "+comp);
    						if(ismin){
    			    			if(comp < val){
    	    						showAlert(this.getResources().getString(R.string.number_minimum_error)+" - ("+query[i+3]+")", this.getResources().getString(R.string.error)); //"Entry is less than required minimum", "Error");
    	    						return false;
    	    					}
    	    				}
    		    		
    	    				else{
    		    				if(comp > val){
    	    						showAlert(this.getResources().getString(R.string.number_maximum_error)+" - ("+query[i+3]+")", this.getResources().getString(R.string.error)); //"Entry is greater than required maximum", "Error");
    	    						return false;
    	    					}
    	    				}
    					}

    				}

   		
    				
	    		}
	    	//}
	    	//else
	    	//	Log.i("MINMAX CHECK", "NO MATCH");
	    	
	    return true;
	    }
	    
	    
	    private void flipTo(String target){
	    	
	    	flipper.setInAnimation(null);
            flipper.setOutAnimation(null);
            
	    	while(thispage != allitemposhash.get(target)){
	    		//flipper.showNext();
	    		//thispage++;
	    		flipToNext(0);
	    	}
	    		
	    	// Just in case ...
	    	if(thispage == lastpage)
	    		return;
	    		    	
	    }
	    
	  /*  private boolean checkPrimaryKeyComplete(){
	    	
	    	for(String key : textviews){
				if((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase(""))){
					return false;
				}
				if(primary_keys.contains(key)){
					primary_key += ","+key+","+textviewhash.get(key).getText().toString();
		    		}
	    	}
			
	    	primary_key = primary_key.replaceFirst(",", "");
			if(dbAccess.checkValue(coretable, "pkey", primary_key)){
				return false;
			}
			
	    	return true;
	    }*/
	    
	  /*  private void checkPrimaryKeyExists(){
	    	
	    	String primary_key_check = "";
	    	boolean keyexists = true;
	    	
	    	// Key values must be complete for this method to be called so don't need this
	    	//for(String key : textviews){
			//	if(primary_keys.contains(key) && ((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase("")))){
			//		keyexists = false;
			//	}
			//}
	    	
	    	// If all key values are set check if it already exists
	    	//if(keyexists){
	    		for(String key : textviews){
	    			if(primary_keys.contains(key)){
	    				primary_key_check += ","+key+","+textviewhash.get(key).getText().toString();
		    			}
	    		}
			
	    		String key_to_check = primary_key_check.replaceFirst(",", "");
	    		if(!dbAccess.checkValue(coretable, "pkey", key_to_check)){
	    			keyexists = false;
	    		}
	    	//}
			
	    		// For John - IGNORE THIS ONE
	    	if(keyexists){
			
	    		primary_key_check = primary_key_check.replaceFirst(",", "(");
	    		primary_key_check += ")";
			
	    		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    		alertDialog.setTitle("Warning");
			
	        	alertDialog.setMessage("Record with this primary key "+primary_key_check+" already exists. Do you want to overwite?");
	        	alertDialog.setButton("Yes", new DialogInterface.OnClickListener(){
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			if(usesgps)
	        				removeGPSUpdates();
	        			alldata = 1;
	    	    		if(confirmData(1)){
	    	    			// It's from a branch
	                		if(isnew == 2){
	                			setResult(RESULT_OK, getIntent());
	                			finish();
	                		}
	                		else if(!fromdetails){
	    	    				selectTable(false);
	    	    			}
	    	    			else{
	    	    				Bundle extras = getIntent().getExtras();
	    	    				getIntent().putExtras(extras);
	    	    				extras.putString("target", extras.getString("target"));
	    	    				setResult(RESULT_OK, getIntent());
	    	    				finish();
	    	    			}            			
	    	    		}
	        		}
	        	});
	        	
	            alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener(){
	            	public void onClick(DialogInterface dialog, int whichButton) {
	            		return;
	            	}
	            });
	        
	            alertDialog.setButton3("No", new DialogInterface.OnClickListener(){
	            	public void onClick(DialogInterface dialog, int whichButton) {
	            		if(usesgps)
	                    	removeGPSUpdates();
	            		// It's from a branch
	            		if(isnew == 2){
	            			setResult(RESULT_OK, getIntent());
	            			finish();
	            		}
	            		else if(!fromdetails){
	            			selectTable(false); // false
	            		}
	            		else{
	            			Bundle extras = getIntent().getExtras();
	            			getIntent().putExtras(extras);
	            			extras.putString("target", extras.getString("target"));
	            			setResult(RESULT_OK, getIntent());
	            			finish();
	            		}
	            	}
	            });
	            
	            alertDialog.show();
	    	} 
	    	else{
	    		if(usesgps)
	    			removeGPSUpdates();
	    		alldata = 1;
	    		if(confirmData(1)){
	    			// It's from a branch
            		if(isnew == 2){
            			dbAccess.close();
            			setResult(RESULT_OK, getIntent());
            			dbAccess.close();
            			finish();
            		}
            		else if(!fromdetails){
	    				selectTable(false);
	    			}
	    			else{
	    				dbAccess.close();
	    				Bundle extras = getIntent().getExtras();
	    				getIntent().putExtras(extras);
	    				extras.putString("target", extras.getString("target"));
	    				setResult(RESULT_OK, getIntent());
	    				dbAccess.close();
	    				finish();
	    			}            			
	    		}
	    	} 
	    	// For John To Here
	    	
	    	
			
	    } */
	    
	    private boolean confirmData(int type){
			
			//gpssettingshash.put(tag+"_"+lat, gpslocation.getLatitude());
			//gpssettingshash.put(tag+"_"+lon, gpslocation.getLatitude());
			//gpssettingshash.put(tag+"_"+alt, gpslocation.getLatitude());
			//gpssettingshash.put(tag+"_"+gpsacc, gpslocation.getLatitude());
	    	               	
	    	String result = "", gpsresult = "";//, pkey_fields = "", pkeytest = ""
	    	primary_key = "";
	    	boolean store = true;
	    	for(String key : textviews){
	    		//Log.i("ERROR", key);
				if((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase(""))){
					//if(key.equalsIgnoreCase("GPS")){
					//	gpsresult += " WARNING: GPS NOT SET.";
					//	store = false;
					//}
					//else 
					if(primary_keys.contains(key)){
			    		showAlert(this.getResources().getString(R.string.primary_key_required), this.getResources().getString(R.string.error)); //"Entry required for primary key field", "Error");
						alldata = 1;
						return false;
			    	}
					else if(requiredfields.contains(key)){
						result += " " + key;
						store = false;
					}
				}
				//if(primary_keys.contains(key)){
				//	primary_key += ","+key+","+textviewhash.get(key).getText().toString();
					//pkey_fields += ","+key;
		    	//	}
			}
	    	
	   	
	    	for(String key : gpstags){
				if(!gpstagstoskip.contains(key)){
					if((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase(""))){
						gpsresult += this.getResources().getString(R.string.gps_warning)+" "+key+". "; //"WARNING: LOCATION NOT SET FOR "+key+". ";
						store = false;
						if(requiredfields.contains(key)){
							result += " " + key;
							store = false;
						}
					}
				}
			}
	    	
	    	for(String key : photos){
				if((imageviewvalhash.get(key) == null) || (imageviewvalhash.get(key).equalsIgnoreCase("-1"))){
					if(requiredfields.contains(key)){
						result += "  "+key +" - ("+this.getResources().getString(R.string.photo)+")"; //(Photo)";
						store = false;
					}
				}
			}
			
	    	for(String key : videos){
				if((videoviewvalhash.get(key) == null) || (videoviewvalhash.get(key).equalsIgnoreCase("-1"))){
					if(requiredfields.contains(key)){
						result += "  "+key +" - ("+this.getResources().getString(R.string.video)+")"; //(Video)";
						store = false;
					}
				}
			}
	    	
	    	for(String key : audio){
				if((audioviewvalhash.get(key) == null) || (audioviewvalhash.get(key).equalsIgnoreCase("-1"))){
					if(requiredfields.contains(key)){
						result += "  "+key +" - ("+this.getResources().getString(R.string.audio)+")"; //(Audio)";
						store = false;
					}
				}
			}
	    	
			for(String key : spinners){
				if(thisspinnerhash.get(key).getSelectedItemPosition() == 0){
					if(primary_keys.contains(key)){
						showAlert(this.getResources().getString(R.string.primary_key_required), this.getResources().getString(R.string.error));
						alldata = 1;
						return false;
			    	}
					if(requiredspinners.contains(key)){
						result += " " + key;
						store = false;
					}
				}
				//if(primary_keys.contains(key)){
					//primary_key += ","+key+","+thisspinnerhash.get(key).getSelectedItem().toString();
					//pkey_fields += ","+key;
		    		//}
			}
			
			for(String key : radios){
				if(radioselectedhash.get(key).equalsIgnoreCase("")){
					if(primary_keys.contains(key)){
						showAlert(this.getResources().getString(R.string.primary_key_required), this.getResources().getString(R.string.error));
						alldata = 1;
						return false;
			    	}
					if(requiredradios.contains(key)){
						result += " " + key;
						store = false;
					}
				}
				///if(primary_keys.contains(key)){
					//primary_key += ","+key+","+radioselectedhash.get(key);
					//pkey_fields += ","+key;
		    		//}
			}
			
			// If the record is being edited don't want a warning
			// For John - IGNORE THIS
			// This is now checked when Next is pressed so don't uncomment 
			/*if(!fromdetails){
				primary_key = primary_key.replaceFirst(",", "");
				if(dbAccess.checkValue(coretable, "pkey", primary_key)){
					pkey_fields = pkey_fields.replaceFirst(",", "(");
					pkey_fields += ")";
					pkeytest = "Entry exists for this primary key "+pkey_fields+"."; //. Tap store again to continue and confirm overwrite";
					//alldata = 1;
					store = false;
				}
			} */
			// Back button has been pressed so need to check primary key value
			//else if(type ==1){
				
			//}

			if(!store && alldata == 0){
				//if(pkeytest.length() > 0)
				//	showAlert(pkeytest, "Error");
				//else 
				if(result.length() > 0){
					//if(pkeytest.length() > 0)
					//	showAlert(pkeytest+" "+this.getResources().getString(R.string.entries_required_for)+": "+result +". "+gpsresult+ " "+this.getResources().getString(R.string.tap_store_to_continue), this.getResources().getString(R.string.error)); //   " Entries required for: "+ result +". "+gpsresult+" Tap store again to continue", "Error");
					//else
						showAlert(this.getResources().getString(R.string.entries_required_for)+": "+result +". "+gpsresult+ " "+this.getResources().getString(R.string.tap_store_to_continue), this.getResources().getString(R.string.error)); // "Entries required for: "+ result +". "+gpsresult+" Tap store again to continue", "Error");
				}
				else{
					//if(pkeytest.length() > 0)
					//	showAlert(pkeytest+" "+this.getResources().getString(R.string.tap_store_to_continue), this.getResources().getString(R.string.error)); // " Tap store again to continue", "Error");
					//else
						showAlert(gpsresult+" "+this.getResources().getString(R.string.tap_store_to_continue), this.getResources().getString(R.string.error)); // " Tap store again to continue", "Error");
				}
				alldata = 1;
				return false;
			}
						
			//else if(gpsresult.length() > 0){
			//	showAlert(gpsresult+" Tap store again to continue");
			//	alldata = 1;
			//	return false;
			//}
			
	    	alldata = 0;
	    	

	    	//if((textviewhash.get(coretablekey).getText().toString() == null) || (textviewhash.get(coretablekey).getText().toString().equalsIgnoreCase(""))){
	    	//	showAlert("Entry required for primary key field: "+ coretableview, "Error");
			//	alldata = 1;
			//	return false;
	    	//}
	    	
	        /*for(String key : spinners){
	        	extras.putInt(key, thisspinnerhash.get(key).getSelectedItemPosition());
	        }*/
	        
	        for(String key : checkboxes){
	        	if(checkboxhash.get(key).isChecked()){
	        		//extras.putBoolean(key, true);
	        	}
	        	else{
	        		//extras.putBoolean(key, false);
	        	}
	        }

	        /*extras.putInt("isstored", 0);
	        getIntent().putExtras(extras);
	        setResult(RESULT_OK, getIntent());
	        finish();*/
	    	
	        
	        
	    	storeData(type);
	    	return true;
	  
		}
	    
	    private void storeData(int type){ //int full){
	    	
	    	String message = "", entry, ecjumped = "";
	    	
	    	primary_key = "";
	    	title = "";
	    	//String titletext = "";
	    	
	    // Use a hash to get these values from strings.xml
	    	HashMap<String, String> rowhash = new HashMap<String, String>();
	    	//HashMap<String, String> imagerowhash = new HashMap<String, String>();
	    	//HashMap<String, String> videorowhash = new HashMap<String, String>();
	    	//HashMap<String, String> audiorowhash = new HashMap<String, String>();
	    	Vector<String> branchtodelete = new Vector<String>();
	    	//rowhash.put("rowId", ""+id);
	    	//rowhash.put("remoteId", ""+remoteid);
	    	
	    	if(type == 3){
	    		for(String key : textviews){
	    			//if(primary_keys.contains(key))
	    				//	Log.i("KEY TEXTVIEW", key+" "+textviewhash.get(key).getText().toString());
	    			if(primary_keys.contains(key) && ((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase("")))){
	    				return;
	    			}
	    		}
	    		for(String key : spinners){
	    			//if(primary_keys.contains(key))
    					//Log.i("KEY SPINNER", key);
		    		if(primary_keys.contains(key) && (thisspinnerhash.get(key).getSelectedItemPosition() == 0)){
		    			return;
		    		}
	    		}
		    	for(String key : radios){
		    		//if(primary_keys.contains(key))
    					//Log.i("KEY RADIO", key + " X"+radioselectedhash.get(key)+"X");
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
	    	//if(dbAccess.getValue(coretable, "genkey") != null && dbAccess.getValue(coretable, "genkey").length() > 0){
	    	//	title += "- "+phonekey + " "; 
	        //}
 	    	
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
	    			
	    			//Log.i("STORING", key+" "+textviewhash.get(key));
	    			/*if(key.equalsIgnoreCase("GPS")){
	    				rowhash.put("'eclat'", lat);
	    				rowhash.put("'eclon'", lon);
	    				rowhash.put("'ecalt'", alt);
	    				rowhash.put("'ecgpsacc'", gpsacc);
	    				if(listfields.contains(key) && lat.length() > 2){
	    					title += "- LAT: " + lat ;
	    					title += "LON: " + lon ;
	    					title += "ACC: " + gpsacc ;
	    					title += "ALT: " + alt ;
	    				}
	    			}
	    			else*/
	    			if(textviewhash.get(key) == null){
	    				rowhash.put("'"+key+"'", "");
	    				
	    			}
	    			else{
	    				entry = textviewhash.get(key).getText().toString();
	    				entry = entry.replaceAll("\\\\n", " ");
	    				//Log.i("KEY", key +" VALUE "+entry);
	    				rowhash.put("'"+key+"'", entry); //textviewhash.get(key).getText().toString());
	    				if(listfields != null && listfields.contains(key) && textviewhash.get(key).getText().toString().length()>0){
	    					//titletext = entry.replaceAll("_", " ");
	    					title += "- "+ textviewhash.get(key).getText().toString() + " "; // textviewhash.get(key).getText().toString()
	    					Log.i("TITLE KEY " + key, title);
	    				}
	    				if(primary_keys.contains(key)){
	    					primary_key += ","+key+","+entry;
	    		    		}
	    				}
	    			}
	    		}
	    	
	    	if(gpstags != null){
	    		for(String key : gpstags){
	    			if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    				rowhash.put("'"+key+"_lat'", "");
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
	    	 
	    	for(String key : spinners){
	    			    		
	    		if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    			rowhash.put("'"+key+"'", "Null");
	    			//ecjumped += ","+key;
	    			continue;
	    		}
	    		
	    		/*String[] tempstring = dbAccess.getValue(coretable, "spinner_"+key).split(",,");
	    		String[] tempstring2 = dbAccess.getValue(coretable, "spinner_values_"+key).split(",,");

	    		String value;
	    		value = thisspinnerhash.get(key).getSelectedItem().toString();
	    		int i = 0;
	    		//boolean match = false;
	    		for(i = 0; i < tempstring.length; i++){
	    			if(tempstring[i].equalsIgnoreCase(value)){
	    				//match = true;
	    				break;
	    			}
	    		}*/
	    		
	    		rowhash.put("'"+key+"'", spinnervalshash.get(key+"_"+thisspinnerhash.get(key).getSelectedItemPosition())); //    ""+tempstring2[i]); //thisspinnerhash.get(key).getSelectedItem().toString());
	    		//Log.i("SPINNER STORE", key + " IS "+tempstring2[i]);
	    		
	    		if(listspinners != null && listspinners.contains(key) && !(thisspinnerhash.get(key).getSelectedItemPosition() == 0)){ // && match){ // spinnersvalueshash.get(key)[thisspinnerhash.get(key).getSelectedItemPosition()]).equalsIgnoreCase("Select")
					title += "- "+ thisspinnerhash.get(key).getSelectedItem().toString() + " "; // (spinnersvalueshash.get(key)[thisspinnerhash.get(key).getSelectedItemPosition()]) + " ";
	    			//title += "- "+spinnersvalueshash.get(thisspinnerhash.get(key).getSelectedItemPosition()) + " ";
	    		}
	    		if(primary_keys.contains(key)){
					primary_key += ","+key+","+spinnervalshash.get(key+"_"+thisspinnerhash.get(key).getSelectedItemPosition()); //value;
		    		}
	         }
	    	
	    	for(String key : radios){
	    		if(jumpreversevec.contains(""+allitemposhash.get(key))){
	    			rowhash.put("'"+key+"'", "Null");
	    			//ecjumped += ","+key;
	    			continue;
	    		}
	    		
	    		rowhash.put("'"+key+"'", ""+radioselectedhash.get(key));
	    		
	    		//String[] tempstring = dbAccess.getValue(coretable, "radio_"+key).split(",,");
	    		//String[] tempstring2 = dbAccess.getValue(coretable, "radio_values_"+key).split(",,");

	    		String value;
	    		value = radioselectedhash.get(key);
	    		//int i;
	    		//boolean match = false;
	    		//for(i = 1; i < tempstring2.length; i++){
	    		//	if(tempstring2[i].equalsIgnoreCase(value)){
	    		//		match = true;
	    		//		break;
	    		//	}
	    		//}
	    		
	    		if(listradios != null && listradios.contains(key) && !value.equalsIgnoreCase("")){ //) && match){
					title += "- "+ value + " "; //tempstring[i] + " "; //radioselectedhash.get(key) + " "; // (radiosvalueshash.get(key)[radioselectedhash.get(key)]) + " ";
	    			//title += "- "+spinnersvalueshash.get(thisspinnerhash.get(key).getSelectedItemPosition()) + " ";
	    		}
	    		
	    		if(primary_keys.contains(key)){
					primary_key += ","+key+","+value;
		    		}

	         }
	         
	         for(String key : checkboxes){
	        	 if(jumpreversevec.contains(""+allitemposhash.get(key))){
	        		// ecjumped += ","+key;	
	        		 continue;
	        	 }
	        	 
	         	if(checkboxhash.get(key).isChecked()){
	         		rowhash.put("'"+key+"'", "1");
	         		if(listcheckboxes.contains(key))
	         			title += "- "+key+": " + "1 ";
	         	}
	         	else{
	         		rowhash.put("'"+key+"'", "0");
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
	        			//imagerowhash.put("id", imageviewvalhash.get(key));
	        			dbAccess.createFileRow("Image", imageviewvalhash.get(key));
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
		        			//videorowhash.put("id", videoviewvalhash.get(key));
		        			dbAccess.createFileRow("Video", videoviewvalhash.get(key));
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
		        			//audiorowhash.put("id", audioviewvalhash.get(key));
		        			dbAccess.createFileRow("Audio", audioviewvalhash.get(key));
		        		}
			        }
		         }
	         
	    	//String photo = photoid;
      
	    	//rowhash.put("photo", photo);
	    	//rowhash.put("date", date);
	    	rowhash.put("ecremote", ""+0);
	    	rowhash.put("ecstored", "N");
	    	
	    	ecjumped = ecjumped.replaceFirst(",", "");
	    	rowhash.put("ecjumped", ecjumped);
	    	
	    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	       	   	
	       	String ecdate = ""+cal.getTimeInMillis();
	       	      	
	       	rowhash.put("ecdate", ecdate);
	       		       	
	       	primary_key = primary_key.replaceFirst(",", "");
	       		 	       	
	       	rowhash.put("ecpkey", primary_key);
	       	
	       	title = title.replaceFirst("- ", "");
	    	
	    	if(title.equalsIgnoreCase("")){
	    		//title = this.getResources().getString(R.string.no_title_set); //"No Title Set";
	    		title = primary_key.split(",")[1];
	    	}
	    	//Log.i("TITLE", title);

	    	rowhash.put("ectitle", title);
	    	
	       	rowhash.put("ecfkey", foreign_key);
	       		    		       	
	    	dbAccess.createRow(coretable, rowhash);
	    	
	    	/*if(imagerowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Image", imagerowhash);
	    	if(videorowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Video", videorowhash);
	    	if(audiorowhash.keySet().size() > 0)
	    		dbAccess.createFileRow("Audio", audiorowhash);*/
	    			    	
	    	/*if(imageviewvalhash != null){
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
		         } */
	         
	    	stored = true;
	    	
	    	//Log.i("JUMPED", ecjumped);
	    	// Delete any branches that have now been jumped
	    	// Need the vector for this as it can only be done when the primary key is complete
	    	if(type != 3){ // Don't delete as page is flipping
	    		for(String table : branchtodelete){
	    			//Log.i("BRANCH JUMPED", table);
	    			dbAccess.deleteBranchRows(table, primary_key);
	    		}
	    	}
	    	
	    	// Don't want a message if the back button has been pressed
	    	if(type == 0){
	    		if(message.length() > 0)
	    			confirmHome(message, 1);
	    		else
	    			confirmHome("", 1);
	    	}
	    	else if(type == 2)
	    		showAlert(this.getResources().getString(R.string.entry_saved), this.getResources().getString(R.string.success)); //"Entry Saved", "Success");
	    	//showToast("Record Saved");
	    	//}
	    }
	    
	   /* private void confirmHome(String message){
	    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    	alertDialog.setTitle("Select");
	    	if(message.length() > 0)
	    		alertDialog.setMessage(message);
	    	
	    	RelativeLayout ll = new RelativeLayout(this);
	    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
		    //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		    
		    Button tableButton = new Button(this);
		    tableButton.setText("Home");
		    ll.addView(tableButton); //, lp);
		    
		    Button addButton = new Button(this);
		    tableButton.setText("Add "+coretable);
		    ll.addView(addButton); //, lp);
		    
		    Button listButton = new Button(this);
		    tableButton.setText("List/Synch Entries");
		    ll.addView(listButton); //, lp);
		    
		    tableButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		selectTable(); //false
		    		//confirmBack(); //null);
		    		}
		    	});
		    
		    addButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		newEntry(); //false
		    		//confirmBack(); //null);
		    		}
		    	});
		    
		    listButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		listRecords(); //false
		    		//confirmBack(); //null);
		    		}
		    	});
		    		    
		    alertDialog.setView(ll);
	    	
	        //alertDialog.setButton("New Entry", new DialogInterface.OnClickListener(){
	        //     public void onClick(DialogInterface dialog, int whichButton) {
	        //    	 newEntry();
	        //     }
	        //});
	        //alertDialog.setButton3("List/Synch Entries", new DialogInterface.OnClickListener(){
	        //    public void onClick(DialogInterface dialog, int whichButton) {
	        //    	//selectTable(true);
	        //    	listRecords();
	        //    }
	       //});
		    
		    
	        alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener(){
	        	public void onClick(DialogInterface dialog, int whichButton) {
	        		return;
	        	}
	        });
	        alertDialog.show();	
	        
	        
		} */
	    
	    int position = 1;
	    private void confirmHome(String message, int type){
	    	
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
	        	
	    	alert.setTitle(R.string.select);
	    	
	    	//LinearLayout ll = new LinearLayout(this);
	    	//LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
	 	    //ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    	//ll.setOrientation(1);
		 	//ll.setGravity(Gravity.CENTER);
		 	//ll.setLayoutParams(lp);
		 	   
	    	//RelativeLayout ll = new RelativeLayout(this);
		    //ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
		    
	 	    //ScrollView s = new ScrollView(this);
	 	    //ll.addView(s);
	 	    
	 	   //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
		    //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		   
	 	   //LinearLayout ll2 = new LinearLayout(this);
	 	  // ll2.setOrientation(1);
	 	  // ll2.setGravity(Gravity.CENTER);
	 	  // ll2.setLayoutParams(lp);
	    	//LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT );
	 	    
	 	  //TableLayout l=new TableLayout(this);
		    //l.setColumnStretchable(0, true);
		    //l.setColumnStretchable(1, true);
	 	  	
		   // s.addView(ll2); 
		    
		    //RelativeLayout rl2;
		    //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		    
	    	
		    if(message != null && message.length() > 0){
		    	alert.setMessage(message);
		 		 //  TextView tv = new TextView(this);
		 		//   tv.setText(message);
		 		//     ll.addView(tv);
		 	   }
		    
		    if(type == 1){
		    	int selected = 1;
		    	//final int position = 0;
		    	String next = dbAccess.getNextTable(coretable);
		    
		    	Vector<String> items1 = new Vector<String>();
		    
		    	// Check how many tables there are
		    	if(isbranch == 0) //isnew != 2)  // multitable && 
		    		items1.addElement(this.getResources().getString(R.string.select_form)); //"Select Form");
		    	else{
		    		selected = 0;
		    		position = 0;
		    	}
		    	items1.addElement(this.getResources().getString(R.string.add_another)+" "+coretable); // "Add Another "
		    	// It's a branch
		    	if(isbranch == 1) //isnew == 2)
		    		items1.addElement(this.getResources().getString(R.string.return_to_main_entry)); //Return to Main Entry");
		    	else{
		    		items1.addElement(this.getResources().getString(R.string.list_synch_entries)); //"List/Synch Entries");
		    		if(next != null)
		    			items1.addElement(this.getResources().getString(R.string.add)+" "+next + " "+this.getResources().getString(R.string.to)+" "+coretable); // add to
		    	}
		    	CharSequence[] items2 = new CharSequence[items1.size()];
		   
		    	items1.toArray(items2);
		    
		    	alert.setSingleChoiceItems(items2, selected, new DialogInterface.OnClickListener() {
		    		public void onClick(DialogInterface dialog, int whichButton) {
		    			position = whichButton;
		    			//Log.i("POSITION", " = "+position);
		    			/* Do whatever with checked buttons */ 
		    			}
					} 
		    	);
	    	}
	    	else{
	    		position = 0;
	    	}
		    
		   /* ListView lv = new ListView(this); 
 		    
		    ArrayList<String> items = new ArrayList<String>();
 		    
 		    items.add("Select Table");
 		   	items.add("Add Another "+coretable);
 		   	items.add("List/Synch Entries");
 		   	String next = dbAccess.getNextTable(coretable);
	    	
	    	if(next != null)
	    		items.add("Add "+next + " To "+coretable);

 		     		    
 		    ListAdapter notes = new ArrayAdapter<String>(this, R.layout.records_row, items);
 		    lv.setAdapter(notes);
 		    
 		    lv.setOnItemClickListener(new OnItemClickListener() {
 		        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

 		        	if(position == 0){
 		        		selectTable(false); 
 		        		return;
 		        	}
 		        	else if(position == 1){
 		        		newEntry();
 		        		
 		        	}
 		        	else if(position == 2){
 		        		listRecords();
 		        		return;
 		        	}
 		        	else if(position == 3){
 		        		selectTable(true);
 		        		return;
 		        	}
 		        }
 		      });
 	    	
 		    
 	    	ll.addView(lv); */
		    
		    
	 	   
	 	   
		/*    Button tableButton = new Button(this);
		    tableButton.setText("Select Table");
		    //tableButton.setGravity(Gravity.CENTER_HORIZONTAL);
		    //tableButton.setGravity(Gravity.CENTER_VERTICAL);
		    //tableButton.setGravity(Gravity.CENTER);
		    //tableButton.setGravity(1);
		    //rl2 = new RelativeLayout(this);
	 		 // rl2.addView(tableButton, lp);
		   // tableButton.
		    	
		    ll2.addView(tableButton, lp2);
		    
		    Button addButton = new Button(this);
		    addButton.setText("Add "+coretable);
		   // rl2 = new RelativeLayout(this);
		   // rl2.addView(addButton, lp);
		    ll2.addView(addButton, lp2);
		    
		    Button listButton = new Button(this);
		    listButton.setText("List/Synch Entries");
		   // rl2 = new RelativeLayout(this);
	 		//  rl2.addView(listButton, lp);
		    ll2.addView(listButton, lp2);
		    
		    tableButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		selectTable(false); //false
		    		//confirmBack(); //null);
		    		return;
		    		}
		    	});
		    
		    addButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		newEntry(); //false
		    		//confirmBack(); //null);
		    		return;
		    		}
		    	});
		    
		    listButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    		listRecords(); //false
		    		//confirmBack(); //null);
		    		return;
		    		}
		    	});
	    	alert.setView(ll); //filterView);    	   
	    	     	        
	    	String next = dbAccess.getNextTable(coretable);
	    	
	    	if(next != null){
	    		alert.setPositiveButton("Add "+next, new DialogInterface.OnClickListener() {  
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			selectTable(true);
	    			return;
		         }
	    	  }); */
	    	 
 	    	alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {  // Confirm
	    		public void onClick(DialogInterface dialog, int whichButton) {	 
	    			if(isbranch == 0){ //isnew != 2){  // multitable && 
	    				if(position == 0){
	    					//Log.i("HOME CHECK", "SELECT TABLE");
	    					selectTable(false); 
	    					return;
	    				}
	    				else if(position == 1){
	    					newEntry();
	    					return;
	    				}
	    				else if(position == 2){
	    					listRecords();
	    					return;
	    						
	    				}
 		        		 if(position == 3){
 		        			 selectTable(true);
 		        			 return;
 		        		 }
	    			}
	    			else{
	    				Log.i("HOME CHECK", "IS BRANCH");
	    				// The if/else probably no longer needed here as 
	    				// isbranch == 1 is the only option.
	    				// The multitable option is now deleted as
	    				// single tables now have the same list but previously
	    				// would have been the "else" here
	    				if(position == 0){
	    					//Log.i("HOME CHECK", "NEW ENTRY");
	    					if(isbranch == 1)
	    						newEntry();
	    					else
	    						selectTable(true);
	    					//newEntry();
	    				}
	    				else if(position == 1){
	    					// It's a branch
	    					//Log.i("POSITION", "POSITION = 1");
	    					if(isbranch == 1){ //isnew == 2){
	    						Intent i = getIntent();
	    						i.putExtra("BRANCH_FORM", coretable);
	    						setResult(RESULT_OK, i); //getIntent());
	    						dbAccess.close();
	    			       	  	finish(); 
	    					}
	    					else{
	    						listRecords();
	    						return;
	    					}
	    				}
	    			}
		         }
	    	  });
	    	    
	    	  alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Cancel  
	    	    public void onClick(DialogInterface dialog, int whichButton) { 
	    	    	return;
	    	      // Canceled.  
	    	    }  
	    	  });  
	    	     
	    	  //alert.setView(ll);
	    	  alert.show();  
	    	
	    }
	    
	    private void confirmEdit(){
	    	if(canedit){
	    		if(checkDoubleInput() && checkValidDate() && checkEntry() && checkRe() && checkMatch() && checkMinMax() && checkMinMax2()){ // checkTotals() && 
	            	flipToNext(2);
	            	storeData(1);
	            	//Log.i("IN EDIT", "IN HERE");
	    		}
	    		else{
	    			Log.i("IN EDIT 2", "IN HERE 2");
	    		//	return;
	    		}
	    	}
	    	
	    	Bundle extras = getIntent().getExtras();
        	getIntent().putExtras(extras);
        	extras.putString("target", extras.getString("target"));
			setResult(RESULT_OK, getIntent());
			dbAccess.close();
            finish();
	    }
	    
	    private void newEntry(){
	    	resetData();
	    	
	    	jumpreversevec.clear();
	    	requiredfields = storedrequiredfields;
	    	requiredspinners = storedrequiredspinners;
	    	requiredradios = storedrequiredradios;
	    	
	    	//int p = thispage;
	    	flipper.setInAnimation(null);
            flipper.setOutAnimation(null);
	    	while(thispage != 1){
	    		flipper.showPrevious();
	    		thispage--;
	    	}
	    	
	    	//Log.i("THIS PAGE", ""+thispage);
	    	
	    	//thispage = 1;
	    	
	    	/*
	    	Intent i = new Intent(this, EntryNote.class);
     		
     		i.putExtra("table", coretable);
     		i.putExtra("new", 1);
     		
     		for(String key : spinhash.keySet()){
    			if(stored && key.equalsIgnoreCase(coretable) && newentry) // confirmed
    				i.putExtra(key, textviewhash.get(coretablekey).getText().toString());
    			else
    				i.putExtra(key, spinhash.get(key));	
    		
    		}
     		for(String key : keytablehash.keySet()){
     			i.putExtra(key, keytablehash.get(key));
     		}
     		
     		//Log.i("FOREIGN KEY", list_foreign_key);
     		
     		i.putExtra("select_table_key_column", list_select_table);
     		i.putExtra("foreign_key", list_foreign_key);
     		i.putExtra("select_table", select_table);
     		//i.putExtra("keytable", keytable);
     		//i.putExtra("keyvalue", keyvalue);
     	    startActivity(i); */
	    }
	    
	    private void selectTable(boolean newtable){
	    	
	    	if(locationManager != null){
				//locationManager.removeUpdates(this);
	    		removeGPSUpdates();
	    	}
	    	
	    	Intent i;
	    	
	    	//if(!fromlist){
	    		i = new Intent(this, TableSelect.class); //NewEntry
	    		    
	    		// Only need to return the first table setting as the rest are set automatically in TableSelect
	    		/*int pos;
	    		for(String key : spinhash.keySet()){
	    			pos = spinhash.get(key);
	    			if(stored && key.equalsIgnoreCase(coretable) && newentry) // confirmed
	    				pos++;
	    			i.putExtra(key, pos);	
	    		
	    		}*/
	    		
	    		for(String key : spinhash.keySet()){
	    			if(stored && key.equalsIgnoreCase(coretable) && newentry){ // confirmed
	    				i.putExtra(key, title); //textviewhash.get(coretablekey).getText().toString());
	    				//Log.i("TABLE SELECT 2", key+" "+title);
	    			}
	    			else{
	    				i.putExtra(key, spinhash.get(key));
	    				//Log.i("TABLE SELECT", key+" "+spinhash.get(key));
	    			}
	    		
	    		}
	    		
	    	
	    		// If this is the first entry for this table need to set the spinner
	    		//if(spinhash.get(coretable) == null)
	    		//	i.putExtra(coretable, 1);
	    	
	    		// A new record needs to be added to the next table
	    		if(newtable){
		    		String next = dbAccess.getNextTable(coretable);
		    		i.putExtra("nexttable", next);
		    		//Log.i("TABLE NEXT", next);
		    	}
	    		
	    		i.putExtra("selectedtable", coretable);
	    		
	    		startActivity(i);
	    	//}
	    	/*else{
	    		i = new Intent(this, ListRecords.class);
	    		
	    		i.putExtra("table", coretable);
	    		i.putExtra("select_table_key_column", list_select_table);
	    		i.putExtra("foreign_key", list_foreign_key);

	    		//Log.i("CALL LIST", "CORE "+coretable+" SELECT "+list_select_table+" FOREIGN "+list_foreign_key);	    		
	    	    startActivity(i);
	    	}*/
	    }
	    
	    
	    private void listRecords(){
	    	
	    	if(locationManager != null){
				//locationManager.removeUpdates(this);
				removeGPSUpdates();
	    	}
	    	
	    	Intent i;
	    	
	    	/*if(!fromlist){
	    		i= new Intent(this, ListTables.class); //NewEntry
	    		    		    		    	
	    		startActivity(i);
	    	}
    	   	
    	   	else{*/
	    		// If its from a details view then the foreign key is unknown
	    		if(fromdetails)
	    			foreign_key = "Null";
	    		
	    		i = new Intent(this, ListRecords.class);
	    		
	    		i.putExtra("table", coretable);
	    		i.putExtra("select_table", select_table);
	    		i.putExtra("select_table_key_column", list_select_table);
	    		i.putExtra("foreign_key", foreign_key);
	    		
	    		// FindRecord sends a query
	 		   i.putExtra("query", "none");
	 		   	
	 		   try{
	 			  dbAccess.close();
	 		   }
	 		   catch(Exception e){}
	 		   
	    		//////Log.i("CALL LIST", "CORE "+coretable+" SELECT "+list_select_table+" FOREIGN "+list_foreign_key);	    		
	    	    startActivityForResult(i, ACTIVITY_LIST);
	    	//}
	    }
	    
	    
	/* private void setGPS(){
		 		 
		EditText gpstext = textviewhash.get("GPS");
		 
		//long oldtime, newtime;
		//double oldtime, newtime;
						
		//Location gpslocation = locationManager.getLastKnownLocation(IP.getName());
			
		//Criteria criteria = new Criteria();
       	//String bestProvider = locationManager.getBestProvider(criteria, false);

       	////Location gpslocation = locationManager.getLastKnownLocation(bestProvider);
		////gpslocation = locationManager.getLastKnownLocation(IP.getName());
				       	
		//try{
		//	oldtime = gps_time; //gpslocation.getTime();
			//Log.i("OLD TIME", ""+oldtime);
		//}
		//catch(NullPointerException npe){
		if(oldtime == 0){
			showAlert("GPS position fix not obtained or GPS unavailable. Wait for position fix and try again and ensure GPS is enabled", "Error");
			return;
			}

		try { 
			Thread.sleep ( 1200 ); 
		    }  
		catch ( InterruptedException e ) { 
		    } 
		        
		//gpslocation = locationManager.getLastKnownLocation(IP.getName()); 
		//gpslocation = locationManager.getLastKnownLocation(bestProvider);
		newtime = gps_time; //gpslocation.getTime();
		
		Log.i("OLD/NEW TIME", ""+oldtime +" "+newtime);
		        
       //if(gpslocation.getLatitude() == 0 && gpslocation.getLongitude() == 0)
    	//   return "Initial GPS location not obtained yet";

		//Log.i("BEST PROVIDER", bestProvider+" "+oldtime+" "+newtime);
		
		if(oldtime == newtime){
			showAlert("GPS position fix not obtained. Wait for position fix and try again", "Error");
			return;
			}

		oldtime = newtime;
		
		lat = Double.toString(latitude); //gpslocation.getLatitude()); 
		lon = Double.toString(longitude); //gpslocation.getLongitude());
		alt = Double.toString(altitude); //gpslocation.getAltitude()); 
		gpsacc = Double.toString(accuracy);// gpslocation.getAccuracy());
		
		gpstext.setText("GPS Set - Accuracy "+gpsacc+"m");
		
		gpsButton.setTextColor(Color.BLUE);
		gpsButton.setText("GPS assigned - tap again to update");
		gpsset = true;
	 }*/
	 
	 private void setGPS(){
		 	
			//long oldtime, newtime;
		 	//double oldtime, newtime;
					
			//Criteria criteria = new Criteria();
			//String bestProvider = locationManager.getBestProvider(criteria, false);

			////Location gpslocation = locationManager.getLastKnownLocation(bestProvider);

			//Location gpslocation = locationManager.getLastKnownLocation(IP.getName());
							
			////gpslocation = locationManager.getLastKnownLocation(IP.getName());
					
				
			//try{
			//	oldtime = gps_time; //gpslocation.getTime();
				
			//}
			//catch(NullPointerException npe){
		 	if(oldtime == 0){
				showAlert(this.getResources().getString(R.string.waiting_for_location), this.getResources().getString(R.string.error)); //"Location not obtained. Wait for position fix and try again", "Error");
				for(TextView tv : gpstvwarnvec)
					tv.setText(R.string.location_search); //"Searching for location ...");
				return;
				}

		 	//gps_count = 0;
			//try { 
			//	Thread.sleep ( 2000 ); 
			//    }  
			//catch ( InterruptedException e ) { 
			//    } 
			     
			 mHandler = new Handler();
		        
		        new Thread() {
		            public void run() {
		         	 //  long t0,t1;
		         	   try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		              // t0=System.currentTimeMillis();
		              // do{
		              //     t1=System.currentTimeMillis();
		              // }
		              // while (t1-t0<1000);
		               Looper.prepare();
							/*You can use threads but all the views, and all the views related APIs,
		                 must be invoked from the main thread (also called UI thread.) To do
		                 this from a background thread, you need to use a Handler. A Handler is
		                 an object that will post messages back to the UI thread for you. You
		                 can also use the various post() method from the View class as they
		                 will use a Handler automatically. */
		                 //if(!images){
		                 	mHandler.post(new Runnable() {
		                 		public void run() {
		                 	    	setGPS2();
		                    			//updateData(0); 
		                 		}
		                 	});
		                 //}
		                 Looper.loop();
		                 Looper.myLooper().quit(); 
		                 
		                  
		            }
		       }.start();
		       
	 }
	 
	 private void setGPS2(){
		 
		 String tag = gpsposhash.get(thispage);
		 		 
		// Log.i("GPS TAG", tag+" "+thispage);
		// Log.i("ACC", gpssettingshash.get(tag+"_acc"));
			//gpslocation = locationManager.getLastKnownLocation(IP.getName());
			////bestProvider = locationManager.getBestProvider(criteria, false);
			////gpslocation = locationManager.getLastKnownLocation(bestProvider);
			//newtime = gps_time; //gpslocation.getTime();
			//Log.i("OLD/NEW TIME", oldtime +" "+newtime);        
			//Log.i("BEST PROVIDER", bestProvider+" "+oldtime+" "+newtime+" "+gpslocation.getTime());
	       //if(gpslocation.getLatitude() == 0 && gpslocation.getLongitude() == 0)
	    	//   return "Initial GPS location not obtained yet";
			        
			//if(oldtime == newtime){
				// If gps_count is still 0 then no new gps positions have been recorsed
		    if(oldtime == newtime){ // (gpscount == 0)[
				showAlert(this.getResources().getString(R.string.location_error), this.getResources().getString(R.string.error)); //"Location not obtained. Wait for position fix and try again", "Error");
				for(TextView tv : gpstvwarnvec)
					tv.setText(R.string.location_search); //"Searching for location ...");
				return;
				}
			
			//oldtime = newtime;
		   
		    //if(Double.parseDouble(gpssettingshash.get(tag+"_acc")) > -1 && accuracy > Double.parseDouble(gpssettingshash.get(tag+"_acc"))){
		    if(!gpssettingshash.get(tag+"_acc").equalsIgnoreCase("N/A") && Double.parseDouble(accuracy) != -1 && Double.parseDouble(accuracy) > Double.parseDouble(gpssettingshash.get(tag+"_acc"))){
		    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		    	alertDialog.setTitle(R.string.warning); //"Warning");
		    	alertDialog.setMessage(this.getResources().getString(R.string.gps_replace_1) +" "+Double.parseDouble(gpssettingshash.get(tag+"_acc")) +"m "+this.getResources().getString(R.string.with)+" "+accuracy+"m\n"+this.getResources().getString(R.string.gps_replace_2)); // "This will replace previous accuracy of "+Double.parseDouble(gpssettingshash.get(tag+"_acc")) +"m with "+accuracy+"m\nSave Location?");
		    	alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){ // Yes
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	updateGPS2();
	            	}
		    	});
	        
		    	alertDialog.setButton3(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){ // No
		    		public void onClick(DialogInterface dialog, int whichButton) {
		    		}
		    	});
	       
		    	alertDialog.show();	
		    }
		    else{
		    	updateGPS2();
		    }
	 }
		    
	private void updateGPS2(){
			
		String tag = gpsposhash.get(thispage);
		EditText gpstext = textviewhash.get(tag);
		showAlert(this.getResources().getString(R.string.location_updated), this.getResources().getString(R.string.success)); //"Location Updated", "Success");
		   
		gpssettingshash.put(tag+"_lat", latitude); //Double.toString(latitude)); //gpslocation.getLatitude()));
		gpssettingshash.put(tag+"_lon", longitude); //Double.toString(longitude)); //gpslocation.getLongitude()));
		gpssettingshash.put(tag+"_alt", altitude); //Double.toString(altitude)); //gpslocation.getAltitude()));
		gpssettingshash.put(tag+"_acc", accuracy); //Double.toString(accuracy)); //gpslocation.getAccuracy()));
		gpssettingshash.put(tag+"_bearing", bearing); //Double.toString(bearing));
		gpssettingshash.put(tag+"_provider", provider);
		
		gpstext.setText(this.getResources().getString(R.string.location_set)+" "+accuracy +"m\nLat "+latitude+"\nLon "+longitude+"\nAlt "+altitude+"\nBearing "+bearing); // Location Set - Accuracy
			
		gpsbuttonhash.get(tag).setTextColor(Color.BLUE);
		gpsbuttonhash.get(tag).setText(R.string.location_assigned); // Location assigned - tap again to update
		gpssethash.put(tag, true);
		}
	 
	 /*private void readBarcode(){
	 	 
		 	String tag = gpsposhash.get(thispage); 	
		 	
		 	EditText gpstext = textviewhash.get(tag);
			 
			
		 }*/
	 
	public void showImageAlert(String title, String result){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setPositiveButton(R.string.select_image, new DialogInterface.OnClickListener() { // Select Image

             public void onClick(DialogInterface dialog, int whichButton) {
            	 addPhoto("0");
             }
        })
        .setNegativeButton(R.string.capture_photo, new DialogInterface.OnClickListener() { // Capture Photo

             public void onClick(DialogInterface dialog, int whichButton) {
            	 addPhoto("1");
             }
        }).show();	
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
		
		/*Intent i = new Intent(this, ImageSwitcher_epi_collect.class);
	    
		i.putExtras(this.getIntent().getExtras());
		i.putExtra("PHOTOVIEW_ID", id);
		i.putExtra("GALLERY", gallery); //"0");
		i.putExtra("PHOTO_ID", photoid);
		i.putExtra("PHOTO_REF_ID", id);
		i.putExtra("PHOTO_TABLE", coretable);
		
	    startActivityForResult(i, ACTIVITY_PHOTO);*/
	    
	    
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
		

			File file = new File(Epi_collect.appFiles+"/"+project, "temp.jpg");
		
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
			//else
			//	recAudio();
		}
		catch(NullPointerException npe){
			
		}
		recAudio();
		
	 }
		
	 public void recAudio(){
		 
		 audioactive = true;
		 
		 audiodir = Epi_collect.appFiles+"/"+project+"/audio"; //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + dbAccess.getProject();
		 
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
	    	audioid = coretable+"_"+id+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".3gp";
	    	
	    	
		}
		//recorder = new AudioRecorder();
		try{
			recorder.record(audiodir+"/"+audioid, audiotextviewhash.get(audioviewposhash.get(thispage)), audiorecordbuttonhash.get(audioviewposhash.get(thispage)), audioplaybuttonhash.get(audioviewposhash.get(thispage)), audiostopbuttonhash.get(audioviewposhash.get(thispage)));
			
		}
		catch(Exception e){
			//Log.i("RECORDING", e.toString()+" "+audiodir+"/"+audioid);
			audioactive = false;
			return;
			
		}
		
		audioviewvalhash.put(id, audioid);
		//audioplaybuttonhash.get(audioviewposhash.get(thispage)).setEnabled(true);
		
		//audioactive = false;
	}
	 
	 public void playAudio(){
		 audioactive = true;
		 
		 audiodir = Epi_collect.appFiles+"/"+project+"/audio"; //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + dbAccess.getProject();
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
			  //havesdcard = false;
			  showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.audio_card_error)); // "Error", "SD card not present. Required for audio recording"
			  return false;
		  }
		  
		  return true;
	 }
	 
	public void updateData(Bundle extras, int type){
		
		// NEED TO ADD VIDEO HERE
		
		if(type == 1){
			String photoviewid = imageviewposhash.get(thispage), photoid = imagefile;
		/*if(extras != null){
			photoviewid	= extras.getString("PHOTOVIEW_ID");
			
			photoid = extras.getString("PHOTO_ID");
		}
		else{
			
			photoviewid = imageviewposhash.get(thispage);

			try{
			if(imageviewvalhash.get(photoviewid) != null)
				photoid = imageviewvalhash.get(photoviewid);
			}
			catch(NullPointerException npe){}
		}*/
			
			//Log.i("PHOTO", thumbdir+"/"+photoid);
			//showAlert("UPDATING IMAGE - PHOTO "+ photoviewid+" ID "+photoid, "MESSAGE");
			if(photoid != null && !photoid.endsWith("-1")){
				
				try{
					
					// Need to use bitmap to get the image to update
					Bitmap bmp = BitmapFactory.decodeFile(thumbdir+"/"+photoid);
					imageviewhash.get(photoviewid).setImageBitmap(bmp);
					
					//imageviewhash.get(photoviewid).setImageURI(Uri.parse(thumbdir+"/"+photoid));
					//imageviewhash.get(photoviewid).invalidate();
					//imageviewhash.get(photoviewid).refreshDrawableState();
					imageviewvalhash.put(photoviewid, photoid);
					photobuttonhash.get(photoviewid).setText("Tap to Update Photo");
					
					imageviewhash.get(photoviewid).setOnClickListener(new View.OnClickListener() {
        		    	public void onClick(View arg0) {
        		    		
        		          	showImage();
        		        }
        		           
        		    });
				}
				catch(Exception e){
					showAlert(this.getResources().getString(R.string.image_error), this.getResources().getString(R.string.error)); // "Image not available", "Error"
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
			
			//Log.i("PHOTO", photoviewid+" ID "+photoid);
			if(videoid != null && !videoid.endsWith("-1")){
				try{
					//Log.i("RETURNING VIDEOID", videoid);
					// This will work in 2.2
					//Bitmap bm = ThumbnailUtils.createVideoThumbnail(videodir+"/"+videoid, ThumbnailUtils.MINI_KIND);
					Bitmap bm = getVideoFrame(videodir+"/"+videoid);
					videoviewhash.get(videoviewid).setImageBitmap(bm);
					
					//ContentResolver crThumb = getContentResolver();
					//BitmapFactory.Options options=new BitmapFactory.Options();
					//options.inSampleSize = 1;
					//Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
					//videoviewhash.get(videoviewid).setImageBitmap(curThumb);
					
					videoviewvalhash.put(videoviewid, videoid);
					videobuttonhash.get(videoviewid).setText("Tap to Update Video");
					
					videoviewhash.get(videoviewid).setOnClickListener(new View.OnClickListener() {
        		    	public void onClick(View arg0) {
        		    		
        		          	playVideo();
        		        }
        		           
        		    });
					
				}
				catch(Exception e){
					showAlert(this.getResources().getString(R.string.video_error), this.getResources().getString(R.string.error)); // "Video not available", "Error"
				}
			}
		}

		//}
	}
	
	private boolean checkEntry(){ //int page){
		
		if(fromdetails)
			return true;
		
		// If this is a second check on the primary key need to check if the key has changed
		if(secondkeycheck){
			String tempkey = getCurrentKey(); //"";
    		/*for(String pkey : textviews){
	    		if(primary_keys.contains(pkey)){
	    				tempkey += ","+pkey+","+textviewhash.get(pkey).getText().toString();
	    			}
    		}
    		
	    	for(String pkey : spinners){	
    			if(primary_keys.contains(pkey)){
   					tempkey += ","+pkey+","+thisspinnerhash.get(pkey).getSelectedItem().toString();
		    		}
	    	}
	    	
	    	for(String pkey : radios){	    		
		    	if(primary_keys.contains(pkey)){
    				tempkey += ","+pkey+","+radioselectedhash.get(pkey);
			    	}
	    	}
    		
    		tempkey = tempkey.replaceFirst(",", ""); */  			
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
    	if(textviewposhash.get(thispage) != null){
    		key = textviewposhash.get(thispage);
    		//Log.i("ERROR CHECK KEY:",key);
    		String val = textviewhash.get(key).getText().toString();
    		val = val.replaceFirst("^\\s+", "");
    		val = val.replaceFirst("^\\n+", "");
    		textviewhash.get(key).setText(val);
    		if(requiredfields.contains(key) && ((val == null) || (val.equalsIgnoreCase("")))){
    			for(String key2 : gpstags){
    				if(key.equalsIgnoreCase(key2)){
    					showCheckAlert(this.getResources().getString(R.string.location_required)); //"WARNING: Location not set and is a required field");
    					secondcheck = true;
    					return false;
    				}
    			}
    			
       			if(primary_keys.contains(key)){
       				showCheckAlert(this.getResources().getString(R.string.key_required_1)+" "+ key+".<br>"+this.getResources().getString(R.string.key_required_2)); // "Value required for primary key field "+ key+".<br>Entry cannot be stored"
        			secondcheck = true;
					return false;
        		}
    			//if(key.equalsIgnoreCase(coretablekey)){ //(textviewhash.get(coretablekey).getText().toString() == null) || (textviewhash.get(coretablekey).getText().toString().equalsIgnoreCase(""))){
            	//	showCheckAlert("Value required for primary key field.<br>Entry cannot be stored");
            	//		secondcheck = true;
    			//		return false;
    			//	}
    			secondcheck = true;
    			showCheckAlert(this.getResources().getString(R.string.value_required)); //"WARNING: Value is a required field");
    			return false;
    			
    		}
    		
    		//if(primary_keys.contains(key)){
    		//	primary_key = key+","+textviewhash.get(key).getText().toString();
    		//	if(dbAccess.checkValue(coretable, "ecpkey", primary_key)){
    		//		showCheckAlert(this.getResources().getString(R.string.entry_exists_1)+" "+primary_key+".<br>"+this.getResources().getString(R.string.entry_exists_2)); // "Entry exists for this primary key "+primary_key+".<br>Any changes to record cannot be undone"
    		//		secondcheck = true;
        	//		return false;
    		//	}
    		//}
    	}
    	
    	boolean noerrors = true; //, keyexists = false;
    	if((textviewposhash.get(thispage) != null && primary_keys.contains(textviewposhash.get(thispage))) || 
    			(spinnerposhash.get(thispage) != null && primary_keys.contains(spinnerposhash.get(thispage))) ||
    			(radioposhash.get(thispage) != null && primary_keys.contains(radioposhash.get(thispage)))){
    		
    		String tempkey = getCurrentKey(); //"";
    		/*for(String pkey : textviews){
	    		if(primary_keys.contains(pkey)){
	    				tempkey += ","+pkey+","+textviewhash.get(pkey).getText().toString();
	    			}
    		}
    		
	    	for(String pkey : spinners){	
    			if(primary_keys.contains(pkey)){
   					tempkey += ","+pkey+","+thisspinnerhash.get(pkey).getSelectedItem().toString();
		    		}
	    	}
	    	
	    	for(String pkey : radios){	    		
		    	if(primary_keys.contains(pkey)){
    				tempkey += ","+pkey+","+radioselectedhash.get(pkey);
			    	}
	    	}
    		
    		tempkey = tempkey.replaceFirst(",", ""); */  			
    		if(dbAccess.checkValue(coretable, "ecpkey", tempkey)){
    			if(dbAccess.getValue("change_synch").equalsIgnoreCase("false") && dbAccess.getValue(coretable, "ecstored", tempkey).equalsIgnoreCase("R")){
    				showAlert(this.getResources().getString(R.string.remote_edit_error), this.getResources().getString(R.string.error));
    				return false;
    			}
    			// If genkey is used for the primary key then don't print message. Value is added to form when it is created so as
    			// soon as "next" button is pressed it is stored. If it is on second or greater page it will cause message to 
    			// be shown as it is already in the database
    			else if(genkey.length() == 0){
    				showCheckAlert(this.getResources().getString(R.string.entry_exists_1)); //+".<br>"+this.getResources().getString(R.string.entry_exists_2)); // "Entry exists for this primary key "+primary_key+".<br>Any changes to record cannot be undone"
    				secondcheck = false;  //true;
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
				//return false;
			}
		}
		
    	if(videoviewposhash.get(thispage) != null){
    		key = videoviewposhash.get(thispage);
			if(requiredfields.contains(key) && ((videoviewvalhash.get(key) == null) || (videoviewvalhash.get(key).equalsIgnoreCase("-1")))){
				showCheckAlert(this.getResources().getString(R.string.video_required)); //"WARNING: Video is required");
				secondcheck = true;
				noerrors = false;
				//return false;
			}
		}
    	
    	if(audioviewposhash.get(thispage) != null){
    		key = audioviewposhash.get(thispage);
			if(requiredfields.contains(key) && ((audioviewvalhash.get(key) == null) || (audioviewvalhash.get(key).equalsIgnoreCase("-1")))){
				showCheckAlert(this.getResources().getString(R.string.audio_required)); //"WARNING: Audio is required");
				secondcheck = true;
				noerrors = false;
				//return false;
			}
		}
    	
    	if(spinnerposhash.get(thispage) != null){
    		key = spinnerposhash.get(thispage);
    		
    		if(thisspinnerhash.get(key).getSelectedItemPosition() == 0){
    			if(primary_keys.contains(key)){
       				showCheckAlert(this.getResources().getString(R.string.key_required_1)+" "+ key+".<br>"+this.getResources().getString(R.string.key_required_2)); // "Value required for primary key field "+ key+".<br>Entry cannot be stored"
        			secondcheck = true;
        			noerrors = false;
    				//return false;
        		}
    			else if(requiredspinners.contains(key)){
    				showCheckAlert(this.getResources().getString(R.string.value_required)); //"WARNING: Value is required");
    				secondcheck = true;
    				noerrors = false;
					//return false;
    			}
			}
			
		}
    	
    	if(radioposhash.get(thispage) != null){
    		key = radioposhash.get(thispage);
			if(radioselectedhash.get(key).equals("")){
				if(primary_keys.contains(key)){
       				showCheckAlert(this.getResources().getString(R.string.key_required_1)+" "+ key+".<br>"+this.getResources().getString(R.string.key_required_2)); // "Value required for primary key field "+ key+".<br>Entry cannot be stored"
        			secondcheck = true;
        			noerrors = false;
    				//return false;
        		}
				else if(requiredradios.contains(key)){
					showCheckAlert(this.getResources().getString(R.string.value_required)); //"WARNING: Value is required");
					secondcheck = true;
					noerrors = false;
					//return false;
				}
			}
		}
		
    	return noerrors;
    	//return true;
  
	}
	
	private String getCurrentKey(){
		String tempkey = "";
		for(String pkey : textviews){
    		if(primary_keys.contains(pkey)){
    				tempkey += ","+pkey+","+textviewhash.get(pkey).getText().toString();
    			}
		}
		
    	for(String pkey : spinners){	
			if(primary_keys.contains(pkey)){
					tempkey += ","+pkey+","+thisspinnerhash.get(pkey).getSelectedItem().toString();
	    		}
    	}
    	
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
        alert.setMessage(Html.fromHtml(result)); //+".<br><br>"+this.getResources().getString(R.string.next_to_continue))); //Tap Next again to continue"))
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // OK

             public void onClick(DialogInterface dialog, int whichButton) {
            	
             }
        });
        /*.setNegativeButton("No", new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 validentry = false;
             }
        })*/
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
	
	private void showHierarchy(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	
    	alert.setTitle(R.string.hierarchy);  // Hierarchy
    	//LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
    	alert.setView(Hierarchy.setLayout(spinhash, coretable, this)); //filterView);    	   
    	     	        
    	alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // OK  
    	public void onClick(DialogInterface dialog, int whichButton) {
    	    return;		  
    		
    	  }
    	  });  
    	    
    	alert.show();    
	}

	private void setGPSMenu(){
		/*if(gpsposhash.get(thispage) == null){
			Log.i("GPS", "IN HERE");
    		optionsmenu.getItem(CHANGE_GPS).setVisible(false);
    		optionsmenu.getItem(CHANGE_GPS).setEnabled(false);
    	}
    	else{
    		optionsmenu.getItem(CHANGE_GPS).setVisible(true);
    		optionsmenu.getItem(CHANGE_GPS).setEnabled(true);
    	
    	}*/
	}

	 private void showToast(String text){
	    	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	    }
	 
	//@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//gps_count++;
		
		//if(oldtime == 0)
    	//	oldtime = location.getTime(); //gps_time;
		oldtime = newtime;
   		newtime = location.getTime(); //gps_time;
   		
        //double lon = loc.getLatitude();
        //double lat = loc.getLongitude();
        latitude = Double.toString(location.getLatitude());
        longitude = Double.toString(location.getLongitude());
        if(location.hasAltitude())
        	altitude = Double.toString(location.getAltitude());
        else
        	altitude = "N/A";
        //gps_time = location.getTime();
        if(location.hasAccuracy() && location.getAccuracy()  != 0.0)
        	accuracy = Double.toString(location.getAccuracy());
        else
        	accuracy = "N/A";
        if(location.hasBearing())
        	bearing = Double.toString(location.getBearing());
        else
        	bearing = "N/A";
        provider = location.getProvider();
             
        if(oldtime != newtime && oldtime != 0)
        	for(TextView tv : gpstvwarnvec)
        		tv.setText(R.string.location_obtained); //"Location obtatined");
        //Log.i("LOCATION SET", ""+location.getProvider());
        
        // Reset count to prevent it getting too large
        //if(gps_count == 1000)
        //	gps_count = 1;
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
	
	/* Class My Location Listener */
   /* public class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc)
        {
        	// Initialise oldtime to confirm that gps is enabled
        	if(oldtime == 0)
        		oldtime = gps_time;
            //double lon = loc.getLatitude();
            //double lat = loc.getLongitude();
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
            altitude = loc.getAltitude();
            gps_time = loc.getTime();
            accuracy = loc.getAccuracy();
            //gps_text.append("\nLongitude: "+lon+" - Latitude: "+lat);
            //UseGps.this.mlocManager.removeUpdates(this); 
            //gps_button.setEnabled(true); 
            
            //Log.i("GPS", latitude+" "+longitude+" "+gps_time);
        }

        public void removeUpdates(){
        	EntryNote.this.locationManager.removeUpdates(this); 
        }
        
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

    }*/
	
	private void playVideo(){
		
		Intent i = new Intent(this, VideoPlayer.class);
		
		String id = videoviewposhash.get(thispage);
		
		//showAlert(id, "TEST");
		//String videoid = "0";
		//try{
		//if(videoviewvalhash.get(id) != null)
		//	videoid = videoviewvalhash.get(id);
		//}
		//catch(NullPointerException npe){}
		
		
		i.putExtra("VIDEO_ID", videoviewvalhash.get(id));
		i.putExtras(this.getIntent().getExtras());
		
	    startActivity(i);
	
	/*	//get current window information, and set format, set it up differently, if you need some special effects
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		//the VideoView will hold the video
		VideoView videoHolder = new VideoView(this);
		//MediaController is the ui control howering above the video (just like in the default youtube player).
		videoHolder.setMediaController(new MediaController(this));
		//Passing a video file to the video holder
		videoHolder.setVideoURI(Uri.parse(videodir+"/"+videoviewvalhash.get(videoviewposhash.get(thispage))));
		//get focus, before playing the video.
		videoHolder.requestFocus();
        videoHolder.start();*/
	
	/*	MediaPlayer mp = new MediaPlayer();
		try{
			mp.setDataSource(videodir+"/"+videoviewvalhash.get(videoviewposhash.get(thispage)));
			mp.prepare();
			mp.start();
		}
		catch(Exception e){} */
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
		//Log.i("VIDEO LOC", uri);
 
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

	
	//@SuppressLint("NewApi")
	private void createThumbnail(){
	   	File file = new File(Epi_collect.appFiles+"/"+project, "temp.jpg");
	   	
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
	    	showAlert(this.getResources().getString(R.string.photo_error), this.getResources().getString(R.string.photo_card_error)); //"Photo Error", "SD card may not be present. Required for photo capture");
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
	   	//Log.i("THUMBNAIL", "HERE");
	   	//Log.i("THUMBNAIL2", imagefile);
	   	
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

