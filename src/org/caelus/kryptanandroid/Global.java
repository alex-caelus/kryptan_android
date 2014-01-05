package org.caelus.kryptanandroid;

public class Global
{
	public static final int ACTIVITY_REQUEST_CODE_OPEN_FILE = 0;
	public static final int ACTIVITY_REQUEST_CODE_SECRET_LIST = 1;
	public static final int ACTIVITY_REQUEST_CODE_SECRET_DETAIL = 2;
	public static final int ACTIVITY_REQUEST_CODE_SYNC = 3;
	public static final int ACTIVITY_REQUEST_CODE_NEW_PASSWORD = 4;

	public static final String EXTRA_CORE_PWD_LABELS = "org.caelus.kryptanandroid.PWD_LABELS";
	public static final String EXTRA_CORE_SHOW_SEARCH = "org.caelus.kryptanandroid.SHOW_SEARCH";
	public static final String EXTRA_CORE_PWD = "org.caelus.kryptanandroid.PWD";
	public static final String EXTRA_CORE_PWD_FILE_INSTANCE = "org.caelus.kryptanandroid.PWD_FILE_INSTANCE";
	public static final String EXTRA_CORE_FILTER_COLLECTION = "org.caelus.kryptanandroid.PWD_FILTER_INSTANCE";
	public static final String EXTRA_CORE_PASSWORD_IS_NEWLY_CREATED = "org.caelus.kryptanandroid.PWD_NEWLY_CREATED";
	
	public static final int MINIMUM_MASTERKEY_LENGTH = 8;
	public static final long SHIFT_DOUBLE_CLICK_TIME_MILLIS = 500;
	public static final String THEME_ACCENT_COLOR_STRING = "#669900";
	
	public static final int GENERATE_CHARACTERS_MIN = 4;
	public static final int GENERATE_CHARACTERS_MAX = 64;
	public static final int GENERATE_CHARACTERS_DEFAULT = 12;
	
}
