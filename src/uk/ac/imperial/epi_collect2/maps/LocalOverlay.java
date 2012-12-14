package uk.ac.imperial.epi_collect2.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import uk.ac.imperial.epi_collect2.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.GeoPoint;

public class LocalOverlay extends Overlay{ 
	
    private List<DBAccess.Row> rows;
    private Hashtable <String, String> recordhash = new Hashtable<String, String>(); 
    private Hashtable <Long, Integer> pointhashx = new Hashtable<Long, Integer>();
    private Hashtable <Long, Integer> pointhashy = new Hashtable<Long, Integer>();
    private int thistype;
    private Bitmap pin = null;
    private LocalMap thismap;
    private ArrayList<MapLocation> mapLocations;
    //private static String[] textviews = new String[0];
    private static String[] spinners = new String[0];
    //private static String[] checkboxes = new String[0];
    private static Hashtable <String, String[]>spinnershash = new Hashtable <String, String[]>();
    private Vector<String> gpstags = new Vector<String>();
    private boolean needskey = false;
    int canvascount = 0;;
    //private String table;
    //private static Vector<String> listfields, listspinners, listcheckboxes; 

    public LocalOverlay(LocalMap map, List<DBAccess.Row> dbrows, int type, DBAccess dbAccess, String table){
    	thismap = map;
        rows = dbrows;
        thistype = type;
           
        getValues(dbAccess, table);
        
        //canvascount = 0;
    }

    public void draw(Canvas canvas, MapView mapView, boolean b) {
    	//if(canvascount == 0){
        //	canvascount = 1;
        super.draw(canvas, mapView, b);

       /* To detect event on mapview you have to enabled clicks (very important), like this :
        	Java:
        	myMapView.setClickable(true);
        	in the onCreate method
        	After, you just have to catch event with a dispatchTouchEvent(MotionEvent event).
        	So I detect finger touch coordinates and I compare with station coordinates with a tolerance. 
        */
        
        canvascount++;
        Log.i("IN CANVAS", ""+canvascount);
              
        recordhash.clear();
        pointhashx.clear();
        pointhashy.clear();
        GeoPoint geoPoint;
        Point screenCoords;
        mapLocations = new ArrayList<MapLocation>();
        //int count = 1;
        for (DBAccess.Row row : rows) {
        	//count++;
        	for(String key : gpstags){
        		
        		try{
        			if(thistype == 1)
        				pin = BitmapFactory.decodeResource(this.thismap.getResources(), R.drawable.blue);
        			else
        				pin = BitmapFactory.decodeResource(this.thismap.getResources(), R.drawable.red);
        			geoPoint = new GeoPoint((int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6), (int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6));
        	Log.i("Point Lat: "+ row.datastrings.get(key+"_lat")+" Lon: "+row.datastrings.get(key+"_lon"), " Lat2: "+(int)(Double.parseDouble(row.datastrings.get(key+"_lat"))*1000000)+" Lon2: "+(int)(Double.parseDouble(row.datastrings.get(key+"_lon"))*1000000));
        		//String maptext = getMapText(row);
        	
        			mapLocations.add(new MapLocation(row.ectitle, row.ecpkey, row.ecphonekey, geoPoint, +(int)(Float.parseFloat(row.datastrings.get(key+"_lat"))*1E6),(int)(Float.parseFloat(row.datastrings.get(key+"_lon"))*1E6)));
        	
        		//geoPoint = new GeoPoint((int)(Double.parseDouble(row.datastrings.get(key+"_lat"))*1000000), (int)(Double.parseDouble(row.datastrings.get(key+"_lon"))*1000000));
        			screenCoords = new Point();
        			mapView.getProjection().toPixels(geoPoint, screenCoords);
            
        			canvas.drawBitmap(pin, screenCoords.x - 16, screenCoords.y - 32, null);
        		}
        	catch(NumberFormatException nfe){
        		//Log.i("GPS KEY", row.datastrings.get(key+"_lat"));
        	}
        	}
        	
        //}
        } 
    }
    
    public void setMap(LocalMap thisMap){
    	
    	thismap = thisMap;
    }
    
    //private String getMapText(DBAccess.Row row){
    	
