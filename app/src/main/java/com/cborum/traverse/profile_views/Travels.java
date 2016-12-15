package com.cborum.traverse.profile_views;

import android.app.Activity;
import android.widget.RelativeLayout;

/**
 * Created by Kristian Nielsen on 10-11-2016.
 */

public class Travels extends AbstractProfileView {

    protected static Travels instance;
    private static RelativeLayout tab;
    private int layout;
    private int headerId;

    protected Travels(int layout, RelativeLayout tab, int headerId) {
        this.layout = layout;
        this.tab = tab;
        this.headerId = headerId;
    }

    public static Travels getInstance(int layout, RelativeLayout tab, int headerId){
        return (Travels) getAbstractInstance(Travels.class, layout, tab, headerId);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getLayout() {
        return layout;
    }

    @Override
    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    public RelativeLayout getTab() {
        return tab;
    }

    @Override
    public void setTab(RelativeLayout tab) {
        this.tab = tab;
    }

    @Override
    public int getHeaderId() {
        return headerId;
    }

    @Override
    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }
}
