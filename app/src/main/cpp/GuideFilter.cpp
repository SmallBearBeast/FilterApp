#include <GuideFilter.h>

void test() {
    Mat mat = imread("C:/Users/Administrator/Desktop/my_try_simple/data/outdoor8.jpg", CV_LOAD_IMAGE_COLOR);
    if (mat.empty()) {
        return;
    }
    mat = scaleMat(mat, 0.4);
    Mat output = EnhanceBrightness(mat, 0.5f);
}

double getCount() {
	double t = (double)getTickCount();
	t = ((double)getTickCount() - t) / getTickFrequency();
	return t;
}
Mat scaleMat(Mat mat, double scale) {
	Size size;
	Mat resizeMat;
	size.width = mat.cols * scale;
	size.height = mat.rows * scale;
	resize(mat, resizeMat, size, 0, 0, INTER_AREA);
	return resizeMat;
}

Mat cumsum(Mat& src, int rc) {
    int cols = src.cols;
    int rows = src.rows;
    Mat mat = Mat::zeros(rows, cols, CV_64FC1);
    double start = 0;
    if (rc == 1) {
        for (int i = 0; i < cols; i++) {
            start = 0;
            for (int j = 0; j < rows; j++) {
                start = start + src.at<double>(j, i);
                mat.at<double>(j, i) = start;
            }
        }
    }
    else if (rc == 2) {
        for (int i = 0; i < rows; i++) {
            start = 0;
            for (int j = 0; j < cols; j++) {
                start = start + src.at<double>(i, j);
                mat.at<double>(i, j) = start;
            }
        }
    }
    return mat;
}

Mat boxfilter(Mat& src, int r) {
	int cols = src.cols;
	int rows = src.rows;
	Mat dest = Mat::zeros(rows, cols, CV_64FC1);
	Mat temp = cumsum(src, 1);
	//printMatToFile(temp);
	int i = 0;
	int j = 0;
	for (; i < r + 1; i++) {
		for (j = 0; j < cols; j++) {
			dest.at<double>(i, j) = temp.at<double>(i + r, j);
		}
	}
	//printMatToFile(dest);
	for (; i < rows - r; i++){
		for (j = 0; j < cols; j++) {
			dest.at<double>(i, j) = temp.at<double>(i + r, j) - temp.at<double>(i - r - 1, j);
		}
	}
	//printMatToFile(dest);
	for (; i < rows; i++) {
		for (j = 0; j < cols; j++) {
			dest.at<double>(i, j) = temp.at<double>(rows - 1, j) - temp.at<double>(i - r - 1, j);
		}
	}
	//printMatToFile(dest);
	temp = cumsum(dest, 2);
	//printMatToFile(temp);
	for (i = 0; i < rows; i++) {
		for (j = 0; j < r + 1; j++) {
			dest.at<double>(i, j) = temp.at<double>(i , j + r);
		}
	}
	//printMatToFile(dest);
	for (i = 0; i < rows; i++) {
		for (j = r + 1; j < cols - r; j++) {
			dest.at<double>(i, j) = temp.at<double>(i, j + r) - temp.at<double>(i, j - r - 1);
		}
	}
	//printMatToFile(dest);
	for (i = 0; i < rows; i++) {
		for (j = cols - r; j < cols; j++) {
			dest.at<double>(i, j) = temp.at<double>(i, cols - 1) - temp.at<double>(i, j - r - 1);
		}
	}
	//printMatToFile(dest);
	return dest;
}
Mat guidefilter(Mat& I, Mat& P, int r, double eps) {
    int rows = I.rows;
    int cols = I.cols;
    Mat n = Mat::ones(rows, cols, CV_64FC1);
    n = boxfilter(n, r);
    Mat meanI = boxfilter(I, r) / n;
    Mat meanP = boxfilter(P, r) / n;
    Mat meanIP = I.mul(P);
    meanIP = boxfilter(meanIP, r) / n;
    Mat covIP = meanIP - meanI.mul(meanP);
    Mat meanII = I.mul(I);
    meanII = boxfilter(meanII, r) / n;
    Mat varI = meanII - meanI.mul(meanI);
    Mat a = covIP / (varI + eps);
    Mat b = meanP - a.mul(meanI);
    Mat meanA = boxfilter(a, r) / n;;
    Mat meanB = boxfilter(b, r) / n;;
    return meanA.mul(I)+ meanB;
}

Mat EnhanceBrightness(Mat& I, float dx) {
    int r = I.rows;
    if (I.rows > I.cols) {
        r = I.cols;
    }
    r = r / 10;
    Mat rgbMat = I;
    if (rgbMat.empty()) {
        cout << "read image failed" << endl;
        return rgbMat;
    }
    Mat hsvMat;
    Mat vMat;
    Mat maskMat;
    Mat hsvChannels[3];
    cvtColor(rgbMat, hsvMat, CV_RGB2HSV);
    split(hsvMat, hsvChannels);
    for (int i = 0; i < 3; i++) {
        hsvChannels[i].convertTo(hsvChannels[i], CV_64FC1, 1.0 / 255);
    }
    vMat = hsvChannels[2];
    maskMat = vMat > dx;
    Mat erodeElement = getStructuringElement(MORPH_RECT, Size(5, 5));
    erode(maskMat, maskMat, erodeElement);
    Mat dilateElement = getStructuringElement(MORPH_RECT, Size(5, 5));
    dilate(maskMat, maskMat, dilateElement);
    maskMat = 1 - maskMat;
    maskMat.convertTo(maskMat, CV_64FC1);
    Mat refinedMaskMat = guidefilter(vMat, maskMat, r, 0.000001);

    Mat vGuideMat = guidefilter(vMat, vMat, r / 2, 0.001);
    Mat vRecoverMat = vMat / (vGuideMat + 0.3);
    Mat rgbRecoverMat;
    Mat hsvRecoverMat;
    hsvChannels[2] = vRecoverMat;
    for (int i = 0; i < 3; i++) {
        hsvChannels[i].convertTo(hsvChannels[i], CV_8UC1, 255);
    }
    merge(hsvChannels, 3, hsvRecoverMat);
    cvtColor(hsvRecoverMat, rgbRecoverMat, CV_HSV2RGB);

    rgbMat.convertTo(rgbMat, CV_64FC1, 1.0 / 255);
    rgbRecoverMat.convertTo(rgbRecoverMat, CV_64FC1, 1.0 / 255);
    Mat refinedMaskChannels[3];
    for (int i = 0; i < 3; i++) {
        refinedMaskChannels[i] = refinedMaskMat;
    }
    Mat refinedMat;
    merge(refinedMaskChannels, 3, refinedMat);
    Mat outputMat ;
    subtract(1, refinedMat, outputMat);
    outputMat = rgbMat.mul(outputMat) + rgbRecoverMat.mul(refinedMat);
    return outputMat;
}

void printMatToFile(Mat& mat, char * p) {
    ofstream of(p);
    if (!of.is_open()) {
        cout << "open file error!" << endl;
        return;
    }

    int rows = mat.rows;
    int cols = mat.cols;
    int a = 0, b = 0, c = 0;
    if(mat.channels() == 1){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a = mat.at<uchar>(i, j);
                of << a << " ";
            }
            of << endl;
        }
    }
    else if (mat.channels() == 3) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a = mat.at<Vec3b>(i, j)[0];
                b = mat.at<Vec3b>(i, j)[1];
                c = mat.at<Vec3b>(i, j)[2];
                of << a << "-" << b << "-" << c << " ";
            }
            of << endl;
        }
    }
    of.close();
}

