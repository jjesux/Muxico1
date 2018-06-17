package com.jjesuxyz.muxico;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jjesu on 6/4/2018.
 */

public class BasePublicStorage extends AppCompatActivity{

    MainActivity mainActivity;
    MediaPlayer mp;
    Button btnDirectoryAccess, btnPlaySong;

    File storageRootDir = null, realSDCardDir = null, internalSDCardDir = null, realInternalDir = null;
    File storageDirSelected = null;
    Button btnCerrarAct;
    private AdapterStorageDir adapterStorageDir;
    private ArrayList<DirectoryInfo> arrayList;
    private String dirPath;
    private String dirDescription;

    private ListView listView;
    final int REQUEST_CODE = 342;
    private final int PERMISION_ACCESS_CODE = 3456;
    private boolean bPermissionsState = false;

    private final String t = "NIKO";


    public BasePublicStorage(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public BasePublicStorage(){
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_sdcard_dir);

        Intent data = getIntent();
        bPermissionsState = data.getBooleanExtra("boolPermiso", false);



        btnPlaySong = (Button) findViewById(R.id.btnPlayId);
        btnPlaySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp = new MediaPlayer();
                try {
                    mp.setDataSource("/storage/3061-6433/Caifanes - Detras de ti.mp3");
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        arrayList = new ArrayList();
        getStorageDirectories();

        adapterStorageDir = new AdapterStorageDir(getApplicationContext(), R.layout.storage_lv_row, arrayList);
        listView = (ListView) findViewById(R.id.listDirStorageId);
        listView.setAdapter(adapterStorageDir);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dirPath = ((DirectoryInfo)parent.getItemAtPosition(position)).getStrDirPath();
                dirDescription = ((DirectoryInfo)parent.getItemAtPosition(position)).getStrDirInfo();
                if (dirPath.equals("emulated")) {
                    storageDirSelected = new File(storageRootDir + "/" + dirPath + "/0");
                }
                else if (dirPath.equals("self")) {
                    storageDirSelected = new File(storageRootDir + "/" + dirPath + "/primary");
                }
                else {
                    storageDirSelected = new File(storageRootDir + "/" + dirPath);
                }
                Toast.makeText(getApplicationContext(), "Dir. Selected: " + storageDirSelected.getAbsolutePath(), Toast.LENGTH_LONG).show();
                l("Path of selected directory: " + storageDirSelected.getAbsolutePath());
                l("Description of selected directory: " + dirDescription);
                Intent intent = new Intent();
                intent.putExtra("DirSelected", storageDirSelected.getAbsolutePath());
                //BasePublicStorage.this.setIntent(intent);
                BasePublicStorage.this.setResult(RESULT_OK, intent);
                finish();


            }
        });

    }

    public void getStorageDirectories(){
        if (bPermissionsState == true) {
            File dirPath = Environment.getExternalStorageDirectory().getParentFile().getParentFile();
            storageRootDir = Environment.getExternalStorageDirectory().getParentFile().getParentFile();


            l("File Music:  " + storageRootDir.getAbsolutePath());
            for (String strDirPath : storageRootDir.list()) {
                l("Directory Path: " + strDirPath);
                arrayList.add(new DirectoryInfo(strDirPath, "Aqui va Dir Info"));
            }
        }
        else {
            l("Access Storage Permission has not been granted, yet. Ask User again.");
        }
    }



    public class AdapterStorageDir extends ArrayAdapter {

        private Context context;
        private ArrayList<DirectoryInfo> arrListDirInfo;

        private final String t = "NIKO";


        public AdapterStorageDir(@NonNull Context context, @LayoutRes int resource,
                                 @NonNull ArrayList<DirectoryInfo> aLDirInfo) {

            super(context, resource, aLDirInfo);
            this.context = context;
            arrListDirInfo = aLDirInfo;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            l("Funciono getView()");

            ViewHolder viewHolder;
            //viewHolder = new ViewHolder();

            if (convertView == null) {
                l("convertView is null, listview is being populated");
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storage_lv_row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.txtVwDirPath = (TextView) convertView.findViewById(R.id.txtVwdirPathId);
                viewHolder.txtVwDirDescription = (TextView) convertView.findViewById(R.id.txtVwDescriId);
                convertView.setTag(viewHolder);
            } else {
                l("convertView is being reused. ListView is being scroll-up/down");
                viewHolder = (ViewHolder) convertView.getTag();
                if (viewHolder == null) {
                    l("convertView ERROR: viewholder is null");
                } else {
                    l("convertView ERROR: NO ES NULL");
                }
            }

            //txtVwDirPath.setText("DirPath: " + arrListDirInfo.get(position));
            //txtVwDirDescri.setText("Directory information...");
            l("List View funcion despues del el if-else");

            viewHolder.txtVwDirPath.setText(arrListDirInfo.get(position).getStrDirPath());
            viewHolder.txtVwDirDescription.setText(arrListDirInfo.get(position).getStrDirInfo());

            l("List View funcion antes del return");

            return convertView;
        }





        private void l(String str) {
            Log.d(t, this.getClass().getSimpleName() + " -> " + str);
        }

    }

    static class ViewHolder {
        protected TextView txtVwDirPath;
        protected TextView txtVwDirDescription;
    }

    public class DirectoryInfo {

        private String strDirPath;
        private String strDirInfo;

        public DirectoryInfo(String dirPath, String dirInfo){
            strDirPath = dirPath;
            strDirInfo = dirInfo;
        }

        public String getStrDirPath(){
            return strDirPath;
        }

        public String getStrDirInfo(){
            return strDirInfo;
        }
    }


    private void l(String str){
        Log.d(t, this.getClass().getSimpleName() + " -> " + str);
    }
}
