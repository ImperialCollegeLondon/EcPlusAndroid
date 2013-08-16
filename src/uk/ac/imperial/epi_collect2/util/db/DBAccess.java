package uk.ac.imperial.epi_collect2.util.db;

//import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//import java.io.FileReader;
import java.io.FileWriter;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
//import java.net.URLEncoder;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Calendar;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
//import java.util.TimeZone;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.net.URLConnection;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.imperial.epi_collect2.Epi_collect;
import uk.ac.imperial.epi_collect2.R;

//import uk.ac.imperial.epi_collect2.util.xml.ParseXML.XMLHandler;

//import android.telephony.TelephonyManager;
import android.accounts.Account;
import android.accounts.AccountManager;
//import android.annotation.SuppressLint;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
//import android.telephony.TelephonyManager;
import android.telephony.TelephonyManager;
import android.util.Log; 	 
import android.widget.Toast;


//@SuppressLint("NewApi")
@SuppressLint("NewApi")
public class DBAccess {
    public class Row extends Object {
    	//public long rowId;
    	//public String remoteId;
    	public Hashtable<String, String> spinners = new Hashtable<String, String>();
    	public Hashtable<String, String> radios = new Hashtable<String, String>();
        //public String gpslat;
        //public String gpslon;
        //public String gpsalt;
        //public String gpsacc;
        public Hashtable<String, String> photoids = new Hashtable<String, String>();
        public Hashtable<String, String> videoids = new Hashtable<String, String>();
        public Hashtable<String, String> audioids = new Hashtable<String, String>();
        public String date;
        public String stored;
        public String ectitle;
        public String ecpkey;
        public String ecphonekey;
        public Hashtable<String, String> datastrings = new Hashtable<String, String>();
        public Hashtable<String, Boolean> checkboxes = new Hashtable<String, Boolean>();
        public Hashtable<String, Integer> rgroups = new Hashtable<String, Integer>();
        public boolean remote = false;
    }

    private static final String TAG = "EPI_Table";
    private static final String DATABASE_NAME = "epi_collect";
    private final static String PROJECT_URL = "http://plus.epicollect.net";
    private String DATABASE_TABLE = "data";
    private String DATABASE_PROJECT = "";
    //private static final String DATABASE_TABLE_PROJECTS = "projects";
    private static final int DATABASE_VERSION = 3;
    private final Context mCtx;
    //private static String thumbdir; 
    private SQLiteDatabase db;
    private static String[] textviews = new String[0];
    private static String[] photos = new String[0];
    private static String[] videos = new String[0];
    private static String[] audio = new String[0];
    private static String[] spinners = new String[0];
    private static String[] radios = new String[0];
    private static String[] checkboxes = new String[0];
    private static String[] checkboxgroups = new String[0];
    private static String[] gpstags = new String[0];
    private static Vector<String> textviews2;
    private static Vector<String> photos2;
    private static Vector<String> videos2;
    private static Vector<String> audio2;
    private static Vector<String> spinners2;
    private static Vector<String> radios2;
    private static Vector<String> checkboxes2;
    private static Vector<String> checkboxgroups2;
    private static Vector<String> gpstags2;
    private static Vector<String> doubles = new Vector<String>();
    private static Vector<String> integers = new Vector<String>();
    private static Vector<String> uppercase = new Vector<String>();    
    private static Vector<String> local = new Vector<String>();
    //private static Hashtable <String, String[]>spinnershash = new Hashtable <String, String[]>();
    //private static Hashtable <String, String[]>radioshash = new Hashtable <String, String[]>();
    private static Hashtable <String, String[]>checkboxhash = new Hashtable <String, String[]>();
    //private static Hashtable <String, String[]>spinnersvalueshash = new Hashtable <String, String[]>();
    //private static Hashtable <String, String[]>radiosvalueshash = new Hashtable <String, String[]>();
    private static HashMap <String, String>checkboxvaluesvalueshash = new HashMap <String, String>();
    private Hashtable<String, String>setdates; //, dates
    //private static Vector<String> requiredfields, requiredspinners;//, requiredradios;
    private static Vector<String> listfields=new Vector<String>(), listspinners=new Vector<String>(), listradios=new Vector<String>(); //, listcheckboxes=new Vector<String>();
    private HashMap<Integer, String> orderedtables = new HashMap<Integer, String>();
	private HashMap<String, Integer> orderedtablesrev = new HashMap<String, Integer>();
	private static final int BUFFER = 2048; // For zip
    //private static String synch_url; //, login_url;
	private static Context thiscontext;
    
