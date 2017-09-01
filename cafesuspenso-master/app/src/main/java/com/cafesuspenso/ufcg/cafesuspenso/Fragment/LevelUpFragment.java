package com.cafesuspenso.ufcg.cafesuspenso.Fragment;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.cafesuspenso.ufcg.cafesuspenso.R;

import org.w3c.dom.Text;

public class LevelUpFragment extends AppCompatActivity {

    private String status;
    private String text;

    public LevelUpFragment() {
        // Required empty public constructor
    }

    public static LevelUpFragment newInstance() {
        LevelUpFragment frag = new LevelUpFragment();
        return frag;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up_fragment);

        status = getIntent().getStringExtra("status");
        text = getIntent().getStringExtra("text");

        TextView statusT = (TextView) findViewById(R.id.status);
        statusT.setText(status);

        TextView textT = (TextView) findViewById(R.id.text);
        textT.setText(text);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}
