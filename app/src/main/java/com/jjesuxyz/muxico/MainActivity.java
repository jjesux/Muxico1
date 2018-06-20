package com.jjesuxyz.muxico;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjesuxyz.muxico.DBData.DBAccess;
import com.jjesuxyz.muxico.DBData.DataAnalisis;

import java.io.File;
import java.util.ArrayList;


/**
 * MainActivity class is the entry point of this app. It is used to create the main UI, ask
 * for permission access storage, get data from the DB play list table, set that data in the
 * ListView. It also sets and synchronizes the SeekBar widget with the song to be played. It
 * also ask the class controller to synchronize its ArrayList data from the db play list
 * table.
 */
public class MainActivity extends ListActivity {
                                        //Context passed to other classes
    private Context contexto = this;
                                        //Class to control MP3 file playing
    private ElControl elControl = null;
    private MediaPlayer mdPlayer = null;
                                        //Set TextView to show info about song playing
    private TextView txtvwPlayingSongName;
    private TextView txtvwSongCurrentTime;
    private TextView txtvwSongTotalTime;
                                        //Set of main window buttons and ID's variables
    private Button btnPlay;             //ID's are used in a switch block
    private final int BTN_PLAY_ID = R.id.btnPlayId;
    private Button btnNext;
    private final int BTN_PLAYNEXT_ID = R.id.btnNextId;
    private Button btnPrevious;
    private final int BTN_PLAYPREVIOUS_ID = R.id.btnPreviousId;
    private Button btnStop;
    private final int BTN_STOPPLAY_ID = R.id.btnStopId;
    private Button btnPause;
    private final int BTN_PAUSEPLAY_ID = R.id.btnPauseId;
    private Button btnLoop;
    private final int BTN_LOOP_ID = R.id.btnLoopId;
    boolean btnLoopState = false;
                                        //Button to display music library
    private Button btnMusLib;
    private final int BTN_MUS_LIB_ID = R.id.btnMusLibId;
                                        //Button to turn the radio on
    private Button btnRadio;
    private final int BTN_RADIO_ID = R.id.btnRadioId;
                                        //Button to turn the equalizer on
    private Button btnSoundEqualizer;
    private final int BTN_SOUNDEQUALIZER_ID = R.id.btnSoundEqualizerId;
                                        //Class to handle buttons events
    private UIBotonListener uiBtnEventMngmt;
                                        //List of song in the device sdcard
    private ArrayList<String> arrListPlaylistFromDB;
                                        //ListView to show list of song
    private ListView lView;
                                        //ListView Adapter
    MuxListViewAdapter muxListViewAdapter;
                                        //Represent the song being played progress
    private SeekBar seekBar;
                                        //State of the pause button
    private boolean bBtnPauseState = false;
                                        //Variable set to manage permission to access device storage
    private final int PERMISION_ACCESS_CODE = 3456;
    private boolean bPermissionsState = false;

    private final int RESULT_FROM_ACTIVITY = 20;

    public static final String UPDATE_NEEDED = "UPDATE_MUS_LIB";
                                        //Variable to get data from DB play list table
    ElModelo elModelo;






