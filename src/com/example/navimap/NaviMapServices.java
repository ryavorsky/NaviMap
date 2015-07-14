package com.example.navimap;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class NaviMapServices {
	
	public static List<LatLng> getPoints(String startGeoPoint, String stopGeoPoint){
		try {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("https://maps.googleapis.com")
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setClient(new MyUrlConnectionClient()).build();
			RouteApi routeService = restAdapter.create(RouteApi.class);
			RouteResponseMaps routeResponse = routeService.getRoute(
					startGeoPoint, stopGeoPoint);

			return PolyUtil.decode(routeResponse.getPoints());
		} catch (Exception e) {
			return null;
		}
	 }
	
	public static LatLng getLatLng(String address, String components){
		try {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("https://maps.googleapis.com")
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setClient(new MyUrlConnectionClient()).build();
			RouteApi routeService = restAdapter.create(RouteApi.class);
			RouteResponseGeocoding routeResponse = routeService.getLatLng(
					address, components, "ru");

			return routeResponse.getLatLng();
		} catch (Exception e) {
			return null;
		}
	 }
	
	public static String getAddrByLatLng(LatLng d){
		try {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("https://maps.googleapis.com")
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setClient(new MyUrlConnectionClient()).build();
			RouteApi routeService = restAdapter.create(RouteApi.class);
			//RouteResponseGeocoding routeResponse = routeService.getAddrByLatLng(String.format("%.10f", d.latitude)+","+String.format("%.10f", d.longitude), "ru");
			RouteResponseGeocoding routeResponse = routeService.getAddrByLatLng(d.latitude+","+d.longitude, "ru");
			
			return routeResponse.getAddr();
		} catch (Exception e) {
			return null;
		}
	 }
	
	public static String getAddr(String city, String navi) {
		try {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("http://geo.viz-labs.ru")
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setClient(new MyUrlConnectionClient()).build();
			RouteApi routeService = restAdapter.create(RouteApi.class);
			RouteResponseNavi routeResponse = routeService.getAddr(city, navi);

			return routeResponse.getAddr();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static List<String> getDrivers(String server_token, String start_latitude, String start_longitude, String end_latitude, String end_longitude) {
		try {
			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("https://sandbox-api.uber.com")
					.setLogLevel(RestAdapter.LogLevel.FULL)
					.setClient(new MyUrlConnectionClient()).build();
			RouteApi routeService = restAdapter.create(RouteApi.class);
			RouteResponseUber routeResponse = routeService.getDrivers(server_token, start_latitude, start_longitude, end_latitude, end_longitude);

			return routeResponse.getDrivers();
		} catch (Exception e) {
			return null;
		}
	}
	
	private interface RouteApi {
	    @GET("/maps/api/directions/json")
	    RouteResponseMaps getRoute(
	            @Query(value = "origin") String position,
	            @Query(value = "destination") String destination);
	    
	    @GET("/maps/api/geocode/json")
	    RouteResponseGeocoding getLatLng(
	            @Query(value = "address") String address,
	    		@Query(value = "components") String components,
	    		@Query(value = "language") String language);
	    
	    @GET("/maps/api/geocode/json")
	    RouteResponseGeocoding getAddrByLatLng(
	    		@Query(value = "latlng") String latlng,
	    		@Query(value = "language") String language);
	    
	    @GET("/navidb/search.php")
	    RouteResponseNavi getAddr(
	            @Query("city") String city,
	            @Query("addr") String addr);
	    
	    @GET("/v1/estimates/price")
	    RouteResponseUber getDrivers(
	            @Query("server_token") String server_token,
	            @Query("start_latitude") String start_latitude,
	            @Query("start_longitude") String start_longitude,
	            @Query("end_latitude") String end_latitude,
	            @Query("end_longitude") String end_longitude);
	}
	
	private class RouteResponseMaps {
	    public List<Route> routes;

	    public String getPoints() {
	        return this.routes.get(0).overview_polyline.points;
	    }

	    class Route {
	        OverviewPolyline overview_polyline;
	    }

	    class OverviewPolyline {
	        String points;
	    }
	}
	
	private class RouteResponseGeocoding {
	    private List<Result> results;
	    private String status;
	    
	    class Result {
	        Geometry geometry;
	        String formatted_address;
	        List<Component> address_components;
	    }
	    
	    class Geometry {
	    	MyLocation location;
	    	String location_type;
	    }
	    
	    class MyLocation {
	    	double lat;
	    	double lng;
	    }
	    
	    class Component {
	    	String short_name;
	    	List<String> types;
	    }
	    
	    public LatLng getLatLng(){
	    	return new LatLng(results.get(0).geometry.location.lat, results.get(0).geometry.location.lng);
	    }
	    
	    /*public String getAddr(){
	    	for(int i=0; i<results.size();i++)
	    		if(results.get(i).geometry.location_type.equals("ROOFTOP")) return results.get(i).formatted_address;
	    	return null;
	    }*/
	    
	    public String getAddr(){
	    	List<Component> cmp = results.get(0).address_components;
	    	String res = "";
	    	for(int i = cmp.size()-1; i>=0; i--)
	    		if(cmp.get(i).types.contains("locality") || cmp.get(i).types.contains("route") || cmp.get(i).types.contains("street_number") || cmp.get(i).types.contains("sublocality_level_2")){
	    			res += cmp.get(i).short_name;
	    			res += ", ";
	    		}
	    	if(!res.equals("")) return res.substring(0, res.length()-2); //Убирам лишнюю подстроку ", " из окончания
	    	else return null;
	    }
	    
	}
	
	private class RouteResponseNavi {
	    private String city_en;
	    private String street_en;
	    private String number_en;
	    
	    public String getAddr(){
	    	return city_en+","+street_en+","+number_en;
	    }
	    
	}
	
	private class RouteResponseUber {
		private List<Product> prices;
	    
	    public List<String> getDrivers(){
	    	List<String> res = new ArrayList<String>();
	    	for(int i=0; i<this.prices.size();i++)
	    		res.add(this.prices.get(i).get_str());
	    	return res;
	    }
	    
	    class Product {
	    	String display_name;
	    	String estimate;
	    	
	    	public String get_str(){
	    		return display_name+", "+estimate.substring(0, 3)+" "+estimate.substring(3);
	    	}
	    	
	    }
	    
	}

}
