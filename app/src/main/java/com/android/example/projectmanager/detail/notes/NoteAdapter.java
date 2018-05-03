package com.android.example.projectmanager.detail.notes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.util.List;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    public interface NoteAdapterOnClickHandler {
        void onClick(Note task);
    }

    private final NoteAdapterOnClickHandler mClickHandler;

    List<Note> noteList;

    public NoteAdapter(NoteAdapterOnClickHandler clickHandler, List<Note> noteList) {
        mClickHandler = clickHandler;
        this.noteList = noteList;
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForListItem = R.layout.list_item_note;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteName.setText(note.getNoteName());
    }

    @Override
    public int getItemCount() {
        if(noteList != null) {
            return noteList.size();
        } else {
            return 0;
        }
    }

    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView noteName;

        public NoteHolder(View itemView) {
            super(itemView);

            noteName = itemView.findViewById(R.id.tv_note);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Note note = noteList.get(adapterPosition);

            mClickHandler.onClick(note);
        }
    }

}
