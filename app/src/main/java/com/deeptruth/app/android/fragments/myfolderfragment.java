package com.deeptruth.app.android.fragments;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    ArrayList<folder> arraylist=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        if (rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            fetchfoldersfromdirectory();

            img_camera.setVisibility(View.VISIBLE);
            txt_title_actionbarcomposer.setText("MyFolders");
            img_arrow_back.setOnClickListener(this);
            img_camera.setOnClickListener(this);
            img_dotmenu.setOnClickListener(this);

            if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
            {
                img_camera.setVisibility(View.GONE);
            }
            else
            {
                img_camera.setVisibility(View.VISIBLE);
            }

            int numberOfColumns = 2;
            recyclerlist.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
            adapter = new folderdataadapter(getActivity(),arraylist, new adapteritemclick() {
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
                        if(BuildConfig.FLAVOR.equalsIgnoreCase(config.build_flavor_reader))
                        {
                            medialistreader fragmatriclist=new medialistreader();
                            gethelper().replaceFragment(fragmatriclist, true, false);
                        }
                        else
                        {
                            fragmentmedialist fragmatriclist=new fragmentmedialist();
                            fragmatriclist.shouldlaunchcomposer(false);
                            gethelper().replaceFragment(fragmatriclist, true, false);
                        }
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
                            {
                                arraylist.clear();
                                adapter.notifyDataSetChanged();
                                fetchfoldersfromdirectory();
                                adapter.notifyDataSetChanged();
                            }

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
                    arraylist.remove(arraylist.size()-1);
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),"","0",false,false));
                    arraylist.add(new folder("",false,true));
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

        File rootdir = new File(config.dirmedia);
        if(! rootdir.exists())
            return;

        File[] files = rootdir.listFiles();
        for (File file : files)
        {
            if(! file.getName().equalsIgnoreCase("cache"))
            {
                String[] thumbnailpath=getfolderthumbnailpath(file.getAbsolutePath());
                if(file.getName().equalsIgnoreCase(config.allmedia))
                {
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),thumbnailpath[0],thumbnailpath[1],true,false));
                }
                else
                {
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),thumbnailpath[0],thumbnailpath[1],false,false));
                }
            }
        }
        arraylist.add(new folder("",false,true));
    }

    public String[] getfolderthumbnailpath(String directorypath)
    {
        String thumbnailurl="";
        String[] filedirectoryinfo ={"","0"};
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
        int filecount=0;
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            do{
                filecount++;
                String mediafilepath = "" + cursor.getString(cursor.getColumnIndex("mediafilepath"));
                String localkey = "" + cursor.getString(cursor.getColumnIndex("localkey"));

                if(! new File(mediafilepath).exists())
                {
                    filecount--;
                    mdbhelper.deletefromstartvideoinfobylocalkey(localkey);
                    mdbhelper.deletefrommetadatabylocalkey(localkey);
                }
                else
                {
                    if(thumbnailurl.trim().isEmpty())
                        thumbnailurl= "" + cursor.getString(cursor.getColumnIndex("thumbnailurl"));
                }

            }while(cursor.moveToNext());

            filedirectoryinfo[0] = thumbnailurl;
            filedirectoryinfo[1]=""+filecount;
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
}
