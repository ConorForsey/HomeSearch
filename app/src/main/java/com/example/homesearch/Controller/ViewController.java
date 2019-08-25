package com.example.homesearch.Controller;

import android.content.Context;

import com.example.homesearch.Model.Crime;
import com.example.homesearch.Model.GeocodeDataHandler;
import com.example.homesearch.Model.Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewController {
    //Declare Crimes list
    private ArrayList<Crime> mCrimes;
    private GeocodeDataHandler geocodeDataHandler;

    public ViewController(){
        geocodeDataHandler = new GeocodeDataHandler();
    }

    public String getGeoCodeData(String url){
        return geocodeDataHandler.getHTTPData(url);
    }

    public ArrayList<Crime> parseJSON(String response) {
        mCrimes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject crimeObj = jsonArray.getJSONObject(i);
                Crime newCrime = new Crime();

                newCrime.setmCategory(crimeObj.getString("category"));
                newCrime.setmLocationType(crimeObj.getString("location_type"));

                JSONObject crimeLocationObj = crimeObj.getJSONObject("location");
                newCrime.setmLatitude(crimeLocationObj.getDouble("latitude"));

                JSONObject crimeStreetObj = crimeLocationObj.getJSONObject("street");
                newCrime.setmStreetID(crimeStreetObj.getInt("id"));
                newCrime.setmStreetName(crimeStreetObj.getString("name"));
                newCrime.setmLongitude(crimeLocationObj.getDouble("longitude"));
                newCrime.setmContext(crimeObj.getString("context"));

                if (crimeObj.getString("outcome_status").equals("null")) {
                    newCrime.setmOutcome_status("Not Investigated");
                } else {
                    JSONObject outcomeObj = crimeObj.getJSONObject("outcome_status");
                    newCrime.setmOutcome_status("Investigated");
                    newCrime.setmOutcome_Category(outcomeObj.getString("category"));
                    newCrime.setmOutcome_Date(outcomeObj.getString("date"));
                }

                newCrime.setmPersistentID(crimeObj.getString("persistent_id"));
                newCrime.setmCrimeID(crimeObj.getInt("id"));
                newCrime.setmLocationSubtype(crimeObj.getString("location_subtype"));
                newCrime.setmMonth(crimeObj.getString("month"));

                mCrimes.add(newCrime);
            }
            return mCrimes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
