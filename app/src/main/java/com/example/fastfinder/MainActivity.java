package com.example.fastfinder;

import static java.lang.String.valueOf;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String ID = "com.example.fastfinder.MESSAGE2";
    public List<View> browseViews = new ArrayList<View>();
    public List<View> vegeViews = new ArrayList<View>();
    public List<View> dessViews = new ArrayList<View>();
    public List<View> savViews = new ArrayList<View>();
    public List<View> vegViews = new ArrayList<View>();
    public List<View> searchViews = new ArrayList<View>();

    public LinearLayout linearLayout;
    public LinearLayout linearLayout2;

    public String recentQuery = "";
    String api_key;

    public String email;

    BottomNavigationView nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(handleStart()) {
            setContentView(R.layout.activity_main);
            linearLayout = findViewById(R.id.explore);
            linearLayout2 = findViewById(R.id.explore2);
            nm = findViewById(R.id.bottom_navigation);
            api_key = getString(R.string.api_key);
            setTitle("FoodBrowser");
            Button btn = findViewById(R.id.explore_button);
            jsonParseExplore(btn);
        }

    }

    public void renderViews(List<View> views, LinearLayout l ){
        Log.d("renderca2", valueOf(views.size()));
        l.removeAllViews();
        for(int i = 0; i < views.size(); i++){
            View view  = views.get(i);
            l.addView(view);
        }
    }


    public void SearchByTopic(String tag, List<View> views){
        String url = "https://api.spoonacular.com/recipes/random?apiKey="+api_key+"&number=20"+tag;
        Log.d("lookforcall", views.toString());
        if(views.size()==0) {
            StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray recipes = jsonObject.getJSONArray("recipes");
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject recipe = recipes.getJSONObject(i);
                            String name = recipe.getString("title");
                            String image_url = recipe.getString("image");
                            int id = recipe.getInt("id");

                            LayoutInflater lf = getLayoutInflater();
                            View myLayout = lf.inflate(R.layout.layout, null);
                            TextView card = myLayout.findViewById(R.id.maintext);
                            ImageView main_img = myLayout.findViewById(R.id.cardimg);
                            TextView ids = myLayout.findViewById(R.id.ids);

                            ids.setText(valueOf(id));
                            Picasso.get().load(image_url).into(main_img);

                            card.setText(name);
                            views.add(i, myLayout);
                        }
                        renderViews(views, linearLayout);
                        Log.d("rendercal5", views.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }//
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("err", error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(sr);
        } else {
            renderViews(views, linearLayout);
            Log.d("rendercal4", valueOf(views.size()));
        }

    }
    public void getResults(View view){
        EditText sb = findViewById(R.id.searchbar);
        String query = sb.getText().toString().toLowerCase();

        if(!Objects.equals(recentQuery, query)) {
            recentQuery = query;
            String url = "https://api.spoonacular.com/recipes/complexSearch?apiKey="+api_key+"&query=" + query;
            StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray recipes = jsonObject.getJSONArray("results");
                        for (int i = 0; i < recipes.length(); i++) {
                            JSONObject recipe = recipes.getJSONObject(i);
                            String name = recipe.getString("title");
                            String image_url = recipe.getString("image");
                            int id = recipe.getInt("id");

                            LayoutInflater lf = getLayoutInflater();
                            View myLayout = lf.inflate(R.layout.layout, null);
                            TextView card = myLayout.findViewById(R.id.maintext);
                            ImageView main_img = myLayout.findViewById(R.id.cardimg);
                            TextView ids = myLayout.findViewById(R.id.ids);

                            ids.setText(valueOf(id));
                            Picasso.get().load(image_url).into(main_img);
                            Log.e("griffin5", name);
                            card.setText(name);
                            searchViews.add(i, myLayout);
                            renderViews(searchViews, linearLayout2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(sr);
        } else {
            renderViews(searchViews, linearLayout2);
        }
    }

    public void showInfo(View view){
        TextView tview = (TextView) view;
        Intent intent = new Intent(this, DisplayInfoActivity.class);
        String ids = tview.getText().toString();
        intent.putExtra(ID, ids);
        startActivity(intent);
    }
    public void changeTabs(int a, int s, boolean flag){

        LinearLayout ll = findViewById(a);
        LinearLayout sb = findViewById(s);
        animate_entry(ll,sb, flag);
    }
    public void goSearch(MenuItem view) {
        changeTabs(R.id.lin, R.id.lin2, false);
        view.setChecked(true);
    }
    public void goHome(MenuItem view){
        changeTabs(R.id.lin2, R.id.lin,  false);
        view.setChecked(true);
    }
    public void goSignOut(MenuItem view){
        changeTabs(R.id.lin2, R.id.lin, true);
        view.setChecked(true);
    }
    public void handleSignOut(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void animate_entry(LinearLayout ll, LinearLayout l1, boolean flag) {
        ConstraintLayout x = findViewById(R.id.signout_layout);
        int time = 700;
        if(!flag) {
            x.animate().alpha(0f).setDuration(time);
            x.setVisibility(View.INVISIBLE);
            ll.animate().alpha(0f).setDuration(time);
            ll.setVisibility(View.INVISIBLE);
            l1.setAlpha(0f);
            l1.setVisibility(View.VISIBLE);
            l1.animate().alpha(1f).setDuration(time);
        } else {
            ll.animate().alpha(0f).setDuration(time);
            ll.setVisibility(View.INVISIBLE);
            l1.animate().alpha(0f).setDuration(time);
            l1.setVisibility(View.INVISIBLE);
            x.setAlpha(0f);
            x.setVisibility(View.VISIBLE);
            x.animate().alpha(1f).setDuration(time);
        }
    }
    void setColor(View view, int color){
        if(view instanceof Button){
            ((Button)view).setTextColor(color);
        }
    }
    void changeButtonBackground(View q, int w, int color){
        q.setBackgroundResource(w);
        setColor(q, color);
    }
    void altChangeButtonBackground(int q, int w, int color){
        View q1 = findViewById(q);
        changeButtonBackground(q1, w, color);
    }
    void setButtonBackgroundsAndSearch(int q,int w, int e, int r, View view, String s, List<View> views){
        int white = ResourcesCompat.getColor(getResources(), R.color.white, null);
        int mainColor = ResourcesCompat.getColor(getResources(), R.color.gray_600, null);
        altChangeButtonBackground(q, R.drawable.round, mainColor);
        altChangeButtonBackground(w, R.drawable.round, mainColor);
        altChangeButtonBackground(e, R.drawable.round, mainColor);
        altChangeButtonBackground(r, R.drawable.round, mainColor);

        changeButtonBackground(view, R.drawable.round2, white);
        SearchByTopic(s, views);
    }
    public void jsonParseExplore(View view){
        setButtonBackgroundsAndSearch(R.id.veg_button, R.id.des_btn, R.id.ve_btn, R.id.sav_btn, view, "", browseViews);
    }
    public void jsonParseVegetarian(View view){
        setButtonBackgroundsAndSearch(R.id.explore_button, R.id.des_btn, R.id.ve_btn, R.id.sav_btn, view, "&tags=vegetarian", vegeViews);
    }
    public void jsonParseDessert(View view){
        setButtonBackgroundsAndSearch(R.id.explore_button, R.id.veg_button, R.id.ve_btn, R.id.sav_btn, view, "&tags=dessert", dessViews);
    }
    public void jsonParseSavory(View view){
        setButtonBackgroundsAndSearch(R.id.explore_button, R.id.veg_button, R.id.des_btn, R.id.ve_btn, view, "&tags=savory", savViews);
    }
    public void jsonParseVegan(View view){
        setButtonBackgroundsAndSearch(R.id.explore_button, R.id.veg_button, R.id.des_btn, R.id.sav_btn, view, "&tags=vegan", vegViews);
    }
    boolean handleStart()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            email = currentUser.getEmail();
            return true;
        } else {
            startActivity(new Intent(this, SignUpActivty.class));
        }
        return false;
    }
}



