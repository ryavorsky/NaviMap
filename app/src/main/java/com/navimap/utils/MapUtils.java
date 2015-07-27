package com.navimap.utils;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;
import com.navimap.settings.Constants;

/**
 * Created by Makvit on 27.07.2015.
 */
public class MapUtils {

    /**
     * Returns the approximate distance in meters between this location and the given location. Distance is defined using the WGS84 ellipsoid.
     *
     * @param latLng1 start location
     * @param latLng2 destination location
     * @return the approximate distance in meters
     */
    public static double getDistance(LatLng latLng1, LatLng latLng2) {
        Location location1 = new Location("");
        location1.setLatitude(latLng1.latitude);
        location1.setLongitude(latLng1.longitude);
        Location location2 = new Location("");
        location2.setLatitude(latLng2.latitude);
        location2.setLongitude(latLng2.longitude);
        return location1.distanceTo(location2);
    }

    public static City getNearestCity(LatLng latLng) {
        return City.getNearestCity(latLng);
    }

    /**
     * расстояние до экватора и гринвича
     *
     * @param latLng
     * @return
     */
    public static Double[] getZeroAxisDistance(LatLng latLng) {
        return getDistancesToAxis(latLng, new LatLng(0, 0));
    }

    public static Double[] getDistancesToAxis(LatLng targetLocation, LatLng axisLocation) {
        double distanceToEquator = MapUtils.getDistance(targetLocation, new LatLng(axisLocation.latitude, targetLocation.longitude)) * Math.signum(targetLocation.longitude - axisLocation.longitude);
        double distanceToGreenwich = MapUtils.getDistance(targetLocation, new LatLng(targetLocation.latitude, axisLocation.longitude)) * Math.signum(targetLocation.latitude - axisLocation.latitude);
        return new Double[]{distanceToEquator, distanceToGreenwich};
    }

    public static Double[] getZeroAxisNaviAddress(LatLng latLng) {
        Double[] distance = getZeroAxisDistance(latLng);
        distance[0] = distance[0] + 55000000;
        distance[1] = distance[1] + 55000000;
        return distance;
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
