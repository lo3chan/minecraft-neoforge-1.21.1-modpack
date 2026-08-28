/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.ShaderInstance
 */
package net.irisshaders.iris.platform;

import java.util.function.Supplier;
import net.irisshaders.iris.vertices.ImmediateState;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;

public class Bypass
extends RenderStateShard.ShaderStateShard {
    public Bypass(Supplier<ShaderInstance> original) {
        super(() -> {
            ImmediateState.bypass = true;
            ShaderInstance i = (ShaderInstance)original.get();
            ImmediateState.bypass = false;
            return i;
        });
    }
}

