package com.jjesuxyz.muxico;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jjesuxyz.muxico.DBData.DBAccess;
import com.jjesuxyz.muxico.DBData.DBAccessHelper;

import java.io.File;
import java.util.ArrayList;



/**
 * ELModelo public class is used to get access to the local database to insert records into
 * its tables and also to extract data from its tables. In theory all the user interface
 * activities and user interface fragments must use this class to access the local database.
 * This class manages any data manipulation before sending that data to the classes that
 * asked for that data.
 *
 * Created by jjesu on 6/3/2018.
 */

public class ElModelo {
                                        //Object to be pass to the class accessing the local DB
    private Context context;
                                        //Arrays to hold data from database tables
    private ArrayList<String> arrListFiles;
    private ArrayList<String> arrayListPLayList;
                                        //Variable is used to count how many mp3 files from SD Card
    private Integer counter = 0;
                                        //Final variable for debugging purposes.
    private final String t = "NIKO";
                                        //Variable to access local database
    private DBAccess dbAccess;




    /**
     * Public class constructor is used to perform some global class variables initialization.
     * It receives a Context object as the only parameter it needs.
     *
     * @param context type Context
     */
    public ElModelo(Context context){

        arrListFiles =      new ArrayList<>();
        arrayListPLayList = new ArrayList<>();
        this.context = context;
       // getAllMP3Files(null, 1);
    }   //End of constructor



    /**
     * insertMultipleRecordsIntoPlayListTable(ArrayList) function is used to insert multiple
     * MP3 records into the local database play list table. It inserts into the database table
     * all the records it receives as an ArrayList parameter.
     * It does not returns any value.
     *
     * @param arrayListFilePath type ArrayList
     */
    public void insertMultipleRecordsIntoPlayListTable(ArrayList<String> arrayListFilePath){
                                        //Checking that ArrayList has been instantiated before????
        if (arrayListFilePath != null) {
                                        //Getting access to the local database
            dbAccess = new DBAccess(context);
                                        //Inserting multiple records into the database
            dbAccess.insertMultipleRecordsIntoPlayListTable(arrayListFilePath);
            dbAccess.close();
            dbAccess = null;
        }
        else {
                                        //Code just for debugging purposes
            l("Function insertMultipleRecordsIntoPlayListTable(AL<Str> => NULL) ");
        }

    }   //End of insertMultipleRecordsIntoPlayListTable() function



    /**
     * getArrayListFromPlayListTable() function is used to get all the records from
     * the local database play list table. It returns all those records after extracting them
     * the Cursor object it receives.
     *
     * @return type ArrayList
     */
    public ArrayList<String> getArrayListFromPlayListTable(){
                                        //ArrayList to hold all MP3 records
        arrayListPLayList = new ArrayList<>();
                                        //Instantiating object to access local DB
        dbAccess = new DBAccess(context);
                                        //Getting all MP3 records from local DB
        Cursor cursor = dbAccess.getAllMP3FilePathsFromDBPlaylistTable();
        int index = cursor.getColumnIndex(DBAccessHelper.TABLE_PLAY_LIST);
                                        //Extracting MP3 records from Cursor
        while (cursor.moveToNext()) {
            String str = cursor.getString(0);
            arrayListPLayList.add(str);
        }
                                        //Closing local DB
        dbAccess.close();
                                        //Returning all MP3 files gotten from local Db
        return arrayListPLayList;

    }   //End of getArrayListFromPlayListTable() function



    /**
     * getAllMP3FilePathFromDBFulllistTable(String) function is used to get all the records from
     * the local database full list table. It returns all those records after extracting them
     * the Cursor object it receives.
     *
     * Note: The String parameter it receives it is never used.
     *
     * @param strParam type String
     * @return type ArrayList
     */
    public ArrayList<String> getAllMP3FilePathFromDBFulllistTable(String strParam) {
                                        //ArrayList to hold all MP3 records
        arrayListPLayList = new ArrayList<>();
                                        //Instantiating object to access local DB
        dbAccess = new DBAccess(context);
                                        //Getting all MP3 records from local DB
        Cursor cursor = dbAccess.getAllMP3FilePathsFromDBFullListTable();
        int index = cursor.getColumnIndex(DBAccessHelper.TABLE_FULL_LIST);
                                        //Extracting MP3 records from Cursor
        while (cursor.moveToNext()) {
            String str = cursor.getString(0);
            arrayListPLayList.add(str);
        }
                                        //Closing local DB
        dbAccess.close();
                                        //Returning all MP3 files gotten from local Db
        return arrayListPLayList;

    }   //End of getAllMP3FilePathFromDBFulllistTable() function



