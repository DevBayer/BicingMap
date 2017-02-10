package com.lluis.bayer.bicingmap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alexvasilkov.events.Events;
import com.lluis.bayer.bicingmap.models.Station;
import com.lluis.bayer.bicingmap.services.APIService;
import com.lluis.bayer.bicingmap.utils.Graphics;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

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
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
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
            if(station.getStatus().equals("OPN")) {
                startMarker.setIcon(writeOnDrawable(R.drawable.map_marker_icon2, station.getPercentage(), station.getBikes(), station.getId()));
            }else {
                Drawable dr = getResources().getDrawable(R.drawable.ic_pin_drop_48px);
                Bitmap bitmap = Graphics.getBitmap((VectorDrawable) dr);
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 96, 96, true));
                d.setColorFilter(new PorterDuffColorFilter(Color.YELLOW,PorterDuff.Mode.SRC_IN));
                startMarker.setIcon(d);
            }

            System.out.println("Station -> bikes: "+station.getBikes()+"/"+station.getSlots()+" percentage: "+station.getPercentage());

            startMarker.setSnippet("Status: "+station.getStatus()+" - Bikes: "+station.getBikes()+"/"+station.getSlots());
            startMarker.setTitle(station.getStreetName());

            poiMarkers.add(startMarker);
        }
        map.getOverlays().add(poiMarkers);
        map.invalidate();
        // put markers on map & refresh
    }


    public BitmapDrawable writeOnDrawable(int drawableId, int percentage, String text, String stationId){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setFakeBoldText(true);
        paint.setTextSize(55);
        if(text.equals("0")) {
            paint.setColor(Color.RED);
        }else{
            paint.setColor(Color.GREEN);
        }

        Canvas canvas = new Canvas(bm);

        Paint paint3 = new Paint();
        LinearGradient shader = new LinearGradient(0,0,0,bm.getHeight(), Color.BLUE, Color.BLUE, Shader.TileMode.CLAMP);
        System.out.println("percentage: "+percentage+" bikes: "+text);
        if((percentage <= 100 && percentage >= 76) || percentage >= 100 ){
            System.out.println("first if");
            shader = new LinearGradient(0,0,0,bm.getHeight(), Color.RED, Color.RED, Shader.TileMode.CLAMP);
        }else{
            if(percentage <= 75 && percentage >= 51 ) {
                System.out.println("2 if");
                shader = new LinearGradient(0, 25, 0, bm.getHeight(), Color.BLACK, Color.RED, Shader.TileMode.CLAMP);
            }else if(percentage <= 50 && percentage >= 26 ) {
                System.out.println("3 if");
                shader = new LinearGradient(0, 50, 0, bm.getHeight(), Color.BLACK, Color.RED, Shader.TileMode.CLAMP);
            }else if(percentage <= 25 && percentage >= 0 ) {
                System.out.println("4 if");
                shader = new LinearGradient(0, 0, 0, bm.getHeight(), Color.BLACK, Color.BLACK, Shader.TileMode.CLAMP);
            }
        }

        paint3.setShader(shader);
        paint3.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0,0,bm.getWidth(),bm.getHeight(),paint3);

        canvas.drawText(text, bm.getWidth()/2 - (text.length() * 15), 110, paint);


        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.FILL);
        paint2.setFakeBoldText(true);
        paint2.setTextSize(48);
        paint2.setColor(Color.BLACK);


        canvas.drawText("Estació "+stationId, 0, bm.getHeight(), paint2);


        return new BitmapDrawable(bm);
    }


}
