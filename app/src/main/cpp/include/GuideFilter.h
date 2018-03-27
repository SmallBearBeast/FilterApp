#ifndef GUIDE_FILTER_H
#define GUIDE_FILTER_H

#include <opencv2/opencv.hpp>
#include <iostream>
#include <fstream>
using namespace cv;
using namespace std;

Mat boxfilter(Mat& src, int r);
Mat scaleMat(Mat mat, double scale);
Mat cumsum(Mat& src, int rc);
Mat guidefilter(Mat& I, Mat& p, int r, double eps);
Mat EnhanceBrightness(Mat& I, float dx);
void printMatToFile(Mat& mat, char * p);
void test();
double getCount();

#endif // !GUIDE_FILTER_H

#pragma once