    /**
     * onCreate(Bundle) function is used to check storage access, initialize variables, get
     * data from DB and display this data to the user. It also set the ListView click listener
     * class and the ListView adapter. It also set the SeekBar widget change listener.
     *
     * @param savedInstanceState type Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                                        //Checking and asking permission to access storage
        checkStorageAccessPermission();

                                        //TextView variables initialization
        txtvwPlayingSongName = findViewById(R.id.txtvwPlayingSongNameId);
        txtvwSongCurrentTime = findViewById(R.id.txtvwSongCurrentTimeId);
        txtvwSongTotalTime = findViewById( R.id.txtvwSongTotalTimeId);
                                        //Player will loop the list of song
        btnLoopState = true;

                                        //Button variables, & listener initialization
        setButtons();
                                        //Buttons Click event listener
        uiBtnEventMngmt = new UIBotonListener();
        setButtonsClickListener();


                                        //ListView Setting
        elModelo = new ElModelo(contexto);
        arrListPlaylistFromDB = elModelo.getArrayListFromPlayListTable();
        muxListViewAdapter = new MuxListViewAdapter(this, arrListPlaylistFromDB);
                                        //Music controller setting
        elControl = new ElControl(MainActivity.this);
        elControl.setArrayListPlaylist(arrListPlaylistFromDB);

        lView = getListView();
                                        //Setting the ListView Adapter
        lView.setAdapter(muxListViewAdapter);
                                        //ListView listener implementation
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvw = view.findViewById(R.id.txtvwMP3FilePath);
                Toast.makeText(MainActivity.this, tvw.getText(), Toast.LENGTH_SHORT).show();
                                        //Playing song by clicking one in the ListView of songs
                elControl.playSongFromList(position);
            }
        });
                                        //End of ListView code

                                        //Setting seek bar to show the progress of song being played
                                        //SeekBar variable initialization
        seekBar = findViewById(R.id.seekBId);
                                        //SeekBar listener implementation
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mdPlayer = elControl.getMdPlayer();
                                        //Song is being playing
                    if(mdPlayer != null && progress < elControl.getSongTotalTime()) {
                        mdPlayer.seekTo(progress * 1000);
                    }
                    else{               //Song ended or it is not being played
                        seekBar.setProgress(0);
                    }
                }

            }
                                        //Function part of Listener, not implemented in this app
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
                                        //Function part of Listener, not implemented in this app
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

        });                             //End of SeekBar implementation

    }   //End of onCreate() function




    /**
     * getMyListView() function is used just to return the main window ListView.
     *
     * @return lView type ListView
     */
    public ListView getMyListView(){
        return lView;

    }   //End of getMyListView() function



    /**
     * setbBtnPauseState() function is used to change the pause button text/state. The text is
     * changed so the user can know when the pause button is on or off.
     *
     * //NOTE: ESTA FUNCtION NO SE USA EN ESTA VERSION DE ESTA APPLICAtION!!!!!!!!!!!!!!!
     *
     * @param bBtnPauseState type boolean
     */
    public void setbBtnPauseState(boolean bBtnPauseState) {
        if(bBtnPauseState) {
            this.btnPause.setText("ON");
        }
        else{
            btnPause.setText("");       //Indicate that the pause process is off.
        }

    }   //End of setbBtnPauseState() function




    /**
     * setBtnSoundEqualizerState() function is used to change the text on the equalizer
     * button. This text is on or off depending if the equalizer is on or off.
     *
     * @param stateONOFF type String
     */
    public void setBtnSoundEqualizerState(String stateONOFF){
        btnSoundEqualizer.setText(stateONOFF);

    }   //End of setBtnSoundEqualizerState() function




    /**
     * setTxtvwPlayingSongName() function is used to set the text on the TextView that
     * contains the name of the song being played.
     *
     * @param actualPlayingSongName type String
     */
    public void setTxtvwPlayingSongName(String actualPlayingSongName){
        File file = new File(actualPlayingSongName);
                                        //Displaying song name being played
        txtvwPlayingSongName.setText(file.getName());

    }   //End of setTxtvwPlayingSongName() function




    /**
     * setTxtvwSongTotalTime() function is used to set the text on the TextView that
     * contains time that the song being played last.
     *
     * @param strTiempoTotal type String
     */
    public void setTxtvwSongTotalTime(String strTiempoTotal){
        txtvwSongTotalTime.setText(strTiempoTotal);

    }   //End of setTxtvwSongTotalTime() function




    /**
     * setTxtvwSongCurrentTime() function is used to set the current progress time of
     * the song being played.
     *
     * @param strCurrentTime type String
     */
    public void setTxtvwSongCurrentTime(String strCurrentTime){
        txtvwSongCurrentTime.setText(strCurrentTime);

    }   //End of setTxtvwSongCurrentTime() function




    /**
     * setSeekBarProgress() function is used to set the SeekBar progress of the song
     * being playing.
     *
     * @param progreso type int
     */
    public void setSeekBarProgress(int progreso){
        seekBar.setProgress(progreso);

    }   //End of setSeekBarProgress() function




    /**
     * setSeekBarMax() function is used to set the maximum value that the SeekBar will
     * handle.
     *
     * @param songTiempoTotal type int
     */
    public void setSeekBarMax(int songTiempoTotal){
        seekBar.setMax(songTiempoTotal);

    }   //End of setSeekBarMax() function




