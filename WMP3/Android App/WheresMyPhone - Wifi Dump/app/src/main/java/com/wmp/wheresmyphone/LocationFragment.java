package com.wmp.wheresmyphone;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class LocationFragment extends Fragment {
    private AdView mAdView;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler = new Handler();
    private Switch simpleSwitch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdView adView = (AdView)getView().findViewById(R.id.adView);
        runAds(adView);
        AdView adView2 = (AdView)getView().findViewById(R.id.adView2);
        runAds(adView2);
        simpleSwitch = (Switch) view.findViewById(R.id.switch1);
        mHandler = new Handler();
        startRepeatingTask();

    }

    protected void runAds(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                Boolean switchState = simpleSwitch.isChecked();
                if(switchState) {
                    Context context2 = getActivity();
                    CharSequence text2 = "retrieving";
                    int duration2 = Toast.LENGTH_SHORT;

                    Toast toast2 = Toast.makeText(context2, text2, duration2);

                    toast2.show();
                    Scanner scan2 = new Scanner();
                    scan2.start_scan(getActivity(), true);
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };
    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

}
