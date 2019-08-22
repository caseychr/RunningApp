package com.runningapp.runningapp;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.runningapp.runningapp.injector.ActivityComponent;
import com.runningapp.runningapp.injector.ContextModule;
import com.runningapp.runningapp.injector.DaggerActivityComponent;

public class MainActivity extends AppCompatActivity {

    public static ActivityComponent sActivityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sActivityComponent = DaggerActivityComponent.builder()
                .contextModule(new ContextModule(getApplicationContext())).build();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            fragment = new MapFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
