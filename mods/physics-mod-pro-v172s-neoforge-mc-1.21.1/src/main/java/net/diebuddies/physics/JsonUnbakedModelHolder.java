/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  org.joml.Matrix4f
 */
package net.diebuddies.physics;

import net.minecraft.client.renderer.block.model.BlockModel;
import org.joml.Matrix4f;

public class JsonUnbakedModelHolder {
    public BlockModel model;
    public Matrix4f transformation;

    public JsonUnbakedModelHolder(BlockModel model, Matrix4f transformation) {
        this.model = model;
        this.transformation = transformation;
    }
}

