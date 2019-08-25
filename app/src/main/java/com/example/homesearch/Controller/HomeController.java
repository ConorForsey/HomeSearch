package com.example.homesearch.Controller;

import android.content.Context;
import com.example.homesearch.Model.Home;
import com.example.homesearch.Model.HomeDatabaseHelper;
import java.util.ArrayList;

public class HomeController {
    private HomeDatabaseHelper db;

    //This controller will use the database as the model
    public HomeController(Context context){
        db = new HomeDatabaseHelper(context);
    }

    public ArrayList<Home> getAllHomes(){
        return db.getAllHomes();
    }

    public boolean addData(String locationName, String address){
        if(db.AddHome(locationName, address)){
            return true;
        } else return false;
    }

    public void removeData(int position){

     db.deleteHome(position);
    }
}
