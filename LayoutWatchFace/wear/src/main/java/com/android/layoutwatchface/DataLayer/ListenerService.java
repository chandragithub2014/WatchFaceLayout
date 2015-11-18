package com.android.layoutwatchface.DataLayer;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by 245742 on 9/28/2015.
 */
public class ListenerService extends WearableListenerService {

    GoogleApiClient mGoogleApiClient;
    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    @Override
    public void onCreate() {
        super.onCreate();
      /*  mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();*/
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {}
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.v("myTag", "DataMap received on watch: " + dataMap);

                // Broadcast DataMap contents to wearable activity for display
                // The content has the golf hole number and distances to the front,
                // middle and back pin placements.
/*
 DataMap dataMap = new DataMap();

        dataMap.putInt("reminderCount", 0);
        dataMap.putInt("workListCount", 0);
 */
                Log.v("myTag", "DataMap received on watch Reminder Count: "+dataMap.getInt("reminderCount"));
                         Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("reminderCount", dataMap.getInt("reminderCount"));
                messageIntent.putExtra("workListCount", dataMap.getInt("workListCount"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

            }
        }
    }
}
