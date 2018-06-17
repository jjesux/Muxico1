package com.jjesuxyz.muxico;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;




/**
 * MusLib class
 *
 * Created by jjesu on 6/6/2018.
 */

public class MusLib extends FragmentActivity
                        implements ActionBar.TabListener,
                                    ViewPager.OnPageChangeListener,
                                    ListaCompletaFrgmTab.ListaCompletaFrgmInterfaceListener,
                                    PlayListFrgmTab.PlayListInterfaceListener,
                                    AlbumListFragTab.AlbumInterfaceListener{

                                        //Set of varaibles to hold references to fragment activities
    private PlayListFrgmTab playListFrgmTab;
    private ListaCompletaFrgmTab listaCompletaFrgmTab;
    private AlbumListFragTab albumListFragTab;
                                        //ViewPage to achieve the swipe behavior of the fragments
    private ViewPager viewPager;
                                        //Swipe adapter for the view pager
    private SwipePageLibAdapter swipePageLibAdapter;
                                        //Set of variables to hold tabs references
    private ActionBar.Tab tab1;
    private final String PLAYLIST = "PLAY LIST";
    private ActionBar.Tab tab2;
    private final String ALL_MP3_FILES = "ALL MP3 FILES";
    private ActionBar.Tab tab3;
    private final String ALBUMS = "ALBUMS";
                                        //ActionBar widget
    private ActionBar actionBar;
                                        //Arrays holding MP3 file paths
    private ArrayList<String> arrayLIstFilePaths;
    private ArrayList<String> arrayListPlayList;
                                        //boolean to know if there were data change.
    private boolean blUpdateNeeded = false;




    /**
     * onCreate(Bundle) function is used to initialize ArrayList that will hold data from the
     * local database. It also defines the tabs that are going to be inserted into the action
     * bar. It defines these tabs before adding them into the ActionBar widget.
     *
     * @param savedInstanceState type Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mus_lib_layout);

        arrayListPlayList = null;
                                    //Instantiating ArrayList to hold dabase data
        arrayLIstFilePaths = new ArrayList<>();
        arrayLIstFilePaths.add("uno");
        arrayLIstFilePaths.add("dos");
        arrayLIstFilePaths.add("tres");
                                        //Getting the ActionBar widget to isert tabs
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                                        //Tab 1 to insert into ActionBar widget
        tab1 = actionBar.newTab();
        tab1.setText(PLAYLIST);
        tab1.setTabListener(this);
        actionBar.addTab(tab1);
                                        //Tab 2 to insert into ActionBar widget
        tab2 = actionBar.newTab();
        tab2.setText(ALL_MP3_FILES);
        tab2.setTabListener(this);
        actionBar.addTab(tab2);
                                        //Tab 3 to insert into ActionBar widget
        tab3 = actionBar.newTab();
        tab3.setText(ALBUMS);
        tab3.setTabListener(this);
        actionBar.addTab(tab3);
                                        //Instantiating the SwipePageAdapter
        swipePageLibAdapter = new SwipePageLibAdapter(getSupportFragmentManager());
                                        //Initializing ViewPager to hold fragments and swipe behavior
        viewPager = (ViewPager) findViewById(R.id.pager);
                                        //Setting the SwipeAdapter
        viewPager.setAdapter(swipePageLibAdapter);
                                        //Setting the ViewPage swipe change listener
        viewPager.setOnPageChangeListener(this);

    }   //End of onCreate() function



    /**
     * onTabSelected(ActionBar.Tab, FragmentTransaction) function is used to
     *
     * @param tab type ActionBar.Tab
     * @param ft type FragmentTransaction
     */
    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        if (viewPager != null) {
            viewPager.setCurrentItem(tab.getPosition());
        }
    }   //End of onTabSelected() function



    /**
     * onTabUnselected(ActionBar.Tab, android.app.FragmentTransaction function is not implemented
     * in this project yet.
     *
     * @param tab type ActionBar.Tab
     * @param ft type android.app.FragmentTransaction
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
    }   //End of onTabUnselected() function



    /**
     * onTabReselected(ActionBar.Tab, FragmentTransition) function is not implemented in this
     * project yet.
     *
     * @param tab
     * @param ft
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
    }   //End of onTabReselected() function



    /**
     * onPageScrolled(int, float, int) function is not implemented in this project yet.
     *
     * @param position type int
     * @param positionOffset type float
     * @param positionOffsetPixels type int
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }   //End of onPageScrolled() function



    /**
     * onPageSelected(int) function is used to
     *
     * @param position type int
     */
    @Override
    public void onPageSelected(int position) {
                                        //Log.d("NIKO", "onPageSelected(position: " + position);
        ActionBar actionB = getActionBar();
        if (actionB != null) {
            actionB.setSelectedNavigationItem(position);
        }

    }   //End of onPageSelected() function



    /**
     * onPageScrollStateChanged(int) function is used to
     *
     * @param state type int
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }   //End of onPageScrollStateChanged() function



    /**
     * comunicacionInterfaceFunction() function used to
     *
     * @param blUpdate type boolean
     * @param resultCode type int
     */
    @Override
    public void comunicacionInterfaceFunction(boolean blUpdate, int resultCode) {
        Intent intent = new Intent();
        intent.putExtra("UPDATE_MUS_LIB", blUpdate);

        setResult(MainActivity.RESULT_OK, intent);
        finish();

    }   //End of comunicacionInterfaceFunction() function



    /**
     * onBackPressed() function is used to
     */
    @Override
    public void onBackPressed(){
        setResult(MainActivity.RESULT_OK);
        super.onBackPressed();
    }   //End of onBackPressed() function



    /**
     * onDestroy() function is used to
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
    }   //End of onDestroy() function





    /**
     *
     */
    private class SwipePageLibAdapter extends FragmentPagerAdapter {


        /**
         *
         * @param fm type FragmentManager
         */
        public SwipePageLibAdapter(FragmentManager fm) {
            super(fm);
        }   //End of public class constructor


        /**
         *
         * @param position type int
         * @return type Fragment
         */
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    Bundle playListArgs = new Bundle();
                    playListArgs.putStringArrayList("LISTA", arrayLIstFilePaths);
                    PlayListFrgmTab playListFrag = new PlayListFrgmTab();
                    playListFrag.setArguments(playListArgs);
                    Log.d("NIKO", "getItem(pos 0)");
                    return playListFrag;

                case 1:
                    ListaCompletaFrgmTab listaCompletaFrgmTab = new ListaCompletaFrgmTab();
                    Log.d("NIKO", "getItem(1)");
                    return listaCompletaFrgmTab;
                case 2:
                    AlbumListFragTab albumListFragTab = new AlbumListFragTab();
                    Log.d("NIKO", "getiTEM(2)");
                    return albumListFragTab;
                default:
                    return null;
            }

        }   //End of getItem() function


        /**
         *
         * @return type int
         */
        @Override
        public int getCount() {
            return 3;

        }   //End of getCount() function

    }   //End of SwipePageLibAdapter private inner class

}   //End of Class MusLib



/***************************************END OF FILE MusLib.java************************************/