    /**
     * deleteOneRowOnPlayListTable(String) function is used to deleted one record from the
     * local database play list table. The string parameter it receives is the record that
     * will be deleted from the database play list table.
     * It returns the number of records deleted, 1 or 0.
     *
     * @param strTmp String
     * @return type int
     */
    public int deleteOneRowOnPlayListTable(String strTmp){
                                        //Variable to hold the number of deleted records
        int delRecordResult;
                                        //Instantiating DBAccess object to actually access the DB
        dbAccess = new DBAccess(context);
                                        //Deleting one record from database play list table
        delRecordResult = dbAccess.deleteOneRowsFromPlaylistTable(strTmp);
                                        //if for debugging purposes
        if (delRecordResult <= 0){
            l("Funcion deleteOneRowOnPlayListTable(String ERROR " + delRecordResult);
        }

        return delRecordResult;

    }   //End of deleteOneRowOnPlayListTable() function



    /**
     * deleteAllRecordsOnPlayListTable() function is used to delete all records from the database
     * play list table. It returns the number of deleted records.
     *
     * @return type int
     */
    public int deleteAllRecordsOnPlayListTable(){
                                        //Variable to hold the number of deleted records
        int delRecordResult;
                                        //Instantiating DBAccess to actually access the local DB
        dbAccess = new DBAccess(context);
                                        //Deleting all records from database play list table
        delRecordResult = dbAccess.deleteAllRowsFromPlaylistTable();
        if (delRecordResult <= 0){
            l("Deleted records number with Function deleteAllRowsFromPlayListTable()" + delRecordResult);
        }
                                        //Returning number of deleted records from play list table
        return delRecordResult;

    }   //End of deleteAllRecordsOnPlayListTable() function



    /**
     * getAllMP3Files(File, int) function is used
     * @param file type File
     * @param level type int
     */
    private void getAllMP3Files(File file, int level){
                                        //Checking if it is the first function call
        if(file == null && level <= 1){
            //file = Environment.getExternalStorageDirectory();//.getParentFile();
            file = new File("/storage/3061-6433");//Environment.getExternalStorageDirectory();//.getParentFile();

            getAllMP3Files(file, ++level);
        }
        else{
            //Pushing mp3 files into the ArrayList
            if(file.isFile()) {
                if (isFileMP3(file.getAbsolutePath())) {
                    arrListFiles.add(file.getAbsolutePath());
                    counter++;
                }
            }
            //Moving into another directory, deeper level
            else if(file.isDirectory() && level <= 5){
                level = level + 1;
                File[] arrFiles = file.listFiles();
                if(arrFiles != null){
                    for(int i = 0; i < arrFiles.length; i++){
                        //This recursive function is calling itself
                        getAllMP3Files(arrFiles[i], level);
                    }
                }
            }
        }
    }   //End of getAllMP3Files() function



    /**
     * isFileMP3() function is used to check if the file name it receives as a parameter
     * is an mp3 file or a file of any other type. It returns true if file name is an
     * mp3 file, false otherwise.
     *
     * @param pathFile type String
     * @return type boolean
     */
    private boolean isFileMP3(String pathFile){
        if(pathFile.endsWith(".mp3") || pathFile.endsWith(".MP3")){
            return true;
        }
        else {
            return false;
        }

    }   //End of isFileMP3() function



    /**
     * getArrayListOfFiles() public function is used to send the list of mp3 files to
     * another class of this project. It returns the variable holding the list of mp3
     * files. It returns the variable even if it is null;
     *
     * @return type ArrayList
     */
    public ArrayList<String> getArrayListOfFiles(){
        return arrListFiles;
    }   //End of getArrayListOfFiles() function



    /**
     * getArrayListOfFilesInfo() function is not used in this project, yet.
     */
    public void getArrayListOfFilesInfo(){
        if(arrListFiles == null) {
            l("Array List of MP3 files is null");
        }
        else if (arrListFiles.size() <= 0) {
            l("Array list of mp3 files is empty.");
        }
        else {
            for (String strFileName : arrListFiles){
                l("MP3 file name: " + strFileName);
            }
        }

    }   //End of getArrayListOfFilesInfo() function



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


}   //End of Class ElModelo



/*************************************END OF FILE ElModelo.java************************************/

