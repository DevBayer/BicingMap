package com.lluis.bayer.bicingmap.api;

import com.lluis.bayer.bicingmap.models.Station;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 23878410v on 31/01/17.
 */

public class ViaBicing {
    public static final String BASE_URL = "http://wservice.viabicing.cat/";
    private EndpointInterface service;

    public ViaBicing() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(EndpointInterface.class);
    }

    public List<Station> getStations() throws IOException {
        Call<com.lluis.bayer.bicingmap.models.ViaBicing> call = service.getStations();
        Response<com.lluis.bayer.bicingmap.models.ViaBicing> response = call.execute();

        if(!response.isSuccessful()) return null;

        return response.body().getStations();

    }

}
