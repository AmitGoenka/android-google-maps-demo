package com.example.mapdemo;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: agoenka
 * Created At: 11/10/2016
 * Version: ${VERSION}
 */

public class PushRequest {

    public String markerId;
    String userId;
    public LatLng mapLocation;
    public String title;
    public String snippet;

    public PushRequest(JSONObject data) throws JSONException {
        JSONObject location = data.getJSONObject("location");
        markerId = data.getString("markerId");
        title = data.getString("title");
        snippet = data.optString("snippet");
        userId = data.getString("userId");
        mapLocation = new LatLng(location.getDouble("lat"), location.getDouble("long"));
    }
}