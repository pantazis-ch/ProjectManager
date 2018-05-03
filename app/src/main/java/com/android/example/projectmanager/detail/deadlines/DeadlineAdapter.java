package com.android.example.projectmanager.detail.deadlines;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.util.List;

public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.DeadlineHolder> {

    public interface DeadlineAdapterOnClickHandler {
        void onClick(Deadline deadline, Boolean isLastDeadline);
    }

    private final DeadlineAdapterOnClickHandler mClickHandler;

    private List<Deadline> deadlineList;

    public DeadlineAdapter(DeadlineAdapterOnClickHandler clickHandler, List<Deadline> deadlineList) {
        mClickHandler = clickHandler;
        this.deadlineList = deadlineList;
    }

    @Override
    public DeadlineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_deadline, parent, false);

        return new DeadlineHolder(view);
    }

    @Override
    public void onBindViewHolder(DeadlineHolder holder, int position) {
        Deadline deadline = deadlineList.get(position);
        holder.deadline.setText(deadline.getDate() + "   " + deadline.getTime());
        holder.deadlineTitle.setText(deadline.getDeadlineTitle());

        if(deadline.getNotificationId() != -1) {
            holder.imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if(deadlineList != null) {
            return deadlineList.size();
        } else {
            return 0;
        }
    }

    public class DeadlineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView deadlineTitle;
        TextView deadline;

        ImageView imageView;

        public DeadlineHolder(View itemView) {
            super(itemView);

            deadline = itemView.findViewById(R.id.tv_deadline);
            deadlineTitle = itemView.findViewById(R.id.tv_deadline_title);
            imageView = itemView.findViewById(R.id.ic_notification_enabled);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Deadline deadline = deadlineList.get(adapterPosition);

            Boolean isLastDeadline = false;
            if(adapterPosition == deadlineList.size() - 1) {
                isLastDeadline = true;
            }

            mClickHandler.onClick(deadline, isLastDeadline);
        }
    }

}
