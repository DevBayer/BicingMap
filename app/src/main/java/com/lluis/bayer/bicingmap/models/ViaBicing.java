package com.lluis.bayer.bicingmap.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 23878410v on 31/01/17.
 */

public class ViaBicing implements Serializable {
    List<Station> stations;

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}
