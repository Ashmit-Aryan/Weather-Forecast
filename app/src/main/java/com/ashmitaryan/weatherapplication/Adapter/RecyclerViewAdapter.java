package com.ashmitaryan.weatherapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashmitaryan.weatherapplication.Model.Common;
import com.ashmitaryan.weatherapplication.Model.MyList;
import com.ashmitaryan.weatherapplication.Model.WeatherForecastResult;
import com.ashmitaryan.weatherapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mCtx;
    private List<MyList> list = new ArrayList<>();

    public RecyclerViewAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.rv_forcaste,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i("RecyclerViewAdapter", String.valueOf(list.get(position)));
        Picasso.get().load("https://openweathermap.org/img/wn/" + list.get(position).getWeatherList().get(0).getIcon() + "@2x.png")
                .into(holder.WeatherIcon);
        holder.RCTemp.setText(new StringBuilder(String.valueOf(list
                .get(position)
                .getMain()
                .getTemp()))
                .append(mCtx.getResources()
                        .getString(R.string.celcius)
                )
        );
        holder.RCDescription.setText(new StringBuilder(list
                .get(position)
                .getWeatherList()
                .get(0)
                .getMain()
                + " , "
                + list
                .get(position)
                .getWeatherList()
                .get(0)
                .getDescription())
        );
        holder.RCMin_Max.setText(new StringBuilder(mCtx.getResources().getString(R.string.temperature))
                .append(list
                        .get(position)
                        .getMain()
                        .getTemp_min()
                )
                .append(mCtx.getResources().getString(R.string.celcius))
                .append(list
                        .get(position)
                        .getMain()
                        .getTemp_max()
                )
                .append(mCtx.getResources().getString(R.string.celcius))
        );
        holder.RCDate.setText(new StringBuilder(Common.covertUnixToDate(list
                        .get(position)
                        .getDt()
                ))
        );
        holder.RCPressure.setText(new StringBuilder(mCtx.getResources().getString(R.string.pressure))
                .append(list
                        .get(position)
                        .getMain()
                        .getPressure()
                )
                .append(" hpa")
        );
        holder.RCHumidity.setText(new StringBuilder(mCtx.getResources().getString(R.string.humidity))
                .append(list
                        .get(position)
                        .getMain()
                        .getHumidity()
                )
                .append(" %")
        );
        holder.RCWindSpeed.setText(new StringBuilder(mCtx.getResources().getString(R.string.wind_speed))
                .append(list
                        .get(position)
                        .getWind()
                        .getSpeed()
                )
                .append("in")
                .append(list
                        .get(position)
                        .getWind()
                        .getDeg()
                )
        );
    }

    public void setList(List<MyList> myLists){
        this.list = myLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView WeatherIcon;
        TextView RCDate, RCTemp, RCDescription, RCMin_Max , RCPressure, RCHumidity, RCWindSpeed;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            WeatherIcon = itemView.findViewById(R.id.RCivWeatherIcon);
            RCDate = itemView.findViewById(R.id.tvDate);
            RCDescription = itemView.findViewById(R.id.RCtvDescription);
            RCTemp = itemView.findViewById(R.id.RCtvTemperature);
            RCMin_Max = itemView.findViewById(R.id.RCtvTemp_max_Temp_min);
            RCPressure = itemView.findViewById(R.id.RCtvPressure);
            RCHumidity = itemView.findViewById(R.id.RCtvHumidity);
            RCWindSpeed = itemView.findViewById(R.id.RCtvWindSpeed);
        }
    }
}
