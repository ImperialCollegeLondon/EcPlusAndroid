package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import uk.ac.imperial.epi_collect2.maps.LocalMap;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
//import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener; 
import android.content.res.Configuration;
import android.graphics.Color;

public class ListRecords extends ListActivity implements Runnable{
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int ACTIVITY_MAP=3;
    //private static final String KEY_STORED = "stored";
    //private static final String KEY_ISSTORED = "isstored";
    //private static final String KEY_STORED = "stored";
    //private static final String KEY_LAT = "lat";
    //private static final String KEY_LON = "lon";
    //private static final String KEY_ALT = "alt";
    //private static final String KEY_ACC = "gpsacc";
    //private static final String KEY_PHOTO = "photo";
    //private static final String KEY_ID = "id";
    //private static final String KEY_SOURCE = "source";
    //private static final String KEY_REMOTEID = "remoteid";
    //private static final String KEY_DATE = "date";
    public static final String KEY_SYNCHRONIZED = "synch";
    private static final int INSERT_ID = 1; //Menu.FIRST;
    private static final int SYNCH_ID = 2; //Menu.FIRST + 2;
    //private static final int SYNCH_IMAGES_ID = 3;
    private static final int LOCAL_ID = 4;
    private static final int REMOTE_ID = 5;
    private static final int ALL_ID = 6;
    private static final int MAP_ID = 7; //Menu.FIRST + 3;
    private static final int DELSYNCH_ID = 8; //Menu.FIRST + 3;
    private static final int DELREMOTE_ID = 9;
    private static final int DELALL_ID = 10; //Menu.FIRST + 3;
    private static final int HOME = 11;
    private static final int HELP = 12;
    private static final int SELECT_TABLE = 13;
    private static final int RETURN = 14;
    private DBAccess dbAccess;
    private List<DBAccess.Row> rows; //, localrows;
    private ProgressDialog myProgressDialog = null; 
    private ArrayAdapter<String> notes;
    private Handler mHandler; 
   // private ButtonListener myOnClickListener1 = new ButtonListener();
   // private ButtonListener2 myOnClickListener2 = new ButtonListener2();
   // private ButtonListener3 myOnClickListener3 = new ButtonListener3();
    //private String sIMEI, project;
    //private static String[] textviews = new String[0];
    private static String[] spinners = new String[0];
    //private static String[] checkboxes = new String[0];
    private static Hashtable <String, String[]>spinnershash = new Hashtable <String, String[]>();
    //private static Vector<String> listfields, listspinners, listcheckboxes;
    private static String[] radios = new String[0];
    private static Hashtable <String, String[]>radioshash = new Hashtable <String, String[]>();
    private HashMap<String, Integer> restore_remote = new HashMap<String, Integer>(), restore_local = new HashMap<String, Integer>();  
   	private Button synchButton; //, helpButton;
   	private boolean allsynched = true, searching = false;
   	private String table, select_table="Null", select_table_key="Null", foreign_key="Null"; // , primary_key
   	private LinkedHashMap <String, String> spinhash = new LinkedHashMap<String, String>();
   	private Bundle extras;
   	private String query;
   	private Thread thread;
   	private TextView emptytv;
   	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
    	//try{
        super.onCreate(icicle);
            
        extras = getIntent().getExtras();
        query = extras.getString("query");
        
        if(query.equalsIgnoreCase("none") || query.equalsIgnoreCase("branch")){
        	setContentView(R.layout.records_list);
        	synchButton = (Button) findViewById(R.id.synch);
        }
        else{
        	setContentView(R.layout.records_list_search);
        	searching = true;
        }
        
	    //helpButton = (Button) findViewById(R.id.help);
        TextView labelText = (TextView) findViewById(R.id.labeltext);
        emptytv = (TextView) findViewById(R.id.empty);
	    	    
	    labelText.setText(R.string.record_list);
	    emptytv.setText(R.string.no_entries_available);
	    if(!searching)
	    	synchButton.setText(R.string.tap_to_synch);
	    
