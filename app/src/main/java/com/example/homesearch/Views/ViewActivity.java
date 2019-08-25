package com.example.homesearch.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.homesearch.Controller.ViewController;
import com.example.homesearch.Model.Crime;
import com.example.homesearch.Adapter.CrimeAdapter;
import com.example.homesearch.Model.GeocodeDataHandler;
import com.example.homesearch.Model.IResult;
import com.example.homesearch.Model.VolleyService;
import com.example.homesearch.R;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.joda.time.DateTime;
import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity implements OnMapReadyCallback{
    //Recycler view
    private RecyclerView recyclerView;

    //Arraylist adapter
    private CrimeAdapter mAdapter;

    //When no data in mCrimes is present, displays no data text
    TextView mEmptyView;

    //Declare Controller
    private ViewController mVC;

    //Volley Callback
    IResult mResultCallback = null;

    //Declare Volley Service
    VolleyService mVolleyService;

    //Crime array list
    private ArrayList<Crime> mCrimes;

    //Map fragment
    private SupportMapFragment mapFragment;
    private FragmentTransaction fm;

    //Main Activity location search
    private Intent intent;
    private String mLocationMessage;

    //System date - Using the joda library
    private int mMonth = 1; //Change find more recent or less recent crime data results
    //Subtracted 3 months as that is how often the uk police database is updated
    private DateTime mDate = new DateTime().minusMonths(mMonth);
    // Format for output
    DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM");

    //Search location title and snippet - For google maps
    private String mTitle;
    private String mSnippet;

    //Search Longitude and Latitude
    private double mLat;
    private double mLng;

    //Creates square around mLat and mLng - To be used to create a custom polygon
    private double mDist = 0.005; //Can be changed to increase or decrease search range
    private double mLat1, mLng1, mLat2, mLng2, mLat3, mLng3, mLat4, mLng4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //Get the activity that started this activity and extract the string
        intent = getIntent();

        //Recycler view
        recyclerView = (RecyclerView)findViewById(R.id.uiCrimesRecycler);
        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //No data textbox
        mEmptyView = (TextView) findViewById(R.id.uiNodataTextbox);

        //Initialise View Controller
        mVC = new ViewController();

        //Initialise Crimes list and adapter
        mCrimes = new ArrayList<>();
        mAdapter = new CrimeAdapter(mCrimes);

        //Check if data in present in the mCrimes list
        setRecyclerView(mCrimes);

        //Initialise volley callback
        initVolleyCallback();
        //Create a new volley service
        mVolleyService = new VolleyService(mResultCallback, ViewActivity.this);

        //Recycler view properties
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //Google maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fm = mapFragment.getFragmentManager().beginTransaction();
        fm.hide(mapFragment);

        Intent intent = getIntent();
        mTitle = intent.getStringExtra(MainActivity.EXTRA_ID);
        mLocationMessage = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        mSnippet = mLocationMessage;
        new GetCoordinates().execute(mLocationMessage);
    }

    private void initVolleyCallback(){
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String response) {
                mCrimes = mVC.parseJSON(response);
                //Will keep searching through dates until the most recent data is found
                if(mCrimes.size() == 0){
                    mEmptyView.setText("Searching for data");
                    mMonth = mMonth + 1;
                    mDate = new DateTime().minusMonths(mMonth);
                    mVolleyService.getCrimeData("https://data.police.uk/api/crimes-street/all-crime?poly="
                            + mLat1 + "," + mLng1 + ":"
                            + mLat2 + "," + mLng2 + ":"
                            + mLat3 + "," + mLng3 + ":"
                            + mLat4 + "," + mLng4 + ":"
                            + "&date=" + dtfOut.print(mDate));
                }else{
                    //Data has been found
                    mAdapter = new CrimeAdapter(mCrimes);
                    recyclerView.setAdapter(mAdapter);
                    setRecyclerView(mCrimes);
                    LoadMapData();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                error.printStackTrace();
                setRecyclerView(mCrimes);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uiHomeIcon:
                finish();
                break;
            case R.id.uiMapIcon:
                recyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);
                fm.show(mapFragment);
                break;
            case R.id.uiListIcon:
                fm.hide(mapFragment);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        return true;
    }

    private void setRecyclerView(ArrayList<Crime> crime){
        if (crime.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Marker for center point
        LatLng cameraCenter = new LatLng(mLat,mLng);
        googleMap.addMarker(new MarkerOptions()
                .position(cameraCenter)
                .title(mTitle)
                .snippet(mSnippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

        //Move camera to center point and zoom
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraCenter, 14.0f));

        //Make markers for all crimes in area
        for(int i = 0; i < mCrimes.size(); i++){
            Crime c = mCrimes.get(i);
            LatLng newMarker = new LatLng(c.getmLatitude(), c.getmLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(newMarker)
                    .title(c.getmCategory())
                    .snippet(c.getmOutcome_status())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(ViewActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address  + "&key=" + "AIzaSyDQSgzGJM-U5T_IAmYKdg1sz1O-piYUlew";
                response = mVC.getGeoCodeData(url);
                return response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                mLat = Double.valueOf(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString());
                mLng = Double.valueOf(((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString());

                SearchCrimeData(mLat,mLng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void SearchCrimeData(Double lat, Double lng){
        //Create a square of coordinates around lat, lng
        mLat1 = lat - mDist;
        mLng1 = lng + mDist;

        mLat2 = lat + mDist;
        mLng2 = lng + mDist;

        mLat3 = lat - mDist;
        mLng3 = lng - mDist;

        mLat4 = lat + mDist;
        mLng4 = lng - mDist;

        //Search for data in mDist radius - Can be changed in variable at the top
        mVolleyService.getCrimeData("https://data.police.uk/api/crimes-street/all-crime?poly="
                + mLat1 + "," + mLng1 + ":"
                + mLat2 + "," + mLng2 + ":"
                + mLat3 + "," + mLng3 + ":"
                + mLat4 + "," + mLng4 + ":"
                + "&date=" + dtfOut.print(mDate));
    }

    private void LoadMapData(){
        mapFragment.getMapAsync(this);
    }
}
