package com.cborum.traverse.profile_views;

import android.app.Activity;
import android.widget.RelativeLayout;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Kristian Nielsen on 11-11-2016.
 */

public abstract class AbstractProfileView {


    // Abstract Metode til at få en ny instans af en singleton-klasse, der nedarver fra AbstractProfileView
    // Kræver at den nedarvende klasse har et "instance" felt
    public static Object getAbstractInstance(Class<?> cls, int layout, RelativeLayout tab, int headerId){
        Object instance = null;
        try {
            instance = cls.getDeclaredField("instance").get(null);
            if(instance == null){
                instance = cls
                        .getDeclaredConstructor(int.class, tab.getClass(), int.class)
                        .newInstance(layout, tab, headerId);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return instance;
    }

    protected AbstractProfileView(){

    }

    public abstract void onLoad();

    public abstract void onDestroy();

    public abstract int getLayout();

    public abstract void setLayout(int layout);

    public abstract RelativeLayout getTab();

    public abstract void setTab(RelativeLayout tab);

    public abstract int getHeaderId();

    public abstract void setHeaderId(int headerId);

}
