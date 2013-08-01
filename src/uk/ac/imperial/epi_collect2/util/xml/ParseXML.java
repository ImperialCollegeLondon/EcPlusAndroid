package uk.ac.imperial.epi_collect2.util.xml;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
//import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.imperial.epi_collect2.Epi_collect;
//import uk.ac.imperial.epi_collect2.util.db.DBAccess;

//import org.xml.sax.helpers.XMLReaderFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
//import java.util.Hashtable;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import android.app.Activity;
//import android.location.LocationManager;
import android.net.http.SslError;
import android.os.Environment;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public class ParseXML{

	InputStream xml_stream;
	//String xml_stream;
	StringBuffer views = new StringBuffer(""), textviews = new StringBuffer(""), checkboxes = new StringBuffer(""), 
	spinners = new StringBuffer(""), radios = new StringBuffer(""), checkboxgroups = new StringBuffer(""), doubles = new StringBuffer(""), 
	integers = new StringBuffer(""), uppercase = new StringBuffer(""), requiredtext = new StringBuffer(""), requiredspinners = new StringBuffer(""), requiredradios = new StringBuffer(""), 
	listfields = new StringBuffer(""), listspinners = new StringBuffer(""), listradios = new StringBuffer(""), listcheckboxes = new StringBuffer(""),
	local = new StringBuffer(""), doublecheck = new StringBuffer(""), genkey = new StringBuffer(""), totals = new StringBuffer(""),
	dates = new StringBuffer(""), setdates = new StringBuffer(""), settimes = new StringBuffer(""), radioimages = new StringBuffer(""), jumps = new StringBuffer(""), jumps1 = new StringBuffer(""), jumps2 = new StringBuffer(""), nodisplay = new StringBuffer(""),
	photos = new StringBuffer(""), gps = new StringBuffer(""), regex = new StringBuffer(""), matches = new StringBuffer(""), defaultvals = new StringBuffer(""), mincheck = new StringBuffer(""), maxcheck = new StringBuffer(""), mincheck2 = new StringBuffer(""), maxcheck2 = new StringBuffer(""), barcodes = new StringBuffer(""), noteditable = new StringBuffer(""),
	search = new StringBuffer(""), videos = new StringBuffer(""), audio = new StringBuffer(""), branch = new StringBuffer(""), groups = new StringBuffer(""), crumbs = new StringBuffer("");
	HashMap<String, StringBuffer> spinnerrefs = new HashMap<String, StringBuffer>();
	HashMap<String, StringBuffer> spinnervalues = new HashMap<String, StringBuffer>();
	HashMap<String, StringBuffer> radiorefs = new HashMap<String, StringBuffer>();
	HashMap<String, StringBuffer> radiovalues = new HashMap<String, StringBuffer>();
	HashMap<String, StringBuffer> checkboxrefs = new HashMap<String, StringBuffer>();
	HashMap<String, StringBuffer> checkboxvalues = new HashMap<String, StringBuffer>();
	HashMap<Integer, String> foreignkeys_toset = new HashMap<Integer, String>();
	String app_name="", project="", changesynch = "false";
	String url_string = "";
	//String demoxml = "<xform> <model> <submission id=\"ahBlcGljb2xsZWN0c2VydmVycg8LEgdQcm9qZWN0GKbgFAw\" projectName=\"demoproject\" allowDownloadEdits=\"false\" versionNumber=\"1.1\"/> </model><input name=\"name\" required=\"true\" title=\"true\"> <label>What is your name?</label> </input><select1 name=\"age\" required=\"true\" chart=\"bar\"> <label>What is your age?</label> <item><label>below10</label><value>below10</value></item> <item><label>11to20</label><value>11to20</value></item> <item><label>21to30</label><value>21to30</value></item> <item><label>31to40</label><value>31to40</value></item> <item><label>41to50</label><value>41to50</value></item> <item><label>51to60</label><value>51to60</value></item> <item><label>61to70</label><value>61to70</value></item> <item><label>above70</label><value>above70</value></item> </select1><select1 name=\"sex\" required=\"true\"> <label>Male or Female?</label> <item><label>Male</label><value>Male</value></item> <item><label>Female</label><value>Female</value></item> </select1><select1 name=\"searchengine\" required=\"true\" chart=\"bar\"> <label>Which search engine do you most often use?</label> <item><label>Google</label><value>Google</value></item> <item><label>Yahoo</label><value>Yahoo</value></item> <item><label>Baidu</label><value>Baidu</value></item> <item><label>Bing</label><value>Bing</value></item> <item><label>Ask</label><value>Ask</value></item> <item><label>AOL</label><value>AOL</value></item> <item><label>AltaVista</label><value>AltaVista</value></item> <item><label>other</label><value>other</value></item> </select1><select name=\"socialnetworks\" required=\"true\" chart=\"pie\"> <label>Which social networking sites do you use?</label> <item><label>MySpace</label><value>MySpace</value></item> <item><label>Facebook</label><value>Facebook</value></item> <item><label>Hi5</label><value>Hi5</value></item> <item><label>Friendster</label><value>Friendster</value></item> <item><label>Orkut</label><value>Orkut</value></item> <item><label>Bebo</label><value>Bebo</value></item> <item><label>Tagged</label><value>Tagged</value></item> <item><label>Xing</label><value>Xing</value></item> <item><label>Badoo</label><value>Badoo</value></item> <item><label>Xanga</label><value>Xanga</value></item> <item><label>51.com</label><value>51com</value></item> <item><label>Xiaonei</label><value>Xiaonei</value></item> <item><label>ChinaRen</label><value>ChinaRen</value></item> </select></xform>";
	String remote_xml = ""; //"http://epicollectserver.appspot.com/downloadFromServer";
    String synch_url = ""; //"http://epicollectserver.appspot.com/uploadToServer";
    String button_image_url = "";
    String synch_local_url = "None";
    String local_remote_xml = "None";
    String reg_email = "", epicollect_version = "2", project_description = "", project_version = "1.0", server = ""; //, project_type = "full"; //, foreign_key = "";
    //boolean usephonekey = false;
    
    // Store the information for each table. This is the information in createTable()
    HashMap<String, StringBuffer> tableshash = new HashMap<String, StringBuffer>();
    HashMap<String, HashMap<String, String>> tablerowshash = new HashMap<String, HashMap<String, String>>();
    //HashMap<String, HashMap<String, String>> tablenumhash = new HashMap<String, HashMap<String, String>>();
    //HashMap<String, String> temptablenumhash = new HashMap<String, String>();
    
    LinkedHashMap<String, HashMap<String, String>> tablekeyhash = new LinkedHashMap<String, HashMap<String, String>>();
    HashMap<String, String> projecthash = new HashMap<String, String>();
    String tablename = "", tablekey=""; //, show_previous = "false"; //, tablenum = "";
	
	final Vector<String> types = new Vector<String>();
	//DBAccess dbAccess;
	private int table_num = 0;
	private Vector<String> branchesvec = new Vector<String>();
	//private boolean callerkey_req = false;

	public ParseXML(String url_s){ // "http://www.doc.ic.ac.uk/~dmh1/Android//xml.xml"ArrayList<Row>
		//Log.i(getClass().getSimpleName(), "URL: "+ url_s);
		//demoxml = "";
		url_string = url_s;
		server = Epi_collect.project_server;
	}
	
	public ParseXML(){ // "http://www.doc.ic.ac.uk/~dmh1/Android//xml.xml"ArrayList<Row>
	}

	//@Override
	//public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
	//    handler.proceed(); // Ignore SSL certificate errors
	//}
	
	public boolean getXML(){    
	
		types.add("select1");
		types.add("radio");
		types.add("input");
		types.add("select");
		types.add("textarea");
		types.add("photo");
		types.add("video");
		types.add("audio");
		types.add("location");
		types.add("gps");
		types.add("GPS");
		types.add("barcode");
		types.add("branch");
		types.add("group");
		//types.add("table");
		//types.add("table_num");
		//types.add("table_name");
		//types.add("submission");
		//types.add("id");
		
    	try{
    		
    		//Log.i("XML CHECK", url_string);
    		if(url_string.startsWith("SD_")){
    			url_string = url_string.replaceFirst("SD_", "");
    			try{
    				xml_stream = new FileInputStream(new File(Epi_collect.appFiles+"/"+url_string));
    				//xml_file = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/"+xml_file); 
    			}
    			catch(Exception e){
    				try{
        				xml_stream = new FileInputStream(new File(Epi_collect.appFiles+"/"+url_string));
        				//xml_file = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/"+xml_file); 
        			}
        			catch(Exception ex){
        				return false;
        			}    			
        		}
    			
    		}
    		// Need to check length of url_string so that demo project loads
    		else if(url_string.length() > 0){
    			Log.i(getClass().getSimpleName(), "URL: "+ url_string);
    			URL url = new URL(url_string); //("http://www.doc.ic.ac.uk/~dmh1/Android//xml.xml"); // xml_url); //"http://www.google.co.uk");
    			
    			HttpURLConnection urlc = null;

    	        if (url.getProtocol().toLowerCase().equals("https")) {
    	            Epi_collect.trustAllHosts();
    	                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
    	                https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
    	                urlc = https;
    	        } else {
    	                urlc = (HttpURLConnection) url.openConnection();
    	        }

    			
    			//HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
    			urlc.setRequestMethod("GET");
    			urlc.connect();
    			xml_stream = urlc.getInputStream();
    		}

    		
    		//Log.i(getClass().getSimpleName(), "VIEWS: "+ xml_stream.toString());
    		
    	}
    	catch(MalformedURLException  ex){
    		//System.out.println("1 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 1 "+ex.toString());
    		return false;
    		}
    	catch (IOException ex) {
    		//System.out.println("2 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 2 "+ex.toString());
    		return false;
    		}
    	
    	try {
    		 
    	      SAXParserFactory factory = SAXParserFactory.newInstance();
    	      SAXParser saxParser = factory.newSAXParser();
    	 
    	      XMLHandler handler = new XMLHandler();
    	      saxParser.parse(xml_stream, handler);   	 

    	      if(tablename.length() > 0){
    	    	  createTable();
    	    	  createIndividualTable();
    	    	  createRow();
	          }
    	      
    	      
    	      // For EpiCollect1 form data
    	      
    	     if(tablename.length() == 0) {
	        	 HashMap<String, String> temptablekeyhash = new HashMap<String, String>();
	        	 temptablekeyhash.put("tablenum", "1");
	        	 temptablekeyhash.put("tablename", project); //"Data");
	        	 tablename = project; //"Data";
	        	 tablekey = "eckey";
	        	 temptablekeyhash.put("tablekey", tablekey); 
	        	 temptablekeyhash.put("maintable", "1"); 
	        	 //show_previous = "false";
	        	 tablekeyhash.put(tablename, temptablekeyhash);
	        	 
	        	 settimes.append(",,ecdatev1\tyyyy-MM-dd HH:mm:ss");
	        	 views.insert(0, ",,,input,,eckey,,KEY,,,input,,ectime,,Time,,,input,,ecdatev1,,Date/Time,,,input,,ectime,,Time,,,gps,,ecgps,,GPS,,,photo,,ecphoto,,Photo");
	        	 nodisplay.append(",,eckey");
	        	 nodisplay.append(",,ectime");
	        	 genkey.append(",,eckey");
	        	 //views.append(",,Date/Time"); 
	        	 noteditable.append(",,ecdate");
	        	 textviews.append(",,eckey");
	        	 textviews.append(",,ectime");
	        	 textviews.append(",,ecdatev1");
	        	 photos.append(",,ecphoto");
	        	 gps.append(",,ecgps");
	        	 epicollect_version = "1";
	        	 
	        	 createTable();
	        	 createIndividualTable();
	        	 createRow();
    	     }
    	      
    	      //HashMap<String, StringBuffer> tableshash = new HashMap<String, StringBuffer>();
    	      //HashMap<String, HashMap<String, String>> tablerowsshash = new HashMap<String, HashMap<String, String>>();
    	      //HashMap<String, String> tablesnumhash = new HashMap<String, String>();
    	      
    	      
    	      /*for(String key : tableshash.keySet()){
    	    	  //Log.i(getClass().getSimpleName(), "TABLESHASH "+key+" " + tableshash.get(key).toString());
    	      }
    	      
    	      for(String key : tablerowshash.keySet()){
    	    	  for(String key2 : tablerowshash.get(key).keySet()){
    	    		  //Log.i(getClass().getSimpleName(), "TABLEROWSHASH "+key+" " + key2+" "+tablerowshash.get(key).get(key2));
    	    	  }
    	      }
    	      
    	      for(String key : tablekeyhash.keySet()){
    	    	  for(String key2 : tablekeyhash.get(key).keySet()){
    	    		  //Log.i(getClass().getSimpleName(), "TABLEKEYHASH "+key+" " + key2+" "+tablekeyhash.get(key).get(key2));
    	    	  }
    	      } */
    	      
    	      //saxParser.parse(xml_stream, handler);
    	 
    	    } catch (Exception e) {
    	      //e.printStackTrace();
    	      Log.e(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return false;
    	    }
    	    //Log.i(getClass().getSimpleName(), "VIEWS "+views.toString());
    	    //System.out.println(views.toString());
    	    
    	    //createTable();
    	    return true;
    	  }
	
	public boolean getDemoXML(StringBuffer buf){    
		
		
		types.add("select1");
		types.add("radio");
		types.add("input");
		types.add("select");
		types.add("textarea");
		types.add("photo");
		types.add("video");
		types.add("audio");
		types.add("location");
		types.add("gps");
		types.add("GPS");
		types.add("barcode");
		types.add("branch");
		types.add("group");
    	
    	try {
    		 
    	     SAXParserFactory factory = SAXParserFactory.newInstance();
    	     SAXParser saxParser = factory.newSAXParser();
    	 
    	     XMLHandler handler = new XMLHandler();
    	      
    	     String demoxml = buf.toString();
    	      
    	     InputStream is = new ByteArrayInputStream(demoxml.getBytes("UTF-8"));
    	     saxParser.parse(is, handler);   	 
    	      
    	   //For version 1 project
        	/*
        	 HashMap<String, String> temptablekeyhash = new HashMap<String, String>();
        	 temptablekeyhash.put("tablenum", "1");
        	 temptablekeyhash.put("tablename", project); // "Data"
        	 tablename = project; //"Data";
        	 tablekey = "eckey";
        	 temptablekeyhash.put("tablekey", tablekey); 
        	 temptablekeyhash.put("maintable", "1"); 
        	 temptablekeyhash.put("fromgroup", "0"); 
        	 //show_previous = "false";
        	 tablekeyhash.put(tablename, temptablekeyhash);
        	         	 
        	 settimes.append(",,ecdatev1\tyyyy-MM-dd HH:mm:ss");
        	 views.insert(0, ",,,input,,eckey,,KEY,,,input,,ecdatev1,,Date/Time,,,gps,,ecgps,,GPS,,,photo,,ecphoto,,Photo");
        	 nodisplay.append(",,eckey");
        	 genkey.append(",,eckey");
        	 //views.append(",,Date/Time"); 
        	 textviews.append(",,eckey");
        	 textviews.append(",,ectime");
        	 textviews.append(",,ecdatev1");
        	 photos.append(",,ecphoto");
        	 gps.append(",,ecgps");
        	 epicollect_version = "1"; */
        	 //views.insert(1, ",,,photo,,ecphoto,,Photo");
        	 
        	 createTable();
        	 createIndividualTable();
        	 createRow();
     	      
        	// Log.i("DEMO XML", "HERE 3");  
    	      //saxParser.parse(xml_stream, handler);
    	 
    	    } catch (Exception e) {
    	      //e.printStackTrace();
    	      Log.i(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return false;
    	    }
    	    //Log.i(getClass().getSimpleName(), "VIEWS "+views.toString());
    	    //System.out.println(views.toString());
    	    
    	    //createTable();
    	    return true;
    	  }


	public String getValues(){
		return views.toString();
	}

	public String getProject(){
		return project;
	}
	
	/*public StringBuffer createTable(){
		
		StringBuffer table = new StringBuffer("create table if not exists "+ project +" (textviews text, checkboxes text, spinners text," +
				" checkboxgroups text, doubles text, integers text, requiredtext text, requiredspinners text," +
				" listfields text, listspinners text, listcheckboxes text, change_synch text, remote_xml, image_url, synch_url)");
		
		for(String key : spinnerrefs.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : spinnervalues.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : checkboxrefs.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : checkboxvalues.keySet()){
			table.append(", '"+key+"' text");
		}
		
		table.append(", notes_layout text)");
		
		tableshash.put(tablename, table);
		//dbAccess.createTable(table);
		return table;
	}*/
	
	public StringBuffer createTable(){
		
		StringBuffer table = new StringBuffer("create table if not exists "+ project +" (change_synch text, remote_xml text, synch_url text, synch_local_url text, local_remote_xml text, button_image_url text, reg_email text, epicollect_version text, project_description text, project_version text, ecdescription text)"); // image_url text, 
		
		projecthash.put("change_synch", changesynch);
		if(epicollect_version.equalsIgnoreCase("1")){
			remote_xml = "http://epicollectserver.appspot.com/downloadFromServer";
			synch_url = "http://epicollectserver.appspot.com/uploadToServer";
		}
		else{
			//if(server.length() == 0)
			//	server = Epi_collect.project_server; //dbAccess.getSettings("url");
			if(remote_xml.length() == 0)
				remote_xml = server + "/project/download";
			if(synch_url.length() == 0)
				synch_url = server + "/project/upload";
		}
		projecthash.put("remote_xml", remote_xml);
		projecthash.put("synch_url", synch_url);
		projecthash.put("synch_local_url", synch_local_url);
		projecthash.put("local_remote_xml", local_remote_xml);
		projecthash.put("button_image_url", button_image_url);
		projecthash.put("reg_email", reg_email);
		projecthash.put("epicollect_version", epicollect_version);
		projecthash.put("project_description", project_description);
		projecthash.put("project_version", project_version);
		//projecthash.put("project_type", project_type);
		
		return table;
	} 
	
	public HashMap<String, String> getProjectHash(){
		
		 return projecthash;
	}
	
	public void createIndividualTable(){
		
		StringBuffer table = new StringBuffer("create table if not exists "+ project +"_"+ tablename+" (textviews text, checkboxes text, spinners text, radios text, radioimages text," +
				" checkboxgroups text, doubles text, integers text, uppercase text, photos text, videos text, audio text, gps text, totals text, jumps text, jumps1 text, jumps2 text, regex text, matches text, defaultvals text,"+
				" mincheck text, maxcheck text, mincheck2 text, maxcheck2 text, search text, nodisplay text, editable text, doublecheck text, genkey text, dates text, setdates text, settimes text, local text, requiredtext text, requiredspinners text, requiredradios text," +
				" listfields text, listspinners text, listradios text, listcheckboxes text, barcodes text, branch text, groups text, crumbs text");
		
		for(String key : spinnerrefs.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : spinnervalues.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : radiorefs.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : radiovalues.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : checkboxrefs.keySet()){
			table.append(", '"+key+"' text");
		}
		
		for(String key : checkboxvalues.keySet()){
			table.append(", '"+key+"' text");
		}
		
		table.append(", notes_layout text");
		
		table.append(", ecpkey text)");
		
		//table.append(", show_previous text)");
		
		tableshash.put(tablename, table);
		//dbAccess.createTable(table);
		//return table;
	}
	
	public HashMap<String, StringBuffer> getIndividualTables(){
		
			 return tableshash;
		}
	
	public void createRow(){
		
		HashMap<String, String> rowhash = new HashMap<String, String>();
    	//rowhash.put("project", project);
		
		// Not sure this is now needed - or what it does??
		if((tablekeyhash.get(tablename).get("maintable").equalsIgnoreCase("1") || tablekeyhash.get(tablename).get("fromgroup").equalsIgnoreCase("1")) && foreignkeys_toset.containsKey(table_num)){ 
			textviews.append(",,"+foreignkeys_toset.get(table_num));
			views.append(",,,"+"input");
			views.append(",,"+ foreignkeys_toset.get(table_num));
			views.append(",,"+ "LABEL: " +foreignkeys_toset.get(table_num));
			nodisplay.append(",,"+foreignkeys_toset.get(table_num));
		}
		/*else if(callerkey_req){  // Its a branch
			String ref = tablekeyhash.get(tablekeyhash.get(tablename).get("branch_caller")).get("tablekey");
			textviews.append(",," + ref);
			views.append(",,,"+"input");
			views.append(",,"+ ref);
			views.append(",,"+ "LABEL: " + ref);
			nodisplay.append(",,"+ref);
		}*/
		
		if(textviews.length() > 0)
			rowhash.put("textviews", textviews.toString().replaceFirst(",,", ""));
    	if(checkboxes.length() > 0)
    		rowhash.put("checkboxes", checkboxes.toString().replaceFirst(",,", ""));
    	if(spinners.length() > 0){
    		rowhash.put("spinners", spinners.toString().replaceFirst(",,", ""));
    		//Log.i(getClass().getSimpleName(), "SPINNERS IN XML "+spinners.toString());
    	}
    	if(radios.length() > 0)
    		rowhash.put("radios", radios.toString().replaceFirst(",,", ""));
    	if(radioimages.length() > 0)
    		rowhash.put("radioimages", radioimages.toString().replaceFirst(",,", ""));
    	if(checkboxgroups.length() > 0)
    		rowhash.put("checkboxgroups", checkboxgroups.toString().replaceFirst(",,", ""));
    	if(doubles.length() > 0)
    		rowhash.put("doubles", doubles.toString().replaceFirst(",,", ""));
    	if(integers.length() > 0)
    		rowhash.put("integers", integers.toString().replaceFirst(",,", ""));
    	if(uppercase.length() > 0)
    		rowhash.put("uppercase", uppercase.toString().replaceFirst(",,", ""));
    	if(photos.length() > 0)
    		rowhash.put("photos", photos.toString().replaceFirst(",,", ""));
    	if(videos.length() > 0)
    		rowhash.put("videos", videos.toString().replaceFirst(",,", ""));
    	if(audio.length() > 0)
    		rowhash.put("audio", audio.toString().replaceFirst(",,", ""));
    	if(gps.length() > 0)
    		rowhash.put("gps", gps.toString().replaceFirst(",,", ""));
    	if(doublecheck.length() > 0)
    		rowhash.put("doublecheck", doublecheck.toString().replaceFirst(",,", ""));
    	if(genkey.length() > 0)
    		rowhash.put("genkey", genkey.toString().replaceFirst(",,", ""));
    	if(dates.length() > 0)
    		rowhash.put("dates", dates.toString().replaceFirst(",,", ""));
    	if(setdates.length() > 0)
    		rowhash.put("setdates", setdates.toString().replaceFirst(",,", ""));
    	if(crumbs.length() > 0)
    		rowhash.put("crumbs", crumbs.toString().replaceFirst(",,", ""));
    	if(settimes.length() > 0)
    		rowhash.put("settimes", settimes.toString().replaceFirst(",,", ""));
    	if(local.length() > 0)
    		rowhash.put("local", local.toString().replaceFirst(",,", ""));
    	if(totals.length() > 0)
    		rowhash.put("totals", totals.toString().replaceFirst(",,", ""));
    	if(jumps.length() > 0){
    		// Need to remove the whitespace
    		String temp = jumps.toString().replaceFirst(",,", "");
    		temp = temp.replaceAll("\\s*,\\s*", ",");
    		//double version = 1.0;
    		//try{
    		//	version = Double.parseDouble(project_version);
    		//}
    		//catch(NumberFormatException npe){
    			//Log.i("PARSING VERSION ERROR", npe.toString());
    		//}
    		//Log.i("PARSING VERSION WORKED", ""+version);
    		//if(version > 1.0){
    		//	Log.i("PARSING VERSION WORKED 2", ""+version);
    		//	rowhash.put("jumps1", temp); //jumps.toString().replaceFirst(",,", ""));
    		//}
    		//else{
    		//	Log.i("PARSING VERSION WORKED 3", ""+version);
    			rowhash.put("jumps", temp);
    		//}
    	}
    	if(jumps1.length() > 0){
    		// Need to remove the whitespace
    		
    		String temp = jumps1.toString().replaceFirst(",,", "");
    		//Log.i("JUMP1 FOUND", ""+temp);
    		temp = temp.replaceAll("\\s*,\\s*", ",");
    		rowhash.put("jumps1", temp); //jumps.toString().replaceFirst(",,", ""));
    	}
    	if(jumps2.length() > 0)
    		rowhash.put("jumps2", jumps2.toString().replaceFirst(",,", ""));
    	if(regex.length() > 0)
    		rowhash.put("regex", regex.toString().replaceFirst(",,", ""));
    	if(matches.length() > 0)
    		rowhash.put("matches", matches.toString().replaceFirst(",,", ""));
    	if(defaultvals.length() > 0)
    		rowhash.put("defaultvals", defaultvals.toString().replaceFirst(",,", ""));
    	if(mincheck.length() > 0)
    		rowhash.put("mincheck", mincheck.toString().replaceFirst(",,", ""));
    	if(maxcheck.length() > 0)
    		rowhash.put("maxcheck", maxcheck.toString().replaceFirst(",,", ""));
    	if(mincheck2.length() > 0)
    		rowhash.put("mincheck2", mincheck2.toString().replaceFirst(",,", ""));
    	if(maxcheck2.length() > 0)
    		rowhash.put("maxcheck2", maxcheck2.toString().replaceFirst(",,", ""));
    	if(search.length() > 0)
    		rowhash.put("search", search.toString().replaceFirst(",,", ""));
    	if(barcodes.length() > 0)
    		rowhash.put("barcodes", barcodes.toString().replaceFirst(",,", ""));
    	if(branch.length() > 0)
    		rowhash.put("branch", branch.toString().replaceFirst(",,", ""));
    	if(groups.length() > 0)
    		rowhash.put("groups", groups.toString().replaceFirst(",,", ""));
    	if(nodisplay.length() > 0)
    		rowhash.put("nodisplay", nodisplay.toString().replaceFirst(",,", ""));
    	if(noteditable.length() > 0)
    		rowhash.put("editable", noteditable.toString().replaceFirst(",,", ""));
    	if(requiredtext.length() > 0)
    		rowhash.put("requiredtext", requiredtext.toString().replaceFirst(",,", ""));
    	if(requiredspinners.length() > 0)
    		rowhash.put("requiredspinners", requiredspinners.toString().replaceFirst(",,", ""));
    	if(requiredradios.length() > 0)
    		rowhash.put("requiredradios", requiredradios.toString().replaceFirst(",,", ""));
    	
    	String listfieldsval = "";	
    	//if(usephonekey)
    	//	listfieldsval = "phonekey,,";
    	//if(listfields.length() > 0)
    	//	listfieldsval += listfields.toString().replaceFirst(",,", "");
    	//if(listfieldsval.length() > 0)
    	//	rowhash.put("listfields", listfieldsval);
    	if(listfields.length() > 0)
    		rowhash.put("listfields", listfields.toString().replaceFirst(",,", ""));
    	
    	if(listspinners.length() > 0)
    		rowhash.put("listspinners", listspinners.toString().replaceFirst(",,", ""));
    	if(listradios.length() > 0)
    		rowhash.put("listradios", listradios.toString().replaceFirst(",,", ""));
    	if(listcheckboxes.length() > 0)
    		rowhash.put("listcheckboxes", listcheckboxes.toString().replaceFirst(",,", ""));
    		
    	//rowhash.put("change_synch", changesynch);
    	//rowhash.put("remote_xml", remote_xml);
    	//rowhash.put("image_url", image_url);
    	//rowhash.put("synch_url", synch_url);
    	
    	for(String key : spinnerrefs.keySet()){
    		rowhash.put("'"+key+"'", spinnerrefs.get(key).toString());
		}
		
		for(String key : spinnervalues.keySet()){
			rowhash.put("'"+key+"'", spinnervalues.get(key).toString());
		}
		for(String key : radiorefs.keySet()){
    		rowhash.put("'"+key+"'", radiorefs.get(key).toString());
		}
		
		for(String key : radiovalues.keySet()){
			rowhash.put("'"+key+"'", radiovalues.get(key).toString());
		}
		
		for(String key : checkboxrefs.keySet()){
			rowhash.put("'"+key+"'", checkboxrefs.get(key).toString());
		}
		
		for(String key : checkboxvalues.keySet()){
			rowhash.put("'"+key+"'", checkboxvalues.get(key).toString());
		}
		
		rowhash.put("notes_layout", views.toString().replaceFirst(",,,", ""));
		
		rowhash.put("ecpkey", tablekey);
		
		//rowhash.put("show_previous", show_previous);
		
		//Log.i(getClass().getSimpleName(), "XML VIEW "+views.toString());
		
		tablerowshash.put(tablename, rowhash);
		
		//return rowhash;
		//dbAccess.createProjectRow(rowhash);
	}
	
	public HashMap<String, HashMap<String, String>> getRows(){
		
		 return tablerowshash;
	}
	
	public LinkedHashMap<String, HashMap<String, String>> getTablekeyhash(){
		
		return tablekeyhash;
	}
	    
	
	    public class XMLHandler extends DefaultHandler {
	   	 
	        boolean item = false;
	        boolean label = false;
	        boolean value = false;
	        //boolean submission = false;
	        //boolean allow = false;
	        boolean intype = false;
	        boolean inselect = false;
	        boolean inselect1 = false;
	        boolean inradio = false;
	        boolean ininput = false;
	        boolean download = false;
	        boolean download_local = false;
	        boolean upload = false;
	        boolean upload_local = false;
	        boolean button_url = false;
	        boolean remail = false;
	        boolean descrip = false;
	        boolean inbranch = false;
	        boolean ecdescrip = false;
	        //boolean image = false;
	        String ref = "", req="", num="", doubl="", upper="", dat="", tim="", setdat="", settime, dcheck="", gkey="", gkey2="", title="", loc="", tot="", radimages="", jump="", jump1="", jump2="", re_check="", match="", def_val="", min="", max="", min2="", max2="", tosearch="", nodisp="", edit="", bar="", branform="", group_num="", crumb="";
	        String thislabel = "";
	        StringBuilder builder;
	        

	        public void startElement(String uri, String qName, String localName, Attributes attributes) throws SAXException {
		    	
	        	builder = new StringBuilder();
	        
	        //FOR OLD VERSION
	        //if(attributes.getValue("ref") != null){
	        if(attributes.getValue("name") != null || attributes.getValue("ref") != null){
	        	ref = "";
	        	req = "";
	        	num = "";
	        	upper = "";
	        	doubl = "";
	        	dat = "";
	        	setdat = "";
	        	tim = "";
	        	settime = "";
	        	dcheck = "";
	        	gkey = "";
	        	gkey2 = "";
	        	title = "";
	        	loc = "";
	        	tot = "";
	        	radimages="";
	        	jump = "";
	        	jump1 = "";
	        	jump2 = "";
	        	re_check = "";
	        	re_check = "";
	        	match = "";
	        	def_val = "";
	        	min = "";
	        	max = "";
	        	min2 = "";
	        	max2 = "";
	        	tosearch = "";
	        	nodisp = "";
	        	edit = "";
	        	bar = "";
	        	branform="";
	        	group_num="";
	        	crumb="";
	        	ref = attributes.getValue("name");
	        	// FOR OLD VERSION
	        	if(ref == null)
	        		ref = attributes.getValue("ref");
	        	req = attributes.getValue("required");
	        	num = attributes.getValue("integer");
	        	upper = attributes.getValue("uppercase");
	        	doubl = attributes.getValue("decimal"); // double
	        	dat = attributes.getValue("date");
	        	setdat = attributes.getValue("setdate");
	        	tim = attributes.getValue("time");
	        	settime = attributes.getValue("settime");
	        	dcheck = attributes.getValue("verify"); // doublecheck inputcheck
	        	gkey = attributes.getValue("gen_key");
	        	gkey2 = attributes.getValue("genkey");
	        	title = attributes.getValue("title");
	        	loc = attributes.getValue("local");
	        	tot = attributes.getValue("total");
	        	radimages = attributes.getValue("images");
	        	jump = attributes.getValue("jump");
	        	jump1 = attributes.getValue("jump1");
	        	jump2 = attributes.getValue("jump2");
	        	re_check = attributes.getValue("regex");
	        	match = attributes.getValue("match");
	        	def_val = attributes.getValue("default");
	        	min = attributes.getValue("min");
	        	max = attributes.getValue("max");
	        	min2 = attributes.getValue("min2");
	        	max2 = attributes.getValue("max2");
	        	tosearch = attributes.getValue("search");
	        	nodisp = attributes.getValue("display");
	        	edit = attributes.getValue("edit");
	        	bar = attributes.getValue("barcode");
	        	branform = attributes.getValue("branch_form");
	        	group_num = attributes.getValue("group_num");
	        	crumb = attributes.getValue("crumb");
	        }
	    	  
	         if(qName.equalsIgnoreCase("form")){
	        	 
	        	 if(tablename.length() > 0){
	        		 createIndividualTable();
	        		 createRow();
	        	 }
	        	 clearAll();
	        	 HashMap<String, String> temptablekeyhash = new HashMap<String, String>();
	        	 try{
	        		 table_num = Integer.parseInt(attributes.getValue("num")); //++;
	        	 }
	        	 catch(NumberFormatException nfe){
	        		 table_num++;
	        	 }
	        	 temptablekeyhash.put("tablenum", ""+table_num); // ""+table_num
	        	 temptablekeyhash.put("tablename", attributes.getValue("name"));
	        	 tablename = attributes.getValue("name");
	        	 tablekey = attributes.getValue("key");
	        	 tablekey = tablekey.replaceAll(",", ";");
	        	 	        	 
	        	 // If no key is assigned to this table generate one
	        	/* if(tablekey.length() == 0){
		          		textviews.append(",,"+"eckeyid");
		          		genkey.append(",,"+"eckeyid");
	          			nodisplay.append(",,"+"eckeyid");
	          			//usephonekey = true;
	          			//listfields.insert(0, ",,phonekey");
	        	 }*/
	        	         	 
	        	 temptablekeyhash.put("tablekey", tablekey); 
	        	 //show_previous = "false";
	        	 
	        	 String maintable = "1";
	        	 
	        	 String group = "0";
	        	 
	        	 try{
	        		 if(attributes.getValue("main").equalsIgnoreCase("false"))
	        			 maintable = "0";
	        	 }
	        	 catch(NullPointerException npe){
	        		 maintable = "1";
	        	 }
	        	 if(maintable == null)
	        		 maintable = "1";
	        	 
	        	 try{
	        		 group = attributes.getValue("group");
	        	 }
	        	 catch(NullPointerException npe){
	        		 group = "0";
	        	 }
	        	 if(group == null)
	        		 group = "0";
	        	 
	        	 if(!group.equalsIgnoreCase("0"))
	        		 maintable = "0";
	        	 
	        	 temptablekeyhash.put("fromgroup", group); 

	        	 tablekeyhash.put(tablename, temptablekeyhash);
	        	 	
	        	 if(branchesvec.contains(tablename))
	        		 maintable = "0";
	        	/* String caller_form = "";
	        	 try{
	        		 caller_form = attributes.getValue("callerform");
	        	 }
	        	 catch(NullPointerException npe){
	        		 caller_form = "";
	        	 }
	        	 if(caller_form == null)
	        		 caller_form = "";
	        	 
	        	 callerkey_req = false;
	        	 if(caller_form.length() > 0){
	        		 callerkey_req = true;
	        		 maintable = "0";
	        	 }*/
	        	 
	//Log.i("CALLER W", tablename+" "+caller_form);     
	        	// temptablekeyhash.put("branch_caller", caller_form);  
	        	 
	        	 temptablekeyhash.put("maintable", maintable); 
	        	 
	        	 if(maintable.equalsIgnoreCase("1"))
	        		 foreignkeys_toset.put(table_num + 1, tablekey);
		 
	        	/* String caller_key = "";
	        	 try{
	        		 caller_key = attributes.getValue("callerkey");
	        	 }
	        	 catch(NullPointerException npe){
	        		 caller_key = "";
	        	 }
	        	 if(caller_key == null)
	        		 caller_key = "";

	        	 temptablekeyhash.put("branch_caller_key", caller_key); */

	        	 
	        	 
	        	 //tablekey = "";
	        	 
	        	 //try{
	        	//	 show_previous = attributes.getValue("show_previous");
	        	// }
	        	// catch(Exception e){}
	         }
	         
	      // FOR OLD VERSION
	         if(qName.equalsIgnoreCase("table_data")){
	        	 
	        	 if(tablename.length() > 0){
	        		 createIndividualTable();
	        		 createRow();
	        	 }
	        	 clearAll();
	        	 HashMap<String, String> temptablekeyhash = new HashMap<String, String>();
	        	 try{
	        		 table_num = Integer.parseInt(attributes.getValue("table_num")); //++;
	        	 }
	        	 catch(NumberFormatException nfe){
	        		 table_num++;
	        	 }
	        	 temptablekeyhash.put("tablenum", ""+table_num); //attributes.getValue("table_num"));
	        	 temptablekeyhash.put("tablename", attributes.getValue("table_name"));
	        	 tablename = attributes.getValue("table_name");
	        	 tablekey = attributes.getValue("table_key");
	        	 tablekey = tablekey.replaceAll(",", ";");
	        	 temptablekeyhash.put("fromgroup", "0"); 
	        	 temptablekeyhash.put("maintable", "1"); 
	        	 
	        	// If no key is assigned to this table generate one
	        	 /*if(tablekey.length() == 0){
		          		textviews.append(",,"+"eckeyid");
		          		genkey.append(",,"+"eckeyid");
	          			nodisplay.append(",,"+"eckeyid");
	          			//usephonekey = true;
	          			//listfields.insert(0, ",,phonekey");
	        	 }*/
	        	 
	        	 temptablekeyhash.put("tablekey", tablekey); //attributes.getValue("table_key"));
	        	 //show_previous = "false";
	        	 tablekeyhash.put(tablename, temptablekeyhash);
	        	 
	        	 //try{
	        	//	 show_previous = attributes.getValue("show_previous");
	        	// }
	        	 //catch(Exception e){}
	         } 
	        
	         if(qName.equalsIgnoreCase("ecdescription")){
	    		  ecdescrip = true;
	    	  }
	          if(qName.equalsIgnoreCase("downloadFromServer")){
	    		  download = true;
	    	  }
	          if(qName.equalsIgnoreCase("downloadFromLocalServer")){
	    		  download_local = true;
	    	  }
	    	  if(qName.equalsIgnoreCase("uploadToServer")){
	    		  upload = true;
	    	  }
	    	  if(qName.equalsIgnoreCase("uploadToLocalServer")){
	    		  upload_local = true;
	    	  }
	    	  if(qName.equalsIgnoreCase("downloadRadioImages")){
	    		  button_url = true;
	    	  }
	    	  if(qName.equalsIgnoreCase("description")){
	    		  descrip = true;
	    	  }
	    	  if(qName.equalsIgnoreCase("registrationEmail")){
	    		  remail = true;
	    	  }
	    	  
	    	  if(types.contains(qName)){
	          	intype = true;
	          	if(qName.equalsIgnoreCase("textarea")){
	          		views.append(",,,"+"input");
	          	}
	          	else{
	          		views.append(",,,"+qName);
	          	}
	          	views.append(",,"+ref);
	          	if(qName.equalsIgnoreCase("select")){
	          		inselect = true;
	          		checkboxgroups.append(",,"+ref);
	          	}
	          	else
	          		inselect = false;
	          	
	          	if(qName.equalsIgnoreCase("select1")){
	          		inselect1 = true;
	          		spinners.append(",,"+ref);
	          		if(req != null && req.equalsIgnoreCase("true"))
	          			requiredspinners.append(",,"+ref);
	          		if(title != null && title.equalsIgnoreCase("true"))
	          			listspinners.append(",,"+ref);
	          	}
	          	else
	          		inselect1 = false;
	          	
	          	if(qName.equalsIgnoreCase("radio")){
	          		inradio = true;
	          		radios.append(",,"+ref);
	          		if(req != null && req.equalsIgnoreCase("true"))
	          			requiredradios.append(",,"+ref);
	          		if(title != null && title.equalsIgnoreCase("true"))
	          			listradios.append(",,"+ref);
	          		if(radimages != null && radimages.equalsIgnoreCase("true")){
	          			radioimages.append(",,"+ref);
	          		}
	          	}
	          	else
	          		inradio = false;
	          	
	          	if(qName.equalsIgnoreCase("input") || qName.equalsIgnoreCase("textarea") || qName.equalsIgnoreCase("barcode") || qName.equalsIgnoreCase("group")){
	          		ininput = true;
	          		textviews.append(",,"+ref);
	          		
	          		// If the foreign key is set then remove it from the hash
	          		if(table_num > 1 && foreignkeys_toset.get(table_num) != null && foreignkeys_toset.get(table_num).equals(ref))
	          			foreignkeys_toset.remove(table_num);
	          		
	          		/*if(callerkey_req && tablekeyhash.get(tablekeyhash.get(tablename).get("branch_caller")).get("tablekey").equals(ref))
	          			callerkey_req = false;*/
	          		
	          		if(req != null && req.equalsIgnoreCase("true")){
	          			requiredtext.append(",,"+ref);
	          		}
	          		if(title != null && title.equalsIgnoreCase("true"))
	          			listfields.append(",,"+ref);
	          		if(num != null && num.equalsIgnoreCase("true"))
	          			integers.append(",,"+ref);
	          		if(doubl != null && doubl.equalsIgnoreCase("true"))
	          			doubles.append(",,"+ref);
	          		if(upper != null && upper.equalsIgnoreCase("true"))
	          			uppercase.append(",,"+ref);
	          		if(dat != null)
	          			dates.append(",,"+ref+"\t"+dat);
	          		if(setdat != null)
	          			setdates.append(",,"+ref+"\t"+setdat);
	          		if(crumb != null)
	          			crumbs.append(",,"+ref+"\t"+crumb);
	          		if(tim != null)
	          			dates.append(",,"+ref+"\t"+tim);
	          		if(settime != null)
	          			settimes.append(",,"+ref+"\t"+settime);
	          		if(dcheck != null && dcheck.equalsIgnoreCase("true"))
	          			doublecheck.append(",,"+ref);
	          		if(gkey != null && gkey.equalsIgnoreCase("true")){
	          			genkey.append(",,"+ref);
	          			//usephonekey = true;
	          			//listfields.insert(0, ",,phonekey");
	          		}	
	          		if(gkey2 != null && gkey2.equalsIgnoreCase("true")){
	          			genkey.append(",,"+ref);
	          		}	
	          		if(loc != null && loc.equalsIgnoreCase("true"))
	          			local.append(",,"+ref);
	          		if(tot != null){ // && tot.length() > 0){
	          			totals.append(",,"+ref+"\t"+tot);
	          		}
	          		if(jump != null){
	          			jumps.append(",,"+ref+","+jump);
	          		}
	          		if(jump1 != null){
	          			jumps1.append(",,"+ref+","+jump1);
	          		}
	          		if(jump2 != null){
	          			jumps2.append(",,"+ref+","+jump2);
	          		}
	          		if(re_check != null){
	          			regex.append(",,"+ref+"\t"+re_check);
	          		}
	          		if(match != null){
	          			matches.append(",,"+ref+"\t"+match);
	          		}
	          		if(def_val != null){
	          			defaultvals.append(",,"+ref+"\t"+def_val);
	          		}
	          		if(min != null){
	          			mincheck.append(",,"+ref+"\t"+min);
	          		}
	          		if(max != null){
	          			maxcheck.append(",,"+ref+"\t"+max);
	          		}
	          		if(min2 != null){
	          			mincheck2.append(",,"+ref+","+min2);
	          		}
	          		if(max2 != null){
	          			maxcheck2.append(",,"+ref+","+max2);
	          		}
	          		if(tosearch != null && tosearch.equalsIgnoreCase("true")){
	          			search.append(",,"+ref);
	          		}
	          		if(nodisp != null && nodisp.equalsIgnoreCase("false"))
	          			nodisplay.append(",,"+ref);
	          		if(edit != null && edit.equalsIgnoreCase("false"))
	          			noteditable.append(",,"+ref);
	          		if(bar != null && bar.equalsIgnoreCase("true"))
	          			barcodes.append(",,"+ref);
	          		if(group_num != null)
	          			groups.append(",,"+ref+"\t"+group_num);
	          		
	          	}
	          	else if(qName.equalsIgnoreCase("photo")){
	          		photos.append(",,"+ref);
	          		if(loc != null && loc.equalsIgnoreCase("true"))
	          			local.append(",,"+ref);
	          		
	          	}
	          	else if(qName.equalsIgnoreCase("branch")){
	          		branch.append(",,"+ref);
	          		inbranch = true;
	          	      		
	          	}
	          	else if(qName.equalsIgnoreCase("video")){
	          		videos.append(",,"+ref);
	          		if(loc != null && loc.equalsIgnoreCase("true"))
	          			local.append(",,"+ref);
	          		
	          	}
	          	else if(qName.equalsIgnoreCase("audio")){
	          		audio.append(",,"+ref);
	          		if(loc != null && loc.equalsIgnoreCase("true"))
	          			local.append(",,"+ref);
	          		
	          	}
	          	else if(qName.equalsIgnoreCase("location") || qName.equalsIgnoreCase("gps")){
	          		gps.append(",,"+ref);
	          		if(req != null && req.equalsIgnoreCase("true"))
	          			requiredtext.append(",,"+ref);
	          		if(loc != null && loc.equalsIgnoreCase("true"))
	          			local.append(",,"+ref);
	          	}
	          	else{
	          		if(def_val != null)
	          			defaultvals.append(",,"+ref+"\t"+def_val);
	          		if(jump != null)
	          			jumps.append(",,"+ref+","+jump);
	          		if(jump1 != null)
	          			jumps1.append(",,"+ref+","+jump1);
	          		if(jump2 != null)
	          			jumps2.append(",,"+ref+","+jump2);
	          		if(nodisp != null && nodisp.equalsIgnoreCase("false"))
	          			nodisplay.append(",,"+ref);
	          		if(edit != null && edit.equalsIgnoreCase("false"))
	          			noteditable.append(",,"+ref);
	          		if(tosearch != null && tosearch.equalsIgnoreCase("true"))
	          			search.append(",,"+ref);
	          		ininput = false;
	          	}
	          }

	            if (qName.equalsIgnoreCase("ITEM")) {
	          	  item = true;
	            }

	            if (qName.equalsIgnoreCase("LABEL")) {
	          	  label = true;
	            }

	            if (qName.equalsIgnoreCase("VALUE")) {
	            	//if(inselect)
	            	//	views.append(",,"+ref);
	          	  value = true;
	            }

	            // Old version (SCORE) - can be deleted
	            if (qName.equalsIgnoreCase("SUBMISSION")) {
	          	  //System.out.println("ID: "+attributes.getValue("id"));
	          	  //System.out.println("Project: "+attributes.getValue("projectName"));
	          	  //System.out.println("Edits: "+attributes.getValue("allowDownloadEdits"));
	          	  if(attributes.getValue("projectName") != null)
	          		  project = attributes.getValue("projectName");
	          	  app_name = "EpiCollect "+project;
	          	  if(attributes.getValue("allowDownloadEdits") != null)
	          		  changesynch = attributes.getValue("allowDownloadEdits");
	          	  //submission = true;
	              //if(attributes.getValue("type") != null)
		    	//	  project_type = attributes.getValue("type");
			          	  
	            }
	            
	            // New version
	            if (qName.equalsIgnoreCase("PROJECT")) {
		          	  if(attributes.getValue("name") != null)
		          		  project = attributes.getValue("name");
		          	  app_name = "EpiCollect "+project;
		          	  if(attributes.getValue("allowDownloadEdits") != null)
		          		  changesynch = attributes.getValue("allowDownloadEdits");
	          	  
		            }
	            // New version
	            if (qName.equalsIgnoreCase("SERVER")) {
		          	  if(attributes.getValue("name") != null)
		          		  server = attributes.getValue("server");
		          	  //else
		          	  //	  server = Epi_collect.project_server; //dbAccess.getSettings("url"); 
		          	  
		          	  if(attributes.getValue("downloadFromServer") != null)
		          		  remote_xml = attributes.getValue("downloadFromServer");
		          	  else
		          		  remote_xml = server + "/download";
		          	  
		          	  if(attributes.getValue("uploadToServer") != null)
		          		  synch_url = attributes.getValue("uploadToServer");
		          	  else
		          		  synch_url = server + "/upload";
		          	  
			          if(attributes.getValue("downloadFromLocalServer") != null)
			        	  local_remote_xml = attributes.getValue("downloadFromLocalServer");
			    	  
			    	  if(attributes.getValue("uploadToLocalServer") != null)
			    		  synch_local_url = attributes.getValue("uploadToLocalServer"); 
			    	  
			    	  /*  if(attributes.getValue("description") != null)
			    		  project_description = attributes.getValue("description");
			    	  
			    	  if(attributes.getValue("registrationEmail") != null)
			    	  	  reg_email = attributes.getValue("registrationEmail"); */
			    	  
		        	} 
	            
	    	 
	            if(qName.equalsIgnoreCase("ECML")){
	            	if(attributes.getValue("version") != null)
		    		  project_version = attributes.getValue("version");
		    	}
	            	            
	            if (qName.equalsIgnoreCase("INPUT")) {
	          	  // Initialise text view here and complete in characters
	          	  //System.out.println("Ref: "+attributes.getValue("ref"));
	          	  //System.out.println("Required: "+attributes.getValue("required"));
	          	  //System.out.println("Numeric: "+attributes.getValue("numeric"));
	          	 // submission = true;
	            }
	            
	          }
	        

	         // public void endElement(String uri, String qName, String localName) throws SAXException {
	          public void characters(char ch[], int start, int length) throws SAXException {
	               // System.out.println("End Element :" + qName);
	        	  builder.append(ch,start,length);
	          }

	        //  public void characters(char ch[], int start, int length) throws SAXException {
	        public void endElement(String uri, String qName, String localName) throws SAXException {
	        	  
	        	if(ecdescrip){
		    		  project_description = builder.toString(); //new String(ch, start, length);
		    		  Log.i("EC DESCRIPTION", builder.toString());
		    		  ecdescrip = false;
		    	  }
	        	  if(download){
		    		  remote_xml = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML REMOTE "+remote_xml);
		    		  download = false;
		    	  }
	        	  if(download_local){
		    		  local_remote_xml = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML REMOTE "+remote_xml);
		    		  download_local = false;
		    	  }
		    	  //if(image){
		    		//  image_url = new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML IMAGE "+image_url);
		    		//  image = false;
		    	  //}
		    	  if(upload){
		    		  synch_url = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML SYNCH "+synch_url);
		    		  upload = false;
		    	  }
		    	  if(upload_local){
		    		  synch_local_url = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML SYNCH "+synch_url);
		    		  upload_local = false;
		    	  }
		    	  if(button_url){
		    		  button_image_url = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML SYNCH "+synch_url);
		    		  button_url = false;
		    	  }
		    	  
		    	  if(descrip){
		    		  project_description = builder.toString(); //new String(ch, start, length);
		    		  descrip = false;
		    	  }
		    	  
		    	 if(remail){
		    		  reg_email = builder.toString(); //new String(ch, start, length);
		    		  //Log.i(getClass().getSimpleName(), "XML SYNCH "+synch_url);
		    		  remail = false;
		    	  }
		    	  
	            if (item) {
	              //System.out.println("In Item");
	              item = false;
	            }

	            if (label) {
	            	thislabel = builder.toString(); //new String(ch, start, length);
	                //System.out.println("Label : "+ new String(ch, start, length));
	                label = false;
	                if(intype){
	              	  views.append(",,"+builder.toString()); //new String(ch, start, length));
	              	  intype = false;
	                }
	                else if(inselect){
	              	  views.append(",,"+builder.toString()); //new String(ch, start, length)); //ch.length));   
	              	  //checkboxes.append(",,"+ref+"_"+new String(ch, start, length));
	                }
	                if(inbranch){
	                	views.append(",,"+branform);  
	                	inbranch = false;
	                	branchesvec.addElement(branform);
	                }
	             }

	            if (value) {
	                //System.out.println("Value : "+ new String(ch, start, length));
	                
	                if(inselect){
	                	checkboxes.append(",,"+ref+"_"+builder.toString()); //new String(ch, start, length));
	                	views.append(",,"+builder.toString()); //new String(ch, start, length));    
	                	//views.append(",,"+ref);
	                	if(checkboxrefs.get("checkbox_"+ref) == null){
	                		checkboxrefs.put("checkbox_"+ref, new StringBuffer(ref+"_"+builder.toString())); //new String(ch, start, length)));
	                	}
	                	else
	                		checkboxrefs.get("checkbox_"+ref).append(",,"+ref+"_"+builder.toString()); //new String(ch, start, length));
	                	
	                	if(checkboxvalues.get("checkbox_values_"+ref) == null){
	                		checkboxvalues.put("checkbox_values_"+ref, new StringBuffer(builder.toString())); //new String(ch, start, length)));
	                	}
	                	else
	                		checkboxvalues.get("checkbox_values_"+ref).append(",,"+builder.toString()); //new String(ch, start, length));
	                	
	                	//checkboxes.append(",,"+ref+"_"+new String(ch, start, length));
	                	if(title != null && title.equalsIgnoreCase("true"))
		          			listcheckboxes.append(",,"+ref+"_"+builder.toString()); //new String(ch, start, length));
	                }
	                
	                if(inselect1){
	                	if(spinnerrefs.get("spinner_"+ref) == null)
	                		spinnerrefs.put("spinner_"+ref, new StringBuffer("Select"));
	                	
	                	spinnerrefs.get("spinner_"+ref).append(",,"+thislabel);
	                	
	                	if(spinnervalues.get("spinner_values_"+ref) == null)
	                		spinnervalues.put("spinner_values_"+ref, new StringBuffer("Null"));
	                	
	                	spinnervalues.get("spinner_values_"+ref).append(",,"+builder.toString()); //new String(ch, start, length));
	                	
	                }
	                
	                if(inradio){
	                	if(radiorefs.get("radio_"+ref) == null)
	                		radiorefs.put("radio_"+ref, new StringBuffer());
	                	
	                	radiorefs.get("radio_"+ref).append(",,"+thislabel);
	                	
	                	if(radiovalues.get("radio_values_"+ref) == null)
	                		radiovalues.put("radio_values_"+ref, new StringBuffer());
	                	
	                	radiovalues.get("radio_values_"+ref).append(",,"+builder.toString()); //new String(ch, start, length));
	                	
	                }
	                
	                value = false;
	             }

	            //if (submission) {
	            //    System.out.println("Submission : "
	            //        + new String(ch, start, length));
	            //    submission = false;
	            // }

	           // System.out.println("FINAL : " + new String(ch, start, length));
	          }

	          
	        }


	    private void clearAll(){
	    	views.setLength(0); // = new StringBuffer("");
	    	textviews.setLength(0); // = new StringBuffer("");
	    	checkboxes.setLength(0); // = new StringBuffer(""); 
	    	spinners.setLength(0); // = new StringBuffer("");
	    	radios.setLength(0); // = new StringBuffer("");
	    	checkboxgroups.setLength(0); // = new StringBuffer("");
	    	doubles.setLength(0); // = new StringBuffer("");
	    	integers.setLength(0); // = new StringBuffer("");
	    	uppercase.setLength(0); // = new StringBuffer("");
	    	photos.setLength(0); // = new StringBuffer("");
	    	videos.setLength(0); // = new StringBuffer("");
	    	audio.setLength(0); // = new StringBuffer("");
	    	gps.setLength(0); // = new StringBuffer("");
	    	dates.setLength(0); // = new StringBuffer("");
	    	setdates.setLength(0); // = new StringBuffer("");
	    	settimes.setLength(0); // = new StringBuffer("");
	    	crumbs.setLength(0); // = new StringBuffer("");
	    	doublecheck.setLength(0); // = new StringBuffer("");
	    	genkey.setLength(0); // = new StringBuffer("");
	    	local.setLength(0); // = new StringBuffer("");
	    	totals.setLength(0); // = new StringBuffer("");
	    	radioimages.setLength(0); // = new StringBuffer("");
	    	jumps.setLength(0); // = new StringBuffer("");
	    	jumps1.setLength(0); // = new StringBuffer("");
	    	jumps2.setLength(0); // = new StringBuffer("");
	    	regex.setLength(0); // = new StringBuffer("");
	    	matches.setLength(0); // = new StringBuffer("");
	    	defaultvals.setLength(0); // = new StringBuffer("");
	    	mincheck.setLength(0); // = new StringBuffer("");
	    	maxcheck.setLength(0); // = new StringBuffer("");
	    	mincheck2.setLength(0); // = new StringBuffer("");
	    	maxcheck2.setLength(0); // = new StringBuffer("");
	    	search.setLength(0); // = new StringBuffer("");
	    	nodisplay.setLength(0); // = new StringBuffer("");
	    	noteditable.setLength(0); // = new StringBuffer("");
	    	requiredtext.setLength(0); // = new StringBuffer("");
	    	requiredspinners.setLength(0); // = new StringBuffer(""); 
	    	requiredradios.setLength(0); // = new StringBuffer("");
	    	listfields.setLength(0); // = new StringBuffer("");
	    	listspinners.setLength(0); // = new StringBuffer("");
	    	listradios.setLength(0); // = new StringBuffer("");
	    	listcheckboxes.setLength(0); // = new StringBuffer("");
	    	barcodes.setLength(0); // = new StringBuffer("");
	    	groups.setLength(0); // = new StringBuffer("");
	    	branch.setLength(0); // = new StringBuffer("");
	    	spinnerrefs.clear(); // = new HashMap<String, StringBuffer>();
	    	spinnervalues.clear(); // = new HashMap<String, StringBuffer>();
	    	radiorefs.clear(); // = new HashMap<String, StringBuffer>();
	    	radiovalues.clear(); // = new HashMap<String, StringBuffer>();
	    	radioimages.setLength(0); // = new StringBuffer("");
	    	checkboxrefs.clear(); // = new HashMap<String, StringBuffer>();
	    	checkboxvalues.clear(); // = new HashMap<String, StringBuffer>();
	    	//usephonekey = false;
	    	//String app_name="", project="", changesynch = "false";
	    	//String url_string = "";
	    }
	    
	
	//private int getLocID(String loc){

//public static void main(String[] args) {
//	new ParseXML();
//}
}