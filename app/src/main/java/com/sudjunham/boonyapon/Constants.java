package com.sudjunham.boonyapon;

import android.Manifest;


public class Constants {

    public final static String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public final static String GALLERY_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public final static int CAMERA_PERMISSION_CODE = 1;
    public final static int GALLERY_PERMISSION_CODE = 2;

    public final static int GALLERY_INTENT = 100;
    public final static int CAMERA_INTENT = 200;
    public final static int IMAGE_HEAD_INTENT = 300;

    public  static final String  SHARED_PERFERENCES_CONTAINER = "com.neo";
    public static final String SHARED_KEY = "shkey";
    public static String SAVE_DATA = "SAVEDATA";

    public final static String VISION_ACTIVITY = "VISION_ACTIVITY";

    public final static String  LANDING_GALERY = "Landing_gallery";
    public final static String LANDING_CAMERA = "landing_camera";
    public final static String LANDING_EDIT = "landing_edit";
    public final static int EDITING = 999;
    public final static String EDIT_INTENT = "EINTENT";


}
