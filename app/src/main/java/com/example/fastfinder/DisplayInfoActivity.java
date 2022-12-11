package com.example.fastfinder;

import static com.example.fastfinder.MainActivity.EMAIL;
import static com.example.fastfinder.MainActivity.ID;
import static com.example.fastfinder.MainActivity.SAVED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayInfoActivity extends AppCompatActivity {
    public String email;
    public int Id;
    public FirebaseDatabase database;
    public String saved;
    public Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_info);
        setTitle("Recipe Information");

        Intent intent = getIntent();

        String ids = intent.getStringExtra(ID);
        email = intent.getStringExtra(EMAIL);
        saved = intent.getStringExtra(SAVED);
        Id = Integer.parseInt(ids);

        database = FirebaseDatabase.getInstance();
        TextView btn = findViewById(R.id.savebtnn);
        if(saved.equals("true")){
            btn.setVisibility(View.GONE);
        }
        getSpecific();
    }

    public void handleSave2(View view) {
        btn.setVisibility(View.GONE);
        IngredientModel im = new IngredientModel(email, Id);
        database.getReference().child("ingredients").push().setValue(im);
        Toast.makeText(this, "Dish Saved!", Toast.LENGTH_SHORT).show();
    }

    public void getSpecific(){
        LinearLayout linearLayout = findViewById(R.id.ingredients);
        linearLayout.removeAllViews();
        String url = "https://api.spoonacular.com/recipes/" + Integer.toString(Id) + "/information?apiKey="+getString(R.string.api_key)+"&includeNutrition=false";

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                Log.e("griffin2", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String image_url = jsonObject.getString("image");
                    String Title = jsonObject.getString("title");
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