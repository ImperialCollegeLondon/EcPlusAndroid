package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
import java.util.HashMap;
//import java.util.Hashtable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

//import uk.ac.imperial.epi_collect2.EntryNote.MyGestureDetector;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.app.AlertDialog;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
//import android.graphics.Color;
///import android.location.LocationManager;
import android.os.Bundle;
//import android.text.InputType;
//import android.util.Log;
//import android.view.GestureDetector;
import android.util.Log;
import android.view.Gravity;
//import android.view.KeyEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
import android.widget.LinearLayout;
//import android.widget.ListAdapter;
import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.Spinner;
//import android.widget.TableLayout;
import android.widget.TextView;
//import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class DetailsView extends Activity {

	//private static final int EXIT = 1;
	private static final int ACTIVITY_BRANCH = 1;
	private DBAccess dbAccess;
	private String primary_key, coretable; //, coretablekey; entryid, 
	//private static String[] textviews = new String[0];
    //private static String[] spinners = new String[0];
    //private static String[] checkboxes = new String[0];
	private Bundle extras;
	private List<String> items;
	private HashMap<Integer, String> listpos = new HashMap<Integer, String>();
	private ListView lv;
    private ArrayAdapter<String> notes;
    //private static final int ACTIVITY_EDIT=1;
    private static final int HIERARCHY = 1;
    private static final int SELECT = 2;
    private static final int DELETE = 3;
    private static final int RESET = 4; 
    private static final int RETURN = 5;
	private LinkedHashMap <String, String> spinhash = new LinkedHashMap<String, String>();
	private Vector<String> nodisplay = new Vector<String>(), ecjumped = new Vector<String>();
	//private Hashtable<Integer, Integer>listindex = new Hashtable<Integer, Integer>();
	private Button selectButton;
	private boolean frombranch = false, change_synch = true;
	private TextView statustext;
	private String[] tempstring;
	private Object[] tempstring2;
	private Vector<String> valuevec;
	private String status = "";
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
	
        dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    super.setTitle("EpiCollect+ "+dbAccess.getProject());
	    
	    extras = getIntent().getExtras();
	    
	    if (extras != null){
	    	for(String key : dbAccess.getKeys().keySet()){
	    		spinhash.put(key, extras.getString(key));
	    	}
	    }
	    
	    if(dbAccess.getValue("change_synch").equalsIgnoreCase("false"))
			change_synch = false;
	    
	    primary_key = extras.getString("primary_key");
	    coretable = extras.getString("table");
		//coretablekey = dbAccess.getKeyValue(coretable);
	    //entryid = extras.getString("table");
	    if(extras.getInt("frombranch") == 1)
	    	frombranch = true;
    	//Log.i("EXTRAS", "BRANCH _ "+extras.getInt("frombranch"));
	    try{
	    	status = extras.getString("status");
	    }
	    catch(NullPointerException npe){}
	    
	    getValues(coretable);
	    setContentView(setLayout(coretable, extras)); 
        
	}
	
	// Prevents warnings about casting from an object to a hashtable
	 //@SuppressWarnings("unchecked")
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	super.onActivityResult(requestCode, resultCode, data);
	        
	 // Cascades the exit program call
	   	if (requestCode == 123) {
		 	   this.finish();
		 	   }
	   	
	   	Bundle extras = null;
	   	if(data != null){
	   		extras = data.getExtras();
	   		if(extras.getInt("frombranch") == 1)
		    	frombranch = true;
	   	}
	    switch(requestCode) {
	    case 1:
	    	updateList();
	    	//String target = extras.getString("target");
	    	//Log.i("LIST CHECK", lv.getItemAtPosition(1).getClass().toString());
	        break;
	    } 
	} 
	 
	 public boolean onKeyDown(int keyCode, KeyEvent event)
	    {
	    	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
    			dbAccess.close();
        		Intent i = getIntent();
    			setResult(RESULT_OK, i);
    	        finish();
	    		
	    	}
	    	if(keyCode == KeyEvent.KEYCODE_MENU)
	    		openOptionsMenu();

	        return true;
	    } 
	    
	
	 @Override
		public boolean onPrepareOptionsMenu(Menu menu) {
			
			menu.clear();
		    if(!frombranch){
		    	// If there is only 1 table don't want option to select entry
		    	if(dbAccess.getKeys().size() > 1)
		    		menu.add(0, SELECT, 0, R.string.menu_select_entry);
		    	// There is no hierarchy for the first table so don't show it
		    	if(dbAccess.getTableNum(coretable) > 1)
		    		menu.add(0, HIERARCHY, 0, R.string.menu_hierarchy);
		    }
		    //else{
		    	menu.add(0, DELETE, 0, R.string.menu_del_entry);
		    //}
		    if(!frombranch && dbAccess.getValue(coretable, "ecstored", primary_key).equalsIgnoreCase("Y"))
		    	menu.add(0, RESET, 0, R.string.menu_reset_synch2);
		    menu.add(0, RETURN, 0, R.string.menu_return);
		    
		    return super.onPrepareOptionsMenu(menu);
		}
	 
	 @Override
	    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	        super.onMenuItemSelected(featureId, item);
	        switch(item.getItemId()) {
	        case SELECT:
				selectEntry();
				break;
	        case HIERARCHY:
				showHierarchy();
				break;
	        case DELETE:
				showDeleteEntryAlert();
				break;
	        case RESET:
	        	showResetSynchAlert();
	        	break;
	        case RETURN:
	        	dbAccess.close();
	        	Intent i = getIntent();
				i.putExtras(extras);
				setResult(RESULT_OK, i);
		        finish();
	    		break;
	        }
		    return true;
	    }
	 

	private void getValues(String table){
    	
    	// Reset values as otherwise odd values seem to get left behind:
    	// Project - data - back -back - new project - data -> causes crash
    	// If first project has spinner and second doesn't then second "spinners"
    	// still contains
    	//textviews = new String[0];
        //spinners = new String[0];
        //checkboxes = new String[0];
        
    	//if(dbAccess.getValue(table, "textviews") != null)
		//	textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
    	//if(dbAccess.getValue(table, "spinners") != null)
    	//	spinners = (dbAccess.getValue(table, "spinners")).split(",,");
    	//if(dbAccess.getValue(table, "checkboxes") != null)
    	//	checkboxes = (dbAccess.getValue(table, "checkboxes")).split(",,");

    	if(dbAccess.getValue(table, "nodisplay") != null && dbAccess.getValue(table, "nodisplay").length() > 0){
    		for(String key : (dbAccess.getValue(table, "nodisplay")).split(",,")){
    			nodisplay.addElement(key);
    		} 
    	}
    	
    	if(dbAccess.getValue(table, "ecjumped", primary_key) != null && dbAccess.getValue(table, "ecjumped", primary_key).length() > 0){
    		for(String key : (dbAccess.getValue(table, "ecjumped", primary_key)).split(",")){
    			Log.i("DB JUMPED", dbAccess.getValue(table, "ecjumped", primary_key));
    			ecjumped.addElement(key);
    		}
    	}
    }
	
