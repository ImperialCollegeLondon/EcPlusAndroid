package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Vector;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
//import android.text.InputType;
//import android.telephony.TelephonyManager;
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

public class ListTables extends Activity {
 
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
	Button synchButton;
	private ProgressDialog myProgressDialog = null; 
	private Handler mHandler;
	private boolean allsynched = true;
	private String email;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        //super.setTitle(this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/app_name", null, null))); 
        
		dbAccess = new DBAccess(this);
	    dbAccess.open();
		
	    super.setTitle("EpiCollect+ "+dbAccess.getProject());
	    
	    getValues();
	    setContentView(setLayout()); 
	    
	    setSynchButton();
	    
	    mHandler = new Handler();
        
	    Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	if(thisspinnerhash != null){
        		for(String key : thisspinnerhash.keySet()){
        			thisspinnerhash.get(key).setSelection(extras.getInt(key));
        			//Log.i("SPINNER SETTING", "KEY "+key+" POS "+extras.getInt(key));
        			}
        		}
        	}
        
        final String possibleEmail="";
		Account[] accounts = AccountManager.get(this).getAccounts();
    	for (Account account : accounts) {
    	  if(account.type.equalsIgnoreCase("com.google") && possibleEmail.contains("@gmail.com")){
    		  email = account.name;
    		  continue;
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
	    Button listButton, listAllButton; //, findButton;
	    RelativeLayout rl2;
	    RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    RelativeLayout.LayoutParams rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    
	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
	    
	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    
	    synchButton = new Button(this);
	    if(dbAccess.checkSynchronised())
	    	synchButton.setText(R.string.all_synched); //"All Data Synchronised");
	    else
	    	synchButton.setText(R.string.synchronise); //"Synchronise");
	   		    		    
		synchButton.setOnClickListener(new View.OnClickListener() {
		   	public void onClick(View view) {
			    		
		   		synchroniseData();
		  	 	
		   	    }
		   	});
		   
		
		
		l.addView(synchButton, lp);
		
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
    		//tempstring = (dbAccess.getSpinnerValues(key)).split(",,");
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
 	    	
 	    	spinnerselhash.put(key, this.getResources().getString(R.string.select)); //"Select");
 	    	spin.setTag(key);
 	    	
 	    	rl2 = new RelativeLayout(this);
 	  	    // l.addView(addButton, lp);
 	    	
 	    	listAllButton = new Button(this);
 	    	listAllButton.setText(this.getResources().getString(R.string.list_all)+" - "+key);
 	    	listAllButton.setTag(key);
 		    		    
 	    	listAllButton.setOnClickListener(new View.OnClickListener() {
 		    	public void onClick(View view) {
 		    	 	 		    		
 		    		listAllRecords((String)view.getTag());
 		    	 	
 		    	    }
 		    	});
 		   
 		   rl2.addView(listAllButton, rlp3);
 	    	
 		  if(count > 1){
 			  listButton = new Button(this);
 			  listButton.setText(this.getResources().getString(R.string.list_selected)+" - "+key);
 			  listButton.setTag(key);
		    		    
 			  listButton.setOnClickListener(new View.OnClickListener() {
 				   public void onClick(View view) {
 					   listSelected((String)view.getTag());
		    	    	}
		    		});
 		  
 			   rl2.addView(listButton, rlp5);
 		   	}
 		 
		 
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

    private void listSelected(String table){
		
		int tablepos = orderedtablesrev.get(table) - 1;
		
		if(tablepos > 0 && spinnerselhash.get(orderedtables.get(tablepos)).equalsIgnoreCase("Select")){
			showAlert(orderedtables.get(tablepos)+" must be selected first", "Error");
			return;
		}
		 
		Intent i = new Intent(this, ListRecords.class);
		
		i.putExtra("table", table);
		i.putExtra("select_table_key_column", keyshash.get(orderedtables.get(tablepos)));
		i.putExtra("foreign_key", spinnerselhash.get(orderedtables.get(tablepos)));
		
		//if(tablepos > 0){
		//	for(int pos = 0; pos <= tablepos; pos++){
		//		i.putExtra(keyshash.get(orderedtables.get(pos)), spinnerselhash.get(orderedtables.get(pos)));
		//	}
		//}

		for(String key : thisspinnerhash.keySet()){
			i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
			}
		
		dbAccess.close();
	    startActivity(i);
	 }

	   private void listAllRecords(String table){
			 
			Intent i = new Intent(this, ListRecords.class);
			
			i.putExtra("table", table);
			i.putExtra("select_table_key_column", "Null");
			i.putExtra("foreign_key", "Null");
			
			//if(tablepos > 0){
			//	for(int pos = 0; pos <= tablepos; pos++){
			//		i.putExtra(keyshash.get(orderedtables.get(pos)), spinnerselhash.get(orderedtables.get(pos)));
			//	}
			//}

			for(String key : thisspinnerhash.keySet()){
				i.putExtra(key, thisspinnerhash.get(key).getSelectedItemPosition());
				}
			
			dbAccess.close();
		    startActivity(i);
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
			
			dbAccess.close();
			
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
	    	dbAccess.close();
	 	   	startActivity(i);
	    }
	   
	  /* private void synchroniseData(){
		   String result = dbAccess.synchroniseAll();
		   if(result.equalsIgnoreCase("Success")){
			   showAlert("Synchronisation successful", "Success");
			   synchButton.setText("All Data Synchronised");
			   
		   }
		   else{
			   showAlert(result, "Error");
			   synchButton.setText("Synchronise");
		   }
	    } */
	   

	   Builder ad;
	   String result = ""; //"Success";
	   // 
	    private void synchroniseData(){ 
	    	//TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			final String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
			
	    	
	    	myProgressDialog = ProgressDialog.show(this, this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.synch_failed)+"...", true);

	    	final String synch_failed = this.getResources().getString(R.string.synch_failed), synch_success = this.getResources().getString(R.string.synch_success),
	    			success = this.getResources().getString(R.string.synch_success), error = this.getResources().getString(R.string.error);
	    	new Thread() {
	           public void run() {
	        	   result = synch_failed; //"Synchronisation Failed";
	                try{
	                	result = dbAccess.synchroniseAll(android_id, email);
	                } catch (Exception e) {
	                	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
	                }
	                // Dismiss the Dialog
	                myProgressDialog.dismiss();
	                Looper.prepare();
	                //showSynchAlert(result);
	                
	                if(result.equalsIgnoreCase(success)){ //"Success")){
	     			   showAlert(success, synch_success); //"Synchronisation successful", "Success");
	     			   //synchButton.setText("All Data Synchronised");
	     			   
	     		   }
	     		   else{
	     			   showAlert(result, error); //"Error");
	     			   //synchButton.setText("Synchronise");
	     		   }
	                /*You can use threads but all the views, and all the views related APIs,
	                must be invoked from the main thread (also called UI thread.) To do
	                this from a background thread, you need to use a Handler. A Handler is
	                an object that will post messages back to the UI thread for you. You
	                can also use the various post() method from the View class as they
	                will use a Handler automatically. */
	                //if(!images){
	                	mHandler.post(new Runnable() {
	                		public void run() {
	                	    	setSynchButton();
	                   			//updateData(0); 
	                		}
	                	});
	                //}
	                Looper.loop();
	                Looper.myLooper().quit(); 
	                
	                 
	           }
	      }.start();

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

	      
	   /* public void showSynchAlert(String result){
	    	
	    	new AlertDialog.Builder(this)
	        .setTitle("Completed")
	        .setMessage(result)
	        .setNegativeButton("OK", new DialogInterface.OnClickListener() {

	             public void onClick(DialogInterface dialog, int whichButton) {
	            	 //updateData();
	             }
	        }).show();
	    	

	    	
	    	
	    } */
	    
	    private void setSynchButton(){
	    	
	    	allsynched = dbAccess.checkSynchronised();
	    	
	    	if(allsynched){
	        	synchButton.setTextColor(Color.WHITE);
	        	synchButton.setText("No Entries to Synchronise");
	        	synchButton.setEnabled(false);
	        }
	        else{
	        	synchButton.setTextColor(Color.BLACK);
	        	synchButton.setText("Tap to Synchronise Entries");
	        	synchButton.setEnabled(true);
	        }
	    	
	    }
	    

		   
}

