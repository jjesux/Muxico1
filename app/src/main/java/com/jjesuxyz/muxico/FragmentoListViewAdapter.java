package com.jjesuxyz.muxico;

import android.app.Activity;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;




/**
 * FragmentoListViewAdapter class is used to construct the layout of each row in the fragment
 * ListView. The listView calls the adapter getView function to get the View that hold the
 * row information.
 *
 * Created by jjesu on 6/13/2018.
 */

public class FragmentoListViewAdapter extends BaseAdapter {
                                        //List of global class variables
                                        //Reference to the MainActivity class
    private Activity activity;
                                        //ArrayList holding the list of songs to be played
    private ArrayList<String> arrayListFilePaths;
                                        //TextViews in each row
    private TextView txtvwNumTmp;
    private TextView txtvwTxtTmp;
                                        //Layout of each ListView row
    private LayoutInflater inflater;
                                        //SD Card directory path
    private String strExternalStorage;





    /**
     * FragmentoListViewAdapter constructor is used to get a reference to the MainActivity class
     * and to get the ArrayList holding the list of song to be displayed in the fragment  ListView.
     * It is also used to initialize a variable to hold the SD Card directory path.
     *
     * @param activity type Activity
     * @param arrayList type ArrayList
     */
    public FragmentoListViewAdapter(Activity activity, ArrayList<String> arrayList){
        arrayListFilePaths = new ArrayList<>();
        arrayListFilePaths.addAll(arrayList);
        strExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.activity = activity;
        inflater = activity.getLayoutInflater();

    }   //End of FragmentoListViewAdapter constructor




    /**
     * getCount() function is used to returns the size of the ArrayList holding the list of songs
     * file paths to be shown in the ListView window.
     *
     * @return type int
     */
    @Override
    public int getCount() {
        return arrayListFilePaths.size();

    }   //End of getCount() function




    /**
     * getItem(int) function is used to the info of each row as a Object.
     *
     * @param position type int
     * @return type Object
     */
    @Override
    public Object getItem(int position) {
        return arrayListFilePaths.get(position);

    }   //End of getItem() function




    /**
     * getItemId(int) function is used to return the id of each widget in ListView rows.
     *
     * @param position type int
     * @return type long
     */
    @Override
    public long getItemId(int position) {
        return 0;

    }   //End of getItemId() function




    /**
     * getData() function is used to return the whole list of data that is shown in the
     * ListView of the fragments used in this app.
     *
     * @return type ArrayList
     */
    public ArrayList<String> getData() {
        return arrayListFilePaths;

    }   //End of getData() function




    /**
     * getView(int, View, ViewGroup) function is used to get the layout info of each row in the
     * ListView in each fragment implemented in this app.
     *
     * @param position type int
     * @param convertView type View
     * @param parent type ViewGroup
     * @return type View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
                                        //Inflating the ListView row layout
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_layout, null);
        }
                                        //Getting reference to the ListView row widgets
        txtvwNumTmp = convertView.findViewById(R.id.txtvwRowNumber);
        txtvwTxtTmp = convertView.findViewById(R.id.txtvwMP3FilePath);
                                        //Setting ListView row widgets
        txtvwNumTmp.setText(String.valueOf(position) + "  ");
        txtvwTxtTmp.setText(arrayListFilePaths.get(position).replace(strExternalStorage, ""));
                                        //Returning the View holding ListView row layout info
        return convertView;

    }   //End of getView() function


}   //End of Class FragmentoListViewAdapter



/****************************END OF FILE PFragmentoListViewAdapter.java****************************/


