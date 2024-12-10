package org.firstinspires.ftc.teamcode.excutil.keyframer;

import android.util.Pair;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers.DcMotorPositionable;
import org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers.IPositionable;
import org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers.KeyframableComponent;
import org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers.ServoPositionable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Keyframer {

    static class RouteKeyframe {
        public Map<KeyframableComponent, Double> positions = new HashMap<>();
        public Pose2d pose;

        public RouteKeyframe() {

        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();

            for (Map.Entry<KeyframableComponent, Double> entries : positions.entrySet()) {
                out.append(String.format("%s: %f", entries.getKey().first, entries.getValue()) + "\n");
            }
            out.append(String.format("Pose2D(x, y, heading degrees): %s", pose.toString()));

            return out.toString();

        }
    }

    public static KeyframableComponent component(String name, Servo servo) {
        return new KeyframableComponent(name, new ServoPositionable(servo));
    }

    public static KeyframableComponent component(String name, DcMotor motor) {
        return new KeyframableComponent(name, new DcMotorPositionable(motor));
    }

    private List<KeyframableComponent> relevantComponents;
    private SampleMecanumDrive drive;

    public Keyframer(SampleMecanumDrive drive, KeyframableComponent... components) {
        relevantComponents = Arrays.asList(components);
        this.drive = drive;
    }

    public List<RouteKeyframe> keyframes = new ArrayList<>();

    public void takeKeyframe() {
        RouteKeyframe keyframe = new RouteKeyframe();

        for (KeyframableComponent component : relevantComponents) {
            keyframe.positions.put(
                component, component.second.getPosition()
            );
        }

        keyframe.pose = drive.getPoseEstimate();

        keyframes.add(keyframe);
    }

    public void popKeyframe() {
        if (keyframes.size() > 0) keyframes.remove(keyframes.size() - 1);
    }

    public void clearKeyframes() {
        keyframes.clear();
    }

    public void export() {
        File keyframeOut = AppUtil.getInstance().getSettingsFile("keyframe" + System.currentTimeMillis() + ".out");

        StringBuilder out = new StringBuilder();

        for (RouteKeyframe keyframe : keyframes) {
            out.append(keyframe.toString() + "\n=======================\n");
        }


        ReadWriteFile.writeFile(keyframeOut, out.toString());
    }




}
