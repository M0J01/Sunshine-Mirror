package com.example.andriud.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;


/**
 * Created by M0J0 on 11/9/2017.
 */


public class DetailActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Infalte Menu, adds items to action bar if present
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //no inspection simplified statemetn
        if (id==R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Placeholder fragment with a simple view
     *
     */
    public static class PlaceholderFragment extends Fragment {
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            return rootView;
        }
    }
}
