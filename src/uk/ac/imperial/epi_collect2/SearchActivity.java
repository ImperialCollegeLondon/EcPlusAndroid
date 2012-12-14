package uk.ac.imperial.epi_collect2;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SearchActivity extends ListActivity{
	
	static final String SEARCH_FIELD="1";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent queryIntent = getIntent();
		
		//Log.i("SEARCH", "IN ON SEARCH!!!!!!!!!!!!!!!!!!!!!!!!");
		
		String textid = "None";
		Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
		if (appData != null) {
		    textid = appData.getString(SearchActivity.SEARCH_FIELD);
		}
		
		//Log.i("QUERY TEXT", textid);
		
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			String searchKeywords = queryIntent.getStringExtra(SearchManager.QUERY);
		}
	}
}
