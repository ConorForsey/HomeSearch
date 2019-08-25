package com.example.homesearch.Model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VolleyService {
    IResult mResultCallback = null;
    Context mContext;
    ArrayList<Crime> mCrimes;

    public VolleyService(IResult CallbackResult, Context context){
        mResultCallback = CallbackResult;
        mContext = context;
        mCrimes = new ArrayList<>();
    }

    public void getCrimeData(String url){
        try{
            RequestQueue mQueue = Volley.newRequestQueue(mContext);
            //Data request
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                if(mResultCallback != null){
                                    mResultCallback.notifySuccess(response);
                                }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(error);
                }
            });
            mQueue.add(request);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Crime> getCrimes(){
        return mCrimes;
    }

}
