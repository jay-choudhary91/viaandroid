package com.deeptruth.app.android.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.akash.revealswitch.OnToggleListener;
import com.deeptruth.app.android.R;
import com.deeptruth.app.android.adapter.managementcontrolleradapter;
import com.deeptruth.app.android.applicationviavideocomposer;
import com.deeptruth.app.android.interfaces.appmanagementitemclick;
import com.deeptruth.app.android.interfaces.itemchanged;
import com.deeptruth.app.android.models.managementcontroller;
import com.deeptruth.app.android.utils.common;
import com.deeptruth.app.android.utils.config;
import com.deeptruth.app.android.utils.popupview;
import com.deeptruth.app.android.utils.xdata;
import com.deeptruth.app.android.views.customfonttextview;
import com.deeptruth.app.android.views.divideritemdecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class appmanagementfragment extends basefragment implements itemchanged,View.OnClickListener {

    View rootview;
    @BindView(R.id.recyview_item)
     RecyclerView recyview_item;

    @BindView(R.id.txt_title_actionbarcomposer)
    customfonttextview txt_title_actionbar;
    @BindView(R.id.img_arrow_back)
    ImageView img_arrow_back;


    ArrayList<managementcontroller> managementcontrollers = new ArrayList<>();
    managementcontrolleradapter mcontrolleradapter;
    popupview mpopupview;

    @Override
    public int getlayoutid() {
        return R.layout.fragment_appmanagement;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootview == null)
        {
            rootview = super.onCreateView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, rootview);
            gethelper().drawerenabledisable(false);

            txt_title_actionbar.setText("App Management");
            img_arrow_back.setOnClickListener(this);

            getdata();

            mcontrolleradapter = new managementcontrolleradapter(getActivity(),managementcontrollers,mItemClick);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyview_item.setLayoutManager(mLayoutManager);
            recyview_item.addItemDecoration(new divideritemdecoration(applicationviavideocomposer.getactivity()));
            recyview_item.setItemAnimator(new DefaultItemAnimator());
            recyview_item.setAdapter(mcontrolleradapter);

        }
        return rootview;
    }

    @Override
    public void onItemChanged(Object object) {

    }

    appmanagementitemclick mItemClick=new appmanagementitemclick() {
        @Override
        public void onItemClicked(Object object) {

            mpopupview = new popupview();
            managementcontroller managecontroller = (managementcontroller) object;

            if(managecontroller.getTxtName().equals(config.LIST_In_APP_PURCHASE) && managecontroller.getAction().isEmpty())
            {
                /*InAppPurchaseControllerFragment frag=new InAppPurchaseControllerFragment();
                getHelper().addFragment(frag, false, true);*/
            }
            else if(managecontroller.getTxtName().equals(config.LIST_XAPI) && managecontroller.getAction().isEmpty())
            {
                mpopupview.showPopupView(getActivity(),getString(R.string.enter_url),getString(R.string.enter_complete_url),managecontroller,mitemchanged);
            }else if(managecontroller.getTxtName().equals(config.LIST_SETTINGS) && managecontroller.getAction().isEmpty())
            {
                fatchsettingvaluefragment addOfferFragment=new fatchsettingvaluefragment();
                gethelper().addFragment(addOfferFragment, false, true);
            }

        }

        @Override
        public void onAddSettingClicked(Object object) {


        }
    };

    itemchanged mitemchanged=new itemchanged() {
        @Override
        public void onItemChanged(Object object) {

        }
    };



    public void  getdata(){

      managementcontroller managementControllers = new managementcontroller(config.LIST_XAPI,false);
      managementcontrollers.add(managementControllers);

      managementControllers = new managementcontroller(config.LIST_SETTINGS,true);
      managementcontrollers.add(managementControllers);

      managementControllers = new managementcontroller(config.LIST_In_APP_PURCHASE,false);
      managementcontrollers.add(managementControllers);


  }

    @Override
    public void onHeaderBtnClick(int btnid) {
        super.onHeaderBtnClick(btnid);
        switch (btnid)
        {
            case R.id.img_backarrow:
                gethelper().onBack();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_arrow_back:
                gethelper().onBack();
                break;
        }
    }
}
