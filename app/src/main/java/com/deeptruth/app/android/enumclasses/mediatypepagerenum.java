package com.deeptruth.app.android.enumclasses;

import com.deeptruth.app.android.R;

/**
 * Created by ${matraex} on 12/4/19.
 */

public enum mediatypepagerenum
{
    Item1("", R.layout.row_composemediatype,0),
    Item2("", R.layout.row_composemediatype,1),
    Item3("PHOTO", R.layout.row_composemediatype,2),
    Item4("VIDEO", R.layout.row_composemediatype,3),
    Item5("AUDIO", R.layout.row_composemediatype,4),
    Item6("", R.layout.row_composemediatype,5),
    Item7("", R.layout.row_composemediatype,6);

    private String itemname;
    private int mLayoutResId;
    private int itemposition;

    mediatypepagerenum(String itemname, int layoutResId, int itemposition) {
        this.itemname = itemname;
        mLayoutResId = layoutResId;
        this.itemposition = itemposition;
    }

    public int getItemposition() {
        return itemposition;
    }


    public String getItemname() {
        return itemname;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
