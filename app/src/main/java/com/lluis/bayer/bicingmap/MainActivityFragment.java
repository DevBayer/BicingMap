package com.lluis.bayer.bicingmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.events.Events;
import com.lluis.bayer.bicingmap.models.Station;
import com.lluis.bayer.bicingmap.services.APIService;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    MapView map;
    ProgressDialog dialog;
    Context mContext;

    public MainActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Events.register(this);
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("Descargando datos...");

        Intent apiservice = new Intent(getContext(), APIService.class);
        getActivity().startService(apiservice);

        map = (MapView) view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
        GeoPoint startPoint = new GeoPoint(41.390205, 2.154007);
        mapController.setCenter(startPoint);

        return view;
    }

    @Events.Subscribe("begin-api-call")
    private void BeginApiCall(){
        dialog.show();
    }

    @Events.Subscribe("end-api-call")
    private void EndApiCall(){
        dialog.hide();
    }

    @Events.Subscribe("return-api-call")
    private void ReturnApiCall(ArrayList<Station> stations){
        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(mContext);
        for (Station station : stations) {
            Marker startMarker = new Marker(map);
            startMarker.setPosition(new GeoPoint(station.getDoubleLatitude(), station.getDoubleLongitude()));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_logo_bicing));
            startMarker.setTitle("["+station.getStatus()+"] "+station.getStreetName());
            poiMarkers.add(startMarker);
        }
        map.getOverlays().add(poiMarkers);
        map.invalidate();
        // put markers on map & refresh
    }


}
