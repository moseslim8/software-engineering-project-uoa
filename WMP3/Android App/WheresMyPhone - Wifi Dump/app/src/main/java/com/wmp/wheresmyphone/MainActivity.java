package com.wmp.wheresmyphone;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

class result
{
    public double match;
    public String coord;
}
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        checkPermission();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LocationFragment()).commit();
        }

        //Wifi Dump Button

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mInterstitialAd.show();
            }
        });
        File root = new File(Environment.getExternalStorageDirectory(), "WifiScan");
// if external memory exists and folder with name Notes
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }

    }

    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new LocationFragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new WebViewFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SettingsFragment();
                            break;
                        default:
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
    public void checkPermission() {
        //If permissions NOT granted
        if((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_WIFI_STATE))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CHANGE_WIFI_STATE))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_NETWORK_STATE))+
                (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET)) != PackageManager.PERMISSION_GRANTED) {
            if((ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_WIFI_STATE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.CHANGE_WIFI_STATE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_NETWORK_STATE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.INTERNET))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Storage, Location, and Internet " + " permissions are required");
                builder.setTitle("Please grant these permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(mActivity, new String[] {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.INTERNET
                        },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                //Grant permissions
                ActivityCompat.requestPermissions(mActivity, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET
                },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }
        //If permissions ALREADY granted
        else {
//            Toast.makeText(mContext, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int grantResults[]) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                //When request is cancelled, results array is empty
                if((grantResults.length > 0) && (grantResults[0] + grantResults[1] +
                        grantResults[2] + grantResults[3] + grantResults[4] +
                        grantResults[5] + grantResults[6]) == PackageManager.PERMISSION_GRANTED) {
                    //Grant permissions
                    Toast.makeText(mContext, "Permissions granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Deny permissions
                    Toast.makeText(mContext, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
