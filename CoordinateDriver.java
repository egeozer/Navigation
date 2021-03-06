/*
 * SquareDriver.java
 */
package Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class CoordinateDriver extends Thread implements UltrasonicController{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	EV3LargeRegulatedMotor leftMotor; EV3LargeRegulatedMotor rightMotor;
	double leftRadius; double rightRadius; double width;
	double  previousAngle = 0;
	
	public static final int DELTASPD = 100;
	private int distance;
	private int distError;
	private int filterControl;
	private final int motorLow = 100;
	private final int motorHigh = 150;
	private final int FILTER_OUT = 20;
	private final int bandCenter = 35;
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
		leftMotor.synchronizeWith(new EV3LargeRegulatedMotor[] {rightMotor});
		
	}

	public void run() {
		//while(true){
			
		
		travelTo(60,30);
		travelTo(30,30);
		travelTo(30,60);
		travelTo(60,0);
	
	}

	
	
	
	
	void travelTo(double x, double y){

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

		//set linear speed	
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		//move forwards with motors synchronized
		//leftMotor.startSynchronization();	
		//while(distError < 25){
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
		//leftMotor.endSynchronization();
		//}
		//	return;
	

		
		
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

	@Override
	public void processUSData(int distance) {
		
		
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
					
			distError=bandCenter-this.distance;			// Compute error using filter distance
			System.out.println(distError);				//	prints the distError on the ev3 display for debugging 

			travelTo(60,30);
			travelTo(30,30);
			travelTo(30,60);
			travelTo(60,0);
			
	/*	if (distError >= 25) {				// Too close to the wall
				
								//Critical point(very close to the wall), the robot will go backwards to avoid possible collision
					leftMotor.setSpeed(motorLow);
					rightMotor.setSpeed(motorLow);
					leftMotor.backward();
					rightMotor.backward();						
				
				
			}	
		else{
			travelTo(60,30);
		}
		*/
		
		//while(distError<15){
			//travelTo(60,30);
			//travelTo(30,30);
			//travelTo(30,60);
			//travelTo(60,0);
		//}
	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}