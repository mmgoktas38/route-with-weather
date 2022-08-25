package com.kogo.iroad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.kogo.iroad.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener, Serializable {

    //google map object
    private GoogleMap mMap = null;
    private ActivityMapsBinding binding;

    // location apikey
    private final String APIKEY = "AIzaSyCZs3vnvP8MC0U5bCUQd0fkdatM546SCKQ";

    private ArrayList<MarkerOptions> markerOptionsList1 = new ArrayList<>();
    private ArrayList<MarkerOptions> markerOptionsList2 = new ArrayList<>();
    private ArrayList<MarkerOptions> markerOptionsList3 = new ArrayList<>();

    private Marker endM = null;
    private String colorfulRoadTag = null;
    private int countsTimesOfEnteredGetWeatherDetails = 0;
    private int sum = 0;
    private String tagPolyline = null;

    private String startTime = null, endTime = null, totalDistance = null;

    // defination for JSON attributes
    private String distanceValue = null;

    // autoComplated definations
    private String fromAddress = null, toAddress = null;
    private String fromName = null, toName = null;
    private String fromLatLng = null, toLatLng = null;
    private String strEndLatLon = null;
    private LatLng startLatLngFromEdittext = null, endLatLngToEdittext = null, tempLatLng = null;
    private LatLng myLocationltlng = null;

    // start-finish time definition
    private int departureHour, departureMinutes, destinationHour, destinationMinute;

    //to get location permissions.
    private final static int REQUEST_CODE = 100;
    boolean locationPermission = false;

    //polyline object
    private List<Polyline> polylines = null;
    private List<Polyline> polyline1 = null;
    private List<Polyline> polyline2 = null;

    // defination for JSON attributes - some of them not using just attributes
    private String markerDistanceTextToOrigin = null;
    private String markerDistanceValueToOrigin = null;
    private String markerDurationTextToOrigin = null;
    private String markerDurationValueToOrigin = null;
    private String markerEndAddress = null;
    private String markerEndLocationLatt = null;
    private String markerEndLocationLngg = null;
    private String markerStartAddress = null;
    private String markerStartLocationLatt = null;
    private String markerStartLocationLngg = null;

    // wayPoints marker lists
    ArrayList<Marker> markers1 = new ArrayList<>();
    ArrayList<Marker> markers2 = new ArrayList<>();
    ArrayList<Marker> markers3 = new ArrayList<>();

    // attributes for intent - maps
    Map<String, String> markerLatLonTempList = new HashMap<>();
    Map<String, String> markerLatLonIconList = new HashMap<>();
    Map<String, String> markerLatLonCodeList = new HashMap<>();
    Map<String, String> markerLatLonis_dayList = new HashMap<>();
    Map<String, String> markerDistanceFromOrigin = new HashMap<>();
    Map<String, String> markerDistanceFromOriginValue = new HashMap<>();
    ArrayList<String> orderedLatLonList = new ArrayList<>();

    private Dialog dialog;  // for not found any routes
    FusedLocationProviderClient fusedLocationProviderClient;   // to get current location

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // dark mode cancel
        dialog = new Dialog(MapsActivity.this);         // yol bulunamadıysa kullanıldı
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);        // kendi konumunu bulmak için tanımlandı

        //request location permission
        requestPermision();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initPlacesSDK();

        binding.imageViewChange.setOnClickListener(view -> {
            if (startLatLngFromEdittext == null && myLocationltlng != null){
                startLatLngFromEdittext = myLocationltlng;
            }
            String newFromPlaceName = binding.editTextTo.getText().toString();
            String newToPlaceName = binding.editTextFrom.getText().toString();
            binding.editTextFrom.setText(newFromPlaceName);
            binding.editTextTo.setText(newToPlaceName);
            tempLatLng = startLatLngFromEdittext;
            startLatLngFromEdittext = endLatLngToEdittext;
            endLatLngToEdittext = tempLatLng;


        });

        binding.editTextFrom.setFocusable(false);
        binding.constraintLayoutA.setOnClickListener(view -> { writeAddressFromPlace(); });
        binding.editTextFrom.setOnClickListener(view -> {  writeAddressFromPlace();    });

        binding.editTextTo.setFocusable(false);
        binding.constraintLayoutB.setOnClickListener(view -> {  writeAddressToPlace();  });
        binding.editTextTo.setOnClickListener(view -> {  writeAddressToPlace();   });

        binding.buttonShowTimeline.setEnabled(false);
        binding.buttonShowTimeline.setOnClickListener(view -> {
            Map<String, String> markers1Infos = new HashMap<>();
            Map<String, String> markers2Infos = new HashMap<>();
            Map<String, String> markers3Infos = new HashMap<>();

            for (int i = 0; i < markers1.size(); i++) {
                String oldTitle = markers1.get(i).getTitle();
                Log.e("oldTitle", oldTitle);
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                oldTitle = hour + ":" + minute;
                String markerLat = String.valueOf(markers1.get(i).getPosition().latitude);
                String markerLon = String.valueOf(markers1.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                markers1Infos.put(strMarkerLatLon, oldTitle);
            }
            for (int i = 0; i < markers2.size(); i++) {
                String oldTitle = markers2.get(i).getTitle();
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                oldTitle = hour + ":" + minute;
                String markerLat = String.valueOf(markers2.get(i).getPosition().latitude);
                String markerLon = String.valueOf(markers2.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                markers2Infos.put(strMarkerLatLon, oldTitle);
            }
            for (int i = 0; i < markers3.size(); i++) {
                String oldTitle = markers3.get(i).getTitle();
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                if (minute.length() == 1) {
                    minute = "0" + minute;
                }

                oldTitle = hour + ":" + minute;
                String markerLat = String.valueOf(markers3.get(i).getPosition().latitude);
                String markerLon = String.valueOf(markers3.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                markers3Infos.put(strMarkerLatLon, oldTitle);
            }

            Intent intent = new Intent(MapsActivity.this, TimelineActivity.class);
            intent.putExtra("markerLatLonTempList", (Serializable) markerLatLonTempList);
            intent.putExtra("orderedLatLonList", (Serializable) orderedLatLonList);
            intent.putExtra("markerLatLonIconList", (Serializable) markerLatLonIconList);
            intent.putExtra("markerLatLonis_dayList", (Serializable) markerLatLonis_dayList);
            intent.putExtra("markerDistanceFromOrigin", (Serializable) markerDistanceFromOrigin);
            intent.putExtra("markerDistanceFromOriginValue", (Serializable) markerDistanceFromOriginValue);
            intent.putExtra("markers1Infos", (Serializable) markers1Infos);
            intent.putExtra("markers2Infos", (Serializable) markers2Infos);
            intent.putExtra("markers3Infos", (Serializable) markers3Infos);
            intent.putExtra("colorfulRoadTag", colorfulRoadTag);
            intent.putExtra("strEndLatLon", strEndLatLon);

            if (fromName == null) {
                fromName = "Current Location";
            }
            intent.putExtra("fromName", fromName);
            intent.putExtra("toName", toName);
            intent.putExtra("startTime", startTime);
            intent.putExtra("endTime", endTime);
            intent.putExtra("totalDistance", totalDistance);

            startActivity(intent);
        });

        binding.buttonDirection.setEnabled(false);
        binding.buttonDirection.setOnClickListener(view -> {
                String uri = null;
                if (startLatLngFromEdittext == null){
                    uri = "http://maps.google.com/maps?saddr=" + String.valueOf(myLocationltlng.latitude) + "," + String.valueOf(myLocationltlng.longitude) + "&daddr=" + String.valueOf(endLatLngToEdittext.latitude) + "," + String.valueOf(endLatLngToEdittext.longitude);
                }
                else {
                    uri = "http://maps.google.com/maps?saddr=" + String.valueOf(startLatLngFromEdittext.latitude) + "," + String.valueOf(startLatLngFromEdittext.longitude) + "&daddr=" + String.valueOf(endLatLngToEdittext.latitude) + "," + String.valueOf(endLatLngToEdittext.longitude);
                }
                Log.e("uri", uri);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
        });

        binding.buttonGo.setOnClickListener(view -> {

            if (mMap != null) {
                mMap.clear();
            }
            markerLatLonTempList.clear();
            markerLatLonIconList.clear();
            markerLatLonCodeList.clear();
            markerLatLonis_dayList.clear();
            markerDistanceFromOriginValue.clear();
            markerDistanceFromOrigin.clear();
            markerOptionsList1.clear();
            markerOptionsList2.clear();
            markerOptionsList3.clear();
            markers1.clear();
            markers2.clear();
            markers3.clear();
            orderedLatLonList.clear();
            countsTimesOfEnteredGetWeatherDetails = 0;
            sum = 0;
            tagPolyline = null;
            Log.e("startLatLngFromEdittext", String.valueOf(startLatLngFromEdittext));
            Log.e("endLatLngToEdittext", String.valueOf(endLatLngToEdittext));
            Log.e("myLocationltlng", String.valueOf(myLocationltlng));
            if (fromLatLng == null && toLatLng == null) {
                Toast.makeText(MapsActivity.this, "Please fill in the blank area!", Toast.LENGTH_SHORT).show();
            }
            else {
                if (startLatLngFromEdittext == null) {
                    Findroutes(myLocationltlng, endLatLngToEdittext);
                    startLatLngFromEdittext = myLocationltlng;
                }
                else {
                    Findroutes(startLatLngFromEdittext, endLatLngToEdittext);
                }

                runLottieDialog();
            }

        });


    }

    private void writeAddressFromPlace(){
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapsActivity.this);
        startActivityForResult(intent, 100);

    }
    private void writeAddressToPlace(){
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(MapsActivity.this);
        startActivityForResult(intent, 200);
    }

    // autoComplete işlemleri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            binding.editTextFrom.setText(place.getAddress());
            double fromPlaceLat = place.getLatLng().latitude;
            double fromPlaceLng = place.getLatLng().longitude;
            startLatLngFromEdittext = new LatLng(fromPlaceLat, fromPlaceLng);
            fromLatLng = "" + fromPlaceLat + "," + fromPlaceLng;
            fromName = place.getName();
            fromAddress = place.getAddress().replace(" ", "");

        }
        if (requestCode == 200 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            binding.editTextTo.setText(place.getAddress());
            double toPlaceLat = place.getLatLng().latitude;
            double toPlaceLng = place.getLatLng().longitude;
            endLatLngToEdittext = new LatLng(toPlaceLat, toPlaceLng);
            toLatLng = "" + toPlaceLat + "," + toPlaceLng;
            toName = place.getName();
            toAddress = place.getAddress().replace(" ", "");

        }
    }

    // konum izni alıyor
    private void requestPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true;
        } else {    // eğer izin vermediyse izin iste
            ActivityCompat.requestPermissions(MapsActivity.this,  new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
    }

    // konum izni alıyor devamı
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                locationPermission = true;
                getLastLocation();
            }
            else {
                Toast.makeText(MapsActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
    }

    // konumu getirir
    private void getLastLocation(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null){

                                try {
                                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    Log.e("Address size: ", String.valueOf(addresses.size()));

                                    myLocationltlng=new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude()); //device location

                                    CameraUpdate cameraUpdate = null;
                                    if(startLatLngFromEdittext != null){
                                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                                startLatLngFromEdittext,14f);
                                    }
                                    else {
                                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                                myLocationltlng,14f);
                                        binding.editTextFrom.setText("Current Location");
                                    }
                                    mMap.animateCamera(cameraUpdate);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });


        }
        else {
            askPermission();
        }

    }

    // izin verilmemişse sor
    private void askPermission() {

        ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

        Log.e("burda","burda");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("burda","burda1");
            return;
        }

    }

    public void initPlacesSDK() {
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyCZs3vnvP8MC0U5bCUQd0fkdatM546SCKQ");
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);
    }

    public void runLottieDialog(){
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style2));    //haritayı dark mode yapıyor
    //    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getLastLocation();
        statusCheck();
        mMap.setTrafficEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);        // bakkkk
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.equals(marker)){
                    marker.hideInfoWindow();
                }
                return true;
            }
        });
        //get destination location when user click on map
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                endLatLngToEdittext = latLng;
            }
        });*/

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                statusCheck();
                if (isNetworkAvailable(MapsActivity.this)){
                    if (myLocationltlng != null){
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocationltlng,10f);
                        mMap.animateCamera(cameraUpdate);
                    }
                    else {
                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MapsActivity.this, "Request permission needed!",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        getLastLocation();
                    }
                }
                else {
                    Toast.makeText(MapsActivity.this, "Check internet connection!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

    // konumu açık mı değil mi kontrol ediyor
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    // konum açmak için alert oluşturuyor konum açma sayfasına yönlendiriyor
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        getLastLocation();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateCameraZoom(LatLng latLng){
        int distance;
        if (distanceValue != null){

            distance = Integer.parseInt(distanceValue);
        }else{
            distance = 9000;
        }

        CameraUpdate cameraUpdate = null;
        if (distance < 1000){
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng,14f);
        }
        else if(distance >= 1000 && distance < 10000 ){
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng,12f);
        }
        else if(distance >= 10000 && distance < 1000000 ){
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng,8f);
        }
        else {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng,5f);
        }

        mMap.animateCamera(cameraUpdate);

    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {

        if (Start == null || End == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyCZs3vnvP8MC0U5bCUQd0fkdatM546SCKQ")  //also define your api key here.
                    .build();
            routing.execute();
            if (startLatLngFromEdittext == null) {
                updateCameraZoom(myLocationltlng);
            } else {
                updateCameraZoom(startLatLngFromEdittext);
            }

        }
    }

    // yollar hep bunun içinde bulunuyor hesaplanıyor
    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int k) {
        // k = gideceği yol
        // arraylist route ise kaç farklı yol varsa ona bakıyor

        binding.buttonShowTimeline.setVisibility(View.VISIBLE);
        binding.buttonDirection.setVisibility(View.VISIBLE);

        if(polylines!=null || polyline1!=null || polyline2!=null) {
            polylines.clear();
            polyline1.clear();
            polyline2.clear();
        }

        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;

        polylines = new ArrayList<>();
        polyline1 = new ArrayList<>();
        polyline2 = new ArrayList<>();

        int suggestedRoad = k;

        //add route(s) to the map using polyline
        List<Polyline> polylinesColorList = new ArrayList<>();


        for ( k = 0; k <arrayList.size(); k++){
            PolylineOptions polyOptions = new PolylineOptions(); // çizgi ayarları
            polyOptions.width(20);                              // çizgi kalınlığı
            polyOptions.addAll(arrayList.get(k).getPoints());   // tüm noktaları alıyor ekliyor
            polyOptions.clickable(true);

            if (k!=suggestedRoad){
                polyOptions.color(getResources().getColor(R.color.gray));   // eğer önerilen yol bu değilse çizgi gri olsun
            }else {
                polyOptions.color(getResources().getColor(R.color.color1));   // eğer önerilen yol bu ise çizgi mavi olsun
                polyOptions.zIndex(1);
            }
            //Log.e("getDistanceText  blue ", String.valueOf(arrayList.get(k).getDistanceText()));
            Polyline polyline = mMap.addPolyline(polyOptions);

            polyline.setClickable(true);
            polylineStartLatLng = polyline.getPoints().get(0);
            int allPointsSize = polyline.getPoints().size();
            polylineEndLatLng=polyline.getPoints().get(allPointsSize-1);

            polylinesColorList.add(polyline);

            if(polylines.isEmpty()) {
                polyline.setTag("polylines");
                polylines.add(polyline);
                // bu if ilk yollar çizildiğinde mavi ise bu yol uzaklık ve süre bilgilerini yazdırıyor mavi int kodu -14575885
                if (polyline.getColor() == -222188){//-222188, color1 in değeri
                    writeDistanceAndDuration(arrayList, "polylines");
                    colorfulRoadTag = polyline.getTag().toString();
                }
                Log.e("polylines", "girdi");
                int distanceForMarkerCounts = arrayList.get(k).getDistanceValue();
                calculateDistanceDurationWeather(polyline, polyline.getTag().toString(), distanceForMarkerCounts );
            }
            else if(polyline1.isEmpty()) {
                polyline.setTag("polyline1");
                polyline1.add(polyline);
                if (polyline.getColor() == -222188){//-222188, color1 in değeri
                    writeDistanceAndDuration(arrayList, "polyline1");
                    colorfulRoadTag = polyline.getTag().toString();
                }
                Log.e("polyline1", "girdi");
                int distanceForMarkerCounts = arrayList.get(k).getDistanceValue();
                calculateDistanceDurationWeather(polyline, polyline.getTag().toString(), distanceForMarkerCounts);
            }
            else if(polyline2.isEmpty()) {
                polyline.setTag("polyline2");
                polyline2.add(polyline);
                if (polyline.getColor() == -222188){//-222188, color1 in değeri
                    colorfulRoadTag = polyline.getTag().toString();
                    writeDistanceAndDuration(arrayList, "polyline2");
                }
                Log.e("polyline2", "girdi");
                int distanceForMarkerCounts = arrayList.get(k).getDistanceValue();
                calculateDistanceDurationWeather(polyline, polyline.getTag().toString(), distanceForMarkerCounts);

            }

        }
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {

                // tıklanan mavi oluyor ve o rota haritada üst kısma çıkıyor zIndex1 ile
                for(Polyline polylineData: polylinesColorList){
                    if(polyline.getId().equals(polylineData.getId())){
                        polylineData.setColor(ContextCompat.getColor(MapsActivity.this, R.color.color1));
                        writeDistanceAndDuration(arrayList, polylineData.getTag().toString());
                        polylineData.setZIndex(1);
                        showMarkers(polylineData.getTag().toString(), polylineData);
                        colorfulRoadTag = polylineData.getTag().toString();
                    }
                    else{
                        polylineData.setColor(ContextCompat.getColor(MapsActivity.this, R.color.gray));
                        polylineData.setZIndex(0);
                        hideMarkers(polylineData.getTag().toString());
                    }
                }

            }
        });

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.icon(BitmapFromVector(getApplicationContext(), R.drawable.start_point_marker));
        startMarker.title(fromName);
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.icon(BitmapFromVector(getApplicationContext(), R.drawable.location));
        endMarker.title(toName +  " - " + String.valueOf(destinationHour) + "." + String.valueOf(destinationMinute));
        endM =  mMap.addMarker(endMarker);
        endM.setTag("endMarker");

    }

    public void writeDistanceAndDuration(ArrayList<Route> arrayList, String polyLineTag){
        int order=0;
        if(polyLineTag.equals("polylines")){
            order = 0;
        }
        else if(polyLineTag.equals("polyline1")){
            order = 1;
        }
        else {
            order = 2;
        }
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        departureHour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        departureMinutes = calendar.get(Calendar.MINUTE);

        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();

        // add currentTimeMilles and duration value (duration value is second so we multiple 1000 and converted millisecond)
        long destinationTimeMillis = currentTimeMillis + ((arrayList.get(order).getDurationValue())*1000) ;

        // convert destinationTimeMillis to date format and get hour and minute
        Date dateLater=new Date(destinationTimeMillis);
        destinationHour = dateLater.getHours();
        destinationMinute = dateLater.getMinutes();

        String departureTime = departureHour + ":" + departureMinutes;


        String getdepartureTime[] = departureTime.split(":");
        String hourdeparture = getdepartureTime[0];
        String minutedeparture = getdepartureTime[1];
        if (hourdeparture.length() == 1){
            hourdeparture = "0" + hourdeparture;
        }
        if (minutedeparture.length() == 1){
            minutedeparture = "0" + minutedeparture;
        }

        departureTime = hourdeparture + ":" + minutedeparture;

        String destinationTime = destinationHour + ":" + destinationMinute;
        String getdestinationTime[] = destinationTime.split(":");
        String hourdestination = getdestinationTime[0];
        String minutedestination = getdestinationTime[1];
        if (hourdestination.length() == 1){
            hourdestination = "0" + hourdestination;
        }
        if (minutedestination.length() == 1){
            minutedestination = "0" + minutedestination;
        }

        destinationTime = hourdestination + ":" + minutedestination;

        binding.textViewDepartureTime.setText(departureTime);
        binding.textViewDestinationTime.setText(destinationTime);

        binding.textViewDistance.setText(arrayList.get(order).getDistanceText());
        totalDistance = arrayList.get(order).getDistanceText();
        String strDurations = arrayList.get(order).getDurationText();
        if (strDurations.contains("mins")){
            strDurations = strDurations.replace("mins","m");
        }
        if (strDurations.contains("min")){
            strDurations = strDurations.replace("min","m");
        }
        if (strDurations.contains("hours")){
            strDurations = strDurations.replace("hours","h");
        }
        if (strDurations.contains("hour")){
            strDurations = strDurations.replace("hour","h");
        }
        binding.textViewDuration.setText(strDurations);
        if (endM != null){
            endM.setTitle(toName +  " - " + String.valueOf(destinationHour) + "." + String.valueOf(destinationMinute));
        }

        startTime = departureTime;
        endTime = destinationTime;

    }

    // polyline gönderiyoruz bu bir yoldur bu yolda birsürü marker nokta var mesela 1200 nokta var bunu 6 ya bölüyoruz 200. 400. 600. 800. 1000. noktaları  getJsonWhereis bu fonksiyona gönderiyoruz birde başlangıç noktası gönderiyoruz başlangıç noktası ile bu noktalar arası mesafe ve süreyi alıyoruz
    @SuppressLint("LongLogTag")
    public void calculateDistanceDurationWeather(Polyline polyline, String polylineTag, int distanceForMarkerCounts){

        if (distanceForMarkerCounts>=1500000){
            for (int i = 1 ; i<8; i++){
                getJsonWhereis(polyline ,distanceForMarkerCounts,String.valueOf(polyline.getPoints().get(0).latitude)+","+String.valueOf(polyline.getPoints().get(0).longitude),String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/8)*i).latitude)+","+String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/8)*i).longitude), polylineTag);
            }
        }
        else if (distanceForMarkerCounts>1000000 && distanceForMarkerCounts<1500000){
            for (int i = 1 ; i<7; i++){
                getJsonWhereis(polyline,distanceForMarkerCounts,String.valueOf(polyline.getPoints().get(0).latitude)+","+String.valueOf(polyline.getPoints().get(0).longitude),String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/7)*i).latitude)+","+String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/7)*i).longitude), polylineTag);
            }
        }
        else {
            for (int i = 1 ; i<6; i++){
                getJsonWhereis(polyline,distanceForMarkerCounts,String.valueOf(polyline.getPoints().get(0).latitude)+","+String.valueOf(polyline.getPoints().get(0).longitude),String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/6)*i).latitude)+","+String.valueOf(polyline.getPoints().get((polyline.getPoints().size()/6)*i).longitude), polylineTag);
            }
        }

        getJsonWhereis(polyline ,distanceForMarkerCounts,String.valueOf(polyline.getPoints().get(0).latitude)+","+String.valueOf(polyline.getPoints().get(0).longitude),String.valueOf(polyline.getPoints().get((polyline.getPoints().size()-1)).latitude)+","+String.valueOf(polyline.getPoints().get((polyline.getPoints().size()-1)).longitude), polylineTag);
    }

    public void hideMarkers(String polylineTag){

        if (polylineTag.equals("polylines")){
            for (int i  = 0; i< markers1.size(); i++){
                markers1.get(i).setVisible(false);
            }

        }
        if (polylineTag.equals("polyline1")){
            for (int i  = 0; i< markers2.size(); i++){
                markers2.get(i).setVisible(false);
            }
        }
        if (polylineTag.equals("polyline2")){
            for (int i  = 0; i< markers3.size(); i++){
                markers3.get(i).setVisible(false);
            }
        }
    }

    public void showMarkers(String polylineTag, Polyline polyline){
        orderedLatLonList.clear();
        Log.e("girdi",polylineTag);
        if (polylineTag.equals("polylines")){
            for (int i  = 0; i< markers1.size(); i++){
                String oldTitle  = markers1.get(i).getTitle();
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1){
                    hour = "0" + hour;
                }
                if (minute.length() == 1){
                    minute = "0" + minute;
                }

                oldTitle = hour + ":" + minute;
                String markerLat =  String.valueOf(markers1.get(i).getPosition().latitude);
                String markerLon =  String.valueOf(markers1.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                strEndLatLon = polyline.getPoints().get(polyline.getPoints().size()-1).latitude + "," + polyline.getPoints().get(polyline.getPoints().size()-1).longitude;
                // sonuncunun saatini yanlış çekiyor o yüzden, son konum saatini byolculuğun bitiş saat değişkeninden alıyoruz
                if (strMarkerLatLon.equals(strEndLatLon)){
                    String destinationTime = destinationHour + ":" + destinationMinute;
                    String getdestinationTime[] = destinationTime.split(":");
                    String hourdestination = getdestinationTime[0];
                    String minutedestination = getdestinationTime[1];
                    if (hourdestination.length() == 1){
                        hourdestination = "0" + hourdestination;
                    }
                    if (minutedestination.length() == 1){
                        minutedestination = "0" + minutedestination;
                    }

                    oldTitle = hourdestination + ":" + minutedestination;
                }
                for (String key : markerLatLonIconList.keySet()){
                    if (key.equals(strMarkerLatLon)){
                        orderedLatLonList.add(strMarkerLatLon);
                        markers1.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView( markerLatLonIconList.get(key) , markerLatLonTempList.get(key) + "°" , oldTitle ,markerLatLonCodeList.get(key), markerLatLonis_dayList.get(key))));
                        markers1.get(i).setVisible(true);
                    }
                }
            }
        }
        if (polylineTag.equals("polyline1")){
            for (int i  = 0; i< markers2.size(); i++){
                String oldTitle  = markers2.get(i).getTitle();
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1){
                    hour = "0" + hour;
                }
                if (minute.length() == 1){
                    minute = "0" + minute;
                }

                oldTitle = hour + ":" + minute;
                String markerLat =  String.valueOf(markers2.get(i).getPosition().latitude);
                String markerLon =  String.valueOf(markers2.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                strEndLatLon = polyline.getPoints().get(polyline.getPoints().size()-1).latitude + "," + polyline.getPoints().get(polyline.getPoints().size()-1).longitude;

                // sonuncunun saatini yanlış çekiyor o yüzden, son konum saatini byolculuğun bitiş saat değişkeninden alıyoruz
                if (strMarkerLatLon.equals(strEndLatLon)){
                    String destinationTime = destinationHour + ":" + destinationMinute;
                    String getdestinationTime[] = destinationTime.split(":");
                    String hourdestination = getdestinationTime[0];
                    String minutedestination = getdestinationTime[1];
                    if (hourdestination.length() == 1){
                        hourdestination = "0" + hourdestination;
                    }
                    if (minutedestination.length() == 1){
                        minutedestination = "0" + minutedestination;
                    }

                    oldTitle = hourdestination + ":" + minutedestination;
                }
                for (String key : markerLatLonIconList.keySet()){
                    if (key.equals(strMarkerLatLon)){
                        orderedLatLonList.add(strMarkerLatLon);
                        markers2.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView( markerLatLonIconList.get(key) , markerLatLonTempList.get(key) + "°" , oldTitle,markerLatLonCodeList.get(key), markerLatLonis_dayList.get(key))));
                        markers2.get(i).setVisible(true);
                    }
                }
            }
        }
        if (polylineTag.equals("polyline2")){
            for (int i  = 0; i< markers3.size(); i++){
                String oldTitle  = markers3.get(i).getTitle();
                String getTime[] = oldTitle.split(":");
                String hour = getTime[0];
                String minute = getTime[1];
                if (hour.length() == 1){
                    hour = "0" + hour;
                }
                if (minute.length() == 1){
                    minute = "0" + minute;
                }
                oldTitle = hour + ":" + minute;
                String markerLat =  String.valueOf(markers3.get(i).getPosition().latitude);
                String markerLon =  String.valueOf(markers3.get(i).getPosition().longitude);
                String strMarkerLatLon = markerLat + "," + markerLon;
                strEndLatLon = polyline.getPoints().get(polyline.getPoints().size()-1).latitude + "," + polyline.getPoints().get(polyline.getPoints().size()-1).longitude;

                // sonuncunun saatini yanlış çekiyor o yüzden, son konum saatini byolculuğun bitiş saat değişkeninden alıyoruz
                if (strMarkerLatLon.equals(strEndLatLon)){
                    String destinationTime = destinationHour + ":" + destinationMinute;
                    String getdestinationTime[] = destinationTime.split(":");
                    String hourdestination = getdestinationTime[0];
                    String minutedestination = getdestinationTime[1];
                    if (hourdestination.length() == 1){
                        hourdestination = "0" + hourdestination;
                    }
                    if (minutedestination.length() == 1){
                        minutedestination = "0" + minutedestination;
                    }

                    oldTitle = hourdestination + ":" + minutedestination;
                }
                for (String key : markerLatLonIconList.keySet()){
                    if (key.equals(strMarkerLatLon)){
                        orderedLatLonList.add(strMarkerLatLon);
                        markers3.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView( markerLatLonIconList.get(key) , markerLatLonTempList.get(key) + "°" , oldTitle ,markerLatLonCodeList.get(key), markerLatLonis_dayList.get(key))));
                        markers3.get(i).setVisible(true);
                    }
                }
            }
        }

    }

    // marker iconları için bitmap oluşturucu
    private Bitmap getMarkerBitmapFromView(String iconURL, String celcius, String time, String code, String isAday) {
        String tempImageName = null;
        String url[] = iconURL.split("/");
        String iconName = url[url.length-1];
        String icon[] = iconName.split("\\.");
        String iconNameWithoutPNG = icon[0];
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custommarkerdesign, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.imageViewIcon);
      //  Picasso.get().load("https:"+iconURL).into(markerImageView);

