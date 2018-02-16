package com.adjointtechnologies.vectormarico.database;

import io.requery.Entity;
import io.requery.Key;

/**
 * Created by lokeshmutyala on 02-11-2017.
 */
@Entity
public interface StoreDetails {

    @Key
    public String getStoreId();

    public String getUniqueId();

    public String getStoreName();

    public boolean getOtpSent();

    public boolean getOtpVerified();

    public String getStoreCategory();

    public String getVectorId();

    public String getStoreCondition();

    public boolean getSyncStatus();

    public String getSurveyTime();

    public String getIsStoreFound();

    public String getOwnerName();

    public String getOwnerMobile();

    public String getAlternateMobile();

    public boolean getOtpSent2();

    public boolean getOtpVerified2();

    public String getOwnerHasSmartPhone();

    public String getIsMaricoSalesMan();

    public String getIsHairOil();

    public String getIsPosterPasted();

    public String getPosterReason();

    public String getIsMaricoNoRetailerPhone();

    public String getIsOutletActivated();
}
