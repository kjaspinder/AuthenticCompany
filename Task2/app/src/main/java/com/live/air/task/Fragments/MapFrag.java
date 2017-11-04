package com.live.air.task.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.live.air.task.R;
import com.live.air.task.Utils.FullScreenTransparentLoading;
import com.live.air.task.Utils.SharePref;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by JASPINDER on 11/4/2017.
 */

public class MapFrag extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private double lat, lng;
    private GestureDetector gestureDetector;
    TextView temperature;
    Button moreDetails;
    RelativeLayout weather;
    static String sUrlForecastIO = "https://api.darksky.net/forecast/99d1a26ed69b7f812943da40f304f9a8/";
    static double mCurrentTemp = -100;
    static String micon = "";
    static double mCurrentHumidity = 0;
    static double mCurrentPrecip = 0;
    static double mCurrentWind= 0;


    static String timeZone = "";
    static String dailysummary = "";
    static double maxtemp = 0;
    static double mintemp = 0;
    static String mSummary = "";


    public static MapFrag newInstance(double lat, double lng) {
        MapFrag f = new MapFrag();
        Bundle args = new Bundle();
        args.putDouble("LAT", lat);
        args.putDouble("LNG", lng);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_map, container, false);
        moreDetails = (Button) v.findViewById(R.id.moreDetails);
        temperature = (TextView) v.findViewById(R.id.temperature);
        weather = (RelativeLayout) v.findViewById(R.id.weather);
        if (getArguments() != null) {
            lat = getArguments().getDouble("LAT");
            lng = getArguments().getDouble("LNG");
        }
        getMapFragment().getMapAsync(this);

        moreDetails.setOnClickListener(this);
        return v;
    }


    private MapFragment getMapFragment() {
        FragmentManager fm = null;


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fm = getFragmentManager();
        } else {
            fm = getChildFragmentManager();
        }

        return (MapFragment) fm.findFragmentById(R.id.map);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        weather.setVisibility(View.GONE);
        final LatLng latlong = new LatLng(lat, lng);
        final MarkerOptions marker = new MarkerOptions().position(latlong)
                .title("Current Location");
        googleMap.addMarker(marker);
        weather.setVisibility(View.VISIBLE);
        getWeather(latlong);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 7));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                weather.setVisibility(View.GONE);
                MoveMarker(googleMap, latLng);
            }
        });


    }

    public void MoveMarker(GoogleMap map, LatLng latLng) {
        map.clear();
        final MarkerOptions marker = new MarkerOptions().position(latLng)
                .title("Current Location");
        map.addMarker(marker);
        if(SharePref.isNetworkAvailable(getActivity())) {
            FullScreenTransparentLoading.INSTANCE.launch();
            getWeather(latLng);
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
        }
    }

    public void getWeather(final LatLng latLng) {
        if (lat != 0 && lng != 0) {
            AsyncTask<Void, Integer, JSONObject> downloader = new AsyncTask<Void, Integer, JSONObject>() {
                @Override
                protected void onPostExecute(JSONObject response) {
                    FullScreenTransparentLoading.INSTANCE.dismiss();
                    JSONObject current;
                    try {
                        if (response != null) {
                            current = response.getJSONObject("currently");
                            weather.setVisibility(View.VISIBLE);

                            mCurrentTemp = current.getDouble("temperature");
                            temperature.setText(mCurrentTemp+" "+"F");
                            micon = current.getString("icon");
                            mCurrentHumidity = current.getDouble("humidity");
                            mCurrentPrecip = current.getDouble("precipIntensity");
                            mCurrentWind = current.getDouble("windSpeed");
                            mSummary = current.getString("summary");

                            JSONArray dailyArray = response.getJSONObject("daily").getJSONArray("data");
                            dailysummary = response.getJSONObject("daily").getString("summary");
                            JSONObject dailyclimate = new JSONObject(dailyArray.get(0).toString());
                            maxtemp = dailyclimate.getDouble("temperatureMax");
                            mintemp = dailyclimate.getDouble("temperatureMin");

                            timeZone = response.getString("timezone");


                        } else {
                            Toast.makeText(getActivity(),"Featching weather failed. Please try again.",Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        Log.e("CCU_WEATHER", "Exception: " + e.getMessage());
                    } catch (NullPointerException e) {
                        Log.e("CCU_WEATHER", "Exception: " + e.getMessage());
                    }
                }

                @Override
                protected JSONObject doInBackground(Void... params) {

                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(sUrlForecastIO + String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude));
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String responseBody;
                    try {
                        responseBody = httpclient.execute(httpget, responseHandler);
                        JSONObject response = new JSONObject(responseBody);
                        return response;
                    } catch (ClientProtocolException e) {
                        Log.e("CCU_WEATHER", "Exception: " + e.getMessage());
                    } catch (IOException e) {
                         Log.e("CCU_WEATHER", "Exception: " + e.getMessage());
                    } catch (JSONException e) {
                         Log.e("CCU_WEATHER", "Exception: " + e.getMessage());
                    } finally {
                        httpclient.getConnectionManager().closeExpiredConnections();
                    }
                    return null;
                }
            };
            downloader.execute();
        }
    }


    @Override
    public void onClick(View v) {
        WeatherFrag frag = WeatherFrag.newInstance(mCurrentPrecip,mCurrentWind,timeZone,micon,mCurrentTemp,dailysummary,mSummary,maxtemp,mintemp,mCurrentHumidity);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.mapframe, frag);
        transaction.addToBackStack(WeatherFrag.class.getName());
        transaction.commitAllowingStateLoss();
    }
}
