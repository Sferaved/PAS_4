package com.taxi_pas_4.ui.weather;

import static com.taxi_pas_4.androidx.startup.MyApplication.sharedPreferencesHelperMain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxi_pas_4.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private final Context context;
    private final List<WeatherResponse.ForecastItem> forecastList;

    String localCode = sharedPreferencesHelperMain.getValue("locale", "uk").toString();
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.forLanguageTag(localCode));
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale(localCode));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", new Locale(localCode));

    public WeatherAdapter(Context context, List<WeatherResponse.ForecastItem> forecastList) {
        this.context = context;
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weather_forecast, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherResponse.ForecastItem forecast = forecastList.get(position);

        // Устанавливаем день недели и дату
        try {
            Date date = inputFormat.parse(forecast.getDtTxt());
            if (date != null) {
                holder.tvDay.setText(dayFormat.format(date));
                holder.tvDate.setText(dateFormat.format(date));
            }
        } catch (Exception e) {
            holder.tvDay.setText(forecast.getDtTxt());
        }

        // Температура
        if (forecast.getMain() != null) {
            int temp = (int) Math.round(forecast.getMain().getTemp());
            holder.tvTemp.setText(temp + "°C");
        }

        // Иконка погоды
        if (forecast.getWeather() != null && !forecast.getWeather().isEmpty()) {
            String iconCode = forecast.getWeather().get(0).getIcon();
            holder.ivWeatherIcon.setImageResource(getWeatherIcon(iconCode));
        }

        // Описание
        if (forecast.getWeather() != null && !forecast.getWeather().isEmpty()) {
            String description = forecast.getWeather().get(0).getDescription();
            holder.tvDescription.setText(capitalizeFirstLetter(description));
        }

        // Влажность
        if (forecast.getMain() != null) {
            holder.tvHumidity.setText(forecast.getMain().getHumidity() + "%");
        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDate, tvTemp, tvDescription, tvHumidity;
        ImageView ivWeatherIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvHumidity = itemView.findViewById(R.id.tv_humidity);
            ivWeatherIcon = itemView.findViewById(R.id.iv_weather_icon);
        }
    }

    private int getWeatherIcon(String iconCode) {
        switch (iconCode) {
            case "01d": case "01n": return R.drawable.ic_clear_sky;
            case "02d": case "02n": return R.drawable.ic_few_clouds;
            case "03d": case "03n": case "04d": case "04n": return R.drawable.ic_broken_clouds;
            case "09d": case "09n": return R.drawable.ic_shower_rain;
            case "10d": case "10n": return R.drawable.ic_rain;
            case "11d": case "11n": return R.drawable.ic_thunderstorm;
            case "13d": case "13n": return R.drawable.ic_snow;
            case "50d": case "50n": return R.drawable.ic_mist;
            default: return R.drawable.ic_weather_default;
        }
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}