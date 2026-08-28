/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.dynamics.joints.Joint;
import net.diebuddies.jbox2d.dynamics.joints.JointDef;
import net.diebuddies.jbox2d.dynamics.joints.JointType;

public class GearJointDef
extends JointDef {
    public Joint joint1 = null;
    public Joint joint2 = null;
    public float ratio;

    public GearJointDef() {
        super(JointType.GEAR);
    }
}

