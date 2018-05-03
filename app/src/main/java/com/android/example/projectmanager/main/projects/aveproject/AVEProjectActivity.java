package com.android.example.projectmanager.main.projects.aveproject;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;

public class AVEProjectActivity extends AppCompatActivity {

    private Fragment aveProjectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave_project);

        Project project = getIntent().getParcelableExtra("project");
        String projectKey = getIntent().getStringExtra("projectKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(project != null) {
            getSupportActionBar().setTitle(R.string.title_edit_project);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_project);
        }

        if (savedInstanceState != null) {
            aveProjectFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveProjectFragment");
        } else {
            aveProjectFragment = AVEProjectFragment.newInstance(project, projectKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_project, aveProjectFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveProjectFragment", aveProjectFragment);
    }

}
