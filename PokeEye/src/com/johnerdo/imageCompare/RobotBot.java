package com.johnerdo.imageCompare;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class RobotBot {
		Robot robot;
		public RobotBot(){
			try {
				this.robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		public void setMousePosition(Point point){
			this.robot.mouseMove(point.x, point.y);
		}
		
		public void leftClick(){
			// LEFT CLICK
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}
		public static void Screen(){
			RobotBot rob = new RobotBot();
			rob.setMousePosition(new Point(104,64));
			rob.leftClick();
		}
		///104 64 
		public static void main(String [] args){
			RobotBot rob = new RobotBot();
			rob.setMousePosition(new Point(104,64));
			rob.leftClick();
		}
}

