/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.dynamics.Filter;

public class FixtureDef {
    public Shape shape = null;
    public Object userData = null;
    public float friction = 0.2f;
    public float restitution = 0.0f;
    public float density = 0.0f;
    public boolean isSensor = false;
    public Filter filter = new Filter();

    public Shape getShape() {
        return this.shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Object getUserData() {
        return this.userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public float getFriction() {
        return this.friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return this.restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getDensity() {
        return this.density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public boolean isSensor() {
        return this.isSensor;
    }

    public void setSensor(boolean isSensor) {
        this.isSensor = isSensor;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }
}

