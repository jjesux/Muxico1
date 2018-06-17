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
 * Created by jjesu on 6/13/2018.
 */

public class FragmentoListViewAdapter extends BaseAdapter {


    private Activity activity;
    // String[] strArray;
    private ArrayList<String> arrayListFilePaths;
    private TextView txtvwNumTmp;
    private TextView txtvwTxtTmp;

    private LayoutInflater inflater;
    private String strExternalStorage;


    public FragmentoListViewAdapter(Activity activity, ArrayList<String> arrayList){
        arrayListFilePaths = new ArrayList<>();
        arrayListFilePaths.addAll(arrayList);
        strExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.activity = activity;
        inflater = activity.getLayoutInflater();
    }





    @Override
    public int getCount() {
        return arrayListFilePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListFilePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public ArrayList<String> getData() {
        return arrayListFilePaths;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.row_layout, null);
        }

        txtvwNumTmp = (TextView) convertView.findViewById(R.id.txtvwRowNumber);
        txtvwTxtTmp = (TextView) convertView.findViewById(R.id.txtvwMP3FilePath);

        txtvwNumTmp.setText(String.valueOf(position) + "  ");
        txtvwTxtTmp.setText(arrayListFilePaths.get(position).replace(strExternalStorage, ""));

        return convertView;
    }
}
