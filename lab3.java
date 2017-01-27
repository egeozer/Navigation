
package Navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class lab3 {
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	// Constants
	public static final double WHEEL_RADIUS = 2.14;
	public static final double TRACK = 14.5;  

	public static void main(String[] args) {
		
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		final CoordinateDriver driver = new CoordinateDriver(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);

		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		//Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		//OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);

		
		// start the odometer, the odometry display and (possibly) the
		// odometry correction
			

			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
			
			odometer.start();
			odometryDisplay.start();
		//	odometryCorrection.start();
			
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			
			odometer.start();
			odometryDisplay.start();
			//odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					driver.travelTo(60,30);
					driver.travelTo(30,30);
					driver.travelTo(30,60);
					driver.travelTo(60,0);
					driver.travelTo(0,0);
					//driver.travelTo(30,-60);
					//driver.travelTo(0,60);//CoordinateDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
					//CoordinateDriver.travelTo(3,3);
				}
			}).start();
		
		odometer.start();
		odometryDisplay.start();
		//odometryCorrection.start();

		// spawn a new Thread to avoid SquareDriver.drive() from blocking
	

		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}