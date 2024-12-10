package org.firstinspires.ftc.teamcode.excutil.keyframer.wrappers;

import android.util.Pair;

public class KeyframableComponent extends Pair<String, IPositionable> {
    public KeyframableComponent(String first, IPositionable second) {
        super(first, second);
    }
}
