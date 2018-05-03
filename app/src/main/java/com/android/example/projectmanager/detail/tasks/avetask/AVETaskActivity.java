package com.android.example.projectmanager.detail.tasks.avetask;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.detail.tasks.Task;
import com.android.example.projectmanager.R;

public class AVETaskActivity extends AppCompatActivity {

    private Fragment aveTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave_task);

        String projectKey = getIntent().getStringExtra("projectKey");
        Task task = getIntent().getParcelableExtra("task");
        String taskKey = getIntent().getStringExtra("taskKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(task != null) {
            getSupportActionBar().setTitle(R.string.title_edit_task);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_task);
        }

        if (savedInstanceState != null) {
            aveTaskFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveTaskFragment");
        } else {
            aveTaskFragment = AVETaskFragment.newInstance(projectKey, task, taskKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_task, aveTaskFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveTaskFragment", aveTaskFragment);
    }


}
