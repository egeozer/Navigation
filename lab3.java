
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
	public static final double WHEEL_RADIUS = 2.12;
	public static final double TRACK = 13.7;  

	public static void main(String[] args) {
		
		Odometer odometer = new Odometer(leftMotor, rightMotor);
		 CoordinateDriver2 driver = new CoordinateDriver2( WHEEL_RADIUS, WHEEL_RADIUS, TRACK, odometer);
		
		 coordinateFollower follower = new coordinateFollower( bandCenter, bandWidth, motorLow, motorHigh, driver, odometer);
	
		final TextLCD t = LocalEV3.get().getTextLCD();
		
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		

										// This thread samples the US and invokes
		UltrasonicPoller usPoller = null;							// the selected controller on each cycle

	
		
		usPoller = new UltrasonicPoller(usDistance, usData,  follower);
		
		
	
		
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
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_LEFT) {
		odometer.start();
		odometryDisplay.start();
		
		driver.travelTo(60,30);
		driver.travelTo(30,30);
		driver.travelTo(30,60);
		driver.travelTo(60,0);
		
		
		
		} else { 
			odometer.start();
			odometryDisplay.start();
			usPoller.start();
	
			driver.setNavigating(true);
			driver.travelTo(0,60);

				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		
			driver.travelTo(60,0);

		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}