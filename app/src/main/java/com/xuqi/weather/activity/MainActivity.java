package com.xuqi.weather.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;

import com.xuqi.weather.R;

public class MainActivity extends Activity {
    private long exitTime = 0;
    private static final String TAG = "MainActivity";
//    private EditText text;
//    private Button button;
    private SearchView mSearchView;
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSearchView = (SearchView) findViewById(R.id.search);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
//        text = (EditText) findViewById(R.id.city_name);
//        button = (Button) findViewById(R.id.search);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
               intent.putExtra("city",newText);
                startActivity(intent);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // 关联检索配置和SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public void onBackPressed(){
//        if (isFromWeatherActivity) {
//            Intent intent = new Intent(this,WeatherActivity.class);
//            startActivity(intent);
//        }
//        finish();
        if ((System.currentTimeMillis() - exitTime) > 2000)
        {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else
        {
            this.finish();
        }
    }
}