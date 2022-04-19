package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    EditText cityInput;
    TextView cityName, nhietdo, hientrang, results;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String apiKey = "cc3a92d941f18252d44a4bc748a34a79";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        cityName = findViewById(R.id.cityName);
        nhietdo = findViewById(R.id.nhietdo);
        hientrang = findViewById(R.id.hientrang);
        results = findViewById(R.id.result);

        cityInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityInput.setText("");
            }
        });
    }

    public void getWeather(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityInput.getWindowToken(), 0);
        String tempUrl;
        String city = cityInput.getText().toString();
        if (city.equals("")) {
            cityName.setText("");
            nhietdo.setText("");
            hientrang.setText("");
            results.setText("Vui lòng nhập tên thành phố");
        } else {
            tempUrl = url +  "?q=" + city + "&appid=" + apiKey + "&lang=vi";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String op = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                        String cloud = jsonObjectCloud.getString("all");
                        JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                        String country = jsonObjectSys.getString("country");
                        String name = jsonObject.getString("name");

                        String cityNameOp = name + " (" + country + ")";
                        cityName.setText(cityNameOp);
                        String nhietdoOp = String.format("%.2f", temp) + "°C";
                        nhietdo.setText(nhietdoOp);
                        String hientrangOp = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
                        hientrang.setText(hientrangOp);

                        op += "Độ ẩm: " + humidity + "%\nTốc độ gió: "
                                + wind + " m/s\nMây: " + cloud + "%\nÁp suất: " + pressure + " hPa";
                        results.setText(op);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    results.setText("Thành phố không tồn tại");
                }
            });
            requestQueue.add(stringRequest);
        }
    }
}