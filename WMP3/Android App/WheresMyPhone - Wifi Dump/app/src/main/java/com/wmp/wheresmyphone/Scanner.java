package com.wmp.wheresmyphone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Scanner extends AppCompatActivity {
    WifiManager wManager;
    EditText eText;
    FileWriter out;
    ArrayList<result> a = new ArrayList<>();
    Context ctext;
    boolean save = true;
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);// wifi manager
        eText = (EditText) (findViewById(R.id.editText));
        setContentView(R.layout.fragment_location);
        mHandler = new Handler();
        startRepeatingTask();
    }



    public void start_scan(Context context, boolean locat) {
        if (locat) {
            save = false;
        } else {
            save = true;
        }
        ctext = context;
        wManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);// wifi manager
        eText = (EditText) ((Activity) context).findViewById(R.id.editText);
        context.registerReceiver(br, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wManager.startScan();
    }

    public void unregisterReceiver() {
        ctext.unregisterReceiver(br);//your broadcast
    }

    final BroadcastReceiver br = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiList = wManager.getScanResults();
            int found = 0;
            String loc= "-1,-1,-1";
            if(save) {
                loc = eText.getText().toString();
            }
            String str = loc + "\n";
            final ArrayList<String> dump = new ArrayList<>();
            for (int i = 0; i < wifiList.size(); i++) {
                String wifid;
                ScanResult sr1 = wManager.getScanResults().get(i);
                System.out.println("BSSID: " + sr1.BSSID);
                wifid = sr1.BSSID + " " + sr1.level;
                dump.add(wifid);
                System.out.println("RSSI: " + sr1.level);
                found++;

                str += "BSSID: " + sr1.BSSID + " " + "Level: " + sr1.level + "\n";
                if (i == wifiList.size() - 1) {
                    str += "\n\n";
                }
                File root = new File(Environment.getExternalStorageDirectory(), "WifiScan");
// if external memory exists and folder with name Notes
                if (!root.exists()) {
                    root.mkdirs(); // this will create folder.
                }
                File filepath = new File(root, "wifidump.txt"); // file path to save

                try {
                    out = new FileWriter(filepath, true);
                } catch (IOException e) {
                    Log.d("test", "this broke it first");
                    e.printStackTrace();
                }

// Write this data to file

                try {
                    out.append(str);
                } catch (IOException e) {
                    Log.d("test", "this broke it");
                    e.printStackTrace();
                }


            }


            // Convert JsonObject to String Format

            String userString = con_to_json(loc, dump).toString();
            userString += '\n';
// Define the File Path and its Name
            File root = new File(Environment.getExternalStorageDirectory(), "WifiScan");
