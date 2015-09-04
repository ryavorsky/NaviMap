package com.navimap.utils;

import com.google.android.gms.maps.model.LatLng;
import com.navimap.settings.Constants;

/**
 * Created by Makvit on 27.07.2015.
 */
public class MapUtils {
    private static final double k1 = 111111.1;
    private static final double k2 = 62647.6;

    /**
     * Returns the approximate distance in meters between this location and the given location. Distance is defined using the WGS84 ellipsoid.
     *
     * @param latLng1 start location
     * @param latLng2 destination location
     * @return the approximate distance in meters
     */
    public static double getDistance(LatLng latLng1, LatLng latLng2) {
        double d1 = (latLng1.latitude-latLng2.latitude)*k1;
        double d2 = (latLng1.longitude-latLng2.longitude)*k2;
        return Math.sqrt(d1*d1+d2*d2);
    }

    public static City getNearestCity(LatLng latLng) {
        return City.getNearestCity(latLng);
    }

    public static String getNavi8(LatLng latLng){
        MapUtils.City city = MapUtils.City.getNearestCity(latLng);
        double[] d = new double[2];
        d[0] = (latLng.latitude-city.getLat())*k1;
        d[1] = (latLng.longitude-city.getLng())*k2;

        int navi1 = (int)Math.round(d[0]/10) + 5000;
        int navi2 = (int)Math.round(d[1]/10) + 5000;

        if (navi1 < 0 || navi1 > 9999 || navi2 < 0 || navi2 > 9999) {
            d[0] = latLng.latitude*k1+55000000;
            d[1] = latLng.longitude*k2+55000000;
            int[] dist = new int[2];
            dist[0] = (int) (d[0]/10);
            dist[1] = (int) (d[1]/10);
            String code = String.valueOf(dist[0]).substring(0,3)+String.valueOf(dist[1]).substring(0,3);
            String index = String.format("%04d",dist[0] % 10000) + " " + String.format("%04d", dist[1] % 10000);
            return "(" + code + ") " + index;
        } else {
            return "(" + city.getNaviCode().replaceFirst("0", "+") + ") " +  String.format("%04d",navi1) + " " +  String.format("%04d",navi2);
        }
    }

    public static LatLng GetLatLngNavi8(String code){
        LatLng latLng=null;
        String strCityNaviCode = code.substring(0, code.indexOf(" ")).replace("(", "").replace(")", "").replace("+", "0");
        String strNaviCode = code.substring(code.indexOf(" ")).replace(" ","");
        int naviCode = Integer.parseInt(strNaviCode);
        int cityCode = Integer.parseInt(strCityNaviCode);
        if (cityCode<100000) {
            int[] d = new int[]{(int)Math.floor(naviCode/10000),naviCode%10000};
            City city = null;
            for (City item : City.values()) {
                if (item.getNaviCode().equals(strCityNaviCode))
                    city = item;
            }
            if (city!=null) {
                double lat = (d[0]-5000)*10/k1+city.getLat();
                double lng = (d[1]-5000)*10/k2+city.getLng();
                latLng = new LatLng(lat, lng);
            }
        } else {
            double[] dist = new double[2];
            dist[0] = ((int)Math.floor(cityCode/1000))*10000+((int)Math.floor(naviCode/10000));
            dist[1] = cityCode%1000 *10000+ naviCode%10000;
            dist[0] = dist[0]*10-55000000;
            dist[1] = dist[1]*10-55000000;
            double lat = dist[0]/k1;
            double lng = dist[1]/k2;
            latLng = new LatLng(lat, lng);
        }
        return latLng;
    }

    public static LatLng GetLatLngNavi6(String city, int Navi) {
        String str = NaviMapUtils.getAddr(city, Integer.toString(Navi));
        if (str == null) return null;
        return NaviMapUtils.getLatLng(str, "");
    }

    public static String getLink(String code) {
        return "http://navic.me/" + code.replace("(", "").replace("+", "0").replace(") ", ".").replace(" ", "");
    }

    public static boolean isCityCanBeUsedAsAxisToLocation(City city, LatLng location) {
        Double distance = getDistance(new LatLng(city.lat, city.lng), location);
        if (distance <= Constants.MAX_DISTANCE_TO_CITY_CENTER)
            return true;
        else return false;
    }

    public enum City {
        //code,lat,lng,country phone code,city phone code, navicode
        PARIS(0, 48.856614, 2.3522219, 33, 1, "0331"),
        TOKYO(1, 35.6894875, 139.6917064, 81, 3, "0813"),
        AMSTERDAM(2, 52.3702157, 4.8951679, 31, 20, "03120"),
        LONDON(3, 51.5073509, -0.1277583, 44, 20, "04420"),
        NEW_YORK(4, 40.7127837, -74.0059413, 1, 212, "01212"),
        MOSCOW(5, 55.755826, 37.6173, 7, 495, "07495"),
        LOS_ANGELES(6, 34.0522342, -118.2436849, 1, 213, "01213"),
        SAN_FRANCISCO(7, 37.7749295, -122.4194155, 1, 415, "01415"),
        HONG_KONG(8, 22.3593252, 114.1408686, 852, 852, "0852"),
        DUBAI(9, 25.073858, 55.2298444, 971, 4, "09714"),
        CAPE_TOWN(10, -33.9248685, 18.4240553, 27, 21, "02721"),
        KAZAN(11, 55.7955015, 49.073303, 7, 843, "07843");

        private int intValue;
        private double lat;
        private double lng;
        private int countryCode;
        private int cityCode;
        private String naviCode;

        City(int intValue, double lat, double lng, int countryCode, int cityCode, String naviCode) {
            this.intValue = intValue;
            this.lat = lat;
            this.lng = lng;
            this.countryCode = countryCode;
            this.cityCode = cityCode;
            this.naviCode = naviCode;
        }

        public static City getCity(String naviCode) {
            for (City city : City.values()) {
                if (city.naviCode.equals(naviCode))
                    return city;
            }
            return null;
        }

        /**
         * Returns the approximatly nearest city to target location using the WGS84 ellipsoid.
         *
         * @param latLng target location
         * @return
         */
        public static City getNearestCity(LatLng latLng) {
            City nearest = null;
            double distance = Double.MAX_VALUE;

            for (City city : City.values()) {
                double currentDistance = MapUtils.getDistance(latLng, new LatLng(city.lat, city.lng));
                if (distance > currentDistance) {
                    nearest = city;
                    distance = currentDistance;
                }
            }
            return nearest;
        }

        public String getNaviCode() {
            return naviCode;
        }

        public int getCityCode() {
            return cityCode;
        }

        public int getCountryCode() {
            return countryCode;
        }

        public double getLng() {
            return lng;
        }

        public double getLat() {
            return lat;
        }

        public LatLng getLatLng() {
            return new LatLng(lat,lng);
        }
    }
}
