/*
 * SquareDriver.java
 */
package Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class CoordinateDriver extends Thread{
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	EV3LargeRegulatedMotor leftMotor; EV3LargeRegulatedMotor rightMotor;
	double leftRadius; double rightRadius; double width;
	double  previousAngle = 0;
	
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
		//	}
			//driver.travelTo(0,0);
			//driver.travelTo(70,70);
			//driver.travelTo(30,-60);
			//driver.travelTo(0,60);//CoordinateDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
			//CoordinateDriver.travelTo(3,3);
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
		leftMotor.startSynchronization();	
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
		leftMotor.endSynchronization();
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
		//set rotational speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//turn with motors synchronized
		leftMotor.startSynchronization();
		leftMotor.rotate(convertAngle(leftRadius, width, theta), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, theta), false);
		leftMotor.endSynchronization();
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