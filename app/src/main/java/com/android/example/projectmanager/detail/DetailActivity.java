package com.android.example.projectmanager.detail;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;

public class DetailActivity extends AppCompatActivity {

    private Project project;
    private String projectKey;

    private DetailPagerAdapter detailPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        project = getIntent().getParcelableExtra("project");
        projectKey = getIntent().getStringExtra("projectKey");

        getSupportActionBar().setTitle(project.getName());

        mViewPager = findViewById(R.id.detail_pager);
        detailPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager(), this, project, projectKey);
        mViewPager.setAdapter(detailPagerAdapter);

    }
}
