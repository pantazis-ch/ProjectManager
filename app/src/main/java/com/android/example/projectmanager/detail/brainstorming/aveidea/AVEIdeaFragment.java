package com.android.example.projectmanager.detail.brainstorming.aveidea;


import android.content.Context;
import android.content.res.TypedArray;
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

import com.android.example.projectmanager.detail.brainstorming.Idea;
import com.android.example.projectmanager.R;
import com.android.example.projectmanager.utilities.Analytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AVEIdeaFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String projectKey;
    private Idea idea;
    private String ideaKey;

    @BindView(R.id.panel_idea) LinearLayout ideaPanel;
    @BindView(R.id.tv_idea_requirement) TextView ideaRequirement;
    @BindView(R.id.et_idea) EditText ideaEditText;
    @BindView(R.id.tv_idea) TextView ideaTextView;

    @BindView(R.id.btn_save_idea) Button saveIdeaButton;

    private boolean isIdeaNameInViewOnlyMode;

    private FirebaseDatabase database;
    private DatabaseReference ideaListRef;

    private String user;


    public AVEIdeaFragment() {
        // Required empty public constructor
    }

    public static AVEIdeaFragment newInstance(String projectKey, Idea idea, String ideaKey) {
        AVEIdeaFragment fragment = new AVEIdeaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, projectKey);
        args.putParcelable(ARG_PARAM2, idea);
        args.putString(ARG_PARAM3, ideaKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectKey = getArguments().getString(ARG_PARAM1);
            idea = getArguments().getParcelable(ARG_PARAM2);
            ideaKey = getArguments().getString(ARG_PARAM3);
        }

        if(savedInstanceState != null) {
            isIdeaNameInViewOnlyMode = savedInstanceState.getBoolean("isIdeaNameInViewOnlyMode");
        } else {
            if(idea != null) {
                isIdeaNameInViewOnlyMode = true;
            } else {
                isIdeaNameInViewOnlyMode = false;
            }
        }

        user = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ave_idea, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, getView());

        if(isIdeaNameInViewOnlyMode) {
            setReadOnlyModeForIdeaName();
        }

        showButton();

        database = FirebaseDatabase.getInstance();
        ideaListRef = database.getReference(user).child("brainstorming");

    }

    private void showButton(){
        if(buttonShouldBeVisible()) {
            saveIdeaButton.setVisibility(View.VISIBLE);
            saveIdeaButton.setText(getButtonText());
            saveIdeaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveIdeaToFirebase();
                }
            });
        } else {
            saveIdeaButton.setVisibility(View.GONE);
        }
    }

    private boolean buttonShouldBeVisible() {
        if(isIdeaNameInViewOnlyMode){
            return false;
        } else {
            return true;
        }
    }

    private String getButtonText() {
        if(idea == null) {
            return getString(R.string.label_add_idea);
        } else {
            return getString(R.string.label_save_changes);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isIdeaNameInViewOnlyMode", isIdeaNameInViewOnlyMode);

        super.onSaveInstanceState(outState);
    }

    private void setReadOnlyModeForIdeaName() {
        ideaEditText.setVisibility(View.GONE);

        ideaTextView.setVisibility(View.VISIBLE);
        ideaTextView.setText(idea.getIdea());

        ideaRequirement.setVisibility(View.GONE);

        ideaPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditModeForSpecName();
            }
        });
    }

    private void setEditModeForSpecName() {
        ideaTextView.setVisibility(View.GONE);

        ideaEditText.setVisibility(View.VISIBLE);

        ideaRequirement.setVisibility(View.VISIBLE);

        ideaEditText.setText(idea.getIdea());
        ideaEditText.requestFocus();

        ideaPanel.setOnClickListener(null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ideaEditText,InputMethodManager.SHOW_IMPLICIT);

        isIdeaNameInViewOnlyMode = false;

        showButton();
    }

    public void saveIdeaToFirebase() {
        String name;
        if(ideaEditText.getVisibility() == View.VISIBLE) {
            name = ideaEditText.getText().toString();
        } else {
            name = ideaTextView.getText().toString();
        }

        if(name.isEmpty()){
            Toast.makeText(getContext(), R.string.msg_no_idea, Toast.LENGTH_SHORT).show();
            return;
        }

        if(idea != null) {
            idea.setIdea(name);
            ideaListRef.child(ideaKey).setValue(idea);
            Analytics.logEventIdeaSaved(getContext(), false);
        } else {
            int ideaNumber = getActivity().getIntent().getIntExtra("ideaNumber", 0);

            Idea idea = new Idea(projectKey, name, ideaNumber, getRandomColor());
            ideaListRef.push().setValue(idea);
            Analytics.logEventIdeaSaved(getContext(), true);
        }

        getActivity().finish();
    }

    private String getRandomColor() {

        int[] darkColors;

        TypedArray tadc = getContext().getResources().obtainTypedArray(R.array.darkColors);
        darkColors = new int[tadc.length()];
        for (int i = 0; i < tadc.length(); i++) {
            darkColors[i] = tadc.getColor(i, 0);
        }
        tadc.recycle();

        String color;
        Random r = new Random();
        int darkColorIndex = r.nextInt(darkColors.length);
        color = "0" + " " + darkColorIndex;

        return color;
    }
}
