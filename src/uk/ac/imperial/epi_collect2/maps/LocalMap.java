package uk.ac.imperial.epi_collect2.maps;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.imperial.epi_collect2.DetailsView;
import uk.ac.imperial.epi_collect2.Epi_collect;
import uk.ac.imperial.epi_collect2.ListRecords;
//import uk.ac.imperial.epi_collect2.NewEntry;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import uk.ac.imperial.epi_collect2.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
//import com.google.android.maps.Overlay;

public class LocalMap extends MapActivity  implements LocationListener{
    private MapView mMapView;
    private static final int ACTIVITY_CREATE=1;
	private static final int ACTIVITY_LIST=2;
	private static final int ACTIVITY_DETAILS=3;
	private static final int LOCAL_ID = 1; //Menu.FIRST;
    private static final int REMOTE_ID = 2; //Menu.FIRST+1;
    private static final int UPDATE_REMOTE_ID = 3;
    private static final int REMOVE_LOCAL_ID = 4; //Menu.FIRST+1;
    private static final int REMOVE_REMOTE_ID = 5; //Menu.FIRST+1;
    private static final int FILTER_ID = 6; //Menu.FIRST+1;
    private static final int LIST_ID = 7; //Menu.FIRST+1;
    private static final int NEW_ID = 8; //Menu.FIRST+1;
    private static final int MY_LOCATION = 9;
    private static final int LOCATION = 10; //Menu.FIRST;
	private static final int COMPASS = 11; //Menu.FIRST;
	private static final int HOME = 12; //Menu.FIRST;
	private static final int SAT_ID = 13; //Menu.FIRST;
	private static final int MENU_LOC_ID = 14; //Menu.FIRST;
	private static final int MENU_REM_ID = 15; //Menu.FIRST;
   	private static final String KEY_SOURCE = "source";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
    //private static final String KEY_REMOTEID = "remoteid";
    //private static final String KEY_PHOTO = "photo";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_ALT = "alt";
    private static final String KEY_ISSTORED = "isstored";
    private static final String KEY_STORED = "stored";
    private DBAccess dbAccess;
    //public EditText selectText;
    //private LocalOverlay overlay;
    //private List <DBAccess.Row> local_rows;
    //private List <DBAccess.Row> remote_rows;
    private LocationManager locationManager; 
   	//private LocationProvider IP;
   	private ProgressDialog myProgressDialog = null; 
   	private static String[] textviews = new String[0];
    private static String[] spinners = new String[0];
    private static String[] checkboxes = new String[0];
   	private String xml_url; 
   	private Vector<String> selectvec = new Vector<String>();
   	private HashMap<String, String> selecthash = new HashMap<String, String>();
   	//private HashMap<String, Integer> overlayposhash = new HashMap<String, Integer>();
    private MyLocationOverlay myLocationOverlay;
    private MapController mapController;
    private boolean mylocation = true, mycompass = true;
    private Vector<String> doubles = new Vector<String>();
	private Vector<String> integers = new Vector<String>();
	private HashMap<String, Spinner> thisspinnerhash = new HashMap<String, Spinner>();
	final HashMap <String, ArrayList<String>>spinnershash = new HashMap <String, ArrayList<String>>();
	//private HashMap<String, Object>overlayhash = null;
	//private Bundle extras;
	private HashMap<String, Integer> restore_remote = new HashMap<String, Integer>(), restore_local = new HashMap<String, Integer>();  
	//private int restoring_remote = 0;
	private ViewFlipper f;
	private TextView tv, pagetv;
	private EditText et;
	private Spinner spin;
	private CheckBox cb;
	private HashMap<String, EditText> textviewhash;
	private HashMap<String, CheckBox> checkboxhash;
	private ArrayAdapter<String> aspnLocs; 
	private String[] allviews = new String[0];
	private int lastpage = 1;
	private HashMap<String, CheckBox> textcheckhash = new HashMap<String, CheckBox>();
	private HashMap<String, CheckBox> checkcheckhash = new HashMap<String, CheckBox>();
	private String table, pkey;
	private Bundle extras;
	boolean alertshowing = false;
	private boolean showlocal = false, showremote = false, remoteavail = false, localavail = false;
	//private Button localButton, remoteButton;
	private List<Overlay> mapOverlays;
	private MapItemizedOverlay localitems = null, remoteitems = null;
	Vector<GeoPoint> items = new Vector<GeoPoint>();
	
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle); 
        
        extras = getIntent().getExtras();
        table = extras.getString("table");
        
        dbAccess = new DBAccess(this);
        dbAccess.open();
        
        //xml_url = dbAccess.getValue("remote_xml");
        
        super.setTitle("EpiCollect+ "+dbAccess.getProject());
        
        getValues();

        setContentView(R.layout.map);
        
        mMapView = (MapView) findViewById(R.id.mapview); // new LocalMapView(this);
        
        //mMapView.setReticleDrawMode(MapView.ReticleDrawMode.DRAW_RETICLE_UNDER);

        // Added to enable point to be clicked on 
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(true);
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
       	//IP = locationManager.getProvider("gps");
       	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6, 0, this);
       	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, this);

       	mapController = mMapView.getController();
       	mapController.setZoom(8);

        //selectText = (EditText) findViewById(R.id.seltext);
        //selectText.setCursorVisible(false);
        //localButton = (Button) findViewById(R.id.localbut);
        //remoteButton = (Button) findViewById(R.id.remotebut);
        
        mapOverlays = mMapView.getOverlays();
                
        myLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        myLocationOverlay.enableMyLocation();       
        
        //restoring_remote = 0;
        /* Note the onRestoreInstanceState event returns an Object type, which pretty much allows 
         * you to return any data type. To extract the saved data, you can extract
         * it in the onCreate event, using the getLastNonConfigurationInstance() method, like this:
         */
        /*
        overlayhash = (HashMap<String, Object>)getLastNonConfigurationInstance();

        if(overlayhash != null){
        	List<Overlay> oldoverlays = (List<Overlay>)overlayhash.get("overlays");
        	overlayposhash = (HashMap<String, Integer>)overlayhash.get("overlayposhash");
        	// This is called when the screen is rotated to restore the overlays.
        	// The location overlay is automatically restored so don't want to add it 
        	// again, otherwise the hash positions of the overlays is wrong.
        	// Therefore, don't add first overlay - the location
        	int count = 0;
        	if(oldoverlays != null){
        		for(Overlay overlay : oldoverlays){
        			if(count != 0){
        				mMapView.getOverlays().add(overlay);
        				// Otherwise overlay loses map and text box cannot be set with the id
        				((LocalOverlay) overlay).setMap(this);
        			}
        			count++;
        		}
        	}
        	
        	if(overlayhash.get("local_rows") != null)
        		local_rows = (List<DBAccess.Row>)overlayhash.get("local_rows");
        	if(overlayhash.get("remote_rows") != null)
        		remote_rows = (List<DBAccess.Row>)overlayhash.get("remote_rows");
        	
        }
        else{
        	try{
        		restore_local = (HashMap<String, Integer>) extras.get("overlay_local");
        		if(restore_local.keySet().size() > 0){
        			showLocal(0);
        			Log.i(getClass().getSimpleName(), "IN LOCAL MAP RESTORE");
        		}
        		restore_remote = (HashMap<String, Integer>) extras.get("overlay_remote");
        		if(restore_remote.keySet().size() > 0){
        			restoring_remote = 0;
        			Log.i(getClass().getSimpleName(), "IN REMOTE MAP RESTORE");
        			showLocal(1);
        			//showWebResults();
        		}        		
        	}
        	catch(NullPointerException npe){
        		Log.i(getClass().getSimpleName(), "IN LOCAL MAP "+npe);
        	}       	
        } */
        	
        //mMapView.scrollBy(1, 0);
        //mMapView.scrollBy(-1, 0);
        //mMapView.refreshDrawableState();
        
        showEntries(0);
       	if(localitems != null){
       		localavail = true;
       		showlocal = true;
       	}
       	
        //if(dbAccess.checkValue(table, "ecstored", "R")){
           	showEntries(1);
           	if(remoteitems != null){
           		remoteavail = true;
           		showremote = true;
           	}
           	
           	centerZoom();
        	//remoteButon.setEnabled(false);
        	//remoteButton.setText("Hide Remote");
        //}
         
        //showEntries(0);
              
        //showlocal = true;
        //localButton.setText("Hide Local");
        
       /* myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	locationManager.getLastKnownLocation(IP.getName());
            	mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        }); */
        
        //localButton.setOnClickListener(new View.OnClickListener() {
	    //	public void onClick(View arg0) {
	    //		toggleLocal();
	    //    }         
	    //});
        
        //remoteButton.setOnClickListener(new View.OnClickListener() {
	    //	public void onClick(View arg0) {
	    //		//showEntries(1);
	    //		toggleRemote();         
	    //    }          
	    //});
    }

    
    private void toggleLocal(){
    	if(showlocal){
    		mapOverlays.remove(localitems);
			//removeLocalEntries();
			//localButton.setText("Show Local");
			showlocal = false;
		}
    	else{
    		mapOverlays.add(localitems);
    		//showEntries(0);
    		//localButton.setText("Hide Local");
			showlocal = true;
    	}
    	mMapView.getController().scrollBy(1, 1);
    	mMapView.getController().scrollBy(-1, -1);
    }
    
    private void toggleRemote(){
    	if(showremote){
    		//if(remoteitems != null)
    			mapOverlays.remove(remoteitems);
			//removeRemoteEntries();
			//remoteButton.setText("Show Remote");
			showremote = false;
		}
    	else{mapOverlays.add(remoteitems);
    		//showEntries(1);
    		//remoteButton.setText("Hide Remote");
			showremote = true;
    	}
    	mMapView.getController().scrollBy(1, 1);
    	mMapView.getController().scrollBy(-1, -1);

		
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public void showToast(String text){
    	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    
  /*  @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    menu.add(0, HOME, 0, R.string.menu_home);
	    menu.add(0, SAT_ID, 0, R.string.menu_sat_map);
	    //menu.add(0, LOCAL_ID, 0, R.string.menu_local);
	    //menu.add(0, REMOTE_ID, 0, R.string.menu_remote);
	    //menu.add(0, UPDATE_REMOTE_ID, 0, R.string.update_menu_remote);
	    //menu.add(0, FILTER_ID, 0, R.string.menu_filter);
	    //menu.add(0, REMOVE_LOCAL_ID, 0, R.string.menu_remove_local);
	    //menu.add(0, REMOVE_REMOTE_ID, 0, R.string.menu_remove_remote);
	    //menu.add(0, LIST_ID, 0, R.string.menu_list);
	    //menu.add(0, NEW_ID, 0, R.string.menu_new_entry);
	    menu.add(0, MY_LOCATION, 0, R.string.my_location);
	    menu.add(0, COMPASS, 0, R.string.menu_compass);
	    menu.add(0, LOCATION, 0, R.string.menu_location);
	    return true;
	}*/

	@Override

	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(0, HOME, 0, R.string.menu_home);
		
		if(localavail && showlocal)
			menu.add(0, MENU_LOC_ID, 0, R.string.menu_hide_local);
		else if(localavail && !showlocal)
			menu.add(0, MENU_LOC_ID, 0, R.string.menu_show_local);
		if(remoteavail && showremote)
			menu.add(0, MENU_REM_ID, 0, R.string.menu_hide_remote);
        else if(remoteavail && !showremote)
        	menu.add(0, MENU_REM_ID, 0, R.string.menu_show_remote);
	    menu.add(0, SAT_ID, 0, R.string.menu_sat_map);
	    menu.add(0, MY_LOCATION, 0, R.string.my_location);
	    menu.add(0, COMPASS, 0, R.string.menu_compass);
	    menu.add(0, LOCATION, 0, R.string.menu_location);
	    
		return super.onPrepareOptionsMenu(menu);

	}
	
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        switch(item.getItemId()) {
        case SAT_ID:
	    	toggleSatMap();
	        break;
        //case NEW_ID:
        //    createEntry();
        //    break;
        //case LIST_ID:
	    //	listRecords();
	    //    break;
        //case LOCAL_ID:
        //	showEntries(0);
	    //    break;
        //case REMOTE_ID:
        //	showEntries(1); 
	    //    break;
        //case UPDATE_REMOTE_ID:
        //	showWebResults(); 
	    //    break;
        //case REMOVE_LOCAL_ID:
        //	removeLocalEntries();
	    //    break;
        //case REMOVE_REMOTE_ID:
        //	removeRemoteEntries();
	    //    break;
        //case FILTER_ID:
        //	filterEntries();
	    //    break;
        case MENU_LOC_ID:
        	toggleLocal(); 
	        break;
        case MENU_REM_ID:
        	toggleRemote(); 
	        break;
        case MY_LOCATION:
        	try{
        		mapController.animateTo(myLocationOverlay.getMyLocation());
        	}
        	catch(NullPointerException npe){
        		showAlert(this.getResources().getString(R.string.awaiting_gps));
        	}
        case COMPASS:
        	toggleCompass(); 
	        break;
        case LOCATION:
        	toggleLocation(); 
	        break;
        case HOME:
        	locationManager.removeUpdates(this);
        	dbAccess.close();
        	Intent i = new Intent(this, Epi_collect.class);
     	   	startActivity(i);
    		break;	
        }
	    return true;
    }
    
    private void toggleSatMap(){
    	if(mMapView.isSatellite())
			mMapView.setSatellite(false);
		else
			mMapView.setSatellite(true);
    }
    
    private void toggleCompass(){
    	if(mycompass){
    		myLocationOverlay.disableCompass();
    		mycompass = false;
    	}
    	else{
    		myLocationOverlay.enableCompass();
    		mycompass = true;
    	}
    	mMapView.scrollBy(1, 0);
        mMapView.scrollBy(-1, 0);
    	mMapView.refreshDrawableState();
    }
    
    private void toggleLocation(){
    	if(mylocation){
    		myLocationOverlay.disableMyLocation();
    		mylocation = false;
    	}
    	else{
    		myLocationOverlay.enableMyLocation();
    		mylocation = true;
    	}
    	mMapView.scrollBy(1, 0);
        mMapView.scrollBy(-1, 0);
    	mMapView.refreshDrawableState();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		locationManager.removeUpdates(this);
        	returnMapData();
    	}
        return false;
    }
    
    private void returnMapData(){
    	//Intent i = getIntent();  	
    	//i.putExtra("overlay_local", restore_local); 
    	//i.putExtra("overlay_remote", restore_remote);
    	
		setResult(RESULT_OK); //, i);
		dbAccess.close();
		finish();
    }

    public MapView getMMapView() {
        return mMapView;
    }

   /* private void createEntry() {
    	Intent i = new Intent(this, NewEntry.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }*/
    
    public void listRecords() {
    	Intent i = new Intent(this, ListRecords.class);
    	startActivityForResult(i, ACTIVITY_LIST);
    }
    
    private void removeLocalEntries(){
    	//if(overlayposhash.get("local") != null)
        //	mMapView.getOverlays().remove(overlayposhash.get("local")-1);
    	
    	//overlayposhash.remove("local");
    	//mMapView.scrollBy(1, 0);
        //mMapView.scrollBy(-1, 0);
        //mMapView.refreshDrawableState();
    }
    
    private void removeRemoteEntries(){
    	//if(overlayposhash.get("remote") != null)
        //	mMapView.getOverlays().remove(overlayposhash.get("remote")-1);
        
    	//overlayposhash.remove("remote");
        //mMapView.scrollBy(1, 0);
        //mMapView.scrollBy(-1, 0);
        //mMapView.refreshDrawableState();
    }
      
   /* private void filterEntries(){
    	        
    	selecthash.clear();
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);  
        	
    	alert.setTitle("Select Fields");  
    	alert.setView(setLayout()); //filterView);    	   
    	     	        
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {  
    	public void onClick(DialogInterface dialog, int whichButton) {
    	      		  
    		for(String key : textviews){
    			if(textcheckhash.get(key).isChecked())
    				selecthash.put(key, textviewhash.get(key).getText().toString());
    			}
    			for(String key : spinners){
    				int index = thisspinnerhash.get(key).getSelectedItemPosition();
    	  			if(index > 0)
    	  				selecthash.put(key, ""+index); //spinnershash.get(key).get(index));
    			  	}
        	    for(String key : checkboxes){
        	    	if(checkcheckhash.get(key).isChecked()){
        	    		if(checkboxhash.get(key).isChecked())
        	   				selecthash.put(key, "1");
    	  				else
    	  					selecthash.put(key, "0");
        	   		 }
        	   	 } 
        	   	 filterData();
    		  
    	  }
    	  });  
    	    
    	  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
    	    public void onClick(DialogInterface dialog, int whichButton) {  
    	      // Canceled.  
    	    }  
    	  });  
    	     
    	  alert.show();  
    	
    }

    private void filterData(){
    	    	
    	if(local_rows != null && local_rows.size() > 0){
    		List <DBAccess.Row>filter_local = filterRows(local_rows);
    		
    		restore_local.clear();
    		//for(DBAccess.Row row: filter_local)
    		//	restore_local.put(row.remoteId, 1);
    		
    		overlay = new LocalOverlay(this, filter_local, 1, dbAccess);
            if(overlayposhash.get("local") != null)
            	mMapView.getOverlays().remove(overlayposhash.get("local")-1);
            mMapView.getOverlays().add(overlay); 
            overlayposhash.put("local", mMapView.getOverlays().size());
    	}
    	
    	if(remote_rows != null && remote_rows.size() > 0){
    		List <DBAccess.Row>filter_remote = filterRows(remote_rows);
    	
    		restore_remote.clear();
    		//for(DBAccess.Row row: filter_remote)
    		//	restore_remote.put(row.remoteId, 1);
    		
    		overlay = new LocalOverlay(this, filter_remote, 2, dbAccess);
    		if(overlayposhash.get("remote") != null)
    			mMapView.getOverlays().remove(overlayposhash.get("remote")-1);
    		mMapView.getOverlays().add(overlay); //createOverlayController().add(overlay, true);
    		overlayposhash.put("remote", mMapView.getOverlays().size());    		
    	}
        
        mMapView.scrollBy(1, 0);
        mMapView.scrollBy(-1, 0);
        mMapView.refreshDrawableState();

    }
    
    private ArrayList<DBAccess.Row> filterRows(List<DBAccess.Row> tofilter){
    	
    	ArrayList<DBAccess.Row> filtered_rows = new ArrayList<DBAccess.Row>();
    	
    	boolean store;
    	for(DBAccess.Row row : tofilter){
    		store = true;
    		for(String key : textviews){
    			if(selecthash.get(key) == null)
    				continue;
    			if(row.datastrings.get(key) == null || row.datastrings.get(key).length() == 0)
    				store = false;
    			else if(integers.contains(key) && !checkNumber(key, row))
    				store = false;
    			else if(doubles.contains(key) && !checkNumber(key, row))
    				store = false;
    			else if(!selecthash.get(key).equalsIgnoreCase(row.datastrings.get(key)))
    				store = false;
    		}
    	
    		for(String key : spinners){
    			if(selecthash.get(key) == null)
    				continue;
    			if(row.spinners.get(key) == null || selecthash.get(key).equalsIgnoreCase("0")){
    				store = false;
    			}
    			else if(Integer.parseInt(selecthash.get(key)) != row.spinners.get(key)){
    				store = false;
    			}
    		}
    		
    		for(String key : checkboxes){
    			if(selecthash.get(key) == null)
    				continue;
    			if(row.checkboxes.get(key) == null)
    				store = false;
    			else if(Integer.parseInt(selecthash.get(key)) == 1 && !row.checkboxes.get(key) ||
    					Integer.parseInt(selecthash.get(key)) == 0 && row.checkboxes.get(key))
    				store = false;
    	   	 }
    		
    		if(store){
    			filtered_rows.add(row);
    		}
    	}
    	
    	return filtered_rows;
    }
    
    private boolean checkNumber(String key, DBAccess.Row row){
    	
    	float greater, less, match;
        
        String s = selecthash.get(key).replaceAll("\\s+", "");
        
     // Check for presence of non-digits
        String s2 = s.replaceAll("\\d+\\.*\\d*", "");
        s2 = s2.replaceAll("<", "");
        s2 = s2.replaceAll(">", "");
        s2 = s2.replaceAll("=", "");
        if(s2.length() > 0){
        	showAlert("Error with value for "+key);
        	return false;
        	}
        
        Pattern pattern;
        Matcher matcher;
        
        boolean isgreater = false, isless = false;
        float value = Float.parseFloat(row.datastrings.get(key));
        
        try{
            pattern = Pattern.compile(">=*(\\d+\\.*\\d*)");
            matcher = pattern.matcher(s);
            if (matcher.find()) {
              greater = Float.parseFloat(matcher.group(1));
              if(s.contains(">=") && greater < value)
            	  return false;
              else if(greater <= value)
            	  return false;
              isgreater = true;
            }
            pattern = Pattern.compile("<=*(\\d+\\.*\\d*)");
            matcher = pattern.matcher(s);
            if (matcher.find()) {
              less = Float.parseFloat(matcher.group(1));
              if(s.contains("<=") && less > value)
            	  return false;
              else if(less >= value)
            	  return false;
              isless = true;
            }
            if (!isgreater && !isless){
              match = Float.parseFloat(s);
              if(match != value)
            	  return false;
            }
          }
          catch(NumberFormatException npe){
            Log.i("LocalMap", "Error with number");
          }
        
    	return true;
    } */
        
    public void showEntries (int remote){
    	
    	List <DBAccess.Row> rows = null;
    	//if(remote == 0)
    		rows = dbAccess.fetchAllGPSRows(table, remote);
    	//else
    	//	rows = dbAccess.fetchAllGPSRows(table, 1); 

    		if(rows.size() == 0)
    			return;
    		
    		String[] tempstring = null;
    		Vector<String> gpstags = new Vector<String>();
        	if(dbAccess.getValue(table, "gps") != null && dbAccess.getValue(table, "gps").length() > 0){
        		tempstring = (dbAccess.getValue(table, "gps")).split(",,");
        		for(String key : tempstring){     
        			gpstags.addElement(key);
        		}
        		
        	}
    		
        	GeoPoint geoPoint;
            Point screenCoords;
            Drawable pin;
            String title;
            items.clear();
            
            if(remote == 1){
				pin = this.getResources().getDrawable(R.drawable.blue);
				title = this.getResources().getString(R.string.remote); //"Remote";
			}
			else{
				pin = this.getResources().getDrawable(R.drawable.red); // androidmarker); //
				title = this.getResources().getString(R.string.local); //"Local";
			}
			MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(pin, this);
			
    		for (DBAccess.Row row : rows) {
            	//count++;
    			
            	for(String key : gpstags){
            		
            		try{
            			if((int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6) != 0 && (int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6) != 0){
            				geoPoint = new GeoPoint((int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6), (int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6));
            				//Log.i("MAP OVERLAY", (int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6)+" "+(int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6));
            				//MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(pin, this);
            		
            				OverlayItem overlayitem = new OverlayItem(geoPoint, title, row.ectitle);  
            				itemizedoverlay.addOverlay(overlayitem);
            				items.addElement(geoPoint);
            			}
            			
            			//itemizedoverlay.addOverlay(new OverlayItem(new GeoPoint((int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6), (int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6)), "Local", row.title));

            			//mapLocations.add(new MapLocation(row.title, row.pkey, row.phonekey, geoPoint, +(int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6),(int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6)));
            	
            		//geoPoint = new GeoPoint((int)(Double.parseDouble(row.datastrings.get(key+"_lat"))*1000000), (int)(Double.parseDouble(row.datastrings.get(key+"_lon"))*1000000));
            		//	screenCoords = new Point();
            		//	mapView.getProjection().toPixels(geoPoint, screenCoords);
                
            		//	canvas.drawBitmap(pin, screenCoords.x - 16, screenCoords.y - 32, null);
            		}
            	catch(NumberFormatException nfe){
            		//Log.i("GPS KEY", row.datastrings.get(key+"_lat"));
            	}
            	}
            	
    		}
            	
    		mapOverlays.add(itemizedoverlay);
        	if(remote == 1)
        		remoteitems = itemizedoverlay;
        	else
        		localitems = itemizedoverlay;
        	
        	
            	
    		//overlay = new LocalOverlay(this, local_rows, 1, dbAccess, table);
    		//if(overlayposhash.get("local") != null)
    		//	mMapView.getOverlays().remove(overlayposhash.get("local"));
    		//mMapView.getOverlays().add(overlay); 
    		//overlayposhash.put("local", mMapView.getOverlays().size());
    	//}
    	//else{
    		
    		/*remote_rows = dbAccess.fetchAllGPSRows(table, 1); //dbAccess.fetchAllRows(1); 
           	
    		overlay = new LocalOverlay(this, remote_rows, 2, dbAccess, table);
    		if(overlayposhash.get("remote") != null)
    			mMapView.getOverlays().remove(overlayposhash.get("remote"));
    		mMapView.getOverlays().add(overlay); 
    		overlayposhash.put("remote", mMapView.getOverlays().size());
    		*/
    	//}
        
	
        //mMapView.scrollBy(1, 0);
        //mMapView.scrollBy(-1, 0);
        //mMapView.refreshDrawableState();
    }
    
    
    /*Builder ad;
    String result = "Success";
    private void showWebResults(){
    	
    	selectvec.clear();
   		launchFetchXML();
    		
    }*/
        
   /* private void launchFetchXML(){
    	new FetchXML().execute(xml_url);
		myProgressDialog = ProgressDialog.show(this, "Please wait...", "Loading Data...", true);
    }
    
    @SuppressWarnings("unchecked")
	private class FetchXML extends AsyncTask<String, Integer, List> {
        protected List doInBackground(String... urls) {
        	//Location gpslocation = locationManager.getLastKnownLocation(IP.getName());
  		  	//selecthash.put("Latitude", Double.toString(gpslocation.getLatitude())); 
  		  	//selecthash.put("Longitude", Double.toString(gpslocation.getLongitude()));

  		  	return dbAccess.fetchXML(urls[0], selectvec); //, Double.toString(gpslocation.getLatitude()), Double.toString(gpslocation.getLongitude()));
        }

        // Can't use "List<DBAccess.Row> therows" as then this method is never called and 
        // download never completes. Therefore have to cast objects in for methods
        protected void onPostExecute(List therows) {
        	//Log.i(getClass().getSimpleName(), "XML onPostExecute: IN");
        	myProgressDialog.dismiss();
        	if(therows == null){
        		showAlert("Failed", "Record Retrieval Failed");
        	}        		
        	else{
        		showAlert("Completed", "Records Loaded");
        		//if(restoring_remote == 0){
        		//	restore_remote.clear();
        		//	for(Object row: therows)
        		//		restore_remote.put(((DBAccess.Row)row).remoteId, 1);
        		//}
        		//else{
        		//	for(Object row: therows)
            	//		if(restore_remote.get(((DBAccess.Row)row).remoteId) == null)
            	//			therows.remove((DBAccess.Row)row);
        		//}
        		/restoring_remote = 0;
        		//
        		//// Don't want to show rows that are also stored locally but have been synchronized
        		//DBAccess.Row thisrow;
        		//for(int i = 0; i < therows.size(); i++){
        		//	thisrow = (DBAccess.Row)therows.get(i);
        		//	if(dbAccess.checkremoteID(thisrow.remoteId)){
        		//		//Log.i(getClass().getSimpleName(), "DELETING ONE");
        		//		therows.remove(i);
        		//		i--;
        		//	}
        		//}
        		//setRemoteOverlay(therows);
        		showEntries(1);
        	}
        }
    } */
    
    public void showAlert(String title, String result){
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
    
    public void showAlert(String title, String result, String key){
    	pkey = key;
    	
    	new AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(result)
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {
            	 alertshowing = false;
             }
        })
        .setPositiveButton(R.string.details, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
            	showDetails(pkey);
            }
       }).show();	
    }
    
    private void showDetails(String pkey){
    	
    	Intent i = new Intent(this, DetailsView.class);
 		
		i.putExtras(extras);
		i.putExtra("keytable", "");
		i.putExtra("keyvalue", "");
		i.putExtra("new", 0);
		i.putExtra("fromlist", 0);
		
		i.putExtra("primary_key", pkey);
		
		startActivity(i);

    }
    
    /*@SuppressWarnings("unchecked")
	private void setRemoteOverlay(List therows){

    	remote_rows = therows;
    	overlay = new LocalOverlay(this, remote_rows, 2, dbAccess);
    	if(overlayposhash.get("remote") != null)
        	mMapView.getOverlays().remove(overlayposhash.get("remote"));
    	mMapView.getOverlays().add(overlay); 
        overlayposhash.put("remote", mMapView.getOverlays().size());
    	        
        mMapView.scrollBy(1, 0);
        mMapView.scrollBy(-1, 0);
        mMapView.refreshDrawableState();
    } */
    
    private void showDetails(){
    /*	Long thisid;
    	//try{
    	//	thisid = Long.parseLong(selectText.getText().toString());
    	//}
    	//catch (NullPointerException npe){
    	//	return;
    	//}
    	//catch(NumberFormatException npe){
    	//	showAlert("Error", "ID must be an integer");
    	//	return;
    	//}
    	
    	//if(selectText.getText() == null)
    	//	return;
    	
    	//Intent i = new Intent(this, NewEntry.class);
    	DBAccess.Row row = null;
    	if(local_rows == null && remote_rows == null){
    		showAlert("Error", "ID not found");
    		return;
    	}
    	
    	if(local_rows != null){
    		for (DBAccess.Row thisrow : local_rows) {
    		//	if(thisrow.rowId == thisid){
    		//		row = thisrow;
    		//		continue;
    		//	}
    		}
    	}
    	
    	if(row == null && remote_rows != null){
    		for (DBAccess.Row thisrow : remote_rows) {
    			//if(thisrow.rowId == thisid){
    			//	row = thisrow;
    			//	continue;
    			//}
    		}
    	}
    	
    	if(row == null){
    		showAlert("Error", "ID not found");
    		return;
    	} */
    	/*i.putExtra(KEY_ISSTORED, 1);
    	i.putExtra(KEY_STORED, row.stored);
    	
    	if(!row.remote){
    		i.putExtra(KEY_SOURCE, "map_local");
    		i.putExtra(KEY_ISSTORED, 1);
    	}
    	else{
    		i.putExtra(KEY_SOURCE, "map_remote");
     	}
        i.putExtra(KEY_ID, thisid); //row.rowId);
        i.putExtra(KEY_DATE, row.date);
        //i.putExtra(KEY_REMOTEID, row.remoteId);

        for(String key : textviews){
        	i.putExtra(key, row.datastrings.get(key));
        }
        
        for(String key : spinners){
        	i.putExtra(key, row.spinners.get(key));
        }
        
        for(String key : checkboxes){
        	i.putExtra(key, row.checkboxes.get(key));
        }
        
        i.putExtra(KEY_LAT, row.gpslat);
        i.putExtra(KEY_LON, row.gpslon);
        i.putExtra(KEY_ALT, row.gpsalt);
       // try{
       // 	i.putExtra(KEY_PHOTO, row.photoid);
       // }
      //  catch(Exception e){
       // 	i.putExtra(KEY_PHOTO, -1);
       // }
        
        startActivityForResult(i, ACTIVITY_DETAILS); */
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        //myLocationOverlay.disableCompass();
        //myLocationOverlay.disableMyLocation();
        super.onPause();
        dbAccess.close();
        dbAccess = null;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
       	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6, 0, this);
       	//myLocationOverlay.enableCompass();
        //myLocationOverlay.enableMyLocation();
        if (dbAccess == null) {
        	dbAccess = new DBAccess(this);
        	dbAccess.open();
        	}
    }
    
    public void showAlert(String result){
    	new AlertDialog.Builder(this)
        .setTitle("Error")
        .setMessage(result)
        .setNegativeButton("OK", new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
    
    //@Override
    public void onLocationChanged(Location loc) {
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
    public void onStatusChanged(String provider, int status, 
        Bundle extras) {
        // TODO Auto-generated method stub
    } 
    
    @Override  
    protected void onSaveInstanceState(Bundle outState) {  
    	dbAccess.close();
    	super.onSaveInstanceState(outState);  
    	
    }  
    
    /*Another event handler that you can use is the onRetainNonConfigurationInstance event. 
     * This event is fired when an activity is about to be destroyed due to a configuration change. 
     * (Screen orientation changes are considered configuration changes, and by default, all 
     * configuration changes cause the current activity to be destroyed). 
     * You can save your current data structure by returning it in this event, like this:(non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */

    /*  @Override  
     public Object onRetainNonConfigurationInstance() 
     {   
        	HashMap<String, Object> overlaydata = new HashMap<String, Object>();
        	overlaydata.put("overlays", mMapView.getOverlays());
        	overlaydata.put("overlayposhash", overlayposhash);
        	if(local_rows != null)
        		overlaydata.put("local_rows", local_rows);
        	if(remote_rows != null)
        		overlaydata.put("remote_rows", remote_rows);
        	
            return(overlaydata);   
     } */

     // To prevent crashes when screen orientation changes and onProgressDialog is showing.
     // Also, in manifest file have to addthe statement android:configChanges="keyboardHidden|orientation" for that activity.
     // so when your screen orientation is changed, it wont call the onCreate() method again.

     public void onConfigurationChanged(Configuration arg0)
          {
                  super.onConfigurationChanged(arg0);
          }

   	private void getValues(){
    	
    	if(dbAccess.getValue("textviews") != null)
     		textviews = (dbAccess.getValue("textviews")).split(",,"); // "CNTD", 
     	if(dbAccess.getValue("spinners") != null)
     		spinners = (dbAccess.getValue("spinners")).split(",,");
     	if(dbAccess.getValue("checkboxes") != null)
     		checkboxes = (dbAccess.getValue("checkboxes")).split(",,");
     	   	
     	for(String key : (dbAccess.getValue("doubles")).split(",,")){
     	   	doubles.addElement(key);
     	    }
     	        
     	for(String key : (dbAccess.getValue("integers")).split(",,")){
     	  	integers.addElement(key);
     	   }
     	              
     }
   	
   	private void centerZoom(){
   		int minLat = Integer.MAX_VALUE;
   		int maxLat = Integer.MIN_VALUE;
   		int minLon = Integer.MAX_VALUE;
   		int maxLon = Integer.MIN_VALUE;

   		for (GeoPoint item : items) 
   		{ 

   		      int lat = item.getLatitudeE6();
   		      int lon = item.getLongitudeE6();

   		      maxLat = Math.max(lat, maxLat);
   		      minLat = Math.min(lat, minLat);
   		      maxLon = Math.max(lon, maxLon);
   		      minLon = Math.min(lon, minLon);
   		 }

   		mapController.zoomToSpan(Math.abs(maxLat - minLat), Math.abs(maxLon - minLon));
   		mapController.animateTo(new GeoPoint( (maxLat + minLat)/2, 
   		(maxLon + minLon)/2 )); 
   	}
   	
   /* private RelativeLayout setLayout(){
        
    	textviewhash = new HashMap<String, EditText>();
        thisspinnerhash = new HashMap<String, Spinner>();
        checkboxhash = new HashMap<String, CheckBox>();
    	
	    String views =  dbAccess.getValue("notes_layout");// parser.getValues();
	      
	    f = new ViewFlipper(this);
	    
	    RelativeLayout ll = new RelativeLayout(this);
	    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	    
	 // Calculate last page
	    int count = 0, totalcount = 0;
	    String[] viewvalues;
	    
	    views.replaceFirst(",,,", "");
	    allviews = views.split(",,,");
	    
	    for(String thisview : allviews){
	    	viewvalues = thisview.split(",,");
	    	if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("select1")){
	    		count++;
	    		totalcount++;
	    	}
	    	boolean incremented = false;
	    	if(viewvalues[0].equalsIgnoreCase("select")){
	    		for(int i = 3; i < viewvalues.length; i++){
	    			if(!incremented || count % 3 != 0){
	    				count++;
	        			totalcount++;
	    			}
	    			//Log.i(getClass().getSimpleName(), "CHECK COUNT "+count);
	    			incremented = true; // Ensure count incremented at least once
	        		i++; // The viewvalues now contains the label and the value for each checkbox
	    		}
	    	}
	    	if(count > 3){
	    		lastpage++;
	    		count= 0;
	    	}
	    }
	    
	    //for(String thisview : allviews){
	    //	viewvalues = thisview.split(",,");
	    //	if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("select1")){
	    //		count++;
	    //		totalcount++;
	    //	}
	    //	if(viewvalues[0].equalsIgnoreCase("select"))
	    //		for(int i = 3; i < viewvalues.length; i++){
	     //   		count++;
	     //   		totalcount++;
	    //		}
	    //	if(count >= 3){
	    //		lastpage++;
	    //		count= 0;
	    //	}
	    //}
	
	    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
	       
	    ll.addView(f, linear1layout2);
	    
	    //ScrollView.LayoutParams sp = new ScrollView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	    
	    ScrollView s = new ScrollView(this);
	    
	    f.addView(s);
	    
	    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    TableLayout l=new TableLayout(this);
	    l.setColumnStretchable(0, true);
	    l.setColumnStretchable(1, true);
	    
	    s.addView(l); 
	      
	    RelativeLayout rl2;
	    Button bp, bn;
	    RelativeLayout.LayoutParams rlp3=null, rlp4=null, rlp5=null;
	    if(totalcount >= 4){
	    	rl2 = new RelativeLayout(this);
	    	//RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
	    	bp = new Button(this);
	    	bp.setOnClickListener(listenerPrevious);
	    	bp.setWidth(100);
	    	bp.setText("Previous");
	
	    	rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    
	    	rl2.addView(bp, rlp3);
	      
	    	rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    
	    	pagetv = new TextView(this);
	    	pagetv.setText("Page: 1 of "+lastpage);
	    	pagetv.setWidth(100);
	    	pagetv.setTextSize(18);
	    	rl2.addView(pagetv, rlp4);
		
	    	bn = new Button(this);
	    	bn.setOnClickListener(listenerNext);
	    	bn.setWidth(100);
	    	bn.setText("Next");
	    
	    	rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    
	    	rl2.addView(bn, rlp5);
	   
	    	l.addView(rl2);
	    	}
	   
		count = 0;
		   
		int page = 2;
		
	    for(String thisview : allviews){
	    	viewvalues = thisview.split(",,");
	    	
	    	if(count >= 3 && totalcount >= 4){
	    		s = new ScrollView(this);
	    	    
	    	    f.addView(s);
	    	    
	    	    l=new TableLayout(this);
	    	    
	    	    l.setColumnStretchable(0, true);
	    	    l.setColumnStretchable(1, true);
	    	    	
	    	    rl2 = new RelativeLayout(this);
	    	    
	    	    bp = new Button(this);
	    	    bp.setOnClickListener(listenerPrevious);
	    		bp.setWidth(100);
	    	    bp.setText("Previous");
	    	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	    	    
	    	    rl2.addView(bp, rlp3);
	    	    	    	    
	    	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	    
	    	    pagetv = new TextView(this);
	    	    pagetv.setText("Page: "+page+" of "+lastpage);
	    	    pagetv.setWidth(100);
	    	    pagetv.setTextSize(18);
	    	    rl2.addView(pagetv, rlp4);
	    		
	    	    page++;
	    	    
	    		bn = new Button(this);
	    		bn.setOnClickListener(listenerNext);
	    		bn.setWidth(100);
	    	    bn.setText("Next");
	    	    
	    	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	    	    
	    	    rl2.addView(bn, rlp5);
	    	   
	    	    l.addView(rl2);
	    	    
	    	    s.addView(l, lp);
	
	    		count = 0;
	    		}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("input")){  	
	        	cb = new CheckBox(this);
	    		cb.setText(viewvalues[2]);
	    		l.addView(cb, lp);
	    		et = new EditText(this);
	    		
	    		l.addView(et, lp);
	    		textviewhash.put(viewvalues[1], et);
	    		
	    		textcheckhash.put(viewvalues[1], cb);

	    		count++;
	    	}
	    	
	    	 String[] tempstring;
	                  
	    	if(viewvalues[0].equalsIgnoreCase("select1")){
	    		tv = new TextView(this);
	        	tv.setText(viewvalues[2]);
	        	tv.setTextSize(18);
	        	tv.setWidth(100);
	        	l.addView(tv, lp);
	        	
	    		spin = new Spinner(this);
	    		l.addView(spin, lp);

	    		thisspinnerhash.put(viewvalues[1], spin);
	    		tempstring = (dbAccess.getValue("spinner_"+viewvalues[1])).split(",,");
	    		if(spinnershash.get(viewvalues[1]) == null){
	 	    		spinnershash.put(viewvalues[1], new ArrayList<String>());
	 	    	}
	 	    	for (int i = 0; i < tempstring.length; i++) {
	 	    		spinnershash.get(viewvalues[1]).add(tempstring[i]);
	 	    	}
	 	    	
	    		count++;
	    	}
	        	
	    	if(viewvalues[0].equalsIgnoreCase("select")){
	    		//for(String st : viewvalues)
	    		//	Log.i(getClass().getSimpleName(), "CHECKBOX VALS: "+st);
	    		TableRow tr; 

	    		tv = new TextView(this);
	        	tv.setText(viewvalues[2]);
	        	tv.setTextSize(18);
	        	//tv.setWidth(100);
	        	l.addView(tv, lp);
	        	
	        	for(int i = 3; i < viewvalues.length; i++){
	        		tr = new TableRow(this);
	        		cb = new CheckBox(this);
	        		cb.setText(viewvalues[i]);
	        		
	        		i++;
	        		
	        		tr.addView(cb);
	        		checkboxhash.put(viewvalues[1]+"_"+viewvalues[i], cb);
	        		
	        		CheckBox cb2 = new CheckBox(this);
	        		tr.addView(cb2);
	        		checkcheckhash.put(viewvalues[1]+"_"+viewvalues[i], cb2);
	        		
	        		
	        		l.addView(tr, lp);
	        		
	        		count++;
	        	}       	
	    	}     	
	    }

	    
	    for(String key : spinners){
	    	aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
	    	aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	thisspinnerhash.get(key).setAdapter(aspnLocs);
	    } 
	
	    return ll;
    } 
    
    private OnClickListener listenerNext = new OnClickListener() {
        public void onClick(View v) {
        	f.showNext();
        }

    };

    private OnClickListener listenerPrevious = new OnClickListener() {
        public void onClick(View v) {
        	f.showPrevious();
        }

    }; */
     
}

