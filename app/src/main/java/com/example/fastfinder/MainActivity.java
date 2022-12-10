package com.example.fastfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static final String ID = "com.example.fastfinder.MESSAGE2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("FoodBrowser");
        Button btn = findViewById(R.id.explore_button);
        jsonParseExplore(btn);
    }

    public void jsonParseExplore(View view){
        Button veg_btn = findViewById(R.id.veg_button);
        veg_btn.setBackgroundResource(R.drawable.round);
        Button des_btn = findViewById(R.id.des_btn);
        des_btn.setBackgroundResource(R.drawable.round);
        Button ve_btn =findViewById(R.id.ve_btn);
        ve_btn.setBackgroundResource(R.drawable.round);
        Button sav_btn =findViewById(R.id.sav_btn);
        sav_btn.setBackgroundResource(R.drawable.round);

        view.setBackgroundResource(R.drawable.round2);
        LinearLayout linearLayout = findViewById(R.id.explore);
        SearchByTopic(linearLayout, "");//
    }

    public void jsonParseVegetarian(View view){
        Button btn = findViewById(R.id.explore_button);
        btn.setBackgroundResource(R.drawable.round);
        Button des_btn = findViewById(R.id.des_btn);
        des_btn.setBackgroundResource(R.drawable.round);
        Button ve_btn =findViewById(R.id.ve_btn);
        ve_btn.setBackgroundResource(R.drawable.round);
        Button sav_btn =findViewById(R.id.sav_btn);
        sav_btn.setBackgroundResource(R.drawable.round);

        view.setBackgroundResource(R.drawable.round2);
        LinearLayout linearLayout = findViewById(R.id.explore);
        SearchByTopic(linearLayout, "&tags=vegetarian");
    }

    public void jsonParseDessert(View view){
        Button btn = findViewById(R.id.explore_button);
        btn.setBackgroundResource(R.drawable.round);
        Button veg_btn = findViewById(R.id.veg_button);
        veg_btn.setBackgroundResource(R.drawable.round);
        Button ve_btn =findViewById(R.id.ve_btn);
        ve_btn.setBackgroundResource(R.drawable.round);
        Button sav_btn =findViewById(R.id.sav_btn);
        sav_btn.setBackgroundResource(R.drawable.round);

        view.setBackgroundResource(R.drawable.round2);
        LinearLayout linearLayout = findViewById(R.id.explore);
        SearchByTopic(linearLayout, "&tags=dessert");
    }
    public void jsonParseSavory(View view){
        Button btn = findViewById(R.id.explore_button);
        btn.setBackgroundResource(R.drawable.round);
        Button veg_btn = findViewById(R.id.veg_button);
        veg_btn.setBackgroundResource(R.drawable.round);
        Button des_btn = findViewById(R.id.des_btn);
        des_btn.setBackgroundResource(R.drawable.round);
        Button ve_btn =findViewById(R.id.ve_btn);
        ve_btn.setBackgroundResource(R.drawable.round);

        view.setBackgroundResource(R.drawable.round2);
        LinearLayout linearLayout = findViewById(R.id.explore);
        SearchByTopic(linearLayout, "&tags=savory");
    }
    public void jsonParseVegan(View view){
        Button btn = findViewById(R.id.explore_button);
        btn.setBackgroundResource(R.drawable.round);
        Button veg_btn = findViewById(R.id.veg_button);
        veg_btn.setBackgroundResource(R.drawable.round);
        Button des_btn = findViewById(R.id.des_btn);
        des_btn.setBackgroundResource(R.drawable.round);
        Button sav_btn =findViewById(R.id.sav_btn);
        sav_btn.setBackgroundResource(R.drawable.round);

        view.setBackgroundResource(R.drawable.round2);
        LinearLayout linearLayout = findViewById(R.id.explore);
        SearchByTopic(linearLayout, "&tags=vegan");
    }


    public void SearchByTopic(LinearLayout linearLayout,String tag){
        linearLayout.removeAllViews();
        String url = "https://api.spoonacular.com/recipes/random?apiKey=6fa8c58e0fd84b3082ba8464c23037df&number=20"+tag;

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e("griffin2", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray recipes = jsonObject.getJSONArray("recipes");
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        String name = recipe.getString("title");
                        String image_url = recipe.getString("image");
//                        String summary = recipe.getString("summary");
                        int id = recipe.getInt("id");

                        LayoutInflater lf = getLayoutInflater();
                        View myLayout = lf.inflate(R.layout.layout, null);
                        TextView card = myLayout.findViewById(R.id.maintext);
                        ImageView main_img = myLayout.findViewById(R.id.cardimg);
                        TextView ids = myLayout.findViewById(R.id.ids);
//                        TextView desc = myLayout.findViewById(R.id.desc);
                        ids.setText(String.valueOf(id));
//                        desc.setText(summary);
                        Picasso.get().load(image_url).into(main_img);

                        card.setText(name);
                        linearLayout.addView(myLayout);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);
    }

    public void showInfo(View view){
        TextView tview = (TextView) view;
        Intent intent = new Intent(this, DisplayInfoActivity.class);
//        TextView desc = findViewById(R.id.desc);
//        String summary = desc.getText().toString();
        String ids = tview.getText().toString();
//        intent.putExtra(DESC, summary);
        intent.putExtra(ID, ids);
        startActivity(intent);
    }

    public void goSearch(MenuItem view) {
        LinearLayout ll = findViewById(R.id.lin);
        LinearLayout sb = findViewById(R.id.lin2);
        animate_entry(ll,sb);

        BottomNavigationView nm = findViewById(R.id.bottom_navigation);
        MenuItem item = nm.getMenu().findItem(R.id.search);
        item.setChecked(true);

    }
    public void goHome(MenuItem view){
        LinearLayout sb = findViewById(R.id.lin2);
        LinearLayout ll = findViewById(R.id.lin);
        animate_entry(sb, ll);

        BottomNavigationView nm = findViewById(R.id.bottom_navigation);
        MenuItem item = nm.getMenu().findItem(R.id.home);
        item.setChecked(true);
    }
    public void animate_entry(LinearLayout ll, LinearLayout l1) {
        ll.animate().alpha(0f).setDuration(1500);
        ll.setVisibility(View.INVISIBLE);
        l1.setAlpha(0f);
        l1.setVisibility(View.VISIBLE);
        l1.animate().alpha(1f).setDuration(1500);
    }
    public void getResults(View view){
        LinearLayout linearLayout = findViewById(R.id.explore2);
        LinearLayout linearLayout2 = findViewById(R.id.explore);
        linearLayout.removeAllViews();
        linearLayout2.removeAllViews();
        EditText sb = findViewById(R.id.searchbar);
        String query = sb.getText().toString().toLowerCase();

        String url ="https://api.spoonacular.com/recipes/complexSearch?apiKey=6fa8c58e0fd84b3082ba8464c23037df&query="+query;
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray recipes = jsonObject.getJSONArray("results");
                    Log.e("griffin6", "hi");
                    for (int i = 0; i < recipes.length(); i++) {
//
                        JSONObject recipe = recipes.getJSONObject(i);
                        String name = recipe.getString("title");
                        String image_url = recipe.getString("image");
//
                        int id = recipe.getInt("id");

                        LayoutInflater lf = getLayoutInflater();
                        View myLayout = lf.inflate(R.layout.layout, null);
                        TextView card = myLayout.findViewById(R.id.maintext);
                        ImageView main_img = myLayout.findViewById(R.id.cardimg);
                        TextView ids = myLayout.findViewById(R.id.ids);
//
                        ids.setText(String.valueOf(id));
//                        desc.setText(summary);
                        Picasso.get().load(image_url).into(main_img);
                        Log.e("griffin5", name);
                        card.setText(name);
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



