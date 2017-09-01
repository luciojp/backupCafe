package com.cafesuspenso.ufcg.cafesuspenso.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cafesuspenso.ufcg.cafesuspenso.Model.Transaction;
import com.cafesuspenso.ufcg.cafesuspenso.R;
import com.cafesuspenso.ufcg.cafesuspenso.Task.DownloadImageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 19/06/2017.
 */

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private boolean flagType = false;
    private final LayoutInflater layoutInflater;
    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions, boolean flag){
        this.context = context;
        this.transactions = transactions;
        flagType = flag;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.transaction_card, parent, false);
        ViewHolder cardViewHolder = new ViewHolder(view);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Transaction t = transactions.get(position);
        holder.iconDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage("Deseja realmente excluir?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                removeTransaction(t.getId());
                                transactions.remove(position);
                                update(transactions);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        holder.description.setText(t.getName());
        holder.date.setText(new SimpleDateFormat("dd-MM-yyyy").format(t.getDate()));
        Picasso.with(context).load(Uri.parse(transactions.get(position).getImage())).fit().into(holder.image);
       // new DownloadImageTask(holder.image).execute(transactions.get(position).getImage());
    }

    public void removeTransaction(int t){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url;
        if(!flagType)
            url = "http://192.168.130.14:8080/api/user/shared_products/remove_shared_product/" + t;
        else
            url = "http://192.168.130.14:8080/api/user/shared_products/remove_redeemed_product/" + t;

        Log.d("Login3", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RemoveTransaction", response);
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

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void addList(Transaction t, int position) {
        transactions.add(t);
        notifyItemInserted(position);
    }

    public void update(List<Transaction> t){
        this.transactions = t;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconDelete, image;
        private TextView description, date;

        public ViewHolder(View itemView) {
            super(itemView);
            iconDelete = (ImageView) itemView.findViewById(R.id.delete_icon);
            image = (ImageView) itemView.findViewById(R.id.image);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }
}

