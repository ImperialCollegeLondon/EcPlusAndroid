/* 
 * 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.imperial.epi_collect2.media.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.imperial.epi_collect2.Epi_collect;
import uk.ac.imperial.epi_collect2.R;
import uk.ac.imperial.epi_collect2.util.db.DBAccess;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

@SuppressLint("NewApi")
public class ImageSwitcher_epi_collect extends Activity implements
AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory { 

	private static final int SELECT_PICTURE = 5;
    private String selectedImagePath;
    private String filemanagerstring;

private static final int DELETE_ID = 1; //Menu.FIRST;
private static final int SELECT_ID = 2; //Menu.FIRST + 1;
private static final int SYNCH_IMAGES_ID = 3; //Menu.FIRST + 1;
private static final int CAP_PHOTO_ID = 4;
private static final int ACTIVITY_CAP_PHOTO=1;
private static final String KEY_PHOTO = "PHOTO_ID";
private Integer photoid = -1;
private String imagefile = "0", date;
//private Gallery g;
private static String thumbdir; 
private static String picdir; 
ButtonListener myOnClickListener = new ButtonListener();
boolean havesdcard = true;
private DBAccess dbAccess;
//private boolean gallery = true;
private String existing_photoid = "0", photoview_id = "", photo_refid = "", photo_table = "", sIMEI = "", email = "", gallery = "1";

@Override
public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    dbAccess = new DBAccess(this); 
    dbAccess.open();
    
    thumbdir = Epi_collect.appFiles+"/"+dbAccess.getProject()+"/thumbs"; //Environment.getExternalStorageDirectory()+"/EpiCollect/thumbs_epicollect_" + dbAccess.getProject(); //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
    picdir = Epi_collect.appFiles+"/"+dbAccess.getProject()+"/images"; //Environment.getExternalStorageDirectory()+"/EpiCollect/picdir_epicollect_" + dbAccess.getProject(); //this.getResources().getString(this.getResources().getIdentifier(this.getPackageName()+":string/project", null, null));
    
    Log.i("ImageSwitcher THUMBS", thumbdir);
    
    try{
       	File f = new File(thumbdir);
    	if(!f.exists()){
    		f.mkdir();
    	}
    	f = new File(picdir);
    	if(!f.exists())
    		f.mkdir();
    	}
    catch(Exception e){
    	Log.i("ImageSwitcher THUMBS ERROR", thumbdir+" "+e.toString());
    	havesdcard = false;
    	showAlert(this.getResources().getString(R.string.photo_card_error)); //"SD card not present. Required for photo capture");
    }
    
    setContentView(R.layout.imageswitcher_epi_collect);

    mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
    mSwitcher.setFactory(this);
    mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
            android.R.anim.fade_in));
    mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
            android.R.anim.fade_out)); 

  //  g = (Gallery) findViewById(R.id.gallery);
  //  g.setAdapter(new ImageAdapter(this));
  //  g.setOnItemSelectedListener(this);
	
    Bundle extras = getIntent().getExtras();
    
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
   	
   	date = ""+cal.getTimeInMillis();
    //date = extras.getString("date");
    
   	getCount();
   	
    //Log.i("ImageSwitcher GALLERY", extras.getString("GALLERY"));
   	if (extras != null && extras.getString("PHOTO_ID") != null){
   		existing_photoid = extras.getString("PHOTO_ID");
   		//photoview_id = extras.getString("PHOTOVIEW_ID");
   		photo_refid = extras.getString("PHOTO_REF_ID");
   		photo_table = extras.getString("PHOTO_TABLE");
   	}
   	
   	if (extras != null && extras.getString("GALLERY") != null){
    	if(extras.getString("GALLERY").equalsIgnoreCase("1")) {
    		//gallery = false;
    		gallery = "1";
    		captureImage();
    	}
    	else if(extras.getString("GALLERY").equalsIgnoreCase("0")) {
    		//gallery = false;
    		gallery = "0";
    		selectImage();
    	}
   	}
    	
    //if (extras != null && extras.getString("GALLERY") != null && extras.getString("GALLERY").equalsIgnoreCase("0")) {
    //	gallery = false;
    //	captureImage();
    //}
    
    Account[] accounts = AccountManager.get(this).getAccounts();
	for (Account account : accounts) {
	  //String possibleEmail = account.name;
	  if(account.type.equalsIgnoreCase("com.google") && account.name.contains("@gmail.com")){
		  email = account.name;
	  }
	}
	
	TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
 	sIMEI = mTelephonyMgr.getDeviceId();
    
    
}

public void showAlert(String result){
	new AlertDialog.Builder(this)
    .setTitle(R.string.error) //"Error")
    .setMessage(result)
    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {

         public void onClick(DialogInterface dialog, int whichButton) {
        	 if(!havesdcard)
        		 endCamera();
         }
    }).show();	
}

private void endCamera(){
	setResult(RESULT_OK, this.getIntent());
	dbAccess.close();
	finish();  
 }

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete_photo);
    menu.add(0, SELECT_ID, 0, R.string.menu_select);
    //menu.add(0, SYNCH_IMAGES_ID, 0, R.string.menu_photo_synch);
    if(havesdcard)
    	menu.add(0, CAP_PHOTO_ID, 0, R.string.menu_photo);
    return true;
}

@Override
public boolean onMenuItemSelected(int featureId, MenuItem item) {
    super.onMenuItemSelected(featureId, item);
    switch(item.getItemId()) {
    case DELETE_ID:
    	AlertDialog dialog = new AlertDialog.Builder(this).create();

		dialog.setMessage(this.getResources().getString(R.string.delete_picture)); //"Delete Picture?");
		dialog.setButton(this.getResources().getString(R.string.yes), myOnClickListener);
		dialog.setButton2(this.getResources().getString(R.string.no), myOnClickListener);

		dialog.show();
		break;
    case SELECT_ID:
    	Bundle extras = getIntent().getExtras();
        
    	String photo = "-1";
    	if(photoid > -1){
    		Pattern pattern = Pattern.compile("(\\d+\\.jpg)");
            Matcher matcher = pattern.matcher(mImageIds[photoid].toString());
            if(matcher.find())
            {
            	photo = matcher.group(1);
            }
    		extras.putString(KEY_PHOTO, photo); //mImageIds[photoid].toString());
    	}
    	else
    		extras.putString(KEY_PHOTO, "-1");
    	this.getIntent().putExtras(extras);
        setResult(RESULT_OK, this.getIntent());
        dbAccess.close();
        finish();
    	break;
    case CAP_PHOTO_ID:
    	captureImage();
   
		break;
		  
	case SYNCH_IMAGES_ID:
		synchImages();
		break;
    }
    return true;
}

final String picture_deleted = this.getResources().getString(R.string.picture_deleted); 
class ButtonListener implements OnClickListener{
	public void onClick(DialogInterface dialog, int i) {
		switch (i) {
		case AlertDialog.BUTTON1:
		/* Button1 is clicked. Do something */
			String fileloc = mImageIds[photoid].toString();
	    	File f = new File(fileloc);
	    	f.delete();
	    	fileloc = fileloc.replaceFirst("thumbs", "pictures");
	    	f = new File(fileloc);
	    	try{
	    		f.delete();
	    	}
	    	catch (Exception e) {
	    		Log.v( getClass().getSimpleName(), "KEY PHOTO 2"+ fileloc);
	    	}
			
			showToast(picture_deleted); //"Picture deleted");
			onResume();
		break;
		case AlertDialog.BUTTON2:
		/* Button2 is clicked. Do something */
		break;
		}
	}
  }

	private void synchImages(){
		showAlert(dbAccess.uploadAllImages(sIMEI, email));
	}

