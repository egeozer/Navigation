/*
 * SquareDriver.java
 */
package Navigation;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class CoordinateDriver2 extends Thread {
	boolean navigating;
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	EV3LargeRegulatedMotor leftMotor; EV3LargeRegulatedMotor rightMotor;
	double leftRadius; double rightRadius; double width;
	double  previousAngle = 0;
	boolean firstPath = false;
	public static final int DELTASPD = 100;
	boolean collision = false;
	
	
	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}

	Odometer odometer;
	

	public CoordinateDriver2(
			double leftRadius, double rightRadius, double width, Odometer odometer){
		
		this.leftRadius=leftRadius;
		this.rightRadius=rightRadius;
		this.width=width;
		this.odometer = odometer;
		this.leftMotor = this.odometer.getLeftMotor();
		this.rightMotor = this.odometer.getRightMotor();
		
		//getx = odometer.getX();
		
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(800);
		}
		
		//Synchronize motors
		//leftMotor.synchronizeWith(new EV3LargeRegulatedMotor[] {rightMotor});
		
	}

	

	void travelTo(double x, double y){

		double currentX = odometer.getX();
		double currentY = odometer.getY();
		double tempX = x-currentX;
		double tempY = y-currentY;
		double distance;	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e){}
		
		distance = Math.sqrt(  Math.pow((tempX), 2)    + Math.pow((tempY), 2)   );
	
		
		double newAngle = Math.atan2(tempX,tempY)*180/Math.PI;
		double oldAngle = odometer.getTheta()*180/Math.PI;
		double difAngle = newAngle - oldAngle;
		double turnAngle = ((difAngle + 180)%360)-180;
		navigating = true;
		turnTo (turnAngle);
	
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
	
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean isFirstPath() {
		return firstPath;
	}

	void turnTo(double theta){
		//set rotational speed
		navigating = true;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		

		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
		while(true){
			if(!leftMotor.isMoving()&& !rightMotor.isMoving()){
				break;
			}
			
		}
		//leftMotor.endSynchronization();
	}
	
	void goDistance(double distance){
		navigating = true;
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
		while(true){
			if(!leftMotor.isMoving()&& !rightMotor.isMoving()){
				break;
			}
			
		}
	}
	
	
	public void setNavigating(boolean navigating) {
		this.navigating = navigating;
	}

	boolean isNavigating(){
		return navigating;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	
	
	
}