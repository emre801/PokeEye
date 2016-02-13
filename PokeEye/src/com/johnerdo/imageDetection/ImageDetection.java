package com.johnerdo.imageDetection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import com.johnerdo.imageCompare.MatchingMethod;

public class ImageDetection {

	public static Mat getSqaureImage(String imgSourceLocation){
		 Mat imgSource =Highgui.imread(imgSourceLocation);
		//convert the image to black and white does (8 bit)
	    Imgproc.Canny(imgSource, imgSource, 50, 50);

	    //apply gaussian blur to smoothen lines of dots
	    Imgproc.GaussianBlur(imgSource, imgSource, new  org.opencv.core.Size(5, 5), 5);

	    //find the contours
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

	    double maxArea = -1;
	    int maxAreaIdx = -1;
	    //Log.d("size",Integer.toString(contours.size()));
	    MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
	    MatOfPoint2f approxCurve = new MatOfPoint2f();
	    MatOfPoint largest_contour = contours.get(0);
	    //largest_contour.ge
	    List<MatOfPoint> largest_contours = new ArrayList<MatOfPoint>();
	    //Imgproc.drawContours(imgSource,contours, -1, new Scalar(0, 255, 0), 1);

	    for (int idx = 0; idx < contours.size(); idx++) {
	        temp_contour = contours.get(idx);
	        double contourarea = Imgproc.contourArea(temp_contour);
	        //compare this contour to the previous largest contour found
	        if (contourarea > maxArea) {
	            //check if this contour is a square
	            MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
	            int contourSize = (int)temp_contour.total();
	            MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
	            Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize*0.05, true);
	            if (approxCurve_temp.total() == 4) {
	                maxArea = contourarea;
	                maxAreaIdx = idx;
	                approxCurve=approxCurve_temp;
	                largest_contour = temp_contour;
	            }
	        }
	    }

	   Imgproc.cvtColor(imgSource, imgSource, Imgproc.COLOR_BayerBG2RGB);
	   Mat sourceImage =Highgui.imread(imgSourceLocation);/*Highgui.imread(Environment.getExternalStorageDirectory().
	   
	             getAbsolutePath() +"/scan/p/1.jpg");*/
	   Imgproc.GaussianBlur(sourceImage, sourceImage, new  org.opencv.core.Size(5, 5),200);
       
	   double[] temp_double;
	   temp_double = approxCurve.get(0,0);       
	   Point p1 = new Point(temp_double[0], temp_double[1]);
	   //Core.circle(imgSource,p1,55,new Scalar(0,0,255));
	   //Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
	   temp_double = approxCurve.get(1,0);       
	   Point p2 = new Point(temp_double[0], temp_double[1]);
	  // Core.circle(imgSource,p2,150,new Scalar(255,255,255));
	   temp_double = approxCurve.get(2,0);       
	   Point p3 = new Point(temp_double[0], temp_double[1]);
	   //Core.circle(imgSource,p3,200,new Scalar(255,0,0));
	   temp_double = approxCurve.get(3,0);       
	   Point p4 = new Point(temp_double[0], temp_double[1]);
	   System.out.println(p1);
	   System.out.println(p2);
	   System.out.println(p3);
	   System.out.println(p4);
	   
	  // Core.circle(imgSource,p4,100,new Scalar(0,0,255));
	   determinePoints(p1,p2,p3,p4);
	   List<Point> source = new ArrayList<Point>();
	   source.add(topLeft);
	   source.add(bottomLeft);
	   source.add(bottomRight);
	   source.add(topRight);
	   Mat startM = Converters.vector_Point2f_to_Mat(source);
	   Mat result=warp(sourceImage,startM);
	   return result;
	}
	public static Point topRight,topLeft,bottomRight,bottomLeft;
	public static void determinePoints(Point p1, Point p2,Point p3,Point p4){
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		ArrayList<Point> leftPoints = new ArrayList<Point>();
		Point pp1 = leftPoint(points);
		leftPoints.add(pp1);
		points.remove(pp1);
		Point pp2 = leftPoint(points);
		points.remove(pp2);
		Point pp3 = points.get(0);
		Point pp4 = points.get(1);
		if(pp1.y>pp2.y){
			topLeft = pp1;
			bottomLeft = pp2;
		}else{
			bottomLeft = pp1;
			topLeft = pp2;
		}
		
		if(pp3.y>pp4.y){
			topRight = pp3;
			bottomRight = pp4;
		}else{
			bottomRight = pp3;
			topRight= pp4;
		}
		
		
		
	}
	public static Point leftPoint(ArrayList<Point> leftPoint){
		Point left =leftPoint.get(0);
		for(int i =1;i<leftPoint.size();i++){
			 Point currentPoint = leftPoint.get(i);
			if(left.x > currentPoint.x){
				left = currentPoint;
			}
		}
		return left;
	}
	
	public static Mat warp(Mat inputMat,Mat startM) {
        int resultWidth = 400;
        int resultHeight = 240;

        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);



        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(0, resultHeight);
        Point ocvPOut3 = new Point(resultWidth, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, 0);
        //determinePoints(ocvPOut1,ocvPOut2,ocvPOut3,ocvPOut4);
        List<Point> dest = new ArrayList<Point>();
        dest.add(ocvPOut2);
        dest.add(ocvPOut1);
        dest.add(ocvPOut4);
        dest.add(ocvPOut3);
        
        Mat endM = Converters.vector_Point2f_to_Mat(dest);      

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
        Imgproc.warpPerspective(inputMat, 
                                outputMat,
                                perspectiveTransform,
                                new Size(resultWidth, resultHeight), 
                                Imgproc.INTER_CUBIC);
        

        return outputMat;
    }
	
	
	public static void useCameraDetection(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String cameraImage = MatchingMethod.getLatestScreenShot(MatchingMethod.cameraScreenShot);
		System.out.println(cameraImage);
		Random r = new Random();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		Highgui.imwrite(MatchingMethod.screenInfoScreenShot+"\\Facts"+r.nextInt(1000)+".png", getSqaureImage(cameraImage));
		System.out.println("------");
		System.out.println(topLeft);
		System.out.println(topRight);
		System.out.println(bottomLeft);
		System.out.println(bottomRight);
	}
	
	public static void main(String[] args){
		useCameraDetection();
	}

}
