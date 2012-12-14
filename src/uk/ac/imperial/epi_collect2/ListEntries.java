package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Vector;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.graphics.Color;
import android.os.Bundle;
//import android.text.InputType;
import android.util.Log;
//import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
//import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class ListEntries extends Activity {
 
	private static final int ACTIVITY_EDIT=1;
	private ArrayAdapter<String> aspnLocs; 
	//private Hashtable<String, EditText> textviewhash;
	private LinkedHashMap<String, Spinner> thisspinnerhash;
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
	private ViewFlipper f;
	private TextView tv; //, pagetv;
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
	LinkedHashMap<String, String> keyshash;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        //super.setTitle(this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/app_name", null, null))); 
        
		dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    super.setTitle("EpiCollect+ "+dbAccess.getProject());
	    
	    getValues();
	    setContentView(setLayout()); 
        
	    Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	if(thisspinnerhash != null){
        		for(String key : thisspinnerhash.keySet()){
        			thisspinnerhash.get(key).setSelection(extras.getInt(key));
        			//Log.i("SPINNER SETTING", "KEY "+key+" POS "+extras.getInt(key));
        			}
        		}
        	}
        	/*if(extras.getInt("canupdate") == 0){
            	canupdate = false;
       			confirmButton.setTextColor(Color.WHITE);
        		confirmButton.setText("Record synchronized");
        		confirmButton.setEnabled(false);
            }
        	String text;
            for(String key : textviews){
           		text = extras.getString(key);
            	if(text == null){
           			text = "";
                }
            	
            	textviewhash.get(key).setText(text);
            	if(!canupdate)
            		textviewhash.get(key).setFocusable(false); 
            }
            
            int location;
            for(String key : spinners){
            	location = extras.getInt(key);
            	//Log.i(getClass().getSimpleName(), "SPINNER LOCATION: "+ key+" "+extras.getInt(key));
            	if(thisspinnerhash.get(key) != null)
            		thisspinnerhash.get(key).setSelection(location);
            	//if(!canupdate)
            	//	thisspinnerhash.get(key).setsetClickable(false);
            }
            
            for(String key : checkboxes){
            	if(extras.getBoolean(key)){
            		checkboxhash.get(key).setChecked(true);
            	}
            	else{
            		checkboxhash.get(key).setChecked(false);
            	}
            	if(!canupdate)
            		checkboxhash.get(key).setClickable(false);
            } */
            
            

        //} 
	}

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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    //menu.add(R.string.menu_reset);
	    //menu.add(R.string.menu_home);
	    //return super.onCreateOptionsMenu(menu);
	    menu.add(0, RESET_ID, 0, R.string.menu_reset);
	    menu.add(0, HOME, 0, R.string.menu_home);
	    return true;
	}
	
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
        getValues();
	    //setContentView(setLayout()); 
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
    		/*if(canupdate)
    			confirmBack(event);
    		else{
    			Bundle extras = getIntent().getExtras();
            	getIntent().putExtras(extras);
                setResult(RESULT_OK, getIntent());
                finish();
    		}*/
    		
    		goHome();
    			
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
    
    @Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    super.onMenuItemSelected(featureId, item);
	    switch(item.getItemId()) {
	    case RESET_ID:
	    	resetData();
	        break;
	    case HOME:
			confirmHome();
			break;	
	    }
	        
	    return true;
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

    public void showAlert(String result){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.error) //"Error")
        .setMessage(result)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
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
    	//	Log.i(getClass().getSimpleName(), "TEXT VIEW "+t);
    	
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
    	//HashMap<String, Button> buttonhash = new HashMap<String, Button>();
    	spinnerselhash.clear();
    	
        thisspinnerhash = new LinkedHashMap<String, Spinner>();
    		    
	    RelativeLayout ll = new RelativeLayout(this);
	    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
		
	    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ); 
	       
	    ScrollView s = new ScrollView(this);
	    
	    ll.addView(s, linear1layout2);
	        
	    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    TableLayout l=new TableLayout(this);
	    
	    s.addView(l); 
	    
	    String[] tempstring;
	    Button addButton, viewButton, findButton;
	    RelativeLayout rl2;
	    RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    
	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
	    
	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    
	    //final boolean addingspinners = true;
	    int count = 0;
	    for(String key : keyshash.keySet()){
	    	count++;
	    	orderedtables.put(count, key);
	    	orderedtablesrev.put(key, count);
	    	
	    	//Log.i("TABLE SELECT KEY", key + " "+keyshash.get(key)+" DB = "+dbAccess.getSpinnerValues(key, keyshash.get(key)));
	    	
    		tv = new TextView(this);
        	tv.setText(key);
        	tv.setTextSize(18);
        	//tv.setWidth(100);
        	l.addView(tv, lp);
        	
    		spin = new Spinner(this);
    		l.addView(spin, lp);
    		thisspinnerhash.put(key, spin);
    		//tempstring = (dbAccess.getSpinnerValues(key)).split(",,"); // , keyshash.get(key)
    		
    		if(spinnershash.get(key) == null){
 	    		spinnershash.put(key, new ArrayList<String>());
 	    	}
    		
    		HashMap<String, String> tempkthash = dbAccess.getSpinnerValues(key);
    		
    		for(String skey : tempkthash.keySet()){
    			spinnershash.get(key).add(tempkthash.get(skey));
     		}
    		
    		
 	    	//for (int i = 0; i < tempstring.length; i++) {  
 	    	//	spinnershash.get(key).add(tempstring[i]);
 	    	//}
 	    	
 	    	spinnerselhash.put(key, "Select");
 	    	spin.setTag(key);
 	    	

 	    	rl2 = new RelativeLayout(this);
 	    // l.addView(addButton, lp);
 	    	
 	    	addButton = new Button(this);
 	    	addButton.setText(this.getResources().getString(R.string.add)+" "+key);
 	    	addButton.setTag(key);
 		    		    
 		   addButton.setOnClickListener(new View.OnClickListener() {
 		    	public void onClick(View view) {
 		    	 	
 		    		
 		    		addData((String)view.getTag());
 		    	 	
 		    	    }
 		    	});
 		   
 		   rl2.addView(addButton, rlp3);
 	    	
 		  viewButton = new Button(this);
 		  viewButton.setText(this.getResources().getString(R.string.view)+" "+key);
 		  viewButton.setTag(key);
		    		    
 		  viewButton.setOnClickListener(new View.OnClickListener() {
		    	public void onClick(View view) {
		    	 	viewData((String)view.getTag());
		    	    }
		    	});
 		  
 		 rl2.addView(viewButton, rlp4);
 		 
 		 findButton = new Button(this);
 		 findButton.setText(this.getResources().getString(R.string.find)+" "+key);
 		 findButton.setTag(key);
		    		    
 		 findButton.setOnClickListener(new View.OnClickListener() {
		   	public void onClick(View view) {
		   	 	findData((String)view.getTag());
		   	    }
		   	});
		 
		 rl2.addView(findButton, rlp5);
		 
		 l.addView(rl2);
 		 
    	}
	    
	    for(String key : thisspinnerhash.keySet()){
	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(key).setAdapter(aspnLocs);
	    
	    }
	    
	    for(String key : thisspinnerhash.keySet()){
	    	
	    	//Log.i("SPIN CHECK", key);
			
	    	spin = thisspinnerhash.get(key);
	    	spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
 	    	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
 	    	    	spinnerselhash.put((String)parent.getTag(), (String)parent.getItemAtPosition(pos));
 	    	        //Object item = parent.getItemAtPosition(pos);
 	    	    	if(pos > 0)
 	    	    		setNextSpinnner((String)parent.getTag(), (String)parent.getItemAtPosition(pos));
 	    	    }
 	    	    public void onNothingSelected(AdapterView<?> parent) {
 	    	    }
 	    	});
	    }
	
	    return ll;
	    
	 }

    private void addData(String table){
		//Log.i(getClass().getSimpleName(), "Add TAG "+table);
		
		int tablepos = orderedtablesrev.get(table) - 1;
		
		//Log.i("TABLE POS CHECK 1", table +" "+orderedtables.get(tablepos) + " VAL "+spinnerselhash.get(orderedtables.get(tablepos)));
		 
		if(tablepos > 0 && spinnerselhash.get(orderedtables.get(tablepos)).equalsIgnoreCase("Select")){
			showAlert(orderedtables.get(tablepos)+" "+this.getResources().getString(R.string.selected_first)); //must be selected first");
			return;
		}
		 
		Intent i = new Intent(this, EntryNote.class);
		
		i.putExtra("table", table);
		if(tablepos > 0){
			for(int pos = 0; pos <= tablepos; pos++){
				//i.putExtra("keytable_", keyshash.get(orderedtables.get(tablepos)));
				//i.putExtra("keyvalue_", spinnerselhash.get(orderedtables.get(tablepos)));
				i.putExtra(keyshash.get(orderedtables.get(pos)), spinnerselhash.get(orderedtables.get(pos)));
			}
		}
		//else{
		//	i.putExtra("keytable", "");
		//	i.putExtra("keyvalue", "");
		//}
	    
		//if(!changesynch && !remoteid.equalsIgnoreCase("0"))
	    //	i.putExtra("canupdate", 0);
	    //else
	    //	i.putExtra("canupdate", 1);
		i.putExtra("new", 1);
		
		for(String key : thisspinnerhash.keySet()){
			i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
			}
		
	    startActivityForResult(i, ACTIVITY_EDIT);
	 }

	 
	 private void setNextSpinnner(String table, String value){
			
		 String nexttable = orderedtables.get(orderedtablesrev.get(table) + 1);
		 
			if(spinnerselhash.get(nexttable) == null){
				//Log.i("SPIN CHECK ERROR", "IT IS ");
				return;
			}
			
			String tableid = keyshash.get(orderedtables.get(orderedtablesrev.get(table)));
			
			//Log.i("SPIN CHECK 2", nexttable);
			
			//String[] tempstring = (dbAccess.getNextSpinnerValues(nexttable, value).split(",,"));
    		//if(spinnershash.get(nexttable) == null){
 	    		spinnershash.put(nexttable, new ArrayList<String>());
 	    	//}
 	    		
 	    	LinkedHashMap<String, String> tempkthash = dbAccess.getNextSpinnerValues(nexttable, value);
 	    		
 	    	for(String skey : tempkthash.keySet()){
 	    		spinnershash.get(nexttable).add(tempkthash.get(skey));
  	    		}
 	    	//for (int i = 0; i < tempstring.length; i++) {  
 	    	//	//Log.i("SPIN CHECK 3", tempstring[i]);
 	    	//	spinnershash.get(nexttable).add(tempstring[i]);
 	    	//}
 	    	
 	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(nexttable));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(nexttable).setAdapter(aspnLocs); 
	    	
		 }
	 
	 	private void viewData(String table){
				 
			Intent i = new Intent(this, EntryNote.class);
			
			i.putExtra("table", table);
			i.putExtra("keytable", "");
			i.putExtra("keyvalue", "");
			i.putExtra("new", 0);
			
			int pos = thisspinnerhash.get(table).getSelectedItemPosition();
			if(pos == 0)
				return;
			
			i.putExtra("primary_key", (String)thisspinnerhash.get(table).getItemAtPosition(pos));
			
			for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
				}
			
		    startActivityForResult(i, ACTIVITY_EDIT);
		 }

	 
	 private void findData(String table){
		 Log.i(getClass().getSimpleName(), "Find TAG "+table);
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
	    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    	alertDialog.setTitle(R.string.confirm); //"Confirm");
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
}


