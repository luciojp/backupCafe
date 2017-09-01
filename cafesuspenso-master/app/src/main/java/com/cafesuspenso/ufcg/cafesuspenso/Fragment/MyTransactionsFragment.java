package com.cafesuspenso.ufcg.cafesuspenso.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cafesuspenso.ufcg.cafesuspenso.Adapter.TransactionAdapter;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Cafeteria;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Product;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Transaction;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyTransactionsFragment extends Fragment {
    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private RecyclerView recyclerView;
    private String title;
    private TextView titleFragment;
    private boolean flag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_transactions, container, false);
        titleFragment = (TextView) v.findViewById(R.id.title);
        titleFragment.setText(title);

        this.recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        transactions = fetchTransactions();
        this.transactionAdapter = new TransactionAdapter(getContext(), transactions, flag);

        registerForContextMenu(recyclerView);
        this.recyclerView.setAdapter(transactionAdapter);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        transactionAdapter.update(transactions);

        return v;
    }

    private List<Transaction> fetchTransactions() {
        List<Transaction> result = new ArrayList<>();
        Transaction t;

        if(flag)
            getTransactionsRedeemed();
        else
            getTransactionsShared();

        Collections.sort(result, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction, Transaction t1) {
                if (transaction.getDate().after(t1.getDate()))
                    return -1;
                else
                    return 1;
            }
        });
        return result;
    }

    public void changeTitle(String title){
        this.title = title;
        if(title.equals("Meus resgates"))
            flag = true;
        else
            flag = false;
    }

    public void getTransactionsShared() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.130.14:8080/api/user/shared_products";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Transactions", response);
                        saveTransactions(response);
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

    public void getTransactionsRedeemed() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://192.168.130.14:8080/api/user/redeem_products";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Transactions", response);
                        saveTransactions(response);
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

    private void saveTransactions(String response) {
        ArrayList result = new ArrayList<>();
        Random rand = new Random();
        Date d;
        d = new Date();

        if(response != null){
            Random random = new Random();
            try {
                JSONArray responsePost = new JSONArray(response);

                for (int i = 0; i < responsePost.length(); i++) {
                    JSONObject marked = responsePost.getJSONObject(i);

                    String name = marked.getString("name");
                    int id = marked.getInt("id");
                    String description = marked.getString("description");
                    Double price = marked.getDouble("price");
                    String imagem = marked.getString("image");
                    Boolean accepted = marked.getBoolean("accepted");
                    result.add(new Transaction(id,description,imagem,d,name,price));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        transactions = result;
        this.transactionAdapter = new TransactionAdapter(getContext(), transactions, flag);
        registerForContextMenu(recyclerView);
        this.recyclerView.setAdapter(transactionAdapter);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        transactionAdapter.update(transactions);

    }


}