private void showToast(String text){
	Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
}

@Override
 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   	super.onActivityResult(requestCode, resultCode, data);
   	//Log.i(getClass().getSimpleName(), "VIEW: HERE 2"); 
   	
   	if (requestCode == SELECT_PICTURE) {
   		try{
   			Uri selectedImageUri = data.getData();

   			//OI FILE Manager
   			filemanagerstring = selectedImageUri.getPath();

   			//MEDIA GALLERY
   			selectedImagePath = getPath(selectedImageUri);

   			//DEBUG PURPOSE - you can delete this if you want
   			//if(selectedImagePath!=null)
   			//    System.out.println(selectedImagePath);
   			//else System.out.println("selectedImagePath is null");
   			//if(filemanagerstring!=null)
   			//    System.out.println(filemanagerstring);
   			//else System.out.println("filemanagerstring is null");

   			String fpath = "";
   			//NOW WE HAVE OUR WANTED STRING
   			if(selectedImagePath!=null){
   				System.out.println("selectedImagePath is the right one for you!");
   				fpath = selectedImagePath;
   			}
   			else{
   				System.out.println("filemanagerstring is the right one for you!");
   				fpath = filemanagerstring;
   			}
        
   			if(fpath.length() == 0){
   				Bundle extras = getIntent().getExtras();
   				extras.putString(KEY_PHOTO, "-1");
   				this.getIntent().putExtras(extras);
   				setResult(RESULT_OK, this.getIntent());
   				dbAccess.close();
   				finish();
   			}
   			else{
   				existing_photoid = existing_photoid.replaceAll("\\s+", "");
   				if(existing_photoid.equalsIgnoreCase("0") || existing_photoid.length() == 0){
   					TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
   					imagefile = photo_table+"_"+photo_refid+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".jpg";
   				}
   				else
   					imagefile = existing_photoid;
   				copyFile(new File(fpath), new File(Epi_collect.appFiles, "temp.jpg"));
   			}
   		}
   		catch(NullPointerException npe){
   			Bundle extras = getIntent().getExtras();
			extras.putString(KEY_PHOTO, "-1");
	    	this.getIntent().putExtras(extras);
            setResult(RESULT_OK, this.getIntent());
            try{
            	dbAccess.close();
            }
            catch(NullPointerException npe2){}
            finish();
   		}
    }

   	File file = new File(Epi_collect.appFiles, "temp.jpg");
   	
 // If cancel on camera pressed
	if(!file.exists()){
		
		//if(!gallery){
			Bundle extras = getIntent().getExtras();
			extras.putString(KEY_PHOTO, "-1");
	    	this.getIntent().putExtras(extras);
            setResult(RESULT_OK, this.getIntent());
            try{
            	dbAccess.close();
            }
            catch(NullPointerException npe){}
            finish();
		//}
	//return;
	}
   	
	BitmapFactory.Options options = new BitmapFactory.Options();
	options.inSampleSize = 8;
	Bitmap bmp = BitmapFactory.decodeFile(Epi_collect.appFiles+"/temp.jpg", options);

	int w = bmp.getWidth();
	int h = bmp.getHeight();
	//if((w > h && w > 2048) || ( h > w && h > 2048)){
	if(w > 2048 || h > 2048){
		resizeImage(Epi_collect.appFiles+"/temp.jpg", picdir+"/"+imagefile, 2048, 1536);
	}
	else{
		copyFile(file, new File(picdir+"/"+imagefile));
	}
	
   	try{
   		file.delete();
   	}
   	catch (Exception e){
   		
   	}

   	resizeImage(picdir+"/"+imagefile, thumbdir+"/"+imagefile, 512, 384);
   	
   /* try {
    	// load the original BitMap (500 x 500 px)
    	// This sometimes causes out of memory errors
    	//Bitmap bmp = BitmapFactory.decodeFile(picdir+"/"+imagefile);
    	
    	// Used this instead
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inSampleSize = 8;
    	Bitmap bmp = BitmapFactory.decodeFile(picdir+"/"+imagefile, options);

    				
    	int width = bmp.getWidth();
    	int height = bmp.getHeight();
    	int newWidth, newHeight;
    		        
    	if(width > height){
    	  	newWidth = 512;
    	   	newHeight = 384;
    	}
    	else{
    	  	newWidth = 384;
    	   	newHeight = 512;
    	}
    		       
    	// calculate the scale - in this case = 0.4f
    	float scaleWidth = ((float) newWidth) / width;
    	float scaleHeight = ((float) newHeight) / height;
    		       
    	// create a matrix for the manipulation
    	Matrix matrix = new Matrix();
    	// resize the bit map
    	matrix.postScale(scaleWidth, scaleHeight);
    	// rotate the Bitmap
    	//matrix.postRotate(45);

    	// recreate the new Bitmap
    	Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true); 
    	FileOutputStream out = new FileOutputStream(thumbdir+"/"+imagefile);//this.openFileOutput("ping_media.jpg",MODE_PRIVATE);
    	resizedBitmap.compress(CompressFormat.JPEG, 50, out) ;
    	out.close() ;
    	
    	// Free up the memory used by the bitmaps
    	// Without this can get out of memory exception when 
    	// subsequent photo taken
    	bmp.recycle();
    	resizedBitmap.recycle();
    	
    	//media_path = "/sdcard/dcim/.thumbnails/" ;
    	} catch (FileNotFoundException e) {
    		Log.i("ImageSwitcher","FileNotFoundException generated when using camera") ;
    	} catch (IOException e) {
    		Log.i("ImageSwitcher","IOException generated when using camera") ;
    	} */

    		//if(!gallery){
    			Bundle extras = getIntent().getExtras();
   	    		extras.putString(KEY_PHOTO, imagefile);
     	    	this.getIntent().putExtras(extras);
                setResult(RESULT_OK, this.getIntent());
                //dbAccess.close();
                finish();
    		//}
} 


