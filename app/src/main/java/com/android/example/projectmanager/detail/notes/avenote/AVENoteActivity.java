package com.android.example.projectmanager.detail.notes.avenote;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.example.projectmanager.detail.notes.Note;
import com.android.example.projectmanager.R;

public class AVENoteActivity extends AppCompatActivity {

    private Fragment aveNoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ave_note);

        String projectKey = getIntent().getStringExtra("projectKey");
        Note note = getIntent().getParcelableExtra("note");
        String noteKey = getIntent().getStringExtra("noteKey");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(note != null) {
            getSupportActionBar().setTitle(R.string.title_edit_note);
        } else {
            getSupportActionBar().setTitle(R.string.title_add_new_note);
        }

        if (savedInstanceState != null) {
            aveNoteFragment = getSupportFragmentManager().getFragment(savedInstanceState, "aveNoteFragment");
        } else {
            aveNoteFragment = AVENoteFragment.newInstance(projectKey, note, noteKey);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_ave_note, aveNoteFragment).commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "aveNoteFragment", aveNoteFragment);
    }


}
