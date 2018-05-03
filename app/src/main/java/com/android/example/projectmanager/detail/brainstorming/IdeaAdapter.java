package com.android.example.projectmanager.detail.brainstorming;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.util.List;


public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.IdeaHolder> {

    public interface IdeaAdapterOnClickHandler {
        void onClick(Idea idea);
    }

    private final IdeaAdapterOnClickHandler mClickHandler;

    public List<Idea> ideaList;

    private Context context;

    int[] darkColors;

    public IdeaAdapter(IdeaAdapterOnClickHandler clickHandler, List<Idea> ideaList, Context context) {
        mClickHandler = clickHandler;
        this.ideaList = ideaList;
        this.context = context;

        TypedArray tadc = context.getResources().obtainTypedArray(R.array.darkColors);
        darkColors = new int[tadc.length()];
        for (int i = 0; i < tadc.length(); i++) {
            darkColors[i] = tadc.getColor(i, 0);
        }
        tadc.recycle();
    }

    @Override
    public IdeaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForListItem = R.layout.list_item_idea;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new IdeaHolder(view);
    }

    @Override
    public void onBindViewHolder(IdeaHolder holder, int position) {
        Idea idea = ideaList.get(position);
        holder.idea.setText(idea.getIdea());

        String colorStr = idea.getBackgroundColor();
        String c[] = colorStr.split(" ");

        int colorCategory = Integer.valueOf(c[0]);
        int colorIndex = Integer.valueOf(c[1]);

        if(colorCategory == 0) {
            holder.ideaContainer.setBackgroundColor(darkColors[colorIndex]);
        } else {
            holder.idea.setTextColor(context.getResources().getColor(R.color.color2));
        }
    }

    @Override
    public int getItemCount() {
        if(ideaList != null) {
            return ideaList.size();
        } else {
            return 0;
        }
    }

    public class IdeaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView idea;
        FrameLayout ideaContainer;

        public IdeaHolder(View itemView) {
            super(itemView);

            idea = itemView.findViewById(R.id.tv_idea);
            ideaContainer = itemView.findViewById(R.id.container_idea);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Idea idea = ideaList.get(adapterPosition);

            mClickHandler.onClick(idea);
        }
    }

}
