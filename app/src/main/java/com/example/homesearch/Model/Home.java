package com.example.homesearch.Model;

public class Home {
    private int id;
    private String mName;
    private String mAddress;

    public Home(int id, String name, String address) {
        this.id = id;
        this.mName = name;
        this.mAddress = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
