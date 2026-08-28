/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class DistanceJointDef
extends JointDef {
    public final Vec2 localAnchorA = new Vec2(0.0f, 0.0f);
    public final Vec2 localAnchorB = new Vec2(0.0f, 0.0f);
    public float length = 1.0f;
    public float frequencyHz = 0.0f;
    public float dampingRatio = 0.0f;

    public DistanceJointDef() {
        super(JointType.DISTANCE);
    }

    public void initialize(Body b1, Body b2, Vec2 anchor1, Vec2 anchor2) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.localAnchorA.set(this.bodyA.getLocalPoint(anchor1));
        this.localAnchorB.set(this.bodyB.getLocalPoint(anchor2));
        Vec2 d = anchor2.sub(anchor1);
        this.length = d.length();
    }
}

