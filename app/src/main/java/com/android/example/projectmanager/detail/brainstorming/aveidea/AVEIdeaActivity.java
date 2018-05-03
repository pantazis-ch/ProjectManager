package com.android.example.projectmanager.detail.brainstorming.aveidea;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.detail.brainstorming.Idea;
import com.android.example.projectmanager.R;

public class AVEIdeaActivity extends AppCompatActivity {

    private Fragment aveIdeaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave_idea);

        String projectKey = getIntent().getStringExtra("projectKey");
        Idea idea = getIntent().getParcelableExtra("idea");
        String ideaKey = getIntent().getStringExtra("ideaKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(idea != null) {
            getSupportActionBar().setTitle(R.string.title_edit_idea);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_idea);
        }

        if (savedInstanceState != null) {
            aveIdeaFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveIdeaFragment");
        } else {
            aveIdeaFragment = AVEIdeaFragment.newInstance(projectKey, idea, ideaKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_idea, aveIdeaFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveIdeaFragment", aveIdeaFragment);
    }


}
