package com.example.criminalintent;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by jarod on 5/19/2017.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager(); //Responsible for calling the lifecycle methods
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragment = createFragment();
            //onattach, oncreate, oncreateview called in the fragment when added to the fragment manager
            //onactivitycreated called after onCreate of hosting activity
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
    }
    protected abstract Fragment createFragment();
    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }
}
