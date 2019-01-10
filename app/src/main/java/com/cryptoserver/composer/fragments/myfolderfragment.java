package com.cryptoserver.composer.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.cryptoserver.composer.interfaces.adapteritemclick;
import com.cryptoserver.composer.models.folder;
import com.cryptoserver.composer.models.video;

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

    folderdataadapter adapter;
    ArrayList<folder> arraylist=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        if (rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);

            arraylist.add(new folder("All Media",true,false));
            arraylist.add(new folder("",false,true));

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
                    if(type == 1)
                    {
                        arraylist.remove(arraylist.size()-1);
                        arraylist.add(new folder("Folder Title",false,false));
                        arraylist.add(new folder("",false,true));
                        adapter.notifyDataSetChanged();
                    }
                    else if(type == 2)
                    {

                    }
                    else if(type == 3)
                    {
                        Toast.makeText(getActivity(),"Folder Title",Toast.LENGTH_SHORT).show();
                    }
                    else if(type == 4)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            recyclerlist.setAdapter(adapter);
        }
        return rootview;
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

}
