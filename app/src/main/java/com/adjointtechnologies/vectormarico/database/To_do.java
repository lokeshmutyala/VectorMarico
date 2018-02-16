package com.adjointtechnologies.vectormarico.database;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 05-11-2017.
 */
@Entity
public interface To_do {

    @Key
    @Generated
    public int getId();

    public String getStoreId();

    public Double getLatitude();

    public Double getLongitude();

    public String getStoreName();

    public String getItcStoreId();

    public int getItcStoreIdCount();

    public String getItcStoreName();

    public String getOwnerName();

    public String getOwnerMobileNo();

    public boolean getAlreadyVerified();

    public boolean getCompleteflag();

    public String getStoreGoogleAddress();

    public String getLandMark();

    public String getStoreCategory();

}
