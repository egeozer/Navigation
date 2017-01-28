
package Navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import wallFollower.Printer;



public class lab3 {
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S1");
	
	private static final int bandCenter = 35;			// Offset from the wall (cm)
	private static final int bandWidth = 2;				// Width of dead band (cm)
	private static final int motorLow = 225;			// Speed of slower rotating wheel (deg/sec)
	private static final int motorHigh = 250;	
// Static Resources:
// Ultrasonic sensor connected to input port S1
// Left motor connected to output A
// Right motor connected to output B
	
	// Constants
	public static final double WHEEL_RADIUS = 2.14;
	public static final double TRACK = 13.5;  

	public static void main(String[] args) {
		
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		 CoordinateDriver2 driver = new CoordinateDriver2(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
		 //classtest name = new classtest();
		 coordinateFollower follower = new coordinateFollower(leftMotor, rightMotor, bandCenter, bandWidth, motorLow, motorHigh, driver, odometer);
		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		//Odometer odometer = new Odometer(leftMotor, rightMotor);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		//OdometryCorrection odometryCorrection = new OdometryCorrection(odometer);
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		
										// This thread samples the US and invokes
		UltrasonicPoller usPoller = null;							// the selected controller on each cycle
				
		// Depending on which button was pressed, invoke the US poller and printer with the
		// appropriate constructor.d
		
		usPoller = new UltrasonicPoller(usDistance, usData,  follower);
		
		//Printer printer = null;									// Proportional control selected
		//usPoller = new UltrasonicPoller(usDistance, usData, follower);
			
		// start the odometer, the odometry display and (possibly) the
		// odometry correction
			
		odometer.start();
		odometryDisplay.start();
		usPoller.start();
		driver.start();
		
		driver.travelTo(0,60);
		driver.travelTo(60,0);
		//usPoller.start();
		
		//printer.start();
		//odometryCorrection.start();

		// spawn a new Thread to avoid SquareDriver.drive() from blocking
	//driver.start();
	
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}