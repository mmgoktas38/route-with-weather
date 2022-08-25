package com.kogo.iroad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.kogo.iroad.adapters.TimelineAdapter;
import com.kogo.iroad.databinding.ActivityTimelineBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimelineActivity extends AppCompatActivity implements Serializable  {

    private ActivityTimelineBinding timelineBinding;
    private TimelineAdapter timelineAdapter;
    private ArrayList<Info> infos = new ArrayList<>();
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timelineBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(timelineBinding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // dark mode cancel
        holdRecyclerView();

        timelineBinding.cLBack.setOnClickListener(view -> {
            goMapsActivity();
        });

        HashMap<String, String> markerLatLonis_dayList = (HashMap<String, String>) getIntent().getSerializableExtra("markerLatLonis_dayList");
        HashMap<String, String> markerLatLonTempList = (HashMap<String, String>) getIntent().getSerializableExtra("markerLatLonTempList");
        HashMap<String, String> markerLatLonIconList = (HashMap<String, String>) getIntent().getSerializableExtra("markerLatLonIconList");
        HashMap<String, String> markerDistanceFromOrigin = (HashMap<String, String>) getIntent().getSerializableExtra("markerDistanceFromOrigin");
        HashMap<String, String> markerDistanceFromOriginValue = (HashMap<String, String>) getIntent().getSerializableExtra("markerDistanceFromOriginValue");
        Map<String, String> markers1Infos = (HashMap<String, String>) getIntent().getSerializableExtra("markers1Infos");
        Map<String, String> markers2Infos = (HashMap<String, String>) getIntent().getSerializableExtra("markers2Infos");
        ArrayList<String> orderedLatLonList = (ArrayList<String>) getIntent().getSerializableExtra("orderedLatLonList");
        Map<String, String> markers3Infos = (HashMap<String, String>) getIntent().getSerializableExtra("markers3Infos");
        String colorfulRoadTag = getIntent().getStringExtra("colorfulRoadTag");
        String fromName = getIntent().getStringExtra("fromName");
        String toName = getIntent().getStringExtra("toName");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String totalDistance = getIntent().getStringExtra("totalDistance");
        String strEndLatLon = getIntent().getStringExtra("strEndLatLon");


        Log.e("fromName",fromName);
        Log.e("toName",toName);


        if (totalDistance != null){
            timelineBinding.textViewTotalDistance.setText(totalDistance);
            String[] typeDistance = totalDistance.split(" ");
            if (typeDistance[typeDistance.length-1].equals("mi")){
                timelineBinding.textViewStartDistance.setText("0 mi");
            }
            else {
                timelineBinding.textViewStartDistance.setText("0 km");
            }
        }
        if (startTime != null && endTime != null){
            timelineBinding.textViewStartTime.setText(startTime);
            timelineBinding.textViewEndTime.setText(endTime);
        }
        if (fromName != null && toName != null){
            timelineBinding.textViewStartPlace.setText(fromName);
            timelineBinding.textViewEndPlace.setText(toName);
        }
        else if(fromName != null){
            timelineBinding.textViewStartPlace.setText("Current Location");
            timelineBinding.textViewEndPlace.setText(toName);
        }

        String type = null;
        Double[] sortedDistance2 = new Double[Integer.parseInt(String.valueOf(markerDistanceFromOriginValue.size()))];
        int y = 0;
        for (String key : markerDistanceFromOriginValue.keySet()){
            String[] distanceSplit = String.valueOf(markerDistanceFromOriginValue.get(key)).split(" ");
           // type = distanceSplit[1];
            String distanceValue = distanceSplit[0];
            sortedDistance2[y] = Double.parseDouble(distanceValue);
            y++;
        }
        Arrays.sort(sortedDistance2);
        for (double s : sortedDistance2) {
            System.out.println("distanceee " + s);
        }



        /*Double[] sortedDistance = new Double[Integer.parseInt(String.valueOf(markerDistanceFromOrigin.size()))];
        Map<String, String> sortedTime = new HashMap<>();
        int l = 0;
        for (String key : markerDistanceFromOrigin.keySet()){
            String[] distanceSplit = String.valueOf(markerDistanceFromOrigin.get(key)).split(" ");
            String distanceValue = distanceSplit[0];
            distanceValue = distanceValue.replace(",",".");
            sortedDistance[l] = Double.parseDouble(distanceValue);
            l++;
        }
        Arrays.sort(sortedDistance);
        String[] sortedDistanceString = new String[sortedDistance.length];
        int p = 0;
        for (double s : sortedDistance) {
            sortedDistanceString[p] = String.valueOf(s);
            p++;
            System.out.println("distance " + s);
        }


        String[] sortedTime1 = new String[Integer.parseInt(String.valueOf(markers1Infos.size()))];
        int i = 0;
        for (String key : markers1Infos.keySet()){
            sortedTime1[i] = String.valueOf(markers1Infos.get(key));
            i++;
        }

        String[] sortedTime2 = new String[Integer.parseInt(String.valueOf(markers2Infos.size()))];
        int k = 0;
        for (String key : markers2Infos.keySet()){
            sortedTime2[k] = String.valueOf(markers2Infos.get(key));
            k++;
        }

        String[] sortedTime3 = new String[Integer.parseInt(String.valueOf(markers3Infos.size()))];
        int j = 0;
        for (String key : markers3Infos.keySet()){
            sortedTime3[j] = String.valueOf(markers3Infos.get(key));
            j++;
        }

*/

        if (colorfulRoadTag.equals("polylines")){
            String key = null;
            Arrays.sort(sortedDistance2);
            for (Double s : sortedDistance2) {
                String  str = String.valueOf(s);
                String[] newStr = str.split("\\.");
                str = newStr[0];
                System.out.println("str "+str);
                for(Map.Entry entry: markerDistanceFromOriginValue.entrySet()){
                    System.out.println("entry.getValue() "+entry.getValue());
                    if(str.equals(entry.getValue())){
                        key = (String) entry.getKey();
                        System.out.println(key);
                        if (markers1Infos.containsKey(key) && markerLatLonIconList.containsKey(key) && markerLatLonis_dayList.containsKey(key) && markerLatLonTempList.containsKey(key) && markerDistanceFromOrigin.containsKey(key)){
                            String isDay = markerLatLonis_dayList.get(key);
                            String time = markers1Infos.get(key);
                            Log.e(markers1Infos.get(key), time);
                            String celcius = markerLatLonTempList.get(key);
                            String icon = markerLatLonIconList.get(key);
                            String distance = markerDistanceFromOrigin.get(key);

                            String url[] = icon.split("/");
                            String iconName = url[url.length-1];
                            String iconSplitted[] = iconName.split("\\.");
                            String iconNameWithoutPNG = iconSplitted[0];

                            String tempImageName = null;
                            if (isDay.equals("0")){
                                tempImageName = "n" + iconNameWithoutPNG;
                            }else {
                                tempImageName = "d" + iconNameWithoutPNG ;
                            }

                            if (key.equals(strEndLatLon)){
                                time = endTime;
                                distance = totalDistance;
                            }

                            Info info = new Info(tempImageName, time, celcius, distance);
                            infos.add(info);
                        }
                    }
                }
            }
        timelineAdapter = new TimelineAdapter(TimelineActivity.this, infos);
        timelineBinding.recyclerViewTimeline.setAdapter(timelineAdapter);
    }



        if (colorfulRoadTag.equals("polyline1")){
            String key = null;
            Arrays.sort(sortedDistance2);
            for (Double s : sortedDistance2) {
                String  str = String.valueOf(s);
                String[] newStr = str.split("\\.");
                str = newStr[0];
                System.out.println("str "+str);
                for(Map.Entry entry: markerDistanceFromOriginValue.entrySet()){
                    System.out.println("entry.getValue() "+entry.getValue());
                    if(str.equals(entry.getValue())){
                        key = (String) entry.getKey();
                        System.out.println(key);
                        if (markers2Infos.containsKey(key) && markerLatLonIconList.containsKey(key) && markerLatLonis_dayList.containsKey(key) && markerLatLonTempList.containsKey(key) && markerDistanceFromOrigin.containsKey(key)){
                            String isDay = markerLatLonis_dayList.get(key);
                            String time = markers2Infos.get(key);
                            Log.e(markers2Infos.get(key), time);
                            String celcius = markerLatLonTempList.get(key);
                            String icon = markerLatLonIconList.get(key);
                            String distance = markerDistanceFromOrigin.get(key);

                            String url[] = icon.split("/");
                            String iconName = url[url.length-1];
                            String iconSplitted[] = iconName.split("\\.");
                            String iconNameWithoutPNG = iconSplitted[0];

                            String tempImageName = null;
                            if (isDay.equals("0")){
                                tempImageName = "n" + iconNameWithoutPNG;
                            }else {
                                tempImageName = "d" + iconNameWithoutPNG ;
                            }
                            if (key.equals(strEndLatLon)){
                                time = endTime;
                                distance = totalDistance;
                            }
                            Info info = new Info(tempImageName, time, celcius, distance);
                            infos.add(info);
                        }
                    }
                }
            }
            timelineAdapter = new TimelineAdapter(TimelineActivity.this, infos);
            timelineBinding.recyclerViewTimeline.setAdapter(timelineAdapter);
        }

        if (colorfulRoadTag.equals("polyline2")){
            String key = null;
            Arrays.sort(sortedDistance2);
            for (Double s : sortedDistance2) {
                String  str = String.valueOf(s);
                String[] newStr = str.split("\\.");
                str = newStr[0];
                System.out.println("str "+str);
                for(Map.Entry entry: markerDistanceFromOriginValue.entrySet()){
                    System.out.println("entry.getValue() "+entry.getValue());
                    if(str.equals(entry.getValue())){
                        key = (String) entry.getKey();
                        System.out.println(key);
                        if (markers3Infos.containsKey(key) && markerLatLonIconList.containsKey(key) && markerLatLonis_dayList.containsKey(key) && markerLatLonTempList.containsKey(key) && markerDistanceFromOrigin.containsKey(key)){
                            String isDay = markerLatLonis_dayList.get(key);
                            String time = markers3Infos.get(key);
                            Log.e(markers3Infos.get(key), time);
                            String celcius = markerLatLonTempList.get(key);
                            String icon = markerLatLonIconList.get(key);
                            String distance = markerDistanceFromOrigin.get(key);

                            String url[] = icon.split("/");
                            String iconName = url[url.length-1];
                            String iconSplitted[] = iconName.split("\\.");
                            String iconNameWithoutPNG = iconSplitted[0];

                            String tempImageName = null;
                            if (isDay.equals("0")){
                                tempImageName = "n" + iconNameWithoutPNG;
                            }else {
                                tempImageName = "d" + iconNameWithoutPNG ;
                            }
                            if (key.equals(strEndLatLon)){
                                time = endTime;
                                distance = totalDistance;
                            }
                            Info info = new Info(tempImageName, time, celcius, distance);
                            infos.add(info);
                        }
                    }
                }
            }
            timelineAdapter = new TimelineAdapter(TimelineActivity.this, infos);
            timelineBinding.recyclerViewTimeline.setAdapter(timelineAdapter);
        }

    }

    public void goMapsActivity(){
       onBackPressed();
    }


    public void holdRecyclerView(){
        timelineBinding.recyclerViewTimeline.setHasFixedSize(true);
        timelineBinding.recyclerViewTimeline.setLayoutManager(new LinearLayoutManager(TimelineActivity.this, LinearLayoutManager.VERTICAL, false));
        timelineAdapter = null;
    }


}