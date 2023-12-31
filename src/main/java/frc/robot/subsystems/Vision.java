// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Vision extends SubsystemBase {
    /** Creates a new Vision. */

    DigitalOutput cameraLight;

    int aprilTagID;

    /* FIX ME! */
    // initialize the aprilTagDetected to false
    boolean aprilTagDetected ; 
    double aprilTagDistToCenter ;
    double aprilTagRange;
    // ignore this:
    // double aprilTagX;
    // double aprilTagY;
    // double aprilTagZ;
    // double aprilTagBearing;

    String sourceIP = "Nothing Received";

    int missedFrames = 0;

    // String DELETE = "{ \"detections\": [ { \"detected\": true, \"distToCenter\":
    // 80, \"id\": 1, \"range\": 1 }, { \"detected\": false }, { \"detected\": false
    // }, { \"detected\": true, \"distToCenter\": -88.25484466552734, \"id\": 4,
    // \"range\": 1.5504128731987945 }]}";

    private DatagramChannel visionChannel = null;
    ByteBuffer buffer = ByteBuffer.allocate(1024);

    public Vision() {
        // cameraLight = new DigitalOutput(Constants.VisionConstants.CameraLightID); //
        // error in simulate code

        try {
            visionChannel = DatagramChannel.open();
            InetSocketAddress sAddr = new InetSocketAddress(5800);
            visionChannel.bind(sAddr);
            visionChannel.configureBlocking(false);
        } catch (Exception ex) {
            int wpk = 1;
        }
    }

    // public void LookUpTable() {}
    // public ArrayList<Double> lookUpTArrayList;

    @Override
    public void periodic() {
        try {
            boolean done = false;
            String message = "";
            while (!done) {
                InetSocketAddress sender = (InetSocketAddress) visionChannel.receive(buffer);
                buffer.flip();
                int limits = buffer.limit();
                if (limits > 0) {
                    byte bytes[] = new byte[limits];
                    buffer.get(bytes, 0, limits);
                    message = new String(bytes);
                    sourceIP = sender.getAddress().toString();
                } else {
                    done = true;
                }
                buffer.clear();
            }

            if (message.length() > 0) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(message); 
                ArrayNode detectionsNode = (ArrayNode) rootNode.get("detections");
                double minDistance = Double.MAX_VALUE ;

                for (int i = 0; i < 4; i++) {
                    JsonNode detection = detectionsNode.get(i);
                    if (detection.get("detected").asBoolean()) {
                        if (Math.abs(detection.get("distToCenter").asDouble()) < Math.abs(minDistance)) {
                            aprilTagDistToCenter = detection.get("distToCenter").asDouble();
                            aprilTagID = detection.get("id").asInt();
                            aprilTagRange = detection.get("range").asDouble();
                            aprilTagDetected = detection.get("detected").asBoolean();
                            minDistance = aprilTagDistToCenter ;
                            missedFrames = 0;
                        }
                    }
                }
            } else {
                missedFrames++;
                if (missedFrames >= 10) {
                    this.aprilTagDetected = false;
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception reading vison data");
        }

    }

    public int getAprilTagID() {
        /* FIX ME! */
        // return the aprilTagID from this class
    }

    public boolean getAprilTagDetected() {
        /* FIX ME! */
        // return the appropriate member value from this class
    }

    public double getAprilTagRange() {
       /* FIX ME! */ 
       // return the right thing
    }

    public double getAprilTagDistToCenter() {
       /* FIX ME! */ 
       // return the right thing
    }

    public String getSourceIP() {
        /* FIX ME! */ 
       // return the right thing
    }

    /******** SHUFFLEBOARD ********/

    @Override
    public void initSendable(SendableBuilder builder) {
        super.initSendable(builder);

        builder.setSmartDashboardType("Vision Subsystem");

        builder.addDoubleProperty("April Tag ID", this::getAprilTagID, null);
        builder.addDoubleProperty("April Tag Distance to Center", this::getAprilTagDistToCenter, null);
        builder.addDoubleProperty("April Tag Range", this::getAprilTagRange, null);
        builder.addBooleanProperty("April Tag Detected", this::getAprilTagDetected, null);
        builder.addStringProperty("Sender ID", this::getSourceIP, null);
    }
}