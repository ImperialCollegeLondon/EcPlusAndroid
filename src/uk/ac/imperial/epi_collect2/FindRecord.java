package uk.ac.imperial.epi_collect2;

import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.HashMap;
import java.util.Hashtable;
//import java.util.LinkedHashMap;
//import java.util.List;
import java.util.Vector;

//import com.example.android.searchabledict.WordActivity;

//import uk.ac.imperial.epi_collect2.EntryNote.MyGestureDetector;
import uk.ac.imperial.epi_collect2.util.barcode.IntentIntegrator;
import uk.ac.imperial.epi_collect2.util.barcode.IntentResult;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
//import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import android.location.LocationManager;
//import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
//import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
//import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class FindRecord extends Activity {

	private static final int ACTIVITY_SEARCH=1;
	private static final String SEARCH_FIELD="1";
	private DBAccess dbAccess;
	//private TextView labelText;
	//private EditText recordText;
	//private Button findButton, cancelButton;
	//private HashMap <String, Integer> spinhash = new HashMap<String, Integer>();
	private String table;
	private Bundle extras;
	//private static String[] textviews = new String[0];
    private static String[] spinners = new String[0];
    private static String[] radios = new String[0];
    //private static String[] checkboxes = new String[0];
    private static Vector<String> search = new Vector<String>();
    private Hashtable<String, EditText> textviewhash;
    private Hashtable<String, Spinner> thisspinnerhash;
    private Hashtable <String, ArrayList<String>>spinnershash = new Hashtable <String, ArrayList<String>>();
	private static Hashtable <String, String[]>spinnersvalueshash = new Hashtable <String, String[]>();
	private Hashtable<String, Spinner> thisradiohash;
    private Hashtable <String, ArrayList<String>>radioshash = new Hashtable <String, ArrayList<String>>();
	private static Hashtable <String, String[]>radiosvalueshash = new Hashtable <String, String[]>();
    //private Hashtable<String, CheckBox> checkboxhash;
    private ViewFlipper flipper;
    private String[] allviews = new String[0];
    private int lastpage = 0, thispage = 1;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private GestureDetector gestureDetector;
    private Button bn, bp, searchButton;
    private TextView tv, pagetv;
    private EditText et;
    private Vector<String> doubles = new Vector<String>();
	private Vector<String> integers = new Vector<String>();
	private Spinner spin;
	private ArrayAdapter<String> aspnLocs; 
	private Hashtable<Integer, String> textviewposhash;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
	
        
        dbAccess = new DBAccess(this);
	    dbAccess.open();
	    
	    super.setTitle("EpiCollect+ "+dbAccess.getProject());
	    
	    extras = getIntent().getExtras();
	    
	    //labelText = (TextView) findViewById(R.id.labeltext);
	    //recordText = (EditText) findViewById(R.id.record);
	    //findButton = (Button) findViewById(R.id.findbut);
	    //cancelButton = (Button) findViewById(R.id.cancelbut);
	    
	    table = extras.getString("table");
	    //labelText.setText("Search for " + table);
	    
	    getValues(table);
	    setContentView(setLayout(table)); 
	    
	    //handleIntent(getIntent());
	    
	    /*Intent intent = getIntent();

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // handles a click on a search suggestion; launches activity to show word
        	Log.i("VIEW", "IN VIEW");
            //Intent wordIntent = new Intent(this, WordActivity.class);
            //wordIntent.setData(intent.getData());
            //startActivity(wordIntent);
            //finish();
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            //String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("QUERY", "IN query");
        } */
        
	  /*  cancelButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    		    		
		    	cancel();
		    	 	
		        }
		   	});
	    
	    findButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View view) {
		    		    		
		    	findRecord(table);
		    	 	
		        }
		   	}); */
	}
	
	//private void cancel(){
	//	setResult(RESULT_OK, getIntent());
    //    finish();
	//}
	
	/*@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	
	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      Log.i("QUERY", query);
	      //doMySearch(query);
	    }
	}*/

	
	private void findRecord(){ //String table){
		
		dbAccess.open();
		
		String views =  dbAccess.getValue(table, "notes_layout");
		String[] viewvalues;
		String query = ""; //, spintext;
		
		views = views.replaceFirst(",,,", "");
	    allviews = views.split(",,,");
		
		for(String thisview : allviews){
	    	viewvalues = thisview.split(",,");
	       	//Log.i("VIEWS "+viewvalues[0], viewvalues[1]);
	    	if(!search.contains(viewvalues[1])){ 
	    		continue;
	    	}
	    	
	    	// Changed "match" to "like"
	    	if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("barcode")){
	    		if(textviewhash.get(viewvalues[1]).getText() != null && textviewhash.get(viewvalues[1]).getText().toString().length() > 0){
	    			query += " and \""+viewvalues[1] +"\" like \"%"+ textviewhash.get(viewvalues[1]).getText().toString()+"%\"";
	    		}
	    		//Log.i("QUERY", query);
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("select1")){
	    		//spintext = (spinnersvalueshash.get(viewvalues[1])[thisspinnerhash.get(viewvalues[1]).getSelectedItemPosition()]);
				if(thisspinnerhash.get(viewvalues[1]).getSelectedItemPosition() != 0)
					query += " and \""+viewvalues[1] +"\" like "+ thisspinnerhash.get(viewvalues[1]).getSelectedItemPosition();
	    	}
	    	
	    	if(viewvalues[0].equalsIgnoreCase("radio")){
	    		//spintext = (spinnersvalueshash.get(viewvalues[1])[thisspinnerhash.get(viewvalues[1]).getSelectedItemPosition()]);
	    		try{
	    			if(thisradiohash.get(viewvalues[1]).getSelectedItemPosition() != 0)
	    				query += " and \""+viewvalues[1] +"\" like "+ thisradiohash.get(viewvalues[1]).getSelectedItemPosition();
	    		}
	    		catch(NullPointerException e){}
	    	}
		}
		
		query = query.replaceFirst(" and", "");
		
		//Log.i("QUERY", query);
		
		if(query.length() > 0){
			
		}
		else{
			showAlert(this.getResources().getString(R.string.no_data_to_search)); //"No data entered to search");
			return;
		}
		
		Intent i = new Intent(this, ListRecords.class);
    		
		i.putExtras(extras);
    	i.putExtra("query", query); 

    	dbAccess.close();
    	
   	    startActivityForResult(i, ACTIVITY_SEARCH);
		
		
		/*Intent i = getIntent();
		Bundle extras = new Bundle();
		LinkedHashMap<Integer, String> tablehash;
		LinkedHashMap<String, String> keyshash;
		int tablenum;
		
		String id = recordText.getText().toString();
		
		if(id.length() == 0){
			showAlert("Record ID required");
			return;
		}
		
		String primarykey = dbAccess.getKeyValue(table);
		String tempkey="", tempid="";
	
		if(dbAccess.checkValue(table, primarykey, id)){
			tablehash = dbAccess.getTablesByNum();
			keyshash = dbAccess.getKeys();
			tablenum = dbAccess.getTableNum(table);
			
			// Initialise extras
			//for(String s : keyshash.keySet())
			//	extras.putInt(s, 0);
			
			for(int j = tablenum; j >= 1; j--){
				
				if(j >= 2){
					tempkey = keyshash.get(tablehash.get(j-1));
					tempid = dbAccess.getValue(tablehash.get(j), primarykey, id); //tempkey, 
				}
				
				extras.putInt(tablehash.get(j), dbAccess.getSpinnerPos(tablehash.get(j), primarykey, id, tempkey, tempid));
				
				primarykey = tempkey;
				id = tempid;
			
			}
					
			i.putExtras(extras);
			setResult(RESULT_OK, i);
	        finish();
		}
		else{
			showAlert("Record '"+id+"' for "+table+" is not in the database");
		}*/
	}
	
	@Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   	super.onActivityResult(requestCode, resultCode, data);
	   	
	   	if(data != null)
	   		extras = data.getExtras();
	    switch(requestCode) {
	    case ACTIVITY_SEARCH:
	    	if(resultCode == RESULT_CANCELED){
	    		showAlert(this.getResources().getString(R.string.no_matches)); //"No matching records found in the database");
	    	}
	        break;
	    case IntentIntegrator.REQUEST_CODE: {
	           if (resultCode != RESULT_CANCELED) {
	               	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
	               if (scanResult != null) {
	                   String upc = scanResult.getContents();
	                   // Do whatever you want with the barcode...
	       		 	   EditText barcodetext = textviewhash.get(textviewposhash.get(thispage));
	       		 	   barcodetext.setText(upc);
	               }
	           }
	           break;
	    }
	    }
	 }
   
	
	public void showAlert(String message){
    	new AlertDialog.Builder(this)
        .setTitle(R.string.error) //"Error")
        .setMessage(message)
        .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

             public void onClick(DialogInterface dialog, int whichButton) {

             }
        }).show();	
    }
	
	private void getValues(String table){
    	
    	// Reset values as otherwise odd values seem to get left behind:
    	// Project - data - back -back - new project - data -> causes crash
    	// If first project has spinner and second doesn't then second "spinners"
    	// still contains
    	//textviews = new String[0];
        spinners = new String[0];
        radios = new String[0];
        //checkboxes = new String[0];
        
        if(dbAccess.getValue(table, "search") != null && dbAccess.getValue(table, "search").length() > 0)
    		for(String key : (dbAccess.getValue(table, "search")).split(",,")){
    			search.addElement(key);
    			//Log.i("SEARCH", key);
    		}
        
    	//if(dbAccess.getValue(table, "textviews") != null && dbAccess.getValue(table, "textviews").length() > 0)
		//	textviews = (dbAccess.getValue(table, "textviews")).split(",,"); // "CNTD", 
    	
    	if(dbAccess.getValue(table, "spinners") != null && dbAccess.getValue(table, "spinners").length() > 0){
    		spinners = (dbAccess.getValue(table, "spinners")).split(",,");
    	}
    	
    	String[] tempstring = null;
    	for(String key : spinners){       	
        	tempstring = dbAccess.getValue(table, "spinner_values_"+key).split(",,");
	    	spinnersvalueshash.put(key, tempstring);
        }
    	
    	if(dbAccess.getValue(table, "radios") != null && dbAccess.getValue(table, "radios").length() > 0){
    		radios = (dbAccess.getValue(table, "radios")).split(",,");
    	}
    	
    	for(String key : radios){       	
        	tempstring = dbAccess.getValue(table, "radio_values_"+key).split(",,");
	    	radiosvalueshash.put(key, tempstring);
        }
    	
    	//if(dbAccess.getValue(table, "checkboxes") != null && dbAccess.getValue(table, "checkboxes").length() > 0)
    	//	checkboxes = (dbAccess.getValue(table, "checkboxes")).split(",,");
    	               
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
    }