private LinearLayout setLayout(String table, Bundle extras){
       
	//String primary_key = extras.getString("primary_key");
	//String coretable = extras.getString("table");
	//String coretablekey = dbAccess.getKeyValue(coretable);
	
		TextView tv; //, tv2;
		
        String views =  dbAccess.getValue(table, "notes_layout");// parser.getValues();
               
	    LinearLayout ll = new LinearLayout(this);
	    ll.setOrientation(1);
	    //ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    
	    statustext = new TextView(this);
	    statustext.setText(table + " Details - "+ dbAccess.getValue(table, "ecstored", primary_key));
	    statustext.setTextSize(24);
	    statustext.setGravity(Gravity.CENTER_HORIZONTAL);
    	//tv.setWidth(100);
    	ll.addView(statustext); //, lp);
		
    	View ruler = new View(this); 
    	ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 	    ruler = new View(this); 
 	    ruler.setBackgroundColor(0xFF00FF00); 
 	    ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
 	    ruler = new View(this); 
 	    ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 	   
 	    if(dbAccess.getValue(table, "message", primary_key) != null && dbAccess.getValue(table, "message", primary_key).length() > 0){
 	    	//Log.i("MESSAGE", dbAccess.getValue(table, "message", primary_key));
 	    	tv = new TextView(this);
 	    	tv.setTextSize(18);
 	    	tv.setText(dbAccess.getValue(table, "message", primary_key));
 	    	ll.addView(tv);
 	    	ruler = new View(this); 
 	    	ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 	 	    ruler = new View(this); 
 	 	    ruler.setBackgroundColor(0xFF00FF00); 
 	 	    ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
 	 	    ruler = new View(this); 
 	 	    ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 	    }
 	    
 	    if(!frombranch && dbAccess.getKeys().size() > 1){
 		   selectButton = new Button(this);
 		   selectButton.setText("Select Entry");
	    
 		   selectButton.setOnClickListener(new View.OnClickListener() {
 			   public void onClick(View view) {
 				   selectEntry();
	    			}
	    		});
	    
 		   ruler = new View(this); 
 		   ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
	    	    	   
 		   ll.addView(selectButton);
 	   
 		   ruler = new View(this); 
 		   ruler.setBackgroundColor(0xFF00FF00); 
 		   ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 2));
 		   ruler = new View(this); 
 		   ll.addView(ruler, new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, 5));
 	   }
	    String[] viewvalues;
	    
	    views.replaceFirst(",,,", "");
	    String[] allviews = views.split(",,,");

	   // RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );      
    
	    //TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    //TableLayout l=new TableLayout(this);
	    
	    //ScrollView s = new ScrollView(this);
	    //ll.addView(s); 
	    //s.addView(l);
	    
	    lv = new ListView(this); 
	    
	    items = new ArrayList<String>();
	    
	    ll.addView(lv);
	    
	    
	   // RelativeLayout.LayoutParams rlp3=null, rlp4=null, rlp5=null;
	
	    String label = "", id;
	    int count = 0, pagecount = 0;
	    for(String thisview : allviews){
	    	
	    	viewvalues = thisview.split(",,");
	    	label = "";
	    	
	    	if((viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("barcode") || viewvalues[0].equalsIgnoreCase("group")) && !nodisplay.contains(viewvalues[1])){
	    			   
	    		if(ecjumped.contains(viewvalues[1])){
	    			Log.i("JUMPED", viewvalues[1]);
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id +" - ";

	    			label += dbAccess.getValue(table, viewvalues[1], primary_key);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_1", count+" - "+pagecount);
	    		pagecount++;
	    	}
	    	
	    	else if(viewvalues[0].equalsIgnoreCase("branch")){// && !nodisplay.contains(viewvalues[1])){
	    		
	    		if(ecjumped.contains(viewvalues[1])){
	    			//Log.i("FROMDETAILS IN JUMPED", viewvalues[1]);
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);

	    			int entries = dbAccess.getBranchCount(viewvalues[3], primary_key); //keystring[0], keystring[1]);
    			
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id+" - "+ entries +" "+ this.getResources().getString(R.string.entries);
    		
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_2", count+" - "+pagecount);
	    		pagecount++;
	    	}
	    	
	    	else if((viewvalues[0].equalsIgnoreCase("GPS") || viewvalues[0].equalsIgnoreCase("location")) && !nodisplay.contains(viewvalues[1])){
    			
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id +" - ";
	    		
	    			String lat = dbAccess.getValue(table, viewvalues[1]+"_lat", primary_key); //coretablekey, 
	    			String lon = dbAccess.getValue(table, viewvalues[1]+"_lon", primary_key);
	    			String acc = dbAccess.getValue(table, viewvalues[1]+"_acc", primary_key);
	    			String alt = dbAccess.getValue(table, viewvalues[1]+"_alt", primary_key);
	    			String bearing = dbAccess.getValue(table, viewvalues[1]+"_bearing", primary_key);
	    			if(lat.equalsIgnoreCase("0")){
	    				label += this.getResources().getString(R.string.gps_not_set); //"Not Set";
	    			}
	    			else{
	    				label += "Lat: "+lat+" Lon: "+lon+" Acc: "+acc+"m Alt:"+alt+" Bearing:"+bearing;
	    			}
    			
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_3", count+" - "+pagecount);
	    		pagecount++;
    		}
    	      
	    	else if(viewvalues[0].equalsIgnoreCase("photo") && !nodisplay.contains(viewvalues[1])){
	    		 if(ecjumped.contains(viewvalues[1])){
		    			//listindex.put(count, pagecount);
		    		}
	    		 else{
		    		listpos.put(count, viewvalues[1]);
		    		//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
		    		
		    		id = viewvalues[2].replaceAll("_", " ");
		    		label = id + " - ";
		        	
		    		try{
		    			if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4){
	    			 		Log.i("PHOTO ID", dbAccess.getValue(table, viewvalues[1], primary_key));
		    				label += this.getResources().getString(R.string.photo_avail); //"Photo Available";
		    			}
		    			else
		    				label += this.getResources().getString(R.string.no_photo_avail); //"No Photo Available";
		    		}
		    		catch(NullPointerException npe){
		    			label += this.getResources().getString(R.string.no_photo_avail); //"No Photo Available";
		    		}
		    		//listindex.put(count, pagecount);
		    		count++;
	    		 }
	    		// Log.i("FROMDETAILS2_4", count+" - "+pagecount);
	    		 pagecount++;
		    	}
	    	 
	    	else if(viewvalues[0].equalsIgnoreCase("video") && !nodisplay.contains(viewvalues[1])){
	    		 if(ecjumped.contains(viewvalues[1])){
		    			//listindex.put(count, pagecount);
		    		}
	    		 else{
		    		listpos.put(count, viewvalues[1]);
		    		//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
		    		
		    		id = viewvalues[2].replaceAll("_", " ");
		    		label = id + " - ";
		        	
		    		try{
		    			if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4)
		    				label += this.getResources().getString(R.string.video_avail); //"Video Available";
		    			else
		    				label += this.getResources().getString(R.string.no_video_avail); //"No Video Available";
		    		}
		    		catch(NullPointerException npe){
		    			label += this.getResources().getString(R.string.no_video_avail); //"No Video Available";
		    		}
		    		//listindex.put(count, pagecount);
		    		count++;
	    		 }
	    		// Log.i("FROMDETAILS2_5", count+" - "+pagecount);
	    		 pagecount++;
		    	}
	    	 
	    	else if(viewvalues[0].equalsIgnoreCase("audio") && !nodisplay.contains(viewvalues[1])){
	    		 if(ecjumped.contains(viewvalues[1])){
		    			//listindex.put(count, pagecount);
		    		}
	    		 else{
		    		listpos.put(count, viewvalues[1]);
		    		//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
		    		
		    		id = viewvalues[2].replaceAll("_", " ");
		    		label = id + " - ";
		        	
		    		try{
		    			if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4)
		    				label += this.getResources().getString(R.string.audio_avail); //"Audio Available";
		    			else
		    			label += this.getResources().getString(R.string.no_audio_avail); //No Audio Available";
		    		}
		    		catch(NullPointerException npe){
		    			label += this.getResources().getString(R.string.no_audio_avail); //"No Audio Available";
		    		}
		    		//listindex.put(count, pagecount);
		    		count++;
		    	}
	    		//Log.i("FROMDETAILS2_6", count+" - "+pagecount);
	    		pagecount++;
	    	 }
	    	else if(viewvalues[0].equalsIgnoreCase("select1") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";

	    	        tempstring = (dbAccess.getValue(table, "spinner_"+viewvalues[1])).split(",,");
	    	        // create vector for given list.
	    	        tempstring2 = (dbAccess.getValue(table, "spinner_values_"+viewvalues[1])).split(",,");
	    	        valuevec = new Vector(Arrays.asList(tempstring2));
	    	        
	    	        String value;
	    	        
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    			//Log.i("RETURNED VALUE", value+ " "+valuevec.indexOf(value) +" VALUES "+dbAccess.getValue(table, "spinner_values_"+viewvalues[1])+" KEYS "+dbAccess.getValue(table, "spinner_"+viewvalues[1]));
	    	        if(valuevec.indexOf(value) > -1)
	    	        	label += tempstring[valuevec.indexOf(value)];
	    			/*int i;
	    			boolean match = false;
	    			for(i = 1; i < tempstring2.length; i++){
	    				if(tempstring2[i].equalsIgnoreCase(value)){
	    					match = true;
	    					break;
	    				}
	    			}
	    		
	    			if(match){
	    				label += tempstring[i]; //dbAccess.getValue(table, viewvalues[1], primary_key); // tempstring[Integer.parseInt(dbAccess.getValue(table, viewvalues[1], primary_key))];
	    			} */
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_7", count+" - "+pagecount);
	    		pagecount++;
	    		
	    	}
	    	else if(viewvalues[0].equalsIgnoreCase("radio") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			tempstring = (dbAccess.getValue(table, "radio_"+viewvalues[1])).split(",,");
	    	        // create vector for given list.
	    	        tempstring2 = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");
	    	        valuevec = new Vector(Arrays.asList(tempstring2));
	    	        
	    	        String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    	        if(valuevec.indexOf(value) > -1)
	    	        	label += tempstring[valuevec.indexOf(value)];
	    	        
	    			/*tempstring = (dbAccess.getValue(table, "radio_"+viewvalues[1])).split(",,");
	    			tempstring2 = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");

	    			String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    			int i;
	    			boolean match = false;
	    			for(i = 1; i < tempstring2.length; i++){
	    				if(tempstring2[i].equalsIgnoreCase(value)){
	    					match = true;
	    					break;
	    				}
	    			}
	    		
	    			// Need to catch an exception here in case no buttons are selected
	    			// For a select1 the position "0" is always selected as a default
	    			if(match){
	    				label += tempstring[i]; //dbAccess.getValue(table, viewvalues[1], primary_key); //tempstring[Integer.parseInt(dbAccess.getValue(table, viewvalues[1], primary_key))];
	    			} */
	    	
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_8", count+" - "+pagecount);
	    		pagecount++;
	    	}
	    	else if(viewvalues[0].equalsIgnoreCase("select")){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			//Log.i("FROMDETAILS", count+" - "+viewvalues[1]);
	    	 	
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	    	 	
	    			for(int i = 3; i < viewvalues.length; i+=2){
	        		
	    				label += viewvalues[i]+" : "; //i+1
	        				if(Integer.parseInt(dbAccess.getValue(table, viewvalues[1]+"_"+viewvalues[i+1], primary_key)) == 1)
	        					label += "Y ";
	        				else
	        					label += "N ";
	    			}
	        	
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		//Log.i("FROMDETAILS2_9", count+" - "+pagecount);
	    		pagecount++;
	    	}
	    	
	    	if(label.length() > 0)
	    		items.add(label);
	    	
	    }
	    
	    notes = new ArrayAdapter<String>(this, R.layout.records_row_details, items); //ArrayAdapter
	    lv.setAdapter(notes);
	    
	    lv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	
	        	//if(change_synch || !status.equalsIgnoreCase("R"))
	        		editData(position); //listindex.get(position));
	        	
	        }
	      });

	    return ll;
	    
	 }

	private void updateList(){
		
		//Log.i("UPDATE LIST ", "HERE");
		if(dbAccess == null){
			dbAccess = new DBAccess(this);
			dbAccess.open();
		}
		//String primary_key = extras.getString("primary_key");
		//String coretable = extras.getString("table");
		//String coretablekey = dbAccess.getKeyValue(coretable);
		
		String label, table = coretable, id;
		ecjumped.clear();
		String views =  dbAccess.getValue(table, "notes_layout");// parser.getValues();

		if(dbAccess.getValue(table, "ecjumped", primary_key) != null && dbAccess.getValue(table, "ecjumped", primary_key).length() > 0){
    		for(String key : (dbAccess.getValue(table, "ecjumped", primary_key)).split(",")){
    			ecjumped.addElement(key);
    		}
    	}
		String[] viewvalues;
	    
	    views.replaceFirst(",,,", "");
	    String[] allviews = views.split(",,,");
	    
	    notes.clear();
	    items.clear();
	    int count = 0, pagecount = 0;
		for(String thisview : allviews){
	    	
	    	viewvalues = thisview.split(",,");
	    	label = "";

	    	if((viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("barcode") || viewvalues[0].equalsIgnoreCase("group")) && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	    		
	    			label += dbAccess.getValue(table, viewvalues[1], primary_key);
	    		
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("branch")){// && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			int entries = dbAccess.getBranchCount(viewvalues[3], primary_key); //keystring[0], keystring[1]);
    			
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id+" - "+ entries +" "+ this.getResources().getString(R.string.entries); //entries";

	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}

	    	if((viewvalues[0].equalsIgnoreCase("GPS") || viewvalues[0].equalsIgnoreCase("location")) && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	    		
	    			String lat = dbAccess.getValue(table, viewvalues[1]+"_lat", primary_key);
	    			String lon = dbAccess.getValue(table, viewvalues[1]+"_lon", primary_key);
	    			String acc = dbAccess.getValue(table, viewvalues[1]+"_acc", primary_key);
	    			String alt = dbAccess.getValue(table, viewvalues[1]+"_alt", primary_key);
	    			String bearing = dbAccess.getValue(table, viewvalues[1]+"_bearing", primary_key);
	    			if(lat.equalsIgnoreCase("0")){
	    				label += this.getResources().getString(R.string.gps_not_set); //"GPS Not Set";
	    			}
	    			else{
	    				label += "Lat: "+acc+" Lon: "+lon+" Acc: "+acc+"m Alt:"+alt+" Bearing:"+bearing;
	    			}
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
    		}
	    	if(viewvalues[0].equalsIgnoreCase("photo") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    				    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			try{
	    				if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4){
	    			 		label += this.getResources().getString(R.string.photo_avail); //"Photo Available";
	    				}
	    			 	else
	    					label += this.getResources().getString(R.string.no_photo_avail); //"No Photo Available";
	    			}
	    			catch(NullPointerException npe){
	    				label += this.getResources().getString(R.string.no_photo_avail); //"No Photo Available";
	    			}
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("video") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    				    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			try{
	    				if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4)
	    					label += this.getResources().getString(R.string.video_avail); //"Video Available";
	    				else
	    					label += this.getResources().getString(R.string.no_video_avail); //"No Video Available";
	    			}
	    			catch(NullPointerException npe){
	    				label += this.getResources().getString(R.string.no_video_avail); //"No Video Available";
	    			}
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("audio") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    				    		
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			try{
	    				if(dbAccess.getValue(table, viewvalues[1], primary_key).length() > 4)
	    					label += this.getResources().getString(R.string.audio_avail); //"Audio Available";
	    				else
	    					label += this.getResources().getString(R.string.no_audio_avail); //"No Audio Available";
	    			}
	    			catch(NullPointerException npe){
	    				label += this.getResources().getString(R.string.no_audio_avail); //"No Audio Available";
	    			}
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    		                  
	    	if(viewvalues[0].equalsIgnoreCase("select1") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			tempstring = (dbAccess.getValue(table, "spinner_"+viewvalues[1])).split(",,");
	    	        // create vector for given list.
	    	        tempstring2 = (dbAccess.getValue(table, "spinner_values_"+viewvalues[1])).split(",,");
	    	        valuevec = new Vector(Arrays.asList(tempstring2));
	    	        
	    	        String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    	        if(valuevec.indexOf(value) > -1)
	    	        	label += tempstring[valuevec.indexOf(value)];
	    	        
	    			/*tempstring = (dbAccess.getValue(table, "spinner_"+viewvalues[1])).split(",,");
	    			tempstring2 = (dbAccess.getValue(table, "spinner_values_"+viewvalues[1])).split(",,");
	    		
	    			String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    			int i;
	    			boolean match = false;
	    			for(i = 1; i < tempstring2.length; i++){
	    				if(tempstring2[i].equalsIgnoreCase(value)){
	    					match = true;
	    					break;
	    				}
	    			}
	    		
	    		
	    			if(match){
	    				label += tempstring[i]; //dbAccess.getValue(table, viewvalues[1], primary_key); // tempstring[Integer.parseInt(dbAccess.getValue(table, viewvalues[1], primary_key))];
	    			} */
	    		    		
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    	if(viewvalues[0].equalsIgnoreCase("radio") && !nodisplay.contains(viewvalues[1])){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	    			
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	        	
	    			tempstring = (dbAccess.getValue(table, "radio_"+viewvalues[1])).split(",,");
	    	        // create vector for given list.
	    	        tempstring2 = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");
	    	        valuevec = new Vector(Arrays.asList(tempstring2));
	    	        	    	        
	    	        String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    	        if(valuevec.indexOf(value) > -1)
	    	        	label += tempstring[valuevec.indexOf(value)];
	    	        
	    	        /*tempstring = (dbAccess.getValue(table, "radio_"+viewvalues[1])).split(",,");
	    			tempstring2 = (dbAccess.getValue(table, "radio_values_"+viewvalues[1])).split(",,");

	    			String value;
	    			value = dbAccess.getValue(table, viewvalues[1], primary_key);
	    			int i;
	    			boolean match = false;
	    			for(i = 1; i < tempstring2.length; i++){
	    				if(tempstring2[i].equalsIgnoreCase(value)){
	    					match = true;
	    					break;
	    				}
	    			}
	    		
	    		// Need to catch an exception here in case no buttons are selected
	    		// For a select1 the position "0" is always selected as a default
	    			if(match){
	    				label += tempstring[i]; //dbAccess.getValue(table, viewvalues[1], primary_key); //tempstring[Integer.parseInt(dbAccess.getValue(table, viewvalues[1], primary_key))];
	    			} */
	    		
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		
	    		pagecount++;
	    	}
	    	if(viewvalues[0].equalsIgnoreCase("select")){
	    		if(ecjumped.contains(viewvalues[1])){
	    			//listindex.put(count, pagecount);
	    		}
	    		else{
	    			listpos.put(count, viewvalues[1]);
	    			id = viewvalues[2].replaceAll("_", " ");
	    			label = id + " - ";
	    		
	    			for(int i = 3; i < viewvalues.length; i+=2){
	        		
	    				label += viewvalues[i]+" : "; //i+1
	    				if(Integer.parseInt(dbAccess.getValue(table, viewvalues[1]+"_"+viewvalues[i+1], primary_key)) == 1)
	    					label += "Y ";
	    				else
	    					label += "N ";
	    			}
	    			notes.insert(label, count);
	    			//listindex.put(count, pagecount);
	    			count++;
	    		}
	    		pagecount++;
	    	}

	    }
		
		//notes = new ArrayAdapter<String>(this, R.layout.records_row_details, items); //ArrayAdapter
	    //lv.setAdapter(notes);
	    
	    //lv.setOnItemClickListener(new OnItemClickListener() {
	     //   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	     //   	editData(position);
	     //   	
	     //   }
	     // });
		notes.notifyDataSetChanged();
	}


	private void editData(int position){
				 
		Intent i = new Intent(this, EntryNote.class);
		
		extras.putString("target", listpos.get(position));
    	if(frombranch)
    		extras.putInt("branch", 1); // WAS FROMBRANCH
    	else
    		extras.putInt("branch", 0); // WAS FROMBRANCH
    	extras.putString("table", coretable);
    	extras.putString("status", status);
    	//Log.i("STATUS", status);
		i.putExtras(extras);
		
		dbAccess.close();
	    startActivityForResult(i, 1);
	 }
	
	/*private void selectTable(){
		
		//Intent i = new Intent(this, TableSelect.class); //NewEntry
	    
		//i.putExtras(getIntent().getExtras());
		// Only need to return the first table setting as the rest are set automatically in TableSelect
		//int pos;
		//for(String key : spinhash.keySet()){
		//	pos = spinhash.get(key);
		//	i.putExtra(key, pos);	
		//
		//}

	
		//startActivity(i);
		
		setResult(RESULT_OK, getIntent());
        finish();
	} */
	
	@Override
    protected void onPause() {
        super.onPause();
        dbAccess.close();
        dbAccess = null;
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (dbAccess == null) {
        	dbAccess = new DBAccess(this);
        	dbAccess.open();
        }
        
    }
    
	@Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	super.onSaveInstanceState(outState);  
            
    	// NEED TO COMPLETE THIS
    	
    	outState.putString("primary_key", primary_key);
    	outState.putString("table", coretable);
    	//outState.putString("coretable", coretablekey);
    	outState.putString("table", coretable);
    	
    	/*for(String key : textviews){
    		outState.putString(key, textviewhash.get(key).getText().toString());
    	   	}
    	
        for(String key : spinners){
        	outState.putInt(key, thisspinnerhash.get(key).getSelectedItemPosition());
        }
        
        for(String key : checkboxes){
        	if(checkboxhash.get(key).isChecked()){
        		outState.putBoolean(key, true);
        	}
        	else{
        		outState.putBoolean(key, false);
        	}
        }*/
    }  
	
	
	private void showHierarchy(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);  
    	
    	alert.setTitle("Hierarchy");  
    	LinkedHashMap<String, String> spinhash2 = spinhash;
    	
    	// Need to set spinhash2 in case the details view is loaded from a list view
    	// In this case spinhash will not have been set 
    	LinkedHashMap<Integer, String> tablehash = dbAccess.getTablesByNum();
    	LinkedHashMap<String, String> keyshash = dbAccess.getKeys();
    	int tablenum = dbAccess.getTableNum(coretable);
    	String tempkey="", tempid="", this_pkey = primary_key;
		
		for(int j = tablenum; j >= 1; j--){
			if(j >= 2){
				tempkey = keyshash.get(tablehash.get(j-1));
			}
			
			tempid = dbAccess.getValue(tablehash.get(j), "ectitle", this_pkey); //tempkey, 
			//Log.i("SPINHASH", tablehash.get(j)+" "+tempid);
			spinhash2.put(tablehash.get(j), tempid);
			
			if(j >= 2){
				this_pkey = dbAccess.getFKey(tablehash.get(j), tempkey, this_pkey);
			}
		}
    	
    	alert.setView(Hierarchy.setLayout(spinhash2, coretable, this)); //filterView);    	   
    	     	        
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
    	public void onClick(DialogInterface dialog, int whichButton) {
    	    return;		  
    		
    	  }
    	  });  
    	    
    	alert.show();    
	}

	
	private void selectEntry(){
		
		Intent i = new Intent(this, TableSelect.class);
		Bundle extras = new Bundle();
		LinkedHashMap<Integer, String> tablehash;
		LinkedHashMap<String, String> keyshash;
		int tablenum;
		
		
		String tempkey="", tempid="";
	
			tablehash = dbAccess.getTablesByNum();
			keyshash = dbAccess.getKeys();
			tablenum = dbAccess.getTableNum(coretable);
			
			// Initialise extras
			for(String s : keyshash.keySet())
				extras.putString(s, "Select");
			
			for(int j = tablenum; j >= 1; j--){
				
				if(j >= 2){
					tempkey = keyshash.get(tablehash.get(j-1));
				}
					tempid = dbAccess.getValue(tablehash.get(j), "ectitle", primary_key); //tempkey, 
					
					//Log.i("DETAILS", tempkey+" "+tempid);
				//}
				
				extras.putString(tablehash.get(j), tempid);
				//Log.i("DETAILS 2", tablehash.get(j)+" "+tempid);
				//primary_key += ","+key+","+entry;
				if(j >= 2){
					primary_key = dbAccess.getFKey(tablehash.get(j), tempkey, primary_key);
				}
				//Log.i("DETAILS 3", primary_key);
			}
				
			extras.putString("nexttable", coretable);
			
			dbAccess.close();
			
			i.putExtras(extras);
			startActivity(i); 
		
	} 
	
	public void showDeleteEntryAlert(){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.delete_entry) //"Delete Entry?")
        .setMessage(R.string.confirm_delete) //"This will delete the entry.\nContinue?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // "Yes"

             public void onClick(DialogInterface dialog, int whichButton) {
            	 deleteEntry();
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // "No"

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();
    }
	
	private void deleteEntry(){
		
		//String[] key_val = primary_key.split(",");
		dbAccess.deleteRow(coretable, "ecpkey", primary_key); // key_val[0], key_val[1]);
		dbAccess.close();
 		Intent i = getIntent();
 		i.putExtras(extras);
		setResult(RESULT_OK, i);
	    finish();
	}
    
	public void showResetSynchAlert(){ 
		final String details = this.getResources().getString(R.string.details);
    	new AlertDialog.Builder(this)
        .setTitle(R.string.reset_synch) //"Reset Synchronised")
        .setMessage(R.string.reset_synch_message) //"Reset the synchronised status of this record?")
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // Yes

             public void onClick(DialogInterface dialog, int whichButton) {
            	dbAccess.updateRecord(coretable, "ecpkey", primary_key, "ecstored", "N");
 	        	statustext.setText(coretable + " "+ details +" - N"); // Details
             }
        })
        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // No

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
}
