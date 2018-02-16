package com.adjointtechnologies.vectormarico;

import android.os.Environment;

/**
 * Created by lokeshmutyala on 02-11-2017.
 */

public class ConstantValues {
    public static String app_version="V1";
    public static final String baseUrl="http://54.245.52.20/marico_activity/";
    public static final String CONTENT_AUTHORITY = "com.adjointtechnologies.maricovector";
    public static final String dataBasePath=Environment.getExternalStorageDirectory()+"/MarVector/databases";
    public static final String imagepath= Environment.getExternalStorageDirectory()+"/MarVector/images";
    public static final String imageprocess=Environment.getExternalStorageDirectory()+"/MarVector/imageprocess";
    public static final String imagecomplete=Environment.getExternalStorageDirectory()+"/MarVector/imagepcomplete";
}
