/*
 * SquareDriver.java
 */
package Navigation;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import wallFollower.BangBangController;

public class CoordinateDriver extends Thread{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	EV3LargeRegulatedMotor leftMotor; EV3LargeRegulatedMotor rightMotor;
	double leftRadius; double rightRadius; double width;
	double  previousAngle = 0;
	
	private static final int bandCenter = 35;			// Offset from the wall (cm)
	private static final int bandWidth = 2;				// Width of dead band (cm)
	private static final int motorLow = 225;			// Speed of slower rotating wheel (deg/sec)
	private static final int motorHigh = 250;			// Speed of the faster rotating wheel (deg/sec)
	private static final Port usPort = LocalEV3.get().getPort("S1");
	Odometer odometer;
	

	public CoordinateDriver(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
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
		//RegulatedMotor regLeftMotor = leftMotor;
		//RegulatedMotor regRightMotor = leftMotor;
		//regLeftMotor.synchronizeWith(new RegulatedMotor[] {regRightMotor});
	}

		public void run() {
			//while(true){
				
			
			travelTo(60,30);
			travelTo(30,30);
			travelTo(30,60);
			travelTo(60,0);
		//	}
			//driver.travelTo(0,0);
			//driver.travelTo(70,70);
			//driver.travelTo(30,-60);
			//driver.travelTo(0,60);//CoordinateDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
			//CoordinateDriver.travelTo(3,3);
		}

	
	
	
	
	void travelTo(double x, double y){
		coordinateFollower follower = new coordinateFollower(leftMotor, rightMotor, bandCenter, bandWidth, motorLow, motorHigh);
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		double tempX = x-currentX;
		double tempY = y-currentY;
		double distance;
	
		double angle;
		/*for(int i = 0; i<10; i++){
			System.out.println("------------------------------");
				System.out.println(odometer.getTheta());
				System.out.println(odometer.getTheta());
				System.out.println("------------------------------");
		}
		*/
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e){}
		
		distance = Math.sqrt(  Math.pow((tempX), 2)    + Math.pow((tempY), 2)   );
	
		
		//angle = ((Math.atan2(tempX,tempY)*180/Math.PI-odometer.getTheta()*180/Math.PI)+180)%360-180;
		angle =Math.atan2(tempX,tempY)*180/Math.PI;
		
		turnTo (-previousAngle);
		previousAngle = angle;
		
		
		turnTo (angle);
		
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
	/*	
		for(int i = 0; i<10; i++){
			System.out.println("------------------------------");
				System.out.println(odometer.getTheta());
				System.out.println(odometer.getTheta());
				System.out.println("------------------------------");
		}
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		leftMotor.rotate(convertAngle(leftRadius, width, -angle), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, -angle), false);
		
		*/

		//odometer.setX(x);
		//odometer.setY(y);

		
		
	}
	
	void turnTo(double theta){
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
				
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
	}
	
	boolean isNavigating(){
		if(leftMotor.isMoving())
			return true;
		return false;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}