package Navigation;

import lejos.hardware.Button;
import lejos.hardware.motor.*;
import Navigation.UltrasonicController;

public class coordinateFollower implements UltrasonicController{

	private final int bandCenter;
	private final int motorLow, motorHigh, FILTER_OUT = 20;
	public static final int DELTASPD = 100;
	private int distance;
	private int distError;
	private int filterControl;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	CoordinateDriver2 driver;
	boolean firstCoord = false;
	Odometer odometer;
	boolean collision;
	int counter;
	boolean block;
	
	
	public coordinateFollower(
			int bandCenter, int bandwidth, int motorLow, int motorHigh, CoordinateDriver2 driver, Odometer odometer) {

		//Default Constructor
		this.bandCenter = bandCenter;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		//this.leftMotor = leftMotor;
		//this.rightMotor = rightMotor;
		//this.odometer = odometer;

		//leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		//rightMotor.setSpeed(motorHigh);
		//leftMotor.forward();
		//rightMotor.forward();
		filterControl = 0;
		this.driver = driver;
		this.odometer = odometer;
		this.leftMotor = this.odometer.getLeftMotor();
		this.rightMotor = this.odometer.getRightMotor();
		
	}
	

	@Override
	public void processUSData(int distance) {
//counter++;
		// rudimentary filter - tosses out invalid samples corresponding to null signal.
		
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} 
		else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} 	
		else {
			// distance went below 255: reset filter and leave
			// distance alone.
			filterControl = 0;
			this.distance = distance;
		}

		// Main control loop: read distance, determine error, adjust speed, and repeat
					
			distError=bandCenter-distance;			// Compute error using filter distance
			//System.out.println(distError);				//	prints the distError on the ev3 display for debugging 
		
		if (distError > 25 ) {				// Too close to the wall
			block = true;
								driver.setNavigating(true);
			//leftMotor.setSpeed(motorLow);
			driver.turnTo(90);
			driver.goDistance(25);
			driver.turnTo(-90);
			driver.goDistance(40);
			driver.turnTo(-90);
			driver.goDistance(25);
			driver.turnTo(90);
			if(odometer.getTheta()*180/Math.PI>50){
			driver.travelTo(60,0);
			}
			else{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				driver.setCollision(false);
				driver.travelTo(0,60);
				driver.travelTo(60,0);
			}
			driver.setNavigating(false);
		//	if(odometer.angle)
		//	driver.travelTo(60,0);
			//rightMotor.setSpeed(motorLow);
		}
			//leftMotor.backward();
			
		
		
							
	}
	
	public boolean isBlock() {
		return block;
	}


	@Override
	public int readUSDistance() {
		return this.distance;
	}
}