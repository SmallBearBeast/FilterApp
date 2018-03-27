package com.example.jason.heartratedetection.ui.widget.solve;

/**
 * Created by Administrator on 2018/3/17.
 */

public interface OnSolveListener {
    void complete();
    void cancel();
    void solveStart();
    void solveEnd();
    void reset();
}
