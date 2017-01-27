/*
 * SquareDriver.java
 */
package Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class CoordinateDriver {
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	EV3LargeRegulatedMotor leftMotor; EV3LargeRegulatedMotor rightMotor;
	double leftRadius; double rightRadius; double width;
	
	
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
			motor.setAcceleration(300);
		}
		
		//Synchronize motors (optional correction for more accuracy) -MK
		//RegulatedMotor regLeftMotor = leftMotor;
		//RegulatedMotor regRightMotor = leftMotor;
		//regLeftMotor.synchronizeWith(new RegulatedMotor[] {regRightMotor});
	}
	
	
	void travelTo(double x, double y){
		double currentX = odometer.getX();
		double currentY = odometer.getY();
		double tempX = x-currentX;
		double tempY = y-currentY;
		double distance;
		double angle;
		for(int i = 0; i<10; i++){
			System.out.println("------------------------------");
				System.out.println(currentX);
				System.out.println(currentY);
				System.out.println("------------------------------");
		}
		
		//double xPos = odometer.getY();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e){}
		
		distance = Math.sqrt(  Math.pow((tempX), 2)    + Math.pow((tempY), 2)   );
		
		if(tempX>0 && tempY>= -2 && tempY<=2)
			angle = 90;
		else if(tempX<0 && tempY >= -2 && tempY<=2)
			angle = -90;
		
		else if(tempY >0 && tempX >= -2 && tempX<=2)
			angle = 0;
		else if(tempY<0 && tempX >= -2 && tempX<=2){
			angle = 180;
			
		}
		else if (tempY<-2 && tempX<-2){
		angle = 90-Math.atan((tempY)/(tempX))*180/Math.PI-180; 
		}
		else{
		angle = 90-Math.atan((tempY)/(tempX))*180/Math.PI; 
		System.out.println("ELSLELELELELELELELEL");
		}
		
				
		turnTo(angle);
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		
		leftMotor.rotate(convertDistance(leftRadius, distance), true);
		rightMotor.rotate(convertDistance(rightRadius, distance), false);
		
		for(int i = 0; i<10; i++){
			System.out.println("------------------------------");
				System.out.println(currentX);
				System.out.println(currentY);
				System.out.println("------------------------------");
		}
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		leftMotor.rotate(convertAngle(leftRadius, width, -angle), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, -angle), false);
		
		

		odometer.setX(x);
		odometer.setY(y);

		
		
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