private void resizeImage(String oldimage, String newimage, int w, int h){
	try {
		// load the original BitMap (500 x 500 px)
		// This sometimes causes out of memory errors
		//Bitmap bmp = BitmapFactory.decodeFile(picdir+"/"+imagefile);
	
		// Used this instead
	
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap bmp = BitmapFactory.decodeFile(oldimage, options);

				
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int newWidth, newHeight;
		        
		if(width > height){
			newWidth = w;
			newHeight = h;
		}
		else{
			newWidth = h;
			newHeight = w;
		}
		       
		// calculate the scale - in this case = 0.4f
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		       
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// rotate the Bitmap
		//matrix.postRotate(45);

		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true); 
		FileOutputStream out = new FileOutputStream(newimage);//this.openFileOutput("ping_media.jpg",MODE_PRIVATE);
		resizedBitmap.compress(CompressFormat.JPEG, 50, out) ;
		out.close() ;
	
		// Free up the memory used by the bitmaps
		// Without this can get out of memory exception when 
		// subsequent photo taken
		bmp.recycle();
		resizedBitmap.recycle();
	
		//media_path = "/sdcard/dcim/.thumbnails/" ;
		} catch (FileNotFoundException e) {
			Log.i("ImageSwitcher","FileNotFoundException generated when using camera") ;
		} catch (IOException e) {
			Log.i("ImageSwitcher","IOException generated when using camera") ;
		}
}



