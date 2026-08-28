/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class MouseJointDef
extends JointDef {
    public final Vec2 target = new Vec2();
    public float maxForce;
    public float frequencyHz;
    public float dampingRatio;

    public MouseJointDef() {
        super(JointType.MOUSE);
        this.target.set(0.0f, 0.0f);
        this.maxForce = 0.0f;
        this.frequencyHz = 5.0f;
        this.dampingRatio = 0.7f;
    }
}

