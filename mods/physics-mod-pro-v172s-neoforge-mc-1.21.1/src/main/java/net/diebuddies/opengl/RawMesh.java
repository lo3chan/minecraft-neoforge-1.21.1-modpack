/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package net.diebuddies.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.diebuddies.physics.snow.math.AABB3D;
import org.lwjgl.system.MemoryUtil;

public class RawMesh {
    public ByteBuffer data;
    public ByteBuffer indexData;
    public AABB3D boundingBox;

    public void destroy() {
        if (this.data != null) {
            MemoryUtil.memFree((Buffer)this.data);
        }
        this.data = null;
        if (this.indexData != null) {
            MemoryUtil.memFree((Buffer)this.indexData);
        }
        this.indexData = null;
    }
}

