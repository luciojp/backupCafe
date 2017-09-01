package com.cafesuspenso.ufcg.cafesuspenso.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.cafesuspenso.ufcg.cafesuspenso.Fragment.LevelUpFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Cafeteria;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Product;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.HashMap;
import java.util.Map;

public class ShareCoffeeActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{

    private BillingProcessor bp;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private Cafeteria cafeteria;
    private Product product;
    private int qntdDisponiveis, resgatados, compartilhados, level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_coffee);

        cafeteria = getIntent().getParcelableExtra("cafeteria");

        TextView name = (TextView) findViewById(R.id.txt_name_product);
        TextView price = (TextView) findViewById(R.id.txt_preco);

        name.setText(cafeteria.getProduct().getName());
        price.setText("R$ " + cafeteria.getProduct().getPrice());



        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        qntdDisponiveis = sharedPref.getInt("cafesDisponiveis", 0);
        resgatados = sharedPref.getInt("cafesResgatados", 0);
        compartilhados = sharedPref.getInt("cafesCompartilhados", 0);
        level = sharedPref.getInt("level", 0);

        /*
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */


        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtoArILztD40ekBXJ9a2QjDXbxCsTwF8rNw2+w3htDaWEHNy6IZHi5ZEwPhHGDK6t/BurtZdN6tsB1/8YPPu9yJyAbogiXeZFU8SBt5Sef1RFXc/wW7ovc5uystFliMgA36rcW5bknbdnyLF36oLuaT8xENft8+f274W19KfcllgET1b6ff2JAdae0BAIK0IMMiut8pBxEhVbFW+kLLI/UifhuJm/OhTj4gQWbwMqAD3jiX5cbayFYzu4T/bd/ak2FZED/L7XzKJwH9b5knhM0c/J91ASTJh1qsmwZXzvvdDFOTf5Su3/ZvieXv0tZrOX2nixYv4MGiDlHxSldthhwQIDAQA", this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }

        });

        Button share = (Button) findViewById(R.id
                .share_btn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shop();
                calculateLevel();
            }
        });
    }

    private void shop() {
        //if(bp.isPurchased("android.test.purchased"))
          //  bp.consumePurchase("android.test.purchased");
        bp.purchase(this, "cafesuspenso.cafe");
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        toServer();

        calculateLevel();

        new AlertDialog.Builder(this)
                .setMessage("Deseja compartilhar em suas redes sociais?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        shareWithFacebook();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void calculateLevel() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("cafesCompartilhados", compartilhados + 1);
        compartilhados++;

        switch (level){
            case 0:
                if(compartilhados - resgatados > 4) {
                    editor.putInt("level", level + 1);
                    level++;
                    callFragment("Café com leite");
                }
                break;
            case 1:
                if(compartilhados - resgatados > 9) {
                    editor.putInt("level", level + 1);
                    level++;
                    callFragment("Café grande");
                }
                break;
        }
        editor.apply();
    }

    private void callFragment(String type) {
        Intent intent = new Intent(this, LevelUpFragment.class);
        intent.putExtra("status", "Level UP");
        intent.putExtra("text", "Você subiu de level, você recebeu a insígnia " + type + "! Agora você podera resgatar uma quantidade maior de cafés por dia! Continue compartilhando para subir de level!");
        this.startActivity(intent);
    }


    private void toServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.130.14:8080/api/user/shared_products/" + cafeteria.getId() + "/" + cafeteria.getProduct().getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // openLoginScreen();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LoginE toString", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "lucas123");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void shareWithFacebook() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://cafesuspenso.herokuapp.com"))
                    .setContentTitle("Café Suspenso")
                    .setContentDescription("Resgatei um café")
                    .build();
            shareDialog.show(linkContent);
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this,"Erro na compra", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
