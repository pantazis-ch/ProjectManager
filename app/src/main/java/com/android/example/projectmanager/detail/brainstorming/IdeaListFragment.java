package com.android.example.projectmanager.detail.brainstorming;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.projectmanager.detail.brainstorming.aveidea.AVEIdeaActivity;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class IdeaListFragment extends Fragment implements IdeaAdapter.IdeaAdapterOnClickHandler {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Project project;
    private String projectKey;

    private FirebaseDatabase database;
    private DatabaseReference ideaListRef;

    private String user;

    private Query projectIdeasQuery;

    private RecyclerView mRecyclerView;
    private IdeaAdapter mAdapter;
    private IdeaAdapter.IdeaAdapterOnClickHandler clickHandler;

    private ArrayList<Idea> ideas;
    private HashMap<Idea,String> ideaKeys;

    private boolean delete;

    private boolean itemsMoved;


    public static IdeaListFragment newInstance(Project project, String projectKey) {
        IdeaListFragment fragment = new IdeaListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, project);
        args.putString(ARG_PARAM2, projectKey);
        fragment.setArguments(args);
        return fragment;
    }

    public IdeaListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = getArguments().getParcelable(ARG_PARAM1);
            projectKey = getArguments().getString(ARG_PARAM2);
        }
        user = FirebaseAuth.getInstance().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brainstorming_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ideas = new ArrayList<>();
        ideaKeys = new HashMap<>();

        clickHandler = this;

        mRecyclerView = getView().findViewById(R.id.rv_brainstorming_list);

        mAdapter = new IdeaAdapter(clickHandler, ideas, getContext());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mRecyclerView.setHasFixedSize(false);


        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_add_new_idea);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVEIdeaActivity.class);
                intent.putExtra("ideaNumber", mAdapter.getItemCount());
                intent.putExtra("projectKey", projectKey);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        database = FirebaseDatabase.getInstance();
        ideaListRef = database.getReference(user).child("brainstorming");

        projectIdeasQuery = ideaListRef.orderByChild("projectKey").equalTo(projectKey);

        projectIdeasQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Idea idea = dataSnapshot.getValue(Idea.class);

                ideas.add(idea);

                Collections.sort(ideas, new Comparator<Idea>() {
                    @Override
                    public int compare(Idea i1, Idea i2) {
                        return i1.getIdeaNumber() - i2.getIdeaNumber();
                    }
                });

                mAdapter.notifyItemInserted(mAdapter.getItemCount());

                ideaKeys.put(idea, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Idea idea = dataSnapshot.getValue(Idea.class);

                int index = idea.getIdeaNumber();

                boolean notifyAdapter = false;
                if(!ideas.get(index).getIdea().equals(idea.getIdea())) {
                    ideas.get(index).setIdea(idea.getIdea());
                    notifyAdapter = true;
                }

                if(notifyAdapter) {
                    mAdapter.notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                itemsMoved = true;

                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();

                reArrange(mAdapter.ideaList, fromPosition, toPosition);

                for (int i=0; i<mAdapter.ideaList.size(); i++) {
                    String key = ideaKeys.get((Idea) mAdapter.ideaList.get(i));
                    ideaListRef.child(key).child("ideaNumber").setValue(i);
                }

                mAdapter.notifyItemMoved(fromPosition,toPosition);

                itemsMoved = false;

                return true;
            }

            private void reArrange(List<Idea> list, int fromPosition, int toPosition) {
                if(fromPosition != toPosition){
                    if(fromPosition > toPosition)
                        reArrange(list,fromPosition -1, toPosition);
                    else
                        reArrange(list,fromPosition +1, toPosition);

                    Collections.swap(list, fromPosition, toPosition);
                }
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                delete = true;

                final Snackbar mySnackbar = Snackbar.make(getView(), getString(R.string.msg_item_deleted), Snackbar.LENGTH_SHORT);
                mySnackbar.setAction(getString(R.string.msg_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete = false;
                    }
                });
                mySnackbar.setCallback( new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if(delete) {
                            int position = viewHolder.getAdapterPosition();
                            for (int i = position + 1; i < ideas.size(); i++) {
                                Idea idea = ideas.get(i);
                                int oldPosition = idea.getIdeaNumber();
                                int newPosition = oldPosition - 1;

                                ideas.get(i).setIdeaNumber(newPosition);
                                ideaListRef.child(ideaKeys.get(ideas.get(i))).child("ideaNumber").setValue(newPosition);
                            }

                            ideaListRef.child(ideaKeys.get(ideas.get(position))).removeValue();
                            ideaKeys.remove(ideas.get(position));
                            ideas.remove(position);

                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshDrawableState();
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onShown(Snackbar sb) {
                        super.onShown(sb);
                    }
                });
                mySnackbar.show();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onClick(Idea idea) {
        Intent intent = new Intent(getContext(), AVEIdeaActivity.class);
        intent.putExtra("projectKey", projectKey);
        intent.putExtra("idea", idea);
        intent.putExtra("ideaKey", ideaKeys.get(idea));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