public String getPath(Uri uri) {
    String[] projection = { MediaStore.Images.Media.DATA };
    Cursor cursor = managedQuery(uri, projection, null, null, null);
    if(cursor!=null)
    {
        //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
        //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
        int column_index = cursor
        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    else return null;
}

private void selectImage(){  
	   // in onCreate or any event where your want the user to
// select a file
Intent intent = new Intent();
intent.setType("image/*");
intent.setAction(Intent.ACTION_GET_CONTENT);
startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
}
    
private void captureImage(){
	//Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	
	/*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(FILEPATH));
	startActivityForResult(intent, 0); */

	//long pic_num = 0;
	//java.util.Date date = new java.util.Date();
	//pic_num = date.getTime();
	
	//Log.i("EXISTING", existing_photoid);
	
	existing_photoid = existing_photoid.replaceAll("\\s+", "");
	if(existing_photoid.equalsIgnoreCase("0") || existing_photoid.length() == 0){
		TelephonyManager mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		imagefile = photo_table+"_"+photo_refid+"_"+mTelephonyMgr.getDeviceId()+ "_"+date+".jpg";
	}
	else
		imagefile = existing_photoid;
	
	//Log.i("IMAGEFILE", imagefile);
	
	//imagefile = pic_num-1;
	photoid = mImageIds.length;

	File file = new File(Epi_collect.appFiles, "temp.jpg");
	
	Intent imageCaptureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
	imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
	//startActivityForResult(intent, 0);

	//imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); //Uri.fromFile(new File(picdir, +pic_num+".jpg")));
    startActivityForResult(imageCaptureIntent, ACTIVITY_CAP_PHOTO);
    
}

@SuppressWarnings("unchecked")
public void onItemSelected(AdapterView parent, View v, int position, long id) {
	photoid = position;
	mSwitcher.setImageURI(mImageIds[position]);
}

@SuppressWarnings("unchecked")
public void onNothingSelected(AdapterView parent) {
}

public View makeView() {
    ImageView i = new ImageView(this);
    i.setScaleType(ImageView.ScaleType.FIT_CENTER);
    i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
            LayoutParams.FILL_PARENT));
    return i;
} 