    /**
     * This class helps open and create the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
        	super(context, DATABASE_NAME, null, DATABASE_VERSION);
        	thiscontext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL("DROP TABLE IF EXISTS projects");
        	db.execSQL("create table projects (project text primary key, active text);");
        	db.execSQL("DROP TABLE IF EXISTS firstrun");
        	db.execSQL("create table firstrun (demoloaded int, id int primary key);");
        	db.execSQL("replace into firstrun values(0, 1)");
        	db.execSQL("DROP TABLE IF EXISTS settings");
        	db.execSQL("create table settings (url text, id int primary key);");
        	db.execSQL("replace into settings values('"+ PROJECT_URL +"', 1)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS data");
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;
   
    public DBAccess (Context ctx) {
        this.mCtx = ctx;
    }	
    
    public DBAccess open() throws SQLException {
    	mOpenHelper = new DatabaseHelper(mCtx);
    	db = mOpenHelper.getWritableDatabase();
    	return this;
    }

    public void close() {
    	mOpenHelper.close();
    }
    
    public void setFirstrun(){
    	
    	db.execSQL("update firstrun set demoloaded = 1 where id = 1");
    	
    	//Cursor c = db.rawQuery("select demoloaded from firstrun", null);
        //if (c.getCount() > 0) {
        //    c.moveToFirst();
            //Log.i(getClass().getSimpleName(), "UPDATED FIRST RUN = " + c.getInt(0));
        //}
        //c.close();
    	
    }
    
    public int getFirstrun(){
    	int loaded = 0;
    	Cursor c = db.rawQuery("select demoloaded from firstrun", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            loaded = c.getInt(0);
            //Log.i(getClass().getSimpleName(), "DBACCESS FIRST RUN = " + c.getInt(0));
        }
        c.close();
        return loaded;
    }
    
    public void dropTable(String table){
    	db.execSQL("drop table if exists "+table);
    }
   
    public void createProjectTable(StringBuffer table, String project){
    	db.execSQL(table.toString());  
    	//Log.i("DATABASE 1", table.toString());
    }
    
    public void createFileTable(String table, String project){
    	db.execSQL("drop table if exists "+project+"_"+table);
    	StringBuffer sb = new StringBuffer("create table "+project+"_"+table+" (id text primary key, synch text default \"N\");");
    	db.execSQL(sb.toString());
    }
    
    public void createDataTable( String project, String table, String rtable, String branch_caller, String branch_caller_key){ //, String tablekey){
    	setActiveProject(project);
    	getValues(table);
    	
    	branch_caller_key = branch_caller_key.replaceAll(";", ",");
    	
    	// Can't use "if not exists" with fts 3, so ...
    	db.execSQL("drop table if exists data_"+project+"_"+table);
    	//StringBuffer sb = new StringBuffer("create virtual table data_"+project+"_"+table+" using fts3 (ecstored text");
    	StringBuffer sb = new StringBuffer("create table data_"+project+"_"+table+" (ecstored text");
    	
    	for(String key : textviews){
    		//if(key.equalsIgnoreCase("location") || key.equalsIgnoreCase("GPS")){
    		//	sb.append(", 'eclat' text, 'eclon' text, 'ecalt' text, 'ecgpsacc' text, 'ecgpsprovider' text");
    		//}
    		//else{
    			sb.append(", '"+key+"' text");
    			//if(key.equalsIgnoreCase(tablekey))
        		//	sb.append(" primary key");
      		//}
    	}
    	
    	for(String key : spinners){
    		sb.append(", '"+key+"' text");
    		//if(key.equalsIgnoreCase(tablekey))
    		//	sb.append(" primary key");
    	}
    	
    	for(String key : radios){
    		sb.append(", '"+key+"' text");
    		//if(key.equalsIgnoreCase(tablekey))
    		//	sb.append(" primary key");
    	}
    	
    	for(String key : checkboxes){
    		sb.append(", '"+key+"' text");
    	}
    	
    	for(String key : photos){
    		sb.append(", '"+key+"' text");
    	}
    	
    	for(String key : videos){
    		sb.append(", '"+key+"' text");
    	}
    	
    	for(String key : audio){
    		sb.append(", '"+key+"' text");
    	}
    	
    	// Default N/A for acc as iPhone does not return this value
    	for(String key : gpstags){
    		sb.append(", '"+key+"_lat' text, '"+key+"_lon' text, '"+key+"_alt' text, '"+key+"_acc' text default 'N/A', '"+key+"_bearing' text default 'N/A', '"+key+"_provider' text default 'N/A'");
    	}
    	
    	sb.append(", ecremote int");
    	
    	sb.append(", ecdate int"); 
    	
    	sb.append(", ectitle text");
    	
    	sb.append(", ecpkey text primary key");
    	
    	sb.append(", ecphonekey int");
    	
    	sb.append(", message text");
    	 	
    	if(branch_caller != null && branch_caller.length() > 0){
    		sb.append(", ecfkey text, FOREIGN KEY (ecfkey) REFERENCES data_"+project+"_"+branch_caller+"(ecpkey));"); //"+branch_caller_key+"  "+branch_caller_key+"
    		//Log.i("BRANCH CASCADE", "FOREIGN KEY (Caller_Key) REFERENCES data_"+project+"_"+branch_caller+"("+branch_caller_key+"))");
    		
    	}
    	else{
    		if(!rtable.equals("Null")){
    	    	sb.append(", ecfkey text, ecjumped text, FOREIGN KEY (ecfkey) REFERENCES data_"+project+"_"+rtable+"(ecpkey));");
    	    	//Log.i("FOREIGN KEY", "FOREIGN KEY (fkey) REFERENCES data_"+project+"_"+rtable+"(pkey))");
    		}
    		else
    			sb.append(", ecfkey text, ecjumped text);");
    	}
    	    			
    	//sb.append(", ecjumped text);");
    	
    	//Log.i("DATABASE 2", "CREATE TRIGGER "+project+"_fkd_"+rtable+" BEFORE DELETE ON data_"+project+"_"+rtable+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".fkey = OLD.pkey; END;");
    	Log.i("DATABASE 2", sb.toString());
    	
    	db.execSQL(sb.toString());
    	
    	//String[] temp = value.split(",");
    	//String query = "select pkey from data_"+DATABASE_PROJECT+"_"+nexttable+" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	//for(int i = 2; i < temp.length; i+=2){
    	//	query += " and \"" + temp[i] + " = \""+temp[i+1]+"\""; 
    	//}
    	if(!rtable.equals("Null")){
    		db.execSQL("CREATE TRIGGER "+project+"_fkd_"+rtable+" BEFORE DELETE ON data_"+project+"_"+rtable+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".ecfkey = OLD.ecpkey; END;");
    		//Log.i("NORMAL TRIGGER", "CREATE TRIGGER "+project+"_fkd_"+rtable+" BEFORE DELETE ON data_"+project+"_"+rtable+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".fkey = OLD.pkey; END;");
    	}
    	if(branch_caller != null && branch_caller.length() > 0){
    		Log.i("BRANCH CASCADE 2", "CREATE TRIGGER "+project+"_fkd_"+table+" BEFORE DELETE ON data_"+project+"_"+branch_caller+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+"."+branch_caller_key+" = OLD.Caller_Key; END;");
        	db.execSQL("CREATE TRIGGER "+project+"_fkd_"+table+" BEFORE DELETE ON data_"+project+"_"+branch_caller+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".ecfkey = OLD.ecpkey; END;");
    	}
    	
    	/*if(!rtable.equals("Null"))
    		sb.append(", fkey text, FOREIGN KEY (fkey) REFERENCES data_"+project+"_"+rtable+"(pkey));"); 
    	else
    		sb.append(", fkey text);");
    	  	    	
    	//Log.i("DATABASE 2", "CREATE TRIGGER "+project+"_fkd_"+rtable+" BEFORE DELETE ON data_"+project+"_"+rtable+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".fkey = OLD.pkey; END;");
    	//Log.i("DATABASE 2", sb.toString());
    	
    	
    	db.execSQL(sb.toString());
    	
    	//String[] temp = value.split(",");
    	//String query = "select pkey from data_"+DATABASE_PROJECT+"_"+nexttable+" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	//for(int i = 2; i < temp.length; i+=2){
    	//	query += " and \"" + temp[i] + " = \""+temp[i+1]+"\""; 
    	//}
    	if(!rtable.equals("Null"))
    		db.execSQL("CREATE TRIGGER "+project+"_fkd_"+rtable+" BEFORE DELETE ON data_"+project+"_"+rtable+" FOR EACH ROW BEGIN DELETE from data_"+project+"_"+table+" WHERE data_"+project+"_"+table+".fkey = OLD.pkey; END;");
    	*/
    	
    }
    
    //public void createTableTriggers(String project, String table, String ftable){
    	
    //	getActiveProject();
    	//db.execSQL("CREATE TRIGGER fkd_"+table+" BEFORE DELETE ON "+project+"."+table+" FOR EACH ROW BEGIN DELETE from "+project+"."+ftable+" WHERE fkey = OLD.pkey END;");

    //}
    
    public void createKeyTable(String project){
    	getActiveProject();
    	StringBuffer sb = new StringBuffer("create table if not exists keys_"+project+" (tablenum int primary key, tablename text, tablekey text, maintable text default \"1\", branch_caller text, branch_caller_key text, fromgroup int default 0)");
    	db.execSQL(sb.toString());
    }
    
    public void createKeyRow(HashMap<String, String> values, String proj){
    	getActiveProject();
      	ContentValues initialValues = new ContentValues();
      	
      	for(String key : values.keySet()){
      		initialValues.put(key, values.get(key));
      	}

          db.replace("keys_"+proj, null, initialValues); // replace
         
	}
    
    public String getIsMaintable(String table){
    	getActiveProject();
    	
    	//Log.i("DBACCESS", "select maintable from keys_"+DATABASE_PROJECT+" where tablename = \""+table+"\"");
    	
     	String result = "0";
    	Cursor c = db.rawQuery("select maintable from keys_"+DATABASE_PROJECT+" where tablename = \""+table+"\"", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            //c.close();
            //return result;
        }
        c.close();
        //Log.i("DBACCESS_2", result);
        return result;
    	
    }   
    
    public String getKeyValue(String table){
    	getActiveProject();
    	
     	String result;
    	Cursor c = db.rawQuery("select tablekey from keys_"+DATABASE_PROJECT+" where tablename = \""+table+"\"", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            c.close();
            return result;
        }
        c.close();
        return "";
    	
    }   
    
    public void createProjectRow(HashMap<String, String> values, String proj) {
        
    	getActiveProject();
      	ContentValues initialValues = new ContentValues();
      	for(String key : values.keySet()){
      		initialValues.put(key, values.get(key));
      		//Log.i("CREATING PROJECT ROW", key+ " = "+values.get(key));
      	}

      	try{
          db.replaceOrThrow(proj, null, initialValues); // replace
      	}
      	catch(SQLException sqe){
      		//Log.i("CREATING PROJECT REPLACE ERROR", sqe.toString());
      	}
         
      }
    
    public void getActiveProject(){
    	Cursor c = db.query("projects", new String[] {
            		"project"}, "active='Y'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            DATABASE_TABLE = "data_"+c.getString(0);
            DATABASE_PROJECT = c.getString(0);
        }
        c.close();
    }
    
    public String getProject(){
    	Cursor c = db.query("projects", new String[] {
            		"project"}, "active='Y'", null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            DATABASE_PROJECT = c.getString(0);
        }
        c.close();
        return DATABASE_PROJECT;
    }
    
    public void setActiveProject(String project){
    	
    	db.execSQL("update projects set active = 'N'");
    	
    	db.execSQL("replace into projects values('"+project+"', 'Y')");
    	
    	// In case project already exists
    	db.execSQL("update projects set active = 'Y' where project = \""+project+"\"");
    	
    	DATABASE_TABLE = "data_"+project;
        DATABASE_PROJECT = project;

    }
    
    public void deleteProject(String project){
    	
    	//dropTable(project);
    	//dropTable("data_"+project);
    	//dropTable(project+"_Image");
    	//dropTable(project+"_Video");
    	//dropTable(project+"_Audio");
    	db.execSQL("delete from projects where project = \""+project+"\"");
    }

    public String getNextTable(String table){
    	getActiveProject();
    	int num = 0;
    	String next;
    	Cursor c = db.rawQuery("select tablenum from keys_"+DATABASE_PROJECT  +" where tablename = \""+table+"\"", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            num = c.getInt(0);
            num++;
            c.close();
            c = db.rawQuery("select tablename from keys_"+DATABASE_PROJECT  +" where maintable = 1 and tablenum = "+num, null);
            numRows = c.getCount();
            if (numRows > 0) {
            	c.moveToFirst();
                next = c.getString(0);
                c.close();
                return next;
        	}
            else{
            	c.close();
            	return null;
            }
        }
        else{
        	c.close();
        	return null;
        }
   	
    }   
    
    public LinkedHashMap<String, String> getKeys(){
    	getActiveProject();
    	
    	LinkedHashMap<String, String> keyshash = new LinkedHashMap<String, String>(); 
    	Cursor c = db.rawQuery("select * from keys_"+DATABASE_PROJECT  +" where maintable = 1 order by tablenum", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	keyshash.put(c.getString(1), c.getString(2));
            	//Log.i("TABLE SELECT KEY CHECK", c.getString(1) + " "+c.getString(2));
            	c.moveToNext();
            	}
        	}
        c.close();
        return keyshash;
    	
    }   
    
    public Vector<String> getKeysVector(){
    	getActiveProject();
    	
    	Vector<String> keysvec = new Vector<String>(); 
    	Cursor c = db.rawQuery("select * from keys_"+DATABASE_PROJECT  +" where maintable = 1 order by tablenum", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	keysvec.addElement(c.getString(1));
            	c.moveToNext();
            	}
        	}
        c.close();
        return keysvec;
    	
    }   
    
    
    public LinkedHashMap<String, String> getAllKeys(int withgroup){
    	getActiveProject();
    	
    	LinkedHashMap<String, String> keyshash = new LinkedHashMap<String, String>(); 
    	Cursor c;
    	if(withgroup == 0)
    		c= db.rawQuery("select * from keys_"+DATABASE_PROJECT  +" where fromgroup = 0 and maintable = \"1\" order by tablenum", null);
    	//else if(withgroup == 2)
    	//	c = db.rawQuery("select * from keys_"+DATABASE_PROJECT  +"  where (fromgroup = 0 and maintable = \"1\") or fromgroup = 1 order by tablenum", null);
    	else 
    		c = db.rawQuery("select * from keys_"+DATABASE_PROJECT  +"  where fromgroup = 0 order by tablenum", null);
    	    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	keyshash.put(c.getString(1), c.getString(2));
            	//Log.i("TABLE SELECT KEY CHECK", c.getString(1) + " "+c.getString(2));
            	c.moveToNext();
            	}
        	}
        c.close();
        return keyshash;
    	
    }   
    
    public boolean haveGroup(String proj){
    	getActiveProject();
    	
    	Cursor c= db.rawQuery("select * from keys_"+DATABASE_PROJECT  +" where fromgroup = 1", null);
        if (c.getCount() > 0) {
           c.close();
           return true;
        }
        
    	c.close();
    	return false;
    }
    
    
    public LinkedHashMap<Integer, String> getTablesByNum(){
    	getActiveProject();
    	
    	LinkedHashMap<Integer, String> tablehash = new LinkedHashMap<Integer, String>(); 
    	Cursor c = db.rawQuery("select * from keys_"+DATABASE_PROJECT  +" order by tablenum", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	tablehash.put(c.getInt(0), c.getString(1));
            	//Log.i("TABLE SELECT KEY CHECK", c.getString(0) + " "+c.getString(1));
            	c.moveToNext();
            	}
        	}
        c.close();
        return tablehash;
    	
    }   
    
    // For FindRecord
    public int getTableNum(String table){
    	getActiveProject();
    	
    	//Log.i("QUERY CHECK","select tablenum from keys_"+DATABASE_PROJECT  +" where table = '"+table+"'");
    	Cursor c = db.rawQuery("select tablenum from keys_"+DATABASE_PROJECT  +" where tablename = \""+table+"\"", null);
    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            int res = c.getInt(0);
            c.close();
            return res; //c.getInt(0);
        	}
        c.close();
        return 0;
    	
    }   
    
 // For FindRecord
    public String getTableByNum(int tablenum){
    	getActiveProject();
    	
    	//Log.i("QUERY CHECK","select tablenum from keys_"+DATABASE_PROJECT  +" where table = '"+table+"'");
    	Cursor c = db.rawQuery("select tablename from keys_"+DATABASE_PROJECT  +" where tablenum = "+tablenum, null);
    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            String res = c.getString(0);
            c.close();
            return res; //c.getInt(0);
        	}
        c.close();
        return "";
    	
    }   
    
    public Vector<String> getGroupTables(int group){
    	
    	//StringBuffer sb = new StringBuffer("create table if not exists keys_"+project+" (tablenum int primary key, tablename text, tablekey text, maintable text default \"1\", branch_caller text, branch_caller_key text, fromgroup int default 0)");
    	
    	Vector<String> tables = new Vector<String>();
    	
    	Cursor c = db.rawQuery("select tablename from keys_"+DATABASE_PROJECT  +" where fromgroup = "+group, null);
    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	tables.addElement(c.getString(0));
            	c.moveToNext();
        		}
        	}
        c.close();
    	
    	return tables;
    	
    }
    
    public Vector<String> getFiles(String table){
    	
    	Vector<String> files = new Vector<String>(); 
    	Cursor c = db.rawQuery("select id from "+table, null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	files.addElement(c.getString(0));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return files;
    }
    
    // For FindRecord
    public int getSpinnerPos(String table, String key, String keytofind, String filterkey, String filtervalue){
    	getActiveProject();

    	//Log.i("QUERY CHECK", "select "+key+" from data_"+DATABASE_PROJECT+"_"+table+" order by '"+key+"'");
    	Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+table+" where "+filterkey+" = \""+filtervalue+"\" order by \""+key+"\"", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	//Log.i("QUERY CHECK 2", "TO FIND = "+keytofind+" VAL = "+c.getString(0));
            	if(keytofind.equalsIgnoreCase(c.getString(0))){
            		c.close();
            		return i+1;
            	}
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return 0;
    	
    }   
    
    public Vector<String> getSpinnerValues(String table, String key){
    	getActiveProject();
    	
    	//StringBuffer tempbuff = new StringBuffer("Select");
    	Vector<String> tempvec = new Vector<String>();
    	Cursor c = db.rawQuery("select ectitle from data_"+DATABASE_PROJECT+"_"+table+" order by \""+key+"\"", null);
    	//Cursor c = db.rawQuery("select pkey from data_"+DATABASE_PROJECT+"_"+table+" order by pkey", null);
    	
    	//Log.i("SPINNER VAL TABLE", table);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	//Log.i("SPINNER VAL RESULT", c.getString(0));
            	tempvec.addElement(c.getString(0));
            	//tempbuff.append(",,"+c.getString(0));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return tempvec; //tempbuff.toString();
    	
    }   
    
    public Vector<String> getGroupSpinnerValues(String table){
    	getActiveProject();
    	
    	Vector<String> tempvec = new Vector<String>();
    	Cursor c = db.rawQuery("select ectitle, ecpkey from data_"+DATABASE_PROJECT+"_"+table, null);
    	//Log.i("GROUP QUESRY", "select ectitle from data_"+DATABASE_PROJECT+"_"+table);
    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	tempvec.addElement(c.getString(0));
            	tempvec.addElement(c.getString(1));
            	//Log.i("GROUP VAL", table+"_"+c.getString(1));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return tempvec;
    	
    }   
    
    public Vector<String> getNextGroupSpinnerValues(String nexttable, String table, String value){
    	getActiveProject();
// Log.i("QUERY NEXT", "select ectitle from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"_ID\" = \""+value+"\"");
    	Vector<String> tempvec = new Vector<String>();
    	Cursor c = db.rawQuery("select ectitle, ecpkey from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"_ID\" = \""+value+"\"", null);    	
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	tempvec.addElement(c.getString(0));
            	tempvec.addElement(c.getString(1));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return tempvec; 
    	
    }   
    
    public LinkedHashMap<String, String> getSpinnerValues(String table){ // , String key){
    	getActiveProject();
    	
    	LinkedHashMap<String, String> valhash = new LinkedHashMap<String, String>();
    	
    	//boolean genkey = false;
    	
    	//if(getValue(table, "genkey") != null && getValue(table, "genkey").length() > 0){
        //	genkey = true;
        	
        //}

    	//Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+table+" order by \""+key+"\"", null);
    	Cursor c = db.rawQuery("select ecpkey, ectitle from data_"+DATABASE_PROJECT+"_"+table+" order by ecpkey", null);
    	
    	//Log.i("SPINNER VAL TABLE", table);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	Log.i("SPINNER VAL RESULT", c.getString(0));
            	//if(genkey)
            	//	valhash.put(c.getString(1), c.getString(0)+" "+c.getString(2));
            	//else
            		valhash.put(c.getString(0), c.getString(1));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return valhash;
    	
    }   
    
    //public String getNextSpinnerValues(String nexttable, String key, String table, String value){
    public LinkedHashMap<String, String> getNextSpinnerValues(String nexttable, String value){
    	getActiveProject();
    	
    	LinkedHashMap<String, String> valhash = new LinkedHashMap<String, String>();
    	
    	//boolean genkey = false;
    	
    	//if(getValue(nexttable, "genkey") != null && getValue(nexttable, "genkey").length() > 0){
        //	genkey = true;
        //	
        //}
    	//Log.i("QUERY CHECK", value);
    	String[] temp = value.split(",");
    	String query = "select ecpkey, ectitle from data_"+DATABASE_PROJECT+"_"+nexttable+" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	for(int i = 2; i < temp.length; i+=2){
    		query += " and \"" + temp[i] + "\" = \""+temp[i+1]+"\""; 
    	}
    	
    	//Log.i("QUERY CHECK", query);
    	
    	//StringBuffer tempbuff = new StringBuffer("Select");
    	//Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"\" = \""+value+"\"", null);    	
    	//Cursor c = db.rawQuery("select pkey from data_"+DATABASE_PROJECT+"_"+nexttable+" where fkey = \""+value+"\"", null);
    	    	
    	Cursor c = db.rawQuery(query, null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	//Log.i("QUERY RESULT", c.getString(1)+ " AND "+ c.getString(0)+" "+c.getString(2));
            	//if(genkey)
            	//	valhash.put(c.getString(1), c.getString(0)+" "+c.getString(2));
            	//else
            		valhash.put(c.getString(0), c.getString(1));
            	//tempbuff.append(",,"+c.getString(0));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return valhash;
    	
    }   
    
  public Vector<String> getNextSpinnerValues(String nexttable, String key, String table, String value){
   // public String getNextSpinnerValues(String nexttable, String value){
    	getActiveProject();
    	
    	//Log.i("QUERY CHECK", value);
    	//String[] temp = value.split(",");
    	//String query = "select pkey from data_"+DATABASE_PROJECT+"_"+nexttable+" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	//for(int i = 2; i < temp.length; i+=2){
    	//	query += " and \"" + temp[i] + " = \""+temp[i+1]+"\""; 
    	//}
    	
    	//Log.i("QUERY CHECK", query);
    	
    	//StringBuffer tempbuff = new StringBuffer("Select");
    	Vector<String> tempvec = new Vector<String>();
    	//Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"\" = \""+value+"\"", null);    	
    	Cursor c = db.rawQuery("select ectitle from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"_ID\" = \""+value+"\"", null);    	
    	//Log.i("QUERY CHECK", "select title from data_"+DATABASE_PROJECT+"_"+nexttable+" where \""+table+"_ID\" = \""+value+"\""); 	
    	//Cursor c = db.rawQuery(query, null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	//Log.i("QUERY RESULT", c.getString(0));
            	//tempbuff.append(",,"+c.getString(0));
            	tempvec.addElement(c.getString(0));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return tempvec; //tempbuff.toString();
    	
    }   
    
    public String getValuesForList(String table, String key, String primary_key){
    	getActiveProject();
    	
    	StringBuffer tempbuff = new StringBuffer("Select");
    	//Log.i("QUERY CHECK", "select "+key+" from data_"+DATABASE_PROJECT+"_"+table+" order by '"+key+"'");
    	Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+table+" order by \""+primary_key+"\"", null);
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	tempbuff.append(",,"+c.getString(0));
            	c.moveToNext();
            	}
        	}
        c.close();
        
        return tempbuff.toString();
    	
    }   
    
    public String getGroupValue(String table, String column){
    	getActiveProject();
    	
     	String result="0";
     	Cursor c = db.rawQuery("select "+column+" from "+table, null);
    	if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
        }
        c.close();
        return result;
    	
    }   
    
    public String getValue(String table, String column){
    	getActiveProject();
    	
     	String result="";
     	//Log.i("RUNNING GET VALUE " + table, column);
     	//column = "\""+column+"\"";
     	
     	//String query = "select ? from "+DATABASE_PROJECT+"_"+table;
     	
    	//Cursor c = db.rawQuery(query, new String[]{ column }); //"select \""+column+"\" from "+DATABASE_PROJECT+"_"+table, null);
    	Cursor c = db.rawQuery("select \""+column+"\" from "+DATABASE_PROJECT+"_"+table, null);
    	if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            //Log.i("GET VALUE " + table + " " +column, result); 
            //c.close();
            //return result;
        }
        c.close();
        return result;
    	
    }   
    
    public int getMaxPhonekeyValue(String table){
    	getActiveProject();
    	
     	int result = 0;
    	Cursor c = db.rawQuery("select max(ecphonekey) from data_"+DATABASE_PROJECT+"_"+table, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getInt(0);
            //Log.i("GET VALUE " + table + " " +column, result); 
            //c.close();
            //return result;
        }
        c.close();
        return result;
    	
    }   
    
   /* public boolean checkTableHasEntries(String table){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select * from "+table, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;
    	
    }   */
    
    public String getValueWithFKey(String table, String column, String value){
    	getActiveProject();
    	
    	String[] temp = value.split(",");
    	String query = "select \""+column+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	for(int i = 2; i < temp.length; i+=2){
    		query += " and \"" + temp[i] + "\" = \""+temp[i+1]+"\""; 
    	}
    	
    	//Log.i("LIST QUERY", query);
     	String result ="";
    	Cursor c = db.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            //Log.i("LIST QUERY RESULT", result);
            //c.close();
            //return result;
        }
        c.close();
        return result;
    	
    }
    
    
    //public String getValue(String table, String column, String key, String value){
    public String getValue(String table, String column, String value){
    	getActiveProject();
    	
     	String result = "";
    	//Cursor c = db.rawQuery("select \""+column+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \"" + key +"\" = \"" + value +"\"", null);
    	Cursor c = db.rawQuery("select \""+column+"\" from data_"+DATABASE_PROJECT+"_"+table +" where ecpkey = \"" + value +"\"", null);
       // Log.i("QUERY", "select \""+column+"\" from data_"+DATABASE_PROJECT+"_"+table +" where ecpkey = \"" + value +"\"");
    	if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            //try{
            //	Log.i("MESSAGE RESULT", result);
           // }
            //catch(NullPointerException npe){}
            //c.close();
            //return result;
        }
        c.close();
        //Log.i("QUERY", "select \""+column+"\" from data_"+DATABASE_PROJECT+"_"+table +" where ecpkey = \"" + value +"\"");
        
        return result;
    	
    }   
    
   /* public int getBranchCount(String table, String column, String value){
    	getActiveProject();
    	int count = 0;
    	
    	Cursor c = db.rawQuery("select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where \""+column+"\" = \"" + value +"\"", null);
    	
     	//Log.i("BRANCH", "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where \""+column+"\" = \"" + value +"\"");
    	if (c.getCount() > 0) {
             c.moveToFirst();
             //Log.i("BRANCH2", c.getString(0)); 
             count = c.getInt(0);
         }
         c.close();
         
    	return count;
    } */
    
    public int getBranchCount(String table, String foreign_key){
    	getActiveProject();
    	int count = 0;
    	
    	//Log.i("BRANCH COUNT", foreign_key);
    	String[] temp = foreign_key.split(",");
    	//String query = "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where ecfkey"+ " = "+foreign_key; 
		
		String query = "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where "+ temp[0] + " = \""+temp[1]+"\""; 
		for(int i = 2; i < temp.length; i+=2){
			query += " and " + temp[i] + " = \""+temp[i+1]+"\""; 
		}
    	
		//Log.i("BRANCH COUNT", query);
		
		Cursor c = db.rawQuery(query, null);
		
     	//Log.i("BRANCH", "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where \""+column+"\" = \"" + value +"\"");
    	if (c.getCount() > 0) {
             c.moveToFirst();
             //Log.i("BRANCH2", c.getString(0)); 
             count = c.getInt(0);
         }
         c.close();
         
    	return count;
    }
    
    /*public int getBranchCountWithFkey(String table, String foreign_key){
    	getActiveProject();
    	int count = 0;
    		
    	Log.i("BRANCH COUNT FKEY", "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where ecfkey = \""+foreign_key+"\"");
		String query = "select count(*) from data_"+DATABASE_PROJECT+"_"+table +" where ecfkey = \""+foreign_key+"\""; 
    	
		Cursor c = db.rawQuery(query, null);
		
    	if (c.getCount() > 0) {
             count = c.getInt(0);
         }
         c.close();
         
    	return count;
    }*/
    
    public String getFKey(String table, String value, String key){
    	getActiveProject();
    	
    	String result = "";
    	String[] temp = value.split(",");
    	String query;
    	Cursor c;
    	
    	for(int i = 0; i < temp.length; i++){
    		query = "select \""+temp[i]+"\" from data_"+DATABASE_PROJECT+"_"+table +" where ecpkey = \""+key+"\"";
    		c = db.rawQuery(query, null);
    		if (c.getCount() > 0) {
    			c.moveToFirst();
    			result += ","+temp[i]+","+c.getString(0);
    	        
    		}
    		c.close();
    	}
    	
    	result = result.replaceFirst(",", "");
        return result;
    	
    }
    
    public void updateFileSynchRow(String table, String value){ 
    	getActiveProject();
    	//ContentValues args = new ContentValues();
    	
    	//Log.i("QUERY", "update "+table+" set synch = \"Y\" where id = \""+value+"\"");
    	//db.execSQL("update "+table+" set synch = \"Y\" where id = \""+value+"\"");
    	
    	//db.execSQL("replace into "+table+" values('"+value+"', 'Y')");
    	
    	db.execSQL("replace into "+table+" (id, synch) values('"+value+"', 'Y')"); 
    	//ContentValues args = new ContentValues();
       // args.put("synch", "Y");
       // db.update(table, args, "id=" + "'"+value+"'", null);
    }
    
    public boolean checkProject(String value){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select * from projects where project = \"" + value +"\"", null);
    	if (c.getCount() > 0) {
    		c.close();
            return true;
        }
        c.close();
        return false;
    	
    }   
    
    // For FindRecord to check if an ID is in the database
    public boolean checkValue(String table, String column, String value){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select * from data_"+DATABASE_PROJECT+"_"+table +" where " + column +" = \"" + value +"\"", null);
    	if (c.getCount() > 0) {
    		c.close();
            return true;
        }
        c.close();
        return false;
    	
    }   
    
    public boolean checkJump2(String table, String key, String key_val, String attr, String attr_val){
    	getActiveProject();    	
    	         
    	//Log.i("JUMP2 NEW", "select \""+attr+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \""+key+"\" = \""+key_val+"\" and \""+attr+"\" = \""+ attr_val+"\"");
    	Cursor c = db.rawQuery("select \""+attr+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \""+key+"\" = \""+key_val+"\" and \""+attr+"\" = \""+ attr_val+"\"", null);
        if (c.getCount() > 0) {
        	//c.moveToFirst();
        	//String r = c.getString(0);
        	//Log.i("JUMP2 DB", "PASSED "+r);
        	//if(c.getString(0).equalsIgnoreCase(attr_val)){
        		c.close();
        		return true;
        	//}
        }
       // Log.i("JUMP2 DB", "FAILED");
        c.close();
        return false;
    	
    }   
    
    public String checkMatch(String table, String key, String key_val){
    	getActiveProject();
    	String res = "";
    	//Log.i("JUMP2", "select \""+attr+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \""+key+"\" = \""+key_val+"\" and \""+attr+"\" = \""+ attr_val+"\"");
    	Cursor c = db.rawQuery("select \""+key+"\" from data_"+DATABASE_PROJECT+"_"+table +" where ecpkey = \""+key_val+"\"", null);
        if (c.getCount() > 0) {
        	c.moveToFirst();
        	//Log.i("MATCH RES", c.getString(0));
        	//if(c.getString(0).equalsIgnoreCase(attr_val)){
        		res = c.getString(0);
        	//}
        }
        c.close();
        return res;
    	
    }   
    
    // For the old version of match
    public String checkMatch2(String table, String key, String key_val, String attr){
    	getActiveProject();
    	String res = "";
    	//Log.i("JUMP2", "select \""+attr+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \""+key+"\" = \""+key_val+"\" and \""+attr+"\" = \""+ attr_val+"\"");
    	Cursor c = db.rawQuery("select \""+attr+"\" from data_"+DATABASE_PROJECT+"_"+table +" where \""+key+"\" = \""+key_val+"\"", null);
        if (c.getCount() > 0) {
        	c.moveToFirst();
        	//Log.i("MATCH RES", c.getString(0));
        	//if(c.getString(0).equalsIgnoreCase(attr_val)){
        		res = c.getString(0);
        	//}
        }
        c.close();
        return res;
    	
    }   
    
 // For EpiCollect to count the number of media files
    public int getMediaFileCount(String table, String column, String value){
    	getActiveProject();
   	
    	Cursor c = db.rawQuery("select * from "+table +" where " + column +" = \"" + value +"\"", null);
    	int total = c.getCount();
    	c.close();
        
    	return total; 

    }   
    
 // For FindRecord to check if an ID is in the database
    public boolean checkFileValue(String table, String column, String value){
    	getActiveProject();
    	
    	//Cursor c2 = db.rawQuery("select * from "+table, null);
		//Log.i("checkFileValue", "select * from "+table +" where " + column +" = \"" + value +"\"");
		//Log.i("COUNT", ""+c.getCount());
    	//if (c2.getCount() > 0) {
    	//	c2.moveToFirst();
    	//	for(int i = 0; i < c2.getCount(); i++){
    	//		//Log.i("COUNT 2", ""+i+" IT IS "+c2.getString(0)+" "+c2.getString(1));
    	//		c2.moveToNext();
    	//	} 
        //}
    	
    	Cursor c = db.rawQuery("select * from "+table +" where " + column +" = \"" + value +"\"", null);
		//Log.i("checkFileValue", "select * from "+table +" where " + column +" = \"" + value +"\"");
		//Log.i("COUNT", ""+c.getCount());
    	if (c.getCount() > 0) {
    		//c.moveToFirst();
    		//Log.i("checkFileValue", "select * from "+table +" where " + column +" = \"" + value +"\"");
    		c.close();
            return true; 
        }
        c.close();
        return false;
    	
    }   
    
 // For FindRecord to check if an ID is in the database
    public boolean checkFileValue(String table, String column, String value, String column2, String value2){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select * from "+table +" where " + column +" = \"" + value +"\" and " + column2 +" = \"" + value2 +"\"", null);
		Log.i("checkFileValue", "select * from "+table +" where " + column +" = \"" + value +"\" and " + column2 +" = \"" + value2 +"\"");
		//Log.i("COUNT", ""+c.getCount());
    	if (c.getCount() > 0) {
    		//c.moveToFirst();
    		//Log.i("checkFileValue", "select * from "+table +" where " + column +" = \"" + value +"\"");
    		Log.i("COUNT", ""+c.getCount()); //+" "+c.getString(0)); //+" "+c.getString(1));
    		c.close();
            return true; 
        }
        c.close();
        return false;
    	
    }   

    // Should be redundant
    public String getValue(String column){
    	getActiveProject();
    	
     	String result;
    	Cursor c = db.rawQuery("select \""+column+"\" from "+DATABASE_PROJECT, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            result = c.getString(0);
            c.close();
            return result;
        }
        c.close();
        return "";
    	
    }   
    
    public boolean checkImages(){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select ecrowId from "+DATABASE_TABLE+" where ecstored = 'N'", null);
        if (c.getCount() > 0){ // There are unsyncronized records. Don't upload photos until all thumbnails are uploaded
        	c.close();
        	return true;
        }
        c.close();
        return false;
    	
    }   
    
    public boolean checkValue(String remoteid){
    	getActiveProject();
    	
    	Cursor c = db.rawQuery("select ecrowId from "+DATABASE_TABLE+" where ecremoteId='"+remoteid+"'", null);
        if (c.getCount() > 0) {
        	c.close();
            return true;
        }
        c.close();
        return false;
    	
    }   
    
    public String[] getProjects(){
    	getActiveProject();
    	Cursor c = db.rawQuery("select project from projects order by active desc", null);
    	StringBuffer sb = new StringBuffer("");
    	int numRows = c.getCount();
        if (numRows > 0) {
            c.moveToFirst();
            for (int i = 0; i < numRows; i++){ 
            	sb.append(",,"+c.getString(0));
            	c.moveToNext();
            }
        }
        
        String allprojects = sb.toString();
    	allprojects.replaceFirst(",,,", "");
        
    	c.close();
    	return allprojects.split(",,");
    	
    }
    
    public void createRow(HashMap<String, String> values) {
    	getActiveProject();
    	ContentValues initialValues = new ContentValues();
    	
    	for(String key : values.keySet()){
    		initialValues.put(key, values.get(key));
    	}
        
        //initialValues.put("stored", "N");
        db.replace(DATABASE_TABLE, null, initialValues); // replace
       
    }
    
	//HashMap<String, Vector<HashMap<String, String>>> allrows = new HashMap<String, Vector<HashMap<String, String>>>();

    public void createAllRows(HashMap<String, Vector<HashMap<String, String>>> allrows) {
    	getActiveProject();
    	//ContentValues initialValues = new ContentValues();
    	
    	InsertHelper ih; // = new InsertHelper(db, "columnTable");
    	LinkedHashMap<String, String> keyshash = getAllKeys(1);
    	Vector<HashMap<String, String>> rowvec;
    	Vector<String> columns;
    	int count = 1;
    //Log.i("DB CREATING", "CREATING ALL ROWS");	
    	for(String key : keyshash.keySet()){
    		
    		//Log.i("DB CREATING", "CREATING ALL ROWS FOR "+key);	
    		    		
    		if(allrows.get(key) == null)
    			continue;
    		
    		rowvec = allrows.get(key);
    		
    		//db.beginTransaction();
    		ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+key);
    		try {
    			columns = getColumnNames(key);
    			for(String col : columns){
    				ih.getColumnIndex(col);
    			}
    			for(HashMap<String, String> rowshash : rowvec){
    				//Log.i("LOAD CHECK 4", "IN HERE");
    				ih.prepareForReplace();
    			 
    				// Add the data for each column
    				count = 1;
    				boolean haveresult = false;
    				for(String col : columns){
    					//Log.i("LOAD CHECK 1", count+" "+col+" "+rowshash.get(col));
    					if(rowshash.get(col) != null){
    						//Log.i("LOAD CHECK 2A", count+" "+col+" "+rowshash.get(col));
    						//ih.bind(count, rowshash.get(col));
    						//Log.i("LOAD CHECK 2B", count+" "+col+" "+rowshash.get(col));
    						haveresult = true;
    					}
    					count++;
    				}
    				//ih.toString();
    				if(haveresult){
    					ih.execute();
    				}
    			}
    		ih.close();
   			db.setTransactionSuccessful();
    		} 
    		finally {
    			db.endTransaction();
    		}
    	}
       
    }
    
    private Vector<String> getColumnNames(String table){
    	Vector<String> columns = new Vector<String>();
    	getActiveProject();
    	
    	//Log.i("COLUMN 1", table);
    	try {
            Cursor c = db.query("data_"+DATABASE_PROJECT+"_"+table, null, null, null, null, null, null);
            if (c != null) {
                int num = c.getColumnCount();
                for (int i = 0; i < num; ++i) {
                	//Log.i("COLUMN 2", c.getColumnName(i));
                	columns.addElement(c.getColumnName(i));

                }
                c.close();
            }

        } catch (Exception e) {
            Log.v(table, e.getMessage(), e);
            e.printStackTrace();
        }

    	
    	
    	return columns;
    }
    
    public void createRow(String table, HashMap<String, String> values) {
    	
    	getActiveProject();
    	
    	//Log.i("XML TABLE:",table+" "+DATABASE_PROJECT); 
     	//for(String r : values.keySet())
     	//	Log.i("XML TABLE: "+r, values.get(r));
    	
    	ContentValues initialValues = new ContentValues();
    	//StringBuffer tempsb = new StringBuffer();
    	for(String key : values.keySet()){
    		initialValues.put(key, values.get(key));
    		//tempsb.append(key+" "+values.get(key)+" -- ");
    	}
        
        //initialValues.put("stored", "N");
    	//Log.i("DB CREATE ROW ", "data_"+DATABASE_PROJECT+"_"+table + " RES "+tempsb.toString());
        db.replace("data_"+DATABASE_PROJECT+"_"+table, null, initialValues); // replace
       
    }
    
    public void createFileRow(String table, String value) {
    	
    	getActiveProject();
        
        //initialValues.put("stored", "N");
    	//Log.i("CREATE FILE ROW ", "insert into "+DATABASE_PROJECT+"_"+table+" (id, synch) values('"+value+"', 'N')");
        db.execSQL("replace into "+DATABASE_PROJECT+"_"+table+" (id, synch) values('"+value+"', 'N')"); // replace
       
    } 
    
    public String getSettings(String column){
    	
    	Cursor c = db.rawQuery("select "+column+" from settings", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            String res = c.getString(0);
            c.close();
            return res; //c.getString(0);
        }
        c.close();
        return "";
    }

    public void updateSettingsRow(String column, String value){ //(HashMap<String, String> values) {
    	//ContentValues initialValues = new ContentValues();
    	
    	//for(String key : values.keySet()){
    	//	initialValues.put(key, values.get(key));
    	//	Log.i("SETTINGS", key+" "+values.get(key));
    	//}
        
        //db.replace("settings", null, initialValues); // replace  
    	Log.i("SETINGS UPDATE", "update settings set "+column+" = '"+value+"'");
        db.execSQL("update settings set "+column+" = '"+value+"' where id = 1");
       
    }
    
    public void deleteRow(String table, String key, String keyvalue) {
    	getActiveProject();
  
        db.delete("data_"+DATABASE_PROJECT+"_"+table, key+"=\"" + keyvalue+"\"", null);
    	//Log.i("QUERY", "delete * from data_"+DATABASE_PROJECT+"_"+table+" where " + key + "=\""+keyvalue+"\"");
        //db.execSQL("delete * from data_"+DATABASE_PROJECT+"_"+table+" where " + key + "=\""+keyvalue+"\"");
    }

    public void deleteAllRows(String table) {
    	getActiveProject();
    	
    	db.execSQL("delete from data_"+DATABASE_PROJECT+"_"+table);
    }
    
    public void deleteBranchRows(String table, String foreign_key) {
        getActiveProject();
    	getValues(table);
    	
    	String[] temp = foreign_key.split(",");
    	String query = "delete from data_"+DATABASE_PROJECT+"_"+table +" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    	for(int i = 2; i < temp.length; i+=2){
    		query += " and \"" + temp[i] + "\" = \""+temp[i+1]+"\""; 
    	}
    	//Log.i("BRANCH DEL", query);
    	db.execSQL(query);
    	
    	}
    
    public void deleteAllSynchRows(String table, String value) {
    	getActiveProject();
    	
    	if(value.equalsIgnoreCase("None"))
    		db.execSQL("delete from data_"+DATABASE_PROJECT+"_"+table+" where ecstored = 'Y'");
    }
    
    public void deleteAllRemRows(String table, String value) {
    	getActiveProject();
    	
    	if(value.equalsIgnoreCase("None"))
    		db.execSQL("delete from data_"+DATABASE_PROJECT+"_"+table+" where ecstored = 'R'");
    }
    
    public void deleteAllOrphanRows(String table, String value) {
    	getActiveProject();
    	
    	if(value.equalsIgnoreCase("None"))
    		db.execSQL("delete from data_"+DATABASE_PROJECT+"_"+table+" where ecstored = 'F'");
    }

    public void updateFileTable(int tablenum, int tabletotal){
    	getActiveProject(); 
    	Vector<String> photovec = new Vector<String>(); 
    	Vector<String> videovec = new Vector<String>(); 
    	Vector<String> audiovec = new Vector<String>(); 
    	Vector<String> photovec2 = new Vector<String>(); 
    	Vector<String> videovec2 = new Vector<String>(); 
    	Vector<String> audiovec2 = new Vector<String>(); 
    	
    	String table;
    	Cursor c, c2;
    	int numRows;
    	for(int i = tablenum; i <= tabletotal; i++){
    		c = db.rawQuery("select tablename from keys_"+DATABASE_PROJECT  +" where tablenum = "+i, null);
    		if(c.getCount() > 0){
    			c.moveToFirst();
    			table = c.getString(0);
    			getFileValues(table);
    			
    			//StringBuffer sb = new StringBuffer("create table "+project+"_"+table+" (id text primary key, synch text default \"N\");");
    			for(String p : photos){
    				c2 = db.rawQuery("select '"+p+"' from data_"+DATABASE_PROJECT+"_"+table, null);
    				numRows = c2.getCount();
    		    	c2.moveToFirst();
    		        for (int j = 0; j < numRows; j++){ 
    		        	photovec.addElement(c2.getString(0));
    	    			c2.moveToNext();
    		        }
    		        c2.close();
    			}
    			
    			for(String v : videos){
    				c2 = db.rawQuery("select '"+v+"' from data_"+DATABASE_PROJECT+"_"+table, null);
    				numRows = c2.getCount();
    		    	c2.moveToFirst();
    		        for (int j = 0; j < numRows; j++){ 
    		        	videovec.addElement(c2.getString(0));
    	    			c2.moveToNext();
    		        }
    		        c2.close();
    			}
    			
    			for(String a : audio){
    				c2 = db.rawQuery("select '"+a+"' from data_"+DATABASE_PROJECT+"_"+table, null);
    				numRows = c2.getCount();
    		    	c2.moveToFirst();
    		        for (int j = 0; j < numRows; j++){ 
    		        	audiovec.addElement(c2.getString(0));
    	    			c2.moveToNext();
    		        }
    		        c2.close();
    			}
    	    	//photos = new String[0];
    	    	//videos = new String[0];
    	    	//audio = new String[0];
    			
    		}
    			    	
        	c.close();
    	}
    	
    	c = db.rawQuery("select id from "+DATABASE_PROJECT  +"_Image", null);
    	numRows = c.getCount();
    	if(numRows > 0){
		c.moveToFirst();
		for (int i = 0; i < numRows; i++){ 
			photovec2.addElement(c.getString(0));
	    	c.moveToNext();
		    }
    	}
		c.close();
		
		c = db.rawQuery("select id from "+DATABASE_PROJECT  +"_Video", null);
    	numRows = c.getCount();
    	if(numRows > 0){
		c.moveToFirst();
		for (int i = 0; i < numRows; i++){ 
			videovec2.addElement(c.getString(0));
	    	c.moveToNext();
		    }
    	}
		c.close();
		
		c = db.rawQuery("select id from "+DATABASE_PROJECT  +"_Audio", null);
    	numRows = c.getCount();
    	if(numRows > 0){
		c.moveToFirst();
		for (int i = 0; i < numRows; i++){ 
			audiovec2.addElement(c.getString(0));
	    	c.moveToNext();
		    }
    	}
		c.close();
			
		for(String photo : photovec2){
			if(!photovec.contains(photo))
				db.delete(DATABASE_PROJECT  +"_Image", "id='" + photo+"'", null);
		}
		
		for(String video : videovec2){
			if(!videovec.contains(video))
				db.delete(DATABASE_PROJECT  +"_Video", "id='" + video+"'", null);
		}
		
		for(String audio : audiovec2){
			if(!audiovec.contains(audio))
				db.delete(DATABASE_PROJECT  +"_Audio", "id='" + audio+"'", null);
		}
    	
    }
    
    public void deleteRow(int rowId) {
    	getActiveProject();
    	String pic;
    	//File picfile;
    	Cursor c = db.rawQuery("select photo from "+DATABASE_TABLE+" where ecrowId='"+rowId+"'", null);
        if (c.getCount() > 0) {
        	c.moveToFirst();
        	pic = c.getString(0);
        	deleteImage(pic, true);
        	/*try{
            	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + getProject()+"/"+pic);
            	picfile.delete();
            	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject()+"/"+pic);
            	picfile.delete();
            	}
            catch(Exception e){}*/
        }
        c.close();
 
        db.delete(DATABASE_TABLE, "ecrowid=" + rowId, null);
    }
        
    public void deleteSynchRows() {
    	getActiveProject();
    	String pic;
    	//File picfile;
    	Cursor c = db.rawQuery("select ecphoto from "+DATABASE_TABLE+" where ecstored = 'Y'", null);
    	int numRows = c.getCount();
    	c.moveToFirst();
        for (int i = 0; i < numRows; i++){ 
        	pic = c.getString(0);
        	deleteImage(pic, false);
        	/*try{
            	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject()+"/"+pic);
            	picfile.delete();
            	}
            catch(Exception e){}*/
        	c.moveToNext();
        }
        c.close();
        db.delete(DATABASE_TABLE, "ecstored = 'Y'", null);
    }

    public void deleteRemoteRows() {
    	getActiveProject();
        db.delete(DATABASE_TABLE, "ecstored = 'R'", null);
    }
    
    public void deleteAllRows() {
    	getActiveProject();
    	/*String pic;
    	//File picfile;
    	Cursor c = db.rawQuery("select photo from "+DATABASE_TABLE+" where rowId >= 0", null);
    	int numRows = c.getCount();
    	c.moveToFirst();
        for (int i = 0; i < numRows; i++){ 
        	pic = c.getString(0);
        	deleteImage(pic, false);
        	//try{
            //	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject()+"/"+pic);
            //	picfile.delete();
            //	}
            //catch(Exception e){} 
        	c.moveToNext();
        } */
    	//SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        // Ensures images of synched records only are deleted first
        deleteSynchRows();
        db.delete(DATABASE_TABLE, "ecrowId >= 0", null);
    }
    
    public List<Row> fetchAllRows(int remote) {
    	
    	getActiveProject();
    	getValues();
    	Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+" where ecremote = "+remote, null);
    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        //Log.i("DBACCESS: ", "Fetch all rows");
        try {
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
            	//Log.i("DBACCESS: ", "Fetch all rows loop "+i);
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return ret;
    }
    
    public List<Row> fetchAllGPSRows(String table, int remote){
    	getActiveProject();
    	getValues(table);
    	Cursor c = db.rawQuery("select * from data_"+DATABASE_PROJECT+"_"+table+" where ecremote = "+remote, null);
    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        //Log.i("DBACCESS: ", "Fetch all rows");
        try {
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
            	//Log.i("DBACCESS: ", "Fetch all rows loop "+i);
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return ret; 
    }
    
    public List<Row> fetchAllRows(String table, String select_table, String foreign_key) {
    	
    	getActiveProject();
    	getValues(table);
    	Cursor c;
    	
    	//Log.i("DBACCESS: ", "Fetch all rows "+select_table+ " "+foreign_key);
    	
    	if(select_table.equalsIgnoreCase("Null")){
    		c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table, null);
    		//Log.i("DBACCESS:", "select * from "+DATABASE_TABLE+"_"+table);
    	}
    	else{//Log.i("DBACCESS:", "select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_table +"\" = '"+foreign_key+"'");   
    		c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_table +"\" = \""+foreign_key+"\"", null);
    		//Log.i("DBACCESS:","select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_table +"\" = \""+foreign_key+"\""); 
    	}
    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        //Log.i("DBACCESS: ", "Fetch all rows");
        try {
            int numRows = c.getCount();
            //Log.i("DBACCESS ROWS: ", "Num = "+numRows);
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
            	//Log.i("DBACCESS: ", "Fetch all rows loop "+i);
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return ret;
    }
    
    public List<Row> fetchAllRowsWithFKey(String table, String foreign_key) { // String select_table, String foreign_key 
    	
    	getActiveProject();
    	getValues(table);
    	Cursor c;
    	
    	//Log.i("DBACCESS: ", "Fetch all rows "+table+ " "+foreign_key);
    	
    	//if(select_table.equalsIgnoreCase("Null")){
    	if(foreign_key.equalsIgnoreCase("Null")){
    		c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table +" order by ecstored, ecpkey", null);
    	}
    	else{
    		String[] temp = foreign_key.split(",");
    		String query = "select * from data_"+DATABASE_PROJECT+"_"+table +" where \"" + temp[0] + "\" = \""+temp[1]+"\""; 
    		for(int i = 2; i < temp.length; i+=2){
    			query += " and \"" + temp[i] + "\" = \""+temp[i+1]+"\""; 
    		}
    		query += " order by ecstored, ecpkey";
    		c = db.rawQuery(query, null);
    		//Log.i("DBACCESS: ", query);
    		//c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_table +"\" = \""+foreign_key+"\"", null);
    	}
    	
    	
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
            int numRows = c.getCount();
            if(numRows > 2000)
            	numRows = 2000;
            //Log.i("DBACCESS2: ", ""+numRows);
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return ret;
    }
    
    public List<Row> fetchAllRowsWithQuery(String table, String query) {
    	
    	getActiveProject();
    	getValues(table);
    	Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table +" where "+query, null);
    	
    	//Log.i("SEARCH QUERY", "select * from "+DATABASE_TABLE+"_"+table +" where "+query);
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        try {
            int numRows = c.getCount();
            if(numRows > 2000)
            	numRows = 2000;
            c.moveToFirst();
            //Log.i("SEARCH ROWS", ""+numRows);
            for (int i = 0; i < numRows; ++i) {
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return ret;
    }
    
    public Row fetchRow(String table, String select_column, String select_value) {
    	
    	getActiveProject();
    	getValues(table);
    	Cursor c;
    	Row row = null;
    	
    	c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_column +"\" = \""+select_value+"\"", null);
    	
    	//Log.i("DBACCESS ", "select * from "+DATABASE_TABLE+"_"+table+" where \""+ select_column +"\" = '"+select_value+"'");
        try {
            if(c.getCount() == 0)
            	return null;
            
            c.moveToFirst();
           	row = getRow(c);
         } catch (SQLException e) {
        	 c.close();
            Log.e("booga", e.toString());
        }
        c.close();
        return row;
    }
        
    public boolean checkSynchronised(){
    
    	LinkedHashMap<String, String> keyshash = getAllKeys(0);

    	try{
		for(String table : keyshash.keySet()){
			Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where ecstored = 'N' or ecstored = 'F'", null);

	        try {
	            if(c.getCount() > 0){
	            	c.close();
	            	return false;    
	            }
	        } 
	        catch (SQLException e) {
	        	c.close();
	            Log.e("booga", e.toString());
	        }
	       
		}
    	}
    	catch(Exception e){
    		// If project has failed to load properly from the xml this may fail and prevents EpiCollect from loading
    		return false;
    	}
		return true;
	}
    
    public boolean checkSynchStatus(String stat){
        
    	LinkedHashMap<String, String> keyshash = getAllKeys(0);

    	try{
		for(String table : keyshash.keySet()){
			Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where ecstored = '"+stat+"'", null);

	        try {
	            if(c.getCount() > 0){
	            	c.close();
	            	return true;    
	            }
	        } 
	        catch (SQLException e) {
	        	c.close();
	            Log.e("booga", e.toString());
	        }
	       
		}
    	}
    	catch(Exception e){
    		// If project has failed to load properly from the xml this may fail and prevents EpiCollect from loading
    		return false;
    	}
		return false;
	}
    
   /* public boolean checkRemote(String table, String key){
        
			Cursor c = db.rawQuery("select ecstored from data_"+DATABASE_PROJECT+"_"+table+" where pkey = \""+key+"\"", null);
	         if(c.getCount() > 0){
	        	 c.moveToFirst();
	        	 if(c.getString(0).equalsIgnoreCase("N")){
	        		 c.close();
	        		 return true;
	        	 }
	         }

	        c.close();

	        return false;
		} */
		
		
    public List<Row> fetchAllRows(String table, boolean backup) {
    	
    	getActiveProject();
    	getValues(table);
    	Cursor c;
    	
    	if(backup)
    		c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table, null);
    	else
    		c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where ecstored = 'N' or ecstored = 'F'", null);
    		
    	
    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
    	
        ArrayList<Row> ret = new ArrayList<Row>();
        //Log.i("DBACCESS: ", "Fetch all rows");
        try {
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
            	//Log.i("DBACCESS: ", "Fetch all rows loop "+i);
            	Row row = getRow(c);
               
                ret.add(row); 
                c.moveToNext();
            }
        } catch (SQLException e) {
        	
            Log.e("booga", e.toString());
        }
        c.close();
        return ret;
    }
    
    public void updateAllRows(String table){
    	getActiveProject();
    	ContentValues args = new ContentValues();
    	
        args.put("ecstored", "Y");
        db.update(DATABASE_TABLE+"_"+table, args, "ecstored='N' or ecstored = 'F'", null);
    }
    
    public void updateResetAllRows(String table){
    	getActiveProject();
    	ContentValues args = new ContentValues();
    	
        args.put("ecstored", "N");
        db.update(DATABASE_TABLE+"_"+table, args, "ecstored=" + "'Y'", null);
        
        //db.update(DATABASE_TABLE+"_"+table, args, "ecstored=" + "'R'", null);
    }
    
    public void updateRecord(String table, String pkeyid, String pkey, String col, String val){
    	getActiveProject();
    	
    	db.execSQL("update "+DATABASE_TABLE+"_"+table+" set "+ col +"='"+ val+"' where \""+pkeyid+"\" = \""+pkey+"\"");
    	Log.i("UPDATE", "update "+DATABASE_TABLE+"_"+table+" set "+ col +"='"+ val+"' where \""+pkeyid+"\" = \""+pkey+"\"");
    }
    
    
    private Row getRow(Cursor c){
    	
    	//Cursor c = thisc;
		Row row = new Row();
		//Log.i("DBACCESS 3", "HERE");
        //row.rowId = c.getInt(0);
        //row.remoteId = c.getString(1);
        //row.gpslat = c.getString(2);
        //row.gpslon = c.getString(3);
        //row.gpsalt = c.getString(4);
        //row.gpsacc = c.getString(5);
        //row.photoid = c.getString(6);
        //row.date = c.getString(7);
        row.stored = c.getString(0);
        //Log.i("DBACCESS: ", "Fetch all rows " + row.stored);
        //if(c.getInt(9) == 0)
        //	row.remote = false;
        //else
        //	row.remote = true;
        
        int pos = 1;
       // //Log.i("DB ROWS", "IN HERE");
        for(String key : textviews){
        	//Log.i("DB ROWS", key);
        	/*if(key.equalsIgnoreCase("GPS")){
        		row.datastrings.put("GPS", "1");
        		row.gpslat = c.getString(pos);
        		pos++;
                row.gpslon = c.getString(pos);
                pos++;
                row.gpsalt = c.getString(pos);
                pos++;
                row.gpsacc = c.getString(pos);
        	}
        	else{ */
        		// If the remote data retrieval sends back an
        		// empty string this returns a null
        		try{
        			row.datastrings.put(key, c.getString(pos));
        		}
        		catch(NullPointerException npe){
        			row.datastrings.put(key, "");
        		//}
        	}
        	pos++;
        }
        
        for(String key : spinners){
        	//Log.i("GET ROW SPINNER", key);
        	//Log.i("GET ROW SPINNER", c.getString(pos));
        	try{
        		row.spinners.put(key, c.getString(pos));
        	}
        	catch(NullPointerException npe){
        		row.spinners.put(key, "Null");
        	}
        	pos++;
        }
        
        for(String key : radios){
        	//Log.i("GET ROW RADIO", key);
        	try{
        		row.radios.put(key, c.getString(pos));
        	}
        	catch(NullPointerException npe){}
        	pos++;
        }
        
        for(String key : checkboxes){
        	if(c.getInt(pos) == 1)
        		row.checkboxes.put(key, true);
        	else
        		row.checkboxes.put(key, false);
        	pos++;
        }
        
    	for(String key : photos){
    		try{
    			row.photoids.put(key, c.getString(pos));
    		}
    		catch(NullPointerException npe){
    			row.photoids.put(key, "-1");
    		}
    		pos++;
    	}
    	
    	for(String key : videos){
    		try{
    			row.videoids.put(key, c.getString(pos));
    		}
    		catch(NullPointerException npe){
    			row.videoids.put(key, "-1");
    		}
    		pos++;
    		
    	}
    	
    	for(String key : audio){
    		try{
    			row.audioids.put(key, c.getString(pos));
    		}
    		catch(NullPointerException npe){
    			row.audioids.put(key, "-1");
    		}
    		pos++;
    		
    	}
    	
    	for(String key : gpstags){
    		row.datastrings.put(key+"_lat", c.getString(pos));
    		pos++;
    		row.datastrings.put(key+"_lon", c.getString(pos));
    		pos++;
    		row.datastrings.put(key+"_alt", c.getString(pos));
    		pos++;
     		row.datastrings.put(key+"_acc", c.getString(pos));
    		pos++;
    		row.datastrings.put(key+"_bearing", c.getString(pos));
    		pos++;
    		row.datastrings.put(key+"_provider", c.getString(pos));
    		pos++;
    	}
        
        if(c.getInt(pos) == 0)
            row.remote = false;
        else
        	row.remote = true;
        pos++;
        
        row.date = c.getString(pos);
        pos++;
        
        row.ectitle = c.getString(pos);
        pos++;
        
        row.ecpkey = c.getString(pos);
        pos++;
        
        row.ecphonekey = c.getString(pos);
        //thisc.close();
        return row;
	}
    
    public long getNewID() {
    	getActiveProject();
        long maxid = 0;
        Cursor c = db.rawQuery("select max(ecrowId) from "+DATABASE_TABLE, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            maxid = c.getLong(0);
            maxid++;
            c.close();
            return maxid;
        } 

        c.close();
        return 1;
    }
    
    public void updateRecordID(long rowid, String remoteid, String stored){
    	getActiveProject();
    	ContentValues args = new ContentValues();
    	args.put("ecremoteId", remoteid);
        args.put("ecstored", stored);
        db.update(DATABASE_TABLE, args, "ecrowid=" + rowid, null);
    }
    
    // Used to check if a remote record is also in the local database.
    // If so it is not displayed on the map as a remote record
    public boolean checkremoteID(String remoteid) {
    	getActiveProject();
        Cursor c = db.rawQuery("select count(ecremoteId) from "+DATABASE_TABLE+" where ecremoteId = '"+remoteid+"'", null);
        if (c.getCount() > 0) {
        	c.moveToFirst();
            if(c.getLong(0) > 0){
            	//Log.i(getClass().getSimpleName(), "IT'S IN THE DATABASE");
            	c.close();
            	return true;
            }
        } 
        c.close();
        return false;
    }
    
    public String synchroniseV1(String sIMEI, String email){ //String email, , String password, String sIMEI){
    		
    	String result = "", photoresult = "";
    	//boolean store = true;
    	if (sIMEI==null){
    		sIMEI="null";
    	}
    	
    	String synch_url = getValue("synch_url"); //context.getResources().getString(context.getResources().getIdentifier(this.getClass().getPackage().getName()+":string/synch_url", null, null));
    	//String image_url = "http://epicollectserver.appspot.com/uploadImageToServer";
    	
    	//Log.i(getClass().getSimpleName(), "DB SYNCH: "+ synch_url);  
    	//Log.i(getClass().getSimpleName(), "DB IMAGE: "+ image_url);  
    	
    	String data; //, thisphotoid;
    	
    	getActiveProject();
    	
    	List<Row> rows = fetchAllRows("Data", false);
    	
    	try {
            // Construct data
            //String data;
    		data = "";
            for (Row row : rows) {
            	//Log.i("SYNCH HERE:", "7");
            	//store = true;
            	//if(row.stored.equals("N")){ 
            		//Log.i("SYNCH HERE:", "8");
            		//if(row.photoid != null && row.photoid.length() > 2)
            		//	uploadImage(row.photoid); //(row.photoid, time);
             		
            		//for(String key : row.datastrings.keySet()){
            		//	Log.i("SYNCH DATASTRING:", key+" "+row.datastrings.get(key));
            		//}
            		//String thisremoteid = sIMEI + "_" + row.date;
            		data = URLEncoder.encode("epicollect_insert", "UTF-8") + "=" + URLEncoder.encode("form1", "UTF-8");
            		//Log.i("SYNCH HERE:", "9");
            		data += "&" + URLEncoder.encode("ecEntryId", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("eckey"), "UTF-8");
            		//Log.i("SYNCH HERE:", "10");
            		//if(row.remoteId.equalsIgnoreCase("0"))
            		//	data += "&" + URLEncoder.encode("ecRemoteId", "UTF-8") + "=" + URLEncoder.encode(thisremoteid, "UTF-8");
            		//else
            			data += "&" + URLEncoder.encode("ecRemoteId", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("eckey"), "UTF-8");
            			//Log.i("SYNCH HERE:", "3");            		
            		String value;
            		for(String key : row.datastrings.keySet()){
            			if(row.datastrings.get(key) == null || row.datastrings.get(key).equalsIgnoreCase("")){
            				if(doubles.contains(key))
            					value = "0.0";
            				else if(integers.contains(key))
            					value = "0";
            				else
            					value = "N/A";
            			}
            			else
            				value = row.datastrings.get(key);
            			
            			data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            		}
            		//Log.i("SYNCH HERE:", "4");
            		for(String key : row.spinners.keySet()){
            			value = row.spinners.get(key);
            			
            			data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            		}
            		
                    String[] tempstring;
                    String tempres;
                    int count;
            		for(String key : checkboxgroups){
            			tempres = "";
            			tempstring = checkboxhash.get(key);
            			count = 0;
            			for(String box : tempstring){
            				if(row.checkboxes.get(box) && count == 0){
            					tempres = checkboxvaluesvalueshash.get(box);
            					count++;
            				}
            				else if(row.checkboxes.get(box)){
            					tempres += ","+checkboxvaluesvalueshash.get(box);
            					count++;
            				}
            			}
            			//Log.i("SYNCH HERE:", "5");
            		if(tempres.length() > 0){
            			if(tempres.startsWith(","))
            				tempres.replaceFirst(",", "");
            			
            			data += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(tempres, "UTF-8");
            		}
            			
            		}
            		
            		//Log.i("SYNCH HERE:", "2");
            		//Log.i("SYNCH HERE LAT:", row.datastrings.get("ecgps_lat"));
            		//Log.i("SYNCH HERE LON:", row.datastrings.get("ecgps_lon"));
            		//Log.i("SYNCH HERE ALT:", row.datastrings.get("ecgps_alt"));
            		//Log.i("SYNCH HERE ACC:", row.datastrings.get("ecgps_acc"));
            		//String email = sIMEI;
            		//thisphotoid = sIMEI+ "_"+row.date+".jpg";
            		data += "&" + URLEncoder.encode("ecLatitude", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ecgps_lat"), "UTF-8");
            		data += "&" + URLEncoder.encode("ecLongitude", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ecgps_lon"), "UTF-8");
            		data += "&" + URLEncoder.encode("ecAltitude", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ecgps_alt"), "UTF-8");
            		data += "&" + URLEncoder.encode("ecAccuracy", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ecgps_acc"), "UTF-8");
            		data += "&" + URLEncoder.encode("ecBearing", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ecgps_bearing"), "UTF-8");
            		//data += "&" + URLEncoder.encode("ecTimeCreated", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ectime"), "UTF-8");
            		//data += "&" + URLEncoder.encode("ecLastEdited", "UTF-8") + "=" + URLEncoder.encode(row.datastrings.get("ectime"), "UTF-8");
            		data += "&" + URLEncoder.encode("ecTimeCreated", "UTF-8") + "=" + URLEncoder.encode(row.date, "UTF-8");
            		data += "&" + URLEncoder.encode("ecLastEdited", "UTF-8") + "=" + URLEncoder.encode(row.date, "UTF-8");
            		data += "&" + URLEncoder.encode("ecPhotoPath", "UTF-8") + "=" + URLEncoder.encode(row.photoids.get("ecphoto"), "UTF-8"); // row.photoid+"_"+time, "UTF-8");
            		data += "&" + URLEncoder.encode("ecDeviceID", "UTF-8") + "=" + URLEncoder.encode(sIMEI, "UTF-8"); // row.photoid+"_"+time, "UTF-8");
            		data += "&" + URLEncoder.encode("ecUserEmail", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            		data += "&" + URLEncoder.encode("ecAppName", "UTF-8") + "=" + URLEncoder.encode(DATABASE_PROJECT, "UTF-8");

            		// Send data
            		//Log.i(getClass().getSimpleName(), "DATA: "+ data);
            		//Log.i(getClass().getSimpleName(), "URL: "+ synch_url);
            		URL url = new URL(synch_url); // "http://www.spatialepidemiology.net/epicollect1/test/insert.asp");

            		//URL url = new URL(R.string.data_url);
            		URLConnection conn = url.openConnection();
            		conn.setDoOutput(true);
            		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            		wr.write(data);
            		wr.flush();
            		//Log.i(getClass().getSimpleName(), "WEB RETURN: GOT HERE");
            		// Get the response
           			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
           			String line;
           			while ((line = rd.readLine()) != null) {
           				//Log.i(getClass().getSimpleName(), "WEB RETURN: "+ line);  
           				// Process line...
           				//if(line.contains("0"))
           				//	return "Login Failed. Check email and password";
           					
           				//updateRecordID(row.rowId, thisremoteid, "Y"); //row.rowId);
           			}
           			wr.close();
           			rd.close(); 
           			
           			//The thisphotoid ensures that every photo has a unique id
           			if(row.photoids.get("ecphoto") != null && row.photoids.get("ecphoto").length() > 2){
            			if(!uploadImage(row.photoids.get("ecphoto"), true)) //, thisphotoid); //(row.photoid, time);
            				photoresult = "Images not uploaded";
           			}
           		//}
            }
        } catch (IOException e) {
        	//Log.i(getClass().getSimpleName(), "WEB ERROR: "+ e.toString()); 
        	return "Synchronization Failed: "+e.toString();
        }

        if(result.length() != 0)
        	return "Synchronization Failed - Entries required for: "+result;
        else if(photoresult.length() != 0)
        	return "Synchronization successful but some/all images failed";
        else{
        	updateAllRows("Data");
        	return mCtx.getResources().getString(R.string.synch_success); //"Synchronization Successful";
        }
           	
	} 
       
 // Prevents warnings about List type
/*	@SuppressWarnings("unchecked")
	public int fetchXML(){ //, String lat, String lon){  // ArrayList<Row>
		
		//ArrayList<Row> ret = new ArrayList<Row>();
    	
		//deleteRemoteRows();
		HashMap<String, String> rowhash = new HashMap<String, String>();
		
		getActiveProject();
		getValues("Data");
		
		//String selectstring = "";
		//if(selectvec.size() > 0){
		//	selectstring = selectvec.elementAt(0);
		//for(int i = 1; i < selectvec.size(); i++)
		//	selectstring += ",,"+selectvec.elementAt(i); 
		//}
		
		InputStream xml_stream = null;
    	
    	Element elmnt;
    	NodeList nmElmntLst;
    	Element nmElmnt;
    	NodeList elNm;
    	String xml_url = "http://epicollectserver.appspot.com/downloadFromServer?project="+DATABASE_PROJECT;
    	String title = "";
    	int count = 1;

    	try{
    		
    		URL url = new URL(xml_url);
    		
            HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
            urlc.setRequestMethod("GET");
            urlc.connect();

    		xml_stream = urlc.getInputStream();
    		
    		//Log.i(getClass().getSimpleName(), "XML RETURN: "+ xml_stream.toString());

    	}
    	catch(MalformedURLException  ex){
    		//Log.i(getClass().getSimpleName(), "XML 1: "+ ex.toString());
    		return 0;
    		}
    	catch (IOException ex) {
    		//Log.i(getClass().getSimpleName(), "XML 2: "+ ret.toString());
    		return 0;
    		}
    	
    	try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		Document doc = db.parse(xml_stream); 
    		doc.getDocumentElement().normalize();
    		NodeList nodeLst = doc.getElementsByTagName("entry");

    		for (int s = 0; s < nodeLst.getLength(); s++) { 
    			try{
    				//Row row = new Row();
    				rowhash.clear();
    				
    				title = ""+count;
    				// Initisalise all checkbox values to false
    				//String[] tempstring;
    				//for(String key : checkboxgroups){
    				//	tempstring = checkboxhash.get(key);
    				//	for(String box : tempstring){
    				//		row.checkboxes.put(box, false);
    				//	}
    				//}
    				//row.remote = true;
    				Node fstNode = nodeLst.item(s);
    	    
    				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
    					elmnt = (Element) fstNode;
    					nmElmntLst = elmnt.getElementsByTagName("ecEntryId");
    					nmElmnt = (Element) nmElmntLst.item(0);
    					Log.i(getClass().getSimpleName(), "XML CHECK 13B: ");
    					elNm = nmElmnt.getChildNodes();
    					Log.i(getClass().getSimpleName(), "XML CHECK 13C: ");
    					rowhash.put("eckey", ((Node) elNm.item(0)).getNodeValue());
    					
    					//row.remoteId = ((Node) elNm.item(0)).getNodeValue();
    					Log.i(getClass().getSimpleName(), "XML CHECK 14: ");    					
    					//row.rowId = getNewID();
    					//rowhash.put("rowId", ""+row.rowId);
    					//rowhash.put("eckey", ""+row.datastrings.get("eckey"));
    					rowhash.put("pkey", ((Node) elNm.item(0)).getNodeValue());
    					
    					nmElmntLst = elmnt.getElementsByTagName("ecLatitude");
	    				nmElmnt = (Element) nmElmntLst.item(0);
	    				elNm = nmElmnt.getChildNodes();
	    				rowhash.put("ecgps_lat", ((Node) elNm.item(0)).getNodeValue());
	    				//row.gpslat = ((Node) elNm.item(0)).getNodeValue();
	    				nmElmntLst = elmnt.getElementsByTagName("ecLongitude");
	    				nmElmnt = (Element) nmElmntLst.item(0);
	    				elNm = nmElmnt.getChildNodes();
	    				rowhash.put("ecgps_lon", ((Node) elNm.item(0)).getNodeValue());
	    				//row.gpslon = ((Node) elNm.item(0)).getNodeValue();
	    				nmElmntLst = elmnt.getElementsByTagName("ecAltitude");
	    				nmElmnt = (Element) nmElmntLst.item(0);
	    				elNm = nmElmnt.getChildNodes();
	    				rowhash.put("ecgps_alt", ((Node) elNm.item(0)).getNodeValue());
	    				//row.gpsalt = ((Node) elNm.item(0)).getNodeValue();
	    				try{
	    					nmElmntLst = elmnt.getElementsByTagName("ecAccuracy");
	    					nmElmnt = (Element) nmElmntLst.item(0);
	    					elNm = nmElmnt.getChildNodes();
	    					rowhash.put("ecgps_acc", ((Node) elNm.item(0)).getNodeValue());
	    					//row.gpsacc = ((Node) elNm.item(0)).getNodeValue();
	    				}
	    				catch(NullPointerException npe){
	    					rowhash.put("ecgps_acc", "N/A");
	    					//row.gpsacc = "N/A";
	    				}
	    				    				
	    				nmElmntLst = elmnt.getElementsByTagName("ecTimeCreated");
	    				nmElmnt = (Element) nmElmntLst.item(0);
	    				elNm = nmElmnt.getChildNodes();
	    				rowhash.put("ecdate", ((Node) elNm.item(0)).getNodeValue());
	    				//row.date = ((Node) elNm.item(0)).getNodeValue();
	    				//Log.i(getClass().getSimpleName(), "XML CHECK 6: "+ ((Node)elNm.item(0)).getNodeValue());
	    				for(String key : textviews){
	    					// This isn't in older records so set the value at the end
	    					if(key.equalsIgnoreCase("ecdatev1") || key.equalsIgnoreCase("ectime") || key.equalsIgnoreCase("eckey")){
	    						continue;
	    					}
	            			nmElmntLst = elmnt.getElementsByTagName(key);
	            			if(nmElmntLst.getLength()>=0){
	            				nmElmnt = (Element) nmElmntLst.item(0);
	        					elNm = nmElmnt.getChildNodes();
	        					//row.datastrings.put(key, ((Node) elNm.item(0)).getNodeValue());
	        					rowhash.put(key, ((Node) elNm.item(0)).getNodeValue());
	        					if(listfields.contains(key))
	        						title += " - "+key;
	        					//Log.i(getClass().getSimpleName(), "XML CHECK 7: "+ ((Node)elNm.item(0)).getNodeValue());
	        					//Log.i(getClass().getSimpleName(), "XML TEXTVIEW 1: "+ key+" "+((Node) elNm.item(0)).getNodeValue());
	            			}
	            			else{
	            				//row.datastrings.put(key, "N/A");
	            				rowhash.put(key, "");
	            				//Log.i(getClass().getSimpleName(), "XML TEXTVIEW 1: "+ key+" N/A");
	            			}

	    				}
	            		// Initialise spinners
	            		//for(String val : spinners){
	    				//	row.spinners.put(val, 0);
	    				//	}
	
	            		String value;
	            		int index;
	            		for(String key : spinners){
	            			nmElmntLst = elmnt.getElementsByTagName(key);
	            			if(nmElmntLst.getLength()>=0){
	            				nmElmnt = (Element) nmElmntLst.item(0);
	            				elNm = nmElmnt.getChildNodes();
	            				value = ((Node) elNm.item(0)).getNodeValue();
	            				//Log.i(getClass().getSimpleName(), "XML CHECK 8: "+ ((Node)elNm.item(0)).getNodeValue());
	        				
	            				String[] spinners;
	            				index = 0;
	            				spinners = spinnershash.get(key); 
	        				
	            				for(String val : spinners){
	            					//Log.i(getClass().getSimpleName(), "SPINNER XML INDEX: "+ index+" KEY "+key+" VALUE "+value+" VAL "+val);
	            					if(val.equalsIgnoreCase(value)){
	            						//row.spinners.put(value, index);
	            						rowhash.put(key, ""+index);
	            						if(listspinners.contains(key))
	    	        						title += " - "+key;
	            						//Log.i(getClass().getSimpleName(), "SET SPINNER XML INDEX: "+ index+" KEY "+key+" VALUE "+value+" VAL "+val);
		            					continue;
	            					}
	            					index++;
	            				}
	            			}
	           			
	            		}
	            		String[] tempstring, tempstring2;
	            		for(String key : checkboxgroups){
	            			try{
	            			nmElmntLst = elmnt.getElementsByTagName(key);
	            			if(nmElmntLst.getLength()>=0){
	            				nmElmnt = (Element) nmElmntLst.item(0);
	            				elNm = nmElmnt.getChildNodes();
	            				if(((Node) elNm.item(0)).getNodeValue().length() == 0)
	            					continue;
	            				tempstring = checkboxhash.get(key);
	            				tempstring2 = ((Node) elNm.item(0)).getNodeValue().split(",");
	                			for(String box : tempstring){
	                				for(String box2 : tempstring2){
	                					if(box2.equalsIgnoreCase(checkboxvaluesvalueshash.get(box))){
	                						//if(checkboxhash.get(box2) != null && checkboxhash.get(box2) == true)
	                							rowhash.put(box, "1");
	                						//else
	                						//	rowhash.put(box2, "0");
	                					}
	                				}
	                			}
	            			}
	            			
	            		} 
	            			catch(NullPointerException npe){} 
	            		} 
	            			
	            		
	            		rowhash.put("ecremote", ""+1); 
	            		rowhash.put("ecstored", "R");
	            		rowhash.put("title", title);
	            		rowhash.put("ectime", rowhash.get("ecdate"));
	               		rowhash.put("eckey", rowhash.get("pkey"));
	               		
	               		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	               		Date resultdate = new Date(Long.parseLong(rowhash.get("ecdate")));
	               		rowhash.put("ecdatev1", sdf.format(resultdate));
	            		//if(!checkValue(row.remoteId)){
	            			createRow("Data", rowhash);
	            			
	           			//ret.add(row);
	            		//for(String key : rowhash.keySet())
	            		//	Log.i("ROWHASH", key+" "+rowhash.get(key));
	            		//}
	    			}
	    		}
				catch(NullPointerException npe){
					Log.i(getClass().getSimpleName(), "XML ERROR 1: "+ npe.toString());
					return 0;
				}
				
				count++;
	    		}
	    	}	 
	    	catch (Exception e) {
	    	    e.printStackTrace();
	    	    return 0;
	    	} 
	    	//Log.i(getClass().getSimpleName(), "XML HERE: FINISHED");	
	    	return 3;
	    } 
*/
	
	
	private boolean uploadImage(String photoid, boolean thumb){ //, String thisphotoid){ 
		
		getValues();
		
		String imagedir, image_url;
		//Log.i("DBAccess UPLOADIMAGE","PHOTOID "+ photoid);
		
		HttpURLConnection conn = null;
	    DataOutputStream dos = null;
	    DataInputStream inStream = null;

	   // photoid += ".jpg";
	   String existingFileName = photoid;
       // Is this the place are you doing something wrong.

       String lineEnd = "\r\n";
       String twoHyphens = "--";
       String boundary =  "*****";

       int bytesRead, bytesAvailable, bufferSize;
       byte[] buffer;
       int maxBufferSize = 1*1024*1024;
       
       if(thumb)
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/thumbs"; //Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject(); //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
       else
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/images"; //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + getProject(); //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
           	   
       image_url = "http://epicollectserver.appspot.com/uploadImageToServer"; //getValue("image_url"); // context.getResources().getString(context.getResources().getIdentifier(this.getClass().getPackage().getName()+":string/image_url", null, null));
             
       //String responseFromServer = "";

       String urlString = image_url;
       
       try{
        //------------------ CLIENT REQUEST
	     
	       Log.e("DBAccess","Inside second Method");
	       
	
	       FileInputStream fileInputStream = new FileInputStream(new File(imagedir+"/" + existingFileName) );
	
	        // open a URL connection to the Servlet
	
	        URL url = new URL(urlString);
	
	        // Open a HTTP connection to the URL
	          		
    		if (url.getProtocol().toLowerCase().equals("https")) {
	            Epi_collect.trustAllHosts();
	            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
	            conn = https;
	        } else {
	            conn = (HttpURLConnection) url.openConnection();
	        }
	
	        //conn = (HttpURLConnection) url.openConnection();
	
	        // Allow Inputs
	        conn.setDoInput(true);
	        // Allow Outputs
	        conn.setDoOutput(true);
	
	        // Don't use a cached copy.
	        conn.setUseCaches(false);
	
	        // Use a post method.
	        conn.setRequestMethod("POST");
	
	        conn.setRequestProperty("Connection", "Keep-Alive");
		     
	        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	        dos = new DataOutputStream( conn.getOutputStream() );
	
	        Log.i("PHOTOID", photoid);
	        existingFileName = existingFileName.replace(".jpg", "");
	        Log.i("PHOTOID 2", existingFileName);
	        
	        dos.writeBytes(twoHyphens + boundary + lineEnd);
	        dos.writeBytes("Content-Disposition: form-data; name=\""+photoid+"\";filename=\"" + existingFileName +"\"" + lineEnd);
	        //dos.writeBytes("Content-Disposition: form-data; name=\""+photoid+"\";filename=\"" + existingFileName +"\"" + lineEnd);
	        
	        dos.writeBytes(lineEnd);
	
	        Log.e("DBAccess","Headers are written");
	
	        // create a buffer of maximum size
	
	        bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        buffer = new byte[bufferSize];
	
	        // read file and write it into form...
	
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
	        while (bytesRead > 0){
		         dos.write(buffer, 0, bufferSize);
		         bytesAvailable = fileInputStream.available();
		         bufferSize = Math.min(bytesAvailable, maxBufferSize);
		         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	        }
	
	        // send multipart form data necesssary after file data...
	
	        dos.writeBytes(lineEnd);
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	        // close streams
	        //Log.i("DBAccess","File is written "+ existingFileName);
	        fileInputStream.close();
	        dos.flush();
	        dos.close();


       }
       catch (MalformedURLException ex){
            Log.e("DBAccess", "error: " + ex.getMessage(), ex);
            return false;
       }

       catch (IOException ioe){
	        Log.e("DBAccess", "error: " + ioe.getMessage(), ioe);
	        return false;
	   }

       //------------------ read the SERVER RESPONSE

       try {
             inStream = new DataInputStream ( conn.getInputStream() );
             String str;
	           
             while (( str = inStream.readLine()) != null)
             {
                  Log.e("DBAccess","Server Response "+str);
             }
             inStream.close();
	       }
       catch (IOException ioex){
            Log.e("DBAccess", "error: " + ioex.getMessage(), ioex);
       }

       return true;
     }
		
	private void getValues(String table){
    	textviews = new String[0];
    	photos = new String[0];
    	videos = new String[0];
    	audio = new String[0];
    	spinners = new String[0];
    	radios = new String[0];
    	checkboxes = new String[0];
    	checkboxgroups = new String[0];
    	gpstags = new String[0];
    	
		if(getValue(table, "textviews") != null && getValue(table, "textviews").length() > 0)
			textviews = (getValue(table, "textviews")).split(",,"); // "CNTD", 
			
		if(getValue(table, "photos") != null && getValue(table, "photos").length() > 0)
			photos = (getValue(table, "photos")).split(",,"); // "CNTD", 
		
		if(getValue(table, "videos") != null && getValue(table, "videos").length() > 0)
			videos = (getValue(table, "videos")).split(",,"); // "CNTD", 
		
		if(getValue(table, "audio") != null && getValue(table, "audio").length() > 0)
			audio = (getValue(table, "audio")).split(",,"); // "CNTD", 
		
    	if(getValue(table, "spinners") != null && getValue(table, "spinners").length() > 0){
    		spinners = (getValue(table, "spinners")).split(",,");
    		//Log.i(getClass().getSimpleName(), "SPINNERS IN DBACCESS-"+getValue(table, "spinners").toString()+"-");
    	}
    	
    	if(getValue(table, "radios") != null && getValue(table, "radios").length() > 0)
    		radios = (getValue(table, "radios")).split(",,");
    	
    	if(getValue(table, "checkboxes") != null && getValue(table, "checkboxes").length() > 0)
    		checkboxes = (getValue(table, "checkboxes")).split(",,");
    	
    	if(getValue(table, "checkboxgroups") != null && getValue(table, "checkboxgroups").length() > 0)
    		checkboxgroups = (getValue(table, "checkboxgroups")).split(",,");
    	
    	if(getValue(table, "gps") != null && getValue(table, "gps").length() > 0)
    		gpstags = (getValue(table, "gps")).split(",,");
    	   	
    	if(getValue(table, "doubles") != null && getValue(table, "doubles").length() > 0){
    		for(String key : (getValue(table, "doubles")).split(",,")){
    			doubles.addElement(key);
    		}
    	}
        
    	if(getValue(table, "integers") != null && getValue(table, "integers").length() > 0){
    		for(String key : (getValue(table, "integers")).split(",,")){
    			integers.addElement(key);
    		}
    	}
        
    	if(getValue(table, "local") != null && getValue(table, "local").length() > 0){
    		for(String key : (getValue(table, "local")).split(",,")){
    			local.addElement(key);
    		}
    	}
                           
        String[] tempstring = null;
       /* for(String key : spinners){   
        	//Log.i(getClass().getSimpleName(), "SPINNERS IN DBACCESS2-"+key+"-");
        	tempstring = getValue(table, "spinner_"+key).split(",,");
	    	spinnershash.put(key, tempstring);
        }   
        
        for(String key : radios){   
        	//Log.i(getClass().getSimpleName(), "SPINNERS IN DBACCESS2-"+key+"-");
        	tempstring = getValue(table, "radio_"+key).split(",,");
	    	radioshash.put(key, tempstring);
        }   */
        
       /* if(getProject().equalsIgnoreCase("SCORE_FULL") || getProject().equalsIgnoreCase("SCORE_DEMO") || getProject().equalsIgnoreCase("Tanz")){
        	for(String key : spinners){       	
        		tempstring = getValue(table, "spinner_values_"+key).split(",,");
        		spinnersvalueshash.put(key, tempstring);
        	}
        
        	for(String key : radios){       	
        		tempstring = getValue(table, "radio_values_"+key).split(",,");
        		radiosvalueshash.put(key, tempstring);
        	}
        } */
        
        String[] tempstring2;
        for(String key : checkboxgroups){
        	if(getValue(table, "checkbox_"+key) != null){
        		tempstring = getValue(table, "checkbox_"+key).split(",,");
 	    		checkboxhash.put(key, tempstring);
        	}
 	    	
        	if(getValue(table, "checkbox_values_"+key) != null){
        		tempstring2 = getValue(table, "checkbox_values_"+key).split(",,");
        		for(int i = 0; i < tempstring.length; i++){
        			checkboxvaluesvalueshash.put(tempstring[i], tempstring2[i]);
 	    			}
	    		}
        	}
        
        List<String> list;
        //if(getValue(table, "requiredtext") != null && getValue(table, "requiredtext").length() > 0){
        //	list = Arrays.asList(getValue(table, "requiredtext").split(",,"));
        //	requiredfields = new Vector<String>(list);
        //}
        
        //if(getValue(table, "requiredspinners") != null && getValue(table, "requiredspinners").length() > 0){
        //	list = Arrays.asList(getValue(table, "requiredspinners").split(",,"));
        //	requiredspinners = new Vector<String>(list);
        //}
        
        if(getValue(table, "listfields") != null && getValue(table, "listfields").length() > 0){
        	list = Arrays.asList(getValue(table, "listfields").split(",,")); //("\\s+"));
        	listfields = new Vector<String>(list);
        }
        
        if(getValue(table, "listspinners") != null && getValue(table, "listspinners").length() > 0){
        	list = Arrays.asList(getValue(table, "listspinners").split(",,")); //("\\s+"));
        	listspinners = new Vector<String>(list);
        }
        
        if(getValue(table, "listradios") != null && getValue(table, "listradios").length() > 0){
        	list = Arrays.asList(getValue(table, "listradios").split(",,")); //("\\s+"));
        	listradios = new Vector<String>(list);
        }
        
        //if(getValue(table, "listcheckboxes") != null && getValue(table, "listcheckboxes").length() > 0){
        //	list = Arrays.asList(getValue(table, "listcheckboxes").split(",,")); //("\\s+"));
        //	listcheckboxes = new Vector<String>(list);
        //}
        
        
    }
	
	// Only need dates for SynchroniseAll to return UTC dates
	private  void getDates(String table){
		
		String[] temp;
		//if(dates != null)
		//	dates.clear();
		///else
		//	dates = new Hashtable <String, String>();
		if(setdates != null)
			setdates.clear();
		else
			setdates = new Hashtable <String, String>();
		if(getValue(table, "dates") != null && getValue(table, "dates").length() > 0){
    		for(String key : (getValue(table, "dates")).split(",,")){
    			temp = key.split("\t");
    			if(temp.length > 1){
    				setdates.put(temp[0], temp[1]);
    			}
    		}
    	}
    	
    	if(getValue(table, "setdates") != null && getValue(table, "setdates").length() > 0){
    		for(String key : (getValue(table, "setdates")).split(",,")){
    			temp = key.split("\t");
    			if(temp.length > 1){
    				setdates.put(temp[0], temp[1]);
    			}
    		}
    	}
    	
    	if(getValue(table, "settimes") != null && getValue(table, "settimes").length() > 0){
    		for(String key : (getValue(table, "settimes")).split(",,")){
    			temp = key.split("\t");
    			if(temp.length > 1){
    				setdates.put(temp[0], temp[1]);
    			}
    		}
    	}
	}
	
	private void getValues2(String table){
    	textviews2 = new Vector<String>();
    	photos2 = new Vector<String>();
    	videos2 = new Vector<String>();
    	audio2 = new Vector<String>();
    	spinners2 = new Vector<String>();
    	radios2 = new Vector<String>();
    	checkboxes2 = new Vector<String>();
    	checkboxgroups2 = new Vector<String>();
    	gpstags2 = new Vector<String>();    	
    	
    	//Log.i("DOING TEXTVIEWS2", "NOW");
    	
		if(getValue(table, "textviews") != null && getValue(table, "textviews").length() > 0)
			textviews2 = new Vector(Arrays.asList((getValue(table, "textviews")).split(",,"))); // "CNTD", 
		
		//for(String x : textviews2)
		//	Log.i("TEXTVIEWS2", x);
		
		if(getValue(table, "photos") != null && getValue(table, "photos").length() > 0)
			photos2 = new Vector(Arrays.asList((getValue(table, "photos")).split(",,"))); // "CNTD", 
		
		if(getValue(table, "videos") != null && getValue(table, "videos").length() > 0)
			videos2 = new Vector(Arrays.asList((getValue(table, "videos")).split(",,"))); // "CNTD", 
		
		if(getValue(table, "audio") != null && getValue(table, "audio").length() > 0)
			audio2 = new Vector(Arrays.asList((getValue(table, "audio")).split(",,"))); // "CNTD", 
		
    	if(getValue(table, "spinners") != null && getValue(table, "spinners").length() > 0){
    		spinners2 = new Vector(Arrays.asList((getValue(table, "spinners")).split(",,")));
    		//Log.i(getClass().getSimpleName(), "SPINNERS IN DBACCESS-"+getValue(table, "spinners").toString()+"-");
    	}
    	
    	if(getValue(table, "radios") != null && getValue(table, "radios").length() > 0)
    		radios2 = new Vector(Arrays.asList((getValue(table, "radios")).split(",,")));
    	
    	if(getValue(table, "checkboxes") != null && getValue(table, "checkboxes").length() > 0)
    		checkboxes2 = new Vector(Arrays.asList((getValue(table, "checkboxes")).split(",,")));
    	
    	if(getValue(table, "checkboxgroups") != null && getValue(table, "checkboxgroups").length() > 0)
    		checkboxgroups2 = new Vector(Arrays.asList((getValue(table, "checkboxgroups")).split(",,")));
    	
    	if(getValue(table, "gps") != null && getValue(table, "gps").length() > 0)
    		gpstags2 = new Vector(Arrays.asList((getValue(table, "gps")).split(",,")));
    	
    	
        
    }
	
	private void getFileValues(String table){
    	photos = new String[0];
    	videos = new String[0];
    	audio = new String[0];
		
		if(getValue(table, "photos") != null && getValue(table, "photos").length() > 0)
			photos = (getValue(table, "photos")).split(",,"); // "CNTD", 
		
		if(getValue(table, "videos") != null && getValue(table, "videos").length() > 0)
			videos = (getValue(table, "videos")).split(",,"); // "CNTD", 
		
		if(getValue(table, "audio") != null && getValue(table, "audio").length() > 0)
			audio = (getValue(table, "audio")).split(",,"); // "CNTD", 
		        
    }
	
	// Should be redundant
	private void getValues(){
    	textviews = new String[0];
    	spinners = new String[0];
    	radios = new String[0];
    	checkboxes = new String[0];
    	checkboxgroups = new String[0];
    	
		if(getValue("textviews") != null)
			textviews = (getValue("textviews")).split(",,"); // "CNTD", 
    	if(getValue("spinners") != null)
    		spinners = (getValue("spinners")).split(",,");
    	if(getValue("radios") != null)
    		radios = (getValue("radios")).split(",,");
    	if(getValue("checkboxes") != null)
    		checkboxes = (getValue("checkboxes")).split(",,");
    	if(getValue("checkboxgroups") != null)
    		checkboxgroups = (getValue("checkboxgroups")).split(",,");
    	
    	for(String key : (getValue("doubles")).split(",,")){
        	doubles.addElement(key);
        }
        
        for(String key : (getValue("integers")).split(",,")){
        	integers.addElement(key);
        }
                           
        String[] tempstring = null;
      /*  for(String key : spinners){       	
        	tempstring = getValue("spinner_"+key).split(",,");
	    	spinnershash.put(key, tempstring);
        }   */    
        
       /* if(getProject().equalsIgnoreCase("SCORE_FULL") || getProject().equalsIgnoreCase("SCORE_DEMO") || getProject().equalsIgnoreCase("Tanz")){
        	for(String key : spinners){       	
        		tempstring = getValue("spinner_values_"+key).split(",,");
        		spinnersvalueshash.put(key, tempstring);
        	}
        
            for(String key : radios){       	
            	tempstring = getValue("radio_values_"+key).split(",,");
            	radiosvalueshash.put(key, tempstring);
            }
        } */
        
       /* for(String key : radios){       	
        	tempstring = getValue("radio_"+key).split(",,");
	    	radioshash.put(key, tempstring);
        } */      
        
        String[] tempstring2;
        for(String key : checkboxgroups){
        	if(getValue("checkbox_"+key) != null){
        		tempstring = getValue("checkbox_"+key).split(",,");
 	    		checkboxhash.put(key, tempstring);
        	}
 	    	
        	if(getValue("checkbox_values_"+key) != null){
        		tempstring2 = getValue("checkbox_values_"+key).split(",,");
        		for(int i = 0; i < tempstring.length; i++){
        			checkboxvaluesvalueshash.put(tempstring[i], tempstring2[i]);
 	    			}
	    		}
        	}
        //List<String> list = Arrays.asList(getValue("requiredtext").split(",,"));
        //requiredfields = new Vector<String>(list);
        
        //list = Arrays.asList(getValue("requiredspinners").split(",,"));
        //requiredspinners = new Vector<String>(list);
        
        
        
    }
	
	private void deleteImage(String pic, boolean full){
		File picfile;
		try{
			if(full){
				picfile = new File(Epi_collect.appFiles+"/"+getProject()+"/images/"+pic); //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + getProject()+"/"+pic);
				picfile.delete();
			}
        	picfile = new File(Epi_collect.appFiles+"/"+getProject()+"/thumbs/"+pic); //Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject()+"/"+pic);
        	picfile.delete();
        	}
        catch(Exception e){}
	}
	/*private static void copyFile(File f1, File f2){ //String srFile, String dtFile){
        try{

          InputStream in = new FileInputStream(f1);

          //For Overwrite the file.
          OutputStream out = new FileOutputStream(f2);

          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
          }
          in.close();
          out.close();
        }
        catch(FileNotFoundException ex){
        	//Log.i("ImageSwitcher", ex.toString());
        }
        catch(IOException e){
        	//Log.i("ImageSwitcher", e.toString());
        }
      }*/
	
	public String synchroniseAllOLD(String sIMEI, String email){
		
		//boolean store;
		List<Row> rows;
		String value="";
		Vector<String> photostoupload = new Vector<String>();
		//Vector<String> videostoupload = new Vector<String>();
		//Vector<String> audiotoupload = new Vector<String>();
		Vector<String> localphotostoupload = new Vector<String>();
		//Vector<String> localvideostoupload = new Vector<String>();
		//Vector<String> localaudiotoupload = new Vector<String>();
		
		// First a list of the tables
		// Only need the keys from the hash
		LinkedHashMap<String, String> keyshash = getAllKeys(1);
		
		String dir = Epi_collect.appFiles.toString(); //Environment.getExternalStorageDirectory()+"/EpiCollect/";
		
		//String ecid;
		// fix sIMEI if not set
		if (sIMEI==null){
			sIMEI="null";
		}
		
		//Log.i("LOCAL", getValue("synch_local_url"));
		boolean synchlocal = true;
		if(getValue("synch_local_url").equalsIgnoreCase("None"))
			synchlocal = false;
		
		try{
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(dir+"/"+getProject()+"_data.xml"),"UTF-8");
			//BufferedWriter os = new BufferedWriter(new FileWriter(dir+"/"+getProject()+"_data.xml"));
			os.write("<entries>");
			
			OutputStreamWriter osloc = null;
			//BufferedWriter osloc = null;
			if(synchlocal){
				osloc = new OutputStreamWriter(new FileOutputStream(dir+"/"+getProject()+"_data_local.xml"),"UTF-8");
				//osloc = new BufferedWriter(new FileWriter(dir+"/"+getProject()+"_data_local.xml"));
				osloc.write("<entries>");
			}
			
			
			//os.newLine();
			// Need to specify a newline
			
			for(String table : keyshash.keySet()){
						
				/*local.removeAllElements(); // = new Vector<String>();
		    	
		    	if(getValue(table, "local") != null && getValue(table, "local").length() > 0){
		    		for(String key : (getValue(table, "local")).split(",,")){
		    			local.addElement(key);
		    		}
		    	}*/
		    	
		    	//os.write("IN HERE ");
				//for(String x : local)
				//	os.write("LOCAL "+x);
				
		    	
				os.write("\n\t<table>");
				os.write("\n\t\t<table_name>"+table+"</table_name>");
				if(synchlocal){
					osloc.write("\n\t<table>");
					osloc.write("\n\t\t<table_name>"+table+"</table_name>");
				}
						
				
				getActiveProject();
		    	getValues(table);
		    	Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where ecstored = 'N'", null);
		    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
		    	
		    	Row row;
		    	
		    	//////////////////////////////////////////////////////
		    	//
		    	// FILE ISN'T UPLOADED
		    	// UNCOMMENT PHOTOSUPLOAD
		    	//
		    	//////////////////////////////////////////////////////
		        //ArrayList<Row> ret = new ArrayList<Row>();
		        try {
		            int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	row = getRow(c);				
		            	
		            	c.moveToNext();
				//rows = fetchAllRows(table, false); 
				
				String xkey, gpskey;
				//for (Row row : rows) {
				
	            	//store = true;
	            	
					//ecid = sIMEI +"_"+ row.date;
					
	            	os.write("\n\t\t<entry>");
	            	os.write("\n\t\t\t<ecTimeCreated>"+row.date+"</ecTimeCreated>");
	            	os.write("\n\t\t\t<ecPhoneID>"+sIMEI+"</ecPhoneID>");
	            	//os.write("\n\t\t\t<phonekey>"+row.phonekey+"</phonekey>");
	            	if(synchlocal){
	            		osloc.write("\n\t\t<entry>");
	            		osloc.write("\n\t\t\t<ecTimeCreated>"+row.date+"</ecTimeCreated>");
	            		osloc.write("\n\t\t\t<ecPhoneID>"+sIMEI+"</ecPhoneID>");
	            		//osloc.write("\n\t\t\t<phonekey>"+row.phonekey+"</phonekey>");
	            	}
	            	
	            	//os.write("\n\t\t\t<ecTimeCreated>"+row.date+"</ecTimeCreated>");
	            	
	            	for(String key : row.datastrings.keySet()){
	            		
	            		//Log.i("KEY", key+" VALUE "+row.datastrings.get(key));
	            		
	            		xkey = key.replaceAll("\\s+", "_");
	            		gpskey = key;
	            		//if(key.contains("_lat"))
	            		gpskey = gpskey.replaceAll("_lat", "");
	            		//if(key.contains("_lon"))
	            		gpskey = gpskey.replaceAll("_lon", "");
	            		//if(key.contains("_alt"))
	            		gpskey = gpskey.replaceAll("_alt", "");
	            		//if(key.contains("_acc")) 
	            		gpskey = gpskey.replaceAll("_acc", "");
	            		gpskey = gpskey.replaceAll("_bearing", "");
	            		//if(key.contains("_provider"))
	            		gpskey = gpskey.replaceAll("_provider", ""); 
	            		
	            		//os.write("KEY "+key+" GPSKEY "+gpskey);
	            		
	            		if(row.datastrings.get(key) == null || row.datastrings.get(key).equalsIgnoreCase("")){
            			
	            			if(doubles.contains(key) || integers.contains(key)){
	            				//if(!local.contains(key))
	            					os.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            				if(synchlocal)
	            					osloc.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            			}
	            			else{
	            				//if(!local.contains(key))
	            					os.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            				if(synchlocal)
	            					osloc.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            			}
	            		}
	            		else{
	            			value = row.datastrings.get(key);
	            			value = value.replaceAll("&", "&amp;");
	            			if(synchlocal){
	            				osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(!local.contains(key) && !local.contains(gpskey))
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				}
	            			//else if(!local.contains(key) && !local.contains(gpskey))
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			}
	            		
	            		}

	            		for(String key : row.spinners.keySet()){
	            			//Log.i("SPINNER", key);
	            			xkey = key.replaceAll("\\s+", "_");
	            			
	            			//if(getProject().equalsIgnoreCase("SCORE_FULL") || getProject().equalsIgnoreCase("SCORE_DEMO") || getProject().equalsIgnoreCase("Tanz"))
	            			//	value = spinnersvalueshash.get(key)[Integer.valueOf(row.spinners.get(key))];
	            			//else
	            				value = row.spinners.get(key);
	            			//os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			if(synchlocal){
	            				osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(!local.contains(key))
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			}
	            			else if(!local.contains(key))
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			
	            		}
	            		
	            		for(String key : row.radios.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			
	            			//if(getProject().equalsIgnoreCase("SCORE_FULL") || getProject().equalsIgnoreCase("SCORE_DEMO") || getProject().equalsIgnoreCase("Tanz"))
	            			//	value = radiosvalueshash.get(key)[Integer.valueOf(row.radios.get(key))];
	            			//else
	            				value = row.radios.get(key);
	            			
	            			//os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			if(synchlocal){
	            				osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(!local.contains(key))
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			}
	            			else if(!local.contains(key))
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			
	            		}
        		
	            		String[] tempstring;
	            		String tempres;
	            		int count;
	            		for(String key : checkboxgroups){
	            			xkey = key.replaceAll("\\s+", "_");
	            			tempres = "";
	            			tempstring = checkboxhash.get(key);
	            			count = 0;
	            			
	            			//Log.i("CHECKBOX 1", tempstring.toString()+" - "+key);
	            			for(String box : tempstring){
	            				//Log.i("CHECKBOX 2", box+" - "+key);
	            				if(row.checkboxes.get(box) && count == 0){
	            					tempres = checkboxvaluesvalueshash.get(box);
	            					count++;
	            				}
	            				else if(row.checkboxes.get(box)){
	            					tempres += ","+checkboxvaluesvalueshash.get(box);
	            					count++;
	            				}
	            			}
	            			//Log.i("CHECKBOX 3", tempres);
	            			if(tempres.length() > 0){
	            				if(tempres.startsWith(","))
	            				tempres.replaceFirst(",", "");
	            				
	            				if(synchlocal){	            				
	            					osloc.write("\n\t\t\t<"+xkey+">"+tempres+"</"+xkey+">");
	            					if(!local.contains(key))
	            						os.write("\n\t\t\t<"+xkey+">"+tempres+"</"+xkey+">");
	            				}
	            				else if(!local.contains(key))
	            					os.write("\n\t\t\t<"+xkey+">"+tempres+"</"+xkey+">");
	            			}
	            		}
	            		
	            		for(String key : row.photoids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.photoids.get(key);
	            			
	            			if(!value.equals("-1")){
	            				
	            				//os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(synchlocal){	
	            					localphotostoupload.addElement(value);
	            					osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            					if(!local.contains(key)){
	            						photostoupload.addElement(value);
	            						os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            					}
	            				}
	            				else if(!local.contains(key)){
	            					photostoupload.addElement(value);
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				}
	            			}
	            		}
	            		
	            		for(String key : row.videoids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.videoids.get(key);
        			
	            			//os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			if(synchlocal){
	            				//localvideostoupload.addElement(value);
	            				osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(!local.contains(key)){
	            					//videostoupload.addElement(value);
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				}
	            			}
	            			else if(!local.contains(key)){
	            				//videostoupload.addElement(value);
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			}
	            		}
	            		
	            		for(String key : row.audioids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.audioids.get(key);
        			
	            			//os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			if(synchlocal){	     
	            				//localaudiotoupload.addElement(value);
	            				osloc.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				if(!local.contains(key)){
	            					//audiotoupload.addElement(value);
	            					os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				}
	            			}
	            			else if(!local.contains(key)){
	            				//audiotoupload.addElement(value);
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			}
	            		}
        		
	            		os.write("\n\t\t</entry>");
	            		if(synchlocal)
	            			osloc.write("\n\t\t</entry>");
	            		
					}
				os.write("\n\t</table>");
				if(synchlocal)
					osloc.write("\n\t</table>");
				
                //c.moveToNext();
            
        } catch (SQLException e) {
        	c.close();
            Log.e("booga", e.toString());
        }
        c.close();
		}
		
		os.write("\n</entries>\n");
		os.close();
		if(synchlocal){
			osloc.write("\n</entries>\n");
			osloc.close();
			}
		}
	catch(IOException e){
		return ("ERROR - "+e.toString());
		//System.out.println(e);
		}	
			
	String result = "ERROR: ";
	boolean success = true;
	
	String res = "";
	String project = getProject();
	
	//zipFile(project+"_data.xml");   
	//res = uploadFile(getProject()+"_data.xml.zip", false, false, false, false, false, sIMEI, email);
	res = uploadFile(getProject()+"_data.xml", false, false, false, false, false, sIMEI, email);
	//result += res;
	if(!res.equalsIgnoreCase("200")){ // !res.equalsIgnoreCase("1")
		Log.i("RES1", res);
	//if(!uploadFile(getProject()+"_data.xml", false, false, false, false, false, sIMEI, email)){
		success = false;
		result += "Upload to remote server failed. ";
		if(res.startsWith("Message:")){
			res.replaceFirst("Message:\\s+", "");
			result += "res";
		}
	}
	if(synchlocal){
		//zipFile(project+"_data_local.xml");
		//res = uploadFile(getProject()+"_data_local.xml.zip", true, false, false, false, false, sIMEI, email);
		res = uploadFile(getProject()+"_data_local.xml", true, false, false, false, false, sIMEI, email);
		if(!res.equalsIgnoreCase("200")){ // !res.equalsIgnoreCase("1")
		//if(!uploadFile(getProject()+"_data_local.xml", true, false, false, false, false, sIMEI, email)){
			success = false;
			result += "Upload to local server failed. ";
			if(res.startsWith("Message:")){
				res.replaceFirst("Message:\\s+", "");
				result += "res";
			}
		}
	}

	for(String key : photostoupload){
		if(!uploadFile(key, false, true, true, false, false, sIMEI, email).equalsIgnoreCase("200")){ // ("1") !uploadFile(key, false, true, true, false, false, sIMEI, email)){
			success = false;
			result += "Upload of images to remote server failed. ";
		}
	}
	
	for(String key : localphotostoupload){
		if(!uploadFile(key, true, true, true, false, false, sIMEI, email).equalsIgnoreCase("200")){ // ("1") !uploadFile(key, true, true, true, false, false, sIMEI, email)){
			success = false;
			result += "Upload of images to local server failed. ";
		}
	}
	
	/*for(String key : photostoupload){
		if(!uploadFile(key, false, true, true, false, false, sIMEI, email).equalsIgnoreCase("1")){ //!uploadFile(key, false, true, true, false, false, sIMEI, email)){
			success = false;
			result += "Upload of images to remote server failed. ";
		}
		if(synchlocal){
			if(!uploadFile(key, true, true, true, false, false, sIMEI, email).equalsIgnoreCase("1")){ //!uploadFile(key, true, true, true, false, false, sIMEI, email)){
				success = false;
				result += "Upload of images to local server failed. ";
			}
		}
	} */
	
	/*for(String key : videos){
		if(!uploadFile(key, false, true, true)){
			success = false;
			result += "Upload of videos to remote server failed. ";
		}
		if(synchlocal){
			if(!uploadFile(key, true, true, true)){
				success = false;
				result += "Upload of videos to local server failed. ";
			}
		}
	}*/
	
	if(success){
		for(String table : keyshash.keySet()){
			updateAllRows(table);	
		}
		return "Success";
	}
	else{
		return result;
	} 
	
}
	
	
	
	
	
