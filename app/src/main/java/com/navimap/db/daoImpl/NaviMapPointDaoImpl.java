package com.navimap.db.daoImpl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.navimap.db.dao.NaviMapPointDao;
import com.navimap.db.model.NaviMapPoint;
import com.navimap.utils.StringUtils;

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
    public List<NaviMapPoint> getNaviMapPoints(String query) throws SQLException {
        QueryBuilder queryBuilder = queryBuilder();
        if (!StringUtils.isNullOrEmpty(query))
            queryBuilder.where().like(NaviMapPoint.COLUMN_SEARCH_FIELD, query.toUpperCase());
        queryBuilder.orderBy(NaviMapPoint.COLUMN_IS_FAVORITE, false);
        return queryBuilder.query();
    }

    @Override
    public NaviMapPoint getNaviMapPointByAddressName(String query) throws SQLException {
        QueryBuilder queryBuilder = queryBuilder();
        queryBuilder.where().eq(NaviMapPoint.COLUMN_ADDRESS, query);
        queryBuilder.orderBy(NaviMapPoint.COLUMN_IS_FAVORITE, false);
        return (NaviMapPoint) queryBuilder.queryForFirst();
    }

}
