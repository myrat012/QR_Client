package com.example.new_qr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class HomePage extends AppCompatActivity {
    private Button sahadatnama, guwanama, gurat_hat;
    private Session session;
    private ImageView qrCodeIV;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sahadatnama = findViewById(R.id.button);
        guwanama = findViewById(R.id.button2);
        gurat_hat = findViewById(R.id.button3);

        qrCodeIV = findViewById(R.id.idIVQrcode);

        session = new Session(this);

        if (!session.getSahadatnama().isEmpty()){
            qr_code_creator();
            Log.d("sah", session.getSahadatnama());
            sahadatnama.setBackgroundColor(getResources().getColor(R.color.success));
        }
        if (!session.getGuwaHat().isEmpty()) {
            Log.d("guwah", session.getGuwaHat());
            guwanama.setBackgroundColor(getResources().getColor(R.color.success));
        }
        if (!session.getGuratHat().isEmpty()) {
            Log.d("gurah", session.getGuratHat());
            gurat_hat.setBackgroundColor(getResources().getColor(R.color.success));
        }

        sahadatnama.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        guwanama.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivityGuwaHat.class);
            startActivity(intent);
        });

        gurat_hat.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivityGuratHat.class);
            startActivity(intent);
        });

    }

    private void qr_code_creator() {
        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(session.getSahadatnama(), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
        }
    }

}