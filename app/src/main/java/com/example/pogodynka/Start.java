package com.example.pogodynka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Start extends AppCompatActivity {
    EditText city;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        city = findViewById(R.id.cityEditText);
        textView = findViewById(R.id.textView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
    }

    public void showWeather(View view){

        String url ="";
        String url2 ="";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
        }
        else{

            if (city.getText().toString().equals("")){
                city.setHint("Please enter City name!!");
                city.setBackgroundColor(Color.parseColor("#c77c71"));
            }else {
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city.getText().toString().trim() + "&appid=7f251d1a9dd64823fe3f78a3b0f94249&units=metric";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        String output="";
                        JSONObject jsonObject = null;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");

                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("weather");
                            JSONObject weather = array.getJSONObject(0);
                            String description = weather.getString("description");
                            int id = weather.getInt("id");

                            JSONObject main  = jsonObject.getJSONObject("main");
                            double temp = Math.round(main.getDouble("temp")*10)/10;
                            double temp1 = Math.round(main.getDouble("feels_like")*10)/10;

                            JSONObject wind  = jsonObject.getJSONObject("wind");
                            double speed = Math.round(wind.getDouble("speed")*10)/10;
                            double deg = wind.getDouble("deg");
                            String direction = Direction(deg);

                            try {
                                JSONObject rain  = jsonObject.getJSONObject("rain");
                                double h1 = rain.getDouble("1h");
                                TextView rainView = findViewById(R.id.rain);
                                rainView.setText(h1+" mm");
                            }catch (JSONException e) {
                                TextView rainView = findViewById(R.id.rain);
                                rainView.setText("no data");
                            }


                            JSONObject sys = jsonObject.getJSONObject("sys");
                            int sunrise = sys.getInt("sunrise") + jsonObject.getInt("timezone");
                            int sunset = sys.getInt("sunset") + jsonObject.getInt("timezone");
                            int time  = jsonObject.getInt("dt") + jsonObject.getInt("timezone");

                            String sTime1 = LocalDateTime.parse(Integer.toString(time), DateTimeFormatter.BASIC_ISO_DATE).format(formatter);


                            wID = id;

                            setContentView(R.layout.activity_weather);

                            if(time<sunrise || time>sunset){
                                ImageView background = findViewById(R.id.imageView);
                                background.setImageResource(R.drawable.night);
                            }

                            TextView tempView = findViewById(R.id.tempView);
                            tempView.setText(temp +"\u2103");
                            TextView descriptionView = findViewById(R.id.DescriptionView);
                            descriptionView.setText(description);
                            TextView cityView = findViewById(R.id.cityView);
                            cityView.setText(city.getText().toString());
                            ImageView imageView = findViewById(R.id.imageView4);
                            imageView.setImageResource(setIconRes(wID));

                            TextView dateView1 = findViewById(R.id.date2);
                            dateView1.setText(sTime1);
                            TextView tempView1 = findViewById(R.id.temp);
                            tempView1.setText("feels like: "+temp1 +"\u2103");
                            TextView windView = findViewById(R.id.wind);
                            windView.setText(speed+" m/s\ndegrees: "+deg+" ("+direction+")");


                        } catch (JSONException e) {
                            e.printStackTrace();
                            textView.setText("Didn't Work");
                        }


                    }
                }, error -> Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show());



                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);


            }
        }
    }

    private int wID;


    public String  Direction(double degree){
        if (degree>337.5) return "N";
        if (degree>292.5) return "NW";
        if(degree>247.5) return "W";
        if(degree>202.5) return "SW";
        if(degree>157.5) return "S";
        if(degree>122.5) return "SE";
        if(degree>67.5) return "E";
        if(degree>22.5){return "NE";}
        return "N";
    }

    public int setIconRes(int wID) {
        int iconRes;

        if (wID == 800) {
            iconRes = R.drawable.ic_sunny;
        } else if (wID >= 200 && wID < 300) {
            if (wID >= 210 && wID < 230) {
                iconRes = R.drawable.ic_thunder;
            } else {
                iconRes = R.drawable.ic_rainythunder;
            }
        } else if (wID >= 300 && wID < 600) {
            if (wID == 511) {
                iconRes = R.drawable.ic_snowyrainy;
            } else if (wID >= 500 && wID < 511) {
                iconRes = R.drawable.ic_rainy;
            } else {
                iconRes = R.drawable.ic_rainshower;
            }
        } else if (wID>=600 && wID<700) {
            if (wID>=611 && wID<620){
                iconRes = R.drawable.ic_snowyrainy;
            } else if (wID==600 || wID==622) {
                iconRes = R.drawable.ic_heavysnow;
            }else {
                iconRes = R.drawable.ic_snowy;
            }
        } else if (wID>700 && wID<800) {
            iconRes = R.drawable.ic_mist;
        } else if (wID==802) {
            iconRes = R.drawable.ic_cloudy;
        } else if (wID>802) {
            iconRes = R.drawable.ic_very_cloudy;
        } else {
            iconRes = R.drawable.ic_sunnycloudy;
        }
        return iconRes;
    }



}
