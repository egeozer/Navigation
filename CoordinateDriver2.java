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
	
	public static final int DELTASPD = 100;
	
	
	Odometer odometer;
	

	public CoordinateDriver2(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width, Odometer odometer){
		this.leftMotor=leftMotor;
		this.rightMotor=rightMotor;
		this.leftRadius=leftRadius;
		this.rightRadius=rightRadius;
		this.width=width;
		this.odometer = odometer;
		//getx = odometer.getX();
		
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(800);
		}
		
		//Synchronize motors
		//leftMotor.synchronizeWith(new EV3LargeRegulatedMotor[] {rightMotor});
		
	}

	public void run() {
		//while(true){
			
		while (true) {														// operates continuously
			// print last US reading
						//travelTo(60,30);
			try {
				Thread.sleep(50);											// sleep for 200 mS
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	
	}

	
	
	
	
	void travelTo(double x, double y){

		double currentX = odometer.getX();
		double currentY = odometer.getY();
		double tempX = x-currentX;
		double tempY = y-currentY;
		double distance;
		double angle;
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e){}
		
		distance = Math.sqrt(  Math.pow((tempX), 2)    + Math.pow((tempY), 2)   );
	
		//angle = ((Math.atan2(tempX,tempY)*180/Math.PI-odometer.getTheta()*180/Math.PI)+180)%360-180;
		angle =Math.atan2(tempX,tempY)*180/Math.PI;
		
		turnTo (-previousAngle);
		previousAngle = angle;
		
		
		turnTo (angle);

		//set linear speed	
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
	
	
		
	}
	
	void turnTo(double theta){
		//set rotational speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//turn with motors synchronized
		//leftMotor.startSynchronization();
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
		//leftMotor.endSynchronization();
	}
	
	void goDistance(double distance){
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
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