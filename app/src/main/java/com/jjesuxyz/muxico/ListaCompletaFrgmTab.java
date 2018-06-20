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
import android.widget.Toast;

import java.util.ArrayList;




/**
 * ListaCompletaFrgmTab class is used to manage the UI fragment to let user manage the music
 * library play list and full list tables. It shows a ListView to show data to user, it shows
 * buttons to let user manage that data. User can add one record at time to the play list table.
 * It also let user to update local ListView data with data from the full list table. It allows
 * user to go back to the main UI app.
 *
 * Created by jjesu on 6/8/2018.
 */

public class ListaCompletaFrgmTab extends ListFragment
                                    implements AdapterViewAnimator.OnItemClickListener{

                                        //Global class variable declaration
    private View view;
                                        //Variables to holds MP3 file paths
    private ArrayList<String> arrayListFilePathsToPlayListTb;
    private ArrayList<String> arrayListFilePathsSDCard;
    private ArrayList<String> arrayListFilePathsFulllistTable;

    private ListView listView;          //It shows file paths to user
    private int positionClicked = -1;
                                        //Buttons to control/modify data and UI
    private Button btnPlayAllFiles;
    private Button btnAddSongToPlaylist;
    private Button btnUpdateDBFullList;
                                        //To manage ListView rows selected
    private View vwLastRowSelected = null;
                                        //Local interface to communicate with hosting class
    private ListaCompletaFrgmInterfaceListener mCallback;
                                        //Variable set to manage permission to access device storage
    private final int PERMISSION_ACCESS_CODE = 3456;
    private boolean bPermissionsState = false;
                                        //To detect data update
    private boolean blPlayListUpdated = false;
                                        //ListView Adapter
    private MuxListViewAdapter muxListViewAdapter;




    /**
     * onCreateView(LayoutInflater, ViewGroup, Bundle) function is used to draw the fragment
     * UI and to initialize the buttons that will modify the data in the ListView window.
     * These buttons are used to update the ListView data from the database full list table,
     * to add records to the database play list table and to stop end destroy the fragments
     * and go back to the main user interface.
     *
     * @param inflater type LayoutInflater
     * @param container type ViewGroup
     * @param savedInstanceState type Bundle
     * @return type View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.lista_completa_layout, container, false);

                                        //Instantiating ArrayList to hold MP3 files
        arrayListFilePathsToPlayListTb = new ArrayList<>();

                                        //Button to go back to the main UI
        btnPlayAllFiles = view.findViewById(R.id.btnPlayAllFiles);
        btnPlayAllFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                                        //Letting know hosting class if there were change in LV data
                    mCallback.comunicacionInterfaceFunction(blPlayListUpdated, 10);
                }
            }
        });                             //End of button click listener setting


                                        //Button to add one row/data to DB play list table
        btnAddSongToPlaylist = view.findViewById(R.id.btnAddOneFileToPlaylist);
        btnAddSongToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                        ////Enabling button ?????????
                blPlayListUpdated = true;
                                        //Instantiating object to access database to get file paths
                ElModelo elModelo = new ElModelo(getContext());
                                        //Inserting one record to the DB play list table
                elModelo.insertMultipleRecordsIntoPlayListTable(arrayListFilePathsToPlayListTb);
                                        //Clearing ArrayList for new mp3 file to insert into table
                arrayListFilePathsToPlayListTb.clear();

                //setAddUpdateBtnNewState();
            }
        });                             //End of button click listener setting


                                        //Button to update the ListView window data from the DB
        btnUpdateDBFullList =  view.findViewById(R.id.btnUpdateDBFullList);
        btnUpdateDBFullList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStorageAccessPermission();

                if (bPermissionsState) {
                                        //Making sure MP3 ArrayList is not null and empty
                    if(arrayListFilePathsSDCard != null){
                        arrayListFilePathsSDCard.clear();
                    }
                    else {
                        arrayListFilePathsSDCard = new ArrayList<String>();
                    }
                                        //Instantiating object to access database to get file paths
                    ElModelo elModelo = new ElModelo(getActivity());
                                        //Deleting all data from DB full list table
                    elModelo.deleteAllRecordsOnFullListTable();
                                        //Getting data from SD Card and inserting it into ArrayList
                    arrayListFilePathsSDCard.addAll(elModelo.getArrayListOfFiles());
                                        //Populating Db full list table with new data from SD Card
                    elModelo.insertMultipleRecordsIntoFullListTable(arrayListFilePathsSDCard);
                                        //Clearing ArrayList old data
                    arrayListFilePathsFulllistTable.clear();
                                        //Adding MP3's to ArrayList with data from DB full list table
                    arrayListFilePathsFulllistTable = elModelo.getAllMP3FilePathFromDBFulllistTable("NO SE USA");
                                        //Updating ListView with new data from DB full list table
                    muxListViewAdapter.getData().clear();
                    muxListViewAdapter.getData().addAll(arrayListFilePathsFulllistTable);
                    muxListViewAdapter.createHashMap();
                    muxListViewAdapter.notifyDataSetChanged();

                    elModelo = null;
                }
                else {
                    l("NO PUEDE ACCEDER LA MEMORIA PORQUE LOS PERMISOS NO HAN SIDO OTORGADOS");
                }

            }
        });                             //End of button click listener setting


                                        //View holding info to build the UI of the fragment
        return view;

    }   //End of onCreateView() function




    /**
     * onActivityCreated(Bundle) function is used to initialize, build and populate the ListView
     * with the MP3 file paths that are in the database Play List table. It also instantiate
     * the ListView adapter tha will help to build the ListView.
     *
     * @param savedInstanceState type Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
                                        //Creating object to access the local database
        ElModelo elModelo = new ElModelo(getContext());

        arrayListFilePathsFulllistTable = elModelo.getAllMP3FilePathFromDBFulllistTable("NO SE USA");
                                        //Instantiating the ListView adapter
        muxListViewAdapter = new MuxListViewAdapter(getActivity(), arrayListFilePathsFulllistTable);
                                        //Getting the fragment ListView
        listView = getListView();
                                        //Setting ListView adapter
        setListAdapter(muxListViewAdapter);
                                        //Setting the ListView listener object
        listView.setOnItemClickListener(this);

    }   //End of onActivityCreated() function




    /**
     * onItemClick(AdapterView, View, int, long) callback function is used to detect when a
     * ListView row is clicked or selected.
     *
     * @param parent type AdapterVi
     * @param view type View
     * @param position type int
     * @param id type long
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        positionClicked = position;     //Global variable holds ListView row clicked
                                        //Enabling add button to add a row-song name to DB play list
        btnAddSongToPlaylist.setEnabled(true);
        btnAddSongToPlaylist.setTextColor(Color.rgb(0, 0, 255));
                                        //Updating data on ArrayList holding all MP3 files
        l("Posicion:  " + position);
       arrayListFilePathsToPlayListTb.add(arrayListFilePathsFulllistTable.get(position));
                                        //Highlighting last row selected
        if(vwLastRowSelected != null){
            vwLastRowSelected.setBackgroundColor(Color.rgb(100, 0, 0));
            vwLastRowSelected = null;
        }
                                        //Highlighting present row selected
        view.setBackgroundColor(Color.rgb(230, 230, 230));
                                        //Last row selected becomes to be present row clicked
        vwLastRowSelected = view;

    }   //End of onItemClick() function




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
                                        //mCallback variable used to have a link to hosting class.
        try {
            mCallback = (ListaCompletaFrgmInterfaceListener) activity;
        }
        catch (ClassCastException ccex){
            ccex.printStackTrace();
        }

    }   //End of onAttach() function




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
     * ListaCompletaFrgmInterfaceListener interface is used to communicate with the class that
     * is hosting the set of fragments used in this app.  It defines just one function.
     */
    public interface ListaCompletaFrgmInterfaceListener {

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

    }   //End of l() function



}   //End of Class ListaCompletaFrgmTab



/***********************************END OF FILE ListaCompletaFrgmTab.java**********************************/

