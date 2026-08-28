/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  org.joml.Matrix4d
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package net.diebuddies.physics.verlet;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletStick;
import net.diebuddies.physics.verlet.VerletTriangle;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class VerletSimulationData {
    public List<VerletPoint> points = new ObjectArrayList();
    public List<VerletStick> sticks = new ObjectArrayList();
    public List<VerletQuad> quads = new ObjectArrayList();
    public List<VerletTriangle> triangles = new ObjectArrayList();
    public List<VerletLine> lines = new ObjectArrayList();
    public Vector3d offset;
    public Vector3d bufferOffset;
    public Matrix4d transformation = new Matrix4d();
    public Matrix4d bufferTransformation = new Matrix4d();

    public VerletSimulationData(Vector3d offset) {
        if (offset == null) {
            this.offset = null;
            this.bufferOffset = null;
        } else {
            this.offset = new Vector3d((Vector3dc)offset);
            this.bufferOffset = new Vector3d((Vector3dc)offset);
        }
    }
}

