package com.navimap.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.navimap.db.dao.NaviMapPointDao;
import com.navimap.db.model.NaviMapPoint;
import com.navimap.utils.LogUtils;

import java.sql.SQLException;

public class NaviMapDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "navimap.db";
    private static final int DATABASE_VERSION = 2;

    private static NaviMapDatabaseHelper instance;

    private NaviMapPointDao naviMapPointDao;


    private NaviMapDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static NaviMapDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NaviMapDatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            create(connectionSource);
        } catch (SQLException e) {
            LogUtils.e("Can not create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if (newVersion <= 2) {
                drop(connectionSource);
                create(connectionSource);
            } else {
                if (oldVersion < newVersion) {
                    switch (oldVersion) {
                        //case 2:
                        // upgradeTo2();
                    }
                }
            }
        } catch (SQLException e) {
            LogUtils.e("Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void create(ConnectionSource connectionSource) throws SQLException {
        LogUtils.i("Creating the database");
        TableUtils.createTableIfNotExists(connectionSource, NaviMapPoint.class);
    }

    public void drop(ConnectionSource connectionSource) throws SQLException {
        LogUtils.i("Dropping the database");
        TableUtils.dropTable(connectionSource, NaviMapPoint.class, true);
    }

    public void clear(boolean keepCalendarCache) throws SQLException {
        getNaviMapPointDao().executeRawNoArgs("delete from " + NaviMapPoint.TABLE_NAME);
    }

    @Override
    public void close() {
        super.close();
        naviMapPointDao = null;
    }

    public NaviMapPointDao getNaviMapPointDao() throws SQLException {
        if (naviMapPointDao == null) {
            naviMapPointDao = getDao(NaviMapPoint.class);
        }
        return naviMapPointDao;
    }
}
