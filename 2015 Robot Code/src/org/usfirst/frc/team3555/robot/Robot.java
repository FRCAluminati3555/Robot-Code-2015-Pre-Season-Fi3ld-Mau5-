package org.usfirst.frc.team3555.robot;

import java.awt.Color;

import edu.wpi.first.wpilibj.AnalogOutput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {
    private final String VERSION = "Robot Code - 1.5.6";
    
    private Joystick joyD;
    private Joystick joyOp;
	
    /**Rear Left*/
    private Talon frontL;
	/**Front Left*/
    private Talon frontR;
    /**Rear Right*/
    private Talon rearL;
    /**Front Right*/
    private Talon rearR;
    private SendableChooser autonomousChoose;
    
  	/********************************************************************************\
								Pneumatics Stuff
	\********************************************************************************/	 
    
    private Relay compressorSpike;
    private Compressor compressor;
    private Thread compressorThread = new Thread(() -> {
    	ControlMode startMode = ControlMode.getControlMode(this);    	
    	
    	while(isEnabled() && startMode == ControlMode.getControlMode(this))
    		compressorSpike.set(compressor.enabled() ? Value.kForward : Value.kOff); 	
    	compressorSpike.set(Value.kOff);
    });
    
    private Solenoid pistonOn, pistonOff;
    
    private enum ControlMode {
    	OperatorControl, Autonomous, Test, Practice;
    	
    	public static ControlMode getControlMode(Robot robot) {
    		if(robot.isAutonomous()) return Autonomous;
    		if(robot.isOperatorControl()) return OperatorControl;
    		if(robot.isTest()) return Test;
    		return Practice;
    	}
    }
    
/******************************************************************************************************************************\
											Robot Init.
\******************************************************************************************************************************/ 
    public void robotInit() {
    	joyD = new Joystick(0);
    	joyOp = new Joystick(1);
    	
    	
    	frontL = new Talon(2); //1
        frontR = new Talon(1); //0
        rearL = new Talon(3);  //3
        rearR = new Talon(0);  //2
                
        compressorSpike = new Relay(0);
    	compressor = new Compressor();
    	pistonOn = new Solenoid(0);
    	pistonOff = new Solenoid(1);
    	
    	autonomousChoose = new SendableChooser();
    	for(AutonomousMode mode : AutonomousMode.values()) {
    		if(mode == AutonomousMode.Disabled)
    			autonomousChoose.addDefault(mode.toString(), mode);
    		else
    			autonomousChoose.addObject(mode.toString().replace("_", " "), mode);
    	}
    	
    	SmartDashboard.putData("Autonomous State: ", autonomousChoose);
    }
    
    public void controlCompressor() {
		
    }

/******************************************************************************************************************************\
											Operator Methods
\******************************************************************************************************************************/    
    public void operatorControl() {
    	new Thread(compressorThread).start();
    	boolean lastPistonState = false;
    	
    	while(isEnabled() && isOperatorControl()) {
//        	compressorSpike.set(joyD.getRawButton(5) ? Value.kForward : Value.kOff);
         /********************************************************************************\
									Drive Controls
		 \********************************************************************************/	
//    		int povAngle = joyD.getPOV();
//    		if(povAngle == 270) mecanumSpin(povAngle, 0.75);
//    		if(povAngle == 90)  mecanumSpin(povAngle, 0.75);
//    		if(povAngle == 0)   mecanumDrive(0, -1);
//    		if(povAngle == 180) mecanumDrive(0,  1);
//    		
    		mecanumDrive(joyD.getRawAxis(1) / (joyD.getRawAxis(3)<0.1? 2:1), -joyD.getRawAxis(0));
	      	mecanumSpin(joyD.getRawAxis(4) / 2);
//    		mecanumDrive((Math.abs(joyD.getRawAxis(0))>0 ? joyD.getRawAxis(0)>0 ? 1:-1 :0), (Math.abs(joyD.getRawAxis(1))>0 ? joyD.getRawAxis(1)>0 ? 1:-1 :0));
	      	
//    		if(joyD.getRawButton(1)) frontL.set(1); else frontL.set(0);
//    		if(joyD.getRawButton(2)) frontR.set(1); else frontR.set(0);
//    		if(joyD.getRawButton(3)) rearL.set(1); else rearL.set(0);
//    		if(joyD.getRawButton(4)) rearR.set(1); else rearR.set(0);

      	/********************************************************************************\
									Operator Controls
		\********************************************************************************/	      	 	
	      	if(joyOp.getRawButton(1) && !lastPistonState) {
	      		togglePiston();
	      	}
	      	
	      	lastPistonState = joyOp.getRawButton(1);
	      	
      	/********************************************************************************\
									Print Statements
		\********************************************************************************/
	      	
    		SmartDashboard.putBoolean(" Compressor Enable", compressor.enabled()); 
    		SmartDashboard.putString("Version: ", VERSION);	      		
        }
    }
    
    public void disabled() {
    	compressorSpike.set(Value.kOff);
    }

/******************************************************************************************************************************\
  										Piston & Sensor Methods
\******************************************************************************************************************************/    
    
    private void togglePiston() {
    	pistonOn.set(pistonOff.get()); 	
  		pistonOff.set(!pistonOn.get());
    }
    
    public void pistonOn() {
    	pistonOn.set(true); 	
  		pistonOff.set(false);
    }
    
    public void pistonOff() {
    	pistonOn.set(false); 	
  		pistonOff.set(true);
    }
    
/******************************************************************************************************************************\
  										Drive Methods
\******************************************************************************************************************************/    
    private final double[] DRV_CONFIG_FORWARD = {1, -1, 1, -1};
    private final double[] DRV_CONFIG_LEFT = {-1, -1, 1, 1};
    
//    private void mecanumStrife(double rawAxis) {
//		frontL.set(-rawAxis); 	
//		frontR.set(-rawAxis);
//    	rearL.set(rawAxis);	
//    	rearR.set(rawAxis);
//    }
//    
//    private void mecanumDriveStraight(double rawAxis) {
//		frontL.set(rawAxis); 	
//		frontR.set(rawAxis);
//    	rearL.set(rawAxis);	
//    	rearR.set(rawAxis);//bloop
//    }

    private final float JOYSTICK_SENSITVITY = 0.1f;
    private void mecanumDrive(double x, double y) {
    	double A = 0, B = 0, C = 0, D = 0;
    	
    	if(Math.abs(x) < JOYSTICK_SENSITVITY) x = 0;
    	if(Math.abs(y) < JOYSTICK_SENSITVITY) y = 0;
    	
    	A = DRV_CONFIG_FORWARD[0]*y + DRV_CONFIG_LEFT[0]*x;
    	B = DRV_CONFIG_FORWARD[1]*y + DRV_CONFIG_LEFT[1]*x;
    	C = DRV_CONFIG_FORWARD[2]*y + DRV_CONFIG_LEFT[2]*x;
    	D = DRV_CONFIG_FORWARD[3]*y + DRV_CONFIG_LEFT[3]*x;

    	// This code is used to cap the values of the Motors  
//    	A = ((double)((int)(A*10)))/10.0; A = Math.abs(A) > 0.1 ? 1.0 * A < 0 ? -1 : 1 : A;
//    	B = ((double)((int)(B*10)))/10.0; B = Math.abs(B) > 0.1 ? 1.0 * B < 0 ? -1 : 1 : B;
//    	C = ((double)((int)(C*10)))/10.0; C = Math.abs(C) > 0.1 ? 1.0 * C < 0 ? -1 : 1 : C;
//    	D = ((double)((int)(D*10)))/10.0; D = Math.abs(D) > 0.1 ? 1.0 * D < 0 ? -1 : 1 : D;
    	
    	frontL.set(A); 	frontR.set(B);
    	rearL.set(C);	rearR.set(D);
    }
    
    private void mecanumSpin(double angle) {
    	if(Math.abs(angle) > 0.1) {
    		frontL.set(angle);	frontR.set(angle);
    		rearL.set(angle);	rearR.set(angle);
    	}
    }


/******************************************************************************************************************************\
  										Autonomous Methods
\******************************************************************************************************************************/
   private final float ZONE_CHANGE_AT_HALF_DELAY = 1.15f;
   private final float SPEED = 0.5f;
    
    private enum AutonomousMode {
    	Disabled, Drive, Lift_Left, Lift_Right, 
    	Lift_Forward, Lift_Backward;
    }
    
    public void autonomous() {
    	new Thread(compressorThread).start();
    	switch((AutonomousMode) autonomousChoose.getSelected()) {
    		case Disabled: break; 
    		case Drive: autonomousDrive(); break;
    		case Lift_Left: autonomousLift(0); break;
    		case Lift_Right: autonomousLift(1); break;
    		case Lift_Forward: autonomousLift(2); break;
    		case Lift_Backward: autonomousLift(3); break;
    	}
    }
    
    private void autonomousLift(int dir) {
    	pistonOn();
    	Timer.delay(2);
    	
    	mecanumDrive(dir == 2 ? -SPEED : dir == 3 ? SPEED : 0, dir == 0 ? SPEED : dir == 1 ? -SPEED : 0);
    	Timer.delay(ZONE_CHANGE_AT_HALF_DELAY);
    	mecanumDrive(0, 0);
    }
    
    private void autonomousDrive() {
    	mecanumDrive(-SPEED, 0);
    	Timer.delay(ZONE_CHANGE_AT_HALF_DELAY);
    	mecanumDrive(0, 0);
    }


/******************************************************************************************************************************\
  											Unused Code
\******************************************************************************************************************************/
    @SuppressWarnings("unused")
    private class LED {
    	private AnalogOutput pin;
    	
    	public LED() {
    		pin = new AnalogOutput(0);
    		new Color(255, 0, 0).getRGB();
    	}
    	
    	public void pulse(long time, double volts) {
    		Thread thread = new Thread(() -> {
    			pin.setVoltage(volts);
    			
    			long startTime = System.currentTimeMillis();
    			while((startTime - System.currentTimeMillis()) == time) {
    				try{Thread.sleep(1);}catch(InterruptedException e){}
    			}
    			
    			pin.setVoltage(0);
    		});
    		
    		thread.setName("LED Thread");
    		thread.start();
    	}
    	
    	public boolean isRed() {
    		return DriverStation.getInstance().getAlliance() == Alliance.Red;
    	}
    	
    	public boolean isBlue() {
    		return DriverStation.getInstance().getAlliance() == Alliance.Blue;
    	}
    }
}
