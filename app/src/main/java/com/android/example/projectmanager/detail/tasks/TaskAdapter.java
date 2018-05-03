package com.android.example.projectmanager.detail.tasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.util.List;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    public interface TaskAdapterOnClickHandler {
        void onClick(Task task);
        void onChecked(Task task, int position, boolean checked);
    }

    private final TaskAdapterOnClickHandler mClickHandler;

    public List<Task> taskList;

    private Context mContext;

    public TaskAdapter(Context context, TaskAdapterOnClickHandler clickHandler, List<Task> taskList) {
        mContext = context;
        mClickHandler = clickHandler;
        this.taskList = taskList;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutIdForListItem = R.layout.list_item_task;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskNumber.setText(mContext.getString(R.string.label_task_number) + " " + (task.getTaskNumber() + 1));
        holder.checkBox.setChecked(task.isCompleted());
    }

    @Override
    public int getItemCount() {
        if(taskList!=null){
            return taskList.size();
        } else {
            return 0;
        }
    }

    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView taskNumber;
        public TextView taskName;

        public CheckBox checkBox;

        public TaskHolder(View itemView) {
            super(itemView);

            taskNumber = itemView.findViewById(R.id.tv_task_number);
            taskName = itemView.findViewById(R.id.et_task_description);

            checkBox = itemView.findViewById(R.id.checkbox_task_completed);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int adapterPosition = getAdapterPosition();

                    Task task = taskList.get(adapterPosition);

                    mClickHandler.onChecked(task, adapterPosition, b);
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            Task task = taskList.get(adapterPosition);

            mClickHandler.onClick(task);
        }
    }

}
