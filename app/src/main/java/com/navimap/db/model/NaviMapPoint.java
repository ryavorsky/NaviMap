package com.navimap.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.navimap.db.daoImpl.NaviMapPointDaoImpl;

/**
 * Created by Makvit on 21.07.2015.
 */

@DatabaseTable(tableName = NaviMapPoint.TABLE_NAME, daoClass = NaviMapPointDaoImpl.class)
public class NaviMapPoint extends BaseModel {
    public static final String TABLE_NAME = "navi_map_point";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_NAVI_ADDRESS = "navi_address";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_IS_FAVORITE = "is_favorite";

    @DatabaseField(columnName = COLUMN_LATITUDE, dataType = DataType.DOUBLE_OBJ)
    private Double latitude;
    @DatabaseField(columnName = COLUMN_LONGITUDE, dataType = DataType.DOUBLE_OBJ)
    private Double longitude;
    @DatabaseField(columnName = COLUMN_NAVI_ADDRESS, dataType = DataType.STRING)
    private String naviAddress;
    @DatabaseField(columnName = COLUMN_ADDRESS, dataType = DataType.STRING)
    private String address;
    @DatabaseField(columnName = COLUMN_IS_FAVORITE, dataType = DataType.BOOLEAN)
    private boolean isFavorite;

    public NaviMapPoint() {
    }

    public NaviMapPoint(Double latitude, Double longitude, String naviAddress, String address, boolean isFavorite) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.naviAddress = naviAddress;
        this.address = address;
        this.isFavorite = isFavorite;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getNaviAddress() {
        return naviAddress;
    }

    public void setNaviAddress(String naviAddress) {
        this.naviAddress = naviAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
