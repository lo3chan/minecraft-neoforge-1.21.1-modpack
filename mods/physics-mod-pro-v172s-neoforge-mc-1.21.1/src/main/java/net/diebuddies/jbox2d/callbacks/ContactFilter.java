/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.dynamics.Filter;
import net.diebuddies.jbox2d.dynamics.Fixture;

public class ContactFilter {
    public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
        Filter filterA = fixtureA.getFilterData();
        Filter filterB = fixtureB.getFilterData();
        if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0) {
            return filterA.groupIndex > 0;
        }
        boolean collide = (filterA.maskBits & filterB.categoryBits) != 0 && (filterA.categoryBits & filterB.maskBits) != 0;
        return collide;
    }
}

