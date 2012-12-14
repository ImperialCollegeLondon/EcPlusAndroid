package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Hashtable;
import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Vector;

import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.content.Context;
//import android.util.Log;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class Hierarchy {
	
	//private static Vector<String> listfields, listspinners, listcheckboxes;
	private static DBAccess dbAccess;
	
	public Hierarchy(){
		
		
	}
	
      
  public static RelativeLayout setLayout(LinkedHashMap<String, String> spinhash, String thistable, Context context){
        
	  	RelativeLayout ll = new RelativeLayout(context);
	  	ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
    
   		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
   	
   		ListView lv = new ListView(context); 
	    
   		ArrayList<String>items = new ArrayList<String>();
	    
   		dbAccess = new DBAccess(context);
	    dbAccess.open();
	    
	    //List<DBAccess.Row> rows;
	    
	    //LinkedHashMap<String, String> keyshash = dbAccess.getKeys();
   		//String[] tempstring;
   		
   		/*for(String key : spinhash.keySet()){
   			if(spinhash.get(key) == 0)
   				continue;
   			tempstring = (dbAccess.getSpinnerValues(key, keyshash.get(key))).split(",,");
   			items.add(key + " - " + tempstring[spinhash.get(key)]);
   		}*/
   		

   		//String listitem;
   		//DBAccess.Row row;
   		for(String table : spinhash.keySet()){
   			//Log.i("HIERARCHY", table +" "+ thistable+" "+ spinhash.get(table));
   			if(table.equalsIgnoreCase(thistable))
   				break;
   			//if(spinhash.get(table) == 0)
   			//	continue;
   			//Log.i("HIERARCHY", "Table: "+table);
   			if(spinhash.get(table).equalsIgnoreCase("Select"))
   	   			continue;
   			//tempstring = (dbAccess.getSpinnerValues(table, keyshash.get(table))).split(",,");
   			
   			//Log.i("HIERARCHY", table +" "+ keyshash.get(table)+" "+ spinhash.get(table));
   			//row = dbAccess.fetchRow(table, keyshash.get(table), spinhash.get(table)); //tempstring[spinhash.get(table)]);
   			
   			//if(row == null)
   			//	continue;
    	       	    	   				
   			items.add(table +": "+spinhash.get(table)); // row.title
   		} 
  
   	
	   	     		    
	    ListAdapter notes = new ArrayAdapter<String>(context, R.layout.records_row, items);
	    lv.setAdapter(notes);
	    	    
	    ll.addView(lv, rlp);
	  
	    return ll;
    }
   		
   /*	private static String getItem(DBAccess.Row row, String table){
   	    	
   	    String listitem = "";
   	   	    	
   	    Hashtable <String, String[]>spinnershash = new Hashtable <String, String[]>();
   	    Vector<String> listfields, listspinners, listcheckboxes;       
   	    String[] textviews = new String[0];
   	    String[] spinners = new String[0];
   	    String[] checkboxes = new String[0];
   	    
   	    if(dbAccess.getValue(table, "textviews") != null)
			textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
   	    if(dbAccess.getValue(table, "spinners") != null)
   	    	spinners = (dbAccess.getValue(table, "spinners")).split(",,");
   	    if(dbAccess.getValue(table, "checkboxes") != null)
   	    	checkboxes = (dbAccess.getValue(table, "checkboxes")).split(",,");
 	
   	    
   	    List <String>list = Arrays.asList(dbAccess.getValue(table, "listfields").split("\\s+"));
   	    listfields = new Vector<String>(list);
	     
	     list = Arrays.asList(dbAccess.getValue(table, "listspinners").split("\\s+"));
	     listspinners = new Vector<String>(list);
     
	     list = Arrays.asList(dbAccess.getValue(table, "listcheckboxes").split("\\s+"));
	     listcheckboxes = new Vector<String>(list);
	     
	     String[] tempstring;
	     for(String key : spinners){       	
	    	 tempstring = dbAccess.getValue(table, "spinner_"+key).split(",,");
	    	 spinnershash.put(key, tempstring);
	     }     
   	     
	     for(String key : textviews){
	    	 if(listfields.contains(key)){
	    		 //Log.i("HIERARCHY CHECK 4", row.datastrings.get(key));
	    		 listitem += " " + row.datastrings.get(key)+" ";
	    	 }
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
   	        }

   	    return listitem;
   	    } */
   		
   		
  }