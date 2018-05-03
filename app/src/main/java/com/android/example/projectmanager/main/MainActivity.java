package com.android.example.projectmanager.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.example.projectmanager.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private MainPagerAdapter mainPagerAdapter;
    private ViewPager mViewPager;

    private FirebaseAuth auth;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle(getString(R.string.app_name));

        TextView tv = findViewById(R.id.tv_sign_in_and_out);
        tv.setText(R.string.sign_out_label);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //auth.signOut();
                try {
                    AuthUI.getInstance().signOut(MainActivity.this);
                } catch (Exception e) {

                }

                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                finish();
            }
        });

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.main_pager);
        mViewPager.setAdapter(mainPagerAdapter);
        mViewPager.setPageTransformer(true, new MainPagerTransformer());

    }



}