// if external memory exists and folder with name Notes
            if (!root.exists()) {
                root.mkdirs(); // this will create folder.
            }
            File file = new File(root, "WifiDump.json");
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            try {
                if (save) {
                    bufferedWriter.write(userString);
                }
                bufferedWriter.close();
                Log.d("test", "json written");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //abortBroadcast();
                unregisterReceiver();
                Context context = ctext;
                if (save) {
                    CharSequence text = "Scan Complete " + found + " wifi points";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (!save) {

                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                a.clear();
                                a = dumpCompare(read_json(), con_to_json("test", dump));
                                double max = 0.0;
                                int ind = 0;
                                Log.d("size", String.valueOf(a.size()));
                                for (int i = 0; i < a.size(); i++) {
                                    Log.d("matchper", i + ": " + String.valueOf(a.get(i).match));
                                    if (a.get(i).match > max) {
                                        max = a.get(i).match;
                                        ind = i;
                                    }
                                }
                                String Coords = String.valueOf(a.get(ind).coord);
                                List<String> cList = Arrays.asList(Coords.split(","));
                                if(max>0) {
                                    sendResult(cList.get(0), cList.get(1), cList.get(2));
                                }
                                Log.d("TAG", String.valueOf(a.get(ind).match));
                                int duration = Toast.LENGTH_SHORT;
//                                Context context = ctext;
//                                Toast toast = Toast.makeText(context, a.get(ind).coord, duration);
//                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    t.start();
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!save) {
                save = true;
            }

        }
    };

    JSONObject con_to_json(String coordinates, ArrayList<String> dump) {
        JSONObject jsonObject = new JSONObject();
        JSONObject child = new JSONObject();
        for (int i = 0; i < dump.size(); i++) {
            String data = dump.get(i);
            String[] split = data.split("\\s+");
            try {
                child.put(split[0], split[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        try {
            jsonObject.put(coordinates, child);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    JSONArray read_json() throws IOException, JSONException {
        File root = new File(Environment.getExternalStorageDirectory(), "WifiScan");
// if external memory exists and folder with name Notes
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }
        File file = new File(root, "WifiDump.json");
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        JSONArray result = new JSONArray();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            result.put(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
// This response will have Json Format String
        String response = stringBuilder.toString();
        Log.d("read", String.valueOf(result));
        JSONObject res = new JSONObject(response);
        Log.d("read", String.valueOf(res));
        return result;
    }

    public ArrayList<result> dumpCompare(JSONArray json1, JSONObject wifiscan) throws JSONException {
        JSONArray array = json1;
        Log.d("json", String.valueOf(json1));
        for (int i = 0; i < array.length(); i++) {
            Log.d("json", String.valueOf(array.get(i)));
        }

        //match = SUM((%individual match/100)/(total number of ssid/2))
        ArrayList<result> scanResults = new ArrayList<result>();
        //Iterator<?> data = json1.keys();
        // while(data.hasNext()){
        for (int i = 0; i < json1.length(); i++) {
            JSONObject temp = new JSONObject(String.valueOf(json1.get(i)));
            //Log.d("json1", String.valueOf(temp.names()).substring(2,String.valueOf(temp.names()).length()-2 ) +'\n');
            String dataKeystr = String.valueOf(temp.names()).substring(2, String.valueOf(temp.names()).length() - 2);
            JSONObject datassids = temp.getJSONObject(dataKeystr);
            JSONObject ssids = wifiscan.getJSONObject("test");

            Iterator<?> scanKeys = ssids.keys();
            //Log.d("scan", String.valueOf(scanKeys.next()));
            int count1 = 0;
            int count2 = 0;
            double indivMatch = 0.0;

            while (scanKeys.hasNext()) {
                String Skeynext = (String) scanKeys.next();
                Log.d("Scan", Skeynext);
                count1++;
                Iterator<?> dataKeys = datassids.keys();
                while (dataKeys.hasNext()) {
                    if (count1 == 1) {
                        count2++;
                    }
                    String Dkeynext = (String) dataKeys.next();


                    String dKeyStr = (String) datassids.get(Dkeynext).toString();
                    String sKeyStr = (String) ssids.get(Skeynext).toString();
                    if (Dkeynext.equals(Skeynext)) {
                        Log.d("DC", "match found");
                        int a = Integer.parseInt(dKeyStr);
                        int b = Integer.parseInt(sKeyStr);
                        a = Math.abs(a);
                        b = Math.abs(b);
                        if (b > a) {
                            int temp2 = a;
                            a = b;
                            b = temp2;
                        }
                        indivMatch += (100 - ((a - b) / ((a + b) / 2.0) * 100));
                        Log.d("tag", String.valueOf(indivMatch));
                    }

                    Log.d("Database", Dkeynext + " " + dKeyStr);

                }
            }
            Log.d("count: ", String.valueOf((count1 + count2)));
            indivMatch /= (count1 + count2 + 0.0);
            result tmp = new result();
            tmp.coord = dataKeystr;
            tmp.match = indivMatch;
            Log.d("match", String.valueOf(indivMatch));
            scanResults.add(tmp);

        }

        return scanResults;
    }

    public void sendResult(final String X, final String Y, final String Z) throws IOException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://wheresmyphone.ts.r.appspot.com/Send?X=" + X + "&Y=" + Y + "&Z=" + Z +"&ID=1");
                    //URL url = new URL("http://wheresmyphone.ts.r.appspot.com/");
                    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setRequestProperty("Accept", "*");
                    httpCon.setDoOutput(true);
                    // httpCon.
                    httpCon.setRequestMethod("POST");
                    Log.d("Web", String.valueOf(httpCon.getResponseCode()));
                    //OutputStreamWriter out = new OutputStreamWriter(
                      //      httpCon.getOutputStream());

                    //out.write("Data you want to put");
                    // out.close();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


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
                start_scan(getApplicationContext(),true);
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
