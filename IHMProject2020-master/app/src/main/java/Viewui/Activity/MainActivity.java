package Viewui.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;
import org.xutils.ex.DbException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ihmproject.R;
import com.flag.myapplication.car.bean.User;
import com.flag.myapplication.car.utils.Xutils;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;
import java.util.Objects;

import Viewui.Fragment.MapFragment;
import Viewui.Fragment.ModeDeDeplacementFragment;
import Interface.IButtonDrawerClickListener;
import Interface.IButtonMapListener;
import Viewui.LoginActivity;
import Viewui.RenzhengActivity;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IButtonDrawerClickListener, View.OnClickListener, IButtonMapListener {
    private Intent intent;
    private DrawerLayout drawerLayout;

    private MapFragment mapFragment;
    private boolean permissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(   getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) );

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,
                R.string.naviguation_drawer_open, R.string.naviguation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mainNavigationView = (NavigationView) findViewById(R.id.main_nav_view);
        mainNavigationView.setNavigationItemSelectedListener(this);
        mainNavigationView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                initiateLanguageSpinner();
                return insets;
            }
        });

        if(savedInstanceState == null) startMapFragment();
        findViewById(R.id.zhong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switchLanguage(Locale.SIMPLIFIED_CHINESE);
              //  switchLanguage( "zh");

            }
        });
        findViewById(R.id.ying).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switchLanguage(Locale.ENGLISH);
              //  switchLanguage( "en");

            }
        });
        findViewById(R.id.fayu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguage(Locale.CANADA_FRENCH);
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();
        // map.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        // map.onPause();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }/*else if(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).getTargetFragment() instanceof ModeDeDeplacementFragment){
            startMapFragment();
        }*/
        else if(Objects.equals(Objects.requireNonNull(getSupportActionBar()).getTitle(), "Mode de déplacement")){
            startMapFragment();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_transport_mode) {
            getSupportActionBar().setTitle("Mode de déplacement");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ModeDeDeplacementFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(menuItem.getItemId() == R.id.SwitchGPS) {
            permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!permissionGranted) {

            }
            if (permissionGranted) {
            }
        }

        if(menuItem.getItemId() == R.id.renzheng) {
           startActivity(new Intent(MainActivity.this, RenzhengActivity.class));

        }


        if(menuItem.getItemId() == R.id.tuisong) {
            Toast.makeText(this, "推送成功", Toast.LENGTH_SHORT).show();
        }
        if(menuItem.getItemId() == R.id.tuichu) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            try {
                Xutils.initDbConfiginit().delete(User.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            finish();
        }



        return true;
    }
    private void startMapFragment(){
        getSupportActionBar().setTitle(R.string.app_name);
        if(mapFragment==null)mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                mapFragment).commit();
    }
    private void initiateLanguageSpinner(){
        Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.languages));
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(languageSpinner != null && languageSpinner.getAdapter()==null)
            languageSpinner.setAdapter(languagesAdapter);
    }
    @Override
    public void onCloseModeTransportButtonClicked(View v) {
        startMapFragment();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void mapIntentButtonClicked(View v) {
        switch(v.getId()){
            case R.id.incidentButton :
                intent = new Intent(this, IncidentActivity.class);
                break;
            case R.id.accidentButton:
                intent = new Intent(this, AccidentActivity.class);

                break;
        }
        if(intent != null){
            intent.putExtra("longitude",mapFragment.getUserCurrentLongitude());
            intent.putExtra("latitude",mapFragment.getUserCurrentLatitude());
            // intent.putExtra("name",mapFragment.getLocationName());
        startActivity(intent);}
    }
}
