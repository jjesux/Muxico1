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
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jjesu on 6/13/2018.
 */

public class AlbumListFragTab extends ListFragment
                                    implements AdapterViewAnimator.OnItemClickListener{

    private Button btnAlbumPlayAll;
    private Button btnAlbumAddAlbum;
    private Button btnAlbumDeleteAlbum;

    private View vwLastRowSelected = null;
    private AlbumInterfaceListener mCallBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.albums_layout, container, false);

        btnAlbumPlayAll = view.findViewById(R.id.btnPlayAllAlbum);
        btnAlbumPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "PLay All Albums", Toast.LENGTH_SHORT).show();
                mCallBack.comunicacionInterfaceFunction(false, 10);
            }
        });

        btnAlbumAddAlbum = view.findViewById(R.id.btnAddAlbum);
        btnAlbumAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTmp;
                if(vwLastRowSelected != null){
                    strTmp = ((TextView)vwLastRowSelected.findViewById(R.id.txtvwMP3FilePath)).getText().toString();
                }
                else{
                    strTmp = "ERROR ERROR";
                }
                Toast.makeText(getActivity(), "Adding to Album List:  " + strTmp, Toast.LENGTH_SHORT).show();

                setAddDeleteBtnNewState();
            }
        });


        btnAlbumDeleteAlbum = view.findViewById(R.id.btnDeleteAlbum);

        btnAlbumDeleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTmp;
                if(vwLastRowSelected != null){
                    strTmp = ((TextView)vwLastRowSelected.findViewById(R.id.txtvwMP3FilePath)).getText().toString();
                }
                else{
                    strTmp = "ERROR ERROR";
                }

                Toast.makeText(getActivity(), "Deleting From Album List: " + strTmp, Toast.LENGTH_SHORT).show();

                setAddDeleteBtnNewState();
            }
        });


        return view;
    }


    private void setAddDeleteBtnNewState(){
        if(btnAlbumAddAlbum != null) {
            btnAlbumAddAlbum.setTextColor(Color.rgb(0, 0, 55));
            btnAlbumAddAlbum.setEnabled(false);
        }
        if(btnAlbumDeleteAlbum != null) {
            btnAlbumDeleteAlbum.setTextColor(Color.rgb(0, 0, 55));
            btnAlbumDeleteAlbum.setEnabled(false);
        }

        vwLastRowSelected.setBackgroundColor(Color.rgb(10, 10, 10));
        vwLastRowSelected = null;

    }




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
            mCallBack = (AlbumInterfaceListener) activity;
        }
        catch (ClassCastException ccex) {
            ccex.printStackTrace();
        }

    }   //End of onAttach() function




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

    }   //End of onItemClick() function




    /**
     * AlbumInterfaceListenerInterfaceListener interface is used to communicate with the class that
     * is hosting the set of fragments used in this app.  It defines just one function.
     */
    public interface AlbumInterfaceListener {


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


}   //End of Class AlbumListFragTab



/*********************************END OF FILE AlbumListFragTab.java********************************/

