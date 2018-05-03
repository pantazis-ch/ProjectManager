package com.android.example.projectmanager.detail.notes;


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

import com.android.example.projectmanager.detail.notes.avenote.AVENoteActivity;
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


public class NoteListFragment extends Fragment implements NoteAdapter.NoteAdapterOnClickHandler {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Project project;
    private String projectKey;

    private FirebaseDatabase database;
    private DatabaseReference noteListRef;

    private Query projectNotesQuery;

    private String user;

    private RecyclerView mRecyclerView;
    private NoteAdapter mAdapter;
    private NoteAdapter.NoteAdapterOnClickHandler clickHandler;

    private ArrayList<Note> notes;
    private HashMap<Note,String> noteKeys;

    private boolean delete;


    public static NoteListFragment newInstance() {
        NoteListFragment fragment = new NoteListFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    public NoteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getUid();

        projectKey = getActivity().getIntent().getStringExtra("projectKey");

        notes = new ArrayList<>();
        noteKeys = new HashMap<>();

        clickHandler = this;

        mRecyclerView = getView().findViewById(R.id.rv_note_list);

        mAdapter = new NoteAdapter(clickHandler, notes);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mRecyclerView.setHasFixedSize(false);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_add_new_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AVENoteActivity.class);
                intent.putExtra("projectKey", projectKey
                );
                if(mAdapter != null && mAdapter.getItemCount() !=0 ){
                    intent.putExtra("noteNumber", mAdapter.getItemCount());
                }
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        database = FirebaseDatabase.getInstance();
        noteListRef = database.getReference(user).child("notes");

        projectNotesQuery = noteListRef.orderByChild("projectKey").equalTo(getActivity().getIntent().getStringExtra("projectKey"));

        projectNotesQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Note note = dataSnapshot.getValue(Note.class);

                notes.add(note);

                Collections.sort(notes, new Comparator<Note>() {
                    @Override
                    public int compare(Note n1, Note n2) {
                        return n1.getNoteNumber() - n2.getNoteNumber();
                    }
                });

                mAdapter.notifyItemInserted(mAdapter.getItemCount());

                noteKeys.put(note, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Note note = dataSnapshot.getValue(Note.class);

                int index = note.getNoteNumber();

                boolean notifyAdapter = false;
                if(!notes.get(index).getNoteName().equals(note.getNoteName())) {
                    notes.get(index).setNoteName(note.getNoteName());
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

                reArrange(mAdapter.noteList, fromPosition, toPosition);

                for (int i = 0; i<mAdapter.noteList.size(); i++) {
                    String key = noteKeys.get((Note) mAdapter.noteList.get(i));
                    noteListRef.child(key).child("noteNumber").setValue(i);
                }

                mAdapter.notifyItemMoved(fromPosition,toPosition);

                return true;
            }

            private void reArrange(List<Note> list, int fromPosition, int toPosition) {
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
                            for (int i = position + 1; i < notes.size(); i++) {
                                Note note = notes.get(i);
                                int oldPosition = note.getNoteNumber();
                                int newPosition = oldPosition - 1;

                                notes.get(i).setNoteNumber(newPosition);
                                noteListRef.child(noteKeys.get(notes.get(i))).child("noteNumber").setValue(newPosition);
                            }

                            noteListRef.child(noteKeys.get(notes.get(position))).removeValue();
                            noteKeys.remove(notes.get(position));
                            notes.remove(position);

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
    public void onClick(Note note) {
        Intent intent = new Intent(getContext(), AVENoteActivity.class);
        intent.putExtra("projectKey", projectKey);
        intent.putExtra("note", note);
        intent.putExtra("noteKey", noteKeys.get(note));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

}
