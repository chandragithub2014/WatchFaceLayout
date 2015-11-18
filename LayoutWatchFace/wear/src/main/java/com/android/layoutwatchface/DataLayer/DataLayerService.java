package com.android.layoutwatchface.DataLayer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by CHANDRASAIMOHAN on 10/23/2015.
 */
public class DataLayerService implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    Context ctx;
    GoogleApiClient mGoogleApiClient;
    private static DataLayerService instance;
   String message="companion";
    private DataLayerService(){

    }

    public static DataLayerService getInstance(){
        if(instance == null){
            instance = new DataLayerService();
        }
        return instance;
    }
 /*  public MessageService(Context ctx){
       this.ctx=ctx;
   }*/
    @Override
    public void onConnected(Bundle bundle) {

        Log.v("myApp", "OnConnected entered");

        String WEARABLE_DATA_PATH = "/wearable_data";

        // Create a DataMap object and send it to the data layer
        DataMap dataMap = new DataMap();

        dataMap.putInt("reminderCount", 0);
        dataMap.putInt("workListCount", 0);

        //Requires a new thread to avoid blocking the UI
        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();
     /*   if(mGoogleApiClient.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    for(Node node : nodes.getNodes()) {
                        MessageApi.SendMessageResult result = null;
                        if(message.equalsIgnoreCase("bulk")){
                            Log.d("TAG","Send message when bulk");
                    //        result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", ("bulk"+"$"+ MobileApplication.getInstance().getBulkUpdatedList()).getBytes()).await();
                        }*//*else if(message.equalsIgnoreCase("accountUpdate")){
                            Log.d("TAG","Send message when accountUpdate");
                            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", ("accountUpdate"+"$"+ MobileApplication.getInstance().getUpdatedAccountDetails()).getBytes()).await();
                        }else if(message.equalsIgnoreCase("handoffSearch")){
                            //handoffSearch
                            Log.d("TAG","Send message when handoffSearch");
                            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", ("handoffSearch"+"$"+ MobileApplication.getInstance().getHandOffSearchJSON()).getBytes()).await();
                        }else  if(message.equalsIgnoreCase("handoffpatient")){
                            Log.d("TAG","Send message when handoffpatient");
                            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", ("handoffpatient"+"$"+ MobileApplication.getInstance().getHandOffPatientJSON()).getBytes()).await();
                        }else  if(message.equalsIgnoreCase("revertpatient")){
                            Log.d("TAG","Send message when revertpatient");
                            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", ("revertpatient"+"$"+ MobileApplication.getInstance().getPatientRevertJSON()).getBytes()).await();
                        }*//*
                        else {
                            result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/message_path", message.getBytes()).await();
                        }
                        if(!result.getStatus().isSuccess()){
                            Log.e("test", "error");
                        } else {
                            Log.i("test", "success!! sent to: " + node.getDisplayName());
                        }
                    }
                }
            }).start();

        } else {
            Log.e("test", "not connected");
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void startDataLayerService(Context ctx,String message){
        this.ctx=ctx;
        this.message=message;
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    public void stopDataLayerService(Context ctx){
        this.ctx=ctx;
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {

                // Construct a DataRequest and send over the data layer
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send DataMap");
                }
            }
        }
    }
}
