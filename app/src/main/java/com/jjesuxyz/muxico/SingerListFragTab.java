package com.jjesuxyz.muxico;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jjesuxyz.muxico.DBData.DBAccessHelper;
import java.util.ArrayList;
import java.util.Collections;






/**
 * SingerListFragTab class is used to manage the UI fragment to let user manage the music library
 * of singer name list. it also shows buttons to let user manage and select singer names to see
 * all the MP3-song that that singer sings. It uses a ListView widget to show a list of all the
 * singer names of MP3-songs that the SD Card contains. User can select/click one record/row/singer
 * name and the ListView will display the list of MP3-song that that singer sings, and the user can
 * select/click any of the MP3-song to add it to the play list library. After the song/row is
 * selected the user has to click a button to finally add it to the play list library or database
 * play list table. This app version allows the user to add just one MP3 file at a time to the
 * play list library. The user can click another button to make the ListView display the list of
 * singer names again.
 *
 * Created by jjesu on 6/25/2018.
 */

public class SingerListFragTab extends ListFragment
                                    implements AdapterViewAnimator.OnItemClickListener {

                                        //Buttons to control/modify data and UI
    private Button btnAllSingerPlayAll;
    private Button btnSingerAddSinger;
    private Button btnSingerUpdateSinger;
                                        //Var to know if permission storage access is granted
    private boolean bPermissionsState = false;
                                        //ArrayList holds all MP3 file info that are in SD Card
    private ArrayList<String> arrayListFilePathsSDCard;
                                        //ArrayList holds all the singer names that DB table contains
    private ArrayList<String> arrayListSingerNameTable;
                                        //ArrayList holds all the MP3-song singer names-repeated
                                        //It is also used to eleminated repeated names
    private ArrayList<String> arrayListSingerNames;
                                        //ArrayList holds the singer names but not repeated names
    private ArrayList<String> localArrayListSingerName;
                                        //ArrayList hold MP3 file info to be inserted into the
                                        //DB play list table
    private ArrayList<String> arrayListIntoPlayList;
                                        //Var to show file paths info to user
    private ListView listView;
                                        //Var to high light ListView row clicked
    private View vwLastRowSelected = null;
                                        //Interface to communicate with activity hosting this fragment
    private SingerListFragTab.SingerInterfaceListener mCallBack;
                                        //ListView Adapter
    private MuxListViewAdapter muxListViewAdapter;
                                        //Constant var in case permissions have not being granted
    private final int PERMISSION_ACCESS_CODE = 3456;
                                        //songs mode -> true  /  singer mode -> false
    private boolean singerSongMode = false;
                                        //Var to hold the number of the row clicked by the user
    private int positionAddPl = -1;
                                        //Var used to know if there was any data update
    private boolean blPlayListUpdated = false;





    /**
     * onCreateView(LayoutInflater, ViewGroup, Bundle) function is used to draw the fragment
     * UI and to initialize the buttons that will modify the data in the ListView window.
     * These buttons are used to update the ListView data from the database singer name list,
     * table to populate this DB table with singer names, and to stop end destroy the fragments
     * and go back to the main user interface.
     *
     * @param inflater type LayoutInflater
     * @param container type ViewGroup
     * @param savedInstanceState type Bundle
     * @return type View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.singer_layout, container, false);
                                        //Instantiating AL to hold singer names
        localArrayListSingerName = new ArrayList<>();
                                        //instantiating AL to hold MP3 file info to be inserted into
                                        //the MP3 file play list table
        arrayListIntoPlayList = new ArrayList<String>();

                                        //Button to go back to the main user interface
        btnAllSingerPlayAll = view.findViewById(R.id.btnPlayAllSingerId);
        btnAllSingerPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                        //Letting know hosting class if there were change in LV data
                mCallBack.comunicacionInterfaceFunction(blPlayListUpdated, 10);
            }
        });                             //End of go back button click listener setting

                                        //Button to add one row/data MP3 info to DB play list table
        btnSingerAddSinger = view.findViewById(R.id.btnAddSingerId);
        btnSingerAddSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTmp;
                if(vwLastRowSelected != null){
                                        //Getting string data from row clicked by user
                    strTmp = ((TextView)vwLastRowSelected.findViewById(R.id.txtvwMP3FilePath)).getText().toString();
                    ElModelo elModelo = new ElModelo(getContext());
                                        //Inserting one record to the DB play list table
                                        //Only one record is allowed to be inserted into this table
                    arrayListIntoPlayList.add(localArrayListSingerName.get(positionAddPl));
                    elModelo.insertMultipleRecordsIntoPlayListTable(arrayListIntoPlayList);
                                        //Clearing ArrayList for new mp3 file to insert into table
                    arrayListIntoPlayList.clear();
                    blPlayListUpdated = true;
                    elModelo = null;
                }
                else{
                    strTmp = "ERROR ERROR";
                }
                Toast.makeText(getActivity(), "Adding to Singer List:  " + strTmp, Toast.LENGTH_SHORT).show();
                //setAddDeleteBtnNewState();
            }
        });                             //End of add button click listener setting


                                        //Button to update the ListView window data from the SD Card
        btnSingerUpdateSinger = view.findViewById(R.id.btnUpdateSingerId);
        btnSingerUpdateSinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                        //Checking if user allowed permission to access storage
                checkStorageAccessPermission();
                                        //If permission granted, access data and update DB
                if (bPermissionsState) {
                                        //Instantiating AList to hold singer names
                    arrayListSingerNames = new ArrayList<>();
                                        //instantiating object to access database
                    ElModelo elModelo = new ElModelo(getActivity());
                                        //Making sure MP3 ArrayList is not null and it's empty
                    if(arrayListFilePathsSDCard != null){
                        arrayListFilePathsSDCard.clear();
                    }
                    else {
                        arrayListFilePathsSDCard = new ArrayList<String>();
                    }
                                        //Deleting all data from DB full list table /Making sure
                    elModelo.deleteAllRecordsOnSingerListTable();
                                        //Getting data from SD Card and inserting it into ArrayList
                    arrayListFilePathsSDCard.addAll(elModelo.getArrayListOfFiles());
                                        //Getting singer names from SD Card list NO REPEATED NAMES
                    getArrayListSingerNames();
                                        //Sorting NO REPEATED singer names list in ascend order
                    Collections.sort(arrayListSingerNames);
                                        //Populating Db singer list table with new data from SD Card
                    elModelo.insertMultipleRecordsIntoSingerListTable(arrayListSingerNames);
                                        //Clearing ArrayList old data.
                    arrayListSingerNameTable.clear();
                                        //Adding singer names to AList with data from DB singer list table
                    arrayListSingerNameTable = elModelo.getAllSingerNamesFromDBSingerListTable("NO SE USA");
                                        //Updating ListView with new data from DB singer list table
                    muxListViewAdapter.getData().clear();
                    muxListViewAdapter.getData().addAll(arrayListSingerNameTable);
                    muxListViewAdapter.createHashMap();
                    muxListViewAdapter.notifyDataSetChanged();
                    singerSongMode = false;
                                        //Checking tb full list if empty-populate with SD Card data
                    if (elModelo.isTableEmpty(DBAccessHelper.TABLE_FULL_LIST) == true) {
                        l("table es EMPTY -- POPULATING FULL LIST TABLE:   " + arrayListFilePathsSDCard.size());
                        elModelo.insertMultipleRecordsIntoFullListTable(arrayListFilePathsSDCard);
                    }                   //Just for debugging may be deleted later
                    else {
                        l("TB F-LIST NOT EMPTY");
                    }

                    elModelo = null;
                }
                else {
                    l("NO PUEDE ACCEDER LA MEMORIA PORQUE LOS PERMISOS NO HAN SIDO OTORGADOS");
                }
            }
        });                             //End of update button click listener setting

        return view;

    }   //End of onCreateView() function




    /**
     * getArrayListSingerNames() function is used to extract all the singer names that are stored
     * in the SD Card. arrayListFilePathsSDCard contains all MP3 file path info. This file path
     * info contains the singer names. These singer names may be repeated many times, so this
     * function is also used to eliminated all singer names that are repeated. It just pushes
     * one copy of each singer names into the arrayListSingerNames. This ArrayList is used to
     * populate the singer name list table.
     * It just returns null since the ArrayLists that it uses to get just one copy of all singer
     * names are global in this class.
     *
     * @return type ArrayList
     */
    private ArrayList<String> getArrayListSingerNames(){
                                        //Looping the whole MP3 file path list searching singer names
        for (String str : arrayListFilePathsSDCard){
                                        //Separating String into substring to get substring that
                                        //contains the singer name
            String[] strArr = str.split("/");
                                        //Substring containing the singer name
            String singerNameSong = strArr[strArr.length - 1];
                                        //Separating singer name and MP3 file music name
            String[] singerName = singerNameSong.split("-");
            if (singerName.length > 1) {
                                        //Substring with index zero contains the singer name
                if (arrayListSingerNames.size() <= 0) {
                                        //Pushing first singer name into the ArrayList
                    arrayListSingerNames.add(singerName[0].toLowerCase());
                }
                else {
                                        //Eliminating singer names repeated
                    if (!arrayListSingerNames.contains(singerName[0].toLowerCase())) {
                        arrayListSingerNames.add(singerName[0].toLowerCase());
                    }
                }
            }
        }

        return null;

    }   //End of getArrayListSingerNames() function




    /**
     * onActivityCreated(Bundle) function is used to initialize, build and populate the ListView
     * with the singer names that are contained in the database Singer List table. It also instantiate
     * the ListView adapter tha will help to build the ListView.
     *
     * @param savedInstanceState type Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
                                        //boolean to know if any update was done by user
        blPlayListUpdated = false;
                                        //Creating object to access the local database
        ElModelo elModelo = new ElModelo(getContext());
                                        //Getting all singer names from DB table
        arrayListSingerNameTable = elModelo.getAllSingerNamesFromDBSingerListTable("NO SE USA");
                                        //Instantiating the ListView adapter
        muxListViewAdapter = new MuxListViewAdapter(getActivity(), arrayListSingerNameTable);
                                        //Getting the fragment ListView
        listView = getListView();
                                        //Setting ListView adapter
        setListAdapter(muxListViewAdapter);
                                        //Setting the ListView listener object
        listView.setOnItemClickListener(this);

    }   //End of onActivityCreated() function




    /**
     * setAddDeleteBtnNewState() function is not used in this version of this app.
     *
     */
    private void setAddDeleteBtnNewState(){
        if(btnSingerAddSinger != null) {
            btnSingerAddSinger.setTextColor(Color.rgb(0, 0, 55));
            btnSingerAddSinger.setEnabled(false);
        }

        vwLastRowSelected.setBackgroundColor(Color.rgb(10, 10, 10));
        vwLastRowSelected = null;
    }   //End of setAddDeleteBtnNewState() function




    /**
     * onAttach(Activity) function is used to initialize a global class variable of type interface
     * that is used to communicate with the class that is hosting this set of fragments.
     * It does not return any value.
     *
     * @param activity type Activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallBack = (SingerListFragTab.SingerInterfaceListener) activity;
        }
        catch (ClassCastException ccex) {
            ccex.printStackTrace();
        }

    }   //End of onAttach() function




    /**
     * onItemClick(AdapterView, View, int, long) callback function is used to detect when one
     * of the rows ListView of this fragment is clicked. This ListView shows user two kinds of
     * data. One kind of data is the list of all the MP3-singer that the SD Card contains. It
     * also shows user the list of all the MP3-songs that the clicked singer sings.
     * When this fragment is put on the top of the stack the ListView show the list of singer
     * names, when one of this rows is clicked the data is changed to show MP3-songs sing by the
     * singer.
     *
     * @param parent type AdapterVi
     * @param view type View
     * @param position type int
     * @param id type long
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //positionClicked = position;     //Global variable holds ListView row clicked
                                        //Enabling add button to add a row-song name to DB singer list
        btnSingerAddSinger.setEnabled(true);
        btnSingerAddSinger.setTextColor(Color.rgb(0, 0, 255));
                                        //MP3 File Path Info Mode
                                        //Updating LView with all MP3 file with singer name clicked
        if (singerSongMode == false) {
                                        //Getting singer name that the user clicked
            String strMK = ((TextView)view.findViewById(R.id.txtvwMP3FilePath)).getText().toString();
            ElModelo elModelo = new ElModelo(getContext());
            localArrayListSingerName.clear();
                                        //Getting all songs with the singer name clicked by user
            localArrayListSingerName.addAll(elModelo.getSingerNames(strMK.trim().toLowerCase()));
                                        //Full list tb needs to be populated before getting all songs
            if (localArrayListSingerName.size() <= 0) {
                Toast.makeText(getContext(), "DB may empty. Update Full List table.", Toast.LENGTH_LONG).show();
            }
                                        //Updating LView if every thing above Ok
            muxListViewAdapter.getData().clear();
            muxListViewAdapter.getData().addAll(localArrayListSingerName);
            muxListViewAdapter.createHashMap();
            muxListViewAdapter.notifyDataSetChanged();
                                        //Letting other functions there was one data update
            singerSongMode = true;
        }
                                        //Singer Name Mode
        else {                          //Updating LVIew with singer names data
            positionAddPl = position;   //Row number clicked to used to insert MP3 file into DB
                                        //High Lighting the row that was clicked
            if (vwLastRowSelected != null) {
                vwLastRowSelected.setBackgroundColor(Color.rgb(100, 0, 0));
                vwLastRowSelected = null;
            }
                                        //Highlighting present row selected
            view.setBackgroundColor(Color.rgb(230, 230, 230));
                                        //Last row selected becomes to be present row clicked
            vwLastRowSelected = view;
        }                               //End of singer name mode

    }   //End of onItemClick() function




    /**
     * checkStorageAccessPermission() function is used to check if permission to access storage
     * has been granted already. It asks for that permission if user has not granted that
     * permission yet. It set a boolean variable to let part of this app that storage access
     * is Ok.
     */
    private void checkStorageAccessPermission(){
                                        //if to check if this SDK is Nougat version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        //Checking if storage access has been granted
            int permisoGranted = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permisoGranted == PackageManager.PERMISSION_GRANTED) {
                                        //Permissions to access storage have been granted already
                bPermissionsState = true;
            }
            else {
                bPermissionsState = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                        //App does need to info user why storage access is needed
                    Toast.makeText(getActivity(), "Se necesita acceder la memoria para leer los musical file", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_ACCESS_CODE);
                }
                else {
                                        //App does not need to info user why storage access is needed
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_ACCESS_CODE);
                }
            }
        }
        else {
                                        //Code section to handle storage access on SDK less than N
            l("Esta es una version de android mas vieja que NOUGAT");
        }

    }   //End of checkStorageAccessPermission() function




    /**
     * onRequestPermissionsResult(int, String[], int[]) function is used to manage the user
     * decision of permission granting. bPermissionsState variable is set tp true or false
     * based on user decision. This variable is used to allow this app to access main storage
     * and SD Card storage.
     *
     * @param requestCode type int
     * @param permissions type String[]
     * @param grantResults type int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
                                        //Permission granted case
            case PERMISSION_ACCESS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //l("Permission Granted:  " + grantResults.length + "  -  " + grantResults[0]);
                    bPermissionsState = true;
                }
                break;
                                        //User denied permission to access storage
            default:
                //l("ALGO SALIO MAL CON LOS PERMISOS. EN LA FUNCION ONREQUESTPERMISSIONRESULT");
                bPermissionsState = false;
                break;
        }

    }   //End of onRequestPermissionsResult () function




    /**
     * SingerInterfaceListenerInterfaceListener interface is used to communicate with the class that
     * is hosting the set of fragments used in this app.  It defines just one function.
     */
    public interface SingerInterfaceListener {


        /**
         * Abstract comunicacionInterfaceFunction(boolean, int) function is meant to achieve a
         * link to the hosting class.
         *
         * @param boolUpdate type boolean
         * @param resultCode type int
         */
        void comunicacionInterfaceFunction(boolean boolUpdate, int resultCode);

    }   //End of ListaCompletaFrgmInterfaceListener interface




    /**
     * The l(String) function is used only to debug this class. It uses the Log.d() function to pass
     * the information to the Android Monitor window.
     * This information contains the class name and some information about the error or data
     * about the debugging process.
     *
     * @param str type String
     */
    private void l(String str){
        Log.d("NIKO", this.getClass().getSimpleName() + " -> " + str);
    }


}   //End of Class SingerListFragTab



/*********************************END OF FILE SingerListFragTab.java********************************/

