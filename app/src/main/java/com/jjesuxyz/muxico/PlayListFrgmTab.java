package com.jjesuxyz.muxico;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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
 * PlayListFrgmTab class used to manage the UI fragment to let user manage the music library
 * play list. It shows a ListView to show data to user, it shows buttons to let user manage that
 * data. User can delete data one record at time or all the data with just one click. It allows
 * user to go back to the main UI app.
 *
 * Created by jjesu on 6/8/2018.
 */

public class PlayListFrgmTab extends ListFragment
                                    implements AdapterViewAnimator.OnItemClickListener{

                                        //Global class variable declaration
    private View view;
                                        //It holds file paths
    private ArrayList<String> arrayListFilePaths;
    private ListView listView;          //It shows file paths to user
    private int positionClicked = -1;   //Row number selected
                                        //Buttons to control/modify data and UI
    private Button btnPlaylistPlayAll;  //TO go back to main UI app
    private Button btnPlaylistUpdate;   //To update UI data from DB
                                        //To delete one row of data
    private Button btnPlaylistDeleteASong;
                                        //To delete row/data in ListView UI and DB
    private Button btnPlaylistClearList;
                                        //To manage ListView rows selected
    private View vwLastRowSelected = null;
                                        //Local interface to communicate with hosting class
    private PlayListInterfaceListener mCallback;
                                        //Adapter for the ListView
    private MuxListViewAdapter muxListViewAdapter;
                                        //To detect data update
    private boolean blPlayListUpdated = false;




    /**
     * onCreateView(LayoutInflater, ViewGroup, Bundle) function is used to draw the fragment
     * UI and to initialize the buttons that will modify the data in the ListView window.
     * These buttons are to update the ListView data from the database PlayList table, to
     * clear the ListView data, to delete one row of data at a time and to stop end destroy
     * the fragments and go back to the main user interface.
     *
     * @param inflater type LayoutInflater
     * @param container type ViewGroup
     * @param savedInstanceState type Bundle
     * @return type View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.playlist_layout, container, false);

                                        //Button to go back to the main UI
        btnPlaylistPlayAll = view.findViewById(R.id.btnPlayAllPlaylist);
        btnPlaylistPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null) {
                                        //Letting know hosting class if there were change in LV data
                    mCallback.comunicacionInterfaceFunction(blPlayListUpdated, 1);
                }
            }
        });                             //End of button click listener setting


                                        //Button to update the ListView window data from the DB
        btnPlaylistUpdate =  view.findViewById(R.id.btnUpdatePlaylist);
        btnPlaylistUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListFilePaths != null){
                    arrayListFilePaths.clear();
                    arrayListFilePaths = null;
                }
                                        //Instantiating object to access database to get file paths
                ElModelo elModelo = new ElModelo(getContext());
                                        //making sure ArrayList is not null
                arrayListFilePaths = new ArrayList<>();
                                        //Populating file paths ArrayList with database data
                arrayListFilePaths.addAll(elModelo.getArrayListFromPlayListTable());
                                        //Making sure ArrayList is not null
                if (arrayListFilePaths.size() >= 0) {
                                        //Sending info -new data- to the ListView adapter
                    muxListViewAdapter.getData().clear();
                    muxListViewAdapter.getData().addAll(arrayListFilePaths);
                    muxListViewAdapter.createHashMap();
                    muxListViewAdapter.notifyDataSetChanged();
                                        //Resetting ArrayList and buttons to new states
                    arrayListFilePaths.clear();
                    blPlayListUpdated = true;
                    btnPlaylistClearList.setEnabled(true);
                    btnPlaylistClearList.setTextColor(Color.rgb(0, 0, 255));
                }
                else {
                    Toast.makeText(getContext(), "Play List is empty,", Toast.LENGTH_SHORT).show();
                }
                                        ////Nulling ArrayList to control how to manage data
                arrayListFilePaths = null;
                elModelo = null;
            }
        });                             //End of button click listener setting

                                        //Button to delete one row/data of the ListView window
        btnPlaylistDeleteASong = view.findViewById(R.id.btnPlaylistDeleteSong);
        btnPlaylistDeleteASong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTmp;
                if(vwLastRowSelected != null){
                    strTmp = muxListViewAdapter.getItem(positionClicked).toString();
                                        //Instantiating object to access database to get file paths
                    ElModelo elModelo = new ElModelo(getContext());
                                        //Deleting one record from the database playlist table
                    int delResult = elModelo.deleteOneRowOnPlayListTable(strTmp);
                                        //Nulling db access object to control how to manage data
                    elModelo = null;
                                        ////Enabling button ?????????
                    blPlayListUpdated = true;
                                        //making a button click programmatically to accomplish
                                        //the data update
                    btnPlaylistUpdate.performClick();
                }
                else{
                                        //If some error happen, it just a hint
                    strTmp = "ERROR ERROR";
                }
                Toast.makeText(getActivity(), "Playlist Deleting: " + strTmp, Toast.LENGTH_SHORT).show();
                                        //Changing the delete button state to enable/disable
                setAddDeleteBtnNewState();
            }
        });                             //End of button click listener setting

                                        //Button to clear all the ListView window data
        btnPlaylistClearList =  view.findViewById(R.id.btnPlaylistClearList);
        btnPlaylistClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Playlist Deleting list... ", Toast.LENGTH_SHORT).show();
                                        //Instantiating object to access database to get file paths
                ElModelo elModelo = new ElModelo(getContext());
                                        //deleting all data in the ListView window and in the db table
                int counter = elModelo.deleteAllRecordsOnPlayListTable();
                                        //making sure ArrayList is not null
                if(arrayListFilePaths == null) {
                    arrayListFilePaths = new ArrayList<String>();
                }
                                        //Clearing the ArrayList holding file paths
                arrayListFilePaths.clear();
                                        //Sending info -no data- to the ListView adapter
                muxListViewAdapter.getData().clear();
                muxListViewAdapter.getData().addAll(arrayListFilePaths);
                muxListViewAdapter.notifyDataSetChanged();
                                        //Nulling ArrayList to control how to manage data
                arrayListFilePaths = null;
                elModelo = null;
                                        //Enabling button ?????????
                blPlayListUpdated = true;
            }
        });                             //End of button click listener setting

                                        //View holding info to build the UI of the fragment
        return view;

    }   //End of onCreateView() function



    private ArrayList<String> getArrayListFilePaths() {
        return arrayListFilePaths;
    }


    /**
     *  setAddDeleteBtnNewState() function is used to change the state of the delete button,
     *  enable/unable, and to change the color of the button. These state changes occur when
     *  there is some data in the ListView UI that can be deleted.
     */
    private void setAddDeleteBtnNewState(){
                                        //Un-enabling the button
        if(btnPlaylistDeleteASong != null){
            btnPlaylistDeleteASong.setTextColor(Color.rgb(0, 0, 55));
            btnPlaylistDeleteASong.setEnabled(false);
        }
                                        //modifying the background of the row selected
        vwLastRowSelected.setBackgroundColor(Color.rgb(10, 10, 10));
        vwLastRowSelected = null;

    }   //End of setAddDeleteBtnNewState() function



    /**
     * onActivityCreated(Bundle) function is used to initialize, build and populate the ListView
     * with the MP3 file paths that are in the database Play List table. It also instantiate
     * the ListView adapter tha will help to build the ListView.
     *
     * @param savedInstanceState type Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
                                        //Creating object to access the local database
        ElModelo elModelo = new ElModelo(getContext());
                                        //Instantiating ArrayList to hold file paths from db
        arrayListFilePaths = new ArrayList<>();
        arrayListFilePaths.addAll(elModelo.getArrayListFromPlayListTable());
                                        //Disabling button to clear the play list table
        if (arrayListFilePaths.size() <= 0) {
            btnPlaylistClearList.setTextColor(Color.rgb(0, 0, 55));
            btnPlaylistClearList.setEnabled(false);
        }
                                        //Getting the fragment ListView
        listView = getListView();
                                        //Instantiating the ListView adapter
        muxListViewAdapter = new MuxListViewAdapter(getActivity(), arrayListFilePaths);
        listView.setAdapter(muxListViewAdapter);
                                        //Setting the ListView listener object
        listView.setOnItemClickListener(this);
                                        //Clearing the ArrayList holding file paths
        arrayListFilePaths.clear();
        arrayListFilePaths = null;

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
                                        //Enabling delete button to delete a row-song
        btnPlaylistDeleteASong.setEnabled(true);
        btnPlaylistDeleteASong.setTextColor(Color.rgb(0, 0, 255));
                                        //Highlighting last row selected
        if (vwLastRowSelected != null) {
            vwLastRowSelected.setBackgroundColor(Color.rgb(10, 10, 10));
            vwLastRowSelected = null;
        }
                                        //Highlighting present row selected
        view.setBackgroundColor(Color.rgb(50, 50, 50));
                                        //Last row selected becomes to be present row clicked
        vwLastRowSelected = view;

    }   //End of onItemClick() function



    /**
     * PlayListInterfaceListener interface is used to communicate with the class that is hosting
     * the set of fragments used in this app.  It defines just one function.
     */
    public interface PlayListInterfaceListener {

        /**
         * Abstract comunicacionInterfaceFunction(boolean, int) function is meant to achieve a
         * link to the hosting class.
         *
         * @param boolUpdate type boolean
         * @param resultCode type int
         */
        void comunicacionInterfaceFunction(boolean boolUpdate, int resultCode);

    }   //End of PlayListInterfaceListener interface



    /**
     * onAttach(Activity) function is used to initialize a global class variable of type interface
     * that is used to communicate with the class that is hosting this set of fragments.
     * It does not return any value.
     *
     * @param activity type Activity
     */
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try {                           //mCallback variable used to have a link to hosting class.
            mCallback = (PlayListInterfaceListener) activity;
        }
        catch (ClassCastException ccex) {
            ccex.printStackTrace();
        }
    }   //End of onAttach() function



    /**
     * onDestroyView() function is called by the system before the UI is destroyed. It is not
     * implemented in this app.
     */
    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }   //End of onDestroyView() function



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



}   //End of Class PlayListFrgmTab



/***********************************END OF FILE PlayListFrgmTab.java**********************************/

