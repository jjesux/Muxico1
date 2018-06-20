package com.jjesuxyz.muxico.DBData;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;





/**
 * The class DBAccessHelper is used to create a local database and its tables. It does not make
 * any other function for this app, so far. Final variables defined in this class may be used
 * in other classes of this app.
 *
 * Created by jjesu on 6/3/2018.
 */

public class DBAccessHelper extends SQLiteOpenHelper{

                                        //Global class variables declaration
    public static final String TABLE_FULL_LIST =  "tb_full_list";
    public static final String COLUMN_ID_FL =     "_id";
    public static final String COLUMN_FILE_PATH = "col_file_path";

    public static final String TABLE_PLAY_LIST =  "tb_playlist";
    public static final String COLUMN_PLAYLIST_SONGS = "col_playlist_songs";

    public static final String TABLE_ALBUM_LIST =  "tb_album_list";
    public static final String COLUMN_ALBUMLIST_NAMES = "col_album_list_names";

    private static final String DATABASE_NAME =   "nk.db";
    private static final int DATABASE_VERSION = 1;
                                        //String to create the database tables command
    public static final String DATABASE_CREATE_FULLLIST =
                        "CREATE TABLE " + TABLE_FULL_LIST + "("
                                        + COLUMN_ID_FL + " integer primary key autoincrement, "
                                        + COLUMN_FILE_PATH + " text not null); ";

    public static final String DATABASE_CREATE_PLAYLIST =
                        "CREATE TABLE " + TABLE_PLAY_LIST + "("
                                        + COLUMN_PLAYLIST_SONGS + " text not null); ";

    public static final String DATABASE_CREATE_ALBUMLIST =
                        "CREATE TABLE " + TABLE_ALBUM_LIST + "("
                                        + COLUMN_ALBUMLIST_NAMES + " text not null); ";

    private final String t = "NIKO";    //Variable for debugging usage





    /**
     * Class DBAccessHelper(Context) constructor. It is used pass data to the super class of this
     * class. It needs a Context object as a parameter. This parameter is passed to the super
     * function.
     *
     * @param context type Context
     */
    public DBAccessHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }//End of class constructor




    /**
     * The onCreate(SQLiteDatabase) function is used to create a database and its tables. This
     * function is called by the system. The parameter it receives is a database object.
     *
     *  @param db type SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
                                        //Creating database tables
            db.execSQL(DATABASE_CREATE_FULLLIST);
            db.execSQL(DATABASE_CREATE_PLAYLIST);
            db.execSQL(DATABASE_CREATE_ALBUMLIST);
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }//End of function




    /**
     * The onUpgrade(SQLiteDatabase, int, int) function is not used in this version of this app.
     *
     * @param db type SQLiteDatabase
     * @param oldVersion type int
     * @param newVersion type int
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




    /**
     * The l(String) function is used only to debug this class. It uses the Log.d() function to pass
     * the information to the Android Monitor window.
     * This information contains the class name and some information about the error or data
     * about the debugging process.
     *
     * @param str type String
     */
    private void l(String str){
        Log.d(t, this.getClass().getSimpleName() + " -> " + str);
    }



}   //End of class DBAccessHelper



/********************************END OF FILE DBAccessHelper.java***********************************/
