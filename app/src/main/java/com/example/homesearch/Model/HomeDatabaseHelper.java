package com.example.homesearch.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.homesearch.Model.Home;

import java.util.ArrayList;

public class HomeDatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "home_table";
    private static final int DATABASE_VERSION = 1;
    private static final String COL1 = "ID";
    private static final String COL2 = "NAME";
    private static final String COL3 = "ADDRESS";

    public HomeDatabaseHelper(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME +
                "(" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Retrieve all homes
    public ArrayList<Home> getAllHomes(){
        ArrayList<Home> homes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[] {COL1 , COL2, COL3},
                null, null,null, null,null);

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    homes.add(new Home(Integer.parseInt(cursor.getString(0)),
                                        cursor.getString(1), cursor.getString(2)));
                }while(cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
        return homes;
    }
    //Delete Home
    public void deleteHome(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME,COL1 +" = ?", new String[]{Integer.toString(id)});
    }

    //Add Saved Location
    public boolean AddHome(String locationName, String address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, locationName);
        contentValues.put(COL3, address);

        //Checks if data was input correctly (returns -1 if not)
        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1){
            return false;
        } else return true;
    }
}
