package uk.ac.imperial.epi_collect2.util.barcode;

import uk.ac.imperial.epi_collect2.R;
import uk.ac.imperial.epi_collect2.util.barcode.IntentIntegrator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.TextView;

public class Barcode_Scanner extends Activity {

	EditText barcodetext;
	Button barcodebutton;
	
	@Override
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    
	    setContentView(R.layout.barcode_test);
	    
	    barcodetext = (EditText) findViewById(R.id.barcode);
	    barcodebutton = (Button) findViewById(R.id.scanbut);
	
	    barcodebutton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View arg0) {
	    		IntentIntegrator.initiateScan(Barcode_Scanner.this);
	    		
	    		//Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	    		//intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
	    		//startActivityForResult(intent, 0);
	    	}
           
	    });
	
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
	       case IntentIntegrator.REQUEST_CODE: {
	           if (resultCode != RESULT_CANCELED) {
	               	IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	               if (scanResult != null) {
	                   String upc = scanResult.getContents();
	                   // Do whatever you want with the barcode...
	                   barcodetext.setText(upc);
	               }
	           }
	           break;
	       }
	   }	
		
		/*IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
			if (scanResult != null) {
     		   String contents = intent.getStringExtra("SCAN_RESULT");
     		   String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
     		   barcodetext.setText(format);
     		   // Handle successful scan
     	   } else if (resultCode == RESULT_CANCELED) {
     		   // Handle cancel
     	   } */
	}

}