    /**
     * setButtons() function is used to get all the buttons that are in the main UI window.
     * All these buttons are defined in the main_activity layout file.
     *
     */
    private void setButtons(){
        btnPlay =   findViewById(R.id.btnPlayId);
        btnNext =   findViewById(R.id.btnNextId);
        btnPrevious = findViewById(R.id.btnPreviousId);
        btnStop =   findViewById(R.id.btnStopId);
        btnPause =  findViewById(R.id.btnPauseId);
        btnLoop =   findViewById(R.id.btnLoopId);
        btnMusLib = findViewById(R.id.btnMusLibId);
        btnRadio =  findViewById(R.id.btnRadioId);
        btnSoundEqualizer = findViewById(R.id.btnSoundEqualizerId);

    }   //End of setButtons() function




    /**
     * setButtonsClickListener function is used to set the event listener class that
     * will handle the events of all these buttons.
     *
     */
    private void setButtonsClickListener(){
        btnPlay.setOnClickListener(uiBtnEventMngmt);
        btnNext.setOnClickListener(uiBtnEventMngmt);
        btnPrevious.setOnClickListener(uiBtnEventMngmt);
        btnPause.setOnClickListener(uiBtnEventMngmt);
        btnStop.setOnClickListener(uiBtnEventMngmt);
        btnLoop.setOnClickListener(uiBtnEventMngmt);
        btnMusLib.setOnClickListener(uiBtnEventMngmt);
        btnRadio.setOnClickListener(uiBtnEventMngmt);
        btnSoundEqualizer.setOnClickListener(uiBtnEventMngmt);

    }   //End of setButtonsClickListener() function




    /**
     * UIBotonListener is the only inner class that the main class contains. It is used to
     * handle all the buttons reaction to user clicks on these buttons.
     * This class contains only one function onClick(View vw). This function handles all the
     * buttons reaction.
     */
    private class UIBotonListener implements View.OnClickListener{

        /**
         * onClick function used to handle all button click events. These events are play
         * audio, play previous or next audio, stop or pause audio, set continuous mode,
         * activate the system equalizer to improve sound.
         * All these events are handle in the ElControl class.
         *
         * @param vw type View
         */
        @Override
        public void onClick(View vw) {

            switch (vw.getId()){
                                        //Right now start playing the song
                case BTN_PLAY_ID:
                                        //Checking music playing list is not empty
                    if (arrListPlaylistFromDB.size() >= 1) {
                        elControl.setiSongPlayingNumber(0);
                        elControl.playSong("BOTON");
                        txtvwPlayingSongName.setText(elControl.getCurrentPlayingSongName());
                        txtvwSongCurrentTime.setText("0");
                    }
                    break;
                                        //Playing next song right now
                case BTN_PLAYNEXT_ID:
                                        //Checking music playing list is not empty
                    if (arrListPlaylistFromDB.size() >= 1) {
                        btnPause.setText("");
                        elControl.playNextSong();
                    }
                    break;
                                        //Playing previous song right now
                case BTN_PLAYPREVIOUS_ID:
                                        //Checking music playing list is not empty
                    if (arrListPlaylistFromDB.size() >= 1) {
                        btnPause.setText("");
                        elControl.playPreviousSong();
                    }
                    break;
                                        //Pause song being played
                case BTN_PAUSEPLAY_ID:
                                        //Checking music playing list is not empty
                    if (arrListPlaylistFromDB.size() >= 1) {
                        elControl.pausePlayingSong();
                    }
                    break;
                                        //Stop the song being played
                case BTN_STOPPLAY_ID:
                                        //Checking music playing list is not empty
                    if (arrListPlaylistFromDB.size() >= 1) {
                        elControl.stopPlaying();
                        btnPause.setText("");
                    }
                    break;
                                        //Not implemented yet
                case BTN_LOOP_ID:
                    elControl.setBtnLoopState(true);
                    ;
                    break;
                                        //Start activity to manage play list library list
                case BTN_MUS_LIB_ID:
                    Intent intentoMusLib = new Intent(contexto, MusLib.class);
                    startActivityForResult(intentoMusLib, RESULT_FROM_ACTIVITY);
                    break;
                                        //Activate sound equalizer
                case BTN_SOUNDEQUALIZER_ID:
                    if(elControl.getMdPlayer() == null && !elControl.isMdPlayerPlaying()){
                                        //Asking user to start playing a song first
                        Toast.makeText(getApplicationContext(), "Start playing a song first.",
                                                                        Toast.LENGTH_SHORT).show();
                    }
                    else {
                                        //Starting the sound equalizer activation process.
                                        //This is done in ElControl class.
                        elControl.setEqualizerState();
                    }
                    break;
                                        //Activate radio feature
                case BTN_RADIO_ID:
                    l("Starting radio intent");
                                        //Stoping mp3 playing and equalizer
                    elControl.stopPlaying();
                    elControl.releaseEqualizer(0);
                                        //Starting the radio turn on activity
                    Intent intento = new Intent(contexto, NKRadio.class);
                    intento.putExtra("NIKO", "Este es el NIKOLAZO");
                    startActivity(intento);
                    break;

            }   //End of switch()

            if (arrListPlaylistFromDB.size() <= 0) {
                Toast.makeText(getApplicationContext(), "List Song is empty.", Toast.LENGTH_SHORT).show();
            }
        }   //End of OnClick() function

    }   //End of inner class UIBotonListener




