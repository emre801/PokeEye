package com.johnerdo.imageCompare;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.omg.CORBA.Environment;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class Square {

	public static void getSquare(Mat imgSource) {
		Mat sourceImage = imgSource.clone();
		Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);
		// convert the image to black and white does (8 bit)
		Imgproc.Canny(imgSource, imgSource, 50, 50);

		// apply gaussian blur to smoothen lines of dots
		Imgproc.GaussianBlur(imgSource, imgSource, new org.opencv.core.Size(5,
				5), 5);

		// find the contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		int maxAreaIdx = -1;
		// Log.d("size",Integer.toString(contours.size()));
		MatOfPoint temp_contour = contours.get(0); // the largest is at the
													// index 0 for starting
													// point
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		MatOfPoint largest_contour = contours.get(0);
		// largest_contour.ge
		List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
		// Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0),
		// 1);

		for (int idx = 0; idx < contours.size(); idx++) {
			temp_contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(temp_contour);
			// compare this contour to the previous largest contour found
			if (contourarea > maxArea) {
				// check if this contour is a square
				MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
				int contourSize = (int) temp_contour.total();
				MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
				Imgproc.approxPolyDP(new_mat, approxCurve_temp,
						contourSize * 0.05, true);
				if (approxCurve_temp.total() == 4) {
					maxArea = contourarea;
					maxAreaIdx = idx;
					approxCurve = approxCurve_temp;
					largest_contour = temp_contour;
				}
			}
		}

		Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);

		double[] temp_double;
		temp_double = approxCurve.get(0, 0);
		Point p1 = new Point(temp_double[0], temp_double[1]);
		// Core.circle(imgSource,p1,55,new Scalar(0,0,255));
		// Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
		temp_double = approxCurve.get(1, 0);
		Point p2 = new Point(temp_double[0], temp_double[1]);
		// Core.circle(imgSource,p2,150,new Scalar(255,255,255));
		temp_double = approxCurve.get(2, 0);
		Point p3 = new Point(temp_double[0], temp_double[1]);
		// Core.circle(imgSource,p3,200,new Scalar(255,0,0));
		temp_double = approxCurve.get(3, 0);
		Point p4 = new Point(temp_double[0], temp_double[1]);
		// Core.circle(imgSource,p4,100,new Scalar(0,0,255));
		List<Point> source = getCorners(p1,p2,p3,p4);
		for(Point p: source){
			System.out.println(p);
		}
		Mat startM = Converters.vector_Point2f_to_Mat(source);
		//Imgproc.cvtColor(sourceImage, sourceImage, Imgproc.COLOR_BGR2GRAY);
		Mat result = warp(sourceImage, startM, 5);
		//result = warp(result,result,1);
		// Imgproc.cvtColor(result, result, Imgproc.COLOR_BGR2GRAY);
		Highgui.imwrite(MatchingMethod.screenInfoScreen + "Test1.png", result);
		System.out.println("Done");
		// return result;
	}

	public static List<Point> getCorners(Point p1, Point p2, Point p3, Point p4) {
		
		Point midPoint = new Point();
		midPoint.x = (p1.x + p2.x + p3.x + p4.x)/4d; 
		midPoint.y = (p1.y + p2.y + p3.y + p4.y)/4d;
		Point tL = null,tR = null,bL = null,bR = null;
		
		List<Point> source = new ArrayList<Point>();
		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4);
		
		for(Point p:source){
			if(p.x < midPoint.x){
				if(p.y < midPoint.y)
					bL = p;
				else
					tL = p;
			}else{
				if(p.y < midPoint.y)
					bR = p;
				else
					tR = p;
			}
		}
		
		
		List<Point> result = new ArrayList<Point>();
		result.add(bL);
		result.add(tL);
		result.add(tR);
		result.add(bR);
		return result;
	}

	private static Mat findLargestRectangle(Mat original_image) {
		Mat imgSource = original_image.clone();

		// convert the image to black and white
		Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BGR2GRAY);

		// convert the image to black and white does (8 bit)
		Imgproc.Canny(imgSource, imgSource, 50, 50);

		// apply gaussian blur to smoothen lines of dots
		Imgproc.GaussianBlur(imgSource, imgSource, new Size(5, 5), 5);

		// find the contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = contours.get(0); // the largest is at the
													// index 0 for starting
													// point
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
		for (int idx = 0; idx < contours.size(); idx++) {
			temp_contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(temp_contour);
			// compare this contour to the previous largest contour found
			if (contourarea > maxArea) {
				// check if this contour is a square
				MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
				int contourSize = (int) temp_contour.total();
				Imgproc.approxPolyDP(new_mat, approxCurve, contourSize * 0.05,
						true);
				if (approxCurve.total() == 4) {
					maxArea = contourarea;
					largest_contours.add(temp_contour);
				}
			}
		}
		MatOfPoint temp_largest = largest_contours
				.get(largest_contours.size() - 1);
		largest_contours = new ArrayList<MatOfPoint>();

		largest_contours.add(temp_largest);

		// Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
		Imgproc.drawContours(original_image, largest_contours, -1, new Scalar(
				0, 255, 0), 10);

		// Mat perspectiveTransform = new Mat(3, 3, CvType.CV_32FC1);
		// Imgproc.warpPerspective(original_image, imgSource,
		// perspectiveTransform, new Size(300,300));

		Highgui.imwrite(MatchingMethod.screenInfoScreen + "Test1.png",
				original_image);

		// create the new image here using the largest detected square

		// Toast.makeText(getApplicationContext(), "Largest Contour: ",
		// Toast.LENGTH_LONG).show();

		return imgSource;
	}

	public static Mat warp(Mat inputMat, Mat startM, int factor) {
		
		int resultWidth = 400 * factor;
		int resultHeight = 240 * factor;

		Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

		Point ocvPOut1 = new Point(0, 0);
		Point ocvPOut2 = new Point(0, resultHeight);
		Point ocvPOut3 = new Point(resultWidth, resultHeight);
		Point ocvPOut4 = new Point(resultWidth, 0);
		List<Point> dest = new ArrayList<Point>();
		dest.add(ocvPOut1);
		dest.add(ocvPOut2);
		dest.add(ocvPOut3);
		dest.add(ocvPOut4);
		Mat endM = Converters.vector_Point2f_to_Mat(dest);

		Mat perspectiveTransform = Imgproc
				.getPerspectiveTransform(startM, endM);

		Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform,
				new Size(resultWidth, resultHeight), Imgproc.INTER_AREA);
		Imgproc.GaussianBlur(outputMat, outputMat, new org.opencv.core.Size(5,
				5), 5);
		Imgproc.resize(outputMat, outputMat, new Size(resultWidth/factor,resultHeight/factor) );
		
		Imgproc.threshold(outputMat,outputMat,127,255,Imgproc.THRESH_TOZERO);
		return outputMat;
	}
	

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println(MatchingMethod.screenInfoScreen + "Test.png");
		Mat src = Highgui.imread(MatchingMethod.screenInfoScreen + "Test.png");
		getSquare(src);
		// findLargestRectangle(src);
		// getSquare(Highgui.imread(MatchingMethod.screenInfoScreen+
		// "Test.png"));
	}

}
