package com.jjesuxyz.muxico.DBData;

import java.util.ArrayList;

/**
 * Created by jjesu on 6/3/2018.
 */

public class DataAnalisis {

    ArrayList<String> arrayListFullListSDCard;
    ArrayList<String> arrayListPlaylist;
    ArrayList<String> arrayAlbumList;
    public static final String PLAYLIST_EMPTY = "PLAYLIST_IS_EMPTY";


    /**
     * Class construct used only to initialize member variables to null
     */
    public DataAnalisis(){
    }

    public ArrayList<String> getArrayListPlaylist(){
        return arrayListPlaylist;
    }
}
