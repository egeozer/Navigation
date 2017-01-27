/*

 * Odometer.java

 */

package Navigation;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {

	private double x, y, theta;			// robot position

	private int leftMotorTachoCount, rightMotorTachoCount;

	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	private static final long ODOMETER_PERIOD = 25;	// odometer update period, in ms

	public Object lock;					// lock object for mutual exclusion

	private double distance, angle;		//distance in cm, angle in radians

	private double prevDistance, prevAngle;

	private double WB = lab3.TRACK;

	private double WR = lab3.WHEEL_RADIUS;

	

	// default constructor

	public Odometer(EV3LargeRegulatedMotor leftMotor,EV3LargeRegulatedMotor rightMotor) {

		this.leftMotor = leftMotor;

		this.rightMotor = rightMotor;

		this.x = 0.0;

		this.y = 0.0;

		this.theta = 0.0;

		this.leftMotorTachoCount = 0;

		this.rightMotorTachoCount = 0;

		this.distance = 0.0;

		this.angle = 0.0;

		this.prevDistance = 0.0;

		this.prevAngle = 0.0;

		lock = new Object();

	}


	// run method (required for Thread)

	public void run() {

		long updateStart, updateEnd;


		while (true) {

			updateStart = System.currentTimeMillis();

			

			//TODO put (some of) your odometer code here

			this.updateData();

			synchronized (lock) {

				/**

				 * Don't use the variables x, y, or theta anywhere but here!

				 * Only update the values of x, y, and theta in this block. 

				 * Do not perform complex math

				 * 

				 */

				

				//Set variables

				theta += angle;

				//make sure angle is not negative

				/*if(theta < 0.0) {

					theta = 2*Math.PI + theta;

				}*/	

				x += distance * Math.sin(theta);

				y += distance * Math.cos(theta);	

			}





			// this ensures that the odometer only runs once every period

			updateEnd = System.currentTimeMillis();

			if (updateEnd - updateStart < ODOMETER_PERIOD) {

				try {

					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));

				} catch (InterruptedException e) {

					// there is nothing to be done here because it is not

					// expected that the odometer will be interrupted by

					// another thread

				}

			}

		}

	}

	

	private void updateData() {

		//get tachometer values

		leftMotorTachoCount = leftMotor.getTachoCount();

		rightMotorTachoCount = rightMotor.getTachoCount();

		

		//calculate distance and heading

		distance = WR * Math.PI / 360 * ( leftMotorTachoCount + rightMotorTachoCount);

		angle = WR / WB * Math.PI / 180 * (  leftMotorTachoCount - rightMotorTachoCount  );

		

		distance -= prevDistance;

		angle -= prevAngle;

		

		//update previous values

		prevDistance += distance;

		prevAngle += angle;



	}

	// accessors

	public void getPosition(double[] position, boolean[] update) {

		// ensure that the values don't change while the odometer is running

		synchronized (lock) {

			if (update[0])

				position[0] = x;

			if (update[1])

				position[1] = y;

			if (update[2])

				position[2] = theta * 180/Math.PI;

		}

	}



	public double getX() {

		double result;



		synchronized (lock) {

			result = x;

		}



		return result;

	}



	public double getY() {

		double result;



		synchronized (lock) {

			result = y;

		}



		return result;

	}

	//returns theta in radians

	public double getTheta() {

		double result;



		synchronized (lock) {

			result = theta;

		}

		

		return result;

	}



	// mutators

	public void setPosition(double[] position, boolean[] update) {

		// ensure that the values don't change while the odometer is running

		synchronized (lock) {

			if (update[0])

				x = position[0];

			if (update[1])

				y = position[1];

			if (update[2])

				theta = position[2];

		}

	}



	public void setX(double x) {

		synchronized (lock) {

			this.x = x;

		}

	}



	public void setY(double y) {

		synchronized (lock) {

			this.y = y;

		}

	}



	public void setTheta(double theta) {

		synchronized (lock) {

			this.theta = theta;

		}

	}



	/**

	 * @return the leftMotorTachoCount

	 */

	public int getLeftMotorTachoCount() {

		return leftMotorTachoCount;

	}



	/**

	 * @param leftMotorTachoCount the leftMotorTachoCount to set

	 */

	public void setLeftMotorTachoCount(int leftMotorTachoCount) {

		synchronized (lock) {

			this.leftMotorTachoCount = leftMotorTachoCount;	

		}

	}



	/**

	 * @return the rightMotorTachoCount

	 */

	public int getRightMotorTachoCount() {

		return rightMotorTachoCount;

	}



	/**

	 * @param rightMotorTachoCount the rightMotorTachoCount to set

	 */

	public void setRightMotorTachoCount(int rightMotorTachoCount) {

		synchronized (lock) {

			this.rightMotorTachoCount = rightMotorTachoCount;	

		}

	}

}