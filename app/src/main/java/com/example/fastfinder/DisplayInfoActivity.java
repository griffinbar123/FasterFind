package com.example.fastfinder;

import static com.example.fastfinder.MainActivity.ID;
import static com.example.fastfinder.MainActivity.TITLE;

import android.content.Intent;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayInfoActivity extends AppCompatActivity {
    public String Title;
//    public String Desc;
    public int Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        setTitle("Recipe Information");

        Intent intent = getIntent();
        Title = intent.getStringExtra(TITLE);
        String ids = intent.getStringExtra(ID);
//        Desc = intent.getStringExtra(DESC);
        Id = Integer.parseInt(ids);

        TextView maintitle = findViewById(R.id.maintitle);
        maintitle.setText(Title);

        getSpecific();
    }
    public void getSpecific(){
        LinearLayout linearLayout = findViewById(R.id.ingredients);
        linearLayout.removeAllViews();
        String url = "https://api.spoonacular.com/recipes/" + Integer.toString(Id) + "/information?apiKey=5a331ae9ccfd4285a5c351493fd87cc3&includeNutrition=false";

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e("griffin2", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String image_url = jsonObject.getString("image");
                    ImageView mai_img = findViewById(R.id.maiimg);
                    Picasso.get().load(image_url).into(mai_img);
                    JSONArray recipes = jsonObject.getJSONArray("extendedIngredients");
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        String name = recipe.getString("nameClean");
                        Double amount = recipe.getDouble("amount");
                        String unit = recipe.getString("unit");
                        if(unit.equals("") || unit.equals("small") || unit.equals("large") || unit.equals("medium") ) unit = unit+" units";
                        String finals = String.format("%.2f "+unit+" of "+name, amount);
//                        String finals = Double.toString(amount) + " " + unit +" of " + name;
                        Log.d("griffin4", finals);

                        LayoutInflater lf = getLayoutInflater();
                        View myLayout = lf.inflate(R.layout.ingreds, null);
                        TextView card = myLayout.findViewById(R.id.ingredtext);

                        card.setText(finals);
                        linearLayout.addView(myLayout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);

    }
}