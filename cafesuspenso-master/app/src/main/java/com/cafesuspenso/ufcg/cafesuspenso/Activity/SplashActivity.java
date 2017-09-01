package com.cafesuspenso.ufcg.cafesuspenso.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity {
    private static final long SPLASH_TIME_OUT = 4978;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView myImageView= (ImageView)findViewById(R.id.imgLogo);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        myImageView.startAnimation(myFadeInAnimation);

        loadCafeterias();

        sprint4();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openLoginScreen();
            }
        }, SPLASH_TIME_OUT);

    }

    private void loadCafeterias() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.130.14:8080/api/cafeteria";

        Log.d("Login3", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login4", response);
                        saveMarkers(response, "cafeterias");
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

    private void saveMarkers(String timeLine, String result) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(result, timeLine);
        editor.apply();
    }


    private void openLoginScreen() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean isLogged = sharedPref.getBoolean("isLogged", false);

        Intent i;
        if(!isLogged){
            i = new Intent(SplashActivity.this, LoginActivity.class);
        } else {
            i = new Intent(SplashActivity.this, MainActivity.class);
        }

        startActivity(i);
        finish();
    }


    private void sprint4() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("level", 0);
        editor.putInt("cafesDisponiveis", 2);
        editor.putInt("cafesResgatados", 4);
        editor.putInt("cafesCompartilhados", 9);
        editor.apply();

    }
}
