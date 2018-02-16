package com.adjointtechnologies.vectormarico.database;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 02-11-2017.
 */
@Entity
public interface MapperInfo {
    @Key
    @Generated
    public int getId();

    public String getAuditId();
}