        dbAccess = new DBAccess(this); 
        dbAccess.open();
        
        super.setTitle("EpiCollect "+dbAccess.getProject());
                
        table = extras.getString("table");
              
        if(query.equalsIgnoreCase("none") || query.equalsIgnoreCase("branch")){
        	select_table = extras.getString("select_table");
        	select_table_key = extras.getString("select_table_key_column");
        	foreign_key = extras.getString("foreign_key");
               
        	if(!select_table_key.equalsIgnoreCase("Null"))
        		labelText.setText(table+ " "+this.getResources().getString(R.string.list_for)+" " + select_table + " - " +  dbAccess.getValueWithFKey(select_table, "ectitle", foreign_key));
        	else
        		labelText.setText(this.getResources().getString(R.string.full)+" "+ table+ " "+this.getResources().getString(R.string.list));
        }
        else{
        	labelText.setText(this.getResources().getString(R.string.record_search_results));
        }
        getValues(extras.getString("table"));
                
		fillData(table, foreign_key, query); // select_table_key, foreign_key, extras.getString("query")
        	
        mHandler = new Handler();
    	//}
    	//catch (NullPointerException npe){
    	//	Log.i(getClass().getSimpleName(), "DB ERROR "+ npe);  
    	//	//showAlert(npe +"No Entries In Database");
    	//}
    	
        // MAKE SPINHASH A STATIC OBJECT IN A SEPARATE CLASS
        // CAN THEN BE ACCESSED FROM ANYWHERE
        //dbAccess.open();
        if (extras != null){
        	dbAccess.open();
	    	for(String key : dbAccess.getKeys().keySet()){
	    		spinhash.put(key, extras.getString(key));
	    	}
	    }

