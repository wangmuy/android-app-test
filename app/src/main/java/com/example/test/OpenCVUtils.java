package com.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
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

    public static ArrayList<Point3> findAll(String srcPath, String schPath, double threadshold, int maxCount, boolean bgRemove) {
        final Mat srcImg = Imgcodecs.imread(srcPath);
        final Mat schImg = Imgcodecs.imread(schPath);
        ArrayList<Point3> matches = OpenCVUtils.findAllTemplate(srcImg, schImg, threadshold, maxCount, true);
        return matches;
    }

    /**
     * Find all matches
     * @param src
     * @param sch
     * @param threadshold
     * @param maxCount
     * @param bgRemove
     * @return list of (match.x, match.y, match.confidence)
     */
    public static ArrayList<Point3> findAll(Mat src, Mat sch, double threadshold, int maxCount, boolean bgRemove) {
        return findAllTemplate(src, sch, threadshold, maxCount, bgRemove);
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
        final Scalar coverScalar = new Scalar(0);
        final Scalar upDiff = new Scalar(1.0);
        Mat mask = new Mat();
        while(true) {
            final Core.MinMaxLocResult minMax = Core.minMaxLoc(result);
            if (minMax.maxVal < threadshold) {
                Log.d(TAG, "break because maxVal<threadshold, maxVal="+minMax.maxVal);
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
            if(matchList.size() >= maxCount) {
                Log.d(TAG, "break because maxCount reached");
                break;
            }
            // 遮掉当前最匹配的, 继续查找下一个匹配
            Imgproc.floodFill(result, mask, topLeft, coverScalar, null,
                    new Scalar(minMax.maxVal-threadshold+0.1), upDiff, Imgproc.FLOODFILL_FIXED_RANGE);
        }

        if(matchList.size() == 0)
            Log.d(TAG, "no match: threadshold=" + threadshold);
        else
            Log.d(TAG, "total matches "+matchList.size()+" for threadshold "+threadshold);
        return matchList;
    }

    /**
     * Mark all matches and save to Bitmap
     */
    public static Bitmap markMatchesToBitmap(Mat source, Mat search, ArrayList<Point3> matchList) {
        markMatches(source, search ,matchList);
        Imgproc.cvtColor(source, source, Imgproc.COLOR_RGB2BGR);
        Bitmap mark = Bitmap.createBitmap(source.cols(), source.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(source, mark);
        return mark;
    }

    /**
     * Mark all matches using red rectangle
     * @param source
     * @param search
     * @param matchList
     */
    public static void markMatches(Mat source, Mat search, ArrayList<Point3> matchList) {
        final Scalar lineColor = new Scalar(0, 0, 255); // blue for RGB2BGR convert
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
