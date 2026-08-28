/*
 * Decompiled with CFR 0.152.
 */
package net.irisshaders.iris.vertices.sodium.terrain;

public interface ChunkVertexExtension {
    public void iris$setData(byte var1, byte var2, int var3, int var4, int var5, int var6);

    public void iris$ignoresMidBlock(boolean var1);

    public void iris$copyData(ChunkVertexExtension var1);

    public int getLocalPosX();

    public int getLocalPosY();

    public int getLocalPosZ();

    public int getBlockId();

    public byte getRenderType();

    public byte getBlockEmission();

    public boolean ignoreMidBlock();
}

