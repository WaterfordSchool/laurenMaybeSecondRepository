// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  Talon l = new Talon(0);
  Talon r = new Talon(7);

  XboxController driver = new XboxController(0);

  DifferentialDrive dT = new DifferentialDrive(l, r);

  boolean limelightValidTarget = false;
  double limelightDriveCommand = 0.0;
  double limelightSteerCommand = 0.0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {}

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {
    Update_Limelight_Tracking();

    double steer = driver.getX(Hand.kRight);
    double drive = -driver.getY(Hand.kLeft);
    boolean auto = driver.getAButton();

    steer *= 0.70;
    drive *= 0.70;

    if (auto){
      if(limelightValidTarget)
      {
        dT.arcadeDrive(limelightDriveCommand,limelightSteerCommand);
      }
      else{
        dT.arcadeDrive(0.0, 0.0);
      }
      
    }
    else {
      dT.arcadeDrive(drive, steer);
    }
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  public void Update_Limelight_Tracking(){
    final double STEER_K = 0.03;
    final double DRIVE_K = 0.26;
    final double DESIRED_TARGET_AREA = 13.0;
    final double MAX_DRIVE = 0.7;

    double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    double ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);

    if (tv < 1.0){
      limelightValidTarget = false;
      limelightDriveCommand = 0.0;
      limelightSteerCommand = 0.0;
      return;
    }

    limelightValidTarget = true;

    double steer_cmd = tx * STEER_K;
    limelightSteerCommand = steer_cmd;


    double drive_cmd = (DESIRED_TARGET_AREA - ta) * DRIVE_K;

    if (drive_cmd > MAX_DRIVE)
    {
      drive_cmd = MAX_DRIVE;
    }
    limelightDriveCommand = drive_cmd;


  }
}
