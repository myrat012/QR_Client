package com.example.new_qr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivityGuwaHat extends AppCompatActivity {
    private EditText masyn_nomer, masynyn_yly, faa, yasayan_yeri, masynyn_markasy, masynyn_renki, masynyn_matory, masynyn_agramy;
    private Button guwa_save;
    private Session session;
    private RequestQueue rQueue;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guwa_hat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // button
        guwa_save = findViewById(R.id.guwa_save);

        // edit text
        masyn_nomer = findViewById(R.id.masyn_nomer);
        masynyn_yly = findViewById(R.id.masynyn_yly);
        faa = findViewById(R.id.faa);
        yasayan_yeri = findViewById(R.id.yasayan_yeri);
        masynyn_markasy = findViewById(R.id.masynyn_markasy);
        masynyn_renki = findViewById(R.id.masynyn_renki);
        masynyn_matory = findViewById(R.id.masynyn_matory);
        masynyn_agramy = findViewById(R.id.masynyn_agramy);

        // if user exist
        session = new Session(this);
        if (!session.getGuwaHat().isEmpty()){
            guwa_save.setEnabled(false);
            getData();
            Log.d("SESSION", session.getGuwaHat());
            Log.d("SESSION", session.getSahadatnama());
        }

        guwa_save.setOnClickListener(view -> {
            setData();
        });
    }

    private void getData() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.getGuwaHatData(), response -> {
            rQueue.getCache().clear();
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = new JSONObject();

                for (int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    masyn_nomer.setText(jsonObject.getString("nomer"));
                    masynyn_yly.setText(jsonObject.getString("yyly"));
                    faa.setText(jsonObject.getString("faa"));
                    yasayan_yeri.setText(jsonObject.getString("address"));
                    masynyn_markasy.setText(jsonObject.getString("marka"));
                    masynyn_renki.setText(jsonObject.getString("renk"));
                    masynyn_matory.setText(jsonObject.getString("mator"));
                    masynyn_agramy.setText(jsonObject.getString("agram"));
                    session.createGuwaHat(jsonObject.getString("kiminki"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.d("VolleyError", error.toString())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("sahadatnama", session.getSahadatnama());

                return params;
            }
        };

        rQueue = Volley.newRequestQueue(MainActivityGuwaHat.this);
        rQueue.add(stringRequest);

    }

    private void setData() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.sendGuwaHatData(), response -> {
            rQueue.getCache().clear();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("saved")){
                    session.createGuwaHat(session.getSahadatnama());
                    Snackbar.make(guwa_save, "norm gitdi", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomePage.class);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(guwa_save, "bolmady(", Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.d("VolleyError", error.toString())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("nomer", masyn_nomer.getText().toString());
                params.put("yyly", masynyn_yly.getText().toString());
                params.put("faa", faa.getText().toString());
                params.put("address", yasayan_yeri.getText().toString());
                params.put("marka", masynyn_markasy.getText().toString());
                params.put("renk", masynyn_renki.getText().toString());
                params.put("mator", masynyn_matory.getText().toString());
                params.put("agram", masynyn_agramy.getText().toString());
                params.put("kiminki", session.getSahadatnama());

                return params;
            }
        };
        rQueue = Volley.newRequestQueue(MainActivityGuwaHat.this);
        rQueue.add(stringRequest);
    }
}