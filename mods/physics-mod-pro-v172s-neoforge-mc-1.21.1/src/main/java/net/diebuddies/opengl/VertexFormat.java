/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL32C
 */
package net.diebuddies.opengl;

import net.diebuddies.opengl.Data;
import org.lwjgl.opengl.GL32C;

public class VertexFormat {
    private Data[] format;
    private int stride;

    public VertexFormat(Data ... format) {
        this.format = format;
        for (int i = 0; i < format.length; ++i) {
            this.stride += format[i].getStride();
        }
    }

    public void bindAttributeFormat() {
        int offset = 0;
        for (int i = 0; i < this.format.length; ++i) {
            Data attribute = this.format[i];
            GL32C.glEnableVertexAttribArray((int)attribute.getAttribute());
            if (attribute.isPureInteger()) {
                GL32C.glVertexAttribIPointer((int)attribute.getAttribute(), (int)attribute.getSize(), (int)attribute.getDataType(), (int)this.stride, (long)offset);
            } else {
                GL32C.glVertexAttribPointer((int)attribute.getAttribute(), (int)attribute.getSize(), (int)attribute.getDataType(), (boolean)attribute.normalize(), (int)this.stride, (long)offset);
            }
            offset += attribute.getStride();
        }
    }

    public int getStride() {
        return this.stride;
    }
}

