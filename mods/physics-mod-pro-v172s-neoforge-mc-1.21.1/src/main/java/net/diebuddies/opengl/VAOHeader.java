/*
 * Decompiled with CFR 0.152.
 */
package net.diebuddies.opengl;

import java.util.List;
import net.diebuddies.opengl.Data;

public class VAOHeader {
    public int vaoID;
    public int[] bindings;
    public int boundElementBuffer;

    public VAOHeader(int vaoID, List<Data> layout) {
        this.vaoID = vaoID;
        this.bindings = new int[layout.size()];
    }
}

