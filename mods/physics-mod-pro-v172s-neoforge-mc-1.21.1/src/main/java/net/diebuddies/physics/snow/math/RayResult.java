/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package net.diebuddies.physics.snow.math;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.diebuddies.physics.snow.math.RayHit;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RayResult {
    private boolean hit = false;
    private List<RayHit> rayHits = new ObjectArrayList();

    public void addRayHit(RayHit rayHit) {
        this.rayHits.add(rayHit);
        this.hit = true;
    }

    public boolean hasHit() {
        return this.hit;
    }

    public List<RayHit> getRayHits() {
        return this.rayHits;
    }

    public void sortByDistance(final Vector3d start) {
        Collections.sort(this.rayHits, new Comparator<RayHit>(){

            @Override
            public int compare(RayHit o1, RayHit o2) {
                if (o1.point.distance((Vector3dc)start) < o2.point.distance((Vector3dc)start)) {
                    return -1;
                }
                return 1;
            }
        });
    }
}

