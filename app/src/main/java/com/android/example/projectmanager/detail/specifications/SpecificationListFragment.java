package com.android.example.projectmanager.detail.specifications;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.projectmanager.detail.specifications.avespecification.AVESpecificationActivity;
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


public class SpecificationListFragment extends Fragment implements SpecificationAdapter.SpecificationAdapterOnClickHandler {

    private String projectKey;

    private FirebaseDatabase database;
    private DatabaseReference specListRef;
    private Query projectSpecsQuery;

    private String user;

    private RecyclerView mRecyclerView;
    private SpecificationAdapter mAdapter;
    private SpecificationAdapter.SpecificationAdapterOnClickHandler mClickHandler;

    private ArrayList<Specification> specs;
    private HashMap<Specification, String> specKeys;

    private boolean delete;


    public static SpecificationListFragment newInstance() {
        SpecificationListFragment fragment = new SpecificationListFragment();

        return fragment;
    }

    public SpecificationListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        projectKey = getActivity().getIntent().getStringExtra("projectKey");
        user = FirebaseAuth.getInstance().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specification_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        specs = new ArrayList<>();
        specKeys = new HashMap<>();

        mRecyclerView = getView().findViewById(R.id.rv_specification_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mClickHandler = this;

        mAdapter = new SpecificationAdapter(mClickHandler, specs);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton addNewSpecFab = getView().findViewById(R.id.fab_add_new_spec);
        addNewSpecFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVESpecificationActivity.class);
                intent.putExtra("projectKey", projectKey);
                if(mAdapter != null && mAdapter.getItemCount() != 0) {
                    intent.putExtra("specNumber", mAdapter.getItemCount());
                }
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        database = FirebaseDatabase.getInstance();
        specListRef = database.getReference(user).child("specifications");

        projectSpecsQuery = specListRef.orderByChild("projectKey").equalTo(projectKey);

        projectSpecsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Specification spec = dataSnapshot.getValue(Specification.class);

                specs.add(spec);

                Collections.sort(specs, new Comparator<Specification>() {
                    @Override
                    public int compare(Specification s1, Specification s2) {
                        return s1.getSpecNumber() - s2.getSpecNumber();
                    }
                });

                mAdapter.notifyItemInserted(mAdapter.getItemCount());

                specKeys.put(spec, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Specification spec = dataSnapshot.getValue(Specification.class);

                int index = spec.getSpecNumber();

                boolean notifyAdapter = false;
                if(!specs.get(index).getSpecName().equals(spec.getSpecName())) {
                    specs.get(index).setSpecName(spec.getSpecName());
                    notifyAdapter = true;
                }

                if(!specs.get(index).getSpecNote().equals(spec.getSpecNote())) {
                    specs.get(index).setSpecNote(spec.getSpecNote());
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
                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();

                String fromPositionKey = specKeys.get(mAdapter.specList.get(fromPosition));
                String toPositionKey = specKeys.get(mAdapter.specList.get(toPosition));

                specListRef.child(fromPositionKey).child("specNumber").setValue(toPosition);
                specListRef.child(toPositionKey).child("specNumber").setValue(fromPosition);

                ((SpecificationAdapter.SpecificationHolder) viewHolder).specNumber.setText(String.valueOf(toPosition + 1));
                ((SpecificationAdapter.SpecificationHolder) target).specNumber.setText(String.valueOf(fromPosition + 1));

                Collections.swap(specs, fromPosition, toPosition);

                mAdapter.notifyItemMoved(fromPosition, toPosition);

                return true;
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
                            for (int i = position + 1; i < specs.size(); i++) {
                                Specification spec = specs.get(i);
                                int oldPosition = spec.getSpecNumber();
                                int newPosition = oldPosition - 1;

                                specs.get(i).setSpecNumber(newPosition);
                                specListRef.child(specKeys.get(specs.get(i))).child("specNumber").setValue(newPosition);
                            }

                            specListRef.child(specKeys.get(specs.get(position))).removeValue();
                            specKeys.remove(specs.get(position));
                            specs.remove(position);

                            mAdapter.notifyDataSetChanged();
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
    public void onClick(Specification specification) {
        Intent intent = new Intent(getActivity(), AVESpecificationActivity.class);
        intent.putExtra("projectKey", getActivity().getIntent().getStringExtra("projectKey"));
        intent.putExtra("spec", specification);
        intent.putExtra("specKey", specKeys.get(specification));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}
