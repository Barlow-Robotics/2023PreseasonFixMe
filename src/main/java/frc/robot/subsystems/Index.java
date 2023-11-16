// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Index extends SubsystemBase {
    WPI_TalonSRX hopperMotor;

    /* FIX ME! */
    // change the 3 lines below to initialize the variables to 1.0, 0.0, and 0 respectively.
    double hopperDirection;
    double hopperSpeed;
    int stuckCount;

    public Index() {
        hopperMotor = new WPI_TalonSRX(Constants.IndexConstants.HopperMotorID);
        setMotorConfig(hopperMotor);
        hopperMotor.configFactoryDefault();
    }

    @Override
    public void periodic() {
        if (Math.abs(getSupplyCurrent()) > 0.1) {
            stuckCount++;
            /* FIX ME! */
            // When the hopper is stuck for more than 10 scheduler runs, we want to reverse the hopper
            // direction in an attempt to get them unstuck
            // if stuckCount is greater than 10, set it to 0 and reverse the hopperDirection.



        } else {
            stuckCount = 0;
        }
        // if (hopperDirection > 0.0) {
        // hopperMotor.setInverted(InvertType.None);
        // } else {
        // hopperMotor.setInverted(InvertType.InvertMotorOutput);
        // }
        hopperMotor.set(TalonSRXControlMode.PercentOutput, hopperSpeed * hopperDirection);
        // System.out.println("current in index periodic is " + getSupplyCurrent()) ;
    }

    public void startIndex() {
        hopperSpeed = Constants.IndexConstants.HopperMotorSpeed;
    }

    public void stopIndex() {
        hopperSpeed = 0.0;
        // hopperMotor.set(TalonSRXControlMode.PercentOutput, 0.0);
    }

    private double getSupplyCurrent() {
        /* FIX ME! */
        // Hover over the crossed-out line below to see what the problem is, and fix it.
        return this.hopperMotor.getOutputCurrent();
    }

    private double getStatorCurrent() {
        return this.hopperMotor.getStatorCurrent();
    }

    public double getOutput() {
        return hopperMotor.getMotorOutputPercent();
    }

    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Index Subsystem");

        builder.addDoubleProperty("Percent Output", this::getOutput, null);
        builder.addDoubleProperty("Supply Current", this::getSupplyCurrent, null);
        builder.addDoubleProperty("Stator Current", this::getStatorCurrent, null);
    }

    private void setMotorConfig(WPI_TalonSRX motor) {
        motor.configFactoryDefault();
        motor.configClosedloopRamp(Constants.IndexConstants.ClosedVoltageRampingConstant);
        motor.configOpenloopRamp(Constants.IndexConstants.ManualVoltageRampingConstant);
        motor.config_kF(0, Constants.IndexConstants.kF);
        motor.config_kP(0, Constants.IndexConstants.kP);
        motor.config_kI(0, Constants.IndexConstants.kI);
        motor.config_kD(0, Constants.IndexConstants.kD);
        motor.setNeutralMode(NeutralMode.Brake);
    }
}
