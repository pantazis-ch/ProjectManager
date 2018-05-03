package com.android.example.projectmanager.main.projects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.example.projectmanager.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.ProjectHolder> {

    public interface ProjectAdapterOnClickHandler {
        void onClick(Project project, String action);
    }

    private final ProjectAdapterOnClickHandler mClickHandler;

    private ArrayList<Project> projectList;
    private Context mContext;

    public ProjectRecyclerAdapter(Context context, ProjectAdapterOnClickHandler clickHandler, ArrayList<Project> projectList) {
        mContext = context;
        mClickHandler = clickHandler;
        this.projectList = projectList;
    }

    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_project, parent, false);

        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectHolder holder, int position) {
        Project project = projectList.get(position);

        holder.projectName.setText(project.getName());
        holder.projectTag.setText(project.getTag());

        String deadlineStr = project.getDeadline();

        if(deadlineStr.equals("n / a")) {
            if(project.getStatus()) {
                holder.projectStatus.setText(mContext.getString(R.string.label_completed));
                holder.projectStatus.setBackgroundColor(mContext.getResources().getColor(R.color.color5));
                holder.projectStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            } else {
                holder.projectStatus.setText(mContext.getString(R.string.label_in_progress));
                holder.projectStatus.setBackgroundColor(mContext.getResources().getColor(R.color.color6));
                holder.projectStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }

        } else {
            if(project.getStatus()) {
                holder.projectStatus.setText(mContext.getString(R.string.label_completed));
                holder.projectStatus.setBackgroundColor(mContext.getResources().getColor(R.color.color5));
                holder.projectStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            } else {
                long currentTime = System.currentTimeMillis();

                SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy - hh : mm");
                try {

                    Date date1 = sdf.parse(project.getDeadline());
                    Date date2 = new Date(currentTime);

                    int res = date1.compareTo(date2);
                    if(res == -1) {
                        holder.projectStatus.setBackgroundColor(mContext.getResources().getColor(R.color.color4));
                        holder.projectStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                        holder.projectStatus.setText(R.string.label_expired);
                    } else {
                        holder.projectStatus.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                        holder.projectStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                        holder.projectStatus.setText(project.getDate() + " \n " + project.getTime());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(projectList != null) {
            return projectList.size();
        } else {
            return 0;
        }
    }

    public class ProjectHolder extends RecyclerView.ViewHolder {

        private TextView projectName;
        private TextView projectTag;
        private TextView projectStatus;

        private FrameLayout projectOverviewButton;

        public ProjectHolder(View itemView) {
            super(itemView);

            projectName = itemView.findViewById(R.id.tv_project_list_item_name);
            projectTag = itemView.findViewById(R.id.tv_project_list_item_tag);
            projectStatus = itemView.findViewById(R.id.tv_project_status);
            projectOverviewButton = itemView.findViewById(R.id.btn_open_overview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();

                    Project project = projectList.get(adapterPosition);

                    mClickHandler.onClick(project, "detail");
                }
            });

            projectOverviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();

                    Project project = projectList.get(adapterPosition);

                    mClickHandler.onClick(project, "overview");
                }
            });
        }
    }
}
