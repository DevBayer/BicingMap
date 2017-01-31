package com.lluis.bayer.bicingmap.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by 23878410v on 31/01/17.
 */

public interface EndpointInterface {

    @GET("v2/stations")
    Call<com.lluis.bayer.bicingmap.models.ViaBicing> getStations();


}
