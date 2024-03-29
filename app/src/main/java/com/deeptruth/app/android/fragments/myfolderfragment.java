package com.deeptruth.app.android.fragments;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deeptruth.app.android.BuildConfig;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.folderdataadapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.database.databasemanager;
import com.deeptruth.app.android.interfaces.adapteritemclick;
import com.deeptruth.app.android.models.folder;
import com.deeptruth.app.android.utils.appdialog;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.xdata;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.RelativeLayout.TRUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class myfolderfragment extends basefragment implements View.OnClickListener{

    View rootview;
    public myfolderfragment() {
        // Required empty public constructor
    }

    @BindView(R.id.recycler_list)
    RecyclerView recyclerlist;
    @BindView(R.id.layout_mediatype)
    LinearLayout layout_mediatype;
    @BindView(R.id.root_view)
    LinearLayout root_view;

    @BindView(R.id.txt_title_actionbarcomposer)
    TextView txt_title_actionbarcomposer;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;
    @BindView(R.id.img_camera)
    ImageView img_camera;
    @BindView(R.id.img_dotmenu)
    ImageView img_dotmenu;

    adapteritemclick mitemclick=null;
    folderdataadapter adapter;
    ArrayList<folder> folderitemlist =new ArrayList<>();
    private final int request_permissions = 101;
    private Runnable doafterallpermissionsgranted;
    boolean isfullscreen = false;
    int navigationbarheight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        if (rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            navigationbarheight =  common.getnavigationbarheight();
            setlayoutmargin();
            // setheadermargin();
            img_camera.setVisibility(View.VISIBLE);
            txt_title_actionbarcomposer.setText("MyFolders");
            img_arrow_back.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);
            setheaderpadding();
            //gethelper().setwindowfitxy(true);
           // layout_mediatype.setPadding(0,Integer.parseInt(xdata.getinstance().getSetting("statusbarheight")),0,0);


            if(BuildConfig.FLAVOR.contains(config.build_flavor_reader))
            {
                img_camera.setVisibility(View.GONE);
            }
            else
            {
                if(common.isdevelopermodeenable())
                {

                }
                img_camera.setVisibility(View.VISIBLE);
            }

            int numberOfColumns = 2;
            recyclerlist.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
            adapter = new folderdataadapter(getActivity(), folderitemlist, new adapteritemclick() {
                @Override
                public void onItemClicked(Object object) {
                }
                @Override
                public void onItemClicked(Object object, int type) {
                    folder myfolder=(folder)object;
                    if(type == 1)
                    {
                        createdirectory();  // Clicked on plus icon
                    }
                    else if(type == 2)
                    {

                    }
                    else if(type == 3)  // Clicked on folder items (Not plus icon case)
                    {
                        xdata.getinstance().saveSetting(config.selected_folder,myfolder.getFolderdir());
                        fragmentmedialist fragmatriclist=new fragmentmedialist();
                        fragmatriclist.shouldlaunchcomposer(false);
                        gethelper().replaceFragment(fragmatriclist, true, false);
                    }
                    else if(type == 4)   // Delete prompt
                    {
                        String message="Are you sure to delete "+myfolder.getFoldername()+ " folder or directory?";
                        showdeletealert(message,myfolder);
                    }
                }
            });
            recyclerlist.setAdapter(adapter);
        }
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        getallpermissions();
    }

    public void getallpermissions() {
        if (doafterallpermissionsgranted != null) {
            doafterallpermissionsgranted.run();
            doafterallpermissionsgranted = null;
        } else {
            if (common.getstoragedeniedpermissions().isEmpty()) {
                // All permissions are granted

                doafterallpermissionsgranted();

            } else {
                String[] array = new String[common.getstoragedeniedpermissions().size()];
                array = common.getstoragedeniedpermissions().toArray(array);
                ActivityCompat.requestPermissions(applicationviavideocomposer.getactivity(), array, request_permissions);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request_permissions) {
            boolean permissionsallgranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsallgranted = false;
                    break;
                }
            }
            if (permissionsallgranted) {
                doafterallpermissionsgranted = new Runnable() {
                    @Override
                    public void run() {
                        doafterallpermissionsgranted();
                    }
                };
            } else {
                getallpermissions();
            }
        }
    }

    private void doafterallpermissionsgranted() {
        fetchfoldersfromdirectory();
    }


    public void showdeletealert(String message, final folder myfolder){
        new AlertDialog.Builder(getActivity(),R.style.customdialogtheme)
                .setTitle("Alert!!")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(new File(myfolder.getFolderdir()).exists())
                                common.delete(new File(myfolder.getFolderdir()));

                            if(adapter != null)
                                fetchfoldersfromdirectory();

                            if(mitemclick != null)
                                mitemclick.onItemClicked(null);

                            if(dialog != null)
                                dialog.dismiss();
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null)
                            dialog.dismiss();
                    }
                }).show();
    }

    public void createdirectory()
    {
        appdialog.showcreatedirectorydialog(applicationviavideocomposer.getactivity(), new adapteritemclick() {
            @Override
            public void onItemClicked(Object object) {
                String foldername=(String)object;
                String directoryname=config.dirmedia +File.separator+foldername;
                File file=new File(directoryname);

                if (!file.exists())
                    file.mkdirs();

                if(file.exists())
                {
                    folderitemlist.remove(folderitemlist.size()-1);
                    folderitemlist.add(new folder(file.getName(),file.getAbsolutePath(),"","",false,false));
                    folderitemlist.add(new folder("",false,true));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemClicked(Object object, int type) {

            }
        });
    }

    @Override
    public int getlayoutid() {
        return R.layout.fragment_myfolder;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
          case R.id.img_arrow_back:
            gethelper().onBack();
            break;

          case R.id.img_camera:
            composeoptionspagerfragment fragbottombar=new composeoptionspagerfragment();
            gethelper().replaceFragment(fragbottombar, false, true);
            break;

          case R.id.img_dotmenu:
            settingfragment settingfrag=new settingfragment();
            gethelper().addFragment(settingfrag, false, true);
            break;
        }
    }
    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid){
            case R.id.img_backarrow:
                gethelper().onBack();
                break;
        }
    }

    public void fetchfoldersfromdirectory()
    {
        folderitemlist.clear();
        adapter.notifyDataSetChanged();

        File rootdir = new File(config.dirmedia);
        if(! rootdir.exists())
            rootdir.mkdirs();

        File allmediadir = new File(config.dirallmedia);
        if(! allmediadir.exists())
            allmediadir.mkdirs();

        File[] files = rootdir.listFiles();
        for (File file : files)
        {
            if(! file.getName().equalsIgnoreCase(config.cachefolder))
            {
                String[] thumbnailpath=getfolderthumbnailpath(file.getAbsolutePath());
                if(file.getName().equalsIgnoreCase(config.allmedia))
                {
                    folderitemlist.add(new folder(file.getName(),file.getAbsolutePath(),thumbnailpath[0],thumbnailpath[1],true,false));
                }
                else
                {
                    folderitemlist.add(new folder(file.getName(),file.getAbsolutePath(),thumbnailpath[0],thumbnailpath[1],false,false));
                }
            }
        }
        folderitemlist.add(new folder("",false,true));
        adapter.notifyDataSetChanged();
    }

    public String[] getfolderthumbnailpath(String directorypath)
    {
        String thumbnailurl="";
        String[] filedirectoryinfo ={"",""};
        databasemanager mdbhelper=null;
        if (mdbhelper == null) {
            mdbhelper = new databasemanager(applicationviavideocomposer.getactivity());
            mdbhelper.createDatabase();
        }

        try {
            mdbhelper.open();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Cursor cursor = mdbhelper.getallmediabyfolder(directorypath);
        int videocount=0,audiocount=0,imagecount=0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            do{
                String mediafilepath = "" + cursor.getString(cursor.getColumnIndex("mediafilepath"));
                String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));
                String type = "" + cursor.getString(cursor.getColumnIndex("type"));

                if(! new File(mediafilepath).exists())
                {
                    mdbhelper.deletefromstartvideoinfobylocalkey(localkey);
                    mdbhelper.deletefrommetadatabylocalkey(localkey);
                }
                else
                {
                    if(type.equalsIgnoreCase(config.item_video))
                    {
                        videocount++;
                    }
                    else if(type.equalsIgnoreCase(config.item_image))
                    {
                        imagecount++;
                    }
                    else if(type.equalsIgnoreCase(config.item_audio))
                    {
                        audiocount++;
                    }

                    if(thumbnailurl.trim().isEmpty())
                        thumbnailurl= "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));
                }

            }while(cursor.moveToNext());

            filedirectoryinfo[0] = thumbnailurl;
            String strcounter="";
            if(videocount > 0)
                strcounter="Video("+videocount+")";

            if(imagecount > 0)
            {
                if(strcounter.trim().isEmpty())
                {
                    strcounter="Photo("+imagecount+")";
                }
                else
                {
                    strcounter=strcounter+", "+"Photo("+imagecount+")";
                }
            }
            if(audiocount > 0)
            {
                if(strcounter.trim().isEmpty())
                {
                    strcounter="Audio("+audiocount+")";
                }
                else
                {
                    strcounter=strcounter+", "+"Audio("+audiocount+")";
                }
            }
            filedirectoryinfo[1]=strcounter;
        }

        try
        {
            mdbhelper.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  filedirectoryinfo;
    }

    public void setdata(adapteritemclick mitemclick) {
        this.mitemclick = mitemclick;
    }

    public void setheader(boolean isfullscreen){
        this.isfullscreen = isfullscreen;
    }

    public void setlayoutmargin(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,0,navigationbarheight);
        root_view.setLayoutParams(params);
        root_view.requestLayout();
    }

    public void setheaderpadding(){
       /* if(isfullscreen){
            layout_mediatype.setPadding(0,Integer.parseInt(xdata.getinstance().getSetting("statusbarheight")),0,0);

        }else{
            layout_mediatype.setPadding(0,0,0,0);

        }*/
    }

    public void setfooterlayout(boolean isfottermarginset){

        if(isfottermarginset){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,Integer.parseInt(xdata.getinstance().getSetting("headerheight")));
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP,TRUE);
            root_view.setLayoutParams(params);
        }
    }

}