        if(query.equalsIgnoreCase("none") || query.equalsIgnoreCase("branch")){
			setSynchButton();
    	
			final String no_entries = this.getResources().getString(R.string.no_entries_to_synch);
			synchButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					if(rows == null || rows.size() == 0){
						showAlert(no_entries); //"No Entries to Synchronise in Database");
						return;
					}
					synchroniseData();
				}	           
			});
        }
	    
	    //helpButton.setOnClickListener(new View.OnClickListener() {
	    //    public void onClick(View arg0) {
	    //    	showHelp();
	    //    }	           
	    //});
    }
    
    // Need this to prevent "no record found" message when back button pressed
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK){ // event.KEYCODE_BACK
    		dbAccess.close();
    		Intent i = getIntent();
			i.putExtras(extras);
			setResult(RESULT_OK, i);
	        finish();
    	}
    	// Menu button doesn't work by default on this view for some reason
    	if(keyCode == KeyEvent.KEYCODE_MENU)
    		openOptionsMenu();

        return true;
    } 
    
    private void fillData(String tab, String for_key, String query) { // String select_tab, String for_key, String query
    	
    	table = tab;
    	//select_table = select_tab;
    	foreign_key = for_key;
    	
    	allsynched = true;
        // We need a list of strings for the list items
    	List<String> items = new ArrayList<String>();

        // Get all of the rows from the database and create the item list
    	if(dbAccess == null){
    		//Log.i(getClass().getSimpleName(), "DB HERE 1");
    		dbAccess = new DBAccess(this); //, this);
            dbAccess.open();
    	}
    	
    	//Log.i("QUERY IS", query);
    	
    	if(query.equalsIgnoreCase("none") || query.equalsIgnoreCase("branch")){
    		rows = dbAccess.fetchAllRowsWithFKey(table, foreign_key); // select_table, foreign_key
    	}
    	else{
    		rows = dbAccess.fetchAllRowsWithQuery(table, query);
    		if(rows.size() == 0){
    			Intent i = getIntent();
    			i.putExtras(extras);
    			setResult(RESULT_CANCELED, i);
    			dbAccess.close();
    	        finish();
    		}
    		else if(rows.size() == 1){
    			Intent i = getIntent();
    			showDetails(0);
    			setResult(RESULT_OK, i);
    			dbAccess.close();
    			finish();
    		}    		
    	}
    	
        String listitem = "";
        
        if(rows.size() > 0)
        	emptytv.setText("");
        
        for (DBAccess.Row row : rows) {
        	listitem = getItem(row);  
            
            items.add(listitem);
        } 

        notes = new ArrayAdapter<String>(this, R.layout.records_row_details, items);
        setListAdapter(notes);
        
        if(query.equalsIgnoreCase("none") || query.equalsIgnoreCase("branch"))
        	setSynchButton();
    }
    
    public void updateData(){ //int type){
    	
    	notes.clear();
    	allsynched = true;
        // We need a list of strings for the list items
    	//List<String> items = new ArrayList<String>();

        // Get all of the rows from the database and create the item list
    	if(dbAccess == null){
    		//Log.i(getClass().getSimpleName(), "DB HERE 1");
    		dbAccess = new DBAccess(this); //, this);
            dbAccess.open();
    	}
    	
    	//Log.i("QUERY IS", query);
    	
   		rows = dbAccess.fetchAllRowsWithFKey(table, foreign_key); // select_table, foreign_key
    	
   		if(rows.size() > 0)
        	emptytv.setText("");
        
        //String listitem = "";
        int count = 0;
        for (DBAccess.Row row : rows) {
        	//listitem = getItem(row);  
            //items.add(listitem);
            notes.insert(getItem(row), count);
            count++;
        } 

        //notes = new ArrayAdapter<String>(this, R.layout.records_row_details, items);
        notes.notifyDataSetChanged();
        //setListAdapter(notes);
        
       	setSynchButton();
    } 
    
    private String getItem(DBAccess.Row row){
    	
    	String listitem = String.format("%4s ", row.stored); // row.rowId, row.stored);
    	    	
    	listitem += " " + row.ectitle;
    	
    	//String listitem = ""+row.rowId + " | " + row.stored;
    /*	if(row.stored.equalsIgnoreCase("N"))
    		allsynched = false;
        
        for(String key : textviews){
        	if(listfields.contains(key))
        		listitem += " " + row.datastrings.get(key)+" ";
        }
                   
        for(String key : spinners){
        	if(listspinners.contains(key))
        		listitem += " " + spinnershash.get(key)[row.spinners.get(key)]+" ";
        }
        
        for(String key : checkboxes){
        	if(listcheckboxes.contains(key)){
        		if(row.checkboxes.get(key))
        			listitem += " " + key + " = T ";
        		else
        			listitem += " " + key + " = F ";
        	}
        } */

       return listitem;
    }
    
    /**
     * Add stripes to the list view.
     */
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //menu.add(0, INSERT_ID, 0, R.string.menu_new_entry);    
        //menu.add(0, LOCAL_ID, 0, R.string.menu_listlocal);
        //menu.add(0, REMOTE_ID, 0, R.string.menu_listremote);
        //menu.add(0, ALL_ID, 0, R.string.menu_listall);
        //menu.add(0, MAP_ID, 0, R.string.menu_map);
        //menu.add(0, DELSYNCH_ID, 0, R.string.menu_delsynch);
        //menu.add(0, DELREMOTE_ID, 0, R.string.menu_delremote);
        //menu.add(0, DELALL_ID, 0, R.string.menu_delall);
        //menu.add(0, SYNCH_IMAGES_ID, 0, R.string.menu_photo_synch);
        //menu.add(0, SYNCH_ID, 0, R.string.menu_synch);
        menu.add(0, SELECT_TABLE, 0, R.string.menu_select_table);
        menu.add(0, HOME, 0, R.string.menu_home);
       	menu.add(0, RETURN, 0, R.string.menu_return);
        //menu.add(0, HELP, 0, R.string.menu_help);
        return true;
    } 
    
   /* @Override

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(0, SELECT_TABLE, 0, R.string.menu_select_table);
        menu.add(0, HOME, 0, R.string.menu_home);
        menu.add(0, HELP, 0, R.string.menu_help);
	    
		return super.onPrepareOptionsMenu(menu);

	}*/
  
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        switch(item.getItemId()) {
        case INSERT_ID:
            //createEntry();
            break;
        case SYNCH_ID:  
        	if(rows == null || rows.size() == 0){
        		showAlert(this.getResources().getString(R.string.no_local_entries)); //"No Local Entries In Database");
        		break;
        	}
        	synchroniseData(); 
        	//setSynchButton();
            break;
        case LOCAL_ID:  
        	//updateData(0);
        	//setSynchButton();
            break;
        case REMOTE_ID:  
        	//updateData(1);
        	//setSynchButton();
            break;
        case ALL_ID:  
        	//updateData(2);
        	//setSynchButton();
            break;
        case DELREMOTE_ID:  
        	/*try{
        		AlertDialog dialog = new AlertDialog.Builder(this).create();

        		dialog.setMessage("Delete All Remote Records?");
        		dialog.setButton("Yes", myOnClickListener3);
        		dialog.setButton2("No", myOnClickListener3);

        		dialog.show();
        	}
        	catch (NullPointerException npe){
        		//showAlert("No Remote Entries In Database");
        	} */
        	//setSynchButton();
            break;
        case MAP_ID:
	    	//showMap();
	        break;
        case DELSYNCH_ID:
        	/*try{
        		AlertDialog dialog = new AlertDialog.Builder(this).create();

        		dialog.setMessage("Delete Synchronized Records?");
        		dialog.setButton("Yes", myOnClickListener1);
        		dialog.setButton2("No", myOnClickListener1);

        		dialog.show();
        	}
        	catch (NullPointerException npe){
        		showAlert("No Local Entries In Database");
        	}*/
        	//setSynchButton();
	        break;
        case DELALL_ID:
        	/*try{
        		AlertDialog dialog = new AlertDialog.Builder(this).create();

        		dialog.setMessage("Delete All Records?");
        		dialog.setButton("Yes", myOnClickListener2);
        		dialog.setButton2("No", myOnClickListener2);

        		dialog.show();
        	}
        	catch (NullPointerException npe){
        		showAlert("No Entries In Database");
        	}*/
        	//setSynchButton();
	        break;
        case SELECT_TABLE:
        	Intent i = new Intent(this, TableSelect.class); //NewEntry
        	dbAccess.close();
	    	startActivity(i);
    		break;
        //case SYNCH_IMAGES_ID:
    	//	synchImages();
    	//	break;
        case HOME:
        	i = new Intent(this, Epi_collect.class);
        	dbAccess.close();
     	   	startActivity(i);
    		break;
        //case SYNCH_IMAGES_ID:
    	//	synchImages();
    	//	break;
        case RETURN:
        	dbAccess.close();
        	i = getIntent();
			i.putExtras(extras);
			setResult(RESULT_OK, i);
	        finish();
    		break;
        }
        
        return true;
    }

   /* class ButtonListener implements OnClickListener{
		public void onClick(DialogInterface dialog, int i) {
			switch (i) {
			case AlertDialog.BUTTON1:
			// Button1 is clicked. Do something 
				dbAccess.deleteSynchRows();
				
				mHandler.post(new Runnable() {
                    public void run() { 
                    	updateData(0); }
                  });
				
				showToast("Records deleted");
			break;
			case AlertDialog.BUTTON2:
			// Button2 is clicked. Do something 
			break;
			}
		}
      }
    
    class ButtonListener2 implements OnClickListener{
		public void onClick(DialogInterface dialog, int i) {
			switch (i) {
			case AlertDialog.BUTTON1:
			// Button1 is clicked. Do something 
				dbAccess.deleteAllRows();
				
				mHandler.post(new Runnable() {
                    public void run() { 
                    	updateData(0); }
                  });
				
				showToast("Records deleted");
			break;
			case AlertDialog.BUTTON2:
			// Button2 is clicked. Do something 
			break;
			}
		}
      }
    
    class ButtonListener3 implements OnClickListener{
		public void onClick(DialogInterface dialog, int i) {
			switch (i) {
			case AlertDialog.BUTTON1:
			// Button1 is clicked. Do something 
				dbAccess.deleteRemoteRows();
				
				mHandler.post(new Runnable() {
                    public void run() { 
                    	updateData(1); }
                  });
				
				showToast("Records deleted");
			break;
			case AlertDialog.BUTTON2:
			// Button2 is clicked. Do something 
			break;
			}
		}
      } */
    
    //private void showToast(String text){
    //	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    //}
    
    //private void createEntry() {
    //	Intent i = new Intent(this, NewEntry.class);
    //	startActivityForResult(i, ACTIVITY_CREATE);
    //}

    
    Builder ad;
    String result; // = this.getResources().getString(R.string.success); //"Success";
    private void synchroniseData(){ 
    	
    	result = this.getResources().getString(R.string.success);
    	
    	createCancelProgressDialog(this.getResources().getString(R.string.please_wait), this.getResources().getString(R.string.uploading_data), this.getResources().getString(R.string.cancel)); //"Please Wait...", "Uploading Data...", "Cancel");
    	
    	thread = new Thread(this);
    	thread.start();
    	
    /*	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	final String sIMEI = mTelephonyMgr.getDeviceId();
    	
    	
    	myProgressDialog = ProgressDialog.show(this,     
                "Please wait...", "Synchronizing Data...", true);

      new Thread() {
           public void run() {
        	   result = "Synchronisation Failed";
                try{
                	result = dbAccess.synchroniseAll(sIMEI);
                } catch (Exception e) {
                	Log.i(getClass().getSimpleName(), "ERROR: "+ e);
                }
                // Dismiss the Dialog
                myProgressDialog.dismiss();
                Looper.prepare();
                //showSynchAlert(result);
                
                if(result.equalsIgnoreCase("Success")){
     			   showAlert("Success", "Synchronisation successful");
     			   //synchButton.setText("All Data Synchronised");
     			   
     		   }
     		   else{
     			   showAlert("Error", result);
     			   //synchButton.setText("Synchronise");
     		   }
                //You can use threads but all the views, and all the views related APIs,
                //must be invoked from the main thread (also called UI thread.) To do
                //this from a background thread, you need to use a Handler. A Handler is
                //an object that will post messages back to the UI thread for you. You
                //can also use the various post() method from the View class as they
                //will use a Handler automatically. 
                //if(!images){
                	mHandler.post(new Runnable() {
                		public void run() {
                	    	setSynchButton();
                   			updateData(); 
                		}
                	});
                //}
                Looper.loop();
                Looper.myLooper().quit(); 
                
                 
           }
      }.start(); */

    }
    
    public void run(){
    	// ASUS Transformer doesn't have IMEI number so have to ensure it has a value or synchronisation fails
    	String sIMEI = "1";
    	   	
    	String email = "NA";
    	    	
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
     	
     	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
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
     	}
 
    	//myProgressDialog = ProgressDialog.show(this, "Please wait...", "Synchronizing Data...", true);
     
        	   result = this.getResources().getString(R.string.synch_failed); //"Synchronisation Failed";
                try{
                	if(dbAccess.getValue("epicollect_version").equalsIgnoreCase("2"))
             			result = dbAccess.synchroniseAll(sIMEI, email);	
             		else
             			result = dbAccess.synchroniseV1(sIMEI, email);	
                	//result = dbAccess.synchroniseAll(sIMEI, email);
                } catch (Exception e) {
                	Log.i(getClass().getSimpleName(), "SYNCH ERROR: "+ e);
                }
                // Dismiss the Dialog
                myProgressDialog.dismiss();
                Looper.prepare();
                //showSynchAlert(result);
                
                if(result.equalsIgnoreCase(this.getResources().getString(R.string.synch_success))){ //"Success")){
     			   showAlert(this.getResources().getString(R.string.success), this.getResources().getString(R.string.synch_success)); //"Success", "Synchronisation successful");
     			   //synchButton.setText("All Data Synchronised");
     			   
     		   }
     		   else{
     			   showAlert(this.getResources().getString(R.string.error), result);
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
                   			updateData(); 
                		}
                	});
                //}
                Looper.loop();
                Looper.myLooper().quit(); 
                
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
      
    public void showAlert(String result){
    	
    	new AlertDialog.Builder(this)
        .setTitle(R.string.completed) //"Completed")
        .setMessage(result)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 //updateData();
             }
        }).show();	
    	
    	
    }
    
    public void showMap() {
    	Intent i = new Intent(this, LocalMap.class);
    	//i.putExtra(KEY_EMAIL, email);
    	i.putExtra("overlay_local", restore_local);
   		i.putExtra("overlay_remote", restore_remote);
   		dbAccess.close();
    	startActivityForResult(i, ACTIVITY_MAP);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    		showDetails(position);
    		
    	/* Intent i = new Intent(this, NewEntry.class);
         i.putExtra(KEY_SOURCE, "list");
       // i.putExtra(KEY_ID, rows.get(position).rowId);
        i.putExtra(KEY_DATE, rows.get(position).date);
        //i.putExtra(KEY_STORED, rows.get(position).stored);
        i.putExtra(KEY_ISSTORED, 1);
        i.putExtra(KEY_STORED, rows.get(position).stored);
      //  i.putExtra(KEY_REMOTEID, rows.get(position).remoteId);
        
        for(String key : textviews){
        	i.putExtra(key, rows.get(position).datastrings.get(key));
        }
        
        for(String key : spinners){
    		i.putExtra(key, rows.get(position).spinners.get(key));
        }
        
        for(String key : checkboxes){
        	Log.i(getClass().getSimpleName(), "CHECKBOX: "+ key +" "+rows.get(position).checkboxes.get(key));  
    		i.putExtra(key, rows.get(position).checkboxes.get(key));
        }
        
        i.putExtra(KEY_LAT, rows.get(position).gpslat);
        i.putExtra(KEY_LON, rows.get(position).gpslon);
        i.putExtra(KEY_ALT, rows.get(position).gpsalt);
        i.putExtra(KEY_ACC, rows.get(position).gpsacc);
        
        try{
        	i.putExtra(KEY_PHOTO, rows.get(position).photoid);
        }
        catch(Exception e){
        	i.putExtra(KEY_PHOTO, "-1"); */
       	
    	  
    }
    
    private void showDetails(int position){
    	
    	Intent i = new Intent(this, DetailsView.class);
		
		i.putExtra("table", table);
		i.putExtra("keytable", "");
		i.putExtra("keyvalue", "");
		i.putExtra("select_table_key_column", select_table_key);
		i.putExtra("new", 0);
		if(query.equalsIgnoreCase("branch"))
			i.putExtra("frombranch", 1);
		else
			i.putExtra("frombranch", 0);
		//i.putExtra("select_table_key_column", select_table);
    	//i.putExtra("foreign_key", foreign_key);
		
		//String keyvalue = dbAccess.getKeyValue(table);
		i.putExtra("primary_key", rows.get(position).ecpkey); //datastrings.get(keyvalue));
		i.putExtra("status", rows.get(position).stored);
		
		for(String key : spinhash.keySet()){
			i.putExtra(key, spinhash.get(key));
			}
		
		dbAccess.close();
        startActivityForResult(i, ACTIVITY_EDIT); 
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
     	// Cascades the exit program call
	   	if (requestCode == 123) {
		 	   this.finish();
		 	   }
	   	
    	//Bundle extras = data.getExtras();
        switch(requestCode) {
        case ACTIVITY_CREATE:
            //fillData();
            break;
        case ACTIVITY_EDIT:
        	//if(query.equalsIgnoreCase("branch"))
        		updateData();
        	//fillData();
            break; 
        case ACTIVITY_MAP:
        	Bundle extras = null;
    	   	if(data != null)
    	   		extras = data.getExtras();
	    	if(extras != null){
	    		restore_local = (HashMap<String, Integer>) extras.get("overlay_local");
	    		restore_remote = (HashMap<String, Integer>) extras.get("overlay_remote"); 
	    	} 
	        break; 
        }            
    } 

 // To prevent crashes when screen orientation changes and onProgressDialog is showing.
    // Also, in manifest file have to addthe statement android:configChanges="keyboardHidden|orientation" for that activity.
    // so when your screen orientation is changed, it wont call the onCreate() method again.

    public void onConfigurationChanged(Configuration arg0)
         {
                 super.onConfigurationChanged(arg0);
         }

    private void getValues(String table){
    	
    	//if(dbAccess.getValue(table, "textviews") != null)
		//	textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
    	if(dbAccess.getValue(table, "spinners") != null)
    		spinners = (dbAccess.getValue(table, "spinners")).split(",,");
    	if(dbAccess.getValue(table, "radios") != null)
    		radios = (dbAccess.getValue(table, "radios")).split(",,");
    	//if(dbAccess.getValue(table, "checkboxes") != null)
    	//	checkboxes = (dbAccess.getValue(table, "checkboxes")).split(",,");
    	
    	//List <String>list;
    	
    	/*if(dbAccess.getValue(table, "listfields") != null && dbAccess.getValue(table, "listfields").length() > 0){
    		list = Arrays.asList(dbAccess.getValue(table, "listfields").split(",,")); //("\\s+"));
    		listfields = new Vector<String>(list);
    	}
        
    	if(dbAccess.getValue(table, "listspinners") != null && dbAccess.getValue(table, "listspinners").length() > 0){
    		list = Arrays.asList(dbAccess.getValue(table, "listspinners").split(",,")); //("\\s+"));
    		listspinners = new Vector<String>(list);
    	}
        
    	if(dbAccess.getValue(table, "listcheckboxes") != null && dbAccess.getValue(table, "listcheckboxes").length() > 0){
    		list = Arrays.asList(dbAccess.getValue(table, "listcheckboxes").split(",,")); //("\\s+"));
    		listcheckboxes = new Vector<String>(list);
    	}*/
    	
    	String[] tempstring;
        for(String key : spinners){       	
        	tempstring = dbAccess.getValue(table, "spinner_"+key).split(",,");
	    	spinnershash.put(key, tempstring);
        }  
        
        for(String key : radios){       	
        	tempstring = dbAccess.getValue(table, "radio_"+key).split(",,");
	    	radioshash.put(key, tempstring);
        }  
        
        //project = dbAccess.getProject();
        
    }
    
    private void setSynchButton(){
    	
    	// Can throw a null pointer exception after data uplaoded so ...
    	if(dbAccess == null){
    		dbAccess = new DBAccess(this);
            dbAccess.open();
    	}
    	
    	allsynched = dbAccess.checkSynchronised();
    	
    	if(allsynched){
        	synchButton.setTextColor(Color.WHITE);
        	synchButton.setText(R.string.no_data_to_send); //"No Data to Send to Server(s)");
        	synchButton.setEnabled(false);
        }
        else{
        	synchButton.setTextColor(Color.BLACK);
        	synchButton.setText(R.string.send_data); //"Send Data to Remote Server(s)");
        	synchButton.setEnabled(true);
        }
    	
    }
    
    private void showHelp() {
    	Intent i = new Intent(this, Help.class);
    	//i.putExtra("HELP_TEXT", "This is a pre-release candidate version of EpiCollect\n\nFor comments, bugs, queries and suggestions please visit:\n\nhttp://www.epicollect.net\n\nThis version is not for wider distribution");
    	
    	dbAccess.close();
    	startActivity(i);
    }
    
    /*private void synchImages(){
    	if(dbAccess.checkImages())
    		showAlert("Synchronize all entries before synchronizing images");
    	else
    		synchroniseData(true);
    	//showAlert(dbAccess.uploadAllImages());
	}*/
    
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
 		
		 outState.putString("table", table);
		 outState.putString("select_table", select_table);
		 outState.putString("select_table_key_column", select_table_key);
		 outState.putString("foreign_key", foreign_key);
    	
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
    	thread.stop();
    }
    
}