    /**
     * onActivityResult(int, int, Intent) callback function is used to manage user updates on the
     * play list data make on another user interface. It synchronizes-shows new data to user
     * and it asks the controlling class to update and synchronize it data and functioning with
     * the updated data.
     *
     * @param requestCode type int
     * @param resultCode type int
     * @param data type Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_FROM_ACTIVITY && resultCode == RESULT_OK) {
                                        //Getting info to know if user did any data update
            boolean myBool = data.getBooleanExtra(UPDATE_NEEDED, true);
                                        //if to handle data updates done by user
            if (myBool) {
                                        //Clearing ArrayList to get new updated data
                if (arrListPlaylistFromDB != null) {
                    arrListPlaylistFromDB.clear();
                }
                                        //Getting updated data from DB play list table
                arrListPlaylistFromDB = elModelo.getArrayListFromPlayListTable();
                                        //if-making sure ArrayList is not null or empty
                if (arrListPlaylistFromDB != null && arrListPlaylistFromDB.size() == 0) {
                                        //Synchronizing data functioning on controling class
                    elControl.stopPlaying();
                    elControl.getArrayListPlayList().clear();
                    elControl.setiSongPlayingNumber(0);
                                        //Setting info about song that maybe playing now
                    txtvwPlayingSongName.setText(DataAnalisis.PLAYLIST_EMPTY);
                    txtvwSongCurrentTime.setText("0");
                    txtvwSongTotalTime.setText("0");
                }
                                        //Updating list adapter with updated data from DB
                muxListViewAdapter.getData().clear();
                muxListViewAdapter.getData().addAll(arrListPlaylistFromDB);
                muxListViewAdapter.createHashMap();
                muxListViewAdapter.notifyDataSetChanged();
                                        //Asking controlling class to update its play list data from db
                elControl.setArrayListPlaylist(arrListPlaylistFromDB);
                                        //Adjusting song number when it is bigger than ArrayList size
                if (elControl.getiSongPlayingNumber() >= arrListPlaylistFromDB.size()) {
                    elControl.setiSongPlayingNumber(arrListPlaylistFromDB.size() - 1);
                }
            }
            else {
                                        //User did not do any update on play list library
                l("There was not any update done by user on the database play list table");
            }
        }

    }   //End of onActivityResult() function




    /**
     * onDestroy() function is called when the back button of the device is clicked.
     * It stops the music if any is been playing, then destroy the application.
     *
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
                                        //Stopping the music being playing by another class
        elControl.stopPlaying();
        elControl.releaseEqualizer(0);

    }   //End of onDestroy() function



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
            int permisoGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permisoGranted == PackageManager.PERMISSION_GRANTED) {
                                        //Permissions to access storage have been granted already
                bPermissionsState = true;
            }
            else {
                bPermissionsState = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                        //App does need to info user why storage access is needed
                    Toast.makeText(this, "Storage access is needed to search MP3 files.", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISION_ACCESS_CODE);
                }
                else {
                                        //App does not need to info user why storage access is needed
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISION_ACCESS_CODE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                                    @NonNull int[] grantResults) {

        switch (requestCode) {
                                        //Permission granted case
            case PERMISION_ACCESS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bPermissionsState = true;
                }
                break;
                                        //User denied permission to access storage
            default:
                bPermissionsState = false;
                break;
        }

    }   //End of onRequestPermissionsResult () function


    @Override
    public void onBackPressed() {

        super.onBackPressed();
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
        Log.d("NIKO", this.getClass().getSimpleName() + " -> " + str);

    }   //End of l() function



}   //End of Class MainActivity



/***********************************END OF FILE MainActivity.java**********************************/
