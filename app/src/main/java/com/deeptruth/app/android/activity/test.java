package com.deeptruth.app.android.activity;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.deeptruth.app.android.R;
import com.deeptruth.app.android.utils.GetSpeedTestHostsHandler;
import com.deeptruth.app.android.utils.HttpDownloadTest;
import com.deeptruth.app.android.utils.PingTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class test extends Activity {

    static int position = 0;
    static int lastPosition = 0;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Button btn_click= (Button) findViewById(R.id.btn_click);
        tempBlackList = new HashSet<>();
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
    }

    public void getconnectionspeed()
    {
        if (getSpeedTestHostsHandler == null) {
            getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
            getSpeedTestHostsHandler.start();
        }

        //Get egcodes.speedtest hosts
        int timeCount = 600; //1min
        while (!getSpeedTestHostsHandler.isFinished()) {
            timeCount--;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (timeCount <= 0) {
                // No internet
                getSpeedTestHostsHandler = null;
                return;
            }
        }

        //Find closest server
        HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
        HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
        double selfLat = getSpeedTestHostsHandler.getSelfLat();
        double selfLon = getSpeedTestHostsHandler.getSelfLon();
        double tmp = 19349458;
        double dist = 0.0;
        int findServerIndex = 0;
        for (int index : mapKey.keySet()) {
            if (tempBlackList.contains(mapValue.get(index).get(5))) {
                continue;
            }

            Location source = new Location("Source");
            source.setLatitude(selfLat);
            source.setLongitude(selfLon);

            List<String> ls = mapValue.get(index);
            Location dest = new Location("Dest");
            dest.setLatitude(Double.parseDouble(ls.get(0)));
            dest.setLongitude(Double.parseDouble(ls.get(1)));

            double distance = source.distanceTo(dest);
            if (tmp > distance) {
                tmp = distance;
                dist = distance;
                findServerIndex = index;
            }
        }
        String uploadAddr = mapKey.get(findServerIndex);
        final List<String> info = mapValue.get(findServerIndex);
        final double distance = dist;

        if (info == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
            return;
        }

        final List<Double> pingRateList = new ArrayList<>();
        final List<Double> downloadRateList = new ArrayList<>();
        Boolean pingTestStarted = false;
        Boolean pingTestFinished = false;
        Boolean downloadTestStarted = false;
        Boolean downloadTestFinished = false;

        //Init Test
        final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 6);
        final HttpDownloadTest downloadTest = new HttpDownloadTest(uploadAddr.replace(uploadAddr.split("/")[uploadAddr.split("/").length - 1], ""));

        //Tests
        while (true) {
            if (!pingTestStarted) {
                pingTest.start();
                pingTestStarted = true;
            }
            if (pingTestFinished && !downloadTestStarted) {
                downloadTest.start();
                downloadTestStarted = true;
            }

            //Ping Test
            if (pingTestFinished) {
                //Failure
                if (pingTest.getAvgRtt() == 0) {
                    System.out.println("Ping error...");
                } else {
                    //Success

                }
            }

            //Download Test
            if (pingTestFinished) {
                if (downloadTestFinished) {
                    //Failure
                    if (downloadTest.getFinalDownloadRate() == 0) {
                        System.out.println("Download error...");
                    } else {
                        //Success
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String aa=downloadTest.getFinalDownloadRate()+" Mbps";
                                Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_LONG).show();
                                String bb=downloadTest.getFinalDownloadRate()+" Mbps";

                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }

}
