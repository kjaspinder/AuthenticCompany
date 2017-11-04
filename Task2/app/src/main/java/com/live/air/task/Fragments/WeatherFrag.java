package com.live.air.task.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.live.air.task.R;

/**
 * Created by JASPINDER on 11/4/2017.
 */

public class WeatherFrag extends Fragment {


    public static WeatherFrag newInstance(double precip, double wind,String timeZone,String icon,double temp,String dailysumm,String summary,double maxtemp,double mintemp,double humidity) {
        WeatherFrag f = new WeatherFrag();
        Bundle args = new Bundle();
        args.putDouble("precip", precip);
        args.putDouble("humidity", humidity);
        args.putDouble("wind", wind);
        args.putDouble("temp", temp);
        args.putDouble("maxtemp", maxtemp);
        args.putDouble("mintemp", mintemp);
        args.putString("timeZone", timeZone);
        args.putString("icon", icon);
        args.putString("dailysumm", dailysumm);
        args.putString("summary", summary);
        f.setArguments(args);
        return f;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.f_weather_details, container, false);
        TextView place = (TextView) v.findViewById(R.id.place);
        ImageView weather_icon = (ImageView) v.findViewById(R.id.weather_icon);
        TextView temperaturetext = (TextView) v.findViewById(R.id.temperature);
        TextView weather_condition = (TextView) v.findViewById(R.id.weather_condition);
        TextView humiditytext = (TextView) v.findViewById(R.id.humidity);
        TextView precipitation = (TextView) v.findViewById(R.id.precipitation);
        TextView windtext = (TextView) v.findViewById(R.id.wind);
        TextView maxmintemp = (TextView) v.findViewById(R.id.maxmintemp);
        TextView note = (TextView) v.findViewById(R.id.note);

        if (getArguments() != null) {
            Double precip = getArguments().getDouble("precip");
            Double humidity = getArguments().getDouble("humidity");
            Double wind = getArguments().getDouble("wind");
            Double temperature = getArguments().getDouble("temp");
            Double maxtemp = getArguments().getDouble("maxtemp");
            Double mintemp = getArguments().getDouble("mintemp");
            String  timeZone = getArguments().getString("timeZone");
            String  icon = getArguments().getString("icon");
            String  dailysumm = getArguments().getString("dailysumm");
            String  summary = getArguments().getString("summary");
            place.setText(timeZone);
            temperaturetext.setText(temperature+"");
            weather_condition.setText(summary);
            humiditytext.setText(humiditytext.getText()+" "+String.format("%.2f", (humidity*100))+"%");
            precipitation.setText(precipitation.getText()+" "+String.format("%.2f", (precip*100))+"%");
            windtext.setText(windtext.getText()+" "+(String.format("%.2f", wind))+" km/h");
            maxmintemp.setText(maxmintemp.getText()+" "+(String.format("%.2f", maxtemp))+"  / "+ (String.format("%.2f", mintemp)));
            String weather_icon_string = icon.replaceAll("-", "");
            int id = getActivity().getResources().getIdentifier(weather_icon_string, "drawable", getActivity().getPackageName());
            weather_icon.setImageResource(id);
            note.setText(dailysumm);
        }





        return v;
    }
}
