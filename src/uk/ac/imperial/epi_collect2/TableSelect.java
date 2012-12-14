package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
//import java.util.Vector;
//import java.util.List;
//import java.util.Vector;

//import uk.ac.imperial.epi_collect2.EntryNote.MyGestureDetector;
import uk.ac.imperial.epi_collect2.maps.LocalMap;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
//import android.text.InputType;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.Gravity;
import android.telephony.TelephonyManager;
import android.util.Log;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.CheckBox;
//import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
//import android.widget.ScrollView;
//import android.widget.TableLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class TableSelect extends Activity {
 
	//private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_FIND = 2;
	private static final int ACTIVITY_VIEW = 3;
	private static final int ACTIVITY_MAP = 4;
	private ArrayAdapter<String> aspnLocs; 
	//private Hashtable<String, EditText> textviewhash;
	private LinkedHashMap<String, Spinner> thisspinnerhash;
	//private LinkedHashMap<String, EditText> thisedittexthash;
	private Hashtable <String, ArrayList<String>>spinnershash = new Hashtable <String, ArrayList<String>>();
	private HashMap <String, String>spinnerselhash = new HashMap <String, String>();
	//private Hashtable<String, CheckBox> checkboxhash;
	//private Vector<String> doubles = new Vector<String>();
	//private Vector<String> integers = new Vector<String>();
	//private static String[] textviews = new String[0];
    //private static String[] spinners = new String[0];
    //private static String[] checkboxes = new String[0];
    //private static Vector<String> requiredfields, requiredspinners;
	//private int alldata = 0;
	private static final int RESET_ID = 1;
	private static final int HOME = 2;
	private static final int FIND = 3;
	private static final int GET_DATA = 4;
	//private static final int GET_LOCAL_DATA = 5;
	private static final int DEL_ENTRY = 5;
	private static final int DEL_ALL = 6;
	//private static final int DELETE_ENTRY = 5;
	private ViewFlipper f;
	//private TextView tv; //, pagetv;
	//private EditText et;
	private Spinner spin;
	//private CheckBox cb;
	private DBAccess dbAccess;
	//private boolean canupdate = true;
	//private Button confirmButton;

	//private String[] allviews = new String[0];
	int lastpage = 1;
	HashMap<Integer, String> orderedtables = new HashMap<Integer, String>();
	HashMap<String, Integer> orderedtablesrev = new HashMap<String, Integer>();
	//HashMap<String, Integer> spinnersetitem = new HashMap<String, Integer>();
	LinkedHashMap<String, String> keyshash;
	private int spinnercount = 0, flipperpos = 1, flipperlast = 1;
	private ViewFlipper flipper;
	private Hashtable<String, Integer>flipperhash = new Hashtable<String, Integer>();
	private Hashtable<String, TextView>titleviewhash = new Hashtable<String, TextView>();
	private List<String> items, items2;
	private Hashtable<String, String> key_titlehash = new Hashtable<String, String>();
	private Hashtable<String, String> onsavehash = new Hashtable<String, String>();
	private boolean spinnersupdated = false;
	private boolean spinnersset = false;
	private CheckBox remotecb;
	private String entry_selected_table="", sIMEI = "", email = "";
	private boolean hasextras = false;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        //super.setTitle(this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/app_name", null, null))); 
        
		dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    super.setTitle("EpiCollect+ "+dbAccess.getProject());
	    
	    //getValues();
	    setContentView(setLayout()); 
      
	    Bundle extras = getIntent().getExtras();
	    
	    setSpinners(extras);
	    
	    if(extras != null && extras.getString("nexttable") != null){
	    	int page = flipperhash.get(extras.getString("nexttable"));
	    				
        	flipperlast = 1;
        	while(flipperpos != page){
        		flipper.showNext();
      			flipperpos++;
      			if(flipperpos > items.size() + 1)
					flipperpos = 1;
        	}
	    }
	    
	    Account[] accounts = AccountManager.get(this).getAccounts();
    	for (Account account : accounts) {
    	  //String possibleEmail = account.name;
    	  if(account.type.equalsIgnoreCase("com.google") && account.name.contains("@gmail.com")){
    		  email = account.name;
    	  }
    	}
    	
    	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	 	sIMEI = mTelephonyMgr.getDeviceId();
	 	
	 	// If there is only 1 table skip past the table selection
	 	//Log.i("ITEMS", ""+items.size());
	 	if(items.size() < 2){
	 		
	 		flipper.showNext();
	 		flipperpos++;
	 		flipperlast = 2;
	 		//Log.i("POS", ""+flipperpos);
	 	}
	    
	}
	
	private void setSpinners(Bundle extras){
				   
		for(String key : thisspinnerhash.keySet()){
	    	spin = thisspinnerhash.get(key);
	    	spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 	    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
 	    	    	
 	    	    	if(spinnersset == true){
 	    	    		spinnersset = false;
 	    	    		return;
 	    	    	}
 	    	    		
 	    	    	//Log.i("SPINNER SEL CHECK 3", ""+pos);  	
 	    	    	if(pos > 0){
 	    	    		//String table = orderedtables.get(flipperpos-1);
 	    	    		//int tablepos = orderedtablesrev.get(orderedtables.get(flipperpos-1));
 	    	    		titleviewhash.get((String)parent.getTag()).setText((String)parent.getItemAtPosition(pos)); //dbAccess.getValue(table, "title", keyshash.get(orderedtables.get(tablepos)), (String)parent.getItemAtPosition(pos)));
 	    	    	}
 	    	    	else{
 	    	    		titleviewhash.get((String)parent.getTag()).setText("");
 	    	    	}
 	    	    	// This ensures that setNextSpinner is not called when the screen loads,
 	    	    	// otherwise all spinners are reset
 	    	    	// Once the last spinner that needs changing is reached, stop the count as
 	    	    	// the remaining spinners won't be selected
 	    	    	if(pos == 0)
 	    	    		spinnercount = thisspinnerhash.keySet().size();
 	    	    	
 	    	    	//Log.i("SPINNER COUNT 1", ""+spinnercount+" OF "+thisspinnerhash.keySet().size()+" POS "+pos);
 	    	    	
 	    	    	// COMMENTED OUT "RETURN" TO PREVENT PROBLEM REPORTED BY SUE ON 15/07/11 AT 20:58
 	    	    	if(spinnercount < thisspinnerhash.keySet().size()){ // && spinnercount < (thisspinnerhash.keySet().size()*2)){
 	    	    		spinnercount++;
 	    	    		//Log.i("SPINNER COUNT 2", ""+spinnercount+" OF "+thisspinnerhash.keySet().size());
 	    	    		//return;
 	    	    	}

 	    	    	// SPINSELHASH HERE
 	    	    	//String temp[] = (key_titlehash.get((String)parent.getItemAtPosition(pos))).split(",");
 	    	    	//for(int i = 0; i < temp.length; i+=2)
 	    	    	//	spinnerselhash.put(temp[i], temp[i+1]);
 	    	    	
 	    	    	//Log.i("SPINNER SEL CHECK 2", (String)parent.getTag()+" "+key_titlehash.get((String)parent.getItemAtPosition(pos)));
 	    	    	spinnerselhash.put((String)parent.getTag(), key_titlehash.get((String)parent.getTag()+"_"+(String)parent.getItemAtPosition(pos)));
 	    	    	//Log.i("SPINNER HASH PUT 1", ""+(String)parent.getTag()+" - "+key_titlehash.get((String)parent.getTag()+"_"+(String)parent.getItemAtPosition(pos)));
 	    	    	setNextSpinner((String)parent.getTag(), key_titlehash.get((String)parent.getTag()+"_"+(String)parent.getItemAtPosition(pos))); // (String)parent.getItemAtPosition(pos));
 	    	    	//if(pos > 0){
 	    	    	//	//String table = orderedtables.get(flipperpos-1);
 	    	    	//	//int tablepos = orderedtablesrev.get(orderedtables.get(flipperpos-1));
 	    	    	//	titleviewhash.get((String)parent.getTag()).setText((String)parent.getItemAtPosition(pos)); //dbAccess.getValue(table, "title", keyshash.get(orderedtables.get(tablepos)), (String)parent.getItemAtPosition(pos)));
 	    	    	//}
 	    	    	//else{
 	    	    	//	titleviewhash.get((String)parent.getTag()).setText("");
 	    	    	//}
 	    	    }
 	    	    public void onNothingSelected(AdapterView<?> parent) {
 	    	    }
 	    	}); 
	    }
		
		
        if (extras != null) {
        	if(thisspinnerhash != null){
        		for(String key : thisspinnerhash.keySet()){
        			
        			//int ch = 0;
        			for(int i = 0; i < thisspinnerhash.get(key).getCount(); i++){
        				//ch = i;
        				//Log.i("SPINNER SET CHECK "+i, key+" "+((String)thisspinnerhash.get(key).getItemAtPosition(i))+" IS "+ extras.getString(key));
        				if(((String)thisspinnerhash.get(key).getItemAtPosition(i)).equalsIgnoreCase(extras.getString(key))){
        					thisspinnerhash.get(key).setSelection(i);
        					hasextras = true;
        					break;
        				}
        			}
        			
        			//Log.i("SPINNER SET CHECK 2 "+ch, ((String)thisspinnerhash.get(key).getItemAtPosition(ch)) + " SET TO "+(String)thisspinnerhash.get(key).getSelectedItem().toString());
        			
        			onsavehash.put(key, extras.getString(key));
        			
        			setNextSpinner(key, key_titlehash.get(key+"_"+extras.getString(key))); //(String)thisspinnerhash.get(key).getItemAtPosition(extras.getInt(key)));
        			// SPINSELHASH HERE
        			//String temp[] = (extras.getString(key)).split(",");
 	    	    	//for(int i = 0; i < temp.length; i+=2)
 	    	    	//	spinnerselhash.put(temp[i], temp[i+1]);
        			spinnerselhash.put(key, key_titlehash.get(key+"_"+extras.getString(key))); //(String)thisspinnerhash.get(key).getItemAtPosition(extras.getInt(key)));
        			//Log.i("SPINNER HASH PUT 2", ""+key+" - "+key_titlehash.get(extras.getString(key)));
        			//Log.i("SPINNER SEL CHECK", key+" "+extras.getString(key));
        			
        			if(thisspinnerhash.get(key).getSelectedItem().toString().equalsIgnoreCase("Select"))
        				titleviewhash.get(key).setText(""); //dbAccess.getValue(table, "title", keyshash.get(orderedtables.get(tablepos)), (String)parent.getItemAtPosition(pos)));
        			else
        				titleviewhash.get(key).setText((String)thisspinnerhash.get(key).getSelectedItem().toString());
        			spinnersset = true;
        		}
       		
        	}
  
        if(extras.getString("selectedtable") != null && items.size() > 1){
        	int page = flipperhash.get(extras.getString("selectedtable"));
      	
        	//flipperlast = flipperpos;
        	while(flipperpos != page){
        		flipper.showNext();
      			flipperpos++;
      			if(flipperpos > items.size() + 1)
					flipperpos = 1;
        		}
        		//if(items.size() == 1){
        		//	flipper.showNext();
          		//	flipperpos++;
        		//}
        		//Log.i("FLIPPERPOS-2", ""+flipperpos+ " FLIPPERLAST-2 "+flipperlast);
        		flipperlast = flipperpos;
        	}
        }
	}
	
	//protected void onStart() {
	//	super.onStart();
	//for(String key : thisspinnerhash.keySet())
	//	spinnersetitem.put(key, 0);
	//}

	/*private void confirmData(){
		Bundle extras = getIntent().getExtras();
        
    	for(String key : textviews){
   			extras.putString(key, textviewhash.get(key).getText().toString());
    	}
    	               	
    	String result = "";
    	boolean store = true;
    	for(String key : textviews){
			if((textviewhash.get(key).getText().toString() == null) || (textviewhash.get(key).getText().toString().equalsIgnoreCase("")) && requiredfields.contains(key)){
				result += " " + key;
				store = false;
			}
		}
		
		for(String key : spinners){
			if(thisspinnerhash.get(key).getSelectedItemPosition() == 0 && requiredspinners.contains(key)){
				result += " " + key;
				store = false;
			}
		}
		
		if(!store && alldata == 0){
			showAlert("Entries required for: "+ result +". Confirm again to continue");
			alldata = 1;
			return;
		}
		
    	alldata = 0;
    	
        for(String key : spinners){
        	extras.putInt(key, thisspinnerhash.get(key).getSelectedItemPosition());
        }
        
        for(String key : checkboxes){
        	if(checkboxhash.get(key).isChecked()){
        		extras.putBoolean(key, true);
        	}
        	else{
        		extras.putBoolean(key, false);
        	}
        }

        extras.putInt("isstored", 0);
        getIntent().putExtras(extras);
        setResult(RESULT_OK, getIntent());
        finish();
  
	}*/
	
	// Use this version to update menus depending if images etc available
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	
    	menu.clear();
    	
    	if(flipperpos > 1 && items.size() > 1){
    		String table = orderedtables.get(flipperpos-1);
    		//Log.i(table, ""+thisspinnerhash.get(table).getSelectedItemPosition());
    		menu.clear();
    		menu.add(0, HOME, 0, R.string.menu_home);
    		
    		if(!dbAccess.getValue("epicollect_version").equalsIgnoreCase("1") &&
    				dbAccess.getValue(table, "search") != null &&
    				dbAccess.getValue(table, "search").length() > 0){
    			menu.add(0, FIND, 0, R.string.menu_find);
    		}
    		if(flipperpos-1 < orderedtables.size())
    			menu.add(0, GET_DATA, 0, R.string.menu_load_remote);
    		//if(!dbAccess.getValue("local_remote_xml").equalsIgnoreCase("None"))
    		//	menu.add(0, GET_LOCAL_DATA, 0, R.string.menu_get_local_data);
    		if(thisspinnerhash.get(table).getSelectedItemPosition() > 0)
    			menu.add(0, DEL_ENTRY, 0, R.string.menu_del_entry);
    		menu.add(0, DEL_ALL, 0, R.string.menu_del_all);
    	}
    	else
    		menu.add(0, HOME, 0, R.string.menu_home);
    	
    	return super.onPrepareOptionsMenu(menu);
    	
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    //menu.add(R.string.menu_reset);
	    //menu.add(R.string.menu_home);
	    //return super.onCreateOptionsMenu(menu);
	    //menu.add(0, RESET_ID, 0, R.string.menu_reset);
	    menu.add(0, HOME, 0, R.string.menu_home);
	    menu.add(0, GET_DATA, 0, R.string.menu_get_data);
	    if(!dbAccess.getValue("local_remote_xml").equalsIgnoreCase("None"))
	    	menu.add(0, GET_LOCAL_DATA, 0, R.string.menu_get_local_data);
	    if(thisspinnerhash.get(table).getSelectedItemPosition() > 0){
	    	menu.add(0, DEL_ENTRY, 0, R.string.menu_del_entry);
	    menu.add(0, DEL_ALL, 0, R.string.menu_del_all);
	    return true;
	} */
	
	
	
	/*@Override
    protected void onStop() {
        super.onStop();
        dbAccess.close();
        dbAccess = null;
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        if (dbAccess == null) {
        	dbAccess = new DBAccess(this);
        	dbAccess.open();
        }
        getValues();
	    //setContentView(setLayout()); 
    }*/
    
    @Override
    protected void onPause() {
        super.onPause();
        //Log.i("CHECK PAUSE", "IN");
        dbAccess.close();
        dbAccess = null;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        //Log.i("CHECK RESUME", "IN");
        if (dbAccess == null) {
        	dbAccess = new DBAccess(this);
        	dbAccess.open();
        }
        getValues();
	    //setContentView(setLayout()); 
    } 
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
	    	
    		//Log.i("FLIPPERPOS", ""+flipperpos+ " FLIPPERLAST "+flipperlast);
    		if(flipperpos == 1 || (items.size() == 1 && flipperpos == 2))    		
    			goHome();
    		else{
    			while(flipperpos != flipperlast){
    				flipper.showNext();
    				flipperpos++;
    				if(flipperpos > items.size() + 1)
    					flipperpos = 1;
    				}
    		flipperlast--;
    		if(flipperlast == 1 && items.size() == 1)
    			flipperlast = 2;
    		}
    			
    			
    	}
    	// Menu button doesn't work by default on this view for some reason
    	if(keyCode == KeyEvent.KEYCODE_MENU)
    		openOptionsMenu();

        return true;
    } 
    
   /* private void confirmBack(KeyEvent event){
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle("Warning");
        alertDialog.setMessage("Save current data?");
        alertDialog.setButton("Yes", new DialogInterface.OnClickListener(){
             public void onClick(DialogInterface dialog, int whichButton) {
            	alldata = 1;
            	confirmData();
             }
        });
        alertDialog.setButton2("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
            	Bundle extras = getIntent().getExtras();
            	getIntent().putExtras(extras);
                setResult(RESULT_OK, getIntent());
                finish();
            }
       });
        alertDialog.show();	
	} */
    
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	super.onSaveInstanceState(outState);      	
 		
		 //for(String key : thisspinnerhash.keySet()){
		//	 outState.putInt(key, thisspinnerhash.get(key).getSelectedItemPosition());
		//	}
    	
    	for(String key : onsavehash.keySet())
    		outState.putString(key, onsavehash.get(key));
    }  
    
    @Override  
    protected void onRestoreInstanceState(Bundle outState) {  
    	super.onRestoreInstanceState(outState);      	
 		
    	
    }     
    
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    super.onMenuItemSelected(featureId, item);
	    switch(item.getItemId()) {
	    case RESET_ID:
	    	resetData();
	        break;
	    case HOME:
			//confirmHome();
	    	goHome();
			break;
	    case FIND:
	    	findData();
			break;
	    case GET_DATA:
	    	loadRemoteData(false, false);
	    	//getRemoteDataFile(false, false);
			break;
	    //case GET_LOCAL_DATA:
	    //	getRemoteDataFile(true, false);
		//	break;
	    case DEL_ENTRY:
    		showDeleteEntryAlert();
	    	break;
	    case DEL_ALL:
    		showDeleteAllEntriesAlert();
			break;
	    }
	        
	    return true;
	}
    
    @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	super.onActivityResult(requestCode, resultCode, data);
	        	   	
	   	Bundle extras = null;
	   	if(data != null)
	   		extras = data.getExtras();
	    switch(requestCode) {
	    case ACTIVITY_FIND:
	    	if(extras != null){
	    		if (dbAccess == null) {
	            	dbAccess = new DBAccess(this);
	            	dbAccess.open();
	            }
	    		spinnercount = 0;
	    		setSpinners(extras);
	    	} 
	        break; 
	 	case ACTIVITY_VIEW:
	    	if(extras != null){
	    		if (dbAccess == null) {
	            	dbAccess = new DBAccess(this);
	            	dbAccess.open();
	            }
	    		spinnercount = 0;
	    		updateSpinners();
	    		setSpinners(extras);
	    	} 
	        break;
	 	
	 	/*case ACTIVITY_EDIT:
	    	if(extras != null){
	    		if (dbAccess == null) {
	            	dbAccess = new DBAccess(this);
	            	dbAccess.open();
	            }
	    		spinnercount = 0;
	    		updateSpinners();
	    		//setSpinners(extras);
	    	} 
	        break; */
	    }
	    
	} 
    
    private void resetData(){
    	/*for(String key : textviews){
        	textviewhash.get(key).setText("");
        }
        
        for(String key : spinners){
        	thisspinnerhash.get(key).setSelection(0);
        }
        
        for(String key : checkboxes){
        	checkboxhash.get(key).setChecked(false);
        }*/
    }

    
    private void getValues(){
    	
    	// Rest values as otherwise odd values seem to get left behind:
    	// Project - data - back -back - new project - data -> causes crash
    	// If first project has spinner and second doesn't then second "spinners"
    	// still contains
    	//textviews = new String[0];
        //spinners = new String[0];
        //checkboxes = new String[0];
        
    	//if(dbAccess.getValue("textviews") != null)
		//	textviews = (dbAccess.getValue("textviews")).split(",,"); // "CNTD", 
    	//if(dbAccess.getValue("spinners") != null)
    	//	spinners = (dbAccess.getValue("spinners")).split(",,");
    	//if(dbAccess.getValue("checkboxes") != null)
    	//	checkboxes = (dbAccess.getValue("checkboxes")).split(",,");
    	
    	//for(String t : textviews)
    	//	//Log.i(getClass().getSimpleName(), "TEXT VIEW "+t);
    	
    	//for(String key : (dbAccess.getValue("doubles")).split(",,")){
        //	doubles.addElement(key);
        //}
        
        //for(String key : (dbAccess.getValue("integers")).split(",,")){
        //	integers.addElement(key);
        //}
                
        //List<String> list = Arrays.asList((dbAccess.getValue("requiredtext")).split(",,"));
        //requiredfields = new Vector<String>(list);
        
        //list = Arrays.asList((dbAccess.getValue("requiredspinners")).split(",,"));
        //requiredspinners = new Vector<String>(list); 
                        
    }
    
    
    
    private RelativeLayout setLayout(){
    
    	keyshash = dbAccess.getKeys();
    	// SPINSELHASH HERE
    	spinnerselhash.clear();
    	
        thisspinnerhash = new LinkedHashMap<String, Spinner>();
        //thisedittexthash = new LinkedHashMap<String, EditText>();
    		    
	    RelativeLayout ll = new RelativeLayout(this);
	    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
		
	    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ); 
	       
	    //ScrollView s; // = new ScrollView(this);
	    	        
	    //TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    //TableLayout l; //=new TableLayout(this);
	    
	    //s.addView(l); 
	    
	    //String[] tempstring;
	    //Button addButton, viewButton, findButton, addNextButton, listButton;

	    RelativeLayout rl2;
	    RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    rlp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    rlp3.addRule(RelativeLayout.CENTER_HORIZONTAL); // ALIGN_PARENT_LEFT
	    
	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    //rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
	    
	    rlp5.addRule(RelativeLayout.CENTER_HORIZONTAL); // ALIGN_PARENT_RIGHT
	    
	    flipper = new ViewFlipper(this);   
	   // gestureDetector = new GestureDetector(new MyGestureDetector());
	    
	    ll.addView(flipper, linear1layout2); // s
	    
	    // First add the main menu
	    
	    //s = new ScrollView(this);
	        
	    
	    LinearLayout rlv = new LinearLayout(this);
	    rlv.setOrientation(1);
	    //rlv.setGravity(Gravity.CENTER_HORIZONTAL);

	    flipper.addView(rlv);
	    
	    TextView tv = new TextView(this);
	    tv.setTextSize(24);
	    tv.setGravity(Gravity.CENTER_HORIZONTAL);
	    tv.setText("Select Form");
	    rlv.addView(tv);

		
	    View ruler = new View(this); 
	    rlv.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
	    ruler = new View(this); 
	    ruler.setBackgroundColor(0xFF00FF00); 
	    rlv.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
	    ruler = new View(this); 
	    rlv.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
	    
	    ListView lv = new ListView(this); 
	    
	    items = new ArrayList<String>();
	    
	    for(String key : keyshash.keySet()){
	    	//if(dbAccess.getIsMaintable(key).equalsIgnoreCase("1"))
	    	items.add(key);
	    }
	    
	    ListAdapter notes = new ArrayAdapter<String>(this, R.layout.records_row, items);
	    lv.setAdapter(notes);
	    
	    lv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	
	        	// If the user has pressed the back button on an EntryNote page the spinners
	        	// need to be left set so they can return to that table and selection.
	        	// Once a new table selection is made hasextras is reset
	        	if(hasextras == false){
	        		thisspinnerhash.get(items.get(0)).setSelection(0);
    			
	        		setNextSpinner(items.get(0), "Select");
	        		spinnerselhash.put(items.get(0), "Select"); 
	        	}
	        	hasextras = false;
	        	int page = flipperhash.get(items.get(position));
	      	
	        	//Log.i("LIST CLICK CHECK", items.get(position)+" "+page);
	      	
	        	flipperlast = flipperpos;
	        	while(flipperpos != page){
	        		flipper.showNext();
	      			flipperpos++;
	      			if(flipperpos > items.size() + 1)
    					flipperpos = 1;
	        	}
	        	//updateSpinners();
	        }
	      });

	    //RelativeLayout.LayoutParams rlplv = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    //rlplv.addRule(RelativeLayout.BELOW, tv.getId());
	    rlv.addView(lv); //, rlplv);	    
	    
	    //s.addView(rlv);
	    
	    //final boolean addingspinners = true;
	    int count = 1;
	    
	    for(String key : keyshash.keySet()){
	    	//Log.i("ORDERED TABLE", key+" "+count);	    	   	
	    	orderedtables.put(count, key);
	    	orderedtablesrev.put(key, count);
	    	count++;
	    } 
	    
	    LinkedHashMap<String, String> tempkthash;
	    count = 1;
	    for(String key : keyshash.keySet()){
	    	count++;
	       			
	    	flipperhash.put(key, count);
	    	
	    	//orderedtables.put(count-1, key);
	    	//orderedtablesrev.put(key, count-1);
	    	
		    //s = new ScrollView(this);
		    
		    //flipper.addView(s); 
		    
		    LinearLayout rlv2 = new LinearLayout(this);
		    rlv2.setOrientation(1);
		    
		    //l=new TableLayout(this);
		    
		    flipper.addView(rlv2); 
		    	    	
	    	//Log.i("TABLE SELECT KEY", key + " "+count); // + " "+keyshash.get(key)+" DB = "+dbAccess.getSpinnerValues(key, keyshash.get(key)));
	    	
    		tv = new TextView(this);
        	tv.setText(key);
        	tv.setTextSize(24);
        	tv.setGravity(Gravity.CENTER_HORIZONTAL);
        	//tv.setWidth(100);
        	rlv2.addView(tv); //, lp);
    		
        	//ruler = new View(this); 
     	    //rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
     	    ruler = new View(this); 
     	    ruler.setBackgroundColor(0xFF00FF00); 
     	    rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
     	    //ruler = new View(this); 
     	    //rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
    		
     	    
     	    // Only add the spinner if there is more than one table
     	    if(items.size() > 1){
     	    	spin = new Spinner(this);
     	    	rlv2.addView(spin); //, lp);
     	    	thisspinnerhash.put(key, spin);
    		
     	    	//tempstring = (dbAccess.getSpinnerValues(key), keyshash.get(key)).split(",,");
     	    	//tempstring = (dbAccess.getSpinnerValues(key)).split(",,"); 
     	    	if(spinnershash.get(key) == null){
     	    		spinnershash.put(key, new ArrayList<String>());
     	    	}
    		
     	    	 		
     	    	key_titlehash.put(key+"_"+"Select", "Select");
     	    	spinnershash.get(key).add("Select");
     	    	
     	    	// Only need to add the spinner values for the first spinner
     	    	if(count == 2){
     	    		tempkthash = dbAccess.getSpinnerValues(key);
     	    		for(String skey : tempkthash.keySet()){
     	    			spinnershash.get(key).add(tempkthash.get(skey));
     	    			//if(!tempkthash.get(skey).equalsIgnoreCase("Select")){
     	    			key_titlehash.put(key+"_"+tempkthash.get(skey), skey);
     	    			//}
     	    			//else{
     	    			//	key_titlehash.put(key+"_"+"Select", "Select");
     	    			//}
     	    		}
     	    	}
     	    	/*String title;
 	    		for (int i = 0; i < tempstring.length; i++) { 
 	    			if(!tempstring[i].equalsIgnoreCase("Select")){
 	    				//title = dbAccess.getValue(key, "title", keyshash.get(key), tempstring[i]);
 	    				title = dbAccess.getValue(key, "title", tempstring[i]);
 	    				key_titlehash.put(key+"_"+title, tempstring[i]);
 	    			}
 	    			else{
 	    				title = "Select";
 	    				key_titlehash.put(key+"_"+"Select", "Select");
 	    			}
 	    			spinnershash.get(key).add(title); //tempstring[i]);
 	    		}*/
 	    	
     	    	// SPINSELHASH HERE
     	    	spinnerselhash.put(key, "Select");
     	    	spin.setTag(key);
 	    	
     	    	// Add padding
     	    	//View v2 = new View(this);
     	    	//v2.setMinimumHeight(10);
     	    	//rlv2.addView(v2);
 	    	
     	    	TextView tvtitle = new TextView(this);
     	    	rlv2.addView(tvtitle);
     	    	tvtitle.setTextSize(20);
     	    	titleviewhash.put(key, tvtitle);
 	    	
     	    	ruler = new View(this); 
     	    	rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
     	    	ruler = new View(this); 
     	    	ruler.setBackgroundColor(0xFF00FF00); 
     	    	rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
     	    	ruler = new View(this); 
     	    	//ruler = new View(this); 
     	    	//rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2)); 
     	    }
     	    else{
     	    	// Add TextView as some padding
     	    	TextView tvtitle = new TextView(this);
     	    	rlv2.addView(tvtitle);
     	    	tvtitle.setTextSize(20);
     	    }
     	    
 	    	rl2 = new RelativeLayout(this);
 	    	RelativeLayout.LayoutParams rlp6 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
 	    	
 	    	ListView lv2 = new ListView(this); 
 	    	final String edit_view = this.getResources().getString(R.string.edit_view),
 		    		list = this.getResources().getString(R.string.list),
 		    		add_set = this.getResources().getString(R.string.add_set),
 		    		change =this.getResources().getString(R.string.change),
 		    		view_map = this.getResources().getString(R.string.view_map);
 	    	
 		    items2 = new ArrayList<String>();
 		    
 		    items2.add(this.getResources().getString(R.string.add)+" "+key); // Add
 		    if(items.size() > 1)
 		    	items2.add(edit_view+" "+key); //Edit/View
 		   	//items2.add("Find "+key);
 		   	items2.add(list+" "+key); // List
 		   	if(orderedtables.get(count) != null) // && dbAccess.getIsMaintable(orderedtables.get(count)).equalsIgnoreCase("1"))
 		  		items2.add(add_set+" "+orderedtables.get(count) +" to "+key); // Add/Set
 		    if(count != 2)
		   		items2.add(change+" "+orderedtables.get(count-2)); // Change
 		    // ADD THIS BACK TO ADD MAP
 		    if(dbAccess.getValue(key, "gps") != null && dbAccess.getValue(key, "gps").length() > 0)
 		    	items2.add(view_map); //"View Map");
 		   	
 		   //	lv2.setTag(key);
 		     		    
 		    ListAdapter notes2 = new ArrayAdapter<String>(this, R.layout.records_row, items2);
 		    lv2.setAdapter(notes2);
 		    
 		    
 		    		
 		    lv2.setOnItemClickListener(new OnItemClickListener() {
 		        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 		        	
 		        	if(spinnersupdated == true){
 		        		String label = thisspinnerhash.get(table).getSelectedItem().toString();
 		        		dbAccess.updateFileTable(flipperpos-1, orderedtables.size());
 		        		//updateSpinners();
 		        		spinnersupdated = false;
 		        		setNextSpinner(table, key_titlehash.get(table+"_"+label));
 		        		// Reset the spinner
 		        		//for(int i = 0; i < thisspinnerhash.get(table).getCount(); i++){
 		        		//	if(((String)thisspinnerhash.get(table).getItemAtPosition(i)).equalsIgnoreCase(label)){
		        		//		thisspinnerhash.get(table).setSelection(i);
		        		//		break;
		        		//		}
		        		//	}
 		        		spinnerselhash.put(table, key_titlehash.get(table+"_"+label));
 		        		}
 		        	if(position == 0)
 		        		addData();
 		        	else if(position == 1){
 		        		if(parent.getItemAtPosition(position).toString().startsWith(edit_view)) //"Edit/View"))
 		        			viewData();
 		        		else 
 		        			listTable();
 		        			//findData();
 		        	}
 		        	//else if(position == 2)
 		        	//	if(parent.getItemAtPosition(position).toString().startsWith("Find"))
		        	//		findData();
		        	//	else 
		        	//		listTable();
 		        	else if(position == 2)
 		        		if(parent.getItemAtPosition(position).toString().startsWith(list)) //"List"))
 		        			listTable();
		        		else 
		        			loadMap();
 		        	else if(position == 3){
 		        		if(parent.getItemAtPosition(position).toString().startsWith(add_set)) //"Add/Set"))
 		        			addNext();
 		        		else if(parent.getItemAtPosition(position).toString().startsWith(change)) //"Change"))
 		        			changePreviousTable();
 		        		else if(parent.getItemAtPosition(position).toString().startsWith(view_map)) //"View Map"))
		        			loadMap();
 		        	}
 		        	else if(position == 4){
 		        		if(parent.getItemAtPosition(position).toString().startsWith(change)) //"Change"))
 		        			changePreviousTable();
		        		else if(parent.getItemAtPosition(position).toString().startsWith(view_map)) //"View Map"))
		        			loadMap();
 		        	}
 		        	else if(position == 5)
 		        		loadMap();
 		        }
 		      });
 	    	
 		    
 	    	rl2.addView(lv2, rlp6);
 		   rlv2.addView(rl2);
 	    	
 		/*   
 		  	addButton = new Button(this);
 	    	addButton.setText("Add "+key);
 	    	addButton.setTag(key);
 	    	
 		  	addButton.setOnClickListener(new View.OnClickListener() {
 		    	public void onClick(View view) {
 		    	 	
 		    		
 		    		addData((String)view.getTag());
 		    	 	
 		    	    }
 		    	});
 		   
 		  l.addView(addButton); //, rlp2); // rl2 rlp
 	    	
 		  viewButton = new Button(this);
 		  viewButton.setText("Edit "+key);
 		  viewButton.setTag(key);
		    		    
 		  viewButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    	 	viewData((String)view.getTag());
		    	    }
		    	});
 		  
 		 
 		 //rlp2.addRule(RelativeLayout.BELOW, addButton.getId());
 		 l.addView(viewButton); //, rlp3);  // rl2
 		 
 		 findButton = new Button(this);
 		 findButton.setText("Find "+key);
 		 findButton.setTag(key);
		    		    
 		 findButton.setOnClickListener(new View.OnClickListener() {
		   	public void onClick(View view) {
		   	 	findData((String)view.getTag());
		   	    }
		   	});
 		 
 		l.addView(findButton); //, rlp4); // rl2 rlp5
 		
 		listButton = new Button(this);
 		listButton.setText("List "+key);
 		listButton.setTag(key);
		    		    
 		listButton.setOnClickListener(new View.OnClickListener() {
		   	public void onClick(View view) {
		   	 	listTable();
		   	    }
		   	});
		 
		l.addView(listButton); //, rlp4); // rl2 rlp5 
		*/
		 
  		//rlp2.addRule(RelativeLayout.BELOW, viewButton.getId());

 		// To add padding 
 	    /*View v = new View(this);
		View v = new View(this);
 		v.setMinimumHeight(10);
 		rlv2.addView(v);
 		   
 		  v = new View(this);
 	 		v.setMinimumHeight(2);
 	 		v.setBackgroundColor(Color.WHITE);
 	 		// Doesn't seem to work!
 	 		//v.setPadding(0, 20, 0, 20);
 	 		rlv2.addView(v);
 	 		
 	 		// To add padding 
 	 	    v = new View(this);
 	 		v.setMinimumHeight(25);
 	 		rlv2.addView(v);*/
 		   
 		/*ruler = new View(this); 
  	    rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 		
 		ruler = new View(this); 
  	    ruler.setBackgroundColor(0xFFFFFFFF); 
  	    rlv2.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
 	    
 		
  				 
		 addNextButton = new Button(this);
		 addNextButton.setText("Add "+orderedtables.get(count));
		 addNextButton.setTag(key);
		    	
		 addNextButton.setOnClickListener(new View.OnClickListener() {
		   	public void onClick(View view) {
		   	 	//findData((String)view.getTag());
		   	    }
		   	});
 		 
		 //rlp2.addRule(RelativeLayout.BELOW, findButton.getId());

		 rlv2.addView(addNextButton); //, rlp5);
		 
		// l.addView(rl2); */
 		 
    	}
	    
	    for(String key : thisspinnerhash.keySet()){
	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(key).setAdapter(aspnLocs);
	    
	    }
	     
	    
	
	    return ll;
	    
	 }

    public void updateSpinners(){
    	
    	//String [] tempstring;
    	//String title;
    
    	for(String key : thisspinnerhash.keySet()){
    		//tempstring = (dbAccess.getSpinnerValues(key, keyshash.get(key))).split(",,");
    		spinnershash.remove(key);
    		spinnershash.put(key, new ArrayList<String>());
    		
    		LinkedHashMap<String, String> tempkthash = dbAccess.getSpinnerValues(key);
    		
    		key_titlehash.put(key+"_"+"Select", "Select");
    		spinnershash.get(key).add("Select");
    		for(String skey : tempkthash.keySet()){
    			spinnershash.get(key).add(tempkthash.get(skey));
    			//if(!tempkthash.get(skey).equalsIgnoreCase("Select")){
 	    			key_titlehash.put(key+"_"+tempkthash.get(skey), skey);
 	    		//}
 	    		//else{
 	    		//	key_titlehash.put(key+"_"+"Select", "Select");
 	    		//}
    		}
    		
    		/*tempstring = (dbAccess.getSpinnerValues(key)).split(",,");
    		for (int i = 0; i < tempstring.length; i++) { 
	    		if(!tempstring[i].equalsIgnoreCase("Select")){
	    			//title = dbAccess.getValue(key, "title", keyshash.get(key), tempstring[i]);
	    			title = dbAccess.getValue(key, "title", tempstring[i]);
	    			key_titlehash.put(key+"_"+title, tempstring[i]);
	    		}
	    		else{
	    			title = "Select";
	    			key_titlehash.put(key+"_"+"Select", "Select");
	    		}
	    		spinnershash.get(key).add(title); //tempstring[i]);
	    	}*/
	    	
	    	// SPINSELHASH HERE
	    	spinnerselhash.put(key, "Select");
	    	
	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(key).setAdapter(aspnLocs);
    	}
	    	
	    	
    }

