package com.example.test;

import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by wangzhongdi on 2017/6/26.
 */

public class OpenCVUtils {
    private static final String TAG = MainActivity.TAG;//"OpenCVUtils";
    private OpenCVUtils(){}

    static {
        System.loadLibrary("opencv_java3");
    }

    public static boolean init() {
        return OpenCVLoader.initDebug();
    }

    /**
     * Find all matches using template
     * @param src
     * @param sch
     * @param threadshold
     * @param maxCount
     * @param bgRemove
     * @return list of (match.x, match.y, match.confidence)
     */
    public static ArrayList<Point3> findAllTemplate(Mat src, Mat sch, double threadshold, int maxCount, boolean bgRemove) {
        final int procMethod = Imgproc.TM_CCOEFF_NORMED;
        Mat source;
        Mat search;
        Mat srcGray = new Mat();
        Mat schGray = new Mat();
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(sch, schGray, Imgproc.COLOR_BGR2GRAY);
        if(bgRemove) {
            // 边界提取(来实现背景去除的功能)
            source = new Mat();
            Imgproc.Canny(srcGray, source, 100, 200);
            search = new Mat();
            Imgproc.Canny(schGray, search, 100, 200);
        } else {
            source = srcGray;
            search = schGray;
        }

        Mat result = new Mat();
        Imgproc.matchTemplate(source, search, result, procMethod);

        ArrayList<Point3> matchList = new ArrayList<>();
        Scalar coverScalar = new Scalar(0, 0, 0);
        while(true) {
            final Core.MinMaxLocResult minMax = Core.minMaxLoc(result);
            if (minMax.maxVal < threadshold) {
                break;
            }

            Point topLeft; // current best match
            if (procMethod == Imgproc.TM_SQDIFF || procMethod == Imgproc.TM_SQDIFF_NORMED) {
                topLeft = minMax.minLoc;
            } else {
                topLeft = minMax.maxLoc;
            }
            matchList.add(new Point3(topLeft.x, topLeft.y, minMax.maxVal));
            Log.d(TAG, "match: " + topLeft + " - " + new Point(topLeft.x + sch.cols(), topLeft.y + sch.rows()));
            if(matchList.size() >= maxCount)
                break;
            // 遮掉当前最匹配的, 继续查找下一个匹配
            Imgproc.rectangle(result, topLeft, new Point(topLeft.x+search.cols(), topLeft.y+search.rows()), coverScalar, -1);
        }

        if(matchList.size() == 0)
            Log.d(TAG, "no best match: threadshold=" + threadshold);
        return matchList;
    }

    /**
     * Mark all matches using red rectangle
     * @param source
     * @param search
     * @param matchList
     */
    public static void markMatches(Mat source, Mat search, ArrayList<Point3> matchList) {
        final Scalar lineColor = new Scalar(255, 0, 0);
        Point topLeft = new Point();
        Point bottomRight = new Point();
        for(Point3 p3 : matchList) {
            topLeft.x = p3.x;
            topLeft.y = p3.y;
            bottomRight.x = topLeft.x + search.cols();
            bottomRight.y = topLeft.y + search.rows();
            Imgproc.rectangle(source, topLeft, bottomRight, lineColor, 5);
        }
    }
}
