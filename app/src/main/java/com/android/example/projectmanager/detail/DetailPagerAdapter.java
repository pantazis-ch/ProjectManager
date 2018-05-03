package com.android.example.projectmanager.detail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.example.projectmanager.detail.brainstorming.IdeaListFragment;
import com.android.example.projectmanager.detail.deadlines.DeadlineListFragment;
import com.android.example.projectmanager.detail.notes.NoteListFragment;
import com.android.example.projectmanager.detail.specifications.SpecificationListFragment;
import com.android.example.projectmanager.detail.tasks.TaskListFragment;
import com.android.example.projectmanager.main.projects.Project;
import com.android.example.projectmanager.R;


public class DetailPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    private Project project;
    private String projectKey;

    public DetailPagerAdapter(FragmentManager fm, Context context, Project project, String projectKey) {
        super(fm);
        this.mContext = context;
        this.project = project;
        this.projectKey = projectKey;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            return new SpecificationListFragment();
        } else if (position == 1) {
            return IdeaListFragment.newInstance(project, projectKey);
        } else if (position == 2){
            return TaskListFragment.newInstance(project, projectKey);
        } else if (position == 3) {
            return new DeadlineListFragment();
        } else {
            return new NoteListFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.detail_pager_specs_title);
            case 1:
                return mContext.getString(R.string.detail_pager_brainstorming_title);
            case 2:
                return mContext.getString(R.string.detail_pager_tasks_title);
            case 3:
                return mContext.getString(R.string.detail_pager_deadlines_title);
            case 4:
                return mContext.getString(R.string.detail_pager_notes_title);
            default:
                return mContext.getString(R.string.detail_pager_error_title);
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
