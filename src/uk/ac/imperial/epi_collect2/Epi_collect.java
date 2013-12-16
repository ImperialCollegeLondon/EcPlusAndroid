package uk.ac.imperial.epi_collect2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import uk.ac.imperial.epi_collect2.util.ECSettings;
import uk.ac.imperial.epi_collect2.util.ECSettings;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import uk.ac.imperial.epi_collect2.util.xml.ParseXML;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.annotation.SuppressLint;
//import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
//import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ImageView;

//@TargetApi(Build.VERSION_CODES.ECLAIR)
//@SuppressLint("NewApi")
//@SuppressLint("NewApi")
//@TargetApi(Build.VERSION_CODES.ECLAIR)
@SuppressLint("NewApi")
public class Epi_collect extends Activity implements Runnable{
	
	private static String APK_VERSION = "0"; // = "1.11"; //"1.0"; // SCORE 2.2 // "2.6.16"; //
	private static final int ACTIVITY_NEW = 0;
	private static final int ACTIVITY_EMAIL = 13;
	private static final int ACTIVITY_EMAIL2 = 20;
    private static final int LOAD_PROJECT = 1;
    private static final int DELETE_PROJECT = 2;
    private static final int BACKUP_PROJECT = 3;
    private static final int SYNCH_IMAGES_ID = 4;
    private static final int SYNCH_VIDEOS_ID = 5;
    private static final int SYNCH_AUDIO_ID = 6;
    private static final int DEL_MEDIA_ID = 7;
    private static final int RESET_SYNCH_ID = 8;
    //private static final int REGISTER_ID = 9;
    private static final int DEL_SYNCH = 10;
    private static final int DEL_REM = 11;
    private static final int DEL_ALL = 12;
    private static final int DEL_FAIL = 19;
    private static final int VERSION = 14;
    private static final int EMAIL_BACKUP = 15;
    private static final int GROUP_DATA_ID = 16;
    private static final int LOAD_BACKUP = 17;
    private static final int LOAD_REMOTE = 18;
    private static final int MENU_SETTINGS = 21;
    private static final int MENU_DB_BACKUP = 221;
    private ImageView iview;
    private DBAccess dbAccess;
    private Spinner proj_spin;
    public ProgressDialog myProgressDialog = null; 
    private Handler mHandler; 
    private ParseXML parseXML;
    private ButtonListener myOnClickListener = new ButtonListener();
    private String selected_project = "", selected_table, input_project = ""; 
    private TextView urlview1, urlview2;
    private CheckBox remotecb, deleimagescb, deletevideocb, deleteaudiocb; 
    private Button synchButton, picbut, audbut, vidbut; //, remoteButton;
    private int synch_type = 0;
    private Thread thread;
    private boolean getimages, allsynched = true, mediasynched = true;
    public static boolean is_v1 = false; // Need to check this in ParseXML in case project has no forms 
    private  int dataresult = 0;
    private HashMap<String, StringBuffer> tableshash;
    private String email = ""; //, load_fail_url;
    public static String project_server = "";
    public static File appFiles;
    private String android_id;
    
    // Test 3
    /** Called when the activity is first created. 
     * @throws FileNotFoundException */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        super.setTitle("EpiCollect+"); 
        setContentView(R.layout.main);
        
        String packageName = this.getPackageName();
        File externalPath = Environment.getExternalStorageDirectory();
        appFiles = new File(externalPath.getAbsolutePath() + "/Android/data/" + packageName);
        
