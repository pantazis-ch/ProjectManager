package com.android.example.projectmanager.main.projects;

import android.os.Parcel;
import android.os.Parcelable;

public class Project implements Parcelable {

    private String name;
    private String tag;
    private String deadline;
    private String description;
    private boolean status;

    private String dateInfo[];

    public Project(){

    }

    public Project(String name, String tag, String deadline, String description, boolean status) {
        this.name = name;
        this.tag = tag;
        this.deadline = deadline;
        this.description = description;
        this.status = status;
    }

    protected Project(Parcel in) {
        name = in.readString();
        tag = in.readString();
        deadline = in.readString();
        description = in.readString();
        status = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(tag);
        parcel.writeString(deadline);
        parcel.writeString(description);
        parcel.writeByte((byte) (status ? 1 : 0));
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDate() {
        if(deadline.equals("n / a")) {
            return "n / a";
        } else {
            if(dateInfo == null) {
                dateInfo = deadline.split(" - ");
            }
            return dateInfo[0];
        }
    }

    public String getTime() {
        if(deadline.equals("n / a")) {
            return "n / a";
        } else {
            if(dateInfo == null) {
                dateInfo = deadline.split(" - ");
            }
            return dateInfo[1];
        }
    }

}
