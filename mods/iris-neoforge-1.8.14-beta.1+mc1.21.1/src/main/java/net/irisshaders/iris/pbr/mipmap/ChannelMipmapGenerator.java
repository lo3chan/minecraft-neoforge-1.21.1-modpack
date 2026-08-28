/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.FastColor$ABGR32
 */
package net.irisshaders.iris.pbr.mipmap;

import net.irisshaders.iris.pbr.mipmap.AbstractMipmapGenerator;
import net.minecraft.util.FastColor;

public class ChannelMipmapGenerator
extends AbstractMipmapGenerator {
    protected final BlendFunction redFunc;
    protected final BlendFunction greenFunc;
    protected final BlendFunction blueFunc;
    protected final BlendFunction alphaFunc;

    public ChannelMipmapGenerator(BlendFunction redFunc, BlendFunction greenFunc, BlendFunction blueFunc, BlendFunction alphaFunc) {
        this.redFunc = redFunc;
        this.greenFunc = greenFunc;
        this.blueFunc = blueFunc;
        this.alphaFunc = alphaFunc;
    }

    @Override
    public int blend(int c0, int c1, int c2, int c3) {
        return FastColor.ABGR32.color((int)this.alphaFunc.blend(FastColor.ABGR32.alpha((int)c0), FastColor.ABGR32.alpha((int)c1), FastColor.ABGR32.alpha((int)c2), FastColor.ABGR32.alpha((int)c3)), (int)this.blueFunc.blend(FastColor.ABGR32.blue((int)c0), FastColor.ABGR32.blue((int)c1), FastColor.ABGR32.blue((int)c2), FastColor.ABGR32.blue((int)c3)), (int)this.greenFunc.blend(FastColor.ABGR32.green((int)c0), FastColor.ABGR32.green((int)c1), FastColor.ABGR32.green((int)c2), FastColor.ABGR32.green((int)c3)), (int)this.redFunc.blend(FastColor.ABGR32.red((int)c0), FastColor.ABGR32.red((int)c1), FastColor.ABGR32.red((int)c2), FastColor.ABGR32.red((int)c3)));
    }

    public static interface BlendFunction {
        public int blend(int var1, int var2, int var3, int var4);
    }
}

