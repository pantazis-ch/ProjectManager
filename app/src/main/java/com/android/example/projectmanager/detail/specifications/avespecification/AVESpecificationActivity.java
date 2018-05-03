package com.android.example.projectmanager.detail.specifications.avespecification;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.detail.specifications.Specification;
import com.android.example.projectmanager.R;


public class AVESpecificationActivity extends AppCompatActivity {

    private Fragment aveSpecificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave_specification);

        String projectKey = getIntent().getStringExtra("projectKey");
        Specification spec = getIntent().getParcelableExtra("spec");
        String specKey = getIntent().getStringExtra("specKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(spec != null) {
            getSupportActionBar().setTitle(R.string.title_edit_specification);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_specification);
        }

        if (savedInstanceState != null) {
            aveSpecificationFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveSpecificationFragment");
        } else {
            aveSpecificationFragment = AVESpecificationFragment.newInstance(projectKey, spec, specKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_spec, aveSpecificationFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveSpecificationFragment", aveSpecificationFragment);
    }


}
