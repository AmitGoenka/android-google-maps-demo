package com.example.mapdemo.utils;

import android.content.Context;

import com.example.mapdemo.PushRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Author: agoenka
 * Created At: 11/10/2016
 * Version: ${VERSION}
 */

public class PushUtil {

    private Hashtable<String, Marker> userMarkerMap = new Hashtable<>();

    private static JSONObject getPayloadFromMarker(Marker marker) throws JSONException {
        LatLng position = marker.getPosition();

        JSONObject location = new JSONObject();
        location.put("lat", position.latitude);
        location.put("long", position.longitude);

        JSONObject payload = new JSONObject();
        payload.put("location", location);
        payload.put("title", marker.getTitle());
        payload.put("snippet", marker.getSnippet());

        String markerId = ParseUser.getCurrentUser().getObjectId() + "_" + marker.getId();
        payload.put("markerId", markerId);

        payload.put("userId", ParseUser.getCurrentUser().getObjectId());
        return payload;
    }

    public static void sendPushNotification(Marker marker, String channelName) {
        try {
            JSONObject payload = getPayloadFromMarker(marker);
            HashMap<String, String> data = new HashMap<>();
            data.put("customData", payload.toString());
            data.put("channel", channelName);
            // The code that processes this function is listed at:
            // https://github.com/rogerhu/parse-server-push-marker-example/blob/master/cloud/main.js
            ParseCloud.callFunctionInBackground("pushToChannel", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleMarkerUpdates(Context context, PushRequest pushRequest, GoogleMap map) {
        Marker marker = userMarkerMap.get(pushRequest.markerId);
        if (marker != null) {
            marker.setPosition(pushRequest.mapLocation);
            marker.setTitle(pushRequest.title);
        } else {
            BitmapDescriptor defaultMarker = MapUtils.createBubble(context, IconGenerator.STYLE_BLUE, pushRequest.title);
            marker = MapUtils.addMarker(map, pushRequest.mapLocation, pushRequest.title, pushRequest.snippet, defaultMarker);

            MapUtils.dropPinEffect(marker);
            userMarkerMap.put(pushRequest.markerId, marker);
        }
    }
}