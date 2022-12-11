package com.example.fastfinder;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
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
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String ID = "com.example.fastfinder.MESSAGE2";
    public static final String EMAIL = "com.example.fastfinder.MESSAGE1";
    public static final String SAVED = "com.example.fastfinder.MESSAGE3";
    public List<View> browseViews = new ArrayList<View>();
    public List<View> vegeViews = new ArrayList<View>();
    public List<View> dessViews = new ArrayList<View>();
    public List<View> savViews = new ArrayList<View>();
    public List<View> vegViews = new ArrayList<View>();
    public List<View> searchViews = new ArrayList<View>();
    public List<View> savedViews = new ArrayList<View>();

    public Set<Integer> idsOfSaved = new HashSet<Integer>();

    public LinearLayout linearLayout;
    public LinearLayout linearLayout2;
    public LinearLayout linearLayout3;
    public LinearLayout linearLayout4;

    public int recentId;

    public String recentQuery = "";
    String api_key;

    public String email;
    public boolean flag = false;
    BottomNavigationView nm;
    public FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(handleStart()) {
            setContentView(R.layout.activity_main);
            Button btn = findViewById(R.id.explore_button);
            linearLayout = findViewById(R.id.explore);
            linearLayout2 = findViewById(R.id.explore2);
            linearLayout3 = findViewById(R.id.explore_saved);
            linearLayout4 = findViewById(R.id.ingredients);
            nm = findViewById(R.id.bottom_navigation);
            api_key = getString(R.string.api_key);
            jsonParseExplore(btn);
            database = FirebaseDatabase.getInstance();
            setTitle("FasterFinder");
            setUpListener();
        }
    }
    public void setUpListener() {
        Query ingredients = database.getReference().child("ingredients");
        ingredients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("griffbbbb", "I just changed");
                    idsOfSaved.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.d("griffinb1", valueOf(data.getValue()));
                        IngredientModel im = convertFbToIM((Map<String, Object>) data.getValue());
                        if (im.getEmail().equals(email)) {
                            idsOfSaved.add(im.getId());
                        }
                    }
                    getSavedResults();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });
    }
    public void renderViews(List<View> views, LinearLayout l ){
        Log.d("renderca2", valueOf(views.size()));
        l.removeAllViews();
        for(int i = 0; i < views.size(); i++){
            View view  = views.get(i);
            l.addView(view);
        }
    }
    public void getSavedResults(){
        linearLayout3.removeAllViews();
        for (int s : idsOfSaved) {
            String url = "https://api.spoonacular.com/recipes/" + s + "/information?apiKey=" + api_key + "&includeNutrition=false";
            StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject recipe = new JSONObject(response);
                        String name = recipe.getString("title");
                        String image_url = recipe.getString("image");
                        int id = recipe.getInt("id");

                        LayoutInflater lf = getLayoutInflater();
                        View myLayout = lf.inflate(R.layout.saved_card, null);
                        TextView card = myLayout.findViewById(R.id.maintext2);
                        ImageView main_img = myLayout.findViewById(R.id.cardimg2);
                        TextView ids = myLayout.findViewById(R.id.ids3);
                        TextView ids2 = myLayout.findViewById(R.id.ids4);
                        ids.setText(valueOf(id));
                        ids2.setText(valueOf(id));
                        Picasso.get().load(image_url).into(main_img);
                        card.setText(name);
                        linearLayout3.addView(myLayout);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(sr);
        }
    }
    public void SearchByTopic(String tag, List<View> views){
        String url = "https://api.spoonacular.com/recipes/random?apiKey="+api_key+"&number=20"+tag;
//        Log.d("lookforcall", views.toString());
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
                            TextView ids2 = myLayout.findViewById(R.id.ids2);
                            ids.setText(valueOf(id));
                            ids2.setText(valueOf(id));
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
                            TextView ids2 = myLayout.findViewById(R.id.ids2);
                            ids.setText(valueOf(id));
                            ids2.setText(valueOf(id));
                            Picasso.get().load(image_url).into(main_img);
                            Log.e("griffin5", name);
                            card.setText(name);
                            searchViews.add(i, myLayout);
                        }
                        renderViews(searchViews, linearLayout2);
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
    public void handleSave(View view){
        TextView viewt = (TextView) view;
        String ids2 = viewt.getText().toString();
        TextView tv = findViewById(R.id.savebtnn2);
        TextView tv2 = findViewById(R.id.savebtnn);
        TextView tv4 = findViewById(R.id.savebtnn4);
        TextView tv3 = findViewById(R.id.savebtnn3);
        int id = Integer.parseInt(ids2);
        IngredientModel im = new IngredientModel(email, id);
        boolean tflag = false;
        for (int s : idsOfSaved){
            if(s==id){
                tflag = true;
            }
        }
        if(!tflag){
            database.getReference().child("ingredients").push().setValue(im);
            Toast.makeText(MainActivity.this, "Dish Saved!", Toast.LENGTH_SHORT).show();
            if(recentId==im.getId()){
                tv.setVisibility(View.GONE);
                tv2.setVisibility(View.GONE);
                tv3.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                tv4.setVisibility(View.GONE);
                tv3.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(MainActivity.this, "Dish Already Saved", Toast.LENGTH_SHORT).show();
        }
    }
    public IngredientModel convertFbToIM(Map<String, Object> map){
        String finalEmail = "";
        int finalID = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()){
            String name = entry.getKey();
            String value = entry.getValue().toString();
            if(name.equals("email")){
                 finalEmail = value;
            } else {
                finalID = (Integer.parseInt(value));
            }
        }
        IngredientModel im = new IngredientModel(finalEmail, finalID);
        return im;
    }
    public void handleUnSave(View view){
        TextView tv = findViewById(R.id.savebtnn2);
        TextView tv2 = findViewById(R.id.savebtnn);
        TextView tv4 = findViewById(R.id.savebtnn4);
        TextView tv3 = findViewById(R.id.savebtnn3);
        TextView viewt = (TextView) view;
        String ids2 = viewt.getText().toString();
        int id = Integer.parseInt(ids2);
        Query ingredients = database.getReference().child("ingredients");
        ingredients.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String temp_email ="";
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    IngredientModel im = convertFbToIM((Map<String, Object>) data.getValue());
                    if(im.getEmail().equals(email) && im.getId()==id){
                        data.getRef().removeValue();
                        if(recentId==im.getId()){
                            tv.setVisibility(View.VISIBLE);
                            tv2.setVisibility(View.VISIBLE);
                            tv4.setVisibility(View.GONE);
                            tv3.setVisibility(View.GONE);
                        } else {
                            tv.setVisibility(View.GONE);
                            tv2.setVisibility(View.GONE);
                            tv3.setVisibility(View.VISIBLE);
                            tv4.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });
    }
    public void showInfo(View view){
        TextView tview = (TextView) view;
        String ids = tview.getText().toString();
        recentId = Integer.parseInt(ids);
        goInfo(recentId);
    }
    public void changeTabs(int a, int s, int d, boolean flag, boolean flag2){
        LinearLayout ll = findViewById(a);
        LinearLayout sb = findViewById(s);
        LinearLayout db = findViewById(d);
        animate_entry(ll,sb, db, flag, flag2);
    }
    public void goSearch(MenuItem view) {
        changeTabs(R.id.lin, R.id.lin2, R.id.lin3, false, false);
        view.setChecked(true);
    }
    public void goHome(MenuItem view){
        changeTabs(R.id.lin2, R.id.lin, R.id.lin3, false, false);
        view.setChecked(true);
    }
    public void goSignOut(MenuItem view){
        changeTabs(R.id.lin2, R.id.lin, R.id.lin3,true, false);
        view.setChecked(true);
    }
    public void goSaved(MenuItem view){
        changeTabs(R.id.lin2, R.id.lin3, R.id.lin, false, false);
        view.setChecked(true);
    }
    public void goInfo(MenuItem view){
        goInfo(recentId);
        view.setChecked(true);
    }
    public void goInfo(int recentId){
        changeTabs(R.id.lin2, R.id.lin3, R.id.lin, false, true);
        TextView tv = findViewById(R.id.savebtnn2);
        TextView tv2 = findViewById(R.id.savebtnn);
        TextView tv4 = findViewById(R.id.savebtnn4);
        TextView tv3 = findViewById(R.id.savebtnn3);
        if(idsOfSaved.contains(recentId)){
            tv.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            tv4.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.GONE);
            tv4.setVisibility(View.GONE);
        }
        tv.setText(valueOf(recentId));
        tv4.setText(valueOf(recentId));
        getSpecific();
    }
    public void handleSignOut(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
    public void animate_entry(LinearLayout ll, LinearLayout l1, LinearLayout l2, boolean flag, boolean flag2) {
        ConstraintLayout x = findViewById(R.id.signout_layout);
        ConstraintLayout y = findViewById(R.id.display_info_cons);
        int time = 700;
        if(!flag && !flag2) {
            x.animate().alpha(0f).setDuration(time);
            x.setVisibility(View.INVISIBLE);
            ll.animate().alpha(0f).setDuration(time);
            ll.setVisibility(View.INVISIBLE);
            l2.animate().alpha(0f).setDuration(time);
            l2.setVisibility(View.INVISIBLE);
            y.animate().alpha(0f).setDuration(time);
            y.setVisibility(View.INVISIBLE);
            l1.setAlpha(0f);
            l1.setVisibility(View.VISIBLE);
            l1.animate().alpha(1f).setDuration(time);
        } else if(flag && !flag2) {
            ll.animate().alpha(0f).setDuration(time);
            ll.setVisibility(View.INVISIBLE);
            l1.animate().alpha(0f).setDuration(time);
            l1.setVisibility(View.INVISIBLE);
            l2.animate().alpha(0f).setDuration(time);
            l2.setVisibility(View.INVISIBLE);
            y.animate().alpha(0f).setDuration(time);
            y.setVisibility(View.INVISIBLE);
            x.setAlpha(0f);
            x.setVisibility(View.VISIBLE);
            x.animate().alpha(1f).setDuration(time);
        } else {
            ll.animate().alpha(0f).setDuration(time);
            ll.setVisibility(View.INVISIBLE);
            l1.animate().alpha(0f).setDuration(time);
            l1.setVisibility(View.INVISIBLE);
            l2.animate().alpha(0f).setDuration(time);
            l2.setVisibility(View.INVISIBLE);
            x.animate().alpha(0f).setDuration(time);
            x.setVisibility(View.INVISIBLE);
            y.setAlpha(0f);
            y.setVisibility(View.VISIBLE);
            y.animate().alpha(1f).setDuration(time);
        }
    }
    public void setColor(View view, int color){
        if(view instanceof Button){
            ((Button)view).setTextColor(color);
        }
    }
    public void changeButtonBackground(View q, int w, int color){
        q.setBackgroundResource(w);
        setColor(q, color);
    }
    public void altChangeButtonBackground(int q, int w, int color){
        View q1 = findViewById(q);
        changeButtonBackground(q1, w, color);
    }
    public void setButtonBackgroundsAndSearch(int q,int w, int e, int r, View view, String s, List<View> views){
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
    public boolean handleStart() {
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

    public void getSpecific() {
        linearLayout4.removeAllViews();
        String url = "https://api.spoonacular.com/recipes/" + Integer.toString(recentId) + "/information?apiKey=" + getString(R.string.api_key) + "&includeNutrition=false";
//        Log.d("AMIRUNNING?", "Title: " + "Title" + " URL: " + url);
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.d("AMIRUNNING?", "Title: " + "Title2" + " URL: " + url);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String image_url = jsonObject.getString("image");
                    String Title = jsonObject.getString("title");
                    Log.d("AMIRUNNING?", "Title: " + Title + " Id: " + valueOf(recentId));
                    TextView maintitle = findViewById(R.id.maintitle);
                    maintitle.setText(Title);
                    ImageView mai_img = findViewById(R.id.maiimg);
                    Picasso.get().load(image_url).into(mai_img);
                    JSONArray recipes = jsonObject.getJSONArray("extendedIngredients");
                    for (int i = 0; i < recipes.length(); i++) {
                        JSONObject recipe = recipes.getJSONObject(i);
                        String name = recipe.getString("nameClean");
                        Double amount = recipe.getDouble("amount");
                        String unit = recipe.getString("unit");
                        if (unit.equals("") || unit.equals("small") || unit.equals("large") || unit.equals("medium"))
                            unit = unit + " units";
                        String finals = String.format("%.2f " + unit + " of " + name, amount);
//                        String finals = Double.toString(amount) + " " + unit +" of " + name;
//                        Log.d("griffin4", finals);

                        LayoutInflater lf = getLayoutInflater();
                        View myLayout = lf.inflate(R.layout.ingreds, null);
                        TextView card = myLayout.findViewById(R.id.ingredtext);

                        card.setText(finals);
                        linearLayout4.addView(myLayout);
                    }
                } catch (JSONException e) {
                    Log.d("AMIRUNNING?", "Title: " + e.toString() + " Id: " + valueOf(recentId));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d("AMIRUNNING?", "Error: "+error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(sr);
    }
}



