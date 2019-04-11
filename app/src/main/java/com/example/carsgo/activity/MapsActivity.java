package com.example.carsgo.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carsgo.R;
import com.example.carsgo.adapter.PlaceAutocompleteAdapter;
import com.example.carsgo.model.PlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AutoCompleteTextView etSearchText;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private ProgressDialog mapLoadingDialog;
    private ImageView ivCurrentGpsLocation,ivPlaceDetail;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS =new LatLngBounds(new LatLng(-40,-168),new LatLng(71,-136));
    private PlacesData placesData;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        etSearchText=findViewById(R.id.et_input_search);
        ivCurrentGpsLocation=findViewById(R.id.iv_gps);
        ivPlaceDetail=findViewById(R.id.iv_Info);

        mapLoadingDialog=mapLoadingDialog.show(MapsActivity.this,"Status","");
        getLocationPermission();
    }

    //Initialize Search Bar
    private void init(){

        //Create Google Client
        mGoogleApiClient=new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();

        //Set Click Listener on Adapter
        etSearchText.setOnItemClickListener(mAutocompleteListener);

        //Set Up AutoComplete Adapter
        mPlaceAutocompleteAdapter =new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);
        etSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mapLoadingDialog.setMessage("Initializing Search Bar");
        etSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });

        //Show Window Of Info on Click
        ivPlaceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        mMarker.showInfoWindow();
                    }
                }catch (Exception e){

                }
            }
        });

        ivCurrentGpsLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceCurrentLocation();
            }
        });
        hideSoftKeyboard();
    }

    //Find Location using GeoLocator
    private void geoLocate(){

        Log.i(TAG, "geoLocate: Geolocating...");
        String searchText = etSearchText.getText().toString();

        Geocoder geocoder =new Geocoder(MapsActivity.this);
        List<Address> list=new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchText,1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOEXCEPTION"+e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.i(TAG, "geoLocate: Location Found:"+address.toString()  );
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    //Initialize Map Fragment
    private void initMap() {
        mapLoadingDialog.setMessage("Initializing Map..");
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    //Check Permissions for Location
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Getting Location Permission");
        mapLoadingDialog.setMessage("Checking Location Permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: called");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mapLoadingDialog.setMessage("Permissions OK");
                    initMap();
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(MapsActivity.this, "Map is Ready!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mapLoadingDialog.setMessage("Fetching Current User Location");
        getDeviceCurrentLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        init();
        mapLoadingDialog.dismiss();
    }

    //Current Device Location
    private void getDeviceCurrentLocation(){
        mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){

                Task location=mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation=(Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,"My Location");
                        }else {
                            Log.d(TAG, "onComplete: Not Found Location");
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceCurrentLocation: Security Exception" +e.getMessage());
        }
    }

    //Move The Camera According to Position
    private void moveCamera(LatLng latLng,float zoom,String title){
        Log.d(TAG, "moveCamera: Latitude:"+latLng.latitude + "Longitude:"+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));


        if(!title.equals("My Location")){
            MarkerOptions options=new MarkerOptions()
                    .position(latLng)
                    .title(title);

            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    //Move Camera For Displaying INfo
    //Move The Camera According to Position
    private void moveCamera(LatLng latLng,float zoom,PlacesData placesData) {
        Log.d(TAG, "moveCamera: Latitude:"+latLng.latitude + "Longitude:"+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        mMap.clear();

        if(placesData != null){
            try{
               String placeInfo= "Address : "+placesData.getAddress()
                                +"\nName : "+placesData.getPhoneNumber()
                                +"\nRatings : "+placesData.getRating()
                                +"\nWebsite : "+placesData.getWebsiteUri()
                                +"\nLatLng : "+placesData.getLatLng();

               MarkerOptions markerOptions=new MarkerOptions()
                       .position(latLng)
                       .title(placesData.getName())
                       .snippet(placeInfo);

               mMarker=mMap.addMarker(markerOptions);
            } catch (NullPointerException e){
                Log.e(TAG, "moveCamera: Null Pointer"+ e.getMessage() );
            }
        }else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    //Hide Keyboard After Search
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: Connection Failed"+connectionResult);
    }

    // ======================================= Google Places API Auto Complete API ===========================================


    private AdapterView.OnItemClickListener mAutocompleteListener =new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();
            //Place Contains Properties of Location
            final AutocompletePrediction item =mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();


            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceCallback);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceCallback =new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.i(TAG, "onResult: Did not found Result!"+places.getStatus().toString());
                places.release();
                return;
            }
            final Place place=places.get(0);

            try {
                placesData = new PlacesData(
                        place.getName().toString(),
                        place.getAddress().toString(),
                        place.getPhoneNumber().toString(),
                        place.getId(),
                        place.getWebsiteUri(),
                        place.getLatLng(),
                        place.getRating()
                );
                Log.i(TAG, "onResult: Details:"+placesData.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: Exception :Data is not Available!"+e.getMessage());
            }

            //This may through null pointer so,
           // moveCamera(place.getLatLng(),DEFAULT_ZOOM,place.getName().toString());

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude),DEFAULT_ZOOM,placesData);
            places.release();
        }
    };
}
