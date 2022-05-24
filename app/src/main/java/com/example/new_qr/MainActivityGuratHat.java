package com.example.new_qr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivityGuratHat extends AppCompatActivity {
    private EditText pygg_birligi, kysymy, san_belgisi, ondurulen_yyly;
    private DatePicker bellige_alynan_sene;
    private Button gurat_save;
    private Session session;
    private RequestQueue rQueue;
    private String bellige_alnan, belligi_gutaran;

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
        setContentView(R.layout.activity_main_gurat_hat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pygg_birligi = findViewById(R.id.pygg_birligi);
        kysymy = findViewById(R.id.kysymy);
        san_belgisi = findViewById(R.id.san_belgisi);
        ondurulen_yyly = findViewById(R.id.ondurulen_yyly);

        bellige_alynan_sene = findViewById(R.id.bellige_alynan_sene);

        gurat_save = findViewById(R.id.gurat_save);

        // if user exist
        session = new Session(this);
        if (!session.getGuratHat().isEmpty()){
            getdata();
            gurat_save.setEnabled(false);
            Log.d("SESSION", session.getGuratHat());
        }

        gurat_save.setOnClickListener(view -> {
            int day = bellige_alynan_sene.getDayOfMonth();
            int month = bellige_alynan_sene.getMonth() + 1;
            int year = bellige_alynan_sene.getYear();
            bellige_alnan = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
            belligi_gutaran = String.valueOf(year + 2) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
            Log.d("cal", belligi_gutaran);
            setdata();
        });


    }

    private void setdata() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.sendGuratHatData(), response -> {
            rQueue.getCache().clear();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("saved")){
                    session.creatGuratHat(session.getSahadatnama());
                    Snackbar.make(gurat_save, "norm gitdi", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomePage.class);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(gurat_save, "bolmady(", Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.d("VolleyError", error.toString())){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("pyyyg_birligi", pygg_birligi.getText().toString());
                params.put("kysymy", kysymy.getText().toString());
                params.put("nomeri", san_belgisi.getText().toString());
                params.put("ondurulen_yyly", ondurulen_yyly.getText().toString());
                params.put("bellige_alnan_sene", bellige_alnan);
                params.put("gutaryan_yyly", belligi_gutaran);
                params.put("sahadatnama", session.getSahadatnama());

                return params;
            }
        };
        rQueue = Volley.newRequestQueue(MainActivityGuratHat.this);
        rQueue.add(stringRequest);
    }

    private void getdata() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.getGuratHatData(), response -> {
            rQueue.getCache().clear();
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = new JSONObject();

                for (int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    pygg_birligi.setText(jsonObject.getString("pyyyg_birligi"));
                    kysymy.setText(jsonObject.getString("kysymy"));
                    san_belgisi.setText(jsonObject.getString("nomeri"));
                    ondurulen_yyly.setText(jsonObject.getString("ondurulen_yyly"));
                    String[] time = jsonObject.getString("bellige_alnan_sene").split("-");
                    bellige_alynan_sene.updateDate(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
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

        rQueue = Volley.newRequestQueue(MainActivityGuratHat.this);
        rQueue.add(stringRequest);
    }
}