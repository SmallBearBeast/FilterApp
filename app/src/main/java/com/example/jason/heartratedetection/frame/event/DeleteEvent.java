package com.example.jason.heartratedetection.frame.event;

/**
 * Created by Administrator on 2018/3/25.
 */

public class DeleteEvent {
    int state;

    Object obj;

    public DeleteEvent(int state, Object obj) {
        this.state = state;
        this.obj = obj;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