// REMOVE THIS LATER - ONLY FOR TESTING
  /*  
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
                return true;
        else
                return false;
    }

    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    GestureDetector gestureDetector;
    
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

   /* class MyGestureDetector extends SimpleOnGestureListener {

        // from:
        // http://www.codeshogun.com/blog/2009/04/16/how-to-implement-swipe-action-in-android/

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                		//int jumpnum;
                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                // right to left swipe
                        		flipper.setInAnimation(animateInFrom(RIGHT));
                        		flipper.setOutAnimation(animateOutTo(LEFT));
                        		flipper.showNext();
                         
                        	
                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                // left to right swipe         	
                        		flipper.setInAnimation(animateInFrom(LEFT));
                                flipper.setOutAnimation(animateOutTo(RIGHT));
          	                    flipper.showPrevious();
                            
                        }
                } catch (Exception e) {}
                return false;
        }
    } */
    
    private void addData(){
    	
    	String table = orderedtables.get(flipperpos-1);
    	
		//Log.i(getClass().getSimpleName(), "Add TAG "+table);
		
    	//setSpinners(null);
    	
		int tablepos = orderedtablesrev.get(table) - 1;
		
		// SPINSELHASH HERE
		if(tablepos > 0 && spinnerselhash.get(orderedtables.get(tablepos)).equalsIgnoreCase("Select")){
		//if(tablepos > 0 && ((String)thisspinnerhash.get(orderedtables.get(tablepos)).getSelectedItem()).equalsIgnoreCase("Select")){
			showAlert(this.getResources().getString(R.string.error), orderedtables.get(tablepos)+" "+this.getResources().getString(R.string.selected_first)); //must be selected first");
			flipper.showPrevious();
			flipperpos--;
			return;
		}
		 
		Intent i = new Intent(this, EntryNote.class);
		
		i.putExtra("table", table);
		
		String temp[];
		if(tablepos > 0){
			for(int pos = 1; pos <= tablepos; pos++){
				// SPINSELHASH HERE
				//key_titlehash
				//Log.i("SPINNERSELHASH ADD "+table, orderedtables.get(pos)+" "+spinnerselhash.get(orderedtables.get(pos)));
				temp  = (spinnerselhash.get(orderedtables.get(pos))).split(",");
				//Log.i("SPINNERSELHASH 2 "+table, orderedtables.get(pos) +" "+spinnerselhash.get(orderedtables.get(pos)));
	    		for(int j = 0; j < temp.length; j+=2)
	    			i.putExtra(temp[j], temp[j+1]);
				//i.putExtra(keyshash.get(orderedtables.get(pos)), spinnerselhash.get(orderedtables.get(pos)));
				//i.putExtra(keyshash.get(orderedtables.get(pos)), ((String)thisspinnerhash.get(orderedtables.get(pos)).getSelectedItem()));
			}
		}

		i.putExtra("new", 1);
		// Not sure this is needed
		i.putExtra("frombranch", 0);
		
		i.putExtra("branch", 0);
		
		for(String key : thisspinnerhash.keySet()){
			i.putExtra(key, (String)thisspinnerhash.get(key).getSelectedItem()); // .getSelectedItemPosition());
			}
		
		if(tablepos > 0){
			i.putExtra("select_table_key_column", keyshash.get(orderedtables.get(tablepos)));
			// SPINSELHASH HERE
			i.putExtra("foreign_key", spinnerselhash.get(orderedtables.get(tablepos)));
			//i.putExtra("foreign_key", ((String)thisspinnerhash.get(orderedtables.get(tablepos)).getSelectedItem()));
			i.putExtra("select_table", orderedtables.get(tablepos));
		}
		else{
			// This is the first table
			i.putExtra("select_table_key_column", "Null");
			i.putExtra("foreign_key", "Null");
			i.putExtra("select_table", "Null");
		}
		
		startActivity(i);
	    //startActivityForResult(i, ACTIVITY_EDIT);
	 }

	 
	 private void setNextSpinner(String table, String value){
		 
		 //thisedittexthash.get(table).setText(value);
		 
		 //Log.i("SPIN CHECK 1", table+" "+value);
		 
		 //if(value == null)
 		//	value = "Select";
	 
		 String nexttable = orderedtables.get(orderedtablesrev.get(table) + 1);
		 // SPINSELHASH HERE
		 if(spinnerselhash.get(nexttable) == null){
		 //if(((String)thisspinnerhash.get(nexttable).getSelectedItem()) == null){
		
				return;
			}
			
			//String tableid = keyshash.get(orderedtables.get(orderedtablesrev.get(table)));
			
			//Log.i("SPIN CHECK 2", nexttable);
			
			spinnershash.put(nexttable, new ArrayList<String>());
			
			//Log.i("SET NEXT SPINNER CHECK", "nexttable = "+nexttable+" tableid = "+tableid+" value = "+value+ " hash check = "+keyshash.get(nexttable));
			//if (dbAccess == null) {
			//	//Log.i("DBACCESS CHECK", "IS NULL");
			//}
			//else {
			//	//Log.i("DBACCESS CHECK", "NOT NULL");
			//}
			//String[] tempstring = {"Select"};
			
			//key_titlehash.put(nexttable+"_"+"Select", "Select");
    		//spinnershash.get(nexttable).add("Select");
			
			key_titlehash.put(nexttable+"_"+"Select", "Select");
    		spinnershash.get(nexttable).add("Select");
    		
			if(!value.equalsIgnoreCase("Select")){
				//tempstring = (dbAccess.getNextSpinnerValues(nexttable, keyshash.get(nexttable), tableid, value).split(",,"));
				//Log.i("SPIN CHECK 4", nexttable+" "+value);
				//tempstring = (dbAccess.getNextSpinnerValues(nexttable, key_titlehash.get(value)).split(",,"));
				//tempstring = (dbAccess.getNextSpinnerValues(nexttable, value).split(",,"));
				
 	    		
				//}
    		
				LinkedHashMap<String, String> tempkthash = dbAccess.getNextSpinnerValues(nexttable, value);
    		
				//key_titlehash.put(nexttable+"_"+"Select", "Select");
				//spinnershash.get(nexttable).add("Select");
				for(String skey : tempkthash.keySet()){
					spinnershash.get(nexttable).add(tempkthash.get(skey));
					//if(!tempkthash.get(skey).equalsIgnoreCase("Select")){
					key_titlehash.put(nexttable+"_"+tempkthash.get(skey), skey);
					//}
					//else{
					//	key_titlehash.put(nexttable+"_"+"Select", "Select");
					//}
				}
			}
			
			/*String title;
			for (int i = 0; i < tempstring.length; i++) {  
				//Log.i("SPIN CHECK 3", nexttable+" "+tempstring[i]);
				if(!tempstring[i].equalsIgnoreCase("Select")){
 	    			//title = dbAccess.getValue(table, "title", keyshash.get(table), tempstring[i]);
 	    			title = dbAccess.getValue(nexttable, "title", tempstring[i]);
 	    			key_titlehash.put(nexttable+"_"+title, tempstring[i]);
 	    		}
 	    		else{
 	    			title = "Select";
 	    			key_titlehash.put(nexttable+"_"+"Select", "Select");
 	    		}
				//title = dbAccess.getValue(table, "title", keyshash.get(table), tempstring[i]);
	 	    	//key_titlehash.put(title, tempstring[i]);
				spinnershash.get(nexttable).add(title);
			}*/
			//}
 	    	
 	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(nexttable));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(nexttable).setAdapter(aspnLocs); 
	    	
	    	thisspinnerhash.get(nexttable).setSelection(0);
	    	// SPINSELHASH HERE
	    	spinnerselhash.put(nexttable, "Select");
	    	
	    	// Reset the remaining spinners 
	    	setNextSpinner(nexttable, "Select");

		 }
	 
	 	private void viewData(){
	 		
	 		String table = orderedtables.get(flipperpos-1);
				 
			//Intent i = new Intent(this, EntryNote.class);
	 		Intent i = new Intent(this, DetailsView.class);
	 		
			i.putExtra("table", table);
			i.putExtra("keytable", "");
			i.putExtra("keyvalue", "");
			i.putExtra("new", 0);
			i.putExtra("frombranch", 0);
			
			int pos = thisspinnerhash.get(table).getSelectedItemPosition();
			if(pos == 0){
				showAlert(this.getResources().getString(R.string.error), table+" "+this.getResources().getString(R.string.selected_first)); //must be selected first");
				return;
			}
				
			i.putExtra("primary_key", key_titlehash.get(table+"_"+(String)thisspinnerhash.get(table).getItemAtPosition(pos)));
			
			for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, (String)thisspinnerhash.get(key).getSelectedItem()); // .getSelectedItemPosition());
				}
			
			//for(String key : thisspinnerhash.keySet()){
			//	i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
			//	}
			dbAccess.close();
		    startActivityForResult(i, ACTIVITY_VIEW);
		 }

	 
	 private void findData(){
		 
		 String table = orderedtables.get(flipperpos-1);
		 
		 if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("1")){
			 showAlert(this.getResources().getString(R.string.warning), this.getResources().getString(R.string.no_search_fields)); //"Warning", "No search fields are defined for this project");
			 return;
		 }
		 
		try{
		 	if(dbAccess.getValue(table, "search") == null && dbAccess.getValue(table, "search").length() == 0){
		 		showAlert(this.getResources().getString(R.string.warning), this.getResources().getString(R.string.no_search_fields)); //"Warning", "No search fields are defined for this project");
		 		return;
		 	}
	 	}
		catch(NullPointerException npe){
			showAlert(this.getResources().getString(R.string.warning), this.getResources().getString(R.string.no_search_fields)); //"Warning", "No search fields are defined for this project");
	 		return;
		}
	 
		 
		 //Log.i(getClass().getSimpleName(), "Find TAG "+table);
		 
		 //Bundle extras = getIntent().getExtras();
		 
		 Intent i = new Intent(this, FindRecord.class);
	 		
		 i.putExtra("table", table);
		 for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, (String)thisspinnerhash.get(key).getSelectedItem()); // .getSelectedItemPosition());
				}
		 
		 //for(String key : thisspinnerhash.keySet()){
		//	i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
		//	}
			
		 startActivityForResult(i, ACTIVITY_FIND);
	 }
	 
	 private void addNext(){
		String table = orderedtables.get(flipperpos);
		
		// There is no next table so the last item on the list
		// was selected, which is to reset the previous table
		//if(orderedtables.get(flipperpos) == null){
		//	changePreviousTable();
		//	return;
		//}

	    	   	
		int tablepos = orderedtablesrev.get(table)-1;
						 
		// SPINSELHASH HERE
		if(tablepos > 0 && spinnerselhash.get(orderedtables.get(tablepos)).equalsIgnoreCase("Select")){
		//if(tablepos > 0 && ((String)thisspinnerhash.get(orderedtables.get(tablepos)).getSelectedItem()).equalsIgnoreCase("Select")){
			showAlert("Error", orderedtables.get(tablepos)+" must be selected first");
			return;
		}
		 flipper.showNext();
		 flipperlast = flipperpos;
		 flipperpos++;
	 }
	 
	 private void changePreviousTable(){
	 
		 flipper.showPrevious();
		 flipperlast = flipperpos;
		 flipperpos--;
	 }
	 
	 private void loadMap(){
		 String table = orderedtables.get(flipperpos-1);
		 
		 //int pos = thisspinnerhash.get(table).getSelectedItemPosition();
		 //if(pos == 0){
		//	showAlert("Error", table+" must be selected first");
		//	return;
		//}
		 
		 Intent i = new Intent(this, LocalMap.class);
	 		
		 i.putExtra("table", table);
		 for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, (String)thisspinnerhash.get(key).getSelectedItem()); // .getSelectedItemPosition());
				}
		 
					
		 startActivityForResult(i, ACTIVITY_MAP);
	 }
	 
	 	@SuppressWarnings("unused")
	 	private OnClickListener listenerNext = new OnClickListener() {
	        public void onClick(View v) {
	        	f.showNext();
	        }

	    };

	    @SuppressWarnings("unused")
		private OnClickListener listenerPrevious = new OnClickListener() {
	        public void onClick(View v) {
	        	f.showPrevious();
	        }

	    };
	    
	    
	    private void confirmHome(){
	    	
	    	if(flipperpos == 1 || items.size() < 2){
	    		goHome();
	    		return;
	    	}
	    	
	    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    	alertDialog.setTitle(this.getResources().getString(R.string.confirm)); //"Confirm");
	    	alertDialog.setMessage(this.getResources().getString(R.string.home_check)); //"Any unsaved data will be lost. Are you sure?");
	        alertDialog.setButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){
	             public void onClick(DialogInterface dialog, int whichButton) {
	            	goHome();
	             }
	        });

	        	alertDialog.setButton2(this.getResources().getString(R.string.no), new DialogInterface.OnClickListener(){
	        		public void onClick(DialogInterface dialog, int whichButton) {
	        			return;
	        		}
	        	});
	       // }
	        alertDialog.show();	
		}
    
	   private void goHome(){
	    	Intent i = new Intent(this, Epi_collect.class);
	 	   	startActivity(i);
	    }
	   
	   
	   private void listTable(){
			 
		   Intent i = new Intent(this, ListRecords.class);
				
		   int tablepos = orderedtablesrev.get(orderedtables.get(flipperpos-1)) - 1;
		   
		   i.putExtra("table", orderedtables.get(flipperpos-1));
		   
		    // SPINSELHASH HERE
		   //Log.i("SELECTED VALUE", spinnerselhash.get(orderedtables.get(tablepos)));
		   if(tablepos > 0 && !spinnerselhash.get(orderedtables.get(tablepos)).equalsIgnoreCase("Select")){
		   //if(tablepos > 0 && !((String)thisspinnerhash.get(orderedtables.get(tablepos)).getSelectedItem()).equalsIgnoreCase("Select")){
				i.putExtra("select_table_key_column", keyshash.get(orderedtables.get(tablepos)));
		   		// SPINSELHASH HERE	
				i.putExtra("foreign_key", spinnerselhash.get(orderedtables.get(tablepos)));
		   		//i.putExtra("foreign_key", ((String)thisspinnerhash.get(orderedtables.get(tablepos)).getSelectedItem()));
				i.putExtra("select_table", orderedtables.get(tablepos));
			}
			else{
				// This is the first table
				i.putExtra("select_table_key_column", "Null");
				i.putExtra("foreign_key", "Null");
				i.putExtra("select_table", "Null");
			}
		   //i.putExtra("select_table_key_column", "Null");
		   //i.putExtra("foreign_key", "Null");
		   
		   // FindRecord sends a query
		   i.putExtra("query", "none");
			
			for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, (String)thisspinnerhash.get(key).getSelectedItem()); // .getSelectedItemPosition());
				}
			
		    startActivity(i);
	  }
	   
	   boolean islocal, images;
	   private void loadRemoteData(boolean local, boolean image){
	    	
	    	islocal = local;
	    	images = image;
	    	
		   table = orderedtables.get(flipperpos-1);
	    	
	    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
	    	
	    	alert.setTitle(this.getResources().getString(R.string.select_base_table)); //"Select Base Table");
	    	
	    	LinearLayout ll = new LinearLayout(this);
	    	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT );
	 	    //ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    	ll.setOrientation(1);
		 	ll.setGravity(Gravity.CENTER);
		 	ll.setLayoutParams(lp);
		 	   
	 	    ScrollView s = new ScrollView(this);
	 	    ll.addView(s);
	 	    
	 	   //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
		    //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		   
	 	   LinearLayout ll2 = new LinearLayout(this);
	 	   ll2.setOrientation(1);
	 	   ll2.setGravity(Gravity.CENTER);
	 	   ll2.setLayoutParams(lp);
	       //LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
	 	    
	 	  //TableLayout l=new TableLayout(this);
		    //l.setColumnStretchable(0, true);
		    //l.setColumnStretchable(1, true);
	 	  	
		    s.addView(ll2); 
		    
		    //RelativeLayout rl2;
		    //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	//lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		    
	 	   	remotecb = new CheckBox(this);
	 	   	remotecb.setText(this.getResources().getString(R.string.local_download));//"Local Download");
	 	   	remotecb.setChecked(false);
	 	   	if(!dbAccess.getValue("local_remote_xml").equalsIgnoreCase("None"))
	 	   		ll2.addView(remotecb);
	 	   
	 	   	//tableonlycb = new CheckBox(this);
	 	   	//tableonlycb.setText("Load Table Only");
	 	   	//tableonlycb.setChecked(false);
	 	   	//ll2.addView(tableonlycb);
	 	   
	 		TextView tv = new TextView(this);
	 		tv.setTextSize(18);
	 		tv.setGravity(Gravity.CENTER_HORIZONTAL);
	 		tv.setText(this.getResources().getString(R.string.select_base_table)); //"Select table for download");

	 		ll2.addView(tv);
	 	   
	 		Spinner spin = new Spinner(this); 
		    	
	 		ll2.addView(spin);
	 		//keyshash = dbAccess.getKeys();
	 		
	 		Vector<String> tempvec = new Vector<String>();
	 		tempvec.addElement("Select");
			
	 		String thistable = table = orderedtables.get(flipperpos-1);
	 		boolean skiptable = true;
	 		for(String table : dbAccess.getKeys().keySet()){
	 			if(!skiptable)
	 				tempvec.addElement(table);
	 			if(table.equalsIgnoreCase(thistable))
	 				skiptable = false;
	 			}
		
		    	
	 		ArrayAdapter<String> aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tempvec);
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	spin.setAdapter(aspnLocs); 
	    	
	    	spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	    		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		    	   	entry_selected_table = (String)parent.getItemAtPosition(pos);
		    	    }
		    	    public void onNothingSelected(AdapterView<?> parent) {
		    	    }
		    	}); 

	    		alert.setPositiveButton(this.getResources().getString(R.string.get_data), new DialogInterface.OnClickListener() {  
	    		public void onClick(DialogInterface dialog, int whichButton) {
	    			getRemoteDataFile(islocal, images);
	    			return;
		         }
	    	  }); 

	    	    
	    	  alert.setNegativeButton(this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {  
	    	    public void onClick(DialogInterface dialog, int whichButton) { 
	    	    	return;
	    	      // Canceled.  
	    	    }  
	    	  });  
	    	     
	    	  alert.setView(ll); 
	    	  alert.show();  
	    	
	    	
	    }
	    
	   
	   
	   boolean local, getimages;
	   ProgressDialog myProgressDialog = null;
	   String table, entry;
	   int dataresult = 0;
	  private void getRemoteDataFile(boolean islocal, boolean images){
		  local = islocal;
		  getimages = images;
		  
		   table = orderedtables.get(flipperpos-1);
		  
		   if(entry_selected_table.equalsIgnoreCase("Select")){
				  showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.select_remote_table_first)); //"Error", "Remote table must be selected for data download");
				  return;
			  }
		   
		  if(flipperpos == 1){
			  showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.data_download_select_error)); //"Error", "Table must be selected for data download");
			  return;
		  }
		  
		  if(thisspinnerhash.get(table).getSelectedItemPosition() == 0){
			  showAlert(this.getResources().getString(R.string.error), this.getResources().getString(R.string.select_entry_first)); //"Error", "Entry must be selected for data download");
			  return;
		  }
		  
		   entry = key_titlehash.get(table+"_"+(String)thisspinnerhash.get(table).getSelectedItem());
		  entry = entry.replaceFirst("\\S+,", "");
		  
	      myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.loading_data), true);

	      final String error = this.getResources().getString(R.string.error),
	    		  project_data_retrieval_failed = this.getResources().getString(R.string.project_data_retrieval_failed),
	    		  warning = this.getResources().getString(R.string.warning),
	    		  success = this.getResources().getString(R.string.success),
	    		  data_loaded_load_images = this.getResources().getString(R.string.data_loaded_load_images),
	    		  too_many_entries = this.getResources().getString(R.string.too_many_entries),
	    		  data_loaded = this.getResources().getString(R.string.data_loaded);
	    				  
	      new Thread() {
	           public void run() {
	        	   dataresult = 0;
	                try{
	                	dataresult = dbAccess.getRemoteData(dbAccess.getProject(), entry_selected_table, table, entry.replaceAll("\\s+", "%s"), local, getimages, false, sIMEI, email);
	                	
	                } catch (Exception e) {
	                	//Log.i(getClass().getSimpleName(), "ERROR: "+ e);
	                }
	                // Dismiss the Dialog
	                myProgressDialog.dismiss();
	                       		 	
	                Looper.prepare();
	                
	                //Log.i(getClass().getSimpleName(), "ERROR: "+ dataresult);
	                if(dataresult == 0){
	                	showAlert(error, project_data_retrieval_failed); //"Error", "Project data retrieval failed"); //showAlert("Success", "Project Loaded");
	                	myProgressDialog.dismiss();
	                }
	                else if(dataresult == 1)
	                	showImageAlert(success, data_loaded_load_images); //"Success", "Project data successfully loaded. Load associated images?");
	                else if(dataresult == 3)        	
	                    showAlert(warning, too_many_entries); //"Warning", "Returned data contains too many entries.\n\nSelect higher table");
	                else                	
	                	showAlert(success, data_loaded); //"Success", "Project data successfully loaded");
	                
	                spinnersupdated = true;
	       		 	
	                Looper.loop();
	                Looper.myLooper().quit(); 
	             //   
	                
	                 
	           }
	      }.start();
		  
	      
	  }
	  
	 private void deleteEntry(){
		 table = orderedtables.get(flipperpos-1);
		 int pos = thisspinnerhash.get(table).getSelectedItemPosition();
		 dbAccess.deleteRow(table, "ecpkey", key_titlehash.get(table+"_"+(String)thisspinnerhash.get(table).getItemAtPosition(pos)));

		 dbAccess.updateFileTable(flipperpos-1, orderedtables.size());
		 updateSpinners();
	 }
	    
	 private void deleteAllEntries(){
		 
		 table = orderedtables.get(flipperpos-1);
		  
		 dbAccess.deleteAllRows(table);
		 
		 dbAccess.updateFileTable(flipperpos-1, orderedtables.size());

		 updateSpinners();
	 }
	 
	  public void showAlert(String title, String result){
	    	new AlertDialog.Builder(this)
	        .setTitle(title)
	        .setMessage(result)
	        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {

	             }
	        }).show();	
	    }

	  public void showImageAlert(String title, String result){
	    	new AlertDialog.Builder(this)
	        .setTitle(title)
	        .setMessage(result)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {
	            	 loadRemoteData(local, true);
	            	 //getRemoteDataFile(local, true);
	             }
	        })
	        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {

	             }
	        }).show();
	    }

	  public void showDeleteEntryAlert(){
	    	new AlertDialog.Builder(this)
	        .setTitle(R.string.delete_entry) //"Delete Entry?")
	        .setMessage(R.string.confirm_delete_continue) //"This will delete the entry and all associated data.\nContinue?")
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {
	            	 deleteEntry();
	             }
	        })
	        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {

	             }
	        }).show();
	    }
	  
	  public void showDeleteAllEntriesAlert(){
	    	new AlertDialog.Builder(this)
	        .setTitle(R.string.delete_entries) //"Delete Entries?")
	        .setMessage(R.string.delete_entries_confirm) //"This will delete all entries in the table and all associated data.\nContinue?")
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {
	            	 deleteAllEntries();
	             }
	        })
	        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {

	             }
	        }).show();
	    }
}

