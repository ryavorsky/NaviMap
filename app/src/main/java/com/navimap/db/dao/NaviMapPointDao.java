package com.navimap.db.dao;

import com.j256.ormlite.dao.Dao;
import com.navimap.db.model.NaviMapPoint;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Makvit on 21.07.2015.
 */
public interface NaviMapPointDao extends Dao<NaviMapPoint, Long> {
    public List<NaviMapPoint> getNaviMapPoints() throws SQLException;
}
