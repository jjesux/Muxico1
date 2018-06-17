package com.jjesuxyz.muxico;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jjesu on 6/13/2018.
 */

public class AlbumListFragTab extends ListFragment{

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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallBack = (AlbumInterfaceListener) activity;
        }
        catch (ClassCastException ccex) {
            ccex.printStackTrace();
        }
    }

    public interface AlbumInterfaceListener {
        void comunicacionInterfaceFunction(boolean boolUpdate, int resultCode);
    }
}
