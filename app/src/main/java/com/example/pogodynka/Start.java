package com.example.pogodynka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class Start extends AppCompatActivity {
    EditText city;
    TextView textView;

    LocationManager locationManager;
    LocationListener locationListener;

    String url ="";

    String error_message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        city = findViewById(R.id.cityEditText);
        textView = findViewById(R.id.textView);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
        }

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
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void getLocation(View view){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());
                url = "https://api.openweathermap.org/data/2.5/weather?lat="+ Latitude +"&lon=" + Longitude + "&appid=7f251d1a9dd64823fe3f78a3b0f94249&units=metric";
                showWeather(view);
                stopOnClick();
            }

            public void stopOnClick(){
                locationManager.removeUpdates(this);
            }

        };

        error_message="Couldn't get location";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void getCity(View view) {
        city = findViewById(R.id.cityEditText);
        textView = findViewById(R.id.textView);
        error_message="City name not recognised";
        if (city.getText().toString().equals("")) {
            city.setHint("Please enter City name!!");
            city.setBackgroundColor(Color.parseColor("#c77c71"));
        } else {
            url = "https://api.openweathermap.org/data/2.5/weather?q=" + city.getText().toString().trim() + "&appid=7f251d1a9dd64823fe3f78a3b0f94249&units=metric";
            showWeather(view);
        }
    }


    public void goBack(View view){
        setContentView(R.layout.activity_start);
        city = findViewById(R.id.cityEditText);
        textView = findViewById(R.id.textView);
    }


    public void showWeather(View view){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm");

                try {
                    jsonObject = new JSONObject(response);
                    String city = jsonObject.getString("name");
                    JSONArray array = jsonObject.getJSONArray("weather");
                    JSONObject weather = array.getJSONObject(0);
                    String description = weather.getString("description");
                    int id = weather.getInt("id");

                    JSONObject main  = jsonObject.getJSONObject("main");
                    double temp = Math.round(main.getDouble("temp")*10)/10;
                    double temp1 = Math.round(main.getDouble("feels_like")*10)/10;
                    double maxtemp = Math.round(main.getDouble("temp_max")*10)/10;
                    double mintemp = Math.round(main.getDouble("temp_min")*10)/10;

                    JSONObject wind  = jsonObject.getJSONObject("wind");
                    double speed = Math.round(wind.getDouble("speed")*10)/10;
                    double deg = wind.getDouble("deg");
                    String direction = Direction(deg);



                    JSONObject sys = jsonObject.getJSONObject("sys");
                    int sunrise = sys.getInt("sunrise") + jsonObject.getInt("timezone");
                    int sunset = sys.getInt("sunset") + jsonObject.getInt("timezone");
                    int time = jsonObject.getInt("dt") + jsonObject.getInt("timezone");


                    java.util.Date timeDate =new java.util.Date((long)time*1000);
                    String sTime = LocalDateTime.parse(timeDate.toString(), DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyy")).format(formatter);



                    wID = id;

                    setContentView(R.layout.activity_weather);

                    TextView tempView = findViewById(R.id.tempView);
                    tempView.setText(temp +"\u2103");
                    TextView descriptionView = findViewById(R.id.DescriptionView);
                    descriptionView.setText(description);
                    TextView cityView = findViewById(R.id.cityView);
                    cityView.setText(city);
                    ImageView imageView = findViewById(R.id.imageView4);
                    imageView.setImageResource(setIconRes(wID));

                    TextView dateView1 = findViewById(R.id.date2);
                    dateView1.setText(sTime);
                    TextView tempView1 = findViewById(R.id.temp);
                    tempView1.setText("feels like: "+temp1 +"\u2103");
                    TextView tempView2 = findViewById(R.id.max);
                    tempView2.setText("max: "+ maxtemp +"\u2103");
                    TextView tempView3 = findViewById(R.id.min);
                    tempView3.setText("min: "+ mintemp +"\u2103");
                    TextView windView = findViewById(R.id.wind);
                    windView.setText(speed+" m/s\n"+deg+"\u00B0 ("+direction+")");

                    if (jsonObject.has("rain")) {
                        JSONObject rain = jsonObject.getJSONObject("rain");
                        if (rain.has("1h")) {
                            double h1 = rain.getDouble("1h");
                            TextView rainView = findViewById(R.id.rainView);
                            rainView.setText(h1 + " mm");
                        }
                    }


                    if(time<sunrise || time>sunset){
                        ImageView background = findViewById(R.id.imageView);
                        background.setImageResource(R.drawable.night);
                        dateView1.setTextColor(Color.parseColor("#5802E3"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    textView.setText("Didn't Work");
                }


            }
        }, error -> {
            if (error.toString().trim().equals("com.android.volley.ClientError")){
                if (error_message=="City name not recognised") {
                    city.setText("");
                    city.setHint("Please enter City name!!");
                    city.setBackgroundColor(Color.parseColor("#c77c71"));
                }
                Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });



        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);




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
