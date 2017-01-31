package com.lluis.bayer.bicingmap.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.alexvasilkov.events.Events;
import com.lluis.bayer.bicingmap.api.ViaBicing;
import com.lluis.bayer.bicingmap.models.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class APIService extends IntentService {

    public APIService() {
        super("APIService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Events.post("begin-api-call");
            ViaBicing api = new ViaBicing();
            try {
                List<Station> stations = api.getStations();
                System.out.println("apiservice aa: "+stations);
                if(stations != null){
                    System.out.println("apiservice: "+stations.size());
                    Events.create("return-api-call").param(stations).post();
                }
            }catch(IOException e){
                Log.e("ERR", e.getMessage());
            }
            Events.post("end-api-call");
        }
    }
}