        try{
        	//File f = new File(Environment.getExternalStorageDirectory()+"/EpiCollect");
        	if(!appFiles.exists())
        		appFiles.mkdir();
        	}
        catch(Exception e){
        	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.card_error)); //"Error", "SD card not present. Required for photo, video and audio capture");
        }
        
        // Get the apk version from the AndroidManifest.xml file
        try
        {
        	APK_VERSION = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e)
        {
            Log.i("Version", e.getMessage());
        }
        
        dbAccess = new DBAccess(this);
	    dbAccess.open();
	    
	    android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
	    
	    project_server = dbAccess.getSettings("url");
        
	    mHandler = new Handler();

        Button newButton = (Button) findViewById(R.id.newbut);
       // For SCORE
        //remoteButton = (Button) findViewById(R.id.remotebut);
        //remoteButton.setEnabled(false);
        //remoteButton.setText(R.string.menu_load_remote); 
        
        Button helpButton = (Button) findViewById(R.id.helpbut);
        synchButton = (Button) findViewById(R.id.synchbut);
        synchButton.setEnabled(false);
        
        newButton.setText(R.string.start_resume);
        helpButton.setText(R.string.about);
        synchButton.setText(R.string.synch_button_start);
                
        iview = (ImageView) findViewById(R.id.image);
        urlview1 = (TextView) findViewById(R.id.urltext1);
        urlview2 = (TextView) findViewById(R.id.urltext2);
        
        proj_spin = (Spinner) findViewById(R.id.proj_spin);
        
        TextView spintext = (TextView) findViewById(R.id.spintext);
        spintext.setText(R.string.select_project);
        
        Account[] accounts = AccountManager.get(this).getAccounts();
    	for (Account account : accounts) {
    	  if(account.type.equalsIgnoreCase("com.google") && account.name.contains("@gmail.com")){
    		  email = account.name;
    	  }
    	}
    	
    	try{
    		if(email.length() == 0 || email == null)
    			email = "NA";
    	}
    	catch(NullPointerException npe){
    		email = "NA";
    	}
    	
    	// ASUS Transformer doesn't have IMEI number so have to ensure it has a value or synchronisation fails
    	
    	/*TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	 	try{
    		sIMEI = mTelephonyMgr.getDeviceId(); 	
    	}
    	catch(Exception e){
    		sIMEI = "1";
    	}
    	
	 	try{
    		if(sIMEI.length() == 0 || sIMEI == null)
    			sIMEI = "1";
    	}
    	catch(NullPointerException npe){
    		sIMEI = "1";
    	}*/
	 	
        String[] allprojects = dbAccess.getProjects();
    	
        ArrayList<String> temparray = new ArrayList<String>();
        for (int i = 0; i < allprojects.length; i++) {
        	temparray.add(allprojects[i]);
	    	}

        ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparray);
    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	proj_spin.setAdapter(aspnLocs); 
           
    	if(allprojects.length > 1){
    		try{
    			proj_spin.setSelection(1);
    			// For SCORE
    			//remoteButton.setEnabled(true);
    			setSynchButton();
    		}
    		catch(IndexOutOfBoundsException e){
    			
    		}
    		}
    	// For SCORE
    	else{
    		//remoteButton.setEnabled(false);
    	}
    	Bitmap bitMap = BitmapFactory.decodeResource(this.getResources(), R.drawable.epi_icon);
		iview.setImageBitmap(bitMap); 

		// Demo project code
		if(dbAccess.getFirstrun() == 0){
			try{
				String str;
				StringBuffer buf = new StringBuffer("");	
				InputStream in = getResources().openRawResource(R.raw.demo_project);
				InputStreamReader is = new InputStreamReader(in); 

				BufferedReader reader = new BufferedReader(is);
				if (is!=null) {							
					while ((str = reader.readLine()) != null) {	
						buf.append(str + "\n" );
					}				
				}		
				is.close();
				parseXML = new ParseXML("");
				parseXML.getDemoXML(buf);
				dbAccess.setFirstrun();
				
				// Set synch_type to 7 to prevent Project Loaded alert from showing
				// when app starts
				synch_type = 7;
				
				buildProject(true);
			}
			catch(Exception e){
				Log.i("PARSING DEMO ERROR",e.toString());
			}
		} 
    	
		proj_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	String project = proj_spin.getSelectedItem().toString();
		    	if(project == null || project.equalsIgnoreCase("")){
		    		urlview1.setText("");
		    		urlview2.setText("");
		    		synchButton.setEnabled(false);
		    		// For SCORE
		    		//remoteButton.setEnabled(false);
		    	}
		    	else{
		    		dbAccess.setActiveProject(project);
		    		// For SCORE
		    		//remoteButton.setEnabled(true);
		    		setSynchButton();
		    	}
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});
		
        newButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	createEntry();
            }
           
        });
        
     // For SCORE
       /* remoteButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	loadRemoteData();
            }
           
        }); */
        
        synchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	if(!allsynched){
            		Log.i("SYNCH", "DATA");
            		synchroniseData(false, false, false);
            	}
            	else{
            		Log.i("SYNCH", "MEDIA");
            		synchroniseMedia();
            	}
            }
           
        });
        	
        helpButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
            	showHelp();
            }
           
        });
        
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            String url = intent.getDataString();
            getProject(url);
            }
                
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	// Enables menus to be updated if images etc available for upload
    	if(keyCode == KeyEvent.KEYCODE_MENU)
    		openOptionsMenu();

        return true;
    } 
    
    
    // Use this version to update menus depending if images etc available
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		 menu.add(0, LOAD_PROJECT, 0, R.string.load_project);
		 	
		 try{
	    		selected_project = proj_spin.getSelectedItem().toString();
	    	}
	    	catch(NullPointerException npe){
	    	
	    	}
		
	    	if(!selected_project.equalsIgnoreCase("")){
	    		
	    		menu.add(0, LOAD_REMOTE, 0, R.string.menu_load_remote);
	    		menu.add(0, BACKUP_PROJECT, 0, R.string.backup_project);
	    		menu.add(0, EMAIL_BACKUP, 0, R.string.email_backup);
	    		menu.add(0, LOAD_BACKUP, 0, R.string.load_backup);
	    		
	    		//int total = 0;
	    		if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("2")){
	    			if(dbAccess.haveGroup(selected_project))
	    				menu.add(0, GROUP_DATA_ID, 0, R.string.menu_group_data);
	    			/*total = dbAccess.getMediaFileCount(selected_project+"_Image", "synch", "N");
	    			if(total > 0)
	    				menu.add(0, SYNCH_IMAGES_ID, 0, this.getResources().getString(R.string.menu_photo_synch) + " - " + total);
	    			total = dbAccess.getMediaFileCount(selected_project+"_Video", "synch", "N");
	    			if(total > 0)
	    				menu.add(0, SYNCH_VIDEOS_ID, 0, this.getResources().getString(R.string.menu_video_synch) + " - " + total);
	    			total = dbAccess.getMediaFileCount(selected_project+"_Audio", "synch", "N");
	    			if(total > 0)
	    				menu.add(0, SYNCH_AUDIO_ID, 0, this.getResources().getString(R.string.menu_audio_synch) + " - " + total); */
	    			//menu.add(0, REGISTER_ID, 0, R.string.menu_register);
	    		}	
	    		
	    		if(dbAccess.checkSynchStatus("Y")){
	    			menu.add(0, RESET_SYNCH_ID, 0, R.string.menu_reset_synch);
	    			menu.add(0, DEL_SYNCH, 0, R.string.menu_del_synch);
	    		}
	    		if(dbAccess.checkSynchStatus("R"))
	    			menu.add(0, DEL_REM, 0, R.string.menu_del_rem);
	    		if(dbAccess.checkSynchStatus("F"))
	    			menu.add(0, DEL_FAIL, 0, R.string.menu_del_fail);

	    		menu.add(0, DELETE_PROJECT, 0, R.string.delete_project);
	    		menu.add(0, DEL_ALL, 0, R.string.menu_del_projall);	 
	    		//menu.add(0, VERSION, 0, "Version");   
	    		
	    		boolean havemedia = false;
	    		File dir;
	    		String[] chld;
	    		try{
	    		dir = new File(appFiles+"/"+selected_project+"/images");
	    		// Have to check if directory exists as they are only created when a media file is first created  
	    		if(dir.exists()){
	    			chld = dir.list();
	    			if(chld != null && chld.length > 0)
	    				havemedia = true;
	    			}
	    	    
	    		dir = new File(appFiles+"/"+selected_project+"/thumbs");
	    		if(dir.exists()){
	    			chld = dir.list();
	    			if(chld != null && chld.length > 0)
	    				havemedia = true;
	    		}
	    		
	    		dir = new File(appFiles+"/"+selected_project+"/videos");
	    		if(dir.exists()){
	    			chld = dir.list();
	    			if(chld != null && chld.length > 0)
	    				havemedia = true;
	    		}
	    		
	    		dir = new File(appFiles+"/"+selected_project+"/audio");
	    		if(dir.exists()){
	    			chld = dir.list();
	    			if(chld != null && chld.length > 0)
	    				havemedia = true;
	    		}
	    		}
	    		catch(Exception e){}
	    	    if(havemedia)
	    	    	menu.add(0, DEL_MEDIA_ID, 0, R.string.menu_file_del);
	    	}
	    
	    menu.add(0, MENU_SETTINGS, 0, R.string.settings);	
	    
	   // menu.add(0, MENU_DB_BACKUP, 0, "Backup DB");	
		
	    return super.onPrepareOptionsMenu(menu);

	}
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    super.onMenuItemSelected(featureId, item);
	    switch(item.getItemId()) {
	    case LOAD_PROJECT:
	    	loadProject();
	        break;
		case DELETE_PROJECT:
	    	try{
	    		selected_project = proj_spin.getSelectedItem().toString();
	    	}
	    	catch(NullPointerException npe){
	    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
	    		return false;
	    	}
	    	
	    	if(selected_project.equalsIgnoreCase("")){
	    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
	    		return false;
	    	}
			AlertDialog dialog = new AlertDialog.Builder(this).create();

			dialog.setMessage(this.getResources().getString(R.string.delete_project_1)+" "+selected_project+"? "+this.getResources().getString(R.string.delete_project_2)); //"Delete Project "+selected_project+"? All data and photos will be lost!");
			dialog.setButton(this.getResources().getString(R.string.yes), myOnClickListener); // Yes
			dialog.setButton2(this.getResources().getString(R.string.no), myOnClickListener); // No

			dialog.show();

			break;
		case BACKUP_PROJECT:
			if(checkProject()){
	    		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
	    	
	    		alert.setTitle(R.string.backup_project_1); //"Backup Project");  
	    		alert.setMessage(this.getResources().getString(R.string.backup_project_2)+" "+selected_project+" "+this.getResources().getString(R.string.backup_project_3)); //"Any existing backup file for "+selected_project+" will be overwritten.\n\nContinue?");    	   
	 	                 
	    		alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Yes
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				backupProject();
	    		  
	    			}
	    		});  
	    	    
	    		alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {  // No
	    			public void onClick(DialogInterface dialog, int whichButton) {  
	    			}  
	    		});  
	    		
	    		alert.show();  
	    	}
    	   	break;
		case LOAD_BACKUP:
			if(checkProject()){
					AlertDialog.Builder alert = new AlertDialog.Builder(this);  
	    	
					alert.setTitle(R.string.load_backup_1); //"Load Backup File");  
					alert.setMessage(this.getResources().getString(R.string.load_backup_2)+" "+selected_project+" "+this.getResources().getString(R.string.load_backup_3)); //"Any existing data for "+selected_project+" will be overwritten.\n\nContinue?");    	   
	 	                 
					alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {  // Yes
						public void onClick(DialogInterface dialog, int whichButton) {
							loadBackup();
	    		  
						}
					});  
	    	    
					alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {  // No
						public void onClick(DialogInterface dialog, int whichButton) {  
						}  
					});  
	    		
					alert.show();  
	    	}
    		break;
		case EMAIL_BACKUP:
    		emailBackupFile();
    		break;
		case GROUP_DATA_ID:
			getGroupDataFile();
    		break;
		/*case SYNCH_IMAGES_ID:
    		synchImages();
    		break;
		case SYNCH_VIDEOS_ID:
    		synchVideos();
    		break;
		case SYNCH_AUDIO_ID:
    		synchAudio();
    		break;*/
		case DEL_MEDIA_ID:
    		deleteMediaFiles();
    		break;
		case RESET_SYNCH_ID:
			showResetSynchAlert();
    		break;
		//case REGISTER_ID:
		//	registerPhone();
    	//	break;
		case DEL_ALL:
			showDeleteAllEntriesAlert();
    		break;
		case DEL_SYNCH:
			showDeleteSynchEntriesAlert();
    		break;
		case DEL_REM:
			showDeleteRemoteEntriesAlert();
    		break;
		case DEL_FAIL:
			showDeleteFailedEntriesAlert();
    		break;
		case VERSION:
			showAlert(this.getResources().getString(R.string.version), this.getResources().getString(R.string.version_is)+" "+ APK_VERSION); //"Version", "Version is "+ APK_VERSION);
    		break;
		case LOAD_REMOTE:
			loadRemoteData();
    		break;
		case MENU_SETTINGS:
			Intent i = new Intent(this, ECSettings.class);
	    	startActivity(i);
    		break;
		case MENU_DB_BACKUP:
			try {
				backupDatabase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("DB BACKUP", e.toString());
			}
    		break;	
    		
    	}
	    return true;
	}
        
    public void getProject(String newproject){
    	
    	// This is used so that the project name can be edited if there is an error
    	// Saves having to retype the whole thing
    	input_project = newproject;
    	
    	String project_url = dbAccess.getSettings("url");
    	project_server = project_url;
    	
    	if(newproject == null || newproject.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_name_required)); //"Error", "Project name required");
    		return;
    	}
    	
   	
    	if(newproject.startsWith("SD_")){ //|| newproject.startsWith("sd_")){
     		if(!newproject.endsWith(".xml"))
     			newproject = newproject + ".xml";
    		parseXML = new ParseXML(newproject);   
    	}
    	else if(newproject.startsWith("v1_") || newproject.startsWith("V1_")){
    		is_v1 = true;
    		newproject = newproject.replaceFirst("v1_", "");
    		newproject = newproject.replaceFirst("V1_", "");
    		//if(!newproject.endsWith(".xml"))
     		//	newproject = newproject + ".xml";
    		parseXML = new ParseXML("http://epicollectserver.appspot.com/getForm?projectName="+newproject);
    	}
    	else if(newproject.startsWith("http:") || newproject.startsWith("https:")){
    		if(!newproject.endsWith(".xml"))
     			newproject = newproject + ".xml";
    		parseXML = new ParseXML(newproject);   
    	}
    	else{ // if(newproject.startsWith("http:") || newproject.startsWith("https:")){
    		if(!newproject.endsWith(".xml"))
     			newproject = newproject + ".xml";
    		parseXML = new ParseXML(project_url+"/"+newproject);   
    	}
    	
   	
    	synch_type = 6;
    	getProjectForm();
    	
    }
    
    private void backupProject(){
    	myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    	//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.backing_up), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Backing Up Project...", "Cancel");
        synch_type = 8;
        thread = new Thread(this);
        thread.start();
        	
    }
    
    public void createEntry() {
    	
    	/*if(checkProject() && dbAccess.getValue("project_type").equalsIgnoreCase("lite")){
    		Intent i = new Intent(this, EntryNote2.class);
    		
    		i.putExtra("table", dbAccess.getTableByNum(1));
    		
    		i.putExtra("new", 1);
    		// Not sure this is needed
    		i.putExtra("frombranch", 0);
    		
    		i.putExtra("branch", 0);
    		
    		//This is the only table
    		i.putExtra("select_table_key_column", "Null");
    		i.putExtra("foreign_key", "Null");
    		i.putExtra("select_table", "Null");
    		    		
    		startActivity(i);
    	}*/
    	
    	if(checkProject()){ 
    	   	Intent i = new Intent(this, TableSelect.class); //NewEntry
    	   	startActivityForResult(i, ACTIVITY_NEW);
    	}
    }
    
    public void showHelp() {
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		selected_project = "";
    	}
    	  	
    	if(selected_project.length() > 0)
    		dbAccess.setActiveProject(selected_project);
    	
    	Intent i = new Intent(this, Help.class);
    	//i.putExtra("HELP_TEXT", "This is version "+APK_VERSION+" of EpiCollect+.<br><br>For comments, bugs, queries and suggestions please visit:<br><br>http://www.epicollect.net");
    	i.putExtra("VERSION", APK_VERSION);
    	i.putExtra("PROJECT", selected_project);
    	startActivity(i);
    }
   	
    private boolean checkProject(){
    	//String project;
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return false;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return false;
    	}
    	
    	dbAccess.setActiveProject(selected_project);
    	return true;
    }
    
    //@SuppressWarnings("unchecked")
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	super.onActivityResult(requestCode, resultCode, data);
	        
	 	// Cascades the exit program call
	   	if (requestCode == 123) {
		 	   this.finish();
		 	   }
	    switch(requestCode) {
	    case ACTIVITY_EMAIL:
	    	showAlert(this.getResources().getString(R.string.registration), this.getResources().getString(R.string.confirm_registration)); //"Registration", "An email confirming registration will be sent to your gmail account");
	    	break;
	    case ACTIVITY_EMAIL2:
	    	//showAlert("Backup File Email", "Backup file successfully emailed");
	    	break;
	    }
	} 
    
    
    
    
    private void getProjectForm(){
    	myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    	//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_project), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Loading Project...", "Cancel");
    	  	
    	thread = new Thread(this);
    	thread.start();

    }
    
    public void buildProject(boolean projectloaded){
       	
    	LinkedHashMap<String, HashMap<String, String>> tablekeyhash = parseXML.getTablekeyhash();
    	HashMap<String, HashMap<String, String>> tablerowshash = parseXML.getRows();
    	tableshash = parseXML.getIndividualTables();
    	    	
    	String project = parseXML.getProject();
    	if(dbAccess.checkProject(project)){
    		myProgressDialog.dismiss();
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_exists)); //"Registration", "An email confirming registration will be sent to your gmail account");
    		return;
    	}
  	    dbAccess.setActiveProject(project);
  	    dbAccess.dropTable(project);
  	    
  	    // The tables for the image, video and audio files to monitor if they have
  	    // been synchronised
  	    
  	    dbAccess.createFileTable("Image", project);
  	    dbAccess.createFileTable("Video", project);
  	    dbAccess.createFileTable("Audio", project);
  	    
  	    // This creates the basic project table with features common to each project
  	    // change_synch, remote_xml, image_url, synch_url
  	    StringBuffer table = parseXML.createTable();
  	    dbAccess.createProjectTable(table, project); 
  	    
  	    dbAccess.createProjectRow(parseXML.getProjectHash(), project);
  	    
  	    // Now create the individual tables for each project table
  	    for(String key : tableshash.keySet()){
  	    	dbAccess.dropTable(project+"_"+key);
  	    	dbAccess.createProjectTable(tableshash.get(key), project); 
  	    }
  	    
  	  // Now the rows for each individual project table 
  	    for(String key : tablerowshash.keySet()){ 
  	    	dbAccess.createProjectRow(tablerowshash.get(key), project+"_"+key); 
  	    }
  	    
  	    // Now the data tables for each project table
  	    String key, rkey;
  	    Vector<String> v = new Vector<String>();
  	    for(String key2 : tablekeyhash.keySet()){
  	    	v.addElement(key2);
  	    }
  	    
  	    for(int i = 0; i < v.size(); i++){
  	    	key = v.elementAt(i);
  	    	rkey = "Null";
  	    	if(i > 0)
  	    		rkey = v.elementAt(i-1);
  	    	
  	   // Need to include tablekeyhash.get(key).get("tablekey") as this is the key value 
  	    	// for the table. This is used for the cascade deletes on any branch tables
  	    	String branch_caller = tablekeyhash.get(key).get("branch_caller");
  	    	String branch_caller_key = "";
  	    	if(is_v1){}
  	    	else if(branch_caller != null && branch_caller.length() > 0){
  	    		branch_caller_key = tablekeyhash.get(branch_caller).get("tablekey");
  	    		rkey = "Null";
  	    	}
  	    	else if(!tablekeyhash.get(key).get("fromgroup").equalsIgnoreCase("0"))
  	    		rkey = "Null";
  	    	dbAccess.createDataTable(project, key, rkey, branch_caller, branch_caller_key); 
  	    	
  	    	File project_dir = new File(appFiles+"/"+project);
  	    	try{
  	        	if(!project_dir.exists())
  	        		project_dir.mkdir();
  	        	}
  	        catch(Exception e){
  	        	showAlert(this.getResources().getString(R.string.error), "Cannot Create Project Directory"); //"Error", "SD card not present. Required for photo, video and audio capture");
  	        }

	    }

  	    //Now the table keys and numbers

  	    dbAccess.createKeyTable(project);
  	    for(String key2 : tablekeyhash.keySet()){
	    	dbAccess.createKeyRow(tablekeyhash.get(key2), project); 
	    }
  	      	    
	  	 String[] allprojects = dbAccess.getProjects();
	  	
	     ArrayList<String> temparray = new ArrayList<String>();
	     for (int i = 0; i < allprojects.length; i++) {
	    	 temparray.add(allprojects[i]);
		 }
	
	     ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparray);
	  	 aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  	 proj_spin.setAdapter(aspnLocs); 
	  	 proj_spin.setSelection(1);
	  	 
	  /*	String radioimg_dir = Environment.getExternalStorageDirectory()+"/EpiCollect/radioimgdir_epicollect_" + project;
	    	
	    	// Get radiobutton images
	    	Log.i("RadioBut Dir",dbAccess.getValue("button_image_url")+" "+radioimg_dir);
	    	//if(dbAccess.getValue("button_image_url").length() > 0){
	    		//File f = new File(radioimg_dir);
		    	//if(!f.exists()){
		    	//	f.mkdir();
		    	//}
	    		dbAccess.loadImageFile(dbAccess.getValue("button_image_url"), radioimg_dir); */
	    	//}
	    	
	  	 // Projects such as Mali are slow to build so ensure message is only shown when 
	  	 // database build is complete
	  	 if(projectloaded){
	  		 if(synch_type != 7)
	  			 showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.project_loaded)); //"Success", "Project Loaded");
	  		 myProgressDialog.dismiss();
	  	 }

    }
    
    private void loadProject(){
    	
    	LayoutInflater factory = LayoutInflater.from(this);
    	final View filterView = factory.inflate(R.layout.main_project_load, null); 
    	
    	final TextView proj_text = (TextView) filterView.findViewById(R.id.proj_text);
        proj_text.setText(input_project);
        
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        	
    	alert.setTitle(R.string.load_project); //"Load Project");  
    	   alert.setView(filterView);    	   
 	                 
    	  alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // OK 
    	  public void onClick(DialogInterface dialog, int whichButton) {
    		  String project = proj_text.getText().toString();
    		  getProject(project);
    		  
    	  }
    	  });  
    	    
    	  alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  // Cancel
    	    public void onClick(DialogInterface dialog, int whichButton) {  
    	    }  
    	  });  
    	     
    	  alert.show();  
    	
    }
    
    private void deleteProject(){
    	
    	for(String key : dbAccess.getKeys().keySet()){
  	    	dbAccess.dropTable(selected_project+"_"+key);
  	    	dbAccess.dropTable("data_"+selected_project+"_"+key);
  	    }
    	  	    
    	dbAccess.dropTable("keys_"+selected_project);
    	dbAccess.deleteProject(selected_project);
    	dbAccess.dropTable(selected_project+"_Image");
    	dbAccess.dropTable(selected_project+"_Video");
    	dbAccess.dropTable(selected_project+"_Audio");
    	
    	//String thumbdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + selected_project; 
        //String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + selected_project; 
        //String videodir = Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + selected_project; 
        //String audiodir = Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + selected_project; 
        //String radiodir;
        
        Vector<String> dirvec = new Vector<String>();
        //if(dbAccess.getValue("button_image_url").length() > 0)
        //	dirvec.addElement(Environment.getExternalStorageDirectory()+"/EpiCollect/radioimgdir_epicollect_" + selected_project); 
                
        dirvec.addElement(appFiles+"/"+selected_project+"/thumbs");
        dirvec.addElement(appFiles+"/"+selected_project+"/images");
        dirvec.addElement(appFiles+"/"+selected_project+"/videos");
        dirvec.addElement(appFiles+"/"+selected_project+"/audio");
        if(dbAccess.getValue("button_image_url").length() > 0)
        	dirvec.addElement(appFiles+"/"+selected_project+"/radioimg");
        
        File dir;
        for(String s : dirvec){
        	try{
            	dir = new File(s);
            	deleteDirectory(dir);
            	}
            catch(Exception e){}
        }
        
        if(dbAccess.getValue("button_image_url").length() > 0)
        	dbAccess.loadImageFile(dbAccess.getValue("button_image_url"), appFiles+"/"+selected_project+"/radioimg");
       /* try{
        	dir = new File(thumbdir);
        	deleteDirectory(dir);
        	}
        catch(Exception e){}
        
        try{
        	dir = new File(picdir);
        	deleteDirectory(dir);
        	}
        catch(Exception e){}
        
        try{
        	dir = new File(videodir);
        	deleteDirectory(dir);
        	}
        catch(Exception e){}
        
        try{
        	dir = new File(audiodir);
        	deleteDirectory(dir);
        	}
        catch(Exception e){} */

	   	String[] allprojects = dbAccess.getProjects();
	  	
	      ArrayList<String> temparray = new ArrayList<String>();
	      for (int i = 0; i < allprojects.length; i++) {
	    	  temparray.add(allprojects[i]);
		  }

	      ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparray);
	  		aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	  		proj_spin.setAdapter(aspnLocs); 
	  		if(allprojects.length > 1){
	  			try{
	  				proj_spin.setSelection(1);
	  			}
	  			catch(IndexOutOfBoundsException e){}
	  	    }
    }
    
    private void deleteDirectory(File path){
    	try{
    		if( path.exists() ) {
    			File[] files = path.listFiles();
    			for(int i=0; i<files.length; i++) {
    				if(files[i].isDirectory()) {
    					deleteDirectory(files[i]);
    				}
    				else {
    					files[i].delete();
    				}
    			}
    			path.delete();
    	    }
    	}
    	catch(Exception e){}

    }
    
    
    private void loadRemoteData(){
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	
    	// Need to declare this before the version check otherwise if it is "1" it never gets initialised
    	// and causes a null pointer exception
    	remotecb = new CheckBox(this);
 	   	remotecb.setText(this.getResources().getString(R.string.local_download)); //"Local Download");
 	   	remotecb.setChecked(true);

    	if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("1")){
    		selected_table = "Data";
    		getRemoteDataFile(false);
    		return;
    	}
    	
    	selected_table = "Select";
    	
    	    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	
    	alert.setTitle(R.string.select_base_table); //"Select Base Table");
    	
    	LinearLayout ll = new LinearLayout(this);
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
    	ll.setOrientation(1);
	 	ll.setGravity(Gravity.CENTER);
	 	ll.setLayoutParams(lp);
	 	   
 	    ScrollView s = new ScrollView(this);
 	    ll.addView(s);
 	    	   
 	   LinearLayout ll2 = new LinearLayout(this);
 	   ll2.setOrientation(1);
 	   ll2.setGravity(Gravity.CENTER);
 	   ll2.setLayoutParams(lp);
 	  	
	    s.addView(ll2); 

 	   	if(!dbAccess.getValue("local_remote_xml").equalsIgnoreCase("None")){
 	   		ll2.addView(remotecb);
 	   	}
 	   	else{
 	   		remotecb.setChecked(false);
 	   	}

 		TextView tv = new TextView(this);
 		tv.setTextSize(18);
 		tv.setGravity(Gravity.CENTER_HORIZONTAL);
 		tv.setText(R.string.select_download_table); //"Select table for download");

 		ll2.addView(tv);
 	   
 		Spinner spin = new Spinner(this); 
	    	
 		ll2.addView(spin);
 		
 		Vector<String> tempvec = new Vector<String>();
 		tempvec.addElement(this.getResources().getString(R.string.select)); //"Select");
		
 		for(String table : dbAccess.getAllKeys(0).keySet())
 			tempvec.addElement(table);
	
	    	
 		ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tempvec);
    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spin.setAdapter(aspnLocs); 
    	
    	spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	   	selected_table = (String)parent.getItemAtPosition(pos);
	    	    }
	    	    public void onNothingSelected(AdapterView<?> parent) {
	    	    }
	    	}); 

    		alert.setPositiveButton(R.string.get_data, new DialogInterface.OnClickListener() {  // Get Data
    		public void onClick(DialogInterface dialog, int whichButton) {
    			getRemoteDataFile(false);
    			return;
	         }
    	  }); 

    	    
    	  alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {  // Cancel
    	    public void onClick(DialogInterface dialog, int whichButton) { 
    	    	return;
    	    }  
    	  });  
    	     
    	  alert.setView(ll); 
    	  alert.show();  
    	
    	
    }
    
    private void deleteMediaFiles(){
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	selected_table = "Select";
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	
    	alert.setTitle(R.string.confirm_delete); //"Confirm Delete");
    	alert.setMessage(R.string.confirm_media_delete); //"Confirm unused media files to delete");
    	
    	LinearLayout ll = new LinearLayout(this);
    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
    	ll.setOrientation(1);
	 	ll.setGravity(Gravity.CENTER);
	 	ll.setLayoutParams(lp);
	 	   
 	    ScrollView s = new ScrollView(this);
 	    ll.addView(s);
 	    
 	   LinearLayout ll2 = new LinearLayout(this);
 	   ll2.setOrientation(1);
 	   ll2.setGravity(Gravity.CENTER);
 	   ll2.setLayoutParams(lp);

	    s.addView(ll2); 

	    deleimagescb = new CheckBox(this);
	    deleimagescb.setChecked(false);
	    deletevideocb = new CheckBox(this);
	    deletevideocb.setChecked(false);
	    deleteaudiocb = new CheckBox(this);
	    deleteaudiocb.setChecked(false);
	    
	    File dir, dir2;
	    String[] chld, chld2;
	    boolean thumb = false, image = false;
	    try{
	    	dir = new File(appFiles+"/"+selected_project+"/images");
	    	if(dir.exists()){
	    		chld = dir.list();
	    		if(chld != null && chld.length > 0)
	    			image = true;
	    	}
	    	dir2 = new File(appFiles+"/"+selected_project+"/thumbs");
	    	if(dir.exists()){
	    		chld2 = dir2.list();
	    		if(chld2 != null && chld2.length > 0)
	    			thumb = true;
	    	}
	    	if(image == true || thumb == true){
	    		deleimagescb.setText("Delete Images");
 	   			ll2.addView(deleimagescb);
	    	}
	  
	    	dir = new File(appFiles+"/"+selected_project+"/videos");
	    	if(dir.exists()){
	    		chld = dir.list();
	    		if(chld != null && chld.length > 0){
	    			deletevideocb.setText("Delete Videos");
	    			ll2.addView(deletevideocb);
	    		}
	    	}
	    	dir = new File(appFiles+"/"+selected_project+"/audio");
	    	if(dir.exists()){
	    		chld = dir.list();
	    		if(chld != null && chld.length > 0){
	    			deleteaudiocb.setText("Delete Audio Files");
	    			ll2.addView(deleteaudiocb);
	    		}
	    	}
	    }
	    catch(Exception e){}
	    

    		alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {  // Delete
    		public void onClick(DialogInterface dialog, int whichButton) {
    			deleteFiles();
    			return;
	         }
    	  }); 

    	    
    	  alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // Cancel  
    	    public void onClick(DialogInterface dialog, int whichButton) { 
    	    	return;
    	    }  
    	  });  
    	     
    	  alert.setView(ll); 
    	  alert.show();  
    }
      
    private void deleteFiles(){
    	  	
    	//String sd_dir = Environment.getExternalStorageDirectory()+"/EpiCollect";
    	Vector<String> files;
    	File filetodelete;
    	File dir;
    	String[] chld;
    	if(deleimagescb.isChecked()){
    		files = dbAccess.getFiles(selected_project+"_Image");
    		try{
    		dir = new File(appFiles+"/"+selected_project+"/thimbs");
    		// Not necessary to keep checking if directory exists but just being extra careful!
    		if(dir.exists()){
    			chld = dir.list();
    			if(chld != null && chld.length > 0){
    				for(int i = 0; i < chld.length; i++){
    					if(!files.contains(chld[i])){
    						
    							filetodelete = new File(appFiles+"/"+selected_project+"/thumbs/"+chld[i]);
    							filetodelete.delete();
    						}
    						
    					}
    				}
    			}
    		}
    		catch(Exception e){}
    		
    		try{
    		dir = new File(appFiles+"/"+selected_project+"/images");
    		if(dir.exists()){
    			chld = dir.list();
    			if(chld != null && chld.length > 0){
    				for(int i = 0; i < chld.length; i++){
    					if(!files.contains(chld[i])){
    						
    							filetodelete = new File(appFiles+"/"+selected_project+"/images/"+chld[i]);
    							filetodelete.delete();
    						}
    						
    					}
    				}
    			}
    		}
    		catch(Exception e){}
    	}
    	 
    	if(deletevideocb.isChecked()){
    	    files = dbAccess.getFiles(selected_project+"_Video");
    	    try{
    		dir = new File(appFiles+"/"+selected_project+"/videos");
    		if(dir.exists()){
    			chld = dir.list();
    			if(chld != null && chld.length > 0){
    				for(int i = 0; i < chld.length; i++){
    					if(!files.contains(chld[i])){
   							filetodelete = new File(appFiles+"/"+selected_project+"/videos/"+chld[i]);
    						filetodelete.delete();
    					}
    				}
    			}
    		}
    	    }
		catch(Exception e){}
    	}
    	
    	if(deleteaudiocb.isChecked()){
    	    files = dbAccess.getFiles(selected_project+"_Audio");
    	    try{
    		dir = new File(appFiles+"/"+selected_project+"/audio");
    		if(dir.exists()){
    			chld = dir.list();
    			if(chld != null && chld.length > 0){
    				for(int i = 0; i < chld.length; i++){
    					if(!files.contains(chld[i])){
    						filetodelete = new File(appFiles+"/"+selected_project+"/audio/"+chld[i]);
    						filetodelete.delete();
    						}
    					}
    				}
    			}
    		}
    	    catch(Exception e){}
    	}
    }
    
    private void getRemoteDataFile(boolean images){
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	getimages = images;
    	
    	if(selected_table.equalsIgnoreCase("Select")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.data_download_select_error)); //"Error", "Table must be selected for data download");
			return;
    	}
    	
    	dataresult = 0;
    	myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    	//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Loading Project Data...", "Cancel");
    	
    	synch_type = 5;
    	
    	thread = new Thread(this);
    	thread.start();
    
    }
    
    private void getGroupDataFile(){
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    	//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Loading Project Data...", "Cancel");
    	
    	synch_type = 9;
    	
    	thread = new Thread(this);
    	thread.start();
    	
     

    }

  /*  private void synchImages(){
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(dbAccess.checkSynchronised()){
    		checkWiFiConnection("Full size images");
    	}
    	else
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.synch_all_message_images)); //"Error", "Synchronise all entries before synchronising images");
	}
    
    
    private void synchVideos(){
    	
    	// The database table has a Y/N value for synched videos
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(dbAccess.checkSynchronised()){
    		checkWiFiConnection("Video");
    	}
    	else
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.synch_all_message_videos)); //"Error", "Synchronise all entries before synchronising videos");
	}
    
    private void synchAudio(){
    	
    	// The database table has a Y/N value for synched audio
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(dbAccess.checkSynchronised()){
    		checkWiFiConnection("Audio");
    	}
    	else
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.synch_all_message_audio)); //"Error", "Synchronise all entries before synchronising audio files");
	} */


    private void checkWiFiConnection(final String type){ // boolean
    	ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    	if (mWifi.isConnected()) {
    		if(type.equalsIgnoreCase("Full size images"))
    			synchroniseData(true, false, false);
    		else if(type.equalsIgnoreCase("Video"))
    			synchroniseData(false, true, false);
    		else if(type.equalsIgnoreCase("Audio"))
    			synchroniseData(false, false, true);
    	}

    	else{
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        	
        	alert.setTitle(R.string.wifi_unavailable); //"WiFi Unavaialable");  
     	    alert.setMessage(type+" "+this.getResources().getString(R.string.file_synch_confirm)); //files should only be synchronised using wi-fi due to file size.\nContinue anyway?");             
        	alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {  // Yes
        	public void onClick(DialogInterface dialog, int whichButton) {
        		if(type.equalsIgnoreCase("Full size images"))
        			synchroniseData(true, false, false);
        		else if(type.equalsIgnoreCase("Video"))
        			synchroniseData(false, true, false);
        		else if(type.equalsIgnoreCase("Audio"))
        			synchroniseData(false, false, true);
        		}
        	});
        	
        	alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {  // No
            	public void onClick(DialogInterface dialog, int whichButton) {
            		
            		}
            	});  
        	    
        	alert.show();    
    	}
    	
    }
    
    
    
    private void setSynchButton(){
    	
    	allsynched = true;
    	mediasynched = true;
    	selected_project = proj_spin.getSelectedItem().toString();
    	try{
    		allsynched= dbAccess.checkSynchronised();
    		if(dbAccess.checkFileValue(selected_project+"_Image", "synch", "N") || 
    				dbAccess.checkFileValue(selected_project+"_Video", "synch", "N") ||
    				dbAccess.checkFileValue(selected_project+"_Audio", "synch", "N"))
    			mediasynched = false;
    	}
    	catch(Exception e){
    		Log.i("SYNCH EXCEPTION", e.toString());
    		// If project has failed to load properly from the xml this may fail and prevents EpiCollect from loading
    		
    	}
    	
    	if(allsynched && mediasynched){
        	synchButton.setText(R.string.no_data_to_send); //"No Data to Send to Server(s)");
        	synchButton.setEnabled(false);
        }
    	else if(!allsynched){
        	synchButton.setText(R.string.send_data); //"Send Data to Remote Server(s)"); 
        	synchButton.setEnabled(true);
        }
        else{
        	synchButton.setText(R.string.synch_media_button); //"Send Data to Remote Server(s)"); 
        	synchButton.setEnabled(true);
        }
    	
    }
    
    public void showResetSynchAlert(){ 
    	new AlertDialog.Builder(this)
        .setTitle(this.getResources().getString(R.string.reset_synch)) //"Reset Synchronised")
        .setMessage(this.getResources().getString(R.string.reset_synch_confirm)) //"This will upload all records to server on next synchronisation.\n\nReset all synchronised data?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Yes

             public void onClick(DialogInterface dialog, int whichButton) {
            	 resetSynch();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // No

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
    
    private void resetSynch(){
    	 
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	for(String key : dbAccess.getKeys().keySet()){
  	    	dbAccess.updateResetAllRows(key);
  	    }
    	
    	setSynchButton();
    }
    
    private void synchroniseData(final boolean images, final boolean video, final boolean audio){ 
    	
    	if(images){
    		synch_type = 1;
    		myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    		//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.uploading_images), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Uploading Images...", "Cancel");
    	}
    	else if(video){
    		synch_type = 2;
    		myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    		//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.uploading_videos), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Uploading Videos...", "Cancel");
    	}
    	else if(audio){
    		synch_type = 3;
    		myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    		//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.uploading_audio), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Uploading Audio Files...", "Cancel");
    	}
    	else{
    		synch_type = 4;
    		myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
    		//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.uploading_data), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Uploading Data...", "Cancel");
    	}
    	
    	thread = new Thread(this);
    	thread.start();

    }
    
    private void synchroniseMedia(){
    	
    	// In case this is called after media have been synched. Prevents screens appearing when all synched
   	 	//setSynchButton();
   	 	//if(mediasynched)
    	//	return;
    	if(!dbAccess.checkFileValue(selected_project+"_Image", "synch", "N") &&
				!dbAccess.checkFileValue(selected_project+"_Video", "synch", "N") &&
				!dbAccess.checkFileValue(selected_project+"_Audio", "synch", "N"))
    		return;
    	
   		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        	
       	alert.setTitle(R.string.synch_media_files);  // Hierarchy
       	
       	dbAccess = new DBAccess(this);
	    dbAccess.open();
	  	
	    boolean havemedia = false;
    	final RadioGroup rg = new RadioGroup(this);
	  	
	    picbut = new Button(this);
	    
	    LinearLayout rgl = new LinearLayout(this);
   	
    	int total = dbAccess.getMediaFileCount(selected_project+"_Image", "synch", "N");
    	if(total > 0){
    		RadioButton rbi = new RadioButton(this);
    		rbi.setTag("Full Size Images");
   	 		rbi.setText(this.getResources().getString(R.string.menu_photo_synch)+" - "+total); //+" - "+total); //"Synch Images");	
  	   	 	//rbi.setChecked(true);
   	 		rg.addView(rbi);
   	 		havemedia = true;
    	}
    	
    	total = dbAccess.getMediaFileCount(selected_project+"_Video", "synch", "N");
    	
    	if(total > 0){
    		
    		RadioButton rbv = new RadioButton(this);
    		rbv.setTag("Video");
   	 		rbv.setText(this.getResources().getString(R.string.menu_video_synch)+" - "+total); //+" - "+total); //"Synch Images");
   	 		//if(!havemedia)
   	 		//	rbv.setChecked(true);
   	 		rg.addView(rbv);
   	 		havemedia = true;
    	}
  	 	
   	 	total = dbAccess.getMediaFileCount(selected_project+"_Audio", "synch", "N");
   	 	if(total > 0){
   	 		RadioButton rba = new RadioButton(this);
   	 		rba.setTag("Audio");
   	 		rba.setText(this.getResources().getString(R.string.menu_audio_synch)+" - "+total); //+" - "+total); //"Synch Images");
   	 		//if(!havemedia)
   	 		//	rba.setChecked(true);
   	 		rg.addView(rba);
   	 		havemedia = true;
   	 	}	
   	 
   	 	rgl.addView(rg); 
       	     	
   	 	if(havemedia == true){
   	 		alert.setPositiveButton("Send Data", new DialogInterface.OnClickListener() { // R.string.ok  
   	 			public void onClick(DialogInterface dialog, int whichButton) {
   	 				try{
   	 					int selected = rg.getCheckedRadioButtonId();
   	 					RadioButton b = (RadioButton) rg.findViewById(selected);
   	 					checkWiFiConnection(b.getTag().toString());	
   	 				}
   	 				catch(NullPointerException npe){ // In case no buttons are selected
   	 					
   	 				}
   	 				return;
       		
   	 			}
   	 		});
   	 	}
       	
       	alert.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() { // R.string.ok  
           	public void onClick(DialogInterface dialog, int whichButton) {
           		//setSynchButton();
           	    return;		  
           		
           	  }
           	  });  
		
		alert.setView(rgl);
       	    
       	alert.show();    
   	}
    
   

    public void run() {
    	 	  
    	if(synch_type == 8){
    		if(dbAccess.backupProject(android_id, false)){
    			myProgressDialog.dismiss();
    			Looper.prepare();
				showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.backup_success)); //"Success", "Project Backup Successful");
			}
			else{
				myProgressDialog.dismiss();
				Looper.prepare();
				showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.backup_failed)); //"Error", "Project Backup Failed. Check SD Card");
			}
    		
    	}
    	else if(synch_type == 6){ // || synch_type == 7){
 		 boolean result = false;
 		 result = false;
         try{
        	 if(synch_type == 6){
        		 result = parseXML.getXML();
        	 }
        	 /*else{
        		 String str;
        		 StringBuffer buf = new StringBuffer();			
        	  	 InputStream in = this.getResources().openRawResource(R.drawable.demo_project);
        	  	 InputStreamReader is = new InputStreamReader(in); 

        	  	 BufferedReader reader = new BufferedReader(is);
        	  	 if (is!=null) {							
        	  		while ((str = reader.readLine()) != null) {	
        	  			buf.append(str + "\n" );
        	  			}				
        	  		}		
        	  	 is.close();
        	  	 
        		 result = parseXML.getDemoXML(buf);
        	 }*/
         	
         } catch (Exception e) {
         	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
         }

         Looper.prepare();
         if(!result){
         	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_retrieval_failed)); //"Error", "Project retrieval failed. Is project name correct?"); //showAlert("Success", "Project Loaded");
         	myProgressDialog.dismiss();
          }
         
         /*You can use threads but all the views, and all the views related APIs,
         must be invoked from the main thread (also called UI thread.) To do
         this from a background thread, you need to use a Handler. A Handler is
         an object that will post messages back to the UI thread for you. You
         can also use the various post() method from the View class as they
         will use a Handler automatically. */
         else{ 
         	mHandler.post(new Runnable() {
             	public void run() { 
                 	buildProject(true); }
         	});
         }
 	  }
 	  else if(synch_type == 5){
 		 dataresult = 0;
 		Looper.prepare();
         try{
        	 
        	 boolean remoteset = remotecb.isChecked();
 
        	 if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("1")){
        		 dataresult = dbAccess.getRemoteDataV1(); //fetchXML();
        	 }
        	 else{
        		 dataresult = dbAccess.getRemoteData(selected_project, selected_table, "", "", remoteset, getimages, false, android_id, email); //sIMEI
        	 }
         	
         } catch (Exception e) {
         	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
         }
         // Dismiss the Dialog
         myProgressDialog.dismiss();
         if(dataresult == 0){
         	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_data_retrieval_failed)); //"Error", "Project data retrieval failed"); 
         	myProgressDialog.dismiss();
         }
         // Return 0 for error, 1 for success and images to follow and 2 for success and no images 
         else if(dataresult == 1)        	
            showImageAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.data_loaded)+".\n\n"+this.getResources().getString(R.string.load_images)); //"Success", "Project data successfully loaded.\n\nLoad associated images?");
         else if(dataresult == 3)        	
             showAlert(this.getResources().getString(R.string.warning), this.getResources().getString(R.string.too_many_entries)); //"Warning", "Returned data contains too many entries.\n\nSelect higher table");
         else           
         	showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.data_loaded)); //"Success", "Project data successfully loaded");
         
 	  }
 	 else if(synch_type == 9){
 		 dataresult = 0;
 		Looper.prepare();
         try{
        	dataresult = dbAccess.getRemoteData(selected_project, "", "", "", false, false, true, android_id, email); // sIMEI
         	
         } catch (Exception e) {
         	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
         }
         // Dismiss the Dialog
         myProgressDialog.dismiss();
         if(dataresult == 0){
         	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.group_data_failed)); //"Error", "Project group data retrieval failed");
         	myProgressDialog.dismiss();
         }
         else           
         	showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.data_loaded)); //"Success", "Project data successfully loaded");
         
 	  }
 	else if(synch_type == 10){
		 dataresult = 0;
		Looper.prepare();
        try{
        	dataresult = dbAccess.loadFile(appFiles+"/"+selected_project+"/"+android_id+"_data_backup.txt"); 
        	
        } catch (Exception e) {
        	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
        }
        // Dismiss the Dialog
        myProgressDialog.dismiss();
        if(dataresult == 0){
        	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_backup_loading_failed)); //"Error", "Loading of backup data file failed"); 
        	myProgressDialog.dismiss();
        }
        else           
        	showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.data_backup_loaded)); //"Success", "Project backup data successfully loaded");
        
	  }
 	  else{
 		 String synchresult = this.getResources().getString(R.string.synch_failed); //"Synchronisation Failed";
 	 	 
         try{
         	if(synch_type == 1){
         		synchresult = dbAccess.uploadAllImages(android_id, email); // sIMEI
         	}
         	else if(synch_type == 2){
         		synchresult = dbAccess.uploadAllVideos(android_id, email);
         	}
         	else if(synch_type == 3){
         		synchresult = dbAccess.uploadAllAudio(android_id, email);
         	}
         	else if(synch_type == 4){
         		
         		if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("2")){
         			synchresult = dbAccess.synchroniseAll(android_id, email);
         			//if(synchresult.equalsIgnoreCase("Success"))
         			//	synchresult = "Synchronisation successful";
         		}
         		else{
             		synchresult = dbAccess.synchroniseV1(android_id, email);
         		}
         	}
         } catch (Exception e) {
         	Log.i(getClass().getSimpleName(), "SYNCH ERROR: "+ e);
         }
         // Dismiss the Dialog
         myProgressDialog.dismiss();
         Looper.prepare();
         
         if(synchresult.equalsIgnoreCase(this.getResources().getString(R.string.synch_success))){ //synchresult.startsWith("ERROR:")){ 
        	 showAlert(this.getResources().getString(R.string.success), synchresult); // "Success"
        	    	
        }
         else if(synchresult.startsWith("Success")){
        	 synchresult = synchresult.replaceFirst("Success ", "");
       	 	 ////synchroniseMedia();
       	 	 //mHandler.post(new Runnable() {
         	//	public void run() {
         	//		setSynchButton();
         	//	}
       	 	// });
       	 	//if(!mediasynched)
 				synchroniseMedia();
 				mHandler.post(new Runnable() {
 		         	public void run() {
 		         		setSynchButton();
 		       		}
 		  	 	});
        	 showAlert(this.getResources().getString(R.string.success), synchresult); //, synch_type);
        	         	 
         }
		else{
			showAlert(this.getResources().getString(R.string.error), synchresult); // "Error"
		}
         /*You can use threads but all the views, and all the views related APIs,
         must be invoked from the main thread (also called UI thread.) To do
         this from a background thread, you need to use a Handler. A Handler is
         an object that will post messages back to the UI thread for you. You
         can also use the various post() method from the View class as they
         will use a Handler automatically. */
         if(synch_type == 4){
         	mHandler.post(new Runnable() {
         		public void run() {
         	    	setSynchButton();
         		}
         	});
         }
         
 	  }
         Looper.loop();
         Looper.myLooper().quit(); 
         
        
          
    }

    
    class ButtonListener implements OnClickListener{
		public void onClick(DialogInterface dialog, int i) {
			switch (i) {
			case AlertDialog.BUTTON1:
				deleteProject();
			break;
			case AlertDialog.BUTTON2:
			/* Button2 is clicked. Do something */
			break;
			}
		}
      }
    
  
    public void showAlert(String title, String result){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() { // "OK"

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
    
   /* public void showAlert(String title, String result, int type){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() { // "OK"

             public void onClick(DialogInterface dialog, int whichButton) {
            	 if(synch_type == 1)
            		 setImageSynchButton();
            	 else if(synch_type == 2)
            		 setVideoSynchButton();
            	 else if(synch_type == 3)
            		 setAudioSynchButton();
             }
        }).show();	
    } */
        
    public void showImageAlert(String title, String result){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Yes

             public void onClick(DialogInterface dialog, int whichButton) {
            	 getRemoteDataFile(true);
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // No

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
    
    private void createCancelProgressDialog(String title, String message, String buttonText)
    {
    	myProgressDialog = new ProgressDialog(this);
    	myProgressDialog.setTitle(title);
    	myProgressDialog.setMessage(message);
    	myProgressDialog.setButton(buttonText, new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
                // Use either finish() or return() to either close the activity or just the dialog
                endThread();
            	return;
            }
        });
    	myProgressDialog.show();
    }

    private void endThread(){
    	//thread.stop();
    }
    
   /* private void registerPhone(){
    	Account[] accounts = AccountManager.get(this).getAccounts();
    	for (Account account : accounts) {
    	  // TODO: Check possibleEmail against an email regex or treat
    	  // account.name as an email address only for certain account.type values.
    	  String possibleEmail = account.name;
    	  if(account.type.equalsIgnoreCase("com.google") && possibleEmail.contains("@gmail.com")){
    		  
    		  //TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    		  final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    		  final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		  emailIntent.setType("plain/text");
    		  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ dbAccess.getValue("reg_email")});
    		  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.email_1)); //"EpiCollect Phone Registraion");
    		  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, this.getResources().getString(R.string.email_2)+": "+possibleEmail+"\n"+this.getResources().getString(R.string.email_3)+": "+android_id); // "Register Email: "+possibleEmail+"\nPhone ID: "+mTelephonyMgr.getDeviceId())
    		  startActivityForResult(Intent.createChooser(emailIntent, this.getResources().getString(R.string.email_4)+"..."), ACTIVITY_EMAIL);
    		  return;
    	  }
    	}

    } */
    

    
    private void loadBackup(){
    	try{
        	File f = new File(appFiles+"/"+selected_project+"/"+android_id+"_data_backup.txt");
        	if(!f.exists())
        		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.no_backup)); //"Error", "No project backup file available");
        		else{
        			myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);
        			//createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_backup)+"...", this.getResources().getString(R.string.cancel));
        	        synch_type = 10;
        	        thread = new Thread(this);
        	        thread.start();
        		}
        	}
        catch(Exception e){
        	showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.no_backup)); //"Error", "No project backup file available");
        }
    	
    	
    	
    }
	
    	  
    private void emailBackupFile(){
    	
    	try{
    		selected_project = proj_spin.getSelectedItem().toString();
    	}
    	catch(NullPointerException npe){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	if(selected_project.equalsIgnoreCase("")){
    		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.project_required)); //"Error", "Project Required");
    		return;
    	}
    	
    	try{
        	File f = new File(appFiles+"/"+selected_project+"/"+android_id+"_data_backup.txt");
        	if(!f.exists()){
        		showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.backup_not_present)); //"Error", "Backup file not present, backup project first");
        		return;
        	}
        }
        catch(Exception e){
        }
    	Account[] accounts = AccountManager.get(this).getAccounts();
    	for (Account account : accounts) {
    	  // TODO: Check possibleEmail against an email regex or treat
    	  // account.name as an email address only for certain account.type values.
    	  String possibleEmail = account.name;
    	  if(account.type.equalsIgnoreCase("com.google") && possibleEmail.contains("@gmail.com")){
    		  
    		  //TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    		  final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    		  final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		  emailIntent.setType("plain/text");
    		  emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ dbAccess.getValue("reg_email")});
    		  emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.email_5)+" - "+selected_project);
    		  emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, this.getResources().getString(R.string.email_6)+" - "+selected_project+"\n\n"+this.getResources().getString(R.string.email_3) +": "+android_id); // +": "+mTelephonyMgr.getDeviceId()
    		  try{
	            	File f = new File(appFiles+"/"+selected_project+"/"+android_id+"_data_backup.txt");
	            	if(f.exists())
	            		emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(f));
	            	}
	            catch(Exception e){
	            }
    		  
    		  startActivityForResult(Intent.createChooser(emailIntent, this.getResources().getString(R.string.email_4)), ACTIVITY_EMAIL2);
    		  return;
    	  }
    	}

    }
    
    public void showDeleteAllEntriesAlert(){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.delete_entries) //"Delete Entries?")
        .setMessage(this.getResources().getString(R.string.delete_entries_confirm)) //"This will delete all entries and all associated data for this project.\n\nContinue?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Yes

             public void onClick(DialogInterface dialog, int whichButton) {
            	 deleteAllEntries();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // No

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();
    }
    
    private void deleteAllEntries(){
		 
    	Vector<String> alltables = dbAccess.getKeysVector();
		 //String table = alltables.elementAt(0);
		  
		 //dbAccess.deleteAllRows(table);
		 
		 for(String table : alltables)
			 dbAccess.deleteAllRows(table);
		 
		 dbAccess.updateFileTable(1, alltables.size());
		 
		 showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.entries_deleted)); // "All Entries Deleted");

	 }
    
    public void showDeleteSynchEntriesAlert(){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.delete_entries) //"Delete Entries?")
        .setMessage(this.getResources().getString(R.string.delete_synch_entries_confirm)+".\n\n"+this.getResources().getString(R.string.continue_question)) // "This will delete all synchronised entries and all associated data for this project.\n\nContinue?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 deleteAllSynchEntries();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();
    }
    
    private void deleteAllSynchEntries(){
		 
    	Vector<String> alltables = dbAccess.getKeysVector();
		 //String table = alltables.elementAt(0);
		  
    	for(String table : alltables)
    		dbAccess.deleteAllSynchRows(table, "None");
		 
		 dbAccess.updateFileTable(1, alltables.size());
		 
		 showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.synch_entries_deleted)); // "Success", "All Synchronised Entries Deleted");

	 }
    
    public void showDeleteRemoteEntriesAlert(){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.delete_entries) //"Delete Entries?")
        .setMessage(this.getResources().getString(R.string.delete_remote_entries_confirm)+".\n\n"+this.getResources().getString(R.string.continue_question)) // "This will delete all remote entries and all associated data for this project.\n\nContinue?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 deleteRemoteEntries();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();
    }
    
    public void showDeleteFailedEntriesAlert(){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.delete_entries) //"Delete Entries?")
        .setMessage(this.getResources().getString(R.string.delete_orphan_entries_confirm)+".\n\n"+this.getResources().getString(R.string.continue_question)) // "This will delete all orphaned entries and all associated data for this project.\n\nContinue?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 deleteFailedEntries();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();
    }
    
    private void deleteRemoteEntries(){
		 
    	Vector<String> alltables = dbAccess.getKeysVector();
		 //String table = alltables.elementAt(0);
		  
    	for(String table : alltables)
    		dbAccess.deleteAllRemRows(table, "None");
		 
		 dbAccess.updateFileTable(1, alltables.size());
		 
		 showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.remote_entries_deleted)); // "Success", "All Remote Entries Deleted");

	 }
    
    private void deleteFailedEntries(){
		 
    	Vector<String> alltables = dbAccess.getKeysVector();
		 //String table = alltables.elementAt(0);
		  
    	for(String table : alltables)
    		dbAccess.deleteAllOrphanRows(table, "None");
		 
		 dbAccess.updateFileTable(1, alltables.size());
		 
		 showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.orphan_entries_deleted)); // "Success", "All Remote Entries Deleted");

	 }
    
 // always verify the host - dont check for certificate
    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                    return true;
            }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    public static void trustAllHosts() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[] {};
                    }

                    public void checkClientTrusted(X509Certificate[] chain,
                                    String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain,
                                    String authType) throws CertificateException {
                    }
            } };

            // Install the all-trusting trust manager
            try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection
                                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                    e.printStackTrace();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LIFECYCLE", "onPause");
        dbAccess.close();
        dbAccess = null;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("LIFECYCLE", "onResume");
        try{
        	if (dbAccess == null) {
        		dbAccess = new DBAccess(this);
        		dbAccess.open();
        	}
        
        	// When web launch is used if EpiCollect is exited and restarted the project doesn't show unless it is closed completely
        	// and restarted
        	// This ensures it shows on the spinner
        	// One time it crashed, hence the try/catch - just in case
        	String[] allprojects = dbAccess.getProjects();
    	
        	ArrayList<String> temparray = new ArrayList<String>();
        	for (int i = 0; i < allprojects.length; i++) {
        		temparray.add(allprojects[i]);
        	}
        	
        	ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temparray);
        	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	proj_spin.setAdapter(aspnLocs); 
    	
        	if(allprojects.length > 1){
        		try{
        			proj_spin.setSelection(1);
        		}
        		catch(IndexOutOfBoundsException e){}
        	}
        }
        catch(Exception e){}

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
    
    public static void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/uk.ac.imperial.epi_collect2/databases/epi_collect";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory()+"/database.sqlite";
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }
    
}