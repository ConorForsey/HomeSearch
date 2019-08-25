package com.example.homesearch.Model;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;

public interface IResult {
    void notifySuccess(String response);
    void notifyError(VolleyError error);
}
