package com.deeptruth.app.android.enumclasses;

import com.deeptruth.app.android.R;

/**
 * Created by ${matraex} on 12/4/19.
 */

public enum cryptomediatypepagerenum
{
    Item1("", R.layout.row_composemediatype,0),
    Item2("CryptoVIDEO", R.layout.row_composemediatype,1),
    Item3("CryptoPHOTO", R.layout.row_composemediatype,2),
    Item4("CryptoAUDIO", R.layout.row_composemediatype,3),
    Item5("", R.layout.row_composemediatype,4);

    private String itemname;
    private int mLayoutResId;
    private int itemposition;

    cryptomediatypepagerenum(String itemname, int layoutResId, int itemposition) {
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
