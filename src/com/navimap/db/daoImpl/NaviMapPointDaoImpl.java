package com.navimap.db.daoImpl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.navimap.db.dao.NaviMapPointDao;
import com.navimap.db.model.NaviMapPoint;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Makvit on 21.07.2015.
 */
public class NaviMapPointDaoImpl extends BaseDaoImpl<NaviMapPoint, Long> implements NaviMapPointDao {
    public NaviMapPointDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, NaviMapPoint.class);
    }

    @Override
    public List<NaviMapPoint> getNaviMapPoints() throws SQLException {
        QueryBuilder queryBuilder = queryBuilder();
        queryBuilder.orderBy(NaviMapPoint.COLUMN_IS_FAVORITE, true);
        return queryBuilder.query();
    }

}
