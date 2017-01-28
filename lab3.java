
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
	
	
// Static Resources:
// Ultrasonic sensor connected to input port S1
// Left motor connected to output A
// Right motor connected to output B
	
	// Constants
	public static final double WHEEL_RADIUS = 2.14;
	public static final double TRACK = 14.5;  

	public static void main(String[] args) {
		
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		 CoordinateDriver driver = new CoordinateDriver(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
		 //classtest name = new classtest();
		 //coordinateFollower follower = new coordinateFollower(leftMotor, rightMotor, bandCenter, bandWidth, motorLow, motorHigh);
		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		//Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		//OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		//SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
	//	SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		//float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		
		// Setup Printer											// This thread prints status information
		//Printer printer = null;										// in the background
		
		// Setup Ultrasonic Poller									// This thread samples the US and invokes
		//UltrasonicPoller usPoller = null;							// the selected controller on each cycle
				
		// Depending on which button was pressed, invoke the US poller and printer with the
		// appropriate constructor.
		
		//usPoller = new UltrasonicPoller(usDistance, usData,  name);
		
												// Proportional control selected
		//usPoller = new UltrasonicPoller(usDistance, usData, follower);
			
		// start the odometer, the odometry display and (possibly) the
		// odometry correction
		
		// start interface
		int buttonChoice;
		do {
			// clear the display
			t.clear();

			// tell the user to press a button to start the program
			t.drawString("< Left  |  Right >", 0, 0);
			t.drawString("        |         ", 0, 1);
			t.drawString(" Simple | Obstacle", 0, 2);
			t.drawString(" path   | path    ", 0, 3);
			t.drawString("        |         ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_ALL);
		
		if (buttonChoice == Button.ID_LEFT) {
		odometer.start();
		odometryDisplay.start();
		//usPoller.start();
		//odometryCorrection.start();
		// spawn a new Thread to avoid SquareDriver.drive() from blocking
		driver.start();
		//usPoller.start();
		
		
		} else { 
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}