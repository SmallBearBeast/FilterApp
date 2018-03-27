package com.example.jason.heartratedetection.domain;

/**
 * Created by Administrator on 2018/3/12.
 */

public class SolveCase {
    private String title;

    private int icon;

    public SolveCase(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
