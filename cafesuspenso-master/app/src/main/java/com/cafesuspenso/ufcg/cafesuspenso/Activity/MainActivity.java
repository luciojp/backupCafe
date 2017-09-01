package com.cafesuspenso.ufcg.cafesuspenso.Activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cafesuspenso.ufcg.cafesuspenso.Fragment.AboutFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Fragment.LevelUpFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Fragment.MapsFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Fragment.MyTransactionsFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Fragment.RankingFragment;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Cafeteria;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Product;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.cafesuspenso.ufcg.cafesuspenso.Task.DownloadImageTask;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    private boolean stateCard = false;
    private TextView namePlaceTxt, countPlaceTxt;
    private CardView cardInformation;
    private FloatingActionButton fab;
    private Cafeteria cafeteriaSelected;

    private AboutFragment aboutFragment;
    private MapsFragment mapsFragment;
    private MyTransactionsFragment transactionFragment;
    private RankingFragment rankingFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("TagTest", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ButterKnife.bind(this);

        namePlaceTxt = (TextView) findViewById(R.id.name_textView);
        countPlaceTxt = (TextView) findViewById(R.id.count_textView);
        cardInformation = (CardView) findViewById(R.id.information_card);
        moveBottomBarDown();


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCode();
            }
        });

        Button btn = (Button) findViewById(R.id.buttonView);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCafeteriaActivity();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        ImageView imageUser = (ImageView) headerView.findViewById(R.id.imageView);
        TextView nameUser = (TextView) headerView.findViewById(R.id.nameTxt);
        TextView classUser = (TextView) headerView.findViewById(R.id.classTxt);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        nameUser.setText(user.getDisplayName());

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int level = sharedPref.getInt("level", 0);
        String rLevel = "Café pequeno";
        switch(level){
            case 0:
                rLevel = "Café pequeno";
                break;
            case 1:
                rLevel = "Café com leite";
                break;
            default:
                rLevel = "Café grande";
                break;
        }

        classUser.setText(rLevel);
        Picasso.with(this).load(Uri.parse(user.getPhotoUrl().toString())).into(imageUser);

        mapsFragment = new MapsFragment();
        aboutFragment = new AboutFragment();
        transactionFragment = new MyTransactionsFragment();
        rankingFragment = new RankingFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, mapsFragment, "MapsFragment");
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void qrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getString(R.string.qr_code_prompt));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    private void callCafeteriaActivity() {
        Intent intent = new Intent(this, CafeteriaActivity.class);
        intent.putExtra("cafeteria", cafeteriaSelected);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } if (stateCard) {
            moveBottomBarDown();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null){
                Toast.makeText(this, getString(R.string.qr_code_fail_menssage), Toast.LENGTH_LONG).show();
            }else {
                Log.d("QRCODE", "QRCODE1");
                Intent readerIntent = new Intent(this, CafeteriaActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("qrCodeData", result.getContents());
                bundle.putBoolean("qrCode", true);
                readerIntent.putExtras(bundle);
                startActivity(readerIntent);
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        // TODO mudar fragment aqui
        if(id == R.id.nav_map){
            fab.setVisibility(View.VISIBLE);
            cardInformation.setVisibility(View.VISIBLE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, mapsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            if(stateCard){
                moveBottomBarDown();
            }
            fab.setVisibility(View.GONE);

            if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isLogged", false);
                editor.apply();
                callLoginActivity();
            }else if (id == R.id.nav_about) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, aboutFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (id == R.id.nav_my_redeem) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                transactionFragment = new MyTransactionsFragment();
                transactionFragment.changeTitle("Meus resgates");
                fragmentTransaction.replace(R.id.container, transactionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else if (id == R.id.nav_my_share) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                transactionFragment = new MyTransactionsFragment();
                transactionFragment.changeTitle("Meus compartilhamentos");
                fragmentTransaction.replace(R.id.container, transactionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } else if (id == R.id.nav_my_top_donator){
                Intent intent = new Intent(this, LevelUpFragment.class);
                intent.putExtra("status", "Level UP");
                intent.putExtra("text", "Você passou de level, agora você é um usuário pika das galáxias! Agora você pode resgatar muito mais cafés! Continue compartilhando para subir cada vez mais de level!");
                this.startActivity(intent);
            } else if (id == R.id.nav_top_coffee_shop){
                Intent intent = new Intent(this, LevelUpFragment.class);
                intent.putExtra("status", "Level DOWN");
                intent.putExtra("text", "Você caiu de level, agora você é um usuário menos pika das galáxias! Agora você pode resgatar um numero menor de cafés! Continue compartilhando para subir de level!");
                this.startActivity(intent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void moveBottomBarUp(String name, Integer count) {
        if(!stateCard) {
            fab.setVisibility(View.GONE);
            cardInformation.setVisibility(View.VISIBLE);

            ObjectAnimator animX = ObjectAnimator.ofFloat(cardInformation, View.TRANSLATION_Y, cardInformation.getHeight(), 0);
            int animationDurationTime = 250;
            animX.setDuration(animationDurationTime);
            animX.start();
            stateCard = true;
        }
        namePlaceTxt.setText(name);
        countPlaceTxt.setText(count + " café(s) suspenso(s)");
    }

    public void moveBottomBarDown() {
        if(stateCard) {
            fab.setVisibility(View.VISIBLE);

            ObjectAnimator animX = ObjectAnimator.ofFloat(cardInformation, View.TRANSLATION_Y, 0, cardInformation.getHeight());
            int animationDurationTime = 250;
            animX.setDuration(animationDurationTime);
            animX.start();
        }
        stateCard = false;
    }

    public Cafeteria getCafeteriaSelected() {
        return cafeteriaSelected;
    }

    public void setCafeteriaSelected(Cafeteria cafeteriaSelected) {
        this.cafeteriaSelected = cafeteriaSelected;
    }

}