public String synchroniseAll(String sIMEI, String email){
		
		List<Row> rows;
		String value="";
		Vector<String> photostoupload = new Vector<String>();
		Vector<String> localphotostoupload = new Vector<String>();
		Vector<String> primary_keys = new Vector<String>();
		
		// First a list of the tables
		// Only need the keys from the hash
		LinkedHashMap<String, String> keyshash = getAllKeys(1);
		Hashtable<String, String> pkeyhash = new Hashtable<String, String>();
		
		//fix sIMEI for devices which don't set it
		if (sIMEI==null) {
			sIMEI="null";
		}
		
		//String dir = Epi_collect.appFiles.toString(); //Environment.getExternalStorageDirectory()+"/EpiCollect/";
		
		String urlString = "", local_urlString = "", data = "", localdata = "";
		HttpURLConnection conn;
		URL url;
		OutputStreamWriter wr;
		//DataInputStream inStream;
		String retstr, responsecode, responsemessage, responsebody = "", pkey="", message = ""; // , pkeyid
		Vector<String> orphantables = new Vector<String>(), local_orphantables = new Vector<String>();
		boolean synchlocal = true, error = false, server_error = false, client_error = false, other_error = false, orphan_error = false, this_orphan_error = false,
				local_server_error = false, local_client_error = false, local_other_error = false, local_orphan_error = false;
		
		local_urlString = getValue("synch_local_url");
	    urlString = getValue("synch_url");

	    //Log.i("LOCAL URL", local_urlString);
	    
	    if(local_urlString.equalsIgnoreCase("None"))
			synchlocal = false;
	    
	    urlString += "?type=data"; //&phoneid="+sIMEI;
       	//urlString += "&email="+email;
       	local_urlString += "?type=data"; //&phoneid="+sIMEI;
       	//local_urlString += "&email="+email;
				
       	//Log.i("URL STRING", urlString);
       		
       	// Keep the sceen active or db connection lost
       	PowerManager pm = (PowerManager) thiscontext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();

			for(String table : keyshash.keySet()){
			
				getActiveProject();
		    	getValues(table);
		    	getDates(table);
		    	
		    	//pkeyid = getKeyValue(table);
		    	
		    	primary_keys.clear();
		    	for(String key : getValue(table, "ecpkey").split(";")){
	    			primary_keys.addElement(key);
	        	}
	    			    	
		    	Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table+" where ecstored = 'N' or ecstored = 'F'", null);
		    	
		    	Row row;

		        try {
		            int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	row = getRow(c);				
		            	
		            	c.moveToNext();
				
		            	String xkey, gpskey;
			
		            	data = URLEncoder.encode("epicollect_insert", "UTF-8") + "=" + URLEncoder.encode("form1", "UTF-8");
		            	data += "&" + URLEncoder.encode("table", "UTF-8") + "=" + URLEncoder.encode(table, "UTF-8");
		            	if(synchlocal){
		            		localdata = URLEncoder.encode("epicollect_insert", "UTF-8") + "=" + URLEncoder.encode("form1", "UTF-8");
		            		localdata += "&" + URLEncoder.encode("table", "UTF-8") + "=" + URLEncoder.encode(table, "UTF-8");
		            	}
	            	
		            	data += "&" + URLEncoder.encode("ecTimeCreated", "UTF-8") + "=" + URLEncoder.encode(row.date, "UTF-8");
		            	data += "&" + URLEncoder.encode("ecPhoneID", "UTF-8") + "=" + URLEncoder.encode(sIMEI, "UTF-8");
        		
		            	if(synchlocal){
		            		localdata += "&" + URLEncoder.encode("ecTimeCreated", "UTF-8") + "=" + URLEncoder.encode(row.date, "UTF-8");
		            		localdata += "&" + URLEncoder.encode("ecPhoneID", "UTF-8") + "=" + URLEncoder.encode(sIMEI, "UTF-8");
		            	}
        		          	
		            	pkey = "";
		            	for(String key : row.datastrings.keySet()){
		            			            		            		
		            		xkey = key.replaceAll("\\s+", "_");
		            		gpskey = key;
		            		//if(key.contains("_lat"))
		            		gpskey = gpskey.replaceAll("_lat", "");
		            		//if(key.contains("_lon"))
		            		gpskey = gpskey.replaceAll("_lon", "");
		            		//if(key.contains("_alt"))
		            		gpskey = gpskey.replaceAll("_alt", "");
		            		//if(key.contains("_acc")) 
		            		gpskey = gpskey.replaceAll("_acc", "");
		            		gpskey = gpskey.replaceAll("_bearing", "");
		            		//if(key.contains("_provider"))
		            		gpskey = gpskey.replaceAll("_provider", ""); 
	            			            		
		            		if(row.datastrings.get(key) == null || row.datastrings.get(key).equalsIgnoreCase("")){
            			
		            			/*if(doubles.contains(key) || integers.contains(key)){
            						os.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            					if(synchlocal)
	            						osloc.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            					} 
	            					else{
            							os.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            						if(synchlocal)
	            							osloc.write("\n\t\t\t<"+xkey+"></"+xkey+">");
	            						}*/
	            					}

		            			value = row.datastrings.get(key);
		            			
		            			if(primary_keys.contains(key)) //.equalsIgnoreCase(pkeyid))
		            				pkeyhash.put(key,  value); //pkey += ","+key+","+value;
		            			
		            			
		            			value = value.replaceAll("&", "&amp;");
		            			if(synchlocal){
		            				localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
		            				if(!local.contains(key) && !local.contains(gpskey))
		            					data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
		            				}
		            			else //if(!local.contains(key) && !local.contains(gpskey))
		            				data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            				//}
		            			
		            			java.util.Date date = null;
		            			SimpleDateFormat sdf;
		            			//boolean havedate = false;
		            			/*if(dates.containsKey(key)){
		            				 sdf = new SimpleDateFormat(dates.get(key)); //, dateformat2;
		            				 //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		            				 try {
										date = sdf.parse(value);
										havedate = true;
									 } catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		            			}*/
		            			if(setdates.containsKey(key)){
		            				sdf = new SimpleDateFormat(setdates.get(key)); //, dateformat2;
		            				 try {
										date = sdf.parse(value);
										if(synchlocal){
				            				localdata += "&" + URLEncoder.encode(xkey+"_utc", "UTF-8") + "=" + URLEncoder.encode(""+date.getTime(), "UTF-8");
				            				if(!local.contains(key) && !local.contains(gpskey))
				            					data += "&" + URLEncoder.encode(xkey+"_utc", "UTF-8") + "=" + URLEncoder.encode(""+date.getTime(), "UTF-8");
				            				}
				            			else //if(!local.contains(key) && !local.contains(gpskey))
				            				data += "&" + URLEncoder.encode(xkey+"_utc", "UTF-8") + "=" + URLEncoder.encode(""+date.getTime(), "UTF-8");
										//havedate = true;
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		            			}
		            			
		            			
	            		
	            			}

	            			for(String key : row.spinners.keySet()){
	            				xkey = key.replaceAll("\\s+", "_");
	            				value = row.spinners.get(key);
	            				
	            				if(primary_keys.contains(key)) //.equalsIgnoreCase(pkeyid))
	            					pkeyhash.put(key,  value); //pkey += ","+key+","+value;
	            				
	            				if(synchlocal){
	            					localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					if(!local.contains(key))
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            				}
	            				else //if(!local.contains(key))
	            					data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            			
	            			}
	            		
	            			for(String key : row.radios.keySet()){
	            				xkey = key.replaceAll("\\s+", "_");
	            				value = row.radios.get(key);
	            			
	            				if(primary_keys.contains(key)) //.equalsIgnoreCase(pkeyid))
	            					pkeyhash.put(key,  value); //pkey += ","+key+","+value;
	            				
	            				if(synchlocal){
	            					localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					if(!local.contains(key))
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            				}
	            				else //if(!local.contains(key))
	            					data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            			
	            			}
        		
	            			String[] tempstring;
	            			String tempres;
	            			int count;
	            			for(String key : checkboxgroups){
	            				xkey = key.replaceAll("\\s+", "_");
	            				tempres = "";
	            				tempstring = checkboxhash.get(key);
	            				count = 0;
	            			
	            				for(String box : tempstring){
	            					if(row.checkboxes.get(box) && count == 0){
	            						tempres = checkboxvaluesvalueshash.get(box);
	            						count++;
	            					}
	            					else if(row.checkboxes.get(box)){
	            						tempres += ","+checkboxvaluesvalueshash.get(box);
	            						count++;
	            					}
	            				}
	            				if(tempres.length() > 0){
	            					if(tempres.startsWith(","))
	            						tempres.replaceFirst(",", "");
	            				
	            					if(synchlocal){	     
	            						localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(tempres, "UTF-8");
	            						if(!local.contains(key))
	            							data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(tempres, "UTF-8");
	            					}
	            					else //if(!local.contains(key))
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(tempres, "UTF-8");
	            				}
	            			}
	            		
	            			for(String key : row.photoids.keySet()){
	            				xkey = key.replaceAll("\\s+", "_");
	            				value = row.photoids.get(key);
	            			
	            				if(value.equals("-1"))
	            					value = "";
	            				//if(!value.equals("-1")){
	            				
	            					if(synchlocal){	
	            						if(value.length() > 0)
	            							localphotostoupload.addElement(value);
	            						localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            						if(!local.contains(key)){
	            							if(value.length() > 0)
	            								photostoupload.addElement(value);
	            							data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            						}
	            					}
	            					else{ //if(!local.contains(key)){
	            						if(value.length() > 0)
	            							photostoupload.addElement(value);
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					}
	            				//}
	            			}
	            		
	            			for(String key : row.videoids.keySet()){
	            				xkey = key.replaceAll("\\s+", "_");
	            				value = row.videoids.get(key);
	            				
	            				if(value.equals("-1"))
	            					value = "";
        			
	            				if(synchlocal){
	            					localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					if(!local.contains(key)){
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					}
	            				}
	            				else{ //if(!local.contains(key)){
	            					data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            				}
	            			}
	            		
	            			for(String key : row.audioids.keySet()){
	            				xkey = key.replaceAll("\\s+", "_");
	            				value = row.audioids.get(key);
	            				
	            				if(value.equals("-1"))
	            					value = "";
        			
	            				if(synchlocal){	     
	            					localdata += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					if(!local.contains(key)){
	            						data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            					}
	            				}
	            				else{ //if(!local.contains(key)){
	            					data += "&" + URLEncoder.encode(xkey, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
	            				}
	            			}
	            		
	            			this_orphan_error = false;
	            			error = false;
	            		
	            			url = new URL(urlString); 
	            			 
	            			Log.i("SENDING", data);
	            			//conn = (HttpURLConnection)url.openConnection();
	            			               		
	                		if (url.getProtocol().toLowerCase().equals("https")) {
	            	            Epi_collect.trustAllHosts();
	            	            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	            	            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
	            	            conn = https;
	            	        } else {
	            	            conn = (HttpURLConnection) url.openConnection();
	            	        }
	                		
	            			conn.setDoOutput(true);
	            			//conn.setChunkedStreamingMode(0);
	            			wr = new OutputStreamWriter(conn.getOutputStream());
	            			wr.write(data);
	            			wr.flush();
	            			// Get the response
	            			//inStream = new DataInputStream ( conn.getInputStream() );

	            			//retstr = inStream.readLine();
	            			
	            			responsebody = "";
	            			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	               			String line;
	               			while ((line = rd.readLine()) != null) {
	               				responsebody += line;
	               				            				
	               			}
	               			Log.i("FULL SERVER RESPONSE", responsebody);  
	               			
	            			//inStream.close();
	            			
	            			//Log.i("DBAccess","Server Response Stream "+retstr);
	            			
	            			retstr = ""+conn.getResponseCode();
	            				            			
	            			if(retstr.length() == 0)
	            				retstr = "0";
	                       
	            			//Log.i("DBAccess","Server Response "+retstr+" CODE "+conn.getResponseCode()+ " MESSAGE "+conn.getResponseMessage());
	            			//Log.i("DATA SENT", data);
	            			
	            			responsecode = ""+conn.getResponseCode();
	            			responsemessage = ""+conn.getResponseMessage();
	            			
	            			//if(responsecode.startsWith("4") || responsemessage.equalsIgnoreCase("Method Not Allowed") || !responsemessage.equalsIgnoreCase("1")){
	            			//	Log.i("RESPONSE MESSAGE", responsemessage);  
	            				/*try{
		            			// Get the response
		            			InputStream inStream = new BufferedInputStream(conn.getErrorStream()); // Input

		            			//retstr = inStream.readLine();

		            			BufferedReader rd = new BufferedReader(new InputStreamReader(inStream));
		            			
		               			String line;
		               			if((line = rd.readLine()) != null) {
		               				responsemessage = line;
		               				Log.i("FULL SERVER RESPONSE", line);  
		               				
		               			}
		            			}
		            			catch(Exception e){
		            				Log.i("CONNECTION ERROR 2", e.toString());
		            			} */
	            			//}
	            			
	            			conn.disconnect();
	            			
	            			if(responsecode.startsWith("4") || !responsebody.equalsIgnoreCase("1")){
	            				server_error = true; 
	            				error = true;
	            			}
	            			if(responsecode.startsWith("5")){
	            				client_error = true; 
	            				error = true;
	            			}
	            			if(!responsecode.equalsIgnoreCase("200") || retstr.equalsIgnoreCase("0")){
	            				other_error = true; 
	            				error = true;
	            			}
	            			
	            			message = "";
	            			//Log.i("ORPHAN MESSAGE 1:", responsemessage);
	            			if(responsemessage.startsWith("Message")){
	            				Log.i("ORPHAN MESSAGE 2:", responsemessage);
	            				orphan_error = true; 
	            				this_orphan_error = true; 
	            				error = true;
	            				message = responsemessage.replaceFirst("Message\\s*:\\s+", "") + " ";
	            				if(!orphantables.contains(table))
	            					orphantables.addElement(table);
	            			}                    	        
	            			
	                    
	            			//error = false;
	            			//local_orphan_error = false; 
	            			if(synchlocal){	     
	            				//conn = (HttpURLConnection)url.openConnection();
	            					            	    		
	            	    		if (url.getProtocol().toLowerCase().equals("https")) {
	            		            Epi_collect.trustAllHosts();
	            		            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	            		            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
	            		            conn = https;
	            		        } else {
	            		            conn = (HttpURLConnection) url.openConnection();
	            		        }
	            				
	            				conn.setDoOutput(true);
	            				wr = new OutputStreamWriter(conn.getOutputStream());
	            				wr.write(data);
	            				wr.flush();
	            				// Get the response
	            				//inStream = new DataInputStream ( conn.getInputStream() );

	            				//retstr = inStream.readLine();
	            				
	            				//Log.i("DBAccess","Server Response Stream LOCAL "+retstr);
	            				
	            				retstr = ""+conn.getResponseCode();
	                        
	            				if(retstr.length() == 0)
	            					retstr = "0";
	            				
	            				//Log.i("DBAccess","Server Response LOCAL "+retstr+" CODE "+conn.getResponseCode()+ " MESSAGE "+conn.getResponseMessage());
	            			
	            				responsecode = ""+conn.getResponseCode();
	            				responsemessage = ""+conn.getResponseMessage();
	            			
	            				if(responsecode.startsWith("4")){
	            					local_server_error = true; 
	            					error = true;
	            				}
	            				if(responsecode.startsWith("5")){
	            					local_client_error = true; 
	            					error = true;
	            				}
	            				if(!responsecode.equals("200") || retstr.equalsIgnoreCase("0")){
	            					local_other_error = true;
	            					error = true;
	            				}
	            				if(responsemessage.startsWith("Message:")){
	            					local_orphan_error = true; 
	 	                 	    	this_orphan_error = true; 
	 	                 	    	error = true;
	 	                 	    	message += responsemessage.replaceFirst("Message:\\s+", "");
	 	                 	    	if(!local_orphantables.contains(table))
		            					local_orphantables.addElement(table);
	            				}
		                       	                   
	            				//inStream.close();
	            			}	
	            			
	            			// Need the hash and vector to ensure key is in correct order
	            			for(String k : primary_keys)
	            				pkey += ","+k+","+pkeyhash.get(k); //
	            			pkey = pkey.replaceFirst(",", "");
	            					
	            			if(error){
	            				if(this_orphan_error){
	            					updateRecord(table, "ecpkey", pkey, "ecstored", "F");
	            					// Ensure message is cleared
	            					updateRecord(table, "ecpkey", pkey, "message", "");
	            					if(message.length() > 0)
	            						updateRecord(table, "ecpkey", pkey, "message", message);
	            				}
	            				
	            			}
	            			else{
	            				updateRecord(table, "ecpkey", pkey, "ecstored", "Y"); 
	            				// Ensure message is cleared
            					updateRecord(table, "ecpkey", pkey, "message", "");
	            				//updateRecord(table, pkeyid, pkey, "message", "This is the message");
	            			}
	            			wr.close();
	            		
		            	}
				
            
		        } catch (SQLException e) {
		        	c.close();
		        	other_error = true;
		        	Log.e("booga1", e.toString());
		        }catch (IOException ioe) {
		        	c.close();
		        	other_error = true;
		        	//message = "This is the message";
		        	//updateRecord(table, pkeyid, pkey, "message", message);
		        	Log.e("booga2", ioe.toString());
		        }
        c.close();
		}
			
		// Release the screen lock
		wl.release();
	
			
	String result = "";
	//boolean success = true;
	
	String res = "";
	String project = getProject();

	error = false;
	if(server_error || client_error || other_error){ // !res.equalsIgnoreCase("1")
		error = true;
		result += mCtx.getResources().getString(R.string.remote_upload_failed)+".\n"; //Upload to remote server failed. ";
		//if(server_error)
		//	result += "Server error. ";
		//else if(client_error)
		//	result += "Client error. ";
		if(orphan_error){
			result += mCtx.getResources().getString(R.string.remote_entries_not_entered) + ": "; //Entries could not be entered in remote server for tables: ";
			for(String tab : orphantables)
				result += tab + " ";
			result += "\n";
		}
	}
	if(synchlocal){
		if(local_server_error || local_client_error || local_other_error){ // !res.equalsIgnoreCase("1")
			error = true;
			result += mCtx.getResources().getString(R.string.local_upload_failed)+".\n"; //"\nUpload to local server failed. ";
			if(local_orphan_error){
				result += mCtx.getResources().getString(R.string.local_entries_not_entered)+": "; //"\nEntries could not be entered in local server for tables: ";
				for(String tab : local_orphantables)
					result += tab + " ";
				result += "\n";
			}
		}
	}

	for(String key2 : photostoupload){
		if(!uploadFile(key2, false, true, true, false, false, sIMEI, email).equalsIgnoreCase("200")){ // ("1") !uploadFile(key, false, true, true, false, false, sIMEI, email)){
			error = true;
			result += mCtx.getResources().getString(R.string.remote_image_upload_failed) + ".\n"; //"\nUpload of images to remote server failed. ";
		}
	}
	
	for(String key2 : localphotostoupload){
		if(!uploadFile(key2, true, true, true, false, false, sIMEI, email).equalsIgnoreCase("200")){ // ("1") !uploadFile(key, true, true, true, false, false, sIMEI, email)){
			error = true;
			result += mCtx.getResources().getString(R.string.local_image_upload_failed) + ".\n"; //"\nUpload of images to local server failed. ";
		}
	}

	if(!error){
		//for(String table : keyshash.keySet()){
		//	updateAllRows(table);	
		//}
		return mCtx.getResources().getString(R.string.synch_success); //"Success";
	}
	else{
		return result;
	} 
	
}
	
	
	
	
	
	
	
	
	public void zipFile(String file){
		try  { 
						
		      BufferedInputStream origin = null; 
		      FileOutputStream dest = new FileOutputStream(Epi_collect.appFiles+"/"+file+".zip"); //Environment.getExternalStorageDirectory()+"/EpiCollect/"+file+".zip"); 
		 
		      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
		      	      
		      byte data[] = new byte[BUFFER]; 
		 
		      FileInputStream fi = new FileInputStream(Epi_collect.appFiles+"/"+file); //Environment.getExternalStorageDirectory()+"/EpiCollect/"+file); 
		      origin = new BufferedInputStream(fi, BUFFER); 
		      ZipEntry entry = new ZipEntry(file); 
		      out.putNextEntry(entry); 
		      int count; 
		      while ((count = origin.read(data, 0, BUFFER)) != -1) { 
		    	  out.write(data, 0, count); 
		      } 
		      origin.close(); 
		 
		      out.close(); 
		    } catch(Exception e) { 
		      e.printStackTrace(); 
		    } 
	}
	
	public String uploadAllImages(String sIMEI, String email){
		
		File dir = new File(Epi_collect.appFiles+"/"+getProject()+"/images"); //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + getProject());
		//File picfile;
	    String[] chld = dir.list();
	    String result = mCtx.getResources().getString(R.string.error)+": "; //"ERROR: ";
	    String project = getProject();
	    
	    // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    }
	    boolean success = true, overallsuccess = true, loadremote = true, loadlocal = true, synchlocal = true;
		if(getValue("synch_local_url").equalsIgnoreCase("None"))
			synchlocal = false;
		
	    int total = 0, count = 0;
	    if(chld == null || !checkFileValue(project+"_Image", "synch", "N")){
	      return mCtx.getResources().getString(R.string.no_upload_images);
	    }
	    else{
	    	total = chld.length;
	    	for(int i = 0; i < chld.length; i++){
	    		//String fileName = chld[i];
	    		if(chld[i].length() > 3){
	    			if(checkFileValue(project+"_Image", "id", chld[i], "synch", "Y")){
	    				total--;
	    				continue;
	    			}
	    			success = true;
	    			// Remote
	    			if(uploadFile(chld[i], false, true, false, false, false, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], false, true, false, false, false, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadremote = false;
	    				//result += "Upload of images to remote server failed. ";
	    			}
	    			// Local
	    			if(synchlocal && uploadFile(chld[i], true, true, false, false, false, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], true, true, false, false, false, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadlocal = false;
	    				//result += "Upload of images to local server failed. ";
	    			}
	    			if(success){
	    				updateFileSynchRow(project+"_Image", chld[i]);
	    				count++;
	    				//try{
	    				//	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + getProject()+"/"+chld[i]);
	    				//	picfile.delete();
	    	        	//	}
	    				//catch(Exception e){}
	    			}
	    		}
	    		else{
	    			total--;
	    		}
	    	}
	    }
		
	    if(total == 0)
	    	return mCtx.getResources().getString(R.string.no_upload_images); //"No images to upload";
	    else if(overallsuccess)
	    	return "Success "+mCtx.getResources().getString(R.string.upload_of)+" "+total+" "+mCtx.getResources().getString(R.string.upload_of_images); //"Upload of "+total+" images successful";
	    else{
	    	if(!loadremote)
	    		result += mCtx.getResources().getString(R.string.remote_image_upload_failed)+". "; //"Upload of images to remote server failed. ";
	    	if(!loadlocal)
	    		result += mCtx.getResources().getString(R.string.local_image_upload_failed)+". "; //"Upload of images to local server failed. ";
	    	return result;
	    }
	    //else if(count == total)
	    //	return "Upload of "+total+" images successful";
	    //else
	    //	return "Upload of images failed. " +count+" of "+ total+" images uploaded";
	}
	
	public String uploadAllVideos(String sIMEI, String email){
		
		File dir = new File(Epi_collect.appFiles+"/"+getProject()+"/videos"); //Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + getProject());
		//File picfile;
	    String[] chld = dir.list();
	    String result = mCtx.getResources().getString(R.string.error)+": "; //"ERROR: ";
	    String project = getProject();
	   
	    // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    }
	    boolean success = true, overallsuccess = true, loadremote = true, loadlocal = true, synchlocal = true;
	    if(getValue("synch_local_url").equalsIgnoreCase("None"))
			synchlocal = false;
	    
	    int total = 0, count = 0;
	    if(chld == null || !checkFileValue(project+"_Video", "synch", "N")){
	      return mCtx.getResources().getString(R.string.no_upload_videos); //"No videos to upload";
	    }
	    else{
	    	total = chld.length;
	    	for(int i = 0; i < chld.length; i++){
	    		//String fileName = chld[i];
	    		if(chld[i].length() > 3){
	    			if(checkFileValue(project+"_Video", "id", chld[i], "synch", "Y")){
	    				total--;
	    				continue;
	    			}
	    			success = true;
	    			// Remote
	    			if(uploadFile(chld[i], false, false, false, true, false, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], false, false, false, true, false, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadremote = false;
	    				//result += "Upload of videos to remote server failed. ";
	    			}
	    			// Local
	    			if(synchlocal && uploadFile(chld[i], true, false, false, true, false, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], true, false, false, true, false, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadlocal = false;
	    				//result += "Upload of videos to local server failed. ";
	    			}
	    			if(success){
	    				updateFileSynchRow(project+"_Video", chld[i]);
	    				count++;
	    				//try{
	    				//	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + getProject()+"/"+chld[i]);
	    				//	picfile.delete();
	    	        	//	}
	    				//catch(Exception e){}
	    			}
	    		}
	    		else{
	    			total--;
	    		}
	    	}
	    }
		
	    if(total == 0)
	    	return mCtx.getResources().getString(R.string.no_upload_videos); //"No videos to upload";
	    else if(overallsuccess)
	    	return "Success "+mCtx.getResources().getString(R.string.upload_of)+" "+total+" "+mCtx.getResources().getString(R.string.upload_of_videos); //"Upload of "+total+" videos successful";
	    else{
	    	if(!loadremote)
	    		result += mCtx.getResources().getString(R.string.remote_video_upload_failed)+". "; //"Upload of videos to remote server failed. ";
	    	if(!loadlocal)
	    		result += mCtx.getResources().getString(R.string.local_video_upload_failed)+". "; //"Upload of videos to local server failed. ";
	    	return result;
	    }
	    //else if(count == total)
	    //	return "Upload of "+total+" images successful";
	    //else
	    //	return "Upload of images failed. " +count+" of "+ total+" images uploaded";
	}
	
	public String uploadAllAudio(String sIMEI, String email){
		
		File dir = new File(Epi_collect.appFiles+"/"+getProject()+"/audio"); //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + getProject());
		//File picfile;
	    String[] chld = dir.list();
	    String result = "ERROR: ";
	    String project = getProject();
	    // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    }
	    
	    boolean success = true, overallsuccess = true, loadremote = true, loadlocal = true,synchlocal = true;
	    if(getValue("synch_local_url").equalsIgnoreCase("None"))
			synchlocal = false;
	    
	    int total = 0, count = 0;
	    if(chld == null || !checkFileValue(project+"_Audio", "synch", "N")){
	      return mCtx.getResources().getString(R.string.no_upload_audio); //"No audio to upload";
	    }
	    else{
	    	total = chld.length;
	    	for(int i = 0; i < chld.length; i++){
	    		//String fileName = chld[i];
	    		if(chld[i].length() > 3){
	    			if(checkFileValue(project+"_Audio", "id", chld[i], "synch", "Y")){
	    				//Log.i("IN CHECK VALUE", "TRUE"); 
	    				total--;
	    				continue; 
	    			}
	    			success = true;
	    			// Remote
	    			if(uploadFile(chld[i], false, false, false, false, true, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], false, false, false, false, true, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadremote = false;
	    				//result += "Upload of audio to remote server failed. ";
	    			}
	    			// Local
	    			if(synchlocal && uploadFile(chld[i], true, false, false, false, true, sIMEI, email).equalsIgnoreCase("0")){ //!uploadFile(chld[i], true, false, false, false, true, sIMEI, email)){
	    				success = false;
	    				overallsuccess = false;
	    				loadlocal = false;
	    				//result += "Upload of audio to local server failed. ";
	    			}
	    			if(success){
	    				updateFileSynchRow(project+"_Audio", chld[i]);
	    				count++;
	    				//try{
	    				//	picfile = new File(Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + getProject()+"/"+chld[i]);
	    				//	picfile.delete();
	    	        	//	}
	    				//catch(Exception e){}
	    			}
	    		}
	    		else{
	    			total--;
	    		}
	    	}
	    }
		
	    if(total == 0)
	    	return mCtx.getResources().getString(R.string.no_upload_audio); //"No audio to upload";
	    else if(overallsuccess)
	    	return "Success "+mCtx.getResources().getString(R.string.upload_of)+" "+count+" "+mCtx.getResources().getString(R.string.upload_of_audio); // //"Upload of "+count+" audio files successful";
	    else{
	    	if(!loadremote)
	    		result += mCtx.getResources().getString(R.string.remote_audio_upload_failed)+". "; //"Upload of audio to remote server failed. ";
	    	if(!loadlocal)
	    		result += mCtx.getResources().getString(R.string.local_audio_upload_failed) +". "; //"Upload of audio to local server failed. ";
	    	return result;
	    }
	    //else if(count == total)
	    //	return "Upload of "+total+" images successful";
	    //else
	    //	return "Upload of images failed. " +count+" of "+ total+" images uploaded";
	}

	private String uploadFile(String filename, boolean local, boolean image, boolean thumb, boolean video, boolean audio, String sIMEI, String email){ //, String thisphotoid){ 
		
		//getValues();
	
		String imagedir, urlString;
		String project = getProject();
		 // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    }
		HttpURLConnection conn = null;
	    DataOutputStream dos = null;
	    DataInputStream inStream = null;

	   String existingFileName = filename;
	   
	   if(image){
		   filename = filename+".jpg";
	   }
	   
       // Is this the place are you doing something wrong.

       String lineEnd = "\r\n";
       String twoHyphens = "--";
       String boundary =  "*****";

       int bytesRead, bytesAvailable, bufferSize;
       byte[] buffer;
       int maxBufferSize = 1*1024*1024;
       
       if(local){
    	   //imagedir = Environment.getExternalStorageDirectory()+"/EpiCollect"; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   urlString = getValue("synch_local_url");
       }
       else{
    	   //imagedir = Environment.getExternalStorageDirectory()+"/EpiCollect"; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   urlString = getValue("synch_url");
       }
       if(thumb){
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/thumbs"; //Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + project; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   //urlString = getValue("image_url")+"?";
    	   urlString += "?type=thumbnail";
       }
       else if(image){
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/images"; //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + project; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   //urlString = getValue("image_url");
    	   urlString += "?type=full_image";
       }
       else if(video){
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/videos"; //Environment.getExternalStorageDirectory()+"/EpiCollect/videodir_epicollect_" + project; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   //urlString = getValue("image_url");
    	   urlString += "?type=video";
       }
       else if(audio){
    	   imagedir = Epi_collect.appFiles+"/"+getProject()+"/audio"; //Environment.getExternalStorageDirectory()+"/EpiCollect/audiodir_epicollect_" + project; //context.getResources().getString(context.getResources().getIdentifier(context.getPackageName()+":string/project", null, null));
    	   //urlString = getValue("image_url");
    	   urlString += "?type=audio";
       }
       else{
    	   imagedir = Epi_collect.appFiles.toString(); //Environment.getExternalStorageDirectory()+"/EpiCollect";
    	   urlString += "?type=data";
       }
       
       urlString += "&phoneid="+sIMEI;
       urlString += "&email="+email;
             
       Log.i("DATA UPLOAD", urlString);
       
       //Log.i("ServerValues", "synch "+getValue("synch_url")+ " synch_local "+getValue("synch_local_url"));
           	   
       //image_url = getValue("image_url"); // context.getResources().getString(context.getResources().getIdentifier(this.getClass().getPackage().getName()+":string/image_url", null, null));
       
       //String responseFromServer = "";

       
       try{
        //------------------ CLIENT REQUEST
	     
	       Log.i("DBAccess","Inside second Method");
	       
	
	       FileInputStream fileInputStream;
	       
	       fileInputStream = new FileInputStream(new File(imagedir+"/" + existingFileName));
	       //InputStreamReader isr = new InputStreamReader(fileInputStream, "UTF8");

	
	        // open a URL connection to the Servlet
	
	        URL url = new URL(urlString);
	
	        // Open a HTTP connection to the URL
	
	        conn = (HttpURLConnection) url.openConnection();
	           		
    		if (url.getProtocol().toLowerCase().equals("https")) {
	            Epi_collect.trustAllHosts();
	            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
	            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
	            conn = https;
	        } else {
	            conn = (HttpURLConnection) url.openConnection();
	        }
	
	        // Allow Inputs
	        conn.setDoInput(true);
	        // Allow Outputs
	        conn.setDoOutput(true);
	
	        // Don't use a cached copy.
	        conn.setUseCaches(false);
	
	        // Use a post method.
	        conn.setRequestMethod("POST");
	
	        conn.setRequestProperty("Connection", "Keep-Alive");
		     
	        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	        dos = new DataOutputStream( conn.getOutputStream() );
	
	        dos.writeBytes(twoHyphens + boundary + lineEnd);
	        //dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName +"\"" + lineEnd); // existingFileName
	        //dos.writeBytes("Content-Disposition: form-data; name=\""+existingFileName+"\";filename=\"" + existingFileName +"\"" + lineEnd); // uploadedfile
	        
	        if(image)
	        	dos.writeBytes("Content-Disposition: form-data; name=\""+filename+"\";filename=\"" + existingFileName +"\"" + lineEnd);
	        else // if(thumb || video || audio)
	        	dos.writeBytes("Content-Disposition: form-data; name=\"name\";filename=\"" + existingFileName +"\"" + lineEnd);
	        //else
	        //	dos.writeUTF("Content-Disposition: form-data; name=\"name\";filename=\"" + existingFileName +"\"" + lineEnd);
	        
	        //Log.i("UPLOAD CHECK XX ","Content-Disposition: form-data; name=\"name\";filename=\"" + existingFileName +"\"" + lineEnd); //\""+existingFileName+"\"
	        //Log.i("FILE LOC", imagedir+"/" + existingFileName);
	        
	        dos.writeBytes(lineEnd);
	
	        //Log.e("DBAccess","Headers are written");
	
	        // create a buffer of maximum size
	
	        bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        buffer = new byte[bufferSize];
	
	        // read file and write it into form...
	
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
	        //Log.e("DBAccess","TO HERE");
	        while (bytesRead > 0){
		         dos.write(buffer, 0, bufferSize);
		         bytesAvailable = fileInputStream.available();
		         bufferSize = Math.min(bytesAvailable, maxBufferSize);
		         bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	        }
	        //Log.e("DBAccess","TO HERE 2");
	        // send multipart form data necesssary after file data...
	
	        dos.writeBytes(lineEnd);
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	        // close streams
	        Log.i("DBAccess","File is written ");
	        fileInputStream.close();
	        dos.flush();
	        dos.close();

	        Log.i("FILE UPLOAD","FILE UPLOADED");

       }
       catch (MalformedURLException ex){
            Log.e("DBAccess", "error 1: " + ex.getMessage(), ex);
            return "0"; //false;
       }

       catch (IOException ioe){
	        Log.e("DBAccess", "error 2: " + ioe.getMessage(), ioe);
	        return "0"; //false;
	   }
       
       catch (Exception e){
	        Log.e("DBAccess", "error 2: " + e);
	        return "0"; //false;
	   }

       //------------------ read the SERVER RESPONSE

       String retstr = "0";
       try {
             inStream = new DataInputStream ( conn.getInputStream() );

             retstr = inStream.readLine();
             //if(retstr == null)
            //	 retstr = "0";
            // else
            	 retstr = ""+conn.getResponseCode();
             
             if(retstr.length() == 0)
            	 retstr = "0";
            
             Log.i("DBAccess","Server Response "+retstr+" CODE "+conn.getResponseCode()+ " MESSAGE "+conn.getResponseMessage());
            
            String responsecode = ""+conn.getResponseCode(), responsemessage = ""+conn.getResponseMessage();
            if(responsecode.startsWith("4")){
            	return "Message: Server Error"; 
            }
            if(responsecode.startsWith("5")){
            	return "Message: Client Error"; 
            }
            if(responsemessage.startsWith("Message:")){
            	return responsemessage;
            }
            if(!responsecode.equals("200") || retstr.equalsIgnoreCase("0")){
               	return "0"; //false;
            	}
            	                   
             //}
             inStream.close();
	       }
       catch (IOException ioex){
            Log.e("DBAccess", "Response error 1: " + ioex.getMessage(), ioex);
            return "0"; //false;
       }
       catch (Exception ex){
           Log.e("DBAccess", "Response error 2: " + ex.getMessage(), ex);
           return "0"; //false;
      }

       return retstr; //true;
     } 
	
	//@SuppressLint("NewApi")
	public boolean backupProject(String sIMEI, boolean deleteSynch){
		
		//List<Row> rows;
		String value="";
		String project = getProject();
		
		// First a list of the tables
		// Only need the keys from the hash
		LinkedHashMap<String, String> keyshash = getAllKeys(1);
		 // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    }
		String dir = Epi_collect.appFiles+"/"+project+"/"; //Environment.getExternalStorageDirectory()+"/EpiCollect/";
		
		String textfile, xmlfile;
		
		if(deleteSynch){
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			textfile = dir+sIMEI+"_"+cal.getTimeInMillis()+"_data_backup.txt";
			xmlfile = dir+sIMEI+"_"+cal.getTimeInMillis()+"_data_backup.xml";
			}
		else{
			
			textfile = dir+sIMEI+"_data_backup.txt";
			xmlfile = dir+sIMEI+"_data_backup.xml";
		}
		
 	   	
		File f;
       	try{
       		f = new File(textfile);
       		
			if(f.exists()){
				f.delete();
			}
    	}
		catch(Exception e){}
		
		try{
			 f = new File(xmlfile);
			if(f.exists()){
				f.delete();
			}
    	}
		catch(Exception e){}
	
		try{
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(xmlfile),"UTF-8");
			OutputStreamWriter ostxt = new OutputStreamWriter(new FileOutputStream(textfile),"UTF-8");
			os.write("<entries>");
	
			for(String table : keyshash.keySet()){
				os.write("\n\t<table>");
				
				os.write("\n\t\t<table_name>"+table+"</table_name>");
					
				getActiveProject();
		    	getValues(table);
		    	//getDates(table);
		    	
		    	Cursor c = db.rawQuery("select * from "+DATABASE_TABLE+"_"+table, null);
		    	//Cursor c = db.query(DATABASE_TABLE, new String[] {"*"}, null, null, null, null, null);
		    	
		    	Row row;

		        try {
		            int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	row = getRow(c);				
		            	
		            	c.moveToNext();
		            	
				//rows = fetchAllRows(table, true);
				
				String xkey;
				//for (Row row : rows) {
	            	
	            	os.write("\n\t\t<entry>");
	            	os.write("\n\t\t\t<ecTimeCreated>"+row.date+"</ecTimeCreated>");
	            	ostxt.write(table);
	            	ostxt.write("\tecTimeCreated\t"+row.date);
	            	os.write("\n\t\t\t<ecPhonekey>"+row.ecphonekey+"</ecPhonekey>");
	            	ostxt.write("\tecPhonekey\t"+row.ecphonekey);
	            	for(String key : row.datastrings.keySet()){
	            		xkey = key.replaceAll("\\s+", "_");
	            			            		
	            		if(row.datastrings.get(key) == null || row.datastrings.get(key).equalsIgnoreCase("")){
	            			if(doubles.contains(key))
	            				value = "";
	            			else if(integers.contains(key))
	            				value = "";
	            			else
	            				value = ""; //"N/A";
	            			
	            			
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            			}
	            		else{
	            			value = row.datastrings.get(key);
	            			value = value.replaceAll("&", "&amp;");
	            			//if(!local.contains(key))
	            				os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            				ostxt.write("\t"+xkey+"\t"+value);
	            			}

	            		
	            		}
        		
	            		for(String key : row.spinners.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	           				value = row.spinners.get(key);
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            		}
	            		
	            		for(String key : row.radios.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
            				value = row.radios.get(key);
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            		}
        		
	            		String[] tempstring;
	            		String tempres;
	            		int count;
	            		for(String key : checkboxgroups){
	            			xkey = key.replaceAll("\\s+", "_");
	            			tempres = "";
	            			tempstring = checkboxhash.get(key);
	            			count = 0;
	            			for(String box : tempstring){
	            				if(row.checkboxes.get(box) && count == 0){
	            					tempres = checkboxvaluesvalueshash.get(box);
	            					count++;
	            				}
	            				else if(row.checkboxes.get(box)){
	            					tempres += ","+checkboxvaluesvalueshash.get(box);
	            					count++;
	            				}
	            			}
	            			if(tempres.length() > 0){
	            				if(tempres.startsWith(","))
	            					tempres.replaceFirst(",", "");
	            				
	            				os.write("\n\t\t\t<"+xkey+">"+tempres+"</"+xkey+">");
	            				ostxt.write("\t"+xkey+"\t"+tempres);
	            			}
	            		}
	            		
	            		for(String key : row.photoids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.photoids.get(key);
        			
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            		}
	            		
	            		for(String key : row.videoids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.videoids.get(key);
        			
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            		}
	            		
	            		for(String key : row.audioids.keySet()){
	            			xkey = key.replaceAll("\\s+", "_");
	            			value = row.audioids.get(key);
        			
	            			os.write("\n\t\t\t<"+xkey+">"+value+"</"+xkey+">");
	            			ostxt.write("\t"+xkey+"\t"+value);
	            		}
	            		
	            		if(deleteSynch)
	            			updateRecord(table, "ecpkey", row.ecpkey, "ecstored", "Y");
	            		else
	            			ostxt.write("\tecStored"+"\t"+row.stored);	            		
	            		os.write("\n\t\t</entry>");
	            		ostxt.write("\n");
	            		
					}
		        } catch (SQLException e) {
		        	c.close();
		        	
		            Log.e("booga", e.toString());
		        //}
		        c.close();
				}
				os.write("\n\t</table>");
			}
		
		os.write("\n</entries>\n");
		os.close();
		ostxt.close();
		
		MediaScannerConnection.scanFile(thiscontext, new String[] {os.toString()}, null, null);
		MediaScannerConnection.scanFile(thiscontext, new String[] {ostxt.toString()}, null, null);

		}
	catch(IOException e){
		// If there is a problem ensure all synch rows are reset
		if(deleteSynch)
    		for(String key : getKeys().keySet()){
      	    	updateResetAllRows(key);
      	    }
		return false;
		}	
	
	if(!deleteSynch)
		zipFile(xmlfile); //project+"_"+sIMEI+"_data_backup.xml");
	else{
		// Delete all synched rows
		for(String table : keyshash.keySet()){
			deleteAllSynchRows(table, "None");
		}
	}
	
	return true;
	}


	
	
	//boolean hasimages = false;
	public int getRemoteData(String project, String table, String entry_selected_table, String entry, boolean local, boolean getimages, boolean group, String sIMEI, String email){ //, String lat, String lon){
		
		int phonekey = 0, result = 2;
		String line;
		String tablename = "";
		String oldtable = "", id, value, title = "", primary_key = "", foreign_key = "";
		BufferedReader br;
		Vector<String> items;
		Vector<String> positionpos = null;
		Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
		InsertHelper ih = null;
		    // fix sIMEI for devices on which it is not set
	    
	    if (sIMEI==null ){
	    	sIMEI="null";
	    } 		
    	URL url = null;
    		
    	String url_string;
    		
    	if(local)
    		url_string = getValue("local_remote_xml"); 
    	else
    		url_string = getValue("remote_xml");
    		    		 		
    	if(getimages){
    		url_string += "?project="+project+"&type=thumbnail"+"&table="+table+"&entry="+entry;
    	}
    	// If this comes from a table entry then "table" is the table the entry is from and entry_selected_table is the base table 
    	// from down to where entries should be retrieved
    	//http://test.mlst.net/SCORE/downloadFromServer.php?project=SCORE_FULL&type=data&select_table=Location&entry=Ke053&table=Village
    	// will download all of the data down to the location data for village Ke053. That means select_table is now the target table and has the same function as table in the first url.
    	else{
    		if(entry.length() > 0)
    			url_string += "?project="+project+"&type=data"+"&table="+entry_selected_table+"&entry="+entry+"&select_table="+table+"&xml=false";
    		else if(!group)
    			url_string += "?project="+project+"&type=data"+"&table="+table+"&xml=false";
    		else
    			url_string += "?project="+project+"&type=group"+"&table="+table+"&xml=false";
    		//Log.i("DATA DOWNLOAD ",url_string);
    	}
    	    	
    	url_string += "&phoneid="+sIMEI;
    	url_string += "&email="+email;
    	
    	//if(project.equalsIgnoreCase("Zambia")){
    	//	url_string = "http://test.mlst.net/epicollectplusbeta/Zambia/download?table=Country&select_table=Ward&xml=false";
    	//	}
    	
    	// Keep the sceen active or db connection lost
       	PowerManager pm = (PowerManager) thiscontext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); // | PowerManager.ON_AFTER_RELEASE, "My Tag");
        wl.acquire();
        
    	Log.i("GET REMOTE DATA URL", url_string.toString());
            		
    	try {
			url = new URL(url_string);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return 0;
		}
    	
    	if(getimages){
    		
    		//if(loadImageFile(url_string, Epi_collect.appFiles+"/temp.zip") == 0)
    		if(loadImageFile(url_string, Epi_collect.appFiles+"/"+getProject()+"/thumbs") == 0)
    			return 0;
    		// Get the image zip file and uncompress it to the thumbs directory
    	/*	final int BUFFER = 2048;
    		try {
    			
    			try {
    				URL zurl = new URL(url_string);
    				URLConnection conn; 
    				
    				if (zurl.getProtocol().toLowerCase().equals("https")) {
        	            Epi_collect.trustAllHosts();
        	            HttpsURLConnection https = (HttpsURLConnection) zurl.openConnection();
        	            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
        	            conn = https;
        	        } else {
        	            conn = (HttpURLConnection) zurl.openConnection();
        	        }
    				
    				//URLConnection conn = zurl.openConnection();
   			        conn.setDoOutput(true);
   			        conn.setDoInput(true);
   			        conn.setRequestProperty("content-type", "binary/data");
   			        InputStream in = conn.getInputStream();
   			        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");
   			        byte[] b = new byte[1024];
   			        int count;
   			        while ((count = in.read(b)) >= 0) {
   			            out.write(b, 0, count);
   			        }
   			        out.close();
   			        in.close();
    			    } catch (IOException e) {
   			        e.printStackTrace();
   			    }
   			    FileOutputStream fos;
   			    byte data[] = new byte[1024];
   				
   				String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject();
   				try{
   			       	File f = new File(picdir);
   			    	if(!f.exists()){
   			    		f.mkdir();
   			    	}
   				}
       			catch(Exception e){
   			    	//Log.i("ImageSwitcher THUMBS ERROR", thumbdir);
   			    	
   			    }   				
   				
       			BufferedOutputStream dest = null;
       	         BufferedInputStream is = null;
       	         ZipEntry zentry;
       	         ZipFile zipfile = new ZipFile(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");
       	         Enumeration<?> e = zipfile.entries();
       	         while(e.hasMoreElements()) {
       	            zentry = (ZipEntry) e.nextElement();
       	            //System.out.println("Extracting: " +zentry);
       	            is = new BufferedInputStream
       	              (zipfile.getInputStream(zentry));
       	            int count;
       	            byte data2[] = new byte[BUFFER];
       	            String fullpath = zentry.getName();
   		            String filename = fullpath.replaceAll(".*[/\\\\]","");
   		            filename = filename.replaceFirst("tn_","");
   		            //fos = new FileOutputStream(picdir+"/"+filename);
       	            fos = new FileOutputStream(picdir+"/"+filename);
       	            dest = new BufferedOutputStream(fos, BUFFER);
       	            while ((count = is.read(data2, 0, BUFFER)) != -1) {
       	               dest.write(data2, 0, count);
       	            }
       	            dest.flush();
       	            dest.close();
       	            is.close();
       	         }
       			
   		      } catch(Exception e) {
   		         e.printStackTrace();
   		         return 0;
   		      } */
    		wl.release();
   		    return 2;
   		}
   		
   		else{
   			try{
   				HttpURLConnection urlc;
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
   				InputStream stream = urlc.getInputStream();
    			
   				br = new BufferedReader(new InputStreamReader(stream));
   				//FileOutputStream out = new FileOutputStream(Epi_collect.appFiles+"/data_download.txt");
   				
   				BufferedWriter out = new BufferedWriter(new OutputStreamWriter
   					  (new FileOutputStream(Epi_collect.appFiles+"/data_download.txt"),"UTF-8"));
   				try{
   					int letter;

   				    while ((letter = stream.read()) != -1) {
   				      out.write((char) letter);
   				      out.flush();
   				    }
   					//byte buf[]=new byte[1024];
   					//int len;
   					//while((len=stream.read(buf))>0)
   					//	out.write(buf,0,len);
   					//out.close();
   					stream.close();
   				}
   				catch (IOException e){}
   				}
   			catch (IOException e){}
   			
   			result = loadFile(Epi_collect.appFiles+"/data_download.txt");
   			
   		}
   		wl.release();
    	
    	return result;
	}
	
	public int loadImageFile(String url_string, String image_dir){
		
		//PowerManager pm = (PowerManager) thiscontext.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag"); // | PowerManager.ON_AFTER_RELEASE, "My Tag");
        //wl.acquire();
		
		final int BUFFER = 2048;
		try {
			
			try {
				URL zurl = new URL(url_string);
				URLConnection conn; 
				
				if (zurl.getProtocol().toLowerCase().equals("https")) {
    	            Epi_collect.trustAllHosts();
    	            HttpsURLConnection https = (HttpsURLConnection) zurl.openConnection();
    	            https.setHostnameVerifier(Epi_collect.DO_NOT_VERIFY);
    	            conn = https;
    	        } else {
    	            conn = (HttpURLConnection) zurl.openConnection();
    	        }
				
				//URLConnection conn = zurl.openConnection();
			        conn.setDoOutput(true);
			        conn.setDoInput(true);
			        conn.setRequestProperty("content-type", "binary/data");
			        InputStream in = conn.getInputStream();
			        FileOutputStream out = new FileOutputStream(Epi_collect.appFiles+"/temp.zip");
			        byte[] b = new byte[1024];
			        int count;
			        while ((count = in.read(b)) >= 0) {
			            out.write(b, 0, count);
			        }
			        out.close();
			        in.close();
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    FileOutputStream fos;
			    //byte data[] = new byte[1024];
				
			    Log.i("DB DIR", image_dir);
				//String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject();
				try{
			       	File f = new File(image_dir); //picdir);
			    	if(!f.exists()){
			    		f.mkdir();
			    	}
				}
   			catch(Exception e){
			    	Log.i("THUMBS ERROR", image_dir);
			    	
			    }   				
				
   			BufferedOutputStream dest = null;
   	         BufferedInputStream is = null;
   	         ZipEntry zentry;
   	         ZipFile zipfile = new ZipFile(Epi_collect.appFiles+"/temp.zip");
   	         Enumeration<?> e = zipfile.entries();
   	         while(e.hasMoreElements()) {
   	            zentry = (ZipEntry) e.nextElement();
   	            //System.out.println("Extracting: " +zentry);
   	            is = new BufferedInputStream
   	              (zipfile.getInputStream(zentry));
   	            int count;
   	            byte data2[] = new byte[BUFFER];
   	            String fullpath = zentry.getName();
		            String filename = fullpath.replaceAll(".*[/\\\\]","");
		            filename = filename.replaceFirst("tn_","");
		            //fos = new FileOutputStream(picdir+"/"+filename);
   	            fos = new FileOutputStream(image_dir+"/"+filename);
   	            dest = new BufferedOutputStream(fos, BUFFER);
   	            while ((count = is.read(data2, 0, BUFFER)) != -1) {
   	               dest.write(data2, 0, count);
   	            }
   	            dest.flush();
   	            dest.close();
   	            is.close();
   	         }
   			
		      } catch(Exception e) {
		         e.printStackTrace();
		        // wl.release();
		         return 0;
		      }
		      
		// wl.release();
		 return 1;
	}
	
	public int loadFile(String filename){
		
		int phonekey = -1, result = 2;
		String line;
		String tablename = "";
		String oldtable = "", id, value, title = "", primary_key = "", foreign_key = "";
		BufferedReader br;
		Vector<String> items;
		Vector<String> positionpos = null;
		Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
		InsertHelper ih = null;
		String storevalueset = "R";
		
		int count = 0;
		
		try{				
			
				LinkedHashMap<String, String> keyshash = getAllKeys(0);
				
				int keycount = 1;
				orderedtables = new HashMap<Integer, String>();
				orderedtablesrev = new HashMap<String, Integer>();
				for(String key : keyshash.keySet()){
					orderedtables.put(keycount, key);
					orderedtablesrev.put(key, keycount);
					keycount++;
				} 
    
   		  	
				//br = new BufferedReader(new FileReader(filename));
				//br = new BufferedReader(new FileReader(new File(filename)));
				
				br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			   // BufferedReader input =  new BufferedReader(new FileReader(new File(System.getProperty("java.library.path")+"\\"+_file)));
			    //String curline = new String(input.readLine().getBytes(),"UTF-8");
			    				
				//br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8")); 
				//int count = 0;
				//String s;
				//while((line = br.readLine()) != null){
				while((line = br.readLine()) != null){
					//count++;
					//if(count%100 == 0)
					//	Log.i("COUNT", ""+count);
					
					//line = new String(s.getBytes(),"UTF-8");
					Log.i("LINE", line);
					title = "";
					primary_key = "";
					foreign_key = "";
					items = new Vector<String>(Arrays.asList(line.split("\t")));
					tablename = items.elementAt(0);
					//Log.i("TABLE", tablename);
					if(!tablename.equalsIgnoreCase(oldtable)){
 						
						phonekey = -1;			
						try{
							ih.close();
						}
						catch(Exception e){}
			
						ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
						positionpos = getRowPositions(tablename);
						getValues(tablename);
						getValues2(tablename);
						oldtable = tablename;
						primary_keys.clear();
						if(getKeyValue(tablename) != null && getValue(tablename, "ecpkey").length() > 0){
							for(String key : (getValue(tablename, "ecpkey")).split(";")){
								primary_keys.addElement(key);
							}
						}
		
						foreign_keys.clear();
						// Need to check for null as branch and group tables wil not be in this hash
						if(orderedtablesrev.get(tablename)!= null && orderedtablesrev.get(tablename) > 1){
							String ftablename = orderedtables.get(orderedtablesrev.get(tablename) - 1);
							
							if(getKeyValue(ftablename) != null && getValue(ftablename, "ecpkey").length() > 0){
								for(String key : (getValue(ftablename, "ecpkey")).split(";")){
									foreign_keys.addElement(key);
								}
							}
						}
					}
		
					ih.prepareForReplace(); //prepareForInsert(); //
					
					storevalueset = "R";
					
					// Set default values for checkboxes
					for(String key : checkboxes){
						ih.bind(positionpos.indexOf(key), "0");
			        }
  		 	
					// Preset the spinner values in case nothing is returned
					for(String key : spinners2){ 
						ih.bind(positionpos.indexOf(key), "Select");
					}
					
					for(int i = 1; i < items.size(); i+=2){
						id = items.elementAt(i);
						if(i+1 >= items.size())
							continue;
						value = items.elementAt(i+1);
						
						//if(value.length() == 0)
						//	continue;
  		 			 		
						if(id.equalsIgnoreCase("ecPhonekey")){
							try{
								phonekey = Integer.valueOf(id);
							}
							catch(NumberFormatException nfe){}
						}
						
						if(id.equalsIgnoreCase("ecStored")){
							ih.bind(positionpos.indexOf("ecstored"), value);
							storevalueset = value;
						}
						
						if(id.equalsIgnoreCase("ecTimeCreated")){
							ih.bind(positionpos.indexOf("ecdate"), value);
						}
								
						if(textviews2.contains(id)){
							ih.bind(positionpos.indexOf(id), value);
							if(value.length() > 0 && listfields != null && listfields.contains(id)){
								title += "- "+ value + " ";
							}
							if(primary_keys.contains(id)){
								primary_key += ","+id+","+value;
							}
							if(foreign_keys.contains(id)){
								foreign_key += ","+id+","+value;
							}
						}
       	  
  		 		
						for(String key : gpstags){
							if(id.equalsIgnoreCase(key+"_lat")){
								ih.bind(positionpos.indexOf(key+"_lat"), value);
								break;
							}
							else if(id.equalsIgnoreCase(key+"_lon")){
								ih.bind(positionpos.indexOf(key+"_lon"), value);
								break;
							}
							else if(id.equalsIgnoreCase(key+"_alt")){
								ih.bind(positionpos.indexOf(key+"_alt"), value);
								break;
							}
							else if(id.equalsIgnoreCase(key+"_acc")){
								ih.bind(positionpos.indexOf(key+"_acc"), value);
								break;
							}
							else if(id.equalsIgnoreCase(key+"_bearing")){
								ih.bind(positionpos.indexOf(key+"_bearing"), value);
								break;
							}
							else if(id.equalsIgnoreCase(key+"_provider")){
								ih.bind(positionpos.indexOf(key+"_provider"), value);
								break;
							}
							
						} 
        	  
						if(photos2.contains(id)){
							ih.bind(positionpos.indexOf(id), value);
							
						// If there is an image it needs to be downloaded
			  				if(value.length() > 2)
			  					result = 1;
						}
       	  
						if(videos2.contains(id)){
							ih.bind(positionpos.indexOf(id), value);
						}
       	  
						if(audio2.contains(id)){
							ih.bind(positionpos.indexOf(id), value);
						}
						
						String[] tempstring, tempstring2;
						if(spinners2.contains(id)){
							//ih.bind(positionpos.indexOf(id), "Select"); 
							//Log.i("SPINNER SET", id+" "+value+" ");
							//value = value.replaceAll("\\s", "");
							//if(value != null && value.length() > 0)
								ih.bind(positionpos.indexOf(id), value); 
							if(listspinners != null && listspinners.contains(id))
								title += "- "+ value + " ";
							if(primary_keys.contains(id)){
								primary_key += ","+id+","+value;
							}
							if(foreign_keys.contains(id)){
								foreign_key += ","+id+","+value;
							}
						}
       	  
						if(radios2.contains(id)){
							ih.bind(positionpos.indexOf(id), ""); 
							ih.bind(positionpos.indexOf(id), value);
							if(listradios != null && listradios.contains(id))
								title += "- "+ value + " ";
						}
       	  
						//Log.i("HERE 3", "STARTING CB");
						if(checkboxgroups2.contains(id)){
							//Log.i("HERE 1", "ID "+id);
							//for(String key : checkboxes){
								try{
									tempstring = checkboxhash.get(id); //key);
									for(String box : tempstring){
										//Log.i("HERE 2", "BOX "+box);
										ih.bind(positionpos.indexOf(box), "0");
									}
									for(String box : tempstring){
										tempstring2 = value.split(",");
										for(String box2 : tempstring2){
											if(box2.equalsIgnoreCase(checkboxvaluesvalueshash.get(box))){
												ih.bind(positionpos.indexOf(box), "1");
											}
										}
									}
								}
								catch(NullPointerException npe){
									Log.i(getClass().getSimpleName(), "PARSE DOWNLOAD FILE ERROR 1: "+ npe.toString());
								}
							//}  
						}
					}
					
					if(storevalueset.equalsIgnoreCase("R")){
						ih.bind(positionpos.indexOf("ecremote"), ""+1);
						ih.bind(positionpos.indexOf("ecstored"), "R");
					}
					else{
						ih.bind(positionpos.indexOf("ecremote"), ""+0);
						ih.bind(positionpos.indexOf("ecstored"), storevalueset);	
					}
 		 
					if(phonekey == -1){
						phonekey = getMaxPhonekeyValue(tablename);
					}
					
					phonekey++;
					
					ih.bind(positionpos.indexOf("ecphonekey"), ""+phonekey);
					if(getValue(tablename, "genkey") != null && getValue(tablename, "genkey").length() > 0){
						//title = "- " + phonekey + " "+ title; 
					}
	    	
					title = title.replaceFirst("- ", "");
					ih.bind(positionpos.indexOf("ectitle"), title);
   	    
					primary_key = primary_key.replaceFirst(",", "");
					ih.bind(positionpos.indexOf("ecpkey"), primary_key);
   	
					foreign_key = foreign_key.replaceFirst(",", "");
					ih.bind(positionpos.indexOf("ecfkey"), foreign_key);
					
					//deleteRow(tablename, "ecpkey", primary_key);
					
					ih.execute();	
					
					count++;
					if(count % 100 == 0){
						Log.i("Count:", ""+count);
					}
				}
				br.close();
	
				try{
					ih.close();
				}
				catch(Exception e){}
			}
			catch(Exception e){
				e.printStackTrace();
				return 0;
			}
	
		return result;
	}
   	 
	
	// NEED TO UPDATE THIS FOR THE INDIVIDUAL TABLES
	
	/*String xmlproject;
	int phonekey;
	HashMap<String, Vector<HashMap<String, String>>> allrows = new HashMap<String, Vector<HashMap<String, String>>>();
	InsertHelper ih;
	public int getRemoteData(String project, String table, String entry_selected_table, String entry, boolean local, boolean getimages, String sIMEI, String email){ //, String lat, String lon){  // ArrayList<Row>
		
		// Using these vectors doesn't increase speed at all
    	
		xmlproject = project;
		hasimages = false;
		//boolean result = false;
		
		InputStream xml_stream = null;
		
		LinkedHashMap<String, String> keyshash = getAllKeys(0);
		
		int keycount = 1;
		orderedtables = new HashMap<Integer, String>();
    	orderedtablesrev = new HashMap<String, Integer>();
        for(String key : keyshash.keySet()){
	    	orderedtables.put(keycount, key);
	    	orderedtablesrev.put(key, keycount);
	    	keycount++;
	    } 
		
		//String tableonly = "false";
		//if(gettableonly == true){
		//	tableonly = "true";
		//}
		
		//Log.i("GET REMOTE DATA URL", "http://www.doc.ic.ac.uk/~dmh1/Android/test_remote.xml?project="+project+"&table="+table);
    	try{
    		
    		URL url;
    		
    		String url_string;
    		
    		if(local)
    			url_string = getValue("local_remote_xml"); 
    		else
    			url_string = getValue("remote_xml");
    		    		 		
    		if(getimages){
    			url_string += "?project="+project+"&type=thumbnail"+"&table="+table+"&entry="+entry;
    		}
    		// If this comes from a table entry then "table" is the table the entry is from and entry_selected_table is the base table 
    		// from down to where entries should be retrieved
    		//http://test.mlst.net/SCORE/downloadFromServer.php?project=SCORE_FULL&type=data&select_table=Location&entry=Ke053&table=Village
    		// will download all of the data down to the location data for village Ke053. That means select_table is now the target table and has the same function as table in the first url.
    		else{
    			if(entry.length() > 0)
    				url_string += "?project="+project+"&type=data"+"&table="+entry_selected_table+"&entry="+entry+"&select_table="+table;
    			else
    				url_string += "?project="+project+"&type=data"+"&table="+table;
    			//Log.i("DATA DOWNLOAD ",url_string);
    		}
    		    	
    		url_string += "&phoneid="+sIMEI;
    		url_string += "&email="+email;
    		
    		Log.i("GET REMOTE DATA URL", url_string.toString());
        	    		
    		url = new URL(url_string);
    		
    		if(getimages){
    			
    			// Get the image zip file and uncompress it to the thumbs directory
    			final int BUFFER = 2048;
    			try {
    				
    				try {
    					URL zurl = new URL(url_string);
    			        URLConnection conn = zurl.openConnection();
    			        conn.setDoOutput(true);
    			        conn.setDoInput(true);
    			        conn.setRequestProperty("content-type", "binary/data");
    			        InputStream in = conn.getInputStream();
    			        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");

    			        byte[] b = new byte[1024];
    			        int count;

    			        while ((count = in.read(b)) >= 0) {
    			            out.write(b, 0, count);
    			        }
    			        out.close();
    			        in.close();

    			    } catch (IOException e) {
    			        e.printStackTrace();
    			    }

    			    FileOutputStream fos;
    			    byte data[] = new byte[1024];
    				
    				String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject();
    				try{
    			       	File f = new File(picdir);
    			    	if(!f.exists()){
    			    		f.mkdir();
    			    	}
    				}
        			catch(Exception e){
    			    	//Log.i("ImageSwitcher THUMBS ERROR", thumbdir);
    			    	
    			    }   				
    				
        			BufferedOutputStream dest = null;
        	         BufferedInputStream is = null;
        	         ZipEntry zentry;
        	         ZipFile zipfile = new ZipFile(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");
        	         Enumeration<?> e = zipfile.entries();
        	         while(e.hasMoreElements()) {
        	            zentry = (ZipEntry) e.nextElement();
        	            //System.out.println("Extracting: " +zentry);
        	            is = new BufferedInputStream
        	              (zipfile.getInputStream(zentry));
        	            int count;
        	            byte data2[] = new byte[BUFFER];
        	            String fullpath = zentry.getName();
     		            String filename = fullpath.replaceAll(".*[/\\\\]","");
     		            filename = filename.replaceFirst("tn_","");
    		            //fos = new FileOutputStream(picdir+"/"+filename);
        	            fos = new FileOutputStream(picdir+"/"+filename);
        	            dest = new BufferedOutputStream(fos, BUFFER);
        	            while ((count = is.read(data2, 0, BUFFER)) != -1) {
        	               dest.write(data2, 0, count);
        	            }
        	            dest.flush();
        	            dest.close();
        	            is.close();
        	         }

    		      } catch(Exception e) {
    		         e.printStackTrace();
    		         return 0;
    		      }
    		      return 2;
    		}
    		
    		else{
    			HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
    			urlc.setRequestMethod("GET");
    			urlc.connect();

    			xml_stream = urlc.getInputStream();
    			//if(xmlproject.equalsIgnoreCase("SCORE_FULL"))
    			//	xml_stream = fixXMLTags(xml_stream);
    			
    			BufferedWriter os = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			BufferedReader br = new BufferedReader(new InputStreamReader(xml_stream));
    			String line = null;
    			int counttable = 0, countentry = 0;
    			
    			int count = 0;
    			while ((line = br.readLine()) != null) {
    				line = line.replaceFirst("<9", "<XNINEX");
    				line = line.replaceFirst("</9", "</XNINEX");
    				if(line.contains("<table>"))
    					counttable++;
    				if(line.contains("<entry>"))
    					countentry++;
    				os.write(line);
    				os.write("\n");
    				//Log.i("XML", line+"\n");
    				count++;
    				if(countentry %100 == 0)
    					Log.i("XML", ""+countentry);
    				if(counttable > 1 && countentry > 10000){
    					br.close();
    	    			os.close();
        				return 3;
    				}
    			}
    			br.close();
    			os.close();
    			
    			try{
    				xml_stream = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			}
    			catch(Exception e){
    				Log.i("XML ENTRY CHECK", e.toString());
    			}
    			
    			if(counttable > 1 && countentry > 10000)
    				return 3;
    			
    		}
    		
    	}
    	catch(MalformedURLException  ex){
    		System.out.println("1 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 1 "+ex.toString());
    		return 0;
    		}
    	catch (IOException ex) {
    		System.out.println("2 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 2 "+ex.toString());
    		return 0;
    		}
    	
    	try {
    		
    		// Clear all remote and synchronised data first to ensure synchronisation with remote server
    		// Records may have been deleted on server since phone last synchronised
    		if(entry.length() > 0){
    			//deleteAllRemRows(entry_selected_table, entry);
    			//deleteAllSynchRows(entry_selected_table, entry);
    		}
    		else{
    			//deleteAllRemRows(orderedtables.get(1), "None");
    			//deleteAllSynchRows(orderedtables.get(1), "None");
    		}
    		
    	      SAXParserFactory factory = SAXParserFactory.newInstance();
    	      SAXParser saxParser = factory.newSAXParser();
    	 
    	      XMLHandler handler = new XMLHandler();
    	      saxParser.parse(xml_stream, handler); 
    	      
    	      try{
					ih.close();
					//db.endTransaction();
					
				}
				catch(Exception npe){}
				//finally{
				//	try{
				//		db.setTransactionSuccessful();
				//	}
				//	catch(Exception e){}
				//}
    	         	 
    	    } catch (Exception e) {
    	      e.printStackTrace();
    	      Log.i(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return 0;
    	    }
    	    //Log.i(getClass().getSimpleName(), "VIEWS "+views.toString());
    	    //System.out.println(views.toString());
    	    
    	    //createTable();
    	    
    	    //createAllRows(allrows);
    	    
    	  if(hasimages)
    	    return 1;
    	  else
    	    return 2;
	}
	
	
	String hashkey;
    public class XMLHandler extends DefaultHandler {
	   	 
    	private Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
        String templocation = "", tablename, lasttablename, value, spinkey = "", radiokey = "", title = "", primary_key="", foreign_key=""; // , xkey
        //HashMap<String, String> rowhash = new HashMap<String, String>();
        //Row row;
        int index;
        boolean have_entry = false, record_stored = false,intextitem = false, inspinitem = false, inradioitem = false, incheckitem = false, ingpsitem = false, inphotoitem = false, invideoitem = false, inaudioitem = false, gettable = false, firstentry = true, indate = false;
        //InsertHelper ih;
        Vector<String> positionpos;
        
        public void startElement(String uri, String qName, String localName, Attributes attributes) throws SAXException {

        	//Log.i("XML QNAME: ", qName);  
         
        	String[] tempstring;
    		
    		
         if(qName.equalsIgnoreCase("table_name")){
     
        	 gettable = true;
        	 
        	 // If rowhash has data it needs to be stored
        	 if(have_entry){ //!rowhash.isEmpty()){
        		 //rowhash.put("ecremote", ""+1);
        		 ih.bind(positionpos.indexOf("ecremote"), ""+1);
        		 //rowhash.put("ecstored", "R");
        		 ih.bind(positionpos.indexOf("ecstored"), "R");
        		 // FOR SCORE LOCATION ID
        		 if(templocation.length() > 0){
        			 title += "- "+templocation;
        			 templocation = "";
        		 }
        		 
        		//phonekey = getMaxPhonekeyValue(lasttablename);
     	       	phonekey++;
     	       	
     	       //Log.i("HERE 1", ""+phonekey +" RES: "+getValue(lasttablename, "genkey"));
     	       
     	       	//rowhash.put("phonekey", ""+phonekey);
     	       	ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
     	    	if(getValue(lasttablename, "genkey") != null && getValue(lasttablename, "genkey").length() > 0){
     	    		title = "- " + phonekey + " "+ title; 
     	    		//Log.i("ADDING PHONEKEY", title);
     	        }
     	    	
        		 title = title.replaceFirst("- ", "");
        		 //rowhash.put("title", title);
        		 ih.bind(positionpos.indexOf("title"), title);
          	    
        		 primary_key = primary_key.replaceFirst(",", "");
        		 //rowhash.put("pkey", primary_key);
        		 ih.bind(positionpos.indexOf("pkey"), primary_key);
          	
        		 foreign_key = foreign_key.replaceFirst(",", "");
        		 //rowhash.put("fkey", foreign_key);
        		 ih.bind(positionpos.indexOf("fkey"), foreign_key);

        		 //if(!checkValue(lasttablename, "pkey", primary_key))
        		
     	       	//createRow(lasttablename, rowhash);
     	       	ih.execute();
        		//ih.close();
     	       countr++;
            	//if(countr % 100 ==0)
            		Log.i("COUNT ROW 3", tablename+" "+countr);
        		          	
        		 	//rowhash = new HashMap<String, String>();
        		 //rowhash.clear();
            	have_entry = false;
        		 //ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
        		 ih.prepareForReplace();
        		 positionpos = getRowPositions(tablename);
        		 title = "";
        		 primary_key = "";
        		 foreign_key = "";
          	
        		 // Initialise all checkbox values to false
        		 //String[] tempstring;
        		 for(String key : checkboxgroups){
        			 tempstring = checkboxhash.get(key);
        			 for(String box : tempstring){
        				 //rowhash.put(box, "0");
        				 ih.bind(positionpos.indexOf(box), "0");
        			 }
        		 }
        		 
        		 //lasttablename = tablename;
          	
        		 // This is needed as if a table has no entries the only way
        		 // to store the last record from the previous table is to 
        		 // do it here
        		 // The recod_stored value is checked when "entry" is reached again
        		 record_stored = true;
        		 // tablename = attributes.getValue("table_name");
        		 // getValues(tablename);
        	 
        		 //row = new Row();
        		 // rowhash.clear();
        		 //Log.i("XML TABLE: ", tablename);
        		 // getitem = false;
        	 }

        	 //else{
        		 
        	 //}
        	// We are at the end of the phone XML tables
    		 if(orderedtablesrev.get(tablename) == null)
    			 return;
         }
         
         if(qName.equalsIgnoreCase("entry")){
        	 //getitem = true;
        	 if(firstentry){
        		 firstentry = false;
        		 lasttablename = tablename;
        	 }
        	 else if(record_stored == false){
        		
        		//rowhash.put("ecremote", ""+1);
        		// NULL POINTER HERE
        		//Log.i("POSITION", ""+positionpos.indexOf("ecremote"));
        		ih.bind(positionpos.indexOf("ecremote"), ""+1);
             	//rowhash.put("ecstored", "R");
             	//Log.i("POSITION", ""+positionpos.indexOf("ecstored"));
             	ih.bind(positionpos.indexOf("ecstored"), "R");
             	
             // FOR SCORE LOCATION ID
       		 if(templocation.length() > 0){
       			 title += templocation;
       			 templocation = "";
       		 }
       		 
       		//phonekey = getMaxPhonekeyValue(lasttablename);
       		phonekey++;
 	       	
 	       //Log.i("HERE 2", ""+phonekey);
 	       
 	       	//rowhash.put("phonekey", ""+phonekey);
 	        ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
 	    	if(getValue(lasttablename, "genkey") != null && getValue(lasttablename, "genkey").length() > 0){
 	    		title = "- " + phonekey + " "+ title; 
 	    		//Log.i("ADDING PHONEKEY", title);
 	        }
 	    	
             	title = title.replaceFirst("- ", "");
             	//rowhash.put("title", title);
             	ih.bind(positionpos.indexOf("title"), title);
             	    
             	primary_key = primary_key.replaceFirst(",", "");
             	//rowhash.put("pkey", primary_key);
             	ih.bind(positionpos.indexOf("pkey"), primary_key);
             	
             	foreign_key = foreign_key.replaceFirst(",", "");
             	//rowhash.put("fkey", foreign_key);
             	ih.bind(positionpos.indexOf("fkey"), foreign_key);
             	//Log.i("XML TABLE:",lasttablename); 
             	//for(String r : rowhash.keySet())
             	//	Log.i("XML TABLE: "+r, rowhash.get(r));

             	//if(!checkValue(lasttablename, "pkey", primary_key))
             	//Log.i("CREATING TABLE: ", tablename);
     	       	
             	//createRow(lasttablename, rowhash);
             	
             	ih.execute();
             	//ih.close();
             	countr++;
             	if(countr % 100 ==0)
             		Log.i("COUNT ROW 1", tablename+" "+countr);
                         	
    		 	//rowhash = new HashMap<String, String>();
             	//rowhash.clear();
             	have_entry = false;
             	//ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
             	ih.prepareForReplace();
             	//positionpos = getRowPositions(tablename);
             	title = "";
             	primary_key = "";
             	foreign_key = "";
             	
             // Initialise all checkbox values to false
        		// NEED THIS IF ROWHASH IS USED
        		for(String key : checkboxgroups){
        			tempstring = checkboxhash.get(key);
        			for(String box : tempstring){
        				//rowhash.put(box, "0");
        				ih.bind(positionpos.indexOf(box), "0");
        			}
        		}
             	
             	lasttablename = tablename;
             	//intextitem = false;
             	//inspinitem = false;
             	//incheckitem = false;
             	//ingpsitem = false;
             	//inphotoitem = false;
             	//invideoitem = false;
             	//inaudioitem = false;
             	//indate = false;
        	 }
        	 record_stored = false;
        	 
         }
         
         if(qName.equalsIgnoreCase("ecdate")){
        	 indate = true;
         }
         

          for(String key : textviews){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			
			//Log.i("XML TABLE CHECK 2: ", xkey+" "+qName);
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 intextitem = true;
	        	 //Log.i("XML TABLE CHECK: ", key);
	        	 break;
	         }
				
         } 

         
          for(String key : photos){
        	 //Log.i("XML PHOTO CHECK: ", key);
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			
			//Log.i("XML PHOTO CHECK: ", xkey);
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inphotoitem = true;
	        	 break;
	         }
				
         } 

         
         
          for(String key : videos){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 invideoitem = true;
	        	 break;
	         }
				
         } 

         
          for(String key : audio){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inaudioitem = true;
	        	 break;
	         }
				
         } 
         


         
           for(String key : gpstags){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			//hashkey = key;
			//Log.i("XML TABLE CHECK 2: ", xkey+" "+qName);
			if(qName.equalsIgnoreCase(key+"_lat")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lat";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_lon")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lon";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_alt")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_alt";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_acc")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_acc";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_provider")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_provider";
	        	 break;
	         }
				
         } 
         

         
          for(String key : spinners){
        	//xkey = key.replaceAll("\\s+", "_");
     		if(qName.equalsIgnoreCase(key)){
     			spinkey = key;
     			inspinitem = true;
     			//Log.i("XML TABLE CHECK: ", xkey);
     			break;
     		}    		
     	} 
         

         
       	 for(String key : radios){
         	
      		//xkey = key.replaceAll("\\s+", "_");
        	String xkey = key;
        	
        	if(xmlproject.equalsIgnoreCase("SCORE_FULL")){
        		xkey = xkey.replaceFirst("9", "XNINEX");
        	} 
      		
      		if(qName.equalsIgnoreCase(xkey)){
      			radiokey = key;
      			inradioitem = true;
      			//Log.i("XML TABLE CHECK: ", xkey);
      			break;
      		}    		
      	} 
        	 
         
           for(String key : checkboxgroups){
        	//checkkey = key;
     		//xkey = key.replaceAll("\\s+", "_");
     		if(qName.equalsIgnoreCase(key)){
     			incheckitem = true;
     			//Log.i("XML TABLE CHECK: ", xkey);
     			break;
     		}    		
         } 
     		
     		//rowhash.put("ecremote", ""+1);
        	//rowhash.put("ecstored", "R");

        	///////createRow(tablename, rowhash);
        	
        	//rowhash.clear();
         } 


        

        public void endElement(String uri, String qName, String localName) throws SAXException {

        	//Log.i("End Element :", qName);
        	if(qName.equalsIgnoreCase("entries") && have_entry){ //!rowhash.isEmpty()){
        		//rowhash.put("ecremote", ""+1);
        		ih.bind(positionpos.indexOf("ecremote"), ""+1);
           		//rowhash.put("ecstored", "R");
           		ih.bind(positionpos.indexOf("ecstored"), "R");
           		
           		//int phonekey = getMaxPhonekeyValue(tablename);
     	       	phonekey++;
     	       	
     	       //Log.i("HERE 3", ""+phonekey);
     	       	//rowhash.put("phonekey", ""+phonekey);
     	        ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
     	    	if(getValue(tablename, "genkey") != null && getValue(tablename, "genkey").length() > 0){
     	    		title = "- " + phonekey + " "+ title; 
     	    		//Log.i("ADDING PHONEKEY", title);
     	        }
     	    	
           		title = title.replaceFirst("- ", "");
           		//rowhash.put("title", title);
           		ih.bind(positionpos.indexOf("title"), title);
           		
           		primary_key = primary_key.replaceFirst(",", "");
             	//rowhash.put("pkey", primary_key);
             	ih.bind(positionpos.indexOf("pkey"), primary_key);
             	
             	foreign_key = foreign_key.replaceFirst(",", "");
             	//rowhash.put("fkey", foreign_key);
             	ih.bind(positionpos.indexOf("fkey"), foreign_key);
     	       	
           		//createRow(tablename, rowhash);
           		ih.execute();
             	//ih.close();
           		countr++;
             	//if(countr % 100 ==0)
             		Log.i("COUNT ROW 2", tablename+" "+countr);

    		 	
    		 	//rowhash = new HashMap<String, String>();
    		 	//rowhash.clear();
    		 	have_entry = false;
    		 	//ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
    		 	ih.prepareForReplace();
    		 	//positionpos = getRowPositions(tablename);
    		 	title = "";
    		 	primary_key = "";
    		 	foreign_key = "";
    		 	
    		 	String[] tempstring;
    		 // Initialise checkbox values in hash
				for(String key : checkboxgroups){
         			tempstring = checkboxhash.get(key);
         			for(String box : tempstring){
         				//rowhash.put(box, "0");
         				ih.bind(positionpos.indexOf(box), "0");
         			}
         		}
          }

       }
          
          public void characters(char ch[], int start, int length) throws SAXException {
	          	
        	  String value = new String(ch, start, length);
        	  if(length == 0 || value.equalsIgnoreCase("N/A"))
        		  value = "";
        	  
        	  if(indate){
        		  //rowhash.put("\"ecdate\"", value);
        		  have_entry = true;
        		  ih.bind(positionpos.indexOf("ecdate"), value);
        		  indate = false;
        	  }
        	  
        	  if(intextitem){
        		//if(value.equalsIgnoreCase("N/A"))
        		//	value = "";
  				//rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
        		  //Log.i("HASH KEY", hashkey+" VALUE "+value+" POS "+positionpos.indexOf(hashkey));
  				ih.bind(positionpos.indexOf(hashkey), value);
  				if(value.length() > 0 && listfields != null && listfields.contains(hashkey)){
  					// FOR SCORE
  					if(hashkey.equalsIgnoreCase("Location_ID"))
  						templocation = value;
  					else
  						title += "- "+ value + " ";
  				}
  				if(primary_keys.contains(hashkey)){
					primary_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				if(foreign_keys.contains(hashkey)){
					foreign_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				//Log.i("XML TEXT 2: ", tablename+" "+hashkey+" "+value);
  				intextitem = false;
        	  }
        	  
        	  if(ingpsitem){
    				//rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
    				have_entry = true;
    				ih.bind(positionpos.indexOf(hashkey), value);
    				ingpsitem = false;
          	  }
        	  
        	  if(inphotoitem){
  				//rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
  				  ih.bind(positionpos.indexOf(hashkey), value);
  				inphotoitem = false;
  				// If there is an image it needs to be downloaded
  				if(value.length() > 2)
  					hasimages = true;
        	  }
        	  
        	  if(invideoitem){
    				//rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
    				ih.bind(positionpos.indexOf(hashkey), value);
    				invideoitem = false;
          	  }
        	  
        	  if(inaudioitem){
  				//rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
  				ih.bind(positionpos.indexOf(hashkey), value);
  				inaudioitem = false;
        	  }
        	  
        	  String[] tempstring, tempstring2;
        	  if(inspinitem){
        		  String[] spinners;
        		  index = 0;
        		  // Initialise value
        		  
        		  //rowhash.put("\""+spinkey+"\"", ""+index);
        		  have_entry = true;
        		  ih.bind(positionpos.indexOf(spinkey), ""+index);
        		  spinners = spinnershash.get(spinkey); 

        		  //value = new String(ch, start, length);
        		  for(String val : spinners){
        			  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  //rowhash.put("\""+spinkey+"\"", ""+index);
        				  have_entry = true;
        				  ih.bind(positionpos.indexOf(spinkey), ""+index);
        				  if(listspinners != null && listspinners.contains(spinkey))
        		    			title += "- "+ value + " ";
        				  if(primary_keys.contains(spinkey)){
        						primary_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        	  			  if(foreign_keys.contains(spinkey)){
        					    foreign_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			  break;
        			  }
        			  index++;
        		  }
        		  
        		  inspinitem = false;
        	  }
        	  
        	  if(inradioitem){
        		  String[] radios;
        		  index = 0;
        		  // Initialise value
        		  
        		  //rowhash.put("\""+radiokey+"\"", ""+index);
        		  have_entry = true;
        		  ih.bind(positionpos.indexOf(radiokey), ""+index);
        		  radios = radioshash.get(radiokey); 

        		  //value = new String(ch, start, length);
        		  for(String val : radios){
        			  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  //rowhash.put("\""+radiokey+"\"", ""+index);
        				  have_entry = true;
        				  ih.bind(positionpos.indexOf(radiokey), ""+index);
        				  if(listradios != null && listradios.contains(radiokey))
        		    			title += "- "+ value + " ";
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			  break;
        			  }
        			  index++;
        		  }
        		  
        		  inradioitem = false;
        	  }
        	  
        	  if(incheckitem){
              for(String key : checkboxgroups){
          		//xkey = key.replaceAll("\\s+", "_");
          		try{
          			tempstring = checkboxhash.get(key);
              		for(String box : tempstring){
              			tempstring2 = value.split(",");
              			for(String box2 : tempstring2){
              				if(box2.equalsIgnoreCase(checkboxvaluesvalueshash.get(box))){
              					//row.checkboxes.put(box2, true);
              					//if(checkboxhash.get(box2) != null && checkboxhash.get(box2) == true)
              						//rowhash.put(box, "1");
              						have_entry = true;
              						ih.bind(positionpos.indexOf(box), "1");
              					//else
              					//	rowhash.put(box2, "0");
              				}
              			}
              		}
          		}
          		catch(NullPointerException npe){
         			Log.i(getClass().getSimpleName(), "XML ERROR 1: "+ npe.toString());
         		}
          		}  
        	  } 
        	  
        	  if(gettable){
    				tablename = new String(ch, start, length);
    				
    				if(orderedtablesrev.get(tablename) != null){ 
    					lasttablename = tablename;
    					    					  					
    					gettable = false;
    					//Log.i("XML TEXT 3: ", "TABLE = "+new String(ch, start, length));
    					getValues(tablename);
    					//getValues2(tablename);
    					
    					phonekey = getMaxPhonekeyValue(tablename);
    					
    					// Ensure rowhash is cleared
    					//rowhash.clear();
    					have_entry = false;
    					getRowPositions(tablename);
    					// CAUSES NULL POINTER EXCEPTION
    					try{
    						ih.close();
    						//db.endTransaction();
    						
    					}
    					catch(Exception npe){}
    					//finally{
    					//	try{
    					//		db.setTransactionSuccessful();
    					//	}
    					//	catch(Exception e){}
    					//}
    					//db.beginTransaction();
    					ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
    					ih.prepareForReplace();
    					positionpos = getRowPositions(tablename);
    					// Initialise checkbox values in hash
    					// NEED THIS IF ROWHASH USED
    					for(String key : checkboxgroups){
    	         			tempstring = checkboxhash.get(key);
    	         			for(String box : tempstring){
    	         				//rowhash.put(box, "0");
    	         				ih.bind(positionpos.indexOf(box), "0");
    	         			}
    	         		}
    				
    					primary_keys.clear();
    					if(getKeyValue(tablename) != null && getValue(tablename, "pkey").length() > 0){
    						for(String key : (getValue(tablename, "pkey")).split(",")){
    							primary_keys.addElement(key);
    						}
    					}
    				
    					foreign_keys.clear();
    					if(orderedtablesrev.get(tablename) > 1){
    						String ftablename = orderedtables.get(orderedtablesrev.get(tablename) - 1);
    				    					
    						if(getKeyValue(ftablename) != null && getValue(ftablename, "pkey").length() > 0){
    							for(String key : (getValue(ftablename, "pkey")).split(",")){
    								foreign_keys.addElement(key);
    							}
    						}
    					}
    			 	}
        	  	}
          	}
          
          
        } */
    
    
 /*   
public int getRemoteDataNew(String project, String table, String entry_selected_table, String entry, boolean local, boolean getimages, String sIMEI, String email){ //, String lat, String lon){  // ArrayList<Row>
		
	Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
		xmlproject = project;
		hasimages = false;
		//boolean result = false;
		
		InputStream xml_stream = null;
		
		LinkedHashMap<String, String> keyshash = getAllKeys();
		
		int keycount = 1;
		orderedtables = new HashMap<Integer, String>();
    	orderedtablesrev = new HashMap<String, Integer>();
        for(String key : keyshash.keySet()){
	    	orderedtables.put(keycount, key);
	    	orderedtablesrev.put(key, keycount);
	    	keycount++;
	    } 

		
		//String tableonly = "false";
		//if(gettableonly == true){
		//	tableonly = "true";
		//}
		
		//Log.i("GET REMOTE DATA URL", "http://www.doc.ic.ac.uk/~dmh1/Android/test_remote.xml?project="+project+"&table="+table);
    	try{
    		
    		URL url;
    		
    		String url_string;
    		
    		if(local)
    			url_string = getValue("local_remote_xml"); 
    		else
    			url_string = getValue("remote_xml");
    		    		 		
    		if(getimages){
    			url_string += "?project="+project+"&type=thumbnail"+"&table="+table+"&entry="+entry;
    		}
    		// If this comes from a table entry then "table" is the table the entry is from and entry_selected_table is the base table 
    		// from down to where entries should be retrieved
    		//http://test.mlst.net/SCORE/downloadFromServer.php?project=SCORE_FULL&type=data&select_table=Location&entry=Ke053&table=Village
    		// will download all of the data down to the location data for village Ke053. That means select_table is now the target table and has the same function as table in the first url.
    		else{
    			if(entry.length() > 0)
    				url_string += "?project="+project+"&type=data"+"&table="+entry_selected_table+"&entry="+entry+"&select_table="+table;
    			else
    				url_string += "?project="+project+"&type=data"+"&table="+table;
    			//Log.i("DATA DOWNLOAD ",url_string);
    		}
    		    	
    		url_string += "&phoneid="+sIMEI;
    		url_string += "&email="+email;
    		
    		Log.i("GET REMOTE DATA URL", url_string.toString());
        	    		
    		url = new URL(url_string);
    		
    		if(getimages){
    			
    			// Get the image zip file and uncompress it to the thumbs directory
    			final int BUFFER = 2048;
    			try {
    				
    				try {
    					URL zurl = new URL(url_string);
    			        URLConnection conn = zurl.openConnection();
    			        conn.setDoOutput(true);
    			        conn.setDoInput(true);
    			        conn.setRequestProperty("content-type", "binary/data");
    			        InputStream in = conn.getInputStream();
    			        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");

    			        byte[] b = new byte[1024];
    			        int count;

    			        while ((count = in.read(b)) >= 0) {
    			            out.write(b, 0, count);
    			        }
    			        out.close();
    			        in.close();

    			    } catch (IOException e) {
    			        e.printStackTrace();
    			    }

    			    FileOutputStream fos;
    			    byte data[] = new byte[1024];

    				
    				String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject();
    				try{
    			       	File f = new File(picdir);
    			    	if(!f.exists()){
    			    		f.mkdir();
    			    	}
    				}
        			catch(Exception e){
    			    	//Log.i("ImageSwitcher THUMBS ERROR", thumbdir);
    			    	
    			    }   				
    				
        			BufferedOutputStream dest = null;
        	         BufferedInputStream is = null;
        	         ZipEntry zentry;
        	         ZipFile zipfile = new ZipFile(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");
        	         Enumeration<?> e = zipfile.entries();
        	         while(e.hasMoreElements()) {
        	            zentry = (ZipEntry) e.nextElement();
        	            //System.out.println("Extracting: " +zentry);
        	            is = new BufferedInputStream
        	              (zipfile.getInputStream(zentry));
        	            int count;
        	            byte data2[] = new byte[BUFFER];
        	            String fullpath = zentry.getName();
     		            String filename = fullpath.replaceAll(".*[/\\\\]","");
     		            filename = filename.replaceFirst("tn_","");
    		            //fos = new FileOutputStream(picdir+"/"+filename);
        	            fos = new FileOutputStream(picdir+"/"+filename);
        	            dest = new BufferedOutputStream(fos, BUFFER);
        	            while ((count = is.read(data2, 0, BUFFER)) != -1) {
        	               dest.write(data2, 0, count);
        	            }
        	            dest.flush();
        	            dest.close();
        	            is.close();
        	         }
        			
    				
    		      } catch(Exception e) {
    		         e.printStackTrace();
    		         return 0;
    		      }
    		      return 2;
    		}
    		
    		else{
    			HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
    			urlc.setRequestMethod("GET");
    			urlc.connect();

    			xml_stream = urlc.getInputStream();
    			//if(xmlproject.equalsIgnoreCase("SCORE_FULL"))
    			//	xml_stream = fixXMLTags(xml_stream);
    			
    			BufferedWriter os = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			BufferedReader br = new BufferedReader(new InputStreamReader(xml_stream));
    			String line = null;
    			int counttable = 0, countentry = 0;
    			
    			int count = 0;
    			while ((line = br.readLine()) != null) {
    				//line = line.replaceFirst("<9", "<XNINEX");
    				//line = line.replaceFirst("</9", "</XNINEX");
    				if(line.contains("<table>"))
    					counttable++;
    				
    				
    				
    				
    				primary_keys.clear();
					if(getKeyValue(tablename) != null && getValue(tablename, "pkey").length() > 0){
						for(String key : (getValue(tablename, "pkey")).split(",")){
							primary_keys.addElement(key);
						}
					}
				
					foreign_keys.clear();
					if(orderedtablesrev.get(tablename) > 1){
						String ftablename = orderedtables.get(orderedtablesrev.get(tablename) - 1);
				    					
						if(getKeyValue(ftablename) != null && getValue(ftablename, "pkey").length() > 0){
							for(String key : (getValue(ftablename, "pkey")).split(",")){
								foreign_keys.addElement(key);
							}
						}
					}
    				
    				
    				if(line.contains("<entry>"))
    					countentry++;
    				os.write(line);
    				os.write("\n");
    				//Log.i("XML", line+"\n");
    				count++;
    				if(countentry %100 == 0)
    					Log.i("XML", ""+countentry);
    				if(counttable > 1 && countentry > 10000){
    					br.close();
    	    			os.close();
        				return 3;
    				}
    			}
    			br.close();
    			os.close();
    			
    			try{
    				xml_stream = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			}
    			catch(Exception e){
    				Log.i("XML ENTRY CHECK", e.toString());
    			}
    			
    			if(counttable > 1 && countentry > 10000)
    				return 3;
    			
    		}
    		
    	}
    	catch(MalformedURLException  ex){
    		System.out.println("1 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 1 "+ex.toString());
    		return 0;
    		}
    	catch (IOException ex) {
    		System.out.println("2 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 2 "+ex.toString());
    		return 0;
    		}
    	
    	try {
    		
    		// Clear all remote and synchronised data first to ensure synchronisation with remote server
    		// Records may have been deleted on server since phone last synchronised
    		if(entry.length() > 0){
    			//deleteAllRemRows(entry_selected_table, entry);
    			//deleteAllSynchRows(entry_selected_table, entry);
    		}
    		else{
    			//deleteAllRemRows(orderedtables.get(1), "None");
    			//deleteAllSynchRows(orderedtables.get(1), "None");
    		}
    		
    	      SAXParserFactory factory = SAXParserFactory.newInstance();
    	      SAXParser saxParser = factory.newSAXParser();
    	 
    	      XMLHandler handler = new XMLHandler();
    	      saxParser.parse(xml_stream, handler);   	 
    	         	 
    	    } catch (Exception e) {
    	      //e.printStackTrace();
    	      Log.i(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return 0;
    	    }
    	    //Log.i(getClass().getSimpleName(), "VIEWS "+views.toString());
    	    //System.out.println(views.toString());
    	    
    	    //createTable();
    	    
    	    //createAllRows(allrows);
    	    
    	  if(hasimages)
    	    return 1;
    	  else
    	    return 2;
	}
    
    */
    private Vector<String> getRowPositions(String table){
    	
    	getValues(table);

    	Vector<String> positionvec = new Vector<String>();
    	//Positions need to start at 1 so add a null in the 0 position
    	positionvec.addElement("NULL");
    	positionvec.addElement("ecstored");
        for(String key : textviews){
        	positionvec.addElement(key);
        }
        
        for(String key : spinners){
        	positionvec.addElement(key);
        }
        
        for(String key : radios){
        	positionvec.addElement(key);
        }
        
        for(String key : checkboxes){
        	positionvec.addElement(key);
        }
        
    	for(String key : photos){
    		positionvec.addElement(key);
    	}
    	
    	for(String key : videos){
    		positionvec.addElement(key);
    	}
    	
    	for(String key : audio){
    		positionvec.addElement(key);
    	}
    	
    	for(String key : gpstags){
    		positionvec.addElement(key+"_lat");
    		positionvec.addElement(key+"_lon");
    		positionvec.addElement(key+"_alt");
    		positionvec.addElement(key+"_acc");
    		positionvec.addElement(key+"_bearing");
    		positionvec.addElement(key+"_provider");
    	}
        
    	positionvec.addElement("ecremote");
    	positionvec.addElement("ecdate");
    	positionvec.addElement("ectitle");
    	positionvec.addElement("ecpkey");
    	positionvec.addElement("ecphonekey");
    	positionvec.addElement("message"); 
    	positionvec.addElement("ecfkey");
    	
    	return positionvec;

    }
    
	Vector<String>v1extras = new Vector<String>();
	int phonekeyv1 = 0;
	boolean hasimages;
	//HashMap<String, Vector<HashMap<String, String>>> allrows = new HashMap<String, Vector<HashMap<String, String>>>();
	public int getRemoteDataV1(){ //, String lat, String lon){  // ArrayList<Row>
				
		InputStream xml_stream = null;
    		
		getValues("Data");
		
		v1extras.addElement("ecEntryId");
		v1extras.addElement("ecLatitude");
		v1extras.addElement("ecLongitude");
		v1extras.addElement("ecAltitude");
		v1extras.addElement("ecAccuracy");
		v1extras.addElement("ecTimeCreated");
		
		phonekeyv1 = getMaxPhonekeyValue("Data");

		
    	URL url;
    	
    	try{	
    		String url_string = "http://epicollectserver.appspot.com/downloadFromServer?project="+DATABASE_PROJECT;;

    		url = new URL(url_string);
    		
    		HttpURLConnection urlc;
    		
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
    	catch(MalformedURLException  ex){
    		System.out.println("1 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 1 "+ex.toString());
    		return 0;
    		}
    	catch (IOException ex) {
    		System.out.println("2 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 2 "+ex.toString());
    		return 0;
    		}
    	
    	try {
    		 
    	      SAXParserFactory factory = SAXParserFactory.newInstance();
    	      SAXParser saxParser = factory.newSAXParser();
    	 
    	      XMLHandler2 handler = new XMLHandler2();
    	      saxParser.parse(xml_stream, handler); 
    	      
    	         	 
    	    } catch (Exception e) {
    	      //e.printStackTrace();
    	      Log.i(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return 0;
    	    }
    	    return 2;
	}
	
	int countr = 0;
	String hashkey;
    public class XMLHandler2 extends DefaultHandler {
	   	 
    	private Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
        String templocation = "", tablename, lasttablename, value, spinkey = "", radiokey = "", title = "", primary_key="", foreign_key=""; // , xkey
        HashMap<String, String> rowhash = new HashMap<String, String>();
        //Row row;
        int index;
        boolean record_stored = false, inv1item = false, intextitem = false, inspinitem = false, inradioitem = false, incheckitem = false, ingpsitem = false, inphotoitem = false, invideoitem = false, inaudioitem = false, gettable = false, firstentry = true, indate = false;
              
        public void startElement(String uri, String qName, String localName, Attributes attributes) throws SAXException {

        	//Log.i("XML QNAME: ", qName);  
         
        	String[] tempstring;
        
         if(qName.equalsIgnoreCase("entry")){
        	 //getitem = true;
        	 if(firstentry){
        		 firstentry = false;
        		 
        	 }
        	 else if(record_stored == false){
        		
        		 rowhash.put("ecremote", ""+1); 
         		rowhash.put("ecstored", "R");
         		rowhash.put("ectime", rowhash.get("ecdate"));
            	rowhash.put("eckey", rowhash.get("ecpkey"));
            		
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            	Date resultdate = new Date(Long.parseLong(rowhash.get("ecdate")));
           		rowhash.put("ecdatev1", sdf.format(resultdate));
           		
           		//int phonekey = getMaxPhonekeyValue("Data");
     	       	phonekeyv1++;
     	       	
     	       //Log.i("HERE 1", ""+phonekey +" RES: "+getValue(lasttablename, "genkey"));
     	       
     	       	rowhash.put("ecphonekey", ""+phonekeyv1);
     	    	title = phonekeyv1 + " - "+ title; 
     	    	
     	    	rowhash.put("ectitle", title);
             	
            	createRow("Data", rowhash);
             	countr++;
             	//if(countr % 100 ==0)
             	//	Log.i("COUNT ROW 3", tablename+" "+countr);
            	
             	rowhash.clear();
             	title = "";
             	primary_key = "";
             	foreign_key = "";
             	
             // Initialise all checkbox values to false
        		for(String key : checkboxgroups){
        			tempstring = checkboxhash.get(key);
        			for(String box : tempstring){
        				rowhash.put(box, "0");
        			}
        		}

        	 }
        	 record_stored = false;
        	 
         }
         
         if(qName.equalsIgnoreCase("ecdate")){
        	 indate = true;
         }
         
         for(String key : v1extras){
 			if(qName.equalsIgnoreCase(key)){
 				hashkey = key; //xkey;
 	        	 inv1item = true;
 	        	 //Log.i("XML TABLE CHECK: ", key);
 	        	 break;
 	         }
 				
          }
         
         for(String key : textviews){
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 intextitem = true;
	        	 //Log.i("XML TABLE CHECK: ", key);
	        	 break;
	         }
				
         }
         
         for(String key : photos){
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inphotoitem = true;
	        	 break;
	         }
				
         }
         
         for(String key : videos){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 invideoitem = true;
	        	 break;
	         }
				
         }
         
         for(String key : audio){
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inaudioitem = true;
	        	 break;
	         }
				
         }
         
         for(String key : gpstags){

			if(qName.equalsIgnoreCase(key+"_lat")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lat";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_lon")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lon";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_alt")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_alt";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_acc")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_acc";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_bearing")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_bearing";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_provider")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_provider";
	        	 break;
	         }
				
         }
         
         for(String key : spinners){
        		
     		if(qName.equalsIgnoreCase(key)){
     			spinkey = key;
     			inspinitem = true;
     			break;
     		}    		
     	}
         
         for(String key : radios){
         	
        	String xkey = key;
      		
      		if(qName.equalsIgnoreCase(xkey)){
      			radiokey = key;
      			inradioitem = true;
      			break;
      		}    		
      	}
        	 
            		
         for(String key : checkboxgroups){
 
     		if(qName.equalsIgnoreCase(key)){
     			incheckitem = true;
     			break;
     		}    		
         }

         } 


        

        public void endElement(String uri, String qName, String localName) throws SAXException {

        	//Log.i("End Element :", qName);
        	if(qName.equalsIgnoreCase("entries") && !rowhash.isEmpty()){
        		rowhash.put("ecremote", ""+1); 
        		rowhash.put("ecstored", "R");
        		rowhash.put("ectime", rowhash.get("ecdate"));
           		rowhash.put("eckey", rowhash.get("ecpkey"));
           		
           		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           		Date resultdate = new Date(Integer.parseInt(rowhash.get("ecdate")));
           		rowhash.put("ecdatev1", sdf.format(resultdate));
     	       	
           		//int phonekey = getMaxPhonekeyValue("Data");
     	       	phonekeyv1++;
     	       	
     	       //Log.i("HERE 1", ""+phonekey +" RES: "+getValue(lasttablename, "genkey"));
     	       
     	       	rowhash.put("ecphonekey", ""+phonekeyv1);
     	        title = phonekeyv1 + " - "+ title; 
    	    	
    	    	rowhash.put("ectitle", title);
    	    	
           		createRow("Data", rowhash);
           		countr++;
             	if(countr % 100 ==0)
             		Log.i("COUNT ROW", tablename+" "+countr);
             	
    		 	rowhash.clear();
    		 	title = "";
    		 	primary_key = "";

          }	
       }
          
          public void characters(char ch[], int start, int length) throws SAXException {
	          	
        	  String value = new String(ch, start, length);
        	  if(length == 0 || value.equalsIgnoreCase("N/A"))
        		  value = "";
        	  
        	  if(indate){
        		  rowhash.put("\"ecdate\"", value);
        		  indate = false;
        	  }
        	  
        	  if(inv1item){
        		  
        		if(hashkey.equalsIgnoreCase("ecEntryId")){
        			rowhash.put("eckey", value);
      				rowhash.put("ecpkey", value);
        		}
        		else if(hashkey.equalsIgnoreCase("ecLatitude")){
        			rowhash.put("ecgps_lat", value);
       		  	}
        		else if(hashkey.equalsIgnoreCase("ecLongitude")){
        			rowhash.put("ecgps_lon", value);
       		  	}
        		else if(hashkey.equalsIgnoreCase("ecAltitude")){
        			rowhash.put("ecgps_alt", value);
       		  	}
        		else if(hashkey.equalsIgnoreCase("ecAccuracy")){
        			rowhash.put("ecgps_acc", value);
       		  	}
        		else if(hashkey.equalsIgnoreCase("ecTimeCreated")){
        			rowhash.put("ecdate", value);
       		  	}
  				
    			inv1item = false;
          	  }
        	  
        	  if(intextitem){
        		//if(value.equalsIgnoreCase("N/A"))
        		//	value = "";
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
  				if(value.length() > 0 && listfields != null && listfields.contains(hashkey)){
  					// FOR SCORE
  					if(hashkey.equalsIgnoreCase("Location_ID"))
  						templocation = value;
  					else
  						title += "- "+ value + " ";
  				}
  				if(primary_keys.contains(hashkey)){
					primary_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				if(foreign_keys.contains(hashkey)){
					foreign_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				//Log.i("XML TEXT 2: ", tablename+" "+hashkey+" "+value);
  				intextitem = false;
        	  }
        	  
        	  if(ingpsitem){
    				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
    				ingpsitem = false;
          	  }
        	  
        	  if(inphotoitem){
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
  				inphotoitem = false;
  				// If there is an image it needs to be downloaded
  				if(value.length() > 2)
  					hasimages = true;
        	  }
        	  
        	  if(invideoitem){
    				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
    				invideoitem = false;
          	  }
        	  
        	  if(inaudioitem){
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
  				inaudioitem = false;
        	  }
        	  
        	  if(inspinitem){
        		  String[] spinners;
        		  index = 0;
        		  // Initialise value
        		  
        		  rowhash.put("\""+spinkey+"\"", "Select"); //""+index);
        		  //spinners = spinnershash.get(spinkey); 

        		  //value = new String(ch, start, length);
        		  //for(String val : spinners){
        			//  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  rowhash.put("\""+spinkey+"\"", value); //""+index);
        				  if(listspinners != null && listspinners.contains(spinkey))
        		    			title += "- "+ value + " ";
        				  if(primary_keys.contains(spinkey)){
        						primary_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        	  			  if(foreign_keys.contains(spinkey)){
        					    foreign_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			 // break;
        			  //}
        			 // index++;
        		 // }
        		  
        		  inspinitem = false;
        	  }
        	  
        	  if(inradioitem){
        		  String[] radios;
        		  index = 0;
        		  // Initialise value
        		  
        		  rowhash.put("\""+radiokey+"\"", ""); //+index);
        		  //radios = radioshash.get(radiokey); 

        		  //value = new String(ch, start, length);
        		  //for(String val : radios){
        			//  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  rowhash.put("\""+radiokey+"\"", value); //""+index);
        				  if(listradios != null && listradios.contains(radiokey))
        		    			title += "- "+ value + " ";
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			  //break;
        			 // }
        			  //index++;
        		 // }
        		  
        		  inradioitem = false;
        	  }
        	  
        	  String[] tempstring, tempstring2;
       		
              for(String key : checkboxgroups){
          		//xkey = key.replaceAll("\\s+", "_");
          		try{
          			tempstring = checkboxhash.get(key);
              		for(String box : tempstring){
              			tempstring2 = value.split(",");
              			for(String box2 : tempstring2){
              				if(box2.equalsIgnoreCase(checkboxvaluesvalueshash.get(box))){
              					//row.checkboxes.put(box2, true);
              					//if(checkboxhash.get(box2) != null && checkboxhash.get(box2) == true)
              						rowhash.put(box, "1");
              					//else
              					//	rowhash.put(box2, "0");
              				}
              			}
              		}
          		}
          		catch(NullPointerException npe){
         			Log.i(getClass().getSimpleName(), "XML ERROR 1: "+ npe.toString());
         		}
          		} 
       		
        	  if(gettable){
    				tablename = new String(ch, start, length);
    				
    				if(orderedtablesrev.get(tablename) != null){ 
    					lasttablename = tablename;
    				
    					gettable = false;
    					//Log.i("XML TEXT 3: ", "TABLE = "+new String(ch, start, length));
    					getValues(tablename);
    					
    					// Ensure rowhash is cleared
    					rowhash.clear();
    					// Initialise checkbox values in hash
    					for(String key : checkboxgroups){
    	         			tempstring = checkboxhash.get(key);
    	         			for(String box : tempstring){
    	         				rowhash.put(box, "0");
    	         			}
    	         		}
    				
    					primary_keys.clear();
    					if(getKeyValue(tablename) != null && getValue(tablename, "ecpkey").length() > 0){
    						for(String key : (getValue(tablename, "ecpkey")).split(";")){
    							primary_keys.addElement(key);
    						}
    					}
    				
    					foreign_keys.clear();
    					if(orderedtablesrev.get(tablename) > 1){
    						String ftablename = orderedtables.get(orderedtablesrev.get(tablename) - 1);
    				    					
    						if(getKeyValue(ftablename) != null && getValue(ftablename, "ecpkey").length() > 0){
    							for(String key : (getValue(ftablename, "ecpkey")).split(";")){
    								foreign_keys.addElement(key);
    							}
    						}
    					}
    			 	}
        	  	}
          	}
          
          
        }
    
    
    
 // NEED TO UPDATE THIS FOR THE INDIVIDUAL TABLES
/*	boolean hasimages = false;
	String xmlproject;
	int phonekey;
	HashMap<String, Vector<HashMap<String, String>>> allrows = new HashMap<String, Vector<HashMap<String, String>>>();
	public int getRemoteData(String project, String table, String entry_selected_table, String entry, boolean local, boolean getimages, String sIMEI, String email){ //, String lat, String lon){  // ArrayList<Row>
		
		// Using these vectors doesn't increase speed at all
		//textviews2 = new Vector<String>();
    	//photos2 = new Vector<String>();
    	//videos2 = new Vector<String>();
    	//audio2 = new Vector<String>();
    	//spinners2 = new Vector<String>();
    	//radios2 = new Vector<String>();
    	//checkboxes2 = new Vector<String>();
    	//checkboxgroups2 = new Vector<String>();
    	//gpstags2 = new Vector<String>();    	
    	
		xmlproject = project;
		hasimages = false;
		//boolean result = false;
		
		InputStream xml_stream = null;
		
		LinkedHashMap<String, String> keyshash = getAllKeys();
		
		int keycount = 1;
		orderedtables = new HashMap<Integer, String>();
    	orderedtablesrev = new HashMap<String, Integer>();
        for(String key : keyshash.keySet()){
	    	orderedtables.put(keycount, key);
	    	orderedtablesrev.put(key, keycount);
	    	keycount++;
	    } 

		
		//String tableonly = "false";
		//if(gettableonly == true){
		//	tableonly = "true";
		//}
		
		//Log.i("GET REMOTE DATA URL", "http://www.doc.ic.ac.uk/~dmh1/Android/test_remote.xml?project="+project+"&table="+table);
    	try{
    		
    		URL url;
    		
    		String url_string;
    		
    		if(local)
    			url_string = getValue("local_remote_xml"); 
    		else
    			url_string = getValue("remote_xml");
    		    		 		
    		if(getimages){
    			url_string += "?project="+project+"&type=thumbnail"+"&table="+table+"&entry="+entry;
    		}
    		// If this comes from a table entry then "table" is the table the entry is from and entry_selected_table is the base table 
    		// from down to where entries should be retrieved
    		//http://test.mlst.net/SCORE/downloadFromServer.php?project=SCORE_FULL&type=data&select_table=Location&entry=Ke053&table=Village
    		// will download all of the data down to the location data for village Ke053. That means select_table is now the target table and has the same function as table in the first url.
    		else{
    			if(entry.length() > 0)
    				url_string += "?project="+project+"&type=data"+"&table="+entry_selected_table+"&entry="+entry+"&select_table="+table;
    			else
    				url_string += "?project="+project+"&type=data"+"&table="+table;
    			//Log.i("DATA DOWNLOAD ",url_string);
    		}
    		    	
    		url_string += "&phoneid="+sIMEI;
    		url_string += "&email="+email;
    		
    		Log.i("GET REMOTE DATA URL", url_string.toString());
        	    		
    		url = new URL(url_string);
    		
    		if(getimages){
    			
    			// Get the image zip file and uncompress it to the thumbs directory
    			final int BUFFER = 2048;
    			try {
    				
    				try {
    					URL zurl = new URL(url_string);
    			        URLConnection conn = zurl.openConnection();
    			        conn.setDoOutput(true);
    			        conn.setDoInput(true);
    			        conn.setRequestProperty("content-type", "binary/data");
    			        InputStream in = conn.getInputStream();
    			        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");

    			        byte[] b = new byte[1024];
    			        int count;

    			        while ((count = in.read(b)) >= 0) {
    			            out.write(b, 0, count);
    			        }
    			        out.close();
    			        in.close();

    			    } catch (IOException e) {
    			        e.printStackTrace();
    			    }

    			    FileOutputStream fos;
    			    byte data[] = new byte[1024];
     				
    				String picdir = Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + getProject();
    				try{
    			       	File f = new File(picdir);
    			    	if(!f.exists()){
    			    		f.mkdir();
    			    	}
    				}
        			catch(Exception e){
    			    	//Log.i("ImageSwitcher THUMBS ERROR", thumbdir);
    			    	
    			    }   				
    				
        			BufferedOutputStream dest = null;
        	         BufferedInputStream is = null;
        	         ZipEntry zentry;
        	         ZipFile zipfile = new ZipFile(Environment.getExternalStorageDirectory()+"/EpiCollect/temp.zip");
        	         Enumeration<?> e = zipfile.entries();
        	         while(e.hasMoreElements()) {
        	            zentry = (ZipEntry) e.nextElement();
        	            //System.out.println("Extracting: " +zentry);
        	            is = new BufferedInputStream
        	              (zipfile.getInputStream(zentry));
        	            int count;
        	            byte data2[] = new byte[BUFFER];
        	            String fullpath = zentry.getName();
     		            String filename = fullpath.replaceAll(".*[/\\\\]","");
     		            filename = filename.replaceFirst("tn_","");
    		            //fos = new FileOutputStream(picdir+"/"+filename);
        	            fos = new FileOutputStream(picdir+"/"+filename);
        	            dest = new BufferedOutputStream(fos, BUFFER);
        	            while ((count = is.read(data2, 0, BUFFER)) != -1) {
        	               dest.write(data2, 0, count);
        	            }
        	            dest.flush();
        	            dest.close();
        	            is.close();
        	         }

    		      } catch(Exception e) {
    		         e.printStackTrace();
    		         return 0;
    		      }
    		      return 2;
    		}
    		
    		else{
    			HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
    			urlc.setRequestMethod("GET");
    			urlc.connect();

    			xml_stream = urlc.getInputStream();
    			//if(xmlproject.equalsIgnoreCase("SCORE_FULL"))
    			//	xml_stream = fixXMLTags(xml_stream);
    			
    			BufferedWriter os = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			BufferedReader br = new BufferedReader(new InputStreamReader(xml_stream));
    			String line = null;
    			int counttable = 0, countentry = 0;
    			
    			int count = 0;
    			while ((line = br.readLine()) != null) {
    				line = line.replaceFirst("<9", "<XNINEX");
    				line = line.replaceFirst("</9", "</XNINEX");
    				if(line.contains("<table>"))
    					counttable++;
    				if(line.contains("<entry>"))
    					countentry++;
    				os.write(line);
    				os.write("\n");
    				//Log.i("XML", line+"\n");
    				count++;
    				if(countentry %100 == 0)
    					Log.i("XML", ""+countentry);
    				if(counttable > 1 && countentry > 10000){
    					br.close();
    	    			os.close();
        				return 3;
    				}
    			}
    			br.close();
    			os.close();
    			
    			try{
    				xml_stream = new FileInputStream(new File(Environment.getExternalStorageDirectory()+"/EpiCollect/xml_download.xml"));
    			}
    			catch(Exception e){
    				Log.i("XML ENTRY CHECK", e.toString());
    			}
    			
    			if(counttable > 1 && countentry > 10000)
    				return 3;
    			
    		}
    		
    	}
    	catch(MalformedURLException  ex){
    		System.out.println("1 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 1 "+ex.toString());
    		return 0;
    		}
    	catch (IOException ex) {
    		System.out.println("2 "+ex);
    		Log.i(getClass().getSimpleName(), "Failed 2 "+ex.toString());
    		return 0;
    		}
    	
    	try {
    		
    		// Clear all remote and synchronised data first to ensure synchronisation with remote server
    		// Records may have been deleted on server since phone last synchronised
    		if(entry.length() > 0){
    			//deleteAllRemRows(entry_selected_table, entry);
    			//deleteAllSynchRows(entry_selected_table, entry);
    		}
    		else{
    			//deleteAllRemRows(orderedtables.get(1), "None");
    			//deleteAllSynchRows(orderedtables.get(1), "None");
    		}
    		
    	      SAXParserFactory factory = SAXParserFactory.newInstance();
    	      SAXParser saxParser = factory.newSAXParser();
    	 
    	      XMLHandler handler = new XMLHandler();
    	      saxParser.parse(xml_stream, handler);   	 
    	         	 
    	    } catch (Exception e) {
    	      //e.printStackTrace();
    	      Log.i(getClass().getSimpleName(), "VIEWS ERROR "+e.toString());
    	      return 0;
    	    }
    	    //Log.i(getClass().getSimpleName(), "VIEWS "+views.toString());
    	    //System.out.println(views.toString());
    	    
    	    //createTable();
    	    
    	    //createAllRows(allrows);
    	    
    	  if(hasimages)
    	    return 1;
    	  else
    	    return 2;
	}
	
	String hashkey;
    public class XMLHandler extends DefaultHandler {
	   	 
    	private Vector<String> primary_keys = new Vector<String>(), foreign_keys = new Vector<String>();
        String templocation = "", tablename, lasttablename, value, spinkey = "", radiokey = "", title = "", primary_key="", foreign_key=""; // , xkey
        HashMap<String, String> rowhash = new HashMap<String, String>();
        //Row row;
        int index;
        boolean have_entry = false, record_stored = false,intextitem = false, inspinitem = false, inradioitem = false, incheckitem = false, ingpsitem = false, inphotoitem = false, invideoitem = false, inaudioitem = false, gettable = false, firstentry = true, indate = false;
        //InsertHelper ih;
        Vector<String> positionpos;
        
        public void startElement(String uri, String qName, String localName, Attributes attributes) throws SAXException {

        	//Log.i("XML QNAME: ", qName);  
         
        	String[] tempstring;
    		
    		
         if(qName.equalsIgnoreCase("table_name")){
     
        	 gettable = true;
        	 
        	 // If rowhash has data it needs to be stored
        	 if(have_entry){ //!rowhash.isEmpty()){
        		 rowhash.put("ecremote", ""+1);
        		 //ih.bind(positionpos.indexOf("ecremote"), ""+1);
        		 rowhash.put("ecstored", "R");
        		 //ih.bind(positionpos.indexOf("ecstored"), "R");
        		 // FOR SCORE LOCATION ID
        		 if(templocation.length() > 0){
        			 title += "- "+templocation;
        			 templocation = "";
        		 }
        		 
        		//phonekey = getMaxPhonekeyValue(lasttablename);
     	       	phonekey++;
     	       	
     	       //Log.i("HERE 1", ""+phonekey +" RES: "+getValue(lasttablename, "genkey"));
     	       
     	       	rowhash.put("phonekey", ""+phonekey);
     	       	//ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
     	    	if(getValue(lasttablename, "genkey") != null && getValue(lasttablename, "genkey").length() > 0){
     	    		title = "- " + phonekey + " "+ title; 
     	    		//Log.i("ADDING PHONEKEY", title);
     	        }
     	    	
        		 title = title.replaceFirst("- ", "");
        		 rowhash.put("title", title);
        		 //ih.bind(positionpos.indexOf("title"), title);
          	    
        		 primary_key = primary_key.replaceFirst(",", "");
        		 rowhash.put("pkey", primary_key);
        		 //ih.bind(positionpos.indexOf("pkey"), primary_key);
          	
        		 foreign_key = foreign_key.replaceFirst(",", "");
        		 rowhash.put("fkey", foreign_key);
        		 //ih.bind(positionpos.indexOf("fkey"), foreign_key);

        		 //if(!checkValue(lasttablename, "pkey", primary_key))
        		
     	       	createRow(lasttablename, rowhash);
     	       	//ih.execute();
        		//ih.close();
     	       countr++;
            	if(countr % 100 ==0)
            		Log.i("COUNT ROW", ""+countr);
          	
        		 	//rowhash = new HashMap<String, String>();
        		 rowhash.clear();
            	have_entry = false;
        		 //ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
        		 //ih.prepareForReplace();
        		 positionpos = getRowPositions(tablename);
        		 title = "";
        		 primary_key = "";
        		 foreign_key = "";
          	
        		 // Initialise all checkbox values to false
        		 //String[] tempstring;
        		 for(String key : checkboxgroups){
        			 tempstring = checkboxhash.get(key);
        			 for(String box : tempstring){
        				 rowhash.put(box, "0");
        			 }
        		 }
        		 
        		 //lasttablename = tablename;
          	
        		 // This is needed as if a table has no entries the only way
        		 // to store the last record from the previous table is to 
        		 // do it here
        		 // The recod_stored value is checked when "entry" is reached again
        		 record_stored = true;
        		 // tablename = attributes.getValue("table_name");
        		 // getValues(tablename);
        	 
        		 //row = new Row();
        		 // rowhash.clear();
        		 //Log.i("XML TABLE: ", tablename);
        		 // getitem = false;
        	 }

        	 //else{
        		 
        	 //}
        	// We are at the end of the phone XML tables
    		 if(orderedtablesrev.get(tablename) == null)
    			 return;
         }
         
         if(qName.equalsIgnoreCase("entry")){
        	 //getitem = true;
        	 if(firstentry){
        		 firstentry = false;
        		 lasttablename = tablename;
        	 }
        	 else if(record_stored == false){
        		
        		rowhash.put("ecremote", ""+1);
        		// NULL POINTER HERE
        		//Log.i("POSITION", ""+positionpos.indexOf("ecremote"));
        		//ih.bind(positionpos.indexOf("ecremote"), ""+1);
             	rowhash.put("ecstored", "R");
             	//Log.i("POSITION", ""+positionpos.indexOf("ecstored"));
             	//ih.bind(positionpos.indexOf("ecstored"), "R");
             	
             // FOR SCORE LOCATION ID
       		 if(templocation.length() > 0){
       			 title += templocation;
       			 templocation = "";
       		 }
       		 
       		//phonekey = getMaxPhonekeyValue(lasttablename);
       		phonekey++;
 	       	
 	       //Log.i("HERE 2", ""+phonekey);
 	       
 	       	rowhash.put("phonekey", ""+phonekey);
 	        //ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
 	    	if(getValue(lasttablename, "genkey") != null && getValue(lasttablename, "genkey").length() > 0){
 	    		title = "- " + phonekey + " "+ title; 
 	    		//Log.i("ADDING PHONEKEY", title);
 	        }
 	    	
             	title = title.replaceFirst("- ", "");
             	rowhash.put("title", title);
             	//ih.bind(positionpos.indexOf("title"), title);
             	    
             	primary_key = primary_key.replaceFirst(",", "");
             	rowhash.put("pkey", primary_key);
             	//ih.bind(positionpos.indexOf("pkey"), primary_key);
             	
             	foreign_key = foreign_key.replaceFirst(",", "");
             	rowhash.put("fkey", foreign_key);
             	//ih.bind(positionpos.indexOf("fkey"), foreign_key);
             	//Log.i("XML TABLE:",lasttablename); 
             	//for(String r : rowhash.keySet())
             	//	Log.i("XML TABLE: "+r, rowhash.get(r));

             	//if(!checkValue(lasttablename, "pkey", primary_key))
             	//Log.i("CREATING TABLE: ", tablename);
     	       	
             	createRow(lasttablename, rowhash);
             	
             	//ih.execute();
             	//ih.close();
             	countr++;
             	if(countr % 100 ==0)
             		Log.i("COUNT ROW", ""+countr);
 
             	
    		 	rowhash = new HashMap<String, String>();
             	rowhash.clear();
             	have_entry = false;
             	//ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
             	//ih.prepareForReplace();
             	positionpos = getRowPositions(tablename);
             	title = "";
             	primary_key = "";
             	foreign_key = "";
             	
             // Initialise all checkbox values to false
        		// NEED THIS IF ROWHASH IS USED
        		for(String key : checkboxgroups){
        			tempstring = checkboxhash.get(key);
        			for(String box : tempstring){
        				rowhash.put(box, "0");
        				//ih.bind(positionpos.indexOf(box), "0");
        			}
        		}
             	
             	lasttablename = tablename;
             	//intextitem = false;
             	//inspinitem = false;
             	//incheckitem = false;
             	//ingpsitem = false;
             	//inphotoitem = false;
             	//invideoitem = false;
             	//inaudioitem = false;
             	//indate = false;
        	 }
        	 record_stored = false;
        	 
         }
         
         if(qName.equalsIgnoreCase("ecdate")){
        	 indate = true;
         }

          for(String key : textviews){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			
			//Log.i("XML TABLE CHECK 2: ", xkey+" "+qName);
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 intextitem = true;
	        	 //Log.i("XML TABLE CHECK: ", key);
	        	 break;
	         }
				
         } 
         
          for(String key : photos){
        	 //Log.i("XML PHOTO CHECK: ", key);
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			
			//Log.i("XML PHOTO CHECK: ", xkey);
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inphotoitem = true;
	        	 break;
	         }
				
         } 
     
         
          for(String key : videos){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 invideoitem = true;
	        	 break;
	         }
				
         } 
         
          for(String key : audio){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
        	 
			if(qName.equalsIgnoreCase(key)){
				hashkey = key; //xkey;
	        	 inaudioitem = true;
	        	 break;
	         }
				
         } 
         
           for(String key : gpstags){
        	 // This can now be removed
			//xkey = key.replaceAll("\\s+", "_");
			//hashkey = key;
			//Log.i("XML TABLE CHECK 2: ", xkey+" "+qName);
			if(qName.equalsIgnoreCase(key+"_lat")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lat";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_lon")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_lon";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_alt")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_alt";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_acc")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_acc";
	        	 break;
	         }
			else if(qName.equalsIgnoreCase(key+"_provider")){
	        	 ingpsitem = true;
	        	 hashkey = key+"_provider";
	        	 break;
	         }
				
         } 
         
          for(String key : spinners){
        	//xkey = key.replaceAll("\\s+", "_");
     		if(qName.equalsIgnoreCase(key)){
     			spinkey = key;
     			inspinitem = true;
     			//Log.i("XML TABLE CHECK: ", xkey);
     			break;
     		}    		
     	} 
         
       	 for(String key : radios){
         	
      		//xkey = key.replaceAll("\\s+", "_");
        	String xkey = key;
        	
        	if(xmlproject.equalsIgnoreCase("SCORE_FULL")){
        		xkey = xkey.replaceFirst("9", "XNINEX");
        	} 
      		
      		if(qName.equalsIgnoreCase(xkey)){
      			radiokey = key;
      			inradioitem = true;
      			//Log.i("XML TABLE CHECK: ", xkey);
      			break;
      		}    		
      	} 
         
           for(String key : checkboxgroups){
        	//checkkey = key;
     		//xkey = key.replaceAll("\\s+", "_");
     		if(qName.equalsIgnoreCase(key)){
     			incheckitem = true;
     			//Log.i("XML TABLE CHECK: ", xkey);
     			break;
     		}    		
         } 
     		
     		//rowhash.put("ecremote", ""+1);
        	//rowhash.put("ecstored", "R");

        	///////createRow(tablename, rowhash);
        	
        	//rowhash.clear();
         } 


        

        public void endElement(String uri, String qName, String localName) throws SAXException {

        	//Log.i("End Element :", qName);
        	if(qName.equalsIgnoreCase("entries") && have_entry){ //!rowhash.isEmpty()){
        		rowhash.put("ecremote", ""+1);
        		//ih.bind(positionpos.indexOf("ecremote"), ""+1);
           		rowhash.put("ecstored", "R");
           		//ih.bind(positionpos.indexOf("ecstored"), "R");
           		
           		//int phonekey = getMaxPhonekeyValue(tablename);
     	       	phonekey++;
     	       	
     	       //Log.i("HERE 3", ""+phonekey);
     	       	rowhash.put("phonekey", ""+phonekey);
     	        //ih.bind(positionpos.indexOf("phonekey"), ""+phonekey);
     	    	if(getValue(tablename, "genkey") != null && getValue(tablename, "genkey").length() > 0){
     	    		title = "- " + phonekey + " "+ title; 
     	    		//Log.i("ADDING PHONEKEY", title);
     	        }
     	    	
           		title = title.replaceFirst("- ", "");
           		rowhash.put("title", title);
           		//ih.bind(positionpos.indexOf("title"), title);
           		
           		primary_key = primary_key.replaceFirst(",", "");
             	rowhash.put("pkey", primary_key);
             	//ih.bind(positionpos.indexOf("pkey"), primary_key);
             	
             	foreign_key = foreign_key.replaceFirst(",", "");
             	rowhash.put("fkey", foreign_key);
             	//ih.bind(positionpos.indexOf("fkey"), foreign_key);
     	       	
           		createRow(tablename, rowhash);
           		//ih.execute();
             	//ih.close();
           		countr++;
             	if(countr % 100 ==0)
             		Log.i("COUNT ROW", ""+countr);
    		 	
    		 	rowhash = new HashMap<String, String>();
    		 	rowhash.clear();
    		 	have_entry = false;
    		 	//ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
    		 	//ih.prepareForReplace();
    		 	positionpos = getRowPositions(tablename);
    		 	title = "";
    		 	primary_key = "";
    		 	foreign_key = "";
    		 	
    		 	String[] tempstring;
    		 // Initialise checkbox values in hash
				for(String key : checkboxgroups){
         			tempstring = checkboxhash.get(key);
         			for(String box : tempstring){
         				rowhash.put(box, "0");
         			}
         		}
          }

       }
          
          public void characters(char ch[], int start, int length) throws SAXException {
	          	
        	  String value = new String(ch, start, length);
        	  if(length == 0 || value.equalsIgnoreCase("N/A"))
        		  value = "";
        	  
        	  if(indate){
        		  rowhash.put("\"ecdate\"", value);
        		  have_entry = true;
        		  //ih.bind(positionpos.indexOf("ecdate"), value);
        		  indate = false;
        	  }
        	  
        	  if(intextitem){
        		//if(value.equalsIgnoreCase("N/A"))
        		//	value = "";
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
  				//ih.bind(positionpos.indexOf(hashkey), value);
  				if(value.length() > 0 && listfields != null && listfields.contains(hashkey)){
  					// FOR SCORE
  					if(hashkey.equalsIgnoreCase("Location_ID"))
  						templocation = value;
  					else
  						title += "- "+ value + " ";
  				}
  				if(primary_keys.contains(hashkey)){
					primary_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				if(foreign_keys.contains(hashkey)){
					foreign_key += ","+hashkey+","+value;
					//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
		    		}
  				//Log.i("XML TEXT 2: ", tablename+" "+hashkey+" "+value);
  				intextitem = false;
        	  }
        	  
        	  if(ingpsitem){
    				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
    				have_entry = true;
    				//ih.bind(positionpos.indexOf(hashkey), value);
    				ingpsitem = false;
          	  }
        	  
        	  if(inphotoitem){
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
  				  //ih.bind(positionpos.indexOf(hashkey), value);
  				inphotoitem = false;
  				// If there is an image it needs to be downloaded
  				if(value.length() > 2)
  					hasimages = true;
        	  }
        	  
        	  if(invideoitem){
    				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
    				//ih.bind(positionpos.indexOf(hashkey), value);
    				invideoitem = false;
          	  }
        	  
        	  if(inaudioitem){
  				rowhash.put("\""+hashkey+"\"", value); //new String(ch, start, length));
        		  have_entry = true;
  				//ih.bind(positionpos.indexOf(hashkey), value);
  				inaudioitem = false;
        	  }
        	  
        	  if(inspinitem){
        		  String[] spinners;
        		  index = 0;
        		  // Initialise value
        		  
        		  rowhash.put("\""+spinkey+"\"", ""+index);
        		  have_entry = true;
        		 // ih.bind(positionpos.indexOf(spinkey), ""+index);
        		  spinners = spinnershash.get(spinkey); 

        		  //value = new String(ch, start, length);
        		  for(String val : spinners){
        			  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  rowhash.put("\""+spinkey+"\"", ""+index);
        				  have_entry = true;
        				  //ih.bind(positionpos.indexOf(spinkey), ""+index);
        				  if(listspinners != null && listspinners.contains(spinkey))
        		    			title += "- "+ value + " ";
        				  if(primary_keys.contains(spinkey)){
        						primary_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        	  			  if(foreign_keys.contains(spinkey)){
        					    foreign_key += ","+spinkey+","+value;
        						//Log.i("XML PRIMARY KEY: ", tablename+" "+primary_key);
        			    		}
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			  break;
        			  }
        			  index++;
        		  }
        		  
        		  inspinitem = false;
        	  }
        	  
        	  if(inradioitem){
        		  String[] radios;
        		  index = 0;
        		  // Initialise value
        		  
        		  rowhash.put("\""+radiokey+"\"", ""+index);
        		  have_entry = true;
        		  //ih.bind(positionpos.indexOf(radiokey), ""+index);
        		  radios = radioshash.get(radiokey); 

        		  //value = new String(ch, start, length);
        		  for(String val : radios){
        			  if(val.equalsIgnoreCase(value)){
        				  //row.spinners.put(value, index);
        				  rowhash.put("\""+radiokey+"\"", ""+index);
        				  have_entry = true;
        				  //ih.bind(positionpos.indexOf(radiokey), ""+index);
        				  if(listradios != null && listradios.contains(radiokey))
        		    			title += "- "+ value + " ";
        				  //Log.i("XML SPINNER: ", xkey+" "+new String(ch, start, length));
	          			  break;
        			  }
        			  index++;
        		  }
        		  
        		  inradioitem = false;
        	  }
        	  
        	  String[] tempstring, tempstring2;
       		
              for(String key : checkboxgroups){
          		//xkey = key.replaceAll("\\s+", "_");
          		try{
          			tempstring = checkboxhash.get(key);
              		for(String box : tempstring){
              			tempstring2 = value.split(",");
              			for(String box2 : tempstring2){
              				if(box2.equalsIgnoreCase(checkboxvaluesvalueshash.get(box))){
              					//row.checkboxes.put(box2, true);
              					//if(checkboxhash.get(box2) != null && checkboxhash.get(box2) == true)
              						rowhash.put(box, "1");
              						have_entry = true;
              						//ih.bind(positionpos.indexOf(box), "1");
              					//else
              					//	rowhash.put(box2, "0");
              				}
              			}
              		}
          		}
          		catch(NullPointerException npe){
         			Log.i(getClass().getSimpleName(), "XML ERROR 1: "+ npe.toString());
         		}
          		} 
       		
        	  if(gettable){
    				tablename = new String(ch, start, length);
    				
    				if(orderedtablesrev.get(tablename) != null){ 
    					lasttablename = tablename;
    					    					  					
    					gettable = false;
    					//Log.i("XML TEXT 3: ", "TABLE = "+new String(ch, start, length));
    					getValues(tablename);
    					//getValues2(tablename);
    					
    					phonekey = getMaxPhonekeyValue(tablename);
    					
    					// Ensure rowhash is cleared
    					rowhash.clear();
    					have_entry = false;
    					//getRowPositions(tablename);
    					// CAUSES NULL POINTER EXCEPTION
    					//ih.close();
    					//ih = new InsertHelper(db, "data_"+DATABASE_PROJECT+"_"+tablename);
    					//ih.prepareForReplace();
    					positionpos = getRowPositions(tablename);
    					// Initialise checkbox values in hash
    					// NEED THIS IF ROWHASH USED
    					for(String key : checkboxgroups){
    	         			tempstring = checkboxhash.get(key);
    	         			for(String box : tempstring){
    	         				rowhash.put(box, "0");
    	         				//ih.bind(positionpos.indexOf(box), "0");
    	         			}
    	         		}
    				
    					primary_keys.clear();
    					if(getKeyValue(tablename) != null && getValue(tablename, "pkey").length() > 0){
    						for(String key : (getValue(tablename, "pkey")).split(",")){
    							primary_keys.addElement(key);
    						}
    					}
    				
    					foreign_keys.clear();
    					if(orderedtablesrev.get(tablename) > 1){
    						String ftablename = orderedtables.get(orderedtablesrev.get(tablename) - 1);
    				    					
    						if(getKeyValue(ftablename) != null && getValue(ftablename, "pkey").length() > 0){
    							for(String key : (getValue(ftablename, "pkey")).split(",")){
    								foreign_keys.addElement(key);
    							}
    						}
    					}
    			 	}
        	  	}
          	}
          
          
        } */
    
    
    
    
}
