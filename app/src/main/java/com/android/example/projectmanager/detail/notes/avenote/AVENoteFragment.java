package com.android.example.projectmanager.detail.notes.avenote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.example.projectmanager.detail.notes.Note;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.Analytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVENoteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String projectKey;
    private Note note;
    private String noteKey;

    @BindView(R.id.panel_note) LinearLayout noteNamePanel;
    @BindView(R.id.tv_note_requirement) TextView noteNameRequirement;
    @BindView(R.id.et_note) EditText noteNameEditText;
    @BindView(R.id.tv_note) TextView noteNameTextView;

    @BindView(R.id.btn_save_note) Button saveNoteButton;

    private boolean isNoteNameInViewOnlyMode;

    private FirebaseDatabase database;
    private DatabaseReference noteListRef;

    private String user;


    public AVENoteFragment() {
        // Required empty public constructor
    }

    public static AVENoteFragment newInstance(String projectKey, Note note, String noteKey) {
        AVENoteFragment fragment = new AVENoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, projectKey);
        args.putParcelable(ARG_PARAM2, note);
        args.putString(ARG_PARAM3, noteKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectKey = getArguments().getString(ARG_PARAM1);
            note = getArguments().getParcelable(ARG_PARAM2);
            noteKey = getArguments().getString(ARG_PARAM3);
        }

        if(savedInstanceState != null) {
            isNoteNameInViewOnlyMode = savedInstanceState.getBoolean("isNoteNameInViewOnlyMode");
        } else {
            if(note != null) {
                isNoteNameInViewOnlyMode = true;
            } else {
                isNoteNameInViewOnlyMode = false;
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ave_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        if(isNoteNameInViewOnlyMode) {
            setReadOnlyModeForNoteName();
        }

        showButtonIfNeeded();

        database = FirebaseDatabase.getInstance();
        noteListRef = database.getReference(user).child("notes");

    }

    private void showButtonIfNeeded(){
        if(buttonShouldBeVisible()) {
            saveNoteButton.setVisibility(View.VISIBLE);
            saveNoteButton.setText(getButtonText());
            saveNoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveNoteToFirebase();
                }
            });
        } else {
            saveNoteButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isNoteNameInViewOnlyMode){
            return false;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(note == null) {
            return getString(R.string.label_add_note);
        } else {
            return getString(R.string.label_save_changes);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isNoteNameInViewOnlyMode", isNoteNameInViewOnlyMode);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForNoteName() {
        noteNameEditText.setVisibility(View.GONE);

        noteNameTextView.setVisibility(View.VISIBLE);
        noteNameTextView.setText(note.getNoteName());

        noteNameRequirement.setVisibility(View.GONE);

        noteNamePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForNoteName();
            }
        });
    }

    private void setEditModeForNoteName() {
        noteNameTextView.setVisibility(View.GONE);

        noteNameEditText.setVisibility(View.VISIBLE);

        noteNameRequirement.setVisibility(View.VISIBLE);

        noteNameEditText.setText(note.getNoteName());
        noteNameEditText.requestFocus();

        noteNamePanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(noteNameEditText,InputMethodManager.SHOW_IMPLICIT);

        isNoteNameInViewOnlyMode = false;

        showButtonIfNeeded();
    }

    public void saveNoteToFirebase() {
        String name;
        if(noteNameEditText.getVisibility() == View.VISIBLE) {
            name = noteNameEditText.getText().toString();
        } else {
            name = noteNameTextView.getText().toString();
        }

        if(name.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_note, Toast.LENGTH_SHORT).show();
            return;
        }

        if(note != null) {
            note.setNoteName(name);
            noteListRef.child(noteKey).setValue(note);
            Analytics.logEventNoteSaved(getContext(), false);
        } else {
            int noteNumber = getActivity().getIntent().getIntExtra("noteNumber", 0);

            Note note = new Note(projectKey, name, noteNumber);
            noteListRef.push().setValue(note);
            Analytics.logEventNoteSaved(getContext(), true);
        }

        getActivity().finish();

    }
}