@Override
protected void onPause() {
    super.onPause();
    //storeData(0);
    dbAccess.close();
    dbAccess = null;
}
    
// Updates gallery if camera has been used
@Override
protected void onResume() {
    super.onResume();
    if (dbAccess == null) {
    	dbAccess = new DBAccess(this);
    	dbAccess.open();
    }
    
   // g.setAdapter(new ImageAdapter(this));
   // g.setSelection(mImageIds.length-1, true);
    photoid = mImageIds.length-1;
} 

public int getCount() {
	
	if(!havesdcard)
		return 0;
	
	File images = new File(thumbdir+"/"); // Environment.getExternalStorageDirectory();
	File[] imagelist = images.listFiles(new ImageFilter());
    
	mFiles = new String[imagelist.length];

	for(int i= 0 ; i< imagelist.length; i++){
		//Log.i(getClass().getSimpleName(), "Image List Length "+imagelist.length+ " PATH "+imagelist[i].getAbsolutePath());
		mFiles[i] = imagelist[i].getAbsolutePath();
	}
	mImageIds = new Uri[mFiles.length];

	for(int i=0; i < mFiles.length; i++){
		mImageIds[i] = Uri.parse(mFiles[i]);   
	}	

	//if(mImageIds.length == 0){
	//	String text = "No images available. Use Capture Photo menu option to take picture";
	//	Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show(); 
	//}
	photoid = mImageIds.length; // pic_num
    return mImageIds.length;
}

@Override  
protected void onSaveInstanceState(Bundle outState) {  
	super.onSaveInstanceState(outState);  
	
	outState.putString("PHOTO_ID", existing_photoid);
	outState.putString("PHOTO_REF_ID", photo_refid);
	outState.putString("PHOTO_TABLE", photo_table);
	outState.putString("GALLERY", gallery);   	
}

@Override
public void onRestoreInstanceState(Bundle savedInstanceState) {
  super.onRestoreInstanceState(savedInstanceState);
}


private ImageSwitcher mSwitcher;
private Uri[] mImageIds; // Integer[] 
private String[] mFiles= null;
private Context mContext;

class ImageFilter implements FilenameFilter
{
	public boolean accept(File dir, String name)
	{
		return (name.endsWith(".jpg"));
	}
}

String no_images = this.getResources().getString(R.string.no_images);
public class ImageAdapter extends BaseAdapter {
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
    	
    	if(!havesdcard)
    		return 0;
    	
		File images = new File(thumbdir+"/"); // Environment.getExternalStorageDirectory();
		File[] imagelist = images.listFiles(new ImageFilter());
        
		mFiles = new String[imagelist.length];

		for(int i= 0 ; i< imagelist.length; i++){
			//Log.i(getClass().getSimpleName(), "Image List Length "+imagelist.length+ " PATH "+imagelist[i].getAbsolutePath());
			mFiles[i] = imagelist[i].getAbsolutePath();
		}
		mImageIds = new Uri[mFiles.length];

		for(int i=0; i < mFiles.length; i++){
			mImageIds[i] = Uri.parse(mFiles[i]);   
		}	

		if(mImageIds.length == 0){
			String text = no_images; //"No images available. Use Capture Photo menu option to take picture";
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show(); 
		}
		photoid = mImageIds.length; // pic_num
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = new ImageView(mContext);

        i.setImageURI(mImageIds[position]);
        i.setAdjustViewBounds(true);
        i.setLayoutParams(new Gallery.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        i.setBackgroundResource(android.R.drawable.picture_frame);

        return i;
    }

    public float getAlpha(boolean focused, int offset) {
        return Math.max(0.2f, 1.0f - (0.2f * Math.abs(offset)));
    }

    public float getScale(boolean focused, int offset) {
        return Math.max(0, offset == 0 ? 1.0f : 0.6f);
    }

}

private static void copyFile(File f1, File f2){ //String srFile, String dtFile){
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
    	Log.i("ImageSwitcher", ex.toString());
    }
    catch(IOException e){
    	Log.i("ImageSwitcher", e.toString());
    }
  }



}