//        Glide.with(this).load("https:"+iconURL).into(markerImageView);

        if (isAday.equals("0")){
            tempImageName = "n" + iconNameWithoutPNG;
        }else {
            tempImageName = "d" + iconNameWithoutPNG ;
        }
        markerImageView.setImageResource(this.getResources().getIdentifier(tempImageName,"drawable", getPackageName()));

        TextView markerTextViewCelcius = (TextView) customMarkerView.findViewById(R.id.textViewCelcius);
        markerTextViewCelcius.setText(celcius);
        TextView markerTextViewTime = (TextView) customMarkerView.findViewById(R.id.textViewTime);
        markerTextViewTime.setText(time);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    // başlangıç ve bitiş marker iconları için bitmap oluşturucu
    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        binding.buttonShowTimeline.setEnabled(false);
        binding.buttonShowTimeline.setVisibility(View.INVISIBLE);
        binding.buttonDirection.setEnabled(false);
        binding.buttonDirection.setVisibility(View.INVISIBLE);
        dialog.setContentView(R.layout.no_road);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button buttonOkPasswordInfo=dialog.findViewById(R.id.buttonOkPasswordInfo);
        dialog.setCanceledOnTouchOutside(true);

        buttonOkPasswordInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public void onRoutingStart() {
        // Toast.makeText(MapsActivity.this,"Finding Route...",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRoutingCancelled() {
        Findroutes(myLocationltlng, endLatLngToEdittext);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
         Findroutes(myLocationltlng, endLatLngToEdittext);
    }

    // fromLatLng bu nokta ile toLatLng bu nokta arasındaki mesafe ve süreyi almak için kullanıyoruz, gidilmek istenen 2 mesafe arasındaki 5 noktayı buluyor
    public void getJsonWhereis(Polyline polyline,int distanceForMarkerCounts, String fromLatLng, String toLatLng, String polylineTag){

        String temporaryUrl = "";
        String mainUrl = "https://maps.googleapis.com/maps/api/directions/json";
        // temporaryUrl = "https://maps.googleapis.com/maps/api/directions/json?destination=39.92347799999999,32.84757099999999&origin=39.914765,32.8583349&key=AIzaSyCZs3vnvP8MC0U5bCUQd0fkdatM546SCKQ";

        temporaryUrl = mainUrl + "?destination=" + toLatLng + "&origin=" + fromLatLng + "&key=" + APIKEY;
        Log.e("api", temporaryUrl);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, temporaryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // Response json yapısı nasıl onu anlamak için sout olarak yazdırdık
                //Log.e("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray routes = jsonObject.getJSONArray("routes");
                    JSONObject route1 = routes.getJSONObject(0);

                    JSONArray legs = route1.getJSONArray("legs");
                    JSONObject leg1 = legs.getJSONObject(0);

                    JSONObject distance = leg1.getJSONObject("distance");
                    markerDistanceTextToOrigin = distance.getString("text");
                    markerDistanceValueToOrigin = distance.getString("value");

                    JSONObject duration = leg1.getJSONObject("duration");
                    markerDurationTextToOrigin = duration.getString("text");
                    markerDurationValueToOrigin = duration.getString("value");

                    markerEndAddress = leg1.getString("end_address");

                    JSONObject endLocation = leg1.getJSONObject("end_location");
                    markerEndLocationLatt = endLocation.getString("lat");
                    markerEndLocationLngg = endLocation.getString("lng");

                    markerStartAddress = leg1.getString("start_address");

                    JSONObject startLocation = leg1.getJSONObject("start_location");
                    markerStartLocationLatt = startLocation.getString("lat");
                    markerStartLocationLngg = startLocation.getString("lng");


                    long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
                    // add currentTimeMilles and duration value (duration value is second so we multiple 1000 and converted millisecond)
                    long destinationTimeMillis = currentTimeMillis + ((Integer.parseInt(markerDurationValueToOrigin))*1000) ;

                    // convert destinationTimeMillis to date format and get hour and minute
                    Date dateLater=new Date(destinationTimeMillis);
                    int destinationHour = dateLater.getHours();
                    int destinationMinute = dateLater.getMinutes();

                    /*if (destinationMinute<30){
                        getWeatherDetails(polyline ,polylineTag ,markerEndLocationLatt +","+ markerEndLocationLngg, destinationHour);
                    }else {
                        getWeatherDetails(polyline ,polylineTag,markerEndLocationLatt +","+ markerEndLocationLngg, destinationHour+1);

                    }*/
                    if (destinationMinute<30){
                        getWeatherDetails(polyline ,polylineTag ,toLatLng, destinationHour);
                    }else {
                        getWeatherDetails(polyline ,polylineTag,toLatLng, destinationHour+1);

                    }

                    markerDistanceFromOrigin.put(toLatLng,markerDistanceTextToOrigin);
                    markerDistanceFromOriginValue.put(toLatLng,markerDistanceValueToOrigin);
                    //loader.dismiss();
                    String[] splitLatLon = toLatLng.split(",");
                    double lat = Double.parseDouble(String.valueOf(splitLatLon[0]));
                    double lon = Double.parseDouble(String.valueOf(splitLatLon[1]));
                    LatLng stepLatLng = new LatLng(lat, lon);
                    MarkerOptions stepMarker = new MarkerOptions();
                    stepMarker.position(stepLatLng);
                  //  stepMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                    stepMarker.title(String.valueOf(destinationHour) + ":" + String.valueOf(destinationMinute)   );

                    int markerCounts = 6;
                    if (distanceForMarkerCounts>=1500000){
                        markerCounts = 8;
                    }
                    else if (distanceForMarkerCounts>1000000 && distanceForMarkerCounts<1500000) {
                        markerCounts = 7;
                    }
                    else {
                        markerCounts = 6;
                    }

                    if (markerOptionsList1.size()<markerCounts && polylineTag.equals("polylines")){
                        markerOptionsList1.add(stepMarker);
                        Marker m = mMap.addMarker(stepMarker);
                        m.setVisible(false);
                        markers1.add(m);
                    }
                    else if (markerOptionsList2.size()<markerCounts && polylineTag.equals("polyline1")){
                        markerOptionsList2.add(stepMarker);
                        Marker m = mMap.addMarker(stepMarker);
                        m.setVisible(false);
                        markers2.add(m);
                    }
                    else if (markerOptionsList3.size()<markerCounts && polylineTag.equals("polyline2")){
                        markerOptionsList3.add(stepMarker);
                        Marker m = mMap.addMarker(stepMarker);
                        m.setVisible(false);
                        markers3.add(m);
                    }

                }catch (JSONException e){
                    Toast.makeText(getApplicationContext(),"Not found any route!", Toast.LENGTH_SHORT).show();
                    Log.e("error", e.toString());
                    dialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Not found any route!", Toast.LENGTH_SHORT).show();
                Log.e("error", error.toString());
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(),"Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Enter Correct City Name", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
        sum+=1;

    }

    public void getWeatherDetails(Polyline polyline , String polylineTag , String latlon, int weatherHour){

        String temporaryUrl = "";

        String mainUrl = "https://api.weatherapi.com/v1/forecast.json?key=";
        String weatherApiKey = "fa1fd96defb94768ac270319221208";


        temporaryUrl = mainUrl + weatherApiKey + "&q=" + latlon + "&days=2&aqi=no&alerts=no" ;
        //temporaryUrl = "https://api.weatherapi.com/v1/forecast.json?key=fa1fd96defb94768ac270319221208&q=39.925533,32.866287&days=2&aqi=no&alerts=no";;
        Log.e("api weather ", temporaryUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, temporaryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject=new JSONObject(response);
                    JSONObject jsonObjectLocation = jsonObject.getJSONObject("location");
                    //localtime = jsonObjectLocation.getString("localtime");
                    String name = jsonObjectLocation.getString("name");


                    JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
                    //  temp_c = jsonObjectCurrent.getString("temp_c");
                    JSONObject jsonObjectCondition=jsonObjectCurrent.getJSONObject("condition");
                    //     text=jsonObjectCondition.getString("text");
                    // icon=jsonObjectCondition.getString("icon");
                    // wind_kph = jsonObjectCurrent.getString("wind_kph");
                    // humidity = jsonObjectCurrent.getString("humidity");
                    // cloud = jsonObjectCurrent.getString("cloud");


                    // Picasso.get().load("https:"+icon).into(imageViewTodayWeatherIcon);


                    JSONObject jsonObjectForecast = jsonObject.getJSONObject("forecast");
                    JSONArray jsonArrayForecastDay=jsonObjectForecast.getJSONArray("forecastday");


                    JSONObject WeatherToday = jsonArrayForecastDay.getJSONObject(0);
                   // dateToday=WeatherToday.getString("date");
                    JSONArray jsonArrayHourToday=WeatherToday.getJSONArray("hour");

                    JSONObject hourToday = jsonArrayHourToday.getJSONObject(weatherHour);
                     String temp_c =hourToday.getString("temp_c");
                    String is_day =hourToday.getString("is_day");

                    JSONObject jsonObjectConditionToday=hourToday.getJSONObject("condition");
                    String  iconToday=jsonObjectConditionToday.getString("icon");
                    String code = jsonObjectConditionToday.getString("code");

                    markerLatLonis_dayList.put(latlon,is_day);
                    markerLatLonCodeList.put(latlon,code);
                    markerLatLonTempList.put(latlon,temp_c);
                    markerLatLonIconList.put(latlon,iconToday);


                    JSONObject WeatherSecond = jsonArrayForecastDay.getJSONObject(1);
                   // dateSecond=WeatherSecond.getString("date");
                    JSONArray jsonArrayHourSecond=WeatherSecond.getJSONArray("hour");
                    JSONObject hourSecond = jsonArrayHourSecond.getJSONObject(12);
                  //  temp_cSecondDay=hourSecond.getString("temp_c");
                    JSONObject jsonObjectConditionSecondDay=hourSecond.getJSONObject("condition");
                  //  iconSecondDay=jsonObjectConditionSecondDay.getString("icon");
                    String textSecondDay = jsonObjectConditionSecondDay.getString("text");

                   // Picasso.get().load("https:"+iconSecondDay).into(imageViewTodayWeatherIconSecondDay);

                    if (polyline.getColor() == -222188){//-222188, color1 in değeri
                        tagPolyline = polyline.getTag().toString();
                    }else{

                    }
                    countsTimesOfEnteredGetWeatherDetails +=1;
                    Log.e("counts", String.valueOf(countsTimesOfEnteredGetWeatherDetails));
                    Log.e("sum", String.valueOf(sum));
                    if (countsTimesOfEnteredGetWeatherDetails == sum && tagPolyline!=null){
                        dialog.dismiss();
                        binding.buttonShowTimeline.setEnabled(true);
                        binding.buttonDirection.setEnabled(true);
                        showMarkers(tagPolyline, polyline);
                    }
                    if (((sum-countsTimesOfEnteredGetWeatherDetails)<=1)){
                        dialog.dismiss();
                        binding.buttonShowTimeline.setEnabled(true);
                        binding.buttonDirection.setEnabled(true);
                    }
                    if (sum>=15  && ((sum-countsTimesOfEnteredGetWeatherDetails)<=3)){
                        dialog.dismiss();
                        showMarkers(tagPolyline, polyline);
                        binding.buttonShowTimeline.setEnabled(true);
                        binding.buttonDirection.setEnabled(true);
                    }



                } catch (JSONException e) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("error",error.toString());

                if (error instanceof NoConnectionError) {
                    Log.e("hata","temporaryUrl");
                    Toast.makeText(getApplicationContext(),"Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Enter Correct City Name", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });


        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


}
