package com.example.fastfinder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("FasterFind");

        jsonParse();
    }


    public void jsonParse() {

        RequestQueue mQueue;

        LinearLayout linearLayout = findViewById(R.id.explore);

        mQueue = Volley.newRequestQueue(this);

        String url = "https://api.foursquare.com/v3/places/search?limit=50";

        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                JSONArray jsonArray = response.getJSONArray("results");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject place = jsonArray.getJSONObject(i);

                                    String name = place.getString("name");
                                    JSONArray categories = place.getJSONArray("categories");
                                    JSONObject category = categories.getJSONObject(0);
                                    JSONObject icon = category.getJSONObject("icon");

                                    String img = icon.getString("prefix")+ icon.getString("suffix");

                                    LayoutInflater lf = getLayoutInflater();
                                    View myLayout = lf.inflate(R.layout.layout, null);
                                    TextView card = myLayout.findViewById(R.id.maintext);
                                    card.setText(name);

//                                    ImageView cardimg = myLayout.findViewById(R.id.cardimg);
//                                    cardimg.setImageIcon(img);
//                                    TextView othertxt = myLayout.findViewById(R.id.desctxt);
//                                    othertxt.setText(distance);

                                    ImageView cardimg = myLayout.findViewById(R.id.cardimg);
                                    ImageLoader imageLoader = new ImageLoader(this);
                                    imageLoader.DisplayImage(img, cardimg);

                                    linearLayout.addView(myLayout);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, Throwable::printStackTrace) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> customHeaders = new HashMap<>();

                        customHeaders.put("Accept", "application/json");
                        customHeaders.put("Authorization", "fsq35BlCYuH6Vsyan/JnNdJYxqtpXyJX06MekXcoYhb85qA=");

                        return customHeaders;
                    }
                };
        Log.d("findlopl", "text");
        mQueue.add(request);
    }
}