package com.android.example.projectmanager.detail.deadlines.avedeadline;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.detail.deadlines.Deadline;
import com.android.example.projectmanager.R;

public class AVEDeadlineActivity extends AppCompatActivity {

    private Fragment aveDeadlineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);

        String projectKey = getIntent().getStringExtra("projectKey");
        Deadline deadline = getIntent().getParcelableExtra("deadline");
        String deadlineKey = getIntent().getStringExtra("deadlineKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(deadline != null) {
            getSupportActionBar().setTitle(R.string.title_edit_deadline);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_deadline);
        }

        if (savedInstanceState != null) {
            aveDeadlineFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveDeadlineFragment");
        } else {
            aveDeadlineFragment = AVEDeadlineFragment.newInstance(projectKey, deadline, deadlineKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_deadline, aveDeadlineFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveDeadlineFragment", aveDeadlineFragment);
    }


}
