package com.example.new_qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_INT = 1;

    private RequestQueue rQueue;
    private EditText reg_input_fam, reg_input_ady, reg_input_atasynyn_ady, reg_input_berlenY, reg_input_doglanY;
    private Button reg_save, surat_sayla;
    private ImageView profile;
    private Bitmap bitmap;
    private DatePicker reg_input_doglanG, reg_input_berlenS;
    private String doglan_gun, berlen_gun, mohleti;
    private Session session;
    private ImageLoadTask imageLoadTask;

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
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // image
        profile = findViewById(R.id.profile);

        // button
        reg_save = findViewById(R.id.reg_save);
        surat_sayla = findViewById(R.id.surat_sayla);

        // edit text
        reg_input_fam = findViewById(R.id.reg_input_fam);
        reg_input_ady = findViewById(R.id.reg_input_ady);
        reg_input_atasynyn_ady = findViewById(R.id.reg_input_atasynyn_ady);
        reg_input_berlenY = findViewById(R.id.reg_input_berlenY);
        reg_input_doglanY = findViewById(R.id.reg_input_doglanY);

        // date picker
        reg_input_doglanG = findViewById(R.id.reg_input_doglanG);
        reg_input_berlenS = findViewById(R.id.reg_input_berlenS);

        // if user exist
        session = new Session(this);
        if (!session.getSahadatnama().isEmpty()){
            getdata();
            reg_save.setEnabled(false);
            Log.d("SESSION", session.getSahadatnama());
        }
        surat_sayla.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"surat sayla 3x4"), GALLERY_INT);
        });

        // click
        reg_save.setOnClickListener(view -> {
            int day = reg_input_doglanG.getDayOfMonth();
            int month = reg_input_doglanG.getMonth() + 1;
            int year = reg_input_doglanG.getYear();
            doglan_gun = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);

            Log.d("doglan guni", doglan_gun);

            day = reg_input_berlenS.getDayOfMonth();
            month = reg_input_berlenS.getMonth() + 1;
            year = reg_input_berlenS.getYear();
            berlen_gun = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
            Log.d("berlen_gun", berlen_gun);

            year = reg_input_berlenS.getYear() + 10;
            mohleti = String.valueOf(year) + '-' + String.valueOf(month) + '-' + String.valueOf(day);
            Log.d("mohleti", mohleti);

            login();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INT) {
            Uri imageDate = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver() , Uri.parse(String.valueOf(imageDate)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            profile.setImageURI(imageDate);
        }
    }

    private void getdata() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.getUserData(), response -> {
            rQueue.getCache().clear();
            String[] time;
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = new JSONObject();

                for (int i=0; i<jsonArray.length(); i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    imageLoadTask = (ImageLoadTask) new ImageLoadTask(connection.getIP()+"qr/"+ session.getSahadatnama() + ".jpg", profile).execute();

                    reg_input_fam.setText(jsonObject.getString("familyasy"));
                    reg_input_ady.setText(jsonObject.getString("ady"));
                    reg_input_atasynyn_ady.setText(jsonObject.getString("atasynyn_ady"));
                    reg_input_doglanY.setText(jsonObject.getString("doglan_yeri"));
                    reg_input_berlenY.setText(jsonObject.getString("berlen_yeri"));

                    time = jsonObject.getString("doglan_guni").split("-");
                    reg_input_doglanG.updateDate(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
                    time = jsonObject.getString("berlen_senesi").split("-");
                    reg_input_berlenS.updateDate(Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
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

        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(stringRequest);

    }

    private void login() {
        Connection connection = new Connection();
        session = new Session(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, connection.sendUserData(), response -> {
            rQueue.getCache().clear();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("saved")){
                    session.createSahadatnama(jsonObject.getString("ayratyn_bellikler"));
                    Snackbar.make(reg_save, "norm gitdi", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, HomePage.class);
                    startActivity(intent);
                }
                else{
                    Snackbar.make(reg_save, "bolmady(", Snackbar.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.d("VolleyError", error.toString())){
            @Override
            protected Map<String,String> getParams(){
                String image = getStringImage(bitmap);
                Map<String,String> params = new HashMap<>();
                params.put("IMG", image);
                params.put("familyasy", reg_input_fam.getText().toString());
                params.put("ady", reg_input_ady.getText().toString());
                params.put("atasynyn_ady", reg_input_atasynyn_ady.getText().toString());
                params.put("doglan_guni", doglan_gun);
                params.put("dogulan_yeri", reg_input_doglanY.getText().toString());
                params.put("berlen_yeri", reg_input_berlenY.getText().toString());
                params.put("berlen_senesi", berlen_gun);
                params.put("mohleti", mohleti);
                params.put("derejesi", "X B X X X");
                params.put("belgisi", "yok");

                return params;
            }
        };
        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(stringRequest);
    }

    private String getStringImage(Bitmap bm) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte, Base64.DEFAULT);
        return encode;
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}