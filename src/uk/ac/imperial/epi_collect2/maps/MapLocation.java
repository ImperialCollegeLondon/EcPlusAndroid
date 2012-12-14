package uk.ac.imperial.epi_collect2.maps;

import com.google.android.maps.GeoPoint;

public class MapLocation {
	
	public String title, pkey, phonekey;
	public double latitude, longitude;
	//public long id;
	public GeoPoint geoPoint;
	
	public MapLocation(String des, String key, String phone, GeoPoint geo, double lat, double lon){
		
		title = des;
		pkey = key;
		phonekey = phone;
		geoPoint = geo;
		latitude = lat;
		longitude = lon;
	}
	
	public GeoPoint getGeoPoint(){
		return geoPoint;
	}
}