    //	String maptext = ""; 
    /*+row.rowId;
    	       
        for(String key : textviews){
        	if(listfields.contains(key))
        		maptext += " " + row.datastrings.get(key);
        }
                   
        for(String key : spinners){
        	if(listspinners.contains(key))
        		maptext += " " + spinnershash.get(key)[row.spinners.get(key)];
        }
        
        for(String key : checkboxes){
        	if(listcheckboxes.contains(key)){
        		if(row.checkboxes.get(key))
        			maptext += " " + key + " = T";
        		else
        			maptext += " " + key + " = F";
        	}
        }*/
    	
    //	return maptext;
    //}
    
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {

        getHitMapLocation(mapView,event);
       
        return false;
    }
    
    private void getHitMapLocation(MapView mapView, MotionEvent event) {

    	String text = "", pkey = "";
    	//long topid = -1000000;

        RectF hitTestRecr = new RectF();
        Point screenCoords = new Point();
        Iterator<MapLocation> iterator = mapLocations.iterator();
        while(iterator.hasNext()) {

            MapLocation testLocation = iterator.next();

            // As above, translate MapLocation lat/long to screen coordinates
            mapView.getProjection().toPixels(testLocation.geoPoint, screenCoords);

            // Use this information to create a hit testing Rectangle to represent the size
            // of our locations icon at the correct location on the screen.
            // As we want the base of our balloon icon to be at the exact location of
            // our map location, we set our Rectangles location so the bottom-middle of
            // our icon is at the screen coordinates of our map location (shown above).
            hitTestRecr.set(-32/2,-32,32/2,0);

            // Next, offset the Rectangle to location of our locations icon on the screen.
            hitTestRecr.offset(screenCoords.x,screenCoords.y);

            // Finally test for match between hit Rectangle and location clicked by the user.
            // If a hit occurred, then we stop processing and return the result;
            if (hitTestRecr.contains(event.getX(),event.getY())) {
            	
            	//if(testLocation.id > topid)
            	//	topid = testLocation.id;
            	
            	if(needskey){
            		text = text + testLocation.phonekey + " " + testLocation.title+"\n";
            	}
            	else{
            		text = text + testLocation.title+"\n";
            	}
            	pkey = testLocation.pkey;
            }
        }

        if(!text.equalsIgnoreCase("")){
        	if(!thismap.alertshowing){
        		thismap.showAlert("Selected Point", text, pkey);
        	   	thismap.alertshowing = true;
        	}
        	//thismap.selectText.setText(""+topid);
        }

    }
    
    private void getValues(DBAccess dbAccess, String table){
    	
    	String[] tempstring = null;
    	if(dbAccess.getValue(table, "gps") != null && dbAccess.getValue(table, "gps").length() > 0){
    		tempstring = (dbAccess.getValue(table, "gps")).split(",,");
    		for(String key : tempstring){     
    			gpstags.addElement(key);
    		}
    		
    	}
    	

    	if(dbAccess.getValue(table, "genkey") != null && dbAccess.getValue(table, "genkey").length() > 0){
           	
           	needskey = true;
        }
    	
    	//if(dbAccess.getValue("textviews") != null)
		//	textviews = (dbAccess.getValue("textviews")).split(",,"); // "CNTD", 
    	////if(dbAccess.getValue("spinners") != null)
    	////	spinners = (dbAccess.getValue("spinners")).split(",,");
    	//if(dbAccess.getValue("checkboxes") != null)
    	//	checkboxes = (dbAccess.getValue("checkboxes")).split(",,");
    	
    	//List <String>list = Arrays.asList(dbAccess.getValue("listfields").split("\\s+"));
        //listfields = new Vector<String>(list);
        
        //list = Arrays.asList(dbAccess.getValue("listspinners").split("\\s+"));
        //listspinners = new Vector<String>(list);
        
        //list = Arrays.asList(dbAccess.getValue("listcheckboxes").split("\\s+"));
        //listcheckboxes = new Vector<String>(list);
    	
    	////String[] tempstring;
        ////for(String key : spinners){       	
        ////	tempstring = dbAccess.getValue("spinner_"+key).split(",,");
	    ////	spinnershash.put(key, tempstring);
        ////}       
        
        
    }

}
