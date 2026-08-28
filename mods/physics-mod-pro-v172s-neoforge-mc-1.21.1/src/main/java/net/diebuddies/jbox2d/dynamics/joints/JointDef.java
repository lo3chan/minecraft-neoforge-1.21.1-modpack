/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class JointDef {
    public JointType type;
    public Object userData;
    public Body bodyA;
    public Body bodyB;
    public boolean collideConnected;

    public JointDef(JointType type) {
        this.type = type;
        this.userData = null;
        this.bodyA = null;
        this.bodyB = null;
        this.collideConnected = false;
    }
}

