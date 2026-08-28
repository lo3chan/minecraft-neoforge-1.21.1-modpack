/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class PulleyJointDef
extends JointDef {
    public Vec2 groundAnchorA = new Vec2(-1.0f, 1.0f);
    public Vec2 groundAnchorB = new Vec2(1.0f, 1.0f);
    public Vec2 localAnchorA = new Vec2(-1.0f, 0.0f);
    public Vec2 localAnchorB = new Vec2(1.0f, 0.0f);
    public float lengthA = 0.0f;
    public float lengthB = 0.0f;
    public float ratio = 1.0f;

    public PulleyJointDef() {
        super(JointType.PULLEY);
        this.collideConnected = true;
    }

    public void initialize(Body b1, Body b2, Vec2 ga1, Vec2 ga2, Vec2 anchor1, Vec2 anchor2, float r) {
        this.bodyA = b1;
        this.bodyB = b2;
        this.groundAnchorA = ga1;
        this.groundAnchorB = ga2;
        this.localAnchorA = this.bodyA.getLocalPoint(anchor1);
        this.localAnchorB = this.bodyB.getLocalPoint(anchor2);
        Vec2 d1 = anchor1.sub(ga1);
        this.lengthA = d1.length();
        Vec2 d2 = anchor2.sub(ga2);
        this.lengthB = d2.length();
        this.ratio = r;
        assert (this.ratio > 1.1920929E-7f);
    }
}

