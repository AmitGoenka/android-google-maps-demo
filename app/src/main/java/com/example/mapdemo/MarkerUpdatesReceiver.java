package com.example.mapdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: agoenka
 * Created At: 11/10/2016
 * Version: ${VERSION}
 */

public class MarkerUpdatesReceiver extends BroadcastReceiver {

    public static final String TAG = MarkerUpdatesReceiver.class.getSimpleName();
    private static final String intentAction = "com.parse.push.intent.RECEIVE";
    private Activity mActivity;

    public interface PushInterface {
        void onMarkerUpdate(PushRequest pushRequest);
    }

    public MarkerUpdatesReceiver(Activity activity) {
        if (!(activity instanceof PushInterface)) {
            throw new IllegalStateException("activity needs to implement push interface");
        } else {
            mActivity = activity;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action: " + action);

        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                JSONObject customData = new JSONObject(json.getString("customData"));

                PushRequest pushRequest = new PushRequest(customData);

                if (!pushRequest.userId.equals(ParseUser.getCurrentUser().getObjectId())) {
                    ((PushInterface) mActivity).onMarkerUpdate(pushRequest);
                }

                Log.d(TAG, "got action " + action + "with " + customData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}