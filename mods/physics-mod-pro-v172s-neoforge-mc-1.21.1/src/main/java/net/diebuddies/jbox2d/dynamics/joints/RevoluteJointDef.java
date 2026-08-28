/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class RevoluteJointDef
extends JointDef {
    public Vec2 localAnchorA = new Vec2(0.0f, 0.0f);
    public Vec2 localAnchorB = new Vec2(0.0f, 0.0f);
    public float referenceAngle = 0.0f;
    public boolean enableLimit = false;
    public float lowerAngle = 0.0f;
    public float upperAngle = 0.0f;
    public boolean enableMotor = false;
    public float motorSpeed = 0.0f;
    public float maxMotorTorque = 0.0f;

    public RevoluteJointDef() {
        super(JointType.REVOLUTE);
    }

    public void initialize(Body b1, Body b2, Vec2 anchor) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
        this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
        this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
    }
}

