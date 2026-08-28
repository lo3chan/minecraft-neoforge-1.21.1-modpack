/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.util;

import net.diebuddies.physics.IRigidBody;
import net.diebuddies.util.IRigidBodyHolder;

public class SimplePoolBodyHolder {
    private IRigidBodyHolder[] objects;
    private int index;

    public SimplePoolBodyHolder(int size) {
        this.objects = new IRigidBodyHolder[size];
        for (int i = 0; i < size; ++i) {
            this.objects[i] = new IRigidBodyHolder();
        }
    }

    public IRigidBodyHolder get(IRigidBody body, double distanceToCamera) {
        if (this.index < this.objects.length) {
            return this.objects[this.index++].set(body, distanceToCamera);
        }
        this.resize();
        return this.objects[this.index++].set(body, distanceToCamera);
    }

    private void resize() {
        IRigidBodyHolder[] newArray = new IRigidBodyHolder[this.objects.length * 2];
        System.arraycopy(this.objects, 0, newArray, 0, this.objects.length);
        for (int i = this.objects.length; i < newArray.length; ++i) {
            newArray[i] = new IRigidBodyHolder();
        }
        this.objects = newArray;
    }

    public void reset() {
        this.index = 0;
    }
}

