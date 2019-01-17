package com.cryptoserver.composer.fragments;


import android.content.DialogInterface;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptoserver.composer.R;
import com.cryptoserver.composer.adapter.adaptermediagrid;
import com.cryptoserver.composer.adapter.folderdataadapter;
import com.cryptoserver.composer.applicationviavideocomposer;
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.folder;
import com.cryptoserver.composer.models.video;
import com.cryptoserver.composer.utils.appdialog;
import com.cryptoserver.composer.utils.common;
import com.cryptoserver.composer.utils.config;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
                        createdirectory();
                    }
                    else if(type == 2)
                    {

                    }
                    else if(type == 3)
                    {
                        Toast.makeText(getActivity(),myfolder.getFoldername(),Toast.LENGTH_SHORT).show();
                    }
                    else if(type == 4)
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

                            arraylist.remove(myfolder);
                            adapter.notifyDataSetChanged();

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
                String directoryname=config.rootdir+File.separator+foldername;
                File file=new File(directoryname);

                if (!file.exists())
                    file.mkdirs();

                if(file.exists())
                {
                    arraylist.remove(arraylist.size()-1);
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),0,false,false));
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

        File rootdir = new File(config.rootdir);
        if(! rootdir.exists())
            return;

        File[] files = rootdir.listFiles();
        for (File file : files)
        {
            if(! file.getName().equalsIgnoreCase("cache"))
            {
                File[] folderfiles = file.listFiles();
                int filecount=folderfiles.length;

                if(file.getName().equalsIgnoreCase(config.allmedia))
                {
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),filecount,true,false));
                }
                else
                {
                    arraylist.add(new folder(file.getName(),file.getAbsolutePath(),filecount,false,false));
                }
            }
        }
        arraylist.add(new folder("",false,true));
    }
}
