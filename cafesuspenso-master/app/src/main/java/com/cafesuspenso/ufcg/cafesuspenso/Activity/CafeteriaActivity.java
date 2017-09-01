package com.cafesuspenso.ufcg.cafesuspenso.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafesuspenso.ufcg.cafesuspenso.Model.Cafeteria;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Product;
import com.cafesuspenso.ufcg.cafesuspenso.Model.QRCodeData;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.cafesuspenso.ufcg.cafesuspenso.Task.DownloadImageTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class CafeteriaActivity extends AppCompatActivity {
    private Button shareBtn, redeemBtn;
    private Cafeteria cafeteria;
    private TextView namePlace, descriptionPlace, countCoffee;
    private ImageView imagePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria);

        cafeteria = getIntent().getParcelableExtra("cafeteria");
        shareBtn = (Button) findViewById(R.id.share_btn);


        Intent readerIntent = getIntent();
        Bundle bundle = readerIntent.getExtras();


        namePlace = (TextView) findViewById(R.id.namePlace);
        descriptionPlace = (TextView) findViewById(R.id.descriptionPlace);
        countCoffee = (TextView) findViewById(R.id.countCoffee);
        imagePlace = (ImageView) findViewById(R.id.imagePlace);
        if(bundle.getBoolean("qrCode", false)) {
            Log.d("QRCODE", "QRCODE2");
            String qrCodeData = bundle.getString("qrCodeData");
            Gson qrCodeDataGson = new Gson();
            QRCodeData qrCodeObject = qrCodeDataGson.fromJson(qrCodeData, QRCodeData.class);
            setInformationCafeteria(qrCodeObject);
        } else {
            namePlace.setText(cafeteria.getPlacename());
            descriptionPlace.setText(cafeteria.getComplement());
            countCoffee.setText(cafeteria.getAvailableCoffee()+"");
            Picasso.with(this).load(Uri.parse(cafeteria.getImagem())).fit().into(imagePlace);
        }


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShareCoffeeActivity.class);
                intent.putExtra("cafeteria", getIntent().getParcelableExtra("cafeteria"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        redeemBtn = (Button) findViewById(R.id.redeem_btn);
        if(cafeteria.getAvailableCoffee() == 0) {
            redeemBtn.setVisibility(View.GONE);
        }
        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), RedeemCoffeeActivity.class);
                    i.putExtra("cafeteria", getIntent().getParcelableExtra("cafeteria"));
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
    }

    private void setInformationCafeteria(QRCodeData qrCodeObject) {
        namePlace.setText(qrCodeObject.getName());
        // Log.d("QRCODE", qrCodeObject.getPlaceImg());
        Picasso.with(this).load(qrCodeObject.getPlaceImg()).fit().into(imagePlace);
        countCoffee.setText(qrCodeObject.getNumCoffees().toString());
        cafeteria = new Cafeteria();
        cafeteria.setName(qrCodeObject.getName());
        cafeteria.setAvailableCoffee(qrCodeObject.getNumCoffees());
        cafeteria.setImagem(qrCodeObject.getPlaceImg());
    }

}