private RelativeLayout setLayout(String table){
    
	// CHANGE TEXTVIEWHASH TO EDITTEXTHASH
	textviewhash = new Hashtable<String, EditText>();
	//checkboxhash = new Hashtable<String, CheckBox>();
    thisspinnerhash = new Hashtable<String, Spinner>();
    thisradiohash = new Hashtable<String, Spinner>();
    textviewposhash = new Hashtable<Integer, String>();
	
    String views =  dbAccess.getValue(table, "notes_layout");// parser.getValues();
            
    flipper = new ViewFlipper(this);
    
    RelativeLayout ll = new RelativeLayout(this);
    ll.setLayoutParams( new ViewGroup.LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
    
 // Calculate last page
    
    int count = 0;
    String[] viewvalues;
    
    views = views.replaceFirst(",,,", "");
    allviews = views.split(",,,");
    
    lastpage = 0;
	 
    for(String thisview : allviews){
    	
    	viewvalues = thisview.split(",,");
  
    	if(search.contains(viewvalues[1])){
    		if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("select1") || viewvalues[0].equalsIgnoreCase("photo") || viewvalues[0].equalsIgnoreCase("gps")){
    			lastpage++;
    		}
    		else if(viewvalues[0].equalsIgnoreCase("select")){
    			lastpage++;
    		}
    	}
    } 

    RelativeLayout.LayoutParams linear1layout2 = new RelativeLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ); 
       
    ll.addView(flipper, linear1layout2);
       
    ScrollView s = new ScrollView(this);
    
    flipper.addView(s); 
    gestureDetector = new GestureDetector(new MyGestureDetector());
    
    TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    TableLayout l=new TableLayout(this);
    
    s.addView(l); 
      
    RelativeLayout rl2;
    RelativeLayout.LayoutParams rlp3=null, rlp4=null, rlp5=null;
    rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    rlp5 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    View v;
        
    if(lastpage >= 2){
    	rl2 = new RelativeLayout(this);
    
    	bp = new Button(this);
    	bp.setOnClickListener(listenerPrevious);
    	bp.setWidth(100);
    	bp.setText(R.string.previous); //"Prev");

    	rlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
          
    	rlp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
    	rlp4.addRule(RelativeLayout.CENTER_VERTICAL);
    
    	pagetv = new TextView(this);
    	pagetv.setText("P 1 of "+lastpage);
    	pagetv.setWidth(100);
    	pagetv.setTextSize(18);
    	rl2.addView(pagetv, rlp4);
	
    	bn = new Button(this);
    	bn.setOnClickListener(listenerNext);
    	bn.setWidth(100);
    	bn.setText(R.string.next); //"Next");
    
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
	
    for(String thisview : allviews){
    	viewvalues = thisview.split(",,");

    	if(!search.contains(viewvalues[1])){ 
    		continue;
    	}
    	
    	if(count >= 1){ // && totalcount >= 7){
    		s = new ScrollView(this);
    	    
    		flipper.addView(s);
    	    
    	    l=new TableLayout(this);
    	    
    	    rl2 = new RelativeLayout(this);
    	    
    	    bp = new Button(this);
    	    bp.setOnClickListener(listenerPrevious);
    		bp.setWidth(100);
    	    bp.setText(R.string.previous); //"Prev");
    	    rlp3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    	    
    	    rl2.addView(bp, rlp3);
    	    	    	    
    	    rlp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
    	    
    	    pagetv = new TextView(this);
    	    pagetv.setGravity(Gravity.CENTER_VERTICAL);
    	    pagetv.setText("P "+page+" of "+lastpage);
    	    pagetv.setTextSize(18);
    	    rl2.addView(pagetv, rlp4);
    		
    	    if(page < lastpage){	    	    
    	    
    		bn = new Button(this);
    		bn.setOnClickListener(listenerNext);
    		bn.setWidth(100);
    	    bn.setText(R.string.next); //"Next");
    	    
    	    rlp5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    	    
    	    rl2.addView(bn, rlp5);
    	    }
    	    
    	    page++;
    	    
    	    l.addView(rl2);
    	    
    	    s.addView(l, lp);

    	    v = new View(this);
    		v.setMinimumHeight(2);
    		v.setBackgroundColor(Color.WHITE);
    		l.addView(v, lp);
    		
    		v = new View(this);
    		v.setMinimumHeight(25);
    		l.addView(v, lp);
    		
    		count = 0;
    		}
    	
    	String label;
    	Button barcodeButton;
    	if(viewvalues[0].equalsIgnoreCase("input") || viewvalues[0].equalsIgnoreCase("barcode")){
    		
    		tv = new TextView(this);
    		
    		label = viewvalues[2].replaceAll("\\\\n", "<br>");
    		tv.setText(Html.fromHtml(label));
        	tv.setTextSize(18);
        	l.addView(tv, lp);
    		
        	if(viewvalues[0].equalsIgnoreCase("barcode")){
        		barcodeButton = new Button(this);
        		barcodeButton.setText(R.string.read_barcode); //"Tap to Read Barcode");
        		barcodeButton.setTag(viewvalues[1]);
        		l.addView(barcodeButton, lp);
			   			
        		barcodeButton.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View arg0) {
        				IntentIntegrator.initiateScan(FindRecord.this);
        				//readBarcode();
        			}
		           
        		});
        	}

    		et = new EditText(this);
    		
    		//if(viewvalues[0].equalsIgnoreCase("input")){
    			if(integers.contains(viewvalues[1])){
    				et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    			}
    			else if(doubles.contains(viewvalues[1])){
    				et.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
    			}
    		//}
    		
    		l.addView(et, lp);
    		
    		textviewposhash.put(page-1, viewvalues[1]);
    		textviewhash.put(viewvalues[1], et);
    		
    		count++;
    		
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
    		v.setMinimumHeight(25);
    		l.addView(v, lp);
    	
    	
    		searchButton = new Button(this);
    		searchButton.setText(R.string.search); // "Search");
       
    		l.addView(searchButton, lp);
        
    		searchButton.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View view) {
    				findRecord();
    			}
    		});
    		

    	}
    		  	
    	String[] tempstring;
          
    	if(viewvalues[0].equalsIgnoreCase("select1")){
    		
    		tv = new TextView(this);
        	label = viewvalues[2].replaceAll("\\\\n", "<br>");
    		tv.setText(Html.fromHtml(label));
        	tv.setTextSize(18);
        	l.addView(tv, lp);
        	
    		spin = new Spinner(this); 
	    	
    		l.addView(spin, lp);
    		
    		thisspinnerhash.put(viewvalues[1], spin);
    		tempstring = (dbAccess.getValue(table, "spinner_"+viewvalues[1])).split(",,");
    		if(spinnershash.get(viewvalues[1]) == null){
 	    		spinnershash.put(viewvalues[1], new ArrayList<String>());
 	    	}
 	    	for (int i = 0; i < tempstring.length; i++) {
 	    		spinnershash.get(viewvalues[1]).add(tempstring[i]);
 	    	}

    		count++;
    		

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
    		v.setMinimumHeight(25);
    		l.addView(v, lp);
    	
    	
    		searchButton = new Button(this);
    		searchButton.setText(R.string.search); //"Search");
       
    		l.addView(searchButton, lp);
        
    		searchButton.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View view) {
    				findRecord();
    			}
    		});
    		
    	}
    }
    
    for(String key : spinners){
    	if(search.contains(key)){
    		aspnLocs = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnershash.get(key));
    		aspnLocs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		thisspinnerhash.get(key).setAdapter(aspnLocs); 
    	}

    }

    return ll;
    
 }

	private OnClickListener listenerNext = new OnClickListener() {
		public void onClick(View v) {

    		flipper.setInAnimation(animateInFrom(RIGHT));
            flipper.setOutAnimation(animateOutTo(LEFT));

   			flipper.showNext();
   			setFocus(thispage, 1);
   			thispage++;  			
    		}
    	};

	private OnClickListener listenerPrevious = new OnClickListener() {
		public void onClick(View v) {

    		flipper.setInAnimation(animateInFrom(LEFT));
            flipper.setOutAnimation(animateOutTo(RIGHT));
            
            flipper.showPrevious();
            setFocus(thispage, -1);
            thispage--;
           	}

		};
		
		 private void setFocus(int page, int type){
		    	
		    	if(textviewposhash.get(page) != null)
		    		textviewhash.get(textviewposhash.get(page)).clearFocus();
		    	if(textviewposhash.get(page+type) != null)
			    	textviewhash.get(textviewposhash.get(page+type)).requestFocus();


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
		                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		                                return false;
		                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		                                // right to left swipe

		                        	flipper.setInAnimation(animateInFrom(RIGHT));
		                        	flipper.setOutAnimation(animateOutTo(LEFT));
		                        	flipper.showNext();
		            	        	setFocus(thispage, 1);
		            	        	thispage++;
		            	        	                   
		                        	
		                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		                                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		                                // left to right swipe
		                        	
		                        		flipper.setInAnimation(animateInFrom(LEFT));
		                                flipper.setOutAnimation(animateOutTo(RIGHT));
		                                	flipper.showPrevious();
		                                	setFocus(thispage, -1);
		                                	thispage--;
		                               
		                            
		                        }
		                } catch (Exception e) {}
		                return false;
		        }
		    }
		    
		// Prevents pressing search option from loading quick search 
		@Override
		public boolean onSearchRequested() {
			//Log.i("SEARCH", "IN ON SEARCH!!!!!!!!!!!!!!!!!!!!!!!!");
			/*	if(textviewposhash.get(thispage) != null){
				Log.i("SEARCH", "IN ON SEARCH 2 "+textviewposhash.get(thispage));
				Bundle appData = extras; // new Bundle();
				appData.putString(SearchActivity.SEARCH_FIELD, textviewposhash.get(thispage));
				startSearch(null, false, appData, false);
			}*/
		    return true;
		    } 
}

