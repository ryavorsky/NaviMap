package com.navimap.db.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class BaseModel implements Serializable{
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREATED_AT = "_created_at";
    public static final String COLUMN_UPDATED_AT = "_updated_at";

    @DatabaseField(generatedId = true, columnName = COLUMN_ID)
    protected long _id = -1;
    @DatabaseField(columnName = COLUMN_CREATED_AT, dataType = DataType.DATE_LONG)
    protected Date createdAt;
    @DatabaseField(columnName = COLUMN_UPDATED_AT, dataType = DataType.DATE_LONG)
    protected Date updatedAt;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
